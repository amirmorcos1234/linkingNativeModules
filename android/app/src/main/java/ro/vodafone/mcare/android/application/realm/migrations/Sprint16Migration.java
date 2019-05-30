package ro.vodafone.mcare.android.application.realm.migrations;


import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;

public class Sprint16Migration extends BaseMigration {


    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema getByOperatorSuccessSchema = schema.get(GetByOperatorSuccess.class.getSimpleName());
        if (getByOperatorSuccessSchema != null) {
            if (!getByOperatorSuccessSchema.hasField("defaultUserId")) {
                getByOperatorSuccessSchema.addField("defaultUserId", String.class);
            }
        }

        RealmObjectSchema ActiveOfferPostpaidSchema = schema.get(ActiveOfferPostpaid.class.getSimpleName());
        if (ActiveOfferPostpaidSchema != null) {
            if (!ActiveOfferPostpaidSchema.hasField("currentDate")) {
                ActiveOfferPostpaidSchema.addField("currentDate", Long.class);
            }
        }

        RealmObjectSchema ActiveOffersSuccessSchema = schema.get(ActiveOffersSuccess.class.getSimpleName());
        if (ActiveOffersSuccessSchema != null) {
            if (!ActiveOffersSuccessSchema.hasField("isNationalOnly")) {
				ActiveOffersSuccessSchema.addField("isNationalOnly", boolean.class);
            }
        }
    }
}