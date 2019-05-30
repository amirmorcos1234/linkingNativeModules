package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.travelling.OffersCard;
import ro.vodafone.mcare.android.card.travelling.RoamingCountryInputCard;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.card.travelling.TravellingRoamingOptionsCard;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Eliza Deaconescu on 4/11/2017.
 */

public class TravellingAbroadFragment extends BaseFragment implements TravellingAbroadContract.AbroadView {
    static public final String TAG = "TravellingAboardFrag";
    @BindView(R.id.country_tittle)
    TextView countryTitle;
    @BindView(R.id.travelling_question)
    TextView travellingQuestion;
    @BindView(R.id.more_info_label)
    TextView moreInfoLabel;
    @BindView(R.id.travelling_aboad_card_view)
    RelativeLayout cardView;
    @BindView(R.id.roaming_tarrifes_container)
    RelativeLayout roamingTarrifesContainer;

    @BindView(R.id.country_input_card)
    RoamingCountryInputCard roamingCountryInputCard;
    @BindView(R.id.roaming_activation_card)
    RoamingStatusCard roamingActivationCard;

    @BindView(R.id.active_options_groupview)
    TravellingRomaingOptionsViewGroup travellingRomaingOptionsViewGroup;

    @BindView(R.id.offers_container)
    CoordinatorLayout offers_container;
    @BindView(R.id.offers_layout)
    LinearLayout offersLayout;
    TravellingAbroadPresenter mTravellingAbroadPresenter  = new TravellingAbroadPresenter(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpNavigationHeader();
        callForAdobeTarget(AdobePageNamesConstants.ROAMING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.travelling_aboard_fragment, container, false);

        ButterKnife.bind(this, v);

        mTravellingAbroadPresenter.getRoamingTariffs();
        mTravellingAbroadPresenter.initTealium();


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roamingCountryInputCard.addSubmitButtonClickListener(countryInputBtnListener);
        cardView.setOnClickListener(cardViewOnClickListener);
        mTravellingAbroadPresenter.requestData();

        roamingActivationCard.showLoading(true);

        settingCard();
        setUpLabels();
        stopLoadingDialog();
    }

