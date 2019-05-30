package ro.vodafone.mcare.android.rest.observables;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Serban Radulescu on 7/18/2018.
 */

public class RetentionObservables {
	private static RetentionObservables instance;

	public static RetentionObservables getInstance() {
		if(instance==null){
			instance = new RetentionObservables();
		}
		return instance;
	}

	private RetentionObservables() {
	}

	public Observable<GeneralResponse<ShopEligibilitySuccess>> getCheckUserEligibilityObservable() {

		return getShopLoginObservable()
				.flatMap(funcShopLoginToShopEligibility);
	}

	private Observable<GeneralResponse<ShopLoginSuccess>> getShopLoginObservable() {
		ShopService shopService = new ShopService(VodafoneController.getInstance());
		return shopService.postShopLogin(null, null);
	}

	private Observable<GeneralResponse<ShopEligibilitySuccess>> getShopEligibilityObservable(String shopSessionToken) {
		ShopService shopService = new ShopService(VodafoneController.getInstance());
		String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn()
				: VodafoneController.getInstance().getUserProfile().getMsisdn();

		return shopService.getShopEligibility(msisdn, shopSessionToken);
	}

	private Observable getObservableError() {
		return Observable.error(new Throwable("Shop Error"));
	}

	private Func1 funcShopLoginToShopEligibility = new Func1<GeneralResponse<ShopLoginSuccess>, Observable<GeneralResponse<ShopEligibilitySuccess>>>() {

		@Override
		public Observable<GeneralResponse<ShopEligibilitySuccess>> call(GeneralResponse<ShopLoginSuccess> generalResponse) {
			if (ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
				RequestSaveRealmObserver.save(generalResponse);
				return getShopEligibilityObservable(generalResponse.getTransactionSuccess().getShopSessionToken());
			}
			return getObservableError();
		}
	};
}
