package ro.vodafone.mcare.android.widget.avatar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.EBUSelectorDialogActivity;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

import static ro.vodafone.mcare.android.ui.header.NavigationHeader.GO_TO_DASHBOARD_WHEN_CLOSE;
import static ro.vodafone.mcare.android.ui.header.NavigationHeader.HAVE_SINGLE_MSISDN;
import static ro.vodafone.mcare.android.ui.header.NavigationHeader.IS_ONLY_BAN_SELECTOR;

/**
 * Created by Alex on 2/7/2017.
 */

public class DashboardMenuAvatar extends RelativeLayout {

    public static String TAG = "DashboardMenuAvatar";
    public static String SELECTOR_VARIABLE_KEY = "selector_variable_key";
    public Profile profile;
    public UserProfile userProfile;
    public List<Subscriber> subscriberList;
    Context context;
    String usermsisdnFromUserProfile;
    SimpleDraweeView draweeAvatarView1;
    SimpleDraweeView draweeAvatarView2;
    SimpleDraweeView draweeAvatarView3;
    TextView dashboardAvatarText;
    Activity activity;
    Uri uri;
    private UserProfileHierarchy userProfileHierarchy;

    private long lastTimeIconClicked;
    private View.OnClickListener actionButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Avatars are pressed ");

            //prevent double click on avatar icon menu from dashboard;
            if (SystemClock.elapsedRealtime() - lastTimeIconClicked < 1000) {
                return;
            }
            lastTimeIconClicked = SystemClock.elapsedRealtime();

