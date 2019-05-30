package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.support.annotation.Nullable;

import ro.vodafone.mcare.android.client.model.identity.BenSuccessEbu;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Serban Radulescu on 7/14/2017.
 */

public class UserIdentitiesService extends BaseService{
    public UserIdentitiesService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<UserEntitiesSuccess>> getUserDetails(String vfContactID, boolean vfEBUMigrated, String vfSsoUserRole, String vfMaCustomer){

        Observable<GeneralResponse<UserEntitiesSuccess>> observable =
                RetrofitCall.getInstance().getIdentities(vfContactID, vfEBUMigrated, vfSsoUserRole, vfMaCustomer)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<UserEntitiesSuccess>> putDefaultUserIdentity(String entityId){

        Observable<GeneralResponse<UserEntitiesSuccess>> observable =
                RetrofitCall.getInstance().putDefaultIdentity(entityId)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EntityDetailsSuccess>> getUserDetails(String vfodsCid){

        Observable<GeneralResponse<EntityDetailsSuccess>> observable =
                RetrofitCall.getInstance().getUserDetails(vfodsCid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<EntityChildItem>>  getInverseHierarchy(String entityId, String entityType,@Nullable String vfOdsPhoneNumber){

        Observable<GeneralResponse<EntityChildItem>> observable =
                RetrofitCall.getInstance().getInverseHierarchy(entityId, entityType,vfOdsPhoneNumber)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<CustomerRestrictionsSuccess>>  getIdentityCostumerRestriction(String vfOdsSsn){
        Observable<GeneralResponse<CustomerRestrictionsSuccess>> observable =
                RetrofitCall.getInstance().getIdentityCostumerRestriction(vfOdsSsn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<BenSuccessEbu>> getEbuBen(String vfOdsBan){
        Observable<GeneralResponse<BenSuccessEbu>> observable =
                RetrofitCall.getInstance().getEbuBen(vfOdsBan)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }
}
