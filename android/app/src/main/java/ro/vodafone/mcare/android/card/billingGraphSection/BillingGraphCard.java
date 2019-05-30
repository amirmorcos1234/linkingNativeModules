package ro.vodafone.mcare.android.card.billingGraphSection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.CalendarDateValidatorController;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.widget.charts.billing.ChartValue;
import ro.vodafone.mcare.android.widget.charts.billing.ChartValueSerie;
import ro.vodafone.mcare.android.widget.charts.billing.StackedBarChartView;


/**
 * Created by Bivol Pavel on 05.04.2017.
 */
public class BillingGraphCard extends VodafoneAbstractCard {

    @BindView(R.id.price_plan_cost)
    VodafoneTextView pricePlanCost;

    @BindView(R.id.aditional_cost_average)
    VodafoneTextView aditionalCostAverage;

/*    @BindView(R.id.aditional_cost_layout)
    RelativeLayout aditionalCostLayout;

    @BindView(R.id.separator_line)
    View separatorLine;*/

    StackedBarChartView billingStackedBarChart;
    List<BillHistoryDetails> billHistoryList;

    private static final String RON_UNIT = "RON";

    DecimalFormat df = new DecimalFormat("#0.00");

    public BillingGraphCard(Context context) {
        super(context);
        init(null);
    }

    public BillingGraphCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BillingGraphCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_billing_graph_section;
    }

    private void init(AttributeSet attrs) {
        ButterKnife.bind(this);
        setupController();
        setOnErrorClickListner();
    }

    private void setupController() {
        BillingGraphCardController.getInstance().setup(this).requestData();
    }

    public BillingGraphCard setOnErrorClickListner() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (errorView != null && errorView.getVisibility() == VISIBLE) {
                    BillingGraphCardController.getInstance().requestData();
                }
            }
        });
        return this;
    }

    public void buildCard(List<BillHistoryDetails> billHistoryList, Double totalPricePlanCost, Double averageAditionalCost) throws NullPointerException {
        this.billHistoryList = billHistoryList;

        hideLoading();
        buildCharValueSeries();
        setTotalPricePlanCost(totalPricePlanCost);
        setAverageAditionalCost(averageAditionalCost);
    }

    public void buildCharValueSeries() throws NullPointerException {

        ChartValueSerie totalAmountValueSerie = new ChartValueSerie();
        ChartValueSerie aditionalCostValueSerie = new ChartValueSerie();

        totalAmountValueSerie.setBarStyle(ContextCompat.getColor(getContext(), R.color.blue_chart_bottom_color), ContextCompat.getColor(getContext(), R.color.blue_chart_top_color), 20);
        aditionalCostValueSerie.setBarStyle(ContextCompat.getColor(getContext(), R.color.purple_chart_color), ContextCompat.getColor(getContext(), R.color.purple_chart_color), 20);

        totalAmountValueSerie.setBarStyle(ContextCompat.getColor(getContext(), R.color.blue_chart_bottom_color), ContextCompat.getColor(getContext(), R.color.blue_chart_top_color),20);
        aditionalCostValueSerie.setBarStyle(ContextCompat.getColor(getContext(), R.color.purple_chart_color), ContextCompat.getColor(getContext(), R.color.purple_chart_color),20);

        for (BillHistoryDetails bill : billHistoryList) {
            String month = getStringMonth(bill.getBillClosedDate());
            aditionalCostValueSerie.addPoint(new ChartValue(month, toFloat(bill.getGsmUtilizedServices())));
            totalAmountValueSerie.addPoint(new ChartValue(month, toFloat(bill.getTotalMonthlyFee())));
        }
        setupChart();
        billingStackedBarChart.addSerie(aditionalCostValueSerie);
        billingStackedBarChart.addSerie(totalAmountValueSerie);
    }

    @Nullable
    private String getStringMonth(Long date) {
        String month = DateUtils.getDate(date, new SimpleDateFormat("MMM", new Locale("RO", "RO")));
        month = WordUtils.capitalize(month);
        if (month != null && month.contains(".")) {
            month = month.substring(0, month.indexOf("."));
        }
        return month;
    }

    private void setupChart() {
        billingStackedBarChart = (StackedBarChartView) findViewById(R.id.chart_view);
        billingStackedBarChart.setGridVis(false, true, true);
        billingStackedBarChart.setGridColor(ContextCompat.getColor(getContext(), R.color.chart_grid_color), ContextCompat.getColor(getContext(), R.color.chart_grid_color), ContextCompat.getColor(getContext(), R.color.chart_grid_color));
        billingStackedBarChart.setGridWidthDip(0, 1, 1);
        billingStackedBarChart.setTextStyle(ContextCompat.getColor(getContext(), R.color.light_gray_text_color), 12, 16);
    }

    private void setTotalPricePlanCost(Double averageAditionalCost) {
        pricePlanCost.setText(NumbersUtils.twoDigitsAfterDecimal(averageAditionalCost) + " " + RON_UNIT);
    }

    private void setAverageAditionalCost(Double averageAditionalCost) {
        if (averageAditionalCost > 0) {
            aditionalCostAverage.setText(NumbersUtils.twoDigitsAfterDecimal(averageAditionalCost) + " " + RON_UNIT);
        } else {
            aditionalCostAverage.setText("0.00" + " " + RON_UNIT);
        }
    }

    public BillingGraphCard showError(String errorMessage) {
        super.showError(true);
        if (errorMessage != null) {
            errorView.setText(errorMessage);
        }
        return this;
    }

    public BillingGraphCard showLoading() {
        super.showLoading(true);
        return this;
    }

    public void hideLoading() {
        super.hideLoading();
        if (errorView != null && errorView.getVisibility() == VISIBLE) {
            errorView.setVisibility(GONE);
        }
    }

    public BillingGraphCard hideCard() {
        Log.d(TAG, "hideCard()");
        this.setVisibility(GONE);
        return this;
    }

    private Float toFloat(Double value) throws NullPointerException {
        return value == null ? 0 : new Float(value);
    }
}
