package ro.vodafone.mcare.android.ui.activities.filters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Victor Radulescu on 5/2017.
 */

public class ServicesFilterActivity extends AppCompatActivity {

    private static final String TAG = "ServicesFilterActivity";

    @BindView(R.id.filter_periods_first_checkbox)
    CheckBox firstCheckBox;
    @BindView(R.id.filter_periods_label_second_checkbox)
    CheckBox secondCheckBox;
    @BindView(R.id.filter_type_with_phone_checkbox)
    CheckBox typeWithPhoneCheckBox;
    @BindView(R.id.filter_type_without_phone_checkbox)
    CheckBox typeWithoutPhoneCheckBox;

    public final static String ELIGIBLE_SIM_ONLY= "eligibleSimOnly";
    public final static String ELIGIBLE_WITH_DEVICE= "eligibleWithDevice";

    public final static String FILTER_RESULT_TYPE = "filter_result_type";
    public final static String FILTER_RESULT_PERIOD = "filter_result_period";

    private boolean showTypeFilter;

    private static boolean [] savedInstancesType = {true,true};
    private static boolean [] savedInstancesPeriod = {false,true};

    private static boolean [] defaultInstancesType = {true,true};
    private static boolean [] defaultInstancesPeriod = {false,true};


    @OnClick(R.id.filter_close)
    public void close(View view){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:other priceplans:filter overlay:button:(x)");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
    @OnClick(R.id.primaryButton)
    public void setFilter(){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:other priceplans:filter overlay:button:aplica");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

        Intent returnIntent = new Intent();
        if(showTypeFilter){
            returnIntent.putExtra(FILTER_RESULT_TYPE,getResultType());
        }
        returnIntent.putExtra(FILTER_RESULT_PERIOD,getResultPeriod());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
    @OnClick(R.id.secondaryButton)
    public void resetFilter(){
        if(showTypeFilter){
            resetTypeCheckboxes();
        }
        resetPeriodCheckboxes();
    }

    @OnCheckedChanged({R.id.filter_periods_first_checkbox,R.id.filter_periods_label_second_checkbox})
    public void checkPeriod(CheckBox checkBox,boolean checked){
        if(checked){
            return;
        }
        if( checkBox.getId()==R.id.filter_periods_first_checkbox && !secondCheckBox.isChecked() ){
            secondCheckBox.setChecked(true);
        }else if(checkBox.getId()==R.id.filter_periods_label_second_checkbox && !firstCheckBox.isChecked()){
            firstCheckBox.setChecked(true);
        }
    }
    @Optional
    @OnCheckedChanged({R.id.filter_type_with_phone_checkbox,R.id.filter_type_without_phone_checkbox})
    public void checkType(CheckBox checkBox,boolean checked){
        if(checked){
            return;
        }
        if( checkBox.getId()==R.id.filter_type_with_phone_checkbox && !typeWithoutPhoneCheckBox.isChecked() ){
            typeWithoutPhoneCheckBox.setChecked(true);
        }else if(checkBox.getId()==R.id.filter_type_without_phone_checkbox && !typeWithPhoneCheckBox.isChecked()){
            typeWithPhoneCheckBox.setChecked(true);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_services_filter);
        ButterKnife.bind(this);

        setIfShowTypeFilter();
        setDefaultValuesOfCheckboxes();
        hideFilterTypeContainer();

        setTealiumTrackingEvent();
    }

    private void setIfShowTypeFilter(){
        boolean simOnly = getIntent().getBooleanExtra(ELIGIBLE_SIM_ONLY,true);
        boolean deviceOnly = getIntent().getBooleanExtra(ELIGIBLE_WITH_DEVICE,true);
        showTypeFilter = simOnly && deviceOnly;
    }

    private void hideFilterTypeContainer(){
        if(showTypeFilter){
            return;
        }
        try{
            findViewById(R.id.filter_type_container).setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean[] getResultType(){
        boolean [] typeResult = new boolean[2];
        typeResult[0] = typeWithPhoneCheckBox.isChecked();
        typeResult[1] = typeWithoutPhoneCheckBox.isChecked();
        savedInstancesType = typeResult;
        return typeResult;
    }
    public boolean[] getResultPeriod(){
        boolean [] periodResult = new boolean[2];
        periodResult[0] = firstCheckBox.isChecked();
        periodResult[1] = secondCheckBox.isChecked();
        savedInstancesPeriod = periodResult;
        return periodResult;
    }

    private void resetTypeCheckboxes(){
        if(!showTypeFilter){
            return;
        }
        typeWithPhoneCheckBox.setChecked(true);
        typeWithoutPhoneCheckBox.setChecked(true);
    }
    private void resetPeriodCheckboxes(){
        firstCheckBox.setChecked(false);
        secondCheckBox.setChecked(true);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILTER_RESULT_PERIOD,getResultPeriod());
        if(showTypeFilter){
            returnIntent.putExtra(FILTER_RESULT_TYPE,getResultType());
        }
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void setDefaultValuesOfCheckboxes(){
        if(showTypeFilter){
            typeWithPhoneCheckBox.setChecked(savedInstancesType[0]);
            typeWithoutPhoneCheckBox.setChecked(savedInstancesType[1]);
        }

        firstCheckBox.setChecked(savedInstancesPeriod[0]);
        secondCheckBox.setChecked(savedInstancesPeriod[1]);
    }

    public static boolean[] getSavedInstancesType() {
        return savedInstancesType;
    }

    public static boolean[] getSavedInstancesPeriod() {
        return savedInstancesPeriod;
    }

    public static void resetValues(){
        savedInstancesPeriod = defaultInstancesPeriod;
        savedInstancesType = defaultInstancesType;
    }

    private void setTealiumTrackingEvent() {
        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);

        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        ServicesFilterTrackingEvent event = new ServicesFilterTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AdobeTargetController().trackPage(this, AdobePageNamesConstants.RETENTION_FILTER_PRICEPLAN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VodafoneController.getInstance().setFromBackPress(true);
    }

    public static class ServicesFilterTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:priceplan:filter overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:priceplan:filter overlay");


            s.prop5 = "sales:all priceplans";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
