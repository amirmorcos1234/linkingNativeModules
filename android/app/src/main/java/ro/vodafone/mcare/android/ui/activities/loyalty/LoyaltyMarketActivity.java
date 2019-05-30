package ro.vodafone.mcare.android.ui.activities.loyalty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.ReservedPromotion;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.interfaces.fragment.loyalty.LoyaltyVoucherCommunicationListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.LoyaltySegmentRequest;
import ro.vodafone.mcare.android.service.LoyaltyMarketService;
import ro.vodafone.mcare.android.ui.activities.store_locator.RecyclerViewMenuActivity;
import ro.vodafone.mcare.android.ui.fragments.loyaltyMarket.LoyaltyMarketVoucherListingsFragment;
import ro.vodafone.mcare.android.ui.fragments.loyaltyMarket.LoyaltyVoucherDetailsFragment;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Victor Radulescu on 8/24/2017.
 */

public class LoyaltyMarketActivity extends RecyclerViewMenuActivity implements
        LoyaltyVoucherCommunicationListener,
        OnRecycleScrollViewCreatedListener,
        OnScrollViewCreatedListener {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    private LoyaltyMarketService loyaltyMarketService;

    private Class[] voucherDataClasses = {LoyaltyVoucherSuccess.class,
            LoyaltyVoucherReservedSuccess.class,
            Promotion.class,
            ReservedPromotion.class};

    private Realm realm;

    private String crmRole;
    private String treatmentSegment;
    private LoyaltySegmentRequest loyaltySegmentRequest;

    private boolean isGetVouchersFinished;
    private boolean isGetReservedVouchersFinished;

    private boolean isGetVouchersError = false;
    private boolean isGetReservedVouchersError = false;

    private boolean refreshVoucherDetails;


    @Override
    protected int getContentLayoutResource() {
        return R.layout.frame_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();


        loyaltyMarketService = new LoyaltyMarketService(LoyaltyMarketActivity.this);

        getUserInfoForRequests();

        isGetVouchersFinished = false;
        isGetReservedVouchersFinished = false;

        refreshVoucherDetails = true;

        if(checkIfEbuIsVdfSubscriptionOrCbu()) {
            getVouchers();
        } else {
            showErrorNoVfSubscription();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void getVouchers() {
        getLoyaltySegment();
        getReservedVouchers();
        showLoadingDialog();
        initFragment(false);
    }

    private void showErrorNoVfSubscription() {
        initFragment(true);
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {
        switchFragment(FragmentUtils.newInstance(FragmentUtils.getFragmentClass(fragment), null));
    }



    private void initFragment(boolean showError) {
        Bundle bundle = new Bundle();
        if(showError) {
            bundle.putBoolean("show_error_no_vdf", true);
        } else {
            bundle.putBoolean("show_error_no_vdf", false);
        }

        try {
            Fragment fragment = LoyaltyMarketVoucherListingsFragment.newInstance();
            fragment.setArguments(bundle);
            switchFragment(fragment);
        } catch (Exception ex) {
            this.finish();
            LOGGER.e("Unknown exception initFragment", ex);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //  transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.frame_layout, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commitAllowingStateLoss();
    }

    void getUserInfoForRequests() {
        crmRole = "";
        treatmentSegment = "";
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            treatmentSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
            Log.d(TAG, "crmRole: " + crmRole);
            Log.d(TAG, "treatmentSegment: " + treatmentSegment);

            List<Ban> banList = new ArrayList<>();
            List<String> banListString = new ArrayList<>();
            banList = UserSelectedMsisdnBanController.getInstance().getBanList();
            if(banList != null) {
                if (banList != null) {
                    for (Ban ban : banList) {
                        banListString.add(ban.getNumber());
                    }
                    String[] ebuBanList = banListString.toArray(new String[banList.size()]);
                    loyaltySegmentRequest = new LoyaltySegmentRequest(ebuBanList);
                }
            } else {
                loyaltySegmentRequest = new LoyaltySegmentRequest(null);
            }
        }

    }

    void clearVoucherData() {
        RealmManager.deleteMultiple(realm, voucherDataClasses);
    }

    public void getLoyaltySegment() {
        clearVoucherData();

        loyaltyMarketService.getLoyaltySegment(crmRole, loyaltySegmentRequest)
                .flatMap(new Func1<GeneralResponse<LoyaltySegmentSuccess>, Observable<GeneralResponse<LoyaltyVoucherSuccess>>>() {
                    @Override
                    public Observable<GeneralResponse<LoyaltyVoucherSuccess>> call(GeneralResponse<LoyaltySegmentSuccess> loyaltySegmentSuccessGeneralResponse) {
                        RequestSaveRealmObserver.save(loyaltySegmentSuccessGeneralResponse);
                        return loyaltyMarketService.getLoyaltyVoucherList(loyaltySegmentSuccessGeneralResponse.getTransactionSuccess().getLpsSegment(),
                                treatmentSegment, crmRole);
                    }
                }).subscribe(new RequestSessionObserver<GeneralResponse<LoyaltyVoucherSuccess>>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                Log.d(TAG, "onCompleted getLoyaltySegment");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "onError getLoyaltySegment");
                isGetVouchersFinished = true;
                isGetVouchersError = true;
                if (isGetReservedVouchersFinished) {
                    stopLoadingDialog();
                    onVoucherFinishedLoading();
                }
            }

            @Override
            public void onNext(GeneralResponse<LoyaltyVoucherSuccess> response) {
                isGetVouchersFinished = true;
                if (response.getTransactionStatus() == 0) {
                    RequestSaveRealmObserver.save(response);
                    isGetVouchersError = false;
                } else {
                    isGetVouchersError = true;
                }
                if (isGetReservedVouchersFinished) {
                    stopLoadingDialog();
                    onVoucherFinishedLoading();
                }
                Log.d(TAG, "onNext getLoyaltySegment");

            }
        });
    }

    public void getReservedVouchers() {
        loyaltyMarketService.getReservedLoyaltyVoucherList()
                .subscribe(new RequestSessionObserver<GeneralResponse<LoyaltyVoucherReservedSuccess>>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "onCompleted getReservedLoyaltyVoucherList");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "onError getReservedLoyaltyVoucherList");
                        isGetReservedVouchersFinished = true;
                        isGetReservedVouchersError = true;
                        if (isGetVouchersFinished) {
                            stopLoadingDialog();
                            onVoucherFinishedLoading();
                        }
                    }

                    @Override
                    public void onNext(GeneralResponse<LoyaltyVoucherReservedSuccess> response) {
                        isGetReservedVouchersFinished = true;
                        if (response.getTransactionStatus() == 0) {
                            RequestSaveRealmObserver.save(response);
                            isGetReservedVouchersError = false;
                        } else {
                            isGetReservedVouchersError = true;
                        }
                        if (isGetVouchersFinished) {
                            stopLoadingDialog();
                            onVoucherFinishedLoading();
                        }
                        Log.d(TAG, "onNext getReservedLoyaltyVoucherList");
                    }
                });
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        switchFragment(fragment);
    }

    public void onVoucherFinishedLoading() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        if (fragment != null && fragment instanceof LoyaltyMarketVoucherListingsFragment) {
            ((LoyaltyMarketVoucherListingsFragment) fragment).setupViews();
        }

        if (fragment != null && fragment instanceof LoyaltyVoucherDetailsFragment) {
            if (refreshVoucherDetails) {
                ((LoyaltyVoucherDetailsFragment) fragment).loadVoucherFromRealm(true);
            } else {
                refreshVoucherDetails = true;
            }
        }
    }

    public boolean isVoucherCurrentPresent(Fragment fragment){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        return currentFragment != null && currentFragment.getClass().equals(fragment.getClass());
    }

    public boolean isGetVouchersFinished() {
        return isGetVouchersFinished;
    }

    public boolean isGetReservedVouchersFinished() {
        return isGetReservedVouchersFinished;
    }

    public boolean isGetVouchersError() {
        return isGetVouchersError;
    }

    public boolean isGetReservedVouchersError() {
        return isGetReservedVouchersError;
    }

    public boolean isRefreshVoucherDetails() {
        return refreshVoucherDetails;
    }

    @Override
    public void setRefreshVoucherDetails(boolean refresh) {
        refreshVoucherDetails = refresh;
    }

    @Override
    public void reloadVouchers() {
        isGetReservedVouchersFinished = false;
        isGetVouchersFinished = false;
        isGetReservedVouchersError = false;
        isGetVouchersFinished = false;
        getLoyaltySegment();
        getReservedVouchers();
    }

    public void onRecycleScrollViewCreated(RecyclerView scrollView) {
        setupRecycleScrollViewForCurrentView(scrollView);
    }

    @Override
    public void onScrollViewCreated(ScrollView scrollView) {
       setupScrollViewForCurrentView(scrollView);
    }

    @Override
    public void onBackPressed() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (shouldOpenParentActivityLoyaltyVoucherListFragmentWhenOnBack()) {
            D.v("Recreate back stack");
            Intent intent = new Intent(this,LoyaltyActivity.class);
            startActivity(intent);
            finish();
        } else {
          super.onBackPressed();
        }
    }

    private boolean shouldOpenParentActivityLoyaltyVoucherListFragmentWhenOnBack(){
        Activity activity = VodafoneController.findActivity(LoyaltyActivity.class);
        //not in LoyaltyVoucherDetailsFragment when onBack
        Fragment fragment = VodafoneController.findFragment(LoyaltyVoucherDetailsFragment.class);
        if(activity==null && fragment==null){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkIfEbuIsVdfSubscriptionOrCbu() {
        User user = VodafoneController.getInstance().getUser();
        ProfileSubscriptionSuccess profileSubscriptionSuccess = (ProfileSubscriptionSuccess) RealmManager.getRealmObject(ProfileSubscriptionSuccess.class);

        if(user instanceof EbuMigrated) {
            if (((EbuMigrated) user).isAuthorisedPerson()
                    || ((EbuMigrated) user).isChooser()
                    || ((EbuMigrated) user).isDelagatedChooser()) {
                if (profileSubscriptionSuccess != null
                        && profileSubscriptionSuccess.getIsVDFSubscription() != null
                        && profileSubscriptionSuccess.getIsVDFSubscription()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
