package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by Bivol Pavel on 10.04.2017.
 */

public class BillingOverviewLabels extends AppLabels{

    public static String getBilling_overview_cost_control_card_title() {
        return getLabelWithPrimaryKey("billing_overview_cost_control_card_title", "Cost Control");
    }

    public static String getBilling_overview_previvios_bills_card_title() {
        return getLabelWithPrimaryKey("billing_overview_previvios_bills_card_title", "Facturile anterioare");
    }

    public static String getBilling_overview_average_bills_cost() {
        return getLabelWithPrimaryKey("billing_overview_average_bills_cost", "Media cheltuielilor tale \n în ultimele 6 luni");
    }

    public static String getBilling_overview_price_plan() {
        return getLabelWithPrimaryKey("billing_overview_price_plan", "Abonamentele tale");
    }

    public static String getBilling_overview_aditional_cost_average() {
        return getLabelWithPrimaryKey("billing_overview_aditional_cost_average", "Media costurilor suplimentare");
    }

    public static String getBilling_overview_current_date() {
        return getLabelWithPrimaryKey("billing_overview_current_date", "Actualizat la: ");
    }

    public static String getBilling_overview_bill_due_date() {
        return getLabelWithPrimaryKey("billing_overview_bill_due_date", "Scadență factură: ");
}

    public static String getBilling_overview_is_passed_due_date_message() {
        return getLabelWithPrimaryKey("billing_overview_is_passed_due_date_message", "Plătește la timp pentru a evita suspendarea serviciilor tale");
    }

    public static String getBilling_overview_last_bill_cycle_date() {
        return getLabelWithPrimaryKey("billing_overview_additional_cost", "Cost suplimentar de la");
    }
    public static String getBilling_overview_costcontrol_for_number() {
        return getLabelWithPrimaryKey("billing_overview_last_bill_cycle_date", " pentru numărul ");
    }

    public static String getBilling_overview_not_payed_bill() {
        return getLabelWithPrimaryKey("billing_overview_not_payed_bill", "Factură neplătită");
    }

    public static String getBilling_overview_bill_payed_on_date() {
        return getLabelWithPrimaryKey("billing_overview_bill_payed_on_date", "Plată efectuată pe");
    }

    public static String getBilling_overview_aditional_cost() {
        return getLabelWithPrimaryKey("billing_overview_aditional_cost", "Include: template cost suplimentar");
    }

    public static String getBilling_overview_total_bill_value() {
        return getLabelWithPrimaryKey("billing_overview_total_bill_value", "template (RON fără TVA)");
    }

    public static String getBilling_overview_price_plan_tab() {
        return getLabelWithPrimaryKey("billing_overview_price_plan_tab", "În abonament");
    }

    public static String getBilling_overview_extra_tab() {
        return getLabelWithPrimaryKey("billing_overview_extra_tab", "Suplimentar");
    }

    public static String getBilling_overview_total_benefits_tab() {
        return getLabelWithPrimaryKey("billing_overview_total_benefits_tab", "Total beneficii");
    }

    public static String getBilling_overview_discounts_tab() {
        return getLabelWithPrimaryKey("billing_overview_discounts_tab", "Reduceri");
    }

    public static String getBilling_overview_connection_fee_tab() {
        return getLabelWithPrimaryKey("billing_overview_connection_fee_tab", "Taxă conectare");
    }

    public static String getBilling_overview_other_charges_and_adjustments_tab() {
        return getLabelWithPrimaryKey("billing_overview_other_charges_and_adjustments_tab", "Alte taxe și ajustări");
    }

    public static String getBilling_overview_equipment_rates_vodafone_tab() {
        return getLabelWithPrimaryKey("billing_overview_equipment_rates_vodafone_tab", "Rate echipamente Vodafone");
    }

    public static String getBilling_overview_total_amount_without_VAT_tab() {
        return getLabelWithPrimaryKey("billing_overview_total_amount_without_VAT_tab", "Total abonat fără TVA");
    }

    public static String getBilling_overview_third_parties_amount_tab() {
        return getLabelWithPrimaryKey("billing_overview_third_parties_amount_tab", "Sume terți");
    }

    public static String getBilling_overview_cancelation_fee_tab() {
        return getLabelWithPrimaryKey("billing_overview_cancelation_fee_tab", "Taxă reziliere");
    }

    public static String getBilling_overview_cancelation_fee_adjustment_tab() {
        return getLabelWithPrimaryKey("billing_overview_cancelation_fee_adjustment_tab", "Ajustare taxă reziliere");
    }

    public static String getBilling_overview_euro_unit() {
        return getLabelWithPrimaryKey("billing_overview_euro_unit", "€");
    }

    public static String getBilling_overview_lei_unit() {
        return getLabelWithPrimaryKey("billing_overview_lei_unit", "LEI");
    }

    public static String getBilling_overview_ron_unit() {
        return getLabelWithPrimaryKey("billing_overview_ron_unit", "RON");
    }
    public static String getBilling_overview_SAYT() {
        return getLabelWithPrimaryKey("billing_overview_sayt_hint", "Caută un număr din contul tău");
    }
    public static String getBillingOverviewDownloadBillTitle() {
        return getLabelWithPrimaryKey("billing_overview_download_bill_title", "Apasă aici pentru a descărca factura");
    }
    public static String getBillingOverviewDownloadBillPrimaryButtonTitle() {
        return getLabelWithPrimaryKey("billing_overview_download_bill_primary_button_title", "Descarcă factura");
    }
    public static String getBillingOverviewDownloadBillSecondaryButtonTitle() {
        return getLabelWithPrimaryKey("billing_overview_download_bill_secondary_button_title", "Descarcă factura detaliată");
    }
    public static String getErrorInformationCanNotBeDisplayed() {
        return getLabelWithPrimaryKey("error_information_can_not_be_displayed_click_to_retry", "Aceste informații nu pot fi afișate.");
    }

    public static String getBillingBanHeaderTitle() {
        return getLabelWithPrimaryKey("billing_ban_header_title", "Factură emisă pentru contul");
    }
    public static String getBillingOverviewUnpaidTitle() {
        return getLabelWithPrimaryKey("billing_overview_unpaid_title", "Factură");
    }

    public static String getBillingOverviewExpiredBillMessage() {
        return getLabelWithPrimaryKey("billing_overview_expired_bill_message", "Detalierea Apelurilor pentru perioada selectată nu mai este disponibilă, pentru detalii te rugăm să iei legătura cu un operator");
    }

    public static String getBilling_overview_no_bill_history_title_message() {
        return getLabelWithPrimaryKey("billing_overview_no_bill_history_title_message", "Momentan nu există facturi emise pe cont");
    }

}
