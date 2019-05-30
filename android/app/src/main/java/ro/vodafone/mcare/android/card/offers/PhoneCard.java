package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/30/2017.
 */

public class PhoneCard extends VodafoneAbstractCard {



    @BindView(R.id.title_tv)
    VodafoneTextView titleTextView;
    @BindView(R.id.details_tv)
    VodafoneTextView detailsTextView;
    @BindView(R.id.price_tv)
    VodafoneTextView priceTextView;

    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.phone_imageView)
    AppCompatImageView phoneImageView;

    String details = "";

    String dot ="\u2022";
    String end ="\n";

    public PhoneCard(Context context) {
        super(context);
        init(null);
    }

    public PhoneCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhoneCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
        //addCardTitle("Telefon recomandat",R.color.card_title_purple);
    }
    private void setAttributes(AttributeSet attributeSet) {
    }
    @Override
    protected int setContent() {
        return R.layout.card_phone;
    }

    public PhoneCard setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }
    public PhoneCard setDetails(String detail){
        details=detail;
        detailsTextView.setText(details);
        return this;
    }


    public PhoneCard setPrice(SpannableStringBuilder price){
        priceTextView.setText(price);
        return this;
    }
    public PhoneCard loadImage(String url){
        Glide.with(getContext()).load(url).placeholder(R.drawable.phone_no_image).into(phoneImageView);
        return this;
    }

    public PhoneCard hideArrow(){
        arrowIndicator.setVisibility(GONE);
        return this;
    }

    public PhoneCard setPricePurple(SpannableStringBuilder price){
        priceTextView.setText(price);
        priceTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.purple));
        return this;
    }

}
