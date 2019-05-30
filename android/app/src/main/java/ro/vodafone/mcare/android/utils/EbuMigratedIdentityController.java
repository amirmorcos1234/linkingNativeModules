package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.identity.BenSuccessEbu;
import ro.vodafone.mcare.android.client.model.identity.CurrentIdentity;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.UserIdentitiesService;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Serban Radulescu on 7/25/2017.
 */
//TODO - save current identity- keep it separated in realm. get Bans and Msisdn from current identity. Update after an identity is selected.
public class EbuMigratedIdentityController {

    private final static String TAG = EbuMigratedIdentityController.class.getCanonicalName();
    private static EbuMigratedIdentityController instance = new EbuMigratedIdentityController();
    private final String ENTITY_CHILD_ITEM_KEY = "entityId";
    String singleIdentityId;
    private String currentIdentityId;
    private String currentIdentityType;

    private Realm realm;

    private Action1<Throwable> toNonVodafoneDashboardErrorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            D.d("AutoLoginObservable onError " + throwable.getMessage());
            // new CustomToast.Builder(VodafoneController.getInstance()).message("Sesiunea a expirat").success(false).show();
            //Toast.makeText(VodafoneController.getInstance(), "Sesiunea a expirat", Toast.LENGTH_SHORT).show();
        }
    };
    private Func1 funcInverseHierarchyToCustomerRestriction = new Func1<GeneralResponse<EntityChildItem>, Observable<GeneralResponse>>() {

        @Override
        public Observable<GeneralResponse> call(GeneralResponse<EntityChildItem> generalResponse) {
            if (ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
                RequestSaveRealmObserver.save(generalResponse);
                updateCurrentEntityChildItem(generalResponse.getTransactionSuccess());
                return getFraudCheckObservable(generalResponse.getTransactionSuccess());
            }
            return getObservableNonVodafoneError();
        }
    };

    private Func1 funcCustomerRestrictionToEbuAuthorisedPersonBen = new Func1<GeneralResponse, Observable<GeneralResponse>>() {

        @Override
        public Observable<GeneralResponse> call(GeneralResponse generalResponse) {
            if (ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
                RequestSaveRealmObserver.save(generalResponse);
                String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
                if (("AuthorizedPerson").equalsIgnoreCase(crmRole)) {
                    return getEbuAuthorisedPersonBenObservable();
                }else{
                    return getUserProfileHierarchyIfNeeded();
                }
            }
            return getObservableNonVodafoneError();
        }
    };

    private Func1 funcEbuAuthorisedPersonToProfileHierarchy = new Func1<GeneralResponse<BenSuccessEbu>, Observable<GeneralResponse<BenSuccessEbu>>>() {

        @Override
        public Observable<GeneralResponse<BenSuccessEbu>> call(GeneralResponse<BenSuccessEbu> generalResponse) {
            User user = VodafoneController.getInstance().getUser();
            String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            if (!("AuthorizedPerson").equalsIgnoreCase(crmRole)) {
                return getUserProfileHierarchyIfNeeded();
            }

            if(generalResponse != null && generalResponse.getTransactionStatus() == 0 && generalResponse.getTransactionSuccess() != null &&
                    generalResponse.getTransactionSuccess().getVfOdsBen() != null) {
                String vfOdsBen = generalResponse.getTransactionSuccess().getVfOdsBen();
                Realm realm = Realm.getDefaultInstance();
                EntityChildItem selectedEntity = getSelectedIdentity();
                selectedEntity.setVfOdsBen(vfOdsBen);
                RealmManager.update(realm, selectedEntity);
                realm.close();

                if (user != null && user instanceof EbuMigrated) {
                    if (((EbuMigrated) user).isSubscriber()) {
                        return Observable.just(generalResponse);
                    }else{
                        return getUserProfileHierarchyIfNeeded();
                    }
                }
            }
            return getObservableNonVodafoneError();
        }
    };

    private Func1 funcCustomerRestrictionToProfileHierarchy = new Func1<GeneralResponse, Observable<GeneralResponse>>() {

        @Override
        public Observable<GeneralResponse> call(GeneralResponse generalResponse) {
            if (ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
                RequestSaveRealmObserver.save(generalResponse);
                User user = VodafoneController.getInstance().getUser();
                if (user != null && user instanceof EbuMigrated) {
                    if (((EbuMigrated) user).isSubscriber()) {
                        return Observable.just(generalResponse);
                    } else {
                        return getUserProfileHierarchyIfNeeded();
                    }
                }
            }
            return getObservableNonVodafoneError();
        }
    };

    public void updateCurrentEntityChildItem(EntityChildItem entityChildItem){
        realm = Realm.getDefaultInstance();
        EntityChildItem currentEntityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        String vfOdsBan = currentEntityChildItem.getVfOdsBan()!=null?currentEntityChildItem.getVfOdsBan():entityChildItem.getVfOdsBan();
        String vfodsCid = currentEntityChildItem.getVfOdsCid()!=null?currentEntityChildItem.getVfOdsCid():entityChildItem.getVfOdsCid();
        String treatmentSegment = currentEntityChildItem.getTreatmentSegment()!=null?currentEntityChildItem.getTreatmentSegment():entityChildItem.getTreatmentSegment();
        String vfOdsBen = currentEntityChildItem.getVfOdsBen() != null ? currentEntityChildItem.getVfOdsBen() : entityChildItem.getVfOdsBen();
        RealmManager.startTransaction();
        currentEntityChildItem.setVfOdsBan(vfOdsBan);
        currentEntityChildItem.setVfOdsCid(vfodsCid);
        currentEntityChildItem.setTreatmentSegment(treatmentSegment);
        currentEntityChildItem.setVfOdsBen(vfOdsBen);
        RealmManager.update(realm, currentEntityChildItem);

        realm.close();
    }

    public EbuMigratedIdentityController() {

    }

    public static EbuMigratedIdentityController getInstance() {
        return instance;
    }

    public void saveToRealmIdentity(String selectedEntityId) {
        realm = Realm.getDefaultInstance();

        RealmManager.startTransaction();

        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);


        EntityChildItem entityChildItem = (EntityChildItem) RealmManager.getRealmObjectAfterStringField(realm, EntityChildItem.class, ENTITY_CHILD_ITEM_KEY, selectedEntityId);
        Log.d(TAG, entityChildItem.getCrmRole() + " " + entityChildItem.getEntityId());

        //get the parent ids for the selected identity
        RealmResults<EntityChildItem> realmResults = RealmManager.getAllRealmObject(realm, EntityChildItem.class);

        EntityChildItem billingCustomerItem = null;
        EntityChildItem accountCuiItem = null;
        EntityChildItem accountHoldingItem = null;

        String selectedBillingCustomerParentId = null;
        String selectedAccountCuiParentId = null;
        String selectedAccountHoldingParentId = null;

        for (int k = 0; k < realmResults.size(); k++) {
            if (realmResults.get(k).getChildList().contains(entityChildItem)) {
                if (entityChildItem.getEntityType().equals("FinancialAccount")) {
                    selectedBillingCustomerParentId = realmResults.get(k).getEntityId();
                    billingCustomerItem = realmResults.get(k);
                }
            }
        }

        for (int k = 0; k < realmResults.size(); k++) {
            if (billingCustomerItem != null) {
                if (realmResults.get(k).getChildList().contains(billingCustomerItem)) {
                    selectedAccountCuiParentId = realmResults.get(k).getEntityId();
                    accountCuiItem = realmResults.get(k);
                }
            }
        }

        for (int k = 0; k < realmResults.size(); k++) {
            if (accountCuiItem != null) {
                if (realmResults.get(k).getChildList().contains(accountCuiItem)) {
                    selectedAccountHoldingParentId = realmResults.get(k).getEntityId();
                    accountHoldingItem = realmResults.get(k);
                }
            }
        }


        Log.d(TAG, "Saving identity: " + entityChildItem.getEntityId());
        Log.d(TAG, "Saving billing parent: " + selectedBillingCustomerParentId);
        Log.d(TAG, "Saving AccountCui parent: " + selectedAccountCuiParentId);
        Log.d(TAG, "Saving AccountHolding parent: " + selectedAccountHoldingParentId);

        CurrentIdentity currentIdentity = new CurrentIdentity();

        currentIdentity.setEntityId(entityChildItem.getEntityId());
        currentIdentity.setEntityType(entityChildItem.getEntityType());
        currentIdentity.setBillingCustomerParentId(selectedBillingCustomerParentId);
        currentIdentity.setAccountCuiParentId(selectedAccountCuiParentId);
        currentIdentity.setAccountHoldingId(selectedAccountHoldingParentId);

        RealmManager.update(realm, currentIdentity);

        VodafoneController.getInstance().setUser(userProfile, false);
        realm.close();
    }

    public EntityChildItem getSelectedIdentity() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getRealmObject(realm, CurrentIdentity.class);

        if (currentIdentity != null) {
            currentIdentityId = currentIdentity.getEntityId();
            EntityChildItem entityChildItemRealm = (EntityChildItem) RealmManager
                    .getRealmObjectAfterStringField(realm, EntityChildItem.class, ENTITY_CHILD_ITEM_KEY, currentIdentityId);
            EntityChildItem entityChildItem = (EntityChildItem) RealmManager.getUnManagedRealmObject(realm, entityChildItemRealm);
            realm.close();
            return entityChildItem;
        } else {
            realm.close();
            return null;
        }
    }

    public boolean isIdentitySubscriber() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity != null) {
            currentIdentityType = currentIdentity.getEntityType();
            String entityType = VodafoneController.getInstance().getApplicationContext().getString(R.string.ebu_migrated_entity_subscriber);
            if (currentIdentityType.equals(entityType)) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }

}
    public boolean isIdentityFinancialAccount() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if(currentIdentity != null) {
            currentIdentityType = currentIdentity.getEntityType();
            String entityType = VodafoneController.getInstance().getApplicationContext().getString(R.string.ebu_migrated_entity_financial_account);
            if (currentIdentityType.equals(entityType)) {
                return true;
            } else {
                return false;
            }
        }else{
            return  false;
        }
    }

    public boolean isIdentityBillingCustomer() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if(currentIdentity==null){
            return false;
        }
        currentIdentityType = currentIdentity.getEntityType();
        String entityType = VodafoneController.getInstance().getApplicationContext().getString(R.string.ebu_migrated_entity_billing_customer);

        if (currentIdentityType.equals(entityType)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIdentityAccountHolding() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        currentIdentityType = currentIdentity.getEntityType();
        String entityType = VodafoneController.getInstance().getApplicationContext().getString(R.string.ebu_migrated_entity_account_holding);

        if (currentIdentityType.equals(entityType)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIdentityAccountCui() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        currentIdentityType = currentIdentity.getEntityType();
        String entityType = VodafoneController.getInstance().getApplicationContext().getString(R.string.ebu_migrated_entity_account_cui);

        if (currentIdentityType.equals(entityType)) {
            return true;
        } else {
            return false;
        }
    }

    public String getBillingCustomerParentId() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getBillingCustomerParentId() != null) {
            return currentIdentity.getBillingCustomerParentId();
        } else {
            return null;
        }
    }

    public String getAccountCuiParentId() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getAccountCuiParentId() != null) {
            return currentIdentity.getAccountCuiParentId();
        } else {
            return null;
        }
    }

    public String getAccountHoldingParentId() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getAccountHoldingId() != null) {
            return currentIdentity.getAccountHoldingId();
        } else {
            return null;
        }
    }

    public EntityChildItem getBillingCustomerParentItem() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getBillingCustomerParentId() != null) {
            EntityChildItem entityChildItem = (EntityChildItem) RealmManager.getRealmObjectAfterStringField(EntityChildItem.class,
                    ENTITY_CHILD_ITEM_KEY, currentIdentity.getBillingCustomerParentId());
            return entityChildItem;
        } else {
            return null;
        }
    }

    public EntityChildItem getAccountCuiParentItem() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getAccountCuiParentId() != null) {
            EntityChildItem entityChildItem = (EntityChildItem) RealmManager.getRealmObjectAfterStringField(EntityChildItem.class,
                    ENTITY_CHILD_ITEM_KEY, currentIdentity.getAccountCuiParentId());
            return entityChildItem;
        } else {
            return null;
        }
    }

    public EntityChildItem getAccountHoldingParentItem() {
        realm = Realm.getDefaultInstance();
        CurrentIdentity currentIdentity = (CurrentIdentity) RealmManager.getUnManagedRealmObject(realm, CurrentIdentity.class);
        realm.close();

        if (currentIdentity.getAccountHoldingId() != null) {
            EntityChildItem entityChildItem = (EntityChildItem) RealmManager.getRealmObjectAfterStringField(EntityChildItem.class,
                    ENTITY_CHILD_ITEM_KEY, currentIdentity.getAccountHoldingId());
            return entityChildItem;
        } else {
            return null;
        }
    }

    public boolean haveRootEntityFinancialAccount() {
        realm = Realm.getDefaultInstance();
        UserEntitiesSuccess userEntitiesSuccess = (UserEntitiesSuccess) RealmManager.getRealmObject(realm, UserEntitiesSuccess.class);
        List<EntityChildItem> financialAccountsRoots = userEntitiesSuccess.getEntitiesList().where().equalTo(EntityChildItem.ENTITY_TYPE_KEY, "FinancialAccount").findAll();
        realm.close();

        return financialAccountsRoots != null && !financialAccountsRoots.isEmpty();
    }

    public boolean haveMultipleRootEntities() {
        realm = Realm.getDefaultInstance();

        UserEntitiesSuccess userEntitiesSuccess = (UserEntitiesSuccess) RealmManager
                .getRealmObject(realm, UserEntitiesSuccess.class);
        String[] entityTypesCountedAsIdentities = {"FinancialAccount", "Subscriber", "BillingCustomer"};
        if(userEntitiesSuccess != null){
            List<EntityChildItem> financialAccountsRoots = userEntitiesSuccess.getEntitiesList().where().in(EntityChildItem.ENTITY_TYPE_KEY,
                    entityTypesCountedAsIdentities)
                    .findAll();
            realm.close();
            return financialAccountsRoots != null && financialAccountsRoots.size()>1;
        }
        return false;
    }

    public EntityChildItem getRootEntityIfIsSingle() {
        realm = Realm.getDefaultInstance();

        UserEntitiesSuccess userEntitiesSuccess = (UserEntitiesSuccess) RealmManager
                .getRealmObject(realm, UserEntitiesSuccess.class);

        String[] entityTypesCountedAsIdentities = {"FinancialAccount", "Subscriber", "BillingCustomer"};
        if(userEntitiesSuccess != null){
            List<EntityChildItem> financialAccountsRoots = userEntitiesSuccess.getEntitiesList().where().in(EntityChildItem.ENTITY_TYPE_KEY,
                    entityTypesCountedAsIdentities)
                    .findAll();


            EntityChildItem entityChildItem = financialAccountsRoots != null && financialAccountsRoots.size() == 1 && userEntitiesSuccess.getEntitiesList().size() == 1
                    ? financialAccountsRoots.get(0)
                    : null;
            realm.close();
            return entityChildItem;
        }
        return null;
    }

    public boolean isSingleIdentity() {
        realm = Realm.getDefaultInstance();

        RealmResults<EntityChildItem> realmResults = RealmManager.getAllRealmObject(realm, EntityChildItem.class);
        List<EntityChildItem> realmResultsUnmanaged = realm.copyFromRealm(realmResults);

        realm.close();

        int counter = 0;
        if (haveMultipleRootEntities()) {
            return false;
        }

        for (int j = 0; j < realmResultsUnmanaged.size(); j++) {
            Log.d(TAG, "realmResultsUnamanaged: id:  " + realmResultsUnmanaged.get(j).getEntityId());
            String type = realmResultsUnmanaged.get(j).getEntityType();
            if (("Subscriber".equals(type) || "BillingCustomer".equals(type))) {
                counter++;
            }
            if (counter > 1) {
                return false;
            }
        }

        return counter <= 1;
    }

    //TODO not finished - is for dashboard refresh - auto login
    public Observable<GeneralResponse> reloadIdentityCalls() {

        EntityChildItem selectedEntity = getSelectedIdentity();
        Context context = VodafoneController.currentActivity();
        if (context == null) {
            return null;
        }
        Observable<GeneralResponse> requestsObservale;
        if (!(isIdentityBillingCustomer() ||
                isIdentityFinancialAccount() ||
                isIdentitySubscriber())) {
            requestsObservale = getFraudCheckObservable(selectedEntity).flatMap(funcCustomerRestrictionToProfileHierarchy);
        } else {
            requestsObservale = getInverseHierarchyObservable()
                    .flatMap(funcInverseHierarchyToCustomerRestriction)
                    .flatMap(funcCustomerRestrictionToEbuAuthorisedPersonBen)
                    .flatMap(funcEbuAuthorisedPersonToProfileHierarchy);
        }
        return requestsObservale;
    }

    private Observable getInverseHierarchyObservable() {
        EntityChildItem selectedEntity = getSelectedIdentity();
        Context context = VodafoneController.currentActivity();
        Context dashboardContext = VodafoneController.getInstance().getDashboardActivity();
        context= context==null? dashboardContext:context;
        if (context == null) {
            return getObservableNonVodafoneError();
        }

        if (selectedEntity == null) {
            //TODO error case
            return getObservableNonVodafoneError();
        }
        String entityId = selectedEntity.getEntityId();
        String entityType = selectedEntity.getEntityType();
        String vfOdsPhoneNumber = EbuMigratedIdentityController.getInstance().isIdentitySubscriber() ? selectedEntity.getVfOdsPhoneNumber():null;

        return new UserIdentitiesService(context).getInverseHierarchy(entityId, entityType,vfOdsPhoneNumber)
                .doOnError(toNonVodafoneDashboardErrorAction);

    }

    private Observable getObservableNonVodafoneError() {
        return Observable.error(new Throwable("Non Dashboard"));
    }

    private Observable getFraudCheckObservable(final EntityChildItem entityChildItem) {
        Context context = VodafoneController.currentActivity();
        Context dashboardContext = VodafoneController.getInstance().getDashboardActivity();
        context= context==null? dashboardContext:context;
        if (context == null) {
            return getObservableNonVodafoneError();
        }
        EntityChildItem selectedEntity = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (selectedEntity == null || entityChildItem == null) {
            return getObservableNonVodafoneError();
        }
        String vfOdsSsn = entityChildItem.getVfOdsSsn();
        D.w("Get fraud check ");
        return new UserIdentitiesService(context).getIdentityCostumerRestriction(vfOdsSsn)
                .doOnError(toNonVodafoneDashboardErrorAction);
    }

    private Observable getEbuAuthorisedPersonBenObservable() {
        Context context = VodafoneController.currentActivity();
        Context dashboardContext = VodafoneController.getInstance().getDashboardActivity();
        context = context==null? dashboardContext:context;

        if (context == null) {
            return getObservableNonVodafoneError();
        }

        EntityChildItem selectedEntity = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (selectedEntity == null) {
            return getObservableNonVodafoneError();
        }

        String vfOdsBan = selectedEntity.getVfOdsBan();
        D.w("Get ebu authorised person ben ");
        return new UserIdentitiesService(context).getEbuBen(vfOdsBan)
                .doOnError(toNonVodafoneDashboardErrorAction);
    }

    private Observable getUserProfileHierarchyIfNeeded() {
        Context context = VodafoneController.currentActivity();
        Context dashboardContext = VodafoneController.getInstance().getDashboardActivity();
        context= context==null? dashboardContext:context;
        if (context == null) {
            return getObservableNonVodafoneError();
        }
        User user = VodafoneController.getInstance().getUser();
        String vfOdsBan = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsBan();

        if (user instanceof EbuMigrated) {

            if (EbuMigratedIdentityController.getInstance().isIdentityBillingCustomer()) {
                List<EntityChildItem> entityChildItems = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getChildList();
                if (entityChildItems != null && !entityChildItems.isEmpty()) {
                    if(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null
                            && !UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan().isEmpty()){
                        vfOdsBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
                    } else  {
                        EntityChildItem firstChild = entityChildItems.get(0);
                        vfOdsBan = firstChild.getVfOdsBan();
                    }
                }
            }
            return new UserDataService(context).reloadSubscriberHierarchy(vfOdsBan).doOnError(toNonVodafoneDashboardErrorAction);
        }
        return getObservableNonVodafoneError();//TODO verify if is correct - posible bad flow
    }


    public void cleanEntityData() {
        realm = Realm.getDefaultInstance();
        Class[] identityClassesToBeRemoved = {EntityDetailsSuccess.class};
        RealmManager.deleteMultiple(realm, identityClassesToBeRemoved);
        realm.close();
    }

    public String getHasOneIdentity() {// == nul => got more
        return singleIdentityId;
    }

    public void setHasOneIdentity(String singleIdentityId) {
        this.singleIdentityId = singleIdentityId;
    }
    public static void setEbuMigratedUserFailedCase(){
        VodafoneController.getInstance().setUser(new EbuMigrated());
    }
    public static void setEbuMigratedUserSuccesCase() {
        User user = User.getNewUser(VodafoneController.getInstance().getUserProfile());
        if (user instanceof EbuMigrated) {
            ((EbuMigrated) user).setEntityVerifyed(true);
        }
        VodafoneController.getInstance().setUser(user);
    }

    public static boolean isUserVerifiedEbuMigrated() {
        User user = VodafoneController.getInstance().getUser();
        return user instanceof EbuMigrated &&
                ((EbuMigrated) user).isEntityVerifyed();
    }
    public static boolean isEbuMigratedSubscriberOrNotVerified(){
        User user = VodafoneController.getInstance().getUser();
        return user instanceof EbuMigrated && (((EbuMigrated) user).isSubscriber() || !((EbuMigrated) user).isEntityVerifyed());
    }
}
