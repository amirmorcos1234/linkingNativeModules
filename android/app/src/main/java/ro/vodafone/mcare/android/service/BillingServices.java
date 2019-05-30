package ro.vodafone.mcare.android.service;

import android.content.Context;

import okhttp3.ResponseBody;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.billing.GetPaymentInputsResponse;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.topUp.TransferCreditTerms;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.PaymentRequest;
import ro.vodafone.mcare.android.rest.requests.RechargeRequest;
import ro.vodafone.mcare.android.rest.requests.TransferCreditRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by  Bivol Pavel on 02.02.2017.
 */
public class BillingServices extends BaseService {

    public BillingServices(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<InvoiceDetailsSuccess>> getInvoiceDetails(String msisdn, String ban) {
        Observable<GeneralResponse<InvoiceDetailsSuccess>> observable =
                RetrofitCall.
                        getInstance().
                        getInvoiceDetails(msisdn, ban)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }


    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentBill(String phoneNumber, Float billAmount, String email, String accountNo, String invoiceNo) {

        PaymentRequest paymentRequest = new PaymentRequest(phoneNumber, billAmount, email, accountNo, invoiceNo);

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                RetrofitCall.
                        getInstance()
                        .doPaymentBill(paymentRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }
    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentBill(String phoneNumber, Float billAmount, String email, String accountNo, String invoiceNo,String token,boolean isSave) {

        PaymentRequest paymentRequest = new PaymentRequest(phoneNumber, billAmount, email, accountNo, invoiceNo,token,isSave);

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                RetrofitCall.
                        getInstance()
                        .doPaymentBill(paymentRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    /*public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentRecharge(String phoneNumber, Float billAmount, String email, String accountNo, String invoiceNo) {

        PaymentRequest paymentRequest = new PaymentRequest(phoneNumber, billAmount, email, accountNo, invoiceNo);

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                RetrofitCall.
                        getInstance().
                        doPaymentRecharge(paymentRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }*/
    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentRecharge(String phoneNumber, Float billAmount, String email, String accountNo, String invoiceNo,String token,boolean save) {

        PaymentRequest paymentRequest = new PaymentRequest(phoneNumber, billAmount, email, accountNo, invoiceNo,token,save);

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                RetrofitCall.
                        getInstance().
                        doPaymentRecharge(paymentRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse<GetPaymentInputsResponse>> doRechargeAndActivate(String phoneNumber, Float billAmount, String email, String offer_id, String subscriberId) {

        RechargeRequest rechargeRequest = new RechargeRequest(phoneNumber, billAmount, email, offer_id, subscriberId);

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                RetrofitCall.
                        getInstance().
                        doRechargeAndActivate(rechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse<BillHistorySuccess>> getBillingHistory(String ben, String vfCid, String vfOdsCid, String crmRole, String ban, int months) {

        Observable<GeneralResponse<BillHistorySuccess>> observable =
                RetrofitCall.
                        getInstance().
                        getBillHistory(ben, vfCid, vfOdsCid, crmRole, ban, months)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse<BillSummarySuccess>> getBillSummary(String vfCid, String vfodsCid, String ban, long bcd, boolean term) {

        Observable<GeneralResponse<BillSummarySuccess>> observable =
                RetrofitCall.
                        getInstance().
                        getBillSummary(vfCid, vfodsCid, ban, bcd, term)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<BillSummaryDetailsSuccess>> getBillSumaryDetails(String vfCid, String vfodsCid, String ban, String sid, long bcd) {

        Observable<GeneralResponse<BillSummaryDetailsSuccess>> observable =
                RetrofitCall.
                        getInstance().
                        getBillSummaryDetails(vfCid, vfodsCid, ban, sid, bcd)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<ResponseBody> downloadBill(String ban, long date, String detailed) {

        Observable<ResponseBody> observable =
                RetrofitCall.
                        getInstance().
                        downloadBill(ban, date, detailed)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


}
