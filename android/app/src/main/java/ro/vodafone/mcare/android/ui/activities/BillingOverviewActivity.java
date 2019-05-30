package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ScrollView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.ui.activities.store_locator.RecyclerViewMenuActivity;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.BillingOverviewFragment;
import ro.vodafone.mcare.android.utils.FragmentUtils;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Bivol Pavel on 05.04.2017.
 */

public class BillingOverviewActivity extends RecyclerViewMenuActivity implements
        OnRecycleScrollViewCreatedListener,
        OnScrollViewCreatedListener {

    public static String TAG = "BillingOverview";
    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;

    @Override
    protected int getContentLayoutResource() {
        return R.layout.activity_billing_overview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atachFragment(FragmentUtils.newInstance(BillingOverviewFragment.class));

    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isTransactionSafe = true;

        if (isTransactionPending) {
            refreshFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() with code: " + requestCode);

        if (data != null) {

            if (resultCode == RESULT_SELECTOR_UPDATED) {
                refreshFragment();
            }
        }
    }

    public void refreshFragment() {
        if (isTransactionSafe) {
            if (FragmentUtils.getVisibleFragment(this, false) instanceof BillingOverviewFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.detach(FragmentUtils.getVisibleFragment(this, false));
                transaction.attach(FragmentUtils.getVisibleFragment(this, false));
                transaction.commit();
            }
        } else {
            isTransactionPending = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        super.onSaveInstanceState(outState);
    }

    public void atachFragment(Fragment fragment) {
        Log.d(TAG, "addFragment()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
    }

    @Override
    public void onScrollViewCreated(ScrollView scrollView) {
        setupScrollViewForCurrentView(scrollView);
        if (scrollView == null) {
            hideToolBar();
        } else {
            mToolbar.setToolBarColor(Color.TRANSPARENT);
            showToolBar();
        }
    }

    @Override
    public void onRecycleScrollViewCreated(RecyclerView scrollView) {
        setupRecycleScrollViewForCurrentView(scrollView);
        if (scrollView == null) {
            hideToolBar();
        } else {
            showToolBar();
        }
    }

    public void hideToolBar() {
        if (mToolbar != null) {
            mToolbar.showDefaultToolBar();
        }
    }

    public void showToolBar() {
        if (mToolbar != null) {
            mToolbar.showToolbarWithoutAnimation();
        }
    }


    public boolean isFragmentPresent(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        return currentFragment != null && currentFragment.getClass().equals(fragment.getClass());
    }
}
