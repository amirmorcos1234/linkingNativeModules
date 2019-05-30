package ro.vodafone.mcare.android.ui.views.datepickers;

import android.content.Context;
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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ro.vodafone.mcare.android.R;

/**
 * Created by User on 20.03.2017.
 */

public class CalendarPickerView extends LinearLayout implements CalendarGridAdapter.Callback {
    public static String TAG = "CalendarPickerView";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    Context mContext = getContext();

    private TextView calendarHeader;
    private GridView grid;

    private String[] monthsArray;
    private ImageView btnPrev;
    private ImageView btnNext;

    private Date date;
    private Callback callback;


    private Calendar currentDate = Calendar.getInstance(new Locale("RO RO"));


    public CalendarPickerView(Context context) {
        super(context);
        this.mContext = context;
        initView(null);
    }

    public CalendarPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(attrs);
    }

    public CalendarPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        initView(attrs);
    }

    public void initView(AttributeSet attrs) {
        Log.d(TAG, "initView: ");
        Log.d(TAG, "initView: currentDate " + currentDate);
        selectedCalendarDate(currentDate.getTime());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this);

        calendarHeader = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);
        monthsArray = getResources().getStringArray(R.array.months_array);

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
        Log.d(TAG, "assignUiListeners: ");
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Next month Button");
                currentDate.add(Calendar.MONTH, 1);
                Log.d(TAG, "Current month changed to: " + currentDate.getTime().getMonth());
                updateCalendarGrid();
            }
        });

        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Previous month Button");
                currentDate.add(Calendar.MONTH, -1);
                updateCalendarGrid();
            }
        });
    }


    private void updateCalendarGrid() {
        Log.d(TAG, "updateCalendarGrid: ");
        ArrayList<Date> cells = new ArrayList<>();
        final Calendar calendar = (Calendar) currentDate.clone();

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
        CalendarGridAdapter adapter = new CalendarGridAdapter(getContext(), cells, currentDate);
        adapter.setCallback(this);
        grid.setAdapter(adapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        calendarHeader.setText(monthsArray[currentDate.getTime().getMonth()] + " " + dateFormat.format(currentDate.getTime()));
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
