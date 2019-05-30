package ro.vodafone.mcare.android.client.model.realm.appconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.client.model.realm.system.SystemProperty;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Victor Radulescu on 1/17/2017.
 */

public class AppConfiguration extends RealmObject {

    public final static String ID_APP_CONFIGURATION_KEY = "id_appConfiguration";
    @PrimaryKey
    private String id_appConfiguration = "";

    private boolean tutorialFlag;

    private boolean seamless;
    private boolean hybried;
    private boolean migrated;
    private boolean allowSeamless = true;
    private String previousMsisdn = null;

    private boolean allowNotifications = true;

    private boolean allowVoucherFlagNotifications = true;

    private boolean isKeepMeLoggedIn = true;

    // privacy configuration
    private boolean privacyFlag = true;
    private boolean optimizationFlag = true;
    private String selectedPage = null;

    public AppConfiguration(String id_appConfiguration) {
        this.id_appConfiguration = id_appConfiguration;
    }

    public AppConfiguration() {
    }

    public AppConfiguration(boolean seamless, String username, String previousMsisdn) {
        this.seamless = seamless;
        id_appConfiguration = username;
        this.previousMsisdn = previousMsisdn;
    }

    public AppConfiguration(boolean seamless, boolean migrated, boolean hybried, String username, String previousMsisdn) {
        this.seamless = seamless;
        this.migrated = migrated;
        this.hybried = hybried;
        id_appConfiguration = username;
        this.previousMsisdn = previousMsisdn;
    }

    protected static String getConfigurationWithPrimaryKey(String key, String defaultValue) {
        Realm realm = Realm.getDefaultInstance();
        SystemProperty property = null;
        try {
            property = realm.where(SystemProperty.class).equalTo("key", key).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = (property != null) ? property.getValue() : defaultValue;
        realm.close();
        return result;
    }

    protected static int getConfigurationWithPrimaryKey(String key, int defaultValue) {
        SystemProperty property = RealmManager.getDefaultInstance().where(SystemProperty.class).equalTo("key", key).findFirst();
        if (property == null) {
            return defaultValue;
        } else {
            try {
                return Integer.valueOf(property.getValue());
            } catch (Exception ex) {
                return defaultValue;
            }
        }
    }

    protected static boolean getConfigurationWithPrimaryKey(String key, boolean defaultValue) {
        SystemProperty property = RealmManager.getDefaultInstance().where(SystemProperty.class).equalTo("key", key).findFirst();
        if (property == null) {
            return defaultValue;
        } else {
            try {
                return Boolean.valueOf(property.getValue());
            } catch (Exception ex) {
                return defaultValue;
            }
        }
    }

    public static String getDashboardWallpaperMorningImageUrl() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_morning_image_url", "http://cms-web-dev-cns.connex.ro/consumption/groups/public/documents/digital_asset/bg_morning.png");
    }

    public static boolean getDisplayChatToBrowserButton() {
        return Boolean.parseBoolean(getConfigurationWithPrimaryKey("chat_is_switch_to_browser_displayed", "false"));
    }

    public static int getPhoneNumberValidationRegexp() {
        return Integer.parseInt(getConfigurationWithPrimaryKey("phone_number_validation_regexp", "^0[0-9]{9}$"));
    }

    public static String getIsFaqSearchButtonVisible() {
        return getConfigurationWithPrimaryKey("is_faq_search_button_visible", "true");
    }

    public static String isFaqButtonVisible() {
        return getConfigurationWithPrimaryKey("is_faq_button_visible", "true");
//                return getConfigurationWithPrimaryKey("is_faq_button_visibleaaaSSS", "false");
    }

    public static String isChatButtonVisible() {
        return getConfigurationWithPrimaryKey("is_chat_button_visible", "true");
//                 return getConfigurationWithPrimaryKey("is_chat_button_visibleaaaSSS", "false");
    }

    public static String isEmailButtonVisible() {
        return getConfigurationWithPrimaryKey("is_email_button_visible", "true");
//        return getConfigurationWithPrimaryKey("is_email_button_visibleaaaSSS", "false");
    }

