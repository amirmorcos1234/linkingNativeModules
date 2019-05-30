package ro.vodafone.mcare.android.widget.messaging.message.messageItem.spinner;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ProgressBar;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;

/**
 * Created by matthewpage on 7/5/16.
 */
public class SpinnerViewHolder extends MessageViewHolder {
    public ProgressBar spinner;
    public SpinnerViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);
        this.spinner = (ProgressBar) itemView.findViewById(R.id.loading_bar);
        this.spinner.getIndeterminateDrawable().setColorFilter(R.color.text_pink, PorterDuff.Mode.MULTIPLY);
    }
}