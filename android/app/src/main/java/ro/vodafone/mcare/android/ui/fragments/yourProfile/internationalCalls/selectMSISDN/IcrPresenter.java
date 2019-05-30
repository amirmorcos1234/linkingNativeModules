package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRateSuccess;
import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRatesRequest;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.InternationalCallsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.InternationalCostRatesService;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

public class IcrPresenter implements IcrContract.Presenter {
	private IcrContract.View view;
	private InternationalCostRatesService service = new InternationalCostRatesService(VodafoneController.getInstance());

	private List<Subscription> subscriptionList = new ArrayList<>();

	public IcrPresenter(IcrContract.View view) {
		this.view = view;
	}

	@Override
	public void getRatesOwn(final String calledPhone) {
		final String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
		String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
		String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() : null;
		boolean vfEBUMigrated = VodafoneController.getInstance().getUserProfile().isMigrated();
		final String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

//        VodafoneController.getInstance().getUser() instanceof PrepaidUser;

		subscriptionList.add(
				service.getIcrOwn(vfPhoneNumber,
						vfSsoUserRole,
						null,
						calledPhone,
						selectedMsisdn,
						crmRole,
						vfEBUMigrated)
						.take(1)
						.subscribe(new RequestSessionObserver<GeneralResponse<InternationalCallingRateSuccess>>() {
							@Override
							public void onNext(GeneralResponse<InternationalCallingRateSuccess> response) {
								processReponse(InternationalCallsMsisdnFragment.NumberType.RECIPIENT_NUMBER, selectedMsisdn, calledPhone, response);
							}

							@Override
							public void onCompleted() {
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);

								processError(e);
							}
						}));
	}


	@Override
	public void getRatesOther(final String callerPhone, final String calledPhone) {
		String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
		String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() : null;

		subscriptionList.add(
				service.getIcrOther(vfSsoUserRole, callerPhone, calledPhone, crmRole)
						.take(1)
						.subscribe(new RequestSessionObserver<GeneralResponse<InternationalCallingRateSuccess>>() {
							@Override
							public void onNext(GeneralResponse<InternationalCallingRateSuccess> response) {
								processReponse(InternationalCallsMsisdnFragment.NumberType.CALLER_NUMBER, callerPhone, calledPhone, response);
							}

							@Override
							public void onCompleted() {
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);

								processError(e);
							}
						}));
	}

	private void processReponse(InternationalCallsMsisdnFragment.NumberType callType, String callerPhone, String calledPhone, GeneralResponse<InternationalCallingRateSuccess> response) {
		if (response.getTransactionFault() != null) {
			if (callType.equals(InternationalCallsMsisdnFragment.NumberType.CALLER_NUMBER)) {
				if (response.getTransactionFault().getFaultCode() != null && response.getTransactionFault().getFaultCode().equals(ErrorCodes.API_67_ICR_NOT_VDF_SUBSCRIBER.getErrorCode()))       //EC6701            not a vodafone subscription
					view.showCallerError(InternationalCallsLabels.notInVodafone());
				else if (response.getTransactionFault().getFaultCode() != null && response.getTransactionFault().getFaultCode().equals(ErrorCodes.API_67_ICR_POSTPAID_PHONE_NUMBER.getErrorCode()))       // postpaid caller number
					view.showToken(callerPhone, calledPhone);
				else if (response.getTransactionFault().getFaultCode() != null && response.getTransactionFault().getFaultCode().equals(ErrorCodes.API_67_ICR_INVALID_SMS_CODE.getErrorCode()))       // invalid sms
					view.showSMSTokenError(InternationalCallsLabels.invalidSmsToken());
				else if (response.getTransactionFault().getFaultCode() != null && response.getTransactionFault().getFaultCode().equals(ErrorCodes.API_67_ICR_SMS_SENT_STILL_VALID.getErrorCode()))       // invalid sms
					view.showToast(InternationalCallsLabels.smsStillValid(), false, true);
				else
					processError(null);
			} else
				processError(null);
		} else
			view.showCosts(callerPhone, calledPhone, response.getTransactionSuccess().getRate(), !callType.equals(InternationalCallsMsisdnFragment.NumberType.CALLER_NUMBER));
	}

	private void processError(Throwable error) {
		view.showToast(TopUPLabels.getTop_up_api_call_fail(), false, true);
	}

	@Override
	public void resendToken(String phone) {
		subscriptionList.add(
				service.getIcrSms(phone)
						.take(1)
						.subscribe(new RequestSessionObserver<GeneralResponse<InternationalCallingRateSuccess>>() {
							@Override
							public void onNext(GeneralResponse<InternationalCallingRateSuccess> response) {

								if(response.getTransactionStatus() == 0)
									view.showToast(InternationalCallsLabels.smsSent(), true, false);
								else if (response.getTransactionFault().getFaultCode() != null && response.getTransactionFault().getFaultCode().equals(ErrorCodes.API_67_ICR_SMS_SENT_STILL_VALID.getErrorCode()))       // invalid sms
									view.showToast(InternationalCallsLabels.smsStillValid(), false, true);
								else
									view.showToast(TopUPLabels.getTop_up_api_call_fail(), false, true);

							}

							@Override
							public void onCompleted() {
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);

								processError(e);
							}
						}));
	}

	@Override
	public void getRates(final String callerPhone, final String calledPhone, String smsCode) {
		String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() : null;
		String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
		String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();

		InternationalCallingRatesRequest body = new InternationalCallingRatesRequest();
		body.setCallerPhone(callerPhone);
		body.setCalledPhone(calledPhone);
		body.setSmsCode(smsCode);
		body.setIsMSISDNResourceType(true);

		subscriptionList.add(
				service.getIcr(crmRole,
						vfSsoUserRole,
						vfPhoneNumber,
						body)
						.take(1)
						.subscribe(new RequestSessionObserver<GeneralResponse<InternationalCallingRateSuccess>>() {
							@Override
							public void onNext(GeneralResponse<InternationalCallingRateSuccess> response) {
								processReponse(InternationalCallsMsisdnFragment.NumberType.CALLER_NUMBER, callerPhone, calledPhone, response);
							}

							@Override
							public void onCompleted() {
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);

								processError(e);
							}
						}));
	}

	@Override
	public void unsubscribe() {
		for (Subscription subscription : subscriptionList)
			subscription.unsubscribe();
	}
}
