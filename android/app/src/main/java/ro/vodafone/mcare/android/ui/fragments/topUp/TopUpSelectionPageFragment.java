package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpSelectionPageFragment extends BaseFragment {

    public static final String TAG = "TopUpSelectionPage";

    private LinearLayout fragmentContainer;
    private boolean isRequestFailed = false;
    private boolean isActiveRecurrentRecharges = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.selection_page_fragment, container, false);

        fragmentContainer = (LinearLayout) v.findViewById(R.id.selection_page_container);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
            getBillingRecharges(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
        } else {
            setupButtons();
        }
        TealiumHelper.tealiumTrackView(TopUpSelectionPageFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);

    }

    private void setupButtons() {
        if(getActivity() != null && !getActivity().isFinishing()){
            List<SelectionPageButton> selectionPageButtons = VodafoneController.getInstance().getUser().getTopUpSelectionPageButtons(getContext());

            if(fragmentContainer != null && fragmentContainer.getChildCount() > 0)
                fragmentContainer.removeAllViews();

            if(selectionPageButtons != null) {
                for (final SelectionPageButton button : selectionPageButtons) {
                    if(button.getTarget() instanceof TopUpRecurrentRechargesFragment){
                        if(!isRequestFailed && isActiveRecurrentRecharges)
                            button.addButton(fragmentContainer);
                    } else
                        button.addButton(fragmentContainer);

                    if (button.getTarget() instanceof Fragment) {
                        button.getLayoutView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fragmentContainer.removeAllViews();
                                callTopUpServices(button);
                                TealiumHelper.tealiumTrackEvent(TopUpSelectionPageFragment.class.getSimpleName(),
                                        button.getButtonTittle(), TealiumConstants.topUpScreenName, "button=");
                            }
                        });
                    }
                }
            }
        }
    }

    private void callTopUpServices(SelectionPageButton button) {
        Log.d(TAG, "callTopUpServices");

        String banID = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        Log.d(TAG, "BAN selected: " + banID);
        if(button.getTarget() instanceof TopUpPrepaidOwnNumberFragment){
            if(getActivity()!=null)
                ((TopUpActivity)getActivity()).addFragment((Fragment) button.getTarget());
        } else if(button.getTarget() instanceof TopUpRecurrentRechargesFragment) {
            if(getActivity()!=null)
                ((TopUpActivity) getActivity()).addFragment((Fragment) button.getTarget());

        } else if (button.getTarget() instanceof TopUpCreditInAdvanceFragment){
            if(getActivity() != null)
                ((TopUpActivity) getActivity()).getCreditInAdvanceEligibility();
        } else {
            showLoadingDialog();
            getFavoriteNumbers(button);
        }
    }

    private void replaceCurrentFragment(SelectionPageButton button) {
            stopLoadingDialog();
            if(getActivity()!=null)
                ((TopUpActivity) getActivity()).addFragment((Fragment) button.getTarget());
    }

    public void getFavoriteNumbers(final SelectionPageButton button) {

        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.getFavoriteNumbers().subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                super.onNext(favoriteNumbersSuccessGeneralResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                replaceCurrentFragment(button);
            }

            @Override
            public void onCompleted() {
                replaceCurrentFragment(button);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((TopUpActivity) getActivity()).getNavigationHeader().setTitle("Reîncarcă");

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser || VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser) {
            ((TopUpActivity)getActivity()).getNavigationHeader().buildMsisdnSelectorHeader();
        } else if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
            ((TopUpActivity)getActivity()).getNavigationHeader().buildBanSelectorHeader();
        }
    }

    public void getBillingRecharges(String ban){
        showLoadingDialog();
        Log.d(TAG, "getBillingRecharges: ban " + ban);
        if(ban!=null){
            Log.d(TAG, "getBillingRecharges service");
            RechargeService rechargeService = new RechargeService(getContext());
            rechargeService.getBillRecharges(ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<BillRechargesSuccess>>() {
                @Override
                public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                    super.onNext(billRechargesSuccessGeneralResponse);
                    if(billRechargesSuccessGeneralResponse.getTransactionStatus() == 0){
                        isRequestFailed = false;
                        checkRecurrentRechargesResults(billRechargesSuccessGeneralResponse.getTransactionSuccess());
                    } else {
                        isRequestFailed = true;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    stopLoadingDialog();
                    isRequestFailed = true;
                    Log.d(TAG, "error service");
                    setupButtons();
                }

                @Override
                public void onCompleted() {
                    stopLoadingDialog();
                    Log.d(TAG, "completed service");
                    setupButtons();
                }
            });
        } else{
            stopLoadingDialog();
            isRequestFailed = true;
            setupButtons();
        }
    }

    private void checkRecurrentRechargesResults(BillRechargesSuccess billRechargesSuccess){
        if(billRechargesSuccess.getMonthlyRecharges().size() > 0 || billRechargesSuccess.getOneTimeRecharges().size() > 0
                || billRechargesSuccess.getWeeklyRecharges().size() > 0)
            isActiveRecurrentRecharges = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getBillingRecharges(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
    }
}
