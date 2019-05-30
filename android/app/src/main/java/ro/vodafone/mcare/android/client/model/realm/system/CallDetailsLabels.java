package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by Eliza Deaconescu on 06.04.2017.
 */

public class CallDetailsLabels extends AppLabels {

    public static String getCall_details_reset_filters() {
        return getLabelWithPrimaryKey("call_details_reset_filters","Resetează filtrele");
    }
    public static String getCall_details_filter_destination_label() {
        return getLabelWithPrimaryKey("call_details_filter_destination_label","Destinație");
    }
    public static String getCall_details_filter_roaming_checkbox_label() {
        return getLabelWithPrimaryKey("call_details_filter_roaming_checkbox_label","Roaming");
    }
    public static String getCall_details_filter_international_checkbox_label() {
        return getLabelWithPrimaryKey("call_details_filter_international_checkbox_label","Internațional");
    }
    public static String getCall_details_filter_period_label() {
        return getLabelWithPrimaryKey("call_details_filter_period_label","Perioada");
    }
    public static String getCall_details_filter_spinner_label() {
        return getLabelWithPrimaryKey("call_details_filter_spinner_label","Selectează luna");
    }
    public static String getCall_details_filter_national_checkbox_label() {
        return getLabelWithPrimaryKey("call_details_filter_national_checkbox_labele","Național");
    }

    public static String getCall_details_filter_national_And_International_checkbox_label() {
        return getLabelWithPrimaryKey("call_details_filter_national_checkbox_labele","Național/Internațional");
    }

    public static String getCall_details_apply_filter_button_label() {
        return getLabelWithPrimaryKey("call_details_apply_filter_button_label","Aplică filtre");
    }
    public static String getCall_details_send_report_tittle_label(){
          return getLabelWithPrimaryKey("call_details_send_report_tittle_label","Trimite raportul pe email");
    }
    public static String getCall_details_select_report_type_label(){
        return getLabelWithPrimaryKey("call_details_select_report_type_label","Selectează formatul raportului");
    }
    public static String getCall_details_report_type_csv_label(){
        return getLabelWithPrimaryKey("call_details_report_type_csv_label","Format CSV");
    }
    public static String getCall_details_report_type_html_label(){
        return getLabelWithPrimaryKey("call_details_report_type_html_label","Format HTML");
    }
    public static String getCall_details_report_email_adress_hint() {
        return getLabelWithPrimaryKey ("call_details_report_email_adress_hint","Adresă de email");
    }
    public static String getCall_details_report_send_report_label() {
        return getLabelWithPrimaryKey ("call_details_report_send_report_label","Trimite raport");
    }
    public static String getCall_details_report_emil_error_message() {
        return getLabelWithPrimaryKey ("call_details_report_emil_error_message","Te rugăm să introduci un email valid!");
    }
    public static String getCall_details_no_elements_header_text() {
        return getLabelWithPrimaryKey ("call_details_no_elements_header_text","Momentan nu există rezultate pentru filtrarea făcută.");
    }
    public static String getCall_details_no_elements_content_text() {
        return getLabelWithPrimaryKey ("call_details_no_elements_content_text","Pentru o căutare mai relevantă folosiţi filtrele.");
    }
    public static String getCall_details_overflow_elements_header_text() {
        return getLabelWithPrimaryKey ("call_details_overflow_elements_header_text","Depășire limită înregistrări.");
    }
    public static String getCall_details_overflow_elements_content_text(){
        return getLabelWithPrimaryKey ("call_details_overflow_elements_content_text","Rezultatele tale depășesc limita de 1000 de înregistrări. Din acest motiv nu putem afişa rezultatele și este nevoie de trimiterea lor pe email pentru a le citi.");
    }
    public static String getCall_details_success_send_email_text(){
            return getLabelWithPrimaryKey ("call_details_success_send_email_text","Raportul a fost trimis la adresa indicată. Îl poţi accesa în următoarele 72 ore, ulterior link-ul expiră.");
    }
    public static String getCall_details_no_results_error_heder_text(){
            return getLabelWithPrimaryKey ("call_details_no_results_error_heder_text","Momentan nu ai facturi emise.\n Te rugăm să revii.");
    }
    public static String getCall_details_system_error_text(){
           return getLabelWithPrimaryKey ("call_details_system_error_text","Serviciu momentan indisponibil!");
    }

