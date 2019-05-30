package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by bogdan marica on 4/8/2017.
 */
@DontObfuscate
public class PostPollRequest {

    private VodafoneMessage message;
    private int contactId;
    private long sessionKey;
    private boolean userFromEnroll;
    private long lastReadTime;

    public void setUuserFromEnroll(boolean userFromEnroll) {
        this.userFromEnroll = userFromEnroll;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public long getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(long sessionKey) {
        this.sessionKey = sessionKey;
    }

    public VodafoneMessage getMessage() {
        return message;
    }

    public void setMessage(VodafoneMessage message) {
        this.message = message;
    }

    public boolean getUserFromEnroll() {
        return userFromEnroll;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

}
