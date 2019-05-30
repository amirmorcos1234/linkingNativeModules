package ro.vodafone.mcare.android.ui.fragments.profile;

import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;

/**
 * Created by Alex on 5/24/2017.
 */

public class ConfirmProfileLabels  extends AppLabels {

    public static String getConfirmProfileLastStepTittleLabel() {
        return getLabelWithPrimaryKey("confirm_profile_last_step_tittle_label", "Te rugăm să introduci codul de activare primit prin SMS");
    }

    public static String getConfirmProfileTermsAndConditionsCheckboxLabel() {
        return getLabelWithPrimaryKey("confirm_profile_terms_and_conditions_checkbox_label", "Sunt de acord cu Termenii și Condițiile");
    }

    public static String getConfirmProfileErrorMessageInvalidPhoneNumber() {
        return getLabelWithPrimaryKey("confirm_profile_error_message_invalid_phone_number", "Formatul numărului introdus nu este valid (ex. 07XXXXXXX)");
    }

    public static String getConfirmProfileFirstStepPageTittleLabel() {
        return getLabelWithPrimaryKey("confirm_profile_first_step_page_tittle_label", "Avem nevoie de datele tale pentru a-ți putea confirma profilul. Mai multe.");
    }

    public static String getConfirmProfileTittleLabel() {
        return getLabelWithPrimaryKey("confirm_profile_tittle_label", "Confirmare profil");
    }

    public static String getConfirmProfileDialogTbdProfileMessage() {
        return getLabelWithPrimaryKey("confirm_profile_dialog_tbd_profile_message", "Confirmarea profilului este necesară pentru actualizarea informațiilor contului tău și se efectuează o singură dată.");
    }

    public static String getConfirmProfileDialogDositiv() {
        return getLabelWithPrimaryKey("confirm_profile_dialog_positive", "Renunță");

    }

    public static String getConfirmProfileDialogNegative(){
        return getLabelWithPrimaryKey("confirm_profile_dialog_negative","Continuă");
    }

    public static String getConfirmProfileDialogLogoutMessage(){
        return getLabelWithPrimaryKey("confirm_profile_dialog_logout_message","Ești sigur că vrei să te deconectezi?");
    }

    public static String getConfirmProfileDialogLogoutPositive(){
        return getLabelWithPrimaryKey("confirm_profile_dialog_logout_positive","Confirmă");
    }

    public static String getConfirmProfileDialogLogoutNegative(){
        return getLabelWithPrimaryKey("confirm_profile_dialog_logout_negative","Anulează");
    }

    public static String getConfirmProfileTelephoneNumberHint(){
        return getLabelWithPrimaryKey("confirm_profile_telephone_number_hint","0xxxxxxxxx");
    }

    public static String getConfirmProfileActivationCodeLabel(){
        return getLabelWithPrimaryKey("confirm_profile_activation_code_label","Cod unic");
    }

    public static String getConfirmProfileActivationCodeLabel2(){
        return getLabelWithPrimaryKey("confirm_profile_activation_code_label2","Cod");
    }

    public static String getConfirmProfileActivationCodeHint(){
        return getLabelWithPrimaryKey("confirm_profile_activation_code_hint","cod");
    }

    public static String getConfirmProfileSecondStepPageTittleLabel(){
        return getLabelWithPrimaryKey("confirm_profile_second_step_page_tittle_label","Te rugăm să introduci codul de activare primit prin SMS.");
    }

    public static String getConfirmProfileActivateProfile(){
        return getLabelWithPrimaryKey("confirm_profile_activate_profile","Activează contul");
    }

   public static String getConfirmProfileEnteredEmailNotEqualWithProfilMail(){
       return getLabelWithPrimaryKey("confirm_profile_entered_email_not_equal_with_profil_mail","Adresa de email nu corespunde cu cea din profil.");
   }

   public static String getConfirmProfilePleaseContactAdministrator(){
       return getLabelWithPrimaryKey("confirm_profile_please_contact_administrator","Profilul nu poate fi reconfirmat. Te rugăm să contactezi administratorul contului!");
   }

   public static String getConfirmProfilePleaseContactClientRealitonalService(){
       return getLabelWithPrimaryKey("confirm_profile_please_contact_client_realitonal_service","Profilul nu poate fi reconfirmat. Te rugăm să contactezi serviciul de relații cu clienții");
   }

   public static String getConfirmProfileAccountAlreadyExistWithThisNumber(){
       return getLabelWithPrimaryKey("confirm_profile_account_already_exist_with_this_number","Există deja un cont activ pentru acest număr de telefon.");
   }

   public static String getConfirmProfileAccountApiCallCodeSendFailed(){
       return getLabelWithPrimaryKey("confirm_profile_account_api_call_code_send_failed","Codul de activare nu a putut fi trimis. Te rugăm să revii mai târziu.");
   }


}