package ro.vodafone.mcare.android.widget.messaging.message.messageItem.master.text;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;


/**
 * Created by matthewpage on 6/27/16.
 */
public abstract class MessageTextViewHolder extends MessageViewHolder {
  //  public ImageView carrot;
    public TextView text;
    public RelativeLayout bubble;

    public MessageTextViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);
    }
}