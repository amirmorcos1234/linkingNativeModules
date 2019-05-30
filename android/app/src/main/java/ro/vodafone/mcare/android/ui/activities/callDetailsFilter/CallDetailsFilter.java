package ro.vodafone.mcare.android.ui.activities.callDetailsFilter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.billDates.RealmBillDates;
import ro.vodafone.mcare.android.client.model.realm.billDates.RealmLong;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFilterModel;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.RealmManager;

import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_PREPAID;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_UNBILLED;

/**
 * Created by Alex on 2/13/2017.
 */
public class CallDetailsFilter extends AppCompatActivity {

    public static CallDetailsFilter instance;
    public static String TAG = "CallDetailsFilter";
    public static String FILTER_OBJECT = "filterObject";
    public static String RESET_FLAG = "resetFlag";
    public static int RESULT_FILTER = 99;

    private CallDetailsFilterModel callDetailsFilterModel;

    private CalendarView cv;
    private Button buttonFilterTo;
    private Button buttonFilterFrom;
    private RelativeLayout callDetailsFilterDropDownContainer;
    private RelativeLayout callDetailsFilterUnbilledPostpaidContainer;
    private RelativeLayout callDetailsFilterNationalContainer;
    private RelativeLayout callDetailsFilterInternationalContainer;
    private RelativeLayout callDetailsFilterNationalAndInternationalContainer;
    private ImageView callDetailsFilterClose;
    private VodafoneButton callDetailsFilterResetFilters;
    private CheckBox callDetailsFilterRoamingCheckbox;
    private CheckBox callDetailsFilterInternationalCheckbox;
    private CheckBox callDetailsFilterNationalCheckbox;
    private CheckBox callDetailsFilterNationalAndInternationalCheckbox;

    private ScrollView callDetailsScrollView;

    private VodafoneButton callDetailsApplyFilterContainer;

    private Spinner spinner;

    private Handler h;

   // private boolean resetFlag;
    private boolean isActiveButton;

    private Date today;
    private Date filterFromSelectedDate;
    private Date filterToSelectedDate;
    private Date billClosedDate;

    private SimpleDateFormat unbilledFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat billedFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"));
    private SimpleDateFormat prepaidFormat = new SimpleDateFormat("MMMM yyyy", new Locale("RO", "RO"));

    private List<String> monthsList;
    private HashMap<String,Calendar> spinnerMap = new HashMap<String, Calendar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        instance = this;

        setContentView(R.layout.call_details_filter);

