package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cosmin deliu on 2/6/2018.
 */

public class GdprService extends BaseService {

    public GdprService(Context context) {
        super(context);
    }


    //Todo Code cleanup - @key should have a better name or a description ( is a realm key that is saved after? is a request key?
    public Observable<GeneralResponse<GdprGetResponse>> getPermissions(String vfPhoneNumber, String vfSid, boolean refreshData, int key) {
        D.w();//Todo Code cleanup
        Observable<GeneralResponse<GdprGetResponse>> observable =
                RetrofitCall.getInstance().getPermissions(vfPhoneNumber, vfSid, refreshData, key)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse> setPermissions(GdprPermissions gdprPermissions, String vfSsoUsername, String customerId, String requestType, String phone) {
        D.w();//Todo Code cleanup
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().setPermissions(gdprPermissions, vfSsoUsername, customerId, requestType, phone)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }
}
