package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.Locale;

import ro.vodafone.mcare.android.R;

/**
 * Created by User on 22.04.2017.
 */

public class LoyaltyPointsFilter extends LinearLayout {
    Date selectedDayTo;
    Date selectedDayFrom;
    String[] monthsArray;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private String TAG = "LoyaltyPointsFilter";
    EventHandler eventHandler;

    Date fromDate;
    Date toDate;
    boolean firstElementSelected = false;
    private Date lastBillCycle;
    private Date lastBillCycleDate;

    private Calendar currentDate = Calendar.getInstance(Locale.getDefault());
    private static final int DAYS_COUNT = 42;


    public LoyaltyPointsFilter(Context context) {
        super(context);
    }

    public LoyaltyPointsFilter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoyaltyPointsFilter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LoyaltyPointsFilter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initControl(Context context, Date billCycleDay, Date dateMinusMonth, Date lastBillCycleDate) {
        this.lastBillCycleDate = lastBillCycleDate;
        lastBillCycle = billCycleDay;
        this.selectedDayTo = billCycleDay;
        selectedDayFrom = dateMinusMonth;
        monthsArray = getResources().getStringArray(R.array.months_array);
        if (billCycleDay != null)
            currentDate.setTime(billCycleDay);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_calendar, this);

        assignUiElements();
        assignClickHandlers();
        updateCalendar();
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
        txtDate = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Next month Button");
                Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
                currentDate.add(Calendar.MONTH, 1);

                if (!(currentDate.get(Calendar.MONTH) > todayCalendar.get(Calendar.MONTH) && currentDate.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR))) {
                    updateCalendar();
                } else {
                    currentDate.add(Calendar.MONTH, -1);

                }

            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Previous month Button");
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });
    }

    private void updateCalendar() {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        grid.setAdapter(new LoyaltyCalendarAdapter(getContext(), cells));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", new Locale("RO", "RO"));

        txtDate.setText(monthsArray[currentDate.getTime().getMonth()] + " " + dateFormat.format(currentDate.getTime()));
    }

    private class LoyaltyCalendarAdapter extends ArrayAdapter<Date> {
        private Date date;
        private LayoutInflater inflater;
        TextView dateText;

        public LoyaltyCalendarAdapter(Context context, ArrayList<Date> days) {
            super(context, R.layout.filter_calendar_day, days);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            date = getItem(position);

            int month = date.getMonth();
            Date today = new Date();
            boolean isClickable = false;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.filter_calendar_day, parent, false);
            }

            convertView.setBackgroundResource(0);
            dateText = ((TextView) convertView);
            dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_button_color));

            dateText.setTypeface(null, Typeface.NORMAL);
            if (month != currentDate.getTime().getMonth()) {
                dateText.setVisibility(GONE);
            } else if (date.after(dateMinusYear(today)) && date.before(today)) {
                dateText.setTextColor(getResources().getColor(R.color.white_text_color));
                isClickable = true;
            }

            displayBackgroundForSelectedInterval(selectedDayFrom, selectedDayTo, date);

            dateText.setText(String.valueOf(date.getDate()));

            if (isClickable) {
                dateText.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (!firstElementSelected) {
                            selectedDayFrom = getItem(position);
                            firstElementSelected = true;
                            fromDate = getItem(position);
                            selectedDayTo = null;

                        } else {
                            selectedDayTo = getItem(position);
                            toDate = getItem(position);
                            firstElementSelected = false;
                            //invert dates
                            if (1 == selectedDayFrom.compareTo(selectedDayTo)) {
                                Date invertDate;
                                invertDate = selectedDayTo;
                                selectedDayTo = selectedDayFrom;
                                selectedDayFrom = invertDate;
                            }
                        }

                        if (selectedDayFrom.equals(selectedDayTo)) {
                            selectedDayFrom = null;
                            selectedDayTo = null;
                        }

                        displayBackgroundForSelectedInterval(selectedDayFrom, selectedDayTo, fromDate);
                        updateCalendar();


                        if (selectedDayTo != null && selectedDayFrom != null) {
                            if (fromDate.getTime() > toDate.getTime()) {
                                eventHandler.onDayLongPress(toDate, fromDate);
                            } else {
                                eventHandler.onDayLongPress(fromDate, toDate);
                            }
                        } else if (selectedDayFrom != null && selectedDayTo == null) {
                            eventHandler.onDayLongPress(fromDate, null);
                        } else {
                            eventHandler.onDayLongPress(null, null);
                        }
                    }
                });
            }

            return convertView;
        }

        private void displayBackgroundForSelectedInterval(Date selectedDateFrom, Date selectedDateTo, Date date) {

            //set default color
            dateText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.overlay_background));

            if (selectedDateFrom != null) {
                int day = date.getDate();
                int month = date.getMonth();
                int year = date.getYear();
                Date today = new Date();

                //Set Red Bacground for selected elements
                if (date.after(selectedDateFrom) && null != selectedDateTo && date.before(selectedDateTo)) {   // between 2 dates
                    dateText.setBackgroundColor(getResources().getColor(R.color.red_button_color));
                }

                if (day == selectedDateFrom.getDate() && month == selectedDateFrom.getMonth() && year == selectedDateFrom.getYear() && selectedDateTo != null) {
                    dateText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_button_color));
                    dateText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_selected_from_rounded_corners));
                } else if (day == selectedDateFrom.getDate() && month == selectedDateFrom.getMonth() && year == selectedDateFrom.getYear() && selectedDateTo == null) {
                    dateText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_single_selected_item_rounded_corners));
                }

                if (selectedDateTo != null && day == selectedDateTo.getDate() && month == selectedDateTo.getMonth() && year == selectedDateTo.getYear()) {
                    dateText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_button_color));
                    dateText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_selected_to_rounded_corners));
                }
                if ((null != selectedDateTo && day == selectedDateTo.getDate() && month == selectedDateTo.getMonth()) && (day == selectedDateFrom.getDate() && month == selectedDateFrom.getMonth())) {
                    dateText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_single_selected_item_rounded_corners));
                }
            }
        }
    }


    public static Date dateMinusMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date dateMinusYear(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {
        void onDayLongPress(Date fromDate, Date toDate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec - 1);
    }
}
