package ro.vodafone.mcare.android.widget.messaging.message;

import android.content.Context;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.spinner.SpinnerItem;

/**
 * Created by matthewpage on 7/5/16.
 */
public class SpinnerMessage extends Message {
    @Override
    public MessageItem toMessageItem(Context context) {
        return new SpinnerItem(context);
    }
}
