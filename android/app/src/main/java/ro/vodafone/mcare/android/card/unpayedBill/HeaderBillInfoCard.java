package ro.vodafone.mcare.android.card.unpayedBill;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;

import org.apache.commons.lang3.text.WordUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;

/**
 * Created by user on 15.04.2017.
 */

public class HeaderBillInfoCard extends VodafoneAbstractCard {

    @BindView(R.id.section_title)
    VodafoneTextView sectionaTitle;

    @BindView(R.id.total_bill_value)
    VodafoneTextView totalBillValue;

    @BindView(R.id.instructional_text)
    VodafoneTextView instructionalText;

    @BindView(R.id.bill_cycle_date)
    VodafoneTextView billCycleDate;
    WeakReference<OnClickListener> onErrorClickListner;

    public HeaderBillInfoCard(Context context) {
        super(context);
        init(null);
    }

    public HeaderBillInfoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_header_bill_info;
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);

        setupController();
        setTransparentBackground();
        //setOnErrorClickListner();
    }

    @Override
    public VodafoneAbstractCard showError(boolean hideContent) {
        this.setOnClickListener(onErrorClickListner!=null ? onErrorClickListner.get():null);
        return super.showError(hideContent);
    }

    private void setupController(){
        HeaderBillInfoCardController.getInstance().setup(this);
    }

    private void setTransparentBackground(){
        setPadding(0,0,0,0);
        this.setBackgroundColor(Color.TRANSPARENT);
        getCardView().setBackgroundColor(Color.TRANSPARENT);
    }
    //TODO move logic on fragment/activity
    public HeaderBillInfoCard setOnErrorClickListner(WeakReference<OnClickListener> onErrorClickListner){
        if(onErrorClickListner!=null){
            this.onErrorClickListner = onErrorClickListner;
        }
        return this;
    }

    public HeaderBillInfoCard setCardTitle(String text) {
        sectionaTitle.setText(text);
        return this;
    }

    public void setAttributes(Double totalBillValue, long billClosedDate){
        showContent();;
        hideLoading();
        if(errorView != null){
            errorView.setVisibility(GONE);
        }

        this.totalBillValue.setText(NumbersUtils.twoDigitsAfterDecimal(totalBillValue) + " " + BillingOverviewLabels.getBilling_overview_ron_unit());
        billCycleDate.setText(getBillCycleDateFormatedText(WordUtils.capitalize(DateUtils.getDate(String.valueOf(billClosedDate),
                new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"))))));

    }

    private SpannableStringBuilder getBillCycleDateFormatedText(String date) {

        String finalText = "Emitere factură: " + date;

        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);
        try {
            StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(boldStyleSpan, 17, finalText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb;
    }
}
