package ro.vodafone.mcare.android.client.model.myCards;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public interface CardsDataSource {
    Observable<CardsResponse> getCards(String vfSsoUsername);

    Observable<AddCardResponse> addCard(String vfSsoUsername, String vfPhoneNumber, String vfSsoEmail);

    Observable<DeleteCardResponse> deleteCard(@NonNull String cardToken);
}
