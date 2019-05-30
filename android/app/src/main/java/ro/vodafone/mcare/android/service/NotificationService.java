package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.NotificationRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bogdan.marica on 3/2/2017.
 */

public class NotificationService extends BaseService {


    public NotificationService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<NotificationFlag>> getNotificationFlag() {

        Observable<GeneralResponse<NotificationFlag>> observable =
                RetrofitCall.getInstance().getNotificationFlag()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return observable;
    }

    public Observable<GeneralResponse<NotificationFlag>> setNotificationFlag(NotificationRequest notificationRequest) {
        Observable<GeneralResponse<NotificationFlag>> observable =
                RetrofitCall.getInstance().setNotificationFlag(notificationRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;

    }

}
