package ro.vodafone.mcare.android.utils;

import android.text.format.Time;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;

import static ro.vodafone.mcare.android.ui.views.datepickers.CalendarGridAdapter.TAG;

/**
 * Created by User on 02.02.2017.
 */
public class DateUtils {

    public static Logger LOGGER = Logger.getInstance(DateUtils.class);

    public static final String getTime(Timestamp t) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String s = sdf.format(t);
        return s;
    }

    public static final String getTime(long t) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String s = sdf.format(t);
        return s;
    }


    public static String getDate(String timeStampStr, DateFormat sdf) {
        try {
            Date netDate = (new Date(Long.parseLong(timeStampStr)));

            return sdf.format(netDate);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getDate(Long timeStampStr, DateFormat sdf) {
        try {
            Date netDate = (new Date(timeStampStr));

            return sdf.format(netDate);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static final String getDay(Timestamp t) {
        String S = new SimpleDateFormat("dd MM").format(t);
        return S;

    }

    public static long dateToTimeStamp(String str_date) {

        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date date = null;
        try {
            date = (Date) formatter.parse(str_date);
        } catch (Exception e) {

        }
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000;

        return timestamp;
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(cal.getTime());
    }

    public static String getDateFromUnix(Long unix_date) {
        Date date = new Date(unix_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static ArrayList<String> getParsedHolidays() {

        String holidays = AppConfiguration.getHolidaysForPaymentAgreement();
        Log.i(TAG,"holidays de pe server: " + holidays);

        ArrayList<String> getHolidaysList = new ArrayList<String>();

        String splitHolidays[] = holidays.split("/");

        for (int i = 0; i < splitHolidays.length; i++) {
            getHolidaysList.add(splitHolidays[i]);
            Log.i("Holidays","Holidays: " + getHolidaysList.get(i));
        }

        return getHolidaysList;
    }

    public static Date fromStringToDate(String dtStart, SimpleDateFormat format){
        Date date = null;
        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static Date toDate(Calendar calendar) {
        return calendar.getTime();
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date getFirstDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static boolean isToday(long when, long serverSysDate) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(serverSysDate);
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static  boolean isTommorow(long date, Long serverSysDate){
        Calendar voucherExpirationDate = Calendar.getInstance();
        voucherExpirationDate.setTimeInMillis(date);
        Calendar tommorow = Calendar.getInstance();
        tommorow.setTimeInMillis(serverSysDate);
        tommorow.add(Calendar.DAY_OF_YEAR, 1);
        return tommorow.get(Calendar.YEAR) == voucherExpirationDate.get(Calendar.YEAR)
                && tommorow.get(Calendar.DAY_OF_YEAR)==voucherExpirationDate.get(Calendar.DAY_OF_YEAR);
    }

    public static  boolean isInTwoDays(long date, Long serverSysDate){
        Calendar voucherExpirationDate = Calendar.getInstance();
        voucherExpirationDate.setTimeInMillis(date);
        Calendar inTwoDays = Calendar.getInstance();
        inTwoDays.setTimeInMillis(serverSysDate);
        inTwoDays.add(Calendar.DAY_OF_YEAR, 2);
        return inTwoDays.get(Calendar.YEAR) == voucherExpirationDate.get(Calendar.YEAR)
                && inTwoDays.get(Calendar.DAY_OF_YEAR)==voucherExpirationDate.get(Calendar.DAY_OF_YEAR);
    }

    public static String translateDateStringToRo(String dateEn, String format)
    {
        DateFormat formatterEn = new SimpleDateFormat(format, Locale.ENGLISH);
        DateFormat formatterRo = new SimpleDateFormat(format, new Locale("ro"));
        String translatedDate = null;
        try {
            translatedDate = formatterRo.format(formatterEn.parse(dateEn));
        } catch (ParseException e) {
            translatedDate = dateEn;
        }

        return translatedDate;
    }

    public static Date timelessDate(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * compare only dates
     */
    public static boolean areDatesEqual(Date lhs, Date rhs)
    {
        if (lhs == null || rhs == null)
            return false;

        return timelessDate(lhs).equals(timelessDate(rhs));
    }
}
