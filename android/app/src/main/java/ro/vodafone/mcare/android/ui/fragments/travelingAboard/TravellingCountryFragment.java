package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;


import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.travelling.RoamingCountryInputCard;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Deaconescu Eliza on 4/11/2017.
 */

public class TravellingCountryFragment extends BaseFragment implements TravellingAbroadContract.CountryView {
    public static final String TAG = "TravelingCountryF";
    @BindView(R.id.roaming_country_input_card)
    RoamingCountryInputCard roamingCountryInputCard;
    @BindView(R.id.roaming_status_card)
    RoamingStatusCard roamingStatusCard;
    @BindView(R.id.country_roaming_tarrifes_content_container)
    RelativeLayout countryTarrifesListView;
    @BindView(R.id.country_travelling_question)
    TextView travellingQuestion;
    @BindView(R.id.offers_container)
    CoordinatorLayout offers_container;
    @BindView(R.id.offers_layout)
    LinearLayout offersLayout;
    TravellingAbroadInteractorImpl mTravellingInteractorImpl;
    TravellingCountryPresenter mTravellingCountryPresenter;
    private boolean isPrepaid;
    private RoamingTariffsSuccess roamingTariffsSuccess;
    private NavigationHeader navigationHeader;
    View.OnClickListener countrySubmitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (roamingCountryInputCard.isInputValid()) {
                String countryName = roamingCountryInputCard.getSearchedCountry();
                Log.d(TAG, "Country input for search is: " + countryName);
                ZonesList zone = mTravellingInteractorImpl.loadZoneList(roamingTariffsSuccess, countryName);
                if (zone != null) {
                    setUpNavigationHeader(countryName);
                    mTravellingCountryPresenter.addOffersContainer(offersLayout, roamingTariffsSuccess, countryName);
                    mTravellingCountryPresenter.addTravellingTarrifesCard(countryTarrifesListView, zone, mTravellingInteractorImpl.countryList(roamingTariffsSuccess, countryName), true);
                } else {
                    new CustomToast.Builder(getContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                }
                //roamingCountryInputCard.getCountryInput().clearFocus();
                roamingCountryInputCard.getCountryInput().setText("");
                scrolltoTop();
            } else {
                Log.d(TAG, "Country empty ");
            }
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roaming);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingShowRates);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("TravellingAbroadFragment", tealiumMapEvent);

        }
    };
    private String countryNameToSearchFor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTravellingCountryPresenter = new TravellingCountryPresenter(this);
        mTravellingInteractorImpl = new TravellingAbroadInteractorImpl();

        navigationHeader = ((TravelingAboardActivity) getActivity()).getNavigationHeader();
        isPrepaid = VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.PREPAID);

        getDataFromArgs();
    }

    private void getDataFromArgs() {
        Bundle args = getArguments();
        countryNameToSearchFor = args.getString("country_name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = View.inflate(getContext(), R.layout.travelling_country_fragment, null);
        ButterKnife.bind(this, v);
        if (navigationHeader != null)
            navigationHeader.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupData();
    }

    private void setupData() {
        roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);

        if (roamingTariffsSuccess != null && roamingCountryInputCard != null) {
            roamingCountryInputCard.setupPrefilledCountrySearch(mTravellingInteractorImpl.getCountryList(roamingTariffsSuccess));
        }

        roamingCountryInputCard.addSubmitButtonClickListener(countrySubmitButtonListener);

        if (VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.PREPAID) ||
                VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.SEAMLESS_PREPAID_USER) ||
                VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.HYBRID)) {
            roamingStatusCard.showLoading(true);
            roamingStatusCard.setVisibility(View.VISIBLE);
            mTravellingCountryPresenter.requestData();
        } else {
            mTravellingCountryPresenter.manageRoamingCard();
        }

        Fresco.initialize(getActivity());

        setUpNavigationHeader(countryNameToSearchFor);
        mTravellingCountryPresenter.addOffersContainer(offersLayout, roamingTariffsSuccess, countryNameToSearchFor);
        mTravellingCountryPresenter.addTravellingTarrifesCard(countryTarrifesListView, mTravellingInteractorImpl.loadZoneList(roamingTariffsSuccess, countryNameToSearchFor),
                mTravellingInteractorImpl.countryList(roamingTariffsSuccess, countryNameToSearchFor), false);
        setupLabels();
        mTravellingCountryPresenter.initTealium();
    }

    public void setupLabels() {
        travellingQuestion.setText(TravellingAboardLabels.getTravelling_aboard_travelling_question());
    }

    public void setUpNavigationHeader(String countryName) {
        CountryList mCountryList = mTravellingInteractorImpl.countryList(roamingTariffsSuccess, countryName);
        navigationHeader.removeViewFromContainer();
        navigationHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        navigationHeader.setTitle("\n" + countryNameToSearchFor);
        navigationHeader.hideSelectorView();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.travelling_aboard_country_header, navigationHeader, false);

        CircleImageView draweeView = (CircleImageView) view.findViewById(R.id.country_header_image);
        draweeView.setDrawingCacheEnabled(true);

        loadImageSafeWithGlide(draweeView, mCountryList);

        TextView welcomeMessageOfSelectedCountry = (TextView) view.findViewById(R.id.welcome_message_of_selected_country);
        welcomeMessageOfSelectedCountry.setText(mCountryList.getMessageLocal());

        TextView translatedMessageOfCurrentCountry = (TextView) view.findViewById(R.id.translated_welcome_message_of_current_country);
        translatedMessageOfCurrentCountry.setText(mCountryList.getMessageRo() + "!");

        navigationHeader.addViewToContainer(view);
        navigationHeader.setVisibility(View.VISIBLE);
    }

    private void loadImageSafeWithGlide(ImageView target, CountryList mCountryList) {
        if (getContext() == null || target == null) {
            return;
        }

        if (mCountryList != null && mCountryList.getFlagURL() != null && !mCountryList.getFlagURL().isEmpty()) {
            Glide.with(getContext())
                    .load(mCountryList.getFlagURL())
                    .placeholder(R.drawable.default_avatar_icon)
                    //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                    .dontAnimate()
                    .into(target);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            roamingCountryInputCard.getCountryInput().setText("");
        } catch (Exception e) {
            D.e("ERROR THROWN HERE IF INFLATING ERROR LAYOUT");
            e.printStackTrace();
        }
    }

    @Override
    public void hideRoamingContainer() {
        roamingStatusCard.setVisibility(View.GONE);
    }

    @Override
    public void scrolltoTop() {
        if (getActivity() != null) {
            ((TravelingAboardActivity) getActivity()).scrolltoTop();
        }
    }

    @Override
    public void populateRoamingStatusCard(String alias, Boolean isActive) {
        roamingStatusCard.setVisibility(View.VISIBLE);
        roamingStatusCard.setIsNationalOnly(mTravellingInteractorImpl.nationalOnlyPP)
                .setIsPrepaid(isPrepaid)
                .setIsActive(isActive)
                .setAliasName(alias)
                .setRoamingDescriptionFromtml(mTravellingInteractorImpl.setRoamingStatusDescription(isActive))
                .buildStatusCard(false);
        roamingStatusCard.hideButton();
        roamingStatusCard.hideNationalOnlyPpDescription();
    }

    public RoamingStatusCard getRoamingStatusCard(){
        return  roamingStatusCard;
    }
    @Override
    public void showOffersView() {
        offers_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOffersView() {
        offers_container.setVisibility(View.GONE);
    }

    public static class TravelingCountryTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "country info";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "country info");

            s.channel = "roaming";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
