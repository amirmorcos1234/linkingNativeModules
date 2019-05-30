package ro.vodafone.mcare.android.client.model.realm.system;

import static ro.vodafone.mcare.android.client.model.realm.system.AppLabels.getLabelWithPrimaryKey;

/**
 * Created by user on 06.04.2017.
 */

public class SettingsLabels {

    public static String getNewPicButton() {
        return getLabelWithPrimaryKey("settings_account_new_picture", "Fă o poză");
    }

    public static String getSelectPicButton() {
        return getLabelWithPrimaryKey("settings_account_select_picture", "Alege o poză");
    }

    public static String getResetPicButton() {
        return getLabelWithPrimaryKey("settings_account_reset_picture", " Redă poza inițială ");
    }

    public static String getChangeAvatarPic() {
        return getLabelWithPrimaryKey("settings_account_change_avatar_pic", "Poza de profil a fost modificată cu succes. Va dura câteva minute până se va afișa în ecranul principal.");
    }

    public static String getRetryButton() {
        return getLabelWithPrimaryKey("settings_electronic_bill_error", "Serviciu momentan indisponibil!\nApasă pentru a reîncerca.");
    }

    public static String getPostPoneButton() {
        return getLabelWithPrimaryKey("settings_postpone_payment_button", "Amână plata");
    }

    public static String getOverlayPostPoneButton() {
        return getLabelWithPrimaryKey("settings_postpone_overlay_positive", "Da");
    }

    public static String getOverlayCancelPostPoneButton() {
        return getLabelWithPrimaryKey("settings_postpone_overlay_negative", "Renunță");
    }

    public static String getSettingsResetPassowordTitle() {
        return getLabelWithPrimaryKey("settings_reset_password_title", "Resetează parola");
    }

    public static String getSettingsAuthentificationTitle() {
        return getLabelWithPrimaryKey("settings_authentification_title", "Autentificare automată");
    }

    public static String getSettingsAuthentificationSubTitle() {
        return getLabelWithPrimaryKey("settings_authentification_subtitle", "Autentifică-te fără a introduce detaliile tale");
    }

    public static String getSettingsPageTitle() {
        return getLabelWithPrimaryKey("settings_page_title", "Setări");
    }

    public static String getSettingsEbillTitle() {
        return getLabelWithPrimaryKey("settings_eBill_title", "Factură electronică");
    }

    public static String getSettingsSimBlockTitle() {
        return getLabelWithPrimaryKey("settings_sim_block_title", "Autentificare automată");
    }

    public static String getSettingsPersonalizeYourServicesTitle() {
        return getLabelWithPrimaryKey("settings_personalize_your_services_title", "Personalizează-ți serviciile");
    }

    public static String getSettingsPrivacyTitle() {
        return getLabelWithPrimaryKey("settings_privacy_title", "Confidențialitate");
    }

    public static String getSettingsNotificationTitle() {
        return getLabelWithPrimaryKey("settings_notification_title", "Notificări");
    }

    public static String getSettingsTermsAndConditionsTitle() {
        return getLabelWithPrimaryKey("settings_terms_and_condition_title", "Termeni și Condiții");
    }

    public static String getSettingsSimDetailsTitle() {
        return getLabelWithPrimaryKey("settings_sim_details_title", "Detalii şi Schimbare SIM");
    }

    public static String getSettingsConfidentialPolicyTitle() {
        return getLabelWithPrimaryKey("settings_confidential_policy_title", "Politică de confidențialitate");
    }

    public static String getSettingsPersonalizeYourServicesSubtitle() {
        return getLabelWithPrimaryKey("settings_personalize_your_services_subtitle", "Adaugă poze și nume de profil");
    }

    public static String getSettingsAuthentificationSubtitle() {
        return getLabelWithPrimaryKey("settings_authentification_subtitle", "Autentifică-te fără a introduce detaliile tale");
    }

    public static String getSettingsYourAccountTitle() {
        return getLabelWithPrimaryKey("settings_your_account", "Contul tău");
    }

    public static String getSettingsCustomizeYourAppTitle() {
        return getLabelWithPrimaryKey("settings_customize_your_app", "Personalizează-ți aplicația");
    }

    public static String getSettingsUtilsInfoTitle() {
        return getLabelWithPrimaryKey("settings_util_info", "Informații utile");
    }

    public static String getSetingsPermissionsTitle() {
        return getLabelWithPrimaryKey("settings_permissions", "Permisiuni");
    }

    public static String getSeamlessApprovalFlagSucess() {
        return getLabelWithPrimaryKey("seamless_auto_authenficication_succcess", "Ai activat autentificarea automată");
    }

    public static String getSeamlessApprovalOverlaySubtitle() {
        String defaultValue = "Data viitoare te vei putea autentifica folosind numele de utilizator și parola.";
        return getLabelWithPrimaryKey("seamless_approval_overlay_subtitle", defaultValue);
    }

    public static String getResetPasswordSucess() {
        return getLabelWithPrimaryKey("toast_password_reset_success", "Parola a fost resetată");
    }

    public static String getValidatorCurrentPasswordMessage() {
        return getLabelWithPrimaryKey("wrong_current_password", "Parola nu este corecta");
    }

    //Block SIM Settings
    public static String getAlternativePhoneMessage() {
        return getLabelWithPrimaryKey("alternative_phone_message", "Introdu un număr de telefon alternativ unde te putem contacta");
    }

    public static String getBlockSimConsequencesTitle() {
        return getLabelWithPrimaryKey("block_sim_consequences_title", "Consecințe situație SIM Blocat dorește deblocare SIM");
    }

    public static String getBlockSimConsequencesMessageOne() {
        return getLabelWithPrimaryKey("block_sim_consequences_message_one", "Îți poți recupera cartela SIM în cea mai apropiată locație Vodafone. Aici vei primi un SIM cu același număr de telefon.");
    }

    public static String getBlockSimConsequencesMessageTwo() {
        return getLabelWithPrimaryKey("block_sim_consequences_message_two", "Taxa pentru schimbarea SIM-ului este de 4 EUR credit.");
    }

    public static String getUnBlockSimConsequencesTitle() {
        return getLabelWithPrimaryKey("unblock_sim_consequences_title", "Consecințe situație SIM Deblocat dorește blocare SIM");
    }

    public static String getUnBlockSimConsequencesMessageOne() {
        return getLabelWithPrimaryKey("unblock_sim_consequences_message_one", "În situația în care nu mai ești în posesia cartelei SIM și dorești ca ea să nu poată fi folosită, apasă pe butonul \"Blochează\".");
    }

    public static String getUnBlockSimConsequencesMessageTwo() {
        return getLabelWithPrimaryKey("unblock_sim_consequences_message_two", "Blocarea cartelei SIM nu va permite folosirea acesteia pentru a efectua sau primi apeluri, mesaje sau alte activități.");
    }

