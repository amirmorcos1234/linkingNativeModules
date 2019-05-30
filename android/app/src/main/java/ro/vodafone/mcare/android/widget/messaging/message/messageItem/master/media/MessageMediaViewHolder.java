package ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.media;

import android.view.View;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;
import ro.vodafone.mcare.android.widget.messaging.view.image.GlideRoundedImageView;


/**
 * Created by matthewpage on 6/27/16.
 */
public class MessageMediaViewHolder extends MessageViewHolder {
    public GlideRoundedImageView media;

    public MessageMediaViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);
    }
}
