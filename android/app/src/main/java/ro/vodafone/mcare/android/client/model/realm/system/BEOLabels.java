package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by Eliza Deaconescu on 06.04.2017.
 */

public class BEOLabels  extends  AppLabels{

    public static String getIncompatible_activation_offer(){
        return getLabelWithPrimaryKey("incompatible_activation_offer", "Activează opţiunea");
    }
    public static String getIncompatible_back_button_offer() {
        return getLabelWithPrimaryKey("incompatible_back_button_offer", "Anulează");
    }
    public static String geMonthly_programmed_recharge() {
        return getLabelWithPrimaryKey("monthly_programmed_recharge", "Lunar în ziua");
    }
    public static String getWeekly_programmed_recharge() {
        return getLabelWithPrimaryKey("weekly_programmed_recharge", "Reîncărcare săptămânală în ziua");
    }
    public static String getDate_programmed_recharge(){
        return getLabelWithPrimaryKey("date_programmed_recharge", "Reîncărcare la data");
    }
    public static String getBeo_no_offers_or_services(){
            return getLabelWithPrimaryKey("beo_no_offers_or_services","Momentan nu există opţiuni configurate pentru tine.");
}
   /* public static String getBeo_another_offers_and_services_active(){
        return getLabelWithPrimaryKey("beo_another_offers_and_services_active","Ai opţiuni în activare si nu poţi activa alte oferte");
    }*/

    public static String getBeo_another_offers_and_services_active(){
        return getLabelWithPrimaryKey("beo_another_offers_and_services_active","Ai opţiuni în activare și nu poţi activa alte oferte.");
    }
    public static String getBeo_activate_prepaid_offer_part1() {
        return getLabelWithPrimaryKey("beo_activate_prepaid_offer_part1", "Ai activat cu succes extraopțiunea %1$s!");
    }
    public static String getBeo_activate_prepaid_offer_service_part1() {
        return getLabelWithPrimaryKey("beo_activate_prepaid_offer_service_part1", "Ai activat cu succes serviciul %1$s!");
    }
    public static String getBeo_activate_prepaid_offer_part2() {
        return getLabelWithPrimaryKey(" beo_activate_prepaid_offer_part2", "Extraopțiunea a fost activată!");
    }
    public static String getBeo_activate_prepaid_offer_service_part2(){
        return getLabelWithPrimaryKey("beo_now_success_activation","Serviciul a fost activat!");
    }

    public static String getBeo_now_success_activation(){
            return getLabelWithPrimaryKey("beo_now_success_activation","Extraopțiunea a fost activată!");
    }


    public static String getBeo_success_activation_next_billcycle(){
        return getLabelWithPrimaryKey("beo_success_activation_next_billcycle", "Extraopțiunea  %1$s se va activa cu succes la următoarea factură!");
    }

    public static String getBeo_success_services_activation_next_billCycle() {
        return getLabelWithPrimaryKey("beo_success_service_activation_next_billCycle", "Serviciul %1$s se va activa cu succes la următoarea factură!");
    }

    public static String getBeo_next_bill_cycle(){
        return getLabelWithPrimaryKey("beo_next_bill_cycle_success_activation","Extraopțiunea va fi activată!");
    }

    public static String getBeo_service_next_billCycle(){
        return getLabelWithPrimaryKey("beo_service_next_billCycle", "Serviciul va fi activat!");
    }
    public static String getBeoOffersNotAvailableMessage(){
        return getLabelWithPrimaryKey("beo_offers_not_available_message", "Oferta nu mai este disponibilă pentru activare");
    }

