package ro.vodafone.mcare.android.widget.messaging.message;

import android.content.Context;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalText.MessageGeneralTextItem;

public class GeneralTextMessage extends Message {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public MessageItem toMessageItem(Context context) {
        return new MessageGeneralTextItem(this,context);
    }
}
