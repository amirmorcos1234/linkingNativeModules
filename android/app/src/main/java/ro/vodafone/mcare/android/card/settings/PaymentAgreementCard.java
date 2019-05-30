package ro.vodafone.mcare.android.card.settings;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.datepickers.PACalendarPickerView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user2 on 4/25/2017.
 */

public class PaymentAgreementCard extends VodafoneAbstractCard {

    private String title, body;

    @BindView(R.id.calendar_title)
    VodafoneTextView titleTextView;
    @BindView(R.id.calendar_subtitle_details)
    VodafoneTextView subtitleDetailsTextView;
    @BindView(R.id.calendar_picker_view)
    PACalendarPickerView calendarPickerView;
    @BindView(R.id.postpone_payment_button)
    VodafoneButton postponePaymentButton;

    private int maxPaymentDays;
    private long currentDate;
    private ArrayList<Date> selectableDates;


    public PaymentAgreementCard(Context context) {
        super(context);
        init(null);
    }

    public PaymentAgreementCard(Context context,int maxPaymentDays, long currentDate, ArrayList<Date> selectableDates) {
        super(context);
        this.maxPaymentDays = maxPaymentDays;
        this.currentDate = currentDate;
        this.selectableDates = selectableDates;
        init(null);
    }

    public PaymentAgreementCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public PaymentAgreementCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @Override
    protected int setContent() {
        return R.layout.payment_agreement_card;
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);
        setCardPaddingsInDp(16,0,16,0);
        //showLoading();
        // showError();
    }

    public void build(){
        setTextIfNotNull(titleTextView, title);
        setTextIfNotNull(subtitleDetailsTextView, body);
        calendarPickerView.setMaxPaymentDaysCurrentDate(maxPaymentDays, currentDate, selectableDates);
    }

    public void hideLoading(){
        super.hideLoading();
    }
    public void showError(){
        clearContent();
        super.showError(true);
    }

    @Override
    public PaymentAgreementCard showLoading(boolean hideContent){
        clearContent();
        super.showLoading(true);
        return this;
    }

    public PaymentAgreementCard setTitle(String title){
        this.title = title;
        return this;
    }

    public PaymentAgreementCard setSubTitleDetails(String body){
        this.body = body;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitleDetails() {
        return body;
    }

    public PaymentAgreementCard setButtonClickListener(View.OnClickListener clickListener){
        this.postponePaymentButton.setOnClickListener(clickListener);
        return this;
    }

    public void disableButton() {
        postponePaymentButton.setClickable(false);
        postponePaymentButton.setEnabled(false);
    }

    public void enableButton() {
        postponePaymentButton.setClickable(true);
        postponePaymentButton.setEnabled(true);
    }


    public PaymentAgreementCard removePaddings(){
        setCardPaddingsInDp(0,0,0,0);
        return this;
    }

    public PaymentAgreementCard setBackground(@ColorRes int color){
        try{
            findViewById(R.id.payment_agreement_card).setBackgroundResource(color);
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public PACalendarPickerView getCalendarPickerView() {
        return calendarPickerView;
    }

    public VodafoneButton getPostponePaymentButton() {
        return postponePaymentButton;
    }

    private void setTextIfNotNull(TextView textView, String text){
        if(text==null){
            textView.setVisibility(GONE);
        }else{
            textView.setText(text);
        }
    }
}
