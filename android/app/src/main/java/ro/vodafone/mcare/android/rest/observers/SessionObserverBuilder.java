package ro.vodafone.mcare.android.rest.observers;

import java.util.concurrent.TimeUnit;

import retrofit2.adapter.rxjava.HttpException;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observables.AutoLoginObservable;
import ro.vodafone.mcare.android.rest.observables.SeamlessAutoLoginObservable;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Victor Radulescu on 7/26/2017.
 */

public class SessionObserverBuilder {
    private static long durationUntilRetry = 1500;

    public static Observable build(final Observable observableToServer) {

        return observableToServer.retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                /*observable.zipWith(observable.range(1, 3), new Func2<Throwable, Integer, Object>() {

                   @Override
                   public Object call(Throwable throwable, Integer integer) {
                       return integer;
                   }
               });*/
               return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        D.d("Retry in "+ throwable.getMessage() +" code ");
                        if(throwable instanceof HttpException){
                            switch (((HttpException) throwable).code()){
                                case 401:
                                    return treat401ErrorCode(throwable);
                                    //TODO case for app should be updated
                              /*  case 403:
                                        return treatAppOutOfDate(throwable);*/
                                default:
                                    return Observable.error(throwable);
                            }
                        }
                        return Observable.error(throwable);
                    }});
            }});
    }

    /**
     * Treats session expired error code.
     */
    public static Observable treat401ErrorCode(Throwable throwable) {
        if(VodafoneController.getInstance().getUser()!=null){
            if( !VodafoneController.getInstance().isSeamless() && VodafoneController.getInstance().getUser().isFullLoggedIn()) {
                ///refresh token
                D.d("Retry in "+ durationUntilRetry);
                AutoLoginObservable.getInstance().start();
                if(AutoLoginObservable.getInstance().isKeepMeLoggedIn()
                        && !(VodafoneController.getInstance().getUser() instanceof EbuMigrated))
                {
                    return Observable.timer(durationUntilRetry, TimeUnit.MILLISECONDS);
                }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
                    return Observable.never();
                }
            }else{
                SeamlessAutoLoginObservable.getInstance().start();
                return Observable.timer(durationUntilRetry, TimeUnit.MILLISECONDS);
            }
            return Observable.error(throwable);
        }
        return Observable.error(throwable);
    }
}