    public static String getSystemErrorText(){
        return getLabelWithPrimaryKey ("call_details_system_error_text","Serviciu momentan indisponibil! Contul nu a fost activat. Te rugăm să reîncerci!");
    }

    public static String getCall_details_system_error_text_system(){
        return getLabelWithPrimaryKey ("call_details_system_error_text_system","Serviciu momentan indisponibil. Apasă pentru a reîncerca.");
    }

    public static String getCall_details_activity_title() {
        return getLabelWithPrimaryKey("call_details_page_title", "Detalii apeluri");
    }
    public static String getCall_details_page_title() {
        return getLabelWithPrimaryKey("call_details_page_title", "Apeluri detaliate");
    }
    public static String getCall_details_calls_filter(){
        return getLabelWithPrimaryKey ("call_details_calls_filter","Apeluri");
    }
    public static String getCall_details_current_calls_details_button() {
        return getLabelWithPrimaryKey ("current_calls_details_button","Detalii apeluri curente");
    }
    public static String getCall_details_billed_calls_details_button(){
            return getLabelWithPrimaryKey ("illed_calls_details_button","Detalii apeluri facturate");
    }
    public static String getCall_unbilled_calls_card_title(){
        return getLabelWithPrimaryKey ("unbilled_calls_card_title", " Filtre factură detaliată");
    }
    public static String getCall_billed_calls_card_title(){
            return getLabelWithPrimaryKey ("billed_calls_card_title", "Filtre factură detaliată");
    }
    public static String getCall_prepaid_calls_card_title(){
        return getLabelWithPrimaryKey ("billed_calls_card_title", "Filtre apeluri detaliate");
    }
    public static String getCall_details_data_filter(){
        return getLabelWithPrimaryKey ("call_details_data_filter","Date");
    }
    public static String getCall_details_sms_filter() {
        return getLabelWithPrimaryKey("call_details_sms_filter", "SMS");
    }
    public static String getCall_details_the_others_filter() {
        return getLabelWithPrimaryKey("call_details_the_others_filter", "Altele");
    }
    public static String getCall_details_filter_button_label(){
        return getLabelWithPrimaryKey ("call_details_filter_button_label","Filtrează lista");
}
    public static String getCall_details_charged_calls(){
        return getLabelWithPrimaryKey ("call_details_charged_calls", "Tarifate");
    }
    public static String getCall_details_includet_calls(){
        return getLabelWithPrimaryKey ("call_details_includet_calls", "Incluse");
    }
    public static String getCall_details_not_found(){
        return getLabelWithPrimaryKey ("call_details_not_found", "Momentan nu există reziștate pentru filtrarea facută.");
    }
    public static String getCall_details_use_filters(){
        return getLabelWithPrimaryKey ("call_details_use_filters", "Pentru o căutare mai relevantă folosiți filtrele.");
    }
    public static String getCall_details_national_call(){
        return getLabelWithPrimaryKey ("call_details_national_call", "Apel național");
    }
    public static String getCall_details_roaming_call(){
        return getLabelWithPrimaryKey ("call_details_roaming_call", "Apel internațional");
    }
    public static String getCall_details_eur() {
        return getLabelWithPrimaryKey("call_details_eur", "EUR");
    }
    public static String getCurrent_calls_details_button() {
        return getLabelWithPrimaryKey("current_calls_details_button" ,"Detalii apeluri curente");
    }
    public static String getBilled_calls_details_button() {
        return getLabelWithPrimaryKey("billed_calls_details_button","Detalii apeluri facturate");
    }

    public static String getBilled_success_message(){
        return getLabelWithPrimaryKey("billed_success_message", "Plată înregistrată cu succes!");
    }

    public static String getBilled_success_message_vov(){
        return getLabelWithPrimaryKey("billed_success_message_vov", "Plata facturii tale a fost înregistrată.");
    }

    public static String getCallDetailsApplyFilterTitle(){
        return getLabelWithPrimaryKey("call_details_apply_filter_title","Filtrare");
    }
}
