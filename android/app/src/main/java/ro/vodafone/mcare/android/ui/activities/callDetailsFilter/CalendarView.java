package ro.vodafone.mcare.android.ui.activities.callDetailsFilter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.billDates.RealmBillDates;
import ro.vodafone.mcare.android.ui.activities.CallDetailsActivity;
import ro.vodafone.mcare.android.utils.RealmManager;


/**
 * Created by Alex on 28/06/2015.
 */
public class CalendarView extends LinearLayout
{
    public static String TAG = CalendarView.class.getSimpleName();

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();
    private Calendar maxEndDate = Calendar.getInstance();
    private Calendar minEndDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    private Date billCycleDay;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    boolean firstElementSelected = false;
    Date fromDate;
    Date toDate;

    String[] monthsArray;
    Date selectedDateFrom;
    Date selectedDateTo;

    private long lastEndDateSelectedTime = 0;

    public CalendarView(Context context)
    {
        super(context);
        Log.d(TAG,"Constructor with context param");
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        Log.d(TAG,"Constructor with context and attrs params");
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        Log.d(TAG,"Constructor with context , attrs , defStyleAttr  params");
    }

    /**
     * Load control xml layout
     */
    public void initControl(Context context, Date billCycleDate, Date selectedToDate)
    {
        this.billCycleDay = billCycleDate;
        Date today = new Date();
        selectedDateFrom = billCycleDate;
        selectedDateTo = selectedToDate;

        monthsArray = getResources().getStringArray(R.array.months_array);
        currentDate.setTime(selectedDateTo);
        maxEndDate.setTime(today);
        minEndDate.setTime(getBillClosedDate());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_calendar, this);
        //loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }


    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(maxEndDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                        maxEndDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                    return;
                }
                Log.d(TAG,"Next month Button");
                currentDate.add(Calendar.MONTH, 1);
                Log.d(TAG,"Current month changed to: "+ currentDate.getTime().getMonth());
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(minEndDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                        minEndDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                    return;
                }
                Log.d(TAG,"Previous month Button");
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });


    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells));

        // update title

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

        txtDate.setText(monthsArray[currentDate.getTime().getMonth()] + " " + dateFormat.format( currentDate.getTime() ));

    }


    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        Date date;
        TextView dateText;

        // for view inflation
        private LayoutInflater inflater;


        public CalendarAdapter(Context context, ArrayList<Date> days){
            super(context, R.layout.filter_calendar_day, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent)
        {
            // day in question

            date = getItem(position);
            int day,month = 0,year;
            if(date!=null){
                day = date.getDate();
                month = date.getMonth();
                year = date.getYear();

            }

            // today
            Date today = new Date();
            CallDetailsActivity callDetailsActivity = (CallDetailsActivity) VodafoneController.findActivity(CallDetailsActivity.class);
            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.filter_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            dateText = ( (TextView)view);
            boolean isClickable = false;
            dateText.setTextColor(getResources().getColor(R.color.gray_button_color));

            // clear styling
            dateText.setTypeface(null, Typeface.NORMAL);


            Date firstDate = getBillClosedDate();

            if (month != currentDate.getTime().getMonth() )
            {
                // if this day is outside current month, grey it out
                dateText.setVisibility(GONE);
            }
            else if(date.after(firstDate)  && date.before(today)){
                dateText.setTextColor(getResources().getColor(R.color.white_text_color));
                isClickable = true;
            }

            displayBackgroundForSelectedInterval(selectedDateFrom, selectedDateTo , date);

            dateText.setText(String.valueOf(date.getDate()));


            if(isClickable) {
                dateText.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (SystemClock.elapsedRealtime() - lastEndDateSelectedTime < 1000){
                            return;
                        }

                        if(!firstElementSelected) {
                            selectedDateFrom = getItem(position);
                            firstElementSelected = true;
                            fromDate =  getItem(position);
                            selectedDateTo = null;
                            eventHandler.onStartDaySelected(fromDate);
                        }
                        else {
                            selectedDateTo =getItem(position);
                            toDate = getItem(position);
                            firstElementSelected = false;
                            //invert dates
                            if(1 == selectedDateFrom.compareTo(selectedDateTo)){
                                Date invertDate;
                                invertDate = selectedDateTo;
                                selectedDateTo = selectedDateFrom;
                                selectedDateFrom = invertDate;
                            }
                        }

                        displayBackgroundForSelectedInterval(selectedDateFrom, selectedDateTo , fromDate);
                        updateCalendar();


                        if(selectedDateTo != null ){

                            lastEndDateSelectedTime = SystemClock.elapsedRealtime();

                            if(fromDate.getTime() > toDate.getTime()) {
                                eventHandler.onDayLongPress(toDate, fromDate);
                            }else {
                                eventHandler.onDayLongPress(fromDate, toDate);
                            }
                        }
                    }
                });
            }
            return view;
        }




        public void displayBackgroundForSelectedInterval(Date selectedDateFrom, Date selectedDateTo , Date date ){
            //Log.d(TAG,"Display Bg: selectedDateFrom - " +selectedDateFrom+ " selectedDateTo: " + selectedDateTo + "  date: " + date );
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();
            Date today = new Date();

            //set default color
            dateText.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.overlay_background));

            //Set Red Bacground for selected elements
            if (date.after(selectedDateFrom)  && null != selectedDateTo && date.before(selectedDateTo)) {   // between 2 dates

                dateText.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red_button_color));
            }



            if ( day == selectedDateFrom.getDate() && month == selectedDateFrom.getMonth() && year == selectedDateFrom.getYear() && selectedDateTo != null) {
                    dateText.setBackgroundColor(getResources().getColor(R.color.red_button_color));
                    dateText.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.calendar_selected_from_rounded_corners));
            }else if( day == selectedDateFrom.getDate()  && month == selectedDateFrom.getMonth() && year == selectedDateFrom.getYear() && selectedDateTo == null){
                dateText.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.calendar_single_selected_item_rounded_corners));
            }

            if ( selectedDateTo != null && day == selectedDateTo.getDate()  && month == selectedDateTo.getMonth() && year == selectedDateTo.getYear()  ) {
                dateText.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.red_button_color));
                dateText.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.calendar_selected_to_rounded_corners));
            }
            if( (null != selectedDateTo && day == selectedDateTo.getDate() && month == selectedDateTo.getMonth()) && ( day == selectedDateFrom.getDate() && month == selectedDateFrom.getMonth()) ){
                dateText.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.calendar_single_selected_item_rounded_corners));
            }

        }


    }

    public Date getBillClosedDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        RealmBillDates billDates = (RealmBillDates) RealmManager.getRealmObject(RealmBillDates.class);
        if(billDates != null && billDates.getBillClosedDatesList()!=null && !billDates.getBillClosedDatesList().isEmpty()) {
            Log.d(TAG, billDates.getBillClosedDatesList().get(0).toString());
            return new Date(billDates.getBillClosedDatesList().get(0).getValue());
        }
        return new Date(c.getTimeInMillis());
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onStartDaySelected(Date fromDate);
        void onDayLongPress(Date fromDate, Date toDate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec - 1);
    }
}
