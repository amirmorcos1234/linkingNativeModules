package ro.vodafone.mcare.android.widget.messaging.message.messageItem.internalUser.media;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.media.MessageMediaViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;
import ro.vodafone.mcare.android.widget.messaging.view.image.GlideRoundedImageView;

/**
 * Created by John C. Hunchar on 5/16/16.
 */
public class MessageInternalUserViewHolder extends MessageMediaViewHolder {

    public MessageInternalUserViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);
        D.w();
        avatar = (ImageView) itemView.findViewById(R.id.message_user_media_image_view_avatar);
        media = (GlideRoundedImageView) itemView.findViewById(R.id.message_user_media_picasso_rounded_image_view_media);
//        initials = (TextView) itemView.findViewById(R.id.message_user_media_text_view_initials);
        timestamp = (TextView) itemView.findViewById(R.id.message_user_media_text_view_timestamp);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        avatarContainer = (ViewGroup) itemView.findViewById(R.id.message_user_media_view_group_avatar);
    }
}
