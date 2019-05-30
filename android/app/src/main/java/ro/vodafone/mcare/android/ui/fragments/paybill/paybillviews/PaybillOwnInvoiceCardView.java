package ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 29.11.2017.
 */

public class PaybillOwnInvoiceCardView extends VodafoneGenericCard {

    @BindView(R.id.bill_not_paied_under_due_date_message)
    LinearLayout billNotPaiedUnderDueDateMessage;

    @BindView(R.id.amount_to_pay)
    VodafoneTextView amount;
    @BindView(R.id.client_code)
    VodafoneTextView clientCode;
    @BindView(R.id.bill_number)
    VodafoneTextView billNumber;
    @BindView(R.id.due_date)
    VodafoneTextView dueDate;

    @BindView(R.id.security_icon)
    ImageView securityIcon;
    @BindView(R.id.pay_own_bill_button)
    VodafoneButton payOwnBillButton;
    @BindView(R.id.cards_container)
    LinearLayout cards_container;
    @BindView(R.id.add_new_card_container)
    LinearLayout add_new_card_container;
    String amountToPay;
    String clientCodeNumber;
    String billNumberValue;
    String dueDateValue;

    public PaybillOwnInvoiceCardView(Context context) {
        super(context);
        init();
    }

    public PaybillOwnInvoiceCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaybillOwnInvoiceCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card_paybill_own_invoice, this);
        ButterKnife.bind(this);
        payOwnBillButton.setText(PayBillLabels.getPayBillButton());
        securityIcon.setColorFilter(Color.parseColor("#ffffff"));
    }

    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }

    public void setClientCodeNumber(String clientCodeNumber) {
        this.clientCodeNumber = clientCodeNumber;
    }

    public void setBillNumberValue(String billNumberValue) {
        this.billNumberValue = billNumberValue;
    }

    public void setDueDateValue(String dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    public void updateView() {
        amount.setText(amountToPay);
        clientCode.setText(clientCodeNumber);
        billNumber.setText(billNumberValue);
        dueDate.setText(dueDateValue);
    }

    public void setPayBillButtonClickListener(OnClickListener clickListener) {
        payOwnBillButton.setOnClickListener(clickListener);
    }

    public void displayNotPaidUnderDueDateMessage() {
        billNotPaiedUnderDueDateMessage.setVisibility(VISIBLE);
    }
    public LinearLayout getCardsContainer(){
        return cards_container;
    }
    public LinearLayout getOtherCardContainer(){
        return add_new_card_container;
    }
}
