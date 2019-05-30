package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.BasicUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.CreditInAdvanceService;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpAnonymousFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpCreditInAdvanceFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPostpaidPost4PreFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPrepaidOwnNumberFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpRecurrentRechargesFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpSelectionPageFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpActivity extends MenuActivity {

    public static String TAG = "TopUpActivity";

    public NavigationHeader navigationHeader;

    public CreditCardSelection selectedCreditCard=null;
    public SaveCreditCard saveCreditCard=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = this;
        selectFragment();

        TopUpTrackingEvent event = new TopUpTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        initNavigationFragment();
    }


    private void selectFragment() {
        User user = VodafoneController.getInstance().getUser();
        if(user instanceof SeamlessEbuUser) {
            addFragment(new TopUpAnonymousFragment());
            return;
        }
        
        if (user instanceof SubUserNonMigrated || user instanceof CorpSubUser
                || user instanceof CorpUser || user instanceof BasicUser || user instanceof NonVodafoneUser
                || user instanceof EbuMigrated) {
            callTopUpServicesForNonVdf();
        } else if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess) {
            addFragment(new TopUpAnonymousFragment());
        } else addFragment(new TopUpSelectionPageFragment());

    }

    public void addFragment(Fragment fragment) {
        try {
            Log.d(TAG, "addFragment()");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
                //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
                transaction.addToBackStack(null);
            }

            if (fragment instanceof TopUpPrepaidOwnNumberFragment) {
                ((TopUpPrepaidOwnNumberFragment) fragment).navigationHeader = navigationHeader;
            }

            transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));

            //check if app is not in background.
            if(VodafoneController.isActivityVisible()){
                transaction.commit();
            }
            else {
                /*
                 If application requires performing the transaction inside async calls in case when app is in background
                 and there is no easy way to guarantee that the callback won’t be invoked after onSaveInstanceState(),
                 you may have to resort to using commitAllowingStateLoss() and dealing with the state loss that might occur.
                 */
                 transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initNavigationFragment() {
        User user = VodafoneController.getInstance().getUser();
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this)
                .setTitle(TopUPLabels.getTop_up_page_title())
                .displayDefaultHeader();

        Log.d(TAG, "user -- " + user.toString());
        if (user instanceof PrepaidUser || user instanceof SeamlessPrepaidUser) {
            navigationHeader.buildMsisdnSelectorHeader();
        } else if (user instanceof PostPaidUser) {
            navigationHeader.buildBanSelectorHeader();
        }

        //hide selector for migrated users, because they have access only to AnonymousFragment.
        if(user instanceof EbuMigrated){
            navigationHeader.hideSelectorView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_SELECTOR_UPDATED) {

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof TopUpRecurrentRechargesFragment) {
                f.onActivityResult(requestCode, resultCode, data);
            } else if(f instanceof TopUpSelectionPageFragment)
                f.onActivityResult(requestCode, resultCode, data);

            if (FragmentUtils.getVisibleFragment(this, false) instanceof TopUpPostpaidPost4PreFragment) {
                Log.d(TAG, "onActivityResult: fragment tag " + FragmentUtils.getTagForFragment(TopUpPostpaidPost4PreFragment.class));
                Log.d(TAG, "onActivityResult: ");
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(FragmentUtils.getVisibleFragment(this, false));
                ft.attach(FragmentUtils.getVisibleFragment(this, false));
                ft.commit();
            }

        } else {

        }
    }

    @Override
    protected int setContent() {
        return R.layout.activity_top_up;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        VodafoneController.getInstance().setFromBackPress(true);
        if(FragmentUtils.getVisibleFragment(this, false) instanceof TopUpPostpaidPost4PreFragment){
            ((TopUpPostpaidPost4PreFragment)FragmentUtils.getInstance(getSupportFragmentManager(),TopUpPostpaidPost4PreFragment.class)).removeGlobalLayoutListener();
        }

    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {
        if (fragment == null) {
            return;
        }
        if (fragment.equalsIgnoreCase(IntentActionName.TOP_UP_AUTO_REDIRECT_NOTIFICATION.getFragmentClassName())) {
            addFragment((Fragment) FragmentUtils.newInstanceByClassName(fragment));
        }else{
            addFragment((Fragment) FragmentUtils.newInstanceByClassName(fragment));

        }
    }

    private void callTopUpServicesForNonVdf() {

        String banID = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        Log.d(TAG, "BAN selected: " + banID);
        showLoadingDialog();
        RechargeService rechargeService = new RechargeService(getBaseContext());
        rechargeService.getFavoriteNumbers().subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                super.onNext(favoriteNumbersSuccessGeneralResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
                addFragment(new TopUpAnonymousFragment());
            }
        });
    }

    public static class TopUpTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "top up";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "top up");


            s.channel = "top up";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "top up";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    public void getCreditInAdvanceEligibility() {
        showLoadingIfNotShowing();
        final Bundle bundle = new Bundle();
        CreditInAdvanceService creditService = new CreditInAdvanceService(this);

        creditService.getCreditInAdvanceEligibility().subscribe(new RequestSaveRealmObserver<GeneralResponse<CreditInAdvanceSuccess>>() {
            @Override
            public void onNext(GeneralResponse<CreditInAdvanceSuccess> response) {
                super.onNext(response);

                if (response.getTransactionStatus() != 0) {
                    if (response.getTransactionFault() != null) {
                        String creditInAdvanceErrorCode = response.getTransactionFault().getFaultCode();
                        bundle.putString("errorCode", creditInAdvanceErrorCode);
                    }
                } else {
                    bundle.putBoolean("isError", false);
                }

                if (FragmentUtils.getVisibleFragment(TopUpActivity.this, false) instanceof TopUpCreditInAdvanceFragment) {
                    ((TopUpCreditInAdvanceFragment) FragmentUtils.getInstance(getSupportFragmentManager(), TopUpCreditInAdvanceFragment.class)).setData(bundle);
                } else {
                    addFragment(FragmentUtils.newInstance(TopUpCreditInAdvanceFragment.class, bundle));
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                bundle.putBoolean("isError", true);
                stopLoadingDialog();

                if (FragmentUtils.getVisibleFragment(TopUpActivity.this, false) instanceof TopUpCreditInAdvanceFragment) {
                    ((TopUpCreditInAdvanceFragment) FragmentUtils.getInstance(getSupportFragmentManager(), TopUpCreditInAdvanceFragment.class)).setData(bundle);
                } else {
                    addFragment(FragmentUtils.newInstance(TopUpCreditInAdvanceFragment.class, bundle));
                }
            }
        });
    }

    public void showLoadingIfNotShowing() {
        if (!getProgressDialog().isShowing()) {
            showLoadingDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VodafoneController.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VodafoneController.activityPaused();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
