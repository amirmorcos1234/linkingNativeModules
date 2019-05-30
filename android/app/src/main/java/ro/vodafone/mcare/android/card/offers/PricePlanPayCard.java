package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;


/**
 * Created by Serban Radulescu on 2/9/2018.
 */

public class PricePlanPayCard extends VodafoneAbstractCard {


    private String title, body, primaryButtonMessage,secondaryButtonMessage;

    @BindView(R.id.card_title)
    VodafoneTextView titleTextView;
    @BindView(R.id.card_body)
    VodafoneTextView bodyTextView;
    @BindView(R.id.card_primaryButton)
    VodafoneButton primaryButton;
    @BindView(R.id.card_secondaryButton)
    VodafoneButton secondaryButton;



    public PricePlanPayCard(Context context) {
        super(context);
        init(null);
    }

    public PricePlanPayCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public PricePlanPayCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @Override
    protected int setContent() {
        return R.layout.price_plan_pay_card;
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);
        setCardPaddingsInDp(10,0,10,0);
    }

    public void build(){

        setTextIfNotNull(titleTextView,title);
        setTextIfNotNull(bodyTextView,body);
        setTextIfNotNull(primaryButton,primaryButtonMessage);
        setTextIfNotNull(secondaryButton,secondaryButtonMessage);

    }

    public PricePlanPayCard setTitleBold(){
        titleTextView.setTypeface(null, Typeface.BOLD);
        return this;
    }

    public PricePlanPayCard setTitleTextSize(int size) {
        titleTextView.setTextSize(ScreenMeasure.spToPx(size));
        return this;
    }

    public void hideLoading(){
        super.hideLoading();
    }
    public void showError(){
        clearContent();
        super.showError(true);
    }

    @Override
    public PricePlanPayCard showLoading(boolean hideContent){
        clearContent();
        super.showLoading(true);
        return this;
    }

    public PricePlanPayCard setTitle(String title){
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public PricePlanPayCard setBody(String body) {
        this.body = body;
        return this;
    }

    public VodafoneTextView getBodyTextView() {
        return bodyTextView;
    }

    public String getPrimaryButtonMessage() {
        return primaryButtonMessage;
    }

    public PricePlanPayCard setPrimaryButtonMessage(String primaryButtonMessage) {
        this.primaryButtonMessage = primaryButtonMessage;
        return this;
    }
    public PricePlanPayCard setPrimaryButtonClickListener(View.OnClickListener clickListener){
        this.primaryButton.setOnClickListener(clickListener);
        return this;
    }

    public String getSecondaryButtonMessage() {
        return secondaryButtonMessage;
    }

    public PricePlanPayCard setSecondaryButtonMessage(String secondaryButtonMessage) {
        this.secondaryButtonMessage = secondaryButtonMessage;
        return this;
    }
    public PricePlanPayCard setSecondaryButtonClickListener(View.OnClickListener clickListener){
        this.secondaryButton.setOnClickListener(clickListener);
        return this;
    }

    public PricePlanPayCard removePaddings(){
        setCardPaddingsInDp(0,0,0,0);
        return this;
    }

    public PricePlanPayCard setPaddings(int left, int top, int right, int bottom){
        findViewById(R.id.general_card_group_view).setPadding(ScreenMeasure.dpToPx(left), ScreenMeasure.dpToPx(top), ScreenMeasure.dpToPx(right), ScreenMeasure.dpToPx(bottom));
        return this;
    }

    public PricePlanPayCard setNormalPaddings(){
        findViewById(R.id.general_card_group_view).setPadding(0, ScreenMeasure.dpToPx(10), 0, ScreenMeasure.dpToPx(10));
        return this;
    }

    public PricePlanPayCard setBackground(@ColorRes int color){
        try{
            findViewById(R.id.general_card_group_view).setBackgroundResource(color);
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    private void setTextIfNotNull(TextView textView, String text){
        if(text==null){
            textView.setVisibility(GONE);
        }else{
            textView.setText(text);
        }
    }

    public VodafoneButton getPrimaryButton() {
        return primaryButton;
    }

    public VodafoneButton getSecondaryButton() {
        return secondaryButton;
    }
}
