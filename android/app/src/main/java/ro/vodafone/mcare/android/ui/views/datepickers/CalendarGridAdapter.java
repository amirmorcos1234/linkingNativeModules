package ro.vodafone.mcare.android.ui.views.datepickers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
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
 * Created by User on 30.03.2017.
 */

public class CalendarGridAdapter extends ArrayAdapter<Date> {
    public static String TAG = CalendarGridAdapter.class.getCanonicalName();
    int previous = 0;
    private boolean isClickable = false;
    private Calendar todayCalendar;
    private Date date;
    private Date previousSelectedDate;
    private Callback callback;
    private ArrayList<Date> selectableDates;

    private RelativeLayout calendarItem;
    private Date selectedDate;
    private Date todayDate;
    private Calendar monthDate;
    private TextView dateText;
    private LayoutInflater inflater;
    private TextView previousClicked;
    private int maxPaymentDays;


    public CalendarGridAdapter(Context context, ArrayList<Date> dates, Calendar currentDate) {
        super(context, R.layout.calendar_item, dates);
        inflater = LayoutInflater.from(context);
        this.todayCalendar = currentDate;
        maxPaymentDays = -1;
    }

    public CalendarGridAdapter(Context context, ArrayList<Date> dates, Calendar currentDate, int maxPaymentDays, Date selectedDate, Calendar monthDate) {
        super(context, R.layout.calendar_item, dates);
        inflater = LayoutInflater.from(context);

        this.todayCalendar = (Calendar) currentDate.clone();

        this.maxPaymentDays = maxPaymentDays;
        this.monthDate = monthDate;


        todayDate = todayCalendar.getTime();

        if (selectedDate != null) {
            this.selectedDate = selectedDate;
        }
    }

    public CalendarGridAdapter(Context context, ArrayList<Date> dates, Calendar currentDate, int maxPaymentDays, Date selectedDate, Calendar monthDate, ArrayList<Date> selectableDates) {
        super(context, R.layout.calendar_item, dates);
        inflater = LayoutInflater.from(context);

        this.selectableDates = selectableDates;

        this.todayCalendar = (Calendar) currentDate.clone();

        this.maxPaymentDays = maxPaymentDays;
        this.monthDate = monthDate;


        todayDate = todayCalendar.getTime();

        if (selectedDate != null) {
            this.selectedDate = selectedDate;
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // day in question
        date = getItem(position);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();

        // inflate item if it does not exist yet
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_item, null);
        }
        dateText = ((TextView) convertView.findViewById(R.id.calendar_item_text));

        calendarItem = (RelativeLayout) convertView.findViewById(R.id.calendar_item);

        dateText.setTypeface(null, Typeface.NORMAL);

        if (maxPaymentDays > 0) {
            if (month != monthDate.getTime().getMonth()) {
                calendarItem.setVisibility(View.GONE);
            }
        } else {
            if (month != todayCalendar.getTime().getMonth()) {
                calendarItem.setVisibility(View.GONE);
            }
        }

        if (maxPaymentDays > 0) {

            if (checkDatesEqual(date, todayDate)) {
                isClickable = false;
                dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.blackNormal));
                dateText.setBackgroundResource(R.drawable.gray_calendar_circle);
            } else
            if (checkDateIsSelectable(date, selectableDates)) {
                isClickable = true;
                dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.blackNormal));
                calendarItem.setBackgroundResource(0);
            } else {
                isClickable = false;
                dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_input_text));
                calendarItem.setBackgroundResource(0);
            }

            if (selectedDate != null)
                if (checkDatesEqual(date, selectedDate)) {
                    dateText.setTextColor(getContext().getResources().getColor(R.color.white_text_color));
                    dateText.setBackgroundResource(R.drawable.red_circle);
                    isClickable = true;

                    setSelectedElement(selectedDate);
                    previousClicked = dateText;
                    previousSelectedDate = selectedDate;
                }
        } else {
            final Date today = new Date();
            //setSelectedElement(today);
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO RO"));
            try {
                Date formatedDate = format.parse(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO RO")).format(today));
                if (date.after(formatedDate)) {
                    isClickable = true;
                    dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.blackNormal));
                    calendarItem.setBackgroundResource(0);
                } else if (date.before(formatedDate)) {
                    isClickable = false;
                    dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_input_text));
                    calendarItem.setBackgroundResource(0);
                } else {
                    isClickable = true;
                    dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
                    dateText.setBackgroundResource(R.drawable.red_circle);
                    previousClicked = dateText;
                    previousSelectedDate = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        dateText.setText(String.valueOf(date.getDate()));


        if (isClickable) {
            dateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dateText = (TextView) v;
                    dateText.setSelected(true);
                    selectedDate = getItem(position);
                    dateText.setTextColor(getContext().getResources().getColor(R.color.white_text_color));
                    dateText.setBackgroundResource(R.drawable.red_circle);
                    Log.d("", "onClick: " + selectedDate);

                    if (previousClicked != null) {

                        if (!checkDatesEqual(selectedDate, previousSelectedDate)) {
                            previousClicked.setBackgroundResource(0);
                            previousClicked.setTextColor(getContext().getResources().getColor(R.color.blackNormal));
                            if (maxPaymentDays > 0) {
                                if (checkDatesEqual(previousSelectedDate, todayDate)) {
                                    previousClicked.setBackgroundResource(R.drawable.gray_calendar_circle);
                                }
                            }

                        }

                    }
                    setSelectedElement(selectedDate);
                    previousClicked = dateText;
                    previousSelectedDate = selectedDate;

                }

            });
        }


        return convertView;
    }

    private boolean checkDateIsSelectable(Date date, ArrayList<Date> dateArrayList) {
        for (Date dateFromArray: dateArrayList) {
            if(checkDatesEqual(date, dateFromArray)){
                return true;
            }
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

    private void setSelectedElement(Date date) {

        if (callback != null) {
            callback.selectedCalendarDate(date);
        }
    }

    public interface Callback {
        public void selectedCalendarDate(Date date);
    }

}
