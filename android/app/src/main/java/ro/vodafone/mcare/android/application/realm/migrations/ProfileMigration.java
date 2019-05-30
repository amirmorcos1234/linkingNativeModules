package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;

public class ProfileMigration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema profileSchema = schema.get(Profile.class.getSimpleName());
        if (profileSchema != null) {
            if (!profileSchema.hasField("subscriberEffectiveDate")) {
                profileSchema.addField("subscriberEffectiveDate", Long.class);
            }
            if (!profileSchema.hasField("activationDate")) {
                profileSchema.addField("activationDate", Long.class);
            }
        }
    }
}
