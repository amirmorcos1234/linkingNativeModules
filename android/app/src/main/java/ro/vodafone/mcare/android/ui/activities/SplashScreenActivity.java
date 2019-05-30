package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adobe.mobile.Config;
import com.vfg.splash.SplashFragment;
import com.vfg.splash.models.BackgroundMode;
import com.vfg.splash.models.SplashListener;
import com.vfg.splash.models.SplashScreenConfiguration;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.notification.DeepLinkDispatcher;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;


/**
 * Created by Andrei DOLTU.
 */
public class SplashScreenActivity extends BaseActivity implements SplashListener {

    private static final String TAG = "SplashScreenActivity";

    private static final String SEAMLESS_LOGIN_FRAGMENT = "SeamlessLoginFragment";

    private Map<String, Object> lifecycleData = null;
    private Map<String, Object> acquisitionData = null;

    public static Logger LOGGER = Logger.getInstance(SplashScreenActivity.class);

    FrameLayout frameLayout;
    public Realm realm;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        realm = Realm.getDefaultInstance();
        // Adobe Analytics
        // Allow the SDK access to the application context
        Config.setContext(this.getApplicationContext());
        Config.setDebugLogging(true);

        frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.fragment_layout);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);


        //instantiating contextData for Adobe Analytics
        TrackingAppMeasurement.contextData = new HashMap<String, Object>();

        Config.registerAdobeDataCallback(new Config.AdobeDataCallback() {
            @Override
            public void call(Config.MobileDataEvent event, Map<String, Object> contextData) {
                String adobeEventTag = "ADOBE_CALLBACK_EVENT";
                switch (event) {
                    case MOBILE_EVENT_LIFECYCLE:
                        /* this event will fire when the Adobe sdk finishes processing lifecycle information */
                        lifecycleData = contextData;
                        break;
                    case MOBILE_EVENT_ACQUISITION_INSTALL:
						/* this event will fire on the first launch of the application after install when installed via an Adobe acquisition link */
                        acquisitionData = contextData;
                        break;
                    case MOBILE_EVENT_ACQUISITION_LAUNCH:
						/* this event will fire on the subsequent launches after the application was installed via an Adobe acquisition link */
                        acquisitionData = contextData;
                        break;
                }
            }
        });

        Log.d(TAG, "onCreate() - init splash");

        SplashscreenTrackingEvent event = new SplashscreenTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        onViewCreated();
    }

    protected void onViewCreated() {
        if (showSplashScreenAnimation()) {
            Log.d(TAG, "Passing through: showSplashScreenAnimation");
            SplashScreenConfiguration splashScreenConfiguration = new SplashScreenConfiguration();
            splashScreenConfiguration.setBackgroundMode(BackgroundMode.NIGHT);
            splashScreenConfiguration.setBackgroundImageResourceId(R.drawable.background);
            SplashFragment splashFragment = SplashFragment.getAnimatedSplashFragment(4000, splashScreenConfiguration);
            try {
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.fragment_layout, splashFragment, FragmentUtils.getTagForFragment(SplashFragment.class));
                t.commitAllowingStateLoss();
            } catch (Exception ex) {
                this.finish();
                LOGGER.e("Unknown exception initFragment", ex);
            }

        } else {
            Log.d(TAG, "Passing through: initializeFragment");

            onSplashScreenAnimationDone();
        }
    }

    //@Override
    public void onSplashScreenAnimationDone() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, LoadingActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        Log.d(TAG, "done() - init done");

        getDeepLinkDataIfAny();

        SplashScreenActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //For switching activities without animation
        overridePendingTransition(0, 0);
        Config.pauseCollectingLifecycleData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean showSplashScreenAnimation() {
        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
        if (userProfile == null) {
            String username = CredentialUtils.getUsername();
            String password = CredentialUtils.getPassword();
            if (username != null && password != null) {

                return false;
            }
        }
        return userProfile == null;
    }

    private void getDeepLinkDataIfAny() {
        if (getIntent() != null && getIntent().getData() != null) {
            DeepLinkDispatcher.dispatchAction(getIntent().getData());
        }
    }

    @Override
    public void onAnimationStarted() {

    }

    @Override
    public void onAnimationCompleted() {
        onSplashScreenAnimationDone();
    }

    public static class SplashscreenTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

                /*s.prop13 = pInfo.versionName;
                s.getContextData().put(TrackingVariable.P_PROP13,pInfo.versionName );*/
            s.pageName = s.prop21 + "splashscreen";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "splash screen");


            s.channel = "Splash";
            s.getContextData().put("&&channel", s.channel);

            //Log.d(TAG, "s.getContextData() = " + s.getContextData());

        }
    }
}