    public static String getBeo_page_tittle(){
        return getLabelWithPrimaryKey("beo_page_tittle","Bonusuri și Opțiuni");
    }
    public static String getBeo_price_label() {
        return getLabelWithPrimaryKey("beo_price_label", "Preț: ");
    }
    public static String getBeo_period_label() {
        return getLabelWithPrimaryKey("beo_price_label", "/lună");
    }
    public static String getExtra_category_tittle() {
        return getLabelWithPrimaryKey("extra_category_tittle","Extraopțiunile mele");
    }
    public static String getActivate_offer_text() {
        return getLabelWithPrimaryKey("activate_offer_text","Activează");
    }
    public static String getOffer_details_tarifate_tittle(){
            return getLabelWithPrimaryKey("offer_details_tarifate_tittle", "Informatii tarife");
    }
    public static String getActivation_back_text(){
     return getLabelWithPrimaryKey("activation_back_text","Înapoi");
    }
    public static String getActivation_continue_text() {
        return getLabelWithPrimaryKey("activation_continue_text", "Continuă");
    }
    public static String getActivation_bank_card_text(){
        return getLabelWithPrimaryKey("activation_bank_card_text", "Card bancar");
    }
    public static String getPay_way_credit_and_card(){
       return getLabelWithPrimaryKey("pay_way_credit_and_card","Credit disponibil + Card Bancar");
}
    public static String getCredit_in_avans_content_text(){
       return getLabelWithPrimaryKey("credit_in_avans_content_text", "Pe acest număr s-a primit credit în avans care nu a fost returnat. Oferta solicitată nu se va activa! Te rugăm să efectuezi o reîncarcare și să reiei procesul de activare ofertă.");
    }
    public static String getActivation_avans_credit_tittle_text(){
     return getLabelWithPrimaryKey("activation_avans_credit_tittle_text", "Credit în avans");
    }
    public static String getConfirm_activation_tittle_text(){
       return getLabelWithPrimaryKey("confirm_activation_tittle_text","Ești sigur?");
    }
    public static String getConfirm_activation_content_text_first_part() {
        return getLabelWithPrimaryKey("confirm_activation_content_text_first_part", "Confirmă că doreşti să cumperi opţiunea pentru");
    }
    public static String getConfirm_activation_content_text_second_part(){
    return getLabelWithPrimaryKey("confirm_activation_content_text_second_part","\u00A0€. Aceasta va fi disponibilă în scurt timp.");
    }
    public static String getPay_way_tittle(){
    return getLabelWithPrimaryKey(" pay_way_tittle","Alege metoda de plată");
    }
    public static String getPay_way_activate_offer_first_part(){
      return getLabelWithPrimaryKey("pay_way_activate_offer_first_part", "Activează această ofertă cu ");
    }
    public static String getPay_way_activate_offer_second_part(){
       return getLabelWithPrimaryKey("pay_way_activate_offer_second_part", "€ pe luna. Selectează metoda de plată.");
    }
    public static String getPending_extraoption_tittle(){
       return getLabelWithPrimaryKey(" pending_extraoption_tittle","Extraopțiuni în așteptare");
    }
    public static String getPending_extraoption_content(){
    return getLabelWithPrimaryKey(" pending_extraoption_content", "Ai pe cont o opţiune care se va reînnoi automat la prima reîncărcare. Dacă faci reîncărcarea acum, opţiunea existentă pe cont se va reînnoi.");
    }
    public static String getIncompatible_activation_content_text() {
        return getLabelWithPrimaryKey("incompatible_activation_content_text","Opţiunea nu a fost activată pentru că nu este compatibilă cu abonamentul tău.Pentru mai multe detalii te rugăm să iei legătura cu noi pe chat.) ");
    }
    public static String getImedite_activation_content_text_first_part() {
            return getLabelWithPrimaryKey("imedite_activation_content_text_first_part", "Confirmă că doreşti să cumperi opţiunea pentru");
        }
    public static String getImedite_activation_content_text_second_part() {
        return getLabelWithPrimaryKey(" imedite_activation_content_text_second_part", " € pe lună. Aceasta va fi disponibilă în scurt timp.");
    }
    public static String getNext_bill_activation_content_text_first_part(){
     return getLabelWithPrimaryKey("next_bill_activation_content_text_first_part","Confirmă că doreşti să cumperi opţiunea pentru");
    }
    public static String getNext_bill_activation_content_text_second_part(){
     return getLabelWithPrimaryKey("next_bill_activation_content_text_second_part"," € pe lună. Aceasta va fi disponibilă începând cu următoarea dată de facturare.");
    }
    public static String getEtf_applied_activation_tittle(){
     return getLabelWithPrimaryKey("etf_applied_activation_tittle", "Activare opţiune");
             }

