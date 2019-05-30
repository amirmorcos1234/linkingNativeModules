package ro.vodafone.mcare.android.application.controllers;

import android.util.Log;

import com.urbanairship.UAirship;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.LoyaltySegmentRequest;
import ro.vodafone.mcare.android.service.LoyaltyMarketService;
import ro.vodafone.mcare.android.service.NotificationService;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Serban Radulescu on 9/29/2017.
 */

public class UrbanAirshipController {

    private static final String TAG = UrbanAirshipController.class.getSimpleName();

    private static UrbanAirshipController ourInstance = new UrbanAirshipController();

    private UrbanAirshipController() {
    }

    public static UrbanAirshipController getInstance() {
        return ourInstance;
    }

    public static void getLoyaltyNotificationsInfo(String crmRole, LoyaltySegmentRequest loyaltySegmentRequest){
        LoyaltyMarketService loyaltyMarketService = new LoyaltyMarketService(VodafoneController.getInstance());
        final NotificationService notificationService = new NotificationService(VodafoneController.getInstance());

        loyaltyMarketService.getLoyaltySegmentIgnoreSession(crmRole, loyaltySegmentRequest)
                .flatMap(new Func1<GeneralResponse<LoyaltySegmentSuccess>, Observable<GeneralResponse<NotificationFlag>>>() {
                    @Override
                    public Observable<GeneralResponse<NotificationFlag>> call(GeneralResponse<LoyaltySegmentSuccess> loyaltySegmentSuccessGeneralResponse) {
                        RequestSaveRealmObserver.save(loyaltySegmentSuccessGeneralResponse);

                        if(notificationService == null) {
                            return null;
                        }

                        return notificationService.getNotificationFlag();
                    }
                }).subscribe(new RequestSessionObserver<GeneralResponse<NotificationFlag>>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                Log.d(TAG, "onCompleted getLoyaltySegment");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "onError getLoyaltySegment");
                addUrbanAirshipTags();
            }

