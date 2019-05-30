package ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalOptions;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;

public class MessageGeneralOptionsViewHolder extends MessageViewHolder {
    public TextView titleTextView;
    public LinearLayout optionsLinearLayout;

    public MessageGeneralOptionsViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);

        this.titleTextView = (TextView) itemView.findViewById(R.id.message_general_options_text_view);
        this.optionsLinearLayout = (LinearLayout) itemView.findViewById(R.id.message_general_options_options_linear_layout);
    }
}
