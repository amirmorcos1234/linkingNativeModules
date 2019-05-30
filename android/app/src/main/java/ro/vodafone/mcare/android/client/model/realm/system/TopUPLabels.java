package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user on 06.04.2017.
 */

public class TopUPLabels extends AppLabels {

    public static String getTop_up_recharge_another_number() {
        return getLabelWithPrimaryKey("Top_up_recharge_another_number", "Reîncarcă numărul unui prieten");
    }

    public static String getTop_up_page_title() {
        return getLabelWithPrimaryKey("top_up_page_title", "Reîncarcă");
    }

    public static String getTop_up_recharge_own_number() {
        return getLabelWithPrimaryKey("top_up_recharge_own_number", "Reîncarcă numărul tău");
    }

    public static String getTop_up_open_recharge_history_prepaid() {
        return getLabelWithPrimaryKey("top_up_open_recharge_history_prepaid", "Vizualizează istoricul tranzacțiilor");
    }

    public static String getTop_up_open_recharge_history_postpaid() {
        return getLabelWithPrimaryKey("top_up_open_recharge_history_postpaid", "Vizualizează istoricul reîncărcărilor");
    }

    public static String getTop_up_recurent_recharge() {
        return getLabelWithPrimaryKey("top_up_recurent_recharge", "Reîncărcări recurente");
    }

    public static String getTop_up_recharge_with_card() {
        return getLabelWithPrimaryKey("top_up_recharge_with_card", "Card bancar, cod reîncărcare");
    }

    public static String getTop_up_see_recharge_history() {
        return getLabelWithPrimaryKey("top_up_see_recharge_history", "Vezi reîncărcările pe care le-ai făcut de pe acest număr");
    }

    public static String getTop_up_open_modify_recharge() {
        // return getLabelWithPrimaryKey("top_up_open_modify_recharge_postpaid", "Vizualizează și modifică reîncărcările recurente");
        return getLabelWithPrimaryKey("top_up_open_modify_recharge_postpaid", " ");
    }

    public static String getTop_up_recharge_card_code_bill() {
        return getLabelWithPrimaryKey("top_up_recharge_card_code_bill", "Card bancar, cod reîncărcare, pe factură");
    }

    public static String getTop_up_see_recharge_history_prepaid() {
        return getLabelWithPrimaryKey("top_up_see_recharge_history_prepaid", "Vezi tranzacțiile pe care le-ai făcut de pe acest număr");
    }

    public static String getTop_up_phone_number_hint() {
        return getLabelWithPrimaryKey("top_up_phone_number_hint", "07XXXXXXXX");
    }

    public static String getTop_up_credit_validity_time() {
        return getLabelWithPrimaryKey("top_up_credit_validity_time", "Valabilitate credit ");
    }

    public static String getTop_up_days() {
        return getLabelWithPrimaryKey("getTop_up_days", "zile");
    }

    public static String getTop_up_your_email_adress() {
        return getLabelWithPrimaryKey("top_up_your_email_adress", "Adresa ta de email:");
    }

    public static String getTop_up_pay_with_card() {
        return getLabelWithPrimaryKey("top_up_pay_with_card", "Plătește cu cardul");
    }

    public static String getTop_up_pay_with_voucher() {
        return getLabelWithPrimaryKey("top_up_pay_with_voucher", "Reîncarcă cu voucher");
    }

    public static String getTop_up_pay_with_invoice() {
        return getLabelWithPrimaryKey("top_up_pay_with_invoice", "Plată pe factură");
    }

    public static String getTop_up_select_favorite_number() {
        return getLabelWithPrimaryKey("top_up_select_favorite_number", "Selectează un număr favorit");
    }

    public static String getTop_up_recharge_button_label() {
        return getLabelWithPrimaryKey("top_up_recharge_button_label", "Reîncarcă");
    }

    public static String getTop_up_other_value() {
        return getLabelWithPrimaryKey("top_up_other_value", "Alta");
    }

    public static String getTop_up_history_no_results() {
        return getLabelWithPrimaryKey("top_up_history_no_results", "Momentan nu există reîncărcări efectuate");
    }

    public static String getTop_up_selected_ban_eligible() {
        return getLabelWithPrimaryKey("top_up_selected_ban_eligible", "Plata reîncărcării va figura pe factura emisă la %1$s \n\nPoți reîncărca maxim %2$s € într-o lună de facturare. Valoarea reîncărcarilor de la ultima factură este %3$s €. Plata se va face pe factura contului de client selectat.");
    }

