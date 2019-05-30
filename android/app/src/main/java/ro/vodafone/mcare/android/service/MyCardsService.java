package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.support.annotation.NonNull;

import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.myCards.DeleteCardResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyCardsService extends BaseService {

    public MyCardsService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<CardsResponse>> getCards(String vfSsoUsername) {
        return RetrofitCall.getInstance().getCards(vfSsoUsername)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GeneralResponse<AddCardResponse>> addCard(String vfSsoUsername, String vfPhoneNumber, String vfSsoEmail) {
        return RetrofitCall.getInstance().addCard(vfSsoUsername, vfPhoneNumber, vfSsoEmail)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GeneralResponse<DeleteCardResponse>> deleteCard(@NonNull String cardToken) {
        return RetrofitCall.getInstance().deleteCard(cardToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
