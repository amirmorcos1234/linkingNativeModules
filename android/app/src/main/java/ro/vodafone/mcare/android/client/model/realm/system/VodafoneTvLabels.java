package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class VodafoneTvLabels extends AppLabels {
    public static String getVtvActiveDevicePageTitle(){
        return getLabelWithPrimaryKey("vtv_devices_page_title", "Dispozitive active");
    }

    public static String getVtvDeviceDetailsPageTitle(){
        return getLabelWithPrimaryKey("vtv_device_details_page_title", "Detalii dispozitiv");
    }

    public static String getVtvConfirmDeleteOverlayTitle(){
        return getLabelWithPrimaryKey("vtv_confirm_overlay_title", "Ești sigur?");
    }

    // TODO: 28.06.2018 modification
    public static String getVtvConfirmDeleteOverlayMessage(){
        return getLabelWithPrimaryKey("vtv_confirm_overlay_body", "Ești sigur că vrei să ștergi dispozitivul \"{device}\"?");
    }

    public static String getVtvConfirmDeleteButtonLabel(){
        return getLabelWithPrimaryKey("vtv_confirm_overlay_primary_button", "Confirmare");
    }

    public static String getVtvDeleteOverlayDismissButton(){
        return getLabelWithPrimaryKey("vtv_confirm_overlay_secondary_button", "Înapoi");
    }

    public static String getVtvLimitExceededOverlayTitle(){
        return getLabelWithPrimaryKey("vtv_restrictive_overlay_title", "Limită depăşită");
    }

    public static String getVtvLimitExceededOverlayMessage(){
        return getLabelWithPrimaryKey("vtv_restrictive_overlay_body", "Următoarea ştergere o poţi efectua pe data de {date}.");
    }

    public static String getVtvLimitExceededOverlayButtonLabel(){
        return getLabelWithPrimaryKey("vtv_restrictive_overlay_secondary_button", "Înapoi");
    }

    public static String getVtvSuccessDeleteVovMessage(){
        return getLabelWithPrimaryKey("vtv_vov_delete_success", "Dispozitivul a fost şters.");
    }

    public static String getVtvSuccesRenameVovMessage(){
        return getLabelWithPrimaryKey("vtv_vov_rename_success", "Dispozitivul a fost redenumit.");
    }

    // TODO: 28.06.2018
    public static String getVtvVovPrimaryButton(){
        return getLabelWithPrimaryKey("vtv_vov_primary_button", "Modifică");
    }

    // TODO: 28.06.2018
    public static String getVtvVovSecondaryButton(){
        return getLabelWithPrimaryKey("vtv_vov_secondary_button", "Ok, am înțeles");
    }

    public static String getVtvSuccessRenameToastMessage(){
        return getLabelWithPrimaryKey("vtv_toast_success", "Modificare salvată!");
    }
    /////-----------------------------

    public static String getVtvVovPrimaryButtonResetPin(){
        return getLabelWithPrimaryKey("vtv_vov_primary_button_reset_pin", "OK");
    }

    public static String getVtvSuccessResetPinVov(){
        return getLabelWithPrimaryKey("vtv_vov_reset_pin", "PINul a fost resetat la valoarea implicită: ");
    }

    public static String getVtvSuccessResetPinToast(){
        return getLabelWithPrimaryKey("vtv_toast_succes_reset_pin", "PINul a fost resetat!”");
    }

    public static String getVtvToastErrorMessage() {
        return getLabelWithPrimaryKey("vtv_toast_error_reset_pin", "Serviciu momentan indisponibil!");
    }

    public static String getGenericRetryErrorMessage() {
        return getLabelWithPrimaryKey("vtv_generic_retry_error_message", "Serviciu momentan indisponibil.\nApasă pentru a reîncerca.");
    }

    public static String getVTVResetPinTitleCard () {
        return getLabelWithPrimaryKey("vtv_reset_pin_title_card" ,"Resetează PIN");
    }

    public static String getVTVResetPinButtonText  () {
        return getLabelWithPrimaryKey("vtv_reset_pin_button_text" ,"Resetează PIN");
    }

    public static String getVTVResetPinTextCard  () {
        return getLabelWithPrimaryKey("vtv_reset_pin_text_card" ,"PINul va fi resetat la valoarea default.");
    }


    ////----------------------------------------------

    public static String getVodafoneTvNoDevicesMessage(){
        return getLabelWithPrimaryKey("vtv_no_devices_error", "Momentan nu ai dispozitive active.");
    }

    // TODO: 28.06.2018
    public static String getVtvNoOffersError(){
        return getLabelWithPrimaryKey("vtv_no_options_error","Momentan nu ai opțiuni active.");
    }

    public static String getVtvOfferCardCategory(){
        return getLabelWithPrimaryKey("vtv_services_category_name", "Vodafone TV");
    }

    public static String getVtvInformativeText(){
        return getLabelWithPrimaryKey("vtv_instructions", "Din această secţiune vei putea să îţi stergi sau editezi device-urile conectate la Vodafone TV. Poți avea până la {devicesLimit} dispozitive active simultan în toate categoriile.");
    }

    public static String getVtvDeviceLimitExceededInformativeText(){
        return getLabelWithPrimaryKey("vtv_instructions_restriction","În acest moment nu mai poţi adăuga alte dispozitive noi.");
    }

    public static String getVtvRenameDeviceButtonLabel(){
        return getLabelWithPrimaryKey("vtv_rename_device_button", "Salvează");
    }

    public static String getVtvDeleteDeviceButtonLabel(){
        return getLabelWithPrimaryKey("vtv_delete_device_button", "Șterge dispozitiv");
    }

    public static String getVtvEditDeviceLabel(){
        return getLabelWithPrimaryKey("vtv_edit_device_label", "Editare dispozitiv:");
    }

    public static String getVtvRenameInputRequiredError(){
        return getLabelWithPrimaryKey("vtv_empty_field_error", "Câmp obligatoriu!");
    }

    public static String getVtvRenameInputFormatError(){
        return getLabelWithPrimaryKey("vtv_device_name_format_error", "Sunt permise doar cifre şi litere.");
    }
    public static String getVtvRenameInputMinimumError(){
        return getLabelWithPrimaryKey("vtv_device_name_minimum_error", "Te rugăm să introduci cel puţin 4 caractere.");
    }

    public static String getVtvBeoRedirectButtonLabel(){
        return getLabelWithPrimaryKey("vtv_beo_button","Activează alte opțiuni");
    }

    public static String getVtvAppRedirectButtonLabel(){
        return getLabelWithPrimaryKey("vtv_app_button", "Deschide Vodafone TV");
    }

    // TODO: 28.06.2018  
    public static String getVtvAppVovLabel(){
        return getLabelWithPrimaryKey("vtv_actvation_vov_primary_button", "Vezi Vodafone TV");
    }

    // TODO: 28.06.2018  
    public static String getVtvActivationVovDismissButton(){
        return getLabelWithPrimaryKey("vtv_activation_vov_dismiss_button", "Ok");
    }


    public static String getVodafoneTvAvailableDevicesForActivation(){
        return getLabelWithPrimaryKey("vodafone_tv_available_devices", "dispozitive disponibile");
    }

    public static String getVodafoneTvCategorySubtitle(){
        return getLabelWithPrimaryKey("vtv_category_subtitle", "");
    }

    public static String getVodafoneTvDevicesAddedLabel(){
        return getLabelWithPrimaryKey("vodafone_tv_devices_added_at", "Adăugat pe %1$s Ora %2$s");
    }
}
