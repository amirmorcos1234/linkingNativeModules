package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.ExcludedPromo;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;

import static ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow.ACTIVATE_IN_MONTHS;
import static ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow.ACTIVATION_DATE;
import static ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow.DEACTIVATE_AFTER_MONTHS;
import static ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow.DEACTIVATION_DATE;
import static ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow.EXCLUDED_PROMOS_LIST;

/**
 * Created by user on 12.03.2018.
 */

public class EbuOffersMigration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();


        if(schema.get(ExcludedPromo.class.getSimpleName()) != null) {
            logCrash(ExcludedPromo.class,false);

        }else{
            schema.create(ExcludedPromo.class.getSimpleName())
                    .addField("promoId",String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("promoName",String.class);
            logCrash(ExcludedPromo.class,true);
        }

        RealmObjectSchema postpaidOfferRowSchema = schema.get(PostpaidOfferRow.class.getSimpleName());

        if (postpaidOfferRowSchema != null) {
            if(!postpaidOfferRowSchema.hasField(EXCLUDED_PROMOS_LIST)) {
                postpaidOfferRowSchema.addRealmListField(EXCLUDED_PROMOS_LIST, schema.get(ExcludedPromo.class.getSimpleName()));
            }
            if(!postpaidOfferRowSchema.hasField(ACTIVATION_DATE)) {
                postpaidOfferRowSchema.addField(ACTIVATION_DATE,Long.class);
            }
            if(!postpaidOfferRowSchema.hasField(DEACTIVATION_DATE)) {
                postpaidOfferRowSchema.addField(DEACTIVATION_DATE,Long.class);
            }
            if(!postpaidOfferRowSchema.hasField(ACTIVATE_IN_MONTHS)) {
                postpaidOfferRowSchema.addField(ACTIVATE_IN_MONTHS,Integer.class);
            }
            if(!postpaidOfferRowSchema.hasField(DEACTIVATE_AFTER_MONTHS)) {
                postpaidOfferRowSchema.addField(DEACTIVATE_AFTER_MONTHS,Integer.class);
            }
        }
    }
}
