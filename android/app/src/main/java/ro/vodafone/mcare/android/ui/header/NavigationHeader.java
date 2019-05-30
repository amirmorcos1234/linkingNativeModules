package ro.vodafone.mcare.android.ui.header;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.EBUSelectorDialogActivity;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.OrderHistoryFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.RetentionFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.CustomServicesFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPrepaidOwnNumberFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpSelectionPageFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.InterfaceUserSelectedMsisdnBan;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Bivol Pavel on 13.02.2017.
 */
public class NavigationHeader extends LinearLayout implements InterfaceUserSelectedMsisdnBan {

    public static String TAG = "NavigationHeader";
    public static String SELECTOR_VARIABLE_KEY = "selector_variable_key";
    public static String EBU_SELECTOR_VARIABLE_KEY = "ebu_selector_variable_key";

    public static String IS_ONLY_BAN_SELECTOR = "is_only_ban_selector";
    public static String GO_TO_DASHBOARD_WHEN_CLOSE = "go_to_dashboard_when_close";
    /**
     * This identity have a single bam and msisdn and api 19 should not be called.
     */
    public static String HAVE_SINGLE_MSISDN = "have_single_msisdn";

    private Activity activity;

    boolean displaySelector = false;
    boolean isBanSelector = false;
    boolean isSubscriberSelector = false;
    private Context mContext;
    private TextView title;
    private VodafoneTextView clientCodeLabel;
    private VodafoneTextView clientCode;
    private VodafoneTextView balance;
    private ImageView rightArrow;
    private CircleImageView usersIcon;
    private LinearLayout selectorLayout;
    private LinearLayout profilePhoto;
    private LinearLayout triangleLayout;
    private LinearLayout extra_header_layout;
    private LinearLayout header_view_container;
    private boolean refreshBalance = true;
    private boolean vodafoneTvSelector = false;

