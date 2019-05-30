package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.ReservedPromotion;

/**
 * Created by Victor Radulescu on 12/21/2017.
 */

public class LoyaltyMigration extends BaseMigration {

    @Override
    public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if(schema.get(LoyaltySegmentSuccess.class.toString()) != null) {
            logCrash(LoyaltySegmentSuccess.class,false);

        }else{
            schema.create(LoyaltySegmentSuccess.class.toString())
                    .addField("id",String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("lpsSegment",String.class)
                    .addField("treatmentSegment",String.class);
            logCrash(LoyaltySegmentSuccess.class,true);
        }

        if(schema.get(Promotion.class.toString()) != null) {
            logCrash(Promotion.class,false);

        }else{
            schema.create(Promotion.class.toString())
                    .addField("promotionId",String.class, FieldAttribute.PRIMARY_KEY)
                    .addField(Promotion.CAMPAIGN_EXPIRY_DATE_KEY,Long.class)
                    .addField("loyaltySegment",String.class)
                    .addField("repositoryId",String.class)
                    .addField("subscriberType",String.class)
                    .addField("treatmentSegment",String.class)
                    .addField("userRole",String.class)
                    .addField("bannerId",String.class)
                    .addField("campaignStartDate",Long.class)
                    .addField(Promotion.CATEGORY_KEY,String.class)
                    .addField("customerType",String.class)
                    .addField("discountValue",String.class)
                    .addField(Promotion.IS_HERO_OFFER,String.class)
                    .addField("partnerHyperlink",String.class)
                    .addField(Promotion.PARTNER_NAME_KEY,String.class)
                    .addField(Promotion.PRIORITY_KEY,Long.class)
                    .addField("quantity",Integer.class)
                    .addField("shortDescription",String.class)
                    .addField("termsAndConditions",String.class)
                    .addField("unitMeasure",String.class)
                    .addField("voucherCode",String.class)
                    .addField(Promotion.VOUCHER_EXPIRY_DATE_KEY,long.class)
                    .addField("voucherName",String.class)
                    .addField(Promotion.IS_RESERVED_KEY,boolean.class);
            logCrash(Promotion.class,true);
        }

        if(schema.get(ReservedPromotion.class.toString()) != null) {
            logCrash(ReservedPromotion.class,false);

        }else{
            schema.create(ReservedPromotion.class.toString())
                    .addField("reservationId",String.class, FieldAttribute.PRIMARY_KEY)
                    .addField(ReservedPromotion.CAMPAIGN_EXPIRY_DATE_KEY,Long.class)
                    .addField("loyaltySegment",String.class)
                    .addField("repositoryId",String.class)
                    .addField("subscriberType",String.class)
                    .addField("treatmentSegment",String.class)
                    .addField("userRole",String.class)
                    .addField("msisdn",String.class)
                    .addField("promotion",Promotion.class)
                    .addField("campaignReservationDate",String.class);
            logCrash(Promotion.class,true);
        }

        if(schema.get(LoyaltyVoucherSuccess.class.toString()) != null) {
            if(!schema.get("LoyaltyVoucherSuccess").hasField("sysdate")) {
                schema.get("LoyaltyVoucherSuccess").addField("sysdate",Long.class);
                logNewFieldAdded("LoyaltyVoucherSuccess","sysdate",Long.class);
            }
        }else{
            schema.create(LoyaltyVoucherSuccess.class.toString())
                    .addField("id",long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmListField("promotions",schema.get(Promotion.class.toString()))
                    .addField("sysdate",Long.class);
            logCrash(LoyaltyVoucherSuccess.class,true);
        }


        if(schema.get(LoyaltyVoucherReservedSuccess.class.toString())!=null) {
            if(!schema.get(LoyaltyVoucherReservedSuccess.class.toString()).hasField("sysdate")) {
                schema.get(LoyaltyVoucherReservedSuccess.class.toString()).addField("sysdate", Long.class);
                logNewFieldAdded(LoyaltyVoucherReservedSuccess.class.toString(), "sysdate", Long.class);
            }
        }else{
            schema.create(LoyaltyVoucherReservedSuccess.class.toString())
                    .addField("id",long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmListField("promotions",schema.get(ReservedPromotion.class.toString()))
                    .addField("sysdate",Long.class);
            logCrash(LoyaltyVoucherReservedSuccess.class,true);
        }

    }
}
