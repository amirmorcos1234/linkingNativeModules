package ro.vodafone.mcare.android.ui.activities.yourServices;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.MenuLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.yourServices.AncomAcceptedOffersFragment;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Eliza Deaconescu on 08/03/2017.
 */
public class YourServicesActivity extends MenuActivity implements ActivityFragmentInterface {

    private VodafoneTextView title;
    PagingScrollView scrollView;
    private NavigationHeader navigationHeader;
    private String TAG = "YourServicesActivity";

    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

        initNavigationFragment();
        //initNavigationHeader();

        scrollView = (PagingScrollView) findViewById(R.id.scrollView);
        redirectToFragment();
    }

    private void redirectToFragment() {
        attachFragment(new YourServicesFragment());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isTransactionSafe=true;

        if (isTransactionPending) {
            refreshFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTransactionSafe=false;
    }

    @Override
    protected int setContent() {

        return R.layout.activity_your_services;
    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {

    }

    public void setTitle() {
        User user = VodafoneController.getInstance().getUser();
        //  if(user instanceof PostPaidUser){
        //     setTitle((String) getResources().getText(R.string.item_products_and_services));
        //   } else {
        setTitle(MenuLabels.getItemServices());
        //}

    }

    public void setTitle(String text) {
        try {
            title.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");

        }
    }

    public void scrolltoTop() {
        scrollView.scrollTo(0, 0);
    }

    private void initNavigationFragment() {
        User user = VodafoneController.getInstance().getUser();
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this);
        if (user instanceof PostPaidUser) {
            navigationHeader.buildMsisdnSelectorHeader()
                    .setTitle(MenuLabels.getItemProductsAndServices());
        } else {
            navigationHeader.setTitle(MenuLabels.getItemServices());
        }

        navigationHeader.displayDefaultHeader();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() with code: " + requestCode);

        if (data != null) {

            if (resultCode == RESULT_SELECTOR_UPDATED) {

                //Here calling reload cost-control

                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (f instanceof AncomAcceptedOffersFragment)
                    f.onActivityResult(requestCode, resultCode, data);
                else
                    refreshFragment();
            }
        }
    }

    public void refreshFragment() {
        if(isTransactionSafe) {
            if (FragmentUtils.getVisibleFragment(this, false) instanceof YourServicesFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.detach(FragmentUtils.getVisibleFragment(this, false));
                transaction.attach(FragmentUtils.getVisibleFragment(this, false));
                transaction.commit();
            }
        }else
        {
            isTransactionPending=true;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        super.onBackPressed();
        try {
            if (FragmentUtils.getVisibleFragment(this, false) != null) {
                setTitle(((OffersFragment) (FragmentUtils.getVisibleFragment(this, false))).getTitle());
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception");
        }
    }

    @Override
    public void attachFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
        fragmentManager.executePendingTransactions();
        scrolltoTop();

        if (fragment instanceof AncomAcceptedOffersFragment && !navigationHeader.isMultipleSubscribers()) {
            navigationHeader.hideSelectorView();
        }

        if(fragment instanceof OffersFragment)
            setTitle(((OffersFragment)fragment).getTitle());
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

}
