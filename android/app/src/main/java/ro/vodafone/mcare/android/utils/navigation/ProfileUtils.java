package ro.vodafone.mcare.android.utils.navigation;

import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by rarestanase on 06/07/2017.
 */

public class ProfileUtils {

    private final static String TAG = ProfileUtils.class.getSimpleName();

    public static long getLastBillCycleDateTimestamp() {

        Calendar calendar;
        Profile profile;

        int billCycleDate = 0;
        int currentDayOfMonth;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if (EbuMigratedIdentityController.isUserVerifiedEbuMigrated()) {
            billCycleDate = getBillCycleDateFromEntityDetails();
        } else {
            profile = (Profile) RealmManager.getRealmObject(Profile.class);
            if (profile != null && profile.getBillCycleDate() != null) {
                billCycleDate = profile.getBillCycleDate();
            }
        }

        Log.d(TAG, "billCycleDate " + billCycleDate);
        Log.d(TAG, "currentDayOfMonth " + currentDayOfMonth);

        if (billCycleDate >= currentDayOfMonth) {
            calendar.add(Calendar.MONTH, -1);
        }

        calendar.set(Calendar.DAY_OF_MONTH, billCycleDate);

        return calendar.getTime().getTime();
    }

    public static int getBillCycleDateFromEntityDetails() {
        EntityDetailsSuccess entityDetailsSuccess = (EntityDetailsSuccess) RealmManager.getRealmObject(EntityDetailsSuccess.class);
        if (entityDetailsSuccess != null) {
            return entityDetailsSuccess.getBillCycle();
        }
        return 0;
    }

    public static String getLastBillCycleDate() {

        Calendar calendar = Calendar.getInstance();

        String lastBillCycleDate = "";

        long timestamp = getLastBillCycleDateTimestamp();
        calendar.setTimeInMillis(timestamp);

        try {
            lastBillCycleDate = WordUtils.capitalize(DateUtils.getDate(String.valueOf((calendar.getTime()).getTime()), new SimpleDateFormat("dd MMMM", new Locale("RO", "RO"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "lastBillCycleDate " + lastBillCycleDate);

        return lastBillCycleDate;
    }

}
