package ro.vodafone.mcare.android.ui.activities.travellingAboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.custom.CustomAutoCompleteEditText;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.fragments.travelingAboard.TravellingCountryFragment;
import ro.vodafone.mcare.android.ui.fragments.travelingAboard.TravellingServiceAdministration;
import ro.vodafone.mcare.android.ui.fragments.travelingAboard.TravellingAbroadFragment;
import ro.vodafone.mcare.android.ui.fragments.travelingAboard.TravellingHintsFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Alex on 4/10/2017.
 */

public class TravelingAboardActivity extends MenuActivity {

    public static String TAG = TravelingAboardActivity.class.getSimpleName();
    public NavigationHeader navigationHeader;
    VodafoneTextView title;
    boolean isPrepaidUser;
    User user;
    public static String IS_PREPAID = "isPrepaidUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        user = VodafoneController.getInstance().getUser();

        if (user instanceof PrepaidUser) {
            Log.d(TAG, "PrepaidUser user");
            isPrepaidUser = true;
            initNavigationHeader(isPrepaidUser);
        } else {
            Log.d(TAG, "PostPaid user");
            isPrepaidUser = false;
            initNavigationHeader(isPrepaidUser);
        }

        addFragment((Fragment) FragmentUtils.newInstanceByClassName(TravellingAbroadFragment.class.getCanonicalName()));
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {
        Log.d(TAG, "switchFragmentOnCreate: " + fragment.getClass().getSimpleName());
        addFragment(FragmentUtils.newInstance(FragmentUtils.getFragmentClass(fragment)));
    }

    public void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment() " + fragment.getTag());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            transaction.addToBackStack(null);
        }

        try {
        } catch (Exception e) {
            Log.e("ERROR", "Exception catched : " + e);
            e.printStackTrace();
        }
        transaction.replace(R.id.travelling_aboard_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
    }

    public void addFragmentWithParams(Fragment fragment, Bundle args){
        Log.d(TAG, "addFragmentWithParams: " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            transaction.addToBackStack(null);
        }

        try {
        } catch (Exception e) {
            Log.e("ERROR", "Exception catched : " + e);
            e.printStackTrace();
        }
        fragment.setArguments(args);
        transaction.replace(R.id.travelling_aboard_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
    }

    private void initNavigationHeader(boolean isprepaid) {
        Log.d(TAG, "initNavigationFragment " + isprepaid);

        navigationHeader.setActivity(this)
                .setTitle(TravellingAboardLabels.getTravelling_aboard_main_page_title())
                .displayDefaultHeader();

        if(user instanceof ResCorp || user instanceof EbuMigrated) {
            Log.d(TAG, "Is postPaid User");
            setSubscriberListOnSelector();
        }

    }

    private void setSubscriberListOnSelector(){
        Log.d(TAG, "setSubscriberListOnSelector");
        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.displayDefaultHeader();
    }

    @Override
    protected int setContent() {
        return R.layout.travelling_aboard_activity;
    }

    public void setTitle() {
        setTitle(TravellingAboardLabels.getTravelling_aboard_page_title());
    }

    public void setTitle(String text) {
        try {
            title.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");

        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Activity Back Pressed");
        Fragment currentFragment = FragmentUtils.getVisibleFragment(this, true);
        if (currentFragment instanceof TravellingCountryFragment) {
            Log.d(TAG, "Fragment TravellingCountryFragment" );
            navigationHeader.removeViewFromContainer();
            initNavigationHeader(isPrepaidUser);
            navigationHeader.setVisibility(View.VISIBLE);
        }else if(currentFragment instanceof TravellingHintsFragment){
            Log.d(TAG, "Fragment TravellingHintsFragment" );
            initNavigationHeader(isPrepaidUser);
        }
        super.onBackPressed();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult ");
        if (data == null) {
            return;
        }
        try {
            Log.d(TAG, "onActivityResult try");
            Fragment currentFragment = FragmentUtils.getVisibleFragment(this, false);
            if (currentFragment instanceof TravellingCountryFragment || currentFragment instanceof TravellingAbroadFragment || currentFragment instanceof TravellingServiceAdministration) {
                if (resultCode == RESULT_SELECTOR_UPDATED) {
                    TravelingAboardActivity.this.hideSnapcardAndOrBubble();

                    String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
                    String subscriberId = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();

                    if (currentFragment instanceof TravellingAbroadFragment) {
                        currentFragment.onActivityResult(requestCode, resultCode, data);
                    }else if(currentFragment instanceof TravellingServiceAdministration){
                        currentFragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }else{
                Log.d(TAG, "onActivityResult another  fragmnet");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof CustomAutoCompleteEditText){
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                if (!rect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }
}
