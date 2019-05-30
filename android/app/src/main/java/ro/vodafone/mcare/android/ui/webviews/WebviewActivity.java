package ro.vodafone.mcare.android.ui.webviews;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.WebViewCampaignLabels;
import ro.vodafone.mcare.android.rest.observers.SessionObserverBuilder;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.header.WebviewNavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bivol Pavel on 28.02.2017.
 */

public class WebviewActivity extends BaseActivity {

	protected final int PICK_CONTACT = 1;
	final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
	boolean before;
	boolean after = true;
	private String PREFS_FILE_NAME;

	private Dialog readContactsPermissionOverlay;
	private Dialog readPermissionFlagCheckedOverlay;

	static final String TAG = "WebviewActivity";
	public static final String KEY_RELOAD_ON_START = "reloadOnStart";
	public static final String KEY_URL = "webviewUrl";
	public static final String PAGE_TITLE = "pageTitle";

	public WebviewNavigationHeader webviewNavigationHeader;
	public String webviewUrl;
	public String nativeRedirectUrl = "native";
	public WebView webView;
	private boolean shouldReloadOnStart = true;

	public HashMap<String, String> headerParams;

	boolean isAutoLoginRunning = false;

	WebViewClient webViewClient = new WebViewClient() {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showLoadingDialog();

			Log.d(TAG, " onPageStarted " + url);
			try {
				webviewNavigationHeader.setSubtitle(new URL(url).getHost());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onPageFinished(WebView webView, String url) {
			super.onPageFinished(webView, url);
			Log.d(TAG, " onPageFinished " + url);
			if (!isAutoLoginRunning)
				stopLoadingDialog();

			if (webView.canGoBack())
				webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
			else
				webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);

			if (webviewUrl == null)
				webviewUrl = "";
//            webviewNavigationHeader.setTitle(webView.getTitle());


		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Boolean bool = overrideUrlLoading(view, url);
			Log.d(TAG, " shouldOverrideUrlLoading " + url + " overwritten:" + bool);
			return bool;
		}

		@TargetApi(Build.VERSION_CODES.N)
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			final String url = request.getUrl().toString();

			Boolean bool = overrideUrlLoading(view, url);
			Log.d(TAG, " shouldOverrideUrlLoading WebResourceRequest " + url + " redirect:" + request.isRedirect() + " overwritten:" + bool);
			return bool;
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			if (url != null && url.contains(nativeRedirectUrl)) {
				new CustomToast.Builder(WebviewActivity.this).message("Success").success(true).show();
//                CustomToast customToast = new CustomToast(WebviewActivity.this, WebviewActivity.this, "Success", true);
//                customToast.show();

//                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
//                    startActivity(intent);-
//                    getActivity().finish();
//                    OffersActivity.mInstance.addFragment(BeoFragment.createFragment(WebviewActivity.this, false, false));
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Log.d(TAG, " onReceivedError " + errorCode + " " + failingUrl);
		}

	};

