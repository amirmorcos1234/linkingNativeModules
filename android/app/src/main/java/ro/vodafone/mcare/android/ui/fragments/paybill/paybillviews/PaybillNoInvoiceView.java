package ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 29.11.2017.
 */

public class PaybillNoInvoiceView extends VodafoneGenericCard {
    @BindView(R.id.check_mark)
    ImageView checkMark;

    @BindView(R.id.no_invoices_message)
    VodafoneTextView noInvoicesMessage;
    @BindView(R.id.issued_date)
    VodafoneTextView issuedDateView;

    public PaybillNoInvoiceView(Context context) {
        super(context);
        init();
    }

    public PaybillNoInvoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaybillNoInvoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card_paybill_no_invoice, this);
        ButterKnife.bind(this);
        setCheckMarkColor();
    }

    private void setCheckMarkColor() {
        checkMark.setColorFilter(getResources().getColor(R.color.green_check_mark_color));
    }

    public void setInvoiceMessage(String invoiceMessage) {
        noInvoicesMessage.setText(invoiceMessage);
    }

    public void setIssuedDate(String issuedDate) {
        issuedDateView.setText(issuedDate);
    }
}
