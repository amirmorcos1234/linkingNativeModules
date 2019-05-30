package ro.vodafone.mcare.android.widget.avatar;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchy;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;

/**
 * Created by Victor Radulescu on 3/16/2017.
 */

public class UserSelectedMsisdnBanController {

    private static UserSelectedMsisdnBanController instance;

    private String selectedMsisdn;
    private String tvSelectedMsisdn;
    private Subscriber selectedSubscriber;
    private TvHierarchy selectedFixedNet;
    private String selectedSubscriberId;

    private String selectedNumberBan = null;
    private String selectedNumberBen = null;
    private Realm realm;

    private InterfaceUserSelectedMsisdnBan changedBanMsisdnListener;

    private UserSelectedMsisdnBanController() {
    }

    public synchronized static UserSelectedMsisdnBanController getInstance() {

        if (instance == null) {
            instance = new UserSelectedMsisdnBanController();
        }
        return instance;
    }

    public String getSelectedMsisdn() {
        if (selectedMsisdn == null) {
            Subscriber subscriber = getSelectedSubscriber();
            if(subscriber!=null){
               selectedMsisdn = subscriber.getMsisdn();
               tvSelectedMsisdn = subscriber.getMsisdn();
            }else{
                realm = Realm.getDefaultInstance();
                Profile profile = ((Profile) RealmManager.getRealmObject(realm, Profile.class));
                selectedMsisdn = profile != null ? profile.getHomeMsisdn() : selectedMsisdn;
                tvSelectedMsisdn = profile != null ? profile.getHomeMsisdn() : selectedMsisdn;;

                if (selectedMsisdn == null) {
                    UserProfile userProfile = ((UserProfile) RealmManager.getRealmObject(realm, UserProfile.class));
                    selectedMsisdn = userProfile != null ? userProfile.getMsisdn() : selectedMsisdn;
                    tvSelectedMsisdn = userProfile != null ? userProfile.getMsisdn() : selectedMsisdn;
                }
                realm.close();
            }
        }
        return selectedMsisdn;
    }
    public void updateSelectedMsisdn(String selectedMsisdn) {
        reloadDashboardIfMsisdnChanged(selectedMsisdn);
        this.selectedMsisdn = selectedMsisdn;
        tvSelectedMsisdn=selectedMsisdn;
        CostControlWidgetController.getInstance().setCurrentMsisdn(selectedMsisdn);
        updateSelectedMsisdnInUserProfile(selectedMsisdn);
    }

    private void reloadDashboardIfMsisdnChanged(String selectedMsisdn) {
        if(this.selectedMsisdn != null && !Objects.equals(selectedMsisdn, this.selectedMsisdn) && !this.selectedMsisdn.contains(selectedMsisdn) && !selectedMsisdn.contains(this.selectedMsisdn)){
            DashboardController.reloadDashboardOnResume();
        }
    }

    public void setSelectedMsisdn(String selectedMsisdn) {
        updateSelectedMsisdn(selectedMsisdn);
        selectedSubscriber = null;
        selectedNumberBan = null;
        selectedNumberBen = null;
    }

    public String getMsisdnFromUserDataProfile() {
        realm = Realm.getDefaultInstance();
        Profile profile = ((Profile) RealmManager.getRealmObject(realm, Profile.class));
        String profileMsisdn = profile != null ? profile.getHomeMsisdn() : null;
        if (profileMsisdn == null) {
            UserProfile userProfile = ((UserProfile) RealmManager.getRealmObject(realm, UserProfile.class));
            profileMsisdn = userProfile != null ? userProfile.getMsisdn() : null;
        }
        realm.close();
        return profileMsisdn;
    }

    public String getWithoutPrefix4MsisdnFromUserDataProfile() {
        realm = Realm.getDefaultInstance();
        Profile profile = ((Profile) RealmManager.getRealmObject(realm, Profile.class));
        String profileMsisdn = profile != null ? profile.getHomeMsisdn() : null;
        if (profileMsisdn == null) {
            UserProfile userProfile = ((UserProfile) RealmManager.getRealmObject(realm, UserProfile.class));
            profileMsisdn = userProfile != null ? userProfile.getMsisdn() : null;
        }
        realm.close();
        if(profileMsisdn!=null && profileMsisdn.length()>=2){
            if ( profileMsisdn.substring(0, 1).equals("4")) {
                return profileMsisdn.substring(1);
            }
        }
        return profileMsisdn;
    }