    public static String getTop_up_immediate_tab_title() {
        return getLabelWithPrimaryKey("top_up_immediate_tab_title", "Imediată");
    }

    public static String getTop_up_programed_tab_title() {
        return getLabelWithPrimaryKey("top_up_programed_tab_title", "Programată");
    }

    public static String getTop_up_delete_favorite_number_popup_message() {
        return getLabelWithPrimaryKey("top_up_delete_favorite_number_popup_message", "Ești sigur că dorești să ștergi acest număr din lista de favorite?");
    }

    public static String getTop_up_popup_positive_message() {
        return getLabelWithPrimaryKey("top_up_popup_positive_message", "Da");
    }

    public static String getTop_up_popup_negative_message() {
        return getLabelWithPrimaryKey("top_up_popup_negative_message", "Nu");
    }

    public static String getTop_up_voucher_hint() {
        return getLabelWithPrimaryKey("top_up_voucher_hint", "Cod voucher");
    }
    /*
     *
     * Top Up Page Error Messages
     *
     */

    public static String getTop_up_invalid_recharge_value() {
        return getLabelWithPrimaryKey("top_up_invalid_recharge_value", "Valoarea de reîncărcare trebuie să fie un număr întreg, cuprins între 4-100");
    }

    public static String getTop_up_error_min_value() {
        return getLabelWithPrimaryKey("top_up_error_min_value", "Valoarea de reîncărcare trebuie să fie un număr întreg, cuprins între {min_value}-100");
    }

    public static String getTop_up_invalid_email() {
        return getLabelWithPrimaryKey("top_up_invalid_email", "Te rugăm să introduci un email valid!");
    }

    public static String getTop_up_invalid_voucher() {
        return getLabelWithPrimaryKey("top_up_invalid_voucher", "Voucherul este invalid");
    }

    public static String getTop_up_expired_voucher() {
        return getLabelWithPrimaryKey("top_up_expired_voucher", "Voucherul este expirat");
    }

    public static String getTop_up_used_voucher() {
        return getLabelWithPrimaryKey("top_up_used_voucher", "Voucherul a fost folosit deja");
    }

    public static String getTop_up_user_is_not_eligible_for_post4pre() {
        return getLabelWithPrimaryKey("top_up_user_is_not_eligible_for_post4pre", "Momentan nu ești eligibil pentru plata pe factură. Pentru mai multe informații te rugăm să contactezi departamentul Relații Clienți.");
    }

    public static String getTop_up_user_is_not_eligible_first_part() {
        return getLabelWithPrimaryKey("top_up_user_not_eligible_for_p4p_recurrent_first_part", "Momentan nu ești eligibil pentru plata pe factură.");
    }

    public static String getTop_up_user_is_not_eligible_second_part() {
        return getLabelWithPrimaryKey("top_up_user_is_not_eligible_for_p4p_recurrent_second_part", "Pentru mai multe informații te rugăm să contactezi departamentul Relații Clienți.");
    }

    public static String getTop_up_msisdn_not_eligible_programmed() {
        return getLabelWithPrimaryKey("top_up_msisdn_not_eligible_programmed", "Momentan nu poți efectua reîncărcări cu plata pe factură.");
    }

    public static String getTop_up_msisdn_is_not_eligible_for_post4pre() {
        return getLabelWithPrimaryKey("top_up_msisdn_is_not_eligible_for_post4pre", "Momentan nu poți efectua reîncărcări cu plata pe factură. Poți reîncărca numărul dorit, alegând plata cu card bancar.");
    }

    public static String getTop_up_user_is_not_eligible_for_post4pre_due_to_max() {
        return getLabelWithPrimaryKey("top_up_user_is_not_eligible_for_post4pre_due_to_max", "Ai depășit numărul maxim al reîncărcărilor programate permise într-o lună de facturare. Poți reîncărca numărul dorit, alegând plata cu card bancar.");
    }

    public static String getTop_up_user_is_not_eligible_due_to_max_programmed() {
        return getLabelWithPrimaryKey("top_up_user_is_not_eligible_due_to_max_programmed", "Ai depășit numărul maxim al reîncărcărilor programate permise într-o lună de facturare.");
    }

    public static String getTop_up_api_call_fail() {
        return getLabelWithPrimaryKey("top_up_api_call_fail", "Serviciu momentan indisponibil");
    }

