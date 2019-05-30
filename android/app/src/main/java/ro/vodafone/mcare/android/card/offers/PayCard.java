package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.AdditionalBenefits;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 4/3/2017.
 */

public class PayCard extends VodafoneGenericCard {


    final int imageSizeInDP = 40;
    @BindView(R.id.card_title)
    VodafoneTextView titleTextView;
    @BindView(R.id.details_tv)
    VodafoneTextView detailsTextView;
    @BindView(R.id.price_plan_details_html)
    VodafoneTextView pricePlanDetailsHtml;
    @BindView(R.id.price_service_tv)
    VodafoneTextView priceServiceTextView;
    @BindView(R.id.price_phone_tv)
    VodafoneTextView pricePhoneTextView;
    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;
    @BindView(R.id.see_price_plan_details_rl)
    RelativeLayout seePricePlanDetailsRl;
    @BindView(R.id.separator_price_plan_and_phone_price)
    View separatorPricePlanAndPhonePrice;
    @BindView(R.id.separator_before_price_plan)
    View separatorBeforePricePlan;
    @BindView(R.id.card_body)
    VodafoneTextView bodyTextView;
    @BindView(R.id.card_primaryButton)
    VodafoneButton primaryButton;
    @BindView(R.id.card_secondaryButton)
    VodafoneButton secondaryButton;
    @BindView(R.id.extra_benefits_tv)
    VodafoneTextView extraBenefitsTv;

    @BindView(R.id.price_plan_details_error_card)
    CardErrorLayout pricePlandDetailsErrorCard;
    boolean isPricePlanDetailEmpty = false;

    String details = "";

    String dot = "\u2022";
    String end = "\n";

    public PayCard(Context context) {
        super(context);
        init(null);
    }

    public PayCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PayCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @OnClick(R.id.arrow)
    public void toggle(View view) {
        if (isExpanded()) {
            unExpand();
        } else {
            expand();
        }
    }

    private void init(AttributeSet attributeSet) {
        ButterKnife.bind(this);
        setAttributes(attributeSet);
        setCardPaddingsInDp(16, 0, 16, 0);
        unExpand();
    }


    public PayCard removePadding() {
        setCardPaddingsInDp(0, 0, 0, 0);
        return this;
    }


    private void setAttributes(AttributeSet attributeSet) {
    }

    @Override
    protected int setContent() {
        return R.layout.card_details_pay;
    }

    public PayCard setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }

    public PayCard setPricePlanDuration(String pricePlanSkuId, Integer duration) {
        if (duration != null) {
            Integer durationYears = duration / 12;

            String formattedDuration;

            if(pricePlanSkuId.contains("keep")) {
                formattedDuration = "Prelungirea abonamentului actual cu păstrarea beneficiilor pe ";
            }
            else{
                formattedDuration = "Cu perioadă contractuală pe ";
            }

            if (durationYears == 1) {
                formattedDuration = formattedDuration + durationYears + " an";
            } else {
                formattedDuration = formattedDuration + durationYears + " ani";
            }
            bodyTextView.setText(formattedDuration);
        }
        return this;
    }

    public PayCard hidePricePlanDuration() {
        bodyTextView.setVisibility(GONE);
        return this;
    }

    public PayCard addToDetails(String detail) {
        if (detail != null) {
            detailsTextView.setText(TextUtils.fromHtmlWithoutSpace(detail));
        }
        return this;
    }


    public PayCard setPricePhone(SpannableStringBuilder text) {
        pricePhoneTextView.setText(text);
        return this;
    }

    public PayCard setPricePlan(SpannableStringBuilder price) {
        priceServiceTextView.setText(price);
        return this;
    }

    private LinearLayout getContentGroupView() {
        return (LinearLayout) findViewById(R.id.pay_card_layout);
    }

    public PayCard addExtraText(String text) {
        LinearLayout linearLayout = getContentGroupView();
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText(text);
        vodafoneTextView.setTextColor(Color.BLACK);
        //linearLayout.addView(vodafoneTextView);
        getContentGroupView().addView(vodafoneTextView);
        return this;
    }

    public PayCard setDetailsFromHtml(String text) {
        if (text == null) {
            pricePlandDetailsErrorCard.setText(RetentionLabels.getNoDetailsFromHtmlError());
            isPricePlanDetailEmpty = true;
            pricePlanDetailsHtml.setVisibility(GONE);
            return this;
        }
        text = TextUtils.removeParagraphTags(text);
        text = TextUtils.fromHtml(text).toString();

        pricePlanDetailsHtml.setText(text);
        return this;
    }