            @Override
            public void onNext(GeneralResponse<NotificationFlag> response) {
                if(response != null && response.getTransactionStatus() == 0) {
                    Realm realm = Realm.getDefaultInstance();
                    ((AppConfiguration) RealmManager.getRealmObject(realm, AppConfiguration.class))
                            .setAllowNotifications(response.getTransactionSuccess().getNotificationsFlag());
                    ((AppConfiguration) RealmManager.getRealmObject(realm, AppConfiguration.class))
                            .setAllowVoucherNotifications(response.getTransactionSuccess().getVouchersFlag());
                    Log.d(TAG, "onNext getLoyaltySegment");
                    realm.close();
                }
                addUrbanAirshipTags();
            }
        });
    }

    public static void configureUrbanAirship(){

        if(VodafoneController.getInstance().getUser() != null) {
            if(VodafoneController.getInstance().getUser().getUserProfile() == null) {
                return;
            }
        }else{
            return;
        }

        String crmRole = "";
        LoyaltySegmentRequest loyaltySegmentRequest = null;
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated
                && EbuMigratedIdentityController.getInstance().getSelectedIdentity()!=null) {
            crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            Log.d(TAG, "crmRole: " + crmRole);

            List<Ban> banList = new ArrayList<>();
            List<String> banListString = new ArrayList<>();
            banList = UserSelectedMsisdnBanController.getInstance().getBanList();
            if(banList!=null){
                for (Ban ban: banList) {
                    banListString.add(ban.getNumber());
                }
                String[] ebuBanList = banListString.toArray(new String[banList.size()]);
                loyaltySegmentRequest = new LoyaltySegmentRequest(ebuBanList);
            } else {
                loyaltySegmentRequest = new LoyaltySegmentRequest(null);
            }
        }


        getLoyaltyNotificationsInfo(crmRole, loyaltySegmentRequest);
    }

    private static void addUrbanAirshipTags(){
        UserProfile userProfile;
        User user;

        if(VodafoneController.getInstance().getUser() != null &&
                VodafoneController.getInstance().getUser().getUserProfile() != null) {
            user = VodafoneController.getInstance().getUser();
            userProfile = VodafoneController.getInstance().getUser().getUserProfile();
        }
        else {
            Log.d(TAG, "UserProfile null, something went wrong at login");
            return;
        }

        String customerType = userProfile.getCustomerType();
        String msisdn = userProfile.getMsisdn();
        String subscriberType = userProfile.getSubscriberType();
        String userRole = userProfile.getUserRoleString();

        if(user instanceof EbuMigrated && EbuMigratedIdentityController.getInstance().getSelectedIdentity()!=null) {
            userRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
        }

        StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();
        String encryptedMsisdn;
        try {
            encryptedMsisdn = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(msisdn));

            UAirship.shared().getNamedUser().setId(encryptedMsisdn);
            UAirship.shared().getPushManager().editTags()
                    .addTag(customerType)
                    .apply();

            UAirship.shared().getPushManager().editTags()
                    .addTag(userRole)
                    .apply();

            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                subscriberType = "postpaid";
            }

            UAirship.shared().getPushManager().editTags()
                    .addTag(subscriberType)
                    .apply();

            Log.d(TAG, "Adding UA tags:");
            Log.d(TAG, "encryptedMsisdn: " + encryptedMsisdn);
            Log.d(TAG, "customerType: " + customerType);
            Log.d(TAG, "userRole: " + userRole);
            Log.d(TAG, "subscriberType: " + subscriberType);
            Log.d(TAG, "isVdfSubscription: " + checkIfEbuIsVdfSubscriptionOrCbu());

            if(checkIfEbuIsVdfSubscriptionOrCbu()) {
                addLoyaltyTags();
            }

            checkNotificationsTags();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addLoyaltyTags(){
        Realm realm = Realm.getDefaultInstance();
        LoyaltySegmentSuccess loyaltySegmentSuccess = (LoyaltySegmentSuccess) RealmManager.getRealmObject(realm, LoyaltySegmentSuccess.class);
        if(loyaltySegmentSuccess == null) {
            return;
        }
        Log.d(TAG, "add loyalty tags");

        String treatmentSegment = loyaltySegmentSuccess.getTreatmentSegment();
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            treatmentSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
        }
        String lpsSegment = loyaltySegmentSuccess.getLpsSegment();

        Log.d(TAG, "treatmentSegment: " + treatmentSegment);
        Log.d(TAG, "lpsSegment: " + lpsSegment);
        UAirship.shared().getPushManager().editTags()
                .addTag(treatmentSegment)
                .apply();
        UAirship.shared().getPushManager().editTags()
                .addTag(lpsSegment)
                .apply();
        realm.close();
    }


    private static void checkNotificationsTags(){
        Realm realm = Realm.getDefaultInstance();

        boolean allowedNotifications = false;
        boolean allowVoucherNotifications = false;
        AppConfiguration appConfiguration = ((AppConfiguration) RealmManager.getRealmObject(realm, AppConfiguration.class));
        if (appConfiguration != null) {
            allowedNotifications = appConfiguration.allowNotifications();
            allowVoucherNotifications = appConfiguration.allowVoucherNotifications();
        }

        if (allowedNotifications) {
            // Enable user notifications
            UAirship.shared().getPushManager().setUserNotificationsEnabled(true);
        } else {
            // set it disabled based on pref.
            UAirship.shared().getPushManager().setUserNotificationsEnabled(false);
        }

        if(allowVoucherNotifications){
            UAirship.shared().getPushManager()
                    .editTags()
                    .addTag("Recommandations")
                    .apply();
        }
        else {
            UAirship.shared().getPushManager()
                    .editTags()
                    .removeTag("Recommandations")
                    .apply();
        }
        realm.close();
    }

    private static boolean checkIfEbuIsVdfSubscriptionOrCbu() {
        User user = VodafoneController.getInstance().getUser();
        Realm realm = Realm.getDefaultInstance();


        if(user instanceof EbuMigrated) {
            if (((EbuMigrated) user).isAuthorisedPerson()
                    || ((EbuMigrated) user).isChooser()
                    || ((EbuMigrated) user).isDelagatedChooser()) {
                ProfileSubscriptionSuccess profileSubscriptionSuccess = (ProfileSubscriptionSuccess) RealmManager
                        .getRealmObject(realm, ProfileSubscriptionSuccess.class);

                if (profileSubscriptionSuccess != null
                        && profileSubscriptionSuccess.getIsVDFSubscription() != null
                        && profileSubscriptionSuccess.getIsVDFSubscription()) {
                    realm.close();
                    return true;
                } else {
                    realm.close();
                    return false;
                }
            }
        }
        return true;
    }
}
