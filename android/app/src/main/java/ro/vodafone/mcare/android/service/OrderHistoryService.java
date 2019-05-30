package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrdersSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user2 on 4/19/2017.
 */

public class OrderHistoryService extends BaseService {

    public OrderHistoryService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrderHistoryList(String shopSessionToken, String vfSsoId, String vfSsoUserRole, String vfSsoUsername){

        Observable<GeneralResponse<ShopOrdersSuccess>> observable =
                RetrofitCall.getInstance().getOrdersHistory(shopSessionToken, vfSsoId, vfSsoUserRole, vfSsoUsername)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrderHistoryDetails(String orderId, String shopSessionToken, String vfSsoUserRole, String vfSsoUsername){

        Observable<GeneralResponse<ShopOrdersSuccess>> observable =
                RetrofitCall.getInstance().getOrderHistoryDetails(shopSessionToken, vfSsoUserRole, vfSsoUsername, orderId)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}
