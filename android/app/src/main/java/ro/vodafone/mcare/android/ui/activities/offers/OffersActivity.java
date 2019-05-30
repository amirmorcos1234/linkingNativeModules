package ro.vodafone.mcare.android.ui.activities.offers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import java.util.Objects;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.realm.system.MenuLabels;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.BeoFragment;
import ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.AncomPendingOffersFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.OffersSelectionPageFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.OrderHistoryFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhoneDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhonesFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PricePlanDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PricePlansFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.RetentionFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.ThankYouFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.navigation.SwitchFragmentListener;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;
import static ro.vodafone.mcare.android.ui.webviews.PhoneShopWebViewActivity.PRICE_PLAN_KEY;

/**
 * Created by Alex on 3/9/2017.
 */

public class OffersActivity extends MenuActivity implements SwitchFragmentListener {

    private static final int THANK_YOU_PAGE_REQUEST_CODE = 10000;
    public static final String IS_SERVICES = "services";
    public static final String IS_BEO = "beo";
    public static String TAG = OffersActivity.class.getSimpleName();
    public NavigationHeader navigationHeader;
    public PagingScrollView scrollView;
    private String beoId;
    public static final String DEEP_LINK_KEY = "deep_link";
    private boolean isFromRoaming;

    VodafoneTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromRoaming = Objects.equals(IntentActionName.OFFERS_BEO_DETAILS.getExtraParameter(), "fromRoaming");

        Log.d(TAG, "onCreate");
        setBeoIdFromSavedInstanceState(savedInstanceState);

