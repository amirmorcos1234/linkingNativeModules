package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayout;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.AdditionalBenefits;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.utils.butterknife.ButterKnifeActions;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static android.view.Gravity.CENTER;

/**
 * Created by Victor Radulescu on 3/24/2017.
 */
//SEE Top Up recurrent page
public class ServiceCard extends VodafoneAbstractCard {

    @BindView(R.id.spacer)
    View spacerView;
    @BindView(R.id.title_tv)
    VodafoneTextView titleTextView;
    @BindView(R.id.details_tv)
    VodafoneTextView detailsTextView;
    @BindView(R.id.price_tv)
    VodafoneTextView priceTextView;
    @BindView(R.id.period_tv)
    VodafoneTextView periodTextView;
    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;
    @BindView(R.id.extra_benefits_tv)
    VodafoneTextView extraBenefitsTv;

    String details = "";
    Context context;

    String dot ="\u2022";
    String end ="\n";

    final int imageSizeInDP = 40;

    @BindViews({R.id.details_tv,R.id.price_tv,R.id.period_tv,R.id.extra_benefits_tv})
    List<TextView> allTextsExceptTitle;

    public ServiceCard(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public ServiceCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public ServiceCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
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
        return R.layout.card_service;
    }

    public ServiceCard setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }
    public ServiceCard setSingleTitleText(String title,int height){

        ButterKnife.apply(allTextsExceptTitle, ButterKnifeActions.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
        if(layoutParams==null){
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        }else{
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = height;
        }
       // layoutParams.gravity = Gravity.CENTER;
        titleTextView.setLayoutParams(layoutParams);
        titleTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        titleTextView.setGravity(CENTER);
        return setTitle(title);
    }

    public ServiceCard addToDetails(String detail){
        details+=detail;
        detailsTextView.setText(details);
        return this;
    }


    public ServiceCard setPeriod(SpannableStringBuilder periodText){
        periodTextView.setText(periodText);
        return this;
    }
    public ServiceCard setPrice(String priceText){
        priceTextView.setText(priceText);
        return this;
    }

    public ServiceCard setPurplePriceColor(){
        priceTextView.setTextColor(ContextCompat.getColor(context, R.color.purple));
        return this;
    }

    private LinearLayout getContentGroupView(){
        return  (LinearLayout) findViewById(R.id.text_group);
    }

    public ServiceCard addExtraText(String text){
        LinearLayout linearLayout = getContentGroupView();
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText(text);
        vodafoneTextView.setTextColor(Color.BLACK);
        //linearLayout.addView(vodafoneTextView);
        getContentGroupView().addView(vodafoneTextView);
        return this;
    }
    public ServiceCard setDetailsFromHtml(String text){
        if(text==null){
            return this;
        }
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText( TextUtils.fromHtml(text));
        vodafoneTextView.setTextColor(Color.BLACK);
        getContentGroupView().addView(vodafoneTextView);
        return this;
    }
    public ServiceCard setDetailsFromHtml(String text,boolean addNewView, boolean formatWithSpaces){
        if(addNewView){
            return setDetailsFromHtml(text);
        }

        if(text == null){
            return this;
        }
        else
        {
            if(text.equals("")){
                return this;
            }
        }

        text = TextUtils.removeParagraphTags(text);
        if(formatWithSpaces){
            detailsTextView.setText(TextUtils.fromHtml(text));
            detailsTextView.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        }
        else {
            detailsTextView.setText(TextUtils.fromHtmlWithoutSpace(text));
            detailsTextView.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        }

        return this;
    }

    public ServiceCard setGridImages(RealmList<AdditionalBenefits> additionalBenefits){

        if(additionalBenefits != null && additionalBenefits.size() != 0) {
            extraBenefitsTv.setText(RetentionLabels.getRetentionExtraBenefits());
            extraBenefitsTv.setVisibility(VISIBLE);
            GridLayout gridLayout = new GridLayout(getContext());
            LinearLayout.LayoutParams layoutParamsGrid = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            gridLayout.setLayoutParams(layoutParamsGrid);
            gridLayout.setColumnOrderPreserved(true);
            gridLayout.setClipToPadding(true);

            for(int i = 0 ; i < additionalBenefits.size() ; i++){
                if(additionalBenefits.get(i).getBenefitImageURL() != null && !additionalBenefits.get(i).getBenefitImageURL().equals(""))
                    gridLayout.addView(setupImageView(additionalBenefits.get(i).getBenefitImageURL()));
            }
            getContentGroupView().addView(gridLayout);

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
        Glide.with(context)
                .load(url)
                .override(ScreenMeasure.dpToPx(imageSizeInDP), ScreenMeasure.dpToPx(imageSizeInDP))
                .into(imageView);

        layoutParamsImage.setMargins(0 , 0 , ScreenMeasure.dpToPx(10), 0);
        imageView.setLayoutParams(layoutParamsImage);
        return imageView;
    }

    public ServiceCard hideMainTextViews(){
        titleTextView.setVisibility(GONE);
        periodTextView.setVisibility(GONE);
        priceTextView.setVisibility(GONE);
        return this;
    }
    public ServiceCard hideSpacer(){
        spacerView.setVisibility(GONE);
        return this;
    }
    public ServiceCard hideArrow(){
        arrowIndicator.setVisibility(GONE);
        return this;
    }
    public ServiceCard addSelectedButton(String text, OnClickListener onClickListener){

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        Button button = new Button(getContext());
        button.setBackgroundColor(Color.BLACK);

        relativeLayout.setBackground(button.getBackground());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT,TRUE);
        VodafoneTextView compatButton = new VodafoneTextView(getContext());
        compatButton.setText(text);
        compatButton.setTextColor(Color.WHITE);
        compatButton.setGravity(CENTER);//roundbuttondarkblue
        compatButton.setId(R.id.button_label);
        compatButton.setSingleLine();
        relativeLayout.addView(compatButton,layoutParams);

        DynamicColorImageView colorImageView = new DynamicColorImageView(getContext());
        Drawable img =  ContextCompat.getDrawable(getContext(),R.drawable.ic_check_circle_greeen_24dp);
        //colorImageView.setBackgroundColorWithRes(R.color.white);
        colorImageView.setImageDrawable(img);
        LayoutParams imageLayoutParams = new LayoutParams(ScreenMeasure.dpToPx(24),ScreenMeasure.dpToPx(24));
        imageLayoutParams.addRule(LEFT_OF,R.id.button_label);
        int margins = ScreenMeasure.dpToPx(12);
        imageLayoutParams.setMargins(margins,margins,margins,margins);
        imageLayoutParams.setMarginEnd(ScreenMeasure.dpToPx(10));

        imageLayoutParams.addRule(CENTER_HORIZONTAL,TRUE);
        relativeLayout.addView(colorImageView,imageLayoutParams);
        compatButton.setOnClickListener(onClickListener);

        LinearLayout.LayoutParams groupParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenMeasure.dpToPx(48));
        groupParams.topMargin=ScreenMeasure.dpToPx(10);
        getContentGroupView().addView(relativeLayout, groupParams);

        return this;
    }
    public ServiceCard addUnselectedButton(String text, OnClickListener onClickListener){
        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.CardTertiaryButton);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenMeasure.dpToPx(48));
        buttonParams.topMargin=ScreenMeasure.dpToPx(10);

        VodafoneButton compatButton = new VodafoneButton(newContext, null, 0);
        compatButton.setLayoutParams(buttonParams);
        compatButton.setActivated(true);
        compatButton.setText(text);
        compatButton.setGravity(CENTER);
        compatButton.setOnClickListener(onClickListener);
        getContentGroupView().addView(compatButton);
        return this;
    }
}
