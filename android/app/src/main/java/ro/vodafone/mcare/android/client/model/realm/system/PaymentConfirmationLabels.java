package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 9/5/2017.
 */

public class PaymentConfirmationLabels extends AppLabels {

    public static String getPaymentConfirmationTitle(){
        return getLabelWithPrimaryKey("payment_confirmation_title","Confirmare plată");
    }

    public static String getNoEligibleMessage(){
        return getLabelWithPrimaryKey("no_eligible_message","Ne pare rău, nu sunteți eligibil pentru confirmarea plății facturii în acastă lună");
    }

    public static String getIneligibleRoleMessage(){
        return getLabelWithPrimaryKey("ineligile_role","Nu ești eligibil pentru confirmarea plății facturii");
    }

    public static String getMessageCardText(){
        return getLabelWithPrimaryKey("message_card_text","Pentru confirmare plată, vă rugăm să contactaţi specialistul dvs de plăţi, program de lucru L-V, 09:00-18:00.");
    }

    public static String getSuccessPaymentToastMessage(){
       return getLabelWithPrimaryKey("success_payment_toast_message","Plată înregistrată cu succes!");
    }

    public static String getPaymentConfirmationVoV(){
        return getLabelWithPrimaryKey("payment_confirmation_vov", "Plata facturii tale a fost înregistrată.");
    }

    public static String getPaymentConfirmationVoVButton(){
        return getLabelWithPrimaryKey("payment_confirmaton_vov_button", "Ok, am înțeles.");
    }

    public static String getPaymentConfirmationErrorMessage(){
        return getLabelWithPrimaryKey("payment_confirmation_error_message","Sistem momentan indisponibil");
    }

    public static String getNotConfirmedErrorMessage(){
        return  getLabelWithPrimaryKey("not_confirmed","Ne pare rau, plata nu a putut fi confirmata");
    }

    public static String getConfirmDialogTitle(){
        return getLabelWithPrimaryKey("confirm_dialog_title","Ești sigur?");
    }

    public static String getDialogMessageFirstPart(){
        return getLabelWithPrimaryKey("dialog_message_first_part","Ai solicitat confirmarea plății a ");
    }

    public static String getDialogMessageSecondPart(){
        return getLabelWithPrimaryKey("dialog_message_second_part"," RON.");
    }

    public static String getPaymentConfirmationConfirmButton(){
        return getLabelWithPrimaryKey("payment_confirmation_confirm_button","Confirmă");
    }

    public static String getPaymentConfirmationCancelButton(){
        return getLabelWithPrimaryKey("payment_confirmation_confirm_button","Renunță");
    }

    public static String getGeneralErrorMessageWithRetry() {
        return getLabelWithPrimaryKey("general_error_message_with_retry","Serviciu momentan indisponibil. Apasă pentru a reîncerca.");
    }

    public static String getPaymentConfirmationAmountError() {
        return getLabelWithPrimaryKey("payment_confirmation_amount_error","Te rugăm să introduci o valoare mai mare sau egală cu 5, având maxim 2 zecimale.");
    }

    public static String getPaymentConfirmationCodeError() {
        return getLabelWithPrimaryKey("payment_confirmation_code_error","Codul de operație nu este corect. Te rugăm să contactezi serviciul clienți la *222");
    }

    public static String getPaymentConfirmationContactError() {
        return getLabelWithPrimaryKey("payment_confirmation_contact_error","Formatul numărului introdus nu este valid (ex. 07XXXXXXXX)");
    }

}
