package ro.vodafone.mcare.android.client.model.myCards;

import android.support.annotation.NonNull;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.service.MyCardsService;
import rx.Observable;
import rx.functions.Func1;

public class CardsRemoteDataSource implements CardsDataSource {
    private static CardsRemoteDataSource INSTANCE;
    private MyCardsService myCardsService = new MyCardsService(VodafoneController.getInstance().getApplicationContext());

    public static CardsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardsRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<CardsResponse> getCards(String vfSsoUsername) {
        return myCardsService.getCards(vfSsoUsername)
                .flatMap(new Func1<GeneralResponse<CardsResponse>, Observable<CardsResponse>>() {
                    @Override
                    public Observable<CardsResponse> call(GeneralResponse<CardsResponse> cardsResponseGeneralResponse) {
                        return Observable.just(cardsResponseGeneralResponse.getTransactionSuccess());
                    }
                })
                .asObservable();
    }

    @Override
    public Observable<AddCardResponse> addCard(String vfSsoUsername, String vfPhoneNumber, String vfSsoEmail) {

        return myCardsService.addCard(vfSsoUsername, vfPhoneNumber, vfSsoEmail)
                .flatMap(new Func1<GeneralResponse<AddCardResponse>, Observable<AddCardResponse>>() {
                    @Override
                    public Observable<AddCardResponse> call(GeneralResponse<AddCardResponse> addCardResponseGeneralResponse) {
                        return Observable.just(addCardResponseGeneralResponse.getTransactionSuccess());
                    }
                })
                .asObservable();
    }

    @Override
    public Observable<DeleteCardResponse> deleteCard(@NonNull String cardToken) {
        return myCardsService.deleteCard(cardToken)
                .flatMap(new Func1<GeneralResponse<DeleteCardResponse>, Observable<DeleteCardResponse>>() {
                    @Override
                    public Observable<DeleteCardResponse> call(GeneralResponse<DeleteCardResponse> stringGeneralResponse) {
                        return Observable.just(stringGeneralResponse.getTransactionSuccess());
                    }
                })
                .asObservable();
    }
}
