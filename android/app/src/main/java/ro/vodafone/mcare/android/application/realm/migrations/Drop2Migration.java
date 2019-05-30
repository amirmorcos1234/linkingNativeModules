package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.creditInAdvance.EligibilityInfo;
import ro.vodafone.mcare.android.client.model.identity.AccountCui;
import ro.vodafone.mcare.android.client.model.identity.AccountHolding;
import ro.vodafone.mcare.android.client.model.identity.AccountSpecialist;
import ro.vodafone.mcare.android.client.model.identity.BillingCustomer;
import ro.vodafone.mcare.android.client.model.identity.CurrentIdentity;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.FinancialAccount;
import ro.vodafone.mcare.android.client.model.identity.SubscriberEntity;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReserveSuccess;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;

import static ro.vodafone.mcare.android.client.model.identity.EntityChildItem.ENTITY_TYPE_KEY;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.PASSWORD;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.USER_NAME;

/**
 * Created by Victor Radulescu on 12/21/2017.
 */

public class Drop2Migration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if(schema.get(UserProfile.class.getSimpleName())!=null){
            DynamicRealmObject dynamicRealmObject = realm.where(UserProfile.class.getSimpleName()).findFirst();
            if(dynamicRealmObject!=null){
                String userName = dynamicRealmObject.get(USER_NAME);
                CredentialUtils.saveUsername(userName);
                String password = dynamicRealmObject.get(PASSWORD);
                CredentialUtils.savePassword(password);
            }
        }
        if(schema.get(Subscriber.class.getSimpleName()) != null) {
            realm.where(Subscriber.class.getSimpleName()).findAll().deleteAllFromRealm();
            try{
                schema.get(Subscriber.class.getSimpleName())
                        .addPrimaryKey("msisdn");
            }catch (Exception e){
                e.printStackTrace();
            }
            logCrash(Subscriber.class,true);
        } else {
            logCrash(Subscriber.class,false);
        }

        if(schema.get(UserProfileHierarchy.class.getSimpleName()) != null) {
            if(!schema.get(UserProfileHierarchy.class.getSimpleName()).hasField("ban")){
                schema.get(UserProfileHierarchy.class.getSimpleName())
                        .addRealmObjectField("ban",schema.get(Ban.class.getSimpleName()));
                logCrash(UserProfileHierarchy.class,true);
            }
        } else {
            logCrash(UserProfileHierarchy.class,false);
        }
        if(schema.get(EntityChildItem.class.getSimpleName()) == null) {
           RealmObjectSchema realmObjectSchema = schema.create(EntityChildItem.class.getSimpleName())
                    .addField("entityId",String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("displayName",String.class)
                    .addField(ENTITY_TYPE_KEY,String.class)
                    .addField("crmRole",String.class)
                    .addField("vfOdsCui",String.class)
                    .addField("vfOdsSsn",String.class)
                    .addField("vfOdsSsnType",String.class)
                    .addField("vfOdsPhoneNumber",String.class)
                    .addField("vfOdsSid",String.class)
                    .addField("vfOdsResourceType",String.class)
                    .addField("treatmentSegment",String.class)
                    .addField("microSegment",String.class)
                    .addField("vfOdsBan",String.class)
                    .addField("vfOdsBen",String.class)
                    .addField("cuiName",String.class);
            realmObjectSchema.addRealmListField("childList",schema.get(EntityChildItem.class.getSimpleName()));
            logCrash(EntityChildItem.class,true);

        } else {
            logCrash(EntityChildItem.class,false);
        }

        if(schema.get(EligibilityInfo.class.getSimpleName()) == null) {
            schema.create(EligibilityInfo.class.getSimpleName())
                    .addField("id",long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("credit",String.class)
                    .addField("fee",String.class)
                    .addField("ciaID",String.class)
                    .addField("trsId",String.class)
                    .addField("minMos",String.class)
                    .addField("minReload",String.class)
                    .addField("maxBalance",String.class)
                    .addField("enabled",String.class)
                    .addField("orderNo",String.class);
            logCrash(EligibilityInfo.class,true);

        } else {
            logCrash(EligibilityInfo.class,false);
        }

        if(schema.get(CreditInAdvanceSuccess.class.getSimpleName()) == null) {
            schema.create(CreditInAdvanceSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("CIAEligibiliy",schema.get(EligibilityInfo.class.getSimpleName()));
            logCrash(CreditInAdvanceSuccess.class,true);

        } else {
            logCrash(CreditInAdvanceSuccess.class,false);
        }

        if(schema.get(CurrentIdentity.class.getSimpleName()) == null) {
            schema.create(CurrentIdentity.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("entityId", String.class)
                    .addField("entityType", String.class)
                    .addField("billingCustomerParentId", String.class)
                    .addField("accountCuiParentId", String.class)
                    .addField("accountHolding", String.class);
        } else {
            logCrash(CurrentIdentity.class,false);
        }

        if(schema.get(CustomerRestrictionsSuccess.class.getSimpleName()) == null) {
            schema.create(CustomerRestrictionsSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("isBlacklistForever", Boolean.class)
                    .addField("isServiceBadDebt", Boolean.class)
                    .addField("isDeviceBlacklist", Boolean.class);
        } else {
            logCrash(CustomerRestrictionsSuccess.class,false);
        }

        if(schema.get(AccountSpecialist.class.getSimpleName()) == null) {
            schema.create(AccountSpecialist.class.getSimpleName())
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("firstName", String.class)
                    .addField("lastName", String.class)
                    .addField("email", String.class)
                    .addField("phone", String.class)
                    .addField("department", String.class)
                    .addField("lockDate", String.class);
        } else {
            logCrash(AccountSpecialist.class,false);
        }

        if(schema.get(EntityDetailsSuccess.class.getSimpleName()) == null) {
            schema.create(EntityDetailsSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("dataSpecialist", AccountSpecialist.class)
                    .addField("voiceSpecialist", AccountSpecialist.class)
                    .addField("billCycle", Integer.class);
        } else
        if (schema.get(EntityDetailsSuccess.class.getSimpleName()) != null){
            schema.get(EntityDetailsSuccess.class.getSimpleName())
                    .addField("billCycle", Integer.class);
        }

        if(schema.get(SubscriberEntity.class.getSimpleName()) == null) {
            schema.create(SubscriberEntity.class.getSimpleName())
                    .addField("displayName", String.class)
                    .addField("entityId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("crmRole", String.class)
                    .addField("vfOdsPhoneNumber", String.class)
                    .addField("vfOdsSid", String.class)
                    .addField("vfOdsResourceType", String.class);
        } else {
            logCrash(SubscriberEntity.class,false);
        }

        if(schema.get(UserEntitiesSuccess.class.getSimpleName()) == null) {
            schema.create(UserEntitiesSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("defaultEntity", String.class)
                    .addField("contactFirstName", String.class)
                    .addRealmListField("entitiesList", schema.get(EntityChildItem.class.getSimpleName()));
        } else {
            logCrash(UserEntitiesSuccess.class,false);
        }

        if(schema.get(FinancialAccount.class.getSimpleName()) == null) {
            schema.create(FinancialAccount.class.getSimpleName())
                    .addField("displayName", String.class)
                    .addField("entityId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("crmRole", String.class)
                    .addField("vfOdsBan", String.class)
                    .addField("vfOdsBen", String.class)
                    .addField("cuiName", String.class);
        } else {
            logCrash(FinancialAccount.class,false);
        }

        if(schema.get(BillingCustomer.class.getSimpleName()) == null) {
            schema.create(BillingCustomer.class.getSimpleName())
                    .addField("displayName", String.class)
                    .addField("entityId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("crmRole", String.class)
                    .addField("vfOdsCui", String.class)
                    .addField("vfOdsSsn", String.class)
                    .addField("vfOdsSsnType", String.class)
                    .addField("vfOdsCid", String.class)
                    .addField("treatmentSegment", String.class)
                    .addField("microSegment", String.class)
                    .addField("cuiName", String.class)
                    .addRealmListField("childList", schema.get(FinancialAccount.class.getSimpleName()));
        } else {
            logCrash(BillingCustomer.class,false);
        }

        if(schema.get(AccountCui.class.getSimpleName()) == null) {
            schema.create(AccountCui.class.getSimpleName())
                    .addField("displayName", String.class)
                    .addField("entityId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("crmRole", String.class)
                    .addField("vfOdsCui", String.class)
                    .addField("vfOdsSsn", String.class)
                    .addField("vfOdsSsnType", String.class)
                    .addRealmListField("childList", schema.get(BillingCustomer.class.getSimpleName()));
        }else {
            logCrash(AccountCui.class,false);
        }

        if(schema.get(AccountHolding.class.getSimpleName()) == null) {
            schema.create(AccountHolding.class.getSimpleName())
                    .addField("displayName", String.class)
                    .addField("entityId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("crmRole", String.class)
                    .addRealmListField("childList", schema.get(AccountCui.class.getSimpleName()));
        } else {
            logCrash(AccountHolding.class,false);
        }
        if(schema.get(LoyaltyVoucherReserveSuccess.class.getSimpleName()) == null) {
            schema.create("LoyaltyVoucherReserveSuccess")
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("voucherCode", String.class);
        } else {
            logCrash(LoyaltyVoucherReserveSuccess.class,false);
        }
    }


}
