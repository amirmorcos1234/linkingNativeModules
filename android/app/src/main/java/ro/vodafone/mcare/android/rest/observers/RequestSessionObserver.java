package ro.vodafone.mcare.android.rest.observers;

import android.support.annotation.CallSuper;
import android.util.Log;

import retrofit2.adapter.rxjava.HttpException;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.exceptions.ClientException;
import ro.vodafone.mcare.android.service.exceptions.InternetConnectionProblemException;
import ro.vodafone.mcare.android.utils.D;
import rx.Observer;

/**
 * Created by Victor Radulescu on 12/5/2016.
 */

public abstract class RequestSessionObserver<T> implements Observer<T> {

    boolean tryAutoLoginIf401Error = true;

    public RequestSessionObserver(boolean tryAutoLoginIf401Error) {
        this.tryAutoLoginIf401Error = tryAutoLoginIf401Error;
    }

    public RequestSessionObserver() {
    }


    @Override
    public void onCompleted() {
        Log.d("RequestSessionObserver","Observable  : completed");
    }

    @CallSuper
    @Override
    public void onError(Throwable e) {
        if(e instanceof HttpException){
            if(((HttpException) e).code()==401 && tryAutoLoginIf401Error){
                if(VodafoneController.getInstance().isSeamless()){
                    D.d("Should autologin seamless");
                    return;
                }else{
                   // AutoLoginObservable.getInstance().start();
                    D.d("Should autologin full");
                }
            }
            ///VodafoneController.getInstance().getHttpExceptionSessionExpireListener().onSessionExpire();
        }
        if(e instanceof InternetConnectionProblemException){
            try {
                throw new AuthenticationServiceException(
                        R.string.internet_connection_problem, e);
            } catch (AuthenticationServiceException e1) {
                e1.printStackTrace();
            }
        }
        else if(e instanceof ClientException){
            try {
                throw new AuthenticationServiceException(
                        R.string.internet_connection_problem, e);
            } catch (AuthenticationServiceException e1) {
                e1.printStackTrace();
            }
        }else{
            Log.e("RequestSessionObserver","Observable error RequestSessionObserver.onError: " + e.getMessage(), e);
        }

    }

}
