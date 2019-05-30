package ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.text;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.messaging.message.MessageSource;
import ro.vodafone.mcare.android.widget.messaging.message.TextMessage;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItemType;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.DateUtils;

/**
 * Created by matthewpage on 6/27/16.
 */
public class MessageTextItem extends MessageItem {

    public MessageTextItem(TextMessage textMessage, Context context) {
        super(textMessage);
        this.context = context;
    }

    @Override
    public void buildMessageItem(MessageViewHolder messageViewHolder) {

        if (message != null && messageViewHolder != null && messageViewHolder instanceof MessageTextViewHolder && context != null) {
            final MessageTextViewHolder messageTextViewHolder = (MessageTextViewHolder) messageViewHolder;

            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

            // Get content
            if (message.getSource() == MessageSource.LOCAL_USER) {
                if (profile != null) {
                    Glide.with(context)
                            .load(profile.getAvatarUrl() == null ? R.drawable.phone_icon : profile.getAvatarUrl())
                            .into(messageViewHolder.avatar);
                } else {
                    Glide.with(context)
                            .load(R.drawable.phone_icon)
                            .into(messageViewHolder.avatar);
                }
            }else {
                Glide.with(context)
                        .load(R.drawable.vodafone_round_red_white)
                        .into(messageViewHolder.avatar);
            }
            // Get content
            String date = DateUtils.getTimestamp(context, message.getDate());
            String text = ((TextMessage) message).getText();

            // Populate views with content
//            messageTextViewHolder.initials.setText(initials != null ? initials : "");
            messageTextViewHolder.text.setText(text != null ? text : "");
//            D.w();

            messageTextViewHolder.timestamp.setText(date != null ? " at " + date : "");
            messageTextViewHolder.user_name.setText(message.getDisplayName());

//            if (message.getSource() == MessageSource.EXTERNAL_USER)
//                messageTextViewHolder.user_name.setText(message.getDisplayName());
//            else {
//
//                String firstName = "";
//                UserProfile up = ((UserProfile) RealmManager.getRealmObject(UserProfile.class));
//                if (up != null)
//                    firstName = up.getFirstName() + " ";
//                else
//                    firstName = "";
//
//                messageTextViewHolder.user_name.setText(firstName);
//
//            }
            messageTextViewHolder.bubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager)
                            context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple text", ((TextMessage) MessageTextItem.this.message).getText());
                    clipboard.setPrimaryClip(clip);
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(150);
                    String toastMessage = (String) context.getText(R.string.message_text_copied);
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            messageViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageTextViewHolder.customSettings.userClicksAvatarPictureListener != null)
                        messageTextViewHolder.customSettings.userClicksAvatarPictureListener.userClicksAvatarPhoto(message.getUserId());
                }
            });
            messageTextViewHolder.avatar.setVisibility(View.VISIBLE);
            messageTextViewHolder.avatarContainer.setVisibility(View.VISIBLE);
            messageTextViewHolder.timestamp.setVisibility(View.VISIBLE);
            messageTextViewHolder.user_name.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public MessageItemType getMessageItemType() {
        if (message.getSource() == MessageSource.EXTERNAL_USER) {
            return MessageItemType.INCOMING_TEXT;
        } else {
            return MessageItemType.OUTGOING_TEXT;
        }
    }

    @Override
    public MessageSource getMessageSource() {
        return message.getSource();
    }
}
