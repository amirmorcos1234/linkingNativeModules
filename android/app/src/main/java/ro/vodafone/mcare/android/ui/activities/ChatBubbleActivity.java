package ro.vodafone.mcare.android.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubble;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubbleSingleton;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.utils.D;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by bogdan marica on 5/2/2017.
 */

public  abstract class ChatBubbleActivity extends BaseActivity implements ChatBubbleSingleton.MessagesCountListener {

    public static String contactId;
    public static String sessionKey;
    public static String wlpjSessionId;
    public static String mEmail;
    float positionY;

    protected ChatBubble chatBubble;

    public ChatBubble getChatBubble() {
        if (chatBubble == null) {
            initChatBubble();
        }

        return chatBubble;
    }
    protected abstract void setLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();

        initChatBubble();

        if (ChatBubbleSingleton.getInstance().getMinimized())
            chatBubble.displayBubble();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatBubble.moveToPosition(positionY);

        if (ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut())
            chatBubble.updateBubbleForAgentClosedCase();
        else
            chatBubble.updateBubble();

        if (VodafoneController.getInstance().supportWindow(this) != null)
            VodafoneController.getInstance().supportWindow(this).resumeSupportWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean sameValue = (ChatBubbleSingleton.getInstance().getMinimized() == VodafoneController.getInstance().isChatConnected());
        if (!sameValue)
            VodafoneController.getInstance().supportWindow(this).resetUnreadNotification();
        boolean isMinimized = !ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut()
                && VodafoneController.getInstance().isChatConnected();
        ChatBubbleSingleton.getInstance().setMinimized(isMinimized, false);
    }

    @Override
    public void onBackPressed() {
        hideSnapcardAndOrBubble();
        KeyboardHelper.hideKeyboard(this);
        super.onBackPressed();
    }

    public void hideSnapcardAndOrBubble() {
        if (VodafoneController.getInstance().supportWindow(this) != null) {
            if (ChatBubbleSingleton.getMinimizeToSnapCard()) {
                VodafoneController.getInstance().supportWindow(this).forceClose(false, false, false);
                chatBubble.hideBubble();
            } else if (!ChatBubbleSingleton.getInstance().getMinimized())
                chatBubble.hideBubble();
        } else if (!ChatBubbleSingleton.getInstance().getMinimized())
            chatBubble.hideBubble();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void initChatBubble() {
//        D.w("initChatBubble");
        chatBubble = new ChatBubble(this);
        chatBubble.initSupportBubble();

        ChatBubbleSingleton.getInstance().setCountListener(this);

        if (ChatBubbleSingleton.getInstance().getCountListener() == null) {
            D.d("ChatBubbleSingleton.getInstance().getCountListener() == NULL");
            //ChatBubbleSingleton.getInstance().setCountListener(this);
        } else
            D.d("ChatBubbleSingleton.getInstance().getCountListener() != null");
        positionY = chatBubble.getCurrentPosition();
    }

    @Override
    public void onUpdated() {
        D.d("COUNT = " + ChatBubbleSingleton.getInstance().getMessagesCount());
        chatBubble.updateBubbleBadge(ChatBubbleSingleton.getInstance().getMessagesCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED && requestCode == RESULT_SELECTOR_UPDATED) {
                D.d("msisdn / ban selected");
                hideSnapcardAndOrBubble();
            }
        }

    }

}
