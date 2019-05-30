package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 10/23/2017.
 */

public class YourProfileLabels extends AppLabels {

    public static String getPayBillDelayPageTitle(){
        return getLabelWithPrimaryKey("get_pay_bill_delay_page_title", "Amânare de plată");
    }
    public static String getPayBillDelayCardTitle(){
        return getLabelWithPrimaryKey("get_pay_bill_delay_card_title", "Amânare plată");
    }

    public static String getPayBillDelayCardSubText(){
        return getLabelWithPrimaryKey("get_pay_bill_delay_card_sub_text", "Amână plata facturii");
    }

    public static String getPayBillConfirmationCardTitle(){
        return getLabelWithPrimaryKey("pay_bill_confirmation_card_title","Confirmare plată");
    }

    public static String getPayBillConfirmationCardSubTitle(){
        return getLabelWithPrimaryKey("pay_bill_confirmation_card_sub_text","Confirmă plata facturii");
    }

    ///// ION Unlimited Labels /////

    public static String getUnlimitedIonCardTitle() {
        return getLabelWithPrimaryKey("unlimited_ion_card_title", "Nelimitat Italia");
    }

    public static String getUnlimitedIonCardSubTitle() {
        return getLabelWithPrimaryKey("unlimited_ion_card_sub_text", "");
    }

    public static String getUnlimitedIonSystemErrorLayout() {
        return getLabelWithPrimaryKey("unlimited_ion_system_error_layout", "Serviciu momentan indisponibil. Apasă pentru a reîncerca.");
    }

    public static String getUnlimitedIonSystemErrorToast() {
        return getLabelWithPrimaryKey("unlimited_ion_system_error_toast", "Serviciu momentan indisponibil");
    }

    public static String getUnlimitedPrepaidIonInactiveText() {
        return getLabelWithPrimaryKey("unlimited_prepaid_ion_inactive","Poţi vorbi nelimitat cu numerele dorite din reţeaua Vodafone Italia şi Spania dacă ai activă o extraopţiune de minim 6 euro credit. Adaugă-ţi acum o extraopţiune!");
    }

    public static String getUnlimitedCBUIonInactiveText1() {
        return getLabelWithPrimaryKey("unlimited_cbu_ion_inactive_text1","Oferta Nelimitat Vodafone Italia include apeluri nelimitate către o listă de 5 numere internaţionale preferate din Vodafone Italia.");
    }

    public static String getUnlimitedCBUIonInactiveText2() {
        return getLabelWithPrimaryKey("unlimited_cbu_ion_inactive_text2","Aceasta se poate activa prin apel la *100#, selectarea Roaming şi Internaţional / Internaţional / Opţiuni, sectiunea Nelimitat Vodafone Italia.");
    }

    public static String getUnlimitedCBUIonInactiveText3() {
        return getLabelWithPrimaryKey("unlimited_cbu_ion_inactive_text3","Poţi beneficia de această ofertă în termen de 60 de zile de la achiziţia sau reînnoirea unui abonament de voce (de minim 10 euro).");
    }

    public static String getUnlimitedCBUIonInactiveText4() {
        return getLabelWithPrimaryKey("unlimited_cbu_ion_inactive_text4","Oferta este gratuită şi are un termen de valabilitate de 3 luni de la activare.");
    }

    public static String getUnlimitedNoPhoneNumberRegisteredText() {
        return getLabelWithPrimaryKey("unlimited_no_phone_number_registered","Nu ai niciun număr de telefon înregistrat.");
    }

    public static String getUnlimitedMsisdnListMaximumLimitTextPart1() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_maximum_limit_text_part1","Ai atins limita de");
    }

    public static String getUnlimitedMsisdnListMaximumLimitTextIsOne() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_maximum_limit_is_one","număr internaţional preferat pe care îl poţi adăuga în listă.");
    }

    public static String getUnlimitedMsisdnListMaximumLimitTextLessThanTwenty() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_maximum_limit_less_than_twenty","numere internaţionale preferate pe care le poţi adăuga în listă.");
    }

    public static String getUnlimitedMsisdnListMaximumLimitTextMoreThanTwenty() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_maximum_limit_more_than_twenty","de numere internaţionale preferate pe care le poţi adăuga în listă.");
    }

    public static String getUnlimitedMsisdnListCardTitle() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_card_title","Listă numere preferate");
    }

    public static String getUnlimitedMsisdnListCardSubTitlePart1() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_card_sub_text_part1","Poți adăuga un număr maxim de");
    }

    public static String getUnlimitedMsisdnListCardSubTitleTextIsOne() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_card_sub_text_is_one","număr.");
    }

    public static String getUnlimitedMsisdnListCardSubTitleTextLessThanTwenty() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_card_sub_text_less_than_twenty","numere.");
    }

    public static String getUnlimitedMsisdnListCardSubTitleTextMoreThanTwenty() {
        return getLabelWithPrimaryKey("unlimited_msisdn_list_card_sub_text_more_than_twenty","de numere.");
    }

    public static String getUnlimitedAddMsisdnCardTitle() {
        return getLabelWithPrimaryKey("unlimited_add_msisdn_card_title","Introdu numărul de telefon");
    }

    public static String getUnlimitedMsisdnNotVodafoneError() {
        return getLabelWithPrimaryKey("unlimited_msisdn_not_vodafone_error","Numărul ales nu este in reţeaua Vodafone. Îl poţi apela la tarife internaţionale standard.");
    }

    public static String getUnlimitedMsisdnNotInListError() {
        return getLabelWithPrimaryKey("unlimited_msisdn_not_in_list_error","Acest număr este deja înregistrat.");
    }

    public static String getUnlimitedRegisteredRequestForPutVov() {
        return getLabelWithPrimaryKey("unlimited_registered_request_for_put_vov","Numărul a fost înregistrat. Aşteaptă confirmarea noastră prin SMS.");
    }

    public static String getUnlimitedRegisteredRequestForPutToast() {
        return getLabelWithPrimaryKey("unlimited_registered_request_for_put_toast","Numărul a fost înregistrat!");
    }

    public static String getUnlimitedRegisteredRequestForDeleteVov() {
        return getLabelWithPrimaryKey("unlimited_registered_request_for_delete_vov","Cererea ta a ajuns la noi. În curând vei primi un SMS de confirmare.");
    }

    public static String getUnlimitedRegisteredRequestForDeleteToast() {
        return getLabelWithPrimaryKey("unlimited_registered_request_for_delete_toast","Cererea a fost înregistrată!");
    }

    public static String getUnlimitedConfirmationOverlayTitle() {
        return getLabelWithPrimaryKey("unlimited_confirmation_overlay_title","Eşti sigur?");
    }

    public static String getUnlimitedConfirmationOverlaySubTitle() {
        return getLabelWithPrimaryKey("unlimited_confirmation_overlay_sub_text","Doreşti să ştergi acest număr de telefon?");
    }

    public static String getUnlimitedIsTobeOrIsVmbText() {
        return getLabelWithPrimaryKey("unlimited_prepaid_is_tobe_or_is_vmb_text","Acest serviciu se adresează clienţilor cu Cartelă Vodafone sau cu servicii de voce şi SMS.");
    }

    public static String getUnlimitedMandatoryFieldError() {
        return getLabelWithPrimaryKey("unlimited_mandatory_field_error","Câmp obligatoriu.");
    }

    public static String getUnlimitedInstructionalText() {
        return getLabelWithPrimaryKey("unlimited_instructional_text","");
    }


    public static String getICRCardTitle() {
        return getLabelWithPrimaryKey("icr_card_title", "Apeluri internaţionale");
    }


}