    private OnClickListener selectorCliclListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof EbuMigrated) {
                openSelectorForMigrated();
            } else {
                openSelectorForNonMigrated();
            }
        }
    };

    public NavigationHeader(Context context) {
        super(context);
        this.mContext = context;
        if (!isInEditMode()) {
            initHeader();
        }
    }

    public NavigationHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (!isInEditMode()) {
            initHeader();
        }
    }

    public NavigationHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (!isInEditMode()) {
            initHeader();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (!isInEditMode()) {
            initHeader();
        }
    }

    private void openSelectorForMigrated() {
        Intent intent = new Intent(getContext().getApplicationContext(), EBUSelectorDialogActivity.class);

        if (isBanSelector) {
            intent.putExtra(IS_ONLY_BAN_SELECTOR, true);
        } else if (isSubscriberSelector)
            intent.putExtra(IS_ONLY_BAN_SELECTOR, false);

        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated && ((EbuMigrated) VodafoneController.getInstance().getUser()).isSubscriber()) {
            intent.putExtra(HAVE_SINGLE_MSISDN, true);
        }
        VodafoneController.currentActivity().startActivityForResult(intent, RESULT_SELECTOR_UPDATED);
    }

    private void openSelectorForNonMigrated() {

        Intent intent = new Intent(getContext().getApplicationContext(), SelectorDialogActivity.class);

        if (vodafoneTvSelector) {
            intent.putExtra("VodafoneTvSelector", "true");
        }
        if (isBanSelector) {
            intent.putExtra(SELECTOR_VARIABLE_KEY, SelectorDialogActivity.SELECT_TYPE_BAN);
        } else if (isSubscriberSelector) {
            intent.putExtra(SELECTOR_VARIABLE_KEY, SelectorDialogActivity.SELECT_TYPE_MSISDN);
        }

        VodafoneController.currentActivity().startActivityForResult(intent, RESULT_SELECTOR_UPDATED);

    }

    public void setUsersIconFromUri(Uri file) {
        Glide.with(getContext())
                .load(file)
                //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                .dontAnimate()
                .into(usersIcon);
    }


    public void initHeader() {// TODO OKSO CR 1. USE BUTTERKNIFE MAYBE?

        UserSelectedMsisdnBanController.getInstance().setChangedBanMsisdnListener(this);

        ViewTreeObserver vto = this.getViewTreeObserver();

        vto.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                resetListener();
            }
        });

        inflate(getContext(), R.layout.navigation_header, this);

        UserSelectedMsisdnBanController.getInstance().setChangedBanMsisdnListener(this);

        selectorLayout = (LinearLayout) findViewById(R.id.selector_container);
        profilePhoto = (LinearLayout) findViewById(R.id.profile_photo);
        triangleLayout = (LinearLayout) findViewById(R.id.triangle_layout);

        usersIcon = (CircleImageView) findViewById(R.id.users_icon);
        usersIcon.setBorderWidth(ScreenMeasure.dpToPx(2));
        usersIcon.setBorderColor(ContextCompat.getColor(mContext, R.color.white));

        rightArrow = (ImageView) findViewById(R.id.open_selector_button);
        rightArrow.setColorFilter(Color.parseColor("#ffffff"));

        clientCode = (VodafoneTextView) findViewById(R.id.client_code_selected);
        clientCodeLabel = (VodafoneTextView) findViewById(R.id.client_code_label);
        balance = (VodafoneTextView) findViewById(R.id.balalnce);

        extra_header_layout = (LinearLayout) findViewById(R.id.extra_header_layout);
        header_view_container = (LinearLayout) findViewById(R.id.header_view_container);

    }

    public void resetListener() {
        UserSelectedMsisdnBanController.getInstance().setChangedBanMsisdnListener(this);
    }

    public void addExtraView(View view) {//todo maybe add check if 1. view has a parent 2. parent has a view
        extra_header_layout.addView(view, 0);
    }

    @Deprecated
    /**
     * Should not be used because it removes the user msisdn/ban selector
     */
    public void removeExtraView() {
        try {
            extra_header_layout.removeViewAt(0);
        } catch (Exception e) {//todo okso cr 5. be explicit with catched errors : it may help someday to know wheather its IllegalState or NullPointer
            e.printStackTrace();
        }
    }

    public void addViewToContainer(View view) {//todo maybe add check if 1. view has a parent 2. parent has a view
        header_view_container.addView(view, 0);
        if (header_view_container.getChildCount() != 0 && view.getVisibility() == VISIBLE) {
            setTriangleLayoutGrayBackground();
        }
    }

    public void removeTriangleView() {
        triangleLayout.setVisibility(GONE);
    }

    public void removeViewFromContainer() {// todo you have null checks and try/catch ..
        try {
            if (header_view_container != null) {
                if (header_view_container.getChildAt(0) != null)
                    header_view_container.removeViewAt(0);

                if (header_view_container.getChildCount() == 0) {
                    setTriangleLayoutTransparentBackground();
                }
            }
        } catch (Exception ex) {//todo okso cr 5. be explicit with catched errors : it may help someday to know wheather its IllegalState or NullPointer
            Log.d(TAG, "Exception on header_view_container.removeViewAt(0)" + ex);
        }
    }

    public void removeAllViewFromContainer() {
        try {
            if (header_view_container != null) {
                if (header_view_container.getChildCount() != 0) {
                    for (int i = 0; i < header_view_container.getChildCount(); i++) {
                        header_view_container.removeViewAt(i);
                    }
                }
                setTriangleLayoutTransparentBackground();
            }
        } catch (Exception ex) {
            Log.d(TAG, "Exception on header_view_container.removeViewAt(0)" + ex);
        }
    }

    public void showBannerView() {
        if (header_view_container != null) {
            header_view_container.setVisibility(VISIBLE);
        }
    }

    public void hideBannerView() {
        if (header_view_container != null) {
            header_view_container.setVisibility(GONE);
        }
    }

    private void updateViewWithSelectedMsisdn() {
        if (vodafoneTvSelector && UserSelectedMsisdnBanController.getInstance().tvServiceSelector()) {
            createFixedNetSelectorView();
        } else if (isSubscriberSelector)
            createMsisdnSelectorView();
    }

    private void updateViewWithSelectedBan() {
        if (isBanSelector)
            createBanSelectorView();
    }

    public void hideAliasAndMsisdn() {
        clientCodeLabel.setVisibility(INVISIBLE);
        clientCode.setVisibility(GONE);
    }

    public void hideBalance() {
        balance.setVisibility(GONE);
    }

    public void showBalance() {
        balance.setVisibility(VISIBLE);
    }

    public void showAliasAndMsisdn() {
        clientCodeLabel.setVisibility(VISIBLE);
        clientCode.setVisibility(VISIBLE);
    }

    public void hideArrowRight() {
        rightArrow.setVisibility(GONE);
    }

    public void showArrowRight() {//todo okso cr: this method can be split , to be easier to understand; if too long
        if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() != 0)
            if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() > 1) {//case we wanna see avatar on non ResCorp users -> Customize your services on alinp4 / 12345678
                if (VodafoneController.getInstance().getUser() instanceof ResCorp
                        || VodafoneController.getInstance().getUser() instanceof CorpUser
                        || VodafoneController.getInstance().getUser() instanceof CorpSubUser
                        || VodafoneController.getInstance().getUser() instanceof ChooserUser
                        || VodafoneController.getInstance().getUser() instanceof DelegatedChooserUser
                        || VodafoneController.getInstance().getUser() instanceof AuthorisedPersonUser
                        && VodafoneController.getInstance().getUser().isFullLoggedIn())
                    rightArrow.setVisibility(VISIBLE);
                else
                    rightArrow.setVisibility(GONE);
            } else
                rightArrow.setVisibility(GONE);
        else
            rightArrow.setVisibility(GONE);
    }

    public void showMsisdnSelector() {
        //Log.d(TAG, "showMsisdnSelector =" + getSelectedSubscriber());
        //if (getSelectedSubscriber() != null) {
        // createMsisdnSelectorView(getSelectedSubscriber());
        //} else {
        try {
            buildMsisdnSelectorHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }

    public void showBanSelector() {
        //  if (getSelectedBan() != null) {
        //    createBanSelectorView(getSelectedBan());
        //} else {
        buildBanSelectorHeader();
        // }
    }

    public NavigationHeader buildMsisdnSelectorHeader() throws NullPointerException {//todo okso cr: this method can be split , to be easier to understand; if's too long
        Log.d(TAG, "buildMsisdnSelectorHeader");
        isSubscriberSelector = true;
        isBanSelector = false;
        displaySelector = false;

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser || VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser
                || VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidHybridUser) {

            createMsisdnSelectorView();

        } else if (VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidHighAccess
                || VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidsLowAccess) {
            if (vodafoneTvSelector && UserSelectedMsisdnBanController.getInstance().tvServiceSelector()) {
                createFixedNetSelectorView();
            } else
                createMsisdnSelectorView();
        } else if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {

            if (allowToDisplayMsisdnSelector() || isResSubInRetention()) {
                if (vodafoneTvSelector && UserSelectedMsisdnBanController.getInstance().tvServiceSelector()) {
                    createFixedNetSelectorView();
                } else
                    createMsisdnSelectorView();//todo why false??
            }

        } else {

            createMsisdnSelectorView();

        }

        return this;
    }

    private boolean isResSubInRetention() {
        User user = VodafoneController.getInstance().getUser();
        if (activity instanceof OffersActivity
                && (FragmentUtils.getVisibleFragment((AppCompatActivity) activity, false) instanceof RetentionFragment ||
                FragmentUtils.getVisibleFragment((AppCompatActivity) activity, false) instanceof OrderHistoryFragment)
                && user instanceof ResSub) {
            return true;
        } else
            return false;
    }

    private boolean isPrepaidInTopUpRecharge() {
        User user = VodafoneController.getInstance().getUser();
        return activity instanceof TopUpActivity
                && (FragmentUtils.getVisibleFragment((AppCompatActivity) activity, false) instanceof TopUpSelectionPageFragment ||
                FragmentUtils.getVisibleFragment((AppCompatActivity) activity, false) instanceof TopUpPrepaidOwnNumberFragment)
                && (user instanceof SeamlessPrepaidUser || user instanceof PrepaidUser);
    }

    public NavigationHeader buildBanSelectorHeader() {
        Log.d(TAG, "buildBanSelectorHeader");
        isSubscriberSelector = false;
        isBanSelector = true;
        displaySelector = false;

        if (UserSelectedMsisdnBanController.getInstance().getBanList() == null) {
            hideSelectorView();
            hideBannerView();
            return this;
        } else if (allowToDisplayBanSelector()) {
            createBanSelectorView();
        }

        return this;
    }

    private boolean allowToDisplayBanSelector() {//todo okso cr: again, if is too long. make a method to check user's availability
        boolean allow = false;
        User user = VodafoneController.getInstance().getUser();

        if (user instanceof ResCorp || user instanceof CorpUser || user instanceof CorpSubUser
                || user instanceof ChooserUser || user instanceof DelegatedChooserUser || user instanceof AuthorisedPersonUser) {
            allow = true;
        }

        return allow;
    }

    private boolean allowToDisplayMsisdnSelector() {//todo okso cr: if's are too long
        boolean allow = false;

        User user = VodafoneController.getInstance().getUser();
/*
        Activity currentActivity = null;
        if(this.activity != null)
            currentActivity = this.activity;
        else
            currentActivity = VodafoneController.currentActivity();

        if (currentActivity != null && currentActivity instanceof OffersActivity
                && FragmentUtils.getVisibleFragment((AppCompatActivity) currentActivity, false) instanceof RetentionFragment) {

            if (user instanceof ResCorp || user instanceof ResSub) {
                allow = true;
            }
        } else*/
        if (user instanceof ResCorp || user instanceof CorpUser || user instanceof CorpSubUser ||
                user instanceof DelegatedChooserUser || user instanceof ChooserUser || user instanceof AuthorisedPersonUser) {
            allow = true;
        }

        return allow || isCustomServicesFragment();
    }

    private boolean isCustomServicesFragment() {
        Activity currentActivity;
        if (this.activity != null)
            currentActivity = this.activity;
        else
            currentActivity = VodafoneController.currentActivity();

        boolean isCustomServicesFragment = false;

        if (currentActivity instanceof AppCompatActivity) {
            Fragment fragment = FragmentUtils.getVisibleFragment((AppCompatActivity) currentActivity, false);
            isCustomServicesFragment = fragment instanceof CustomServicesFragment;
        }

        return isCustomServicesFragment;
    }

    private boolean headerInitedOnCustomServicesFragment() {
        Activity currentActivity = null;
        if (this.activity != null)
            currentActivity = this.activity;
        else
            currentActivity = VodafoneController.currentActivity();

        if (currentActivity instanceof AppCompatActivity) {
            Fragment fragment = FragmentUtils.getVisibleFragment((AppCompatActivity) currentActivity, false);
            if (fragment instanceof CustomServicesFragment)
                return ((CustomServicesFragment) fragment).getHeaderInited();
            else
                return false;
        }
        return false;
    }

    private boolean isYourProfilePage() {
        Activity currentActivity = null;
        if (this.activity != null)
            currentActivity = this.activity;
        else
            currentActivity = VodafoneController.currentActivity();

        if (currentActivity instanceof AppCompatActivity) {
            Fragment fragment = FragmentUtils.getVisibleFragment((AppCompatActivity) currentActivity, false);
            return (fragment instanceof YourProfileFragment);
        }
        return false;
    }

    private void createBanSelectorView() {
        Log.d(TAG, "createBanSelectorView: ");
        displaySelector = true;

        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        if (selectedBan == null)
            return;

        if (isMultipleBans()) {
            clientCode.setText(selectedBan);
            setClientCodeLabelVisibility();
            displayDefaultBanIcon();
            displaySelectorView();
        } else {
            displaySelector = false;
            hideSelectorView();
        }

        setRightArrowVisibility();

    }

    private void createMsisdnSelectorView() {
        if (!isCustomServicesFragment() || !headerInitedOnCustomServicesFragment()) {
//            displaySelector = true;

            displaySelector = isResSubInRetention() || isPrepaidInTopUpRecharge() || isMultipleSubscribers();

            String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

            if (selectedMsisdn == null)
                return;

            displaySelectorView();
            setAvatar();

            if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {

                clientCodeLabel.setVisibility(VISIBLE);
                clientCode.setVisibility(VISIBLE);

                clientCodeLabel.setText("Cont: " + UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
                if (isCustomServicesFragment())
                    clientCode.setText(toStartWithZeroMsisdnFormat(selectedMsisdn));
                else
                    clientCode.setText("Nr: " + toStartWithZeroMsisdnFormat(selectedMsisdn));

            } else {
                clientCode.setText(selectedMsisdn.startsWith("4") ? selectedMsisdn.substring(1) : selectedMsisdn);

                try {
                    setAlias();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            boolean isEbuMigratedWithMultipleBans;

            if (UserSelectedMsisdnBanController.getInstance().getBanList() != null) {
                isEbuMigratedWithMultipleBans = VodafoneController.getInstance().getUser() instanceof EbuMigrated && UserSelectedMsisdnBanController.getInstance().getBanList().size() > 1;
            } else {
                isEbuMigratedWithMultipleBans = false;
            }

            if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() != 0)//todo okso cr: nested if's , long conditions

                if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() > 1 || isEbuMigratedWithMultipleBans) {//case we wanna see avatar on non ResCorp users -> Customize your services on alinp4 / 12345678

                    if (VodafoneController.getInstance().getUser() instanceof ResCorp
                            || VodafoneController.getInstance().getUser() instanceof CorpUser
                            || VodafoneController.getInstance().getUser() instanceof CorpSubUser
                            || VodafoneController.getInstance().getUser() instanceof DelegatedChooserUser
                            || VodafoneController.getInstance().getUser() instanceof AuthorisedPersonUser
                            || VodafoneController.getInstance().getUser() instanceof ChooserUser
                            || isResSubInRetention()
                            && VodafoneController.getInstance().getUser().isFullLoggedIn())
                        setRightArrowVisibility();
                    else
                        rightArrow.setVisibility(GONE);

                } else
                    rightArrow.setVisibility(GONE);

            else
                setRightArrowVisibility();

            if (refreshBalance)
                setBalance();
            else
                refreshBalance = true;
        }
    }

    private void createFixedNetSelectorView() {
        displaySelector = isResSubInRetention() || isPrepaidInTopUpRecharge() || isMultipleSubscribers();
        String alias = UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getAlias();
        String serviceId = UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getServiceId();

        if (serviceId == null)
            return;
        displaySelectorView();
        clientCode.setText(serviceId.startsWith("4") ? serviceId.substring(1) : serviceId);

        setFixedNetAvatar();

        if (alias != null) {
            clientCodeLabel.setVisibility(VISIBLE);
            clientCodeLabel.setText(alias);
        } else
            clientCodeLabel.setVisibility(INVISIBLE);

        boolean isEbuMigratedWithMultipleBans;

        if (UserSelectedMsisdnBanController.getInstance().getBanList() != null) {
            isEbuMigratedWithMultipleBans = VodafoneController.getInstance().getUser() instanceof EbuMigrated && UserSelectedMsisdnBanController.getInstance().getBanList().size() > 1;
        } else {
            isEbuMigratedWithMultipleBans = false;
        }

        if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() != 0)

            if (UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() > 1 || isEbuMigratedWithMultipleBans) {

                if (VodafoneController.getInstance().getUser() instanceof ResCorp
                        || VodafoneController.getInstance().getUser() instanceof CorpUser
                        || VodafoneController.getInstance().getUser() instanceof CorpSubUser
                        || VodafoneController.getInstance().getUser() instanceof DelegatedChooserUser
                        || VodafoneController.getInstance().getUser() instanceof AuthorisedPersonUser
                        || VodafoneController.getInstance().getUser() instanceof ChooserUser
                        || isResSubInRetention()
                        && VodafoneController.getInstance().getUser().isFullLoggedIn())
                    setRightArrowVisibility();
                else
                    rightArrow.setVisibility(GONE);

            } else
                rightArrow.setVisibility(GONE);

        else
            setRightArrowVisibility();

    }


    private void setFixedNetAvatar() {
        String avatar = UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getAvatarUrl();
        String type = UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getServiceType();

        if (type.toLowerCase().contains("fixed")) {
            if (avatar != null) {
                Glide.with(mContext)
                        .load(avatar)
                        .placeholder(R.mipmap.ic_fixed_net)
                        //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                        .dontAnimate()
                        .error(R.mipmap.ic_fixed_net)
                        .into(usersIcon);
            } else
                Glide.with(mContext)
                        .load(R.mipmap.ic_fixed_net)
                        .into(usersIcon);
        } else {
            if (avatar != null) {
                Glide.with(mContext)
                        .load(avatar)
                        .placeholder(R.drawable.phone_icon)
                        //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                        .dontAnimate()
                        .error(R.drawable.phone_icon)
                        .into(usersIcon);
            } else
                Glide.with(mContext)
                        .load(R.drawable.phone_icon)
                        .into(usersIcon);

        }

    }

    private String toStartWithZeroMsisdnFormat(String msisdn) {

        if (msisdn == null) {
            return null;
        }

        if (msisdn.startsWith("4")) {
            msisdn = msisdn.substring(1);
        }

        if (msisdn.length() == 9) {
            msisdn = "0" + msisdn;
        }

        return msisdn;
    }

    private void setClientCodeLabelVisibility() {
        Log.d(TAG, "setClientCodeLabelVisibility");
        if (allowToDisplayBanSelector()) {

            {
                clientCodeLabel.setVisibility(VISIBLE);
                clientCodeLabel.setText("Cont Client");
            }
        } else {
            clientCodeLabel.setVisibility(INVISIBLE);
        }
    }

    public TextView getClientCodeLabel() {
        return clientCodeLabel;
    }

    public TextView getClientCode() {
        return clientCode;
    }

    public void setRightArrowVisibility() {
        boolean isMultipleBans = VodafoneController.getInstance().getUser() instanceof EbuMigrated ? isMultipleBansForEbu() : isMultipleBans();
        if (isMultipleBans || isMultipleSubscribers()) {
            profilePhoto.setOnClickListener(selectorCliclListner);
            rightArrow.setVisibility(VISIBLE);
            rightArrow.setOnClickListener(selectorCliclListner);
        } else {
            rightArrow.setVisibility(GONE);
        }
    }

    public boolean isMultipleBans() {
        Log.d(TAG, "isMultipleBans()");
        boolean isMultiple = false;
        if (UserSelectedMsisdnBanController.getInstance().getBanList() != null)
            isMultiple = isBanSelector && UserSelectedMsisdnBanController.getInstance().getBanList().size() > 1;
        return isMultiple;
    }

    public boolean isMultipleBansForEbu() {
        if (UserSelectedMsisdnBanController.getInstance().getBanList() != null)
            return UserSelectedMsisdnBanController.getInstance().getBanList().size() > 1;
        return false;
    }

    public boolean isMultipleSubscribers() {
        boolean isMultiple = false;

        if (isSubscriberSelector && UserSelectedMsisdnBanController.getInstance().getSubscriberList() != null
                && UserSelectedMsisdnBanController.getInstance().getSubscriberList().size() > 1) {
            Log.d(TAG, "isMultipleSubscribers: " + UserSelectedMsisdnBanController.getInstance().getSubscriberList().size());
            isMultiple = true;
        }
        return isMultiple;
    }

    public void setAvatar() {//todo okso cr: this method can be further improved , to be easier to read and dealt with errors if case
        Log.d(TAG, "setAvatarUrl");
        Realm realm = Realm.getDefaultInstance();
        String avatarUrl = null;
        Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);
        User user = VodafoneController.getInstance().getUser();
        try {
            if (!(user instanceof EbuMigrated))
                avatarUrl = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAvatarUrl();
        } catch (Exception e) {
            D.e("e = " + e);
        }

        usersIcon.setColorFilter(null);

        int drawableResId = -1;

        if (profile != null) {
            if (profile.isTobe()) {
                D.d("IS TOBE");
                drawableResId = R.drawable.tobe_icon;
            } else if (profile.isVMB() && String.valueOf(profile.isVMB()).equals(null)) {
                D.d("IS VMB");
                drawableResId = R.drawable.vmb_icon;
            } else {
                D.d("NOT TOBE  /  NOT VMB");
                drawableResId = R.drawable.phone_icon;
            }

            if (avatarUrl == null) {
                if (user instanceof EbuMigrated) {
                    if (isCustomServicesFragment() || isYourProfilePage())
                        avatarUrl = profile.getAvatarUrl();
                    else {
                        drawableResId = R.drawable.phone_icon;
                    }
                } else {
                    avatarUrl = profile.getAvatarUrl();
                }
            }

            if (isYourProfilePage() && user instanceof ResCorp)
                avatarUrl = profile.getAvatarUrl();

        } else {
            drawableResId = R.drawable.phone_icon;
        }


        if (avatarUrl != null) {//todo 3 -
            try {
                Glide.with(mContext)
                        .load(avatarUrl)
                        .placeholder(drawableResId)
                        //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                        .dontAnimate()
                        .error(drawableResId)
                        .into(usersIcon);
            } catch (Exception e) {
                e.printStackTrace();
                new CustomToast.Builder(getContext()).message("Incarcare avatar nereusita.")
                        .success(false).errorIcon(true).show();
//                new CustomToast(activity, getContext(), "Incarcare avatar nereusita.", false, true).show();
                usersIcon.setImageDrawable(ContextCompat.getDrawable(mContext, drawableResId));
            }
        } else {
            usersIcon.setImageDrawable(ContextCompat.getDrawable(mContext, drawableResId));
        }
        realm.close();
    }

    public void setDefaultAvatarWithUserDataAndNoIdentity() {
        displaySelector = true;
        showAliasAndMsisdn();
        displaySelectorView();
        setMsisdn(UserSelectedMsisdnBanController.getInstance().getWithoutPrefix4MsisdnFromUserDataProfile());
        setAlias(UserSelectedMsisdnBanController.getInstance().getAliasFromUserDataProfile());
        profilePhoto.setOnClickListener(null);
        rightArrow.setVisibility(GONE);
        setAvatar();
    }

    public void setAvatar(String avatarUrl) {
        try {
            Glide.with(mContext)
                    .load(avatarUrl)
                    //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                    .dontAnimate()
                    .into(usersIcon);
        } catch (Exception e) {
            e.printStackTrace();
            new CustomToast.Builder(getContext()).message("Incarcare avatar nereusita.")
                    .success(false).errorIcon(true).show();
//            new CustomToast(activity, getContext(), "Incarcare avatar nereusita.", false, true).show();
        }
    }

    private void displayDefaultBanIcon() {
        usersIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.users_48));
        usersIcon.setColorFilter(Color.parseColor("#ffffff"));
    }

    public void setAlias() throws StringIndexOutOfBoundsException {//todo okso cr: too long , to nested , to long again
        Log.d(TAG, "setAlias()");
        if (UserSelectedMsisdnBanController.getInstance() != null) {
            if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null) {

                if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAlias() != null) {
                    clientCodeLabel.setText(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAlias());
                    Log.d(TAG, "client Code label - " + clientCodeLabel.getText().toString());
                    Log.d(TAG, "client Code label - " + clientCode.getText().toString());

                    clientCodeLabel.setVisibility(VISIBLE);
                } else {
                    clientCodeLabel.setVisibility(INVISIBLE);
                }
            } else {
                if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null) {
                    clientCodeLabel.setText(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAlias());
                    clientCodeLabel.setVisibility(VISIBLE);
                } else
                    clientCodeLabel.setVisibility(INVISIBLE);
            }
        }
    }

    public void setAlias(String alias) {
        setAlias(alias, true);
    }

    public void setAlias(String alias, boolean refreshBalance) {
        this.refreshBalance = refreshBalance;
        if (alias == null || alias.equals("")) {
            clientCodeLabel.setText(clientCode.getText());
        } else {
            clientCodeLabel.setText(alias);
            clientCodeLabel.setVisibility(VISIBLE);
        }
    }

    public void setMsisdn(String msisdn) {
        clientCode.setText(msisdn);
    }

    public NavigationHeader setBalance() {
        BalanceCreditSuccess balanceCreditSuccess = (BalanceCreditSuccess) RealmManager.getRealmObject(BalanceCreditSuccess.class);
        if (balanceCreditSuccess != null && balanceCreditSuccess.isValid()) {//todo okso cr:
            if (balanceCreditSuccess.getBalance() != null && balanceCreditSuccess.getBalance() >= 0) {//todo okso cr:
                this.balance.setText("Credit disponibil: " + NumbersUtils.twoDigitsAfterDecimal(balanceCreditSuccess.getBalance()) + " " + "€");
                this.balance.setVisibility(VISIBLE);
            } else {
                this.balance.setVisibility(GONE);
            }
        }
        return this;
    }

    public NavigationHeader displayDefaultHeader() {
        triangleLayout.setVisibility(VISIBLE);
        displaySelectorView();
        return this;
    }

    public void showTriangleView() {
        setTriangleLayoutGrayBackground();
        triangleLayout.setVisibility(VISIBLE);
    }

    public void displaySelectorView() {
        if (displaySelector) {
            setTriangleLayoutGrayBackground();
            selectorLayout.setVisibility(VISIBLE);
        }
    }

    public void setDisplaySelector(boolean displaySelector) {
        this.displaySelector = displaySelector;
    }

    public void hideSelectorView() {
        selectorLayout.setVisibility(GONE);
        setTriangleLayoutTransparentBackground();
    }

    public void hideSelectorViewWithoutTriangle() {
        setTriangleLayoutTransparentBackground();
        selectorLayout.setVisibility(GONE);
    }

    public NavigationHeader setActivity(Activity activity) {
        this.activity = (Activity) activity;
        return this;
    }

    public TextView getTitle() {
        return this.title;
    }

    public NavigationHeader setTitle(String pageTitle) {
        title = (TextView) findViewById(R.id.title);
        title.setText(pageTitle);

        return this;
    }

    public void setTriangleLayoutTransparentBackground() {
        triangleLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setTriangleLayoutGrayBackground() {
        triangleLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.scroll_black_background_op_45));
    }

    public void setBottomMargin() {
        LinearLayout msisdnLabelContainer = (LinearLayout) this.findViewById(R.id.msisdnLabelContainer);
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        msisdnLabelContainer.setLayoutParams(lp);
    }

    @Override
    public void onBanChanged() {
        updateViewWithSelectedBan();
    }

    @Override
    public void onSubscriberChanged() {
        updateViewWithSelectedMsisdn();
    }

    public NavigationHeader setVodafoneTv(boolean tvSelector) {
        vodafoneTvSelector = tvSelector;
        return this;
    }
    //todo OKso CODE REVIEW final thoughts:

    // - NO CODE DUPLICATION
    // - NOT SPOTTED ANY MISTAKES
    // - METHODS AND VARIABLES NAME OK

    // - CLASS IS EASIER TO BE UNDERSTOOD, BUT:
    // - LOST POINT ON READABILITY
    // - LOST POINTS ON FUNCTIONS LENGTH - functions/methods could be shortened

    //1. IT STILL HAS SOME LOGIC THAT SHOULD NOT BE HERE
    //2. NESTED IF'S SHOULD BE REFACTORED
    //3. LONG IF CONDITIONS SHOULD ME REFACTORED
    //4. HUGE WHITE  SPACES MAKES THE LOGIC HARDER TO FOLLOW
    //5. REMOVE COMMENTED METHODS THAT HAVE NO USAGE ANYMORE
}
