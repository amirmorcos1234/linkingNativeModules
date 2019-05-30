package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.FavoriteNumbersSpinnerAdapter;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpAnonymousFragment extends BaseTopUpFragment implements InputEventsListenerInterface, VodafoneSpinner.Callback, RechargeValueSection.Callback, FavoriteNumbersSpinnerAdapter.Callback {

    public final String TAG = "TopUpAnonymousFragment";

    private LinearLayout emailErrorLayout;

    private VodafoneTextView emailErrorMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_top_up_anonymous, container, false);

        ((TopUpActivity) getActivity()).getNavigationHeader().hideSelectorView();

        phoneNumberErrorLayout = (LinearLayout) view.findViewById(R.id.phone_number_error_layout);
        emailErrorLayout = (LinearLayout) view.findViewById(R.id.email_error_layout);
        rechargeButton = (VodafoneButton) view.findViewById(R.id.recharge_button);
        contactsButton = (LinearLayout) view.findViewById(R.id.contacts_button);
        contactsButton.setOnClickListener(contactsButtonListner);
        favoriteNumbersLayout = (LinearLayout) view.findViewById(R.id.favorite_numbers_layout);
        paymentIconsLayout = (LinearLayout) view.findViewById(R.id.payment_icons_layout);
        payWithVoucherLayout = (LinearLayout) view.findViewById(R.id.pay_with_voucher_layout);
        voucherErrorLayout = (LinearLayout) view.findViewById(R.id.voucher_error_layout);
        rechargeButton.setOnClickListener(rechargeButtonListner);

        phoneNumberInput = (CustomEditText) view.findViewById(R.id.telephone_number_input);
        emailAddressInput = (CustomEditText) view.findViewById(R.id.email_address_input);
        voucherCodeInput = (CustomEditText) view.findViewById(R.id.voucher_input);

        phoneNumberErrorMessage = (VodafoneTextView) view.findViewById(R.id.phone_number_error_message);
        emailErrorMessage = (VodafoneTextView) view.findViewById(R.id.email_error_message);
        voucherErrorMessage = (VodafoneTextView) view.findViewById(R.id.voucher_error_message);
        add_new_card_container = (LinearLayout)view.findViewById(R.id.add_new_card_container);
        cards_container = (LinearLayout)view.findViewById(R.id.cards_container);

        rechargeValueSection = (RechargeValueSection) view.findViewById(R.id.recharge_value_section);

        RadioGroup paymentType = (RadioGroup) view.findViewById(R.id.payment_type);
        paymentType.setOnCheckedChangeListener(localPaymentTypeListner);

        if (!VodafoneController.getInstance().isSeamless()) {
            emailAddressInput.setText(VodafoneController.getInstance().getUserProfile().getEmail());
        }

        displaySelectedPayTypeLayout(paymentIconsLayout); // by default first radioo button in checked
        initSpinner();
        setupLabels();


        rechargeValueSection.setDefaultSelectedBobble(1);
        rechargeValueSection.setOtherValueInput(1);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TopUpActivity) getActivity()).scrolltoTop();
        loadCards();
        TealiumHelper.tealiumTrackView(TopUpAnonymousFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);
    }

    private void setupLabels() {
        ((CustomEditText) view.findViewById(R.id.telephone_number_input)).setHint(TopUPLabels.getTop_up_phone_number_hint());
        ((VodafoneTextView) view.findViewById(R.id.email_adress_label)).setText(TopUPLabels.getTop_up_your_email_adress());
        ((VodafoneTextView) view.findViewById(R.id.recharge_value_label)).setText(TopUPLabels.getTop_up_recharge_value());
        ((CustomEditText) view.findViewById(R.id.email_address_input)).setHint(TopUPLabels.getTop_up_email_adress_hint());
        ((RadioButton) view.findViewById(R.id.pay_with_voucher)).setText(TopUPLabels.getTop_up_pay_with_voucher());
        ((RadioButton) view.findViewById(R.id.pay_with_card)).setText(TopUPLabels.getTop_up_pay_with_card());
    }

    RadioGroup.OnCheckedChangeListener localPaymentTypeListner = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.pay_with_card:
                    rechargeValueSection.activateRadioButtons();
                    displaySelectedPayTypeLayout(paymentIconsLayout);
                    activateButton();
                    break;
                case R.id.pay_with_voucher:
                    rechargeValueSection.disableRadioButtonKeepPreviousSelection();
                    voucherCodeInput.clearFocus();
                    voucherCodeInput.removeHighlight();
                    displaySelectedPayTypeLayout(payWithVoucherLayout);
                    inactivateButton();
                    break;
            }
        }
    };

    @Override
    public void displayErrorMessage() {
        Log.d(TAG, "displayErrorMessage()");

        if (phoneNumberInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
        }

        if (rechargeValueSection.otherValueInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            rechargeValueSection.showError(topUpErrorMessage());
        }

        if (emailAddressInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(emailErrorLayout, emailErrorMessage, TopUPLabels.getTop_up_invalid_email());
        }

        if (voucherCodeInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && voucherCodeInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher_input());
        }
    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage");

        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isHighlighted()) {
            phoneNumberInput.removeHighlight();
        }

        if (rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isHighlighted()) {
            rechargeValueSection.otherValueInput.removeHighlight();
        }

        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isHighlighted()) {
            emailAddressInput.removeHighlight();
        }

        if (voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && voucherCodeInput.isHighlighted()) {
            voucherCodeInput.removeHighlight();
        }

        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || phoneNumberInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            phoneNumberErrorLayout.setVisibility(View.GONE);

        }

        if (rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || rechargeValueSection.otherValueInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            rechargeValueSection.hideError();
        }

        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailAddressInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            emailErrorLayout.setVisibility(View.GONE);
        }

        if (voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || voucherCodeInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            voucherErrorLayout.setVisibility(View.GONE);
        }
        activateButton();
    }

    @Override
    public void activateButton() {
        Log.d(TAG, "activateButton");

        boolean isValideEmail = false;
        boolean isValidePhoneNumber = false;

        //this parameters is true because by default its is hiden
        boolean isValideVoucher = true;
        boolean isValideRechargeValue = true;

        isValideEmail = !emailAddressInput.isEmpty() && !emailAddressInput.isHighlighted();
        isValidePhoneNumber = !phoneNumberInput.isEmpty() && !phoneNumberInput.isHighlighted();

        if (payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            isValideVoucher = !voucherCodeInput.isEmpty() && !voucherCodeInput.isHighlighted();
        }

        if (rechargeValueSection.isVisibleOtherValueLayout()) {
            isValideRechargeValue = !rechargeValueSection.otherValueInput.isEmpty() && !rechargeValueSection.otherValueInput.isHighlighted();
        }

        if (isValideEmail && isValidePhoneNumber && isValideVoucher && isValideRechargeValue) {
            rechargeButton.setEnabled(true);
        }
    }


    @Override
    public void inactivateButton() {
        Log.d(TAG, "inactivateButton");

        rechargeButton.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestReadContactsPermission() {
        super.requestReadContactsPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        phoneNumberInput.setText(((FavoriteNumber) selectedValue).getPrepaidMsisdn());
    }

    @Override
    public void selectElement(int amount) {
        this.amount = amount;
    }

    @Override
    public void displayOtherValueField() {
        if (!rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput.isEmpty()) {
            inactivateButton();
        }
    }

    @Override
    public void hideOtherValueField() {
        activateButton();
    }

    @Override
    public void deleteFavoriteNumeber(int position) {
        deleteFavoriteNumber(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.TOP_UP);
    }
}
