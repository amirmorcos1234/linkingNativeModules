package ro.vodafone.mcare.android.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.Hosts;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.custom.ProgressDialogHandler;
import ro.vodafone.mcare.android.interfaces.fragment.base.BaseFragmentCommunicationListener;
import ro.vodafone.mcare.android.rest.ServiceGenerator;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.WallpaperConfig;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;

/**
 * Created by Victor Radulescu on 2/13/2017.
 */

public class BaseActivity extends AppCompatActivity implements BaseFragmentCommunicationListener {

	public static final int REQUEST_CODE_DASHBOARD_RETURNABLE = 1;

	public static final int RESULT_CODE_FINISH_ALL = 1;

	private static final String TAG = "BaseActivity";
	final int failedPingDurationMs = 2000;
	final int counterLimit = 5;
	private ProgressDialog progressDialog;
	int timeOutCounter = 0;
	boolean showFailedToast = true;
	private Subscription subscription;

	private boolean vodafoneBackgroundSet = false;
	private FirebaseAnalytics firebaseAnalytics;

	/**
	 * User to destroy subriptions added to it when activity is destroyed
	 */
	protected CompositeSubscription activityCompositeSubcription = new CompositeSubscription();

	private BroadcastReceiver versionInterceptorReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			displayVersionInterceptorUpdateApp();
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VodafoneController.getInstance().setLifecycleOwner(this);
		VodafoneController.getInstance().setWasScreenOn(true);
		getProgressDialog();
		firebaseAnalytics = FirebaseAnalytics.getInstance(this);
	}

	public void showLoadingDialog() {
		try {
			if (!getProgressDialog().isShowing())
				getProgressDialog().show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopLoadingDialog() {
		try {
			if (getProgressDialog().isShowing()) {
				getProgressDialog().dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopLoadingAfterDuration(long duration) {

		Observable.timer(duration, TimeUnit.SECONDS)
				.onBackpressureDrop()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Long>() {
					@Override
					public void call(Long aLong) {
						stopLoadingDialog();
					}
				});
	}


	public ProgressDialog getProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = ProgressDialogHandler.progressDialogConstructor(this, AppLabels.getLoadingSpinnerOneMoment());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return progressDialog;
	}


	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		resumeTimer();

		LocalBroadcastManager.getInstance(this).registerReceiver(versionInterceptorReceiver, new IntentFilter(ServiceGenerator.UPDATE_REQUIERED_INTENT));

		VodafoneController.getInstance().setAppComesFromBackground(false);
		VodafoneController.getInstance().setFromBackPress(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopTimer();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(versionInterceptorReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                VodafoneController.getInstance().setWasScreenOn(pm.isInteractive());
            } else
                VodafoneController.getInstance().setWasScreenOn(pm.isScreenOn());
	}

	@Override
	protected void onDestroy() {
//	    VodafoneController.getInstance().destroySupportWindow();
		if (VodafoneController.currentActivity() == null) {
			CustomToast.resetCurrentToast();
		}
		try {
			if (!Realm.getDefaultInstance().isInTransaction() && !Realm.getDefaultInstance().isClosed()) {
				Realm.getDefaultInstance().close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		activityCompositeSubcription.clear();
		super.onDestroy();
	}

	void resumeTimer() {
		//System.out.println("resumed");
		subscription = Observable.interval(2, TimeUnit.SECONDS)
				.onBackpressureDrop()
				.observeOn(Schedulers.io())
				.subscribe(new Action1<Long>() {
							   @Override
							   public void call(Long aLong) {
								   checkInternet();
							   }
						   }
				);
	}

	void stopTimer() {
		if (subscription != null && !subscription.isUnsubscribed()) {
			// System.out.println("stopped");
			subscription.unsubscribe();
		}
	}

	private void setVodafoneBackgroundOnWindow(boolean loadLocalVersionFirst) {
		if (vodafoneBackgroundSet) {
			return;
		}
		vodafoneBackgroundSet = true;
		final String[] wallpaperName = WallpaperConfig.setWallpaper();
		Log.d(TAG, "Setting temporary default wallpaper to avoid white screens");
		if (loadLocalVersionFirst) {
			getWindow().setBackgroundDrawableResource(Integer.valueOf(wallpaperName[0]));
		} else {
			BitmapDrawable bitmapDrawable = VodafoneController.getBackgroundBitmapDrawable();
			if (bitmapDrawable != null) {
				getWindow().setBackgroundDrawable(bitmapDrawable);
			} else {
				getWindow().setBackgroundDrawableResource(Integer.valueOf(wallpaperName[0]));
			}
		}
		if (wallpaperName[1].contains("http")) {
			new AsyncTask<Void, Void, BitmapDrawable>() {
				@Override
				protected BitmapDrawable doInBackground(Void... params) {
					try {
						Bitmap bitmap = Glide.with(getApplicationContext())
								.load(wallpaperName[1])
								.asBitmap()
								.diskCacheStrategy(DiskCacheStrategy.ALL)
								//.placeholder(Integer.valueOf(wallpaperName[0]))
								.error(Integer.valueOf(wallpaperName[0]))
								.into(-1, -1)
								.get();
						return new BitmapDrawable(getResources(), bitmap);
					} catch (Exception e) {
						return null;
					}
				}

				protected void onPostExecute(BitmapDrawable drawableBitmap) {
					if (drawableBitmap != null) {
						getWindow().setBackgroundDrawable(drawableBitmap);
						VodafoneController.setBackgroundBitmapDrawable(drawableBitmap);
					}
				}

				;
			}.execute();
		} else {
			Log.d(TAG, "wallpaper already set default");
		}
	}

	protected void setVodafoneBackgroundOnWindow() {
		//don't set background twice, no point in doing that and can also cause flickering
		setVodafoneBackgroundOnWindow(true);
	}

	protected void setVodafoneBackgroundOnWindowIfWindowBackgroundNotSetted() {
		//don't set background twice, no point in doing that and can also cause flickering
		setVodafoneBackgroundOnWindow(false);
	}

	private void checkInternet() {

		boolean hostIsReachable = false;
		//if(Hosts.isHostWithSSL()){
		hostIsReachable = isServerReachable();
		//}
		/*if(!hostIsReachable){
            hostIsReachable =  isConnectedToServer();
        }*/
		try {
			// Log.d("Internet","counter "+timeOutCounter);

			if (hostIsReachable) {
				showFailedToast = true;
				if (timeOutCounter >= counterLimit) {
					showErrorToastReestabilishNoInternetConnection();
				}
				resetCounter();

			} else {
				timeOutCounter++;
				if (timeOutCounter >= counterLimit && showFailedToast) {
					showErrorToastNoInternetConnection();
					showFailedToast = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showErrorToastNoInternetConnection() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				new CustomToast.Builder(BaseActivity.this).message(AppLabels.getNoInternetConnectionToastMessage())
						.success(false).show();
//                new CustomToastOld(BaseActivity.this, BaseActivity.this, AppLabels.getNoInternetConnectionToastMessage(), false).show();
			}
		});
	}

	private void showErrorToastReestabilishNoInternetConnection() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				new CustomToast.Builder(BaseActivity.this).message(AppLabels.getNoInternetConnectionReestablishToastMessage())
						.success(true).show();
//                new CustomToast(BaseActivity.this, BaseActivity.this, AppLabels.getNoInternetConnectionReestablishToastMessage(), true).show();
			}
		});
	}

	public boolean isServerReachable() {

		ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMan.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnected()) {
			try {
				SocketAddress sockaddr = new InetSocketAddress(Hosts.getJustHost(), 80);
				// Create an unbound socket
				Socket sock = new Socket();

				// This method will block no more than timeoutMs.
				// If the timeout occurs, SocketTimeoutException is thrown.
				sock.connect(sockaddr, failedPingDurationMs);
				return true;

			} catch (MalformedURLException e1) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	public boolean isConnectedToServer() {
		try {
			URL myUrl = new URL(Hosts.getHost());
			URLConnection connection = myUrl.openConnection();
			connection.setConnectTimeout(failedPingDurationMs);
			connection.connect();
			return true;
		} catch (Exception e) {
			// Handle your exceptions
			return false;
		}
	}

	private void resetCounter() {
		timeOutCounter = 0;
	}

	public void showWarningToast(final String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				new CustomToast.Builder(BaseActivity.this).message(message).success(true).show();
//                new CustomToast(BaseActivity.this, BaseActivity.this, message, true).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_DASHBOARD_RETURNABLE) {
			if (resultCode == RESULT_CODE_FINISH_ALL) {
				if (!(this instanceof DashboardActivity)) {
					setResult(RESULT_CODE_FINISH_ALL);
					finish();
					return;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void addToActivityCompositeSubcription(Subscription subscription) {
		if (subscription != null) {
			activityCompositeSubcription.add(subscription);
		}
	}

	public void clearActivityCompositeSubscription() {
		activityCompositeSubcription.clear();
	}

	/**
	 * Release memory when the UI becomes hidden or when system resources become low.
	 *
	 * @param level the memory-related event that was raised.
	 */
	public void onTrimMemory(int level) {

		D.e("OnTrimMemory " + level);
		// Determine which lifecycle or system event was raised.
		switch (level) {
			case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
				VodafoneController.getInstance().setAppComesFromBackground(true);
				break;

			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
				D.e("OnTrimMemory Critical");
				VodafoneController.setBackgroundBitmapDrawable(null);

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

				break;

			case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
			case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
			case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

				break;

			default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
				break;
		}
	}


	protected void displayVersionInterceptorUpdateApp() {

		stopLoadingDialog();

		Log.d("VersionInterceptor", "versionApp.getIsRequired() is :" + BuildConfig.VERSION_NAME);

		final Dialog versionInterceptorDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
		versionInterceptorDialog.setContentView(R.layout.overlay_dialog_notifications);


		Button buttonTurnOff = (Button) versionInterceptorDialog.findViewById(R.id.buttonTurnOff);
		buttonTurnOff.setVisibility(View.GONE);

		Button buttonKeepOn = (Button) versionInterceptorDialog.findViewById(R.id.buttonKeepOn);

		VodafoneTextView overlayTitle = (VodafoneTextView) versionInterceptorDialog.findViewById(R.id.overlayTitle);
		VodafoneTextView overlaySubtext = (VodafoneTextView) versionInterceptorDialog.findViewById(R.id.overlaySubtext);
		ImageView overlayDismissButton = (ImageView) versionInterceptorDialog.findViewById(R.id.overlayDismissButton);

		overlayTitle.setText(AppLabels.getVersionUpdateTitle());
		overlaySubtext.setText(Html.fromHtml(AppLabels.getVersionUpdateSummary()).toString());
		buttonKeepOn.setText(AppLabels.getVersionUpdateButton());


		buttonKeepOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(AppLabels.getVersionUpdateStoreUrl()));
				startActivity(intent);
			}
		});

		overlayDismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				versionInterceptorDialog.dismiss();
			}
		});

		versionInterceptorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				versionInterceptorDialog.dismiss();
			}
		});

		versionInterceptorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				closeAllActivitiesAndNavigateToDashboardOrLogin();
			}
		});


		versionInterceptorDialog.show();
	}

	private void closeAllActivitiesAndNavigateToDashboardOrLogin() {
		if (BaseActivity.this instanceof DashboardActivity)
			return;

		if (VodafoneController.findActivity(DashboardActivity.class) != null)
			new NavigationAction(BaseActivity.this).startAction(IntentActionName.DASHBOARD);
		else
			logoutUserService();
		//VodafoneController.clearAllMenuActivitysExceptDashboard();

	}

	/**
	 * Call this in activity's onStart() method to ensure it is not called when app is brought from background
	 *
	 * @param pageName
	 */
	public void callForAdobeTarget(final String pageName) {
		Log.d(TAG, "track page is from background: " + VodafoneController.getInstance().isAppComesFromBackground());
		Log.d(TAG, "track page is from backpress: " + VodafoneController.getInstance().isFromBackPress());
		if (VodafoneController.getInstance().isAppComesFromBackground() || VodafoneController.getInstance().isFromBackPress() || !VodafoneController.getInstance().isWasScreenOn())
			return;

		new AdobeTargetController().trackPage(this, pageName);
	}

	protected void logoutUserService() {
		new AuthenticationService(VodafoneController.getInstance()).logout().subscribe(new RequestSessionObserver<GeneralResponse>() {
			@Override
			public void onCompleted() {
				Log.d("logout", "completed");
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				Log.d("logout", "onError " + e.getMessage());
				new NavigationAction(BaseActivity.this).finishCurrent(true).startAction(IntentActionName.LOGOUT);
			}

			@Override
			public void onNext(GeneralResponse generalResponse) {
				Log.d("logout", "onNext " + generalResponse.getTransactionStatus());
				new NavigationAction(BaseActivity.this).finishCurrent(true).startAction(IntentActionName.LOGOUT);
			}
		});
	}

	public void sendFirebaseEvent(String firebaseEvent, Bundle bundle) {
		firebaseAnalytics.logEvent(firebaseEvent, bundle);
	}
}
