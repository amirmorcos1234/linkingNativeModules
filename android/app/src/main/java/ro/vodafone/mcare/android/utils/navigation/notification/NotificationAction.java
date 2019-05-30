package ro.vodafone.mcare.android.utils.navigation.notification;

import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

/**
 * Created by Victor Radulescu on 6/21/2017.
 */

public enum NotificationAction {
    NONE(),
    COST_CONTROL_POSTPAID("cost-control-postpaid",IntentActionName.COST_CONTROL_POSTPAID),
    COST_CONTROL_PREPAID("cost-control-prepaid",IntentActionName.COST_CONTROL),
    MY_BILL("my-bill",IntentActionName.BILLING_OVERVIEW),
    PROMOTIONS("promotions",IntentActionName.OFFERS_BEO),//promotions prepaid only ( promotii)
    EXTRAOPTIONS("extraoptiuni",IntentActionName.OFFERS_EXTRAOPTIONS), // bonusuri si optiuni postpaid only
    CALL_DETAIS("detaliere-apeluri",IntentActionName.CALL_DETAILS_REDIRECT_NOTIFICATION),
    TOP_UP("recharge",IntentActionName.TOP_UP_AUTO_REDIRECT_NOTIFICATION),
    PAY_BILL_ANONYMOUS("anonymous-pay-bill",IntentActionName.PAY_BILL_ANONYMOUS),
    LOYALTY_DASH("loyaltydash",IntentActionName.LOYALTY_SELECTION_ACTIVITY),// http://portal-pet.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/loyaltydash/index.htm
    LOYALTY_NEW_OFFERS("loyaltynewoffers",IntentActionName.LOYALTY_MARKET),// http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/loyaltynewoffers/index.htm -
    LOYALTY_MY_OFFERS("loyaltymyoffers",IntentActionName.LOYALTY_MARKET),// http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/loyaltymyoffers/index.htm - for
    LOYALTY_OFFER("loyaltyoffer",IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS),//http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/loyaltyoffer[ID]/index.htm -
    WEBVIEW("webview",IntentActionName.WEBVIEW),
    DETAILS("details",IntentActionName.OFFERS_BEO_DETAILS),
    GDPR_SELECTION("permissions-selection", IntentActionName.SETTINGS_PERMISSIONS_FRAGMENT),
    GDPR_MINOR("permissions-minor", IntentActionName.SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_MINOR),
    GDPR_VODAFONE("permissions-vodafone", IntentActionName.SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_VODAFONE),
    GDPR_PARTENERS("permissions-parteners", IntentActionName.SETTINGS_PERMISSIONS_DETAILS_FRAGMENT_PARTENERS),
    TRAVELLING_ABROAD("roaming-travelling", IntentActionName.TRAVELLING_ABOARD),// http://portal-pet.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/roaming-travelling/index.htm
    TRAVELLING_ABROAD_ADMINISTRATION("roaming-administration", IntentActionName.TRAVELLING_ABOARD_ADMINISTRATION),// http://portal-pet.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/roaming-administration/index.htm
    //http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-phonelisting/index.htm
    RETENTION_PHONES_LISTING("retention-phonelisting", IntentActionName.RETENTION_PHONES_LISTING),
    //http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-phonedetails[ID]/index.htm
    RETENTION_PHONE_DETAILS("retention-phonedetails", IntentActionName.RETENTION_PHONE_DETAILS),
    //http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-promolisting/index.htm
    RETENTION_PRICEPLANS_LISTING("retention-promolisting", IntentActionName.RETENTION_PRICEPLANS_LISTING),
    //http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/retention-promodetails[ID]/index.htm
    RETENTION_PRICEPLAN_DETAILS("retention-promodetails", IntentActionName.RETENTION_PRICEPLAN_DETAILS),
    OFFERS_SERVICES("servicii",IntentActionName.OFFERS_SERVICES); //http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/servicii/index.htm

    final String pageId;
    IntentActionName intentActionName;

    NotificationAction() {
        pageId = null;
        intentActionName = IntentActionName.NONE;
    }

    NotificationAction(String pageId) {
        this.pageId = pageId;
    }

    NotificationAction(String pageId, IntentActionName intentActionName) {
        this.pageId = pageId;
        this.intentActionName = intentActionName;
    }

    public IntentActionName getIntentActionName() {
        return intentActionName;
    }

    public void setIntentActionName(IntentActionName intentActionName) {
        this.intentActionName = intentActionName;
    }

    public String getPageId() {
        return pageId;
    }
}
