package ro.vodafone.mcare.android.utils;

/**
 * Created by craigrouse on 06/03/2016.
 */

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.tealium.internal.listeners.WebViewCreatedListener;
import com.tealium.library.Tealium;
import com.tealium.lifecycle.BuildConfig;
import com.tealium.lifecycle.LifeCycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.RealmResults;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;

public final class TealiumHelper {

    public static String TAG = "TealiumHelper";

    private static Map<String, Object> tealiumMapView;
    private static Map<String, Object> tealiumMapEvent;


    // Identifier for the main instance
    public static final String TEALIUM_INSTANCENAME = "vodafone";

    @SuppressLint("NewApi")
    public static void initialise(Application application) {

        Log.d(TAG, "Passing through: initialise");

        tealiumMapView=new HashMap<>();
        tealiumMapEvent=new HashMap<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            // To connect to WebView if Tag Management is enabled
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // TODO: Change profile name to the profile name for your specific app.
        // TODO: Change profile name to "prod" if this is a production build

        Tealium.Config config = Tealium.Config.create(application, "vodafone", "ro-myvfapp-android",
                ro.vodafone.mcare.android.BuildConfig.TEALIUM_CONF); //PROD

        config.getEventListeners().add(createCookieEnablerListener());
        config.setRemoteCommandEnabled(true);

        // specifies if lifecycle auto tracking should be enabled or not
        boolean isAutoTracking  = true;
        // setup the lifecycle tracking instance
        LifeCycle.setupInstance(TEALIUM_INSTANCENAME, config, isAutoTracking);
        // create a new Tealium instance with the specified instance name
        Tealium.createInstance(TEALIUM_INSTANCENAME, config);
        // Add a remote command for SOASTA to enable SOASTA integration. Uncomment line to use SOASTA via Tealium IQ.
        Tealium.getInstance(TEALIUM_INSTANCENAME).addRemoteCommand(new SoastaRemoteCommand(application, VodafoneController.getInstance().getApplicationContext()));
        // To be used in Phase 2 for Qualtrics
//        Log.d(TAG, "Passing through: initialise - Qualtrics");
//        Tealium.getInstance(TEALIUM_INSTANCENAME).addRemoteCommand(new QualtricsRemoteCommand(application));

    }

    public static void addQualtricsCommand() {

        RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
        GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;
        if (gdprGetResponseOwner != null && gdprGetResponseOwner.getGdprPermissions() != null
                && gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory() != null
                && gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory().equalsIgnoreCase("yes")) {
            Log.d(TAG, "Passing through: initialise - addQualtricsCommand");
            Tealium.getInstance(TEALIUM_INSTANCENAME).addRemoteCommand(new QualtricsRemoteCommand());
        }

    }
    // intended to be called from one of the tracking methods (trackView or trackEvent)
    //Example:

    /*

    Map<String,String> tealiumMap = new HashMap(1);
    tealiumMap.put("somekey","somevalue");
    addVolatileDataString(instance, tealiumMap);

    */

    private static void addVolatileDataString (Tealium instance, Map<String, String> data) {
        // instance can be remotely destroyed by publish settings
        if (instance !=null && data != null) {
            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry prop = (Map.Entry) it.next();
                String key = (String) prop.getKey();
                String val = (String) prop.getValue();
                instance.getDataSources().getVolatileDataSources().put(key, val);
            }
        }
    }
    // intended to be called from one of the tracking methods (trackView or trackEvent)
     /*Example:

            Map<String,String> tealiumMap = new HashMap(1);
            tealiumMap.put("somekey","somevalue");
            addPersistentDataString(instance, tealiumMap);*/

    private static void addPersistentDataString (Tealium instance, Map<String, String> data) {
        // instance can be remotely destroyed by publish settings
        if (instance !=null && data != null) {
            Iterator it = data.entrySet().iterator();
            SharedPreferences.Editor persistentData = instance.getDataSources().getPersistentDataSources().edit();
            while (it.hasNext()) {
                Map.Entry prop = (Map.Entry) it.next();
                String key = (String) prop.getKey();
                String val = (String) prop.getValue();
                persistentData.putString(key, val);
            }
            persistentData.apply();
        }
    }


    public static void trackEvent(String eventName, Map<String, Object> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_INSTANCENAME);
        Log.d("**trackEvent",eventName+" , "+data.toString());
        // instance can be remotely destroyed by publish settings
        if(instance != null) {
            instance.trackEvent(eventName, data);
        }
    }

    public static void trackView(String viewName, Map<String, Object> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_INSTANCENAME);
        Log.d("**trackView",viewName+" , "+data.toString());
        if(instance != null) {
            instance.trackView(viewName, data);
        }
    }

    // returns the current Tealium instance for later use if required
    public static Tealium getInstance () {
        return Tealium.getInstance(TEALIUM_INSTANCENAME);
    }

    private static WebViewCreatedListener createCookieEnablerListener() {
        return new WebViewCreatedListener() {
            @Override
            public void onWebViewCreated(WebView webView) {
                final CookieManager mgr = CookieManager.getInstance();

                // Accept all cookies
                mgr.setAcceptCookie(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mgr.setAcceptThirdPartyCookies(webView, true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    CookieManager.setAcceptFileSchemeCookies(true);
                }
            }

            @Override
            public String toString() {
                return "EnableCookieWebViewCreatedListener";
            }
        };
    }

    public static void tealiumTrackView(String className, String journeyName, String screenName) {
        tealiumMapView.put(TealiumConstants.screen_name, screenName);
        tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole());
        tealiumMapView.put(TealiumConstants.journey_name, journeyName);
        trackView(className, tealiumMapView);
        tealiumMapView.clear();
        tealiumMapEvent.clear();
    }

    public static void tealiumTrackEvent(String className, String eventName, String screenName, String type) {
        tealiumMapEvent.put(TealiumConstants.screen_name, screenName);
        tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole());
        tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.appName + screenName + ":" + type + eventName);
        trackEvent(className, tealiumMapEvent);
        tealiumMapEvent.clear();
        tealiumMapView.clear();
    }
}