    View.OnClickListener cardViewOnClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roaming);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingTipsButton);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("TravellingAbroadFragment", tealiumMapEvent);

            KeyboardHelper.hideKeyboard(getActivity());
            ((TravelingAboardActivity) getActivity()).addFragment(FragmentUtils.newInstance(TravellingHintsFragment.class));
        }
    };

   View.OnClickListener countryInputBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            if (roamingCountryInputCard.isInputValid()) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roaming);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingShowRates);
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("TravellingAbroadFragment", tealiumMapEvent);

                mTravellingAbroadPresenter.searchCountryTarrifs(roamingCountryInputCard.getSearchedCountry());
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
            mTravellingAbroadPresenter.reload(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(), UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
        }
    }

    public String getTitle() {
        return "International și Roaming";
    }

    View.OnClickListener onCardErrorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            roamingActivationCard.showLoading(true);
            if (mTravellingAbroadPresenter.isPrepaid())
                errorStep();
            mTravellingAbroadPresenter.reload(null, null);
        }
    };

    public void settingCard() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, cardView, false);
        final VodafoneTextView cardTitle = (VodafoneTextView) view.findViewById(R.id.cardTitle);
        final VodafoneTextView cardSubtext = (VodafoneTextView) view.findViewById(R.id.cardSubtext);

        cardTitle.setPadding(20, 5, 5, 20);
        cardTitle.setText(TravellingAboardLabels.getTravelling_aboard_card_tittle());
        cardSubtext.setVisibility(android.view.View.GONE);

        cardView.addView(view);
    }

    public void setUpLabels() {
        travellingQuestion.setText(TravellingAboardLabels.getTravelling_aboard_travelling_question1());
        moreInfoLabel.setText(TravellingAboardLabels.getTravelling_aboard_more_information_tittle());
    }

    private void errorStep() {
        (new UserDataService(getActivity())).getUserProfile(false).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
            @Override
            public void onNext(GeneralResponse<Profile> response) {
                super.onNext(response);
                stopLoadingAfterDuration(1);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
            }
        });
    }

    public void setUpNavigationHeader() {
        if(getActivity() == null) {
            return;
        }

        NavigationHeader navigationHeader = ((TravelingAboardActivity) getActivity()).getNavigationHeader();
        navigationHeader.removeViewFromContainer();
        navigationHeader.setTitle(TravellingAboardLabels.getTravelling_aboard_main_page_title());
        navigationHeader.buildMsisdnSelectorHeader();
        if (VodafoneController.getInstance().getUserProfile() != null)
            if (VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.RES_CORP) ||
                    VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                navigationHeader.displayDefaultHeader();
            } else {
                navigationHeader.hideSelectorView();
            }
        else
            navigationHeader.hideSelectorView();
    }

    @Override
    public void onResume() {
        super.onResume();
        roamingCountryInputCard.getCountryInput().setText("");
    }


    @Override
    public void addRelatedOffers(ArrayList<PrePaidOffersList> offers) {
        offersLayout.removeAllViews();
        if(offers!=null&&offers.size()>0)
            offers_container.setVisibility(View.VISIBLE);
        for (PrePaidOffersList offer: offers) {
            OffersCard offersCard = new OffersCard(getContext())
                    .setPrePaidOffersList(offer)
                    .build();
            offersLayout.addView(offersCard);
        }
    }



    @Override
    public void addTravellingTarrifesCard(ZonesList zonesList, boolean showButton, String countryName) {
        TravellingTarrifsCard travellingTarrifsCard = new TravellingTarrifsCard(getContext())
                .setZonesList(zonesList)
                .build(countryName);

        roamingTarrifesContainer.addView(travellingTarrifsCard);
        if(!showButton){
            travellingTarrifsCard.hideExtraOptionsButton();
        }

    }

    @Override
    public void onTypeAccessSuccess(boolean isRoamingActive, boolean isInternationalActive) {

         mTravellingAbroadPresenter.checkTypeAccess();

        if(mTravellingAbroadPresenter.isGeoNumer()){
            mTravellingAbroadPresenter.setUpRoamingContainer(isInternationalActive);
            D.d("Access type International");
            setTealimAccessTypeInternational();
        }else  if (isInternationalActive && isRoamingActive) {
            //Tealium Track AbroadView
            mTravellingAbroadPresenter.setUpRoamingContainer(isRoamingActive);
            setTealiumAccessTypeNational();
        } else if (isInternationalActive && !isRoamingActive) {
            mTravellingAbroadPresenter.setUpRoamingContainer(isRoamingActive);
            D.d("Access type International");
            setTealimAccessTypeInternational();
        } else {
            D.d("Access type National");
            mTravellingAbroadPresenter.setUpRoamingContainer(isRoamingActive);
        }
        roamingActivationCard.hideLoading();
    }


    @Override
    public void onTypeAccessFailed() {
        RealmManager.delete(AccessTypeSuccess.class);
        roamingActivationCard.hideLoading();
        showErrorCard();
    }

    public static class TravellingAbroadTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "roaming";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "roaming");
            s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
            s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);

            s.channel = "roaming";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "roaming";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }

    @Override
    public Activity getActivityInPresenter(){

        return getActivity();
    }

    @Override
    public View.OnClickListener setServiceAdministrationBtnListener() {

        View.OnClickListener clickListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                final TravelingAboardActivity activity = (TravelingAboardActivity) getActivity();
                KeyboardHelper.hideKeyboard(activity);
                TravellingServiceAdministration fragment = new TravellingServiceAdministration();
                activity.addFragment(fragment);
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roaming);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingServiceAdministrationButton);
                final UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
                if (userProfile != null && userProfile.getUserRole() != null)
                    tealiumMapEvent.put(TealiumConstants.user_type, userProfile.getUserRole().getDescription());
                TealiumHelper.trackEvent(TravellingAbroadFragment.this.getClass().getSimpleName(), tealiumMapEvent);


            }
        };
        return clickListener;
   }

    public void showErrorCard(){
        roamingActivationCard.showError(true, TravellingAboardLabels.getTravelling_aboard_system_error_text());
        roamingActivationCard.addOnErrorClickListener(onCardErrorClickListener);
    }


    @Override
    public void setUpTravellingRoamingOptionsViewGroup(List<TravellingRoamingOptionsCard> cardList) {
        travellingRomaingOptionsViewGroup.atachCards(cardList);
        travellingRomaingOptionsViewGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTravellingRoamingOptionsViewGroup() {
        travellingRomaingOptionsViewGroup.removeAllViews();
        travellingRomaingOptionsViewGroup.setVisibility(View.INVISIBLE);
    }
    public void stopLoadingDialogFromBaseFragment(){
        stopLoadingDialog();
    }

    public void showLoadingDialogFromBaseFragment() {
        showLoadingDialog();
    }

    public RoamingStatusCard getRoamingStatusCard(){
        return  roamingActivationCard;
    }

    public RoamingCountryInputCard getRoamingCountryInputCard() {
        return  roamingCountryInputCard;
    }

    public TextView getCountryTitle(){
        return countryTitle;
    }

    public RelativeLayout getRoamingTarrifesContainer(){
        return  roamingTarrifesContainer;
    }

    public void setTealiumAccessTypeNational() {
        //Tealium Track AbroadView
       /* Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "roaming");
        tealiumMapView.put("journey_name", "roaming");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);
*/
        TravellingAbroadTrackingEvent event = new TravellingAbroadTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void setTealimAccessTypeInternational() {
/*
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "roaming");
        tealiumMapView.put("journey_name", "roaming");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);
*/

        TravellingAbroadTrackingEvent event = new TravellingAbroadTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }
}