package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/1/2017.
 */

public class RegisterLabels extends AppLabels {

    public static String getRegisterTitleLabel(){
        return getLabelWithPrimaryKey("register_tittle_label","Înregistrare");
    }

    public static String getRegisterFirstStepPageTitleLabel(){
        return getLabelWithPrimaryKey("register_first_step_page_tittle_label","Te rugăm să introduci un număr de telefon pentru care dorești să creezi contul");
    }

    public static String getRegisterSecondStepPageTitleLabel(){
        return getLabelWithPrimaryKey("register_second_step_page_tittle_label" ,"Te rugăm să introduci un număr de telefon pentru care dorești să creezi contul");
    }

    public static String getRegisterButtonNextStepLabel() {
        return getLabelWithPrimaryKey("register_button_next_step_label","Pasul următor");
    }

    public static String  getRegisterInputPhoneLabel(){
        return getLabelWithPrimaryKey("register_input_phone_label","Număr de telefon");
    }

    public static String getRegisterInputUsernameHint() {
        return getLabelWithPrimaryKey("register_input_username_hint" ,"0xxxxxxxxxxx");
    }

    public static String getRegisterInputUsernameLabel(){
        return getLabelWithPrimaryKey("register_input_username_label" ,"Nume utilizator");
    }

    public static String getRegisterInputFirstNameLabel() {
        return getLabelWithPrimaryKey("register_input_firstname_label","Prenume");
    }

    public static String getRegisterInputFirstnameHint(){
        return getLabelWithPrimaryKey("register_input_firstname_hint","Prenume");
    }

    public static String getRegisterInputLastNameLabel() {
        return getLabelWithPrimaryKey("register_input_lastname_label","Nume");
    }

    public static String  getRegisterInputLastNameHint(){
        return getLabelWithPrimaryKey ("register_input_lastname_hint","Nume");
    }

    public static String getRegisterInputEmailAddressLabel() {
        return getLabelWithPrimaryKey("register_input_email_address_label","Adresă de e-mail");
    }

    public static String getRegisterInputEmailAddressHint() {
        return getLabelWithPrimaryKey ("register_input_email_address_hint","email@exemplu.com");
    }

    public static String getRegisterInputTermsAndConditionLabel(){
        return getLabelWithPrimaryKey("register_input_terms_and_conditions_label","Sunt de acord cu Termenii și Condițiile");
    }

    public static String getRegisterInputNewsLabel() {
        return getLabelWithPrimaryKey("register_input_news_label","Vreau să primesc newsletters de la Vodafone");
    }

    public static String getRegisterInputUniqueCodeLabel(){
        return getLabelWithPrimaryKey("register_input_unique_code_label","Cod unic");
    }

    public static String getRegisterButtonResendCodeLabel (){
        return getLabelWithPrimaryKey("register_button_resend_code_label","Retrimite codul");
    }

    public static String getRegisterInputContactPhoneLabel() {
        return getLabelWithPrimaryKey ("register_input_contact_phone_label","Număr telefon de contact");
    }

    public static String  getRegisterInputContactPhoneHint(){
        return getLabelWithPrimaryKey ("register_input_contact_phone_hint","0xxxxxxxxx");
    }

    public static String getRegisterThirdStepPageTitleLabel (){
        return getLabelWithPrimaryKey("register_third_step_page_tittle_label","Te rugăm să introduci un număr de telefon pentru care dorești să creezi contul");
    }

    public static String getRegisterInputConfimPasswordLabel(){
        return getLabelWithPrimaryKey("register_input_confirm_password_label" ,"Confirmă parola");
    }

    public static String  getRegisterButtonCreateAccountLabel(){
        return getLabelWithPrimaryKey("register_button_create_account_label","Crează cont");
    }

    public static String getRegisterInputAccountActivated(){
        return  getLabelWithPrimaryKey("register_input_account_activated","Contul a fost activat cu succes.");
    }

    public static String getRegisterInputActivationCodeSend(){
        return getLabelWithPrimaryKey("register_input_activation_code_send","Codul de activare a fost trimis.");
    }

    public static String getRegisterErrorMessageInvalidPhoneNumber(){
        return getLabelWithPrimaryKey("register_error_message_invalid_phone_number","Formatul numărului introdus nu este valid (ex. 0XXXXXXXXX)");
    }

    public static String getRegisterErrorMessageMinCharacterUsername(){
        return getLabelWithPrimaryKey("register_error_message_min_characters_username","Numele de utilizator trebuie să conţină cel puţin 4 caractere.");
    }

