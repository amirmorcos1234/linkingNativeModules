package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.OptionDetailsCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
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
 * Created by Bivol Pavel on 11.04.2017.
 */

@Deprecated
public class YourServicesOptionDetailsFragment extends OffersFragment {
    public static String TAG = "YourServicesOptions";
    public static String ACTIVE_OFFER = "activeOffer";
    private static final String DATE_FORMAT = "dd MMMM yyyy";

    @BindView(R.id.option_name)
    VodafoneTextView optionName;

    @BindView(R.id.option_details_card)
    OptionDetailsCard optionDetailsCard;

    @BindView(R.id.price)
    VodafoneTextView price;

    @BindView(R.id.offer_start_date)
    VodafoneTextView offerStartDate;

    @BindView(R.id.offer_end_date)
    VodafoneTextView offerEndDate;

    @BindView(R.id.offer_days_duration)
    VodafoneTextView offerDaysDuration;

    @BindView(R.id.offer_duration_label)
    VodafoneTextView offerDurationLabel;

    private YourServicesActivity yourServicesActivity;
    private TravelingAboardActivity travellingAbroadActivity;
    private OfferRowInterface activeOfferRow;

    public OfferRowInterface getActiveOffer() {
        return activeOfferRow;
    }

    public static YourServicesOptionDetailsFragment createFragment(OfferRowInterface activeOffer) {
        YourServicesOptionDetailsFragment fragment = new YourServicesOptionDetailsFragment();

        Bundle arg = new Bundle();
        arg.putSerializable(ACTIVE_OFFER, activeOffer);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeOfferRow = (OfferRowInterface) getArguments().getSerializable(ACTIVE_OFFER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (yourServicesActivity != null) {
            yourServicesActivity.getNavigationHeader().hideSelectorView();
        } else if (travellingAbroadActivity != null) {
            travellingAbroadActivity.getNavigationHeader().hideSelectorView();
            travellingAbroadActivity.getNavigationHeader().setTitle(getTitle());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_services_option_details, null);

        ButterKnife.bind(this, v);

        if (getActivity() instanceof YourServicesActivity) {
            yourServicesActivity = (YourServicesActivity) getActivity();
        } else if (getActivity() instanceof TravelingAboardActivity) {
            travellingAbroadActivity = (TravelingAboardActivity) getActivity();
        }

        setDataInViews();
        trackView();

        return v;
    }

    private void setDataInViews() {
        try {
            User user = VodafoneController.getInstance().getUser();
            Long startDate;
            Long endDate;

            String priceFormatted = String.valueOf(NumbersUtils.twoDigitsAfterDecimal(activeOfferRow.getOfferPrice())) + " €";

            optionName.setText(activeOfferRow.getOfferName());
            price.setText(priceFormatted);

            if (user instanceof PrepaidUser) {
                startDate = ((ActiveOffer) activeOfferRow).getStartDate();
                endDate = ((ActiveOffer) activeOfferRow).getEndDate();

                if(((ActiveOffer) activeOfferRow).getRoamingInfoUrl() != null && !((ActiveOffer) activeOfferRow).getRoamingInfoUrl().equals("")){
                    optionDetailsCard.setInfoWebUrl(((ActiveOffer) activeOfferRow).getRoamingInfoUrl());
                }
            } else {
                startDate = ((ActiveOfferPostpaid) activeOfferRow).getStartDate();
                endDate = ((ActiveOfferPostpaid)activeOfferRow).getEndDate();
            }

            setAttributes(startDate, endDate);

        } catch(Exception ex) {
            Log.e(TAG, "Exception in setDataInViews", ex);
        }
    }

    public String getTitle() {
        return OffersLabels.getAcceptedOffersDetails();
    }

    private void setAttributes(Long startDate, Long endDate){
        String startDateString = DateUtils.getDate(String.valueOf(startDate), new SimpleDateFormat(DATE_FORMAT, new Locale("RO", "RO")));
        String endDateString = DateUtils.getDate(String.valueOf(endDate), new SimpleDateFormat(DATE_FORMAT, new Locale("RO", "RO")));

        if(startDateString!=null) {
            offerStartDate.setVisibility(View.VISIBLE);
            offerStartDate.setText(String.format(getContext().getResources().getString(R.string.your_services_details_start_date), startDateString));
        }

        if(endDateString!=null) {
            offerEndDate.setVisibility(View.VISIBLE);
            offerEndDate.setText(String.format(getContext().getResources().getString(R.string.your_services_details_end_date), endDateString));
        }

        if (endDate != null&&startDate!=null&&endDate>startDate) {
            offerDurationLabel.setVisibility(View.VISIBLE);
            offerDaysDuration.setVisibility(View.VISIBLE);

            LocalDate start = LocalDate.fromDateFields(new Date(startDate));
            LocalDate end = LocalDate.fromDateFields(new Date(endDate));

            if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
                offerDaysDuration.setText(String.format(getContext().getResources().getString(R.string.your_option_days), (String.valueOf(((ActiveOffer)activeOfferRow).getPeriod()))));
            }else{
                offerDaysDuration.setText(String.format(getContext().getResources().getString(R.string.your_option_days),String.valueOf(Days.daysBetween(start, end).getDays())));
            }

        }

        if (startDate!=null && endDate == null){
            offerDaysDuration.setVisibility(View.VISIBLE);
            offerDaysDuration.setText(ServicesLabels.getServices_unlimited_label());
        }
    }


    private static class YourServicesOptionDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "option details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"option details");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void trackView(){
        Map<String, Object> tealiumMapViewCallDetails =new HashMap(6);
        tealiumMapViewCallDetails.put("screen_name","option details");
        tealiumMapViewCallDetails.put("journey_name","your services");
        tealiumMapViewCallDetails.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapViewCallDetails);

        YourServicesOptionDetailsTrackingEvent event = new YourServicesOptionDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }
}
