package ro.vodafone.mcare.android.application.controllers;

import android.content.Context;

import io.realm.Realm;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistoryRow;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistorySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.SessionIdMap;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.identity.CurrentIdentity;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.TimeToLeaveMap;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlan;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DevicesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.ui.activities.filters.PhonesFilterActivity;
import ro.vodafone.mcare.android.ui.activities.filters.ServicesFilterActivity;
import ro.vodafone.mcare.android.ui.fragments.loyaltyMarket.LoyaltyMarketVoucherListingsFragment;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.creditplus.CreditPlusWidgetController;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;


/**
 * Created by Victor Radulescu on 2/18/2017.
 */
public class UserDataController {

    private static String ssoTokenId;
    private static UserDataController ourInstance = new UserDataController();
    private LoginAction currentLoginAction = LoginAction.NONE;
    private IntentActionName currentDashboardAction = IntentActionName.NONE;
    private IntentActionName currentNotificationDashboardAction = IntentActionName.NONE;
    private String userRoleBeforeLogoutAfterFailedIntentAction;
    private boolean switchFragmentAfterBundle = false;
    private boolean previousUserWasSeamlessBeforeLogoutFailedIntentAction = false;

    private Class[] userDataClasses = {
            UserProfile.class, Profile.class, Subscriber.class,
            UserProfileHierarchy.class, BalanceCreditSuccess.class, CostControl.class,
            UnicaOffer.class, UnicaOffersSuccess.class, Balance.class, NotificationFlag.class,
            RechargeHistorySuccess.class, RechargeHistoryRow.class, AncomPendingOffersSuccess.class,
            ShopLoginSuccess.class, ShopPricePlan.class, ShopProductsSuccess.class, ShopProduct.class,
            ActiveOffersPostpaidSuccess.class, ActiveOfferPostpaid.class, GdprGetResponse.class,
            EligibleOffersPostSuccess.class, EligibleOffersSuccess.class, BalanceSecondarySuccess.class,
            AccessTypeSuccess.class, ActiveOffersSuccess.class, ActiveOffer.class, CurrentIdentity.class,
            UserEntitiesSuccess.class, EntityChildItem.class, EntityDetailsSuccess.class, ProfileSubscriptionSuccess.class,
            BillSummaryItem.class, BillSummarySuccess.class,
            BillSummaryDetailsSuccess.class, SummaryDetails.class, ServiceDetails.class,
            BillHistoryDetails.class, BillHistorySuccess.class, PrepaidOfferRow.class, PostpaidOfferRow.class,
            FavoriteNumbersSuccess.class, FavoriteNumber.class, AccessTypeEBU.class, GetByOperatorSuccess.class,
            DeviceFamiliesList.class, DevicesList.class, ShopEligibilitySuccess.class
    };

    private UserDataController() {
    }

    public static UserDataController getInstance() {
        return ourInstance;
    }

    private void deleteUserInfo() {
        ssoTokenId = null;
        Realm realm = Realm.getDefaultInstance();

        VodafoneController.getInstance().clearData();
        UserSelectedMsisdnBanController.getInstance().clearData();
        CostControlWidgetController.getInstance().destroy();
        BaseService.clearData();
        VoiceOfVodafoneController.getInstance().clearVoiceOfVodafone();
        D.w("deleteUserInfo ");

        RealmManager.deleteValues(realm, TimeToLeaveMap.class, TimeToLeaveMap.IS_USER_DATA, true);
        RealmManager.deleteValues(realm, SessionIdMap.class, SessionIdMap.IS_USER_DATA, true);
        RealmManager.deleteMultiple(realm, userDataClasses);
        LoyaltyMarketVoucherListingsFragment.resetOptions();

        ServicesFilterActivity.resetValues();
        PhonesFilterActivity.resetValues();
        CreditPlusWidgetController.deleteInstance();
        CredentialUtils.clearCredentials();

        realm.close();
    }

