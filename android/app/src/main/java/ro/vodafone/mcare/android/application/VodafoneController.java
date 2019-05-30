package ro.vodafone.mcare.android.application;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.adform.adformtrackingsdk.AdformTrackingSdk;
import com.adobe.mobile.Config;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
import com.vfg.notifications.interfaces.OnNotificationOpenedListener;
import com.vodafone.netperform.NetPerformContext;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import devliving.online.securedpreferencestore.DefaultRecoveryHandler;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.interfaces.exceptions.HttpExceptionSessionExpireListener;
import ro.vodafone.mcare.android.service.tracking.TrackingService;
import ro.vodafone.mcare.android.ui.HelloWorldPackage;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.activities.other.PrivacyActivity;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubbleSingleton;
import ro.vodafone.mcare.android.ui.activities.support.ChatService;
import ro.vodafone.mcare.android.ui.activities.support.StartChatRequest;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.VodafoneNotificationManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by dragos.ivanov on 05.08.2016.
 */
public class VodafoneController extends Application implements OnNotificationOpenedListener, ReactApplication {

    public static final String TAG = VodafoneController.class.getSimpleName();
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.ttf";
    private static final int ADFORM_TRACKING_ID = 1209941;
    public static Typeface canaroExtraBold;
    static Deque<Activity> runningActivities = new LinkedBlockingDeque<>();
    //    static Map<Activity, SupportWindow> supportWindows = new HashMap<>();
    static SupportWindow supportWindow;
    static boolean activityVisible;
    private static Activity currentActivity;
    private static VodafoneController mInstance;
    private static boolean storeLocatorAplicationFreshInstalled;
    private static boolean displayGoToSettingsForPermissionFlag;
    private static boolean api10Failed;
    private static boolean api9Failed;
    private static PublishSubject<Activity> currentActivityObservable = PublishSubject.create();
    public final Handler handler = new Handler();
    HttpExceptionSessionExpireListener httpExceptionSessionExpireListener;
    IntentActionName doIntentActionIfDisableByAppGoingBackground = IntentActionName.NONE;
    BitmapDrawable backgroundBitmapDrawable;
    VoiceOfVodafoneResponse voiceOfVodafoneResponse = null;
    private User user;
    private TrackingService trackingService;
    private boolean isChatConnected = false;
    private ChatService chatService;
    private StartChatRequest chatRequest;
    private LifecycleOwner lifecycleOwner;
    private boolean isAppComesFromBackground = false;
    private int badgeCount = 0;
    private boolean isConversationOpen = false;
    private DashboardActivity dashboardActivity;
    /**
     * Verification if session expired and is needs a relogin
     */
    private boolean sessionExpired = false;
    private boolean librariesInitialized = false;
    private Subscription subscription;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private boolean wasScreenOn = true;
    private boolean isFromBackPress = false;
    private Realm realm;

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
        currentActivityObservable.onNext(activity);
    }

    public static Activity currentActivity() {
        return currentActivity;
    }

    public static void setApi10Failed(boolean api10failed) {
        api10Failed = api10failed;
    }

    public static boolean isAnyActivityNotNull() {
        if (currentActivity != null) {
            return true;
        }
        return false;
    }

    public static FragmentActivity currentFragmentActivity() {
        if ((currentActivity != null) && (currentActivity instanceof FragmentActivity))
            return (FragmentActivity) currentActivity;
        return null;
    }

    public static boolean isActivityVisible(Context context) {
        if (context != null && context instanceof Activity) {
            if (context.equals(currentActivity)) {
                return true;
            }
        }
        return false;
    }

    ;

    public static Activity findActivity(Class<? extends Activity> clazz) {
        for (Activity activity : runningActivities)
            if (activity.getClass() == clazz)
                return activity;
        return null;
    }

    public static FragmentActivity findFragmentActivity(Class<? extends FragmentActivity> clazz) {
        return (FragmentActivity) findActivity(clazz);
    }

    public static FragmentManager currentSupportFragmentManager() {
        final FragmentActivity activity = currentFragmentActivity();
        if (activity == null)
            return null;
        return activity.getSupportFragmentManager();
    }

    public static void clearAllMenuActivitysExceptDashboard() {
        for (Activity activity : runningActivities)
            if (activity instanceof BaseMenuActivity && !(activity instanceof DashboardActivity)) {
                activity.finish();
            }
    }

    public static void clearAllMenuActivitys() {
        for (Activity activity : runningActivities)
            if (activity instanceof BaseMenuActivity) {
                activity.finish();
            }
    }

    public static Fragment findFragment(Class<? extends FragmentActivity> hostActivityClazz, Class<? extends Fragment> fragmentClazz) {
        final String tag = FragmentUtils.getTagForFragment(fragmentClazz);
        final FragmentActivity fragmentActivity = findFragmentActivity(hostActivityClazz);
        if (fragmentActivity == null)
            return null;
        final FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        if (fm != null)
            return fm.findFragmentByTag(tag);
        return null;
    }

    public static Fragment findFragment(Class<? extends Fragment> clazz) {
        final String tag = FragmentUtils.getTagForFragment(clazz);
        for (Activity activity : runningActivities)
            if (activity instanceof FragmentActivity) {
                final FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
                if (fm == null)
                    continue;
                final Fragment fragment = fm.findFragmentByTag(tag);
                if (fragment != null)
                    return fragment;
            }
        return null;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static synchronized VodafoneController getInstance() {
        return mInstance;
    }

    public static boolean getApi9Failed() {
        return api9Failed;
    }

    public static void setApi9Failed(boolean api9failed) {
        api9Failed = api9failed;
    }

    public static BitmapDrawable getBackgroundBitmapDrawable() {
        return getInstance().backgroundBitmapDrawable;
    }

    public static void setBackgroundBitmapDrawable(BitmapDrawable backgroundBitmapDrawable) {
        getInstance().backgroundBitmapDrawable = backgroundBitmapDrawable;
    }

    public static Observable<Activity> getCurrentActivityObservable() {
        return currentActivityObservable;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public ChatService chatService() {
        if (chatService == null)
            chatService = new ChatService(this);
        return chatService;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //added this for native modules
        SoLoader.init(this, /* native exopackage */ false);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        D.w("Application created. old instance was null ? " + String.valueOf(mInstance == null));
        mInstance = this;
        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
        registerSecureSharedPreferences();
        registerActivityLifecycleCallbacks(new ActivityTracker());
        RealmManager.initRealm(this);
//initialize Adform
        AdformTrackingSdk.setAppName("VFRO Android Application");
        AdformTrackingSdk.startTracking(this, ADFORM_TRACKING_ID);
        //initializing tealium
        TealiumHelper.initialise(this);
        //setting soasta mpulse log true
        //MPLog.setDebug(true);

        Fresco.initialize(this);


        boolean allowedNotifications = false;
        realm = Realm.getDefaultInstance();
        AppConfiguration appConfiguration = ((AppConfiguration) RealmManager
                .getRealmObject(realm, AppConfiguration.class));
        if (appConfiguration != null) {
            allowedNotifications = appConfiguration.allowNotifications();
        }
        realm.close();

        final boolean finalAllowedNotifications = allowedNotifications;

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {
                // Perform any airship configurations here

                if (finalAllowedNotifications) {
                    // Enable user notifications
                    airship.getPushManager().setUserNotificationsEnabled(true);
                } else {
                    // set it disabled based on pref.
                    airship.getPushManager().setUserNotificationsEnabled(false);
                }

                // Create a customized default notification factory
                DefaultNotificationFactory defaultNotificationFactory = new DefaultNotificationFactory(getApplicationContext());

                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                    defaultNotificationFactory.setSmallIconId(R.drawable.ic_not_white_bg);
                    defaultNotificationFactory.setColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    defaultNotificationFactory.setSmallIconId(R.mipmap.ic_launcher);
                    defaultNotificationFactory.setColor(NotificationCompat.COLOR_DEFAULT);
                }

                // Set it
                airship.getPushManager().setNotificationFactory(defaultNotificationFactory);
            }
        });

        // initialize the Branch object
        // Branch.getAutoInstance(this);

        //initialize netperform CoreLib
        try {


            // NetPerform: Create NetPerformContext according to the current build configuration.
            NetPerformContext roContext;

            // Please replace IS_PRODUCTION_BUILD by any parameter you use in your build environment
            // to distinguish between production and pre-production (dev / test) builds.
            if (!BuildConfig.DEBUG) {
                // Known NetPerform host apps can create NetPerform in PRODUCTION mode as below.
                // The app’s PlayStore package name has to be set in the manifest, otherwise an
                // excpetion is thrown. Apps integrating NetPerform for the first time have to use a
                // config file provided by the NetPerform SDK team (contact netperformsdk@vodafone.com).
                roContext = new NetPerformContext(getApplicationContext(),
                        NetPerformContext.NetPerformEnvironment.PRODUCTION);
            } else {
                // For all development and test activities, use the PRE-PRODUCTION mode.
                // The measurement data will be uploaded to a pre-production backend.
                // Please set your PlayStore package name with a descriptive extension as e.g.
                // ".dev" for all pre-production builds. The NetPerform SDK will throw an exception for
                // a pre-production environment when your app's manifest declares a PlayStore
                // package name of a known NetPerform host app and is not a DEBUGGABLE build.
                roContext = new NetPerformContext(getApplicationContext(),
                        NetPerformContext.NetPerformEnvironment.PRE_PRODUCTION);
                // OPTIONAL: Enable debug logging
                // ONLY IN DEBUG MODE logcat output is provided.
                roContext.enableDeveloperLogging(true);
            }


            if (0 != (this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                NetPerformContext.Permissions.logManifestPermissionsForDevelop();
            }
            NetPerformContext.Permissions.hasRequiredPermissionsGranted();
            NetPerformContext.Permissions.getRequiredPermissionsNotGranted();

            // RO-CoreLib: Initialize and activate the CoreLib's modules.
            // If the user already opted in before, the measurement service is started
            // automatically.
            roContext.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initTypeface();
        Fonts.init(this);
    }

    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        compositeSubscription.remove(subscription);
    }

    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    private void registerSecureSharedPreferences() {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                // only for gingerbread and newer versions
                SecuredPreferenceStore.init(getApplicationContext(), new DefaultRecoveryHandler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public SupportWindow supportWindow(Activity activity) {
//        if (activity == null)
//            return null;
//
//        try {
//            SupportWindow supportWindow = supportWindows.get(activity);
//            if (supportWindow == null) {
//                supportWindow = SupportWindow.createInstance(activity);
//                supportWindows.put(activity, supportWindow);
//            }
//            return supportWindow;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

//    public void destroySupportWindow(Activity activity) {
//        SupportWindow supportWindow = supportWindows.get(activity);
//        if (supportWindow == null)
//            return;
//        supportWindow.destroy();
//    }

    //do when initialize libraries
    public void initializeLibraries(Activity activity) {
        this.librariesInitialized = true;
        Config.setContext(this);
        Config.setDebugLogging(true);

    }

    public void initializeUserIfDoesntExists(final Activity activity) {
        if (activity instanceof BaseMenuActivity && !(activity instanceof PrivacyActivity)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    realm = Realm.getDefaultInstance();
                    UserProfile userProfile = (UserProfile) RealmManager.getUnManagedRealmObject(realm, UserProfile.class);
                    realm.close();
                    D.d("UserProfile is null? " + String.valueOf(userProfile == null));
                    if (userProfile != null) {
                        VodafoneController.getInstance().setUser(userProfile);
                        D.d("UserProfile is  " + user.getUserProfile().getUserName());
                    } else {
                        UserDataController.getInstance().setCurrentDashboardAction(IntentActionName.NONE);
                        new NavigationAction(activity).finishCurrent(true).startAction(IntentActionName.LOGOUT);
                    }
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        D.w("Configuration changed");
    }

    private void initTypeface() {
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUser(UserProfile userProfile, boolean isSeamless) {
        realm = Realm.getDefaultInstance();

        setIsSeamless(isSeamless, userProfile.getMsisdn());
        UserDataController.setSsoTokenId(userProfile.getSsoTokenId());
        AppConfiguration appConfiguration = (AppConfiguration) RealmManager.getRealmObjectAfterStringField(
                realm,
                AppConfiguration.class,
                AppConfiguration.ID_APP_CONFIGURATION_KEY,
                userProfile.getUserName());
        RealmManager.startTransaction();
        if (appConfiguration == null) {
            appConfiguration = new AppConfiguration(isSeamless, userProfile.getUserName(), userProfile.getMsisdn());
        }

        Log.d("set User isSeamless", String.valueOf(isSeamless));
        //Realm.getDefaultInstance().commitTransaction();
        RealmManager.update(realm, appConfiguration);

        this.user = User.getNewUser(userProfile);
        this.user.setUserProfile(userProfile);
        realm.close();
    }

    public SupportWindow supportWindow(Activity activity) {

        try {

            if (supportWindow == null)
                supportWindow = SupportWindow.createInstance(activity);
            else
                supportWindow.updateSupportWindow(activity);
            return supportWindow;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void destroySupportWindow() {
        if (supportWindow == null)
            return;
        supportWindow.destroy();
    }

    public User getUser() {
        return user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = User.getNewUser(userProfile);
        this.user.setUserProfile(userProfile);
    }

    public UserProfile getUserProfile() {
        realm = Realm.getDefaultInstance();
        UserProfile userProfile = (UserProfile) RealmManager.getUnManagedRealmObject(realm, UserProfile.class);
        realm.close();
        return userProfile;
    }

    public Profile getProfile() {
        realm = Realm.getDefaultInstance();
        Profile profile = (Profile) RealmManager.getUnManagedRealmObject(realm, Profile.class);
        realm.close();
        return profile;
    }

    public AppConfiguration getAppConfiguration() {
        realm = Realm.getDefaultInstance();
        AppConfiguration appConfiguration;
        AppConfiguration appConfigurationUnManaged;
        if (user != null && user.getUserProfile() != null && user.getUserProfile().getUserName() != null) {
            appConfiguration = ((AppConfiguration) RealmManager.getRealmObjectAfterStringField(
                    realm,
                    AppConfiguration.class,
                    AppConfiguration.ID_APP_CONFIGURATION_KEY,
                    user.getUserProfile().getUserName()));
            appConfigurationUnManaged = (AppConfiguration) RealmManager.getUnManagedRealmObject(realm, appConfiguration);
        } else {
            appConfigurationUnManaged = getGeneralAppConfiguration();
        }
        if (appConfigurationUnManaged == null && getUserProfile() != null) {
            appConfigurationUnManaged = new AppConfiguration(false, getUserProfile().getUserName(), getUserProfile().getMsisdn());
            RealmManager.update(realm, appConfigurationUnManaged);
        }
        realm.close();
        return appConfigurationUnManaged;
    }

    /**
     * Checks the value of the 'android:debuggable' flag from the manifest file.
     *
     * @return returns true if its value is set to 'true' or false otherwise.
     */
    public boolean isDebugEnabled() {
        PackageInfo packageInfo = getPackageInfo();
        int flags = packageInfo.applicationInfo.flags;
        return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private PackageInfo getPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to retrieve the application version.", e);
            return null;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void clearData() {
        user = null;

    }

    @Override
    public void onNotificationOpened(String s) {
        D.e();
    }

    public boolean isSeamless() {
        AppConfiguration appConfiguration = getGeneralAppConfiguration();
        return appConfiguration.isSeamless();
    }

    public void setIsSeamless(boolean isSeamless, String previousMsisdn) {
        realm = Realm.getDefaultInstance();
        AppConfiguration appConfiguration = getGeneralAppConfiguration();
        RealmManager.startTransaction(realm);
        appConfiguration.setSeamless(isSeamless);
        if (isSeamless) {
            appConfiguration.setPreviousMsisdn(previousMsisdn);
        }
        RealmManager.update(realm, appConfiguration);
        RealmManager.commitTransaction(realm);
        realm.close();
    }

    public AppConfiguration getGeneralAppConfiguration() {
        realm = Realm.getDefaultInstance();
        AppConfiguration appConfiguration = (AppConfiguration) RealmManager.getRealmObjectAfterStringField(
                realm,
                AppConfiguration.class,
                AppConfiguration.ID_APP_CONFIGURATION_KEY,
                "");
        appConfiguration = (AppConfiguration) RealmManager.getUnManagedRealmObject(realm, appConfiguration);
        if (appConfiguration == null) {
            appConfiguration = createGeneralAppConfiguration();
        }
        realm.close();
        return appConfiguration;
    }

    public AppConfiguration createGeneralAppConfiguration() {
        realm = Realm.getDefaultInstance();
        RealmManager.startTransaction(realm);
        AppConfiguration appConfiguration = new AppConfiguration("");
        RealmManager.commitTransaction(realm);
        realm.close();
        return appConfiguration;
    }

    /**
     * Gets an instance of {@link TrackingService}.
     *
     * @return an instance of {@link TrackingService}.
     */
    public TrackingService getTrackingService() {
        if (trackingService == null) {
            trackingService = new TrackingService(mInstance);
        }
        return trackingService;
    }

    public HttpExceptionSessionExpireListener getHttpExceptionSessionExpireListener() {
        return httpExceptionSessionExpireListener;
    }

    public void setHttpExceptionSessionExpireListener(HttpExceptionSessionExpireListener httpExceptionSessionExpireListener) {
        this.httpExceptionSessionExpireListener = httpExceptionSessionExpireListener;
    }

    public synchronized boolean isSessionExpired() {
        return sessionExpired;
    }

    public synchronized void setSessionExpired(boolean sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public DashboardActivity getDashboardActivity() {
        return dashboardActivity;
    }

    public void setDashboardActivity(DashboardActivity dashboardActivity) {
        this.dashboardActivity = dashboardActivity;
    }

    public void clearDashboard() {
        dashboardActivity = null;
    }

    public IntentActionName getDoIntentActionIfDisableByAppGoingBackground() {
        return doIntentActionIfDisableByAppGoingBackground;
    }

    public void setDoIntentActionIfDisableByAppGoingBackground(IntentActionName doIntentActionIfDisableByAppGoingBackground) {
        this.doIntentActionIfDisableByAppGoingBackground = doIntentActionIfDisableByAppGoingBackground;
    }

    /**
     * @return true the chat is started and current state is active conversation or pending (waiting for agent)
     */
    public boolean isChatConnected() {
        return isChatConnected;
    }

    /**
     * Set curent chat state.
     *
     * @param chatConnected can be true if chat state is active conversation or pending (waiting for agent)
     *                      can be false if chat is not started.
     */
    public void setChatConnected(boolean chatConnected) {
        isChatConnected = chatConnected;
    }

    /**
     * @return true if it is an open conversation with agent (no pending status)
     * false if it is not an open conversation (inactive chat or pending status)
     */
    public boolean isConversationOpen() {
        return isConversationOpen;
    }

    /**
     * @param conversationOpen
     */
    public void setConversationOpen(boolean conversationOpen) {
        this.isConversationOpen = conversationOpen;
    }

    public boolean isLibrariesInitialized() {
        return librariesInitialized;
    }

    public boolean isUserInitialized() {
        return VodafoneController.getInstance().getUser() != null;
    }

    public ChatService getChatService() {
        return chatService;
    }

    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    public StartChatRequest getChatRequest() {
        return chatRequest;
    }

    public void setChatRequest(StartChatRequest chatRequest) {
        this.chatRequest = chatRequest;
    }

    public boolean getStoreLocatorAplicationFreshInstalled() {
        return storeLocatorAplicationFreshInstalled;
    }

    public void setStoreLocatorAplicationFreshInstalled(boolean flag) {
        storeLocatorAplicationFreshInstalled = flag;
    }

    public boolean getDisplayGoToSettingsForPermissionFlag() {
        return displayGoToSettingsForPermissionFlag;
    }

    public void setDisplayGoToSettingsForPermissionFlag(boolean flag) {
        displayGoToSettingsForPermissionFlag = flag;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
        updateAppBadge();
    }

    public int incrementBadgeCount() {
        return badgeCount++;
    }

    public void setBadgeCountFromChatBubble() {
        this.badgeCount = ChatBubbleSingleton.getInstance().getMessagesCount();
    }

    private void updateAppBadge() {
        if (this.badgeCount == 0) {
            VodafoneNotificationManager.clearBadgeCount(this);
        } else {
            VodafoneNotificationManager.applyCount(this, badgeCount);
        }
    }

    public VoiceOfVodafoneResponse getVoiceOfVodafoneResponse() {
        return voiceOfVodafoneResponse;
    }

    public void setVoiceOfVodafoneResponse(VoiceOfVodafoneResponse voiceOfVodafoneResponse) {
        this.voiceOfVodafoneResponse = voiceOfVodafoneResponse;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public boolean isAppComesFromBackground() {
        return isAppComesFromBackground;
    }

    public void setAppComesFromBackground(boolean appComesFromBackground) {
        isAppComesFromBackground = appComesFromBackground;
    }

    public boolean isFromBackPress() {
        return isFromBackPress;
    }

    public void setFromBackPress(boolean fromBackPress) {
        isFromBackPress = fromBackPress;
    }

    public boolean isWasScreenOn() {
        return wasScreenOn;
    }

    public void setWasScreenOn(boolean wasScreenOn) {
        this.wasScreenOn = wasScreenOn;
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new HelloWorldPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }



}
