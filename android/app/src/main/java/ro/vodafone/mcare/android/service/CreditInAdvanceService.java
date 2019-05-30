package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.creditInAdvance.EligibilityInfo;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by User on 11.07.2017.
 */

public class CreditInAdvanceService extends BaseService {
    public CreditInAdvanceService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<CreditInAdvanceSuccess>> getCreditInAdvanceEligibility(){
        Observable<GeneralResponse<CreditInAdvanceSuccess>> observable =
                RetrofitCall.getInstance().getCreditInAdvanceEligibility()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<CreditInAdvanceSuccess>> performCreditInAdvance(EligibilityInfo eligibilityInfo){

        return RetrofitCall.getInstance().performCreditInAdvance(eligibilityInfo)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
