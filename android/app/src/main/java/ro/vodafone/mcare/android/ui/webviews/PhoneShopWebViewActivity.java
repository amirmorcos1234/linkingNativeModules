package ro.vodafone.mcare.android.ui.webviews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by bogdan marica on 5/3/2017.
 */

public class PhoneShopWebViewActivity extends WebviewActivity {
	private static final String TAG = "PhoneShopWebView";

	final String HEADER_COOKIE_KEY = "Cookie";
	final String HEADER_MOBILE_APP_KEY = "mobileAPP";
	final String HEADER_MOBILE_APP_VALUE = "webview_android";
	final String HEADER_REFERER_KEY = "Referer";
	final String HEADER_REFERER_VALUE = "newMcare";
	final String HEADER_SSO_MCARE_TENANT_ID_KEY = "sso-mcare-tenantid";
	final String HEADER_SSO_MCARE_TENANT_ID_VALUE = "mcare";
	final String ATG_SESSION_ID_KEY = "ATG_SESSION_ID";
	final String JSESSION_ID_KEY = "JSESSIONID";
	public static final String PRICE_PLAN_KEY = "pricePlanValue";

	private String nativeRedirectUrl = "native";
	private String eligibilityUrl = "retentionTab.jsp";
	private String thankYouUrl = "/checkout/thankyou.jsp";
	private String termsAndConditionsUrl = "/Termeni%20si%20Conditii%20magazin%20online%20Vodafone.pdf";
	private String assistanceEmailUrl = "mailto:magazin.online_ro@vodafone.com";
	private String contactEmailVdf = OffersLabels.getWebviewShopAssistanceEmail();

	private String shopBaseUrl = null;
	private FirebaseAnalyticsItem pricePlanValue;

	private final String GET_COOKIE_FOR_SHOP_FLAG = "shop_webview";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//webviewNavigationHeader.setBackButtonVisibility(false);

		setShouldReloadOnStart(false);
		shopBaseUrl = getIntent().getStringExtra(KEY_URL);
		pricePlanValue = (FirebaseAnalyticsItem) getIntent().getSerializableExtra(PRICE_PLAN_KEY);
		if (shopBaseUrl != null) {
			if (shopBaseUrl.contains("?"))
				shopBaseUrl = shopBaseUrl.substring(shopBaseUrl.indexOf("?"));
		}

