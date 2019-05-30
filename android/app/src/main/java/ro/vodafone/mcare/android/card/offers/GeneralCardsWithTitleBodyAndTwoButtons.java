package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
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
 * Created by Victor Radulescu on 3/22/2017.
 */

public class GeneralCardsWithTitleBodyAndTwoButtons extends VodafoneAbstractCard {


    private String title, body, primaryButtonMessage,secondaryButtonMessage;

    @BindView(R.id.card_title)
    VodafoneTextView titleTextView;
    @BindView(R.id.card_body)
    VodafoneTextView bodyTextView;
    @BindView(R.id.card_primaryButton)
    VodafoneButton primaryButton;
    @BindView(R.id.card_secondaryButton)
    VodafoneButton secondaryButton;



    public GeneralCardsWithTitleBodyAndTwoButtons(Context context) {
        super(context);
        init(null);
    }

    public GeneralCardsWithTitleBodyAndTwoButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public GeneralCardsWithTitleBodyAndTwoButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @Override
    protected int setContent() {
        return R.layout.general_title_body_two_buttons_card;
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);
        setCardPaddingsInDp(10,0,10,0);
        //showLoading();
       // showError();
    }

    public void build(){

        setTextIfNotNull(titleTextView,title);
        setTextIfNotNull(bodyTextView,body);
        setTextIfNotNull(primaryButton,primaryButtonMessage);
        setTextIfNotNull(secondaryButton,secondaryButtonMessage);

    }

    public GeneralCardsWithTitleBodyAndTwoButtons setTitleBold(){
        titleTextView.setTypeface(null, Typeface.BOLD);
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setTitleTextSize(int size) {
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
    public GeneralCardsWithTitleBodyAndTwoButtons showLoading(boolean hideContent){
        clearContent();
        super.showLoading(true);
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setTitle(String title){
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setBody(String body) {
        this.body = body;
        return this;
    }

    public VodafoneTextView getBodyTextView() {
        return bodyTextView;
    }

    public String getPrimaryButtonMessage() {
        return primaryButtonMessage;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setPrimaryButtonMessage(String primaryButtonMessage) {
        this.primaryButtonMessage = primaryButtonMessage;
        return this;
    }
    public GeneralCardsWithTitleBodyAndTwoButtons setPrimaryButtonClickListener(View.OnClickListener clickListener){
        this.primaryButton.setOnClickListener(clickListener);
        return this;
    }

    public String getSecondaryButtonMessage() {
        return secondaryButtonMessage;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setSecondaryButtonMessage(String secondaryButtonMessage) {
        this.secondaryButtonMessage = secondaryButtonMessage;
        return this;
    }
    public GeneralCardsWithTitleBodyAndTwoButtons setSecondaryButtonClickListener(View.OnClickListener clickListener){
        this.secondaryButton.setOnClickListener(clickListener);
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setWhiteColor(){
        this.secondaryButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons removePaddings(){
        setCardPaddingsInDp(0,0,0,0);
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setPaddings(int left, int top, int right, int bottom){
        findViewById(R.id.general_card_group_view).setPadding(ScreenMeasure.dpToPx(left), ScreenMeasure.dpToPx(top), ScreenMeasure.dpToPx(right), ScreenMeasure.dpToPx(bottom));
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setNormalPaddings(){
        findViewById(R.id.general_card_group_view).setPadding(0, ScreenMeasure.dpToPx(10), 0, ScreenMeasure.dpToPx(10));
        return this;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons setBackground(@ColorRes int color){
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
