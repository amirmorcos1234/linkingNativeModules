package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEbuRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelResponse;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.TravallingHintSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALex on 4/29/2017.
 */

public class TravellingAboardService extends BaseService {


    public TravellingAboardService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<AccessTypeSuccess>> getAccessType(String sid){

        Observable<GeneralResponse<AccessTypeSuccess>> observable =
                RetrofitCall.getInstance().getAccessType(sid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<AccessTypeEBU>> getAccessTypeEbu(String sid){

        Observable<GeneralResponse<AccessTypeEBU>> observable = RetrofitCall.getInstance().getAccessTypeEbu(sid)
                                                                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<AccessTypeSuccess>> putAccessType(String msisnd, String sid, AccessTypeRequest accessTypeRequest){

        Observable<GeneralResponse<AccessTypeSuccess>> observable =
                RetrofitCall.getInstance().putAccessType(msisnd, sid, accessTypeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> putAccessTypeEbu(String vfCRMRole, AccessTypeEbuRequest accessTypeEbuRequest){

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().putAccessTypeEbu(vfCRMRole, accessTypeEbuRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffs(boolean isPrepaid, boolean isEbu){
        Observable<GeneralResponse<RoamingTariffsSuccess>> observable;
        if(isEbu) {
            observable = RetrofitCall.getInstance().getRoamingTariffsEbu()
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            return observable;
        }

        if(isPrepaid) {
            observable = RetrofitCall.getInstance().getRoamingTariffsPrepaid()
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            return observable;
        } else {
             observable = RetrofitCall.getInstance().getRoamingTariffsPostpaid()
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
             return observable;
        }
    }


    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHints(boolean isPrepaid, boolean isEbu){
        Observable<GeneralResponse<TravallingHintSuccess>> observable =
                RetrofitCall.getInstance().getTravellingHints()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        if(isPrepaid) {
            observable =
                    RetrofitCall.getInstance().getTravellingHintsPrepaid()
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        }

        if(isEbu){
            observable =
                    RetrofitCall.getInstance().getTravellingHintsEBU()
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        }
        return  observable;
    }

    public Observable<CountryByIp> getCountryListJson() {
        Observable<CountryByIp> observable =
                RetrofitCall.getInstance().getCountryListJson()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


    public Observable<GeneralResponse> checkEligibilityForEbu(String vfOdsCid, String vfOdsSid){

        Observable<GeneralResponse > observable = RetrofitCall.getInstance().checkEligibilityEbu(vfOdsCid,vfOdsSid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;

    }
    public Observable<GeneralResponse<RoamingLabelResponse>> getRoamingLabels(RoamingLabelRequest roamingLabelRequest){

        Observable<GeneralResponse<RoamingLabelResponse>> observable = RetrofitCall.getInstance().getRoamingLabels(roamingLabelRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;

    }


}
