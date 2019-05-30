package ro.vodafone.mcare.android.ui.activities.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadger;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.ActivityResult;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.RxActivityResult;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.VodafoneNotificationManager;
import ro.vodafone.mcare.android.widget.TabMenu.ScrollableTabCard;
import rx.functions.Action1;

import static android.R.attr.state_checked;
import static android.R.attr.state_enabled;
import static ro.vodafone.mcare.android.ui.activities.support.SupportPopupWindow.INPUT_METHOD_NEEDED;

public class SupportWindow {
	public static final String TAG = SupportWindow.class.getSimpleName();
	private static final int[][] TEXT_STATES = new int[][]{
			new int[]{state_enabled, state_checked},          // enabled checked
			new int[]{state_enabled, -state_checked},         // enabled unchecked
			new int[]{-state_enabled, state_checked},         // disabled checked - invalid
			new int[]{-state_enabled, -state_checked},        // disabled unchecked

	};
	private static final int[] TEXT_COLORS = new int[]{
			Color.BLACK,                                    // enabled checked
			Color.BLACK,                                    // enabled unchecked
			Color.parseColor("#cccccc"),         // disabled checked - invalid
			Color.parseColor("#cccccc")          // disabled unchecked
	};
	private static final int[] ICON_COLORS_AFTER_SELECT = new int[]{
			Color.parseColor("#FFCA1A02"),                  // enabled checked
			Color.parseColor("#CCCCCC"),                    // enabled unchecked
			Color.parseColor("#CCCCCC"),                    // disabled checked - invalid
			Color.parseColor("#cccccc")                     // disabled unchecked
	};
	private static final int[] ICON_COLORS_BEFORE_SELECT = new int[]{
			Color.parseColor("#CCCCCC"),                  // enabled checked
			Color.parseColor("#CCCCCC"),                    // enabled unchecked
			Color.parseColor("#CCCCCC"),                    // disabled checked - invalid
			Color.parseColor("#cccccc")                     // disabled unchecked
	};
	private static final ColorStateList TEXT_COLOR_STATE_LIST = new ColorStateList(TEXT_STATES, TEXT_COLORS);
	private static final ColorStateList ICON_COLOR_STATE_LIST = new ColorStateList(TEXT_STATES, ICON_COLORS_AFTER_SELECT);

	private static final int REQUEST_CODE_OVERLAY = 0x441;
	public static int FAQ_ITEM_POSITION = 0;
	public static int CHAT_ITEM_POSITION = 1;
	public static int EMAIL_ITEM_POSITION = 2;
	@SuppressWarnings("FieldCanBeLocal")
	public Activity activity;
	private WindowManager windowManager;
	public int initialMessageCounter = 0;
	public Views views;
	public MyChatView currentView;
	public StartChatRequest startChatRequest;
	public List<JsonList> mJsonLists;
	public String channel = "mCare";
	public String email;
	public String firstName;
	public String lastName;
	boolean mightBeAClose = true;
	public Context context;
	private boolean wasSnapCard = true;
	private int bottomMarginWithKeyboard = 0;//-150;
	private ChatService chatService;
	private boolean kShown = false;
	private int initMessages;
	private boolean wasPreviousFaq = false;
	private SupportPopupWindow window;
	private ViewGroup rootLayout;
	private SparseArray<Parcelable> hierarchyState;
	private SparseArray<Parcelable> emailHierarchyState;
	private SparseArray<Parcelable> faqHierarchyState;
	private SupportEmailView sev;
	private SupportFaqView sfv;
	private DisplayType lastDisplayType = DisplayType.NONE;
	private boolean vovInProgress = false;
	private boolean shopRequestInProcess = true;
	private boolean emailBlackListed;
	private boolean unhandledError = true;
	private SupportWindowVisibilityListener visibilityListener;
	private Stack<Integer> navStack = new Stack<>();
	private boolean isOpened = false;
	private List<View> editTextList;
	private boolean dockIsGone = false;

	private boolean isChatSecondView = false;
	public int secondViewIndex = -1;

