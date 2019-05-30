package ro.vodafone.mcare.android.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vodafone.netperform.NetPerformContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.TermsAndConditionsActivity;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.GravityCompoundDrawable;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.CacheUtils;
import ro.vodafone.mcare.android.utils.ServiceListener;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;

/**
 * This page contains information that Vodafone collects regarding permissions and network usge policies.
 * Onboarding Activity class includes
 * a. Title– My Vodafone (configurable)
 * b. Body
 * c. Hyperlinks – the user can tap on the links (Apps Privacy Supplement and T&Cs) to navigate to the corresponding content at second level pages.
 * d. Cancel button
 * e. OK button
 */
public class OnboardingActivity extends BaseActivity {

    private static final String FRAGMENT = "fragment";
    private static final String LOGIN_FRAGMENT = "LoginFragment";
    private static final String SEAMLESS_LOGIN_FRAGMENT = "SeamlessLoginFragment";
    private static final int MY_PERMISSIONS_REQUEST = 232;
    public static String TAG = "OnboardingActivity";
    int sdkVersion = Build.VERSION.SDK_INT;
    VodafoneDialog vodafoneDialog;
    @BindView(R.id.text_1)
    VodafoneTextView area1TextView;
    private LinearLayout linearLayout;

    @OnClick(R.id.action_cancel)
    public void cancel(View view) {

        /*//Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name", "data permission");
        tealiumMapEvent.put("event_name", "mcare:data permission:button: Anuleaza");
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

        vodafoneDialog =
                new VodafoneDialog(this, getString(R.string.dialog_tc))
                        .setPositiveMessage(getString(R.string.dialog_cancel))
                        .setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vodafoneDialog.dismiss();
                            }
                        })
                        .setNegativeMessage(getString(R.string.dialog_continue))
                        .setNegativeAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vodafoneDialog.dismiss();
                                vodafoneDialog.negativeAction();

                            }
                        });

        vodafoneDialog.show();

    }

    private void setAcceptTealiumTrackingEvent() {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.onBoardingScreenName);
        tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.onBoarding_ok_event);
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
    }

    @OnClick(R.id.action_continue)
    public void accept(View view) {


        CacheUtils cacheUtils = new CacheUtils(getApplicationContext());
        cacheUtils.setTermsAndConditionsAccepted();

        OnboardingTrackingEvent event = new OnboardingTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        journey.event10 = "event10";
        journey.getContextData().put("event10", journey.event10);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        if (sdkVersion >= 23) {
            checkAndRequestPermissions();
        } else {
            setAcceptTealiumTrackingEvent();
            NetPerformContext.start(ServiceListener.getInstance());

            Intent mainIntent = new Intent(OnboardingActivity.this, LoginActivity.class);
            mainIntent.putExtra(FRAGMENT, SEAMLESS_LOGIN_FRAGMENT);
            OnboardingActivity.this.startActivity(mainIntent);
            OnboardingActivity.this.finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initLabels();
        linearLayout = (LinearLayout) findViewById(R.id.policies_list);

        setVodafoneBackgroundOnWindow();

        ButterKnife.bind(this);
        addTextElements();

        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.onBoardingScreenName);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.onBoardingJourneyName);
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        OnboardingTrackingEvent event = new OnboardingTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        area1TextView.setText(AppLabels.getOobeArea1());

    }

    private void addTextElements() {
        try {
            addPermissionElement(AppLabels.getOobeArea2Title(), TextUtils.fromHtml(AppLabels.getOobeArea2()),
                    R.drawable.ic_phone);
            addPermissionElement(AppLabels.getOobeArea3Title(), TextUtils.fromHtml(AppLabels.getOobeArea3())
                    ,
                    R.drawable.vodafoneshops);
            addHiperLinkTextView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPermissionElement(String title, Spanned text, int resourceDrawable) {
        TextView permissionTextView = (TextView) getLayoutInflater().inflate(R.layout.onboarding_permission_textview, null);
        //set drawable
        Drawable drawable = ContextCompat.getDrawable(this, resourceDrawable);
        GravityCompoundDrawable gravityDrawable = new GravityCompoundDrawable(drawable);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        gravityDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        permissionTextView.setCompoundDrawables(gravityDrawable, null, null, null);

        String finalText = title + " \n" + text;
        SpannableString span = new SpannableString(finalText);
        span.setSpan(new RelativeSizeSpan(1.3f), finalText.indexOf(title), finalText.indexOf(title) + title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), finalText.indexOf(title), finalText.indexOf(title) + title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        permissionTextView.setText(span);
        //set margins style margins not working on dynamic textview added
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, ScreenMeasure.dpToPx(10), 0, 0);

        linearLayout.addView(permissionTextView, linearLayout.getChildCount() - 1, layoutParams);
    }

    /**
     * add a HyperLink TextView
     * TODO may needs changes for dynamic usage
     */
    private void addHiperLinkTextView() {

        TextView hiperLinkTextView = new TextView(this);
        //hiperLinkTextView.setClickable(true);
        String finalText = AppLabels.getOobeArea4();

        String tc = AppLabels.getOobeArea4TermsAndConditionLinkAttribute();
        String appPrivacy = AppLabels.getOobeArea4PrivacyPolicyLinkAttribute();

        int tc_index = finalText.indexOf(tc);
        int appPrivacy_index = finalText.indexOf(appPrivacy);

        //set spannables
        Spannable sb = new SpannableString(finalText);
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.onBoardingScreenName);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.onBoarding_confidentialitate);
                TealiumHelper.trackEvent(OnboardingActivity.this.getClass().getSimpleName(), tealiumMapEvent);