		callForAdobeTarget(AdobePageNamesConstants.RETENTION_CART);
	}

	@Override
	public void initWebView() {
		Log.d(TAG, "initWebView");
		super.initWebView();
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
        /*  Intent returnIntent = new Intent();
                        String result = "giveUp";
                        returnIntent.putExtra("result", result);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();*/
		webView.setFocusable(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		//webviewUrl = webviewUrl.replace("mcareui/cart/mcareShoppingCart.jsp","newmcare/showCookies.jsp");

		//TODO remove DEV environment workaround
		//url="cms-ucm-agw.connex.ro/shop/mcareui/cart/mcareShoppingCart.jsp";
       /* String toReplace="cms-web-dev-cns.connex.ro";
        String toReplaceWith="cms-ucm-agw.connex.ro";
        if(webviewUrl.contains(toReplace)){
            webviewUrl =  webviewUrl.replaceFirst(toReplace, toReplaceWith);
            shopBaseUrl = shopBaseUrl.replaceFirst(toReplace, toReplaceWith);
        }*/
		//webviewUrl = "http://portal-pet-ot.vodafone.ro/shop/newmcare/showHeaders.jsp";
		//webviewUrl = "http://cms-wl-dev2.connex.ro:7041/vfrointegration/web/tools/showHeaders.jsp";

		D.w("webviewUrl = " + webviewUrl);
		CookieManager cookieManager = CookieManager.getInstance();
		D.w(" CookieManager.getInstance()() = " + cookieManager);

		cookieManager.removeAllCookie();
		cookieManager.setCookie(getCookiePath(webviewUrl), getCookie());
		cookieManager.setAcceptCookie(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			cookieManager.setAcceptThirdPartyCookies(webView, true);
		}
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.d(TAG , "shouldOverrideUrlLoading " + url);
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
				Log.d(TAG, "onPageFinished");
				String cookies = CookieManager.getInstance().getCookie(url);
				Log.d(TAG, "All the cookies in a string:" + cookies);
				stopLoadingDialog();

				if (webView.canGoBack())
					webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
				else
					webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);
			}


			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
				Log.d(TAG, "onLoadResource : " + url);
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				Log.d(TAG, "onReceivedError : " + error.toString());
				stopLoadingDialog();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url, getShopHeaders());
				return true;
			}

			@Nullable
			@Override
			public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "URL:" + url);
						overrideUrlLoading(view, url);
					}
				});

				return super.shouldInterceptRequest(view, url);
			}

		});

		webView.loadUrl(webviewUrl, getShopHeaders());
	}

	boolean overrideUrlLoading(WebView view, String url) {

		Log.d(TAG , "shouldOverrideUrlLoading " + url);
		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap(6);
		tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.retentionScreen);
		tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
		tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

		PhoneShopTrackingEvent event = new PhoneShopTrackingEvent();
		TrackingAppMeasurement journey = new TrackingAppMeasurement();
		journey.event7 = "event7";
		journey.getContextData().put("event7", journey.event7);
		event.defineTrackingProperties(journey);
		VodafoneController.getInstance().getTrackingService().trackCustom(event);

		Log.d(TAG, url);
		Log.d(TAG, "before checks");
		if (url.contains(getGoBackToEligibilityUrl())) {

			try {
				new NavigationAction(PhoneShopWebViewActivity.this).finishCurrent(true).startAction(IntentActionName.RETENTION);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}


		} else if (url.contains(getGoToThankYouOrderFinishedUrl())) {
			D.d(TAG + url);

			Intent returnIntent = new Intent();
			String result = "result";
			returnIntent.putExtra("result", result);
			returnIntent.putExtra(PRICE_PLAN_KEY, pricePlanValue);
			setResult(Activity.RESULT_OK, returnIntent);

			//Tealium trigger survey
			//Tealium Track view
			tealiumMapView = new HashMap(4);
			tealiumMapView.put("screen_name", "buypriceplan");
			//add Qualtrics survey
			TealiumHelper.addQualtricsCommand();
			//track
			TealiumHelper.trackView("buypriceplan", tealiumMapView);

			finish();
			return true;
		} else if (url.contains(termsAndConditionsUrl) || url.toLowerCase().endsWith(".pdf")) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		} else if (url.contains(assistanceEmailUrl)) {
			final String emailAddresses[] = new String[1];
			emailAddresses[0] = contactEmailVdf;
			composeEmail(emailAddresses, null);
			return true;
		} else {
			//view.loadUrl(url, getShopHeaders());
			return false;
		}

	}

	@Override
	public void onBackPressed() {
		openExitCartPopUp();
	}

	private void openExitCartPopUp() {
		final String url;

		if (webView.getUrl() == null) {
			try {
				Log.d(TAG, "null url, exiting the cart - something went wrong");
				new NavigationAction(PhoneShopWebViewActivity.this).finishCurrent(true).startAction(IntentActionName.RETENTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		} else {
			url = webView.getUrl();
		}

		Log.d(TAG, "shopbaseUrl: " + shopBaseUrl);
		if ((shopBaseUrl != null) && !shopBaseUrl.isEmpty() && url.startsWith(shopBaseUrl)) {
			webView.loadUrl("javascript:window.VF.listaTelefoaneStergeNumar.openPopup($('#confirmaRenuntaComandaLaBack'));",
					getShopHeaders());
		} else {
			try {
				new NavigationAction(PhoneShopWebViewActivity.this).finishCurrent(true).startAction(IntentActionName.RETENTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//this is done to prevent the app from getting back to the basket with an empty order
		}
	}

	@Override
	public void close() {
		openExitCartPopUp();
	}

	public void composeEmail(String[] addresses, String subject) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:")); // only email apps should handle this
		intent.putExtra(Intent.EXTRA_EMAIL, addresses);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		if (intent.resolveActivity(PhoneShopWebViewActivity.this.getPackageManager()) != null) {
			startActivity(intent);
		}
	}

	private Map<String, String> getShopHeaders() {
		Map<String, String> extraHeaders = new HashMap<>();

		//extraHeaders.put(HEADER_COOKIE_KEY, getCookie());
		extraHeaders.put(HEADER_MOBILE_APP_KEY, HEADER_MOBILE_APP_VALUE);
		extraHeaders.put(HEADER_REFERER_KEY, HEADER_REFERER_VALUE);
		extraHeaders.put(HEADER_REFERER_KEY + "2", HEADER_REFERER_VALUE);//workaround for Chronium issue https://bugs.chromium.org/p/chromium/issues/detail?id=450551
		extraHeaders.put(HEADER_SSO_MCARE_TENANT_ID_KEY, HEADER_SSO_MCARE_TENANT_ID_VALUE);
		return extraHeaders;
	}

	private Map<String, String> getShopHeadersWithCookie() {
		Map<String, String> extraHeaders = new HashMap<>();

		extraHeaders.put(HEADER_MOBILE_APP_KEY, HEADER_MOBILE_APP_VALUE);
		extraHeaders.put(HEADER_REFERER_KEY, HEADER_REFERER_VALUE);
		extraHeaders.put(HEADER_REFERER_KEY + "2", HEADER_REFERER_VALUE);//workaround for Chronium issue https://bugs.chromium.org/p/chromium/issues/detail?id=450551
		return extraHeaders;
	}

	private String getCookie() {
		try {
			ShopLoginSuccess shopLoginSuccess = (ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class);

			String shopSessionToken = shopLoginSuccess.getShopSessionToken();

			Map<String, String> cookiesMap = new HashMap<>();
			cookiesMap.put(ATG_SESSION_ID_KEY, shopSessionToken);
			cookiesMap.put(JSESSION_ID_KEY, shopSessionToken);

			String cookie = BaseService.createCookieStaticWithExtras(this, cookiesMap, GET_COOKIE_FOR_SHOP_FLAG);
			Log.e(TAG, "COOKIE : " + cookie);

			return cookie;
		} catch (Exception ex) {
			//ignore?
		}
		return "";
	}

	public static class PhoneShopTrackingEvent extends TrackingEvent {
		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "retention:shopping cart";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "retention:shopping cart");


			s.prop5 = "sales:shopping cart";
			s.getContextData().put("prop5", s.prop5);
			s.channel = "retention in self care";
			s.getContextData().put("&&channel", s.channel);
			s.eVar18 = "buyphonetopriceplan";
			s.getContextData().put("eVar18", s.eVar18);
			s.eVar19 = "task";
			s.getContextData().put("eVar19", s.eVar19);
		}
	}

	private WebResourceResponse getNewResponse(String url) {

		try {
			OkHttpClient httpClient = new OkHttpClient();

			Request.Builder builder = new Request.Builder()
					.url(url.trim());

			Iterator it = getShopHeadersWithCookie().entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());
				builder.addHeader((String) pair.getKey(), (String) pair.getValue());
				it.remove(); // avoids a ConcurrentModificationException
			}

			Response response = httpClient.newCall(builder.build()).execute();

			return new WebResourceResponse(
					null,
					response.header("content-encoding", "utf-8"),
					response.body().byteStream()
			);

		} catch (Exception e) {
			return null;
		}

	}

	private String getCookiePath(String url) {
		try {
			final URL url1 = new URL(url);
			return url1.getProtocol() + "://" + url1.getHost();
		} catch (Exception e) {
			return url;
		}
	}

	public String getGoBackToEligibilityUrl() {
		return eligibilityUrl;
	}

	public String getGoToThankYouOrderFinishedUrl() {
		return thankYouUrl;
	}

	public String getTermsAndConditionsUrl() {
		return termsAndConditionsUrl;
	}

	public String getAssistanceEmailUrl() {
		return assistanceEmailUrl;
	}
}

