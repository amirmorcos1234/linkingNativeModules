package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRateSuccess;
import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRatesRequest;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InternationalCostRatesService extends BaseService
{
    public InternationalCostRatesService(Context context)
    {
        super(context);
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOwn(String vfPhoneNumber,
                                                                                  String vfSsoUserRole,
                                                                                  String phone,
                                                                                  String calledPhone,
                                                                                  String selectedMsisdn,
                                                                                  String crmRole,
                                                                                  boolean vfEBUMigrated)
    {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = RetrofitCall.getInstance().getIcrOwn(vfPhoneNumber,
                vfSsoUserRole,
                phone,
                calledPhone,
                selectedMsisdn,
                crmRole,
                vfEBUMigrated)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOther(String vfSsoUserRole,
                                                                                    String callerPhone,
                                                                                    String calledPhone,
                                                                                    String crmRole)
    {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = RetrofitCall.getInstance().getIcrOther(vfSsoUserRole,
                callerPhone,
                calledPhone,
                crmRole)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcr(String crmRole,
                                                                               String vfSsoUserRole,
                                                                               String vfPhoneNumber,
                                                                               InternationalCallingRatesRequest body)
    {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = RetrofitCall.getInstance().getIcr(crmRole,
                vfSsoUserRole,
                vfPhoneNumber,
                body)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrSms(String phone)
    {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = RetrofitCall.getInstance().getIcrSms(phone)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }
}
