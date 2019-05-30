package ro.vodafone.mcare.android.ui.views.datepickers;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.DateUtils;

/**
 * Created by user2 on 4/26/2017.
 */

public class PACalendarPickerView extends LinearLayout implements CalendarGridAdapter.Callback {
    public static String TAG = "CalendarPickerView";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    private int maxPaymentDays;
    private long currentDateServer;
    private ArrayList<Date> notSelectableDatesStringToDate;
    private ArrayList<String> notSelectableDates;
    private ArrayList<Date> selectableDates;

    Context mContext = getContext();

    private TextView calendarHeader;
    private GridView grid;
    private RelativeLayout pickerDateToolbar;

    private String[] monthsArray;
    private ImageView btnPrev;
    private ImageView btnNext;

    private Date date;
    private Callback callback;


    private Calendar currentDate = new GregorianCalendar();
    private Calendar fixedCurrentDate = new GregorianCalendar();

    public PACalendarPickerView(Context context) {
        super(context);
        this.mContext = context;
        initView(null);
    }

    public PACalendarPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(attrs);
    }

    public PACalendarPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PACalendarPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        initView(attrs);
    }

    public void initView(AttributeSet attrs) {
        Log.d(TAG, "initView: ");

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this);

        pickerDateToolbar = (RelativeLayout) findViewById(R.id.calendar_picker_date_toolbar);
        calendarHeader = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);
        monthsArray = getResources().getStringArray(R.array.months_array);

        pickerDateToolbar.setPadding(ScreenMeasure.dpToPx(3), ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(3), ScreenMeasure.dpToPx(12));
    }

    public void setMaxPaymentDaysCurrentDate(int maxPaymentDays, long currentDateServer, ArrayList<Date> selectableDates) {
        this.maxPaymentDays = maxPaymentDays;
        this.currentDateServer = currentDateServer;
        this.selectableDates = selectableDates;
        currentDate.setTimeInMillis(currentDateServer);
        fixedCurrentDate.setTimeInMillis(currentDateServer);
        Log.d(TAG, currentDate.getTime().getTime() + "");
        Log.d(TAG, fixedCurrentDate.getTime().getTime() + "");
        Log.d(TAG, fixedCurrentDate.get(Calendar.DAY_OF_MONTH) + " " + fixedCurrentDate.get(Calendar.MONTH));
        updateCalendarGrid();
        assignUiListeners();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec - 1);
    }

    private void assignUiListeners() {
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
        btnPrev.setColorFilter(Color.argb(153,153,153,153));
        btnNext.setColorFilter(Color.argb(153,153,153,153));

        Calendar calendar = (Calendar) currentDate.clone();
        int oldMonth = calendar.get(Calendar.MONTH);

        int recalculatedMaxPaymentDays = recalculateMaxDays(calendar);

        Log.d(TAG, "Payment days" + maxPaymentDays + " - " + recalculatedMaxPaymentDays);
        calendar.add(Calendar.DATE, recalculatedMaxPaymentDays);

        if(oldMonth != calendar.get(Calendar.MONTH))
        {
            setNextButton();
            btnNext.setColorFilter(Color.argb(51,51,51,51));
        }
    }

    private void setNextButton(){
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Next month Button");
                currentDate.add(Calendar.MONTH, 1);
                Log.d(TAG, "Current month changed to: " + currentDate.getTime().getMonth());
                updateCalendarGrid();

                btnNext.setOnClickListener(null);
                btnPrev.setColorFilter(Color.argb(51,51,51,51));
                btnNext.setColorFilter(Color.argb(153,153,153,153));
                setPreviousButton();
            }
        });
    }

    private void setPreviousButton(){
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Previous month Button");
                currentDate.add(Calendar.MONTH, -1);
                updateCalendarGrid();

                btnPrev.setOnClickListener(null);
                setNextButton();
                btnPrev.setColorFilter(Color.argb(153,153,153,153));
                btnNext.setColorFilter(Color.argb(51,51,51,51));

            }
        });
    }


    private void updateCalendarGrid() {
        Log.d(TAG, "updateCalendarGrid: ");
        ArrayList<Date> cells = new ArrayList<>();
        final Calendar calendar = (Calendar) currentDate.clone();
        Log.d(TAG, "updateCalendarGrid: " + calendar);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO RO"));
        while (cells.size() < DAYS_COUNT) {
            try {
                Date date = format.parse(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO RO")).format(calendar.getTime()));
                cells.add(date);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "updateCalendarGrid: " + cells);
        Log.d(TAG, fixedCurrentDate.get(Calendar.DAY_OF_MONTH) + " " + fixedCurrentDate.get(Calendar.MONTH));
        CalendarGridAdapter adapter = new CalendarGridAdapter(getContext(), cells, fixedCurrentDate, maxPaymentDays, date, currentDate, selectableDates);
        adapter.setCallback(this);
        grid.setAdapter(adapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String calendarHeaderTitle = monthsArray[currentDate.getTime().getMonth()] + " " + dateFormat.format(currentDate.getTime());
        calendarHeader.setText(calendarHeaderTitle);
    }

    private int recalculateMaxDays(Calendar calendar) {

        ArrayList<Date> skippedDays = new ArrayList<>();
        for(int i = 0 ; i < maxPaymentDays ; i++){
            // Add day to list
            skippedDays.add(calendar.getTime());
            // Move next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        notSelectableDates = DateUtils.getParsedHolidays();
        int maxDaysPlusWeekendsHolidays = maxPaymentDays;
        for (Date date: skippedDays) {
            if(checkDatesEqualHolidays(date, notSelectableDates) && !isWeekend(date)){
                maxDaysPlusWeekendsHolidays++;
            }

            if(isWeekend(date)){
                maxDaysPlusWeekendsHolidays++;
            }
        }
        return maxDaysPlusWeekendsHolidays;
    }

    private boolean checkDatesEqualHolidays(Date date, ArrayList<String> holidays) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        notSelectableDatesStringToDate = new ArrayList<Date>();

        for (String dateString : holidays) {
            try {
                notSelectableDatesStringToDate.add(dateFormat.parse(dateString));
                Log.i(TAG, ">>CheckDatesEqualHolidays>> String to date" + notSelectableDatesStringToDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (notSelectableDatesStringToDate != null && !notSelectableDatesStringToDate.isEmpty()) {
            for (Date dateCheck : notSelectableDatesStringToDate) {
                if (checkDatesEqual(date, dateCheck)) {
                    return true;
                }
            }
        }


        return false;
    }

    private boolean isWeekend(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.DAY_OF_WEEK, currentDate);
        calendar.setTime(currentDate);
        Log.i(TAG, "CALENDAR: " + calendar.get(Calendar.DAY_OF_WEEK));
        if (calendar.get(Calendar.DAY_OF_WEEK) == calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == calendar.SUNDAY) {
            return true;
        }
        return false;
    }


    private boolean checkDatesEqual(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    @Override
    public void selectedCalendarDate(Date date) {
        this.date = date;
        if (callback != null) {
            callback.selectedCalendarDate(date);
        }
    }

    public interface Callback {
        public void selectedCalendarDate(Date date);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}