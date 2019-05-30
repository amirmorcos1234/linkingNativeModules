package ro.vodafone.mcare.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adobe.mobile.Config;
import com.adobe.mobile.Visitor;
import com.adobe.mobile.VisitorID;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.UrbanAirshipController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubbleSingleton;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.DashboardCommunicatorListener;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessFlag;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.NotificationChannels;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.InterfaceUserSelectedMsisdnBan;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.creditplus.CreditPlusWidgetController;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;


public class DashboardActivity extends MenuActivity implements InterfaceUserSelectedMsisdnBan, DashboardCommunicatorListener {

    public static String TAG = "DashboardActivity";
    public static int flag;
    public static String ANIMATE_KEY = "Animate";
    public static Logger LOGGER = Logger.getInstance(DashboardActivity.class);
    public static int RESULT_TUTORIAL = 20;
    public AuthenticationService authenticationService;
    ImageView splashScreenView;
    RechargeService rechargeService;
    boolean isVisibledashboardActivity = false;
    private boolean shouldDisplayToturial = false;
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Realm realm;

    private SwipeRefreshLayout pull2Refresh;
    private Date lastPull2RefreshTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
        realm = Realm.getDefaultInstance();

        VodafoneController.getInstance().setDashboardActivity(this);
        rechargeService = new RechargeService(getApplicationContext());

        initViews();

        initializeFragment();

        bringToFrontVodafoneIconClickListener();

        initTutorial();

        checkMobileOperator();

        checkAPI19FailedFlag();

