package ro.vodafone.mcare.android.ui.activities.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.register.FirstStepRegisterFragment;
import ro.vodafone.mcare.android.ui.fragments.register.ThirdStepRegisterFragment;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

public class RegisterActivity extends BaseActivity {

    public static  String TAG = "FirstRegisterActivity";

    private String MSISDN;
    private String CUSTOMER_TYPE;
    private String SUBSCRIBER_TYPE;
    private String USERNAME;
    private String EMAIL;
    private boolean IS_MIGRATED;

    private final static String MSISDN_KEY = "MSISDN_KEY";
    private final static String CUSTOMER_TYPE_KEY = "CUSTOMER_TYPE_KEY";
    private final static String SUBSCRIBER_TYPE_KEY = "SUBSCRIBER_TYPE_KEY";
    private final static String USERNAME_KEY = "USERNAME_KEY";
    private final static String EMAIL_KEY = "EMAIL_KEY";
    private final static String IS_MIGRATED_KEY = "IS_MIGRATED_KEY";



    private ImageView backButton;

    private View.OnClickListener backButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Back button Pressed");
            onBackPressed();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        if(savedInstanceState != null) {
            setMSISDN(savedInstanceState.getString(MSISDN_KEY));
            setSUBSCRIBER_TYPE(savedInstanceState.getString(SUBSCRIBER_TYPE_KEY));
            setCUSTOMER_TYPE(savedInstanceState.getString(CUSTOMER_TYPE_KEY));
            setIS_MIGRATED(savedInstanceState.getBoolean(IS_MIGRATED_KEY));
            setUSERNAME(savedInstanceState.getString(USERNAME_KEY));
            setEMAIL(savedInstanceState.getString(EMAIL_KEY));
        }

        setContentView(R.layout.activity_register);
        setVodafoneBackgroundOnWindow();
        init();

        backButton = (ImageView) findViewById(R.id.first_step_back_button);
        backButton.setOnClickListener(backButtonListner);

    }

    void init() {
        if (FragmentUtils.getVisibleFragment(this, false) == null) {
            attachFragment(new FirstStepRegisterFragment());
        }

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.register_1st_step_screen);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.registerJourney);
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        RegisterActivityTrackingEvent event = new RegisterActivityTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void attachFragment(BaseFragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, null);
        transaction.commit();
        fragmentManager.executePendingTransactions();

    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        if(FragmentUtils.getVisibleFragment(this, false) instanceof ThirdStepRegisterFragment){
            redirectToLogin();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MSISDN_KEY, getMSISDN());
        outState.putString(SUBSCRIBER_TYPE_KEY, getSUBSCRIBER_TYPE());
        outState.putString(CUSTOMER_TYPE_KEY, getCUSTOMER_TYPE());
        outState.putBoolean(IS_MIGRATED_KEY, isIS_MIGRATED());
        outState.putString(USERNAME_KEY, getUSERNAME());
        outState.putString(EMAIL_KEY, getEMAIL());
        super.onSaveInstanceState(outState);
    }

    private void redirectToLogin(){
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.FRAGMENT, LoginActivity.LOGIN_FRAGMENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getCUSTOMER_TYPE() {
        return CUSTOMER_TYPE;
    }

    public void setCUSTOMER_TYPE(String CUSTOMER_TYPE) {
        this.CUSTOMER_TYPE = CUSTOMER_TYPE;
    }

    public String getSUBSCRIBER_TYPE() {
        return SUBSCRIBER_TYPE;
    }

    public void setSUBSCRIBER_TYPE(String SUBSCRIBER_TYPE) {
        this.SUBSCRIBER_TYPE = SUBSCRIBER_TYPE;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public boolean isIS_MIGRATED() {
        return IS_MIGRATED;
    }

    public void setIS_MIGRATED(boolean IS_MIGRATED) {
        this.IS_MIGRATED = IS_MIGRATED;
    }

    public static class RegisterActivityTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "register 1st step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "register 1st step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "register";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Registration";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }

    public ImageView getBackButton(){
        return backButton;
    }

}