/*android:text="\u2022 5GB internet pe mobil \n\u2022 Nelimiat"*/

    public PayCard setGridImages(RealmList<AdditionalBenefits> additionalBenefits) {

        if (additionalBenefits != null && additionalBenefits.size() != 0) {
            extraBenefitsTv.setText(RetentionLabels.getRetentionExtraBenefits());
            extraBenefitsTv.setVisibility(VISIBLE);
            android.support.v7.widget.GridLayout gridLayout = new android.support.v7.widget.GridLayout(getContext());
            LinearLayout.LayoutParams layoutParamsGrid = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsGrid.setMargins(ScreenMeasure.dpToPx(10), 0, 0, 0);

            gridLayout.setLayoutParams(layoutParamsGrid);
            gridLayout.setColumnOrderPreserved(true);
            gridLayout.setClipToPadding(true);

            for (int i = 0; i < additionalBenefits.size(); i++) {
                if (additionalBenefits.get(i).getBenefitImageURL() != null || !additionalBenefits.get(i).getBenefitImageURL().equals(""))
                    gridLayout.addView(setupImageView(additionalBenefits.get(i).getBenefitImageURL()));
            }
            getContentGroupView().addView(gridLayout, 4);

        }
        return this;
    }

    private AppCompatImageView setupImageView(String url) {
        LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(ScreenMeasure.dpToPx(imageSizeInDP), ScreenMeasure.dpToPx(imageSizeInDP));

        AppCompatImageView imageView = new AppCompatImageView(getContext());
        imageView.setMaxHeight(ScreenMeasure.dpToPx(imageSizeInDP));
        imageView.setMaxWidth(ScreenMeasure.dpToPx(imageSizeInDP));
        imageView.setMinimumHeight(ScreenMeasure.dpToPx(imageSizeInDP));
        imageView.setMinimumWidth(ScreenMeasure.dpToPx(imageSizeInDP));
        Log.d(TAG, url);
        Glide.with(getContext())
                .load(url)
                .override(ScreenMeasure.dpToPx(imageSizeInDP), ScreenMeasure.dpToPx(imageSizeInDP))
                .into(imageView);

        layoutParamsImage.setMargins(0, 0, ScreenMeasure.dpToPx(10), 0);
        imageView.setLayoutParams(layoutParamsImage);
        return imageView;
    }

    public PayCard hideMainTextViews() {
        titleTextView.setVisibility(GONE);
        pricePhoneTextView.setVisibility(GONE);
        priceServiceTextView.setVisibility(GONE);
        return this;
    }


    public PayCard addSelectedButton(String text) {

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        Button button = new Button(getContext());
        button.setBackgroundColor(Color.BLACK);

        relativeLayout.setBackground(button.getBackground());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        VodafoneTextView compatButton = new VodafoneTextView(getContext());
        compatButton.setText(text);
        compatButton.setTextColor(Color.WHITE);
        compatButton.setGravity(Gravity.CENTER);//roundbuttondarkblue
        compatButton.setId(R.id.button_label);
        compatButton.setSingleLine();
        relativeLayout.addView(compatButton, layoutParams);

        DynamicColorImageView colorImageView = new DynamicColorImageView(getContext());
        Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_circle_greeen_24dp);
        colorImageView.setBackgroundColorWithRes(R.color.white);
        colorImageView.setImageDrawable(img);
        LayoutParams imageLayoutParams = new LayoutParams(ScreenMeasure.dpToPx(24), ScreenMeasure.dpToPx(24));
        imageLayoutParams.addRule(LEFT_OF, R.id.button_label);
        int margins = ScreenMeasure.dpToPx(12);
        imageLayoutParams.setMargins(margins, margins, margins, margins);
        imageLayoutParams.setMarginEnd(ScreenMeasure.dpToPx(10));

        imageLayoutParams.addRule(CENTER_HORIZONTAL, TRUE);
        relativeLayout.addView(colorImageView, imageLayoutParams);

        getContentGroupView().addView(relativeLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(48)));

        return this;
    }

    public PayCard addUnselectedButton(String text) {
        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.CardTertiaryButton);

        AppCompatButton compatButton = new AppCompatButton(newContext);
        compatButton.setActivated(true);
        compatButton.setText(text);
        getContentGroupView().addView(compatButton);
        return this;
    }

    public PayCard hidePricePlanDetails() {
        seePricePlanDetailsRl.setVisibility(GONE);
        return this;
    }

    public PayCard showPricePlanDetails() {
        seePricePlanDetailsRl.setVisibility(VISIBLE);
        return this;
    }

    public PayCard hidePricePlanSeparator() {
        separatorPricePlanAndPhonePrice.setVisibility(GONE);
        separatorBeforePricePlan.setVisibility(GONE);
        return this;
    }

    public PayCard showPricePlanSeparator() {
        separatorPricePlanAndPhonePrice.setVisibility(VISIBLE);
        separatorBeforePricePlan.setVisibility(VISIBLE);
        return this;
    }

    private void unExpand() {
        final RotateAnimation animRotateDown = new RotateAnimation(180.0f, 0.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);
        arrowIndicator.startAnimation(animRotateDown);
        hideDetails();
    }

    private void expand() {
        final RotateAnimation animRotateUp = new RotateAnimation(0.0f, 180.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateUp.setFillAfter(true);
        animRotateUp.setDuration(120);
        arrowIndicator.startAnimation(animRotateUp);
        showDetails();
    }

    private void hideDetails() {
        pricePlanDetailsHtml.setVisibility(GONE);
        pricePlandDetailsErrorCard.setVisibility(GONE);
    }

    private void showDetails() {
        pricePlanDetailsHtml.setVisibility(VISIBLE);
        if(isPricePlanDetailEmpty) {
            pricePlandDetailsErrorCard.setVisibility(VISIBLE);
        }
    }

    private boolean isExpanded() {
        if (pricePlanDetailsHtml.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    public PayCard setPrimaryButtonVisibility(int stockLevel) {
        if (stockLevel <= 0) {
            primaryButton.setVisibility(GONE);
        } else {
            primaryButton.setVisibility(VISIBLE);
        }
        return this;
    }

    public PayCard setPrimaryButtonText(String text) {
        this.primaryButton.setText(text);
        return this;
    }

    public PayCard setSecondaryButtontext(String text) {
        this.secondaryButton.setText(text);
        return this;
    }

    public PayCard setPrimaryButtonClickListener(View.OnClickListener clickListener) {
        this.primaryButton.setOnClickListener(clickListener);
        return this;
    }

    public PayCard setSecondaryButtonClickListener(View.OnClickListener clickListener) {
        this.secondaryButton.setOnClickListener(clickListener);
        return this;
    }
}
