package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails;

/**
 * Created by Serban Radulescu on 2/14/2018.
 */

public class Sprint11D1Migration extends BaseMigration {
    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema ProfileSubscriptionSuccessSchema = schema.get(ProfileSubscriptionSuccess.class.getSimpleName());
        if(ProfileSubscriptionSuccessSchema == null) {
            schema.create(ProfileSubscriptionSuccess.class.getSimpleName())
                    .addField("id",long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("isVDFSubscription", Boolean.class);
        }


        RealmObjectSchema HistoryDetailSchema = schema.get(HistoryDetail.class.getSimpleName());
        if(HistoryDetailSchema == null) {
            schema.create(HistoryDetail.class.getSimpleName())
                    .addField("valueOfDetail", Double.class)
                    .addField("keyOfDetail", String.class)
                    .addField("labelOfDetail", String.class)
                    .addField("valueTextOfDetail", String.class)
                    .addField("priority", Long.class);
        }


        RealmObjectSchema ServiceDetailsSchema = schema.get(ServiceDetails.class.getSimpleName());
        if(ServiceDetailsSchema == null) {
            schema.create(ServiceDetails.class.getSimpleName())
                    .addField("serviceDesc", String.class)
                    .addField("billingAmount", Double.class);
        }

        RealmObjectSchema SummaryDetailsSchema = schema.get(SummaryDetails.class.getSimpleName());
        if(SummaryDetailsSchema == null) {
            schema.create(SummaryDetails.class.getSimpleName())
                    .addField("valueOfDetail",Double.class)
                    .addField("labelOfDetail",String.class)
                    .addField("priority",String.class)
                    .addField("keyOfDetail", String.class)
                    .addRealmListField("extendedDetails",schema.get(ServiceDetails.class.getSimpleName()));
        }

        RealmObjectSchema BillSummaryItemSchema = schema.get(BillSummaryItem.class.getSimpleName());
        if(BillSummaryItemSchema == null) {
            schema.create(BillSummaryItem.class.getSimpleName())
                    .addField("phoneNumber", String.class)
                    .addField("sid", String.class)
                    .addField("totalAmount", Double.class)
                    .addField("additionalCost", Double.class)
                    .addRealmListField("subsTermAmountDetails", schema.get(ServiceDetails.class.getSimpleName()))
                    .addRealmListField("summaryDetails", schema.get(SummaryDetails.class.getSimpleName()));
        }
        RealmObjectSchema BillHistoryDetailsSchema = schema.get(BillHistoryDetails.class.getSimpleName());
        if(BillHistoryDetailsSchema == null) {
            schema.create(BillHistoryDetails.class.getSimpleName())
                    .addField("billClosedDate", Long.class)
                    .addField("totalAmountDue", double.class)
                    .addField("totalMonthlyFee", double.class)
                    .addField("gsmUtilizedServices", double.class)
                    .addRealmListField("historyDetails", schema.get(HistoryDetail.class.getSimpleName()));
        }


        RealmObjectSchema BillSummarySuccessSchema = schema.get(BillSummarySuccess.class.getSimpleName());
        if(BillSummarySuccessSchema == null) {
            schema.create(BillSummarySuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("hideDownloadButton", boolean.class)
                    .addRealmListField("billSummaryList", schema.get(BillSummaryItem.class.getSimpleName()));
        }

        RealmObjectSchema BillSummaryDetailsSuccessSchema = schema.get(BillSummaryDetailsSuccess.class.getSimpleName());
        if(BillSummaryDetailsSuccessSchema == null) {
            schema.create(BillSummaryDetailsSuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmListField("billSummaryDetailsList", schema.get(SummaryDetails.class.getSimpleName()));
        }

        RealmObjectSchema BillHistorySuccessSchema = schema.get(BillHistorySuccess.class.getSimpleName());
        if(BillHistorySuccessSchema == null) {
            schema.create(BillHistorySuccess.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmListField("billHistoryList", schema.get(BillHistoryDetails.class.getSimpleName()));
        }


    }
}
