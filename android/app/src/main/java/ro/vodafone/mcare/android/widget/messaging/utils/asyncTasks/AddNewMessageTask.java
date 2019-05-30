package ro.vodafone.mcare.android.widget.messaging.utils.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import ro.vodafone.mcare.android.ui.activities.support.SlyceMessagingView;
import ro.vodafone.mcare.android.widget.messaging.message.Message;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageRecyclerAdapter;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;
import ro.vodafone.mcare.android.widget.messaging.utils.MessageUtils;


public class AddNewMessageTask extends AsyncTask {
    private List<Message> messages;
    private List<MessageItem> mMessageItems;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private Context context;
    private CustomSettings customSettings;
    private int rangeStartingPoint;
    private SlyceMessagingView view;

    public AddNewMessageTask(
            List<Message> messages,
            List<MessageItem> mMessageItems,
            MessageRecyclerAdapter mRecyclerAdapter,
            RecyclerView mRecyclerView,
            Context context,
            CustomSettings customSettings) {

        this.messages = messages;
        this.mMessageItems = mMessageItems;
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.mRecyclerView = mRecyclerView;
        this.context = context;
        this.customSettings = customSettings;
    }

    public AddNewMessageTask(
            List<Message> messages,
            List<MessageItem> mMessageItems,
            MessageRecyclerAdapter mRecyclerAdapter,
            RecyclerView mRecyclerView,
            Context context,
            CustomSettings customSettings,
            SlyceMessagingView mView) {

        this.messages = messages;
        this.mMessageItems = mMessageItems;
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.mRecyclerView = mRecyclerView;
        this.context = context;
        this.customSettings = customSettings;
        this.view = mView;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        this.rangeStartingPoint = mMessageItems.size() - 1;
        for (Message message : messages) {
            if (context == null) {
                return null;
            }
            mMessageItems.add(message.toMessageItem(context)); // this call is why we need the AsyncTask
        }
        for (int i = rangeStartingPoint; i < mMessageItems.size(); i++) {
            MessageUtils.markMessageItemAtIndexIfFirstOrLastFromSource(i, mMessageItems);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (o != null)
            return;

        boolean isAtBottom = !mRecyclerView.canScrollVertically(1);
        boolean isAtTop = !mRecyclerView.canScrollVertically(-1);

        mRecyclerAdapter.notifyItemRangeInserted(rangeStartingPoint + 1, messages.size() - rangeStartingPoint - 1);
        mRecyclerAdapter.notifyItemChanged(rangeStartingPoint);
        mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
        if(this.view!=null)
        {
            this.view.updateBadge();
        }
       /* if (isAtBottom || messages.get(messages.size() - 1).getSource() == MessageSource.LOCAL_USER) {
            mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
        } else {
            if (isAtTop) {
                ScrollUtils.scrollToTopAfterDelay(mRecyclerView, mRecyclerAdapter);
            }
            Snackbar snackbar = Snackbar.make(mRecyclerView, context.getText(R.string.message_new), Snackbar.LENGTH_SHORT)
                    .setAction(context.getText(R.string.message_view), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mRecyclerView.smoothScrollToPosition(mRecyclerAdapter.getItemCount() - 1);
                        }
                    }).setActionTextColor(customSettings.snackbarButtonColor);
            ViewGroup group = (ViewGroup) snackbar.getView();
            for (int i = 0; i < group.getChildCount(); i++) {
                View v = group.getChildAt(i);
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    textView.setTextColor(customSettings.snackbarTitleColor);
                }
            }
            snackbar.getView().setBackgroundColorWithRes(customSettings.snackbarBackground);
            snackbar.show();
        }*/
    }
}
