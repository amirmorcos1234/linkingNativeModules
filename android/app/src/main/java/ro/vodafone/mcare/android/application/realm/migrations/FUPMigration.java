package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;

import static ro.vodafone.mcare.android.client.model.costControl.Balance.REMAINING_AMOUNT_KEY;
import static ro.vodafone.mcare.android.client.model.costControl.Balance.TOTAL_AMOUNT_KEY;
import static ro.vodafone.mcare.android.client.model.costControl.Balance.USED_AMOUNT_KEY;

/**
 * Created by Victor Radulescu on 1/12/2018.
 */

public class FUPMigration extends BaseMigration{

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema balanceSchema = schema.get(Balance.class.getSimpleName());

        if(balanceSchema != null) {
        if(balanceSchema.hasField(USED_AMOUNT_KEY)){
            balanceSchema.removeField(USED_AMOUNT_KEY);
        }balanceSchema.addField(USED_AMOUNT_KEY,Double.class);

        if(balanceSchema.hasField(REMAINING_AMOUNT_KEY)){
            balanceSchema.removeField(REMAINING_AMOUNT_KEY);
        }balanceSchema.addField(REMAINING_AMOUNT_KEY,Double.class);

        if(balanceSchema.hasField(TOTAL_AMOUNT_KEY)){
            balanceSchema.removeField(TOTAL_AMOUNT_KEY);
        }balanceSchema.addField(TOTAL_AMOUNT_KEY,Double.class);
        }

        RealmObjectSchema balanceShowAndNowShownSchema = schema.get(BalanceShowAndNotShown.class.getSimpleName());

        if(balanceShowAndNowShownSchema != null) {
        if(balanceShowAndNowShownSchema.hasField(USED_AMOUNT_KEY)){
            balanceShowAndNowShownSchema.removeField(USED_AMOUNT_KEY);
        }balanceShowAndNowShownSchema.addField(USED_AMOUNT_KEY,Double.class);

        if(balanceShowAndNowShownSchema.hasField(REMAINING_AMOUNT_KEY)){
            balanceShowAndNowShownSchema.removeField(REMAINING_AMOUNT_KEY);
        }balanceShowAndNowShownSchema.addField(REMAINING_AMOUNT_KEY,Double.class);

        if(balanceShowAndNowShownSchema.hasField(TOTAL_AMOUNT_KEY)){
            balanceShowAndNowShownSchema.removeField(TOTAL_AMOUNT_KEY);
        }balanceShowAndNowShownSchema.addField(TOTAL_AMOUNT_KEY,Double.class);
        }
        RealmObjectSchema activeOfferPostpaidSchema = schema.get(ActiveOfferPostpaid.class.getSimpleName());
        if(activeOfferPostpaidSchema!=null){
            if(!activeOfferPostpaidSchema.hasField(ActiveOfferPostpaid.OFFER_INSTANCE_ID_REALM_KEY)) {
                activeOfferPostpaidSchema.addField(ActiveOfferPostpaid.OFFER_INSTANCE_ID_REALM_KEY, Long.class);
            }
        }
    }
}

