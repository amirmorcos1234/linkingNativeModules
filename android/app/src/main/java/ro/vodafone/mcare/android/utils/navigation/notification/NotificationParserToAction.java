package ro.vodafone.mcare.android.utils.navigation.notification;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

/**
 * Created by Victor Radulescu on 6/21/2017.
 */

public class NotificationParserToAction {

	public static NotificationAction parse(String smsInfo) {
		if (smsInfo == null) {
			return NotificationAction.NONE;
		}
		if (smsInfo.contains(NotificationAction.EXTRAOPTIONS.getPageId())) {
			return NotificationAction.EXTRAOPTIONS;
		} else if (smsInfo.contains(NotificationAction.COST_CONTROL_POSTPAID.getPageId())) {
			return NotificationAction.COST_CONTROL_POSTPAID;
		} else if (smsInfo.contains(NotificationAction.MY_BILL.getPageId())) {
			return NotificationAction.MY_BILL;
		} else if (smsInfo.contains(NotificationAction.PROMOTIONS.getPageId())) {
			return NotificationAction.PROMOTIONS;
		} else if (smsInfo.contains(NotificationAction.COST_CONTROL_PREPAID.getPageId())) {
			return NotificationAction.COST_CONTROL_PREPAID;
		} else if (smsInfo.contains(NotificationAction.CALL_DETAIS.getPageId())) {
			return NotificationAction.CALL_DETAIS;
		} else if (smsInfo.contains(NotificationAction.TOP_UP.getPageId())) {
			return NotificationAction.TOP_UP;
		} else if (smsInfo.contains(NotificationAction.PAY_BILL_ANONYMOUS.getPageId())) {
			return NotificationAction.PAY_BILL_ANONYMOUS;
		} else if (smsInfo.contains(NotificationAction.LOYALTY_DASH.getPageId())) {
			return NotificationAction.LOYALTY_DASH;
		} else if (smsInfo.contains(NotificationAction.LOYALTY_NEW_OFFERS.getPageId())) {
			return parseLoyaltyMarketNewVoucherList();
		} else if (smsInfo.contains(NotificationAction.LOYALTY_MY_OFFERS.getPageId())) {
			return parseLoyaltyMarketMyVoucherList();
		} else if (smsInfo.contains(NotificationAction.LOYALTY_OFFER.getPageId())) {
			return parseLoyaltyDetails(smsInfo);
		} else if (smsInfo.contains(NotificationAction.WEBVIEW.getPageId())) {
			return NotificationAction.WEBVIEW;
		} else if (smsInfo.contains(NotificationAction.RETENTION_PHONE_DETAILS.getPageId())) {
			return parsePhoneDetails(smsInfo);
		} else if (smsInfo.contains(NotificationAction.RETENTION_PRICEPLAN_DETAILS.getPageId())) {
			return parsePricePlanDetails(smsInfo);
		} else if (smsInfo.contains(NotificationAction.DETAILS.getPageId())) {
			return parseSmsInfoWithExtraKey(smsInfo, "offer_id", NotificationAction.DETAILS);
		} else if (smsInfo.contains(NotificationAction.GDPR_SELECTION.getPageId())) {
			return NotificationAction.GDPR_SELECTION;
		} else if (smsInfo.contains(NotificationAction.GDPR_MINOR.getPageId())) {
			return NotificationAction.GDPR_MINOR;
		} else if (smsInfo.contains(NotificationAction.GDPR_VODAFONE.getPageId())) {
			return NotificationAction.GDPR_VODAFONE;
		} else if (smsInfo.contains(NotificationAction.GDPR_PARTENERS.getPageId())) {
			return NotificationAction.GDPR_PARTENERS;
		} else if (smsInfo.contains(NotificationAction.TRAVELLING_ABROAD.getPageId())) {
			Log.d("Travelling", "TRAVELLING_ABROAD");
			return NotificationAction.TRAVELLING_ABROAD;
		} else if (smsInfo.contains(NotificationAction.TRAVELLING_ABROAD_ADMINISTRATION.getPageId())) {
			Log.d("Travelling", "TRAVELLING_ABROAD_ADMINISTRATION");
			return NotificationAction.TRAVELLING_ABROAD_ADMINISTRATION;
		} else if (smsInfo.contains(NotificationAction.RETENTION_PHONES_LISTING.getPageId())) {
			return NotificationAction.RETENTION_PHONES_LISTING;
		} else if (smsInfo.contains(NotificationAction.RETENTION_PRICEPLANS_LISTING.getPageId())) {
			return NotificationAction.RETENTION_PRICEPLANS_LISTING;
		} else if (smsInfo.contains(NotificationAction.OFFERS_SERVICES.getPageId())) {
			return NotificationAction.OFFERS_SERVICES;
		}
		return NotificationAction.NONE;
	}

