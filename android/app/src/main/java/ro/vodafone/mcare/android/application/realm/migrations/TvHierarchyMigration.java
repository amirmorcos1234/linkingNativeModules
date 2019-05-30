package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchy;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;

public class TvHierarchyMigration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema tvHierarchy = schema.get(TvHierarchy.class.getSimpleName());

        if (tvHierarchy == null) {
            schema.create(TvHierarchy.class.getSimpleName())
                    .addField("serviceType", String.class)
                    .addField("serviceId", String.class)
                    .addField("ban", String.class)
                    .addField("infoSource", String.class)
                    .addField("customerId", String.class)
                    .addField("subscriberId", String.class)
                    .addField("creationDate", String.class)
                    .addField("alias",String.class)
                    .addField("avatarURL",String.class);
        }

        RealmObjectSchema tvHierarchyResponse = schema.get(TvHierarchyResponse.class.getSimpleName());

        if (tvHierarchyResponse == null) {
            schema.create(TvHierarchyResponse.class.getSimpleName())
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmListField("activeVtvList", schema.get(TvHierarchy.class.getSimpleName()));

        }


    }
}
