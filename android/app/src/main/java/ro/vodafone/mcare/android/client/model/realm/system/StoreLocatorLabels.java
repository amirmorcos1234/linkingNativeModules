package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/7/2017.
 */

public class StoreLocatorLabels extends AppLabels {

    public static String getStoreLocatorShowBasicErrorCard(){
        return getLabelWithPrimaryKey("store_locator_show_basic_error_card","Nu putem identifica locația ta, caută magazinul dorit în lista de mai jos.");
    }

    public static String getStoreLocatorLoadingMapTitle(){
        return getLabelWithPrimaryKey("store_locator_loading_map_title","Încărcare Hartă");
    }

    public static String getStoreLocatorLoadingMapSubtext(){
        return getLabelWithPrimaryKey("store_locator_loading_map_subtext","Pentru a putea încărca harta, aplicația va consuma din traficul inclus în opțiunile tale.");
    }

    public static String getStoreLocatorShowError(){
        return getLabelWithPrimaryKey("store_locator_show_error","Nu s-au putut descărca magazinele. Apasă aici pentru a reîncerca.");

    }

    public static String getStoreLocatorLocationAccessTitle(){
        return getLabelWithPrimaryKey("store_locator_location_access_title","Acces locație");
    }

    public static String getStoreLocatorLocationAccessSubtext(){
        return getLabelWithPrimaryKey("store_locator_location_access_subtext","Calea către Magazinele Vodafone devine mai simplă permițând aplicației să acceseze locația ta.");
    }

    public static String getPermitLocationsButton(){
        return getLabelWithPrimaryKey("permit_locations_button","Modificare permisiune");
    }

    public static String getCancelButton(){
        return getLabelWithPrimaryKey("cancel_button","Continuă fără localizare");
    }

    public static String getStoreLocatorNavigationHeaderTitle(){
        return getLabelWithPrimaryKey("store_locator_navigation_header_title","Magazine Vodafone");
    }

    public static String getAcceptDataConsumption(){
        return getLabelWithPrimaryKey("accept_data_consumption","Ok");
    }

}


