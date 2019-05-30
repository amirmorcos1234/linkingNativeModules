package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.eligibility.EBillSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.EBillRequest;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bogdan marica on 3/19/2017.
 */

public class ElectronicBillService extends BaseService {

    public ElectronicBillService(Context context) {
        super(context);
    }


    public Observable<GeneralResponse<EBillSuccess>> getEBillBan(String ban, String vfSsoUserRole, String vfSsoUsername) {
        D.w();
        Observable<GeneralResponse<EBillSuccess>> observable =
                RetrofitCall.getInstance().getEBillBan(ban, vfSsoUserRole, vfSsoUsername)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<EBillSuccess>> putEBillBan(EBillRequest eBillRequest, String ban, String vfSsoUserRole, String vfSsoUsername) {
        D.w();
        Observable<GeneralResponse<EBillSuccess>> observable =
                RetrofitCall.getInstance().putEBillBan(eBillRequest, ban, vfSsoUserRole, vfSsoUsername)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse> patchEBillBan(EBillRequest eBillRequest, String ban, String vfSsoUserRole, String vfSsoUsername) {
        D.w();
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().patchEBillBan(eBillRequest, ban, vfSsoUserRole, vfSsoUsername)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<EBillSuccess>> getEBillEBUBan(String ban,
                                                                    String vfSsoUserRole,
                                                                    String vfCRMRole,
                                                                    String vfOdsCid,
                                                                    String vfOdsEntityType ,
                                                                    String vfOdsEntityId,
                                                                    String vfContactID,
                                                                    String vfSsoLastName,
                                                                    String vfSsoFirstName) {
        D.w();
        Observable<GeneralResponse<EBillSuccess>> observable =
                RetrofitCall.getInstance().getEBillEBUBan(ban, vfSsoUserRole, vfCRMRole, vfOdsCid,
                        vfOdsEntityType, vfOdsEntityId,vfContactID, vfSsoLastName, vfSsoFirstName)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<EBillSuccess>> putEBillEBUBan(EBillRequest eBillRequest,
                                                                    String ban,
                                                                    String vfSsoUserRole,
                                                                    String vfCRMRole,
                                                                    String vfSsoUsername,
                                                                    String vfOdsCid,
                                                                    String vfOdsEntityType,
                                                                    String vfOdsEntityId,
                                                                    String vfContactID,
                                                                    String vfSsoFirstName,
                                                                    String vfSsoLastName,
                                                                    String vfOdsSsn,
                                                                    String vfOdsBen) {
        D.w();
        Observable<GeneralResponse<EBillSuccess>> observable =
                RetrofitCall.getInstance().putEBillEBUBan(eBillRequest, ban,vfSsoUserRole,
                        vfCRMRole,vfSsoUsername, vfOdsCid,vfOdsEntityType,vfOdsEntityId,
                        vfContactID, vfSsoFirstName, vfSsoLastName, vfOdsSsn, vfOdsBen)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse> patchEBillEBUBan(EBillRequest eBillRequest,
                                                        String ban,
                                                        String vfSsoUserRole,
                                                        String vfCRMRole,
                                                        String vfSsoUsername,
                                                        String vfOdsCid,
                                                        String vfOdsEntityType,
                                                        String vfOdsEntityId,
                                                        String vfContactID,
                                                        String vfSsoLastName,
                                                        String vfSsoFirstName) {
        D.w();
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().patchEBillEBUBan(eBillRequest, ban, vfSsoUserRole,
                        vfCRMRole, vfSsoUsername,vfOdsCid, vfOdsEntityType,vfOdsEntityId,
                        vfContactID, vfSsoLastName,vfSsoFirstName)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }
}
