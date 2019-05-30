package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/1/2017.
 */

public class RetentionLabels extends AppLabels {

	public static String getRetentionHistoryRechargeTitle() {
		return getLabelWithPrimaryKey("retention_history_recharge_title", "Vezi istoricul comenzilor tale");
	}

	public static String getRetentionHistoryRechargeSecondaryBtn() {
		return getLabelWithPrimaryKey("retention_history_recharge_secondary_btn", "Istoric comenzi");
	}

	public static String getRetentionShoppingCartEmptyFalseTitle() {
		return getLabelWithPrimaryKey("retention_shoppingcart_empty_false_title", "Oferte pentru numărul tău");
	}

	public static String getRetentionShoppingCartEmptyFalseBody() {
		return getLabelWithPrimaryKey("retention_shoppingcart_empty_false_body", "Ai produse sau servicii adăugate in coş. Poţi finaliza comanda sau poţi şterge produsele din coş dacă doreşti alte oferte.");
	}

	public static String getRetentionShoppingCartEmptyFalsePrimaryBtn() {
		return getLabelWithPrimaryKey("retention_shoppingcart_empty_false_primary_btn", "Vezi coş cumpărături");
	}

	public static String getRetentionEligiblePhones() {
		return getLabelWithPrimaryKey("retention_eligible_phones", "Vezi ofertă telefoane");
	}

	public static String getRetentionEligibleServices() {
		return getLabelWithPrimaryKey("retention_eligible_services", "Vezi ofertă abonamente");
	}

	public static String getRetentionEligiblePhonesNotAuthorized() {
		return getLabelWithPrimaryKey("retention_eligible_phones_not_authorized", "Pentru [MSISDN] momentan nu poţi modifica abonamentul. Contactează-ne pe chat sau prin email.");
	}

	public static String getRetentionAddIn() {
		return getLabelWithPrimaryKey("retention_add_in", "Adaugă în coş");
	}

	public static String getRetentionAddPhone() {
		return getLabelWithPrimaryKey("retention_add_phone", "Adaugă telefon");
	}

	public static String getRetentionExtraBenefits() {
		return getLabelWithPrimaryKey("retention_extra_benefits", "În plus ai următoarele:");
	}

	public static String getTitleHome() {
		return getLabelWithPrimaryKey("title_home", "Home");
	}

	public static String getTitleDashboard() {
		return getLabelWithPrimaryKey("title_dashboard", "Dashboard");
	}

	public static String getTitleNotifications() {
		return getLabelWithPrimaryKey("title_notifications", "Notifications");
	}

	public static String getAddToCartText() {
		return getLabelWithPrimaryKey("retention_add_to_cart", "Adaugă în coș");
	}

	public static String getSeeOtherBenefitsForPhoneText() {
		return getLabelWithPrimaryKey("retention_other_benefits_for_phone", "Vezi alte prețuri de oferte");
	}

	public static String getNoDetailsFromHtmlError() {
		return getLabelWithPrimaryKey("retention_no_details_from_html_error", "Aceste informații momentan nu pot fi afișate.");
	}

	public static String getTitleFaq() {
		return getLabelWithPrimaryKey("title_faq", "FAQ");
	}

	public static String getTitleChat2() {
		return getLabelWithPrimaryKey("title_chat2", "Chat");
	}

	public static String getTitleEmail() {
		return getLabelWithPrimaryKey("title_email", "Email");
	}

	public static String getGreenTaxTitle() {
		return getLabelWithPrimaryKey("retention_green_tax_title", "Preţul include {greenTax} taxă timbru verde");
	}

	public static String getGreenTaxSubTitle() {
		return getLabelWithPrimaryKey("retention_green_tax_subtitle", "Acest produs face obiectul unei colectări separate.");
	}

	public static String getNotEligiblePricePlan() {
		return getLabelWithPrimaryKey("not_eligible_price_plan_text", "Momentan abonamentul tău nu poate fi modificat.");
	}

	public static String getNotEligiblePhone() {
		return getLabelWithPrimaryKey("not_eligible_phone_text", "Momentan abonamentul tău nu permite achiziționarea unor noi telefoane.");
	}

	public static String getPricePlanNotInList() {
		return getLabelWithPrimaryKey("price_plan_not_in_list_text", "Momentan nu ești eligibil pentru acest tip de abonament.");
	}

	public static String getPhoneNotInList() {
		return getLabelWithPrimaryKey("phone_not_in_list_text", "Momentan abonamentul tău nu permite achiziționarea acestui telefon.");
	}
}
