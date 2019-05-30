package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 7/27/2017.
 */

public class RecoverLabels extends AppLabels {

    public static String getRecoverUsernamePageTitle(){
        return getLabelWithPrimaryKey("recover_username_page_title","Am uitat numele \\n de utilizator");
    }

    public static String getRecoverUsernameTelephoneNumberLabel(){
        return getLabelWithPrimaryKey("recover_username_telephone_number_label","Număr de telefon");
    }

    public static String getRecoverUsernameTelephoneNumberHint(){
        return getLabelWithPrimaryKey("recover_username_telephone_number_hint","0xxxxxxxxx");
    }

    public static String getRecoverUsernameEmailAdressLabel(){
        return getLabelWithPrimaryKey("recover_username_email_adress_label","Adresă de e-mail");
    }

    public static String getRecoverUsernameEmailAdressHint(){
        return getLabelWithPrimaryKey("recover_username_email_adress_hint","nume@exemplu.com");
    }

    public static String getRecoverUsernameButtonLabel(){
        return getLabelWithPrimaryKey("recover_username_button_label","Trimite numele de utilizator");
    }

    public static String getRecoverUsernameSuccesMessage(){
        return getLabelWithPrimaryKey("recover_username_succes_message","Numele de utilizator a fost trimis pe SMS și e-mail");
    }

    public static String getRecoverPasswordTelephoneNumberInvalidFormat(){
        return getLabelWithPrimaryKey("recover_password_telephone_number_invalid_format","Formatul numărului introdus nu este valid (ex. 0xxxxxxxxx)");
    }

    public static String getRecoverUsernameTelephoneNumberInvalidFormat(){
        return getLabelWithPrimaryKey("recover_username_telephone_number_invalid_format","Formatul numărului introdus nu este valid (ex. 0xxxxxxxxx)");
    }

    public static String getRecoverUsernameEmailInvalidFormat(){
        return getLabelWithPrimaryKey("recover_username_email_invalid_format","Te rugăm să introduci un email valid!");
    }

    public static String getRecoverUsernameNewPasswordAndConfirmPasswordDoNotMatch(){
        return getLabelWithPrimaryKey("recover_username_new_password_and_confirm_password_do_not_match","Câmpurile nu coincid.");
    }

    public static String getRecoverUsernameFailedApiCall(){
        return getLabelWithPrimaryKey("recover_username_failed_api_call","Serviciu momentan indisponibil!");
    }

    public static String getRecoverUsernameEbuMigratedAccountMsisdnAndEmail(){
        return getLabelWithPrimaryKey("recover_username_ebu_migrated_account_msisdn_and_email","Numele de utilizator nu poate fi recuperat. Te rugăm să contactezi administratorul contului!");
    }

    public static String getRecoverUsernameEnteredEmailNotEqualWithProfilMail(){
        return getLabelWithPrimaryKey("recover_username_entered_email_not_equal_with_profil_mail","Adresa de email nu corespunde cu cea din profil.");
    }

    public static String getRecoverPasswordPageTitle(){
        return getLabelWithPrimaryKey("recover_password_page_title","Am uitat parola");
    }

    public static String getRecoverPasswordPageDescriptionStepA(){
        return getLabelWithPrimaryKey("recover_password_page_description","Te rugăm să introduci un număr de telefon și email-ul aferent pentru care dorești să regenerezi parola.");
    }

    public static String getRecoverPasswordPageDescriptionStepB(){
        return getLabelWithPrimaryKey("recover_password_page_description_username","Te rugăm să introduci numele de utilizator pentru care vrei să regenerezi parola");
    }

    public static String getRecoverPasswordTelephoneNumberLabel(){
        return getLabelWithPrimaryKey("recover_password_telephone_number_label","Număr de telefon");
    }

    public static String getRecoverPasswordTelephoneNumberHint(){
        return getLabelWithPrimaryKey("recover_password_telephone_number_hint","0xxxxxxxxx");
    }

    public static String getRecoverPasswordEmailAdressLabel(){
        return getLabelWithPrimaryKey("recover_password_email_adress_label","Adresă de e-mail");
    }

    public static String getRecoverPasswordEmailAdressHint(){
        return getLabelWithPrimaryKey("recover_password_email_adress_hint","nume@exemplu.com");
    }