    public static int getDashboardWallpaperNoonStart() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_noon_start", 1101);
    }

    public static String getExchangedNumberOfMessages() {
        return getConfigurationWithPrimaryKey("exchanged_number_of_messages", "5");
    }

    public static String getSpecialCharacterValidationRegexp() {
        return getConfigurationWithPrimaryKey("special_character_validation_regexp", ".*[\\\\ &!<>=|*@#~$^(){};:?,+`'\\\"%\\\\[\\\\]\\\\/]+.*");
    }

    public static String getLoginPasswordValidationRegexp() {
        return getConfigurationWithPrimaryKey("login_password_validation_regexp", ".{1,}$");
    }

    public static int getDashboardWallpaperMorningEnd() {//
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_morning_end", 1100);
    }

    public static String getPasswordValidationRegexp() {
        return getConfigurationWithPrimaryKey("password_validation_regexp", "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,30}$");
    }

    public static int getApi2Timeout() {
        return getConfigurationWithPrimaryKey("api_2_timeout", 5000);
    }

    public static String getEmailValidationRegexp() {
        return getConfigurationWithPrimaryKey("email_validation_regexp", "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]+)$");
    }

    public static String getDashboardWallpaperNoonImageUrl() {
//        return getConfigurationWithPrimaryKey("dashboard_wallpaper_noon_image_url","http://cms-web-dev-cns.connex.ro/consumption/groups/public/documents/digital_asset/bg_day.png");
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_noon_image_url", "http://cms-web-dev-cns.connex.ro/consumption/groups/public/documents/digital_asset/bg_morning.png");
    }

    public static int getDashboardWallpaperEveningStart() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_evening_start", 1801);
    }

    public static String getLoginUsernameValidationRegexp() {
        return getConfigurationWithPrimaryKey("login_username_validation_regexp", ".{1,60}$");
    }

    public static int getApi1Timeout() {
        return getConfigurationWithPrimaryKey("api_1_timeout", 5000);
    }

    public static String getVodafonePhoneNumberValidationRegexp() {
        return getConfigurationWithPrimaryKey("vodafone_phone_number_validation_regexp", "^07[0-9]{8}$");
    }

    public static String getUsernameLengthValidationRegexp() {
        return getConfigurationWithPrimaryKey("username_length_validation_regexp", ".{4,60}$");
    }

    public static int getDashboardWallpaperMorningStart() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_morning_start", 600);
    }

    public static int getDashboardWallpaperEveningEnd() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_evening_end", 559);
    }

    public static int getDashboardWallpaperNoonEnd() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_noon_end", 1800);
    }

    public static String getDashboardWallpaperEveningImageUrl() {
        return getConfigurationWithPrimaryKey("dashboard_wallpaper_evening_image_url", "http://cms-web-dev-cns.connex.ro/consumption/groups/public/documents/digital_asset/bg_evening.png");
    }

    public static String getAmountRegex() {
        return getConfigurationWithPrimaryKey("amount_regex", "");
    }

    public static int getCallDetailsEventsWillBeDisplayed() {
        return getConfigurationWithPrimaryKey("call_details_events_will_be_displayed", 25);
    }

    public static String getCallDetailsEventsRequest() {
        return getConfigurationWithPrimaryKey("call_details_events_request", "100");
    }

    public static String getHolidaysForPaymentAgreement() {
        return getConfigurationWithPrimaryKey("holidays", "");
    }

    public static String getRoamingOfferId() {
        return getConfigurationWithPrimaryKey("prepaid_roaming_offer_id", "4");
    }

    public static String getBillingOverViewBillMaxSubscriberWillBeDisplayed() {
        return getConfigurationWithPrimaryKey("billing_overview_bill_max_subscribers_will_be_displayed", "20");
    }

    public static String getMarketMaxNumberBanners() {
        return getConfigurationWithPrimaryKey("loyalty_max_number_of_eligible_banners", "3");
    }

    public static int getNonTelcoQuestionnaireTime() {
        return getConfigurationWithPrimaryKey("nontelco_questionnaire_time", 5);
    }

    public static boolean isSorteaza_Displayed() {
        return getConfigurationWithPrimaryKey("isSorteaza_Displayed", true);
    }

    public static String getEbuMigratedIneligibleCustomerSegments() {
        return getConfigurationWithPrimaryKey("payment_confirmation_ineligible_customer_segments", "TOP0;XXL0;XL10");
    }

    public static String getSimReplacementEbuMigratedCustomerSegments1() {
        return getConfigurationWithPrimaryKey("sim_replacement_customer_segments1", "L100;L200;XL10");
    }

    public static String getSimReplacementEbuMigratedCustomerSegments2() {
        return getConfigurationWithPrimaryKey("sim_replacement_customer_segments2", "XXL0;TOP0");
    }

    public static boolean isCreditInAdvancePageDisplayed() {
        return getConfigurationWithPrimaryKey("credit_in_advance_show", true);
    }

    public static String getWebviewCampaignShareStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_share_string_listener", "/share");
    }

    public static String getWebviewCampaignLoginStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_login_string_listener", "/go_to_login");
    }

    public static String getWebviewCampaignReLoginStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_relogin_string_listener", "/autentificare");
    }

    public static String getWebviewCampaignActivateOfferStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_activate_offer_string_listener", "offer_id=");
    }

    public static String getWebviewCampaignExternalLinksStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_external_links_string_listener", "https://play.google.com/store/apps/details?id=ro.vodafone.mcare.android ; https://itunes.apple.com/ro/app/myvodafone-ro/id468772675");
    }

    public static String getWebviewCapaignSendSMSStringListener() {
        return getConfigurationWithPrimaryKey("webview_campaign_send_sms_string_listener", "/sendSMS");
    }

    public static int getFAQMaxArticleSearchLimit() {
        return getConfigurationWithPrimaryKey("faq_max_article_limit", 50);
//        return getConfigurationWithPrimaryKey("is_email_button_visibleaaaSSS", "false");
    }

    public static int getFAQMaxHintsLimit() {
        return getConfigurationWithPrimaryKey("faq_max_hints_limit", 5);
//        return getConfigurationWithPrimaryKey("is_email_button_visibleaaaSSS", "false");
    }

    public static boolean getShowEbuProductAndServices() {
        return getConfigurationWithPrimaryKey("show_ebu_products_and_services", true);
    }

    public static String getEbuMigratedIneligibleToDeleteOfferSegment() {
        return getConfigurationWithPrimaryKey("offer_delete_ineligible_customer_segments", "TOP0;CORP;LSME");
    }

    public static String getNewEshopMsiteUrl() {
        return getConfigurationWithPrimaryKey("new_eshop_msite_url", "https://www.vodafone.ro/eshop/");
    }

    public static boolean isAbleToAccesRetention(String crmRole) {
        List<String> crmRoles = new ArrayList<>();

        crmRoles = Arrays.asList(getConfigurationWithPrimaryKey("crmroles_able_to_acces_retention", "AuthorizedPerson;Chooser;DelegatedChooser;SubUser;PowerUser").toLowerCase().split(";"));

        return crmRole != null
                && crmRoles.contains(crmRole.toLowerCase());
    }

    public static String getNonTelcoDefaultBannerUrl() {
        return getConfigurationWithPrimaryKey("vodafone_voucher_mall_default_banner", "http://cms-ucm-agw.connex.ro:8080/consumption/groups/public/documents/digital_asset/voucher_mall_default_banner.png");
    }

    public static String getEbuBlockSimApiWaitingTime() {
        return getConfigurationWithPrimaryKey("ebu_block_sim_api_waiting_time", "5");
    }

    public static String getVodafoneTvCategories() {
        return getConfigurationWithPrimaryKey("vtv_services_categories", "OTT Products;4G TV+");
    }

    public static List<String> getRoamingOfferIdList() {
        String roamingOffersIdList = getConfigurationWithPrimaryKey("roaming_active_display_offer_ids", "1973,1976,1979");
        String[] splitString = roamingOffersIdList.split(",");
        List<String> offersList = new ArrayList<String>(Arrays.asList(splitString));
        return offersList;
    }

    public static String getVodafoneTvVovExternalLink() {
        return getConfigurationWithPrimaryKey("vtv_vov_external_deeplink", "https://www.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp");
    }

    public static String getVTVListingExternalDeeplink() {
        return getConfigurationWithPrimaryKey("vtv_listing_external_deeplink", "https://www.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp");
    }

    public static boolean getShowVodafoneTvMenu() {
        return getConfigurationWithPrimaryKey("show_vtv", false);
    }

    public static String getGdprPropVariable() {
        return getConfigurationWithPrimaryKey("gdpr_permissions_adobe_variable", "vfBasicProfileCustServiceData");
    }

    public static boolean getShowSimReplacement() {
        return getConfigurationWithPrimaryKey("show_sim_replacement", true);
    }

    public static String getSuperRedKey() {
        return getConfigurationWithPrimaryKey("loyalty_super_red_key", "SuperRED");
    }

    public static String getMinTopUpAmountForVerify() {
        return getConfigurationWithPrimaryKey("top_up_min_amount_verification", "4");
    }

    public static String getRescorpDeeplink() {
        return getConfigurationWithPrimaryKey("product_and_services_rescorp_offers_deeplink", "http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-phonelisting/index.htm");
    }

    public static String getPrivateUserDeeplink() {
        return getConfigurationWithPrimaryKey("product_and_services_private_offers_deeplink", "http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-phonelisting/index.htm");
    }

    public static String getResubDeeplink() {
        return getConfigurationWithPrimaryKey("product_and_services_resSub_offers_deeplink", "http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-phonelisting/index.htm");
    }

    public static Boolean getRescorpShowButton() {
        return getConfigurationWithPrimaryKey("product_and_services_rescorp_show_button_state", true);
    }

    public static Boolean getPrivateUserShowButton() {
        return getConfigurationWithPrimaryKey("product_and_services_private_user_show_button_state", true);
    }

    public static Boolean getResubShowButton() {
        return getConfigurationWithPrimaryKey("product_and_services_resSub_show_button_state", true);
    }

    public static int getPrepaidTransferCreditValueOne() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_value_one", 1);
    }

    public static int getPrepaidTransferCreditValueTwo() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_availability_value_two", 3);
    }

    public static int getPrepaidTransferCreditValueThree() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_availability_value_three", 6);
    }

    public static int getTransferCreditWaitingTime() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_api_waiting_time", 5);
    }

    public static String getPrepaidTransferMinimumRechargeValue() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_minimum_recharge_value" ,"1.5");
    }

    public static int getPrepaidTransferCreditAvailabilityOne() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_availability_one", 5);
    }

    public static int getPrepaidTransferCreditAvailabilityTwo() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_availability_two", 10);
    }

    public static int getPrepaidTransferCreditAvailabilityThree() {
        return getConfigurationWithPrimaryKey("prepaid_transfer_credit_availability_three", 20);
    }

    public boolean isSeamless() {
        return seamless;
    }

    public void setSeamless(boolean seamless) {
        Realm realm = RealmManager.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        this.seamless = seamless;
        realm.close();
    }

    public boolean isHybried() {
        return hybried;
    }

    public void setHybried(boolean hybried) {
        Realm realm = RealmManager.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        this.hybried = hybried;
        realm.close();
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        Realm realm = RealmManager.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        this.migrated = migrated;
        realm.close();
    }

    public String getPreviousMsisdn() {
        return previousMsisdn;
    }

    public void setPreviousMsisdn(String previousMsisdn) {
        this.previousMsisdn = previousMsisdn;
    }

    public boolean isTutorialFlag() {
        return tutorialFlag;
    }

    public void setTutorialFlag(boolean tutorialFlag) {
        this.tutorialFlag = tutorialFlag;
    }

    public void setTutorialFlagTrue() {
        Realm realm = Realm.getDefaultInstance();

        RealmManager.startTransaction(realm);
        this.tutorialFlag = true;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean allowSeamless() {
        return allowSeamless;
    }

    public void setAllowSeamless(boolean allowSeamless) {
        Realm realm = Realm.getDefaultInstance();

        RealmManager.startTransaction(realm);
        this.allowSeamless = allowSeamless;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean allowNotifications() {

//        D.e("REALM Notifications flag IS     = " + allowNotifications);
        return allowNotifications;
    }

    public void setAllowNotifications(boolean allowNotifications) {
        Realm realm = Realm.getDefaultInstance();
        D.d("REALM Notifications flag set to  = " + allowNotifications);

        RealmManager.startTransaction(realm);
        this.allowNotifications = allowNotifications;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean allowVoucherNotifications() {

//        D.e("REALM Notifications flag IS     = " + allowNotifications);
        return allowVoucherFlagNotifications;
    }

    public void setAllowVoucherNotifications(boolean allowVoucherFlagNotifications) {
        Realm realm = Realm.getDefaultInstance();

        D.d("REALM Notifications flag set to  = " + allowVoucherFlagNotifications);

        RealmManager.startTransaction(realm);
        this.allowVoucherFlagNotifications = allowVoucherFlagNotifications;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean getPrivacyFlag() {
        return privacyFlag;
    }

    public void setPrivacyFlag(boolean privacyFlag) {
        Realm realm = Realm.getDefaultInstance();

        RealmManager.startTransaction(realm);
        this.privacyFlag = privacyFlag;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean getOtimizationFlag() {
        return optimizationFlag;
    }

    public void setOptimizationFlag(boolean optimizationFlag) {
        Realm realm = Realm.getDefaultInstance();

        RealmManager.startTransaction(realm);
        this.optimizationFlag = optimizationFlag;
        RealmManager.update(realm, this);
        realm.close();
    }

    public boolean isKeepMeLoggedIn() {
        return isKeepMeLoggedIn;
    }

    public void setIsKeepMeLoggedIn(boolean isKeepMeLoggedIn) {
        Realm realm = Realm.getDefaultInstance();

        RealmManager.startTransaction(realm);
        this.isKeepMeLoggedIn = isKeepMeLoggedIn;
        RealmManager.update(realm, this);
        realm.close();
    }

    public String getId_appConfiguration() {
        return id_appConfiguration;
    }

    public String getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(String selectedPage) {
        RealmManager.startTransaction();
        this.selectedPage = selectedPage;
        RealmManager.update(this);
    }

    @Override
    public int hashCode() {
        return id_appConfiguration.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AppConfiguration && id_appConfiguration.equals(((AppConfiguration) obj).getId_appConfiguration());
    }
}
