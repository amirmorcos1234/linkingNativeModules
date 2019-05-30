package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by bogdan marica on 4/13/2017.
 */
@DontObfuscate
public class StartChatResponse {


    String WLPJSESSIONID;//:"lpG8YvJbyRn2HJhbNNqjwmW0x8y4zCHCDnFZ9ym60HQ9WZzQyM2N!-1653979979!1492079707011",
    String contactId;// 63841,
    String sessionKey;//35uofo2v00"
    boolean isExistingAvayaSession;//true"

    public void setWLPJSESSIONID(String WLPJSESSIONID) {
        this.WLPJSESSIONID = WLPJSESSIONID;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

 public void setIsExistingAvayaSession(boolean isExistingAvayaSession) {
        this.isExistingAvayaSession = isExistingAvayaSession;
    }

    public String getContactId() {
        return contactId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getWLPJSESSIONID() {
        return WLPJSESSIONID;
    }

public boolean getIsExistingAvayaSession() {
        return isExistingAvayaSession;
    }

}