    public static String getTop_up_account_age_insufficient() {
        return getLabelWithPrimaryKey("top_up_account_age_insufficient", "Momentan nu poți efectua plăți pe factură.");
    }

    public static String getTop_up_restriction_or_barring_offer() {
        return getLabelWithPrimaryKey("top_up_restriction_or_barring_offer", "Momentan nu poți efectua plăți pe factură.");
    }

    public static String getTop_up_max_recharge_value_error() {
        return getLabelWithPrimaryKey("top_up_max_recharge_value_error", "Valoarea maximă de reîncărcare cu plata pe factură este de maxim %1$s €.");
    }

    /*
     *
     * Top Up confirmation overlay labels
     *
     */
    public static String getTop_up_confirmation_title() {
        return getLabelWithPrimaryKey("top_up_confirmation_title", "Confirmare");
    }

    public static String getTop_up_confirmation_context() {
        return getLabelWithPrimaryKey("top_up_confirmation_context", "Vei încărca numărul %1$s cu suma de %2$s €.");
    }

    public static String getTop_up_weekly_programmed_confirmation_context() {
        return getLabelWithPrimaryKey("top_up_weekly_programmed_confirmation_context", "Vei reîncărca numărul %1$s cu suma de %2$s €, programat săptămânal în ziua de %3$s. ");
    }

    public static String getTop_up_montly_programmed_confirmation_context() {
        return getLabelWithPrimaryKey("top_up_monthly_programmed_confirmation_context", "Vei reîncărca numărul %1$s cu suma de %2$s €, programat lunar în data de %3$s. ");
    }

    public static String getTop_up_date_programmed_confirmation_context() {
        return getLabelWithPrimaryKey("top_up_date_programmed_confirmation_context", "Vei reîncărca numărul %1$s cu suma de %2$s €, la data de %3$s. ");
    }

    public static String getTop_up_email_adress_hint() {
        return getLabelWithPrimaryKey("top_up_email_adress_hint", "nume@exemplu.com");
    }

    public static String getTopUpValueHint() {
        return getLabelWithPrimaryKey("top_up_value_hint", "Valoarea dorită");
    }

    public static String getTop_up_recharge_value() {
        return getLabelWithPrimaryKey("top_up_recharge_value ", " Valoare reîncărcare:");
    }

    public static String getTop_up_invalid_msisdn() {
        return getLabelWithPrimaryKey("top_up_invalid_msisdn", "Numărul introdus nu poate fi reîncărcat. Te rugăm să introduci un alt număr de telefon, în format național, (de exemplu: 0722000000). Se pot efectua reîncărcări doar pe Cartelă Vodafone, Cartelă Internet sau pe abonamentul Vodafone 2in1.");

    }

    /// trebuie corectat
    public static String getTop_up_invalid_voucher_input() {
        return getLabelWithPrimaryKey("top_up_invalid_voucher_input", "Te rugăm să introduci un Voucher valid.");

    }

    public static String getTop_up_successfull_toast_message() {
        return getLabelWithPrimaryKey("top_up_successfull_toast_message", "Ai reîncărcat cu succes. Balanța va fi actualizată în cel mai scurt timp.");
    }

    public static String getTop_up_confirmation_modify_button() {
        return getLabelWithPrimaryKey("top_up_confirmation_modify_button", "Modifică");
    }

    public static String getTop_up_pay_bill_message() {
        return getLabelWithPrimaryKey("top_up_pay_bill_message", "Plata reîncărcării va figura pe factura emisă la %1$s. ");
    }

    public static String getTop_up_next_bill_message() {
        return getLabelWithPrimaryKey("top_up_next_bill_message", "Plata reîncărcării va figura pe factura următoare.");
    }

    public static String getTop_up_x_date_recharges_avalaible() {
        return getLabelWithPrimaryKey("top_up_x_date_recharges_avalaible", "Poți efectua maxim %1$s reîncărcări programate la o anumită dată într-o lună de facturare. Mai ai disponibile %2$s.");
    }

    public static String getTop_up_x_month_recharges_available() {
        return getLabelWithPrimaryKey("top_up_x_month_recharges_available", "Poți să programezi maxim %1$s reîncărcări cu recurența lunară într-o zi selectată de tine pe parcursul lunii de facturare. Mai ai disponibile %2$s.");
    }

    public static String getTop_up_x_weekly_recharges_available() {
        return getLabelWithPrimaryKey("top_up_x_weekly_recharges_available", "Poți să programezi maxim %1$s reîncărcări cu recurență săptămânală într-o zi selectată de tine pe parcursul lunii de facturare. Mai ai disponibile %2$s.");
    }