    public static String getRecoverPasswordNextStepButtonLabel(){
        return getLabelWithPrimaryKey("recover_password_next_step_button_label","Resetează parola");
    }

    public static String getRecoverPasswordEnterActivationDataLabel(){
        return getLabelWithPrimaryKey("recover_password_enter_activation_data_label","Te rugăm să introduci datele necesare activării contului");
    }

    public static String getRecoverPasswordActivationCodeLabel(){
        return getLabelWithPrimaryKey("recover_password_activation_code_label","Cod unic");
    }

    public static String getRecoverPasswordNewPasswordLabel(){
        return getLabelWithPrimaryKey("recover_password_new_password_label","Parola");
    }

    public static String getRecoverPasswordConfirmationPasswordLabel(){
        return getLabelWithPrimaryKey("recover_password_confirmation_password_label","Confirmă parola");
    }

    public static String getRecoverPasswordResendActivationCode(){
        return getLabelWithPrimaryKey("recover_password_resend_activation_code","Retrimite codul");
    }

    public static String getRecoverPasswordActivateAccountButton(){
        return getLabelWithPrimaryKey("recover_password_activate_account_button","Activează cont");
    }

    public static String getRecoverPasswordActivationCodeSuccessfullySendMessage(){
        return getLabelWithPrimaryKey("recover_password_activation_code_successfully_send_message","Codul de activare a fost trimis.");
    }

    public static String getRecoverPasswordAccountSuccessfullyActivated(){
        return getLabelWithPrimaryKey("recover_password_account_successfully_activated","Contul a fost activat cu succes.");
    }

    public static String getRecoverPasswordPasswordHint(){
        return getLabelWithPrimaryKey("recover_password_password_hint","parola");
    }

    public static String getRecoverPasswordActivationCodeHint(){
        return getLabelWithPrimaryKey("recover_password_activation_code_hint","cod");
    }

    public static String getRecoverPasswordRefuseButton(){
        return getLabelWithPrimaryKey("recover_password_refuse_button","Renunță");
    }

    public static String getRecoverPasswordEmailInvalidFormat(){
        return getLabelWithPrimaryKey("recover_password_email_invalid_format","Te rugăm să introduci un e-mail valid.");
    }

    public static String getRecoverPasswordEbuNonMigratedAccountMsisdnAndEmail(){
        return getLabelWithPrimaryKey("recover_password_ebu_non_migrated_account_msisdn_and_email","Parola nu poate fi regenerată. Te rugăm să contactezi administratorul contului!");
    }

    public static String getRecoverPasswordNotMyVodafoneAccountFirstPart(){
        return getLabelWithPrimaryKey("recover_password_not_myvodafone_account_first_part","Datele introduse nu corespund unui cont My Vodafone existent.");
    }

    public static String getRecoverPasswordNotMyVodafoneAccountSecondPart(){
        return  getLabelWithPrimaryKey("recover_password_not_myvodafone_account_second_part","Vrei să înregistrezi un cont nou?");
    }

    public static String getRecoverPasswordInvalidCode(){
        return getLabelWithPrimaryKey("recover_password_invalid_code","Cod incorect!");
    }

    public static String getRecoveryPasswordExpiredCode() {
        return getLabelWithPrimaryKey("recover_password_expired_code", "Cod expirat!");
    }

    public static String getRecoverPasswordFailedApiCall(){
        return getLabelWithPrimaryKey ("recover_password_failed_api_call","Serviciu momentan indisponibil! Contul nu a fost activat. Te rugăm să reîncerci!");
    }

    public static String getRecoverPasswordPasswordInvalidFormat(){
        return getLabelWithPrimaryKey("recover_password_password_invalid_format","Parola trebuie să fie de minim 8 caractere, să conțină cifre, litere mari și mici.");
    }
    public static String getRecoveryButtonNextStepLabel() {
        return getLabelWithPrimaryKey("recvery_button_next_step_label","Pasul următor");
    }

    public static String getRecoverPasswordErrorBlacklisted() {
        return getLabelWithPrimaryKey("recover_password_error_blacklisted", "Codul de activare nu poate fi trimis!");
    }

}
