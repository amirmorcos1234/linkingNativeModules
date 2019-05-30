package ro.vodafone.mcare.android.client.model.myCards;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import rx.Observable;

public class CardsRepository implements CardsDataSource {

    @Nullable
    private static CardsRepository INSTANCE = null;

    @NonNull
    private final CardsDataSource cardsRemoteDataSource;

    private CardsRepository(@NonNull CardsDataSource cardsRemoteDataSource) {
        this.cardsRemoteDataSource = cardsRemoteDataSource;
    }

    public static CardsRepository getInstance(@NonNull CardsDataSource cardsRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CardsRepository(cardsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<CardsResponse> getCards(String vfSsoUsername) {
        return cardsRemoteDataSource.getCards(vfSsoUsername);
    }

    @Override
    public Observable<AddCardResponse> addCard(String vfSsoUsername, String vfPhoneNumber, String vfSsoEmail) {

        return cardsRemoteDataSource.addCard(vfSsoUsername, vfPhoneNumber, vfSsoEmail);
    }

    @Override
    public Observable<DeleteCardResponse> deleteCard(@NonNull String cardToken) {
        return cardsRemoteDataSource.deleteCard(cardToken);
    }
}
