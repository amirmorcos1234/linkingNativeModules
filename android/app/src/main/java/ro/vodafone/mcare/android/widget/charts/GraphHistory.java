package ro.vodafone.mcare.android.widget.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistoryMonthlyGroup;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistoryRow;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/7/2017.
 */

public class GraphHistory extends RelativeLayout{

    //Graphs default values
    public static final boolean DEF_SHOW_VALUES         = true;
    public static final float   DEF_BAR_WIDTH           = 32.f;
    public static final boolean DEF_FIXED_BAR_WIDTH     = false;
    public static final float   DEF_BAR_MARGIN          = 12.f;
    public static final boolean DEF_SCROLL_ENABLED      = true;
    public static final int     DEF_VISIBLE_BARS        = 6;

    boolean showValues;
    float barWidth;
    float barMargin;
    boolean fixedBarWidth;
    boolean scrollEnabled;
    int visibleBars;


    Context context;

    String title = "Reîncărcările tale din  ultimele 6 luni";
    int titleColor = Color.WHITE;

    boolean showTitle;

    int barChartColor = Color.argb(225,60,176,200);
    int legendColor = Color.argb(225,203,203,203);

    final String unit = "€";

    @BindView(R.id.barchart)
    BarChart barChart;

    @BindView(R.id.bottom_left_text)
    VodafoneTextView infoTextView;

    @BindView(R.id.bottom_right_text)
    VodafoneTextView costTextView;

    @BindView(R.id.chart_title)
    VodafoneTextView titleTextView;

    public GraphHistory(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public GraphHistory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttribrutes(attrs);
    }

    public GraphHistory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttribrutes(attrs);
    }

    protected void init(TypedArray attrs) {
        View view = inflate(context,R.layout.chart_bar_history,this);
        ButterKnife.bind(this,view);

        if(showTitle){
            titleTextView.setText(title);
            titleTextView.setTextColor(titleColor);
        }else{
            titleTextView.setVisibility(GONE);
        }
        setInfoTextMessage();
    }

    @CallSuper
    protected void setAttribrutes(AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.GraphHistory);
        try {
            setGraphAttributes(attrs);

            barChartColor = attributes.getColor(
                    R.styleable.GraphHistory_barChartColor,
                    ContextCompat.getColor(context, R.color.blue_chart_header));

            showTitle = attributes.getBoolean(
                    R.styleable.GraphHistory_showTitle, false);
            title = attributes.getString(R.styleable.GraphHistory_titleText);
            titleColor = attributes.getColor(
                    R.styleable.GraphHistory_titleColor,
                    ContextCompat.getColor(context, R.color.whiteNormalTextColor));
            legendColor = attributes.getColor(
                    R.styleable.GraphHistory_legendColor,
                    ContextCompat.getColor(context, R.color.grey_300));


            init(attributes);
            initGraph();

        } finally {
            attributes.recycle();
        }
    }

    private void setGraphAttributes(AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                org.eazegraph.lib.R.styleable.BaseBarChart,
                0, 0
        );

        try {

             showValues         = a.getBoolean(org.eazegraph.lib.R.styleable.BaseBarChart_egShowValues,         DEF_SHOW_VALUES);
             barWidth           = a.getDimension(org.eazegraph.lib.R.styleable.BaseBarChart_egBarWidth,         Utils.dpToPx(DEF_BAR_WIDTH));
             barMargin          = a.getDimension(org.eazegraph.lib.R.styleable.BaseBarChart_egBarMargin,        Utils.dpToPx(DEF_BAR_MARGIN));
             fixedBarWidth      = a.getBoolean(org.eazegraph.lib.R.styleable.BaseBarChart_egFixedBarWidth,      DEF_FIXED_BAR_WIDTH);
             scrollEnabled      = a.getBoolean(org.eazegraph.lib.R.styleable.BaseBarChart_egEnableScroll,       DEF_SCROLL_ENABLED);
             visibleBars        = a.getInt(org.eazegraph.lib.R.styleable.BaseBarChart_egVisibleBars,            DEF_VISIBLE_BARS);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
    }

    void initGraph(){
        barChart.setShowValues(showValues);
        barChart.setBarWidth(barWidth);
        barChart.setBarMargin(barMargin);
        barChart.setFixedBarWidth(fixedBarWidth);
        barChart.setScrollEnabled(scrollEnabled);
        barChart.setVisibleBars(visibleBars);
        barChart.setLegendColor(legendColor);

    }

    public BarChart setupChart(List<HistoryModel> historyModelList, List<RechargeHistoryMonthlyGroup> allMonthList){
        barChart.addBarList(historyModelList);
        int lastMonthIndex = historyModelList.size()-1;
        setCostTextViewValue(historyModelList,allMonthList);
        return barChart;
    }

    public void setCostTextViewValue(List<HistoryModel> list, List<RechargeHistoryMonthlyGroup> monthListGroup){
        float value = 0;
        for(RechargeHistoryMonthlyGroup model : monthListGroup){
            for(RechargeHistoryRow historyRow : model.getRechargeHistoryRow()){
                value += historyRow.getAmount();
            }
        }
        Log.d("", "setCostTextViewValue: value " + value);
        if(value != 0)
            costTextView.setText(NumbersUtils.twoDigitsAfterDecimal(value/monthListGroup.size()) + " " + unit);
    }

    public void startAnimation(){
        barChart.startAnimation();
    }

    public List<Integer> getDefaultChart(){
        Calendar today = Calendar.getInstance();

        List<Integer> monthList = new ArrayList<>();
        monthList.add(today.get(Calendar.MONTH) + 1);
        for(int i = 0; i<5; i++){
            today.add(Calendar.MONTH, -1);
            monthList.add(today.get(Calendar.MONTH) + 1);
        }

        Collections.reverse(monthList);

        return monthList;
    }

    public void drawDefaultChart(){
        RechargeHistoryMonthlyGroupToModelTranslater modelTranslater = new RechargeHistoryMonthlyGroupToModelTranslater();
        for(int i = 0; i<getDefaultChart().size(); i++){
            barChart.addBar(new HistoryModel(modelTranslater.getMonthStringRepresentation(getDefaultChart().get(i)),0,barChartColor));
        }
    }

    public void setTextColor(int color){

        infoTextView.setTextColor(color);
        costTextView.setTextColor(color);

        if(showTitle){
            titleTextView.setTextColor(color);
        }
    }
    private void setInfoTextMessage(){
        String dot = TextUtils.fromHtml("&#8226").toString();
        String infoText =" Reîncărcări pe lună";
        String finalText = dot + infoText;

        SpannableString span =new SpannableString(finalText);
        span.setSpan(new ForegroundColorSpan(barChartColor),
                finalText.indexOf(dot),
                finalText.indexOf(dot)+dot.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        infoTextView.setText(span);
    }


}
