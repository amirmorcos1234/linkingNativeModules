package ro.vodafone.mcare.android.ui.myCards;

import java.util.List;

import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.interfaces.BasePresenter;
import ro.vodafone.mcare.android.interfaces.BaseView;

public interface MyCardsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showCards(List<Card> myCards);

        void showNoCards();

        void clearCardContainer();

        void showAddNewCard(AddCardResponse addCardResponse);

        void showDeleteCard();

        void showAddCardError();

        void showDeleteCardError();

        void finishActivity();

        void disableAddCreditCard(boolean isEnabled);

        void showDeleteCardSuccess();

    }

    interface Presenter extends BasePresenter {

        void loadCards();

        void addNewCard();

        void deleteCard(String cardToken);

    }
}
