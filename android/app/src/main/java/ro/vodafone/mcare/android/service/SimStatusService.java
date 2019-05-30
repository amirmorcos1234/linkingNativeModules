package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusEBUSuccess;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.SIMChangeStatusRequest;
import ro.vodafone.mcare.android.rest.requests.SIMChangeStatusRequestEBU;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user2 on 4/13/2017.
 */

public class SimStatusService extends BaseService{

    public SimStatusService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<SIMStatusSuccess>> getSimStatus(String sid){

        Observable<GeneralResponse<SIMStatusSuccess>> observable =
                RetrofitCall.getInstance().getSimStatus(sid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<SIMStatusEBUSuccess>> getEbuSimStatus(String sid){

        Observable<GeneralResponse<SIMStatusEBUSuccess>> observable =
                RetrofitCall.getInstance().getEbuSimStatus(sid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse> putSimBlock(String sid, String contactNo) {
        D.w("block sim " + sid);
        SIMChangeStatusRequest simChangeStatusRequest = new SIMChangeStatusRequest(sid, contactNo);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putSimBlock(simChangeStatusRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> putEbuSimBlock(SIMChangeStatusRequestEBU simChangeStatusRequestEBU) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putEbuSimBlock(simChangeStatusRequestEBU,
                        EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole())
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> putSimUnblock(String sid, String contactNo) {
        D.w("unblock sim " + sid);
        SIMChangeStatusRequest simChangeStatusRequest = new SIMChangeStatusRequest(sid, contactNo);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putSimUnblock(simChangeStatusRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> putEbuSimUnblock(SIMChangeStatusRequestEBU simChangeStatusRequestEBU) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putEbuSimUnblock(simChangeStatusRequestEBU,
                        EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole())
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

}
