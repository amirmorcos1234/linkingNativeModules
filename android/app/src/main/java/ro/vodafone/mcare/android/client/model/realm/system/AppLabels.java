package ro.vodafone.mcare.android.client.model.realm.system;

import io.realm.Realm;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by user on 06.04.2017.
 */

public class AppLabels {


    protected static String getLabelWithPrimaryKey(String key, String defaultValue) {
        SystemProperty property =null;
        Realm realm = Realm.getDefaultInstance();
        try {
            property = realm.where(SystemProperty.class).equalTo("key", key).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = (property != null) ? property.getValue() : defaultValue;
        realm.close();
        return result;
    }

    public static String getToastErrorMessage() {
        return getLabelWithPrimaryKey("error_Service_temporarily_unavailable", "Serviciu momentan indisponibil!");
    }

    public static String getToastErrorTryAgainMessage() {
        return getLabelWithPrimaryKey("error_Service_temporarily_unavailable", "Serviciu momentan indisponibil. Apasă pentru a reîncerca");
    }

    public static String getToastErrorSomeInfoNotLoaded() {
        return getLabelWithPrimaryKey("error_Some_info_not_loaded", "Ne pare rău, unele informatii nu au putut fi încărcate.");
    }

    public static String getOverlayAreYouSureTitle() {
        return getLabelWithPrimaryKey("overlay_are_you_sure_title", "Ești sigur?");
    }

    public static String getOverlayCancelButton() {
        return getLabelWithPrimaryKey("overlay_are_you_sure_title", "Anulează");
    }

    public static String getOverlayStopButton() {
        return getLabelWithPrimaryKey("overlay_are_you_sure_title", "Oprește");
    }

    public static String getGiveUpButton() {
        return getLabelWithPrimaryKey("give_up_button", "Anulează");
    }

    public static String getVodafonePageTitle() {
        return getLabelWithPrimaryKey("vodafone_page_title", "My Vodafone");
    }

    public static String getOobeArea4PrivacyPolicyLinkAttribute() {
        return getLabelWithPrimaryKey("oobe_area4_privacy_policy_link_attribute", "Politica de confidențialitate");
    }


    public static String getOobeArea1() {
        return getLabelWithPrimaryKey("oobe_area1", "Ai acces la informațiile despre contul tău și Asistență și poți verifica viteza și detalierea traficului tău de date. Folosim informații din telefonul tău și modul în care folosești aplicația ca să ne ajute să îți oferim oferte și servicii personalizate pentru ca tu să poți profita la maxim de My Vodafone.");
    }

    public static String getOobeArea2() {
        return getLabelWithPrimaryKey("oobe_area2", "Îți vom oferi recomandări și asistență personalizată pe baza calității și conectivității datelor tale și a aplicațiilor pe care le utilizezi");
    }

    public static String getOobeArea3() {
        return getLabelWithPrimaryKey("oobe_area3", "Îți vom oferi recomandări prin captarea locației telefonului tău.\nPoți face modificări legate de informațiile colectate și folosite de catre Vodafone, și ai control asupra modului în care comunicăm cu tine la orice oră folosind funcția Setări.");
    }

    public static String getOobeArea4() {
        return getLabelWithPrimaryKey("oobe_area4", "Apăsând \"OK\", vei permite Vodafone să colecteze și să folosească informațiile tale cum este menționat în Setări, și ești de acord cu Termenii și Condițiile și cu Politica de confidențialitate a aplicației.");

    }

    public static String getErrorLoginLimitsReached() {
        return getLabelWithPrimaryKey("error_login_limits_reached", "Contul este blocat, încearcă din nou peste placeholder minute.");
    }

    public static String getPreloginOtherNetwork() {
        return getLabelWithPrimaryKey("prelogin_other_network", "Momentan nu ai acces la funcționalitatea de logare automată deoarece nu ești conectat la rețeaua Vodafone.");
    }


    public static String getOobeRetriesButton() {
        return getLabelWithPrimaryKey("oobe_retries_button", "Reîncearcă");
    }


    public static String getLoadingSpinnerOneMoment() {
        return getLabelWithPrimaryKey("loading_spinner_one_moment", "");
    }

    public static String getOobeConfirmationIOS() {
        return getLabelWithPrimaryKey("oobe_confirmation_ios", "Ca să poți accesa aplicația, este necesar să fii de acord cu Termenii și Condițiile.");
    }

    public static String getErrorLoginAccountInactiveOrMigrated() {
        return getLabelWithPrimaryKey("error_login_account_inactive_or_migrated", "Contul tău nu este activ. Te rugăm să accesezi Recuperare Parolă.");
    }

    public static String getOobeSettingsButton() {
        return getLabelWithPrimaryKey("oobe_settings_button", "Verifică setările");
    }

    public static String getLoginNotRegisteredLabel() {
        return getLabelWithPrimaryKey("login_not_registered_label", "Nu ești încă înregistrat în My Vodafone?");
    }

    public static String getOobeArea3Title() {
        return getLabelWithPrimaryKey("oobe_area3_title", "Locație");
    }

    public static String getOkButton() {
        return getLabelWithPrimaryKey("ok_button", "OK");
    }

    public static String getOobeNoInternetConnection() {
        return getLabelWithPrimaryKey("oobe_no_internet_connection", "Nu s-a putut efectua conexiunea cu serverul. Te rugăm să te asiguri că ai conexiune la internet și reîncearcă.");
    }

    public static String getLoginForgotUsernameLinkAttributed() {
        return getLabelWithPrimaryKey("login_forgot_username_link_attribute", "numele de utilizator");
    }

    public static String getOobeArea4TermsAndConditionLinkAttribute() {
        return getLabelWithPrimaryKey("oobe_area4_terms_and_conditions_link_attribute", "Termenii și Condițiile");
    }

    public static String getLoginRememberMe() {
        return getLabelWithPrimaryKey("rememberMe", "Păstrează-mă autentificat");
    }

    public static String getLoginPageTitle() {
        return getLabelWithPrimaryKey("login_page_title", "Autentifică-te în \nMy Vodafone");
    }

    public static String getErrorLOginAccountDisable() {
        return getLabelWithPrimaryKey("error_login_account_disabled", "Contul tău nu poate fi autentificat!");
    }

    public static String getOobeArea2Title() {
        return getLabelWithPrimaryKey("oobe_area2_title", "Calitate și Conectivitate");
    }

    public static String getErrorLoginFailedAuthentification() {
        return getLabelWithPrimaryKey("error_login_failed_authentification", "Autentificare nereușită!");
    }

    public static String getLoginPasswordHint() {
        return getLabelWithPrimaryKey("login_password_hint", "parola");
    }

    public static String getErrorLoginInvalidUsernameOrPassword() {
        return getLabelWithPrimaryKey("error_login_invalid_username_or_password", "Nume de utilizator sau parolă greșită.");
    }

    public static String getContinueButton() {
        return getLabelWithPrimaryKey("continue_button", "Continuă");
    }

    public static String getLoginForgotPasswordLinkAttribute() {
        return getLabelWithPrimaryKey("login_forgot_password_link_attribute", "parola");
    }

    public static String getReccurentRechargesTitle() {
        return getLabelWithPrimaryKey("recurrent_recharges_title", "Reîncărcări Recurente");
    }

    public static String getBeoTitle() {
        return getLabelWithPrimaryKey("beo_title", "Bonusuri și Opțiuni");
    }

    public static String getErrorServiceTemporarilyUnavailableClickToRetry() {
        return getLabelWithPrimaryKey("error_Service_temporarily_unavailable_click_to_retry", "Serviciu momentan indisponibil. Apasă pentru a reîncerca");
    }

    public static String getMenuHome() {
        return getLabelWithPrimaryKey("menu_home", "Acasă");
    }

    public static String getMenuLogout() {
        return getLabelWithPrimaryKey("menu_logout", "Deconectare");
    }

    public static String getMenuProfile() {
        return getLabelWithPrimaryKey("menu_profile", "Profilul tău");
    }


    public static String getMenuRecharge() {
        return getLabelWithPrimaryKey("menu_recharge", "Reîncarcă");
    }

    public static String getMenuBillsAndPayments() {
        return getLabelWithPrimaryKey("menu_bills_and_payments", "Factură și Plată");
    }

    public static String getMenuSettings() {
        return getLabelWithPrimaryKey("menu_settings", "Setări");
    }

    public static String getSeamlessLoginEasierLabel() {
        return getLabelWithPrimaryKey("seamless_login_easier_label", "Te poți loga în aplicație rapid dacă ești conectat la rețeaua Vodafone.");
    }

    public static String getErrorLoginAccountLocked() {
        return getLabelWithPrimaryKey("error_login_account_locked", "Contul este blocat, încearcă în 2 ore.");
    }


    public static String getTermsAndConditionsPageTitle() {
        return getLabelWithPrimaryKey("terms_and_conditions_page_title", "Termeni și Condiții");
    }

    public static String getErrorLoginFailedConsecutive() {
        return getLabelWithPrimaryKey("error_login_failed_consecutive", "Contul este blocat. Ai introdus greșit parola de mai mult de 5 ori. Te rugăm să reîncerci în 2 ore.");
    }

    public static String getCancelButton() {
        return getLabelWithPrimaryKey("cancel_button", "Renunţă");
    }

    public static String getSeamlessPageTitle() {
        return getLabelWithPrimaryKey("seamless_page_title", "My Vodafone");
    }

    public static String getMenuLoyality2() {
        return getLabelWithPrimaryKey("menu_loyalty_2", "Program  Loialitate");
    }

    public static String getMenuPromotions() {
        return getLabelWithPrimaryKey("menu_promotions", "Detalii reţea");
    }

    public static String getSeamlessLoginEasierPageTitle() {
        return getLabelWithPrimaryKey("seamless_login_easier_page_title", "Logare automată");
    }

    public static String getErrorLoginRestrictedAccess() {
        return getLabelWithPrimaryKey("error_login_restricted_access", "Tipul tău de utilizator nu poate fi autentificat!");
    }

    public static String getOobeConfirmationAndroid() {
        return getLabelWithPrimaryKey("oobe_confirmation_android", "Dacă nu ești de acord cu Termenii și Condițiile nu poți accesa aplicația. Ești sigur că vrei să ieși din aplicație?");
    }

    public static String getSeamlessEnterButton() {
        return getLabelWithPrimaryKey("seamless_enter_button", "Intră");
    }

    public static String getNoInternetConnection() {
        return getLabelWithPrimaryKey("no_internet_connection", "Nu s-a putut efectua conexiunea cu serverul. Te rugăm să te asiguri că ai conexiune la Internet și reîncearcă.");
    }

    public static String getMenuLoyality() {
        return getLabelWithPrimaryKey("enu_loyalty", "Program (de) Loialitate");
    }

    public static String getMenuServices() {
        return getLabelWithPrimaryKey("menu_services", "Serviciile tale");
    }

    public static String getMenuNotifications() {
        return getLabelWithPrimaryKey("menu_notifications", "Notificările tale");
    }

    public static String setMenu_traveling_abroad() {
        return getLabelWithPrimaryKey("menu_traveling_abroad", "Roaming");
    }

    public static String getMenuPayBill() {
        return getLabelWithPrimaryKey("menu_paybill", "Plăţi");
    }


    public static String getMenuVodafoneShops() {
        return getLabelWithPrimaryKey("menu_vodafoneshops", "Magazine Vodafone");
    }

    public static String getRegisterErrorMessageEmailAddress() {
        return getLabelWithPrimaryKey("register_error_message_email_address", "Te rugăm să introduci un email valid!");
    }

    public static String getMenuOffers() {
        return getLabelWithPrimaryKey("menu_offers", "Oferte și Opțiuni pentru tine");
    }

    public static String getLogoutConfirmationLabel() {
        return getLabelWithPrimaryKey("logout_confirmation_label", "Ești sigur că vrei să te deconectezi?");
    }

    public static String getLast_update() {
        return getLabelWithPrimaryKey("last_update", "Actualizat la ora %1$s");
    }

    public static String getRegister_second_step_email_hint() {
        return getLabelWithPrimaryKey("register_second_step_email_hint", "email@exemplu.com");
    }

    public static String getRegister_input_email_address_label() {
        return getLabelWithPrimaryKey(" register_second_step_email_hint", "Adresă de e-mail");
    }

    public static String getError_login_account_disabled() {
        return getLabelWithPrimaryKey("error_login_account_disabled", "Contul tău nu poate fi autentificat!");
    }

    public static String getOverlay_contacts_permission_title() {
        return getLabelWithPrimaryKey("overlay_contacts_permission_title", "Acces Agendă");
    }

    public static String getOverlay_contacts_permission_context() {
        return getLabelWithPrimaryKey("overlay_contacts_permission_context", "Simplifică accesul către numerele de telefon ale prietenilor, permițând aplicației My Vodafone să acceseze lista de contacte.\n" +
                "Nu vom stoca contactele tale în nici unul dintre sistemele noastre.");
    }

    public static String getAccept_button_label() {
        return getLabelWithPrimaryKey("accept_button_label", "Acceptă");
    }

    public static String getDo_later_button_label() {
        return getLabelWithPrimaryKey("do_later_button_label", "Mai târziu");
    }

    public static String getOk_label() {
        return getLabelWithPrimaryKey("ok_button_label", "OK");
    }

    public static String getError_login_account_inactive_or_migrated() {
        return getLabelWithPrimaryKey("error_login_account_inactive_or_migrated", "Contul tău este inactiv. Te rugăm să accesezi Recuperare Parolă.");
    }

    public static String getError_login_restricted_access() {
        return getLabelWithPrimaryKey("error_login_restricted_access", "Tipul tău de utilizator nu poate fi autentificat!");
    }

    public static String getError_login_invalid_username_or_password() {
        return getLabelWithPrimaryKey("error_login_invalid_username_or_password", "Nume de utilizator sau parolă greșită.");
    }

    public static String getBilling_overview_page_title() {
        return getLabelWithPrimaryKey("billing_overview_page_title", "Factură și plată");
    }

    public static String getPayBillOverlayAcceptBtn() {
        return getLabelWithPrimaryKey("pay_bill_overlay_accept_button", "Reincarcă");
    }

    public static String getPayBillOverlayRefuseBtn() {
        return getLabelWithPrimaryKey("pay_bill_overlay_refuse_button", "Modifică");
    }

    public static String getPayBillOverlayTitle() {
        return getLabelWithPrimaryKey("pay_bill_overlay_title", "Te rugăm să confirmi!");
    }

    public static String getNoInternetConnectionToastMessage() {
        return getLabelWithPrimaryKey("no_internet_connection_toast_message", "Se caută conexiune la server!");
    }

    public static String getNoInternetConnectionReestablishToastMessage() {
        return getLabelWithPrimaryKey("no_internet_connection_reestablish_toast_message", "Conexiune restabilită!");
    }

    public static String getOverlayRefuseBtn() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Înapoi");
    }