    public static String getEtf_applied_activation_content(){
     return getLabelWithPrimaryKey("etf_applied_activation_content", "Dezactivarea anticipată a acestei opţiuni presupune costuri suplimentare pe următoarea factură. Pentru mai multe informaţii te rugăm să iei legătura cu noi pe chat.");
             }
    public static String getIncompatible_activation_tittle(){
     return getLabelWithPrimaryKey("incompatible_activation_tittle", "Activare opţiune");
             }
    public static String getActivation_request_options(){
    return getLabelWithPrimaryKey(" activation_request_options","Ai cerut activarea opţiunii:");
            }
    public static String getCurrent_options_text(){
    return getLabelWithPrimaryKey("current_options_text","Dar deja ai:");
            }
            public static String getImmediate_incompatible_conflict_text_label(){
    return getLabelWithPrimaryKey(" immediate_incompatible_conflict_text_label", "Ne pare rău dar există un conflict între opţiuni. \n" +
            "\nEşti sigur că doreşti să continui? Noua opţiune le va înlocui pe cele active acum incompatibile. Modificările se vor reflecta în contul tău în maxim 24 de ore.");
            }
    public static String getNext_bill_incompatible_conflict_text_label() {
        return getLabelWithPrimaryKey("next_bill_incompatible_conflict_text_label", "Ne pare rău dar există un conflict între opţiuni.\n" +
                "\nEşti sigur că doreşti să continui? Noua opţiune le va înlocui pe cele active acum incompatibile. Modificările se vor reflecta în contul tău începând cu următoarea dată de facturare.");
    }
    public static String getContinue_button() {
        return getLabelWithPrimaryKey("continue_button","Continuă");
    }
    public static String getMonthly_programmed_recharge(){
        return getLabelWithPrimaryKey("monthly_programmed_recharge","Lunar în ziua");
    }

    public static String getEuro_amount_currency(){
        return getLabelWithPrimaryKey("euro_amount_currency", "%1$s €/lună ");
    }
    public static String getEuro_price_currency(){
        return getLabelWithPrimaryKey("euro_price_currency", "%1$s €");
    }

    //Ebu Labels
    public static String getCustomer_restriction_message(){
        return getLabelWithPrimaryKey("customer_restriction_message", "Ne pare rău, nu poţi accesa această pagină. Pentru mai multe detalii, contactează Departamentul de Relaţii şi clienţii.");
    }

    public static String getContact_account_specialist_message(){
        return getLabelWithPrimaryKey("contact_account_specialist_message", "Pentru activarea unei extraopţiuni te rugăm să contactezi specialistul de cont.");
    }

    public static String getGeneral_error_mesage_tap_to_refresh(){
        return getLabelWithPrimaryKey("general_error_mesage_tap_to_refresh", "Sistem momentan indisponibil. Apasă pentru a reîncerca.");
    }

    public static String getGeneral_error_mesage(){
        return getLabelWithPrimaryKey("general_error_mesage", "Sistem momentan indisponibil");
    }

    public static String getPending_orders_message(){
        return getLabelWithPrimaryKey("pending_orders_message", "Există deja o cerere în curs de procesare. Nu se pot efectua operațiuni pe număr până la finalizarea acesteia");
    }

    public static String getDuration_label(){
        return getLabelWithPrimaryKey("duration_label", "Durata:");
    }

    public static String getActivation_date_label(){
        return getLabelWithPrimaryKey("activation_date_label", "Data activare: %1$s");
    }

    public static String getDeactivation_date_label(){
        return getLabelWithPrimaryKey("deactivation_date_label", "Data dezactivare: %1$s");
    }

    public static String getImmediate_label (){
        return getLabelWithPrimaryKey("immediate_label", "Imediată");
    }
    public static String getRequested_date_label(){
        return getLabelWithPrimaryKey("on_demand_label", "La cerere");
    }
    public static String getDuration_in_month_label(){
        return getLabelWithPrimaryKey("duration_in_month_label", "În %1$s luni");
    }