    public String getAliasFromUserDataProfile() {
        realm = Realm.getDefaultInstance();
        Profile profile = ((Profile) RealmManager.getRealmObject(realm, Profile.class));
        String profileAlias = profile != null ? profile.getAlias() : null;
        if (profileAlias == null) {
            UserProfile userProfile = ((UserProfile) RealmManager.getRealmObject(realm, UserProfile.class));
            profileAlias = userProfile != null ? userProfile.getMsisdn() : null;
        }
        realm.close();
        return profileAlias;
    }

    public void clearData() {
        instance = null;
    }

    public String getSelectedNumberBan() {
        realm = Realm.getDefaultInstance();
        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);
        if (selectedNumberBan == null) {
            if(userProfile != null)
                selectedNumberBan = userProfile.getSelectedBanNumber();

            if (selectedNumberBan == null) {
                if (EbuMigratedIdentityController.isUserVerifiedEbuMigrated()) {
                    selectedNumberBan = getSelectedEbuBan();
                }
                if (selectedNumberBan == null) {
                    UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                            .getRealmObject(realm, UserProfileHierarchy.class);
                    if (userProfileHierarchy != null && userProfileHierarchy.getBanList() != null && userProfileHierarchy.getBanList().isValid() && !userProfileHierarchy.getBanList().isEmpty()) {
                        selectedNumberBan = userProfileHierarchy.getBanList().get(0).getNumber();
                    }
                }
                updateSelectedBanNumberInUserProfile(selectedNumberBan);
            }
        }
        realm.close();
        return selectedNumberBan;
    }

    private String getSelectedEbuBan() {
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (EbuMigratedIdentityController.getInstance().isIdentityBillingCustomer()) {
            List<EntityChildItem> entityChildItems = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getChildList();
            if (entityChildItems != null && !entityChildItems.isEmpty()) {
                EntityChildItem firstChild = entityChildItems.get(0);
                return firstChild.getVfOdsBan();
            }
        } else if (entityChildItem != null) {
            return entityChildItem.getVfOdsBan();
        }
        return null;

    }

    public void setSelectedNumberBan(String selectedNumberBan) {
        setSelectedNumberBan(selectedNumberBan, getCurrentEbuBen(selectedNumberBan));
    }

    public void setSelectedNumberBan(String selectedNumberBan, String selectedNumberBen) {
        this.selectedNumberBan = selectedNumberBan;
        this.selectedNumberBen = selectedNumberBen;
        if (changedBanMsisdnListener != null)
            changedBanMsisdnListener.onBanChanged();
        updateSelectedBanNumberInUserProfile(selectedNumberBan);
    }

    public String getSelectedEbuBen() {
        this.selectedNumberBen = getCurrentEbuBen(getSelectedNumberBan());
        return selectedNumberBen;
    }


    private String getCurrentEbuBen(String selectedBanNumber) {
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (EbuMigratedIdentityController.getInstance().isIdentityBillingCustomer()) {
            RealmList<EntityChildItem> entityChildItems = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getChildList();
            if(entityChildItems != null && entityChildItems.size()>0){
                for(EntityChildItem childItem : entityChildItems){
                    if(childItem.getVfOdsBan().equals(selectedBanNumber)){
                        return childItem.getVfOdsBen();
                    }
                }
            }
        } else if (entityChildItem != null) {
            return entityChildItem.getVfOdsBen();
        }
        return null;
    }

    public Ban getSelectedBan() {
        realm = Realm.getDefaultInstance();
        if (selectedNumberBan == null) {
            selectedNumberBan = getSelectedNumberBan();
        }
        Ban banManaged = (Ban) RealmManager.getRealmObjectAfterStringField(realm, Ban.class, Ban.BAN_KEY, selectedNumberBan);
        Ban banUnManaged = (Ban) RealmManager.getUnManagedRealmObject(realm, banManaged);
        realm.close();

        return banUnManaged;
    }

    public void updateSelectedSubscriber(List<Subscriber> subscriberList) {

        for (Subscriber subscriber : subscriberList) {
            if (subscriber.getMsisdn().equals(selectedSubscriber.getMsisdn())
                    || subscriber.getMsisdn().substring(1).equals(selectedSubscriber.getMsisdn())
                    || subscriber.getMsisdn().equals(selectedSubscriber.getMsisdn().substring(1)))
                selectedSubscriber = subscriber;
            break;
        }

    }
    public boolean haveMultipleBans(){
        List<Ban> bans = getBanList();
        return bans!=null && bans.size()>1;
    }

    public List<Ban> getBanList() {
        realm = Realm.getDefaultInstance();
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                .getUnManagedRealmObject(realm, UserProfileHierarchy.class);
        if (userProfileHierarchy != null && userProfileHierarchy.getBanList() != null
                && !userProfileHierarchy.getBanList().isEmpty() && userProfileHierarchy.getBanList().isValid()) {
            return userProfileHierarchy.getBanList();
        } else if (EbuMigratedIdentityController.isUserVerifiedEbuMigrated()) {
            return getEbuMigratedBanList();
        }
        return null;
    }




    private List<Ban> getEbuMigratedBanList() {
        EntityChildItem selectedIdentity = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        List<Ban> ebuBanList = new ArrayList<>();
        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            if (((EbuMigrated) VodafoneController.getInstance().getUser()).isSubscriber()) {
                ebuBanList.add(getBanFromEntityChild(selectedIdentity));
            } else if (entityHaveBan(selectedIdentity) && !entityHaveChildrens(selectedIdentity)) {
                ebuBanList.add(getBanFromEntityChild(selectedIdentity));
            } else if (selectedIdentity.getChildList() != null) {
                ebuBanList = getBansFromEntityChildList(selectedIdentity.getChildList());
            } else {
                return null;
            }
        }
        return ebuBanList;
    }

    private List<Ban> getBansFromEntityChildList(RealmList<EntityChildItem> childItems) {
        List<Ban> ebuBanList = new ArrayList<>();
        for (EntityChildItem eci : childItems) {
            ebuBanList.add(getBanFromEntityChild(eci));
        }
        return ebuBanList;
    }

    private Ban getBanFromEntityChild(EntityChildItem eci) {
        Ban b = new Ban();
        b.setNumber(eci.getVfOdsBan());
        b.setSubscriberList(null);
        return b;
    }

    private boolean entityHaveBan(EntityChildItem eci) {
        return eci.getVfOdsBan() != null;
    }

    private boolean entityHaveChildrens(EntityChildItem eci) {
        return eci.getChildList() != null && !eci.getChildList().isEmpty();
    }

    //TODO delete/comment the Crashlytics logs
    public synchronized Subscriber getSelectedSubscriber() {
        //check if it is a selectedSubscriber

        if (selectedSubscriber == null || !selectedSubscriber.isValid()) {
            realm = Realm.getDefaultInstance();
            UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);

            if (userProfile != null && userProfile.getSelectedMsisdnNumber() != null) {
                String msisdn = userProfile.getSelectedMsisdnNumber().startsWith("4")
                        ? userProfile.getSelectedMsisdnNumber().substring(1)
                        : userProfile.getSelectedMsisdnNumber();
                Subscriber selectedSubscriberManaged =  (Subscriber) RealmManager
                        .getRealmObjectContainsStringField(realm, Subscriber.class, Subscriber.MSISDN_KEY, msisdn);
                selectedSubscriber = (Subscriber) RealmManager
                        .getUnManagedRealmObject(realm, selectedSubscriberManaged);
                if (selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                    Throwable throwable = new Throwable("First If from all Subscribers - vfSid is null");
                    Crashlytics.logException(throwable);
                }
            }

            if (selectedSubscriber == null) {
                User user = VodafoneController.getInstance().getUser();
                if (user instanceof PrepaidUser || user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess) {
                    selectedSubscriber = getSubscriberFromProfile();
                    if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                        Throwable throwable = new Throwable("Second If from getSubscriberFromProfile() - vfSid is null");
                        Crashlytics.logException(throwable);
                    }
//            } else if (user instanceof EbuMigrated && ((EbuMigrated) user).isEntityVerifyed()) {
                } else if (user instanceof EbuMigrated) {
                    selectedSubscriber = getSelectedEbuMigratedSubscriber();
                    if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                        Throwable throwable = new Throwable("Third If from getSelectedEbuMigratedSubscriber() - vfSid is null");
                        Crashlytics.logException(throwable);
                    }
                } else if (user instanceof PostPaidUser) {
                    // get subscriber from UserProfileHierarchy
                    UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                            .getRealmObject(realm, UserProfileHierarchy.class);
                    if (userProfileHierarchy != null) {
                        //check if it is set a selectedMsisdn
                        String msisdnFromUserDataProfile = getMsisdnFromUserDataProfile();
                        if (msisdnFromUserDataProfile == null || msisdnFromUserDataProfile.isEmpty()) {
                            //posible scenario for first attempt to get selected subscriber
                            //selectedMsisdn = null
                            Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);

                            //homeMsisdn as selectedMsisdn
                            if (profile != null) {
                                updateSelectedMsisdn(profile.getHomeMsisdn());
                            }
                        }
                        //check for correct format and return from realm
                        String msisdn = msisdnFromUserDataProfile.startsWith("4") ? msisdnFromUserDataProfile.substring(1) : msisdnFromUserDataProfile;
                        Subscriber selectedSubscriberManaged =  (Subscriber) RealmManager
                                .getRealmObjectContainsStringField(realm, Subscriber.class, Subscriber.MSISDN_KEY, msisdn);
                        selectedSubscriber = (Subscriber) RealmManager
                                .getUnManagedRealmObject(realm, selectedSubscriberManaged);

                        if (selectedSubscriber == null)
                            selectedSubscriber = getSubscriberFromProfile();

                        if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                            Throwable throwable = new Throwable("Fourth If second attempt from all Subscriber classes - vfSid is null");
                            Crashlytics.logException(throwable);
                        }

                    } else if (!(VodafoneController.getInstance().getUser() instanceof SubUserNonMigrated)) {
                        selectedSubscriber = getSubscriberFromProfile();
                        if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                            Throwable throwable = new Throwable("Fifth If from getSubscriberFromProfile() - vfSid is null");
                            Crashlytics.logException(throwable);
                        }
                    } else {
                        selectedSubscriber = getSubscriberFromProfile();
                        if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                            Throwable throwable = new Throwable("Sixth If from getSubscriberFromProfile() - vfSid is null");
                            Crashlytics.logException(throwable);
                        }
                    }

                } else {
                    selectedSubscriber = getSubscriberFromProfile();
                    if(selectedSubscriber == null || selectedSubscriber.getSid() == null) {
                        Throwable throwable = new Throwable("Seventh If from getSubscriberFromProfile() - vfSid is null");
                        Crashlytics.logException(throwable);
                    }
                }
            }
        }
        realm.close();
        return selectedSubscriber;
    }

    public synchronized void setSelectedSubscriber(Subscriber selectedSubscriber) {
        this.selectedSubscriber = selectedSubscriber;
        this.tvSelectedMsisdn = selectedSubscriber.getMsisdn();
        updateSelectedMsisdn(selectedSubscriber.getMsisdn());
        if (changedBanMsisdnListener != null)
            changedBanMsisdnListener.onSubscriberChanged();
    }

    private synchronized Subscriber getSelectedEbuMigratedSubscriber() {
        Subscriber ebuSubscriber = null;
        User user = VodafoneController.getInstance().getUser();
        EbuMigrated ebuMigrated;
        if (user instanceof EbuMigrated) {
            ebuMigrated = (EbuMigrated) user;
        } else {
            return getSubscriberFromProfile();
        }
        if (ebuMigrated.isEntityVerifyed()) {
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            if (entityChildItem != null) {
                if (ebuMigrated.isSubscriber()) {
                    ebuSubscriber = getSubscriberFromCurrentEntityChildItem();
                } else {
                    ebuSubscriber = getFirstSubscriberFromSubscriberHierarchyBan();
                }
            }
        }
        if (ebuSubscriber != null) {
            updateSelectedMsisdn(ebuSubscriber.getMsisdn());
        }
        ebuSubscriber = ebuSubscriber == null ? getSubscriberFromProfile() : ebuSubscriber;
        return ebuSubscriber;
    }

    private synchronized Subscriber getSubscriberFromCurrentEntityChildItem() {
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        Subscriber subscriberFromProfile = getSubscriberFromProfile();
        Subscriber entitySubscriber = new Subscriber();
        if (entityChildItem != null) {
            //   entityChildItem.get
            if(subscriberFromProfile!=null){
                entitySubscriber.setAvatarUrl(subscriberFromProfile.getAvatarUrl());
                entitySubscriber.setResourceType(subscriberFromProfile.getResourceType());
            }
            entitySubscriber.setMsisdn(entityChildItem.getVfOdsPhoneNumber());
            entitySubscriber.setSid(entityChildItem.getVfOdsSid());
        }
        return entitySubscriber;
    }

    private synchronized Subscriber getFirstSubscriberFromSubscriberHierarchyBan() {
        realm = Realm.getDefaultInstance();

        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                .getRealmObject(realm, UserProfileHierarchy.class);
        if (userProfileHierarchy != null) {
            Ban subscriberHierarchyBan = userProfileHierarchy.getBan();
            if (subscriberHierarchyBan != null && subscriberHierarchyBan.getSubscriberList() != null && !subscriberHierarchyBan.getSubscriberList().isEmpty()) {
                Subscriber unManagedSubscriber = (Subscriber) RealmManager
                        .getUnManagedRealmObject(realm, subscriberHierarchyBan.getSubscriberList().get(0));
                realm.close();
                return unManagedSubscriber;
            }
        }
        realm.close();
        return null;
    }

    /*
     * Creates subscriber from Profile
     */
    public synchronized Subscriber getSubscriberFromProfile() {//todo okso cr: long ifs, nested, try/catch with no utility? - what error ?? -
        String alias = null;
        String avatarUrl = null;
        realm = Realm.getDefaultInstance();
        Profile profile = (Profile) RealmManager.getUnManagedRealmObject(realm, Profile.class);
        UserProfile userProfile = (UserProfile) RealmManager.getUnManagedRealmObject(realm, UserProfile.class);
        realm.close();
        //get from profile
        if (profile != null && profile.isValid()) {
            try {
                if (profile.getAlias() != null && !profile.getAlias().equals(""))
                    alias = profile.getAlias();

                if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().equals(""))
                    avatarUrl = profile.getAvatarUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String resourceType = getUserResourceType();
            String sid = userProfile.getSid();

            return new Subscriber(avatarUrl, alias, profile.getHomeMsisdn(),
                    sid, resourceType);
        } else {
            //get from UserProfile
            if(VodafoneController.getInstance().getUserProfile() != null) {
                Subscriber subscriber = new Subscriber(avatarUrl, alias,
                        VodafoneController.getInstance().getUserProfile().getMsisdn(),
                        VodafoneController.getInstance().getUserProfile().getSid(), null);
                return subscriber;
            }else{
                return null;
            }
        }
    }

    private String getUserResourceType() {
        String resourceType = null;
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (entityChildItem != null && VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            resourceType = entityChildItem.getVfOdsResourceType();
        }
        return resourceType;
    }

    public int getNumberOfSubscribers() {
        realm = Realm.getDefaultInstance();
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                .getUnManagedRealmObject(realm, UserProfileHierarchy.class);
        User user = VodafoneController.getInstance().getUser();

        if (userProfileHierarchy != null && userProfileHierarchy.isValid() && userProfileHierarchy.getBan() != null
                && userProfileHierarchy.getBan().getSubscriberList() != null) {
            Log.d("", "getNumberOfSubscribers:subsList " + userProfileHierarchy.getBan().getSubscriberList().size());
            if (user instanceof EbuMigrated && isEbuSubscriberListAvailable()) {
                return (userProfileHierarchy.getBan().getSubscriberList().size());
            } else if (!(user instanceof EbuMigrated)) {
                return userProfileHierarchy.getSubscriberList().size();

            }
        }
        return getSelectedSubscriber() != null ? 1 : 0;
    }

    public InterfaceUserSelectedMsisdnBan getChangedBanMsisdnListener() {
        return changedBanMsisdnListener;
    }

    public void setChangedBanMsisdnListener(InterfaceUserSelectedMsisdnBan changedBanMsisdnListener) {
        this.changedBanMsisdnListener = changedBanMsisdnListener;
    }

    public String getSubscriberSid() {
        if(getSelectedSubscriber() == null) {
            return null;
        }
        return getSelectedSubscriber().getSid();
    }

    public synchronized String getSubscriberAlias() {
        Subscriber subscriber = getSelectedSubscriber();
        return subscriber!= null? subscriber.getAlias():null;
    }

    public String getSubscriberAvatarUrl() {
        return getSelectedSubscriber().getAvatarUrl();
    }

    public String createAliasFromMsisdn() {
        Subscriber subscriber = getSelectedSubscriber();
        if (subscriber == null)
            return null;
        else
            return PhoneNumberUtils.checkNumberMsisdnFormat(subscriber.getMsisdn());
    }

    public List<Subscriber> getSubscriberList() {
        //TODO: fix bad use of realm
        realm = Realm.getDefaultInstance();
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                .getUnManagedRealmObject(realm, UserProfileHierarchy.class);
        realm.close();
        User user = VodafoneController.getInstance().getUser();
        if (userProfileHierarchy != null && userProfileHierarchy.isValid())
            if (!(user instanceof EbuMigrated)) {
                return userProfileHierarchy.getSubscriberList();
            } else {
                if (!((EbuMigrated) user).isSubscriber()) {
                    if (isEbuSubscriberListAvailable()) {
                        return userProfileHierarchy.getBan().getSubscriberList();
                    }
                }
            }
        return null;
    }

    public List<Subscriber> getSortedSubscriberList() {
        if (getSubscriberList() != null && !getSubscriberList().isEmpty()) {
            List<Subscriber> storesList = getSubscriberList();
            Collections.sort(storesList, SelectorDialogActivity.getSubcriberComparator());
            return storesList;
        }

        return null;
    }

    private boolean isEbuSubscriberListAvailable() {
        realm = Realm.getDefaultInstance();
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager
                .getRealmObject(realm, UserProfileHierarchy.class);
        Ban subscriberHierarchyBan = userProfileHierarchy.getBan();
        realm.close();
        return subscriberHierarchyBan != null && subscriberHierarchyBan.getSubscriberList() != null && !subscriberHierarchyBan.getSubscriberList().isEmpty();
    }

    public void resetSelectSubscriberAndBan() {
        selectedSubscriber = null;
        selectedNumberBan = null;
        selectedMsisdn = null;
        tvSelectedMsisdn = null;
        updateSelectedBanNumberInUserProfile(null);
        updateSelectedMsisdnInUserProfile(null);

    }

    private void updateSelectedMsisdnInUserProfile(String selectedMsisdn){
        realm = Realm.getDefaultInstance();
        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);
        if(userProfile != null)
            userProfile.setSelectedMsisdnNumber(selectedMsisdn);
        realm.close();
    }

    private void updateSelectedBanNumberInUserProfile(String selectedNumberBan){
        realm = Realm.getDefaultInstance();
        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);
        if(userProfile != null)
            userProfile.setSelectedBanNumber(selectedNumberBan);
        realm.close();
    }

    public TvHierarchy getSelectedFixedNet() {
        return selectedFixedNet;
    }

    public boolean tvServiceSelector(){
        return selectedFixedNet != null;
    }

    public synchronized void  setSelectedFixedNet(TvHierarchy selectedFixedNet) {
        this.selectedFixedNet = selectedFixedNet;
        if(selectedFixedNet != null)
         selectedSubscriberId = selectedFixedNet.getSubscriberID();
        if (changedBanMsisdnListener != null)
            changedBanMsisdnListener.onSubscriberChanged();
    }

    public String getSelectedSubscriberId() {
        return selectedSubscriberId;
    }

    public void setSelectedSubscriberId(String selectedSubscriberId) {
        this.selectedSubscriberId = selectedSubscriberId;
    }



    public String getTvSelectedMsisdn() {
        return tvSelectedMsisdn;
    }

    public void setTvSelectedMsisdn(String tvSelectedMsisdn) {
        this.tvSelectedMsisdn = tvSelectedMsisdn;
    }

    public Subscriber getSelectedSubscriber(String msisdn) {
        Realm realm = Realm.getDefaultInstance();
        Subscriber selectedSubscriberManaged =  (Subscriber) RealmManager
                .getRealmObjectContainsStringField(realm, Subscriber.class, Subscriber.MSISDN_KEY, msisdn);
        Subscriber selectedSubscriber = (Subscriber) RealmManager
                .getUnManagedRealmObject(realm, selectedSubscriberManaged);
        return selectedSubscriber;
    }

    public boolean getIfFixedSelected() {
        return tvServiceSelector() && selectedFixedNet.getServiceType().toLowerCase().equals("fixed");
    }
}
