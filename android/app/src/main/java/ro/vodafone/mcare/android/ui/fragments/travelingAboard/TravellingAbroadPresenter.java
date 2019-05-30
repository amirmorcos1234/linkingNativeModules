package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.travelling.TravellingRoamingOptionsCard;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by user1 on 4/2/2018.
 */

public class TravellingAbroadPresenter implements TravellingAbroadContract.PresenterAbroad, TravellingAbroadContract.OnAbroadListener{
    public static String TAG = "TravellingAbroadPresenter";

    private TravellingAbroadInteractorImpl mTravellingInteractor;
    private TravellingAbroadContract.AbroadView mAbroadView;
    private TravellingRomaingOptionsViewGroup viewGroup;

    Boolean isRoamingOfferActiveFromList = false;

    private String defaulSelectedSID = VodafoneController.getInstance().getUserProfile().getSid();

    private String currentMsisdn;
    private String currentSubscriberId;
    boolean isPrepaid ;
    Profile profile;

    public TravellingAbroadPresenter(TravellingAbroadContract.AbroadView mView){
        this.mTravellingInteractor = new TravellingAbroadInteractorImpl(this);
        this.mAbroadView = mView;
    }

    public void checkMobileOperator(TextView textView, RelativeLayout layout) {
        CountryByIp countryByIp = (CountryByIp) RealmManager.getRealmObject(CountryByIp.class);
        if (countryByIp != null) {
            String countryCode = countryByIp.getCountryCode();
            if (!"RO".equals(countryCode) && countryCode != null) {
                RoamingTariffsSuccess roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);
                CountryList countryList = mTravellingInteractor.getCountryListObjectByCode(roamingTariffsSuccess, countryCode).first();
                if (countryList != null) {
                    textView.setText(TravellingAboardLabels.getTravelling_aboard_welcome_country_tittle() + countryList.getCountryName() + "!");
                    textView.setVisibility(android.view.View.VISIBLE);
                    if (!isPrepaid() ) {
                        mAbroadView.addTravellingTarrifesCard(mTravellingInteractor.loadZoneList(roamingTariffsSuccess, countryList.getCountryName()), true, countryList.getCountryName());
                    }
                }

            }
        }
    }

    public void checkRoamingActivation(String msisdn, String sid) {
        mTravellingInteractor.isPrepaid();

        if(mTravellingInteractor.isEbu()){
            mTravellingInteractor.getAccesTypeEbu(msisdn,sid, true);
        }else if (isPrepaid) {
            getAccessTypePrepaid(); //call api 20
        } else {
            mTravellingInteractor.getNationalPricePlan(msisdn, sid,true);
        }
    }

    public void getAccessTypePrepaid() {
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if(profile == null)
            getProfileFromApi10();

        checkActiveRoamingOffers4PrePaid(profile.getRoaming());
    }

    public void searchCountryTarrifs(String countryName){
        KeyboardHelper.hideKeyboard(mAbroadView.getActivityInPresenter());
        RoamingTariffsSuccess roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);

        ZonesList zone = mTravellingInteractor.loadZoneList(roamingTariffsSuccess, countryName);
        if (zone != null) {
            Bundle args = new Bundle();
            args.putString("country_name", countryName);
            ((TravelingAboardActivity) mAbroadView.getActivityInPresenter()).addFragmentWithParams(FragmentUtils.newInstance(TravellingCountryFragment.class), args);
        } else
            new CustomToast.Builder(mAbroadView.getActivityInPresenter()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
    }

    public void setUpRoamingContainer(boolean isActive){
        if(isPrepaid()){
            setUpRoamingContainerForPrepaid(isActive, null ,null);
        }else{
            setUpRoamingContainersUserNotPrepid(isActive);
        }
    }

    public void setUpRoamingContainersUserNotPrepid(boolean isActive) {
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        String alias = mTravellingInteractor.getAlias(profile);

        mAbroadView.getRoamingStatusCard().setIsNationalOnly(mTravellingInteractor.getNationalOnlyPP())
                .setIsOffersFromList(isRoamingOfferActiveFromList)
                .setIsPrepaid(isPrepaid())
                .setIsActive(isActive)
                .setAliasName(alias)
                .setRoamingDescription(mTravellingInteractor.setRoamingStatusDescription(isActive))
                .setButtonTextAndListener(mAbroadView.setServiceAdministrationBtnListener(), TravellingAboardLabels.getTravelling_aboard_service_administration_button())
                .buildStatusCard(false);
        mAbroadView.stopLoadingDialogFromBaseFragment();
        mAbroadView.getRoamingStatusCard().hideLoading();
    }


    public void setUpRoamingContainerForPrepaid(boolean isActive, String roamingLabel, Boolean isNationalOnly) {
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        String alias = mTravellingInteractor.getAlias(profile);

        mAbroadView.getRoamingStatusCard().setIsNationalOnly(mTravellingInteractor.getNationalOnlyPP())
                .setIsOffersFromList(isRoamingOfferActiveFromList)
                .setIsPrepaid(isPrepaid())
                .setIsActive(isActive)
                .setAliasName(alias)
                .setRoamingDescriptionFromtml(roamingLabel)
                .setButtonTextAndListener(mAbroadView.setServiceAdministrationBtnListener(), TravellingAboardLabels.getTravelling_aboard_service_administration_button())
                .buildStatusCard(false);
        mAbroadView.getRoamingStatusCard().hideBubleForNationalOnly(isNationalOnly);

        mAbroadView.stopLoadingDialogFromBaseFragment();
        mAbroadView.getRoamingStatusCard().hideLoading();
    }

    public void getRoamingTariffs(){
        TravellingAboardService travellingAboardService = new TravellingAboardService(VodafoneController.getInstance());
        travellingAboardService.getRoamingTariffs(isPrepaid(),
                VodafoneController.getInstance().getUser() instanceof EbuMigrated)
                .subscribe((new RequestSaveRealmObserver<GeneralResponse<RoamingTariffsSuccess>>() {

            @Override
            public void onNext(GeneralResponse<RoamingTariffsSuccess> roamingTariffsResponse) {
                super.onNext(roamingTariffsResponse);
                roamingTariffsResponse.getTransactionSuccess().setTextsInHTML();
                if (roamingTariffsResponse.getTransactionSuccess() != null) {
                    if (roamingTariffsResponse.getTransactionSuccess().getCountryList().size() > 0) {
                        mAbroadView.getRoamingCountryInputCard().setupPrefilledCountrySearch(mTravellingInteractor.getCountryList(roamingTariffsResponse.getTransactionSuccess()));
                        checkMobileOperator(mAbroadView.getCountryTitle(), mAbroadView.getRoamingTarrifesContainer());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onCompleted() {

                mAbroadView.stopLoadingDialogFromBaseFragment();
            }
        }));
    }

    @Override
    public void initTealium() {
        //Tealium Track AbroadView
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.roaming);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.roaming);
        tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        TravellingAbroadFragment.TravellingAbroadTrackingEvent event = new TravellingAbroadFragment.TravellingAbroadTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void checkActiveRoamingOffers4PrePaid(final Boolean roamingStatus) {

        OffersService offersService = new OffersService(VodafoneController.getInstance());
        String sid = VodafoneController.getInstance().getUserProfile().getSid();

        if (sid != null && sid.contains("-")) {
            sid = sid.replace("-", "");
        }

        offersService.getEligibleActiveOffers4PrePaid(sid).subscribe(new RequestSaveRealmObserver<GeneralResponse<ActiveOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
                super.onNext(activeOffersSuccessResponse);
                List<String> roamingOffersIds = AppConfiguration.getRoamingOfferIdList();
                if (activeOffersSuccessResponse.getTransactionSuccess() != null && activeOffersSuccessResponse.getTransactionStatus() == 0) {
 
                        mTravellingInteractor.getRoamingLabels(roamingStatus,true);
                        if (activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList() != null && activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList().size() > 0) {
                            for (ActiveOffer activeOffer : activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList()) {
                               for (int j=0; j<roamingOffersIds.size(); j++) {
                                        if (roamingOffersIds.get(j).equals(String.valueOf(activeOffer.getOfferId()))) {
                                            isRoamingOfferActiveFromList = true;
                                        }
                                    }
                                }
                        }
                        onDataLoaded(activeOffersSuccessResponse.getTransactionSuccess());

                }
                else {
                    isRoamingOfferActiveFromList = false;
                    setUpRelatedOffers(null);
                    showErrorCard();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isRoamingOfferActiveFromList = false;
                showErrorCard();
            }
        });
    }

    private void getCostControl(final ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess){
        UserDataService userDataService = new UserDataService(VodafoneController.getInstance());
        userDataService.reloadCostControl(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn())
            .subscribe(new RequestSaveRealmObserver<GeneralResponse<CostControl>>() {
                @Override
                public void onNext(GeneralResponse<CostControl> costControlGeneralResponse) {
                    super.onNext(costControlGeneralResponse);
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    setupDataPostpaid(activeOffersPostpaidSuccess);
                }
            });
    }

    public void requestData() {

        if(mTravellingInteractor.isEbu()){
            mTravellingInteractor.getAccesTypeEbu(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                    UserSelectedMsisdnBanController.getInstance().getSubscriberSid(), true);
        } else if (isPrepaid()) {
            if(profile != null){
            checkActiveRoamingOffers4PrePaid(profile.getRoaming());
            }else {
                getProfileFromApi10();
            }
        } else {
            try {
                mTravellingInteractor.getNationalPricePlan(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                        UserSelectedMsisdnBanController.getInstance().getSubscriberSid(), true);
            } catch (Exception E) {
                mTravellingInteractor.getNationalPricePlan(VodafoneController.getInstance().getUserProfile().getMsisdn(),
                        VodafoneController.getInstance().getUserProfile().getSid(),true);
            }
        }
    }

    private void getProfileFromApi10() {
        new UserDataService(VodafoneController.getInstance()).getUserProfile(true).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
            @Override
            public void onNext(GeneralResponse<Profile> response) {
                if (response.getTransactionSuccess() != null)
                    super.onNext(response);
                    profile = response.getTransactionSuccess();
                    checkActiveRoamingOffers4PrePaid(profile != null ? profile.getRoaming() : null);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
                checkActiveRoamingOffers4PrePaid(profile != null ? profile.getRoaming() : null);
            }
        });
    }

    public void reload(String msisdn, String subscriberId) {
        mAbroadView.getRoamingStatusCard().showLoading(true);
        if(getViewGroup() != null)
        getViewGroup().removeAllViews();

        mAbroadView.hideTravellingRoamingOptionsViewGroup();
        currentMsisdn = msisdn;
        currentSubscriberId = subscriberId;
        mAbroadView.stopLoadingDialogFromBaseFragment();
        ((TravelingAboardActivity) mAbroadView.getActivityInPresenter()).scrolltoTop();

        RoamingTariffsSuccess roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);
        if (roamingTariffsSuccess != null) {
            mAbroadView.getRoamingCountryInputCard().setupPrefilledCountrySearch(mTravellingInteractor.getCountryList(roamingTariffsSuccess));
        } else {
            getRoamingTariffs();
        }

        if (currentSubscriberId == null) {
            try {
                currentSubscriberId = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();
            } catch (NullPointerException npe) {
                currentSubscriberId = null;
            }
        }
        if (currentMsisdn == null) {
            try {
                currentMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
            } catch (NullPointerException npe) {
                currentMsisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
            }
        }
        checkRoamingActivation(currentMsisdn, currentSubscriberId != null ? currentSubscriberId : defaulSelectedSID );

        mAbroadView.settingCard();
        mAbroadView.setUpLabels();
        mAbroadView.stopLoadingDialogFromBaseFragment();
    }

    public void checkTypeAccess (){
        mAbroadView.getRoamingStatusCard().hideLoading();
        if(mTravellingInteractor.isEbu()){
        } else if (isPrepaid()) {
            ActiveOffersSuccess activeOffersSuccess = (ActiveOffersSuccess) RealmManager.getRealmObject(ActiveOffersSuccess.class);
            onDataLoaded(activeOffersSuccess);
        } else {
            ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess = (ActiveOffersPostpaidSuccess) RealmManager.getRealmObject(ActiveOffersPostpaidSuccess.class);
            onDataLoaded(activeOffersPostpaidSuccess);
        }
    }

    @Override
    public void onSuccess(boolean isRoamingActive, boolean isInternationalActive) {
        mAbroadView.onTypeAccessSuccess(isRoamingActive,isInternationalActive);
    }

    @Override
    public void onFailed() {
          mAbroadView.onTypeAccessFailed();
    }


    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value != null) {
                if (value instanceof ActiveOffersSuccess) {
                    setupDataPrepaid((ActiveOffersSuccess) value);
                }
                if (value instanceof ActiveOffersPostpaidSuccess) {
                    if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
                        getCostControl((ActiveOffersPostpaidSuccess) value);
                    } else {
                        setupDataPostpaid((ActiveOffersPostpaidSuccess) value);
                    }
                }
            } else if (value == null && isPrepaid()) {
                setUpRelatedOffers(null);
            }
        }
        if(viewGroup == null)
            viewGroup = new TravellingRomaingOptionsViewGroup(mAbroadView.getActivityInPresenter());
    }

    private void setUpRelatedOffers(List<TravellingRoamingOptionsCard> cardList){

            CountryByIp countryByIp = (CountryByIp) RealmManager.getRealmObject(CountryByIp.class);
            if (countryByIp != null && countryByIp.getCountryCode() != null && !"RO".equals(countryByIp.getCountryCode())) {
                String countryCode = countryByIp.getCountryCode();
                RoamingTariffsSuccess roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);
                CountryList countryList = mTravellingInteractor.getCountryListObjectByCode(roamingTariffsSuccess, countryCode).first();
                if (countryList != null) {
                    ArrayList<PrePaidOffersList> relatedOffers = mTravellingInteractor.getCountryRoamingOffers(roamingTariffsSuccess, countryList.getCountryName());
                    if ((cardList==null||cardList.size()==0)&& relatedOffers != null && relatedOffers.size() > 0) {
                        mAbroadView.addTravellingTarrifesCard(mTravellingInteractor.loadZoneList(roamingTariffsSuccess, countryList.getCountryName()), false, countryList.getCountryName());
                        mAbroadView.addRelatedOffers(relatedOffers);
                    } else {
                        mAbroadView.addTravellingTarrifesCard(mTravellingInteractor.loadZoneList(roamingTariffsSuccess, countryList.getCountryName()), true, countryList.getCountryName());
                    }
                }

            }



    }
    private void setupDataPrepaid(ActiveOffersSuccess activeOffersSuccess){
        Log.d(TAG,"setupDataPrepaid data Null");
        if(activeOffersSuccess == null || activeOffersSuccess.getActiveOffersList() == null){
        }else{
            if(viewGroup == null)
                viewGroup = new TravellingRomaingOptionsViewGroup(mAbroadView.getActivityInPresenter());

            if(activeOffersSuccess.getActiveOffersList().size() != 0){
                List<TravellingRoamingOptionsCard> cardList = new ArrayList<>();
                List<String> roamingOffersIds = AppConfiguration.getRoamingOfferIdList();

                for (int i = 0; i < activeOffersSuccess.getActiveOffersList().size(); i++){
                    for (int j=0; j<roamingOffersIds.size(); j++) {
                        Log.d(TAG, roamingOffersIds.get(j) + " " + String.valueOf(activeOffersSuccess.getActiveOffersList().get(i).getOfferId()));
                        if (roamingOffersIds.get(j).equals(String.valueOf(activeOffersSuccess.getActiveOffersList().get(i).getOfferId()))) {
                            cardList.add(new TravellingRoamingOptionsCard(viewGroup.getContext()).buildPrepaidCard(activeOffersSuccess.getActiveOffersList().get(i)));
                        }
                    }
                }
                mAbroadView.setUpTravellingRoamingOptionsViewGroup(cardList);
                setUpRelatedOffers(cardList);
            }else {
                setUpRelatedOffers(null);
            }
        }
    }

    private void setupDataPostpaid(ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess){
        if(activeOffersPostpaidSuccess == null || activeOffersPostpaidSuccess.getActiveOffersList() == null){
            Log.d(TAG,"setupDataPostpaidCARD data Null");
        }else{

            if(viewGroup == null)
                viewGroup = new TravellingRomaingOptionsViewGroup(mAbroadView.getActivityInPresenter());

            Log.d(TAG,"setupDataPostpaidCARD data not null");
            List<TravellingRoamingOptionsCard> cardList = new ArrayList<>();
            for(int i = 0; i < activeOffersPostpaidSuccess.getActiveOffersList().size(); i++){
                if(activeOffersPostpaidSuccess.getActiveOffersList().get(i).getOfferCategory().equals("Roaming")) {
                    Log.d(TAG, "Offer category " + activeOffersPostpaidSuccess.getActiveOffersList().get(i).getOfferCategory());
                    cardList.add(new TravellingRoamingOptionsCard(viewGroup.getContext()).buildPostpaidCard(activeOffersPostpaidSuccess.getActiveOffersList().get(i)));
                }else{
                    Log.d(TAG, "Offer category is: " + activeOffersPostpaidSuccess.getActiveOffersList().get(i).getOfferCategory());
                }
            }

            mAbroadView.setUpTravellingRoamingOptionsViewGroup(cardList);
        }
    }

    public TravellingAbroadPresenter setup(TravellingRomaingOptionsViewGroup activeOptionsViewGroup) {
        this.viewGroup = activeOptionsViewGroup;
        return this;
    }

    public TravellingRomaingOptionsViewGroup getViewGroup(){
        return viewGroup;
    }

    public Boolean isGeoNumer(){
        return mTravellingInteractor.isGeonumber();
    }

    public Boolean isPrepaid() {
        isPrepaid = mTravellingInteractor.isPrepaid();
        return isPrepaid;
    }

    @Override
    public void addOffersContainer(ViewGroup view, RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {

    }

    @Override
    public void setRoaming(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly) {
        mAbroadView.stopLoadingDialogFromBaseFragment();
        setUpRoamingContainerForPrepaid(roamingStatus,roamingLabel,isNationalOnly);
    }

    @Override
    public void showErrorCard() {
        mAbroadView.getRoamingStatusCard().hideLoading();
        mAbroadView.stopLoadingDialogFromBaseFragment();
        mAbroadView.showErrorCard();
    }
}
