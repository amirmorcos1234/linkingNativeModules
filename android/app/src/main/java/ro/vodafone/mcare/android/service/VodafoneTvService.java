package ro.vodafone.mcare.android.service;

import android.content.Context;


import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeleteRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.RenameRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class VodafoneTvService extends BaseService {
    public VodafoneTvService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<GetByOperatorSuccess>> getDevicesByOperator(String vfSsoUserRole, String msisdn){
        Observable<GeneralResponse<GetByOperatorSuccess>> observable = RetrofitCall.getInstance().getDevicesByOperator(vfSsoUserRole, msisdn)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> renameActiveDevice(String vfSsoUserRole, String deviceAlias, String externalId, String udid){
        RenameRequest renameRequest = new RenameRequest();
        renameRequest.setAlias(deviceAlias);
        renameRequest.setExternalId(externalId);
        renameRequest.setUdid(udid);

        Observable<GeneralResponse> observable = RetrofitCall.getInstance().renameActiveDevice(vfSsoUserRole, renameRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> deleteActiveDevice(String vfSsoUserRole, String masterUserId, String udid){
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setMasterUserId(masterUserId);
        deleteRequest.setUdid(udid);

        Observable<GeneralResponse> observable = RetrofitCall.getInstance().deleteActiveDevice(vfSsoUserRole, deleteRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ResetPinSuccess>> resetPIN(String vfSsoUserRole, String defaultUserId){
        ResetPinRequest resetPinRequest = new ResetPinRequest();
        resetPinRequest.setDefaultUserId(defaultUserId);

        Observable<GeneralResponse<ResetPinSuccess>> observable = RetrofitCall.getInstance().resetPIN(vfSsoUserRole, resetPinRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}
