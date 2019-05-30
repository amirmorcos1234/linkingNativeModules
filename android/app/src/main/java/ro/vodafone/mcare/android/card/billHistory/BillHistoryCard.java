package ro.vodafone.mcare.android.card.billHistory;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.UnpayedBillFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail.*;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */
public class BillHistoryCard extends VodafoneAbstractCard {

    @BindView(R.id.bill_history_card_container)
    RelativeLayout billHistoryCardLayout;

    @BindView(R.id.date)
    VodafoneTextView date;

    @BindView(R.id.cost)
    VodafoneTextView cost;

    @BindView(R.id.aditional_cost)
    VodafoneTextView aditionalCost;

    @BindView(R.id.bill_status)
    VodafoneTextView billStatus;

    @BindView(R.id.card_image)
    ImageView cardImage;

    private boolean isOlderThanLast3Months;

    private static final String TEMPLATE = "template";

    BillHistoryDetails billHistoryDetails;

    public BillHistoryCard(Context context) {
        super(context);
        init(null);
    }

    public BillHistoryCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BillHistoryCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_bill_history;
    }

    private void init(AttributeSet attrs) {
        ButterKnife.bind(this);
        setNoAditionalCostStyle();
        setArrowVisibility();
        setOnErrorClickListner();
        setCardMargins();
    }

    private void setCardMargins() {
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
    }

    private void setArrowVisibility() {
        if (isClickableByUser()) {
            cardImage.setVisibility(VISIBLE);
        }
    }

    /*
    * Displayed blue if there is no additional cost of bill. Cost control = 0€
    */
    private void setNoAditionalCostStyle() {
        billHistoryCardLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_chart_top_color));
    }

    /*
    * Displayed mauve if there is additional cost of bill. Cost control > 0€
    */
    private void setAditionalCostStyle() {
        billHistoryCardLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
    }

    public BillHistoryCard setOnErrorClickListner() {

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadingView == null || !loadingView.isVisible()) {
                    if (errorView != null && errorView.getVisibility() == VISIBLE) {
                        BillHistoryController.getInstance().requestData();
                    } else {
                        if (isClickableByUser()) {
                            TealiumHelper.tealiumTrackEvent(getClass().getSimpleName(),TealiumConstants.billingOverview_previous_bill_card,TealiumConstants.screen_name, TealiumConstants.bill_card_history);

                            ((BillingOverviewActivity) getContext()).atachFragment(UnpayedBillFragment.newInstance(billHistoryDetails, isOlderThanLast3Months));
                        }
                    }
                }
            }
        });

        return this;
    }

    //TODO move BA logic from View component
    private boolean isClickableByUser() {
        User user = VodafoneController.getInstance().getUser();
        return user instanceof ResCorp
                || user instanceof PrivateUser
                || user instanceof EbuMigrated;
    }

    public BillHistoryCard builCard(BillHistoryDetails billHistoryDetails, boolean isOlderThanLast3Months) {

        this.billHistoryDetails = billHistoryDetails;
        this.isOlderThanLast3Months = isOlderThanLast3Months;

        if (billHistoryDetails.getBillClosedDate() != null) {
            date.setText(WordUtils.capitalize(DateUtils.getDate(String.valueOf(billHistoryDetails.getBillClosedDate()),
                    new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO")))));
        }
        RealmList<HistoryDetail> historyDetails = billHistoryDetails.getHistoryDetails();
        if (historyDetails != null) {
            cost.setText(String.valueOf(NumbersUtils.twoDigitsAfterDecimal(
                    billHistoryDetails.getTotalAmountDue()
            )) + " " + BillingOverviewLabels.getBilling_overview_ron_unit());
        }

        if (billHistoryDetails.getGsmUtilizedServices() > 0) {
            aditionalCost.setText(getFormatAditionalCost(billHistoryDetails.getGsmUtilizedServices()));
            setAditionalCostStyle();
        } else {
            aditionalCost.setVisibility(GONE);
            setNoAditionalCostStyle();
        }
        return this;
}

    private SpannableStringBuilder getFormatAditionalCost(double aditionalCost) {

        String finalText = BillingOverviewLabels.getBilling_overview_aditional_cost();
        String costWithUnit = String.valueOf(NumbersUtils.twoDigitsAfterDecimal(aditionalCost)) + " " + BillingOverviewLabels.getBilling_overview_ron_unit();
        finalText = finalText.replace(TEMPLATE, costWithUnit);
        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

        try {
            int spanStart = finalText.indexOf(costWithUnit);

            StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            ForegroundColorSpan textColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.purple));
            sb.setSpan(boldStyleSpan, spanStart, spanStart + costWithUnit.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(textColorSpan, spanStart, spanStart + costWithUnit.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public BillHistoryCard displayLastCard() {
        cardImage.setVisibility(INVISIBLE);
        this.setOnClickListener(null);
        return this;
    }

    public BillHistoryCard showError(String errorMessage) {
        super.showError(true);
        if (errorMessage != null) {
            errorView.setText(errorMessage);
        }
        return this;
    }

    public BillHistoryCard showLoading() {
        super.showLoading(true);
        return this;
    }

}
