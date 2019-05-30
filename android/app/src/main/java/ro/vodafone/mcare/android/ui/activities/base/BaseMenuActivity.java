package ro.vodafone.mcare.android.ui.activities.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneType;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubbleSingleton;
import ro.vodafone.mcare.android.ui.fragments.dashboard.AddToFavoritesSelectionPageFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.toolbar.DynamicToolbar;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;

/**
 * Created by Victor Radulescu on 12/14/2017.
 */

public abstract class BaseMenuActivity extends ChatBubbleActivity {

	public static String TAG = "BaseMenuActivity";
	public static Logger LOGGER = Logger.getInstance(BaseMenuActivity.class);
	public BaseMenuActivity menuActivity = this;

	public DrawerLayout drawer;
	protected NavigationView navigationView;
	protected VodafoneDialog mDialog;
	private boolean clickedOnce = true;
	protected DynamicToolbar mToolbar;

	public DynamicToolbar getToolbar() {
		return mToolbar;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drawer = findViewById(R.id.drawer_layout);

		drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				KeyboardHelper.hideKeyboard(BaseMenuActivity.this);
			}

			@Override
			public void onDrawerClosed(View drawerView) {

			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});

		setupBackgroudResource();
		initToolbar();
		try {
			if (VodafoneController.getInstance().getUser() != null) {

				setUpNavigationView();

			} else {
				disableNavigationDrawer();
			}
		} catch (Exception ex) {
			D.e("Unknown exception onCreate MenuActivity custom methods." + ex);
			finish();
		}
	}

	private void disableNavigationDrawer() {
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	@Override
	protected void onStart() {
		super.onStart();
		reloadMenu();
		if (UserDataController.getInstance().isSwitchFragmentAfterBundle()) {
			setupFragmentAfterBundleFromIntentAction(getIntent());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (UserDataController.getInstance().isSwitchFragmentAfterBundle()) {
			setupFragmentAfterBundleFromIntentAction(intent);
		}
	}

	private void initToolbar() {
		mToolbar = (DynamicToolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		if (VodafoneController.getInstance().getUser() == null) {
			mToolbar.hideMenuButton();
		}

		mToolbar.setMenuButtonCLickListner(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openDrawer();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		clickedOnce = true;
		LOGGER.d("MenuActivity onResume");
	}

	@Override
	protected void onResumeFragments() {
//        D.e("bad");
		super.onResumeFragments();
		LOGGER.d("MenuActivity onResumeFragments");
	}

	public void openDrawer() {
		KeyboardHelper.hideKeyboard(this);
		try {
			VodafoneController.getInstance().supportWindow(this).forceCloseDontHideBubble(BaseMenuActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		drawer.openDrawer(GravityCompat.END);
	}

	public boolean closeDrawers() {
		if (drawer.isDrawerOpen(GravityCompat.END)) {
			drawer.closeDrawers();
			return true;
		}
		return false;
	}

	public void closeDrawersWithDelay() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				VodafoneController.getInstance().handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (drawer == null) {
							return;
						}
						closeDrawers();
						// make operation on UI - on example
						// on progress bar.
					}
				}, 300);
			}
		}).start();
	}

	public void setUpNavigationView() {

		navigationView = (NavigationView) findViewById(R.id.nav_view);

		if (VodafoneController.getInstance() == null) {
			finish();
		}

		ImageButton imageButton = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.header_btn);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDrawers();
			}
		});
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {

				closeDrawers();

				switch (item.getItemId()) {
					case R.id.item_home:
						navigateToHome();
						callForAdobeTarget(AdobePageNamesConstants.PG_DASHBOARD);
						break;
					case R.id.item_bills_and_payments:
						navigateToPayBill();
						break;
					case R.id.item_paybill:
						navigateToPayBill();
						break;
					case R.id.item_services_details:
						navigateToCallDetails();
						break;
					case R.id.item_settings:
						navigateToSettings();
						break;
					case R.id.item_your_profile:
						navigateToYourProfile();
						break;
					case R.id.item_recharge:
						navigateToTopUp();
						break;
					case R.id.item_offers:
						navigateToBEO();
						break;
					case R.id.item_services:
						navigateToYourServices();
						break;
					case R.id.item_traveling_abroad:
						navigateToTravellingAboard();
						break;
					case R.id.item_logout:
						logout();
						break;
					case R.id.item_login:
						login();
						break;
					case R.id.item_billing_overview:
						navigateToBillingOverview();
						break;
					case R.id.item_loyalty:
						navigateToLoyaltySelectionPage();
						break;
					case R.id.item_vodafoneshops:
						navigateToStoreLocator();
						break;
					case R.id.item_vodafoneTv:
						navigateToVodafoneTvPage();
						break;
					case R.id.item_my_cards:
						navigateToMyCardsPage();
						break;
				}
				setSideMenuTealiumTrackingEvent(item.getTitle().toString());
				if (item.getItemId() != R.id.item_logout) {
					VodafoneController.clearAllMenuActivitysExceptDashboard();
				}
				return true;

			}
		});
	}
	private void setSideMenuTealiumTrackingEvent(String button_tittle){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.sideMenu);
        tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.sideMenuEvent+button_tittle);
        tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("BaseMenuActivity", tealiumMapEvent);


    }

	public void reloadMenu() {
		if (navigationView != null && VodafoneController.getInstance().getUser() != null) {
			navigationView.getMenu().clear();
			hideMenuItems(navigationView);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		closeDrawers();
	}

	private void logout() {
		UserDataController.getInstance().setCurrentDashboardAction(IntentActionName.NONE);
		this.checkChatActive();

	}

	private void checkChatActive() {
		if (VodafoneController.getInstance().isChatConnected()) {
			displayDialog();
		} else {
			displayLogOutDialog();
		}
	}

	private void displayLogOutDialog() {
		mDialog = new VodafoneDialog(menuActivity, AppLabels.getLogoutConfirmationLabel())
				.setPositiveMessage(getString(R.string.dialog_give_up))
				.setNegativeMessage("Confirmă")
				.setDismissActionOnPositive()
				.setNegativeAction(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if (menuActivity != null) {
							new NavigationAction(menuActivity).finishCurrent(true).startAction(IntentActionName.LOGOUT);
						}
					}
				});
		mDialog.show();
	}

	private void login() {
		new NavigationAction(this).finishCurrent(true).startAction(IntentActionName.LOGIN);
	}

	private void displayDialog() {
		final Dialog overlyDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
		try {
			Window window = overlyDialog.getWindow();
			if (window != null) {
				window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
			}
		} catch (Exception ignored) {
		}
		final View content = LayoutInflater.from(this).inflate(R.layout.overlay_dialog_support, null);
		overlyDialog.setContentView(content);
		final Point windowPadding = ViewUtils.getWindowPadding(getWindowManager(), this.getWindow());
		((RelativeLayout) overlyDialog.findViewById(R.id.main_container_for_dialog)).setPadding(0, windowPadding.x, 0, windowPadding.y);
		overlyDialog.show();

		Button closeChat = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
		Button cancel = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

		VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
		VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText("Asistență");
        overlaySubtext.setText("Ești sigur că vrei să închizi această conversație?");

		closeChat.setText("Da");

		cancel.setText("Nu");

		ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
		overlayDismissButton.setImageResource(R.drawable.close_48_white);

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				overlyDialog.dismiss();
			}
		});

		closeChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatBubbleSingleton.getInstance().stopAllObservables();
				ChatBubbleSingleton.getInstance().clearMessageList();

				VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 9, VoiceOfVodafoneCategory.Chat, null, "Ai încheiat conversaţia.", "Ok, am înţeles", "Conversație nouă",
						true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.GoToSupportChat);
				VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
				VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
				VodafoneController.getInstance().setChatConnected(false);
				// VodafoneController.getInstance().isChatActive = false;
				overlyDialog.dismiss();

				new CustomToast.Builder(BaseMenuActivity.this).message("Ai încheiat conversaţia.").success(true).show();

				VodafoneController.getInstance().chatService().logOutChat(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, VodafoneController.getInstance().getUserProfile().getEmail())
						.subscribe(new RequestSessionObserver<String>() {
							@Override
							public void onNext(String response) {
								D.w("logOutChat response = " + response);
							}
						});
				if (menuActivity != null) {
					new NavigationAction(menuActivity).finishCurrent(true).startAction(IntentActionName.LOGOUT);
				}
				displayLogOutDialog();
			}
		});

		overlayDismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				overlyDialog.dismiss();
			}
		});

		overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				overlyDialog.dismiss();
			}
		});


	}

	public void navigateToPayBill() {
		new NavigationAction(this).setExtraParameter(null).startAction(IntentActionName.PAY_BILL, false, true);
	}

	public void navigateToTopUp() {
		new NavigationAction(this).startAction(IntentActionName.TOP_UP, false, true);
	}

	public void navigateToYourServices() {
		if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
			new NavigationAction(this).startAction(IntentActionName.COST_CONTROL, false, true);
		} else {
			new NavigationAction(this).startAction(IntentActionName.SERVICES_PRODUCTS, false, true);
		}
	}

	public void navigateToTravellingAboard() {
		new NavigationAction(this).startAction(IntentActionName.TRAVELLING_ABOARD, false, true);
	}

	public void navigateToStoreLocator() {
		new NavigationAction(this).startAction(IntentActionName.STORE_LOCATOR, false, true);
	}

	public void navigateToCallDetails() {
		new NavigationAction(this).setExtraParameter(null).startAction(IntentActionName.CALL_DETAILS, false, true);
	}

	public void navigateToBEO() {
		new NavigationAction(this).startAction(IntentActionName.OFFERS, false, true);

	}

	public void navigateToSettings() {
		new NavigationAction(this).startAction(IntentActionName.SETTINGS, false, true);
	}

	public void navigateToYourProfile() {
		new NavigationAction(this).startAction(IntentActionName.YOUR_PROFILE, false, true);
	}

	private void navigateToHome() {
/*
		if (VodafoneController.getInstance().getDashboardActivity() != null) {
			VodafoneController.getInstance().getDashboardActivity().reload();
		} else {
			DashboardController.reloadDashboardOnResume();
		}*/
		if (this instanceof DashboardActivity) {
			//part of fix for 6944
			closeDrawersWithDelay();
			return;
		}

		DashboardController.reloadDashboardOnResume();
		setResult(RESULT_CODE_FINISH_ALL);

		new NavigationAction(this).startAction(IntentActionName.DASHBOARD, true);

	}

	private void navigateToBillingOverview() {
		new NavigationAction(this).startAction(IntentActionName.BILLING_OVERVIEW, false, true);
	}

	private void navigateToLoyaltySelectionPage() {
		new NavigationAction(this).startAction(IntentActionName.LOYALTY_SELECTION_ACTIVITY, false, true);
	}

	private void navigateToVodafoneTvPage() {
		new NavigationAction(this).startAction(IntentActionName.VODAFONE_TV, false, true);
	}

	private void navigateToMyCardsPage() {
		new NavigationAction(this).startAction(IntentActionName.MY_CARDS, false, true);
	}

	public void fragmentAnimationFinishedListener() {
	}

	public  String extractNumber(final String text) {
		String wordToFind = "07";
		if(text.contains(wordToFind)) {
			int index = text.indexOf(wordToFind);
			return text.substring(index, index + 10);
		}
		return "";
	}

	public void displayDialog(final VoiceOfVodafoneType voiceOfVodafoneType, final VoiceOfVodafone voiceOfVodafone) {
		final CustomEditText phoneNumberInput;
		final CustomEditText nameInput;
		final VodafoneButton buttonAddToFavorites;
		String message;
		String msisdn;
		final Dialog overlyDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		overlyDialog.setContentView(voiceOfVodafoneType.getLayout());
		overlyDialog.show();

		ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
		buttonAddToFavorites = (VodafoneButton) overlyDialog.findViewById(R.id.buttonAddToFavorites);

		phoneNumberInput = (CustomEditText) overlyDialog.findViewById(R.id.phone_number_field);

		nameInput = (CustomEditText) overlyDialog.findViewById(R.id.name_field);

		VoiceOfVodafoneController controller = VoiceOfVodafoneController.getInstance();
		List<VoiceOfVodafone> listVov = controller.getVoiceOfVodafones();
		for (VoiceOfVodafone vov : listVov) {
			if ((vov.getCategory() == VoiceOfVodafoneCategory.TransferCredit || vov.getCategory() == VoiceOfVodafoneCategory.Recharge) && vov.getLeftAction().equals(VoiceOfVodafoneAction.AddToFavoritesNumbers)) {
				message = vov.getMessage();
				phoneNumberInput.setText(extractNumber(message));
			}
		}

		nameInput.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				nameInput.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (s.toString().trim().length() == 0) {
							buttonAddToFavorites.setEnabled(false);
						} else {
							buttonAddToFavorites.setEnabled(true);
						}
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
				return false;
			}
		});
		buttonAddToFavorites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				overlyDialog.dismiss();
				//call api 28
				try {
					String phoneNumber = phoneNumberInput.getText().toString();
					String name = nameInput.getText().toString();
					AddToFavoritesSelectionPageFragment addToFavoritesSelectionPageFragment = new AddToFavoritesSelectionPageFragment();
					addToFavoritesSelectionPageFragment.addFavoriteNumber(getApplicationContext(), phoneNumber, name, voiceOfVodafone);
				} catch (Exception ex) {
					LOGGER.e("error in addToFavoritesNumbers", ex);
				}

			}
		});

		overlayDismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				overlyDialog.dismiss();
			}
		});

		overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

				overlyDialog.dismiss();
			}
		});
	}

	@Override
	public void startActivity(Intent intent) {
		if (VodafoneController.getInstance().isSessionExpired()) {
			return;
		}
		super.startActivity(intent);
	}

	public abstract void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception;

	public void setupFragmentAfterBundleFromIntentAction(Intent intent) {
		if (this instanceof DashboardActivity) {
			return;
		}
		if (UserDataController.getInstance().getCurrectLoginAction() == null) {
			UserDataController.getInstance().setCurrentDashboardAction(
					UserDataController.getInstance().getCurrentNotificationDashboardAction().getRedirectIntentName());
		}
		if (intent != null) {
			Log.d("MenuActivity", "extra intent here");
			boolean getFromUserData = false;
			boolean getExtraParameterFromUserData = false;


			if (intent.getExtras() == null) {
				if (UserDataController.getInstance().getCurrentDashboardAction().getFragmentClassName() == null ||
						UserDataController.getInstance().getCurrentDashboardAction().getFragmentClassName().isEmpty()) {
					return;
				} else {
					getFromUserData = true;
				}
				if (UserDataController.getInstance().getCurrentDashboardAction().getExtraParameter() == null ||
						UserDataController.getInstance().getCurrentDashboardAction().getExtraParameter().isEmpty()) {
					return;
				} else {
					getExtraParameterFromUserData = true;
				}
			}

			Log.d("MenuActivity", "extra here");
			String fragmentName = getFromUserData ?
					UserDataController.getInstance().getCurrentDashboardAction().getFragmentClassName() :
					intent.getExtras().getString(NavigationAction.FRAGMENT_CLASS_NAME_BUNDLE_KEY);


			String extraParameterName = getExtraParameterFromUserData ?
					UserDataController.getInstance().getCurrentDashboardAction().getExtraParameter() :
					intent.getExtras().getString(NavigationAction.EXTRA_PARAMETER_BUNDLE_KEY);

			try {
				switchFragmentOnCreate(fragmentName, extraParameterName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			UserDataController.getInstance().setSwitchFragmentAfterBundle(false);
		}
		UserDataController.getInstance().setCurrentDashboardAction(IntentActionName.NONE);
		UserDataController.getInstance().setCurrentNotificationDashboardAction(IntentActionName.NONE);
	}

	private void setupBackgroudResource() {

		if (this instanceof DashboardActivity) {
			setVodafoneBackgroundOnWindowIfWindowBackgroundNotSetted();
		} else {
			Log.d(TAG, "setupBackgroundResource: Menu");
			getWindow().setBackgroundDrawableResource(R.drawable.second_background_morning);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	private void hideMenuItems(NavigationView navigationView) {
		if (VodafoneController.getInstance() != null) {
			navigationView.inflateMenu(VodafoneController.getInstance().getUser().getMenu());


			if (VodafoneController.getInstance().getUser() instanceof EbuMigrated && !AppConfiguration.getShowEbuProductAndServices()) {
				MenuItem menuItem = navigationView.getMenu().findItem(R.id.item_services);
				if (menuItem != null)
					menuItem.setVisible(false);
			}

			if (!AppConfiguration.getShowVodafoneTvMenu()) {
				MenuItem menuItem = navigationView.getMenu().findItem(R.id.item_vodafoneTv);
				if (menuItem != null) {
					menuItem.setVisible(false);
				}
			}

		}


	}
}
