package ro.vodafone.mcare.android.injection;

import android.content.Context;
import android.support.annotation.NonNull;

import ro.vodafone.mcare.android.client.model.myCards.CardsRemoteDataSource;
import ro.vodafone.mcare.android.client.model.myCards.CardsRepository;

public class Injection {
    public static CardsRepository provideCardsRepository() {
        return CardsRepository.getInstance(CardsRemoteDataSource.getInstance());
    }
}
