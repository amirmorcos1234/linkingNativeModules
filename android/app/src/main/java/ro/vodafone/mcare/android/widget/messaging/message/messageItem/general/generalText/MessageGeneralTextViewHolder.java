package ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalText;

import android.view.View;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;

public class MessageGeneralTextViewHolder extends MessageViewHolder {
    public TextView messageTextView;

    public MessageGeneralTextViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);

        messageTextView = (TextView) itemView.findViewById(R.id.message_general_text_text_view);
        messageTextView.setTextColor(customSettings.timestampColor);
    }
}