    public static String getTop_up_no_eligible_message() {
        return getLabelWithPrimaryKey("top_up_no_eligible_message", "Poți reîncărca numărul dorit, alegând plata cu card bancar.");
    }

    public static String getTop_up_tap_to_refresh() {
        return getLabelWithPrimaryKey("top_up_tap_to_refresh", "Apasă pentru a reîncărca.");
    }

    public static String getTop_up_recurrent_positive_delete_toast() {
        return getLabelWithPrimaryKey("top_up_recurrent_positive_delete_toast", "Reîncărcarea programată a fost ștearsă.");
    }

    public static String getTop_up_recurrent_negative_delete_toast() {
        return getLabelWithPrimaryKey("top_up_negative_delete_toast", "Reîncărcarea programată nu a fost ștearsă. Te rugăm să încerci mai tîrziu.");
    }

    public static String getTopUp_history_recharged_msisdn() {
        return getLabelWithPrimaryKey("top_up_history_recharged_msisdn", "Reîncărcare pentru <b> %1$s </b>");
    }

    public static String getTop_up_history_channel() {
        return getLabelWithPrimaryKey("top_up_history_channel", "Canal: %1$s");
    }

    public static String getTop_up_history_payment_method() {
        return getLabelWithPrimaryKey("top_up_payment_method", "Metoda de plată: %1$s. ");
    }

    public static String getTop_up_add_favorites_success() {
        return getLabelWithPrimaryKey("top_up_add_favorites_success", "Numărul a fost adăugat cu success în Agenda de numere pentru tranzacții ulterioare!");
    }

    public static String getTop_up_add_favorites_failed() {
        return getLabelWithPrimaryKey("top_up_add_favorites_failed", "Numărul nu a putut fi adăugat în Agenda de numere pentru tranzacții ulterioare!");
    }

    public static String getTop_up_add_favorites_api_failed() {
        return getLabelWithPrimaryKey("top_up_add_favorites_api_failed", "Sistemul nu poate procesa cererea");
    }

    public static String getTop_up_immediate_recharge_vov_message() {
        return getLabelWithPrimaryKey("top_up_immediate_recharge_vov_message", "Ai reîncărcat numărul %1$s cu suma de %2$s €, doreşti să îl salvezi la numere favorite?");
    }

    public static String getTop_up_date_recharge_vov_message() {
        return getLabelWithPrimaryKey("top_up_date_recharge_vov_message", "Vei reîncărca numărul %1$s cu suma de %2$s € la data de %3$s, dorești să îl salvezi la numere favorite? ");
    }

    public static String getTop_up_weekly_recharge_vov_message() {
        return getLabelWithPrimaryKey("top_up_weekly_recharge_vov_message", "Vei reîncărca numărul %1$s cu suma de %2$s € programat săptămânal în ziua de %3$s, dorești să îl salvezi la numere favorite? ");
    }

    public static String getTop_up_monthly_recharge_vov_message() {
        return getLabelWithPrimaryKey("top_up_monthly_recharge_vov_message", "Vei încărca numărul %1$s cu suma de %2$s €, programat lunar în data de %3$s, dorești să îl salvezi la numere favorite? ");
    }

