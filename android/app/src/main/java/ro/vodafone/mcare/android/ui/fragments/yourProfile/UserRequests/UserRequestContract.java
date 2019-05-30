package ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests;

import java.util.List;

import ro.vodafone.mcare.android.client.model.userRequest.UserRequest;
import ro.vodafone.mcare.android.interfaces.MvpContract;

/**
 * Created by alexandrulepadatu on 3/14/18.
 */

public interface UserRequestContract
{
    interface View extends MvpContract.View
    {
        void notifyDataChanged(int tabIndexToSwitchTo);

        void showErrorMessage(String message, int drawableId, boolean allowRetry);

        void showToast(String message, boolean success, boolean error);

        void notifyRequestProcessed(UserRequest.Status status);
    }

    interface Presenter extends MvpContract.Presenter<View>
    {
        List<Object> getListPending();

        List<Object> getListAccepted();

        List<Object> getListRejected();

        void processPendingRequest(int index, UserRequest.Status status);

        void getData(int tabIndexToSwitchTo);
    }
}
