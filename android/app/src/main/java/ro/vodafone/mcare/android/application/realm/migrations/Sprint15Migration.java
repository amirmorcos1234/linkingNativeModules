package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;

public class Sprint15Migration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema userProfileHierarchySchema = schema.get(UserProfileHierarchy.class.getSimpleName());

        if(userProfileHierarchySchema != null) {
            if(!userProfileHierarchySchema.hasField("errorDetectedFlag")) {
                userProfileHierarchySchema.addField("errorDetectedFlag", int.class);
            }
        }

    }
}
