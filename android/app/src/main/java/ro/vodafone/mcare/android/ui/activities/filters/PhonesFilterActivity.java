package ro.vodafone.mcare.android.ui.activities.filters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.filters.MultipleSelectionFilterAdapter;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhonesFragment;
import ro.vodafone.mcare.android.ui.views.nonscrollable.NonScrollAbleGridView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

public class PhonesFilterActivity extends AppCompatActivity implements MultipleSelectionFilterAdapter.OnSelectionListenr {

    private static final String TAG = "PhonesFilterActivity";

    @BindView(R.id.optionsGridView)
    NonScrollAbleGridView optionsGridView;

    @BindView(R.id.primaryButton)
    Button setFilterButton;

    public final static String FILTER_RESULT_BRANDS="filter_result_brands";
    public final static String FILTER_INPUT_BRANDS="filter_input_brands";


    ArrayList<String> brandList;
    private static String [] savedBrands ;

    @OnClick(R.id.filter_close)
    public void close(View view){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:brand filters overlay:button:(x)");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finishActivity(PhonesFragment.FILTER_REQUEST_KEY);
        finish();
    }
    @OnClick(R.id.secondaryButton)
    public void selectAll(View view){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:brand filters overlay:buton:toate telefoanele");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        selectAllResetFilter();
        //setFilterButton.setEnabled(true);
        //setFilter();
    }
    @OnClick(R.id.primaryButton)
    public void setFilter(){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:brand filters overlay:button:aplica");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        Intent returnIntent = new Intent();
        ArrayList<String> results = getResultMarks(false);
        savedBrands= results.toArray(new String[results.size()]);

        if(results.isEmpty()){
            Toast.makeText(this,"Selectaţi cel puţin un brand!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Filter",results.toString());
        returnIntent.putExtra(FILTER_RESULT_BRANDS,results);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_phones_filter);
        ButterKnife.bind(this);
        setupBrandList();
        setAdapter();

        setTealiumTrackingEvent();
    }


    private void setupBrandList(){
        brandList = getIntent().getStringArrayListExtra(FILTER_INPUT_BRANDS);
    }

    private void setAdapter(){
        MultipleSelectionFilterAdapter multipleSelectionFilterAdapter = new MultipleSelectionFilterAdapter(this,brandList,this,savedBrands);
        optionsGridView.setAdapter(multipleSelectionFilterAdapter);
    }

    private void  selectAllResetFilter(){
        Intent returnIntent = new Intent();
        savedBrands= null;//results.toArray(new String[results.size()]);
        returnIntent.putExtra(FILTER_RESULT_BRANDS,savedBrands);
        setResult(Activity.RESULT_OK,returnIntent);

        finish();
    }

    public ArrayList<String> getResultMarks(boolean selectAllTrue) {
        if(selectAllTrue){
            return brandList;
        }

        ArrayList<String> resultList = new ArrayList<>();

        for (int i = 0; i < brandList.size(); i++) {
            String brand = brandList.get(i);
            AppCompatCheckBox checkbox = (AppCompatCheckBox) optionsGridView.findViewWithTag(i);
            if(checkbox.isChecked()){
                resultList.add(brand);
            }
        }
        return resultList;
    }

    public static String[] getSavedBrands() {
        return savedBrands;
    }

    @Override
    public void onAnySelectionSelected(boolean anySelected) {
        setFilterButton.setEnabled(anySelected);
    }
    public static void resetValues(){
        savedBrands =null;
    }

    private void setTealiumTrackingEvent() {
        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);

        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        PhonesFilterTrackingEvent event = new PhonesFilterTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AdobeTargetController().trackPage(this, AdobePageNamesConstants.RETENTION_FILTER_BRAND);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VodafoneController.getInstance().setFromBackPress(true);
    }

    public static class PhonesFilterTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:brand filters overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:brand filters overlay");


            s.prop5 = "sales:phone listing";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
