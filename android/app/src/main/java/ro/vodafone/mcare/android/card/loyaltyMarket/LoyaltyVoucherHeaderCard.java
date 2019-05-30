package ro.vodafone.mcare.android.card.loyaltyMarket;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Serban Radulescu on 8/28/2017.
 */

public class LoyaltyVoucherHeaderCard extends VodafoneAbstractCard {

    @BindView(R.id.discount_value_textview)
    VodafoneTextView discountValueTv;
    @BindView(R.id.discount_label_textview)
    VodafoneTextView discountLabelTv;
    @BindView(R.id.voucher_validity_textview)
    VodafoneTextView validityTv;
    @BindView(R.id.voucher_expiration_date_textview)
    VodafoneTextView expirationDateTv;



    public LoyaltyVoucherHeaderCard(Context context) {
        super(context);
        init(null);
    }

    public LoyaltyVoucherHeaderCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoyaltyVoucherHeaderCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin), (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin));

    }
    private void setAttributes(AttributeSet attributeSet) {
    }

    @Override
    protected int setContent() {
        return R.layout.card_loyalty_header_voucher;
    }


    public LoyaltyVoucherHeaderCard setDiscountValue(String discountValue) {
        discountValueTv.setText(discountValue);
        discountValueTv.setTypeface(Typeface.DEFAULT_BOLD);
        return this;
    }
    public LoyaltyVoucherHeaderCard setDiscountLabel(String discountLabel){
        discountLabelTv.setText(discountLabel);
        return this;
    }

    public LoyaltyVoucherHeaderCard setVoucherValidity(SpannableString validity){
        if(validity.toString().contains("Voucher code")){
            validity.setSpan(new StyleSpan(Typeface.BOLD),
                    0,
                    validity.length(),
                    0);
            validityTv.setText(validity);
        } else {
            validityTv.setText(validity);
        }
        return this;
    }

    public LoyaltyVoucherHeaderCard setVoucherValidityTextSize(int size) {
        validityTv.setTextSize(size);
        return this;
    }

    public LoyaltyVoucherHeaderCard setVoucherExpiration(SpannableString expiration){
        expirationDateTv.setText(expiration);
        return this;
    }

    public LoyaltyVoucherHeaderCard hideDiscountValue(){
        discountValueTv.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherHeaderCard hideDiscountLabel(){
        discountLabelTv.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherHeaderCard hideVoucherValidity(){
        validityTv.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherHeaderCard hideVoucherExpiration(){
        expirationDateTv.setVisibility(GONE);
        return this;
    }
}
