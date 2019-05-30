package ro.vodafone.mcare.android.widget.messaging.message;

import android.content.Context;
import android.net.Uri;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.externalUser.media.MessageExternalUserMediaItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.internalUser.media.MessageInternalUserMediaItem;

/**
 * Created by matthewpage on 6/21/16.
 */
public class MediaMessage extends Message {
    Uri uri;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public MessageItem toMessageItem(Context context) {
        if (this.source == MessageSource.EXTERNAL_USER)
            return new MessageExternalUserMediaItem(this, context);
        else
            return new MessageInternalUserMediaItem(this, context);
    }
}