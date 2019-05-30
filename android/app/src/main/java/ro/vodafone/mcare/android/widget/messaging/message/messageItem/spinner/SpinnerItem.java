package ro.vodafone.mcare.android.widget.messaging.message.messageItem.spinner;


import android.content.Context;

import ro.vodafone.mcare.android.widget.messaging.message.MessageSource;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItemType;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;

/**
 * Created by matthewpage on 7/5/16.
 */
public class SpinnerItem extends MessageItem {
    public SpinnerItem(Context context) {
        super(null);
    }

    @Override
    public void buildMessageItem(MessageViewHolder messageViewHolder) {

    }

    @Override
    public MessageItemType getMessageItemType() {
        return MessageItemType.SPINNER;
    }

    @Override
    public MessageSource getMessageSource() {
        return MessageSource.EXTERNAL_USER;
    }
}
