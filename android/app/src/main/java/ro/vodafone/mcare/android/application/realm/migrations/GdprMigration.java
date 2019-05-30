package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.gdpr.SessionIdMap;


public class GdprMigration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema GdprPermissionsSchema = schema.get(GdprPermissions.class.getSimpleName());
        if(GdprPermissionsSchema == null) {
            schema.create(GdprPermissions.class.getSimpleName())
                    .addField("vfSid", String.class)
                    .addField("channel", String.class)
                    .addField("sessionId", String.class)
                    .addField("vfEmail", String.class)
                    .addField("vfPost", String.class)
                    .addField("vfSmsMmsPush", String.class)
                    .addField("vfOutboundCall", String.class)
                    .addField("vfBasicProfileCustServiceData", String.class)
                    .addField("vfAdvancedProfileNetworkData", String.class)
                    .addField("vfAdvancedProfileOnlineData", String.class)
                    .addField("vfSurveyCategory", String.class)
                    .addField("extEmail", String.class)
                    .addField("extPost", String.class)
                    .addField("extSmsMmsPush", String.class)
                    .addField("extOutboundCall", String.class)
                    .addField("extBasicProfileCustServiceData", String.class)
                    .addField("extAdvancedProfileNetworkData", String.class)
                    .addField("extAdvancedProfileOnlineData", String.class)
                    .addField("extSurveyCategory", String.class)
                    .addField("minorStatus", String.class)
                    .addField("minorBirthDate", String.class)
                    .addField("guardianContactPhone", String.class)
                    .addField("guardianEmailAddress", String.class);
        }

        RealmObjectSchema GdprGetResponseSchema = schema.get(GdprGetResponse.class.getSimpleName());
        if (GdprGetResponseSchema == null) {
            schema.create(GdprGetResponse.class.getSimpleName())
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("gpdrPermissions", schema.get(GdprPermissions.class.getSimpleName()));
        }

        RealmObjectSchema SessionIdMapSchema = schema.get(SessionIdMap.class.getSimpleName());
        if (SessionIdMapSchema == null) {
            schema.create(SessionIdMap.class.getSimpleName())
                    .addField("key", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("value", String.class)
                    .addField("isUserData", boolean.class);
        }

    }
}
