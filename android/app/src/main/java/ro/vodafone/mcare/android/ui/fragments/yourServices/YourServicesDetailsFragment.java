package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.ServicesDetailsCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by User on 12.04.2017.
 */

@Deprecated
public class YourServicesDetailsFragment extends OffersFragment {

    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String INCOMING_SERVICE = "incomingService";

    @BindView(R.id.service_name)
    VodafoneTextView serviceName;

    @BindView(R.id.price)
    VodafoneTextView servicePrice;

    @BindView(R.id.service_start_date)
    VodafoneTextView serviceStartDate;

    @BindView(R.id.service_end_date)
    VodafoneTextView serviceEndDate;

    @BindView(R.id.service_days_duration)
    VodafoneTextView serviceDaysDuration;

    @BindView(R.id.active_services_details_card)
    ServicesDetailsCard servicesDetailsCard;

    private OfferRowInterface activeService;
    private YourServicesActivity yourServicesActivity;
    private TravelingAboardActivity travellingAbroadActivity;
    public static YourServicesDetailsFragment instance;

    public static YourServicesDetailsFragment getInstance() {
        return instance;
    }

    public OfferRowInterface getActiveOffer() {
        return activeService;
    }

    public static YourServicesDetailsFragment createFragment(OfferRowInterface incomingService) {
        YourServicesDetailsFragment fragment = new YourServicesDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable(INCOMING_SERVICE, incomingService);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (yourServicesActivity != null) {
            yourServicesActivity.getNavigationHeader().hideSelectorView();
        } else if (travellingAbroadActivity != null) {
            travellingAbroadActivity.getNavigationHeader().hideSelectorView();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeService = (OfferRowInterface) getArguments().getSerializable(INCOMING_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        instance = this;
        View v = inflater.inflate(R.layout.fragment_active_services_details, null);
        ButterKnife.bind(this, v);

        if (getActivity() instanceof YourServicesActivity) {
            yourServicesActivity = (YourServicesActivity) getActivity();
        } else if (getActivity() instanceof TravelingAboardActivity) {
            travellingAbroadActivity = (TravelingAboardActivity) getActivity();
        }

        setAttributes();
        trackview();

        return v;
    }

    private void setAttributes() {

        serviceName.setText(activeService.getOfferName());
        servicePrice.setText(NumbersUtils.twoDigitsAfterDecimal(activeService.getOfferPrice()) + " €");

        User user = VodafoneController.getInstance().getUser();
        Long startDate;
        Long endDate;

        if (user instanceof PrepaidUser) {
            startDate = ((ActiveOffer) activeService).getStartDate();
            endDate = ((ActiveOffer) activeService).getEndDate();
        } else {
            startDate = ((ActiveOfferPostpaid) activeService).getStartDate();
            endDate = ((ActiveOfferPostpaid) activeService).getEndDate();
        }
        setStartDate(startDate);
        setEndDate(endDate);
        setServiceDuration(startDate, endDate);
    }

    private void setStartDate(Long startDate){
        String formatedStartDate = DateUtils.getDate(startDate, new SimpleDateFormat(DATE_FORMAT, new Locale("RO", "RO")));
        if (formatedStartDate != null) {
            serviceStartDate.setText(String.format(getContext().getResources().getString(R.string.your_services_details_start_date), formatedStartDate));
        } else {
            ((ViewGroup) serviceStartDate.getParent()).removeView(serviceStartDate);
        }
    }

    private void setEndDate(Long endDate){
        String formatedEndDate = DateUtils.getDate(endDate, new SimpleDateFormat(DATE_FORMAT, new Locale("RO", "RO")));
        if (endDate != null) {
            serviceEndDate.setText(String.format(getContext().getResources().getString(R.string.your_services_details_end_date), formatedEndDate));
        } else {
            ((ViewGroup) serviceEndDate.getParent()).removeView(serviceEndDate);
        }
    }

    private void setServiceDuration(Long startDate, Long endDate){
        if (startDate != null && endDate != null) {
            serviceDaysDuration.setText(String.format(getContext().getResources().getString(R.string.your_option_days),
                    String.valueOf(TimeUnit.MILLISECONDS.toDays(endDate - startDate))));
        } else if (startDate != null) {
            serviceDaysDuration.setText(ServicesLabels.getServices_unlimited_label());
        } else if (endDate == null) {
            View parentLayout = servicesDetailsCard.findViewById(R.id.service_duration_layout);
            ((ViewGroup) parentLayout.getParent()).removeView(parentLayout);
        } else {
            ((ViewGroup) serviceDaysDuration.getParent()).removeView(serviceDaysDuration);
        }
    }

    public String getTitle() {
        return "Detalii serviciu";
    }

    public static class YourServicesDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "services details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "services details");


            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void trackview(){
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","services details");
        tealiumMapView.put("journey_name","your services");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        YourServicesDetailsTrackingEvent event = new YourServicesDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }
}
