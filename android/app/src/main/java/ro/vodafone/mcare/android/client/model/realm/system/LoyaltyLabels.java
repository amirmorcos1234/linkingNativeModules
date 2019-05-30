package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyLabels extends AppLabels {
    public static String getLoyalty_points_enroll_in_program() {
        return getLabelWithPrimaryKey("loyalty_points_enroll_in_program", "Înscrie-te în Program");
    }

    public static String getLoyalty_points_gain() {
        return getLabelWithPrimaryKey("loyalty_points_gain", "Cum acumulez puncte?");
    }

    public static String getLoyaltyPointsPageCardTitle() {
        return getLabelWithPrimaryKey("loyalty_points_page_card_title", "Puncte loialitate");
    }

    public static String getLoyaltyPointsActivityPageTitle() {
        return getLabelWithPrimaryKey("loyalty_activity_points_page_title", "Program Loialitate");
    }

    public static String getLoyaltyTransactionHistoryTitle() {
        return getLabelWithPrimaryKey("loyalty_transaction_history_title", "Vezi istoricul tranzacțiilor");
    }

    public static String getLoyaltyReceivedPointsTab() {
        return getLabelWithPrimaryKey("loyalty_received_points_tab", "Puncte primite");
    }

    public static String getLoyaltyUsedPointsTab() {
        return getLabelWithPrimaryKey("loyalty_used_points_tab", "Puncte utilizate");
    }

    public static String getPoints_Gain_Title() {
        return getLabelWithPrimaryKey("loyalty_points_gain_title", "Cum acumulez puncte");
    }

    public static String getLoyalty_enroll_now() {
        return getLabelWithPrimaryKey("loyalty_enroll_now", "Înscrie-te acum");
    }

    public static String getLoyalty_quit_program() {
        return getLabelWithPrimaryKey("loyalty_quit_program", "Renunță la Program");
    }

    public static String getLoyalty_quit_title() {
        return getLabelWithPrimaryKey("loyalty_quit_overlay_title", "Ești sigur ? ");
    }

    public static String getLoyalty_quit_overlay_message() {
        return getLabelWithPrimaryKey("loyalty_quit_overlay_message", "Renunțarea la programul de loialitate presupune că din acest moment, contul tău nu mai este eligibil pentru acumularea de puncte și beneficii. Reînscrierea în program se poate realiza oricând reluând pașii de înscriere din această aplicație, secțiunea “Program Loialitate”. Ești sigur că vrei să renunți la programul de loialitate?");
    }

    public static String getLoyalty_unauthorized_msisdn_message() {
        return getLabelWithPrimaryKey("loyalty_unauthorized_msisdn_message", "Pentru vizualizarea punctelor de loialitate existente, este necesar ca titularul să solicite autorizarea fiecărui număr de telefon. Accesează butonul \"Chat\" pentru a intra în legătură cu un operator.");
    }

    public static String getLoyalty_how_to_receive_points_msg() {
        return getLabelWithPrimaryKey("loyalty_how_to_receive_points_msg", "Îți mulțumim pentru că ne ești alături și te răsplătim pentru timpul petrecut în rețea!\n\n Astfel, am creat următoarele pachete de beneficii pentru tine: Bronze, Silver, Gold și Platinum. \n" +
                "În funcție de valoarea facturilor, vechimea în reţea şi utilizarea concomitentă a mai multor servicii distincte, acumulezi lunar puncte de loialitate. Fiecare punct de loialitate valorează 0,01€. Numărul acestora diferă în funcţie de grupa de loialitate din care faci parte: Bronze, Silver, Gold sau Platinum.\n\n" +
        "Detaliere program: www.vodafone.ro");
    }

    public static String getLoyalty_enroll_message(){
        return getLabelWithPrimaryKey("loyalty_enroll_message", "Prin programul de loialitate, te răsplătim pentru vechimea în reţea, valoarea facturilor şi utilizarea concomitentă a mai multor servicii distincte. Punctele acumulate se transformă în beneficii pentru tine.");
    }

    public static String getLoyalty_history_title(){
        return getLabelWithPrimaryKey("loyalty_history_title", "Istoric tranzacții");
    }

    public static String getLoyalty_error_message(){
        return getLabelWithPrimaryKey("loyalty_error_message","Serviciu momentan indisponibil\nApasă pentru a reîncerca");
    }

    public static String getLoyaltyVoucherErrorMessage(){
        return getLabelWithPrimaryKey("loyalty_voucher_error_message","Sistem momentan indisponibil\nApasă pentru a reîncerca");
    }

    public static String getLoyaltyNoVdfSubscriptionErrorMessage(){
        return getLabelWithPrimaryKey("loyalty_market_error_not_eligible_user_role","Ne pare rău! Utilizatorul selectat nu poate achiziționa vouchere din Magazinul Happy Mall");
    }

    public static String getLoyalty_segment_title(){
        return getLabelWithPrimaryKey("loyalty_segment_title","Client %1$s");
    }

    public static String getLoyalty_segment_description(){
        return getLabelWithPrimaryKey("loyalty_segment_description","Primești un <b>bonus lunar de %1$s</b> pentru punctele de loialitate primite pentru valoarea facturii precedente.");
    }

    public static String getLoyalty_segment_description_silver(){
        return getLabelWithPrimaryKey("loyalty_segment_desciption_silver", "Te răsplătim pentru vechimea în reţea, valoarea facturilor şi utilizarea concomitentă a mai multor servicii distincte.");
    }

    public static String getLoyalty_quit_program_success(){
        return getLabelWithPrimaryKey("loyalty_quit_program_succes", "Dezabonarea din programul de loialitate a fost efectuată");
    }
    public static String getLoyalty_total_points_period(){
        return getLabelWithPrimaryKey("loyalty_total_points_period", "Pentru perioada %1$s - %2$s ai:");
    }

    public static String getLoyalty_total_consumed_points_label(){
        return getLabelWithPrimaryKey("loyalty_total_consumed_points_label", "Total puncte consumate: ");
    }

    public static String getLoyalty_total_received_points_label(){
        return getLabelWithPrimaryKey("loyalty_total_received_points_label", "Total puncte acumulate: ");
    }

    public static String getLoyaltyOffersButton(){
        return getLabelWithPrimaryKey("loyalty_offers_button","Oferte pentru numărul tău");
    }

    public static String getLoyaltyFilterButton(){
        return getLabelWithPrimaryKey("loyalty_filter_button","Filtrare după dată");
    }

    public static String getLoyaltyFilter(){
        return getLabelWithPrimaryKey("loyalty_filter","Filtrare");
    }

    public static String getLoyaltyRefuseProgram(){
        return getLabelWithPrimaryKey("loyalty_refuse_program","Renunță la programul de loialitate");
    }

    public static String getLoyaltyPoints(){
        return getLabelWithPrimaryKey("loyalty_points","Puncte de loialitate");
    }

    public static String getLoyaltyLastUpdate(){
        return getLabelWithPrimaryKey("loyalty_last_update","Ultima actualizare: %1$s");
    }

    public static String getLoyaltyReceivedPoints(){
        return getLabelWithPrimaryKey("loyalty_received_points","Puncte acumulate");
    }

    public static String getToastErrorOverlayPermission(){
        return getLabelWithPrimaryKey("toast_error_overlay_permission","Permisiunea este necesara pentru a va putea oferi suport 24/7.");
    }

    public static String getLoyaltyTotalPoints(){
        return getLabelWithPrimaryKey("loyalty_total_points","Total puncte: %1$s");
    }

    public static String getLoyaltySuccessEnroll(){
        return getLabelWithPrimaryKey("loyalty_success_enroll","Te-ai abonat cu succes în programul de loialitate, pe contul %1$s.");
    }

    public static String getLoyaltyHistoryItemDate(){
        return getLabelWithPrimaryKey("loyalty_history_item_date","Data: %1$s");
    }



    //LoyaltyMarket module Drop 1.1
    public static String getLoyaltyActivitySelectionPageTitle() {
        return getLabelWithPrimaryKey("loyalty_activity_selection_page_title", "Program Loialitate");
    }

    public static String getLoyaltyMarketActivityPageTitle() {
        return getLabelWithPrimaryKey("loyalty_activity_market_page_title", "Vodafone Happy Mall");
    }

    public static String getLoyaltyMarketCardDescription() {
        return getLabelWithPrimaryKey("loyalty_market_card_description", "Îţi răsplătim loialitatea cu reduceri la partenerii Vodafone.");
    }

    public static String getLoyaltyPointsCardDescription() {
        return getLabelWithPrimaryKey("loyalty_points_card_description", "Vezi punctele tale de loialitate și află ce poți face cu ele.");
    }

    public static String getCampaignDetailsActivity() {
        return getLabelWithPrimaryKey("loyalty_market_campaign_default_pdf","https://www.vodafone.ro/regulamentul-happy-mall");
    }
    public static String getLoyaltyVoucherExpiredMessage(){
        return getLabelWithPrimaryKey("loyalty_voucher_expired_message", "Acest voucher a expirat!");
    }
    public static String getLoyaltyVoucherNoNewVouchersMessage(){
        return getLabelWithPrimaryKey("loyalty_voucher_no_new_vouchers_message", "Ne pare rău, momentan nu avem nici un voucher!");
    }
    public static String getLoyaltyVoucherNoMyVouchersFirstMessage(){
        return getLabelWithPrimaryKey("loyalty_voucher_no_my_vouchers_first_message", "Nu ai niciun voucher rezervat!");
    }
    public static String getLoyaltyVoucherNoMyVouchersSecondMessage(){
        return getLabelWithPrimaryKey("loyalty_voucher_no_my_vouchers_second_message", "Achiziționează unele din secțiunea Oferte Noi.");
    }

    public static String getLoyaltyCancelButton(){
        return getLabelWithPrimaryKey("loyalty_cancel_button","Resetează filtrele");
    }

    public static String  getLoyaltyMarketTitle(){
        return getLabelWithPrimaryKey("loyalty_market_title","Vodafone Happy Mall");
    }

    public static String getMarketSpinner() {
        return getLabelWithPrimaryKey("loyalty_market_spinner_title","Sortează");
    }
    public static String getMarketCampaignTitle(){
        return getLabelWithPrimaryKey("loyalty_market_campaign_title","Regulamentul campaniei");
    }

    public static String getLoyaltyVoucherOfferedBy() {
        return getLabelWithPrimaryKey("loyalty_market_offered_by", "Oferit de");
    }

    public static String getLoyaltyVoucherDiscountLabel() {
        return getLabelWithPrimaryKey("loyalty_market_discount_label", "reducere");
    }

    public static String getLoyaltyVoucherFreeLabel() {
        return getLabelWithPrimaryKey("loyalty_market_free_label", "gratuit");
    }

    public static String getLoyaltyVoucherExpiresTodayLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_expires_today_label", "Promoția expiră astăzi. Profită acum.");
    }

    public static String getLoyaltyVoucherExpireLabel(){
        return getLabelWithPrimaryKey("loyalty_market_expire_today_label", "Promoția expiră ");
    }

    public static String getLoyaltyVouchersTodayLabel(){
        return getLabelWithPrimaryKey("loyalty_market_today_label", "astăzi");
    }

    public static String getLoyaltyVoucherExpiresDaysLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_expires_days_label", "Promoția expiră în");
    }

    public static String getLoyaltyVoucherDaysProfitLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_days_profit_label", "zile. Profită acum.");
    }

    public static String getLoyaltyVoucherTomorrowProfitLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_days_profit_label", "zi. Profită acum.");
    }

    public static String getLoyaltyVoucherTomorrowBoldLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_days_bold_label",  "1 zi");
    }

    public static String getLoyaltyVoucherDaysBoldLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_days_bold_label",  "1 zile");
    }

    public static String getLoyaltyVoucherDetailsPageTitle(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_details_title", "Ofertă");
    }

    public static String getLoyaltyVoucherShopOfferLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_shop_label", "Ofertă valabilă în toate magazinele ");
    }

    public static String getLoyaltyVoucherOfferAvailabilityLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_offer_availability_label",  "Ofertă valabilă până la ");
    }

    public static String getLoyaltyVoucherCodeLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_code_label", "Voucher code");
    }

    public static String getLoyaltyVoucherAvailabilityLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_availability_label", "Poți folosi voucherul până la data de ");
    }

    public static String getLoyaltyVoucherReserveLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_reserve_button_label", "Rezervă voucher");
    }

    public static String getLoyaltyVoucherReservedCodeLabel(){
        return getLabelWithPrimaryKey("loyalty_market_voucher_reserved_code_label", "Voucher rezervat. Codul tău este: ");
    }

    public static String getLoyaltyNewOffers() {
        return getLabelWithPrimaryKey("loyalty_market_new_offers", "Oferte noi");
    }

    public static String getLoyaltyMyOffers() {
        return getLabelWithPrimaryKey("loyalty_market_my_offers", "Ofertele mele");
    }

    public static String getLoyaltyFiltering() {
        return getLabelWithPrimaryKey("loyalty_market_filtering", "Filtrează");
    }

    public static String getLoyaltyFilters() {
        return getLabelWithPrimaryKey("loyalty_market_filters", "Filtre");
    }

    public static String getLoyaltySingleFilter() {
        return getLabelWithPrimaryKey("loyalty_market_filter", "Filtru");
    }

    public static String getLoyaltyCampaignDescription() {
        return getLabelWithPrimaryKey("loyalty_market_campaign_details", "Descriere campanie");
    }

    public static String getLoyaltyVoucherAlreadyReserved() {
        return getLabelWithPrimaryKey("loyalty_market_voucher_already_reserved", "Acest voucher a fost deja rezervat.");
    }

    public static String getLoyaltyVoucherNoStock() {
        return getLabelWithPrimaryKey("loyalty_market_voucher_no_stock", "A fost atinsă limita maximă de rezervări pentru acest voucher.");
    }

    public static String getLoyaltyOfferAvailable() {
        return getLabelWithPrimaryKey("loyalty_market_offer_available", "Ofertă valabilă în toate magazinele");
    }

    public static String getLoyaltySortRelevance() {
        return getLabelWithPrimaryKey("loyalty_market_sort_relevance", "Relevanță");
    }

    public static String getLoyaltySortExpiry() {
        return getLabelWithPrimaryKey("loyalty_market_sort_expiry ", "Data expirării");
    }

    public static String getLoyaltySortAlphabetic() {
        return getLabelWithPrimaryKey("loyalty_market_sort_alphabetically", "Alfabetic");
    }

    public static String getMarketErrorInvalidVoucher() {
        return getLabelWithPrimaryKey("loyalty_market_error_invalid_voucher", "Voucherul nu este disponibil pentru a fi rezervat.");
    }

    public static String getErrorServiceTemporarilyUnavailableClickToRetry() {
        return getLabelWithPrimaryKey("error_service_temporarily_unavailable_click_to_retry", "Serviciu momentan indisponibil. Apasă pentru a reîncerca.");
    }

    public static String getErrorNotEligibleUserRole() {
        return getLabelWithPrimaryKey("loyalty_market_error_not_eligible_user_role", "Ne pare rău! Uitlizatorul selectat nu poate achiziţiona vouchere din magazinul Happy Mall");
    }

    public static String getLoyaltyExclusivOffers() {
        return getLabelWithPrimaryKey("loyalty_market_exclusiv_offers", "Oferte Exclusive");
    }

    public static String getLoyaltyMarketSuperRedPageTitle(){
        return getLabelWithPrimaryKey("loyalty_page_title_super_red","Experiența Super RED");
    }

    public static String getLoyaltyMarketSuperRedTitle(){
        return getLabelWithPrimaryKey("loyalty_title_super_red","Experiența Super RED");
    }


}