    public static String  getRegisterErrorMessageInvalidCharactersUsername(){
        return  getLabelWithPrimaryKey("register_error_message_invalid_characters_username","Caracterele &!<>=|%*\"/'`@#~$^()[]{};:?, sunt interzise!");
    }

    public static String getRegisterErrorMessageTermsCheckBox() {
        return getLabelWithPrimaryKey("register_error_message_terms_checkbox","Pentru a continua trebuie să fii de acord cu termenii și condițiile.");
    }

    public static String getRegisterErrorMessageEmailAddress() {
        return getLabelWithPrimaryKey("register_error_message_email_address","Te rugăm să introduci un email valid!");
    }

    public static String getRegisterErrorMessageRegisterPassword(){
        return getLabelWithPrimaryKey("register_error_message_register_password" ,"Parola trebuie să fie de minimum 8 caractere, să conțină cifre, litere mari și mici.");
    }

    public static String getRegisterErrorMessageConfirmPassword(){
        return getLabelWithPrimaryKey("register_error_message_confirm_password" ,"Câmpurile nu coincid.");
    }

    public static String getRegisterErrorMessageEbuUser(){
        return getLabelWithPrimaryKey("register_error_message_ebu_user","Momentan nu se pot crea conturi pentru persoane juridice. Încearcă aici sau contactează departamentul de relații cu clienții!");
    }

    public static String getRegisterErrorMessageAlredyRegisteredAccount(){
        return getLabelWithPrimaryKey("register_error_message_alredy_registered_account","Un cont pentru acest număr de telefon există deja! Poți recupera parola sau numele de utilizator.");
    }

    public static String getRegisterErrorMessageInvalidCode(){
        return getLabelWithPrimaryKey("register_error_message_invalid_code","Cod incorect!");
    }

    public static String getRegisterErrorMessageExpiredCode(){
        return getLabelWithPrimaryKey("register_error_message_expired_code","Cod expirat!");
    }

    public static String getRegisterLink(){
        return getLabelWithPrimaryKey("register_link", "https://m.vodafone.ro/mobil/inregistrare/index.htm");
    }

    public static String getMigratedMaleRadioButtonLabel(){
        return getLabelWithPrimaryKey("migrated_male_radio_button_label", "Domnul");
    }

    public static String getMigratedFemaleRadioButtonLabel(){
        return getLabelWithPrimaryKey("migrated_female_radio_button_label", "Doamna");
    }

    public static String getRegisterErrorMessageAccountExists(){
        return getLabelWithPrimaryKey("register_error_message_account_exists", "Un cont există deja. Te rugăm să accesezi link-ul Am uitat parola.");
    }

    public static String getRegisterErrorMessageNoContactIds(){
        return getLabelWithPrimaryKey("register_error_message_multiple_accounts", "Contul nu poate fi creat. Te rugăm să contactezi departamentul de relații cu clienții.");
    }

    public static String getActivateAccountErrorMessageMultipleContactIds(){
        return getLabelWithPrimaryKey("activate_account_error_message_multiple_contact_ids", "Contul nu poate fi activat. Te rugăm să contactezi departamentul de relații cu clienții.");
    }

    public static String getActivateAccountErrorMultipleContacts(){
        return getLabelWithPrimaryKey("activate_account_error_multiple_contacts", "Contul nu poate fi creat. Te rugăm să contactezi departamentul de relații clienți.");
    }

    public static String getRegisterContinueOverlayButton(){
        return getLabelWithPrimaryKey("register_continue_overlay_button", "Continuă înregistrarea");
    }

    public static String getRegisterCancelOverlayButton(){
        return getLabelWithPrimaryKey("register_cancel_overlay_button", "Anulează");
    }

    public static String getRegisterContinueOverlayTitle(){
        return getLabelWithPrimaryKey("register_continue_overlay_title", "Înregistrare");
    }

    public static String getRegisterContinueOverlayFirstSubtext(){
        return getLabelWithPrimaryKey("register_existing_account_overlay_text", "Ai deja contul %1$s creat pentru numărul de telefon şi adresa de mail pe care ai introdus-o.\n\n Accesează “Continuă înregistrarea” şi introdu codul primit prin SMS sau “Anulează” şi foloseşte un alt email şi număr de telefon.");
    }

    public static String getRegisterFailedAccountActivation(){
        return getLabelWithPrimaryKey("register_failed_account_activation", "Serviciu momentan indisponibil! Contul nu a fost activat. Te rugăm să reîncerci!");
    }

    public static String getRegisterActivationCodeFailed(){
        return getLabelWithPrimaryKey("register_activation_code_fail_send", "Codul de activare nu poate fi trimis!");
    }
}