    public static String getEbuBlockSimConsequencesTitle() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_consequences_title", "Consecințe situație SIM Blocat dorește deblocare SIM");
    }

    public static String getEbuBlockSimConsequencesMessageOne() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_consequences_message_one", "Îţi poţi recupera cartela SIM apelând la reprezentanţii Vodafone. Vei primi un SIM cu acelaşi număr de telefon.");
    }

    public static String getEbuBlockSimConsequencesMessageTwo() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_consequences_message_two", "Pentru că vrem să iţi răsplătim fidelitatea, schimbarea de SIM efectuată din contul tău My Vodafone este gratuită.");
    }

    public static String getEbuUnBlockSimConsequencesTitle() {
        return getLabelWithPrimaryKey("ebu_block_sim_consequences_title", "Consecințe situație SIM Deblocat dorește blocare SIM");
    }

    public static String getEbuUnBlockSimConsequencesMessageOne() {
        return getLabelWithPrimaryKey("ebu_block_sim_consequences_message_one", "În situația în care nu mai ești în posesia cartelei SIM și dorești ca ea să nu poată fi folosită, apasă pe butonul \"Blochează\".");
    }

    public static String getEbuUnBlockSimConsequencesMessageTwo() {
        return getLabelWithPrimaryKey("ebu_block_sim_consequences_message_two", "Blocarea cartelei SIM nu va permite folosirea acesteia pentru a efectua sau primi apeluri, mesaje sau alte activități.");
    }

    public static String getBlockOverlayMessage() {
        return getLabelWithPrimaryKey("block_sim_overlay_message", "Înțeleg că blocarea unei cartele SIM trebuie făcută doar în cazul în care aceasta a fost pierdută sau furată. \n\n Înteleg că abuzul de această funcționalitate poate duce la imposibilitatea deblocării cartelei mele SIM.");
    }

    public static String getBlockSimVovMessage() {
        return getLabelWithPrimaryKey("block_sim_vov_message", "Cererea dumneavoastră este în curs de procesare. Vă rugăm să așteptați 5 minute și să reactualizați pagina pentru a vizualiza status-ul corect al numărului de telefon");
    }

    public static String getBlockSimSuccessToastMessage() {
        return getLabelWithPrimaryKey("block_sim_success_toast_message", "Cererea se procesează.");
    }

    public static String getEbuBlockSimSuccessVovMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. SIM-ul va fi blocat!");
    }

    public static String getEbuBlockSimSuccessToastMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_success_toast_message", "Solicitarea a fost trimisă!");
    }

    public static String getEbuBlockSimDelaySuccessVovMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_delay_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. SIM-ul va fi blocat.");
    }

    public static String getEbuBlockSimDelayFailedVovMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_delay_failed_vov_message", "SIM-ul nu a fost blocat. Vă rugăm reîncercaţi.");
    }

    public static String getEbuBlockSimRequestPendingVovMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_request_pending_vov_message", "Solicitarea ta a fost preluată. Revenim cu o confirmare.");
    }

    public static String getEbuBlockSimRequestPendingToastMessage() {
        return getLabelWithPrimaryKey("ebu_block_sim_request_pending_toast_message", "Solicitarea ta a fost preluată. Revenim cu o confirmare");
    }

    public static String getEbuUnBlockSimSuccessVovMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. SIM-ul va fi deblocat!");
    }

    public static String getEbuUnBlockSimSuccessToastMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_success_toast_message", "Solicitarea a fost trimisă!");
    }

    public static String getEbuUnBlockSimDelaySuccessVovMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_delay_success_vov_message", "Solicitarea ta a fost trimisă spre procesare. SIM-ul va fi deblocat.");
    }

    public static String getEbuUnBlockSimDelayFailedVovMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_delay_failed_vov_message", "SIM-ul nu a fost deblocat. Vă rugăm reîncercaţi.");
    }

    public static String getEbuUnBlockSimRequestPendingVovMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_request_pending_vov_message", "Solicitarea ta a fost preluata. Revenim cu o confirmare.");
    }

    public static String getEbuUnBlockSimRequestPendingToastMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_request_pending_toast_message", "Solicitarea ta a fost preluată. Revenim cu o confirmare");
    }

    public static String getEbuSubUserUnBlockSimRequestVovMessage() {
        return getLabelWithPrimaryKey("ebusubuser_unblock_sim_request_vov_message", "Solicitarea ta a fost trimisă spre aprobare către administratorul contului.");
    }

    public static String getEbuSubUserUnBlockSimRequestToastMessage() {
        return getLabelWithPrimaryKey("ebusubuser_unblock_sim_request_toast_message", "Solicitarea a fost trimisă!");
    }


    public static String getEbuSubUserUnBlockSimRequestPendingToastMessage() {
        return getLabelWithPrimaryKey("ebu_unblock_sim_request_error_pending_toast_message", "Aveţi deja cereri în procesare.");
    }



    //SIM details
    public static String getPukTitle() {
        return getLabelWithPrimaryKey("settings_sim_puk_title", "Codul PUK");
    }

    public static String getSimSeriesTitle() {
        return getLabelWithPrimaryKey("settings_sim_series_title", "Serie SIM");
    }

    public static String getPaymentAgreementTitle() {
        return getLabelWithPrimaryKey("settings_payment_agreement_title", "Amânare de plată");
    }

    public static String getPaymentAgreementPayBillNow() {
        return getLabelWithPrimaryKey("settings_payment_agreement_pay_bill_now", "Plătește factura");
    }

    public static String getPaymentAgreementUserNotEligible() {
        return getLabelWithPrimaryKey("settings_payment_agreement_user_not_eligible", "Ne pare rău, nu sunteți eligibil pentru amânare plată! Te rugăm să contactezi serviciul clienți la *222.");
    }

    public static String getPaymentAgreementErrorCouldntPostpone() {
        return getLabelWithPrimaryKey("settings_payment_agreement_error_postpone", "Ne pare rău, data scadentă a plății nu a putut fi amânată. Pentru suport, te rugăm să contactezi serviciul relații cu clienții la *222.");
    }

    public static String getPaymentAgreementNotEligibleMonth() {
        return getLabelWithPrimaryKey("settings_payment_agreement_not_eligible_this_month", "Ne pare rău, nu ești eligibil de amânare plată.");
    }

    public static String getPaymentAgreementBillExpired() {
        return getLabelWithPrimaryKey("settings_payment_agreement_bill_expired", "Scadența facturii tale a expirat și nu se mai poate înregistra o amânare de plată.");
    }

    public static String getPaymentAgreementAlreadyPostponed() {
        return getLabelWithPrimaryKey("settings_payment_agreement_already_postponed", "Data scadentă a plății a fost deja amânată.");
    }

    public static String getPaymentAgreementSelectDate() {
        return getLabelWithPrimaryKey("settings_payment_agreement_select_date", "Selectează data");
    }

    public static String getPaymentAgreementNoDaysPostpone() {
        return getLabelWithPrimaryKey("settings_payment_agreement_no_days_postpone", "Poți amâna data scadentă a facturii cu până la ");
    }

    public static String getPaymentAgreementOverlayDetails() {
        return getLabelWithPrimaryKey("settings_payment_agreement_overlay_details", "Dorești amânare de plată la data selectată?");
    }

    public static String getPaymentAgreementVovMessage() {
        return getLabelWithPrimaryKey("payment_agreement_vov_message", "Ai amânat plata până pe data de ");
    }

    public static String getPrivacyPolicyCollectDataTitle() {
        return getLabelWithPrimaryKey("privacy_policy_collect_data", "Date pe care le colectăm");
    }

    public static String getPrivacyPolicyDontCollectDataTitle() {
        return getLabelWithPrimaryKey("privacy_policy_dont_collect_data", "Date pe care nu le colectăm");
    }

    public static String getPrivacyPolicyPurposeTitle() {
        return getLabelWithPrimaryKey("privacy_policy_purpose", "În ce scopuri folosim datele tale");
    }

    public static String getPrivacyPolicyDataProtectionTitle() {
        return getLabelWithPrimaryKey("privacy_policy_data_protection", "Protecția și controlul datelor");
    }

    public static String getPrivacyPolicyMoreInfoTitle() {
        return getLabelWithPrimaryKey("privacy_policy_more_info", "Mai multe despre Politica de confidențialitate");
    }

    public static String getBlockSimOverlayTitle() {
        return getLabelWithPrimaryKey("blockSim_overlay_title", "Ești sigur?");
    }

    public static String getBlockSimOverlayBlockButton() {
        return getLabelWithPrimaryKey("blockSim_overlay_block_button", "Blochează SIM");
    }

    public static String getBlockSimOverlayCancelBlockButton() {
        return getLabelWithPrimaryKey("blockSim_overlay_cancel_block_button", "Renunță");
    }

    public static String getCustomServicesChangePicButtonChoose() {
        return getLabelWithPrimaryKey("customServices_change_pic_button_choose", "Vreau să aleg altă poză");
    }

    public static String getCustomServicesChangePicButtonNew() {
        return getLabelWithPrimaryKey("customServices_change_pic_button_new", "Vreau să fac o poză nouă");
    }

    public static String getCustomServicesOverlayTitle() {
        return getLabelWithPrimaryKey("customServices_overlay_title", "Acces Cameră");
    }

    public static String getCustomServicesOverlaySubtext() {
        return getLabelWithPrimaryKey("customServices_overlay_subtext", "Permite aplicației My Vodafone să acceseze Camera și Galerie poze. Nu vom stoca datele tale în nici unul dintre sistemele noastre.");
    }

    public static String getCustomServicesCheckedFlagOverlaySubtext(){
        return getLabelWithPrimaryKey("customServices_checked_flag_overlay_subtext", "Permite aplicației My Vodafone să acceseze Camera și Galerie poze." );
    }

    public static String getCustomServicesButtonActivate() {
        return getLabelWithPrimaryKey("customServices_button_activate", "Acceptă");
    }

    public static String getCustomsServicesButtonRefuze() {
        return getLabelWithPrimaryKey("customServices_button_refuze", "Mai târziu");
    }

    public static String getElectronicBillOverlayTitle() {
        return getLabelWithPrimaryKey("electronicBill_overlay_title", "Activare factură electronică");
    }

    public static String getElectricBillOverlaySubtext() {
        return getLabelWithPrimaryKey("electronicBill_overlay_subtext", "Ești sigur că dorești să primești factura în format electronic?");
    }

    public static String getEnrollCnpErrorMessage() {
        return getLabelWithPrimaryKey("enroll_cnp_error_message", "Te rugăm să introduci un CNP valid.");
    }

    public static String getEnrollPhoneErrorMessage() {
        return getLabelWithPrimaryKey("enroll_phone_error_message", "Te rugăm să introduci un număr de telefon valid.");
    }

    public static String getSettingsCardsAdapterButtonTurnOff() {
        return getLabelWithPrimaryKey("settings_cards_adapter_button_turn_off", "Oprește");
    }

    public static String getSettingsCardsAdapterButtonKeepOn() {
        return getLabelWithPrimaryKey("settings_cards_adapter_button_keep_on", "Anulează");
    }

    public static String getSettingsNotificationsErrorText1() {
        return getLabelWithPrimaryKey("settings_notifications_error_text_1", "Serviciu momentan indisponibil.\n Apasă aici pentru a reîncerca.");
    }

    public static String getLoyaltyConsumedPointsTabErrorCard() {
        return getLabelWithPrimaryKey("loyalty_consumed_points_tab_error_card", "Momentan nu există rezultate pentru filtrarea făcută. Pentru o căutare mai relevantă folosiţi filtrele.");
    }

    public static String getLoyaltyConsumedPointsErrorCard() {
        return getLabelWithPrimaryKey("loyalty_consumed_points_tab_error_card", "Momentan nu există rezultate. Pentru o căutare mai relevantă folosiţi filtrele.");
    }

    public static String getPhonesSpinner() {
        return getLabelWithPrimaryKey("phones_spinner", "Sortare după preţ");
    }

    public static String getPrivacySubtitleOne() {
        return getLabelWithPrimaryKey("privacy_subtitle_one", "Informații despre interacțiunea ta cu noi");
    }


    public static String getPrivacySimpleOne() {
        return getLabelWithPrimaryKey("privacy_simple_one", "Când folosești funcționalitățile principale ale aplicației My Vodafone, de exemplu verificarea facturii, vizualizarea ofertelor tale, a informației despre consumul de date ale unei aplicații sau interacțiunea cu agenții noștri, aceste interacțiuni sunt înregistrate pe serverele noastre.\n");
    }

    public static String getPrivacySubtitleTwo() {
        return getLabelWithPrimaryKey("privacy_subtitle_two", "Informații legate de calitatea și utilizarea conectivității serviciilor și a aplicațiilor:");
    }

    public static String getPrivacySimpleTwo() {
        return getLabelWithPrimaryKey("privacy_simple_two", "În cazul în care nu ai oprit colectarea datelor, atunci aplicația My Vodafone ne va trimite în mod regulat informații legate de acoperire și consumul de date, informații legate de aplicațiile tale, locația ta, cât și informații legate de telefonul tău, cum poți vedea în detaliu mai jos.");
    }

    public static String getPrivacySimpleThree() {
        return getLabelWithPrimaryKey("privacy_simple_three", "Informații legate atât de conectivitatea rețelei și calitatea apelurilor, internetului și a serviciilor de date, cât și statistici legate de volumul traficului de date consumat de telefonul tău, precum:");
    }

    public static String getPrivacyFirstCategory() {
        return getLabelWithPrimaryKey("privacy_first_category", "Despre colectarea datelor");
    }

    public static String getPrivacySecondCategory() {
        return getLabelWithPrimaryKey("privacy_second_category", "Acoperire, conectivitate și utilizarea datelor");
    }

    public static String getPrivacyThirdCategory() {
        return getLabelWithPrimaryKey("privacy_third_category", "Aplicații");
    }

    public static String getThirdCategoryTitle() {
        return getLabelWithPrimaryKey("third_category_title", "Statistici legate de aplicațiile telefonului tău, precum:");
    }

    public static String getPrivacyFourthCategory() {
        return getLabelWithPrimaryKey("privacy_fourth_category", "Telefon");
    }

    public static String getFourthCategoryTitle() {
        return getLabelWithPrimaryKey("fourth_category_title", "Informații tehnice despre telefonul tău, precum:");
    }

    public static String getPrivacyFifthCategory() {
        return getLabelWithPrimaryKey("privacy_fifth_category", "Date de localizare");
    }

    public static String getFifthCategorySimpleOne() {
        return getLabelWithPrimaryKey("fifth_category_simple_one", "Dacă nu ai oprit colectarea din setările aplicației, colectăm date despre locația telefonului tău atunci când măsurăm cele menționate mai sus. De exemplu, știm locația ta aproximativă din telefonul tău și hotspot-ul WiFi pe care îl folosește telefonul tău. Când telefonul tău sau una dintre aplicațiile tale folosește GPS, noi folosim acele date pentru a testa locația într-un mod mai precis, și uneori folosim viteza și direcția de mers dacă sunt disponibile cu informațiile GPS.");
    }

    public static String getFifthCategorySimpleTwo() {
        return getLabelWithPrimaryKey("fifth_category_simple_two", "În cazul în care nu s-a ales altfel, informația este colectată printr-un identificator personal.");
    }

    public static String getPrivacySixthCategory() {
        return getLabelWithPrimaryKey("privacy_sixth_category", "Performanța aplicației");
    }

    public static String getSixthCategorySimpleOne() {
        return getLabelWithPrimaryKey("sixth_category_simple_one", "Informații legate de performanța și utilizarea aplicației My Vodafone, de exemplu când și de câte ori a fost folosită aplicația, alegerile din cadrul aplicației, versiunea aplicației, ultima actualizare, ID-ul aplicației, firma și modelul telefonului.");
    }

    public static String getPrivacySeventhCategory() {
        return getLabelWithPrimaryKey("privacy_seventh_category", "Date sensibile pe care nu le colectăm");
    }

    public static String getSeventhCategorySimpleOne() {
        return getLabelWithPrimaryKey("seventh_category_simple_one", "Înțelegem că unele dintre informațiile pe care le colectăm sunt sensibile așa că am creat aplicația cu scopul de a reduce sensibilitatea acestui tip de informații. \n De exemplu, nu vom colecta informații despre:");
    }

    public static String getSeventhCategorySimpleOnePrim() {
        return getLabelWithPrimaryKey("seventh_category_simple_one_prim", "Informații sensibile pe care nu le colectăm.");
    }

    public static String getPrivacyEighthCategory() {
        return getLabelWithPrimaryKey("privacy_eighth_category", "Îmbunătățirea rețelei și a serviciilor noastre");
    }

    public static String getEighthCategorySimpleOne() {
        return getLabelWithPrimaryKey("eighth_category_simple_one", "În afara folosirii datelor tale pentru a îmbunătăți serviciile cerute, de exemplu, pentru a-ți arăta cât trafic de date consumă o aplicație, folosim datele pentru a ne îmbunătăți calitatea rețelelor și a serviciilor.");
    }

    public static String getEighthCategorySimpleTwo() {
        return getLabelWithPrimaryKey("eighth_category_simple_two", "Analizăm datele colectate pentru a diagnostica conectivitatea, acoperirea sau probleme legate de calitatea conexiunii, a înțelege nevoile de conectivitate din consumul de date, WiFi și aplicații, și folosim aceste informații pentru a îmbunătăți rețeaua și serviciile noastre. Acestea pot include, spre exemplu:");
    }

    public static String getPrivacyNinthCategory() {
        return getLabelWithPrimaryKey("privacy_ninth_category", "Asistență personalizată și recomandări");
    }

    public static String getNinthCategorySimpleOne() {
        return getLabelWithPrimaryKey("ninth_category_simple_one", "În afara folosirii datelor tale pentru a îmbunătăți serviciile cerute, de exemplu, pentru a-ți arăta cât trafic de date consumă o aplicație, folosim aceste date pentru a-ți oferi recomandări personalizate.\nPutem folosi datele colectate pentru a-ți oferi comunicări de marketing personalizate. De exemplu, putem să:");
    }

    public static String getNinthCategorySimpleTwo() {
        return getLabelWithPrimaryKey("ninth_category_simple_two", "Putem prelucra datele colectate împreună cu alte date personale. Nu vom folosi datele colectate pentru a crea profiluri personale.");
    }

    public static String getNinthCategorySimpleThree() {
        return getLabelWithPrimaryKey("ninth_category_simple_three", "Dacă ai ales să ne oferi informații fără a fi identificat în mod personal, toți indicatorii personali sunt șterși din aplicație înainte de a trimite informația către Vodafone și procesăm informația colectată în mod anonim. Nu intenționăm să folosim informațiile pentru a te identifica pe tine sau alte persoane în mod personal și avem măsurile tehnice și organizaționale de prevenție a acestui fapt.");
    }

    public static String getPrivacyTenthCategory() {
        return getLabelWithPrimaryKey("privacy_tenth_category", "Cum poți controla colectarea datelor tale?");
    }

    public static String getTenthCategorySimpleOne() {
        return getLabelWithPrimaryKey("tenth_category_simple_one", "Vodafone îți oferă alegeri și control asupra datelor pe care ești dispus să le împărtășești din telefonul tău. Poți alege să:");
    }

    public static String getPrivacyEleventhCategory() {
        return getLabelWithPrimaryKey("privacy_eleventh_category", "Cum protejăm datele tale?");
    }

    public static String getPrivacyTwelfthCategory() {
        return getLabelWithPrimaryKey("privacy_twelfth_category", "Cât timp păstrăm datele tale?");
    }

    public static String getPrivacyThirteenthCategory() {
        return getLabelWithPrimaryKey("privacy_thirteenth_category", "Politica de confidențialitate Vodafone");
    }

    public static String getTenthCategorySimpleTwo() {
        return getLabelWithPrimaryKey("tenth_category_simple_two", "Ține minte că oprirea acestor preferințe va impacta nivelul de personalizare pe care ți-l putem oferi.");
    }

    public static String getEleventhCategorySimpleOne() {
        return getLabelWithPrimaryKey("eleventh_category_simple_one", "Vodafone se supune măsurilor tehnice și de siguranță organizațională impuse de lege și de standardele industriei pentru a-ți proteja datele personale împotriva accesului neautorizat. Noi luăm măsuri pentru a ne asigura că folosim datele tale personale exact cum este descris în politica de confidențialitate și pentru a respecta alegerile pe care le-ai făcut. Creăm parteneriate doar cu furnizorii de servicii care oferă același nivel de siguranță a informației pe care îl aștepți de la Vodafone.");
    }

    public static String getTwelfthCategorySimpleOne() {
        return getLabelWithPrimaryKey("twelfth_category_simple_one", "Datele ce privesc calitatea și utilizarea serviciilor tale de conectivitate, datele de localizare și ale aplicațiilor sunt reținute nu mai mult de 14 luni de la data colectării.");
    }

    public static String getTwelfthCategorySimpleTwo() {
        return getLabelWithPrimaryKey("twelfth_category_simple_two", "Vodafone poate păstra rapoarte statistice agregate care au fost generate prin utilizarea informației listate timp de o perioadă îndelungată, dar aceste rapoarte nu te identifică ca utilizator și nu vor fi folosite în nici un mod care ți-ar putea afecta intimitatea.");
    }

    public static String getTwelfthCategorySimpleThree() {
        return getLabelWithPrimaryKey("twelfth_category_simple_three", "Pentru informații legate de alt tip de retenții de date cu Vodafone, te rugăm să faci referință la politica de confidențialitate.");
    }

    public static String getLinkAncomOffersTC() {
        return getLabelWithPrimaryKey("link_ancom_offers_tC", "https://www.vodafone.ro/termeni-aplicatie");
    }

    public static String getLinkAncomOffersSpecific() {
        return getLabelWithPrimaryKey("link_ancom_offers_specific", "https://www.vodafone.ro/clauzele-specifice-contractului-la-distanta");
    }

    public static String getLinkContractDetails() {
        return getLabelWithPrimaryKey("link_contract_details", "https://www.vodafone.ro/clauze-contract-incheiat-la-distanta");
    }

    public static String getLinkContractDetailsAccepted() {
        return getLabelWithPrimaryKey("link_contract_details_accepted", "https://www.vodafone.ro/clauzele-specifice-contractului-la-distanta");
    }

    public static String getIplanetCookieName() {
        return getLabelWithPrimaryKey("iplanet_cookie_name", "iPlanetDirectoryPro");
    }

    public static String getIplanetCookiePath() {
        return getLabelWithPrimaryKey("iplanet_cookie_path", "/");
    }

    public static String getIplanetCookieMaxage() {
        return getLabelWithPrimaryKey("iplanet_cookie_maxage", "1296000");
    }

    public static String getNotificationTitle(){
        return getLabelWithPrimaryKey("notificationTitle","Notificări de la Vodafone");
    }

    public static String getLoyaltyNotificationTitle(){
        return getLabelWithPrimaryKey("loyalty_notification_title","Vodafone Happy Mall");
    }

    public static String getLoyaltyNotificationDescrption(){
        return getLabelWithPrimaryKey("loyalty_notification_description","Bifând această opțiune, vei primi notificări privind ultimele oferte, recomandări și mesaje relevante din Programul Happy Mall, direct pe pagina principală a telefonului. Debifând această opțiune, vei primi în continuare notificări generale din aplicația My Vodafone.");

    }

    public static String getNotificationsMessage1() {
        return getLabelWithPrimaryKey("notifications_message1", "Cu această opțiune pornită, vei primi ultimele noutăți, recomandări personalizate și alte mesaje relevante direct pe ecranul principal.");
    }

    public static String getNotificationsMessage2() {
        return getLabelWithPrimaryKey("notifications_message2", "Chiar dacă ai această opțiune oprită, tot vei primi mesaje legate de factura ta.");
    }

    public static String getOverlayTitle() {
        return getLabelWithPrimaryKey("overlayTitle", "Eşti sigur?");
    }

    public static String getNotificationsOverlayButtonTurnOff() {
        return getLabelWithPrimaryKey("notifications_overlayButtonturnOff", "Da, oprește");
    }

    public static String getPolicyOverlayButtonturnOff() {
        return getLabelWithPrimaryKey("policy_overlayButtonturnOff", "Da, dezactivează");
    }

    public static String getNotificationsOverlayText() {
        return getLabelWithPrimaryKey("notifications_overlayText", "Dacă oprești această opțiune nu vom mai putea să-ți trimitem oferte de marketing și vânzări.");
    }

    public static String getOverlaySubtext() {
        return getLabelWithPrimaryKey("overlaySubtext", "Dacă oprești această opțiune nu vom mai putea să îmbunătățim serviciile prin colectarea datelor tale.");
    }

    public static String getOverlaySubtext2() {
        return getLabelWithPrimaryKey("overlaySubtext2", "Nu vei mai avea posibilitatea de a testa viteza internetului tău, de a verifica consumul de date al aplicațiilor tale sau de a primi asistență personalizată și recomandări.");
    }

    public static String getEnableServicesAndRecomandationsToast() {
        return getLabelWithPrimaryKey("enable_services_and_recomandations_toast", "În scopul de a oferi servicii personalizate și recomandări, opțiunea de optimizare a rețelei este de asemenea activată.");
    }

    public static String getOverlayButtonKeepOn() {
        return getLabelWithPrimaryKey("overlayButtonkeepOn", "Păstrează opţiunea pornită");
    }

    public static String getOverlayButtonTurnOff() {
        return getLabelWithPrimaryKey("overlayButtonturnOff", "Opreşte");
    }

    public static String getElectronicMediaTypeCD() {
        return getLabelWithPrimaryKey("electronicMediaTypeCD", "Primeşti factura tiparită şi pe CD cu detalierea numerelor de pe cont, iar pentru fiecare număr se emite factură separată.");

    }

    public static String getElectronicInactivBillTextNotAllowedUsers() {
        return getLabelWithPrimaryKey("electronicInactivBillTextNotAllowedUsers", "Momentan primești factura prin poștă. Dacă dorești să primești factura și prin e-mail, te rugăm să introduci adresa de e-mail și să apeși butonul Trimite.");
    }

    public static String getElectronicActivBillTextNotAllowedUsers() {
        return getLabelWithPrimaryKey("electronicActivBillTextNotAllowedUsers", "Momentan primești factura pe e-mail-ul administratorului de cont. Dacă dorești să primești factura și pe o altă adresă de e-mail, te rugăm să introduci adresa de e-mail și să apeși Trimite.");
    }

    public static String getElectronicInactivBillTextAllowedUsers() {
        return getLabelWithPrimaryKey("electronicInactivBillTextAllowedUsers", "Momentan primești factura prin poștă. Dacă dorești să primești factura prin e-mail, te rugăm să introduci adresa de e-mail.");
    }

    public static String getElectronicActivBillTextAllowedUsers() {
        return getLabelWithPrimaryKey("electronicActivBillTextAllowedUsers", "Momentan primești factura pe e-mail. Dacă dorești să primești factura și pe altă adresă de e-mail, te rugăm să introduci adresa de e-mail și să apeși Trimite.");
    }


    public static String getElectronicBillActiv() {
        return getLabelWithPrimaryKey("electronic_bill_activ", "Activă");
    }

    public static String getElectronicBillInactiv() {
        return getLabelWithPrimaryKey("electronic_bill_inactiv", "Inactivă");
    }

    public static String getBack() {
        return getLabelWithPrimaryKey("back", "Înapoi");
    }

    public static String getChangePassword() {
        return getLabelWithPrimaryKey("changePassword", "Schimbă parola");
    }

    public static String getOldPassword() {
        return getLabelWithPrimaryKey("oldPassword", "parola actuală");
    }

    public static String getnewPassword() {
        return getLabelWithPrimaryKey("newPassword", "parola nouă");
    }

    public static String getConfirmPassword() {
        return getLabelWithPrimaryKey("confirmPassword", "confirmă parola");
    }

    public static String getOldPasswordB() {
        return getLabelWithPrimaryKey("oldPasswordB", "Parola actuală");
    }

    public static String getNewPasswordB() {
        return getLabelWithPrimaryKey("newPasswordB", "Parola nouă");
    }

    public static String getConfirmPasswordB() {
        return getLabelWithPrimaryKey("confirmPasswordB", "Confirmă parola");
    }

    public static String getEnrollNow() {
        return getLabelWithPrimaryKey("enroll_now", "Înscrie-te acum");
    }

    public static String getphone_number() {
        return getLabelWithPrimaryKey("phone_number", "Număr de telefon");
    }

    public static String getAccept_Tc1() {
        return getLabelWithPrimaryKey("accept_tc1", "Sunt de acord cu ");
    }

    public static String getAccept_Tc2() {
        return getLabelWithPrimaryKey("accept_tc2", "<u>Termenii şi Condiţiile</u>");
    }

    public static String getBadPasswordConfimation() {
        return getLabelWithPrimaryKey("bad_password_confimation", "Câmpurile nu coincid.");
    }

    public static String gethint_CNP() {
        return getLabelWithPrimaryKey("hint_CNP", "1890526112959");
    }

    public static String getSimCurrentStatus() {
        return getLabelWithPrimaryKey("sim_current_status", "Status curent: ");
    }

    public static String getSimLockButton() {
        return getLabelWithPrimaryKey("sim_lock_button", "Blochează");
    }

    public static String getSimUnlockButton() {
        return getLabelWithPrimaryKey("sim_unlock_button", "Deblochează");
    }

    public static String getSimStatusBlocked() {
        return getLabelWithPrimaryKey("sim_status_blocked", "Blocat");
    }

    public static String getSimStatusUnblocked() {
        return getLabelWithPrimaryKey("sim_status_unblocked", "Deblocat");
    }

    public static String getBadPhoneNumber() {
        return getLabelWithPrimaryKey("bad_phone_number", "Te rugăm să introduci un număr de telefon valid.");
    }

    public static String getSmallErrorMessage() {
        return getLabelWithPrimaryKey("small_error_message", "Serviciu momentan indisponibil. \nApasă aici pentru a reîncerca.");
    }

    public static String getSimpleSmallError() {
        return getLabelWithPrimaryKey("simple_small_error", "Serviciu momentan indisponibil!");
    }

    public static String getPostponePayment() {
        return getLabelWithPrimaryKey("postpone_payment", "Amână plata");
    }

    public static String getEnrollHeaderTitle() {
        return getLabelWithPrimaryKey("enroll_header_title", "Înscrie-te în YOU");
    }

    public static String getEnrollHeaderText() {
        return getLabelWithPrimaryKey("enroll_header_text", "Bine ai venit în YOU! De acum eşti înscris în YOU şi te poţi bucura de ofertă.");
    }

    public static String getConfidentialityInformationSection() {
        return getLabelWithPrimaryKey("confidentiality_information_section", "Putem face recomandări și servicii personalizate bazate pe locația ta, calitatea și conectivitatea serviciilor și consumul de date al aplicațiilor tale.");
    }

    public static String getPrivacyPageTitle(){
        return getLabelWithPrimaryKey("privacy_page_title", "Politica de confidențialitate Vodafone");
    }

    public static String getMinorAccountTitle() {
        return getLabelWithPrimaryKey("minor_account_title", "Cont minor");
    }
    public static String getActiveMinor() {
        return getLabelWithPrimaryKey("active_minor", "Activ");
    }

    public static String getInactiveMinor() {
        return getLabelWithPrimaryKey("inactive_minor", "Inactiv");
    }

    public static String getVodafonePermissionsTitle() {
        return getLabelWithPrimaryKey("vodafone_permissions_title", "Permisiuni Vodafone");
    }

    public static String getVodafonePartenersTitle() {
        return getLabelWithPrimaryKey("vodafone_parteners_title", "Permisiuni Parteneri Vodafone");
    }

    public static String getSidErrorMessage() {
        return getLabelWithPrimaryKey("sid_error_message", "Informaţiile pentru acest număr de telefon sunt indisponibile.");
    }

    public static String getGdprPermissionsNotFoundErrorMessage() {
        return getLabelWithPrimaryKey("gdpr_permissions_not_found_error_message", "Ne pare rău. Utilizatorul selectat nu poate seta permisiuni.");
    }

    public static String getPermissionsInstructionalText() {
        return getLabelWithPrimaryKey("permissions_instructional_text", "Ai posibilitatea să îți exprimi acordul pentru prelucrarea datelor tale cu caracter personal, inclusiv a codului numeric personal, în condițiile descrise în \"Informarea privind prelucrarea datelor cu caracter personal\" disponibilă pe www.vodafone.ro/privacy. Vei beneficia astfel de servicii și oferte personalizate, adaptate preferințelor tale, astfel încât să beneficiezi de cea mai bună experiență Vodafone.");
    }

    public static String getMinorAccountCardTitle() {
        return getLabelWithPrimaryKey("minor_account_card_title", "Cont de minor");
    }

    public static String getMinorAccountCardSubtextMinor() {
        return getLabelWithPrimaryKey("minor_account_card_subtext_minor", "Aceste servicii vor fi utilizate de către un minor.");
    }

    public static String getMinorAccountCardSubtextAdult() {
        return getLabelWithPrimaryKey("minor_account_card_subtext_adult", "Aceste servicii vor fi utilizate de către un adult.");
    }

    public static String getMinorAccountSecondCardSubText1() {
        return getLabelWithPrimaryKey("minor_account_second_card_subtext1", "Modificarea contului de minor se poate face de către titularul de contract.");
    }

    public static String getMinorAccountSecondCardSubText2() {
        return getLabelWithPrimaryKey("minor_account_second_card_subtext2", "Modificarea contului de minor se poate face de către titularul de contract în Magazinele Vodafone sau prin apel la *222.");
    }

    public static String getCommercialCommunicationTitle() {
        return getLabelWithPrimaryKey("commercial_communication_title", "Comunicări Comerciale");
    }

    public static String getCommercialCommunicationSubtextVodafone() {
        return getLabelWithPrimaryKey("commercial_communication_subtext_vodafone", "Contactarea pentru marketing şi transmiterea de oferte comerciale se poate face prin urmatoarele canale de comunicare:");
    }

    public static String getCommercialCommunicationSubtextParteners() {
        return getLabelWithPrimaryKey("commercial_communication_subtext_parteners", "Contactarea pentru marketing şi transmiterea de oferte comerciale, din partea Vodafone în cadrul parteneriatelor sale, se poate face prin următoarele canale de comunicare:");
    }

    public static String getSurveyTitle() {
        return getLabelWithPrimaryKey("survey_title", "Sondaj de opinie");
    }

    public static String getSurveySubtextVodafone() {
        return getLabelWithPrimaryKey("survey_subtext_vodafone", "Acord/Sondaj pentru transmiterea sugestiilor de îmbunătăţire a relaţiei cu Vodafone.");
    }

    public static String getSurveySubtextParteners() {
        return getLabelWithPrimaryKey("survey_subtext_parteners", "Acord/Sondaj pentru transmiterea sugestiilor de îmbunătăţire a relaţiei cu Vodafone şi partenerii.");
    }

    public static String getBasicProfileTitle() {
        return getLabelWithPrimaryKey("basic_profile_title", "Creare de profil");
    }

    public static String getBasicProfileSubtextVodafone() {
        return getLabelWithPrimaryKey("basic_profile_subtext_vodafone", "Întelegând modul în care utilizezi produsele şi serviciile Vodafone, iţi putem îmbunătaţi experienţa în reţea şi iţi putem crea oferte adaptate nevoilor tale.");
    }

    public static String getBasicProfileSubtextParteners() {
        return getLabelWithPrimaryKey("basic_profile_subtext_parteners", "Ocazional ne dorim să te informăm despre anumite oferte ale partenerilor noştri, care considerăm că ar putea fi de interes pentru tine. Înţelegând modul în care utilizezi produsele şi serviciile Vodafone, partenerii noştri iţi pot recomanda oferte şi servicii de interes pentru tine, în aria ta de acoperire.");
    }

    public static String getSmsMmsPushTitle() {
        return getLabelWithPrimaryKey("sms_mms_push_title", "Mesaje SMS/MMS/Push USSD ");
    }

    public static String getEmailTitle() {
        return getLabelWithPrimaryKey("email_title", "Email");
    }

    public static String getPostTitle() {
        return getLabelWithPrimaryKey("post_title", "Poștă");
    }

    public static String getOutboundCallTitle() {
        return getLabelWithPrimaryKey("outbound_call_title", "Apelare Telefonică");
    }

    public static String getNetworkDataTitle() {
        return getLabelWithPrimaryKey("network_data_title", "Date de trafic şi localizare");
    }

    public static String getNetworkDataSubtextVodafone() {
        return getLabelWithPrimaryKey("network_data_subtext_vodafone", "Prin analiza datelor de trafic şi localizare în rețea iţi putem recomanda oferte şi servicii de interes pentru ține, în aria ta de acoperire. De asemenea, iţi putem îmbunătăţi experienţa generală în reţeaua Vodafone.");
    }

    public static String getNetworkDataSubtextParteners() {
        return getLabelWithPrimaryKey("network_data_subtext_parteners", "Ocazional ne dorim să te informăm despre anumite oferte ale partenerilor noştri, care considerăm că ar putea fi de interes pentru tine. Prin analiza datelor de trafic şi localizare în reţea, partenerii noştri iţi pot recomanda oferte şi servicii de interes pentru tine, în aria ta de acoperire.");
    }

    public static String getOnlineDataTitle() {
        return getLabelWithPrimaryKey("online_data_title", "Date de navigare pe internet");
    }

    public static String getOnlineDataSubtextVodafone() {
        return getLabelWithPrimaryKey("online_data_subtext_vodafone", "Prin analiza datelor de trafic şi localizare în retea iţi putem recomanda oferte şi servicii de interes pentru tine, în aria ta de acoperire. De asemenea, iţi putem îmbunătăţi experienţa generală in reţeaua Vodafone.");
    }

    public static String getOnlineDataSubtextParteners() {
        return getLabelWithPrimaryKey("online_data_subtext_parteners", "Ocazional ne dorim să te informăm despre anumite oferte ale partenerilor noştri, care considerăm că ar putea fi de interes pentru tine. Prin analiza datelor de navigare pe internet partenerii noştri iţi pot recomanda oferte şi servicii de interes pentru tine, în aria ta de acoperire.");
    }

    public static String getEditMinorAccountTitle() {
        return getLabelWithPrimaryKey("edit_minor_account_title", "Cont minor");
    }

    public static String getEditMinorAccountSubtext1() {
        return getLabelWithPrimaryKey("edit_minor_account_subtext1", "Prin completarea acestor date confirm că, în calitate de reprezentant legal* al utilizatorului minor, sunt de acord să primesc comunicări comerciale ca urmare a prelucrării datelor cu caracter personal ale utilizatorului minor.");
    }

    public static String getEditMinorAccountSubtext2() {
        return getLabelWithPrimaryKey("edit_minor_account_subtext2", "*Reprezentantul legal poate fi: părintele, sau, după caz, tutorele.");
    }

    public static String getEditMinorAccountEmailInvalidFormat(){
        return getLabelWithPrimaryKey("edit_minor_account_email_invalid_format","Te rugăm să introduci o adresă de email validă, de exemplu ion.popescu@mail.com.");
    }

    public static String getEditMinorAccountTelephoneNumberInvalidFormat() {
        return getLabelWithPrimaryKey("edit_minor_account_telephone_number_format","Te rugăm să introduci un număr corect de contact (fix sau mobil) în format naţional sau internaţional.");
    }

    public static String getInstructionalText1() {
        return getLabelWithPrimaryKey("instructional_text1","Activează pentru a putea primi oferte personalizate, special create pentru tine.");
    }

    public static String getInstructionalText2() {
        return getLabelWithPrimaryKey("instructional_text2","Această opţiune nu poate fi activată pe acest cont.");
    }

    public static String getInstructionalText2ForCommercialCommunications() {
        return getLabelWithPrimaryKey("instructional_text2_for_commercial_communications","Aceste opţiuni nu pot fi activate pe acest cont.");
    }

    public static String getInstructionalText3() {
        return getLabelWithPrimaryKey("instructional_text3","Această opţiune nu poate fi activată în cazul serviciilor utilizate de către un minor.");
    }

    public static String getInstructionalText3ForCommercialCommunications() {
        return getLabelWithPrimaryKey("instructional_text3_for_commercial_communications","Aceste opţiuni nu pot fi activate în cazul serviciilor utilizate de către un minor.");
    }

    public static String getInstructionalText4() {
        return getLabelWithPrimaryKey("instructional_text4","Această permisiune poate fi activată doar dacă permisiunea “Creare de profil” este activă.");
    }

    public static String getBasicProfileSeeDetailsVodafone() {
        return getLabelWithPrimaryKey("basic_profile_see_details_vodafone","Datele tale de contact şi de utilizare a serviciilor vor putea fi prelucrate pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi să anticipăm preferinţele personale şi comportamentele cu privire la serviciile utilizate, astfel încât să beneficiaţi de cea mai bună experienţă Vodafone.");
    }

    public static String getBasicProfileSeeDetailsParteners() {
        return getLabelWithPrimaryKey("basic_profile_see_details_parteners","Datele tale de contact şi de utilizare a serviciilor vor putea fi prelucrate de Vodafone în cadrul parteneriatelor sale, pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi anticipăm preferinţele personale şi  comportamentele cu privire la serviciile utilizate, astfel încât să beneficiezi de cea mai bună experienţă Vodafone.");
    }

    public static String getNetworkDataSeeDetailsVodafone() {
        return getLabelWithPrimaryKey("network_data_see_details_vodafone","Datele de Trafic şi de Localizare rezultate din utilizarea serviciilor vor putea fi prelucrate pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi să anticipăm preferinţele personale şi comportamentele cu privire la serviciile utilizate, astfel încât să beneficiezi de cea mai bună experienţă Vodafone.");
    }

    public static String getNetworkDataSeeDetailsParteners() {
        return getLabelWithPrimaryKey("network_data_see_details_parteners","Datele de Trafic şi de Localizare rezultate din utilizarea serviciilor vor putea fi prelucrate de Vodafone în cadrul parteneriatelor sale, pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi să anticipăm preferinţele personale si  comportamentele cu privire la serviciile utilizate, astfel încât să beneficiezi de cea mai bună experienţă Vodafone.");
    }

    public static String getOnlineDataSeeDetailsVodafone() {
        return getLabelWithPrimaryKey("online_data_see_details_vodafone","Datele de Trafic şi de Localizare rezultate din utilizarea serviciilor vor putea fi prelucrate pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi să anticipăm preferinţele personale şi comportamentele cu privire la serviciile utilizate, astfel încât să beneficiezi de cea mai bună experienţă Vodafone.");
    }

    public static String getOnlineDataSeeDetailsParteners() {
        return getLabelWithPrimaryKey("online_data_see_details_parteners","Datele de navigare pe Internet rezultate din utilizarea serviciilor vor putea fi prelucrate de Vodafone în cadrul parteneriatelor sale,  pentru marketing şi transmiterea de oferte comerciale, prin crearea de profiluri de utilizator. Acestea ne vor ajuta să analizăm şi să anticipăm preferinţele personale şi comportamentele cu privire la serviciile utilizate, astfel încât să beneficiezi de cea mai bună experienţă Vodafone.");
    }

    public static String getSurveySeeDetailsVodafone() {
        return getLabelWithPrimaryKey("survey_see_details_vodafone","Ne dorim să îmbunătăţim constant serviciile şi produsele oferite, de aceea te putem contacta pentru a ne transmite sugestiile şi impresiile tale.");
    }

    public static String getSurveySeeDetailsParteners() {
        return getLabelWithPrimaryKey("survey_see_details_parteners","Ne dorim să îmbunătăţim constant serviciile şi produsele oferite de partenerii Vodafone, de aceea te putem contacta pentru a ne transmite sugestiile şi impresiile tale.");
    }

    public static String getRegisteredRequest1() {
        return getLabelWithPrimaryKey("registered_request1","Cererea a fost înregistrată");
    }

    public static String getRegisteredRequest2() {
        return getLabelWithPrimaryKey("registered_request2","Cererea este în curs de procesare.");
    }

    public static String getRegisteredRequest3() {
        return getLabelWithPrimaryKey("registered_request3","Cererea ta va fi procesată în scurt timp.");
    }

    public static String getPhoneNumberLabel() {
        return getLabelWithPrimaryKey("phone_number_label","Telefon reprezentant legal");
    }

    public static String getEmailAddressLabel() {
        return getLabelWithPrimaryKey("email_address_label","Email reprezentant legal");
    }

    public static String getMinorBirthdateLabel() {
        return getLabelWithPrimaryKey("minor_birthdate_label","Dată naştere minor");
    }

    /* Replacement Labels */

    public static String getReplacementTitleCard() {
        return getLabelWithPrimaryKey("replacement_title_card","Schimbare cartelă SIM");
    }

    public static String getReplacementSimLabel() {
        return getLabelWithPrimaryKey("replacement_sim_series_label","Seria SIM");
    }

    public static String getReplacementPhoneNumberLabel() {
        return getLabelWithPrimaryKey("replacement_phone_number_label","Număr de contact");
    }

    public static String getReplacementSimError() {
        return getLabelWithPrimaryKey("replacement_sim_series_error","Seria SIM este greşită");
    }

    public static String getReplacementPhoneNumberError() {
        return getLabelWithPrimaryKey("replacement_phone_number_error","Formatul numărului introdus nu este valid (ex. 0xxxxxxxxx)");
    }

    public static String getReplacementButtonText() {
        return getLabelWithPrimaryKey("replacement_button_text","Modifică seria SIM");
    }

    public static String getReplacementSubTitleCBU() {
        return getLabelWithPrimaryKey("replacement_sub_title_cbu","Foloseşte această funcţionalitate doar dacă ai SIM-ul nou. Dacă nu îl ai, poţi primi un SIM nou, cu acelaşi număr de telefon, în magazinele sau la dealerii Vodafone. Pentru că vrem să îți răsplătim fidelitatea, schimbarea de SIM efectuată din contul tău MyVodafone este gratuită.");
    }

    public static String getReplacementSubTitleEbuCustomerSegment1() {
        return getLabelWithPrimaryKey("replacement_sub_title_ebu_customer_segment1","Pentru că vrem să îți răsplătim fidelitatea, schimbarea de SIM efectuată din contul tău MyVodafone este gratuită. Dacă doriți un nou SIM, vă rugăm să faceți o cerere de schimbare într-un magazin Vodafone sau la un dealer autorizat Vodafone.");
    }

    public static String getReplacementSubTitleEbuCustomerSegment2() {
        return getLabelWithPrimaryKey("replacement_sub_title_ebu_customer_segment2","Pentru că vrem să îți răsplătim fidelitatea, schimbarea de SIM pentru contul tău este gratuită. Dacă doriți să activați un număr nou sau să schimbați SIM-ul unui număr existent, însă nu aveți un SIM nou, vă rugăm să vă contactați specialistul de cont.");
    }

    public static String getReplacementConfirmationTitle() {
        return getLabelWithPrimaryKey("replacement_confirmation_title","Eşti sigur?");
    }

    public static String getReplacementConfirmationSubTitle() {
        return getLabelWithPrimaryKey("replacement_confirmation_sub_title","Confirmă că doreşti să schimbi Seria SIM introdusă anterior.");
    }

    public static String getReplacementToastConfirmation() {
        return getLabelWithPrimaryKey("replacement_toast_confirmation_message","Cererea a fost înregistrată!");
    }

    public static String getReplacementVovConfirmation() {
        return getLabelWithPrimaryKey("replacement_vov_confirmation_message","Cererea este în curs de procesare. Vei primi un SMS odată cu procesarea schimbării.");
    }

}
