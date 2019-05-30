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

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Bivol Pavel on 28.02.2017.
 */
public class TopUpWebViewFActivity extends WebviewActivity {

    public static String TAG = "TopUpWebView";

    private String htmlInputs;
    private String successUrl;

    private String msisdn;
    private String amount;

    private boolean isOwnRecharge;


    public void setHtmlInputs(String htmlInputs) {
        this.htmlInputs = htmlInputs;
    }

    public void setsuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public void setMsisdn(String msisdn){
        this.msisdn = msisdn;
    }
    public void setAmount(String amount){
        this.amount = amount;
    }

    public boolean getIsOwnRecharge() {
        return isOwnRecharge;
    }

    public void setOwnRecharge(boolean ownRecharge) {
        isOwnRecharge = ownRecharge;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            create(bundle.getString("htmlInputs"),
                    bundle.getString("successUrl"),
                    bundle.getString("msisdn"),
                    bundle.getString("amount"),
                    bundle.getBoolean("isOwnRecharge"));
    }

    public void create(String htmlInputs, String successUrl, String msisdn, String amount, boolean isOwnRecharge) {
        setHtmlInputs(htmlInputs);
        setsuccessUrl(successUrl);
        setMsisdn(msisdn);
        setAmount(amount);
        setOwnRecharge(isOwnRecharge);
    }

    public void initWebView() {
        Log.d(TAG, "initWebView()");
        super.initWebView();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: " + msisdn);
                Log.d(TAG, "onPageStarted: " + amount);
                Log.d(TAG, " onPageStarted " + url);
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
                Log.d(TAG, " onPageFinished ");
                stopLoadingDialog();

                if (webView.canGoBack())
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
                else
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);
//                webviewNavigationHeader.setTitle(webView.getTitle());
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                stopLoadingDialog();
                Log.e(TAG, "WebResourceError - " + error.toString());
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

        if ((url).contains(successUrl)) {
            Log.d(TAG, "url - " + url);
            displayConfirmationDialogOnDashboard();
            return true;
        }
        return false;
    }

    private void displayConfirmationDialogOnDashboard() {
        RechargeService rechargeService = new RechargeService(this);
        rechargeService.getFavoriteNumbers().subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                super.onNext(favoriteNumbersSuccessGeneralResponse);
                if (favoriteNumbersSuccessGeneralResponse.getTransactionStatus() == 0 && RealmManager.getRealmObject(FavoriteNumbersSuccess.class) != null) {
                    List<FavoriteNumber> favoriteNumbersList = ((FavoriteNumbersSuccess) RealmManager.getRealmObject(FavoriteNumbersSuccess.class)).getFavoriteNumbers();

                    if (favoriteNumbersList.size() == 0){
                        if(!VodafoneController.getInstance().getUserProfile().getMsisdn().equals(formatMsisdn(msisdn))
                                && !getIsOwnRecharge()) {
                            VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn, amount, false), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                            Log.d("vov widget", "insertAuto");
                            VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                            //Tealium trigger survey
                            //Tealium Track view
                            Map<String, Object> tealiumMapView = new HashMap(4);
                            tealiumMapView.put("screen_name", "recharge");
                            //add Qualtrics survey
                            TealiumHelper.addQualtricsCommand();
                            //track
                            TealiumHelper.trackView("recharge", tealiumMapView);
                        } else {
                            VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn, amount, true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                            Log.d("vov widget", "insertAuto");
                            VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                            //Tealium trigger survey
                            //Tealium Track view
                            Map<String, Object> tealiumMapView = new HashMap(4);
                            tealiumMapView.put("screen_name", "recharge");
                            //add Qualtrics survey
                            TealiumHelper.addQualtricsCommand();
                            //track
                            TealiumHelper.trackView("recharge", tealiumMapView);
                        }
                    } else {
                        for (FavoriteNumber favoriteNumber : favoriteNumbersList) {
                            if (msisdn.equalsIgnoreCase(favoriteNumber.getPrepaidMsisdn())
                                    || VodafoneController.getInstance().getUserProfile().getMsisdn().equals(formatMsisdn(msisdn))
                                    || getIsOwnRecharge()) {
                                VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn, amount, true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                                Log.d("vov widget", "insertAuto");
                                VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                                //Tealium trigger survey
                                //Tealium Track view
                                Map<String, Object> tealiumMapView = new HashMap(4);
                                tealiumMapView.put("screen_name", "recharge");
                                //add Qualtrics survey
                                TealiumHelper.addQualtricsCommand();
                                //track
                                TealiumHelper.trackView("recharge", tealiumMapView);

                                break;
                            } else {
                                VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn,amount,false), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                                Log.d("vov widget", "insertAuto");
                                VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                                //Tealium trigger survey
                                //Tealium Track view
                                Map<String, Object> tealiumMapView = new HashMap(4);
                                tealiumMapView.put("screen_name", "recharge");
                                //add Qualtrics survey
                                TealiumHelper.addQualtricsCommand();
                                //track
                                TealiumHelper.trackView("recharge", tealiumMapView);


                            }
                        }
                    }
                } else {
                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn,amount,true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                    Log.d("vov widget", "insertAuto");
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                    //Tealium trigger survey
                    //Tealium Track view
                    Map<String, Object> tealiumMapView =new HashMap(4);
                    tealiumMapView.put("screen_name","topup");
                    //add Qualtrics survey
                    TealiumHelper.addQualtricsCommand();
                    //track
                    TealiumHelper.trackView("recharge", tealiumMapView);

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn,amount,true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                Log.d("vov widget", "insertAuto");
                VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
            }

            @Override
            public void onCompleted() {
                logGoogleAnalytics();

                new CustomToast.Builder(TopUpWebViewFActivity.this).message(TopUPLabels.getTop_up_successfull_toast_message()).success(true).show();

                new NavigationAction(TopUpWebViewFActivity.this).startAction(IntentActionName.DASHBOARD,true);


                //Tealium trigger survey
                //Tealium Track view
                Map<String, Object> tealiumMapView =new HashMap<>(4);
                tealiumMapView.put("screen_name","recharge");
                //add Qualtrics survey
                TealiumHelper.addQualtricsCommand();
                //track
                TealiumHelper.trackView("recharge", tealiumMapView);

            }
        });
    }

    private String createVovMessage(String msisdn, String amount, boolean isInFavorites){
        String message = null;
            if(!isInFavorites && !getIsOwnRecharge()){
                message = String.format(TopUPLabels.getTop_up_immediate_recharge_vov_message(), msisdn, NumbersUtils.getIntegerPart(amount));
            } else {
                message = StringUtils.substringBefore(String.format(TopUPLabels.getTop_up_immediate_recharge_vov_message(),msisdn,NumbersUtils.getIntegerPart(amount)),",") + ".";
            }
        return message;
    }

    private String formatMsisdn(String msisdn){
        String formatedMsisdn = msisdn;
        if(msisdn.startsWith("0"))
            formatedMsisdn = "4" + msisdn;

        return formatedMsisdn;
    }

    private void logGoogleAnalytics() {
        String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

        FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
        firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
        firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("top_up_MSISDN", msisdn);
        firebaseAnalyticsItem.addFirebaseAnalyticsParams("top_up_XXEUR", amount+"EUR");

        sendFirebaseEvent(firebaseAnalyticsItem.getFirebaseAnalyticsEvent(),
                FirebaseAnalyticsUtils.getBundleFromParams(firebaseAnalyticsItem.getFirebaseAnalyticsParams()));
    }
}
