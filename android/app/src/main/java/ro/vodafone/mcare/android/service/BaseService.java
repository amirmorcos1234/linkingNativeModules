package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dragos.ivanov on 05.08.2016.
 */
public abstract class BaseService {

    protected static final String REQUESTER_PASSWORD = "RequesterPassword";
    protected static final String REQUESTER_ID = "RequesterId";
    protected static final String INITIAL_TOKEN = "initialToken";
    protected static final String SEAMLESS_MSISDN = "seamlessMsisdn";
    protected Context context;
    protected String requesterId;
    protected String requesterPasssword;

    private static final String WLPJ_SESSION_ID = "WLPJSESSIONID";
    private static final String RP_COOKIE = "RPCOOKIE";
    private static final String J_SESSION_ID = "JSESSIONID";
    private static final String AMLB_COOKIE = "amlbcookie";
    private static final String[] COOKIES_AFTER_LOGIN = {WLPJ_SESSION_ID, RP_COOKIE, J_SESSION_ID};

    private static String wlpjsessionid;
    private static String rpcookie;
    private static String jsessionid;
    private static String amlbcookie;

    public static String getAmlbCookie() {
        return amlbcookie;
    }

    public static String getRpCookie() {
        return rpcookie;
    }

    public BaseService(Context context) {
        this.context = context;
        requesterId = (context != null ? context : VodafoneController.getInstance()).getResources().getString(R.string.requesterId);
        requesterPasssword = (context != null ? context : VodafoneController.getInstance()).getResources().getString(R.string.requesterPassword);
    }

    public static String createCookieStatic(Context context) {


        String cookie_value;
        String name = SettingsLabels.getIplanetCookieName();
        String value_final;

        value_final = UserDataController.getSsoTokenId();

        cookie_value = name + "=" + value_final + ";";

        if (wlpjsessionid != null) {
            cookie_value += wlpjsessionid + ";";
        }
        if (rpcookie != null) {
            cookie_value += rpcookie + ";";
        }
        if (jsessionid != null) {
            cookie_value += jsessionid + ";";
        }
        if (amlbcookie != null) {
            cookie_value += amlbcookie + ";";
        }
        Log.d("COOKIE", "value_final : " + cookie_value);

        return cookie_value;
    }

    public static String createCookieStaticWithExtras(Context context, Map<String, String> cookiesMap, String flag) {


        String cookie_value;

        String cookieExtra = "";
        for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
            cookieExtra = cookieExtra + entry.getKey() + "=" + entry.getValue() + ";";
        }

        Log.d("COOKIE", "cookieExtra : " + cookieExtra);
        if (flag.equals("shop_webview")) {
            String name = SettingsLabels.getIplanetCookieName();

            String value_final = "";

            value_final = UserDataController.getSsoTokenId();

            cookie_value = name + "=" + value_final + ";" + cookieExtra;
        } else {
            cookie_value = createCookieStatic(context) + cookieExtra + ";";
        }


        Log.d("COOKIE", "cookie_value : " + cookie_value);
        return cookie_value;
    }

    static void setCookieAfterLogin(List<String> cookiesAfterLogin) {
        Log.d("COOKIE", "cookieAfterLogin : " + cookiesAfterLogin.size());
        for (String cookie : cookiesAfterLogin) {
            Log.d("COOKIE", "cookieAfterLogin : " + cookie);
            if (cookie.contains(RP_COOKIE)) {
                rpcookie = cookie.split(";")[0];
            } else if (cookie.contains(WLPJ_SESSION_ID)) {
                wlpjsessionid = cookie.split(";")[0];
            } else if (cookie.contains(AMLB_COOKIE)) {
                amlbcookie = cookie.split(";")[0];
            }
        }
    }

    public static void setCookieAfterGetChatStateVdfChat(List<String> cookieAfterGetChatStateVdfChat) {
        if (cookieAfterGetChatStateVdfChat == null) {
            return;
        }
        for (String cookie : cookieAfterGetChatStateVdfChat) {
            if (cookie.contains(WLPJ_SESSION_ID)) {
                wlpjsessionid = cookie.split(";")[0];
            }
        }
    }

    public static void clearData() {
        wlpjsessionid = null;
        rpcookie = null;
        jsessionid = null;
        amlbcookie = null;

    }

    public static void setWlpjsessionid(String wlpjsessionid) {
        BaseService.wlpjsessionid = WLPJ_SESSION_ID + "=" + wlpjsessionid;
    }

    static Observable prepare(Observable observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
