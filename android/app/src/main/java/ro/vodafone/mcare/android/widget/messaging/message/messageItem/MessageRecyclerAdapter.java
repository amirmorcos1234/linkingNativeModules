package ro.vodafone.mcare.android.widget.messaging.message.messageItem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubbleSingleton;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.externalUser.media.MessageExternalUserMediaViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.externalUser.text.MessageExternalUserTextViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalOptions.MessageGeneralOptionsViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.general.generalText.MessageGeneralTextViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.internalUser.media.MessageInternalUserViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.internalUser.text.MessageInternalUserTextViewHolder;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.spinner.SpinnerViewHolder;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final String TAG = MessageRecyclerAdapter.class.getName();

    private List<MessageItem> mMessageItems;
    private CustomSettings customSettings;
    private RecyclerView chatRecyclerView;
    private PagingScrollView scrollView;
    private Context context;

    public MessageRecyclerAdapter(RecyclerView recyclerView, CustomSettings customSettings, PagingScrollView scrollView) {
        this.chatRecyclerView = recyclerView;
        this.customSettings = customSettings;
        this.scrollView = scrollView;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessageViewHolder viewHolder = null;

        MessageItemType messageItemType = MessageItemType.values()[viewType];
        switch (messageItemType) {

            case INCOMING_MEDIA:
                View scoutMediaView = inflater.inflate(R.layout.item_message_external_media, parent, false);
                viewHolder = new MessageExternalUserMediaViewHolder(scoutMediaView, customSettings);
                break;

            case INCOMING_TEXT:
                View scoutTextView = inflater.inflate(R.layout.item_message_external_text, parent, false);
                viewHolder = new MessageExternalUserTextViewHolder(scoutTextView, customSettings);
                break;

            case OUTGOING_MEDIA:
                View userMediaView = inflater.inflate(R.layout.item_message_user_media, parent, false);
                viewHolder = new MessageInternalUserViewHolder(userMediaView, customSettings);
                break;

            case OUTGOING_TEXT:
                View userTextView = inflater.inflate(R.layout.item_message_user_text, parent, false);
                viewHolder = new MessageInternalUserTextViewHolder(userTextView, customSettings);
                break;

            case SPINNER:
                View spinnerView = inflater.inflate(R.layout.item_spinner, parent, false);
                viewHolder = new SpinnerViewHolder(spinnerView, customSettings);
                break;

            case GENERAL_TEXT:
                View generalTextView = inflater.inflate(R.layout.item_message_general_text, parent, false);
                viewHolder = new MessageGeneralTextViewHolder(generalTextView, customSettings);
                break;

            case GENERAL_OPTIONS:
                View generalOptionsView = inflater.inflate(R.layout.item_message_general_options, parent, false);
                viewHolder = new MessageGeneralOptionsViewHolder(generalOptionsView, customSettings);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        if (messageViewHolder == null) {
            return;
        }
//        D.w();
        // Build the item
        MessageItem messageItem = getMessageItemByPosition(position);
        if (messageItem != null) {
            messageItem.setContext(context);
            messageItem.buildMessageItem(messageViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return ChatBubbleSingleton.getInstance().getSavedMessageItemsList() != null ? ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize() : 0;
    }

    @Override
    public int getItemViewType(int position) {

        // Get the item type
        Integer itemType = getMessageItemType(position);
        if (itemType != null) {
            return itemType;
        }

        return super.getItemViewType(position);
    }

    private MessageItem getMessageItemByPosition(int position) {
        if (ChatBubbleSingleton.getInstance().getSavedMessageItemsList() != null && !ChatBubbleSingleton.getInstance().getSavedMessageItemsList().isEmpty()) {
            if (position >= 0 && position < ChatBubbleSingleton.getInstance().getSavedMessageItemsList().size()) {
                MessageItem messageItem = ChatBubbleSingleton.getInstance().getSavedMessageItemsList().get(position);
                if (messageItem != null) {
                    return messageItem;
                }
            }
        }

        return null;
    }

    private Integer getMessageItemType(int position) {
        MessageItem messageItem = getMessageItemByPosition(position);
        if (messageItem != null) {
            return messageItem.getMessageItemTypeOrdinal();
        }

        return null;
    }

    public void updateMessageItemDataList(MessageItem messageItem) {
        if(VodafoneController.getInstance().isChatConnected() ||
             VodafoneController.getInstance().isConversationOpen()){
            ChatBubbleSingleton.getInstance().saveReceivedOrSentMessageItem(messageItem);
            updateRecyclerViewWithNewlyInsertedData();
        }
    }

    private void updateRecyclerViewWithNewlyInsertedData() {
        if (ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize() > 0) {
            this.notifyItemInserted(ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize() - 1);
            Log.d(TAG, "updateRecyclerViewWithNewlyInsertedData: size" + ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize());

            chatRecyclerView.smoothScrollToPosition(ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize() - 1);

            // ScrollUtils.scrollToBottomAfterDelay(chatRecyclerView, this);

        }
    }
}
