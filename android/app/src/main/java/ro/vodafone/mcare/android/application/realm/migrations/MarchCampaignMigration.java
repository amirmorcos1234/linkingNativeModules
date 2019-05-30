package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;

import static ro.vodafone.mcare.android.client.model.realm.UserProfile.PASSWORD;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.SELECTED_BAN_NUMBER;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.SELECTED_MSISDN_NUMBER;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.USER_NAME;

/**
 * Created by Victor Radulescu on 2/9/2018.
 */

public class MarchCampaignMigration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema userProfileSchema = schema.get(UserProfile.class.getSimpleName());
        if (userProfileSchema != null) {
            if(!userProfileSchema.hasField(SELECTED_MSISDN_NUMBER)) {
                userProfileSchema.addField(SELECTED_MSISDN_NUMBER,String.class);
            }
            if(!userProfileSchema.hasField(SELECTED_BAN_NUMBER)) {
                userProfileSchema.addField(SELECTED_BAN_NUMBER,String.class);
            }
        }
    }
}
