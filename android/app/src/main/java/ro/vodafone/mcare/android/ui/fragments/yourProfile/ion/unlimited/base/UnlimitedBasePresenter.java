package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.IonController;
import ro.vodafone.mcare.android.client.model.ion.IONPostpaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IONPrepaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IonEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.ion.IonModel;
import ro.vodafone.mcare.android.client.model.ion.PhoneNumberPrefix;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UnlimitedIonService;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.mvp.BaseMVPPresenter;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;

public class UnlimitedBasePresenter extends BaseMVPPresenter<Contract.ContractView> implements Contract.ContractPresenter {

    private int maximumLimit = 5;
    private int numberOfMsisdns = 0;
    private int minLengthPhoneNumber = 9;
    private int maxLengthPhoneNumber = 10;

    private Realm realm;
    private String defaultPrefix;
    private Subscription subscription;
    private List<IonModel> msisdnList;
    private List<String> prefixes;
    private IonEligibilitySuccess ionEligibilitySuccess;
    private HashMap<String, PhoneNumberPrefix> prefixesRestrictions;

    public UnlimitedBasePresenter(Contract.ContractView view) {
        super(view);
    }

    @Override
    public void initRealm() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void getUnlimitedIon(Context context) {
        if (subscription != null)
            subscription.unsubscribe();

        if (getView() == null)
            return;

        getView().showLoadingDialog();

        RequestSessionObserver<GeneralResponse<IonEligibilitySuccess>> sessionObserver
                = new RequestSessionObserver<GeneralResponse<IonEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<IonEligibilitySuccess> generalResponse) {
                manageGetUnlimitedIonResponse(generalResponse);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
                getView().setParentVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
                getView().manageErrorCodes(IonController.SYSTEM_ION_ERROR, IonController.LAYOUT_ION_ERROR);
                getView().setParentVisibility(View.VISIBLE);
            }

        };

        if (IonController.isPrepaidUser()) {
            subscription = getUnlimitedIonForPrepaid(context).subscribe(sessionObserver);
            return;
        }

        subscription = getUnlimitedIonForPostpaid(context).subscribe(sessionObserver);
    }

    @Override
    public void putUnlimitedIon(Context context, String ionPhoneNumber) {
        if (subscription != null)
            subscription.unsubscribe();

        if (getView() == null)
            return;

        getView().showLoadingDialog();

        RequestSessionObserver<GeneralResponse> sessionObserver
                = new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse generalResponse) {
                managePutUnlimitedIonResponse(generalResponse);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
                getView().manageErrorCodes(IonController.SYSTEM_ION_ERROR, IonController.TOAST_ION_ERROR);
            }
        };

        if (IonController.isPrepaidUser()) {
            subscription = putUnlimitedForPrepaid(context, ionPhoneNumber).subscribe(sessionObserver);
            return;
        }

        subscription = putUnlimitedForPostpaid(context, ionPhoneNumber).subscribe(sessionObserver);
    }

    @Override
    public void deleteUnlimitedIon(Context context, String ionPhoneNumber) {
        if (subscription != null)
            subscription.unsubscribe();

        if (getView() == null)
            return;

        getView().showLoadingDialog();

        RequestSessionObserver<GeneralResponse> sessionObserver
                = new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse generalResponse) {
                manageDeleteUnlimitedIonResponse(generalResponse);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                if (getView() == null)
                    return;

                getView().stopLoadingDialog();
                getView().manageErrorCodes(IonController.SYSTEM_ION_ERROR, IonController.TOAST_ION_ERROR);
            }
        };

        if (IonController.isPrepaidUser()) {
            subscription = deleteUnlimitedForPrepaid(context, ionPhoneNumber).subscribe(sessionObserver);
            return;
        }

        subscription = deleteUnlimitedForPostpaid(context, ionPhoneNumber).subscribe(sessionObserver);
    }

    private Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonForPrepaid(Context context) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable;
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();
        if (VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)
            vfSid = "";
        observable = new UnlimitedIonService(context).getUnlimitedIonPrepaid(vfSid, vfSsoUserRole);
        return observable;
    }

    private Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonForPostpaid(Context context) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable;
        String vfSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
        observable = new UnlimitedIonService(context).getUnlimitedIonPostpaid(vfSid, selectedMsisdn);
        return observable;
    }

    private Observable<GeneralResponse> putUnlimitedForPrepaid(Context context, String ionPhoneNumber) {
        Observable<GeneralResponse> observable;

        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();
        String vfCid = VodafoneController.getInstance().getUserProfile().getCid();
        String vfSsoUsername = VodafoneController.getInstance().getUserProfile().getUserName();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSsoEmail = VodafoneController.getInstance().getUserProfile().getEmail();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();

//        if (VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)
//            vfSid = "";

        IONPrepaidRequest ionPrepaidRequest = new IONPrepaidRequest();
        ionPrepaidRequest.setOfferId(ionEligibilitySuccess.getOfferId());
        ionPrepaidRequest.setIonPhoneNo(ionPhoneNumber);

        observable = new UnlimitedIonService(context).putUnlimitedIonPrepaid(vfSid, vfCid, vfSsoUsername,
                vfPhoneNumber, vfSsoEmail, vfSsoUserRole, ionPrepaidRequest);

        return observable;
    }

    private Observable<GeneralResponse> putUnlimitedForPostpaid(Context context, String ionPhoneNumber) {
        Observable<GeneralResponse> observable;

        String vfCid = VodafoneController.getInstance().getUserProfile().getCid();
        String vfSsoUsername = VodafoneController.getInstance().getUserProfile().getUserName();
        String vfSsoEmail = VodafoneController.getInstance().getUserProfile().getEmail();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String selectedSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();

        IONPostpaidRequest ionPostpaidRequest = new IONPostpaidRequest();
        ionPostpaidRequest.setOfferId(ionEligibilitySuccess.getOfferId());
        ionPostpaidRequest.setIonPhoneNo(ionPhoneNumber);
        ionPostpaidRequest.setSelectdSid(selectedSid);
        ionPostpaidRequest.setSelectedMsisdn(selectedMsisdn);

        observable = new UnlimitedIonService(context).putUnlimitedIonPostpaid(vfCid, vfSsoUsername,
                vfSsoEmail, vfSsoUserRole, ionPostpaidRequest);
        return observable;
    }

    private Observable<GeneralResponse> deleteUnlimitedForPrepaid(Context context, String ionPhoneNumber) {
        Observable<GeneralResponse> observable;

        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();
        String vfCid = VodafoneController.getInstance().getUserProfile().getCid();
        String vfSsoUsername = VodafoneController.getInstance().getUserProfile().getUserName();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSsoEmail = VodafoneController.getInstance().getUserProfile().getEmail();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();

//        if (VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)
//            vfSid = "";

        IONPrepaidRequest ionPrepaidRequest = new IONPrepaidRequest();
        ionPrepaidRequest.setOfferId(ionEligibilitySuccess.getOfferId());
        ionPrepaidRequest.setIonPhoneNo(ionPhoneNumber);

        observable = new UnlimitedIonService(context).deleteUnlimitedIonPrepaid(vfSid, vfCid, vfSsoUsername,
                vfPhoneNumber, vfSsoEmail, vfSsoUserRole, ionPrepaidRequest);

        return observable;
    }

    private Observable<GeneralResponse> deleteUnlimitedForPostpaid(Context context, String ionPhoneNumber) {
        Observable<GeneralResponse> observable;

        String vfCid = VodafoneController.getInstance().getUserProfile().getCid();
        String vfSsoUsername = VodafoneController.getInstance().getUserProfile().getUserName();
        String vfSsoEmail = VodafoneController.getInstance().getUserProfile().getEmail();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String selectedSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();

        IONPostpaidRequest ionPostpaidRequest = new IONPostpaidRequest();
        ionPostpaidRequest.setOfferId(ionEligibilitySuccess.getOfferId());
        ionPostpaidRequest.setIonPhoneNo(ionPhoneNumber);
        ionPostpaidRequest.setSelectdSid(selectedSid);
        ionPostpaidRequest.setSelectedMsisdn(selectedMsisdn);

        observable = new UnlimitedIonService(context).deleteUnlimitedIonPostpaid(vfCid, vfSsoUsername,
                vfSsoEmail, vfSsoUserRole, ionPostpaidRequest);
        return observable;
    }

    private void manageGetUnlimitedIonResponse(GeneralResponse<IonEligibilitySuccess> generalResponse) {
        if (getView() == null)
            return;

        if (IonController.checkGetResponse(IonController.CHECK_ION_SUCCESS_RESPONSE, generalResponse)) {
            ionEligibilitySuccess = generalResponse.getTransactionSuccess();
            if (IonController.checkListFnfNumbersNotEmpty(ionEligibilitySuccess.getFnfNumbers()))
                createIonModelList();
            if (IonController.checkListPrefixesNotEmpty(ionEligibilitySuccess.getPrefixes()))
                createPrefixesList();
            getView().createCards();
            return;
        }

        getView().manageErrorCodes(IonController.checkGetResponse(IonController.CHECK_ION_FAULT_RESPONSE, generalResponse)
                ? generalResponse.getTransactionFault().getFaultCode()
                : IonController.SYSTEM_ION_ERROR, IonController.LAYOUT_ION_ERROR);
    }

    private void managePutUnlimitedIonResponse(GeneralResponse generalResponse) {
        if (getView() == null)
            return;

        if (IonController.checkPutAndDeleteResponse(IonController.CHECK_ION_SUCCESS_RESPONSE, generalResponse)) {
            getView().dismissDialogIfSuccessful();
            getView().redirectVovInDashboardAfterPut();
            return;
        }

        getView().manageErrorCodes(IonController.checkPutAndDeleteResponse(IonController.CHECK_ION_FAULT_RESPONSE, generalResponse)
                ? generalResponse.getTransactionFault().getFaultCode()
                : IonController.SYSTEM_ION_ERROR, IonController.TOAST_ION_ERROR);

//        getView().manageErrorCodes("EC07302", IonController.TOAST_ION_ERROR);
    }

    private void manageDeleteUnlimitedIonResponse(GeneralResponse generalResponse) {
        if (getView() == null)
            return;

        if (IonController.checkPutAndDeleteResponse(IonController.CHECK_ION_SUCCESS_RESPONSE, generalResponse)) {
            getView().redirectVovInDashboardAfterDelete();
            return;
        }

        getView().manageErrorCodes(IonController.checkPutAndDeleteResponse(IonController.CHECK_ION_FAULT_RESPONSE, generalResponse)
                ? generalResponse.getTransactionFault().getFaultCode()
                : IonController.SYSTEM_ION_ERROR, IonController.TOAST_ION_ERROR);
    }

    private void createIonModelList() {
        msisdnList = new ArrayList<>();
        maximumLimit = ionEligibilitySuccess.getLimitNumbers();

        for (String number : ionEligibilitySuccess.getFnfNumbers())
            if (!TextUtils.isEmpty(number))
                msisdnList.add(new IonModel(number, findPrefixAndGetCountry(number)));

        numberOfMsisdns = msisdnList.size();
    }

    private void createPrefixesList() {
        prefixes = new ArrayList<>();
        prefixesRestrictions = new HashMap<>();

        defaultPrefix = ionEligibilitySuccess.getPrefixes().get(0).getPrefix();
        for (PhoneNumberPrefix prefix : ionEligibilitySuccess.getPrefixes()) {
            prefixes.add(prefix.getPrefix());
            prefixesRestrictions.put(prefix.getPrefix(), prefix);
            if (prefix.getCountry().equalsIgnoreCase(IonController.DEFAULT_COUNTRY_PREFIX))
                defaultPrefix = prefix.getPrefix();
        }

    }

    private String findPrefixAndGetCountry(String number) {
        for (PhoneNumberPrefix prefix : ionEligibilitySuccess.getPrefixes()) {
            if (number.startsWith(prefix.getPrefix())) {
                return prefix.getCountry();
            }
        }
        return null;
    }

    @Override
    public int getMaximumLimit() {
        return maximumLimit;
    }

    @Override
    public int getNumberOfMsisdns() {
        return numberOfMsisdns;
    }

    @Override
    public List<IonModel> getMsisdnList() {
        return msisdnList;
    }

    @Override
    public List<String> getPrefixes() {
        return prefixes;
    }

    @Override
    public boolean prefixIsValid(String prefix) {
        boolean valid = !TextUtils.isEmpty(prefix)
                && prefixesRestrictions != null
                && prefixesRestrictions.get(prefix) != null;

        if (valid)
            changePhoneNumberRestrictions(prefix);
        else {
            minLengthPhoneNumber = 9;
            maxLengthPhoneNumber = 10;
        }

        return valid;
    }

    @Override
    public void changePhoneNumberRestrictions(String prefix) {
        minLengthPhoneNumber = prefixesRestrictions.get(prefix).getMinLength();
        maxLengthPhoneNumber = prefixesRestrictions.get(prefix).getMaxLength();
    }

    @Override
    public int getMaxLengthMsisdnAfterPrefix() {
        return maxLengthPhoneNumber;
    }

    @Override
    public int getMinLengthMsisdnAfterPrefix() {
        return minLengthPhoneNumber;
    }

    @Override
    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    @Override
    public Profile getProfileFromRealm() {
        if (realm == null)
            initRealm();
        return (Profile) RealmManager.getRealmObject(realm, Profile.class);
    }
}
