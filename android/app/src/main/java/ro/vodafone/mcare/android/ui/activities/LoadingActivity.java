package ro.vodafone.mcare.android.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.security.cert.CertificateException;
import java.util.Date;

import javax.net.ssl.SSLHandshakeException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmModel;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.Configurations;
import ro.vodafone.mcare.android.client.model.realm.system.Labels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.Maintenance;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.SystemStatusSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.VersionApp;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.ResourcesService;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.CacheUtils;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.SettingUtils;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;
import rx.Subscription;

public class LoadingActivity extends BaseActivity {

    public static String TAG = LoadingActivity.class.getSimpleName();
    Dialog overlyDialog;

    VodafoneDialog vodafoneDialog = null;
    private boolean newestLabels=false,newestConfigurations=false;
    Runnable runnable;
    private boolean nextActivityStarted=false;

    @BindView(R.id.loadingTextView)
    AutoResizeTextView loadingTextView;

    @BindView(R.id.loadingContainer)
    LinearLayout loadingContainer;

    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        setVodafoneBackgroundOnWindow();

        setDisplayText(false);

        checkInternet();
    }



    private void checkInternet(){

        if(!NetworkUtils.isNetworkConnected(this)){
            vodafoneDialog= new VodafoneDialog(this,getString(R.string.dialog_no_internet_connection_message))
                    .setPositiveMessage(getString(R.string.dialog_no_internet_connection_positive))
                    .setNegativeMessage(getString(R.string.dialog_no_internet_connection_negative))
                    .setNegativeAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SettingUtils.openNetworkSettings(LoadingActivity.this);
                        }
                    })
                    .setPositiveAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            vodafoneDialog.dismiss();
                            //loadData(1000, null);
                            checkInternet();
                        }
                    });
            vodafoneDialog.setCancelable(false);
            vodafoneDialog.show();
        }else{
                getSystemStatus();
                getCountryNameByIp();
        }
    }

    private void continueToNextActivity(){
        nextActivityStarted =true;
        CacheUtils cacheUtils = new CacheUtils(getApplicationContext());

        System.out.println("isAcceptedTermsAndConditions "+cacheUtils.isAcceptedTermsAndConditions());
        //Todo also need to check if T&C changed
        if(cacheUtils.checkFirstRunOrUpdate() || !cacheUtils.isAcceptedTermsAndConditions()){
            Intent mainIntent = new Intent(LoadingActivity.this,OnboardingActivity.class);
            //mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            //loadData(1000, mainIntent);
            startActivity(mainIntent);
            finish();
        }else{
            Intent mainIntent = new Intent(LoadingActivity.this,LoginActivity.class);
            mainIntent.putExtra(LoginActivity.FRAGMENT, LoginActivity.SEAMLESS_LOGIN_FRAGMENT);
           // mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //loadData(1000, mainIntent);
            startActivity(mainIntent);
            finish();
        }
    }

    private void setDisplayText(boolean visible) {
        Log.d(TAG, "setDisplayText: ");
        if (!visible) {
            loadingContainer.setVisibility(View.GONE);
            return;
        }
        loadingContainer.setVisibility(View.VISIBLE);
        RealmModel currentLabels = RealmManager.getRealmObject(realm, Labels.class);
        RealmModel currentConfigurations = RealmManager.getRealmObject(realm, Configurations.class);
        if (currentLabels == null || currentConfigurations == null) {
            loadingTextView.setText(R.string.onboarding_configuring_text);
        } else {
            loadingTextView.setText(AppLabels.getLoadingSpinnerOneMoment());
        }
    }

    private void getData() {
        setDisplayText(true);
        ResourcesService resourcesService = new ResourcesService(this);
       Subscription subscriptionLabels = resourcesService.retrieveLabels().subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

                                                        @Override
                                                        public void onCompleted() {
                                                            newestLabels = true;

                                                            if(isDataReady() && !nextActivityStarted){
                                                                continueToNextActivity();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            super.onError(e);
                                                            try {
                                                                newestLabels = true;
                                                                //TODO Thow other exceptions if needed. Toast the error after codes. Use a treatment class parsing class context.
                                                                if(isDataReady() && !nextActivityStarted){
                                                                    continueToNextActivity();
                                                                }

                                                            }catch (Exception e1){
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }
        );
        activityCompositeSubcription.add(subscriptionLabels);
        Subscription configsSubcription = resourcesService.retrieveConfigurations().subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {
            @Override
            public void onCompleted() {
                newestConfigurations = true;

                if(isDataReady()){
                    continueToNextActivity();
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                newestConfigurations = true;
                try {
                    if(isDataReady()){
                        continueToNextActivity();
                    }
                }catch (Exception e1){
                    e.printStackTrace();
                }
            }
        });
        activityCompositeSubcription.add(configsSubcription);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //For switching activities without animation
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if (overlyDialog != null) {
            overlyDialog.dismiss();
            overlyDialog = null;
        }
        if (vodafoneDialog != null) {
            vodafoneDialog.dismiss();
            vodafoneDialog = null;
        }
    }

    private boolean isDataReady(){
        return newestConfigurations && newestLabels;
    }

    protected void displayServerDownOverlay(final Maintenance maintenanceBean) {

        final Dialog overlyDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_system_down);
        overlyDialog.show();

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
        overlayDismissButton.setVisibility(View.INVISIBLE);

        Button buttonTurnOFF = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView description = (VodafoneTextView) overlyDialog.findViewById(R.id.description);
        final LinearLayout txtTimerLayout = (LinearLayout) overlyDialog.findViewById(R.id.txtTimerLayout);
        final VodafoneTextView txtTimerMinute = (VodafoneTextView) overlyDialog.findViewById(R.id.txtTimerMinute);
        final VodafoneTextView txtTimerSecond = (VodafoneTextView) overlyDialog.findViewById(R.id.txtTimerSecond);
        final VodafoneTextView txtTimerHour = (VodafoneTextView) overlyDialog.findViewById(R.id.txtTimerHour);

        overlayTitle.setText(Html.fromHtml(maintenanceBean.getTitle()).toString());
        description.setText(Html.fromHtml(maintenanceBean.getSummary()).toString());
        buttonTurnOFF.setText(Html.fromHtml(maintenanceBean.getPrimaryButtonLabel()).toString());

        countDownStart(maintenanceBean.getUpTime(), txtTimerLayout, txtTimerMinute, txtTimerSecond, txtTimerHour);

        buttonTurnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
                overlyDialog.dismiss();
            }
        });

        overlyDialog.setCancelable(false);
        overlyDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_BACK){
                    finish();
                    System.exit(0);
                }
                return true;
            }
        });
    }

    public void getCountryNameByIp(){
        TravellingAboardService travellingAboardService = new TravellingAboardService(getBaseContext());
        travellingAboardService.getCountryListJson().subscribe(new RequestSaveRealmObserver<CountryByIp>() {
            @Override
            public void onNext(CountryByIp countryByIpSuccessGeneralResponse) {
                super.onNext(countryByIpSuccessGeneralResponse);
                }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
            }
        });
    }

    public void getSystemStatus() {

        final String version = BuildConfig.VERSION_NAME;
        ResourcesService resourcesService = new ResourcesService(this);
        setDisplayText(true);
        resourcesService.getSystemStatus().subscribe(new RequestSaveRealmObserver<GeneralResponse<SystemStatusSuccess>>(false) {
            @Override
            public void onNext(GeneralResponse<SystemStatusSuccess> systemStatusSuccessGeneralResponse) {
                super.onNext(systemStatusSuccessGeneralResponse);
                Log.d(TAG, "getSystemStatus() - onNext()");

                if (systemStatusSuccessGeneralResponse.getTransactionStatus() == 0 && systemStatusSuccessGeneralResponse.getTransactionSuccess() != null) {
                    Log.d("getSystemStatus", "getSystemStatusSuccess");
                    Maintenance maintenanceBean;
                    VersionApp versionApp;
                    maintenanceBean = systemStatusSuccessGeneralResponse.getTransactionSuccess().getMaintenance();
                    Log.d(TAG, "maintenanceBeanList  is : " + maintenanceBean.toString());
                    versionApp = systemStatusSuccessGeneralResponse.getTransactionSuccess().getVersionApp();
                    Log.d(TAG, "versionApp  is : " + versionApp.toString());

                    if (maintenanceBean != null && maintenanceBean.getIsServerDown()) {
                        Log.d(TAG, "displayServerDownOverlay");
                        displayServerDownOverlay(maintenanceBean);
                    } else if (versionApp.compareWithCurrentAppVersion(version) > 0) {
                        Log.d(TAG, "displayUpgradeApp");
                        displayUpgradeApp(versionApp);
                    } else {
                        Log.d(TAG, "getSystemStatus");
                        getData();
                    }
                } else {
                    getData();
                    Log.d(TAG, "getSystemStatus");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("TAG", "getSystemStatus onError");
                if (e instanceof CertificateException || e instanceof SSLHandshakeException) {
                    try {
                        displayStaticUpgradeApp();
                    } catch (Exception e1) {
                    }
                } else {
                    getData();
                }
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "getSystemStatus onCompleted");
            }
        });

    }


    protected void displayUpgradeApp(final VersionApp versionApp) {

        overlyDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonTurnOff = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);
        Button buttonKeepOn = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);
        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        overlayTitle.setText(Html.fromHtml(versionApp.getTitle()).toString());
        overlaySubtext.setText(Html.fromHtml(versionApp.getDescription()).toString());
        buttonKeepOn.setText(Html.fromHtml(versionApp.getPrimaryButtonLabel()).toString());
        buttonTurnOff.setText(Html.fromHtml(versionApp.getSecondaryButtonLabel()).toString());

        Log.d(TAG, "versionApp.getIsRequired() is :" + versionApp.getIsRequired());
        if(versionApp.getIsRequired()){
            overlayDismissButton.setVisibility(View.INVISIBLE);
            buttonTurnOff.setVisibility(View.VISIBLE);
        }else{
            buttonTurnOff.setVisibility(View.INVISIBLE);
        }

        buttonKeepOn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(versionApp.getStoreURL()));
                startActivity(viewIntent);
            } catch (Exception e) {
                Log.d(TAG,"error in redirect to store");
                e.printStackTrace();
            }
        }
    });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
                overlyDialog.dismiss();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
                getData();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
                System.exit(0);
                overlyDialog.dismiss();
            }
        });

    }

    protected void displayStaticUpgradeApp() {

        overlyDialog = new Dialog(LoadingActivity.this, android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonTurnOff = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);
        Button buttonKeepOn = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);
        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        overlayTitle.setText(Html.fromHtml( "Aplica&#x21b;ia necesit&#x103; update"));
        overlaySubtext.setText(Html.fromHtml("Pentru a ne asigura c&#x103; ai parte de cea mai bun&#x103; experien&#x21b;&#x103; folosind aplica&#x21b;ia My Vodafone, este nevoie s&#x103; actualizezi aplica&#x21b;ia la ultima versiune."));
        buttonKeepOn.setText(Html.fromHtml("Intr&#x103; &#xee;n Play Store"));
        buttonTurnOff.setText(Html.fromHtml("Ie&#x219;i din aplica&#x21b;ie"));

        buttonKeepOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=ro.vodafone.mcare.android"));
                    startActivity(viewIntent);
                } catch (Exception e) {
                    Log.d(TAG, "error in redirect to store");
                    e.printStackTrace();
                }
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
                overlyDialog.dismiss();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
                getData();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
                System.exit(0);
                overlyDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    public void countDownStart(final Long timeSend, final LinearLayout txtTimerLayout, final VodafoneTextView txtTimerMinute,
                               final VodafoneTextView txtTimerSecond, final VodafoneTextView txtTimerHour){

        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    // Here Set your Event Date
                    Date timeFromServer = new Date(timeSend);
                    Date currentDate = new Date();
                    if (timeFromServer.after(currentDate)) {
                        long diff = timeFromServer.getTime() - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        hours += days * 24;
                        txtTimerHour.setText("" + String.format("%02d", hours));
                        txtTimerMinute.setText("" + String.format("%02d",  minutes));
                        txtTimerSecond.setText("" + String.format("%02d", seconds));
                    } else {
                        Log.d(TAG, " -> The app is in maintenance, but the datetime from server IS SET IN THE PAST");

                        txtTimerLayout.setVisibility(View.GONE);//according to VNM-5110, entire grey box should be hidden
//                        for ( int i = 0; i < txtTimerLayout.getChildCount();  i++ ){
//                            View view = txtTimerLayout.getChildAt(i);
//                            view.setVisibility(View.GONE); // Or whatever you want to do with the view.
//                        }
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

}
