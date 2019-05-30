package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.Configurations;
import ro.vodafone.mcare.android.client.model.realm.system.Labels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.SystemStatusSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 1/9/2017.
 */

public class ResourcesService  extends BaseService {

    public ResourcesService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<Labels>> retrieveLabels(){
        //TODO get last update, if need ( 24h passed)
       // String last_update= String.valueOf(System.currentTimeMillis());
        String last_update= "1";

        Observable<GeneralResponse<Labels>> observable =
                RetrofitCall.getInstance().retrieveLabels(last_update)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<Configurations>> retrieveConfigurations(){
        //TODO get last update, if need ( 24h passed)
        String last_update="1";

        Observable<GeneralResponse<Configurations>> observable =
                RetrofitCall.getInstance().retrieveConfigurations(last_update)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<SystemStatusSuccess>> getSystemStatus(){

        Observable<GeneralResponse<SystemStatusSuccess>> observable =
                RetrofitCall.getInstance().getSystemStatus()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }
}