    public static String getActivation_confirmation_label(){
        return getLabelWithPrimaryKey("activation_confirmation_label", "Confirmă că doreşti să cumperi opţiunea pentru %1$s € pe lună.");
    }

    public static String getEtf_overlay_title(){
        return getLabelWithPrimaryKey("etf_overlay_title", "Taxă de reziliere");
    }

    public static String getSubUser_etf_overlay_message(){
        return getLabelWithPrimaryKey("subUser_etf_overlay_message", "Nu eşti autorizat să modifici acest tip de extraopţiuni.");
    }

    public static String getEtf_overlay_message(){
        return getLabelWithPrimaryKey("etf_overlay_message", "Adăugarea extraopţiunii, va duce la ştergerea opţiunii %1$s şi va genera o taxă de reziliere în valoare de %2$s €.  Eşti de accord cu această modificare?");
    }

    public static String getEtf_multiple_options_overlay_message(){
        return getLabelWithPrimaryKey("etf_multiple_options_overlay_message", "Adăugarea extraopţiunii, va duce la ştergerea opţiunilor %1$s şi va genera o taxă de reziliere în valoare de %2$s €.  Eşti de accord cu această modificare?");
    }

    public static String getEtf_ok_button_text(){
        return getLabelWithPrimaryKey("etf_ok_button_text", "Da");
    }
    public static String getEtf_dismiss_button_text(){
        return getLabelWithPrimaryKey("etf_dismiss_button_text", "Nu");
    }
    public static String getSubUser_deactivate_offer_success_vov_message(){
        return getLabelWithPrimaryKey("subUser_deactivate_offer_success_vov_message", "Solicitarea ta a fost trimisă spre aprobare către administratorul contului.");
    }
    public static String getActivate_option_success_vov_message(){
        return getLabelWithPrimaryKey("activate_option_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. Opţiunea %1$s va fi activată! ");
    }
    public static String getActivate_service_success_vov_message(){
        return getLabelWithPrimaryKey("activate_service_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. Serviciul %1$s va fi activat! ");
    }
    public static String getActivate_option_success_vov_message_delayed(){
        return getLabelWithPrimaryKey("activate_option_success_vov_message_delayed", "Solicitarea ta a fost trimisă spre procesare. Opţiunea %1$s va fi activată! ");
    }
    public static String getActivate_service_success_vov_message_delayed(){
        return getLabelWithPrimaryKey("activate_service_success_vov_message_delayed", "Solicitarea ta a fost trimisă spre procesare. Serviciul %1$s va fi activat! ");
    }
    public static String getSend_request_confirmation_vov_message(){
        return getLabelWithPrimaryKey("send_request_confirmation_vov_message", "Solicitarea ta a fost preluată. Revenim cu o confirmare.");
    }
    public static String getDeactivate_offer_vov_button_message(){
        return getLabelWithPrimaryKey("deactivate_offer_vov_button_message", "Ok, am înţeles");
    }
    public static String getOffer_activation_success_toast_message(){
        return getLabelWithPrimaryKey("offer_deactivation_success_toast_message", "Solicitarea a fost trimisă!");
    }

    public static String getService_activation_failed_vov_message(){
        return getLabelWithPrimaryKey("service_activation_failed_vov_message", "Serviciul %1$s nu a fost activat. Vă rugăm reîncercaţi");
    }

    public static String getOption_activation_failed_vov_message(){
        return getLabelWithPrimaryKey("option_activation_failed_vov_message", "Opţiunea %1$s nu a fost activată. Vă rugăm reîncercaţi");
    }

    public static String getNot_eligibile_for_offer_activation_message(){
        return getLabelWithPrimaryKey("not_eligibile_for_offer_activation", "Oferta nu este disponibilă pentru activare");
    }

    public static String getNot_eligibile_for_service_activation_message(){
        return getLabelWithPrimaryKey("not_eligibile_for_service_activation", "Serviciul nu este disponibil pentru activare");
    }
}
