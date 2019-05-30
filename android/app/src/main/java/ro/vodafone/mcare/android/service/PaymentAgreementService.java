package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.paymentAgreement.PaymentAgreementSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.PaymentAgreementRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user2 on 4/25/2017.
 */

public class PaymentAgreementService extends BaseService{

    public PaymentAgreementService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<PaymentAgreementSuccess>> getPaymentAgreement(String segment, String phone, String crmRole){

        Observable<GeneralResponse<PaymentAgreementSuccess>> observable =
                RetrofitCall.getInstance().getPaymentAgreement(segment, phone, crmRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<PaymentAgreementSuccess>> putPaymentAgreement(Long agreementDate, String accountId, String userStatus, String phone,String crmRole) {

        PaymentAgreementRequest paymentAgreementRequest = new PaymentAgreementRequest();
        paymentAgreementRequest.setAgreementDate(agreementDate);
        paymentAgreementRequest.setAccountId(accountId);
        paymentAgreementRequest.setUserStatus(userStatus);
        paymentAgreementRequest.setPhone(phone);
        paymentAgreementRequest.setCrmRole(crmRole);

        Observable<GeneralResponse<PaymentAgreementSuccess>> observable =
                RetrofitCall.getInstance().putPaymentAgreement(paymentAgreementRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<PaymentAgreementSuccess>> postPaymentAgreement(String ban){

        Observable<GeneralResponse<PaymentAgreementSuccess>> observable = RetrofitCall.getInstance().postPaymentAgreement(ban).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

}
