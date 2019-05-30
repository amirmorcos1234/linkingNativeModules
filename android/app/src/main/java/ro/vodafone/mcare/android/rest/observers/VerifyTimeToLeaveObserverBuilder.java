package ro.vodafone.mcare.android.rest.observers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.client.model.realm.system.TimeToLeaveMap;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Victor Radulescu on 2/24/2017.
 */

public class VerifyTimeToLeaveObserverBuilder {


    public Observable build(final Observable observableToServer, final Class<? extends RealmObject> timeToLiveRealmClassToRetrieved, final int... key) {
        long ttl = (key.length > 0)
                ? TimeToLeaveMap.getTimeToLiveForClassAfterKey(timeToLiveRealmClassToRetrieved, key[0])
                : TimeToLeaveMap.getTimeToLiveForClass(timeToLiveRealmClassToRetrieved);

        final Observable observable = Observable.just(ttl).map(new Func1<Long,Boolean>() {
            @Override
            public Boolean call(Long ttl) {
                return (System.currentTimeMillis()-ttl) > 0;
            }
        }).flatMap(new Func1<Boolean, Observable<?>>() {
            @Override
            public Observable<?> call(Boolean aBoolean) {
                if(aBoolean){
                    //delete realm cache before server call
                    if (key.length > 0)
                        RealmManager.deleteAfterKey(timeToLiveRealmClassToRetrieved, key[0]);
                    else
                        RealmManager.delete(timeToLiveRealmClassToRetrieved);

                    return SessionObserverBuilder.build(observableToServer);
                }
                final Observable[] observable1 = new Observable[1];
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("UI thread", "I am the UI thread");
                        observable1[0] = (key.length > 0)
                                ? RealmManager.getUnmanagedRealmObjectObservableByKey(timeToLiveRealmClassToRetrieved, key[0])
                                : RealmManager.getRealmObjectObservable(timeToLiveRealmClassToRetrieved);

                        if (observable1[0] == null && key.length > 0) {
                            observable1[0] = SessionObserverBuilder.build(observableToServer);
                        }
                    }
                });
                return observable1[0];
            }
        });
        return observable;
    }
    private Observable getExceptionObservable(){
        return null;
    }

}