	boolean overrideUrlLoading(WebView view, String url) {

		if (isAutoLoginRunning)
			return true;

		if (url.toLowerCase().contains(AppConfiguration.getWebviewCampaignShareStringListener().toLowerCase())) {
			try {
				Uri uri = Uri.parse(url);

				Set<String> params = uri.getQueryParameterNames();

				String title = "";
				String message = "";
				String shareUrl = "";
				String imageUrl = "";

				if (params.contains("title"))
					title = URLDecoder.decode(uri.getQueryParameter("title"), "UTF-8");

				if (params.contains("message"))
					message = URLDecoder.decode(uri.getQueryParameter("message"), "UTF-8");

				if (params.contains("url"))
					shareUrl = URLDecoder.decode(uri.getQueryParameter("url"), "UTF-8");

				if (params.contains("image"))
					imageUrl = URLDecoder.decode(uri.getQueryParameter("image"), "UTF-8");

				Uri imageUri = null;
				try {
					if (!imageUrl.isEmpty())
						imageUri = Uri.parse(imageUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
				shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n" + shareUrl);
				/*if (imageUrl != null) {
					shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image*//*");
				} else*/
				shareIntent.setType("text/plain");
				startActivity(Intent.createChooser(shareIntent, WebViewCampaignLabels.getShareTitle()));

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (url.toLowerCase().contains(AppConfiguration.getWebviewCapaignSendSMSStringListener().toLowerCase())) {
			try {
				Uri uri = Uri.parse(url);

				Set<String> params = uri.getQueryParameterNames();

				String recipient = "";
				String message = "";

				if (params.contains("recipient"))
					recipient = URLDecoder.decode(uri.getQueryParameter("recipient"), "UTF-8");

				if (params.contains("message"))
					message = URLDecoder.decode(uri.getQueryParameter("message"), "UTF-8");

				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SENDTO);

				if(!TextUtils.isEmpty(recipient)){
					// Set intent data
					// This ensures only SMS apps respond
					shareIntent.setData(Uri.parse("smsto:" + recipient));

					// Alternate data scheme
					//intent.setData(Uri.parse("sms:" + phoneNumber));
				}else {
					// If the sms link built without phone number
					shareIntent.setData(Uri.parse("smsto:"));

					// Alternate data scheme
					//intent.setData(Uri.parse("sms:" + phoneNumber));
				}

				if(!TextUtils.isEmpty(message)){
					// Set intent body
					shareIntent.putExtra("sms_body",message);
				}

				if(shareIntent.resolveActivity(getPackageManager())!=null){
					// Start the sms app
					startActivity(shareIntent);
				}else {
					new CustomToast.Builder(WebviewActivity.this).message("No SMS app found.").success(true).show();
				}

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (url.toLowerCase().contains(AppConfiguration.getWebviewCampaignActivateOfferStringListener().toLowerCase())) {
			try {
				Uri uri = Uri.parse(url);

				Set<String> params = uri.getQueryParameterNames();

				String pageId = "";
				String offerId = "";

				if (params.contains("page_id"))
					pageId = URLDecoder.decode(uri.getQueryParameter("page_id"), "UTF-8");

				if (params.contains("offer_id"))
					offerId = URLDecoder.decode(uri.getQueryParameter("offer_id"), "UTF-8");

				NavigationAction navAction = new NavigationAction(this, IntentActionName.OFFERS_BEO_DETAILS).setOneUsageSerializedData(offerId);
				navAction.startAction();

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (url.toLowerCase().contains(AppConfiguration.getWebviewCampaignLoginStringListener().toLowerCase())) {
			try {
				//GO TO LOGIN

				NavigationAction navAction = new NavigationAction(this, IntentActionName.WEBVIEW_RELOGIN).setOneUsageSerializedData(webviewUrl);
				navAction.startAction();


				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (url.toLowerCase().contains(AppConfiguration.getWebviewCampaignReLoginStringListener().toLowerCase())) {
			try {

				handleRelogin();

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (AppConfiguration.getWebviewCampaignExternalLinksStringListener().toLowerCase().contains(url.toLowerCase())) {
			try {


				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (url.toLowerCase().endsWith(".pdf")) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		return false;
	}

	void handleRelogin() {
		isAutoLoginRunning = true;

		showLoadingDialog();
		Log.d(TAG, "Relogin start");
		Observable observable = SessionObserverBuilder.treat401ErrorCode(new Throwable("error")).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

		observable.subscribe(new Observer() {
			@Override
			public void onCompleted() {
				Log.d(TAG, "Relogin onCompleted not runnable");
				WebviewActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						VodafoneController.getInstance().handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								isAutoLoginRunning = false;
								Log.d(TAG, "Relogin onCompleted");
								initWebView();
							}
						}, 2000);
					}
				});

			}

			@Override
			public void onError(Throwable e) {
				Log.d(TAG, "Relogin onError");
				isAutoLoginRunning = false;
				new NavigationAction(WebviewActivity.this).finishCurrent(true).startAction(IntentActionName.LOGOUT);
				stopLoadingDialog();
			}

			@Override
			public void onNext(Object o) {
				Log.d(TAG, "Relogin onNext");

			}
		});


	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		setShouldReloadOnStart(false);

		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			webviewUrl = b.getString(KEY_URL);
			if (b.keySet().contains(KEY_RELOAD_ON_START))
				shouldReloadOnStart = b.getBoolean(KEY_RELOAD_ON_START);
		}
		if (webviewUrl == null)
			webviewUrl = "";

		//webviewUrl = "http://cms-web-dev-cns.connex.ro/vfrointegration/web/tools/showHeaders.jsp";
		webView = (WebView) findViewById(R.id.webview);
		stopLoadingAfterDuration(10);

		webviewNavigationHeader = (WebviewNavigationHeader) findViewById(R.id.webviewNavigation_header);
		webviewNavigationHeader.setActivity(this)
				.init()
				.setWebview(webView);

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);

				String mTitle = title;
				Bundle extras = WebviewActivity.this.getIntent().getExtras();
				if (extras != null) {
					if (extras.getString(PAGE_TITLE) != null)
						mTitle = extras.getString(PAGE_TITLE);
				}
				Log.d(TAG, "onReceivedTitle: mTitle " + mTitle);
				webviewNavigationHeader.setTitle(mTitle);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});

		//do this after subclasses of this activity have called onCreate
		VodafoneController.getInstance().handler.post(new Runnable() {
			@Override
			public void run() {
				if (!shouldReloadOnStart)
					initWebView();
			}
		});

		makeAdobeTargetCallIfNeed();

		TealiumHelper.tealiumTrackView(WebviewActivity.class.getSimpleName(),
				TealiumConstants.webViewJourney,TealiumConstants.webViewScreenName);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (shouldReloadOnStart)
			initWebView();
	}

	public void initWebView() {
		Log.d(TAG, "load webview " + webviewUrl);

		setupHeaderParamasAndCookies();

		if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
			webView.setWebContentsDebuggingEnabled(true);
		}

		webView.clearHistory();
		webView.clearFormData();
		webView.clearCache(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setFocusable(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebViewClient(webViewClient);

		webView.addJavascriptInterface(new WebAppInterface(), "Android");

		if (headerParams != null && headerParams.size() > 0)
			webView.loadUrl(webviewUrl, headerParams);
		else
			webView.loadUrl(webviewUrl);
	}

	public void setupHeaderParamasAndCookies() {
		headerParams = new HashMap<>();

		StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();
		String encryptedMsisdn = "";
		try {
			encryptedMsisdn = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(VodafoneController.getInstance().getUserProfile().getMsisdn()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringMsisdnCrypt billCycleDateCrypt = new StringMsisdnCrypt();
		String encryptedBillCycleDate = "";
		try {
			encryptedBillCycleDate = StringMsisdnCrypt.bytesToHex(billCycleDateCrypt.encrypt(String.valueOf(VodafoneController.getInstance().getProfile().getBillCycleDate())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringMsisdnCrypt subscriberEffectiveDateCrypt = new StringMsisdnCrypt();
		String encryptedSubscriberEffectiveDate = "";
		try {
			encryptedSubscriberEffectiveDate = StringMsisdnCrypt.bytesToHex(subscriberEffectiveDateCrypt.encrypt(String.valueOf(VodafoneController.getInstance().getProfile().getSubscriberEffectiveDate())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringMsisdnCrypt activationDateCrypt = new StringMsisdnCrypt();
		String encryptedActivationDate = "";
		try {
			encryptedActivationDate = StringMsisdnCrypt.bytesToHex(activationDateCrypt.encrypt(String.valueOf(VodafoneController.getInstance().getProfile().getActivationDate())));
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringMsisdnCrypt sidCrypt = new StringMsisdnCrypt();
		String encryptedSID = "";
		try {
			encryptedSID = StringMsisdnCrypt.bytesToHex(sidCrypt.encrypt(VodafoneController.getInstance().getUserProfile().getSid()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String ssoToken = "";
		try {
			ssoToken = UserDataController.getSsoTokenId();
		} catch (Exception e) {
			e.printStackTrace();
		}


		String userRole = null;
		try {
			if (EbuMigratedIdentityController.getInstance() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null) {
				userRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
			} else if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.HYBRID) {
				userRole = VodafoneController.getInstance().getUserProfile().getCustomerType();
			} else {
				userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
			}

		} catch (Exception e) {
			userRole = "nonvfuser";
			e.printStackTrace();
		}
		userRole = userRole.toLowerCase();

		String appVer = "";
		try {

			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVer = packageInfo.versionName;

		} catch (Exception e) {
			e.printStackTrace();
		}

		headerParams.put("UID", encryptedMsisdn + "");
		headerParams.put("URole", userRole + "");
		headerParams.put("AppVer", appVer + "");
		headerParams.put("SubscriberID", encryptedSID + "");
		headerParams.put("SSOTokenID", ssoToken + "");
		headerParams.put("Cookie", BaseService.createCookieStatic(VodafoneController.getInstance()) + "");

        if (VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole() != null) {
            if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.HYBRID ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.SEAMLESS_HYBRID ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.RES_SUB ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.RES_CORP ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.SEAMLESS_HIGH_ACCESS ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.SEAMLESS_LOW_ACCESS ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.PRIVATE_USER) {
                headerParams.put("subscriberEffectiveDate", encryptedSubscriberEffectiveDate + "");
                headerParams.put("activationDate", encryptedActivationDate + "");
                headerParams.put("billcycledate", encryptedBillCycleDate + "");
                try {
                    headerParams.put("isVMB", Boolean.toString(VodafoneController.getInstance().getProfile().isVMB()) + "");
                } catch (Exception e) {
                    headerParams.put("isVMB", "");
                }
            } else if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.PREPAID ||
                    VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.SEAMLESS_PREPAID_USER) {
                try {
                    headerParams.put("isTOBE", Boolean.toString(VodafoneController.getInstance().getProfile().isTobe()) + "");
                } catch (Exception e) {
                    headerParams.put("isTOBE", "");
                }
            }
        }

		CookieManager cookieManager = CookieManager.getInstance();
		Log.d(TAG, " CookieManager.getInstance()() = " + cookieManager);

		String cookiePath = getCookiePath(webviewUrl);

		try {
			cookieManager.removeAllCookie();

			cookieManager.setCookie(cookiePath, BaseService.createCookieStatic(VodafoneController.getInstance()));

			for (Map.Entry<String, String> entry : headerParams.entrySet()) {

				String key = entry.getKey();
				String value = entry.getValue();
				cookieManager.setCookie(cookiePath, key + "=" + value);
				Log.d(TAG + "_cookies", key + "=" + value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cookieManager.setAcceptCookie(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			cookieManager.setAcceptThirdPartyCookies(webView, true);
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


	public boolean isShouldReloadOnStart() {
		return shouldReloadOnStart;
	}

	/**
	 * Sets whether the activity should reload the url every time it comes into the foreground.
	 *
	 * @param shouldReloadOnStart default true
	 */
	public void setShouldReloadOnStart(boolean shouldReloadOnStart) {
		this.shouldReloadOnStart = shouldReloadOnStart;
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack())
			webView.goBack();
		else {
			VodafoneController.getInstance().setFromBackPress(true);
			super.onBackPressed();
		}
	}

	public void close() {
		super.onBackPressed();
	}

	private void makeAdobeTargetCallIfNeed() {
		if (webviewUrl == null)
			return;
		if (webviewUrl.equals(SettingsLabels.getLinkContractDetails()) || webviewUrl.equals(SettingsLabels.getLinkContractDetailsAccepted())
				|| webviewUrl.equals(SettingsLabels.getLinkAncomOffersSpecific())) {
			callForAdobeTarget();
		}
	}

	private void callForAdobeTarget() {
		if (VodafoneController.getInstance().isAppComesFromBackground() || VodafoneController.getInstance().isFromBackPress() || !VodafoneController.getInstance().isWasScreenOn())
			return;

		new AdobeTargetController().trackPage(this, AdobePageNamesConstants.PG_CONTRACT_DETAILS_DIS);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		Log.d("", "onRequestPermissionsResult");
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("", "Permision granted");
					opentContactsPage();
					readContactsPermissionOverlay.dismiss();

				} else {
					if (readContactsPermissionOverlay.isShowing()) {
						readContactsPermissionOverlay.dismiss();
						//displayPermissionCheckedFlagOverlay();
					} else if (readPermissionFlagCheckedOverlay.isShowing()) {
						readPermissionFlagCheckedOverlay.dismiss();
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
						after = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
				}
			}
		}
	}


	protected void opentContactsPage() {

		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

		startActivityForResult(intent, PICK_CONTACT);
	}

	@TargetApi(Build.VERSION_CODES.M)
	protected void requestReadContactsPermission() {
		int hasWriteContactsPermission = this.checkSelfPermission(Manifest.permission.READ_CONTACTS);
		if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
			before = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
			if (isFirstTimeAskingPermission(this, Manifest.permission.READ_CONTACTS)) {
				firstTimeAskingPermission(this, Manifest.permission.READ_CONTACTS, false);
				displayReadContactsPermissionOverlay();
			} else if (!before && !after) {
				displayPermissionCheckedFlagOverlay();
			} else {
				displayReadContactsPermissionOverlay();
			}
		} else opentContactsPage();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case PICK_CONTACT:
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
							ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
					Cursor c = this.getContentResolver().query(contactData, projection, null, null, null);
					c.moveToFirst();

					int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
					int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

					String name = c.getString(nameIdx);
					name = StringEscapeUtils.escapeEcmaScript(name);
					String phoneNumber = c.getString(phoneNumberIdx);

					webView.loadUrl("javascript:onContactRequestSucces(\"" + name + "\", \"" + phoneNumber +"\")");

//					if (phoneNumber != null) {
//
//						if (phoneNumber.contains(" ") || phoneNumber.contains(")") || phoneNumber.contains("(") || phoneNumber.contains("-")) {
//							phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
//						}
//
//						if (phoneNumber.length() > 10) {
//							phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
//						}
//					}
//
//					phoneNumberInput.setText(phoneNumber);

					c.close();

				}
				break;
		}
	}

	private void displayPermissionCheckedFlagOverlay() {
		readPermissionFlagCheckedOverlay = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		readPermissionFlagCheckedOverlay.setContentView(R.layout.overlay_dialog_notifications);
		readPermissionFlagCheckedOverlay.show();

		VodafoneButton acceptPermissionBtn = (VodafoneButton) readPermissionFlagCheckedOverlay.findViewById(R.id.buttonKeepOn);
		acceptPermissionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readPermissionFlagCheckedOverlay.dismiss();
			}
		});

		VodafoneButton refusePermisionBtn = (VodafoneButton) readPermissionFlagCheckedOverlay.findViewById(R.id.buttonTurnOff);
		refusePermisionBtn.setVisibility(View.GONE);

		ImageView closeBtn = (ImageView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlayDismissButton);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readPermissionFlagCheckedOverlay.dismiss();
			}
		});

		VodafoneTextView overlayTitle = (VodafoneTextView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlayTitle);
		VodafoneTextView overlayContext = (VodafoneTextView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlaySubtext);

		overlayContext.setText(WebViewCampaignLabels.getOverlay_Gamification_contacts_permission_context_denied());
		overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());

		acceptPermissionBtn.setText(AppLabels.getOk_label());
	}

	private void displayReadContactsPermissionOverlay() {

		readContactsPermissionOverlay = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		readContactsPermissionOverlay.setContentView(R.layout.overlay_dialog_notifications);
		readContactsPermissionOverlay.show();

		VodafoneButton acceptPermissionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonKeepOn);
		acceptPermissionBtn.setText(AppLabels.getPayBillOverlayAcceptBtn());
		acceptPermissionBtn.setOnClickListener(readContactsPermissionListener);

		VodafoneButton refusePermisionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonTurnOff);
		refusePermisionBtn.setText(AppLabels.getPayBillOverlayRefuseBtn());
		refusePermisionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readContactsPermissionOverlay.dismiss();
			}
		});

		ImageView closeBtn = (ImageView) readContactsPermissionOverlay.findViewById(R.id.overlayDismissButton);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readContactsPermissionOverlay.dismiss();
			}
		});

		VodafoneTextView overlayTitle = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlayTitle);
		overlayTitle.setText(AppLabels.getPayBillOverlayTitle());
		VodafoneTextView overlayContext = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlaySubtext);

		overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());
		overlayContext.setText(WebViewCampaignLabels.getOverlay_Gamification_contacts_permission_context());
		acceptPermissionBtn.setText(AppLabels.getAccept_button_label());
		refusePermisionBtn.setText(AppLabels.getDo_later_button_label());
	}

	View.OnClickListener readContactsPermissionListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
						REQUEST_CODE_ASK_PERMISSIONS);
		}
	};

	private void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
		SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
		sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
	}

	private boolean isFirstTimeAskingPermission(Context context, String permission) {
		return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
	}

	public class WebAppInterface {

		@JavascriptInterface
		public void requestContact() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestReadContactsPermission();
			} else {
				opentContactsPage();
			}
		}


	}

}
