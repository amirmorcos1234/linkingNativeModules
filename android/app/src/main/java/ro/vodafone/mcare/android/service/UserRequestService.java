package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequestsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by alexandrulepadatu on 3/16/18.
 */

public class UserRequestService extends BaseService
{
    public UserRequestService(Context context)
    {
        super(context);
    }

    public Observable<GeneralResponse<UserRequestsSuccess>> getUserRequests(boolean vfEBUMigrated,
                                                                            String vfSsoUserRole,
                                                                            String vfOdsCid,
                                                                            String vfOdsBan,
                                                                            String crmRole,
                                                                            boolean pending,
                                                                            boolean accepted,
                                                                            boolean rejected)
    {
        Observable<GeneralResponse<UserRequestsSuccess>> observable =
                RetrofitCall.getInstance().getUserRequests(vfEBUMigrated,
                        vfSsoUserRole,
                        vfOdsCid,
                        vfOdsBan,
                        crmRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

        return observable;
    }

    public Observable<GeneralResponse<UserRequestsSuccess>> postUserRequest(String action,
                                                                            String requestId,
                                                                            String requesterEmail,
                                                                            boolean vfEBUMigrated,
                                                                            String vfSsoUserRole,
                                                                            String vfOdsCid,
                                                                            String vfOdsBan,
                                                                            String crmRole,
                                                                            String requesterName)
    {
        Observable<GeneralResponse<UserRequestsSuccess>> observable = RetrofitCall.getInstance().postUserRequest(action,
                requestId,
                requesterEmail,
                vfEBUMigrated,
                vfSsoUserRole,
                vfOdsCid,
                vfOdsBan,
                crmRole,
                requesterName)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}
