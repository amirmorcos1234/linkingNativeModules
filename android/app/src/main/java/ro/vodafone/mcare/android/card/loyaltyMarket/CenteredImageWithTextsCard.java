package ro.vodafone.mcare.android.card.loyaltyMarket;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 9/14/2017.
 */

public class CenteredImageWithTextsCard extends VodafoneAbstractCard {

    @BindView(R.id.card_imageView)
    DynamicColorImageView imageView;
    @BindView(R.id.textview_first)
    VodafoneTextView textViewFirst;
    @BindView(R.id.textview_second)
    VodafoneTextView textViewSecond;
    @BindView(R.id.text_container)
    LinearLayout textContainer;

    public CenteredImageWithTextsCard(Context context) {
        super(context);
        init(null);
    }

    public CenteredImageWithTextsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CenteredImageWithTextsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_with_centered_image_and_texts;
    }
    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
    }
    public CenteredImageWithTextsCard setFirstText(String text){
        if(text==null){
            textViewFirst.setVisibility(GONE);
        }else{
            textViewFirst.setVisibility(View.VISIBLE);
            textViewFirst.setText(text);
        }
        return this;
    }

    public CenteredImageWithTextsCard makeFirstTextNotBold() {
        textViewFirst.setFont(VodafoneTextView.TextStyle.VODAFONE_RG);
        textViewFirst.setTextColor(ContextCompat.getColor(getContext(), R.color.pay_bill_bold_text_color));
        textViewFirst.setTextAlignment(TEXT_ALIGNMENT_INHERIT);
        textViewFirst.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        return this;
    }

    public CenteredImageWithTextsCard setSecondText(String text){
        if(text==null){
            textViewSecond.setVisibility(GONE);
        }else{
            textViewSecond.setVisibility(View.VISIBLE);
            textViewSecond.setText(text);
        }
        return this;
    }
    public CenteredImageWithTextsCard loadImage(String url){
        if(url!=null && imageView!=null){
            Glide.with(getContext()).load(url).into(imageView);
        }
        return this;
    }

    public void setAttributes(AttributeSet attributes) {
    }
}