	private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {

				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					switch (item.getItemId()) {

						case R.id.item_faq:
							changeMenuItemCheckedStateColor(views.navigation);
//                            bottomMarginWithKeyboard = -150;
							pushNavigationItemToBackStack(item);
							if (views.navigation.getSelectedItemId() != R.id.item_faq || isSnapCard()) {
								if (views.content != null)
									inflateFaqLayout();
							}
							openSpanChatIfNeededRecyclerView();

							return true;
						case R.id.item_chat:
							changeMenuItemCheckedStateColor(views.navigation);
							pushNavigationItemToBackStack(item);
							D.w();
							if (views.navigation.getSelectedItemId() != R.id.item_chat) {
								if (views.content != null) {
									inflateChatLayout();
								}
							}
							openSpanChatIfNeeded();

							return true;
						case R.id.item_email:
							changeMenuItemCheckedStateColor(views.navigation);
//                            bottomMarginWithKeyboard = -150;
							pushNavigationItemToBackStack(item);
							if (views.navigation.getSelectedItemId() != R.id.item_email) {
								if (views.content != null)
									inflateEmailLayout();
							}
							openSpanChatIfNeeded();

							return true;
					}
					return false;
				}
			};
	private View.OnTouchListener editTextTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

				D.d("onTouch()");
				if (isOpened)
					return false;

				onKShow();

				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (isOpened)
							return;

					}
				}, 100);
			}
			return false;
		}
	};

	private SupportWindow(Activity activity) {
		this.activity = activity;
		this.windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
	}

	public static SupportWindow createInstance(Activity activity) {
		return new SupportWindow(activity);
	}

	public void updateSupportWindow(Activity activity) {
		this.activity = activity;
		this.windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
	}

	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}

	public boolean isChatSecondView() {
		return isChatSecondView;
	}

	public void setChatSecondView(boolean chatSecondView) {
		isChatSecondView = chatSecondView;
	}

	public int getSecondViewIndex() {
		return secondViewIndex;
	}

	public void setSecondViewIndex(int secondViewIndex) {
		this.secondViewIndex = secondViewIndex;
	}

	public void show() {
		show(lastDisplayType);
	}

	public void show(boolean snapCard) {
		show(lastDisplayType, snapCard);
	}

	public void show(DisplayType type) {
		boolean isSnapCardAllowed = false;
		if ((VodafoneController.getInstance().getUser() instanceof EbuNonMigrated) ||
				(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
			isSnapCardAllowed = true;
		} else isSnapCardAllowed = !(activity instanceof DashboardActivity);

		show(type, isSnapCardAllowed);
	}

	public void show(DisplayType type, boolean snapCard) {
		lastDisplayType = type;
		if (activity.isFinishing() || activity.isDestroyed())
			return;
		if (isShowing())
			return;

		if (type != DisplayType.NONE)
			snapCard = false;
		checkDrawOverlayPermission(type, snapCard);

	}

	public DisplayType getLastDisplayType() {
		return lastDisplayType;
	}

	@SuppressLint("InflateParams")
	private void showInternal(DisplayType type, boolean snapCard) {
		releaseViews();

		rootLayout = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.window_support, null);
		rootLayout.setBackground(new BitmapDrawable());
		views = new Views();
		ButterKnife.bind(views, rootLayout);
		views.isSnapCard = snapCard;
		wasSnapCard = snapCard;
		views.window_search_box.setVisibility(View.GONE);

		ViewGroup.LayoutParams params2 = views.recyclerView.getLayoutParams();
		params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
		views.recyclerView.setLayoutParams(params2);

		if ((!snapCard) && (type == DisplayType.NONE)) {
			type = DisplayType.FAQ;
			views.isSnapCard = false;
		}
		if (snapCard)
			type = DisplayType.NONE;

		createWindow(snapCard);

		if (RealmManager.getRealmObject(Profile.class) != null)
			emailBlackListed = ((Profile) RealmManager.getRealmObject(Profile.class)).isEmailBlacklisted();
		initNavigationMenu();

//        scv = new SupportChatView(this);
		try {
			if (VodafoneController.isAnyActivityNotNull()) {
				sev = new SupportEmailView(this);
				sfv = new SupportFaqView(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ImageView closeButton = (ImageView) views.cardTitle.findViewById(R.id.close_button);
		views.minimizeButton = (ImageView) views.cardTitle.findViewById(R.id.minimize_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (currentView instanceof SlyceMessagingView) {
					SlyceMessagingView smv = (SlyceMessagingView) currentView;
					smv.unreadMessagesCount = 0;
				}
				close(true, false, false); //FIXME Lucian if X is clicked we donut want to "keepState"
			}
		});

		views.minimizeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				minimize();
			}
		});

		views.goPCButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				displayGoToPCOverlay();
			}
		});
		views.goPCButton.setVisibility(View.GONE);

		try {

			if (!isChatSecondView || secondViewIndex == -1)
				getInfoFromUserProfile();

		} catch (Exception e) {
			D.e("e = " + e);

		}

		switch (type) {
			case FAQ:
				//Tealium Track Event
				Map<String, Object> tealiumMapEventFaq = new HashMap<>(6);
				tealiumMapEventFaq.put("screen_name", "help&support");
				tealiumMapEventFaq.put("event_name", "mcare:help&support:button:faq");
				tealiumMapEventFaq.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
				TealiumHelper.trackEvent("event_name", tealiumMapEventFaq);

				inflateFaqLayout();
				animateStartViewBig();
				views.isSnapCard = false;
				break;
			case CHAT:
				//Tealium Track Event
				Map<String, Object> tealiumMapEventChat = new HashMap<>(6);
				tealiumMapEventChat.put("screen_name", "help&support");
				tealiumMapEventChat.put("event_name", "mcare:help&support:button:chat");
				tealiumMapEventChat.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
				TealiumHelper.trackEvent("event_name", tealiumMapEventChat);

				inflateChatLayout();
				animateStartViewBig();
				views.isSnapCard = false;
				break;
			case EMAIL:
				//Tealium Track Event
				Map<String, Object> tealiumMapEventEmail = new HashMap<>(6);
				tealiumMapEventEmail.put("screen_name", "help&support");
				tealiumMapEventEmail.put("event_name", "mcare:help&support:button:email");
				tealiumMapEventEmail.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
				TealiumHelper.trackEvent("event_name", tealiumMapEventEmail);

				inflateEmailLayout();
				animateStartViewBig();
				views.isSnapCard = false;
				break;
			default:
				views.minimizeButton.setVisibility(View.GONE);
				uncheckAll();
				animateStartSnapCard();
				removeFAQPageViews();
				views.isSnapCard = true;
		}

		restoreHierarchyState();

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap<>(6);
		tealiumMapView.put("screen_name", "help&support");
		tealiumMapView.put("journey_name", "help&support");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		SupportTrackingEvent event = new SupportTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);

		if (visibilityListener != null) {
			visibilityListener.onVisibilityChanged(true);
		}

		if (snapCard) uncheckAll();//faq is selected when snapcard is displayed

		displayAllowedButtons();
	}

	public void getInfoFromUserProfile() {
		UserProfile userProfile = VodafoneController.getInstance().getUserProfile();

		if (userProfile.getEmail() != null && !userProfile.getEmail().equals("")) {
			email = userProfile.getEmail();
		}
		if (userProfile.getLastName() != null && !userProfile.getLastName().equals(""))
			lastName = userProfile.getLastName();
		if (userProfile.getFirstName() != null && !userProfile.getFirstName().equals(""))
			firstName = userProfile.getFirstName();
	}

	void displayAllowedButtons() {

		if (!AppConfiguration.isEmailButtonVisible().toLowerCase().equals("true") && !AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
			hideDockMenu();
		else if (!AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
			hideChatButton();
		else if (!AppConfiguration.isEmailButtonVisible().toLowerCase().equals("true"))
			hideEmailButton();

	}

	private void hideChatButton() {
		EMAIL_ITEM_POSITION = 1;
		CHAT_ITEM_POSITION = 0;
		D.d("HIDE CHAT BUTTON");
		if (views != null)
			views.navigation.getMenu().removeItem(R.id.item_chat);
	}

	private void hideEmailButton() {
		D.d("HIDE EMAIL BUTTON");
		EMAIL_ITEM_POSITION = 0;

//        D.d("HIDE CHAT BUTTON");
		if (views != null)
			views.navigation.getMenu().removeItem(R.id.item_email);
	}

	private void hideDockMenu() {
		D.d("HIDE DOCK MENU");
		if (!(isSnapCard() || wasSnapCard()) && views != null)
			forceHideDockMenu();
		else {
			hideChatButton();
			hideEmailButton();
		}
	}

	void forceHideDockMenu() {
		if (views != null) {
			views.dock_menu.setVisibility(View.GONE);
			dockIsGone = true;
			if (views.scrollView.getVisibility() == View.VISIBLE) {
				views.scrollView.setBackgroundResource(R.drawable.rounded_corner_support_bottom);
			} else if (views.recyclerView.getVisibility() == View.VISIBLE) {
				views.recyclerView.setBackgroundResource(R.drawable.rounded_corner_support_bottom);
				views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
			}

		}
	}

	public void updateDockMenu() {
		if (views != null && !dockIsGone) {
			views.dock_menu.setVisibility(View.VISIBLE);
			views.scrollView.setBackgroundResource(0);
			views.recyclerView.setBackgroundResource(0);
			views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
			setBottomNavigationMenuVisibility(true);
			VodafoneController.getInstance().handler.post(new Runnable() {
				@Override
				public void run() {
					try {
						views.dock_menu.setVisibility(View.VISIBLE);
						views.scrollView.setBackgroundResource(0);
						views.recyclerView.setBackgroundResource(0);
						views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
						setBottomNavigationMenuVisibility(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		displayAllowedButtons();
	}

	private void createWindow(final boolean snapCard) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels - getStatusBarHeight();
		height = snapCard ? ViewGroup.LayoutParams.WRAP_CONTENT : height;
		int width = displayMetrics.widthPixels;

		window = new SupportPopupWindow(rootLayout, width, height, true);

		window.setWindowLayoutMode(width, height);
		window.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
		window.setOutsideTouchable(false);
		window.setTouchModal(false);
		window.setFocusable(true);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		window.setLayoutInScreenEnabled(true);


		//window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		window.setInputMethodMode(INPUT_METHOD_NEEDED);
		window.setAnimationStyle(0);
		window.showAtLocation((IBinder) null, Gravity.BOTTOM, 0, 0);

		window.getContentView().setPadding(0, 0, 0, 0);
		Log.d(SupportWindow.class.getSimpleName(), "CREATE WINDOW");

		if (isSeamless()) {
			//disable chat
			if (views != null && CHAT_ITEM_POSITION != 0) {
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setCheckable(false);
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setEnabled(false);
				setUpItemStateColor(views.navigation);
			}
		} else {
			D.d("API 2- GOOD  *** CHECK IS IN ACTIVE TIME = ENTER CHAT FLOW OR NOT");
			/*if (!AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
				checkTimeAndEligibility(new RequestSessionObserver<GeneralResponse>() {
					@Override
					public void onNext(GeneralResponse response) {
						unhandledError = false;
						if (response.getTransactionStatus() == 2) {
							if (views != null && CHAT_ITEM_POSITION != 0) {
								views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setCheckable(false);
								views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setEnabled(false);
								setUpItemStateColor(views.navigation);
							}
						}
					}

					@Override
					public void onCompleted() {
						super.onCompleted();
						stopLoading();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);//1 here
						D.e("ERROR  = " + e);
						stopLoading();
						if (unhandledError && views != null && CHAT_ITEM_POSITION != 0) {
							views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setCheckable(false);
							views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setEnabled(false);
							setUpItemStateColor(views.navigation);
						}
					}
				});*/
		}

	}

	public boolean isSeamless() {
		User user = VodafoneController.getInstance().getUser();
		boolean isSeamless = VodafoneController.getInstance().isSeamless();
		SeamlessPostPaidHighAccess u1 = new SeamlessPostPaidHighAccess();
		SeamlessPostPaidsLowAccess u2 = new SeamlessPostPaidsLowAccess();
		SeamlessPrepaidHybridUser u3 = new SeamlessPrepaidHybridUser();
		SeamlessPrepaidUser u4 = new SeamlessPrepaidUser();
		String className = user.getClass().toString();
		String u1className = u1.getClass().toString();
		String u2className = u2.getClass().toString();
		String u3className = u3.getClass().toString();
		String u4className = u4.getClass().toString();
		return className.equalsIgnoreCase(u1className)
				|| className.equalsIgnoreCase(u2className)
				|| className.equalsIgnoreCase(u3className)
				|| className.equalsIgnoreCase(u4className)
				|| isSeamless;
	}

	public void destroy() {
		Log.d(SupportWindow.class.getSimpleName(), "DESTROY WINDOW");
		if (window != null) {
			window.dismiss();
			window = null;
		}

	}

	private void initNavigationMenu() {
		if (views != null) {
			views.navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
			views.navigation.getMenu().getItem(0).setCheckable(false);

			views.navigation.setVisibility(View.VISIBLE);
			setupNavigation();
		}
	}

	public void resetUnreadNotification() {
		if (currentView instanceof SlyceMessagingView) {
			SlyceMessagingView smv = (SlyceMessagingView) currentView;
			initialMessageCounter = smv.getTotalNotifications();
			initMessages = smv.getMessageCount();
			smv.unreadMessagesCount = 0;
			smv.updateBadge();
		}
	}

	void minimize() {
		D.i("isSnapcard = " + ChatBubbleSingleton.getMinimizeToSnapCard());

		if (!ChatBubbleSingleton.getMinimizeToSnapCard()) {

			lastDisplayType = getDispayType();
			saveHierarchyState();

			boolean isMinimized = !ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut() && VodafoneController.getInstance().isChatConnected();

			ChatBubbleSingleton.getInstance().setMinimized(isMinimized, false);
			ChatBubbleSingleton.getInstance().setDisplayType(lastDisplayType);

			animateClose();

			initMessages = -232;
			if (currentView instanceof SlyceMessagingView) {
				SlyceMessagingView smv = (SlyceMessagingView) currentView;
				initialMessageCounter = smv.getTotalNotifications();
				initMessages = smv.getMessageCount();
				smv.unreadMessagesCount = 0;
				smv.updateBadge();
			} else if (currentView instanceof SupportChatSecondView) {
				isChatSecondView = true;
				secondViewIndex = ((SupportChatSecondView) currentView).infoSelectedIndex;
			}
//        setChatBubbleMinimized(getIsVoV());//FIXME Lucian please review this line if possible
		} else {
			if (currentView instanceof SlyceMessagingView) {
				SlyceMessagingView smv = (SlyceMessagingView) currentView;
				initialMessageCounter = smv.getTotalNotifications();
				initMessages = smv.getMessageCount();
				smv.unreadMessagesCount = 0;
				smv.updateBadge();
			}

			this.unselectItemsAfterMinimize();
			forceClose(false, true, false);
			((ChatBubbleActivity) VodafoneController.currentActivity()).getChatBubble().goToAssistanceSnapCard();

		}
	}

	private void unselectItemsAfterMinimize() {
		if (views == null)
			return;

		views.navigation.getMenu().getItem(FAQ_ITEM_POSITION).setCheckable(false);
		views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setCheckable(false);
		views.navigation.getMenu().getItem(EMAIL_ITEM_POSITION).setCheckable(false);
	}

	public void resumeSupportWindow() {
		if (currentView instanceof SlyceMessagingView && VodafoneController.getInstance().isChatConnected()) {
			ChatBubbleSingleton.getInstance().setMinimized(false, false);
			SlyceMessagingView smv = (SlyceMessagingView) currentView;
			initialMessageCounter = smv.getTotalNotifications();
			VodafoneController.getInstance().setBadgeCount(0);
			smv.updateBadge();
			VodafoneNotificationManager.cancelNotification(1, getContext());
		}
	}

	public void forceClose(boolean showVoV, boolean keepState, boolean isRed) {
		if (currentView instanceof SlyceMessagingView) {
			if (showVoV) {
				lastDisplayType = getDispayType();
				saveHierarchyState();

				initMessages = -232;
				if (currentView instanceof SlyceMessagingView) {
					SlyceMessagingView smv = (SlyceMessagingView) currentView;
					initMessages = smv.getMessageCount();
				}
				setChatBubbleMinimized(getIsVoV());
				if (!getIsVoV())
					forceHideBubble();
				releaseViews();
			} else {
				setChatBubbleMinimized(isRed);
				if (!isRed)
					forceHideBubble();
				releaseViews();
			}
		} else {
			setChatBubbleMinimized(isRed);
			if (!keepState) {
				lastDisplayType = DisplayType.NONE;
				navStack.clear();
				clearHierarchyState();
			}
			if (!isRed)
				forceHideBubble();
			releaseViews();
		}
	}

	public void forceCloseDontHideBubble(BaseMenuActivity act) {
		if (views == null) {
			return;
		}
		boolean wasShowing = views.isShowing;
		setChatBubbleMinimized(false);
		lastDisplayType = DisplayType.NONE;
		navStack.clear();
		clearHierarchyState();
		releaseViews();
		if (activity instanceof ChatBubbleActivity) {
			final ChatBubbleActivity cba = (ChatBubbleActivity) activity;
			cba.getChatBubble().forceHide();
		}
		if (wasShowing)
			act.getChatBubble().getChatButton().setVisibility(View.VISIBLE);
	}

	private void forceHideBubble() {
		if (activity instanceof ChatBubbleActivity) {
			final ChatBubbleActivity cba = (ChatBubbleActivity) activity;
			cba.getChatBubble().forceHide();
		}
	}

	public void closeFromGOPcButton() {
		showLoadingDialog();
		if ((currentView != null && currentView instanceof SlyceMessagingView)) {
			ChatBubbleSingleton.getInstance().stopAllObservables();
			ChatBubbleSingleton.getInstance().clearMessageList();
		}

		setChatBubbleMinimized(false);
		ShortcutBadger.removeCount(application());
		ChatBubbleSingleton.getInstance().setClosedByAgentButNotLoggedOut(false);

		currentView = null;
		SupportWindow.this.close(false, false, false);

		stopLoadingDialog();
	}

	public void closeChatFromRetention() {
		setChatBubbleMinimized(false);
		animateCloseSnapCard(true);
		lastDisplayType = DisplayType.NONE;
		navStack.clear();
		clearHierarchyState();
	}

	public void close(boolean showVoV, boolean keepState, boolean isRed) {
		isChatSecondView = false;
		secondViewIndex = -1;
		if (currentView instanceof SlyceMessagingView) {
			initialMessageCounter = ((SlyceMessagingView) currentView).getTotalNotifications();
			((SlyceMessagingView) currentView).updateBadge();
			if (showVoV) {
//				if (!((SlyceMessagingView) currentView).endMessageOnce)
				if (ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut())
					logOutChat(null, true);
				else
					displayDialog();
			} else {
				setChatBubbleMinimized(isRed);
				animateClose();
			}
		} else {
			setChatBubbleMinimized(isRed);
			animateClose();
			if (!keepState) {//CLEARS STATE
				lastDisplayType = DisplayType.NONE;
				navStack.clear();
				clearHierarchyState();
			}
		}
	}

	public void logOutChat(final Dialog overlyDialog, final boolean clear) {

		int finalMessagesCount = 0;

		if (currentView instanceof SlyceMessagingView)
			finalMessagesCount = ((SlyceMessagingView) currentView).geLastSessiontMessagesCount();

		if (ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut()) {

			showLoadingDialog();
			if ((currentView != null && currentView instanceof SlyceMessagingView) || clear) {
				ChatBubbleSingleton.getInstance().stopAllObservables();
				ChatBubbleSingleton.getInstance().clearMessageList();
			}
			closeChatWindow(overlyDialog);
			stopLoadingDialog();

		} else {

			RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
			GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;

			boolean pSurveyEligible = finalMessagesCount >= Integer.valueOf(AppConfiguration.getExchangedNumberOfMessages())
					&& gdprGetResponseOwner != null
					&& gdprGetResponseOwner.getGdprPermissions() != null
					&& gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory() != null
					&& gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory().equalsIgnoreCase("yes");

			showLoadingDialog();
			chatService.logOutChatWithSurveyCheckWLPJSESSIONID(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, email, pSurveyEligible)
					.subscribe(new RequestSessionObserver<String>() {
						@Override
						public void onNext(String response) {
							stopLoadingDialog();

							if ((currentView != null && currentView instanceof SlyceMessagingView) || clear) {
								ChatBubbleSingleton.getInstance().stopAllObservables();
								ChatBubbleSingleton.getInstance().clearMessageList();
							}

							if (response.toLowerCase().contains("conversatia nu a fost")) {
								new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
							} else {

								closeChatWindow(overlyDialog);

								D.d("responseS = " + response.toLowerCase());
								D.i("response.toLowerCase().contains YES = " + response.toLowerCase().contains("yes"));
								D.d("hardcode.contains YES = " + "<div id=\"ajaxresponse\"><div id=\"errorstatus\" data-survey=\"YES\">0</div></div>".contains("yes"));
								D.d("hardcode.contains YES = " + "<div id=\"ajaxresponse\"><div id=\"errorstatus\" data-survey=\"yes\">0</div></div>".contains("yes"));
								D.i("response.toLowerCase().contains NO = " + response.toLowerCase().contains("no"));

								if (response.toLowerCase().contains("yes")) {
//                                     test url:  https://vodafone.eu.qualtrics.com/SE/?SID=SV_agYXto3afAO01Nz&3vj4dsgt00&OnlineTestingTeam_ro@vodafone.com&67922
									showSurveyInWebView();
								}
							}
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							stopLoadingDialog();
							new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
						}
					});
		}
	}

	private void releaseViews() {
		if (views != null)
			views.isShowing = false;
		destroy();

		if (rootLayout != null) {
			try {
				rootLayout.getAnimation().cancel();
				ViewGroup parent = (ViewGroup) rootLayout.getParent();
				if (parent != null) {
					parent.removeView(rootLayout);
				}
			} catch (Exception ignored) {
			}

			rootLayout = null;
		}
		chatService = VodafoneController.getInstance().chatService();
		views = null;
		currentView = null;
		sfv = null;
		sev = null;

		if (visibilityListener != null) {
			visibilityListener.onVisibilityChanged(false);
		}
	}

	public void updateChatBubbleForAgentClosedCase(boolean minimized) {
		try {
			ChatBubbleSingleton.getInstance().setMinimizedFlag(minimized);
			if (activity instanceof ChatBubbleActivity) {
				((ChatBubbleActivity) activity).getChatBubble().updateBubbleForAgentClosedCase();
			}
		} catch (Exception ignored) {
		}
	}

	public void setChatBubbleMinimized(boolean minimized) {
		try {
			ChatBubbleSingleton.getInstance().setMinimized(minimized, minimized);
			if (activity instanceof ChatBubbleActivity) {
				((ChatBubbleActivity) activity).getChatBubble().updateBubble();
			}
		} catch (Exception ignored) {
		}
	}

	private void displayDialog() {
		final Dialog overlyDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar);
		try {
			Window window = overlyDialog.getWindow();
			if (window != null)
				window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

		} catch (Exception ignored) {
		}
		final View content = LayoutInflater.from(activity).inflate(R.layout.overlay_dialog_notifications, null);
		overlyDialog.setContentView(content);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) content.getLayoutParams();

        final Point windowPadding = ViewUtils.getWindowPadding(windowManager, activity.getWindow());
        try {
            //   params.height = ViewUtils.getScreenHeight() - windowPadding.x - windowPadding.y;
            params.topMargin = windowPadding.x;
            params.bottomMargin = windowPadding.y;
            content.setLayoutParams(params);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

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
				logOutChat(overlyDialog, true);
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

	private void closeChatWindow(Dialog overlyDialog) {
		setChatBubbleMinimized(false);
		ShortcutBadger.removeCount(application());
		ChatBubbleSingleton.getInstance().setClosedByAgentButNotLoggedOut(false);

		if (overlyDialog != null) {

			VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 9, VoiceOfVodafoneCategory.Chat, null, "Ai încheiat conversaţia.", "Ok, am înţeles", "Conversație nouă",
					true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.GoToSupportChat);
			VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
			VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
			VodafoneController.getInstance().setChatConnected(false);
			overlyDialog.dismiss();
			new CustomToast.Builder(getContext()).message("Ai încheiat conversaţia.").success(true).show();

		}

		currentView = null;
		SupportWindow.this.close(false, false, false);
	}

	private void showSurveyInWebView() {
		String url = "https://vodafone.eu.qualtrics.com/SE/?SID=SV_agYXto3afAO01Nz";
		String sessionID = ChatBubbleActivity.sessionKey;
		String operatorID = ChatBubbleActivity.contactId;
		String username = email;

		url += "&" + sessionID + "&" + username + "&" + operatorID;
		D.i("url = " + url);
		//Tealium Track Event
		Map<String, Object> tealiumMapEvent = new HashMap(6);
		tealiumMapEvent.put("screen_name", "faq");
		tealiumMapEvent.put("event_name", "mcare:faq:button:asistenta");
		tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackEvent("event_name", tealiumMapEvent);

		Intent intent = new Intent(getContext(), WebviewActivity.class);
		intent.putExtra(WebviewActivity.KEY_URL, url);
		VodafoneController.currentActivity().startActivityForResult(intent, 10000);
	}

	private void displayGoToPCOverlay() {
		final Dialog overlyDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar);

		try {
			Window window = overlyDialog.getWindow();
            if (window != null)
                window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

		} catch (Exception ignored) {
		}

		final View content = LayoutInflater.from(activity).inflate(R.layout.overlay_dialog_notifications, null);
		overlyDialog.setContentView(content);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) content.getLayoutParams();

		final Point windowPadding = ViewUtils.getWindowPadding(windowManager, activity.getWindow());
		try {
			//   params.height = ViewUtils.getScreenHeight() - windowPadding.x - windowPadding.y;
			params.topMargin = windowPadding.x;
			params.bottomMargin = windowPadding.y;
			content.setLayoutParams(params);
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

		overlyDialog.show();

		Button goPc = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
		Button cancel = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

		VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
		VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

		overlayTitle.setText("Asistenţă");
		overlaySubtext.setText("Ai ales să continui conversația pe PC/browser. Ai la dispoziție 2 minute să te autentifici și să continui conversația. În caz contrar, trebuie să redeschizi o nouă sesiune de chat. " +
				"\n\n"  + "Ești sigur că vrei să continui pe PC/browser?");


		goPc.setText("Da");
		cancel.setText("Nu");

		ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
		overlayDismissButton.setImageResource(R.drawable.close_48_white);

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				overlyDialog.dismiss();
			}
		});

		goPc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatService.switchToPC(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, email, "[" + firstName + " " + lastName + "]")
						.subscribe(new RequestSessionObserver<String>() {
							@Override
							public void onNext(String response) {
								D.d("switchToPC response = " + response);
							}

							@Override
							public void onCompleted() {
								super.onCompleted();
								D.w();
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);
								D.e("e = " + e);
							}
						});

				VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(7, 20, VoiceOfVodafoneCategory.Chat, null,
						"Sesiunea de chat va continua pe PC/browser. Ai la dispoziție 2 minute să te autentifici și să continui conversația.",
						"Ok, am înţeles", "Conversaţie nouă", true, false, VoiceOfVodafoneAction.Dismiss, null);
				VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
				VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

				ChatBubbleSingleton.getInstance().setMinimized(false, false);
				vovInProgress = true;

				closeFromGOPcButton();

				new CustomToast.Builder(activity).message("Sesiunea de chat va continua pe PC/browser").success(true).show();
				overlyDialog.dismiss();
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

	private void animateStartViewBig() {

		views.isShowing = true;
		views.isSnapCard = false;
		changeMenuItemBackground(views.navigation);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getRealSize(size);

		final int screenWidth = size.x;
		final int screenHeight = size.y;

		int margins = ScreenMeasure.dpToPx(10);

		FrameLayout animLayout = new FrameLayout(rootLayout.getContext());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.setMargins(margins, margins, margins, margins);

		final View v = new View(rootLayout.getContext());
		v.setBackground(ContextCompat.getDrawable(rootLayout.getContext(), R.drawable.rounded_corner_select_dialog));

		display.getSize(size);
		int heightWithBar = size.y;
		int heightBar = screenHeight - heightWithBar;
		int animatedViewHeight = screenHeight - heightBar - 4 * margins;
		int animatedViewWidth = screenWidth - 2 * margins;

		FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(animatedViewWidth, animatedViewHeight);
		viewParams.setMargins(0, 0, margins, margins);
		animLayout.addView(v, viewParams);

		animLayout.setLayoutParams(params);

		rootLayout.addView(animLayout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		views.mainLayout.setAlpha(0);

		ViewAnimator.animate(v)
				.interpolator(new LinearInterpolator())
				.translationX(animatedViewWidth, margins)
				.translationY(animatedViewHeight, margins)
				.scaleX(0.1f, 1)
				.scaleY(0.1f, 1)
				.duration(350)
				.onStop(new AnimationListener.Stop() {
					@Override
					public void onStop() {
						if (views != null)
							views.mainLayout.setAlpha(1f);
						ViewAnimator.animate(v)
								.alpha(1, 0)
								.duration(450)
								.start();
					}
				}).start();

		{
			if (views != null) {
				views.redBubble.setVisibility(View.VISIBLE);
			}
			ViewAnimator.animate(views.cardTitle.findViewById(R.id.redBubble))
					.interpolator(new LinearInterpolator())
					.scaleX(0, 1)
					.scaleY(0, 1)
					.onStart(new AnimationListener.Start() {
						@Override
						public void onStart() {
							final ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
							int mDuration = 450; //in millis
							va.setDuration(mDuration);
							va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								public void onAnimationUpdate(ValueAnimator animation) {
									if ((views == null) || (views.cardTitle == null)) {
										va.cancel();
										return;
									}
									final View redBubble = views.cardTitle.findViewById(R.id.redBubble);
									if (redBubble == null) {
										va.cancel();
										return;
									}
									redBubble.setAlpha((Float) animation.getAnimatedValue());
								}
							});
							va.setStartDelay(350);
							va.start();

						}
					})
					.startDelay(300)
					.duration(450)
					.start();
		}
	}

	private void animateStartSnapCard() {
		views.isShowing = true;
		if (views.mainLayout != null) {
			views.isSnapCard = true;
			changeMenuItemBackground(views.navigation);
			final Rect to = new Rect();
			views.mainLayout.setClipBounds(to);
			views.mainLayout.getLocalVisibleRect(to);
			to.top = to.bottom;
			views.mainLayout.setClipBounds(to);
			views.mainLayout.setVisibility(View.INVISIBLE);

			ViewUtils.postOnPreDraw(views.mainLayout, new Runnable() {
				@Override
				public void run() {
					animateChatBubble(new Runnable() {
						@Override
						public void run() {
							try {
								if (views.mainLayout != null)
									animateShowSnapCardAfterChatBubbleMove();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			});
		}
	}

	private void animateShowSnapCardAfterChatBubbleMove() {
		if (!isShowing()) {
			releaseViews();
			return;
		}
		final Rect to = new Rect();
		views.mainLayout.setClipBounds(to);
		views.mainLayout.getLocalVisibleRect(to);
		views.mainLayout.setVisibility(View.VISIBLE);
		Rect from = new Rect(to);
		from.bottom = from.top;

		ObjectAnimator anim1 = ObjectAnimator.ofObject(views.mainLayout,
				"clipBounds", new RectEvaluator(), from, to);
		anim1.setDuration(200);
		anim1.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				if (views != null) {
					views.mainLayout.setClipBounds(null);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (views != null) {
					views.mainLayout.setClipBounds(null);
				}
			}
		});

		ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.1f, 1f);
		anim2.setDuration(600);
		anim2.setStartDelay(50);
		anim2.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				if (views != null) {
					views.redBubble.setVisibility(View.VISIBLE);
				}
			}
		});
		anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (float) animation.getAnimatedValue();
				if (views != null) {
					views.redBubble.setScaleX(value);
					views.redBubble.setScaleY(value);
				}
			}
		});

		AnimatorSet set = new AnimatorSet();
		set.playTogether(anim1, anim2);
		set.start();
	}

	private void animateClose() {
		if (views != null)
			views.isShowing = false;
		if (currentView == null) {
			animateCloseSnapCard(false);
		} else {
			animateCloseBig();
		}
	}

	private void animateCloseSnapCard(final boolean hideChatBubble) {
		if (views == null)
			return;
		views.isShowing = false;
		if (views.mainLayout == null)
			return;
		Rect from = new Rect();
		views.mainLayout.getLocalVisibleRect(from);
		Rect to = new Rect(from);
		to.bottom = to.top;

		ObjectAnimator anim1 = ObjectAnimator.ofObject(views.mainLayout,
				"clipBounds", new RectEvaluator(), from, to);
		anim1.setDuration(200);
		anim1.setStartDelay(0);
		anim1.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				releaseViews();
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				releaseViews();
				if (hideChatBubble && activity instanceof ChatBubbleActivity) {
					ChatBubble bubble = ((ChatBubbleActivity) activity).getChatBubble();
					bubble.forceHide();
					return;
				}
				animateChatBubbleRestore(null);
			}
		});
		anim1.start();
	}

	private void animateCloseBig() {
		if ((views == null) || (views.mainLayout == null))
			return;
		views.isShowing = false;

		Point position = chatBubbleRestore();

		if (position != null) {
			int margin = ScreenMeasure.dpToPx(10);
			views.mainLayout.setPivotX(position.x - margin);
			views.mainLayout.setPivotY(position.y - margin);
		} else {
			views.mainLayout.setPivotX(views.mainLayout.getWidth());
			views.mainLayout.setPivotY(views.mainLayout.getHeight());
		}

		ObjectAnimator anim1 = ObjectAnimator.ofFloat(views.mainLayout, "scaleX", 1, 0);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(views.mainLayout, "scaleY", 1, 0);
		anim1.setDuration(400);
		anim2.setDuration(400);

		AnimatorSet animator = new AnimatorSet();
		animator.playTogether(anim1, anim2);
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				releaseViews();
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				releaseViews();
			}
		});
		animator.start();
	}

	private void animateChatBubble(Runnable onAnimationEnd) {
		if (activity != null && activity instanceof ChatBubbleActivity) {
			ChatBubble bubble = ((ChatBubbleActivity) activity).getChatBubble();
			if (bubble.isVisible() && rootLayout != null) {
				float margin = ScreenMeasure.dpToPx(18);
				float x = margin;
				float y = rootLayout.getHeight() - margin + ViewUtils.getWindowPadding(windowManager, activity.getWindow()).x;
				bubble.animateTo(x, y, onAnimationEnd);
			}
		}
	}

	private void animateChatBubbleRestore(Runnable onAnimationEnd) {
		if (activity != null && activity instanceof ChatBubbleActivity) {
			ChatBubble bubble = ((ChatBubbleActivity) activity).getChatBubble();
			if (bubble == null)
				return;
			bubble.animateBack(onAnimationEnd);
		}
	}

	private Point chatBubbleRestore() {
		if (activity != null && activity instanceof ChatBubbleActivity) {
			ChatBubble bubble = ((ChatBubbleActivity) activity).getChatBubble();
			if (bubble == null)
				return null;
			return bubble.restore();
		}
		return null;
	}

	private void initFAQPageViews() {
		if (views == null)
			return;
		views.goPCButton.setVisibility(View.GONE);
		views.scrollView.setVisibility(View.GONE);
		views.recyclerView.setVisibility(View.VISIBLE);

		if (views.dock_menu.getVisibility() == View.GONE) {
			views.scrollView.setBackgroundResource(0);
			views.recyclerView.setBackgroundResource(R.drawable.rounded_corner_support_bottom);
			views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
		}

		views.window_scrollable_tab_card.setVisibility(View.VISIBLE);

		views.window_search_box.setVisibility(View.GONE);
		views.window_search_container.setVisibility(View.GONE);
		views.triangle_view.setVisibility(View.GONE);

		views.minimizeButton.setVisibility(View.GONE);

		views.faq_search_button.setVisibility(View.GONE);

		if (currentView instanceof SupportFaqView)
			((SupportFaqView) currentView).showFAQSearchButton();
	}

	private void initChatConversationViews() {
		if (views != null) {
			views.scrollView.setVisibility(View.GONE);
			views.recyclerView.setVisibility(View.VISIBLE);
			views.slyceInputID.setVisibility(View.VISIBLE);

			if (views.dock_menu.getVisibility() == View.GONE) {
				views.scrollView.setBackgroundResource(0);
				views.recyclerView.setBackgroundResource(0);
				views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
				views.slyceInputID.setBackgroundResource(R.drawable.rounded_corner_support_bottom);
			}


			views.window_scrollable_tab_card.setVisibility(View.GONE);

			views.minimizeButton.setVisibility(View.VISIBLE);
			views.faq_search_button.setVisibility(View.GONE);
		}
	}

	private void removeFAQPageViews() {
		updateDockMenu();

		if (views != null) {
			views.scrollView.setVisibility(View.VISIBLE);
			views.recyclerView.setVisibility(View.GONE);

			if (views.dock_menu.getVisibility() == View.GONE) {
				views.recyclerView.setBackgroundResource(0);
				views.recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.support_window_grey));
				views.scrollView.setBackgroundResource(R.drawable.rounded_corner_support_bottom);
			}

			views.window_scrollable_tab_card.setVisibility(View.GONE);
			views.window_search_box.setVisibility(View.GONE);
			views.window_search_container.setVisibility(View.GONE);
			views.triangle_view.setVisibility(View.GONE);

			if (currentView instanceof SupportChatSecondView ||
					currentView instanceof SlyceMessagingView)
				views.minimizeButton.setVisibility(View.VISIBLE);

			views.faq_search_button.setVisibility(View.GONE);
		}
	}

	void inflateError(String errorMessage, int item, View.OnClickListener clickListener, boolean showEmailButton) {
		removeFAQPageViews();

		if (views != null) {
			changeMenuItemCheckedStateColor(views.navigation);
			views.navigation.getMenu().getItem(item).setChecked(true);
			pushNavigationItemToBackStack(views.navigation.getMenu().getItem(item));
		}

		uncheckAll();
		setBottomNavigationMenuVisibility(true);
		currentView = new SupportApiErrorView(this, errorMessage, clickListener, showEmailButton);
		inflateLayout();
	}

	void inflateError(String errorMessage, boolean isChatAvailable) {
		removeFAQPageViews();

		if (views != null) {
			if (isChatAvailable) {
				changeMenuItemCheckedStateColor(views.navigation);
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setChecked(true);
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setEnabled(true);
				pushNavigationItemToBackStack(views.navigation.getMenu().getItem(CHAT_ITEM_POSITION));
			} else {
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setEnabled(false);
				views.navigation.getMenu().getItem(CHAT_ITEM_POSITION).setChecked(false);
			}
		}

		setBottomNavigationMenuVisibility(true);
		currentView = new SupportApiErrorView(this, errorMessage);
		inflateLayout();
	}

	void inflateError() {
		D.w();
		removeFAQPageViews();

		try {
			unhandledError = true;
			setBottomNavigationMenuVisibility(true);
			currentView = new SupportApiErrorView(this, null);
			inflateLayout();
			pushNavigationItemToBackStack(views.navigation.getMenu().getItem(CHAT_ITEM_POSITION));
		} catch (Exception e) {
			D.e("e = " + e);
		}
	}

	void inflateEmailLayout() {
		removeFAQPageViews();
		views.goPCButton.setVisibility(View.GONE);

		if (views != null)
			views.slyceInputID.setVisibility(View.GONE);

		updateDockMenu();
		if ((views != null) && (views.navigation != null)) {
			views.isSnapCard = false;
			changeMenuItemBackground(views.navigation);
		}
		currentView = sev;
		sev.initDefaults(this.activity, "clearPrefilledEmailSubject");
		inflateLayout();
		setNavigationHighLight(EMAIL_ITEM_POSITION);

		if (views != null)
			views.window_title.setText(R.string.title_email);

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap<>(6);
		tealiumMapView.put("screen_name", "email");
		tealiumMapView.put("journey_name", "help&support");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		SupportEmailTrackingEvent event = new SupportEmailTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);

	}

	public void inflateEmailLayoutFromFaq(String emailSubject) {
		Log.d(TAG, "-> inflateEmailLayout");

		removeFAQPageViews();

		if ((views != null) && (views.navigation != null)) {
			views.isSnapCard = false;
			changeMenuItemBackground(views.navigation);
		}
		currentView = sev;
		sev.initDefaults(this.activity, emailSubject);
		inflateLayout();
		setNavigationHighLight(EMAIL_ITEM_POSITION);

		if (views != null)
			views.window_title.setText(R.string.title_email);

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap<>(6);
		tealiumMapView.put("screen_name", "email");
		tealiumMapView.put("journey_name", "help&support");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		SupportEmailTrackingEvent event = new SupportEmailTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);

	}

	void inflateFaqLayout() {

		initFAQPageViews();
		wasPreviousFaq = true;
		views.goPCButton.setVisibility(View.GONE);

		if (views != null)
			views.slyceInputID.setVisibility(View.GONE);

		updateDockMenu();
		Log.d(TAG, "-> inflateFaqLayout");
		if ((views != null) && (views.navigation != null)) {
			views.isSnapCard = false;
			changeMenuItemBackground(views.navigation);
		}
		//enable color when clicked
		if (views != null && views.navigation != null)
			views.navigation.getMenu().getItem(FAQ_ITEM_POSITION).setCheckable(true);

//		changeMenuItemCheckedStateColor(views.navigation);
//		views.navigation.getMenu().getItem(FAQ_ITEM_POSITION).setChecked(true);
//		pushNavigationItemToBackStack(views.navigation.getMenu().getItem(CHAT_ITEM_POSITION));

		setRecyclerViewMatchParent();
		setNavigationHighLight(FAQ_ITEM_POSITION);
		if (views != null)
			views.window_title.setText("FAQs");


		currentView = sfv;
		sfv.isErrorDisplayed = true;
		sfv.refreshErrorPage();

		inflateLayout();

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap<>(6);
		tealiumMapView.put("screen_name", "faq");
		tealiumMapView.put("journey_name", "help&support");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		SupportFAQTrackingEvent event = new SupportFAQTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);
	}

	public void inflateChatLayout() {
		removeFAQPageViews();

		if ((views != null) && (views.navigation != null)) {
			views.isSnapCard = false;
			changeMenuItemBackground(views.navigation);
		}

		setNavigationHighLight(CHAT_ITEM_POSITION);
		if (views != null)
			views.window_title.setText(RetentionLabels.getTitleChat2());

		if (VodafoneController.getInstance().getUser() instanceof EbuMigrated ||
				VodafoneController.getInstance().getUser() instanceof EbuNonMigrated ||
				VodafoneController.getInstance().getUser() instanceof NonVodafoneUser) {
			setMatchParent();
			inflateError(" Momentan , serviciul de Chat nu este disponibil pentru categoria ta de client. ", true);
		} else {
			setMatchParent();

			shopRequestInProcess = true;
			//checkTimeAndEligibility();

			//Tealium Track View
			Map<String, Object> tealiumMapView = new HashMap<>(6);
			tealiumMapView.put("screen_name", "chat select");
			tealiumMapView.put("journey_name", "help&support");
			tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
			TealiumHelper.trackView("screen_name", tealiumMapView);

			SupportChatTrackingEvent event = new SupportChatTrackingEvent();
			TrackingAppMeasurement journey = new TrackingAppMeasurement();
			journey.event7 = "event7";
			journey.getContextData().put("event7", journey.event7);
			event.defineTrackingProperties(journey);
			VodafoneController.getInstance().getTrackingService().trackCustom(event);
		}
		changeMenuItemCheckedStateColor(views.navigation);
	}

	private void uncheckAll() {
		if (views == null)
			return;
		setUpItemStateColor(views.navigation);
		views.navigation.setItemTextColor(TEXT_COLOR_STATE_LIST);
	}

	void pushNavigationItemToBackStack(MenuItem item) {
		if (item == null)
			return;
		//if element is already in stack, bring to front
		if (navStack.contains(item.getItemId())) {
			final int pos = navStack.indexOf(item.getItemId());
			//don't bring to front the element which started the navigation
			if (pos != 0) {
				navStack.removeElementAt(pos);
				navStack.push(item.getItemId());
			}
		} else {
			navStack.push(item.getItemId());
		}
	}

	private void setNavigationHighLight(int pos) {
		Log.d(TAG, "-> setNavigationHighLight");
		changeMenuItemBackground(views.navigation);
//        changeMenuItemCheckedStateColor(views.navigation);//2
		final MenuItem item = views.navigation.getMenu().getItem(pos);
		item.setChecked(true);
		pushNavigationItemToBackStack(item);
		if (pos != FAQ_ITEM_POSITION)
			if (views != null)
				views.window_title.setText(views.navigation.getMenu().getItem(pos).getTitle());
			else if (views != null)
				views.window_title.setText("FAQs");
	}

	void changeMenuItemCheckedStateColor(BottomNavigationView bottomNavigationView) {
		Log.d(TAG, "-> changeMenuItemCheckedStateColor");
		if (bottomNavigationView != null) {
			bottomNavigationView.setItemTextColor(TEXT_COLOR_STATE_LIST);
			bottomNavigationView.setItemIconTintList(ICON_COLOR_STATE_LIST);
		}
	}

	private void setUpItemStateColor(BottomNavigationView bottomNavigationView) {
		Log.d(TAG, "-> changeMenuItemCheckedStateColor");
		if (bottomNavigationView != null) {
			bottomNavigationView.setItemTextColor(TEXT_COLOR_STATE_LIST);
			bottomNavigationView.setItemIconTintList(new ColorStateList(TEXT_STATES, ICON_COLORS_BEFORE_SELECT));
		}
	}

	private void setMatchParent() {
		if (views == null)
			return;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels - getStatusBarHeight();
		int width = displayMetrics.widthPixels;

		window.setHeight(height);
		window.setWindowLayoutMode(width, height);
		window.update(width, height);

		ViewGroup.LayoutParams params = views.mainLayout.getLayoutParams();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		views.mainLayout.setLayoutParams(params);

		ViewGroup.LayoutParams params2 = views.scrollView.getLayoutParams();
		params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
		views.scrollView.setLayoutParams(params2);
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = activity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void setRecyclerViewMatchParent() {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels - getStatusBarHeight();
		int width = displayMetrics.widthPixels;


		window.setHeight(height);
		window.setWindowLayoutMode(width, height);
		window.update(width, height);

		if (views != null) {
			ViewGroup.LayoutParams params = views.mainLayout.getLayoutParams();
			params.height = ViewGroup.LayoutParams.MATCH_PARENT;
			views.mainLayout.setLayoutParams(params);

			ViewGroup.LayoutParams params2 = views.recyclerView.getLayoutParams();
			params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
			views.recyclerView.setLayoutParams(params2);
		}
	}

	private void onKHide() {
		D.d("onKHide()");

		isOpened = false;


		D.d("set margins for hide keyboard");
		setMargins(views.mainLayout,
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin));
	}

	private void onKShow() {
		D.d("onKShow()");
		isOpened = true;
		final int bottomMargin;

		if (currentView instanceof SlyceMessagingView) {
			bottomMargin = 0;
		} else {
			//Hide Navigation buttons
			bottomMargin = -views.navigation.getHeight();
		}

		D.d("set margins for show keyboard");
		setMargins(views.mainLayout,
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				(int) rootLayout.getContext().getResources().getDimension(R.dimen.adapter_horizontal_margin),
				bottomMargin);
	}

	private void setListenerToRootView() {

		if (views == null)
			return;

		views.mainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

				final int curWidth = right - left;
				final int oldWidth = oldRight - oldLeft;
				final int curHeight = bottom - top;
				final int oldHeight = oldBottom - oldTop;
				if (curWidth == oldWidth) {

					if ((curHeight < oldHeight) && getScreenHeightMinusViewHeight() > 0) {
						if (isOpened)
							return;

						isOpened = true;
						views.mainLayout.postDelayed(new Runnable() {
							@Override
							public void run() {
								onKShow();
							}
						}, 50);

					} else if ((curHeight > oldHeight) && getScreenHeightMinusViewHeight() == 0) {
						if (!isOpened)
							return;

						views.mainLayout.post(new Runnable() {
							@Override
							public void run() {

								if (!isOpened)
									return;

								if ((curHeight > oldHeight) && getScreenHeightMinusViewHeight() == 0) {
									onKHide();
								}
							}
						});
					}
				}
			}
		});
	}

	private int getScreenHeightMinusViewHeight() {
		Rect r = new Rect();
		View rootview = ((Activity) getContext()).getWindow().getDecorView(); // this = activity
		rootview.getWindowVisibleDisplayFrame(r);

		Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;

		return height - r.bottom;

	}

	private void setTouchListenerOnViewChilds(View view) {

		D.d("setTouchListenerOnViewChillds");

		initEditTextList();
		findEditTextFromViewGroup(view);

		if (!editTextList.isEmpty()) {
			D.d("editTextList list size : " + editTextList.size());
			for (View v : editTextList) {
				v.setOnTouchListener(editTextTouchListener);
			}
		}
	}

	private void initEditTextList() {
		if (editTextList == null) {
			editTextList = new ArrayList<>();
		} else {
			editTextList.clear();
		}
	}

	private void findEditTextFromViewGroup(View View) {
		D.d("findEditTextFromViewGroup()");

		if (View instanceof EditText) {
			editTextList.add(View);
		} else if (View instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) View).getChildCount(); i++) {
				findEditTextFromViewGroup(((ViewGroup) View).getChildAt(i));
			}
		}
	}

	void hideFaqSearchButton() {
		views.faq_search_button.setVisibility(View.GONE);
	}

	void disableGOPCButton(boolean disabled) {
//false for disable
		if (views == null)
			return;

		if (this.displayPcButton()) {
			views.goPCButton.setVisibility(View.VISIBLE);
		} else {
			views.goPCButton.setVisibility(View.GONE);
		}
		views.goPCButton.setClickable(disabled);
		if (!disabled)
			views.goPCButton.setDrawableColor(R.color.card_primary_btn_disabled);//red
		else
			views.goPCButton.setDrawableColor(R.color.dark_gray_background);//red

	}

	private boolean displayPcButton() {
		return AppConfiguration.getDisplayChatToBrowserButton();
	}

	private void checkTimeAndEligibility() {

		showLoadingDialog();

//        D.d("API 1 - GOOD  *** CHECK IS IN ACTIVE TIME = ENTER CHAT FLOW OR NOT");
		chatService.getChatEligibility(true, true)
				.subscribe(new RequestSessionObserver<GeneralResponse>() {
					@Override
					public void onNext(GeneralResponse response) {
						unhandledError = false;

						// Hardcodare pentru testare chat OFF
//						response.setTransactionStatus(2);
//						response.setTransactionFault(new TransactionFault());
//						response.getTransactionFault().setFaultCode("EC05302");

						if (response.getTransactionStatus() == 2) {
							if (response.getTransactionFault() != null) {
								switch (response.getTransactionFault().getFaultCode()) {
									case "EC05301":
										inflateError("Accesul la serviciul de Chat a fost blocat din cauza utilizării abuzive. ", true);
										break;
									case "EC05302":
										inflateError("Momentan , serviciul de Chat nu este disponibil pentru categoria ta de client. ", true);
										break;
									case "EC05303":
										inflateError("Datorită numărului mare de solicitări, momentan serviciul de chat nu este disponibil. Te rugăm să revii sau să ne contactezi telefonic la *222. ", false);
										break;
									case "EC05304":
										inflateError("Poți discuta cu noi pe chat de luni până vineri în intervalul 8:00-22:00 iar sâmbătă, duminică și de sărbătorile legale între 8:00-18:00. Te așteptăm în timpul programului nostru de lucru! ", true);
										break;
									default:
										//TODO verify correct merge
										inflateError();
//                                        new CustomToast.Builder(application()).message("UNKNOWN ERROR.").success(false).show();
										break;
								}
							}
						} else if (response.getTransactionStatus() == 0) {
							new SupportChatView(SupportWindow.this);
						} else {
							inflateError();
						}

//						new SupportChatView(SupportWindow.this);

					}

					@Override
					public void onCompleted() {
						super.onCompleted();
						stopLoading();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						D.e("ERROR  = " + e);
						stopLoading();
						inflateError();
					}
				});

	}

	private void checkTimeAndEligibility(RequestSessionObserver<GeneralResponse> mySubscriber) {
		showLoadingDialog();
		D.d("API 1 - GOOD  *** CHECK IS IN ACTIVE TIME = ENTER CHAT FLOW OR NOT");
		chatService.getChatEligibility(true, true)
				.subscribe(mySubscriber);
	}

	void showLoadingDialog() {
		if (activity instanceof BaseActivity) {
			((BaseActivity) activity).showLoadingDialog();
		}
	}

	void stopLoadingDialog() {
		if (activity != null && activity instanceof BaseActivity) {
			((BaseActivity) activity).stopLoadingDialog();
		}
	}

	void stopLoading() {
		shopRequestInProcess = false;
		stopLoadingDialog();
	}

	void setBottomNavigationMenuVisibility(boolean visible) {
		if (views != null)
			views.navigation.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public boolean isSnapCard() {
		return views == null || views.isSnapCard;
	}

	public boolean wasSnapCard() {
		return wasSnapCard;
	}

	private void openSpanChatIfNeeded() {
		if (views != null && views.scrollView.getHeight() == 0) {
			snapToFullView();
		}
	}

	private void openSpanChatIfNeededRecyclerView() {

		if (views != null && views.recyclerView.getHeight() == 0)
			snapToFullViewRecyclerView();

		checkAndUpdateDockVisibility();

	}

	private void checkAndUpdateDockVisibility() {
		displayAllowedButtons();
		/*if (!AppConfiguration.isEmailButtonVisible().toLowerCase().equals("true") && !AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
            forceHideDockMenu();*/
	}

	private boolean wasPreviousViewFAQ() {
		return wasPreviousFaq;
	}

	private void snapToFullView() {
		if (views == null)
			return;

		if (!wasPreviousViewFAQ()) {
			final View v = views.scrollView;

			views.mainLayout.invalidate();
			views.mainLayout.requestLayout();
			int height = views.mainLayout.getMeasuredHeight() - v.getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_height)
					- ScreenMeasure.dpToPx(48 + 16);

			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.mainLayout.getLayoutParams();
			params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
			views.mainLayout.setLayoutParams(params);

			LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) views.scrollView.getLayoutParams();
			params2.height = 0;
			views.scrollView.setLayoutParams(params2);


			ValueAnimator animator = ValueAnimator.ofInt(0, height);
			animator.setDuration(400);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					if (v == null) {
						animation.cancel();
						return;
					}
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
					if (params == null) {
						animation.cancel();
						return;
					}
					params.height = (int) animation.getAnimatedValue();
					v.setLayoutParams(params);
				}
			});
			animator.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationCancel(Animator animation) {
					onEnd();
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					onEnd();
				}

				private void onEnd() {
					if (views != null) {
						FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.mainLayout.getLayoutParams();
						params.height = FrameLayout.LayoutParams.MATCH_PARENT;
						views.mainLayout.setLayoutParams(params);

						LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) views.scrollView.getLayoutParams();
						params2.height = ViewGroup.LayoutParams.MATCH_PARENT;
						views.scrollView.setLayoutParams(params2);
					}
				}
			});
			animator.start();
		} else {
			wasPreviousFaq = false;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT,
					1
			);
			views.scrollView.setLayoutParams(params);
		}
	}

	private void snapToFullViewRecyclerView() {
		if (views == null)
			return;

		if (views.scrollView.getHeight() == 0) {
			final View v = views.recyclerView;

			views.mainLayout.invalidate();
			views.mainLayout.requestLayout();
			int height = views.mainLayout.getMeasuredHeight() - v.getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_height)
					- ScreenMeasure.dpToPx(48 + 16);

			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.mainLayout.getLayoutParams();
			params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
			views.mainLayout.setLayoutParams(params);

			ViewGroup.LayoutParams params2 = (ViewGroup.LayoutParams) views.recyclerView.getLayoutParams();
			params2.height = 0;
			views.recyclerView.setLayoutParams(params2);


			ValueAnimator animator = ValueAnimator.ofInt(0, height);
			animator.setDuration(400);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					if (v == null) {
						animation.cancel();
						return;
					}
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
					if (params == null) {
						animation.cancel();
						return;
					}
					params.height = (int) animation.getAnimatedValue();
					v.setLayoutParams(params);
				}
			});
			animator.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationCancel(Animator animation) {
					onEnd();
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					onEnd();
				}

				private void onEnd() {
					if (views != null) {
						FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.mainLayout.getLayoutParams();
						params.height = FrameLayout.LayoutParams.MATCH_PARENT;
						views.mainLayout.setLayoutParams(params);

						LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) views.recyclerView.getLayoutParams();
						params2.height = ViewGroup.LayoutParams.MATCH_PARENT;
						views.recyclerView.setLayoutParams(params2);
					}

				}
			});
			animator.start();
		} else {
			try {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT,
						1);
				views.recyclerView.setLayoutParams(params);
			} catch (ClassCastException cce) {
				D.e("cce here");
				cce.printStackTrace();
				D.e();
			}
		}
	}

	public void inflateLayout() {
		if (views == null)
			return;

		views.content.removeAllViews();
		//setMatchParent();
		views.content.addView(currentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		bottomMarginWithKeyboard = 0;

		if (currentView instanceof SupportFaqView) {
			setRecyclerViewMatchParent();
			initFAQPageViews();
		} else if (currentView instanceof SlyceMessagingView) {
			setMatchParent();
			initChatConversationViews();
		} else {
			setMatchParent();
			removeFAQPageViews();
		}

		if (currentView instanceof SupportChatSecondView || currentView instanceof SlyceMessagingView)
			views.minimizeButton.setVisibility(View.VISIBLE);
		else
			views.minimizeButton.setVisibility(View.GONE);

		if (!(currentView instanceof SlyceMessagingView)) {
			views.slyceInputID.setVisibility(View.GONE);
			updateDockMenu();
//            D.e("top = " + window.views.scrollView.getTop());
//            D.e("bot = " + window.views.scrollView.getBottom());
			VodafoneController.getInstance().handler.post(new Runnable() {
				@Override
				public void run() {
					if (views != null) {
						views.scrollView.scrollTo(0, 0);
						D.i("top = " + views.scrollView.getTop());
						D.i("bot = " + views.scrollView.getBottom());
					}
				}
			});
		}

		bottomMarginWithKeyboard = 0;

		if (currentView instanceof SupportFaqView) {
			views.scrollView.scrollTo(0, 0);
		} else if (currentView instanceof SupportChatFirstView) {
			views.scrollView.scrollTo(0, 0);
		} else if (currentView instanceof SupportChatSecondView) {
			views.scrollView.scrollTo(0, 0);
		} else if (currentView instanceof SlyceMessagingView) {
			//bottomMarginWithKeyboard = -60;
		}

		setListenerToRootView();
	}

	public Context getContext() {
		return rootLayout != null ? rootLayout.getContext() : null;
	}

	View getRootLayout() {
		return rootLayout;
	}

	ChatService getChatService() {
		return chatService;
	}

	void createChatRequest() {
		startChatRequest = new StartChatRequest();

		startChatRequest.setFirstName(firstName);
		startChatRequest.setLastName(lastName);
		startChatRequest.setEmail(email);
		startChatRequest.setCategoryName("Not Selected");
	}

	boolean isShopRequestInProcess() {
		return shopRequestInProcess;
	}

	void setShopRequestInProcess(boolean shopRequestInProcess) {
		this.shopRequestInProcess = shopRequestInProcess;
	}

	boolean isEmailBlackListed() {
		return emailBlackListed;
	}

	public Application application() {
		return VodafoneController.getInstance();
	}

	public boolean isShowing() {
		return window != null && window.isShowing() && (views == null || views.isShowing);
	}

	private void checkDrawOverlayPermission(final DisplayType type, final boolean snapCard) {
		final boolean permissionUsed = false;
		if (permissionUsed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(application())) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
			RxActivityResult.startActivityForResult(activity, intent, REQUEST_CODE_OVERLAY)
					.subscribe(new Action1<ActivityResult>() {
						@TargetApi(23)
						@Override
						public void call(ActivityResult activityResult) {
							if (Settings.canDrawOverlays(application())) {
								if (!isShowing()) {
									showInternal(type, snapCard);
								}
							} else {
								new CustomToast.Builder(application()).success(false)
										.message(LoyaltyLabels.getToastErrorOverlayPermission())
										.duration(CustomToast.LENGTH_LONG).show();
							}
						}
					});
		} else {
			if (!isShowing()) {
				showInternal(type, snapCard);
			}
		}
	}

	private void saveHierarchyState() {
		if (rootLayout != null) {
			hierarchyState = new SparseArray<>();
			rootLayout.saveHierarchyState(hierarchyState);
		}
		if (sev != null) {
			emailHierarchyState = new SparseArray<>();
			sev.saveHierarchyState(emailHierarchyState);
		}
		if (sfv != null) {
			faqHierarchyState = new SparseArray<>();
			sfv.saveHierarchyState(faqHierarchyState);
		}
	}

	private void restoreHierarchyState() {
		if (rootLayout != null && hierarchyState != null) {
			rootLayout.restoreHierarchyState(hierarchyState);
		}
		if (sev != null && emailHierarchyState != null) {
			sev.restoreHierarchyState(emailHierarchyState);
		}
		if (sfv != null && faqHierarchyState != null) {
			sfv.restoreHierarchyState(faqHierarchyState);
		}
	}

	private void clearHierarchyState() {
		hierarchyState = null;
		emailHierarchyState = null;
		faqHierarchyState = null;
	}

	private void setupNavigation() {
		Log.d(TAG, "-> Setup navigation");
		changeMenuItemBackground(views.navigation);
		setUpItemStateColor(views.navigation);//1
	}

	private void changeMenuItemBackground(BottomNavigationView navigation) {
		if (navigation != null) {
         /*   List<Integer> ids = new ArrayList<>();

            ids.add(R.id.item_faq);
            if (AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
                ids.add(R.id.item_chat);
            if (AppConfiguration.isEmailButtonVisible().toLowerCase().equals("true"))
                ids.add(R.id.item_email);*/
//            ids.add(R.id.item_faq);

			BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
			int menuItemsNo = menuView.getChildCount();

			for (int i = 0; i < menuItemsNo; i++) {
				View child = menuView.getChildAt(i);
				if (child != null) {
					ImageView icon = (ImageView) child.findViewById(R.id.icon);
					if (icon != null) {
						ViewGroup.LayoutParams params = icon.getLayoutParams();
						params.width = ScreenMeasure.dpToPx(32);
						params.height = ScreenMeasure.dpToPx(32);
						icon.setLayoutParams(params);

						if (wasSnapCard())
							icon.setBackgroundResource(R.drawable.white_circle_bg);
						else
							icon.setBackground(null);
					}
				}
			}
			if (wasSnapCard()) {
				((View) navigation.getParent()).setBackgroundResource(R.drawable.rounded_corner_support_bottom);
			} else {
				((View) navigation.getParent()).setBackgroundResource(R.drawable.rounded_corner_support_bottom_light);
			}
		}
	}

	private DisplayType getDispayType() {
		if (currentView != null) {
			if (currentView instanceof SupportEmailView) {
				return DisplayType.EMAIL;
			} else if (currentView instanceof SupportFaqView) {
				return DisplayType.FAQ;
			} else {
				return DisplayType.CHAT;
			}
		} else {
			return DisplayType.NONE;
		}
	}

	private boolean getIsVoV() {
		final boolean isChat = getDispayType() == DisplayType.CHAT;
		return isChat && vovInProgress;
	}

	void onChatStart() {
		vovInProgress = true;
	}

	public SupportFaqView getFAQView() {
		return sfv;
	}

	void setVisibilityListener(SupportWindowVisibilityListener visibilityListener) {
		this.visibilityListener = visibilityListener;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int id) {
		return rootLayout == null ? null : (T) rootLayout.findViewById(id);
	}

	void setLastDisplayType(DisplayType type) {
		lastDisplayType = type;
	}

	public enum DisplayType {
		NONE,
		CHAT,
		FAQ,
		EMAIL
	}

	interface SupportWindowVisibilityListener {
		void onVisibilityChanged(boolean visible);
	}

	private static class SupportTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "help&support";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "help&support");
			s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
			s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);
			s.channel = "help&support";
		}
	}

	private static class SupportFAQTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "faq";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "faq");
			s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
			s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);
			s.channel = "help&support";
			s.getContextData().put("&&channel", s.channel);
		}
	}

	private static class SupportChatTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "chat select";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "chat select");
			s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
			s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);

			s.channel = "help&support";
			s.getContextData().put("&&channel", s.channel);
			s.eVar18 = "begin chat";
			s.getContextData().put("eVar18", s.eVar18);
			s.eVar19 = "query";
			s.getContextData().put("eVar19", s.eVar19);
		}
	}

	private static class SupportEmailTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "email";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "email");
			s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
			s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);

			s.channel = "help&support";
			s.getContextData().put("&&channel", s.channel);
		}
	}

	class Views {
		@BindView(R.id.mainLayout)
		LinearLayout mainLayout;
		@BindView(R.id.content)
		FrameLayout content;
		@BindView(R.id.scrollView)
		PagingScrollView scrollView;
		@BindView(R.id.navigation)
		BottomNavigationView navigation;
		@BindView(R.id.message)
		TextView window_title;
		@BindView(R.id.cardTitle)
		View cardTitle;
		@BindView(R.id.pc_button)
		DynamicColorImageView goPCButton;
		@BindView(R.id.minimize_button)
		ImageView minimizeButton;
		@BindView(R.id.faq_search_button)
		ImageView faq_search_button;
		@BindView(R.id.triangle_view)
		ImageView triangle_view;
		@BindView(R.id.redBubble)
		ImageView redBubble;

		@BindView(R.id.recyclerView)
		RecyclerView recyclerView;
		@BindView(R.id.window_scrollable_tab_card)
		ScrollableTabCard window_scrollable_tab_card;
		@BindView(R.id.dock_menu)
		FrameLayout dock_menu;

		@BindView(R.id.slyceInputID)
		RelativeLayout slyceInputID;

		@BindView(R.id.chat_recycler_view_container)
		LinearLayout chatContainer;


		@BindView(R.id.window_search_box)
		RelativeLayout window_search_box;

		@BindView(R.id.searchbox_input_field)
		CustomEditTextCompat searchbox_input_field;

		@BindView(R.id.searchbox_search_button)
		LinearLayout searchbox_search_button;

		@BindView(R.id.window_search_container)
		LinearLayout window_search_container;

		@BindView(R.id.window_search_top_delimiter)
		View window_search_top_delimiter;

		@BindView(R.id.window_search_back_arrow)
		DynamicColorImageView window_search_back_arrow;

		@BindView(R.id.window_search_back_arrow_textView)
		VodafoneTextView window_search_back_arrow_textView;

		@BindView(R.id.window_search_no_results_container)
		LinearLayout window_search_no_results_container;

		@BindView(R.id.window_search_no_results_keyword)
		VodafoneTextView window_search_no_results_keyword;

		@BindView(R.id.window_search_no_results_hint)
		VodafoneTextView window_search_no_results_hint;

		@BindView(R.id.window_search_no_results_hints_container)
		FlexboxLayout window_search_no_results_hints_container;

		@BindView(R.id.window_search_recyclerView)
		RecyclerView window_search_recyclerView;

		private boolean isSnapCard = false;
		private boolean isShowing = false;
	}
}
