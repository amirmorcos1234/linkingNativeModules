package ro.vodafone.mcare.android.ui.activities.support;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import rx.Subscription;


/**
 * Created by Bogdan Marica on 5/10/2017.
 * .
 */

public class ChatBubbleSingleton {
    public static final int MESSAGES_COUNT_FOR_SURVEY = 5;
    public static final int SESSIONS_COUNT_FOR_SURVEY = 5;
    private static final ChatBubbleSingleton ourInstance = new ChatBubbleSingleton();
    private static boolean minimizeToSnapCard = false;
    private MessagesCountListener messagesCountListener;
    private boolean isMinimized = false;
    private int messagesCount = 0;

    boolean isConversationMessagesObservableRestarted = false;

    boolean agentResponseEmptyOnce = true;

    //number of messages sent between operator and client in bi-directional way.
    private int numberOfMessagesInActiveChat = 0;

    rx.Subscription waitingForAgent = null;
    rx.Subscription waitingForMessages = null;

    private SupportWindow.DisplayType displayType = SupportWindow.DisplayType.NONE;

    private List<MessageItem> messageItemsList = new ArrayList<>();

    private boolean isClosedByAgentButNotLoggedOut = false;

    private ChatBubbleSingleton() {
    }

    String WLPJSESSIONID;

    public String getWLPJSESSIONID(){
        return WLPJSESSIONID;
    }

    public void setWLPJSESSIONID(String wlpjsessionid){
        WLPJSESSIONID=wlpjsessionid;
    }

    public synchronized static ChatBubbleSingleton getInstance() {
        return ourInstance;
    }

    public static boolean getMinimizeToSnapCard() {
        return minimizeToSnapCard;
    }

    public static void setMinimizeToSnapCard(boolean minToSnapCard) {
        minimizeToSnapCard = minToSnapCard;
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
        if (messagesCountListener != null)
            messagesCountListener.onUpdated();
    }

    public boolean getMinimized() {
        return isMinimized;
    }

    public void setMinimizedFlag(boolean minimized) {
        isMinimized = minimized;
    }

    public void setMinimized(boolean minimized, boolean disableCountListener) {
        isMinimized = minimized;
        if (!isMinimized) {
            messagesCountListener = disableCountListener ? null : messagesCountListener;
            setMessagesCount(0);
        }
    }

    public boolean isClosedByAgentButNotLoggedOut() {
        return isClosedByAgentButNotLoggedOut;
    }

    public void setClosedByAgentButNotLoggedOut(boolean closedByAgentButNotLoggedOut) {
        isClosedByAgentButNotLoggedOut = closedByAgentButNotLoggedOut;
    }

    public SupportWindow.DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(SupportWindow.DisplayType toBeSet) {
        displayType = toBeSet;
    }

    public MessagesCountListener getCountListener() {
        return messagesCountListener;
    }

    public void setCountListener(MessagesCountListener listener) {
        messagesCountListener = listener;
    }


    public void stopAllObservables() {
        VodafoneController.getInstance().setChatConnected(false);
        VodafoneController.getInstance().setConversationOpen(false);
        if (waitingForAgent != null)
            if (!waitingForAgent.isUnsubscribed()) waitingForAgent.unsubscribe();
        if (waitingForMessages != null)
            if (!waitingForMessages.isUnsubscribed()) waitingForMessages.unsubscribe();
    }

    /**
     * Clear current saved message list
     */
    public void clearMessageList() {
        messageItemsList.clear();
        numberOfMessagesInActiveChat = 0;

    }

    public List<MessageItem> getSavedMessageItemsList() {
        return messageItemsList;
    }

    public void saveReceivedOrSentMessageItem(MessageItem messageItem) {
        messageItemsList.add(messageItem);
    }

    public int getSavedMessageItemsListSize() {
        return messageItemsList.size();
    }


    /**
     * Increment number of messages sent in an active chat
     */
    public void incrementNumberOfMessagesInActiveChat() {
        numberOfMessagesInActiveChat++;
    }

    public int getNumberOfMessagesInActiveChat() {
        return numberOfMessagesInActiveChat;
    }

    public interface MessagesCountListener {
        void onUpdated();
    }

    public Subscription getWaitingForAgent() {
        return waitingForAgent;
    }

    public void setWaitingForAgent(Subscription waitingForAgent) {
        this.waitingForAgent = waitingForAgent;
    }

    public Subscription getWaitingForMessages() {
        return waitingForMessages;
    }

    public void setWaitingForMessages(Subscription waitingForMessages) {
        this.waitingForMessages = waitingForMessages;
    }


    /**
     * Method to identify if messageObservable is restarted
     * (this can happen when observable is unsubscribed and subscribed again during an active chat session)
     * @param isConversationMessagesObservableRestarted
     */

    public void setConversationMessagesObservableRestarted(boolean isConversationMessagesObservableRestarted) {
        this.isConversationMessagesObservableRestarted = isConversationMessagesObservableRestarted;
    }

    public boolean isConversationMessagesObservableRestarted() {
        return isConversationMessagesObservableRestarted;
    }

    public boolean isAgentResponseEmptyOnce() {
        return agentResponseEmptyOnce;
    }

    public void setAgentResponseEmptyOnce(boolean agentResponseEmptyOnce) {
        this.agentResponseEmptyOnce = agentResponseEmptyOnce;
    }

    /**
     *
     * @return number of messages between costumer and operator.
     */

   /* public int getNumberOfActiveConversationMessages() {
        int numberOfMessages = 0;

        for (MessageItem messageItem : messageItemsList) {
//            if (messageItem.getMessage().getMessageType() == MessageType.OPEN_CONVERSATION_MESSAGE) {
//                numberOfMessages++;
//            }
        }

        return numberOfMessages;
    }*/
}


