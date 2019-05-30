package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by User1 on 5/5/2017.
 */

@DontObfuscate
public class SupportData {


    String WLPJSESSIONID;
    String contactId;
    String sessionKey;
    String mEmail;

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getWLPJSESSIONID() {
        return WLPJSESSIONID;
    }

    public void setWLPJSESSIONID(String WLPJSESSIONID) {
        this.WLPJSESSIONID = WLPJSESSIONID;
    }

    public String getcontactId() {
        return contactId;
    }

    public String getsessionKey() {
        return sessionKey;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }


}
