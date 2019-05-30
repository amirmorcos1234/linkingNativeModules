package ro.vodafone.mcare.android.widget.messaging.message.messageItem.internalUser.text;

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
public class MessageInternalUserTextViewHolder extends MessageTextViewHolder {

    public MessageInternalUserTextViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);

        avatar = (ImageView) itemView.findViewById(R.id.message_user_text_image_view_avatar);
       // carrot = (ImageView) itemView.findViewById(R.id.message_user_text_image_view_carrot);
      //  initials = (TextView) itemView.findViewById(R.id.message_user_text_text_view_initials);
        text = (TextView) itemView.findViewById(R.id.message_user_text_text_view_text);
        timestamp = (TextView) itemView.findViewById(R.id.message_user_text_view_timestamp);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        avatarContainer = (ViewGroup) itemView.findViewById(R.id.message_user_text_view_group_avatar);
        bubble = (RelativeLayout) itemView.findViewById(R.id.message_user_text_view_group_bubble);

        //Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_rounded_rectangle_white);
        // Drawable drawable = itemView.getContext().getDrawable();
       // drawable.setColorFilter(customSettings.localBubbleBackgroundColor, PorterDuff.Mode.SRC_ATOP);
      //  bubble.setBackground(drawable);
       // carrot.setColorFilter(customSettings.localBubbleBackgroundColor);
       // text.setTextColor(customSettings.localBubbleTextColor);
     //   timestamp.setTextColor(customSettings.localBubbleTextColor);
    }
}
