package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.util.Log;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationRequest;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user1 on 8/24/2017.
 */

public class PaymentConfirmationService extends BaseService {

    public PaymentConfirmationService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<PaymentConfirmationSuccess>> getPaymentConfirmation(String segment, String phone, String crmRole) {
        Log.d("", "Retrofit call: getPaymentConfirmation");
        Observable<GeneralResponse<PaymentConfirmationSuccess>> observable =
                RetrofitCall.getInstance().getPaymentConfirmation(segment, phone, crmRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<PaymentConfirmationSuccess>> postPaymentConfirmation(PaymentConfirmationRequest paymentConfirmationRequest, String vfOdsCid, String vfOdsSid) {
        Log.d("", "Retrofit call: postPaymentConfirmation");
        Observable<GeneralResponse<PaymentConfirmationSuccess>> observable =
                RetrofitCall.getInstance().postPaymentConfirmation(paymentConfirmationRequest, vfOdsCid, vfOdsSid).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }
}