            boolean allowAvatarSelector = EbuMigratedIdentityController.isUserVerifiedEbuMigrated() ? allowEbuAvatarSelector() : allowAvatarSelector();
            if (allowAvatarSelector) {
                if (isEbuMigrated())
                    openEBUSelectorDialogActivity();
                else
                    openSelectorDialogActivity();
            } else {
                new NavigationAction(getContext()).startAction(IntentActionName.SETTINGS_SERVICES);
            }
        }
    };

    public DashboardMenuAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DashboardMenuAvatar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardMenuAvatar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    private boolean isEbuMigrated() {
        return (VodafoneController.getInstance().getUser() instanceof EbuMigrated);
    }

    public void init(Activity activity) {
        Log.d(TAG, "init");

        this.activity = activity;

        inflate(context, R.layout.dashboard_avatar_menu_widget, this);

        trackView();
        initElements();
    }

    public void refresh() {
        initElements();
    }

    /*
    *Tealium Track Event
    */
    private void trackView() {
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name", "dashboard");
        tealiumMapEvent.put("event_name", "mcare: dashboard: button: service selector");
        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
    }

    private void initElements() {
        Log.d(TAG, "On Init Elements");
        userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
        profile = (Profile) RealmManager.getRealmObject(Profile.class);
        userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);
        usermsisdnFromUserProfile = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

        subscriberList = UserSelectedMsisdnBanController.getInstance().getSortedSubscriberList();

        initAvatarMenu();
    }

    private void initAvatarMenu() {
        View actionButton = findViewById(R.id.menu_avatar);
        draweeAvatarView1 = (SimpleDraweeView) findViewById(R.id.avatar);
        draweeAvatarView2 = (SimpleDraweeView) findViewById(R.id.avatar2);
        draweeAvatarView3 = (SimpleDraweeView) findViewById(R.id.avatar3);
        dashboardAvatarText = (TextView) findViewById(R.id.avatar_text);

        setAlias();

        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
            ebuMigratedAvatarView();
        else
            otherThanMigratedAvatarView();

        if(!(VodafoneController.getInstance().getUser() instanceof SeamlessEbuUser)) {
            actionButton.setOnClickListener(actionButtonClickListener);
        }
    }

    private void otherThanMigratedAvatarView() {
        if (isSubscriberListNotEmpty()) {
            if (isUserWithMultipleSubscribers()) {
                manageIsValidForMultipleMSISDNCase();
            } else {
                manageOneMSISDNFromSubscriberCase();
            }
        } else {
            if (isProfileNotNull()) {
                manageOneMSISDNFromSubscriberCase();
            } else {
                if (isUserProfileNotNull()) {
                    manageOneMSISDNFromUserProfileCase();
                }
                D.e(" WE HAVE NO INFO ABOUT USER");
                D.e(" PERSONALIZEAZA-TI PAGE NOT AVAILABLE");
            }
        }
    }

    public boolean isSubscriberListNotEmpty() {
        return (subscriberList != null && !subscriberList.isEmpty());
    }

    public boolean isProfileNotNull() {
        return (profile != null);
    }

    public boolean isUserWithMultipleSubscribers() {
        User user = VodafoneController.getInstance().getUser();
        // TODO: 30.10.2017 check for multiple/single roles
        return user instanceof ResCorp;
    }

    public boolean isUserProfileNotNull() {
        return (userProfile != null);
    }

    public void manageIsValidForMultipleMSISDNCase() {
        int subscriberSelectedIndex = 0;
        Subscriber selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();

        if (selectedSubscriber != null) {
            String selectedMsisdnFormatted = !selectedSubscriber.getMsisdn().startsWith("4")
                    ? cut4FromMsisdn(selectedSubscriber.getMsisdn()) : selectedSubscriber.getMsisdn();

            /*
            * Set index for selected subscriber
            */
            for (int k = 0; k < subscriberList.size(); k++) {
                if (subscriberList.get(k).getMsisdn().contains(selectedMsisdnFormatted)) {
                    subscriberSelectedIndex = k;
                }
            }

            /*
            * Set avatar for first view
            */
            if (selectedSubscriber.getAvatarUrl() != null) {
                uri = Uri.parse(selectedSubscriber.getAvatarUrl());
                draweeAvatarView1.setDrawingCacheEnabled(false);
                draweeAvatarView1.setVisibility(VISIBLE);
                draweeAvatarView1.setImageURI(uri);
            }
        }

            /*
            * Create new list with subscribers indexes without selected subscriber index
            */
        ArrayList<Integer> otherAvatarIndexes = new ArrayList<>();
        for (int j = 0; j < subscriberList.size(); j++) {
            if (j != subscriberSelectedIndex) {
                otherAvatarIndexes.add(j);
            }
        }

            /*
            * Start to draw other avatars
            */
        try {
            if (otherAvatarIndexes.get(0) != null) {
                if (subscriberList.get(otherAvatarIndexes.get(0)) != null) {
                    Log.d(TAG, "Avatar URL Image 2:  " + subscriberList.get(otherAvatarIndexes.get(0)).getAvatarUrl());
                    Log.d(TAG, "Avatar msisdn:  " + subscriberList.get(otherAvatarIndexes.get(0)).getMsisdn());
                    draweeAvatarView2.setDrawingCacheEnabled(false);
                    draweeAvatarView2.setVisibility(VISIBLE);


                    if (subscriberList.get(otherAvatarIndexes.get(0)).getAvatarUrl() != null) {
                        Log.d(TAG, "second subscriber");
                        uri = Uri.parse(subscriberList.get(otherAvatarIndexes.get(0)).getAvatarUrl());
                        draweeAvatarView2.setImageURI(uri);
                    }
                } else {
                    Log.d(TAG, "no subscriber in 2nd position, hide Avatar icon");
                    draweeAvatarView2.setVisibility(GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Some error ocurred draweeAvatarView2: " + e);
            draweeAvatarView2.setVisibility(GONE);
        }

        try {
            if (otherAvatarIndexes.get(1) != null) {
                if (subscriberList.get(otherAvatarIndexes.get(1)) != null) {
                    Log.d(TAG, "Avatar URL Image 3:  " + subscriberList.get(otherAvatarIndexes.get(1)).getAvatarUrl());
                    draweeAvatarView3.setDrawingCacheEnabled(false);
                    draweeAvatarView3.setVisibility(VISIBLE);

                    D.e(" THIRD AVATAR URL : " + subscriberList.get(otherAvatarIndexes.get(1)).getAvatarUrl());

                    if (subscriberList.get(otherAvatarIndexes.get(1)).getAvatarUrl() != null) {
                        uri = Uri.parse(subscriberList.get(otherAvatarIndexes.get(1)).getAvatarUrl());
                        draweeAvatarView3.setImageURI(uri);
                    }
                } else {
                    Log.d(TAG, "no subscriber in 3rd position, hide Avatar icon");
                    draweeAvatarView3.setVisibility(GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Some error ocurred draweeAvatarView3: " + e);
            draweeAvatarView3.setVisibility(GONE);
        }

    }

    public void manageOneMSISDNFromSubscriberCase() {
        draweeAvatarView2.setVisibility(GONE);
        draweeAvatarView3.setVisibility(GONE);
        Subscriber selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();
        if (selectedSubscriber != null) {
            String msisdn = selectedSubscriber.getNumberWithout4PrefixOneMsisdn();
            String alias = selectedSubscriber.getAlias();

            dashboardAvatarText.setText(alias != null ? alias : msisdn);
            if (selectedSubscriber.getAvatarUrl() != null) {
                uri = Uri.parse(selectedSubscriber.getAvatarUrl());
                draweeAvatarView1.setDrawingCacheEnabled(false);
                draweeAvatarView1.setImageURI(uri);
            }
        }
    }

    public void manageEbuOneMSISDNFromProfile() {
        draweeAvatarView2.setVisibility(GONE);
        draweeAvatarView3.setVisibility(GONE);
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if (profile != null) {
            String msisdn = profile.getHomeMsisdnWithout4Prefix();
            String alias = profile.getAlias();

            dashboardAvatarText.setText(alias != null ? alias : msisdn);

            if (profile.getAvatarUrl() != null) {
                uri = Uri.parse(profile.getAvatarUrl());
                draweeAvatarView1.setDrawingCacheEnabled(false);
                draweeAvatarView1.setImageURI(uri);
            }
        }
    }


    public void manageOneMSISDNFromProfileCase() {
        draweeAvatarView2.setVisibility(GONE);
        draweeAvatarView3.setVisibility(GONE);


        Subscriber profileSubscriber = new Subscriber(
                profile.getAvatarUrl(),
                profile.getAlias(),
                profile.getHomeMsisdn(),
                VodafoneController.getInstance().getUserProfile().getSid(), null);

        String avatarUrl = profileSubscriber.getAvatarUrl();

        if (avatarUrl != null) {
            D.e(" FIRST AVATAR URL : " + avatarUrl);

            uri = Uri.parse(avatarUrl);
            draweeAvatarView1.setDrawingCacheEnabled(false);
            draweeAvatarView1.setImageURI(uri);
        }
    }

    public void manageOneMSISDNFromUserProfileCase() {
        draweeAvatarView2.setVisibility(GONE);
        draweeAvatarView3.setVisibility(GONE);
    }

    private void setAlias() {

        String avatarAlias = UserSelectedMsisdnBanController.getInstance().getSubscriberAlias();
        if (avatarAlias == null)
            avatarAlias = UserSelectedMsisdnBanController.getInstance().createAliasFromMsisdn();
        Log.d(TAG, "Dashboard avatarText is : " + avatarAlias);
        dashboardAvatarText.setText(avatarAlias);
    }

    private String cut4FromMsisdn(String selectedMsisdn) {
        String selectedMsisdnNo4;
        if (selectedMsisdn.startsWith("4"))
            selectedMsisdnNo4 = selectedMsisdn.substring(1);
        else
            selectedMsisdnNo4 = selectedMsisdn;
        return selectedMsisdnNo4;
    }

    private boolean isValidForMultipleMsisdnsToDisplay() {
        return subscriberList != null && !subscriberList.isEmpty() && profile != null && isUserWithMultipleSubscribers();
    }

    private boolean allowAvatarSelector() {
        if (subscriberList != null && userProfileHierarchy.isValid() && subscriberList.size() > 1) {
            return isValidForMultipleMsisdnsToDisplay();
        }
        return false;
    }

    private boolean allowEbuAvatarSelector() {
        return checkAllowMultipleAvatarEbu();
    }

    private void openSelectorDialogActivity() {
        Intent intent = new Intent(getContext().getApplicationContext(), SelectorDialogActivity.class);
        intent.putExtra(SELECTOR_VARIABLE_KEY, SelectorDialogActivity.SELECT_TYPE_MSISDN);
        activity.startActivityForResult(intent, 1);
    }

    private void openEBUSelectorDialogActivity() {

        Intent intent = new Intent(getContext().getApplicationContext(), EBUSelectorDialogActivity.class);
        intent.putExtra(IS_ONLY_BAN_SELECTOR, false);
        intent.putExtra(GO_TO_DASHBOARD_WHEN_CLOSE, true);
        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated && ((EbuMigrated) VodafoneController.getInstance().getUser()).isSubscriber()) {
            intent.putExtra(HAVE_SINGLE_MSISDN, true);
        }

        activity.startActivity(intent);
    }

    private void ebuMigratedAvatarView() {
        if (checkAllowMultipleAvatarEbu() && EbuMigratedIdentityController.isUserVerifiedEbuMigrated()) {
            multipleAvatarView();
        } else {
            manageEbuOneMSISDNFromProfile();
        }
    }

    private boolean checkAllowMultipleAvatarEbu() {
        boolean haveMultipleIdentities = !EbuMigratedIdentityController.getInstance().isSingleIdentity();
        User user = VodafoneController.getInstance().getUser();
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);
        boolean haveMultipleSubscribers = (
                userProfileHierarchy != null &&
                        userProfileHierarchy.getBan() != null &&
                        userProfileHierarchy.getBan().getSubscriberList() != null) && userProfileHierarchy.getBan().getSubscriberList().size() > 1;
        return ((EbuMigrated) user).isSubscriber() ? haveMultipleIdentities : (haveMultipleSubscribers || haveMultipleIdentities);
    }

    private void multipleAvatarView() {
        draweeAvatarView2.setVisibility(VISIBLE);
        draweeAvatarView3.setVisibility(VISIBLE);
    }
}
