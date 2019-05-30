package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;


/**
 * Created by Victor Radulescu on 2/9/2018.
 */

public class GreenTaxMigration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema objectSchema = schema.get(ShopProduct.class.getSimpleName());
        if (objectSchema != null) {
            if(!objectSchema.hasField(ShopProduct.GREEN_TAX)) {
                objectSchema.addField(ShopProduct.GREEN_TAX,Double.class);
            }
        }
    }
}