        scrollView = (PagingScrollView) findViewById(R.id.scrollView);
        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);

        initNavigationHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FragmentUtils.getVisibleFragment(this, false) == null) {
            switchFragmentOnCreate(OffersSelectionPageFragment.class.getCanonicalName(), null);
        }
    }

    private void initNavigationHeader() {
        navigationHeader.displayDefaultHeader();
        navigationHeader.setActivity(this);
        showHeaderIfRescorpResSub();
    }

    private void showHeaderIfRescorpResSub() {
        if (VodafoneController.getInstance().getUser() instanceof ResCorp || VodafoneController.getInstance().getUser() instanceof ResSub) {
            navigationHeader.buildMsisdnSelectorHeader();
        }
    }


    public void addFragment(OffersFragment fragment) {

        hideSnapcardAndOrBubble();

        Log.d(TAG, "addFragment() " + fragment.getTag());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!isFragmentVisible(fragment)) {
            transaction.addToBackStack(null);
        }

        ((ViewGroup) findViewById(R.id.beo_fragment_container)).removeAllViews();
        transaction.replace(R.id.beo_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
        fragmentManager.executePendingTransactions();

    }

    @Override
    protected int setContent() {
        return R.layout.activity_offers;
    }

    public void scrolltoTop() {
        scrollView.scrollTo(0, 0);
    }

    public NavigationHeader getNavigationHeader() {

        return navigationHeader;
    }

    public void setTitle() {
        setTitle(MenuLabels.getItemOffers());
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
        VodafoneController.getInstance().setFromBackPress(true);
        Fragment currentFragment = FragmentUtils.getVisibleFragment(this, true);

        closeDrawers();
        //TODO: fix back pressed for Retention deep links accessed fragments
        try {
            if (currentFragment instanceof ThankYouFragment || currentFragment instanceof OrderHistoryFragment
                    || currentFragment instanceof PhonesFragment || currentFragment instanceof PricePlansFragment) {
                FragmentUtils.clearFragmentsBackStack(this);
                addFragment(new RetentionFragment());
            } else if (currentFragment instanceof PricePlanDetailsFragment) {
                FragmentUtils.clearFragmentsBackStack(this);
                addFragment(new PricePlansFragment());
            } else if (currentFragment instanceof PhoneDetailsFragment) {
                FragmentUtils.clearFragmentsBackStack(this);
                addFragment(new PhonesFragment());
            } else if ((currentFragment instanceof BeoFragment || currentFragment instanceof RetentionFragment || currentFragment instanceof AncomPendingOffersFragment) &&
                    !FragmentUtils.isFragmentInBackStack(OffersSelectionPageFragment.class, this)) {
                addFragment(new OffersSelectionPageFragment());
            } else if (currentFragment instanceof BeoDetailedFragment) {
                if (VodafoneController.getInstance().getUser() instanceof NonVodafoneUser) {
                    new NavigationAction(OffersActivity.this).startAction(IntentActionName.DASHBOARD);
                } else {
                    navigationHeader.removeViewFromContainer();
                    if (!FragmentUtils.isFragmentInBackStack(BeoFragment.class, this)) {
                        if (((BeoDetailedFragment) currentFragment).isServices()) {
                            new NavigationAction(OffersActivity.this).startAction(IntentActionName.OFFERS_SERVICES);
                        } else if (isFromRoaming) {
                            finish();
                        } else {
                            new NavigationAction(OffersActivity.this).startAction(IntentActionName.OFFERS_BEO_NO_SEAMLESS);
                        }
                    } else {
                        super.onBackPressed();
                    }
                }
            } else if (currentFragment instanceof OffersSelectionPageFragment) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void switchFragmentOnCreate(String fragmentName, String extraParameter) {
        Log.d(TAG, "switchFragmentOnCreate: ");
        if (fragmentName == null) {
            return;
        }
        if (fragmentName.equalsIgnoreCase(IntentActionName.OFFERS_BEO.getFragmentClassName())) {
            boolean isServices = IntentActionName.OFFERS_SERVICES.getExtraParameter() != null && IntentActionName.OFFERS_SERVICES.getExtraParameter().equals(extraParameter);
            OffersFragment serviceBeoFramgment = (OffersFragment) BeoFragment.createFragment(isServices, false);
            addFragment(serviceBeoFramgment);
        } else if (fragmentName.equalsIgnoreCase(IntentActionName.OFFERS_BEO_DETAILS.getFragmentClassName())) {
            if (IntentActionName.OFFERS_BEO_DETAILS.getExtraParameter() == null || IntentActionName.OFFERS_BEO_DETAILS.getExtraParameter().isEmpty()||
                    isFromRoaming) {
                setBeoId();
                Bundle bundle = new Bundle();
                bundle.putString(OfferRowInterface.KEY_ID, beoId);
                Fragment fragment = FragmentUtils.newInstance(BeoDetailedFragment.class, bundle);
                addFragment((OffersFragment) fragment);
            }
        } else {
            addFragment((OffersFragment) FragmentUtils.newInstanceByClassName(fragmentName));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult" + " " + requestCode + " " + resultCode);
        if (data == null) {
            return;
        }
        try {

            if (requestCode == THANK_YOU_PAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {

                    String result = data.getStringExtra("result");
                    FirebaseAnalyticsItem pricePlanValue = (FirebaseAnalyticsItem) data.getSerializableExtra(PRICE_PLAN_KEY);
                    Log.d(TAG, result);
                    DashboardController.reloadDashboardOnResume();
                    addFragment(ThankYouFragment.newInstance(pricePlanValue));
                }
            }

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.beo_fragment_container);
            if (f instanceof RetentionFragment) {
                f.onActivityResult(requestCode, resultCode, data);
            }

            if (f instanceof OrderHistoryFragment) {
                f.onActivityResult(requestCode, resultCode, data);
            }

            if (f instanceof BeoFragment) {
                f.onActivityResult(requestCode, resultCode, data);
            }

            Fragment currentFragment = FragmentUtils.getVisibleFragment(this, false);
            if (currentFragment instanceof OffersSelectionPageFragment) {

                if (resultCode == RESULT_SELECTOR_UPDATED) {

                    String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
                    String subscriberId = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();

                    BeoFragment beoFragment = (BeoFragment) VodafoneController.findFragment(OffersActivity.class, BeoFragment.class);
                    if (beoFragment != null) {
                        beoFragment.runFlow();
                    }
                }
            }

            if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
                if (f instanceof AncomPendingOffersFragment) {
                    f.onActivityResult(requestCode, resultCode, data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentAdd(Fragment fragment) {
        if (fragment instanceof OffersFragment)
            addFragment((OffersFragment) fragment);
    }

    @Override
    public void onFragmentBack() {
        Log.d(TAG, "onFragmentBack");
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("detailed_beo_id", beoId);
    }

    private void setBeoId() {
        String id = IntentActionName.OFFERS_BEO_DETAILS.getOneUsageSerializedData();
        if (id != null) {
            beoId = id;
        }
    }

    private void setBeoIdFromSavedInstanceState(Bundle bundle) {
        if (bundle != null && bundle.getString("detailed_beo_id") != null) {
            beoId = bundle.getString("detailed_beo_id");
        }
    }

    public boolean isFragmentVisible(Fragment fragment) {

        Fragment currentFragment = FragmentUtils.getVisibleFragment(this, false);
        return currentFragment != null && currentFragment.getClass().equals(fragment.getClass());
    }


}
