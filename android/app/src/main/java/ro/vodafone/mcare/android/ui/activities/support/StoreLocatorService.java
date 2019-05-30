package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;

import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.service.BaseService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bogdan Marica on 7/10/2017.
 */

public class StoreLocatorService extends BaseService {
    public StoreLocatorService(Context context) {
        super(context);
    }

    public Observable<String> getLocationsList() {

        Observable<String> observable =
                RetrofitCall.getInstance().getLocationsList()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}
