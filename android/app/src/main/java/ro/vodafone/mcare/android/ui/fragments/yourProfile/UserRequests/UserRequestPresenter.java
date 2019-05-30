package ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.PaymentConfirmationLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.realm.system.UserRequestsLabels;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequest;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequestsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserRequestService;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

/**
 * Created by alexandrulepadatu on 3/15/18.
 */

public class UserRequestPresenter implements UserRequestContract.Presenter
{
//    private static final String TAG = "UserRequestPresenter";

    private UserRequestContract.View view;
    private UserRequestService userRequestService = new UserRequestService(VodafoneController.getInstance());

    private List<Object> listPending = new ArrayList<>();
    private List<Object> listAccepted = new ArrayList<>();
    private List<Object> listRejected = new ArrayList<>();

    private List<Subscription> subscriptionList = new ArrayList<>();


    UserRequestPresenter(UserRequestContract.View view)
    {
        this.view = view;
    }

    @Override
    public void getData(final int tabIndexToSwitchTo)
    {
        if (isAccesRestricted())
        {
            view.showErrorMessage(UserRequestsLabels.getFraudCaseMessage(), R.drawable.warning, false);
            return;
        }

        boolean vfEBUMigrated = VodafoneController.getInstance().getUserProfile().isMigrated();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String vfOdsCid = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid() : null;
        String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() : null;
        String vfOdsBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        subscriptionList.add(
            userRequestService.getUserRequests(vfEBUMigrated,
                    vfSsoUserRole,
                    vfOdsCid,
                    vfOdsBan,
                    crmRole,
                    true,
                    true,
                    true)
            .take(1)
            .subscribe(new RequestSessionObserver<GeneralResponse<UserRequestsSuccess>>() {
                @Override
                public void onNext(GeneralResponse<UserRequestsSuccess> response)
                {
                    processResponse(response, tabIndexToSwitchTo);
                }

                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    super.onError(e);

                    if (view != null)
                        view.showErrorMessage(PaymentConfirmationLabels.getGeneralErrorMessageWithRetry(), R.drawable.no_offers, true);
                }
            }));
    }

    @Override
    public List<Object> getListPending() {
        return listPending;
    }

    @Override
    public List<Object> getListAccepted() {
        return listAccepted;
    }

    @Override
    public List<Object> getListRejected() {
        return listRejected;
    }

    private boolean isAccesRestricted(){
        CustomerRestrictionsSuccess customerRestrictionsSuccess = (CustomerRestrictionsSuccess)
                RealmManager.getRealmObject(CustomerRestrictionsSuccess.class);

        return customerRestrictionsSuccess != null && (customerRestrictionsSuccess.getIsBlacklistForever()
                || customerRestrictionsSuccess.getIsCollectionRestricted() || customerRestrictionsSuccess.getIsDeviceBlacklist()
                || customerRestrictionsSuccess.getIsServiceBadDebt());
    }

    private boolean processResponse(final GeneralResponse<UserRequestsSuccess> response, int tabIndexToSwitchTo)
    {
        if (view != null && response.getTransactionFault() != null)
        {
            view.showErrorMessage(UserRequestsLabels.getFraudCaseMessage(), R.drawable.warning, false);
            return false;
        }

        filterData(response.getTransactionSuccess().getPendingList(),
                response.getTransactionSuccess().getAcceptedList(),
                response.getTransactionSuccess().getRejectedList(),
                tabIndexToSwitchTo);

        return true;
    }

    private void filterData(List<UserRequest> newListPending, List<UserRequest> newListAccepted, List<UserRequest> newListRejected, int tabIndexToSwitchTo)
    {
        listPending.clear();
        listAccepted.clear();
        listRejected.clear();

        final DateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);

        for (UserRequest request : newListPending)
            request.setStatus(UserRequest.Status.PENDING);

        for (UserRequest request : newListAccepted)
            request.setStatus(UserRequest.Status.ACCEPTED);

        for (UserRequest request : newListRejected)
            request.setStatus(UserRequest.Status.REJECTED);

        Collections.sort(newListPending, new Comparator<UserRequest>() {
            @Override
            public int compare(UserRequest lhs, UserRequest rhs) {
                try {
                    Date lhsTime = formatter.parse(lhs.getRequestTime());
                    Date rhsTime = formatter.parse(rhs.getRequestTime());

                    return rhsTime.compareTo(lhsTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });
        listPending.addAll(newListPending);

        // sort the lists by date

        Comparator<UserRequest> comparator = new Comparator<UserRequest>() {
            @Override
            public int compare(UserRequest lhs, UserRequest rhs) {
                try {
                    Date lhsTime = formatter.parse(lhs.getProcessTime());
                    Date rhsTime = formatter.parse(rhs.getProcessTime());

                    return lhsTime.compareTo(rhsTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        };

        Collections.sort(newListAccepted, comparator);
        Collections.reverse(newListAccepted);
        groupRequests(newListAccepted, listAccepted);

        Collections.sort(newListRejected, comparator);
        Collections.reverse(newListRejected);
        groupRequests(newListRejected, listRejected);

        if (view != null)
            view.notifyDataChanged(tabIndexToSwitchTo);
    }

    private void groupRequests(@NonNull List<UserRequest> listRequests, @NonNull List<Object> outGroupedRequests)
    {
        String previousDate = null;

        long today = new Date().getTime();
        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        for (UserRequest request : listRequests)
        {
            boolean areDatesEqual = false;
            try {
                areDatesEqual = DateUtils.areDatesEqual(format.parse(request.getProcessTime()), format.parse(previousDate));
            }
            catch (Exception e) {}

            if (!areDatesEqual)
            {
                if (DateUtils.isToday(DateUtils.fromStringToDate(request.getProcessTime(), format).getTime(), today))
                    outGroupedRequests.add(AppLabels.getTodayLabel());
                else
                    outGroupedRequests.add(StringUtils.capitalizeFirstLetter(DateUtils.translateDateStringToRo(request.getProcessTime(), "d MMM yyyy")));

                previousDate = request.getProcessTime();
            }

            outGroupedRequests.add(request);
        }
    }

    @Override
    public void processPendingRequest(final int index, final UserRequest.Status status)
    {
        final String action = status.name().toLowerCase(Locale.getDefault());

        final UserRequest request = (UserRequest) listPending.get(index);
        request.setStatus(status);

        boolean vfEBUMigrated = VodafoneController.getInstance().getUserProfile().isMigrated();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String vfOdsCid = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid() : null;
        String crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() : null;
        String vfOdsBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        subscriptionList.add(userRequestService.postUserRequest(action,
                request.getRequestId(),
                request.getRequesterEmail(),
                vfEBUMigrated,
                vfSsoUserRole,
                vfOdsCid,
                vfOdsBan,
                crmRole,
                request.getRequesterName())
                .take(1)
                .subscribe(new RequestSessionObserver<GeneralResponse<UserRequestsSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<UserRequestsSuccess> response)
                    {
                        if (view != null)
                        {
                            if (response.getTransactionFault() != null)
                            {
                                view.showToast(TopUPLabels.getTop_up_api_call_fail(), false, true);
                                request.setStatus(UserRequest.Status.PENDING);
                                view.notifyDataChanged(UserRequest.Status.PENDING.getValue());
                            }
                            else
                            {
                                view.showToast(UserRequestsLabels.getRequestProcessed(), true, false);
                                getData(request.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        if (view != null)
                            view.showToast(TopUPLabels.getTop_up_api_call_fail(), false, true);
                    }
                }));
    }

    @Override
    public void unsubscribe()
    {
        for (Subscription subscription : subscriptionList) {
            subscription.unsubscribe();
        }
    }
}
