package ro.vodafone.mcare.android.card.orderHistory;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user2 on 4/21/2017.
 */

public class OrderHistoryDetailsCard extends VodafoneAbstractCard {

    @BindView(R.id.title_tv)
    VodafoneTextView titleTextView;
    @BindView(R.id.details_tv)
    VodafoneTextView detailsTextView;

    public OrderHistoryDetailsCard(Context context) {
        super(context);
        init(null);
    }

    public OrderHistoryDetailsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OrderHistoryDetailsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public OrderHistoryDetailsCard setTitle(String title) {
        if(title == null)
            title = "";
        else
            title = title.replaceAll("&#x103;", "ă");

        StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
        spannableStringBuilder.setSpan(boldStyleSpan, 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        titleTextView.setText(spannableStringBuilder);
        return this;
    }
    public OrderHistoryDetailsCard setDetails(String detail){

        if(detail == null)
            detail = "";
        else
            detail = detail.replaceAll("&#x103;", "ă");

        detailsTextView.setText(detail);
        return this;
    }

    private void setAttributes(AttributeSet attributeSet) {
    }

    public void showError(String errorText){
        clearContent();
        super.showError(true, errorText);
    }

    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
    }

    private LinearLayout getContentGroupView(){
        return  (LinearLayout) findViewById(R.id.text_group);
    }

    public OrderHistoryDetailsCard addExtraTextGroup(String title, String detail, boolean hideTitle, boolean showSeparator) {

        if(title == null)
            title = "";
        else
            title = title.replaceAll("&#x103;", "ă");

        if(detail == null)
            detail = "";
        else
            detail = detail.replaceAll("&#x103;", "ă");

        View extraView = LayoutInflater.from(getContext()).inflate(R.layout.simple_extra_horizontal_detail_card, null);
        VodafoneTextView vodafoneTitleView = (VodafoneTextView) extraView.findViewById(R.id.extra_title_tv);
        VodafoneTextView vodafoneDetailView = (VodafoneTextView) extraView.findViewById(R.id.extra_details_tv);

        if(hideTitle) {
            if(!title.equals("")) {
                detail = title + " " + detail;
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), 0, ScreenMeasure.dpToPx(10));
            vodafoneDetailView.setLayoutParams(layoutParams);

            StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(detail);
            spannableStringBuilder.setSpan(boldStyleSpan, 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            vodafoneTitleView.setVisibility(GONE);
            vodafoneDetailView.setText(spannableStringBuilder);
        } else {
            StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
            spannableStringBuilder.setSpan(boldStyleSpan, 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            vodafoneTitleView.setText(spannableStringBuilder);
            vodafoneDetailView.setText(detail);
        }

        View separatorLine = (View) extraView.findViewById(R.id.separator_line);
        if(!showSeparator)
        {
            separatorLine.setVisibility(GONE);
        }

        getContentGroupView().addView(extraView);
        return this;
    }


    @Override
    protected int setContent() {
        return R.layout.card_order_details;
    }
}
