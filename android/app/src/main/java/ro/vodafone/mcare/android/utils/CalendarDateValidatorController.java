package ro.vodafone.mcare.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Serban Radulescu on 11/27/2017.
 */

public class CalendarDateValidatorController {

    public static final String TAG = CalendarDateValidatorController.class.getSimpleName();

    private static CalendarDateValidatorController mInstance;

    private CalendarDateValidatorController(){
    }

    public static CalendarDateValidatorController getInstance() {
        if(mInstance==null){
            mInstance = new CalendarDateValidatorController();
        }
        return mInstance;
    }

    public ArrayList<Date> getSelectableDatesForPA(int maxPaymentDays, Calendar currentDate) {
        maxPaymentDays++;
        ArrayList<String> holidaysStringArray = DateUtils.getParsedHolidays();
        ArrayList<Date> holidaysDates = new ArrayList<>();
        Calendar maxPaymentDate = (Calendar) currentDate.clone();
        Calendar startPaymentDate = (Calendar) currentDate.clone();


        startPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
        maxPaymentDate.add(Calendar.DAY_OF_MONTH, maxPaymentDays);

        ArrayList<Date> selectableDaysForPa = new ArrayList<>();
        holidaysDates = getDatesFromStrings(holidaysStringArray);

        while(startPaymentDate.before(maxPaymentDate)){
            Date currentDay = startPaymentDate.getTime();

            if(isWeekend(currentDay)){
                maxPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
                startPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
            } else
            if (checkDatesEqualHolidays(currentDay, holidaysDates))  {
                maxPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
                startPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            else {
                startPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
                selectableDaysForPa.add(currentDay);
            }
        }

        return selectableDaysForPa;
    }

    private boolean checkDatesEqualHolidays(Date date, ArrayList<Date> holidays) {

        if (holidays != null && !holidays.isEmpty()) {
            for (Date dateCheck : holidays) {
                if (checkDatesEqual(date, dateCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Date> getDatesFromStrings(ArrayList<String> holidaysStringArray) {
        ArrayList<Date> holidaysDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (String dateString : holidaysStringArray) {
            try {
                holidaysDates.add(dateFormat.parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return holidaysDates;
    }

    private boolean isWeekend(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.DAY_OF_WEEK, currentDate);
        calendar.setTime(currentDate);
        return calendar.get(Calendar.DAY_OF_WEEK) == calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == calendar.SUNDAY;
    }


    private boolean checkDatesEqual(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
