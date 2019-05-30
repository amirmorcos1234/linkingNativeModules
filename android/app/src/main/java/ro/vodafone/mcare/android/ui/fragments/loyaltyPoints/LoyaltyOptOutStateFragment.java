package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyPointsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyOptOutStateFragment extends BaseFragment {
    private String TAG = "LoyaltyOptOutState";

    private RelativeLayout enrollInProgram;
    private RelativeLayout gainPoints;

    private LoyaltyPointsActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loyalty_points_fragment, container, false);

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_opt_out_state);
        activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);

        Log.d(TAG, "onCreateView");
        if(activity!=null){
            activity.setTitle(LoyaltyLabels.getLoyaltyPointsActivityPageTitle());
        }
        enrollInProgram = (RelativeLayout) v.findViewById(R.id.enroll_in_program);
        gainPoints = (RelativeLayout) v.findViewById(R.id.gain_loyalty_points);

        addButtons();
        setupClickListeners();

        return v;
    }

    private void addButtons() {
        View enrollView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, enrollInProgram, false);
        VodafoneTextView enrollCardTitle = (VodafoneTextView) enrollView.findViewById(R.id.cardTitle);
        VodafoneTextView enrollSubtext = (VodafoneTextView) enrollView.findViewById(R.id.cardSubtext);
        enrollSubtext.setVisibility(View.GONE);
        enrollCardTitle.setText(LoyaltyLabels.getLoyalty_points_enroll_in_program());
        enrollInProgram.addView(enrollView);

        View gainPointsView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, gainPoints, false);
        VodafoneTextView pointsCardTitle = (VodafoneTextView) gainPointsView.findViewById(R.id.cardTitle);
        VodafoneTextView pointsSubtext = (VodafoneTextView) gainPointsView.findViewById(R.id.cardSubtext);
        pointsSubtext.setVisibility(View.GONE);
        pointsCardTitle.setText(LoyaltyLabels.getLoyalty_points_gain());
        gainPoints.addView(gainPointsView);

    }

    private void setupClickListeners() {
        enrollInProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackEvent(TealiumConstants.loyalty_enroll_program);
                if(activity!=null)
                     activity.addFragment(new EnrollInProgramPageFragment());
            }
        });

        gainPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackEvent(TealiumConstants.loyalty_gain_points);
                if(activity!=null)
                    activity.addFragment(new LoyaltyPointsReceiveFragment());
            }
        });
    }
    private void trackEvent(String event){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_opt_out_state);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
    @Override
    public void onResume() {
        super.onResume();
        ((LoyaltyPointsActivity) getActivity()).getNavigationHeader().buildBanSelectorHeader();
    }
}
