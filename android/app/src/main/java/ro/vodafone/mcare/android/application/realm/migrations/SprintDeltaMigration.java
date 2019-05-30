package ro.vodafone.mcare.android.application.realm.migrations;


import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmFieldType;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.gdpr.SessionIdMap;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails;

public class SprintDeltaMigration extends BaseMigration {


    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

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