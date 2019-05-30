package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.card.travelling.RoamingCountryInputCard;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.card.travelling.TravellingRoamingOptionsCard;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;

/**
 * Created by Deaconescu on 18/4/2018.
 */

public class TravellingRomaingOptionsViewGroup extends LinearLayout  implements TravellingAbroadContract.AbroadView{

    TravellingAbroadPresenter mTravellingAbroaddPresenter = new TravellingAbroadPresenter(this);

    public TravellingRomaingOptionsViewGroup(Context context) {
        super(context);
        init(null);
    }

    public TravellingRomaingOptionsViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        this.setOrientation(VERTICAL);
        setupController();
    }

    private void setupController(){
        mTravellingAbroaddPresenter.setup(this);
//                .requestData();
    }

    public void atachCards(List<TravellingRoamingOptionsCard> cardList){
        removeAllViews();
        for(TravellingRoamingOptionsCard card : cardList){
            this.addView(card);
        }
    }


    @Override
    public void addTravellingTarrifesCard(ZonesList zonesList, boolean showButton, String countryName) {

    }


    @Override
    public void onTypeAccessSuccess(boolean isRoamingActive, boolean isInternationalActive) {

    }

    @Override
    public void onTypeAccessFailed() {

    }

    @Override
    public Activity getActivityInPresenter() {
        return null;
    }

    @Override
    public OnClickListener setServiceAdministrationBtnListener() {
        return null;
    }

    @Override
    public void showErrorCard() {

    }

    @Override
    public void showLoadingDialogFromBaseFragment() {

    }

    @Override
    public void stopLoadingDialogFromBaseFragment() {

    }

    @Override
    public void settingCard() {

    }

    @Override
    public void setUpLabels() {

    }

    @Override
    public RoamingStatusCard getRoamingStatusCard() {
        return null;
    }

    @Override
    public RoamingCountryInputCard getRoamingCountryInputCard() {
        return null;
    }

    @Override
    public TextView getCountryTitle() {
        return null;
    }

    @Override
    public RelativeLayout getRoamingTarrifesContainer() {
        return null;
    }

    @Override
    public void setUpTravellingRoamingOptionsViewGroup(List<TravellingRoamingOptionsCard> cardList) {

    }

    @Override
    public void hideTravellingRoamingOptionsViewGroup() {

    }

    @Override
    public void addRelatedOffers(ArrayList<PrePaidOffersList> offers) {

    }
}
