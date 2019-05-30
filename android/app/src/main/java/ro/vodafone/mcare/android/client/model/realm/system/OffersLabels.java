package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by Deaconescu Eliza on 07.04.2017.
 */

public class OffersLabels extends AppLabels {

    public static String getOffers_for_you_page_postpaid_title() {
        return getLabelWithPrimaryKey("offers_for_you_page_title", "Oferte pentru tine");
    }
    public static String getOffers_for_you_page_prepaid_title() {
        return getLabelWithPrimaryKey("offers_for_you_page_title", "Promoții");
    }
    public static String getOffers_for_you_mobile_internet(){
        return getLabelWithPrimaryKey(" offers_for_you_mobile_internet","Internet pe mobile");
    }
    public static String getOffers_for_you_monthly(){
         return getLabelWithPrimaryKey("offers_for_you_monthly", "pe lună");
    }
    public static String getOffers_for_you_beo_button_label() {
         return getLabelWithPrimaryKey("offers_for_you_beo_button_label", "Bonusuri și Opțiuni");
     }
    public static String getOffers_for_you_services_button_label(){
          return getLabelWithPrimaryKey("offers_for_you_services_button_label","Servicii");
    }
    public static String getOffers_for_you_you_button_label() {
          return getLabelWithPrimaryKey("offers_for_you_you_button_label", "Înscrie-te în YOU");
    }
    public static String getOffers_for_you_phone_and_subscription_button_label(){
          return getLabelWithPrimaryKey("offers_for_you_phone_and_subscription_button_label","Telefoane și Abonamente");
    }
    public static String getOffers_for_you_pending_offers(){
           return getLabelWithPrimaryKey("offers_for_you_pending_offers","Oferte în așteptare");
    }
    public static String getOffersForYouAcceptedOffers(){
        return getLabelWithPrimaryKey("accepted_offers_title","Oferte acceptate");
    }

    public static String getAcceptedOffersContractDetails(){
        return getLabelWithPrimaryKey("accepted_contract_details","Detalii contract");
    }

    public static String getTop_up_confirmation_title() {
        return getLabelWithPrimaryKey("top_up_confirmation_title","Te rugăm să confirmi!");
    }

    public static String getOrdersHistoryTitle(){
        return getLabelWithPrimaryKey("orders_history_title","Istoric comenzi");
    }

    public static String getOrderHistoryDetailsTitle(){
        return getLabelWithPrimaryKey("orders_history_details_title","Detalii comandă");
    }

    public static String getOrderHistoryDetailsDeliveryInfo(){
        return getLabelWithPrimaryKey("orders_history_delivery_information","Informații de livrare");
    }

    public static String getOrderHistoryDetailsNumber(){
        return getLabelWithPrimaryKey("orders_history_details_number","Număr de comandă: ");
    }

    public static String getOrderHistoryDetailsDateSubmitted(){
        return getLabelWithPrimaryKey("orders_history_details_date_submitted","Expediat: ");
    }

    public static String getOrderHistoryDetailsEstimatedDeliveryTime(){
        return getLabelWithPrimaryKey("orders_history_details_delivery_time","Timp de livrare estimat: ");
    }

    public static String getOrderHistoryDetailsStatus(){
        return getLabelWithPrimaryKey("orders_history_details_status","Status: ");
    }

    public static String getOrderHistoryDetailsAwbId(){
        return getLabelWithPrimaryKey("orders_history_details_awb_id","Nota de referință: ");
    }
    public static String getOrderHistoryDetailsCustomerPhoneNumber(){
        return getLabelWithPrimaryKey("orders_history_details_customer_phone_number","Număr contact client: ");
    }
    public static String getOrderHistoryDetailsCustomerDeliveryAddress(){
        return getLabelWithPrimaryKey("orders_history_details_customer_delivery_address","Livrat la: ");
    }

    public static String getOrderHistoryDetailsProductInfo(){
        return getLabelWithPrimaryKey("orders_history_details_product_information","Produse");
    }



    public static String getOrderHistoryDetailsPaymentInfo(){
        return getLabelWithPrimaryKey("orders_history_details_payment_information","Metoda de plată și facturare");
    }

    public static String getOrderHistoryDetailsSelectedPaymentMethod(){
        return getLabelWithPrimaryKey("orders_history_details_selected_payment_method","Metoda de plată: ");
    }

    public static String getOrderHistoryDetailsCustomerAddress(){
        return getLabelWithPrimaryKey("orders_history_details_product_information","Adresă client: ");
    }


    public static String getOrderHistoryDetailsPromotionalDiscount(){
        return getLabelWithPrimaryKey("orders_history_details_promotional","Discount promoțional: ");
    }

    public static String getOrderHistoryDetailsOnlineDiscount(){
        return getLabelWithPrimaryKey("orders_history_details_online","Discount online: ");
    }

    public static String getOrderHistoryDetailsProducerDiscount(){
        return getLabelWithPrimaryKey("orders_history_details_producer","Discount producător: ");
    }

    public static String getOrderHistoryDetailsTotalAmount(){
        return getLabelWithPrimaryKey("orders_history_details_total_amount","Total de plată (incl. TVA): ");
    }

    public static String getOrderHistoryDetailsExchangeRate(){
        return getLabelWithPrimaryKey("orders_history_details_exchange_rate","Curs de schimb: 1€ = ");
    }

