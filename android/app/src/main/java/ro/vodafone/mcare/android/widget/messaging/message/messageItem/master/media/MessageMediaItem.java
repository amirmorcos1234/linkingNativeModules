package ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.media;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;


import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.messaging.ViewImageActivity;
import ro.vodafone.mcare.android.widget.messaging.message.MediaMessage;
import ro.vodafone.mcare.android.widget.messaging.message.MessageSource;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItemType;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.DateUtils;


/**
 * Created by matthewpage on 6/27/16.
 */
public abstract class MessageMediaItem extends MessageItem {

    public MessageMediaItem(MediaMessage mediaMessage, Context context) {
        super(mediaMessage);
        this.context = context;
    }

    @Override
    public void buildMessageItem(MessageViewHolder messageViewHolder) {
//        D.e("CHECKPOINT");

        if (message != null && messageViewHolder != null && messageViewHolder instanceof MessageMediaViewHolder) {
            final MessageMediaViewHolder messageMediaViewHolder = (MessageMediaViewHolder) messageViewHolder;
//            D.w("CHECKPOINT");

            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
//            D.w("avatar url : "+profile.getAvatarUrl());

            // Get content
            if (message.getSource() == MessageSource.LOCAL_USER)
                if (profile != null)
                    Glide.with(context)
                            .load(profile.getAvatarUrl() == null ? R.drawable.phone_icon : profile.getAvatarUrl())
                            .into(messageViewHolder.avatar);
                else
                    Glide.with(context)
                            .load(R.drawable.phone_icon)
                            .into(messageViewHolder.avatar);
            else
                Glide.with(context)
                        .load(R.drawable.vodafone_round_red_white)
                        .into(messageViewHolder.avatar);


            date = DateUtils.getTimestamp(context, message.getDate());
            final Uri uri = getMediaMessage().getUri();

            Glide.with(context)
                    .load(uri)
                    .bitmapTransform(new RoundedCornersTransformation(context,30,10))
                    .into(messageMediaViewHolder.media);


            messageMediaViewHolder.media.setWidthToHeightRatio((float) 3 / 4);

            // Populate views with content
            messageMediaViewHolder.timestamp.setText(date != null ? " at " + date : "");
//            messageMediaViewHolder.initials.setText(initials != null ? initials : "");


            messageViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageMediaViewHolder.customSettings.userClicksAvatarPictureListener != null)
                        messageMediaViewHolder.customSettings.userClicksAvatarPictureListener.userClicksAvatarPhoto(message.getUserId());
                }
            });

            messageMediaViewHolder.media.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.putExtra("URI", uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            messageMediaViewHolder.avatar.setVisibility(View.VISIBLE);
            messageMediaViewHolder.avatarContainer.setVisibility(View.VISIBLE);
//            messageMediaViewHolder.initials.setVisibility(View.VISIBLE);
            messageMediaViewHolder.media.setVisibility(View.VISIBLE);
            messageMediaViewHolder.timestamp.setVisibility(View.VISIBLE);
            messageMediaViewHolder.user_name.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public MessageItemType getMessageItemType() {
        if (message.getSource() == MessageSource.EXTERNAL_USER) {
            return MessageItemType.INCOMING_MEDIA;
        } else {
            return MessageItemType.OUTGOING_MEDIA;
        }
    }

    @Override
    public MessageSource getMessageSource() {
        return message.getSource();
    }

    public MediaMessage getMediaMessage() {
        return (MediaMessage) message;
    }

    public boolean dateNeedsUpdated(long time) {
        return DateUtils.dateNeedsUpdated(context, time, date);
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }
}
