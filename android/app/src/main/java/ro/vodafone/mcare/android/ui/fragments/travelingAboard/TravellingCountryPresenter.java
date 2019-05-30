package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.travelling.OffersCard;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Eliza Deaconescu on 4/4/2018.
 */

public class TravellingCountryPresenter implements TravellingAbroadContract.CountryPresenter, TravellingAbroadContract.OnAbroadListener {

    Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
    private ArrayList<PrePaidOffersList> prePaidOffersLists;
    private TravellingAbroadInteractorImpl mTravellingInteractor;
    private TravellingAbroadContract.CountryView mView;
    private Boolean isPrepaid = VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.PREPAID);

    public TravellingCountryPresenter(TravellingCountryFragment view) {
        this.mTravellingInteractor = new TravellingAbroadInteractorImpl(this);
        this.mView = view;
        prePaidOffersLists = new ArrayList<>();
    }

    public void initTealium() {
        //Tealium Track AbroadView
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.countryInfo);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.roaming);
        tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        TravellingCountryFragment.TravelingCountryTrackingEvent event = new TravellingCountryFragment.TravelingCountryTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    public void setUpRoamingContainer(boolean isActive) {

        String alias = mTravellingInteractor.getAlias(profile);

        mView.populateRoamingStatusCard(alias, isActive);
    }

    @Override
    public void addOffersContainer(ViewGroup view, RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {
        prePaidOffersLists.clear();
        view.removeAllViews();
        prePaidOffersLists = mTravellingInteractor.getCountryRoamingOffers(roamingTariffsSuccess, writtenCountryName);
        if (mTravellingInteractor.isPrepaid() && !prePaidOffersLists.isEmpty()) {
            mView.showOffersView();
        } else {
            mView.hideOffersView();
        }
        for (PrePaidOffersList offer : prePaidOffersLists) {
            OffersCard offersCard = new OffersCard(mView.getContext())
                    .setPrePaidOffersList(offer)
                    .build();
            view.addView(offersCard);
        }
    }

    public void addTravellingTarrifesCard(RelativeLayout countryTarrifesListView, ZonesList zonesList, CountryList countryList, boolean refresh) {
        if (refresh) {
            countryTarrifesListView.removeAllViewsInLayout();
            countryTarrifesListView.requestLayout();
            countryTarrifesListView.invalidate();
        }
        TravellingTarrifsCard travellingTarrifsCard = new TravellingTarrifsCard(mView.getContext())
                .setZonesList(zonesList)
                .build(countryList.getCountryName());
        countryTarrifesListView.addView(travellingTarrifsCard);
        if (mTravellingInteractor.isPrepaid() && !prePaidOffersLists.isEmpty()) {
            travellingTarrifsCard.hideExtraOptionsButton();
        }
        mView.scrolltoTop();
    }

    public void manageRoamingCard() {
        if (mTravellingInteractor.isGeonumber()) {
            mView.hideRoamingContainer();
        } else if (mTravellingInteractor.isEbu()) {
            AccessTypeEBU accessTypeEBU = (AccessTypeEBU) RealmManager.getRealmObject(AccessTypeEBU.class);
            if (accessTypeEBU != null) {
                setUpRoamingContainer(accessTypeEBU.getIsRoaming());
            } else {
                mView.hideRoamingContainer();
            }
        } else if (profile != null && profile.getRoaming() != null && isPrepaid) {
            setUpRoamingContainer(profile.getRoaming());
        } else if (!isPrepaid) {

            if (profile != null && profile.getRoaming() != null) {
                setUpRoamingContainer(profile.getRoaming());
                return;
            }

            AccessTypeSuccess accessTypeSuccess = (AccessTypeSuccess) RealmManager.getRealmObject(AccessTypeSuccess.class);
            if (accessTypeSuccess != null && accessTypeSuccess.getIsRoaming() != null) {
                setUpRoamingContainer(accessTypeSuccess.getIsRoaming());
                return;
            }

            mView.hideRoamingContainer();

        } else {
            mView.hideRoamingContainer();
        }
    }

    public void requestData() {
        if (profile != null&&profile.getRoaming()!=null) {
            getRoamingLabels(profile.getRoaming());
        }
    }

    private void getRoamingLabels(boolean roamingStatus) {
        mTravellingInteractor.getRoamingLabels(roamingStatus, true);

    }

    @Override
    public void onSuccess(boolean isRoamingActive, boolean isInternationalActive) {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void setRoaming(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly) {
        String alias = mTravellingInteractor.getAlias(profile);
        mView.populateRoamingStatusCard(alias, roamingStatus);
        RoamingStatusCard roamingStatusCard = mView.getRoamingStatusCard();
        roamingStatusCard.hideLoading();
        roamingStatusCard.setIsNationalOnly(mTravellingInteractor.nationalOnlyPP)
                .setIsPrepaid(isPrepaid)
                .setIsActive(roamingStatus)
                .setAliasName(alias)
                .setRoamingDescriptionFromtml(roamingLabel)
                .buildStatusCard(false);
        roamingStatusCard.hideButton();
        roamingStatusCard.hideNationalOnlyPpDescription();
    }

    @Override
    public void showErrorCard() {

    }
}
