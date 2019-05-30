package ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalText;


import android.content.Context;

import ro.vodafone.mcare.android.widget.messaging.message.GeneralTextMessage;
import ro.vodafone.mcare.android.widget.messaging.message.MessageSource;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItemType;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;

public class MessageGeneralTextItem extends MessageItem {
    public MessageGeneralTextItem(GeneralTextMessage message,Context context) {
        super(message);
    }

    @Override
    public void buildMessageItem(
            MessageViewHolder messageViewHolder) {
        MessageGeneralTextViewHolder viewHolder = (MessageGeneralTextViewHolder) messageViewHolder;
        GeneralTextMessage generalTextMessage = (GeneralTextMessage) message;
        viewHolder.messageTextView.setText(generalTextMessage.getText());
    }

    @Override
    public MessageItemType getMessageItemType() {
        return MessageItemType.GENERAL_TEXT;
    }

    @Override
    public MessageSource getMessageSource() {
        return MessageSource.GENERAL;
    }
}
