package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by bogdan marica on 4/12/2017.
 */
@DontObfuscate
public class ChatStateResponse {
    String chatState = "null";

    public void setChatState(String chatState) {
        this.chatState = chatState;
    }

    public String getChatState() {
        return chatState;
    }
}
