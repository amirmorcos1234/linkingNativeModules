package ro.vodafone.mcare.android.ui.myCards;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.myCard.CreditCard;
import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCardsFragment extends BaseFragment implements MyCardsContract.View {
    @BindView(R.id.card_container)
    LinearLayout cardContainer;

    @BindView(R.id.no_cards_view)
    RelativeLayout noCardsView;

    @BindView(R.id.terms_condition)
    VodafoneTextView termsAndConditions;

    @BindView(R.id.add_card)
    VodafoneButton addNewCardButton;

    @BindView(R.id.terms_and_conditions_layout)
    LinearLayout termsAndConditionsLayout;

    @BindView(R.id.max_cards)
    VodafoneTextView maxCardNumber;

    MyCardsContract.Presenter presenter;

    DeleteCardConfirmationDialog deleteCardConfirmationDialog;

    public MyCardsFragment() {
        // Required empty public constructor
    }

    public static MyCardsFragment newInstance() {
        return new MyCardsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_cards, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void setLoadingIndicator(boolean active) {

        if (getView() == null) {
            return;
        }
        if (active) {
            showLoadingDialog();
        } else {
            stopLoadingDialog();
        }
    }

    @Override
    public void showCards(List<Card> myCards) {
        addCards(myCards);
    }

    @Override
    public void showNoCards() {
        cardContainer.removeAllViews();
        cardContainer.addView(noCardsView);
        noCardsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearCardContainer() {
        cardContainer.removeAllViews();
    }

    @Override
    public void showAddNewCard(AddCardResponse addCardResponse) {
        IntentActionName.ADD_CREDIT_CARD_WEBVIEW.setOneUsageSerializedData(new Gson().toJson(addCardResponse));
        new NavigationAction(getActivity()).finishCurrent(false).startAction(IntentActionName.ADD_CREDIT_CARD_WEBVIEW);
    }

    @Override
    public void showDeleteCard() {

    }

    @Override
    public void showAddCardError() {
        new CustomToast.Builder(getActivity()).message(R.string.add_card_error).success(false).show();
    }

    @Override
    public void showDeleteCardError() {
        new CustomToast.Builder(getActivity()).message(R.string.delete_card_error).success(false).show();
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void disableAddCreditCard(boolean isEnabled) {
        if (isEnabled) {
            addNewCardButton.setEnabled(!isEnabled);
            addNewCardButton.setText(getResources().getString(R.string.add_card_disabled));
            maxCardNumber.setText(getResources().getString(R.string.max_number_of_cards));
            termsAndConditionsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDeleteCardSuccess() {
        new CustomToast.Builder(getActivity()).message(R.string.delete_card_success).success(true).show();
    }

    @Override
    public void setPresenter(MyCardsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void addCards(List<Card> myCards) {
        if (!myCards.isEmpty()) {
            addNewCardButton.setText(getResources().getString(R.string.add_card_disabled));
            for (final Card card : myCards) {
                cardContainer.addView(new CreditCard(getActivity())
                        .setCreditCardExpirationDate(new SimpleDateFormat("MMMM yyyy", new Locale("RO", "RO"))
                                .format(new Date(card.getCardExpirationDate())))
                        .setCreditCardNumber(card.getCardType() + " " + card.getCardNumberMasked())
                        .setCardTypeImage(card.getCardType())
                        .deleteClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteCardConfirmationDialog = new DeleteCardConfirmationDialog(getActivity(), android.R.style.Theme_Black_NoTitleBar, card);
                                deleteCardConfirmationDialog.show();
                            }
                        }));
            }
        }
    }

    @OnClick(R.id.terms_condition)
    void termsAndConditionsClick() {
        IntentActionName.WEBVIEW.setOneUsageSerializedData(getString(R.string.terms_and_conditions_url));
        new NavigationAction(getActivity()).finishCurrent(false).startAction(IntentActionName.WEBVIEW);
    }

    @OnClick(R.id.add_card)
    void addNewCardClick() {
        presenter.addNewCard();
    }

    private void dismissDialog() {
        if (deleteCardConfirmationDialog != null && deleteCardConfirmationDialog.isShowing()) {
            deleteCardConfirmationDialog.dismiss();
            deleteCardConfirmationDialog = null;
        }
    }

    class DeleteCardConfirmationDialog extends Dialog {

        @BindView(R.id.overlayDismissButton)
        ImageView closeDialog;

        @BindView(R.id.card_details)
        VodafoneTextView cardDetails;

        @BindView(R.id.button_delete)
        VodafoneButton delete;

        @BindView(R.id.button_cancel)
        VodafoneButton cancel;

        private Card card;

        public DeleteCardConfirmationDialog(@NonNull Context context, int themeResId, Card card) {
            super(context, themeResId);
            this.card = card;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.delete_card_confirmation_dialog);

            ButterKnife.bind(this);

            try {
                cardDetails.setText(card.getCardType().toUpperCase() + " " + card.getCardNumberMasked() + "?");
            } catch (Exception e) {
            }
        }

        @OnClick({R.id.button_cancel, R.id.overlayDismissButton})
        void xClick() {
            dismiss();
        }

        @OnClick(R.id.button_delete)
        void deleteCard() {
            presenter.deleteCard(card.getToken());
        }
    }

}