    public void startAction() {

        switch (currentLoginAction) {
            case LOGOUT:
                deleteUserInfo();
                currentLoginAction = LoginAction.NONE;
                break;
            case NONE:
                break;
        }
    }
    //TODO cleanup
    public void startDashboardAction(Context context) {
        try {
            if (currentNotificationDashboardAction == null && userRoleBeforeLogoutAfterFailedIntentAction != null && VodafoneController.getInstance().isSeamless()) {
                currentDashboardAction = IntentActionName.NONE;
                return;
            }
            //don't do nothing if previos already failed
            if (currentNotificationDashboardAction != null && userRoleBeforeLogoutAfterFailedIntentAction != null && !currentDashboardAction.isAllowed()) {
                currentNotificationDashboardAction = IntentActionName.NONE;
            }

            if (currentNotificationDashboardAction != null) {
                currentNotificationDashboardAction.runActionNotification(context, false);
            }

            if (userRoleBeforeLogoutAfterFailedIntentAction != null && userRoleBeforeLogoutAfterFailedIntentAction.equalsIgnoreCase(VodafoneController.getInstance().getUser().getUserProfile().getUserRoleString())
                    && !VodafoneController.getInstance().isSeamless()) {
                if (currentDashboardAction.isAllowed()) {
                    currentDashboardAction.runAction(context, false);
                } else {
                    currentDashboardAction = IntentActionName.NONE;
                }
                previousUserWasSeamlessBeforeLogoutFailedIntentAction = false;
                userRoleBeforeLogoutAfterFailedIntentAction = null;
            } else if (userRoleBeforeLogoutAfterFailedIntentAction == null) {
                currentDashboardAction.runAction(context, false);
            } else if (!previousUserWasSeamlessBeforeLogoutFailedIntentAction) {
                currentDashboardAction.runAction(context, false);
            }
            safeDisableAnyRedirectIntentIfNoNeedForFragmentToBeSwitched();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void safeDisableAnyRedirectIntentIfNoNeedForFragmentToBeSwitched() {
        if (UserDataController.getInstance().getCurrentDashboardAction().getFragmentClassName() == null) {
            UserDataController.getInstance().setCurrentDashboardAction(IntentActionName.NONE);
            UserDataController.getInstance().setCurrentNotificationDashboardAction(IntentActionName.NONE);
        }
    }

    public LoginAction getCurrectLoginAction() {
        return currentLoginAction;
    }

    public UserDataController setCurrentLoginAction(LoginAction currectLoginAction) {
        this.currentLoginAction = currectLoginAction;
        return ourInstance;
    }

    public IntentActionName getCurrentDashboardAction() {
        return currentDashboardAction;
    }

    public void setCurrentDashboardAction(IntentActionName currentDashboardAction) {
        //userRoleBeforeLogoutAfterFailedIntentAction = VodafoneController.getInstance().getUser().getUserRole();
        this.currentDashboardAction = currentDashboardAction;
    }

    public boolean isSwitchFragmentAfterBundle() {
        return switchFragmentAfterBundle;
    }

    public void setSwitchFragmentAfterBundle(boolean switchFragmentAfterBundle) {
        this.switchFragmentAfterBundle = switchFragmentAfterBundle;
    }

    public void setUserRoleBeforeLogoutAfterFailedIntentAction(String userRoleBeforeLogoutAfterFailedIntentAction) {
        this.userRoleBeforeLogoutAfterFailedIntentAction = userRoleBeforeLogoutAfterFailedIntentAction;
    }

    public boolean isPreviousUserWasSeamlessBeforeLogoutFailedIntentAction() {
        return previousUserWasSeamlessBeforeLogoutFailedIntentAction;
    }

    public void setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(boolean previousUserWasSeamlessBeforeLogoutFailedIntentAction) {
        this.previousUserWasSeamlessBeforeLogoutFailedIntentAction = previousUserWasSeamlessBeforeLogoutFailedIntentAction;
    }

    public IntentActionName getCurrentNotificationDashboardAction() {
        return currentNotificationDashboardAction;
    }

    public void setCurrentNotificationDashboardAction(IntentActionName currentNotificationDashboardAction) {
        this.currentNotificationDashboardAction = currentNotificationDashboardAction;
    }

    public synchronized static String getSsoTokenId() {
        if (ssoTokenId == null) {
            UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
            if (userProfile != null) {
                ssoTokenId = userProfile.getSsoTokenId();
            }
        }
        return ssoTokenId;
    }

    public synchronized static void setSsoTokenId(String ssoTokenId) {
        UserDataController.ssoTokenId = ssoTokenId;
    }

}
