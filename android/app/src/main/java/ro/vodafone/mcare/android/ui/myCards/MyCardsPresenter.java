package ro.vodafone.mcare.android.ui.myCards;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsRepository;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.myCards.DeleteCardResponse;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MyCardsPresenter implements MyCardsContract.Presenter {

    @NonNull
    private final CardsRepository cardsRepository;

    @NonNull
    private final MyCardsContract.View cardsView;

    @NonNull
    private CompositeSubscription mSubscriptions;


    public MyCardsPresenter(@NonNull CardsRepository cardsRepository,
                            @NonNull MyCardsContract.View cardsView) {
        this.cardsRepository = cardsRepository;
        this.cardsView = cardsView;

        mSubscriptions = new CompositeSubscription();
        cardsView.setPresenter(this);
    }

    @Override
    public void loadCards() {
        cardsView.setLoadingIndicator(true);
        mSubscriptions.clear();
        Subscription subscription = cardsRepository.getCards(VodafoneController.getInstance().getUserProfile().getUserName())
                .subscribe(new Observer<CardsResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d("", "onCompleted: ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        cardsView.showNoCards();
                        cardsView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onNext(CardsResponse cardsResponse) {
                        cardsView.disableAddCreditCard(cardsResponse.getMaxReached());
                        processCards(cardsResponse.getCardList());
                        cardsView.setLoadingIndicator(false);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void addNewCard() {
        cardsView.setLoadingIndicator(true);
        mSubscriptions.clear();
        Subscription subscription = cardsRepository.addCard(VodafoneController.getInstance().getUserProfile().getUserName(),
                VodafoneController.getInstance().getUser().getUserProfile().getMsisdn(),
                VodafoneController.getInstance().getUserProfile().getEmail())
                .subscribe(new Observer<AddCardResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError:", e.toString());
                        cardsView.setLoadingIndicator(false);
                        cardsView.showAddCardError();
                    }

                    @Override
                    public void onNext(AddCardResponse addCardResponse) {
                        cardsView.showAddNewCard(addCardResponse);
                        cardsView.setLoadingIndicator(false);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteCard(String cardToken) {
        mSubscriptions.clear();
        Subscription subscription = cardsRepository.deleteCard(cardToken)
                .subscribe(new Observer<DeleteCardResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        cardsView.showDeleteCardError();
                    }

                    @Override
                    public void onNext(DeleteCardResponse s) {
                        if (s != null) {
                            cardsView.finishActivity();
                            cardsView.showDeleteCardSuccess();
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processCards(List<Card> cards) {
        cardsView.clearCardContainer();
        if (cards != null && !cards.isEmpty()) {
            cardsView.showCards(cards);
        } else {
            cardsView.showNoCards();
        }
    }

    @Override
    public void subscribe() {
        loadCards();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