	public static NotificationAction parse(String smsInfo, String url) {
		if (smsInfo == null) {
			return NotificationAction.NONE;
		}
		if (url != null && !url.isEmpty() && url.contains("vodafone.ro/")) {
			if (smsInfo.contains(NotificationAction.WEBVIEW.getPageId())) {
				NotificationAction.WEBVIEW.getIntentActionName().setOneUsageSerializedData(url);
				return NotificationAction.WEBVIEW;
			}
		} else {
			return parse(smsInfo);
		}

		return NotificationAction.NONE;
	}

	private static NotificationAction parseLoyaltyDetails(String smsInfo) {
		String loyaltyDetailsPageId = NotificationAction.LOYALTY_OFFER.getPageId();
		if (loyaltyDetailsPageId != null && !loyaltyDetailsPageId.isEmpty()) {
			Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(smsInfo);
			if (m.find()) {
				String promotionId = m.group(1);
				D.w(promotionId);
				IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS.setOneUsageSerializedData(promotionId);
				return NotificationAction.LOYALTY_OFFER;
			}
		}
		return NotificationAction.NONE;
	}

	private static NotificationAction parsePhoneDetails(String smsInfo) {
		String phoneDetailsPageId = NotificationAction.RETENTION_PHONE_DETAILS.getPageId();
		if (phoneDetailsPageId != null && !phoneDetailsPageId.isEmpty()) {
			Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(smsInfo);
			if (m.find()) {
				String phoneId = m.group(1);
				D.w(phoneId);
				IntentActionName.RETENTION_PHONE_DETAILS.setSecondSerializedData(phoneId);
				return NotificationAction.RETENTION_PHONE_DETAILS;
			}
		}
		return NotificationAction.NONE;
	}

	private static NotificationAction parsePricePlanDetails(String smsInfo) {
		String pricePlanDetailsPageId = NotificationAction.RETENTION_PRICEPLAN_DETAILS.getPageId();
		if (pricePlanDetailsPageId != null && !pricePlanDetailsPageId.isEmpty()) {
			Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(smsInfo);
			if (m.find()) {
				String pricePlanId = m.group(1);
				D.w(pricePlanId);
				IntentActionName.RETENTION_PRICEPLAN_DETAILS.setSecondSerializedData(pricePlanId);
				return NotificationAction.RETENTION_PRICEPLAN_DETAILS;
			}
		}
		return NotificationAction.NONE;
	}

	private static NotificationAction parseLoyaltyMarketNewVoucherList() {
		IntentActionName.LOYALTY_MARKET
				.setOneUsageSerializedData("new_voucher_list");
		return NotificationAction.LOYALTY_NEW_OFFERS;
	}

	private static NotificationAction parseLoyaltyMarketMyVoucherList() {
		IntentActionName.LOYALTY_MARKET
				.setOneUsageSerializedData("reserved_voucher_list");
		return NotificationAction.LOYALTY_NEW_OFFERS;
	}

	/**
	 * For smsInfo as page_id=details&offer_id=12345. You can have more than one type of key with format
	 * page_id=page_id_value&key_1=key_1_value&_key_2=_key_2_value...
	 *
	 * @param smsInfo            Retrived from notification or sms
	 * @param key                other key than page_id
	 * @param notificationAction notification action to return and set the one usage data.
	 * @return
	 */
	private static NotificationAction parseSmsInfoWithExtraKey(String smsInfo, String key, NotificationAction notificationAction) {
		if (smsInfo != null && !smsInfo.isEmpty() && smsInfo.contains("&" + key)) {
			if (smsInfo.contains(notificationAction.getPageId())) {
				String[] strings = smsInfo.split(Pattern.quote("&" + key));
				if (strings.length == 1) {
					notificationAction.getIntentActionName().setOneUsageSerializedData(strings[0]);
				}
			}
		}
		return notificationAction;
	}

	public static NotificationAction parseWithOfferId(String smsInfo, String offerId) {
		if (smsInfo.contains(NotificationAction.DETAILS.getPageId())
				&& offerId != null) {
			NotificationAction.DETAILS.getIntentActionName().setOneUsageSerializedData(offerId);
			return NotificationAction.DETAILS;
		} else
			return NotificationAction.NONE;
	}
}