    public static String getOrderHistoryHeaderProcessing(){
        return getLabelWithPrimaryKey("orders_history_details_header_processing","În curs de procesare");
    }

    public static String getOrderHistoryHeaderCancelled(){
        return getLabelWithPrimaryKey("orders_history_details_header_cancelled","Comandă anulată");
    }

    public static String getOrderHistoryHeaderReturned(){
        return getLabelWithPrimaryKey("orders_history_details_header_returned","Comandă returnată");
    }

    public static String getOrderHistoryHeaderUnprocessed(){
        return getLabelWithPrimaryKey("orders_history_details_header_unprocessed","Comandă neprocesată");
    }

    public static String getOrderHistoryHeaderDelivered(){
        return getLabelWithPrimaryKey("orders_history_details_header_delivered","Comandă livrată");
    }

    public static String getOrderHistoryHeaderSent(){
        return getLabelWithPrimaryKey("orders_history_details_header_delivered","Comandă expediată");
    }

    public static String getOrderHistoryHeaderOrderRequestSent(){
        return getLabelWithPrimaryKey("orders_history_details_header_order_request_sent","Comanda a fost trimisă");
    }

    public static String getOrderHistoryHeaderWeGotTheOrder(){
        return getLabelWithPrimaryKey("orders_history_details_header_order_received","We've got your order");
    }

    public static String getThankYouTitle(){
        return getLabelWithPrimaryKey("thank_you_title","Comandă finalizată");
    }

    public static String getThankYouHeaderLabel(){
        return getLabelWithPrimaryKey("thank_you_header_label","Comanda a fost trimisă");
    }

    public static String getThankYouHeaderSmallLabel(){
        return getLabelWithPrimaryKey("thank_you_header_small_label","Te vom ține la current");
    }

    public static String getThankYouInfoLabel(){
        return getLabelWithPrimaryKey("thank_you_info_label","Vei primi pe email confirmarea comenzii tale. Te rugăm să păstrezi email-ul respectiv.");
    }

    public static String getThankYouInfoSmallLabel(){
        return getLabelWithPrimaryKey("thank_you_info_small_label","Pentru mai multe detalii, te rugăm să ne contactezi pe chat online sau scrie-ne pe adresa magazin.online.ro@vodafone.ro");
    }

    public static String getThankYouEmailLabel(){
        return getLabelWithPrimaryKey("thank_you_email_label","magazin.online.ro@vodafone.ro");
    }

    public static String getThankYouHistoryTitle(){
        return getLabelWithPrimaryKey("thank_you_history_title","Istoricul comenzilor tale");
    }

    public static String getThankYouHistoryButton(){
        return getLabelWithPrimaryKey("thank_you_history_button","Istoric comenzi");
    }

    public static String getNoOrdersInHistory(){
        return getLabelWithPrimaryKey("no_orders_in_history","Nu există comenzi plasate.");
    }

    public static String getAcceptedOffersDetails(){
        return getLabelWithPrimaryKey("accepted_offers_details","Detalii opțiune");
    }

    public static String getAcceptedOffersOverlayTitle(){
       return getLabelWithPrimaryKey("overlayTitleAcceptedOffers","Acceptă oferta");
    }

    public static String getrefusedOffersButton(){
       return getLabelWithPrimaryKey("overlayTitleRefusedOffers","Refuz oferta");
    }

    public static String getOverlaySubtextRefusedOffers(){
        return getLabelWithPrimaryKey("overlaySubtextRefusedOffers","Eşti sigur că doreşti să refuzi această ofertă?");
    }

    public static String getNoButtonAcceptedOffers(){
        return getLabelWithPrimaryKey("NoButtonAcceptedOffers","Înapoi");
    }

    public static String getAcceptedOffersGeneralTC(){
        return getLabelWithPrimaryKey("accepted_offers_general_t_c","Clauzele specifice contractului încheiat la distanță");
    }

    public static String getOverlaySubtextAcceptedOffers(){
        return getLabelWithPrimaryKey("overlaySubtextAcceptedOffers","Eşti sigur că doreşti să accepţi această ofertă?");
    }

    public static String getProposalsPendingAcceptOffer(){
        return getLabelWithPrimaryKey("proposals_pending_accept_offer","Oferta a fost acceptată");}

    public static String getProposalsPendingRefusedOffer(){
        return getLabelWithPrimaryKey("proposals_pending_refused_offer","Oferta a fost refuzată");
    }

    public static String getProposalsPendingNoOffers(){
        return getLabelWithPrimaryKey("proposals_pending_no_offers","Nu există oferte în așteptare pentru acest număr.");
    }

    public static String getAPIFailedError() {
        return getLabelWithPrimaryKey("proposals_pending_system_error", "Sistem momentan indisponibil. Apasă aici pentru a reîncerca.");
    }

    public static String getNoAcceptedOffers(){
        return getLabelWithPrimaryKey("no_accepted_offers","Nu există oferte acceptate pentru acest număr.");
    }

    public static String getWebviewShopAssistanceEmail(){
        return getLabelWithPrimaryKey("webview_shop_assistance_email","magazin.online.ro@vodafone.ro");
    }

    public static String getAdobeTargetCategoryName(){
        return getLabelWithPrimaryKey("adobe_target_category_name","Promoții Adobe");
    }
}
