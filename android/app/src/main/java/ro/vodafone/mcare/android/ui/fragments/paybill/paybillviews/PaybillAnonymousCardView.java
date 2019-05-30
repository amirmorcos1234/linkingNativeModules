package ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.ui.utils.DecimalDigitsInputFilter;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 29.11.2017.
 */

public class PaybillAnonymousCardView extends VodafoneGenericCard {
    @BindView(R.id.telephone_number_input)
    CustomEditText phoneNumberInput;
    @BindView(R.id.email_address_input)
    CustomEditText emailInput;
    @BindView(R.id.invoice_value)
    CustomEditText invoiceValueInput;

    @BindView(R.id.paybill_phone_number_error)
    TooltipError phoneErrorLayout;
    @BindView(R.id.email_address_error)
    TooltipError emailErrorLayout;
    @BindView(R.id.invoice_value_error)
    TooltipError invoiceValueErrorLayout;

    @BindView(R.id.pay_another_bill_button)
    VodafoneButton payAnotherBillButton;
    @BindView(R.id.contacts_button)
    LinearLayout contactsButton;

    @BindView(R.id.paybill_error_view)
    CardErrorLayout errorView;
    @BindView(R.id.phone_number_label)
    VodafoneTextView phoneNumberLabel;
    @BindView(R.id.phone_input_container)
    LinearLayout phoneInputContainer;
    @BindView(R.id.add_new_card_container)
    LinearLayout add_new_card_container;
    @BindView(R.id.cards_container)
    LinearLayout cards_container;

    public PaybillAnonymousCardView(Context context) {
        super(context);
        init();
    }

    public PaybillAnonymousCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaybillAnonymousCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card_paybill_anonymous, this);
        ButterKnife.bind(this);
        payAnotherBillButton.setText(PayBillLabels.getPayBillButton());
        invoiceValueInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)});
        errorView.setMargins(0, 0, 0, 0);
    }

    private void displayError(TooltipError tooltipError, String errorMessage) {
        tooltipError.setVisibility(VISIBLE);
        tooltipError.setText(errorMessage);
        tooltipError.setColorBackground(R.color.pay_bill_error_layout_color);
    }

    public void setContactsButtonClickListener(OnClickListener clickListener) {
        contactsButton.setOnClickListener(clickListener);
    }

    public void setPayAnotherBillButtonListener(OnClickListener clickListener) {
        payAnotherBillButton.setOnClickListener(clickListener);
    }

    public void showApiFailedCardError() {
        phoneNumberLabel.setVisibility(GONE);
        phoneInputContainer.setVisibility(GONE);
        errorView.setVisibility(VISIBLE);
        errorView.setText(PayBillLabels.getPayOwnBillAnonymouslyMessage());
    }

    public void displayInputError() {
        if (phoneInputContainer.getVisibility() == VISIBLE && phoneNumberInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            if (phoneNumberInput.isValide() == CustomEditText.INVALID_FORMAT_FIELD_STATUS) {
                displayError(phoneErrorLayout, PayBillLabels.getPayBillInvalidPhoneNumber());
            } else {
                displayError(phoneErrorLayout, PayBillLabels.getPayBillInvalidPhoneNumber());
            }
        }
        if (emailInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && emailInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(emailErrorLayout, PayBillLabels.getPayBillInvalidEmail());
        }

        if (invoiceValueInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && invoiceValueInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(invoiceValueErrorLayout, PayBillLabels.getPayBillInvalidInvoiceValue());
        }
    }

    public void hideInputError() {
        if (phoneInputContainer.getVisibility() == VISIBLE && phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isHighlighted()) {
            phoneNumberInput.removeHighlight();
        }

        if (emailInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailInput.isHighlighted()) {
            emailInput.removeHighlight();
        }

        if (invoiceValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && invoiceValueInput.isHighlighted()) {
            invoiceValueInput.removeHighlight();
        }

        if (phoneInputContainer.getVisibility() == VISIBLE && (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS
                || phoneNumberInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS)) {
            phoneErrorLayout.setVisibility(View.GONE);
        }

        if (emailInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            emailErrorLayout.setVisibility(View.GONE);
        }

        if (invoiceValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || invoiceValueInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            invoiceValueErrorLayout.setVisibility(View.GONE);
        }
        activatePaymentButton();
    }

    public void inactivatePaymentButton() {
        payAnotherBillButton.setEnabled(false);
    }

    public void activatePaymentButton() {
        if (phoneInputContainer.getVisibility() == VISIBLE) {
            if (!phoneNumberInput.isEmpty() && !phoneNumberInput.isHighlighted() && !emailInput.isEmpty() && !emailInput.isHighlighted()
                    && !invoiceValueInput.isEmpty() && !invoiceValueInput.isHighlighted() && !invoiceValueInput.getText().toString().equals(".") && Double.valueOf(invoiceValueInput.getText().toString()) >= 1) {
                payAnotherBillButton.setEnabled(true);
            }
        } else {
            if (!emailInput.isEmpty() && !emailInput.isHighlighted()
                    && !invoiceValueInput.isEmpty() && !invoiceValueInput.isHighlighted() && !invoiceValueInput.getText().toString().equals(".") && Double.valueOf(invoiceValueInput.getText().toString()) >= 1) {
                payAnotherBillButton.setEnabled(true);
            }
        }
    }

    public void displayPhoneNumberError(String message) {
        phoneErrorLayout.setText(message);
        phoneErrorLayout.setVisibility(VISIBLE);
        phoneNumberInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS,false);
        phoneErrorLayout.setColorBackground(R.color.pay_bill_error_layout_color);
    }

    public String getPhoneNumber() {
        return phoneNumberInput.getText().toString();
    }

    public String getInvoiceValue() {
        return invoiceValueInput.getText().toString();
    }

    public String getEmail() {
        return emailInput.getText().toString();
    }

    public void fillNumberInput(String text) {
        phoneNumberInput.setText(text);
        phoneNumberInput.validateCustomEditText();
    }

    public void fillEmailInput(String text){
        if(text != null && !text.equals(""))
            emailInput.setText(text);
    }
    public LinearLayout getCardsContainer(){
        return cards_container;
    }
    public LinearLayout getOtherCardContainer(){
        return add_new_card_container;
    }
}

