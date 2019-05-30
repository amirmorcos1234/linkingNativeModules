package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DevicesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;

public class Sprint14Migration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema devicesListSchema = schema.get(DevicesList.class.getSimpleName());

        if (devicesListSchema == null) {
            schema.create(DevicesList.class.getSimpleName())
                    .addField("udid", String.class)
                    .addField("name", String.class)
                    .addField("activatedOn", Long.class);
        }

        RealmObjectSchema devicesFamiliesSchema = schema.get(DeviceFamiliesList.class.getSimpleName());

        if (devicesFamiliesSchema == null) {
            schema.create(DeviceFamiliesList.class.getSimpleName())
                    .addRealmListField("devices",schema.get(DevicesList.class.getSimpleName()))
                    .addField("name", String.class)
                    .addField("deviceLimit", Integer.class)
                    .addField("icon", String.class);
        }

        RealmObjectSchema getByOperatorSuccessSchema = schema.get(GetByOperatorSuccess.class.getSimpleName());

        if (getByOperatorSuccessSchema == null) {
            schema.create(GetByOperatorSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("masterUserId", String.class)
                    .addRealmListField("deviceFamilies",schema.get(DeviceFamiliesList.class.getSimpleName()))
                    .addField("deleteNextDeviceAction", Long.class)
                    .addField("deleteRestricted", Boolean.class)
                    .addField("devicesLimit", Integer.class)
                    .addField("externalId", String.class);
        }
    }

}
