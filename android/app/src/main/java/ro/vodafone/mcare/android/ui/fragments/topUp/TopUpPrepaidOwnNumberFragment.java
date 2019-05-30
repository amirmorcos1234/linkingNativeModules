package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsRepository;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRechargesSuccess;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.MyCardsService;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.myCards.MyCardsFragment;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpPrepaidOwnNumberFragment extends BaseTopUpFragment implements InputEventsListenerInterface, RechargeValueSection.Callback{
    public static final String TAG = "PrepaidOwnNumber";

    private LinearLayout emailErrorLayout;

    public  NavigationHeader navigationHeader;

    private VodafoneTextView emailErrorMessage;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(navigationHeader!=null) {
            navigationHeader.setTitle(TopUPLabels.getTop_up_page_title());
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(navigationHeader!=null) {
            navigationHeader.setTitle(TopUPLabels.getTop_up_recharge_own_number());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_top_up_prepaid_own_number, container, false);

        emailErrorMessage = (VodafoneTextView) view.findViewById(R.id.email_error_message);
        voucherErrorMessage  = (VodafoneTextView) view.findViewById(R.id.voucher_error_message);

        emailErrorLayout = (LinearLayout) view.findViewById(R.id.email_error_layout);
        rechargeButton = (VodafoneButton) view.findViewById(R.id.recharge_button);
        rechargeButton.setOnClickListener(rechargeButtonListner);

        paymentIconsLayout = (LinearLayout) view.findViewById(R.id.payment_icons_layout);
        payWithVoucherLayout = (LinearLayout) view.findViewById(R.id.pay_with_voucher_layout);
        voucherErrorLayout = (LinearLayout) view.findViewById(R.id.voucher_error_layout);
        displaySelectedPayTypeLayout(paymentIconsLayout); // by default first radioo button in checked

        rechargeValueSection = (RechargeValueSection) view.findViewById(R.id.recharge_value_section);

        emailAddressInput = (CustomEditText) view.findViewById(R.id.email_address_input);
        voucherCodeInput = (CustomEditText) view.findViewById(R.id.voucher_input);

        paymentType = (RadioGroup) view.findViewById(R.id.payment_type);
        paymentType.setOnCheckedChangeListener(paymentTypeListner);
        add_new_card_container = (LinearLayout)view.findViewById(R.id.add_new_card_container);
        cards_container = (LinearLayout)view.findViewById(R.id.cards_container);
        setupLabels();


        if(!VodafoneController.getInstance().isSeamless()){
            emailAddressInput.setText(VodafoneController.getInstance().getUserProfile().getEmail());
        }
        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecommendedRecharge();
        loadCards();
        ((TopUpActivity) getActivity()).scrolltoTop();
        TealiumHelper.tealiumTrackView(TopUpPrepaidOwnNumberFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpPrepaidOwnNumberScreenName);
    }

    private void setupRecommendedRecharges(){
        if(RealmManager.getRealmObject(RecommendedRechargesSuccess.class) != null){
            rechargeValueSection.setRecommendedRechargeValues(((RecommendedRechargesSuccess)
                    RealmManager.getRealmObject(RecommendedRechargesSuccess.class)).getRecommendedRecharges(),true,false);
            rechargeValueSection.setDefaultSelectedBobble(0);
           // rechargeValueSection.setOtherValueInput(0);
            rechargeValueSection.setOtherValueInputString("6");
        } else {
            rechargeValueSection.setRecommendedRechargeValues(null,true,false);
            rechargeValueSection.setDefaultSelectedBobble(1);
            rechargeValueSection.setOtherValueInput(1);
        }
    }

    private void setupLabels(){
        ((VodafoneTextView)view.findViewById(R.id.email_adress_label)).setText(TopUPLabels.getTop_up_your_email_adress());
        ((CustomEditText)view.findViewById(R.id.email_address_input)).setHint(TopUPLabels.getTop_up_email_adress_hint());
        ((CustomEditText)view.findViewById(R.id.other_value_input)).setHint(TopUPLabels.getTopUpValueHint());
        ((RadioButton)view.findViewById(R.id.pay_with_card)).setText(TopUPLabels.getTop_up_pay_with_card());
        ((RadioButton)view.findViewById(R.id.pay_with_voucher)).setText(TopUPLabels.getTop_up_pay_with_voucher());
        ((VodafoneButton)view.findViewById(R.id.recharge_button)).setText(TopUPLabels.getTop_up_recharge_button_label());
        ((CustomEditText)view.findViewById(R.id.voucher_input)).setHint(TopUPLabels.getTop_up_voucher_hint());

    }


    @Override
    public void displayErrorMessage() {
        Log.d(TAG, "displayErrorMessage()");

        if(rechargeValueSection.otherValueInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            rechargeValueSection.showError(topUpErrorMessage());
        }

        if(emailAddressInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(emailErrorLayout, emailErrorMessage, TopUPLabels.getTop_up_invalid_email());
        }

        if(voucherCodeInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && voucherCodeInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS){
            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher_input());
        }

    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG,"hideErrorMessage");

        if(rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isHighlighted()){
            rechargeValueSection.otherValueInput.removeHighlight();
        }

        if(emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isHighlighted()){
            emailAddressInput.removeHighlight();
        }

        if(voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && voucherCodeInput.isHighlighted()){
            voucherCodeInput.removeHighlight();
        }

        if(rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || rechargeValueSection.otherValueInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            rechargeValueSection.hideError();
        }

        if(emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailAddressInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            emailErrorLayout.setVisibility(View.GONE);
        }

        if(voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || voucherCodeInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            voucherErrorLayout.setVisibility(View.GONE);
        }
        activateButton();
    }

    @Override
    public void activateButton() {
        Log.d(TAG,"activateButton");

        boolean isValideEmail = false;
        boolean isValideRechargeValue = true;//this parameter is true because by default it is hiden
        boolean isValideVoucher = true;

        isValideEmail = !emailAddressInput.isEmpty() && !emailAddressInput.isHighlighted();

        if(rechargeValueSection.isVisibleOtherValueLayout()){
            isValideRechargeValue = !rechargeValueSection.otherValueInput.isEmpty() && !rechargeValueSection.otherValueInput.isHighlighted();
        }

        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "activateButton: voucher check");
            isValideVoucher = !voucherCodeInput.isEmpty() && !voucherCodeInput.isHighlighted();
        }

        if(isValideEmail && isValideRechargeValue && isValideVoucher){
            rechargeButton.setEnabled(true);
        }
    }


    @Override
    public void inactivateButton() {
        Log.d(TAG,"inactivateButton");

        rechargeButton.setEnabled(false);
    }

    @Override
    public void selectElement(int amount) {
        this.amount = amount;
    }

    @Override
    public void displayOtherValueField() {
        if(!rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput.isEmpty()){
            inactivateButton();
        }
    }

    @Override
    public void hideOtherValueField() {
        activateButton();
    }
    private void getRecommendedRecharge(){
        showLoadingDialog();

        if (getView() != null)
            getView().setVisibility(View.GONE);

        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.getRecommendedRechargesValues(3, 4).subscribe(new RequestSaveRealmObserver<GeneralResponse<RecommendedRechargesSuccess>>() {
            @Override
            public void onNext(GeneralResponse<RecommendedRechargesSuccess> recommendedRechargesSuccessGeneralResponse) {
                super.onNext(recommendedRechargesSuccessGeneralResponse);
                if(getView() != null)
                    getView().setVisibility(View.VISIBLE);

                setupRecommendedRecharges();
                stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (getView() != null)
                    getView().setVisibility(View.VISIBLE);

                stopLoadingDialog();
                RealmManager.delete(RecommendedRechargesSuccess.class);
                setupRecommendedRecharges();
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.TOP_UP);
    }
}