    public static String getTop_up_transfer_credit_Vov_succes_save_number() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_Vov_succes_save_number", "Ai transferat {x} euro credit pe numărul {07xxxxxxxx}, dorești să îl salvezi la numere favorite? ");
    }

    public static String getTop_up_transfer_credit_Vov_succes() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_Vov_succes" ,"Ai transferat {x} euro credit pe numărul {07xxxxxxxx}.");
    }

    public static String getTop_up_history_instructional_text() {
        return getLabelWithPrimaryKey("top_up_history_instructional_text", "Istoricul curent nu include costurile reîncărcărilor suplimentare. ");
    }

    public static String getTop_up_succes_recurrent_recharge() {
        return getLabelWithPrimaryKey("top_up_success_recurrent_recharge", "Reîncărcare programată înregistrată cu succes.");
    }

    public static String getTop_up_transfer_credit_page_title() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_page_title", "Transferă credit");
    }

    public static String getTop_up_transfer_credit_title_value_card() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_title_value_card", "Valoare transfer");
    }

    public static String getTop_up_transfer_credit_availability_value_card() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_availability_value_card" ,"Valabilitate credit %@ zile");
    }

    public static String getTop_up_transfer_credit_view_conditions_of_use() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_view_conditions_of_use", "Vizualizează condiţiile de utilizare");
    }

    public static String getTop_up_transfer_credit_overlay_title() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_overlay_title", "Eşti sigur?");
    }

    public static String getTop_up_transfer_credit_overlay_confirm_button() {
        return getLabelWithPrimaryKey("top_up_transfer_credit_overlay_confirm_button", "Confirmare");
    }

    public static String getTop_up_transfer_credit_transfer_button() {
        return getLabelWithPrimaryKey("top_up_transfer_credit_transfer_button", "Transferă");
    }

    public static String getTop_up_transfer_credit_overlay_back_button() {
        return getLabelWithPrimaryKey("top_up_transfer_credit_overlay_back_button", "Înapoi");
    }

    public static String getTop_up_transfer_credit_overlay_subtitle() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_overlay_subtitle", "Ai ales să transferi {x} euro credit către numărul {07xxxxxxxx}.\nDacă eşti de acord, alege Confirmare.");
    }

    public static String getTop_up_transfer_credit_title() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_title", "Transferă credit unui prieten");
    }

    public static String getTop_up_transfer_credit_subtitle() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_subtitle", "Poți transfera din creditul tău unui alt număr de Cartelă Vodafone");
    }

    public static String getTop_up_transfer_credit_error_1() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_1", "Stare SIM: BLOCAT");
    }

    public static String getTop_up_transfer_credit_error_2() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_2", "Pentru a folosi acest serviciu, numărul tău trebuie să fie în perioada de valabilitate a creditului.");
    }

    public static String getTop_up_transfer_credit_error_3() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_3", "În acest moment ai {x} Euro credit. Pentru a putea realiza transferul trebuie să îți rămână minim 0.5 Euro credit pe cont.");
    }

    public static String getTop_up_transfer_credit_error_wrong_recipient_msisdn() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_wrong_recipient_msisdn", "Numărul introdus nu este valid (ex. 07xxxxxxxx). Se pot efectua transferuri doar pe un număr de Cartelă Vodafone.");
    }

    public static String getTop_up_transfer_credit_error_same_msisdn_added() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_same_msisdn_added" ,"Numărul nu poate primi credit prin transfer. Alege alt număr.");
    }

    public static String getTop_up_transfer_credit_error_pending_transfer() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_error_pending_transfer", "Aveţi o cerere în curs de procesare, vă rugăm reveniţi.");
    }

    public static String getTop_up_transfer_credit_toast_succes_message() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_toast_succes_message", "Creditul a fost transferat pe numărul ales de tine.");
    }

    public static String getTop_up_transfer_credit_Vov_request_received() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_Vov_request_received", "Solicitarea ta a fost preluată. Revenim cu confirmare.");
    }

    public static String getTop_up_transfer_credit_Vov_request_fail() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_Vov_request_fail", "Transferul nu a fost efectuat. Vă rugăm să reîncercaţi.");
    }

    public static String getTop_up_transfer_credit_vov_mesaje_ok() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_vov_mesaje_ok", "Ok, am inteles");
    }

    public static String getTop_up_transfer_credit_use_condtions_json() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_use_terms_json", "<p>Numărul de telefon către care faci transferul primeşte valabilitate &icirc;n funcție de valoarea transferată:</p><ul><li>pentru 1 &euro; credit primeşte 5 zile de valabilitate</li><li>pentru 3 &euro; credit primeşte 10 zile de valabilitate</li><li>pentru 6 &euro; credit primeşte 20 zile de valabilitate</li></ul><p>Poți să transferi credit dacă:</p><ul><li>ai minim 90 de zile vechime &icirc;n rețea</li><li>ai re&icirc;ncărcat de minim 8 E credit de la activare</li><li>ești &icirc;n perioada de valabilitate a creditului</li><li>ai credit mai mare de 1,5 &euro;</li></ul><p>&Icirc;n fiecare lună poți transfera credit din contul tău de maximum 4 ori.</p><p>După efectuarea transferului trimitem un mesaj de confirmare pe numărul tău și pe numărul căruia ai ales să transferi.</p>");
    }

    public static String getTop_up_transfer_credit_toast_ttl_succes_message() {
        return getLabelWithPrimaryKey("getTop_up_transfer_credit_toast_ttl_succes_message" ,"Cererea ta a fost înregistrată.");
    }



}
