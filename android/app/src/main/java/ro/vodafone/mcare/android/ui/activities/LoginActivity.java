package ro.vodafone.mcare.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adobe.mobile.Config;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SeamlessLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.login.ActivateAccountFragment;
import ro.vodafone.mcare.android.ui.fragments.login.LoginFragment;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessLoginFragment;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.WifiSettingsFragment;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Go;
import ro.vodafone.mcare.android.utils.NetworkReceiver;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;

public class LoginActivity extends BaseActivity implements NetworkReceiver.NetworkEventReceiver{

    public static String TAG = "LoginActivity";

    public static final String FRAGMENT= "fragment";
    public static final String LOGIN_FRAGMENT = "LoginFragment";
    public static final String SEAMLESS_LOGIN_FRAGMENT = "SeamlessLoginFragment";
    public static final String COMMAND = "command";
    public static final String DISPLAY_PRE_LOGIN_PAGE_COMMAND = "displayPreLoginPage";
    public static final String SEAMLESS_LOGIN_COMMAND = "callSeamlessLoginServices";
    public static final String CHECK_MOBILE_PROVIDER_COMMAND = "checkMobileProviderCommand";

    private NetworkReceiver networkReceiver;
    private VodafoneDialog vodafoneDialog;

    //private ImageView logo;
    private ImageView backButton;

    private VodafoneTextView title;
    public String newPassword;

    private Map<String, Object> lifecycleData = null;
    private Map<String, Object> acquisitionData = null;

    private boolean networkEventReceived = false;

    VodafoneDialog vodafone2Dialog = null;

    @BindView(R.id.splashscreen)
    ImageView splashScreenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_login);
        setVodafoneBackgroundOnWindowIfWindowBackgroundNotSetted();

        //networkReceiver = NetworkReceiver.registerReceiver(this);

        //Tealium Track view
        /*Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","choose login type");
        tealiumMapView.put("journey_name","Login");
        TealiumHelper.trackView("screen_name", tealiumMapView);*/

        // Adobe Analytics
        // Allow the SDK access to the application context
        Config.setContext(this.getApplicationContext());
        Config.setDebugLogging(BuildConfig.ADOBE_DEBUG_CONFIG);

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


        UserDataController.getInstance().startAction();

        ButterKnife.bind(this);

        title = (VodafoneTextView) findViewById(R.id.title);

        backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LoginTrackingEvent event = new LoginTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getFragmentFromIntent();
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void getFragmentFromIntent(){
        Log.d(TAG, "getFragmentFromIntent()");
        //get fragment to display from intent
        Intent intent = getIntent();
        String fragment = intent.getStringExtra(FRAGMENT);

        if(fragment != null){
            switch (fragment){
                case LOGIN_FRAGMENT:
                    this.displayLoginFragment();
                    break;
                case SEAMLESS_LOGIN_FRAGMENT:
                    this.callSeamlessLoginFragmentServices(SEAMLESS_LOGIN_COMMAND);
                    break;
                case DISPLAY_PRE_LOGIN_PAGE_COMMAND:
                    this.callSeamlessLoginFragmentServices(DISPLAY_PRE_LOGIN_PAGE_COMMAND);
                    break;
            }
        }else{
            //display login fragment by default
            displayLoginFragment();
        }
    }

    public void callSeamlessLoginFragmentServices(String command){
        Log.d(TAG, "callSeamlessLoginFragmentServices()");

        splashScreenView.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(COMMAND, command );

        SeamlessLoginFragment seamlessLoginFragment = new SeamlessLoginFragment();
        seamlessLoginFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, seamlessLoginFragment, FragmentUtils.getTagForFragment(seamlessLoginFragment))
                .addToBackStack(null)
                .commit();
    }

    public void displaySeamlessLoginFragment(){
        try {
            Log.d(TAG, "displaySeamlessLoginFragment()");
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "choose login type");
            tealiumMapEvent.put("event_name", "mcare:choose login type:button:login seamless");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);
