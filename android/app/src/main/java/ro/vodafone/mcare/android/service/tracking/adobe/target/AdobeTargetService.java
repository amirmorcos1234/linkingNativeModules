package ro.vodafone.mcare.android.service.tracking.adobe.target;

import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;

/**
 * Created by Bivol Pavel on 27.04.2018.
 */

public class AdobeTargetService {

    public Single<List<String>> getOffersIdsFromTarget(final TargetLocationRequest locationRequest){

        return Single.create(new Single.OnSubscribe<List<String>>() {

            @Override
            public void call(final SingleSubscriber<? super List<String>> singleSubscriber) {

                Target.loadRequest(locationRequest, new Target.TargetCallback<String>() {

                    @Override
                    public void call(String s) {
                        try{
                            List<String> offersList = new Gson().fromJson(s, new TypeToken<List<String>>(){}.getType());
                            singleSubscriber.onSuccess(offersList);
                        }catch (JsonSyntaxException e){
                            singleSubscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    public Observable<List<String>> getHardcodedOffersIdsFromTarget(final boolean succesffulResponse){
        return rx.Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                if(succesffulResponse){//First 3 is from options then from services
                    subscriber.onNext(Arrays.asList("255369713", "255369703", "255741733", "255722443", "255722433", "255369743"));
                    subscriber.onCompleted();
                }else{
                    subscriber.onError(new Exception());
                }
            }
        });
    }
}
