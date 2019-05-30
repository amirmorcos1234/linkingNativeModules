package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptInSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptOutSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyPointsSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.shop.ShopCartRequest;
import ro.vodafone.mcare.android.client.model.shop.ShopCartSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginRequest;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlanSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.LoyaltyProgramRequest;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 3/27/2017.
 */

public class ShopService  extends BaseService {

    public ShopService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<ShopLoginSuccess>> postShopLogin(@Nullable String phoneSkuId,@Nullable String shopSessionToken){
        ShopLoginRequest shopLoginRequest = new ShopLoginRequest();
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() : VodafoneController.getInstance().getUserProfile().getMsisdn();
        Log.d("Retention null msisdn", msisdn);
        shopLoginRequest.setMsisdn(msisdn);
        shopLoginRequest.setPhoneSkuId(phoneSkuId);
        shopLoginRequest.setShopSessionToken(shopSessionToken);

        Observable<GeneralResponse<ShopLoginSuccess>> observable =
                RetrofitCall.getInstance().postShopLogin(shopLoginRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopEligibilitySuccess>> getShopEligibility(String msisdn, String shopSessionToken){
        Observable<GeneralResponse<ShopEligibilitySuccess>> observable =
                RetrofitCall.getInstance().getShopEligibility(msisdn, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsRecommended(String pricePlanSkuId, String shopSessionToken){
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                RetrofitCall.getInstance().getShopProductsRecommended(pricePlanSkuId, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsListings(String pricePlanSkuId, String shopSessionToken) {
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                RetrofitCall.getInstance().getShopProductsListings(pricePlanSkuId, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsDetails(String pricePlanSkuId, String phoneSkuId, String shopSessionToken) {
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                RetrofitCall.getInstance().getShopProductsDetails(pricePlanSkuId, phoneSkuId, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansRecommended(String phoneSkuId, String shopSessionToken){
        Observable<GeneralResponse<ShopPricePlanSuccess>> observable =
                RetrofitCall.getInstance().getShopPricePlansRecommended(phoneSkuId, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansListings(String phoneSkuId, String shopSessionToken){
        Observable<GeneralResponse<ShopPricePlanSuccess>> observable =
                RetrofitCall.getInstance().getShopPricePlansListings(phoneSkuId,shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable putShopCart(String phoneSkuId, String pricePlanSkuId,
                                                                    String productId, String cfgSkuId, String shopSessionToken){
        ShopCartRequest shopCartRequest = new ShopCartRequest();
        shopCartRequest.setPhoneSkuId(phoneSkuId);
        shopCartRequest.setPricePlanSkuId(pricePlanSkuId);
        shopCartRequest.setProductId(productId);
        shopCartRequest.setCfgSkuId(cfgSkuId);

        Observable<GeneralResponse<ShopCartSuccess>> observable =
                RetrofitCall.getInstance().putShopCart(shopCartRequest, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopCartSuccess>> deleteShopCart(String phoneSkuId, String pricePlanSkuId,
                                                                    String productId, String cfgSkuId, String shopSessionToken){
        ShopCartRequest shopCartRequest = new ShopCartRequest();
        shopCartRequest.setPhoneSkuId(phoneSkuId);
        shopCartRequest.setPricePlanSkuId(pricePlanSkuId);
        shopCartRequest.setProductId(productId);
        shopCartRequest.setCfgSkuId(cfgSkuId);

        Observable<GeneralResponse<ShopCartSuccess>> observable =
                RetrofitCall.getInstance().deleteShopCart(shopCartRequest, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ShopLoyaltyProgramSuccess>> getLoyaltyProgram(String selectedBan, String shopSessionToken){
        Observable<GeneralResponse<ShopLoyaltyProgramSuccess>>observable =
                RetrofitCall.getInstance().getLoyaltyProgram(selectedBan, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<OptInSuccess>> postEnrollLoyaltyProgram(String selectedBan, String shopSessionToken){
        LoyaltyProgramRequest loyaltyProgramRequest = new LoyaltyProgramRequest();
        loyaltyProgramRequest.setSelectedBan(selectedBan);

        Observable<GeneralResponse<OptInSuccess>> observable =
                RetrofitCall.getInstance().postEnrollLoyaltyProgram(shopSessionToken, loyaltyProgramRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ShopLoyaltyPointsSuccess>> getLoyaltyPoints(String ban, String dateTo, String dateFrom, String shopSessionToken){

        Observable<GeneralResponse<ShopLoyaltyPointsSuccess>> observable =
                RetrofitCall.getInstance().getLoyaltyPoints(ban, dateTo, dateFrom, shopSessionToken)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<OptOutSuccess>> quitLoyaltyProgram(String selectedBan, String shopSessionToken){
       LoyaltyProgramRequest loyaltyProgramRequest = new LoyaltyProgramRequest();
        loyaltyProgramRequest.setSelectedBan(selectedBan);

        Observable<GeneralResponse<OptOutSuccess>> observable =
                RetrofitCall.getInstance().deleteQuitLoyaltyProgram(shopSessionToken,loyaltyProgramRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


}