*/
            title.setText(AppLabels.getSeamlessPageTitle());
            title.setVisibility(View.VISIBLE);

            splashScreenView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);

            Bundle bundle = new Bundle();
            bundle.putString(COMMAND, DISPLAY_PRE_LOGIN_PAGE_COMMAND);

            SeamlessLoginFragment seamlessLoginFragment = new SeamlessLoginFragment();
            seamlessLoginFragment.setArguments(bundle);


            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                    .replace(R.id.fragment_container, seamlessLoginFragment, FragmentUtils.getTagForFragment(seamlessLoginFragment))
                    .addToBackStack(null)
                    .commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void displayWifiSettingsFragment(){
        Log.d(TAG, "displayWifiSettingsFragment()");

        title.setText(SeamlessLabels.getLoginEasierTitle());
        title.setVisibility(View.VISIBLE);

        splashScreenView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);

        final WifiSettingsFragment fragment = new WifiSettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void displayLoginFragment(){
        Log.d(TAG, "displayLoginFragment()");
        try {
  /*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "choose login type");
            tealiumMapEvent.put("event_name", "mcare:choose login type:button:login");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);
*/
            title.setText(LoginLabels.getLoginToMyVodafone());
            title.setVisibility(View.VISIBLE);

            splashScreenView.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);

            final LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                    .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                    .addToBackStack(null)
                    .commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void displayActivateAccountFragment(){
        Log.d(TAG, "displayLoginFragment()");

        title.setText(AppLabels.getActivateAccountPageTitle());
        title.setVisibility(View.VISIBLE);

        splashScreenView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);

        final ActivateAccountFragment fragment = new ActivateAccountFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        KeyboardHelper.hideKeyboard(this);

        if(FragmentUtils.getVisibleFragment(this, false) instanceof ActivateAccountFragment){
            loadLogOutDialog();
        }else if(FragmentUtils.getVisibleFragment(this, false) instanceof LoginFragment){
            if(NetworkUtils.isIOYM(getApplicationContext())){
                displaySeamlessLoginFragment();
            }else{
                AppConfiguration appConfiguration = VodafoneController.getInstance().getGeneralAppConfiguration();
                if(appConfiguration!=null && appConfiguration.getPreviousMsisdn()!=null){
                    displaySeamlessLoginFragment();
                }else{
                    displaySeamlessLoginFragment();
                    vodafoneDialog = new VodafoneDialog(this, AppLabels.getPreloginOtherNetwork())
                            .setOnePositiveButton(getResources().getString(R.string.dialog_continue))
                            .setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    vodafoneDialog.dismiss();
                                    displayLoginFragment();
                                }
                            });
                    vodafoneDialog.setCancelable(false);
                    vodafoneDialog.show();
                }
            }
        }else if(FragmentUtils.getVisibleFragment(this, false) instanceof SeamlessLoginFragment){
            Go.minimizeApp(this);
        }
    }

    public static void clearFile(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tealiumlogs.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    void loadLogOutDialog(){
        Log.d(TAG, "loadLogOutDialog()");
        vodafone2Dialog= new VodafoneDialog(this,getString(R.string.activate_account_dialog_logout_message))
                .setPositiveMessage(getString(R.string.activate_account_dialog_logout_negative))
                .setNegativeMessage(getString(R.string.activate_account_dialog_logout_positive))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Negative2");
                        vodafone2Dialog.dismiss();

                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Positive2");
                        vodafone2Dialog.dismiss();

                        clearFile(VodafoneController.getInstance().getApplicationContext());

                        new AuthenticationService(VodafoneController.getInstance()).logout().subscribe(new RequestSessionObserver<GeneralResponse>() {
                            @Override
                            public void onCompleted() {
                                Log.d("logout","completed");
                                UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT).startAction();
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                Log.d("logout","onError "+e.getMessage());
                                displayLoginFragment();
                                UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT).startAction();
                            }

                            @Override
                            public void onNext(GeneralResponse generalResponse) {
                                Log.d("logout","onNext " + generalResponse.getTransactionStatus());
                                displayLoginFragment();
                            }
                        });
                    }
                });
        vodafone2Dialog.setCancelable(true);
        vodafone2Dialog.show();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        Config.collectLifecycleData(this);
        // -or-Config.collectLifecycleData(this, contextData);
        //Analytics.trackState("Login", null);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        overridePendingTransition(0, 0);
        Config.pauseCollectingLifecycleData();
    }


    @Override
    public void onreceive(NetworkInfo networkInfo) {
          if(!NetworkUtils.isWifiConnection(this) && NetworkUtils.isMobileNetworkConnection(this)
                  && FragmentUtils.getVisibleFragment(this, false) instanceof WifiSettingsFragment
                  && !networkEventReceived){

              Log.d(TAG, "Wifi = off && Mobile Network = On ");
              LoginActivity loginActivity = (LoginActivity)VodafoneController.findActivity(LoginActivity.class);
              if(loginActivity != null){
                  networkEventReceived = true;
                  loginActivity.callSeamlessLoginFragmentServices(CHECK_MOBILE_PROVIDER_COMMAND);
              }
        }
    }

    public static class LoginTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "login page";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"login page");


            s.prop22 = s.pageName;
            s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_3, "mcare:"+"login page");
            s.prop23 = s.prop22;
            s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_4, "mcare:"+"login page");
            s.channel = "Login";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone login";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
        }
    }
}
