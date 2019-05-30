package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.simDetails.SIMDetailsSuccess;
import ro.vodafone.mcare.android.client.model.simReplace.SIMReplaceSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.SimReplaceBean;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user2 on 4/14/2017.
 */

public class SimDetailsService extends BaseService {

    public SimDetailsService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<SIMDetailsSuccess>> getSimDetails(String sid){

        Observable<GeneralResponse<SIMDetailsSuccess>> observable =
                RetrofitCall.getInstance().getSimDetails(sid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<SIMDetailsSuccess>> getEbuSimDetails(String msisdn, String vfOdsSid){
        Observable<GeneralResponse<SIMDetailsSuccess>> observable =
                RetrofitCall.getInstance().getEbuSimDetails(msisdn, vfOdsSid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<SIMReplaceSuccess>> postReplaceSim(String selectedMsisdn, String vfEBUMigrated,
                                                                         String vfContactID, String vfSid,
                                                                         String vfCid, SimReplaceBean simReplaceBean) {
        Observable<GeneralResponse<SIMReplaceSuccess>> observable =
                RetrofitCall.getInstance().postReplaceSim(selectedMsisdn, vfEBUMigrated, vfContactID, vfSid, vfCid, simReplaceBean)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

}
