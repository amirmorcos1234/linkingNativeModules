package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.application.controllers.IonController;
import ro.vodafone.mcare.android.client.model.ion.IONPostpaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IONPrepaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IonEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UnlimitedIonService extends BaseService {

    public UnlimitedIonService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPrepaid(String vfSid, String vfSsoUserRole) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable =
                RetrofitCall.getInstance().getUnlimitedIonPrepaid(vfSid, vfSsoUserRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPostpaid(String vfSsoUserRole, String selectedMsisdn) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable =
                RetrofitCall.getInstance().getUnlimitedIonPostpaid(vfSsoUserRole, selectedMsisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse> putUnlimitedIonPrepaid(String vfSid, String vfCid, String vfSsoUsername,
                                                              String vfPhoneNumber, String vfSsoEmail, String vfSsoUserRole,
                                                              IONPrepaidRequest ionPrepaidRequest) {
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putUnlimitedIonPrepaid(vfSid, vfCid, vfSsoUsername, vfPhoneNumber, vfSsoEmail, vfSsoUserRole, ionPrepaidRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;

    }

    public Observable<GeneralResponse> putUnlimitedIonPostpaid(String vfCid, String vfSsoUsername, String vfSsoEmail,
                                                               String vfSsoUserRole, IONPostpaidRequest ionPostpaidRequest) {
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putUnlimitedIonPostpaid(vfCid, vfSsoUsername, vfSsoEmail, vfSsoUserRole, ionPostpaidRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;

    }

    public Observable<GeneralResponse> deleteUnlimitedIonPrepaid(String vfSid, String vfCid, String vfSsoUsername, String vfPhoneNumber,
                                                                 String vfSsoEmail, String vfSsoUserRole, IONPrepaidRequest ionPrepaidRequest) {
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().deleteUnlimitedIonPrepaid(vfSid, vfCid, vfSsoUsername, vfPhoneNumber, vfSsoEmail, vfSsoUserRole, ionPrepaidRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse> deleteUnlimitedIonPostpaid(String vfCid, String vfSsoUsername, String vfSsoEmail,
                                                                  String vfSsoUserRole, IONPostpaidRequest ionPostpaidRequest) {
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().deleteUnlimitedIonPostpaid(vfCid, vfSsoUsername, vfSsoEmail, vfSsoUserRole, ionPostpaidRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

}