        authenticationService = new AuthenticationService(getApplicationContext());
        authenticationService.getSeamlessFlag().subscribe(new RequestSessionObserver<GeneralResponse<SeamlessFlag>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onNext(GeneralResponse<SeamlessFlag> response) {
//                D.w();
                if (response.getTransactionSuccess() != null) {
                    VodafoneController.setApi9Failed(response.getTransactionStatus() == 2);

                    boolean seamlessFlag = response.getTransactionSuccess().getSeamlessFlag();
                    ((AppConfiguration) RealmManager.getRealmObject(realm, AppConfiguration.class))
                                                    .setAllowSeamless(seamlessFlag);
                } else {
                    VodafoneController.setApi9Failed(true);
                    D.e("response.getTransactionSuccess() == NULL");
                }
            }

            @Override
            public void onError(Throwable e) {
                D.e("error : " + e);
                super.onError(e);
                VodafoneController.setApi9Failed(true);
            }
        });

        UrbanAirshipController.configureUrbanAirship();

        displayChat();
        chatBubble.displayBubble();

        configureAdobeTarget();

        NotificationChannels.createDownloadBillNotificationChannel(this);
		NotificationChannels.createChatNotificationChannel(this);
    }

    private void configureAdobeTarget() {
        UserProfile userProfile = (UserProfile) RealmManager.getUnManagedRealmObject(realm, UserProfile.class);
        String vfPhoneNumber = userProfile.getMsisdn();
        StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();

        String encryptedMsisdn = null;
        try {
            encryptedMsisdn = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(vfPhoneNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String identifierType = "vodafonerocrmdata";
        String identifier = encryptedMsisdn;
        Visitor.syncIdentifier(identifierType, identifier,
                VisitorID.VisitorIDAuthenticationState.VISITOR_ID_AUTHENTICATION_STATE_AUTHENTICATED);

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        callForAdobeTarget(AdobePageNamesConstants.PG_DASHBOARD);
                    }
                });

    }

    private void setDashboardTealiumTracking() {
        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.dashboard);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.dashboard);
        final UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        if (userProfile != null && userProfile.getUserRole() != null)
            tealiumMapView.put(TealiumConstants.user_type, userProfile.getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.voiceOfVodafone);
        tealiumMapEvent.put(TealiumConstants.journey_name,TealiumConstants.dashboard);
        tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapEvent);
        DashboardTrackingEvent event = new DashboardTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDashboardTealiumTracking();
        UserDataController.getInstance().startDashboardAction(this);
    }

    private void displayChat() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("openChat")) {
            VodafoneController.getInstance().destroySupportWindow();
            if (!VodafoneController.getInstance().supportWindow(this).isShowing()) {
                findViewById(R.id.drawer_layout).post(new Runnable() {
                    public void run() {
                        VodafoneController.getInstance().supportWindow(DashboardActivity.this).show(SupportWindow.DisplayType.CHAT);
                    }
                });
            }
        }
    }

    @Override
    public void finish() {
        D.printCallers();
        D.e("FINNISH DASH");

        super.finish();
    }

    private void initTutorial() {

        boolean checkedTutorialFlag = VodafoneController.getInstance().getAppConfiguration().isTutorialFlag();

        if ((VodafoneController.getInstance().getUser() != null) && (VodafoneController.getInstance().getUserProfile() != null))
            if (!checkedTutorialFlag && VodafoneController.getInstance().getUserProfile().getUserRole() != UserRole.NON_VF_USER) {
                //hardcoded delay, should be taken from labels
                int configurableTimeDelay = 9000;
                try {
                    //flag = prefs.getInt("tutorial_access_no", 0); //returns 0 if preference doesn't exist
                    //flag = EncryptedSharedPreferences.getInt(prefs,"tutorial_access_no",0);
                    //Log.d(TAG, "Tutorial flag found is: " + flag + ", and configurable number for showing tutorial is: " + tutorialNumber);
                    //if (flag < tutorialNumber) {
                    Log.d(TAG, "Showing tutorial...");
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            if (VodafoneController.getInstance().getUser() != null) {
                                if (VodafoneController.isActivityVisible()) {
                                    if (isVisibledashboardActivity) {
                                        startTutorialActivity();
                                    }
                                } else {
                                    shouldDisplayToturial = true;
                                }
                            }
                        }
                    };
                    Handler h = new Handler();
                    // The Runnable will be executed after the given delay time
                    h.postDelayed(r, configurableTimeDelay); // will be delayed for X seconds
                    saveFlag();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void startTutorialActivity() {
        SharedPreferences pref = getSharedPreferences("TutorialFirstRun", Context.MODE_PRIVATE);
        if(pref.getBoolean("tutorial_first_run", true)) {
            User user = VodafoneController.getInstance().getUser();
            if (!(user instanceof EbuMigrated)
                    && !(user instanceof EbuNonMigrated)
                    && !(user instanceof SeamlessPrepaidUser)
                    && !(user instanceof SeamlessPrepaidHybridUser)
                    && !(user instanceof SeamlessPostPaidsLowAccess)
                    && !(user instanceof SeamlessPostPaidHighAccess)) {
                closeDrawers();
                startActivityForResult(new Intent(DashboardActivity.this, TutorialActivity.class), RESULT_TUTORIAL);
            }
        }
    }

    private void saveFlag() {
        flag++;
        // VodafoneController.getInstance().getAppConfiguration().setTutorialFlagTrue();
        Log.d(TAG, "starting saveFlag() with tutorial flag = " + flag);
        //EncryptedSharedPreferences.putInt(prefs,"tutorial_access_no", flag);
        //Log.d(TAG, "ending saveFlag() with tutorial flag = " + flag);
    }

    @Override
    protected int setContent()
    {
        return R.layout.content_dashboard;
    }

    private void initViews() {
        splashScreenView = (ImageView) super.getContent().findViewById(R.id.splashscreen);

        pull2Refresh = findViewById(R.id.pull2Refresh);
        pull2Refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        pull2Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (lastPull2RefreshTime != null && new Date().getTime() < lastPull2RefreshTime.getTime() + 5000)
                {
                    pull2Refresh.setRefreshing(false);
                    return;
                }

                reload();
            }
        });
    }

    @Override
    public void stopSwipeRefreshAnimation()
    {
        pull2Refresh.setRefreshing(false);

        lastPull2RefreshTime = new Date();
    }

    @Override
    protected void onResume() {
        //call before super
        RealmManager.checkNumberOfRealmInstances();
        if(VodafoneController.getInstance().getUser() == null) {
            new NavigationAction(this).finishCurrent(true).startAction(IntentActionName.LOGOUT);
            return;
        }
        super.onResume();
        if(realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        isVisibledashboardActivity = true;
        UserSelectedMsisdnBanController.getInstance().setChangedBanMsisdnListener(this);

        if (DashboardController.shouldReloadDashboardOnResume()) {
            reload();
        } else {
            refresh();
        }

        if (!VodafoneController.getInstance().getAppConfiguration().isTutorialFlag() && shouldDisplayToturial) {
            startTutorialActivity();
            shouldDisplayToturial = false;
        }

        getToolbar().showDashboardToolBar();
        DashboardController.registerAtTheEndOfResumeAction();

        ChatBubbleSingleton.getInstance().setCountListener(this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        realm.close();
        isVisibledashboardActivity = false;
        Config.pauseCollectingLifecycleData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VodafoneController.getInstance().clearDashboard();
    }

    public void load() {
        initFragment(false);
    }

    public void reload() {
        cancelSubscriptions();
        cleanRealmData();
        CostControlWidgetController.deleteInstance();

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser)
            CreditPlusWidgetController.getInstance().reloadRefreshBalance();

        VoiceOfVodafoneController.getInstance().cleanSubcribers();

        if(!(VodafoneController.getInstance().getUser() instanceof SeamlessEbuUser)
                && VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            reloadForEbu();
        }else{
            initFragment(false);}
    }

    private void cancelSubscriptions(){
        compositeSubscription.clear();
    }

    public void reloadForEbu(){
        showLoadingDialog();
        Observable identityCallsObservable = EbuMigratedIdentityController.getInstance().reloadIdentityCalls();
        Subscription subscription = identityCallsObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                stopLoadingDialog();
                reloadMenu();

                initFragment(false);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(DashboardActivity.this).message(AppLabels.getToastErrorSomeInfoNotLoaded()).success(false).show();
                EbuMigratedIdentityController.setEbuMigratedUserFailedCase();
                reloadMenu();
                initFragment(false);
            }

            @Override
            public void onNext(GeneralResponse response) {
                if (ResponseValidatorUtils.isValidGeneralRealmResponse(response)) {
                    super.onNext(response);
                    EbuMigratedIdentityController.setEbuMigratedUserSuccesCase();
                }
            }
        });
        compositeSubscription.add(subscription);
        addToActivityCompositeSubcription(subscription);
    }

    public void refresh(){
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            CreditPlusWidgetController.getInstance().refresh();
        }
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }

    public void refreshAvatarWidget() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_layout);
        if (currentFragment != null && currentFragment instanceof BaseDashboardFragment) {
            BaseDashboardFragment fragment = (BaseDashboardFragment) currentFragment;
            fragment.refresh();
            return;
        }
        reload();
    }

    private void initFragment(boolean animate) {
        try {
            BaseDashboardFragment fragment = VodafoneController.getInstance().getUser().getDashboardFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(DashboardActivity.ANIMATE_KEY, animate);
            fragment.setArguments(bundle);
            fragment.setDashboardCommunicatorListener(this);

            android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.fragment_layout, fragment, FragmentUtils.getTagForFragment(fragment));
            t.commitAllowingStateLoss();
        } catch (Exception ex) {
            this.finish();
            LOGGER.e("Unknown exception initFragment", ex);
        }
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawers()) {
            moveTaskToBack(true);
        }
    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {}

    public RechargeService getService() {
        return rechargeService;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                VoiceOfVodafoneController.getInstance().cleanSubcribers();
                VoiceOfVodafoneController.getInstance().clearVoiceOfVodafone();
                CostControlWidgetController.getInstance().destroy();
                cleanRealmData();
                initFragment(true);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == RESULT_TUTORIAL && resultCode == RESULT_OK)
            chatBubble.displayBubble();
    }

    public void cleanRealmData() {
        RealmManager.delete(realm, CostControl.class);
        RealmManager.delete(realm, BalanceCreditSuccess.class);
        RealmManager.delete(realm, BalanceSecondarySuccess.class);
        RealmManager.delete(realm, EligibleOffersPostSuccess.class);
        RealmManager.delete(realm, EligibleOffersSuccess.class);
        RealmManager.delete(realm, InvoiceDetailsSuccess.class);
        EbuMigratedIdentityController.getInstance().cleanEntityData();
    }

    public void bringToFrontVodafoneIconClickListener() {

        final View reloadButton = new View(this);
        reloadButton.setId(R.id.top_up_programed_tab_container);
        addContentView(reloadButton, new RelativeLayout.LayoutParams(ScreenMeasure.dpToPx(80), ScreenMeasure.dpToPx(60)));

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawers();
                reload();
                reloadButton.setEnabled(false);

                callForAdobeTarget(AdobePageNamesConstants.PG_DASHBOARD);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reloadButton.setEnabled(true);
                    }
                }, 3500);
            }
        });

        menuScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > ScreenMeasure.dpToPx(60)) {
                    reloadButton.setVisibility(View.GONE);
                } else {
                    reloadButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void initializeFragment() {
        initFragment(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initializeFragment();
    }

    @Override
    public void onBanChanged() {}

    @Override
    public void onSubscriberChanged() {
        //callForAdobeTarget(AdobePageNamesConstants.PG_DASHBOARD);
    }

    public static class DashboardTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "dashboard";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "dashboard");


            s.channel = "dashboard";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void checkMobileOperator() {
        loadCountries();
    }

    private void checkAPI19FailedFlag() {
        int errorDetectedFlag = getSharedPreferences("API19", MODE_PRIVATE).getInt("errorDetectedFlag", 0);
        if (VodafoneController.getInstance().getUser() instanceof CBUUser && errorDetectedFlag == 2)
            VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
    }

    public void loadCountries() {
        if(getApplicationContext()==null){
            return;
        }
        boolean isEbuNonSeamless = false;
        User user = VodafoneController.getInstance().getUser();
        if(!(user instanceof SeamlessEbuUser) && user instanceof EbuMigrated) {
            isEbuNonSeamless = true;
        }
        Subscription subscription = new TravellingAboardService(getApplicationContext())
                .getRoamingTariffs(VodafoneController.getInstance().getUser() instanceof PrepaidUser, isEbuNonSeamless)
                .subscribe((new RequestSaveRealmObserver<GeneralResponse<RoamingTariffsSuccess>>() {

                    @Override
                    public void onNext(GeneralResponse<RoamingTariffsSuccess> roamingTariffsSuccess) {
                        if (roamingTariffsSuccess.getTransactionSuccess() != null) {

                            super.onNext(roamingTariffsSuccess);

                            checkMobileOperatorAndDisplayVov();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));

        //compositeSubscription.add(subscription);
    }

    public void checkMobileOperatorAndDisplayVov() {

        CountryByIp countryByIp = (CountryByIp) RealmManager.getRealmObject(realm, CountryByIp.class);
        if (countryByIp != null) {

            String countryCode = countryByIp.getCountryCode();
            Log.d("testCountry", "");
            RoamingTariffsSuccess roamingTariffsSuccess = (RoamingTariffsSuccess) RealmManager.getRealmObject(RoamingTariffsSuccess.class);
            CountryList countryList = countryList(roamingTariffsSuccess, countryByIp.getCountryCode());
            if (!"RO".equals(countryCode) && countryCode != null) {
                VoiceOfVodafoneController.getInstance().pushStashToView(new VoiceOfVodafone(23, 20, VoiceOfVodafoneCategory.Travelling_abroad, null, "Eşti acum în " + countryList.getCountryName() + "!", "Ok", "Mai multe detalii", true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.RedirectToRoaming));
                VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
            }
        }
    }

    public CountryList countryList(RoamingTariffsSuccess roamingTariffsSuccess, String countyIsoCode) {
        CountryList countryList = null;
        if (roamingTariffsSuccess != null) {
            countryList = roamingTariffsSuccess.getCountryList()
                    .where().equalTo(CountryList.COUNTRY_CODE_ISO, countyIsoCode).findFirst();
        }
        return countryList;
    }
}