                new NavigationAction(OnboardingActivity.this).finishCurrent(false).startAction(IntentActionName.PRIVACY_NO_FLAGS);
            }
        }, appPrivacy_index, appPrivacy_index + appPrivacy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //sb.setSpan(new URLSpan(appPrivacy),finalText.indexOf(appPrivacy),finalText.indexOf(appPrivacy)+appPrivacy.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.onBoardingScreenName);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.onBoarding_terms);
                TealiumHelper.trackEvent(OnboardingActivity.this.getClass().getSimpleName(), tealiumMapEvent);

                Intent mainIntent = new Intent(OnboardingActivity.this, TermsAndConditionsActivity.class);
                mainIntent.putExtra(WebviewActivity.KEY_URL, getString(R.string.terms_and_conditions_url));
                OnboardingActivity.this.startActivity(mainIntent);

            }
        }, tc_index, tc_index + tc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        hiperLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        hiperLinkTextView.setText(sb);

        hiperLinkTextView.setTextColor(ContextCompat.getColor(this, R.color.whiteNormalTextColor));
        hiperLinkTextView.setTextSize(12f);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, ScreenMeasure.dpToPx(10), 0, 0);

        hiperLinkTextView.setLayoutParams(layoutParams);
        linearLayout.addView(hiperLinkTextView, linearLayout.getChildCount() - 1);

    }

    private void initLabels() {
        //Log.e("OnBoardingActivty",labels.getOobe_area1());
    }

    private void checkAndRequestPermissions() {

        //normal no user permission required
        //int internetPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        //int networkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        //int wifiPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        //int bootPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED);

        //user permission required
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int readCallLogStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int wakeLockStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        int accessNotifStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        int bindNotifStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        int readLogsStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (readCallLogStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (wakeLockStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }
        if (accessNotifStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        }
        if (bindNotifStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        }
        if (readLogsStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_LOGS);
        }

        Log.d(TAG, "listPermissionsNeeded = " + listPermissionsNeeded);


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

        if (NetPerformContext.Permissions.isUsageAccessPermissionRequired()) {
            this.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

    }


    //netperform permissions

    /*The minimum set of required Android permissions is as follows:
             android.permission.INTERNET
             android.permission.ACCESS_NETWORK_STATE
             android.permission.ACCESS_WIFI_STATE
             android.permission.RECEIVE_BOOT_COMPLETED
             android.permission.ACCESS_COARSE_LOCATION
             android.permission.ACCESS_FINE_LOCATION
             android.permission.READ_PHONE_STATE

    */

    //optional

    /*

    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
    <uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />
    <uses-permission android:name="android.permission.READ_LOGS" /

    >*/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 232: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    NetPerformContext.start(ServiceListener.getInstance());
                }


                setAcceptTealiumTrackingEvent();
                Intent mainIntent = new Intent(OnboardingActivity.this, LoginActivity.class);
                mainIntent.putExtra(FRAGMENT, LoginActivity.SEAMLESS_LOGIN_FRAGMENT);
                OnboardingActivity.this.startActivity(mainIntent);
                OnboardingActivity.this.finish();

                return;
            }
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(R.anim.slid_up_fragment, R.anim.slid_down_fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.DATA_PERMISSIONS);
    }

    public static class OnboardingTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "data permission";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "data permission");


            s.channel = "OOBE";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "oobe";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);


        }
    }
}