        initViewElements();
        setCheckboxesClickListeners();
        initHandler();
        getDataFromIntent();
        initView();
    }

    private void initViewElements(){
        cv = ((CalendarView)findViewById(R.id.calendar_view));
        callDetailsFilterClose = (ImageView) findViewById(R.id.call_details_filter_close);
        spinner = (Spinner) findViewById(R.id.call_details_month_filter_spinner);

        callDetailsFilterNationalAndInternationalCheckbox = (CheckBox) findViewById(R.id.call_details_filter_national_and_international_checkbox);
        callDetailsFilterRoamingCheckbox = (CheckBox) findViewById(R.id.call_details_filter_roaming_checkbox);
        callDetailsFilterInternationalCheckbox = (CheckBox) findViewById(R.id.call_details_filter_international_checkbox);
        callDetailsFilterNationalCheckbox = (CheckBox) findViewById(R.id.call_details_filter_national_checkbox);

        callDetailsFilterNationalAndInternationalContainer = (RelativeLayout) findViewById(R.id.call_details_filter_national_and_international_container);
        callDetailsFilterNationalContainer = (RelativeLayout) findViewById(R.id.call_details_filter_national_container);
        callDetailsFilterInternationalContainer = (RelativeLayout) findViewById(R.id.call_details_filter_international_container);

        callDetailsFilterDropDownContainer = (RelativeLayout) findViewById(R.id.call_details_filter_drop_down_container);
        callDetailsFilterUnbilledPostpaidContainer = (RelativeLayout) findViewById(R.id.call_details_filter_unbilled_postpaid_container);

        callDetailsScrollView = (ScrollView) findViewById(R.id.call_details_scroll_view);

        callDetailsFilterResetFilters = (VodafoneButton) findViewById(R.id.call_details_filter_reset_filters);
        buttonFilterFrom = (Button) findViewById(R.id.call_details_filter_button_from);
        buttonFilterTo = (Button) findViewById(R.id.call_details_filter_button_to);

        callDetailsApplyFilterContainer = (VodafoneButton) findViewById(R.id.call_details_apply_filter_container);

        buttonFilterFrom.setOnClickListener(showCalendarListener);
        buttonFilterTo.setOnClickListener(showCalendarListener);
        callDetailsFilterClose.setOnClickListener(closeFilter);
        callDetailsFilterResetFilters.setOnClickListener(resetFilters);

        callDetailsApplyFilterContainer.setOnClickListener(applyFilter);
    }

    private void setCheckboxesClickListeners(){

        callDetailsFilterNationalAndInternationalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if(checkCheckBoxes()){
                    activateFilterButton();
                }else{
                    inactivateFilterButton();
                }
            }
        });

        callDetailsFilterRoamingCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if(checkCheckBoxes()){
                    activateFilterButton();
                }else{
                    inactivateFilterButton();
                }
            }
        });

        callDetailsFilterInternationalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(checkCheckBoxes()){
                    activateFilterButton();
                }else{
                    inactivateFilterButton();
                }
            }
        });

        callDetailsFilterNationalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(checkCheckBoxes()){
                    activateFilterButton();
                }else{
                    inactivateFilterButton();
                }
            }
        });
    }

    private void initHandler(){
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {

                cv.setVisibility(View.GONE);

                if(checkCheckBoxes()) {
                    activateFilterButton();
                }
            };
        };
    }

    private void getDataFromIntent(){
        Intent i = getIntent();
        callDetailsFilterModel = (CallDetailsFilterModel) i.getSerializableExtra(FILTER_OBJECT);
    }

    public void initView(){
        Log.d(TAG, "initView");

        Log.d(TAG, "international - " +callDetailsFilterModel.isInternational());
        Log.d(TAG, "national - " +callDetailsFilterModel.isNational());


        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID)){
            if(callDetailsFilterModel.isInternational() && callDetailsFilterModel.isInternational()){
                callDetailsFilterNationalAndInternationalCheckbox.setChecked(true);
            }else{
                callDetailsFilterNationalAndInternationalCheckbox.setChecked(false);
            }
        }else{
            callDetailsFilterInternationalCheckbox.setChecked(callDetailsFilterModel.isInternational());
            callDetailsFilterNationalCheckbox.setChecked(callDetailsFilterModel.isNational());
        }

        callDetailsFilterRoamingCheckbox.setChecked(callDetailsFilterModel.isRoaming());

        today = new Date(callDetailsFilterModel.getEndDate());

        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID)){
            callDetailsFilterNationalAndInternationalContainer.setVisibility(View.VISIBLE);
            callDetailsFilterNationalContainer.setVisibility(View.GONE);
            callDetailsFilterInternationalContainer.setVisibility(View.GONE);
        }else{
            if(callDetailsFilterModel.getCategory() != null && callDetailsFilterModel.getCategory().getName().equals("DATE")){
                callDetailsFilterInternationalContainer.setVisibility(View.GONE);
            }else{
                callDetailsFilterInternationalContainer.setVisibility(View.VISIBLE);
            }
        }

        if(callDetailsFilterModel.getReportType() != null && (callDetailsFilterModel.getReportType().equals(REPORT_TYPE_UNBILLED))){
            initCalendar();
        }else{
            callDetailsFilterUnbilledPostpaidContainer.setVisibility(View.GONE);
            callDetailsFilterDropDownContainer.setVisibility(View.VISIBLE);
            setupSpinner();
        }
        setupLabels();

        if (checkCheckBoxes()) {
            activateFilterButton();
        } else {
            inactivateFilterButton();
        }

    }

    private void setupLabels(){

        ((VodafoneButton)findViewById(R.id.call_details_filter_reset_filters)).setText(CallDetailsLabels.getCall_details_reset_filters());
        ((TextView)findViewById(R.id.call_details_filter_destination_label)).setText(CallDetailsLabels.getCall_details_filter_destination_label());
        callDetailsFilterNationalCheckbox.setText(CallDetailsLabels.getCall_details_filter_national_checkbox_label());
        callDetailsFilterNationalAndInternationalCheckbox.setText(CallDetailsLabels.getCall_details_filter_national_And_International_checkbox_label());
        callDetailsFilterInternationalCheckbox.setText(CallDetailsLabels.getCall_details_filter_international_checkbox_label());
        callDetailsFilterRoamingCheckbox.setText(CallDetailsLabels.getCall_details_filter_roaming_checkbox_label());
        ((TextView)findViewById(R.id.call_details_filter_period_label)).setText(CallDetailsLabels.getCall_details_filter_period_label());
        ((TextView)findViewById(R.id.call_details_filter_spinner_label)).setText(CallDetailsLabels.getCall_details_filter_spinner_label());
        callDetailsApplyFilterContainer.setText(CallDetailsLabels.getCall_details_apply_filter_button_label());

    }

    public void initCalendar(){
        Log.d(TAG,"initCalendar()");

        billClosedDate = new Date(callDetailsFilterModel.getStartDate());
        filterFromSelectedDate = billClosedDate;  //default initialization
        filterToSelectedDate = today;  //default initialization
        Log.d(TAG,"Default initialisation filterToSelectedDate : " + filterToSelectedDate);
        buttonFilterFrom.setText(unbilledFormat.format( filterFromSelectedDate )+"");
        buttonFilterTo.setText(unbilledFormat.format( filterToSelectedDate )+"");

        cv.initControl(getBaseContext(), filterFromSelectedDate, filterToSelectedDate);

        // assign event handler
        cv.setEventHandler(new CalendarView.EventHandler()
        {
            @Override
            public void onStartDaySelected(Date fromDate) {
                buttonFilterFrom.setText(unbilledFormat.format( fromDate ) + "");
                buttonFilterTo.setText("Alege perioada");
                inactivateFilterButton();
            }

            @Override
            public void onDayLongPress(Date fromDate, Date toDate)
            {

                Log.d(TAG, "fromDate = " +fromDate);
                Log.d(TAG, "toDate = " +toDate);

                filterFromSelectedDate = fromDate;
                filterToSelectedDate = toDate;
                // show returned day
                buttonFilterFrom.setText(unbilledFormat.format( fromDate ) + "");
                buttonFilterTo.setText(unbilledFormat.format( toDate ) + "");
                checkCheckBoxes();

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            h.sendEmptyMessage(0);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        });
    }

    public void setupSpinner(){

        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID)){
            setLastMonthsList();
        }else{
            setbillsClosedDateList();
        }
        initSpinner();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.call_details_filter_spinner, monthsList);
        adapter.setDropDownViewResource(R.layout.call_details_filter_item);

        spinner.setAdapter(adapter);

        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID)){
            Log.d(TAG, " selected month" + monthsList.indexOf(DateUtils.getDate(String.valueOf(callDetailsFilterModel.getEndDate()), prepaidFormat)));
            Log.d(TAG, " selected month" + DateUtils.getDate(String.valueOf(callDetailsFilterModel.getEndDate()), prepaidFormat));
            spinner.setSelection(monthsList.indexOf(WordUtils.capitalize(DateUtils.getDate(String.valueOf(callDetailsFilterModel.getEndDate()), prepaidFormat))));

        }else{
            Log.d(TAG, " selected month" + callDetailsFilterModel.getLastBillClosedDate());
            spinner.setSelection(monthsList.indexOf(WordUtils.capitalize(DateUtils.getDate(String.valueOf(callDetailsFilterModel.getLastBillClosedDate()), billedFormat))));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Calendar selectedCalendar = spinnerMap.get(monthsList.get(position));

                if(callDetailsFilterModel.getReportType().equals("PREPAID")){
                    filterFromSelectedDate = getFirstDayOfMonth(selectedCalendar);
                    filterToSelectedDate = getLastDayOfMonth(selectedCalendar);
                }else{
                    billClosedDate = selectedCalendar.getTime();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void initSpinner(){
        Log.d(TAG, "setupSpinner()");
        float scale = getResources().getDisplayMetrics().density;
        int offsetInDP = 45;
        int offset = (int) (offsetInDP *scale + 0.5f);

        spinner.setDropDownVerticalOffset(spinner.getHeight() + offset);
        spinner.setFocusable(false);
    }

    private void setbillsClosedDateList(){
        Log.d(TAG, "setBillsDatesList()");

        RealmBillDates billDates = (RealmBillDates) RealmManager.getRealmObject(RealmBillDates.class);
        monthsList = new ArrayList<>();

        if(billDates != null){
            List<RealmLong> billClosedDatesList = billDates.getBillClosedDatesList();

            if(billClosedDatesList != null || !billClosedDatesList.isEmpty()){

                for(int i = 0; i < billClosedDatesList.size(); i++){

                    Calendar calendar = DateUtils.toCalendar(new Date(billClosedDatesList.get(i).getValue()));

                    spinnerMap.put(WordUtils.capitalize(DateUtils.getDate(String.valueOf(calendar.getTimeInMillis()), billedFormat)), calendar);
                    monthsList.add(WordUtils.capitalize(DateUtils.getDate(String.valueOf(calendar.getTimeInMillis()), billedFormat)));
                }
            }
        }
    }

    private void setLastMonthsList(){
        monthsList = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            Calendar calendar = DateUtils.toCalendar(new Date());

            calendar.add(Calendar.MONTH, -i);

            spinnerMap.put(WordUtils.capitalize(DateUtils.getDate(String.valueOf((calendar.getTime()).getTime()), prepaidFormat)), calendar);
            monthsList.add(WordUtils.capitalize(DateUtils.getDate(String.valueOf((calendar.getTime()).getTime()), prepaidFormat)));
        }
    }

    private Date getFirstDayOfMonth(Calendar calendar){
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayOfMonth(Calendar calendar){
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    View.OnClickListener showCalendarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cv.setVisibility(View.VISIBLE);
            callDetailsScrollView.fullScroll(View.FOCUS_DOWN);
        }
    };

    View.OnClickListener closeFilter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           onBackPressed();
        }
    };


    View.OnClickListener resetFilters = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();

            intent.putExtra(RESET_FLAG, true);
            setResult(RESULT_FILTER, intent);
            //initView();
            finish();
        }
    };

    View.OnClickListener applyFilter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isActiveButton) {
                Log.d(TAG, "applyFilter clicked");
                callDetailsService();
            }
        }
    };

    public void callDetailsService(){
        Log.d(TAG, "callDetailsService()");

        Intent intent = new Intent();

        CallDetailsFilterModel responseFilterModel = new CallDetailsFilterModel();

        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID)){
            responseFilterModel.setNational(callDetailsFilterNationalAndInternationalCheckbox.isChecked());
            responseFilterModel.setInternational(callDetailsFilterNationalAndInternationalCheckbox.isChecked());
        }else{
            responseFilterModel.setNational(callDetailsFilterNationalCheckbox.isChecked());
            responseFilterModel.setInternational(callDetailsFilterInternationalCheckbox.isChecked());
        }
        responseFilterModel.setRoaming(callDetailsFilterRoamingCheckbox.isChecked());

        if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID) || callDetailsFilterModel.getReportType().equals(REPORT_TYPE_UNBILLED)){
            responseFilterModel.setStartDate(filterFromSelectedDate.getTime());
            responseFilterModel.setEndDate(filterToSelectedDate.getTime());
        }else{
            responseFilterModel.setLastBillClosedDate(billClosedDate.getTime());
        }

        intent.putExtra(FILTER_OBJECT, responseFilterModel);
        setResult(RESULT_FILTER, intent);
        finish();
    }

    public void activateFilterButton() {
        isActiveButton = true;
        callDetailsApplyFilterContainer.setEnabled(true);
    }


    public void inactivateFilterButton() {
        isActiveButton = false;
        callDetailsApplyFilterContainer.setEnabled(false);
    }

    public boolean checkCheckBoxes(){

        boolean result = false, INResult = false, IResult = false, NResult = false, RResult = false;
        String toButtonText = buttonFilterTo.getText().toString();


        if(callDetailsFilterNationalAndInternationalContainer.getVisibility() == View.VISIBLE && callDetailsFilterNationalAndInternationalCheckbox.isChecked()){
            INResult = true;
        }

        if(callDetailsFilterInternationalContainer.getVisibility() == View.VISIBLE && callDetailsFilterInternationalCheckbox.isChecked()){
            IResult = true;
        }

        if(callDetailsFilterNationalContainer.getVisibility() == View.VISIBLE && callDetailsFilterNationalCheckbox.isChecked()){
            NResult = true;
        }

        if(callDetailsFilterRoamingCheckbox.isChecked()){
            RResult = true;
        }

        if(INResult || IResult || NResult || RResult){
            result = true;
        }


        boolean periodResult = true;
        if(toButtonText.contains("Alege perioada")){
            periodResult = false;
        }

        Log.d(TAG, "result");
        return (result && periodResult);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}


