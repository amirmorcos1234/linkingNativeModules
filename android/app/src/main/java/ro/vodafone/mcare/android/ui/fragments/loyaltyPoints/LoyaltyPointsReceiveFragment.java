package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyPointsActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyPointsReceiveFragment extends OffersFragment {
    public static String TAG = "LoyaltyPointsReceive";

    private String shopSessionToken;
    LoyaltyPointsActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);

        if(activity!=null){
            activity.getNavigationHeader().hideSelectorView();
            shopSessionToken =activity.getShopSessionToken();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loyalty_points_receive, container, false);
        Log.d(TAG, "onCreateView");

        VodafoneTextView pointsReceiveMessage = (VodafoneTextView) v.findViewById(R.id.points_receive_message);
        pointsReceiveMessage.setText(LoyaltyLabels.getLoyalty_how_to_receive_points_msg());

        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_faq);

        LoyaltyFAQTrackingEvent event = new LoyaltyFAQTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        if(activity!=null)
                activity.getNavigationHeader().setTitle(getTitle());
        return v;
    }


    @Override
    public String getTitle() {
        return LoyaltyLabels.getPoints_Gain_Title();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(activity!=null)
            activity.getNavigationHeader().setTitle(LoyaltyLabels.getLoyaltyPointsActivityPageTitle());
    }

    public static class LoyaltyFAQTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty faq";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"loyalty faq");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
