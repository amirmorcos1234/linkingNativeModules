package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.application.controllers.TemporaryFlagController;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReserveSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.LoyaltySegmentRequest;
import ro.vodafone.mcare.android.rest.requests.ReserveVoucherRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Serban Radulescu on 9/5/2017.
 */

public class LoyaltyMarketService extends BaseService {

    public LoyaltyMarketService(Context context) {
        super(context);
    }

    //API 68 Loyalty segment @GET and @POST("/api/shop/loyalty/segment/")
    public Observable<GeneralResponse<LoyaltySegmentSuccess>> getLoyaltySegment(String crmRole,
                                                                                LoyaltySegmentRequest loyaltySegmentRequest) {

        Observable<GeneralResponse<LoyaltySegmentSuccess>> observable;
        if(loyaltySegmentRequest != null) {
            observable = RetrofitCall.getInstance().postEbuLoyaltySegment(crmRole, loyaltySegmentRequest, true)
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
        } else {
             observable = RetrofitCall.getInstance().getLoyaltySegment(crmRole, true)
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
        }

        return observable;
    }

    public Observable<GeneralResponse<LoyaltySegmentSuccess>> getLoyaltySegmentIgnoreSession(String crmRole,
                                                                                             LoyaltySegmentRequest loyaltySegmentRequest) {

        Observable<GeneralResponse<LoyaltySegmentSuccess>> observable;
        if(loyaltySegmentRequest != null) {
            observable = RetrofitCall.getInstance().postEbuLoyaltySegment(crmRole, loyaltySegmentRequest, false)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
        } else {
            observable = RetrofitCall.getInstance().getLoyaltySegment(crmRole, false)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
        }
        return observable;
    }


    //API 69 Loyalty vouchers @GET("/api/shop/loyalty/voucher/")
    public Observable<GeneralResponse<LoyaltyVoucherSuccess>> getLoyaltyVoucherList(String loyaltySegment,
                                                                                    String treatmentSegment,
                                                                                    String crmRole) {


        Observable<GeneralResponse<LoyaltyVoucherSuccess>> observable =
                RetrofitCall.getInstance().getLoyaltyVoucherList(loyaltySegment, treatmentSegment, crmRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }


    //API 69 Loyalty reserved vouchers @GET("/api/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReservedSuccess>> getReservedLoyaltyVoucherList() {

        Observable<GeneralResponse<LoyaltyVoucherReservedSuccess>> observable =
                RetrofitCall.getInstance().getReservedLoyaltyVoucherList()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    //API 69 Loyalty reserved vouchers @POST("/api/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReserveSuccess>> postReservedVoucher(ReserveVoucherRequest reserveVoucherRequest) {


        Observable<GeneralResponse<LoyaltyVoucherReserveSuccess>> observable =
                RetrofitCall.getInstance().postReservedVoucher(reserveVoucherRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }
}
