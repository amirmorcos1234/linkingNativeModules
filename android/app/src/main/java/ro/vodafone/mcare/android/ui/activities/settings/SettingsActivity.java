package ro.vodafone.mcare.android.ui.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.FrameLayout;


import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.NotificationService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.fragments.settings.CBUBlockSimFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.CustomServicesFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.ElectronicBillFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.PermissionsFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.PrivacyPolicyFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.SettingsFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.SimDetailsFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by bogdan.marica on 2/23/2017.
 */

public class SettingsActivity extends MenuActivity {

    private static final String FRAGMENT_CLASS_NAME_KEY = "fragmentClassNameKey";
    public AuthenticationService authenticationService;
    NotificationService notificationService;
    NavigationHeader navigationHeader;
    UserDataService userDataService;
    private VodafoneTextView title;
    private String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

        userDataService = new UserDataService(this);

        notificationService = new NotificationService(getApplicationContext());
        authenticationService = new AuthenticationService(getApplicationContext());

        initNavigationHeader();
    }

    public UserDataService getUserDataService() {
        return userDataService;
    }

    public void attachFragment(SettingsFragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();

        try {
            setTitle(fragment.getTitle());
        } catch (Exception e) {
            Log.e("ERROR", "Exception catched : " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected int setContent() {
        return R.layout.activity_settings;
    }

    public void setTitle() {

        setTitle((String) SettingsLabels.getSettingsPageTitle());
    }

    public void setTitle(String text) {
        try {
            title.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");

        }
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    private void initNavigationHeader() {
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this);
        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        D.w("onBackPressed");
        VodafoneController.getInstance().setFromBackPress(true);
        if ((FragmentUtils.getVisibleFragment(this, false).getClass().getName().equals(SettingsFragment.class.getName()))) {
            finish();
        } else if ((FragmentUtils.getVisibleFragment(this, false) instanceof CustomServicesFragment) &&
                !FragmentUtils.isFragmentInBackStack(SettingsFragment.class, this)) {

            KeyboardHelper.hideKeyboard(this);

            CustomServicesFragment customServicesFragment = (CustomServicesFragment) FragmentUtils.getVisibleFragment(this, false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //Remove dislplayed fragment
            transaction.remove(customServicesFragment).commitAllowingStateLoss();
            clearFragmentContainer();

            //Atach selection page
            attachFragment(new SettingsFragment());
            fragmentManager.executePendingTransactions();

        } else if (FragmentUtils.getVisibleFragment(this, false) instanceof CBUBlockSimFragment && !FragmentUtils.isFragmentInBackStack(SettingsFragment.class,this)){
            FragmentUtils.clearFragmentsBackStack(this);
            new NavigationAction(SettingsActivity.this).finishCurrent(false).startAction(IntentActionName.SETTINGS);
        } else {
            super.onBackPressed();
        }

        if (FragmentUtils.getVisibleFragment(this, false) == null) {
            finish();
        }
    }

    public void clearFragmentContainer() {
        ((FrameLayout) findViewById(R.id.fragment_container)).removeAllViews();
    }

    @Override
    public void switchFragmentOnCreate(String fragmentName, String extraParameter) {
        if (extraParameter != null && !extraParameter.isEmpty()) {
            attachFragment((SettingsFragment) FragmentUtils.newInstanceByClassName(fragmentName, extraParameter));
            return;
        }
        attachFragment((SettingsFragment) FragmentUtils.newInstanceByClassName(fragmentName));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        D.d(TAG + "onActivityResult() with code: " + requestCode);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof SimDetailsFragment
                || f instanceof ElectronicBillFragment
                || f instanceof CustomServicesFragment
                || f instanceof PrivacyPolicyFragment
                || f instanceof PermissionsFragment) {
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        D.e("onRequestPermissionsResult");
        Fragment fragment = FragmentUtils.getVisibleFragment(this, false);
        if (fragment != null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        D.d("onRestoreInstanceState ");
        if (FragmentUtils.getVisibleFragment(this, false) != null) {
            outState.putString(FRAGMENT_CLASS_NAME_KEY, FragmentUtils.getVisibleFragment(this, false).getClass().getName());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        D.d("onRestoreInstanceState " + savedInstanceState.getString(FRAGMENT_CLASS_NAME_KEY));

        try {
            if (FragmentUtils.getVisibleFragment(this, false) == null) {
                attachFragment((SettingsFragment) FragmentUtils.newInstanceByClassName(savedInstanceState.getString(FRAGMENT_CLASS_NAME_KEY)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
