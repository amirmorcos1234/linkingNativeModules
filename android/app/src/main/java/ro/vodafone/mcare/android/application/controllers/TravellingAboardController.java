package ro.vodafone.mcare.android.application.controllers;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class TravellingAboardController {

    private static TravellingAboardController mInstance;

    public static synchronized TravellingAboardController getInstance() {
        if (mInstance == null) {
            mInstance = new TravellingAboardController();
        }
        return mInstance;
    }

    public void refreshProfile(final Context context, final ProgressDialog progressDialog) {
        if (!progressDialog.isShowing())
            progressDialog.show();

        Observable.timer(5, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        (new UserDataService(context)).getUserProfile(true).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
                            @Override
                            public void onNext(GeneralResponse<Profile> response) {
                                if (response.getTransactionSuccess() != null)
                                    super.onNext(response);
                                Observable.timer(1, TimeUnit.SECONDS)
                                        .onBackpressureDrop()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Long>() {
                                            @Override
                                            public void call(Long aLong) {
                                                D.w();
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                            }
                                        });
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        });
                    }
                });
    }

}
