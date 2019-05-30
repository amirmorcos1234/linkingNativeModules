package ro.vodafone.mcare.android.card.loyaltyMarket;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.cropoverlay.utils.ImageViewUtil;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;

/**
 * Created by Serban Radulescu on 8/25/2017.
 */

public class LoyaltyVoucherCard extends VodafoneAbstractCard {

    @BindView(R.id.voucher_name_textview)
    VodafoneTextView voucherNameTv;
    @BindView(R.id.voucher_short_description_textview)
    VodafoneTextView voucherShortDescriptionTv;
    @BindView(R.id.discount_value_textview)
    VodafoneTextView voucherDiscountValueTv;
    @BindView(R.id.discount_label_textview)
    VodafoneTextView voucherDiscountLabel;


    @BindView(R.id.discount_container)
    RelativeLayout discountContainer;

    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.voucher_product_image)
    AppCompatImageView voucherProductImageView;
    @BindView(R.id.voucher_red_warning_band_image)
    AppCompatImageView voucherRedWarningBandImageView;

    @BindView(R.id.voucher_warning_expiration_textview)
    VodafoneTextView voucherExpirationWarningTv;
    private boolean isColapsed = false;
    private boolean isGrayScaled = false;

    
    public LoyaltyVoucherCard(Context context) {
        super(context);
        init(null);
    }

    public LoyaltyVoucherCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoyaltyVoucherCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
    }

    private void setAttributes(AttributeSet attributeSet) {
    }

    @Override
    protected int setContent() {
        return R.layout.card_loyalty_voucher;
    }

    public LoyaltyVoucherCard setVoucherName(String voucherName) {
        voucherNameTv.setText(voucherName);
        return this;
    }

    public LoyaltyVoucherCard setVoucherShortDescription(String voucherDescription){
        if(voucherDescription != null && !voucherDescription.equals("")) {
            String label = LoyaltyLabels.getLoyaltyVoucherOfferedBy() + " " + voucherDescription;
            voucherShortDescriptionTv.setText(label);
            voucherShortDescriptionTv.setVisibility(VISIBLE);
        } else {
            voucherShortDescriptionTv.setVisibility(INVISIBLE);
        }
        return this;
    }

    public LoyaltyVoucherCard setVoucherDiscount(String voucherDiscount) {
        if(isColapsed){
            expandDiscountContainer();
        }
        voucherDiscountValueTv.setText(voucherDiscount);
        return this;
    }

    public LoyaltyVoucherCard setVoucherDiscountLabel(String text){
       if(isColapsed){
           expandDiscountContainer();
       }
       voucherDiscountLabel.setText(text);
        return this;
    }

    private void expandDiscountContainer(){
        isColapsed = false;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) discountContainer.getLayoutParams();
        layoutParams.width = ScreenMeasure.dpToPx(92);
        discountContainer.setLayoutParams(layoutParams);
    }

    public LoyaltyVoucherCard colapseDiscountContainer(){
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) discountContainer.getLayoutParams();
        layoutParams.width = ScreenMeasure.dpToPx(5);
        discountContainer.setLayoutParams(layoutParams);
        isColapsed = true;
        voucherDiscountValueTv.setText("");
        voucherDiscountLabel.setText("");

        return this;
    }

    public LoyaltyVoucherCard setDiscountContainerColor(int color){
        discountContainer.setBackgroundResource(color);
        return this;
    }

    public LoyaltyVoucherCard showVoucherRedBand(){
        voucherRedWarningBandImageView.setVisibility(VISIBLE);
        return this;
    }

    public LoyaltyVoucherCard hideVoucherRedBand(){
        voucherRedWarningBandImageView.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherCard setVoucherExpirationWarningText(Long date, Long serverSysDate){
        if(date == null) {
            hideVoucherExpirationWarningText();
            return this;
        }
        if(isExpired(date, serverSysDate)){
            String label = LoyaltyLabels.getLoyaltyVoucherExpiredMessage();
            voucherExpirationWarningTv.setText(label);
            voucherExpirationWarningTv.setVisibility(VISIBLE);
        }
        else {
            Calendar voucherExpirationDate = Calendar.getInstance();
            voucherExpirationDate.setTimeInMillis(date);
            String labelString;
            SpannableString label;
            if(DateUtils.isToday(date, serverSysDate)){
                labelString = LoyaltyLabels.getLoyaltyVoucherExpiresTodayLabel();
                label = new SpannableString(labelString);
                label.setSpan(new StyleSpan(Typeface.BOLD),
                        LoyaltyLabels.getLoyaltyVoucherExpireLabel().length(),
                        (LoyaltyLabels.getLoyaltyVoucherExpireLabel().length() + 1 + LoyaltyLabels.getLoyaltyVouchersTodayLabel().length()),
                        0);
                voucherExpirationWarningTv.setText(label);
                voucherExpirationWarningTv.setVisibility(VISIBLE);
            } else
            if(DateUtils.isTommorow(date, serverSysDate)){
                labelString = LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel() + " " + 1 + " " + LoyaltyLabels.getLoyaltyVoucherTomorrowProfitLabel();
                label = new SpannableString(labelString);
                label.setSpan(new StyleSpan(Typeface.BOLD),
                        LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel().length(),
                        (LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel().length() + 1 + LoyaltyLabels.getLoyaltyVoucherTomorrowBoldLabel().length()),
                        0);
                voucherExpirationWarningTv.setText(label);
                voucherExpirationWarningTv.setVisibility(VISIBLE);
            }
            else if(DateUtils.isInTwoDays(date, serverSysDate)) {
                labelString = LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel() + " " + 2 + " " + LoyaltyLabels.getLoyaltyVoucherDaysProfitLabel();
                label = new SpannableString(labelString);
                label.setSpan(new StyleSpan(Typeface.BOLD),
                        LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel().length(),
                        (LoyaltyLabels.getLoyaltyVoucherExpiresDaysLabel().length() + 1 + LoyaltyLabels.getLoyaltyVoucherDaysBoldLabel().length()),
                        0);
                voucherExpirationWarningTv.setText(label);
                voucherExpirationWarningTv.setVisibility(VISIBLE);
            }
            else {
                hideVoucherExpirationWarningText();
            }
        }
        return this;
    }


    public LoyaltyVoucherCard hideVoucherExpirationWarningText(){
        voucherExpirationWarningTv.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherCard hideArrowIndicator(){
        arrowIndicator.setVisibility(GONE);
        return this;
    }

    public LoyaltyVoucherCard showArrowIndicator(){
        arrowIndicator.setVisibility(VISIBLE);
        return this;
    }

    public LoyaltyVoucherCard setImagesGrayed(){
        if(!isGrayScaled) {
            isGrayScaled = true;
            setProductImageViewGrayed();
        }
        return this;
    }
    public LoyaltyVoucherCard setImagesColored(){
        if(isGrayScaled){
            isGrayScaled = false;
            setProductImageViewColored();
        }
        return this;
    }

    public LoyaltyVoucherCard setProductImageViewGrayed(){
        if(voucherProductImageView !=null){
            ImageViewUtil.setGrayScaleImageFilter(voucherProductImageView);
        }
        return this;
    }

    public LoyaltyVoucherCard setProductImageViewColored(){
        if( voucherProductImageView !=null){
            voucherProductImageView.setColorFilter(null);
        }
        return this;
    }

    public LoyaltyVoucherCard loadProductImage(String url){
        if(getContext()!=null && url!=null && !url.isEmpty()){
            Glide.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.vodafone_voucher_mall_placeholder)
                    .error(R.drawable.vodafone_voucher_mall_placeholder)
                    .into(voucherProductImageView);
        }
        if(url == null){
            Glide.with(getContext()).load(R.drawable.vodafone_voucher_mall_placeholder)
                    .placeholder(R.drawable.vodafone_voucher_mall_placeholder)
                    .error(R.drawable.vodafone_voucher_mall_placeholder)
                    .into(voucherProductImageView);
        }
        return this;
    }

    public boolean isExpired(Long expiryDate, Long serverSysDate) {

        try {
            return expiryDate < serverSysDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
