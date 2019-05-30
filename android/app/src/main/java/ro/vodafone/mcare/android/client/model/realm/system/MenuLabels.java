package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/8/2017.
 */

public class MenuLabels extends AppLabels {

    public static String getItemHome(){
        return  getLabelWithPrimaryKey("item_home","Acasă");
    }

    public static String getItemPayBill(){
        return getLabelWithPrimaryKey("item_paybill","Plăţi");
    }

    public static String getItemServices(){
        return getLabelWithPrimaryKey("item_services","Serviciile tale");
    }

    public static String getItemServicesPrepaid(){
        return getLabelWithPrimaryKey("item_services_prepaid","Cost Control");
    }

    public static String getItemProductsAndServices(){
        return getLabelWithPrimaryKey("item_products_and_services","Produsele şi serviciile tale");
    }

    public static String getItemServicesDetails(){
        return getLabelWithPrimaryKey("item_services_details","Detalii servicii");
    }

    public static String getItemServicesCalls(){
        return getLabelWithPrimaryKey("item_services_calls","Detalii apeluri");
    }

    public static String getItemOffers(){
        return getLabelWithPrimaryKey("item_offers","Oferte și opțiuni pentru tine.");
    }

    public static String getItemOffersPrepaid(){
        return getLabelWithPrimaryKey("item_offers_prepaid","Promoţii");
    }

    public static String getItemOffersPostpaid(){
        return getLabelWithPrimaryKey("item_offers_postpaid","Promoții și opțiuni");
    }

    public static String getItemOffersPostpaid1(){
        return getLabelWithPrimaryKey("item_offers_postpaid1","Oferte pentru tine");
    }

    public static String getItemLoyalty(){
        return getLabelWithPrimaryKey("item_loyalty","Program Loialitate");
    }

    public static String getItemBillsAndPayments(){
        return getLabelWithPrimaryKey("item_bills_and_payments","Plată factură");
    }

    public static String getItemBillingOverview(){
        return getLabelWithPrimaryKey("item_billing_overview","Istoric Factură");
    }

    public static String getItemTravelingAbroad(){
        return getLabelWithPrimaryKey("item_traveling_abroad","Roaming");
    }

    public static String getItemRecharge(){
        return getLabelWithPrimaryKey("item_recharge","Reîncărcare");
    }

    public static String getItemNotifications(){
        return getLabelWithPrimaryKey("item_notifications","Notificările tale");
    }

    public static String getItemSettings(){
        return getLabelWithPrimaryKey("item_settings","Setări");
    }

    public static String getItemLogout(){
        return getLabelWithPrimaryKey("item_logout","Deconectare");
    }

    public static String getItemLogin(){
        return getLabelWithPrimaryKey("item_login","Autentificare");
    }
}
