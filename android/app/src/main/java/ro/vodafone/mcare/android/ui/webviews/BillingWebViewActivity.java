package ro.vodafone.mcare.android.ui.webviews;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.payBill.BillingWebViewModel;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.PayBillActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by User1 on 5/3/2017.
 */

public class BillingWebViewActivity extends WebviewActivity {


    public static String TAG = BillingWebViewActivity.class.getSimpleName();
    private static final String ANALYTICS_LOG_PAY_BILL_KEY = "pay_bill";
    private static final String ANALYTICS_LOG_EO_ACTIVATION_KEY = "eo_activation";


    private String htmlInputs;
    private String successUrl;
    private String successMessage;
    private String activityIdentifier;
    private boolean isServices;
    private BillingWebViewModel billingWebViewModel;

    public void setHtmlInputs(String htmlInputs) {
        this.htmlInputs = htmlInputs;
    }

    public void setsuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }


    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getActivityIdentifier() {
        return activityIdentifier;
    }

    public void setActivityIdentifier(String activityIdentifier) {
        this.activityIdentifier = activityIdentifier;
    }

    public boolean isServices() {
        return isServices;
    }

    public void setServices(boolean services) {
        isServices = services;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        billingWebViewModel =  new Gson().fromJson(IntentActionName.BILLING_WEBVIEW.getOneUsageSerializedData(), BillingWebViewModel.class);

        if(billingWebViewModel != null) {
            create(billingWebViewModel.getHtmlInputs(),
                    billingWebViewModel.getSuccessUrl(),
                    billingWebViewModel.getSuccessMessage(),
                    billingWebViewModel.getActivityIdentifier(),
                    billingWebViewModel.isServices());

        }

        Log.d(TAG, "onCreate: htmlInputs " + htmlInputs);
        Log.d(TAG, "onCreate: successUrl " + successUrl);
        Log.d(TAG, "onCreate: activityIdentifier" + activityIdentifier);
        Log.d(TAG, "onCreate: isServices" + isServices);
    }

    public void create(String htmlInputs, String successUrl, String successMessage, String activityIdentifier, boolean isServices) {

        setHtmlInputs(htmlInputs);
        setsuccessUrl(successUrl);
        setSuccessMessage(successMessage);
        setActivityIdentifier(activityIdentifier);
        setServices(isServices);
    }

    @Override
    public void initWebView() {
        super.initWebView();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("", "onPageStarted: " + url);
                showLoadingDialog();

                try {
                    webviewNavigationHeader.setSubtitle(new URL(url).getHost());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopLoadingDialog();
                if (webView.canGoBack())
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
                else
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);
//                webviewNavigationHeader.setTitle(webView.getTitle());
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("", "onReceivedError: " + error);
                stopLoadingDialog();
            }

			@Nullable
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG + " shouldInterceptRequest", "URL:" + url);
						handleUri(url);
					}
				});

				return super.shouldInterceptRequest(view, url);
			}

        });
        webView.loadData(htmlInputs, "text/html", "UTF-8");
    }

    private boolean handleUri(final String url) {

        Log.d(TAG, "shouldOverrideUrlLoading: uri " + url);
        if (url != null && (url).contains(successUrl)) {
            displaySuccessMessage();
            return true;
        }
        return false;
    }

    private void displaySuccessMessage(){
        Log.d("", "displaySuccessMessage: ");
        logGoogleAnalytics();

       // Intent intent = new Intent(BillingWebViewActivity.this, DashboardActivity.class);
        if(activityIdentifier.equalsIgnoreCase("PayBillActivity")){
            VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(2, 20, VoiceOfVodafoneCategory.Pay_Bill, null, CallDetailsLabels.getBilled_success_message_vov(), "Ok, am înțeles.", null,
                    true, false, VoiceOfVodafoneAction.Dismiss, null);
            VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
            new CustomToast.Builder(BillingWebViewActivity.this).message(CallDetailsLabels.getBilled_success_message())
                    .success(true).show();
//            CustomToast customToast = new CustomToast(BillingWebViewActivity.this, BillingWebViewActivity.this,CallDetailsLabels.getBilled_success_message(), true);
//            customToast.show();

            //Tealium trigger survey
            //Tealium Track view
            Map<String, Object> tealiumMapView = new HashMap<>(4);
            tealiumMapView.put("screen_name","paybill");
            //add Qualtrics survey
            TealiumHelper.addQualtricsCommand();
            //track
            TealiumHelper.trackView("paybill", tealiumMapView);

            PayBillActivity.PayBillTrackingEvent event = new PayBillActivity.PayBillTrackingEvent();
            TrackingAppMeasurement journey = new TrackingAppMeasurement();
            journey.event8 = "event8";
            journey.getContextData().put("event8", journey.event8);
            event.defineTrackingProperties(journey);
            VodafoneController.getInstance().getTrackingService().trackCustom(event);

            new NavigationAction(BillingWebViewActivity.this).startAction(IntentActionName.DASHBOARD,true);
        } else if (activityIdentifier.equalsIgnoreCase("BeoActivationPrepaid")){
            if(billingWebViewModel.getOfferName()!=null) {
                Log.d(TAG, "displaySuccessMessage: offerName " + billingWebViewModel.getOfferName());
                VoiceOfVodafone vov = new VoiceOfVodafone(11, 20, VoiceOfVodafoneCategory.EO_Activation, null, String.format(
                        isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part1() : BEOLabels.getBeo_activate_prepaid_offer_part1(),
                        billingWebViewModel.getOfferName()), "Ok", null,
                        true, false, VoiceOfVodafoneAction.Dismiss, null);
                VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
            }
            new CustomToast.Builder(BillingWebViewActivity.this).message(isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part2() : BEOLabels.getBeo_activate_prepaid_offer_part2())
                    .success(true).show();
//            CustomToast customToast = new CustomToast(BillingWebViewActivity.this, BillingWebViewActivity.this, BEOLabels.getBeo_activate_prepaid_offer_part2(), true);
//            customToast.show();

            new NavigationAction(this).startAction(IntentActionName.DASHBOARD);
        }


        //Tealium trigger survey
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap<>(4);
        tealiumMapView.put("screen_name","paybill");
        //add Qualtrics survey
        TealiumHelper.addQualtricsCommand();
        //track
        TealiumHelper.trackView("paybill", tealiumMapView);

    }

    private void logGoogleAnalytics() {
        FirebaseAnalyticsItem firebaseAnalyticsItem = billingWebViewModel.getAnalyticsValue();
        sendFirebaseEvent(firebaseAnalyticsItem.getFirebaseAnalyticsEvent(),
                FirebaseAnalyticsUtils.getBundleFromParams(firebaseAnalyticsItem.getFirebaseAnalyticsParams()));
    }
}
