package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrepaidRealmLong;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;

public class PrePaidRoamingMigration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema offersListSchema = schema.get(PrePaidOffersList.class.getSimpleName());
        if (offersListSchema == null) {
            schema.create(PrePaidOffersList.class.getSimpleName())
                    .addField("offerName", String.class)
                    .addField("offerPrice", Integer.class)
                    .addField("offerShortDescription", String.class)
                    .addField("offerId", Long.class, FieldAttribute.PRIMARY_KEY);
        }
        RealmObjectSchema realmLongSchema = schema.get(PrepaidRealmLong.class.getSimpleName());
        if (realmLongSchema == null) {
            schema.create(PrepaidRealmLong.class.getSimpleName())
                    .addField("offerId", Long.class);
        }
        RealmObjectSchema countryListSchema = schema.get(CountryList.class.getSimpleName());
        if (!countryListSchema.hasField("RelatedOffers")) {
            schema.get(CountryList.class.getSimpleName())
                    .addRealmListField("RelatedOffers", schema.get(PrepaidRealmLong.class.getSimpleName()));
        }
        RealmObjectSchema roamingTariffsSuccessSchema = schema.get(RoamingTariffsSuccess.class.getSimpleName());
        if (!roamingTariffsSuccessSchema.hasField("offersList")) {
            schema.get(RoamingTariffsSuccess.class.getSimpleName())
                    .addRealmListField("offersList", schema.get(PrePaidOffersList.class.getSimpleName()));
        }
        RealmObjectSchema userProfileSchema = schema.get(UserProfile.class.getSimpleName());
        if (userProfileSchema != null) {
            if (!userProfileSchema.hasField("vfActivationToken")) {
                userProfileSchema.addField("vfActivationToken", String.class);
            }

            if (!userProfileSchema.hasField("vfIsEmailValidated")) {
                userProfileSchema.addField("vfIsEmailValidated", boolean.class);
            }

            if (!userProfileSchema.hasField("vfIsUsernameValidated")) {
                userProfileSchema.addField("vfIsUsernameValidated", boolean.class);
            }

            if (!userProfileSchema.hasField("vfFixedBan")) {
                userProfileSchema.addField("vfFixedBan", String.class);
            }

            if (!userProfileSchema.hasField("vfFixedCid")) {
                userProfileSchema.addField("vfFixedCid", String.class);
            }

            if (!userProfileSchema.hasField("vfIsPhoneValidated")) {
                userProfileSchema.addField("vfIsPhoneValidated", boolean.class);
            }
        }
    }
}
