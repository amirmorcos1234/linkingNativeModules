package ro.vodafone.mcare.android.ui.activities.loyalty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tealium.library.Tealium;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyOptInNotAuthorizedState;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyOptInStateFragment;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyOptOutStateFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyPointsActivity extends MenuActivity {
    public static String TAG = "LoyaltyPointsActivity";
    public static final String OPT_IN = "optIn";
    public static final String OPT_OUT = "optOut";
    public static final String OPT_IN_UNAUTHORIZED = "optInUnauthorized";
    public static String SERVICE_ERROR = "error";
    public static String SERVICE_SUCCES = "success";
    public NavigationHeader navigationHeader;
    VodafoneGenericCard loadingCard;
    VodafoneGenericCard errorCard;
    private String shopSessionToken;
    private FrameLayout container;
    private String banState;
    View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eShopLogin();
        }
    };

    @Override
    protected int setContent() {
        return R.layout.activity_loyalty_points;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        container = (FrameLayout) findViewById(R.id.fragment_container);
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this)
                .setTitle(LoyaltyLabels.getLoyaltyPointsActivityPageTitle())
                .displayDefaultHeader();
        setBanListOnSelector();
        eShopLogin();

        LoyaltyPointsTrackingEvent event = new LoyaltyPointsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
           // transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            if (fragment.getClass() != LoyaltyOptInNotAuthorizedState.class && fragment.getClass() != LoyaltyOptOutStateFragment.class && fragment.getClass() != LoyaltyOptInStateFragment.class) {
                transaction.addToBackStack(null);
            } else {
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }
            }
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {
    }

    private void setBanListOnSelector() {
        Log.d(TAG, "setBanListOnSelector");
        navigationHeader.buildBanSelectorHeader();
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    public String getShopSessionToken() {
        return shopSessionToken;
    }

    public void setShopSessionToken(String shopToken) {
        this.shopSessionToken = shopToken;
    }

    private void eShopLogin() {
        showLoading();
        new ShopService(this).postShopLogin(null, null).subscribe(new RequestSessionObserver<GeneralResponse<ShopLoginSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopLoginSuccess> response) {
                if (response.getTransactionStatus() == 0) {
                    setShopSessionToken(response.getTransactionSuccess().getShopSessionToken());
                    hideLoading();
                    getBanState();
                } else {
                    showError(errorClickListener);
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showError(errorClickListener);
            }
        });

    }

    public void showError(View.OnClickListener listener) {
        container.removeAllViews();
        errorCard = new VodafoneGenericCard(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(errorCard, layoutParams);

        errorCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
        errorCard.getErrorView().setOnClickListener(listener);
    }

    public void showLoading() {
        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }
        loadingCard = new VodafoneGenericCard(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(loadingCard, layoutParams);
        loadingCard.showLoading(true);
    }

    public void hideLoading() {
        if(loadingCard != null) {
            loadingCard.hideLoading();
        }
    }

    public void getBanState() {
        showLoading();
        ShopService shopService = new ShopService(this);
        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        shopService.getLoyaltyProgram(selectedBan==null?"" : selectedBan, shopSessionToken)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<ShopLoyaltyProgramSuccess>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showError(errorClickListener);
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopLoyaltyProgramSuccess> response) {
                        super.onNext(response);
                        if (response.getTransactionStatus() == 0) {
                            getChatBubble().hideBubble();
                            hideLoading();
                            if (response.getTransactionSuccess() != null && response.getTransactionSuccess().getState() != null) {
                                banState = response.getTransactionSuccess().getState();
                                switch (banState) {
                                    case OPT_IN:
                                        addFragment(new LoyaltyOptInStateFragment());
                                        break;
                                    case OPT_OUT:
                                        addFragment(new LoyaltyOptOutStateFragment());
                                        break;
                                    case OPT_IN_UNAUTHORIZED:
                                        addFragment(new LoyaltyOptInNotAuthorizedState());
                                        getChatBubble().displayBubble(true);
                                        break;
                                    default:
                                        showError(errorClickListener);
                                        break;
                                }
                            } else {
                                showError(errorClickListener);
                            }
                        }
                        else {
                            showError(errorClickListener);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }
                });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() with code: " + resultCode);

        if (data != null) {
            Log.d(TAG, "onActivityResult: data not null");
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                refreshPage();
            }
        }
    };

    public void refreshPage() {
        if (FragmentUtils.getVisibleFragment(this, false) != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(FragmentUtils.getVisibleFragment(this, false));
            transaction.commit();
        }
        getBanState();
    }

    public void setTitle() {
        try {
            setTitle((String) LoyaltyLabels.getLoyaltyPointsActivityPageTitle());
            navigationHeader.setTitle(LoyaltyLabels.getLoyaltyPointsActivityPageTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.LOYALTY_PROGRAM);
    }

    public static class LoyaltyPointsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty program";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty program");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);

        }
    }
}
