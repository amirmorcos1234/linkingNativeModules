package ro.vodafone.mcare.android.widget.messaging.message.messageItem.externalUser.text;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.text.MessageTextViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageExternalUserTextViewHolder extends MessageTextViewHolder {

    public MessageExternalUserTextViewHolder(View itemView, final CustomSettings customSettings) {
        super(itemView, customSettings);

        avatar = (ImageView) itemView.findViewById(R.id.message_scout_text_image_view_avatar);
        text = (TextView) itemView.findViewById(R.id.message_scout_text_text_view_text);
        timestamp = (TextView) itemView.findViewById(R.id.message_scout_text_text_view_timestamp);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        avatarContainer = (ViewGroup) itemView.findViewById(R.id.message_scout_text_image_view_avatar_group);
        bubble = (RelativeLayout) itemView.findViewById(R.id.message_scout_text_view_group_bubble);

        Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_rounded_rectangle_white);
        drawable.setColorFilter(customSettings.externalBubbleBackgroundColor, PorterDuff.Mode.SRC_ATOP);
    }
}
