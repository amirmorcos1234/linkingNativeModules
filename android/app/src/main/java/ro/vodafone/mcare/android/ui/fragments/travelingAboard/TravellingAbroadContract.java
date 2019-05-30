package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import ro.vodafone.mcare.android.card.travelling.RoamingCountryInputCard;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.card.travelling.TravellingRoamingOptionsCard;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;

/**
 * Created by Eliza Deaconescu on 4/2/2018.
 */

public interface TravellingAbroadContract {

    interface PresenterAbroad {

        void initTealium();

        void checkMobileOperator(TextView textView, RelativeLayout layout);

        void getRoamingTariffs();

        void getAccessTypePrepaid();

        void setUpRoamingContainer(boolean isActive);

        void checkActiveRoamingOffers4PrePaid(final Boolean roamingStatus);

        void searchCountryTarrifs(String countryName);

        void requestData();

        void checkTypeAccess();

        Boolean isPrepaid();
        void addOffersContainer(ViewGroup view, RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName);

    }

    interface AdministrationPresenter {

        void setUpVariables();

        void errorStep();

        void setUpCardView();

        void initTealium();

        void runAdministrationFlowPrepaid(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly);

        void runAdministrationFlowPostpaid(String sid);

        void runAdministrationFlow(String msisdn, String sid);

        void reload(String msisdn, String sid);

        void activateOrInactivateRoaming(final boolean isActivtion);

        String setOperation(final boolean isRoaming, final boolean isInternational, final String serviceType);

        String hasInternationalStatus(Boolean isInternational);

        void activationConfirmationOverlay(final String serviceType);

        String getInternationalDescription(boolean isInternationalActive);

        String hasNoInternationalStatus(Boolean isRoaming);

        void checkPending(String sid, String type, boolean doNotDisplayLoading);
    }

    interface CountryPresenter {
        void initTealium();

        void manageRoamingCard();

        void addTravellingTarrifesCard(RelativeLayout countryTarrifesListView, ZonesList zonesList, CountryList countryList, boolean refresh);

        void setUpRoamingContainer(boolean isActive);

        void addOffersContainer(ViewGroup view, RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName);
    }

    interface Interactor {

        RealmResults<CountryList> getCountryListObject(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName);

        ZonesList loadZoneList(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName);

        ZonesList getCorrespondingZone(RoamingTariffsSuccess roamingTariffsSuccess, int zoneId);

        String[] getCountryList(RoamingTariffsSuccess roamingTariffsSuccess);

        CountryList countryList(RoamingTariffsSuccess roamingTariffsSuccess, String countyName);

        void getAccessTypePostPaid(String sid, boolean isFromTravelling);

        String getAlias(Profile profile);

        void getNationalPricePlan(String msisdn, String sid, boolean isFromTravelling);

        String setRoamingStatusDescription(boolean isActive);

        Boolean getNationalOnlyPP();

        // Methods for EBU
        void getAccesTypeEbu(String msisdn, String sid, boolean isFromTravelling);

        Boolean isEbu();

        Boolean isGeonumber();

        Boolean isPrepaid();
    }

    interface AbroadView {

        void addTravellingTarrifesCard(ZonesList zonesList,boolean showButton, String countryName);

        void onTypeAccessSuccess(boolean isRoamingActive, boolean isInternationalActive);

        void onTypeAccessFailed();

        Activity getActivityInPresenter();

        android.view.View.OnClickListener setServiceAdministrationBtnListener();

        void showErrorCard();

        void showLoadingDialogFromBaseFragment();

        void stopLoadingDialogFromBaseFragment();

        void settingCard();

        void setUpLabels();

        RoamingStatusCard getRoamingStatusCard();

        //        RoamingStatusCard getRoamingStatusCardForPrepaid();
        RoamingCountryInputCard getRoamingCountryInputCard();

        TextView getCountryTitle();

        RelativeLayout getRoamingTarrifesContainer();

        void setUpTravellingRoamingOptionsViewGroup(List<TravellingRoamingOptionsCard> cardList);

        void hideTravellingRoamingOptionsViewGroup();
        void addRelatedOffers(ArrayList<PrePaidOffersList> offers);

    }

    interface CountryView {
        Context getContext();

        void scrolltoTop();

        void hideRoamingContainer();

        void populateRoamingStatusCard(String alias, Boolean isActive);

        void showOffersView();

        void hideOffersView();

        RoamingStatusCard getRoamingStatusCard();
    }

    interface AdministrationView {
        void onTypeAccessSuccess(boolean isRoamingActive, boolean isInternationalActive);

        void onTypeAccessFailed();

        AppCompatActivity getActivityInAdministration();

        void showErrorCard();
    }

    interface OnAbroadListener {
        void onSuccess(boolean isRoamingActive, boolean isInternationalActive);

        void onFailed();

        void setRoaming(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly);

        void showErrorCard();
    }

    interface OnAdministrationListener {
        void onSuccess(boolean isRoamingActive, boolean isInternationalActive);

        void onFailed();

        void setRoaming(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly);

        void showErrorCard();
    }

}
