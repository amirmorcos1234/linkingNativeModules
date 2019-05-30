package ro.vodafone.mcare.android.ui.fragments.login.seamless;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.Go;
import ro.vodafone.mcare.android.utils.NetworkReceiver;
import ro.vodafone.mcare.android.utils.TealiumHelper;

public class WifiSettingsFragment extends BaseFragment{

    public static String TAG = "WifiSettingsFragment";

    VodafoneButton wifiSettingsButton;
    VodafoneButton loginButton;
    VodafoneButton registerButton;
    LinearLayout wifiSettingsFragmentContainer;

    private NetworkReceiver networkReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wifi_settings, null);

        wifiSettingsFragmentContainer = (LinearLayout) v.findViewById(R.id.wifi_settings_fragment_container);

        wifiSettingsButton = (VodafoneButton) v.findViewById(R.id.wifi_setting_button);
        wifiSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.turnOffWifiScreen);
                tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.turnOffWifi_setting_button);
                TealiumHelper.trackEvent(WifiSettingsFragment.this.getClass().getSimpleName(), tealiumMapEvent);

                Go.Setting(getContext());
            }
        });

        loginButton = (VodafoneButton) v.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.turnOffWifiScreen);
                tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.turnOffWifi_login_button);
                TealiumHelper.trackEvent(WifiSettingsFragment.this.getClass().getSimpleName(), tealiumMapEvent);


                LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);

                if(loginActivity != null){
                    loginActivity.displayLoginFragment();
                }

/*                Animation anim = AnimationUtils.loadAnimation(getContext(),
                        R.anim.slid_down_fragment);

                wifiSettingsFragmentContainer.startAnimation(anim);
                wifiSettingsFragmentContainer.setVisibility(View.GONE);*/

            }
        });

        registerButton = (VodafoneButton) v.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.turnOffWifiScreen);
                tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.turnOffWifi_register_button);
                TealiumHelper.trackEvent(WifiSettingsFragment.this.getClass().getSimpleName(), tealiumMapEvent);

                Intent intent = new Intent(getActivity().getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.turnOffWifiScreen);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.OOBEJourney);
        TealiumHelper.trackView(WifiSettingsFragment.this.getClass().getSimpleName(), tealiumMapView);

        WifiSettingsTrackingEvent event = new WifiSettingsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
        networkReceiver = NetworkReceiver.registerReceiver((AppCompatActivity) getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkReceiver);
    }


    public static class WifiSettingsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "turn off wifi";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"turn off wifi");


            s.channel = "OOBE";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
