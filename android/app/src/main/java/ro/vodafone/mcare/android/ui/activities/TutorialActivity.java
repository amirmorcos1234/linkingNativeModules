package ro.vodafone.mcare.android.ui.activities;

/**
 * @author Andrei DOLTU
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.utils.ViewPagerAdapterTutorial;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Andrei DOLTU on 2/09/2017.
 */
public class TutorialActivity extends BaseActivity {
    private static final String TAG = "TutorialActivity";
    ViewPager viewPager;
    PagerAdapter adapter;
    int[] pageOneA;
    int[] pageOneB;
    int[] pageTwo;
    int[] pageThree;
    int[] pageFour;
    int[] pageFive;
    int[] pageSix;
    int[] skipButton;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        pageOneA = new int[] {R.drawable.tutorial_step1, 0, 0, 0, 0, 0};
//		pageOneB = new int[] {R.drawable.tutorial_screen_1_b, 0, 0, 0, 0, 0};
        pageTwo = new int[] {0, R.drawable.tutorial_step2, 0, 0, 0, 0};
        pageThree = new int[] {0, 0, R.drawable.tutorial_step3, 0, 0, 0};
        try{
            userProfile = VodafoneController.getInstance().getUser().getUserProfile();
            userProfile.getCustomerType();
            userProfile.getUserName();
            Log.d(TAG, "Tutorial getUserName + getCustomerType = " +
                    String.valueOf(userProfile.getUserName()) + " " +
                    String.valueOf(userProfile.getCustomerType()));
        }catch (Exception e){
            e.printStackTrace();
        }

        if(userProfile!=null && !(userProfile.getCustomerType() == null) && userProfile.getCustomerType().contentEquals("postpaid")){
            //setting user postapid steps 4.5.6 pictures
            pageFour = new int[] {0, 0, 0, R.drawable.tutorial_step4_postpaid, 0, 0};
            pageFive = new int[] {0, 0, 0, 0, R.drawable.tutorial_step5_postpaid, 0};
            pageSix = new int[] {0, 0, 0, 0, 0, R.drawable.tutorial_step6_postpaid};
        }else{
            //setting user prepaid steps 4.5.6 pictures
            pageFour = new int[] {0, 0, 0, R.drawable.tutorial_step4_prepaid, 0, 0};
            pageFive = new int[] {0, 0, 0, 0, R.drawable.tutorial_step5_prepaid, 0};
            pageSix = new int[] {0, 0, 0, 0, 0, R.drawable.tutorial_step6_prepaid};

        }

        skipButton = new int[] { R.drawable.blackborder_blacktext_whitebutton,
                R.drawable.blackborder_blacktext_whitebutton, R.drawable.blackborder_blacktext_whitebutton,
                R.drawable.blackborder_blacktext_whitebutton, R.drawable.blackborder_blacktext_whitebutton, 0 };
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager_tutorial);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapterTutorial(TutorialActivity.this,
                pageOneA, pageOneB, pageTwo, pageThree, pageFour,
                pageFive, pageSix, skipButton);
        trackWithTelium();
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }
    private void trackWithTelium(){
        //Tealium Track View
        try {
            Map<String, Object> tealiumMapView = new HashMap(6);
            tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.tutorial);
            tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.tutorial);
            if(VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

            TutorialTrackingEvent event = new TutorialTrackingEvent();
            VodafoneController.getInstance().getTrackingService().track(event);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void finishTutorial(View v){
        if(viewPager.getCurrentItem() == 5) {
            SharedPreferences pref = getSharedPreferences("TutorialFirstRun",  Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("tutorial_first_run", false);
            editor.apply();
            VodafoneController.getInstance().getAppConfiguration().setTutorialFlagTrue();
            setIntentForBackToDashboard();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        setIntentForBackToDashboard();
        super.onBackPressed();
    }

    private void setIntentForBackToDashboard() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

    public void nextPageTutorial(View view) {
        int x =  viewPager.getCurrentItem();
        if(x < 5){
            viewPager.setCurrentItem(x + 1);
        }
    }

    public static class TutorialTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "tutorial";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"tutorial");


            s.channel = "tutorial";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}