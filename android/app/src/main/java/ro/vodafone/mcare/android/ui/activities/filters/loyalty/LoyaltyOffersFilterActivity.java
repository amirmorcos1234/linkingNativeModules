package ro.vodafone.mcare.android.ui.activities.filters.loyalty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.filters.MultipleSelectionFilterAdapter;
import ro.vodafone.mcare.android.client.adapters.filters.MultipleSelectionFilterRecyclerViewAdapter;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhonesFragment;
import ro.vodafone.mcare.android.ui.views.nonscrollable.ExpandableHeightGridView;

/**
 * Created by Victor Radulescu on 9/4/2017.
 */

public class LoyaltyOffersFilterActivity  extends AppCompatActivity implements MultipleSelectionFilterRecyclerViewAdapter.OnSelectionListenr {

    private static final String TAG = "LoyaltyOffFiltActivity";

    @BindView(R.id.optionsGridView)
    RecyclerView optionsGridView;

    @BindView(R.id.primaryButton)
    Button setFilterButton;

    @BindView(R.id.grid_layout_firstOptionBtn)
    AppCompatCheckBox firstOptionGridLayoutBtn;

    @BindView(R.id.grid_layout_secondOptionBtn)
    AppCompatCheckBox secondOptionGridLayoutBtn;

    public final static String FILTER_RESULT_BRANDS="filter_result_brands";
    public final static String FILTER_RESULT_SHOW_EXPIRE_OPTION="filter_result_show_expire_option";
    public final static String FILTER_INPUT_CATEGORIES ="filter_input_brands";
    public final static String FILTER_SAVED_INPUT_CATEGORIES ="filter_saved_input_brands";
    public final static String FILTER_INPUT_SHOW_EXPIRE_OPTION ="filter_input_expired_option";
    public final static String FILTER_SAVED_INPUT_SHOW_EXPIRE_OPTION ="filter_saved_input_expired_option";

    ArrayList<String> offerList;
    private  String [] savedOffers;
    private  boolean [] savedExpiredOptions;

    private boolean showExpiredOption;
    private boolean anyCategorySelected;

    @OnClick(R.id.filter_close)
    public void close(View view){
        setupCloseFiltersButtonTrackingEvent();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finishActivity(PhonesFragment.FILTER_REQUEST_KEY);
        finish();
    }

    @OnClick(R.id.secondaryButton)
    public void selectAll(View view){
        setupResetFiltersButtonTrackingEvent();
        selectAllResetFilter();
    }

    @OnClick(R.id.primaryButton)
    public void setFilter(){
        setupFilterButtonTrackingEvent();

        Intent returnIntent = new Intent();
        ArrayList<String> results = getResultMarks(false);
        savedExpiredOptions = getExpireOptions();
        savedOffers = results.toArray(new String[results.size()]);

        Log.d("Filter",results.toString());
        returnIntent.putExtra(FILTER_RESULT_BRANDS,results);
        returnIntent.putExtra(FILTER_RESULT_SHOW_EXPIRE_OPTION,savedExpiredOptions);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnCheckedChanged({R.id.grid_layout_firstOptionBtn,R.id.grid_layout_secondOptionBtn})
    public void checkType(CheckBox checkBox, boolean checked){
        setFilterButtonActiveIfAnySelectedFromBoth();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_loyalty_offer_filter);
        ButterKnife.bind(this);
        setupOfferList();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        optionsGridView.setLayoutManager(layoutManager);
        setAdapter();
        setupIfExpiredOption();

    }

    private void setupOfferList(){
        offerList = getIntent().getStringArrayListExtra(FILTER_INPUT_CATEGORIES);
        ArrayList<String> savedOptions = getIntent().getStringArrayListExtra(FILTER_SAVED_INPUT_CATEGORIES);
        if(savedOptions!=null){
            savedOffers = new String[savedOptions.size()];
            savedOptions.toArray(savedOffers);
        }
    }
    private void setupIfExpiredOption(){
        showExpiredOption = getIntent().getBooleanExtra(FILTER_INPUT_SHOW_EXPIRE_OPTION,false);
        savedExpiredOptions = getIntent().getBooleanArrayExtra(FILTER_SAVED_INPUT_SHOW_EXPIRE_OPTION);
        if(!showExpiredOption){
            findViewById(R.id.optionsGridContainer).setVisibility(View.GONE);
        }else if(savedExpiredOptions!=null){
            firstOptionGridLayoutBtn.setChecked(savedExpiredOptions[0]);
            secondOptionGridLayoutBtn.setChecked(savedExpiredOptions[1]);
        }
    }

    private void setAdapter(){
        MultipleSelectionFilterRecyclerViewAdapter multipleSelectionFilterAdapter = new MultipleSelectionFilterRecyclerViewAdapter(this,offerList,this,savedOffers);
        optionsGridView.setAdapter(multipleSelectionFilterAdapter);
    }

    private void  selectAllResetFilter(){
        Intent returnIntent = new Intent();
        savedOffers= null;
        savedExpiredOptions =null;
        returnIntent.putExtra(FILTER_RESULT_BRANDS,savedOffers);
        returnIntent.putExtra(FILTER_RESULT_SHOW_EXPIRE_OPTION,savedExpiredOptions);
        setResult(Activity.RESULT_OK,returnIntent);

        finish();
    }

    public ArrayList<String> getResultMarks(boolean selectAllTrue) {
        if(selectAllTrue){
            return offerList;
        }

        return ((MultipleSelectionFilterRecyclerViewAdapter)optionsGridView.getAdapter()).getSavedSelections();
    }

    public boolean [] getExpireOptions(){
        boolean [] expiredOptions = new boolean[2];
        expiredOptions[0] = firstOptionGridLayoutBtn.isChecked();
        expiredOptions[1] = secondOptionGridLayoutBtn.isChecked();
        return expiredOptions;
    }

    @Override
    public void onAnySelectionSelected(boolean anySelected) {
        this.anyCategorySelected = anySelected;
        setFilterButtonActiveIfAnySelectedFromBoth();
    }

    private boolean anyExpiredOptionSelected() {
        return !showExpiredOption || firstOptionGridLayoutBtn.isChecked() || secondOptionGridLayoutBtn.isChecked();
    }

    private void setFilterButtonActiveIfAnySelectedFromBoth(){
        setFilterButton.setEnabled(anyExpiredOptionSelected() && anyCategorySelected);
    }

    public static class VodafoneMarketFilterTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty filter";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty filter");


            s.channel = "loyalty";
            s.getContextData().put("&&channel", s.channel);
            s.events = "event3";
            s.getContextData().put("event3", s.event3);
            s.events = "event5";
            s.getContextData().put("event5", s.event5);
            s.events = "event6";
            s.getContextData().put("event6", s.event6);

            s.prop5 = "content";
            s.getContextData().put("prop5", s.prop5);

            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            s.eVar73 = "loyalty";
            s.getContextData().put("eVar73", s.eVar73);
        }
    }

    private void setupFilterButtonTrackingEvent() {
        VodafoneMarketFilterTrackingEvent event = new VodafoneMarketFilterTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.event65 = "Aplica Filtre";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = "mcare:loyalty program:button:aplica filtre";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setupResetFiltersButtonTrackingEvent() {
        VodafoneMarketFilterTrackingEvent event = new VodafoneMarketFilterTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.event65 = "Reseteaza";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = "mcare:loyalty program:button:reseteaza";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setupCloseFiltersButtonTrackingEvent() {
        VodafoneMarketFilterTrackingEvent event = new VodafoneMarketFilterTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.event65 = "X";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = "mcare:loyalty program:button:x";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }
}
