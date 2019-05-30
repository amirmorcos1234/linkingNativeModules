package ro.vodafone.mcare.android.utils.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import butterknife.OnClick;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.application.controllers.TemporaryFlagController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.payBill.BillingWebViewModel;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.activities.support.ChatService;
import ro.vodafone.mcare.android.ui.fragments.Beo.BeoFragment;
import ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFilterModel;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.fragments.loyaltyMarket.LoyaltyVoucherDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.AncomPendingOffersFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhoneDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhonesFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PricePlanDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PricePlansFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.RetentionFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.AnonymousPayBillFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.PayBillFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.CustomServicesFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.ElectronicBillFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.MinorAccountFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.PermissionsFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.SettingsFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.SimDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.VodafoneAndPartenersFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpAnonymousFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPostpaidPost4PreFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPrepaidOwnNumberFragment;
import ro.vodafone.mcare.android.ui.fragments.travelingAboard.TravellingServiceAdministration;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import rx.Observer;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_BILLED;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_PREPAID;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_UNBILLED;
import static ro.vodafone.mcare.android.utils.navigation.NavigationAction.EXTRA_PARAMETER_BUNDLE_KEY;
import static ro.vodafone.mcare.android.utils.navigation.NavigationAction.FRAGMENT_CLASS_NAME_BUNDLE_KEY;

public enum IntentActionName {
    NONE(),
    DASHBOARD("dashboard_page", null, null) {
        @Override
        public boolean isAllowed() {
            return VodafoneController.getInstance().getUser() != null;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            super.setIntentAction(intent);
        }
    },
    DASHBOARD_NO_FLAGS("dashboard_page", null, null),
    PRIVACY_NO_FLAGS("privacy_page", null, null),
    TOP_UP("topUp_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    TOP_UP_AUTO_REDIRECT_NOTIFICATION("topUp_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void setNotificationAction(Context context) {
            User user = VodafoneController.getInstance().getUser();
            if (user == null) {
                return;
            } else if (user instanceof PostPaidUser && !(user instanceof SeamlessEbuUser)) {
                redirectIntentName = TOP_UP_POSTPAID_OTHER_NUMBER;
            } else if (user instanceof PrepaidUser) {
                redirectIntentName = TOP_UP_PREPAID_OWN_NUMBER;
            } else {
                return;
            }
        }
    },

