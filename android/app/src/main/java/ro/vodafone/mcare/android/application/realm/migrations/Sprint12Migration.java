package ro.vodafone.mcare.android.application.realm.migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusEBUSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeCampaignDetails;

/**
 * Created by Serban Radulescu on 6/4/2018.
 */

public class Sprint12Migration extends BaseMigration {

	@Override
	public void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion) {
		RealmSchema schema = realm.getSchema();

		RealmObjectSchema SimStatusEbuSuccessSchema = schema.get(SIMStatusEBUSuccess.class.getSimpleName());
		if(SimStatusEbuSuccessSchema == null) {
			schema.create(SIMStatusEBUSuccess.class.getSimpleName())
					.addField("primaryKey", long.class, FieldAttribute.PRIMARY_KEY)
					.addField("isActiv", Boolean.class)
					.addField("productId", String.class)
					.addField("productSpecName", String.class);
		}

		RealmObjectSchema AccessTypeEbuSchema = schema.get(AccessTypeEBU.class.getSimpleName());
		if(AccessTypeEbuSchema == null) {
			schema.create(AccessTypeEBU.class.getSimpleName())
					.addField("primaryKey", long.class, FieldAttribute.PRIMARY_KEY)
					.addField("isInternational", Boolean.class)
					.addField("isRoaming", Boolean.class)
					.addField("productId", String.class)
					.addField("productSpecName", String.class);
		}

		RealmObjectSchema AdobeCampaignDetailsSchema = schema.get(AdobeCampaignDetails.class.getSimpleName());
		if(AdobeCampaignDetailsSchema == null) {
			schema.create(AdobeCampaignDetails.class.getSimpleName())
					.addField("elementID", String.class, FieldAttribute.PRIMARY_KEY)
					.addField("recurrenceCounter", int.class)
					.addField("recurrenceTTL", long.class);
		}

		RealmObjectSchema AncomOfferSchema = schema.get(AncomOffer.class.getSimpleName());
		if(AncomOfferSchema != null) {
			if(!AncomOfferSchema.hasField("contractDetails")) {
				AncomOfferSchema.addField("contractDetails", String.class);
			}
		}
	}
}
