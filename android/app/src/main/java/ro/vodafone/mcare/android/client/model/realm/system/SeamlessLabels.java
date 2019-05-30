package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/8/2017.
 */

public class SeamlessLabels extends AppLabels {

    public static String getLoginEasierWithVodafoneLabel(){
        return getLabelWithPrimaryKey("login_easier_with_vodafone_label","Te poți autentifica rapid în aplicație dacă ești conectat la rețeaua Vodafone.");
    }

    public static String getEnterButonLabel(){
        return getLabelWithPrimaryKey("enter_buton_label","Intră");
    }

    public static String getLoginWithVdfNetWorkInsteadWifiLabel(){
        return getLabelWithPrimaryKey("login_with_vdf_network_instead_wifi_label","Deconectează-te de la Wi-Fi ca să te loghezi folosind rețeaua Vodafone.");
    }

    public static String getLoginWitVodafoneAccountLabel (){
        return  getLabelWithPrimaryKey("login_with_vodafone_account_label" ,"Autentifică-te cu un cont Vodafone");
    }

    public static String getAuthenticationButtonLabel(){
        return getLabelWithPrimaryKey("authentication_button_label","Autentificare");
    }

    public static String getLoginEasierTitle(){
        return getLabelWithPrimaryKey("login_easier_title","Autentificare automată");
    }

    public static String getSkipUsualRegistrationProcessLabel(){
        return getLabelWithPrimaryKey("skip_usual_registration_precess_label","Sari peste procesul de log-in și înregistrare prin scurta oprire a Wi-Fi-ului și conectându-te la rețeaua Vodafone.");
    }

    public static String getVodafoneRememberYourDetailsLabel() {
        return getLabelWithPrimaryKey("vodafone_remember_your_details_label","Nu-ți face griji, va fi necesar să faci asta o singură dată. Aplicația My Vodafone va reține detaliile tale pentru a te autentifica în viitor prin Wi-Fi sau prin rețeaua Vodafone.");
    }

    public static String getWifiSettingsButtonLabel() {
        return getLabelWithPrimaryKey("wifi_settings_button_label" ,"Setări Wi-Fi");
    }

    public static String getDontTurnOffWifiLabel() {
        return getLabelWithPrimaryKey("dont_wont_turn_off_wifi_label" ,"Nu vreau să opresc Wi-Fi.");
    }

    public static String getRegisterButtonLabel() {
        return getLabelWithPrimaryKey("register_button_label" ,"Înregistrare");
    }

    public static String  getConnectedToVodafoneNetworkLabel() {
        return getLabelWithPrimaryKey ("connected_to_vodafone_network_label","Te-ai conectat la rețeaua Vodafone. Opțiunea de conectare automată va fi activată. Poți dezactiva această opțiune din meniul de setări.");
    }

    public static String  getDisconnectedVodafoneNetworkLabel() {
        return getLabelWithPrimaryKey ("connected_to_vodafone_network_label","Opțiunea de conectare automată este dezactivată pentru acest cont. O poți activa dupa autentificare, din meniul setări.");
    }

    public static String getConfirmButtonLabel(){
        return getLabelWithPrimaryKey("confirm_button_label","Confirmă");
    }

    public static String getRevokeButtonLabel(){
        return getLabelWithPrimaryKey("confirm_button_label","Renunță");
    }
}
