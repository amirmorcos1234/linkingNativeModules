package ro.vodafone.mcare.android.client.model.realm.system;

import static ro.vodafone.mcare.android.client.model.realm.system.AppLabels.getLabelWithPrimaryKey;

/**
 * Created by User on 11.07.2017.
 */

public class CreditInAdvanceLabels {

    public static String getTop_up_credit_in_advance(){
        return getLabelWithPrimaryKey("top_up_credit_in_advance", "Credit în avans");
    }

    public static String getTop_up_credit_in_advance_button_description(){
        return getLabelWithPrimaryKey("top_up_credit_in_advance_button_description","Poți primi credit în avans pe care îl vei returna la prima reîncărcare");
    }

    public static String getCredit_in_advance_instructional_text(){
        return getLabelWithPrimaryKey("credit_in_advance_instructional_text", "Primești credit în avans atunci când: ");
    }

    public static String getCredit_in_advance_first_dot_text(){
        return getLabelWithPrimaryKey("credit_in_advance_first_dot_text","numărul tău se află în perioada de valabilitate și creditul tău a scăzut sub 4,50 € credit;");
    }

    public static String getCredit_in_advance_second_dot_text(){
        return getLabelWithPrimaryKey("credit_in_advance_second_dot_text", "ai reîncărcat de minim 10.00 € de la activare.");
    }

    public static String getCredit_in_advance_accept_credit(){
        return getLabelWithPrimaryKey("credit_in_advance_accept_credit", "Vreau Credit");
    }

    public static String getCredit_in_advance_recharge_button_label(){
        return getLabelWithPrimaryKey("credit_in_advance_recharge_button_label", "Reîncarcă");
    }

    public static String getCredit_in_advance_general_not_eligible_message(){
        return getLabelWithPrimaryKey("credit_in_advance_general_not_eligible_message","Numărul tău nu îndeplinește condițiile pentru a primi credit în avans.");
    }

    public static String getCredit_in_advance_not_eligible_error_message(){
        return getLabelWithPrimaryKey("credit_in_advance_not_eligible_error_message", "Pe acest număr s-a primit credit în avans care nu a fost returnat. Creditul primit în avans se va retrage automat la prima reîncărcare împreună cu taxa aferentă.");
    }

    public static String get_ok_overlay_button(){
        return getLabelWithPrimaryKey("ok_overlay_button", "Da");
    }

    public static String getCredit_in_advance_overlay_title(){
        return getLabelWithPrimaryKey("credit_in_advance_overlay_title", "Ești sigur că dorești \ncredit în avans?");
    }

    public static String getCredit_in_advance_overlay_text(){
        return getLabelWithPrimaryKey("credit_in_advance_overlay_title", "Ai solicitat să primești credit în avans. Creditul primit în avans în valoare de %1$s € se va retrage automat la prima reîncărcare împreună cu o taxă de %2$s € credit.");
    }

    public static String getCredit_in_advance_success_toast(){
        return getLabelWithPrimaryKey("credit_in_advance_toast_error", "Vei primi creditul în avans solicitat în cel mai scurt timp!");
    }

    public static String getCredit_in_advance_vov_message(){
        return getLabelWithPrimaryKey("credit_in_advance_vov_message", "Ai primit creditul în avans solicitat. Creditul total se va actualiza în secțiunea Cost Control a aplicației în câteva minute.");
    }
}