    public static String getOverlayCallBtn() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Sună acum");
    }

    public static String getConfirmProfileOverlayTitle() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Confirmare profil");
    }

    public static String getConfirmProfileOverlayText() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Pentru confirmarea profilului te rugăm să ne contactezi la relaţii cu clienţii.");
    }

    public static String getRecoveryPasswordOverlayTitle() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Recuperare parola");
    }

    public static String getRecoveryPasswordOverlayTexte() {
        return getLabelWithPrimaryKey("overlay_refuse_button", " Pentru recuperarea parolei te rugăm să ne contactezi la relaţii cu clienţii.");
    }

    public static String getSelfRegisterOverlayTitle() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Înregistrare");
    }

    public static String getSelfRegisterOverlayTexte() {
        return getLabelWithPrimaryKey("overlay_refuse_button", "Pentru înregistrare te rugăm să ne contactezi la relaţii cu clienţii.");
    }

    public static String getRONAmountCurrency() {
        return getLabelWithPrimaryKey("ron_amount_currency", "%1$s RON");
    }

    public static String getEURAmountCurrency() {
        return getLabelWithPrimaryKey("eur_amount_currency", "%1$s €");
    }

    public static String getGenericRetryErrorMessage() {
        return getLabelWithPrimaryKey("generic_retry_error_message", "Serviciu momentan indisponibil.\nApasă pentru a reîncerca.");
    }

    public static String getActivateAccountPageTitle() {
        return getLabelWithPrimaryKey("activate_account_page_title", "Activare cont");
    }

    public static String getElectronicBillActivateButton() {
        return getLabelWithPrimaryKey("electronic_bill_activate_button", "Activează");
    }

    public static String getElectronicBillSendButton() {
        return getLabelWithPrimaryKey("electronic_bill_send_button", "Trimite");
    }

    public static String getVersionUpdateTitle(){
        return getLabelWithPrimaryKey("version_update_overlay_title_android","Aplicaţia necesită update");
    }

    public static String getVersionUpdateSummary(){
        return getLabelWithPrimaryKey("version_update_overlay_summary_android","Pentru a utiliza această funcţionalitate, te rugăm să îţi actualizezi aplicaţia");
    }

    public static String getVersionUpdateButton(){
        return getLabelWithPrimaryKey("version_update_overlay_button_android","Intră în Play Store");
    }

    public static String getVersionUpdateStoreUrl(){
        return getLabelWithPrimaryKey("version_update_overlay_url_android","https://play.google.com/store/apps/details?id=ro.vodafone.mcare.android");
    }

    public static String getAcceptLabel() {
        return getLabelWithPrimaryKey("lbl_accept","Acceptă");
    }

    public static String getRejectLabel() {
        return getLabelWithPrimaryKey("lbl_deny","Respinge");
    }

    public static String getSuccessLabel() {
        return getLabelWithPrimaryKey("lbl_success", "Succes");
    }

    public static String getFromLabel() {
        return getLabelWithPrimaryKey("lbl_from", "De la");
    }

    public static String getOnLabel() {
        return getLabelWithPrimaryKey("lbl_on", "pe");
    }

    public static String getTodayLabel() {
        return getLabelWithPrimaryKey("lbl_today", "Azi");
    }

    public static String getApi19FailedErrorMessage() {
        return getLabelWithPrimaryKey("api_19_failed_error_message", "Ne pare rău, unele informaţii nu au putut fi încărcate.");
    }

    public static String getApi19TimeoutErrorMessage() {
        return getLabelWithPrimaryKey("api_19_timeout_error_message", "Ne pare rău, unele informaţii nu au putut fi încărcate. Te rugăm să reîncerci.");
    }

}
