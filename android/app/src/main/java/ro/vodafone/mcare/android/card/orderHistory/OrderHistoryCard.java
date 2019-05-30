package ro.vodafone.mcare.android.card.orderHistory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user2 on 4/20/2017.
 */



public class OrderHistoryCard extends VodafoneAbstractCard {

    @BindView(R.id.spacer)
    View spacerView;
    @BindView(R.id.order_number_tv)
    VodafoneTextView orderNumberTv;
    @BindView(R.id.order_status_tv)
    VodafoneTextView orderStatusTv;
    @BindView(R.id.order_date_submitted_tv)
    VodafoneTextView orderDateSubmittedTv;
    @BindView(R.id.order_cost_tv)
    VodafoneTextView orderCostTv;
    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    String dot ="\u2022";
    String end ="\n";

    final int imageSizeInDP =32;

    public OrderHistoryCard(Context context) {
        super(context);
        init(null);
    }

    public OrderHistoryCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OrderHistoryCard(Context context, AttributeSet attrs, int defStyleAttr) {
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
        return R.layout.card_order_history;
    }

    public void showError(String errorText){
        clearContent();
        super.showError(true, errorText);
    }

    public OrderHistoryCard setOrderNumber(String orderNumber) {
        if(orderNumber != null) {
            orderNumberTv.setText(orderNumber);
        }
        return this;
    }
    public OrderHistoryCard setOrderStatus(String orderStatus){
        orderStatusTv.setText("Status: " + orderStatus);
        return this;
    }


    public OrderHistoryCard setOrderCost(String orderCost){


        String text = "Cost: "+ orderCost + " RON";
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new StyleSpan(Typeface.BOLD),0,text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        orderCostTv.setText(sp);
        return this;
    }
    public OrderHistoryCard setOrderDate(String orderDate){

        String text = "Data plasării: " + orderDate;
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new StyleSpan(Typeface.BOLD),0,text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        orderDateSubmittedTv.setText(sp);
        return this;
    }

    private LinearLayout getContentGroupView(){
        return  (LinearLayout) findViewById(R.id.text_group);
    }

    public OrderHistoryCard addExtraText(String text){
        LinearLayout linearLayout = getContentGroupView();
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText(text);
        vodafoneTextView.setTextColor(Color.BLACK);
        //linearLayout.addView(vodafoneTextView);
        getContentGroupView().addView(vodafoneTextView);
        return this;
    }

    public OrderHistoryCard setDetailsFromHtml(String text){
        if(text==null){
            return this;
        }
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText( TextUtils.fromHtml(text));
        vodafoneTextView.setTextColor(Color.BLACK);
        getContentGroupView().addView(vodafoneTextView);
        return this;
    }

    public OrderHistoryCard hideMainTextViews(){
        orderNumberTv.setVisibility(GONE);
        orderCostTv.setVisibility(GONE);
        orderDateSubmittedTv.setVisibility(GONE);
        return this;
    }
    public OrderHistoryCard hideSpacer(){
        spacerView.setVisibility(GONE);
        return this;
    }
    public OrderHistoryCard hideArrow(){
        arrowIndicator.setVisibility(GONE);
        return this;
    }
}
