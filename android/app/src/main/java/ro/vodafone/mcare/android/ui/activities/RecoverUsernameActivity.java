package ro.vodafone.mcare.android.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.fragments.recover.RecoverUsernameFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bogdan Marica on 20.10.2017.
 */
public class RecoverUsernameActivity extends BaseActivity {

    public static String TAG = "RecoverUsernameActivity";

    private ImageView logo;
    private ImageView backButton;

    private VodafoneTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_username);

        initTracking();
        initActivityViews();

        attachRecoverUsernameFragment();
    }

    void initActivityViews() {
        setVodafoneBackgroundOnWindow();

        title = (VodafoneTextView) findViewById(R.id.title);

        backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * init tracking event
     */
    void initTracking() {
        /*
        Map<String, Object> tealiumMapView = new HashMap(4);
        tealiumMapView.put("recover username", "mcare:recover username");
        tealiumMapView.put("recover data", "mcare:recover username");
        TealiumHelper.trackView("mcare:recover username", tealiumMapView);*/

        RecoverUsernameTrackingEvent event = new RecoverUsernameTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void attachRecoverUsernameFragment() {
        RecoverUsernameFragment fragment = new RecoverUsernameFragment();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VodafoneController.getInstance().setFromBackPress(true);
    }

    public static class RecoverUsernameTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "recover username";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "recover username");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "recover data";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Recover Username";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
