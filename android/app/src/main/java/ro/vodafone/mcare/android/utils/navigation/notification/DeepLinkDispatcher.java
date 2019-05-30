package ro.vodafone.mcare.android.utils.navigation.notification;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

/**
 * Created by Bivol Pavel on 25.04.2018.
 */

public class DeepLinkDispatcher {

    private static String deeplink;
    private static String offerId;
    private static String url;

    private static String getDeepLink(Uri data) {
        Set<String> params = data.getQueryParameterNames();
        try {
            if (params.contains("page_id"))
                return URLDecoder.decode(data.getQueryParameter("page_id"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getOfferId(Uri data) {
        Set<String> params = data.getQueryParameterNames();
        try {
            if (params.contains("offer_id"))
                return URLDecoder.decode(data.getQueryParameter("offer_id"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getUrl(Uri data) {
        Set<String> params = data.getQueryParameterNames();
        try {
            if (params.contains("url"))
                return URLDecoder.decode(data.getQueryParameter("url"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void dispatchAction(Uri data) {
        NotificationAction notificationAction = getNotificationAction(data);
        NotificationNavigationAction.startNavigationAction(notificationAction);
    }

    public static NotificationAction getNotificationAction(Uri data) {
        if (data != null && !Uri.EMPTY.equals(data)) {

            deeplink = getDeepLink(data);
            offerId = getOfferId(data);
            url = getUrl(data);

            if (deeplink != null && !deeplink.isEmpty() && (url == null || url.isEmpty()) && (deeplink.toLowerCase().startsWith("http://") || deeplink.startsWith("https://"))) {
                url = deeplink;
                deeplink = "webview";
            }

            if (deeplink != null && !deeplink.isEmpty()) {
                if (offerId == null || offerId.isEmpty()) {
                    return NotificationNavigationAction.getNotificationAction(deeplink, url);
                } else {
                    return NotificationNavigationAction.getNotificationActionWithOfferId(deeplink, offerId);
                }
            }
        }
        return NotificationAction.NONE;
    }
}