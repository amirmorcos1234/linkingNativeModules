package ro.vodafone.mcare.android.widget.messaging.message.messageItem.externalUser.media;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.media.MessageMediaViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;
import ro.vodafone.mcare.android.widget.messaging.view.image.GlideRoundedImageView;

/**
 * Created by John C. Hunchar on 5/16/16.
 */
public class MessageExternalUserMediaViewHolder extends MessageMediaViewHolder {

    public MessageExternalUserMediaViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);

        this.avatarContainer = (ViewGroup) itemView.findViewById(R.id.message_scout_media_image_view_avatar_group);
        avatar = (ImageView) itemView.findViewById(R.id.message_scout_media_image_view_avatar);
        media = (GlideRoundedImageView) itemView.findViewById(R.id.message_scout_media_picasso_rounded_image_view_media);
        timestamp = (TextView) itemView.findViewById(R.id.message_scout_media_text_view_timestamp);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
//        initials = (TextView) itemView.findViewById(R.id.message_scout_media_text_view_initials);
    }
}
