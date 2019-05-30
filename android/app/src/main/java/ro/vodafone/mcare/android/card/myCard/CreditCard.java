package ro.vodafone.mcare.android.card.myCard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public class CreditCard extends VodafoneAbstractCard {

    @BindView(R.id.credit_card_number)
    VodafoneTextView credit_card_number;

    @BindView(R.id.credit_card_expiration_date)
    VodafoneTextView credit_card_expiration_date;

    @BindView(R.id.delete_card_text_view)
    VodafoneTextView delete_card_text_view;

    @BindView(R.id.card_type_image)
    ImageView card_type_image;

    public CreditCard(Context context) {
        super(context);
        ButterKnife.bind(this);
        getCardView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        setCardMargins(0,
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                0,
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
    }

    @Override
    protected int setContent() {
        return R.layout.credit_card_layout;
    }

    public CreditCard setCreditCardNumber(String number) {
        credit_card_number.setText(number);
        return this;
    }

    public CreditCard setCreditCardExpirationDate(String expirationDate) {
        credit_card_expiration_date.setText("Expiră în " + expirationDate);
        return this;
    }

    public CreditCard setCardTypeImage(String cardType) {
        if (cardType.equalsIgnoreCase("visa")) {
            card_type_image.setImageResource(R.drawable.ic_visa);
        } else if (cardType.equalsIgnoreCase("mastercard")) {
            card_type_image.setImageResource(R.drawable.ic_mastercard);
        } else {
            card_type_image.setImageResource(R.drawable.ic_credit_card_black);
        }
        return this;
    }

    public CreditCard deleteClick(OnClickListener onClickListener) {
        delete_card_text_view.setOnClickListener(onClickListener);
        return this;
    }

    public VodafoneTextView getDeleteText() {
        return delete_card_text_view;
    }

    public VodafoneTextView getExpirationDate() {
        return credit_card_expiration_date;
    }
}
