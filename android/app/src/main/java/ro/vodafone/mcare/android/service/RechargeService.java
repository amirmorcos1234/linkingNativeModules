package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.util.Log;

import ro.vodafone.mcare.android.client.model.topUp.TransferCreditTerms;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistorySuccess;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.eligibility.Post4preCountersSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.UserPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.recharge.RechargeVoucherSuccess;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRechargesSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.BillRechargeRequest;
import ro.vodafone.mcare.android.rest.requests.RechargeVoucherRequest;
import ro.vodafone.mcare.android.rest.requests.TransferCreditRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user on 28.02.2017.
 */
public class RechargeService extends BaseService {

    public RechargeService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<FavoriteNumbersSuccess>> getFavoriteNumbers(){

        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                RetrofitCall.getInstance().getFavoriteNumbers()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return  observable;
    }

    public Observable<GeneralResponse<FavoriteNumbersSuccess>> addFavoriteNumber(String msisdn, String nickname){

        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                RetrofitCall.getInstance().addFavoriteNumber(msisdn, nickname)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<FavoriteNumbersSuccess>> deleteFavoriteNumber(String vfSsoUsername, String msisdn){

        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                RetrofitCall.getInstance().deleteFavoriteNumber(vfSsoUsername, msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse> validateSubscriber(String msisdn){

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().validateSubscriber(msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<RecommendedRechargesSuccess>> getRecommendedRechargesValues(Integer rechargeNumbers, Integer minAmount){

        Observable<GeneralResponse<RecommendedRechargesSuccess>> observable =
                RetrofitCall.getInstance().getRecommendedRechargesValues(rechargeNumbers, minAmount)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }


    public Observable<GeneralResponse<UserPost4preEligibilitySuccess>> checkMsisdnPost4PreEligibility(String vfPhoneNumber){

        Observable<GeneralResponse<UserPost4preEligibilitySuccess>> observable =
                RetrofitCall.getInstance().checkMsisdnPost4PreEligibility(vfPhoneNumber)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<BanPost4preEligibilitySuccess>> checkBanPost4PreEligibility(String ban){

        Observable<GeneralResponse<BanPost4preEligibilitySuccess>> observable =
                RetrofitCall.getInstance().checkBanPost4PreEligibility(ban)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<Post4preCountersSuccess>> getPost4PreCounters(){
        Log.d("", "Recharge service: getPost4PreCounters");
        Observable<GeneralResponse<Post4preCountersSuccess>> observable = RetrofitCall.getInstance().getPost4PreCounters()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse<BillRechargesSuccess>> getBillRecharges(String ban){
        Log.d("", "Recharge service: getBillRecharges for ban : " + ban);
        Observable<GeneralResponse<BillRechargesSuccess>> observable;
        if(ban!=null) {
            observable = RetrofitCall.getInstance().getBillRecharges(ban)
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        } else {
            observable = RetrofitCall.getInstance().getBillRecharges()
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        }
        return observable;
    }

    public Observable<GeneralResponse<RechargeVoucherSuccess>> rechargeWithVoucher(String prepaidMsisdn, String voucherCode){
        Log.d("", "Recharge service: rechargeWithVoucher");

        RechargeVoucherRequest request = new RechargeVoucherRequest();
        request.setPrepaidMsisdn(prepaidMsisdn);
        request.setVoucherCode(voucherCode);

        Observable<GeneralResponse<RechargeVoucherSuccess>> observable =
                RetrofitCall.getInstance().postRechargeWithVoucher(request)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPostPaid(String ban){
        Log.d("", "Recharge service: getBillRecharges");
        Observable<GeneralResponse<RechargeHistorySuccess>> observable =
                RetrofitCall.getInstance().getHistoryRechargesPostPaid(ban)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPrePaid(){
        Log.d("", "Recharge service: getBillRecharges");
        Observable<GeneralResponse<RechargeHistorySuccess>> observable =
                RetrofitCall.getInstance().getHistoryRechargesPrePaid()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    //Delete recurent recharge for  Weekly recharge type
    public Observable<GeneralResponse<BillRechargesSuccess>> deleteRecurrentWeeklyRecharge(Integer scheduleID, Float amount, BillRechargeRequest.RechargeTypeEnum rechargeType, Integer day, String ban, String msisdn){

            BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
            billRechargeRequest.setScheduleID(scheduleID);
            billRechargeRequest.setAmount(amount);
            billRechargeRequest.setDayInWeek(day);
            billRechargeRequest.setRechargeType(rechargeType);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().deleteBillRecharges(ban, msisdn,billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    //Delete recurent recharge for  Monthly recharge type
    public Observable<GeneralResponse<BillRechargesSuccess>> deleteRecurrentMonthlyRecharge(Integer scheduleID, Float amount, BillRechargeRequest.RechargeTypeEnum rechargeType, Integer dayInMonth, String ban, String msisdn){

        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setScheduleID(scheduleID);
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setDayInMonth(dayInMonth);
        billRechargeRequest.setRechargeType(rechargeType);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().deleteBillRecharges(ban, msisdn,billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }


    //Delete recurent recharge for OneTimeRecharge
    public Observable<GeneralResponse<BillRechargesSuccess>> deleteRecurrentDateRecharge(Integer scheduleID, Float amount, BillRechargeRequest.RechargeTypeEnum rechargeType, Long oneTimeDate, String ban, String msisdn){

        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setScheduleID(scheduleID);
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setOneTimeDate(oneTimeDate);
        billRechargeRequest.setRechargeType(rechargeType);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().deleteBillRecharges(ban, msisdn,billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<BillRechargesSuccess>> addBillBanRechargesImmediatelyPrepaid(Float amount, BillRechargeRequest.RechargeTypeEnum rechargeType, String distinctBAQuery, String ban, String msisdn ){

        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setRechargeType(rechargeType);
        billRechargeRequest.setDistinctBAquery(distinctBAQuery);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().addBillBanRechargesImmediatelyPrepaid(ban,msisdn,billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<BillRechargesSuccess>> addWeeklyProgrammedRecharge(Float amount, BillRechargeRequest.RechargeTypeEnum rechargeTypeEnum, String ban, String msisdn, Integer dayInWeek, String ben){
        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setRechargeType(rechargeTypeEnum);
        billRechargeRequest.setDayInWeek(dayInWeek);
        billRechargeRequest.setDistinctBAquery(ben);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().addWeeklyRecurringRecharge(ban,msisdn,billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<BillRechargesSuccess>> addMonthlyProgrammedRecharge(Float amount, BillRechargeRequest.RechargeTypeEnum rechargeTypeEnum, String ban, String msisdn, Integer dayInMonth, String ben){
        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setRechargeType(rechargeTypeEnum);
        billRechargeRequest.setDayInMonth(dayInMonth);
        billRechargeRequest.setDistinctBAquery(ben);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().addMonthlyRecurringRecharge(ban, msisdn, billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<BillRechargesSuccess>> addDateProgrammedRecharge(Float amount, BillRechargeRequest.RechargeTypeEnum rechargeTypeEnum, String ban, String msisdn, long date, String ben){
        BillRechargeRequest billRechargeRequest = new BillRechargeRequest();
        billRechargeRequest.setAmount(amount);
        billRechargeRequest.setRechargeType(rechargeTypeEnum);
        billRechargeRequest.setOneTimeDate(date);
        billRechargeRequest.setDistinctBAquery(ben);

        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                RetrofitCall.getInstance().addDateRecurringRecharge(ban, msisdn, billRechargeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse> transferCredit(TransferCreditRequest transferCreditRequest, String vfSid, String vfPhoneNumber, String vfSsoUserRole) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().
                        transferCredit(transferCreditRequest, vfSid, vfPhoneNumber, vfSsoUserRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<TransferCreditTerms> getTransferCreditTerms() {

        Observable<TransferCreditTerms> observable =
                RetrofitCall.getInstance().
                        getTransferCreditTerms()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}