    TOP_UP_PREPAID_OWN_NUMBER("topUp_page", TopUpPrepaidOwnNumberFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    }, TOP_UP_ANONYMOUS("topUp_page", TopUpAnonymousFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    }, TOP_UP_POSTPAID_OTHER_NUMBER("topUp_page", TopUpPostpaidPost4PreFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    OFFERS("offers_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if(user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

		@Override
		public void doNotAllowedRedirectAction(Context context) {
			super.doNotAllowedRedirectAction(context);
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof NonVodafoneUser) {
                if (context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(OFFERS);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
		}
    },
    OFFERS_BANNER_FROM_GAUGE("offers_page", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
        }
    },
    OFFERS_BEO("offers_page", BeoFragment.class.getCanonicalName(), "beo") {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            boolean isSeamless = VodafoneController.getInstance().isSeamless();
            //user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPrepaidUser || user instanceof Seamless
            //if (user instanceof PostPaidUser) {
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser ||
                    isSeamless || !(user instanceof PrepaidUser)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(OFFERS_BEO);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },
    OFFERS_EXTRAOPTIONS("offers_page", BeoFragment.class.getCanonicalName(), "beo") {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return !(user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser ||
                    !(user instanceof PostPaidUser));
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(OFFERS_EXTRAOPTIONS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            // UserDataController.getInstance().setCurrentDashboardAction(NONE);
        }
    },
    OFFERS_BEO_NO_SEAMLESS("offers_page", BeoFragment.class.getCanonicalName(), "beo") {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return !(user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(OFFERS_BEO_NO_SEAMLESS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    }, OFFERS_BEO_WITH_SEAMLESS("offers_page", BeoFragment.class.getCanonicalName(), "beo") {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    OFFERS_BEO_DETAILS("offers_page", BeoDetailedFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity) &&
                    !(getExtraParameter().equals("fromRoaming"))) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return !(user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(OFFERS_BEO_DETAILS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },
    OFFERS_SERVICES("offers_page", BeoFragment.class.getCanonicalName(), "services") {
        @Override
        protected void setIntentAction(Intent intent) {
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            super.setIntentAction(intent);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(OFFERS_SERVICES);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },
    RETENTION("offers_page", RetentionFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            //intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            boolean isSeamless = VodafoneController.getInstance().isSeamless();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || isSeamless || user instanceof SeamlessPrepaidHybridUser || user instanceof SeamlessEbuUser ||
                    user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(RETENTION);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    RETENTION_PHONES_LISTING("offers_page", PhonesFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            //intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setOneUsageSerializedData(OffersActivity.DEEP_LINK_KEY);
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
			if(user instanceof CBUUser || user instanceof PrepaidHybridUser)
				return true;
			else
				return false;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(RETENTION_PHONES_LISTING);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    RETENTION_PHONE_DETAILS("offers_page", PhoneDetailsFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            //intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setOneUsageSerializedData(OffersActivity.DEEP_LINK_KEY);
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if(user instanceof CBUUser || user instanceof PrepaidHybridUser)
                return true;
            else
                return false;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(RETENTION_PHONE_DETAILS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    RETENTION_PRICEPLANS_LISTING("offers_page", PricePlansFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            //intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setOneUsageSerializedData(OffersActivity.DEEP_LINK_KEY);
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
			if(user instanceof CBUUser || user instanceof PrepaidHybridUser)
				return true;
			else
				return false;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(RETENTION_PRICEPLANS_LISTING);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    RETENTION_PRICEPLAN_DETAILS("offers_page", PricePlanDetailsFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            //intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setOneUsageSerializedData(OffersActivity.DEEP_LINK_KEY);
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
			if(user instanceof CBUUser || user instanceof PrepaidHybridUser)
				return true;
			else
				return false;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(RETENTION_PRICEPLAN_DETAILS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    SERVICES_PRODUCTS("services_page", null, null) {
        @Override
        public boolean isAllowed() {
            boolean isSeamless = VodafoneController.getInstance().isSeamless();
            User user = VodafoneController.getInstance().getUser();//postpaid produse si serviciile tale  si prepaid serviciile tale
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || user instanceof SeamlessEbuUser || isSeamless || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            saveIntentActionForAfterRelogin(SERVICES_PRODUCTS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @OnClick
        protected void runAction(Context context, boolean finishCurrent, boolean newInstance) {
            if (!(VodafoneController.getInstance().getUser() instanceof EbuMigrated) || AppConfiguration.getShowEbuProductAndServices()) {
                super.runAction(context, finishCurrent, newInstance);
            }
        }
    },

    COST_CONTROL_POSTPAID("services_page", null, null) {
        @Override
        public boolean isAllowed() {
            boolean isSeamless = VodafoneController.getInstance().isSeamless();
            User user = VodafoneController.getInstance().getUser();//postpaid produse si serviciile tale  si prepaid serviciile tale
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser ||
                    isSeamless || !(user instanceof PostPaidUser)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            saveIntentActionForAfterRelogin(COST_CONTROL_POSTPAID);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    ANCOM_PENDING_OFFERS("offers_page", AncomPendingOffersFragment.class.getCanonicalName(), null) {
        @Override
        public boolean isAllowed() {
            //Todo
            boolean isSeamless = VodafoneController.getInstance().isSeamless();
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    isSeamless || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            saveIntentActionForAfterRelogin(ANCOM_PENDING_OFFERS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    COST_CONTROL("services_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof PrepaidUser) {
                return true;
            }
            return false;
        }
    },
    TRAVELLING_ABOARD("travelling_page", null, null) {
        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(TRAVELLING_ABOARD);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    TRAVELLING_ABOARD_ADMINISTRATION("travelling_page", TravellingServiceAdministration.class.getCanonicalName(), null) {
        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                    || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(TRAVELLING_ABOARD_ADMINISTRATION);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    SETTINGS_BLOCK_SIM("block_sim", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if(user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(SETTINGS_BLOCK_SIM);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)
                    && !(context instanceof SettingsActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    SETTINGS_SIM_DETAILS("settings_page", SimDetailsFragment.class.getCanonicalName(), null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if(user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(SETTINGS_SIM_DETAILS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)
                    && !(context instanceof SettingsActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    SETTINGS_EBILL_FRAGMENT("settings_page", ElectronicBillFragment.class.getCanonicalName(), null) {
        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return !(user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(SETTINGS_EBILL_FRAGMENT);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    SETTINGS("settings_page", SettingsFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    SETTINGS_PERMISSIONS_FRAGMENT("settings_page", PermissionsFragment.class.getCanonicalName(), null) {

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return (user instanceof PrepaidUser
                    && !(user instanceof SeamlessPrepaidUser)
                    && !(user instanceof SeamlessPrepaidHybridUser))
                    || user instanceof PrivateUser
                    || user instanceof ResCorp
                    || user instanceof ResSub
                    || (user instanceof EbuMigrated
                    && !(user instanceof SeamlessEbuUser));
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if(user instanceof NonVodafoneUser) {
                if(context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(SETTINGS_PERMISSIONS_FRAGMENT);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_MINOR("settings_page", MinorAccountFragment.class.getCanonicalName(), PermissionsFragment.MINOR_ACCOUNT_CARD) {

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return (user instanceof PrepaidUser
                    && !(user instanceof SeamlessPrepaidUser)
                    && !(user instanceof SeamlessPrepaidHybridUser))
                    || user instanceof PrivateUser
                    || user instanceof ResCorp
                    || user instanceof ResSub;
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if(user instanceof NonVodafoneUser) {
                if(context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_MINOR);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },

    SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_VODAFONE("settings_page", VodafoneAndPartenersFragment.class.getCanonicalName(), PermissionsFragment.VODAFONE_PERMISSIONS_CARD) {

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return (user instanceof PrepaidUser
                    && !(user instanceof SeamlessPrepaidUser)
                    && !(user instanceof SeamlessPrepaidHybridUser))
                    || user instanceof PrivateUser
                    || user instanceof ResCorp
                    || user instanceof ResSub
                    || (user instanceof EbuMigrated
                    && !(user instanceof SeamlessEbuUser));
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if(user instanceof NonVodafoneUser) {
                if(context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_VODAFONE);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },

    SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_PARTENERS("settings_page", VodafoneAndPartenersFragment.class.getCanonicalName(), PermissionsFragment.VODAFONE_PARTENERS_CARD) {

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            return (user instanceof PrepaidUser
                    && !(user instanceof SeamlessPrepaidUser)
                    && !(user instanceof SeamlessPrepaidHybridUser))
                    || user instanceof PrivateUser
                    || user instanceof ResCorp
                    || user instanceof ResSub
                    || (user instanceof EbuMigrated
                    && !(user instanceof SeamlessEbuUser));
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if(user instanceof NonVodafoneUser) {
                if(context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_PARTENERS);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },

    SETTINGS_SERVICES("settings_page", CustomServicesFragment.class.getCanonicalName(), null),

    PAY_BILL("paybill_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    }, PAY_BILL_OWN("paybill_page", PayBillFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            intent.putExtra("getInvoiceFromRealm", true);
        }
    }, PAY_BILL_ANONYMOUS("paybill_page", AnonymousPayBillFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },

    LOGOUT("login_page", null, null) {
        @Override
        public void runAction(Context context, boolean finishCurrent) {
            UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT);

            String res = getExtraParameter();

            new ChatService(context).logOutChat(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, ChatBubbleActivity.mEmail).subscribe(new Observer<String>() {
                @Override
                public void onNext(String s) {
                    D.w();
                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    D.e("e = " + e);
                }
            });


            new AuthenticationService(VodafoneController.getInstance()).logout().subscribe(new Observer<GeneralResponse>() {
                @Override
                public void onCompleted() {
                    Log.d("logout", "completed");

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("logout", "onError " + e.getMessage());


                }

                @Override
                public void onNext(GeneralResponse generalResponse) {
                    Log.d("logout", "onNext " + generalResponse.getTransactionStatus());

                }
            });
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);

            super.setIntentAction(intent);
        }

    }, LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA("login_page", null, null) {
        @Override
        public void runAction(Context context, boolean finishCurrent) {
            UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT);
            new ChatService(context).logOutChat(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, ChatBubbleActivity.mEmail).subscribe(new Observer<String>() {
                @Override
                public void onNext(String s) {
                    D.w();
                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    D.e("e = " + e);
                }
            });
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);

            super.setIntentAction(intent);
        }

    },

    SUPPORT_24_7("support_activity", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            D.w();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);

            super.setIntentAction(intent);
        }
    },

    SUPPORT_24_7_FAQ("support_activity", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            D.w();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);

            intent.putExtra("inflateFAQ", true);

            super.setIntentAction(intent);
        }
    },

    SUPPORT_24_7_CHAT("support_activity", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            D.w();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);

            intent.putExtra("inflateChat", true);

            super.setIntentAction(intent);
        }
    },

    LOGIN("login_page", null, null) {
        @Override
        public void runAction(Context context, boolean finishCurrent) {
            UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT);
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);

            super.setIntentAction(intent);
        }

    },

    BILLING_OVERVIEW("billing_overview", null, null) {
        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessEbuUser || !(user instanceof PostPaidUser)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(BILLING_OVERVIEW);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    CALL_DETAILS("callDetails_page", null, null) {
        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessPrepaidUser || user instanceof SeamlessEbuUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(CALL_DETAILS);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },
    CALL_DETAILS_REDIRECT_NOTIFICATION("callDetails_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void setNotificationAction(Context context) {
            User user = VodafoneController.getInstance().getUser();
            if (user == null) {
                return;
            } else if (user instanceof PostPaidUser) {
                redirectIntentName = CALL_DETAILS_UNBILLED;
            } else if (user instanceof PrepaidUser) {
                redirectIntentName = CALL_DETAILS_PREPAID;
            } else {
                return;
            }
        }
    },
    CALL_DETAILS_UNBILLED("callDetails_page", CallDetailsFragment.class.getCanonicalName(), "bundle_key") {
        Category category;

        private Category getCategory() {
            return category;
        }

        private Category getCategoryOnceAndClean() {
            Category categoryToReturn = category;
            category = null;
            return categoryToReturn;
        }

        /**
         * Call it before starting intent
         * @param category
         */
        public void setCategory(Category category) {
            this.category = category;
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPrepaidUser
                    || user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void runAction(Context context, boolean finishCurrent) {
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            CallDetailsFilterModel callDetailsFilterModel = new CallDetailsFilterModel();
            callDetailsFilterModel.setReportType(REPORT_TYPE_UNBILLED);
            callDetailsFilterModel.setCategory(getCategoryOnceAndClean());
            //For "Incluse" tab set costIndicatr - o
            //callDetailsFilterModel.setCostIndicator("0");
            String serializedObject = new Gson().toJson(callDetailsFilterModel);
            intent.putExtra(CALL_DETAILS_UNBILLED.extraParameter, serializedObject);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(CALL_DETAILS_UNBILLED);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    CALL_DETAILS_PREPAID("callDetails_page", CallDetailsFragment.class.getCanonicalName(), "bundle_key") {
        Category category;

        private Category getCategory() {
            return category;
        }

        private Category getCategoryOnceAndClean() {
            Category categoryToReturn = category;
            category = null;
            return categoryToReturn;
        }

        /**
         * Call it before starting intent
         * @param category
         */
        public void setCategory(Category category) {
            this.category = category;
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessPrepaidUser || user instanceof SeamlessEbuUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void runAction(Context context, boolean finishCurrent) {
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            CallDetailsFilterModel callDetailsFilterModel = new CallDetailsFilterModel();
            callDetailsFilterModel.setReportType(REPORT_TYPE_PREPAID);
            callDetailsFilterModel.setCategory(getCategoryOnceAndClean());
            //For "Incluse" tab set costIndicatr - o
            //callDetailsFilterModel.setCostIndicator("0");
            String serializedObject = new Gson().toJson(callDetailsFilterModel);
            intent.putExtra(CALL_DETAILS_PREPAID.extraParameter, serializedObject);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(CALL_DETAILS_PREPAID);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    CALL_DETAILS_BILLED("callDetails_page", CallDetailsFragment.class.getCanonicalName(), "bundle_key") {
        Category category;
        long lastBillClosedDate;

        private Category getCategory() {
            return category;
        }

        private Category getCategoryOnceAndClean() {
            Category categoryToReturn = category;
            category = null;
            return categoryToReturn;
        }

        public long getLastBillClosedDateOnceAndClean() {
            long lastBillClosedDateToReturn = lastBillClosedDate;
            lastBillClosedDate = 0;
            return lastBillClosedDateToReturn;
        }

        /**
         * Call it before starting intent
         * @param lastBillClosedDate
         */
        public void setLastBillClosedDate(long lastBillClosedDate) {
            this.lastBillClosedDate = lastBillClosedDate;
        }

        /**
         * Call it before starting intent
         * @param category
         */
        public void setCategory(Category category) {
            this.category = category;
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess ||
                    user instanceof SeamlessPrepaidUser || user instanceof SeamlessEbuUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public void runAction(Context context, boolean finishCurrent) {
            super.runAction(context, finishCurrent);
        }

        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            CallDetailsFilterModel callDetailsFilterModel = new CallDetailsFilterModel();
            callDetailsFilterModel.setReportType(REPORT_TYPE_BILLED);
            callDetailsFilterModel.setCategory(getCategoryOnceAndClean());

            //@Serban Radulescu
            //very ugly fix for VNM-6463
            //to get the selected bill from billHistory

            long lastBillFromPayBill = 0;
            String redirectParams = getOneUsageSerializedData();
            String[] split = redirectParams.split(":");
            String tabSelected = split[1];
            try {
                lastBillFromPayBill = Long.parseLong(split[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (lastBillFromPayBill != 0) {
                callDetailsFilterModel.setLastBillClosedDate(lastBillFromPayBill);
            } else {
                callDetailsFilterModel.setLastBillClosedDate(getLastBillClosedDateOnceAndClean());
            }
            //For "Incluse" tab set costIndicatr - o
            callDetailsFilterModel.setCostIndicator(tabSelected);
            String serializedObject = new Gson().toJson(callDetailsFilterModel);
            intent.putExtra(CALL_DETAILS_BILLED.extraParameter, serializedObject);
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(CALL_DETAILS_BILLED);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },

    LOYALTY_SELECTION_ACTIVITY("loyalty_selection_activity", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPostPaidHighAccess
                    || user instanceof SeamlessPrepaidUser
                    || user instanceof SeamlessEbuUser
                    || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if (user instanceof NonVodafoneUser) {
                if (context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(LOYALTY_SELECTION_ACTIVITY);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },
    LOYALTY_MARKET("loyalty_market", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity) && !(context instanceof LoyaltyActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPostPaidHighAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if (user instanceof NonVodafoneUser) {
                if (context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(LOYALTY_MARKET);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },
    LOYALTY_MARKET_VOUCHER_DETAILS("loyalty_market", LoyaltyVoucherDetailsFragment.class.getCanonicalName(), null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof LoyaltyActivity) && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPostPaidHighAccess ||
                    user instanceof SeamlessEbuUser || user instanceof NonVodafoneUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);

            User user = VodafoneController.getInstance().getUser();
            if (user instanceof NonVodafoneUser) {
                if (context instanceof DashboardActivity) {
                    return;
                }
                new NavigationAction(context).finishCurrent(true).startAction(DASHBOARD);
            } else {
                saveIntentActionForAfterRelogin(LOYALTY_MARKET_VOUCHER_DETAILS);
                new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
            }
        }
    },

    LOYALTY_PROGRAM("loyalty_program", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (TemporaryFlagController.getInstance().isHideNonTelco()) {
                if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                    ((Activity) context).finish();
                }
            } else {
                if (context instanceof Activity && !(context instanceof LoyaltyActivity)) {
                    ((Activity) context).finish();
                }
            }
        }

        @Override
        public boolean isAllowed() {
            User user = VodafoneController.getInstance().getUser();
            if (user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPostPaidHighAccess
                    || user instanceof SeamlessEbuUser) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            saveIntentActionForAfterRelogin(LOYALTY_PROGRAM);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    }, WEBVIEW("webview_page", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            intent.putExtra(WebviewActivity.KEY_URL, getOneUsageSerializedData());
        }
    }, BILLING_WEBVIEW("billing_page", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            BillingWebViewModel billingWebViewModel = new Gson().fromJson(getSerializedData(), BillingWebViewModel.class);
            String htmlForm = billingWebViewModel.getHtmlInputs();
            intent.putExtra(WebviewActivity.KEY_URL, htmlForm);
        }
    }, STORE_LOCATOR_DETAILS("store_locator_details_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    STORE_LOCATOR("store_locator_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    YOUR_PROFILE("your_profile_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    IDENTITY_SELECTOR("identity_selector_page", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    WEBVIEW_RELOGIN("webview_page", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
        }

        @Override
        public boolean isAllowed() {
            return false;
        }

        @Override
        public void doNotAllowedRedirectAction(Context context) {
            super.doNotAllowedRedirectAction(context);
            WEBVIEW.setOneUsageSerializedData(getOneUsageSerializedData());
            saveIntentActionForAfterRelogin(WEBVIEW);
            new NavigationAction(context).finishCurrent(true).startAction(LOGOUT);
        }
    },
    VODAFONE_TV("vodafone_tv", null, null){
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    },
    MY_CARDS("my_cards", null, null) {
        @Override
        protected void doActionAfterIntentActionCompleted(Context context) {
            if (context instanceof Activity && !(context instanceof DashboardActivity)) {
                ((Activity) context).finish();
            }
        }
    }, ADD_CREDIT_CARD_WEBVIEW("add_credit_card_web_view", null, null) {
        @Override
        protected void setIntentAction(Intent intent) {
            super.setIntentAction(intent);
            AddCardResponse addCardResponse = new Gson().fromJson(getSerializedData(), AddCardResponse.class);
            String htmlForm = addCardResponse.getHtmlForm();
            intent.putExtra(WebviewActivity.KEY_URL, htmlForm);
        }
    };

    /**
     * Intent action name declared in manifest.
     * Example in manifest:
     * <activity
     * android:name="ro.vodafone.mcare.android.ui.activities.PayBillActivity">
     * <intent-filter>
     * <action android:name="paybill_page" />
     * <category android:name="android.intent.category.DEFAULT" />
     * </intent-filter>
     * <meta-data
     * <p>
     * </activity>
     * <p>
     * android:name="paybill_page" specific PayBillActivity
     */
    final private String intentActionName;
    /**
     * Fragment class name as ro.vodafone.mcare.android.ui.fragments.topUp
     */
    final private String fragmentClassName;
    /**
     * Extra value parameter
     */
    private String extraParameter;

    private String oneUsageSerializedData;

    private String secondSerializedData;

    IntentActionName redirectIntentName;

    //String vExtraParameter;

    //String vIntentActionName;

    //String vFragmentClassName;

    IntentActionName() {
        intentActionName = null;
        fragmentClassName = null;
    }

    IntentActionName(@NonNull String intentActionName,
                     @Nullable String fragmentClassName,
                     @Nullable String extraParameter) {
        this.intentActionName = intentActionName;
        this.fragmentClassName = fragmentClassName;

        this.extraParameter = extraParameter;
    }

    public String getIntentActionName() {
        return /*vIntentActionName!=null ? vIntentActionName:*/ intentActionName;
    }

    public String getFragmentClassName() {
        return /*vFragmentClassName!=null ? vFragmentClassName:*/ fragmentClassName;
    }

    public String getExtraParameter() {
        return /*vExtraParameter!=null ? vExtraParameter: */extraParameter;
    }

    public void setExtraParameter(String extraParameter) {
        this.extraParameter = extraParameter;
    }

    @CallSuper
    public void runAction(Context context, boolean finishCurrent) {
        //setupDefaultVariables();
        runAction(context, false, false);
        if (context instanceof Activity && finishCurrent) {
            ((Activity) context).finish();
        }
    }

    public boolean ifSeamlessLogOut(Context context) {
        User user = VodafoneController.getInstance().getUser();
        boolean isSeamless = user instanceof SeamlessPostPaidHighAccess
                || user instanceof SeamlessPostPaidsLowAccess
                || VodafoneController.getInstance().isSeamless();

        if (isSeamless) {
            doNotAllowedRedirectAction(context);
            return true;
        }
        return false;
    }

    @CallSuper
    public void runActionNotification(Context context, boolean finishCurrent) {
        if (!isAllowed()) {
            if (VodafoneController.getInstance().isSeamless())
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(true);
            else {
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(false);
            }

            if (ifSeamlessLogOut(context))
                return;

            UserDataController.getInstance().setCurrentDashboardAction(NONE);
            UserDataController.getInstance().setUserRoleBeforeLogoutAfterFailedIntentAction(null);
            return;
        }
        if (intentActionName == null) {
            return;
        }
        setNotificationAction(context);

        if (redirectIntentName != null) {
            //safe to not loop redirect
            if (redirectIntentName != this) {
                redirectIntentName.runActionNotification(context, finishCurrent);
                return;
            }
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putBoolean("inflateFAQ", true);

        setupBundle(bundle);
        setIntentAction(intent);

        setActionToStartSpecifiedFragment();
        try {
            String action = intent.getAction();
            if (VodafoneController.currentActivity() instanceof BaseMenuActivity
                    && action != null && VodafoneController.currentActivity() != null
                    && VodafoneController.currentActivity().getIntent() != null
                    && VodafoneController.currentActivity().getIntent().getAction() != null
                    && action.equals(VodafoneController.currentActivity().getIntent().getAction())) {

                D.d(intent.getAction() + "current activity intent action " + VodafoneController.currentActivity().getIntent().getAction());

                ((BaseMenuActivity) VodafoneController.currentActivity()).switchFragmentOnCreate(fragmentClassName, extraParameter);
            } else {
                context.startActivity(intent, bundle);
                doActionAfterIntentActionCompleted(context);
                resetNotifications();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context instanceof Activity && finishCurrent) {
            ((Activity) context).finish();
        }
    }

    @CallSuper
    public void runAction(Class myClass, Context context, boolean finishCurrent) {
        //setupDefaultVariables();
        runAction(myClass, context);
        if (context instanceof Activity && finishCurrent) {
            ((Activity) context).finish();
        }
    }

    protected void runAction(Context context, boolean finishCurrent, boolean newInstance) {
        if (intentActionName == null) {
            return;
        }
        if (!isAllowed()) {
            if (VodafoneController.getInstance().isSeamless())
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(true);
            else {
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(false);
            }
            doNotAllowedRedirectAction(context);
            return;
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putBoolean("inflateFAQ", true);

        setupBundle(bundle);
        setIntentAction(intent);

        setActionToStartSpecifiedFragment();
        try {
            String action = intent.getAction();
            if (!newInstance && VodafoneController.currentActivity() instanceof BaseMenuActivity
                    && action != null && VodafoneController.currentActivity() != null
                    && VodafoneController.currentActivity().getIntent() != null
                    && VodafoneController.currentActivity().getIntent().getAction() != null
                    && action.equals(VodafoneController.currentActivity().getIntent().getAction())) {


                D.d(intent.getAction() + "current activity intent action " + VodafoneController.currentActivity().getIntent().getAction());

                ((BaseMenuActivity) VodafoneController.currentActivity()).switchFragmentOnCreate(fragmentClassName, extraParameter);
            } else {
                context.startActivity(intent, bundle);
                doActionAfterIntentActionCompleted(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActionToStartSpecifiedFragment() {
        if (fragmentClassName != null && !fragmentClassName.isEmpty()) {
            UserDataController.getInstance().setSwitchFragmentAfterBundle(true);
        }
    }

    private void resetNotifications() {
        UserDataController.getInstance().setCurrentDashboardAction(IntentActionName.NONE);
        UserDataController.getInstance().setCurrentNotificationDashboardAction(IntentActionName.NONE);
    }

    protected void runAction(Class myClass, Context context) {
        if (!isAllowed()) {
            if (VodafoneController.getInstance().isSeamless())
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(true);
            else {
                UserDataController.getInstance().setPreviousUserWasSeamlessBeforeLogoutFailedIntentAction(false);
            }
            doNotAllowedRedirectAction(context);
            return;
        }
        UserDataController.getInstance().setUserRoleBeforeLogoutAfterFailedIntentAction(null);
        runAction(context, false, false);

//        context.overridePendingTransition(R.anim.hold, R.anim.fade_in);

    }

    /**
     * Check if the intent action is allowed. Override this method to check for different permissions as current user type.
     *
     * @return true if is allowed to start the action, otherwise false
     */
    public boolean isAllowed() {
        return true;
    }

    /**
     * Ovveride this method to do your one logic for your action. Default is doing nothing.
     *
     * @param context
     */
    public void doNotAllowedRedirectAction(Context context) {
    }

    @CallSuper
    protected void setIntentAction(Intent intent) {
        if (intent != null) {
            intent.setAction(getIntentActionName());
            if (fragmentClassName != null) {
                intent.putExtra(FRAGMENT_CLASS_NAME_BUNDLE_KEY, fragmentClassName);

            }
            if (extraParameter != null) {
                intent.putExtra(EXTRA_PARAMETER_BUNDLE_KEY, extraParameter);
            }
        }
    }

    @CallSuper
    protected void setupBundle(Bundle inOutBundle) {
        if (fragmentClassName != null) {
            inOutBundle.putString(FRAGMENT_CLASS_NAME_BUNDLE_KEY, fragmentClassName);
        }
        if (extraParameter != null) {
            inOutBundle.putString(EXTRA_PARAMETER_BUNDLE_KEY, extraParameter);
        }
    }

    public void setNotificationAction(Context context) {
    }

    protected void saveIntentActionForAfterRelogin(IntentActionName intentionActionNameToSave) {
        UserDataController.getInstance().setUserRoleBeforeLogoutAfterFailedIntentAction(
                VodafoneController.getInstance().getUserProfile().getUserRoleString());

        UserDataController.getInstance().setCurrentDashboardAction(intentionActionNameToSave);

    }

    public String getOneUsageSerializedData() {
        String returnData = oneUsageSerializedData;
        oneUsageSerializedData = null;
        return returnData;
    }

    public void setOneUsageSerializedData(String oneUsageSerializedData) {
        this.oneUsageSerializedData = oneUsageSerializedData;
    }

    public String getSerializedData() {
        return oneUsageSerializedData;
    }

    ///////////////////////////////////////

    public String getOneUsageSecondSerializedData() {
        String data = secondSerializedData;
        secondSerializedData = null;
        return data;
    }

    public void setSecondSerializedData(String secondSerializedData) {
        this.secondSerializedData = secondSerializedData;
    }

    public String getSecondSerializedData() {
        return secondSerializedData;
    }

    ////////////////////////////////////////

    protected void doActionAfterIntentActionCompleted(Context context) {
    }

    public IntentActionName getRedirectIntentName() {
        return redirectIntentName;
    }
}