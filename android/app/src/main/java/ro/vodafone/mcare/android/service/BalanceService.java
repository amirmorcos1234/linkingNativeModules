package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Daniel Slavic
 */

public class BalanceService extends BaseService {

    public BalanceService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<BalanceCreditSuccess>> getBalanceCredit(boolean verifyTimeToLeave){

        Observable<GeneralResponse<BalanceCreditSuccess>> observable =
                RetrofitCall.getInstance().getPrepaidBalanceCredit(verifyTimeToLeave)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        /*observable.onErrorReturn(new Func1<Throwable, GeneralResponse<BalanceCreditSuccess>>() {
            @Override
            public GeneralResponse<BalanceCreditSuccess> call(Throwable throwable) {

                return null;

            }
        });*/

        return  observable;
    }

    public Observable<GeneralResponse<BalanceSecondarySuccess>> getBalanceSecondary(){

        Observable<GeneralResponse<BalanceSecondarySuccess>> observable =
                RetrofitCall.getInstance().getPrepaidBalanceSecondary()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }
    
}
