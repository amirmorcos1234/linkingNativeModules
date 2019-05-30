package ro.vodafone.mcare.android.ui.fragments.callDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.CallDetailsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 13.02.2017.
 */
public class CallDetailsSelectionPageFragment extends BaseFragment {

    public static String TAG = "CallDetailsSelectionP";

    private CallDetailsActivity callDetailsActivity;

    private LinearLayout currentCallsButton;
    private LinearLayout billedCallsButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callDetailsActivity = (CallDetailsActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_call_details_selection_page, null);

        currentCallsButton = v.findViewById(R.id.current_calls_button);
        billedCallsButton = v.findViewById(R.id.billed_calls_button);

        currentCallsButton.setOnClickListener(clickListener);
        billedCallsButton.setOnClickListener(clickListener);

        if(shouldShowBanSelector()){
            callDetailsActivity.setBanListOnSelector();
        }else{
            callDetailsActivity.hideSelector();
        }

        setupLabels(v);

        return v;
    }

    private boolean shouldShowBanSelector() {
        return !EbuMigratedIdentityController.isUserVerifiedEbuMigrated();
    }

    private void setupLabels(View view){
        ((VodafoneTextView)view.findViewById(R.id.current_calls_button_labels)).setText(CallDetailsLabels.getCurrent_calls_details_button());
        ((VodafoneTextView)view.findViewById(R.id.billed_calls_button_labels)).setText(CallDetailsLabels.getBilled_calls_details_button());
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Map<String, Object> tealiumMapView;

            switch(v.getId()) {

                case R.id.current_calls_button:
                    callDetailsActivity.setReportType(CallDetailsActivity.REPORT_TYPE_UNBILLED);
                    callDetailsActivity.getBillingDate(1);

                    tealiumMapView = new HashMap(6);
                    //Tealium Track View
                    tealiumMapView.put("screen_name", "current call details");
                    tealiumMapView.put("journey_name", "call details");
                    tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackView("screen_name", tealiumMapView);

                    CallDetailsSelectionPageFragment.CallDetailsCurrentTrackingEvent currentEvent = new CallDetailsSelectionPageFragment.CallDetailsCurrentTrackingEvent();
                    VodafoneController.getInstance().getTrackingService().track(currentEvent);

                    break;
                case R.id.billed_calls_button:
                    callDetailsActivity.setReportType(CallDetailsActivity.REPORT_TYPE_BILLED);
                    callDetailsActivity.getBillingDate(3);

                    tealiumMapView = new HashMap(6);
                    //Tealium Track View
                    tealiumMapView.put("screen_name", "billed call details");
                    tealiumMapView.put("journey_name", "call details");
                    tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackView("screen_name", tealiumMapView);

                    CallDetailsSelectionPageFragment.CallDetailsBilledTrackingEvent billedEvent = new CallDetailsSelectionPageFragment.CallDetailsBilledTrackingEvent();
                    VodafoneController.getInstance().getTrackingService().track(billedEvent);

                    break;
            }
        }
    };

    public static class CallDetailsCurrentTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "current call details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"current call details");


            s.channel = "call details";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class CallDetailsBilledTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "billed call details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"billed call details");


            s.channel = "call details";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class CallDetailsPrepaidTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "prepaid call details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"prepaid call details");


            s.channel = "call details";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
