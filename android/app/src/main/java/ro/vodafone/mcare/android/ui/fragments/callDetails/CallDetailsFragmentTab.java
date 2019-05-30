package ro.vodafone.mcare.android.ui.fragments.callDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.CallDetailsRecyclerViewAdapter;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsRow;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.billDates.BillsDatesSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.CallDetailsRequest;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.activities.CallDetailsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_BILLED;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_PREPAID;
import static ro.vodafone.mcare.android.ui.activities.CallDetailsActivity.REPORT_TYPE_UNBILLED;

public class CallDetailsFragmentTab extends BaseFragment {

    public static String TAG = "FragmentTab";

    private LinearLayout v;
    @BindView(R.id.no_results_layout)
    public LinearLayout noResultsLayout;
    @BindView(R.id.overflow_elements_layout)
    public LinearLayout overflowElementsLayout;
    @BindView(R.id.system_error_layout)
    public FrameLayout systemErrorLayout;
    private LinearLayoutManager linearLayoutManager;

    private CustomWidgetLoadingLayout customWidgetLoadingLayout;
    @BindView(R.id.error_circle)
    public ImageView errorCircle;
    @BindView(R.id.call_details_list)
    public RecyclerView callDetailsList;
    private PagingScrollView scrollView;

    private Integer startIndex;
    private Integer endIndex;
    private int bootCounter = 0;
    private int pageItemsCount = 25;

    private boolean shouldChangeTab;
    private boolean hasMoreElements;
    private boolean loadData;
    private boolean isGlobalListenerActive = false;

    private List<CallDetailsRow> callDetailsRows;
    private List<CallDetailsRow> callDetailLoadedItems;

    private CallDetailsRecyclerViewAdapter callDetailsAdapter;

    private CallDetailsRequest callDetailsRequest;
    private CallDetailsRequest previviosCallDetailsRequest;

    private CallDetailsFilterModel callDetailsFilterModel;
    private CallDetailsActivity callDetailsActivity;

    @BindView(R.id.fragment_call_details_card_error)
    CardErrorLayout cardErrorLayout;

    private Handler mHandler;

    @OnClick(R.id.fragment_call_details_card_error)
    public void retry(CardErrorLayout view) {
        refreshWithBillDates();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callDetailsActivity = (CallDetailsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() with tag -" + getTag());

        v = (LinearLayout) inflater.inflate(R.layout.fragment_tab_layout, container, false);

        scrollView = getActivity().findViewById(R.id.scroll_view);
        ButterKnife.bind(this, v);
        mHandler = new ListHandler();
        callDetailsRows = new ArrayList<>();

        LinearLayout tabContainer = v.findViewById(R.id.fragment_tab_container);

        customWidgetLoadingLayout = new CustomWidgetLoadingLayout(getContext());
        customWidgetLoadingLayout.build(tabContainer, Color.RED, ViewGroupParamsEnum.card_params);

        errorCircle.setColorFilter(Color.parseColor("#999999"));

        callDetailsList.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        callDetailsList.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(callDetailsList.getContext(),
                linearLayoutManager.getOrientation());
        callDetailsList.addItemDecoration(dividerItemDecoration);

        addGlobalLayoutListener();

        return v;
    }

    private void addGlobalLayoutListener() {
        if (!isGlobalListenerActive) {
            v.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }
        isGlobalListenerActive = true;
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (getActivity() == null) {
                return;
            }
            Log.d(TAG, "onGlobalLayout: " + overflowElementsLayout.getHeight());
            if (overflowElementsLayout.getVisibility() == View.VISIBLE && overflowElementsLayout.getHeight() > 0) {
                resizeFragment(overflowElementsLayout.getHeight());
                v.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
                isGlobalListenerActive = false;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        v.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        isGlobalListenerActive = false;
        scrollView.setOnBottomReachedListener(null);
    }

    private void setupLabels() {
        ((VodafoneTextView) v.findViewById(R.id.no_results_heder_text)).setText(CallDetailsLabels.getCall_details_no_elements_header_text());
        ((VodafoneTextView) v.findViewById(R.id.no_results_content_text)).setText(CallDetailsLabels.getCall_details_no_elements_content_text());
        ((VodafoneTextView) v.findViewById(R.id.overflow_elements_header_text)).setText(CallDetailsLabels.getCall_details_overflow_elements_header_text());
        ((VodafoneTextView) v.findViewById(R.id.overflow_elements_content_text)).setText(CallDetailsLabels.getCall_details_overflow_elements_content_text());
        cardErrorLayout.setText(CallDetailsLabels.getCall_details_system_error_text_system());
    }

    public void refresh() {
        returnFragmentSize();
        addGlobalLayoutListener();

        if(callDetailsActivity.isBillClosedDatesFailed()) {
            hideCustomWidgetLoadingLayout();
            setVisibleErrorContainer();
            return;
        }

        if (setupData()) {
            Log.d(TAG, "setupData returns true");
            getCallDetails();
        } else {
            Log.d(TAG, "setupData returns false");
            setVisibleErrorContainer();
        }
    }

    public void getCallDetailsData() {
        returnFragmentSize();
        addGlobalLayoutListener();

        if (setupData()) {
            Log.d(TAG, "setupData returns true");
            getCallDetails();
        } else {
            Log.d(TAG, "setupData returns false");
            setVisibleErrorContainer();
        }
    }

    public void refreshWithBillDates() {

        if(!(VodafoneController.getInstance().getUser() instanceof PrepaidUser)) {
            if(callDetailsActivity.isBillClosedDatesFailed()) {
                if(callDetailsActivity.getReportType().equalsIgnoreCase(REPORT_TYPE_BILLED)) {
                    getBillingDate(3);
                } else {
                    getBillingDate(1);
                }
            } else {
                getCallDetailsData();
            }
        } else {
            getCallDetailsData();
        }
    }

    public void getBillingDate(int range) {
        if(callDetailsActivity == null || getContext() == null) {
            return;
        }
        hideAllContainers();
        customWidgetLoadingLayout.show();

        UserDataService userDataService = new UserDataService(getContext());

        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        String cid = userProfile.getCid();
        String vfOdsCid = null;
        String vfCRMRole = null;

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            vfOdsCid = entityChildItem.getVfOdsCid();
            vfCRMRole = entityChildItem.getCrmRole();
        }

        userDataService.getBillDates(selectedBan, cid, vfOdsCid, range, vfCRMRole).subscribe(new RequestSessionObserver<GeneralResponse<BillsDatesSuccess>>() {
            @Override
            public void onNext(GeneralResponse<BillsDatesSuccess> billsDatesSuccessGeneralResponse) {
                stopLoadingDialog();

                if (billsDatesSuccessGeneralResponse.getTransactionStatus() == 0) {
                    callDetailsActivity.saveBillsDatesToRealm(billsDatesSuccessGeneralResponse.getTransactionSuccess());
                    if (billsDatesSuccessGeneralResponse.getTransactionSuccess().getBillClosedDatesList() != null) {
                        callDetailsActivity.setbillsClosedDateList();
                        callDetailsActivity.setLastBillClosedDate();
                    }
                    callDetailsActivity.setBillClosedDatesFailed(false);
                    getCallDetailsData();
                } else {
                    callDetailsActivity.clearBillClosedDate();
                    callDetailsActivity.setBillClosedDatesFailed(true);

                    hideCustomWidgetLoadingLayout();
                    setVisibleErrorContainer();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                callDetailsActivity.setBillClosedDatesFailed(true);
                hideCustomWidgetLoadingLayout();
                setVisibleErrorContainer();
            }
        });
    }

    private void getCallDetails() {

        loadData = true;
        final Category category = ((CallDetailsActivity) getActivity()).getSelectedFilter();

        hideAllContainers();
        customWidgetLoadingLayout.show();

        if (isAnotherrequest()) {
            Log.d(TAG, "resets indexes because it's a new call");
            previviosCallDetailsRequest = callDetailsRequest;
            resetIndex();
        }

        setCurrentIndexesOnRequest();
        hideReportContainer();

        String vfOdsCid = null;
        String vfCRMRole = null;

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            vfOdsCid = entityChildItem.getVfOdsCid();
            vfCRMRole = entityChildItem.getCrmRole();
        }

        UserDataService userDataService = new UserDataService(getContext());
        userDataService.getCallDetails(callDetailsRequest, vfOdsCid, vfCRMRole).subscribe(new RequestSessionObserver<GeneralResponse<CallDetailsSuccess>>() {
            @Override
            public void onNext(GeneralResponse<CallDetailsSuccess> callDetailsSuccessGeneralResponse) {
                if (getActivity() == null) {
                    return;
                }
                if (((CallDetailsActivity) getActivity()).getSelectedFilter() != null
                        && ((CallDetailsActivity) getActivity()).getSelectedFilter().equals(category)) {
                    final CallDetailsSuccess callDetailsSuccess = callDetailsSuccessGeneralResponse.getTransactionSuccess();
                    hideCustomWidgetLoadingLayout();
                    if (callDetailsSuccess != null) {
                        //Display send report on mail
                        if (callDetailsSuccess.getLimitExceededInd() != null && callDetailsSuccess.getLimitExceededInd()) {
                            callDetailsList.setVisibility(View.GONE);
                            if (FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class) != null &&
                                    ((CallDetailsFragment) FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class)).getLastSelectedTabId().equals(callDetailsRequest.getCostIndicator()) &&
                                    ((CallDetailsActivity) getActivity()).getSelectedFilter().equals(category)) {
                                setVisibleReportContainer();
                            }
                        } else {

                            if (callDetailsSuccess.getCallDetailsList() != null &&
                                    !callDetailsSuccess.getCallDetailsList().isEmpty()) {
                                if (callDetailsRows.size() > 0) {
                                    Log.d(TAG, "Current List Size is: " + callDetailsRows.size());
                                    callDetailsRows.addAll(callDetailsSuccess.getCallDetailsList());
                                    Log.d(TAG, "Populated List Size is: " + callDetailsRows.size());
                                } else {
                                    Log.d(TAG, "First time population: ");
                                    callDetailsRows = callDetailsSuccess.getCallDetailsList();
                                }
                                Log.d(TAG, "SetupListView");
                                hideErrorContainer();
                                hideReportContainer();
                                setupListView();

                            } else {
                                if (callDetailsRows.isEmpty()) {
                                    Log.d(TAG, " response callDetailsRows.isEmpty() is true");
                                    setVisibleNoResultContainer();
                                    resetIndex();
                                }
                                if (callDetailsList.getChildCount() == 0) {
                                    Log.d(TAG, " response shouldChangeTab is true");
                                    callDetailsActivity.changeTabIfNoResult();
                                    resetIndex();
                                }

                                hasMoreElements = false;
                            }
                        }
                    } else {
                        resetIndex();
                        setVisibleErrorContainer();
                    }
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                loadData = false;

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                loadData = false;
                Log.d(TAG, "onError() getCallDetails");
                if (getActivity() == null) {
                    return;
                }
                hideCustomWidgetLoadingLayout();
                setVisibleErrorContainer();
            }
        });
    }

    private void hideCustomWidgetLoadingLayout() {
        if (getActivity() != null) {
			customWidgetLoadingLayout.hide();

			try {


				if (callDetailsFilterModel.getReportType() != null)
				{
					if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_BILLED))
					{
						Map<String, Object> tealiumMapView = new HashMap(6);
						tealiumMapView.put("screen_name", "current call details");
						tealiumMapView.put("journey_name", "call details");
						tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
						TealiumHelper.trackView("screen_name", tealiumMapView);
					}
					else if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_UNBILLED))
					{
						Map<String, Object> tealiumMapView = new HashMap(6);
						tealiumMapView.put("screen_name", "billed call details");
						tealiumMapView.put("journey_name", "call details");
						tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
						TealiumHelper.trackView("screen_name", tealiumMapView);
					} else if(callDetailsFilterModel.getReportType().equals(REPORT_TYPE_PREPAID))
					{
						Map<String, Object> tealiumMapView = new HashMap(6);
						tealiumMapView.put("screen_name", "prepaid call details");
						tealiumMapView.put("journey_name", "call details");
						tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
						TealiumHelper.trackView("screen_name", tealiumMapView);
					}
				}

//                    if (FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class) != null &&
//                            !((CallDetailsFragment) FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class)).getLastSelectedTabId().equals(callDetailsRequest.getCostIndicator())) {
//                    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    private boolean isAnotherrequest() {
        Log.d(TAG, "isAnotherrequest");
        return previviosCallDetailsRequest == null || previviosCallDetailsRequest != callDetailsRequest;
    }

    private void setCurrentIndexesOnRequest() {
        callDetailsRequest.setStartIndex(startIndex);
        callDetailsRequest.setEndIndex(endIndex);
    }

    private boolean setupData() {
        Log.d(TAG, "setupData");
        hasMoreElements = true;
        callDetailsFilterModel = callDetailsActivity.getCallDetailsFilterObject();

        initAdapter();
        buildCallDetailsRequest();
        resetIndex();

        return validateCallDetailsRequest();
    }

    private CallDetailsRequest buildCallDetailsRequest() {

        callDetailsRequest = new CallDetailsRequest();
        if (callDetailsFilterModel != null) {
            callDetailsRequest.setReportType(callDetailsFilterModel.getReportType());
            callDetailsRequest.setCostIndicator(this.getTag());

            if (callDetailsFilterModel.getCategory() != null)
                callDetailsRequest.setCategory(callDetailsFilterModel.getCategory().getName());
            callDetailsRequest.setStartDate(callDetailsFilterModel.getStartDate());
            callDetailsRequest.setEndDate(callDetailsFilterModel.getEndDate());

            callDetailsRequest.setNationalInd(callDetailsFilterModel.isNational());
            callDetailsRequest.setInternationaInd(callDetailsFilterModel.isInternational());
            callDetailsRequest.setRoamingInd(callDetailsFilterModel.isRoaming());
            callDetailsRequest.setBillClosedDate(callDetailsFilterModel.getLastBillClosedDate());

            if (callDetailsFilterModel.getReportType() != null && (callDetailsFilterModel.getReportType().equals(REPORT_TYPE_BILLED)
                    || callDetailsFilterModel.getReportType().equals(CallDetailsActivity.REPORT_TYPE_UNBILLED))) {
                callDetailsRequest.setSubscriberId(UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
                String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
                callDetailsRequest.setPhoneNumber(msisdn);
                callDetailsRequest.setBanId(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());

            } else {
                callDetailsRequest.setSubscriberId(formatSubscriberId(VodafoneController.getInstance().getUserProfile().getSid()));
                String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
                callDetailsRequest.setPhoneNumber(msisdn);
            }
        }
        return callDetailsRequest;
    }

    private boolean validateCallDetailsRequest() {
        boolean result;
        if (callDetailsFilterModel.getReportType() != null && callDetailsFilterModel.getReportType().equals(REPORT_TYPE_BILLED)) {
            result = !haveNullParameter(callDetailsRequest.getCostIndicator(), callDetailsRequest.getSubscriberId(), callDetailsRequest.getPhoneNumber(),
                    callDetailsRequest.getBanId(), callDetailsRequest.getBillClosedDate(), callDetailsRequest.getCategory());
        } else {
            result = !haveNullParameter(callDetailsRequest.getCostIndicator(), callDetailsRequest.getSubscriberId(), callDetailsRequest.getPhoneNumber(),
                    callDetailsRequest.getStartDate(), callDetailsRequest.getEndDate(), callDetailsRequest.getCategory());
        }
        return result;
    }

    private void resetIndex() {
        bootCounter = 0;
        startIndex = 1;
        endIndex = 100;
        callDetailsRows.clear();
    }

    private String formatSubscriberId(String subscriberId) {
        if (subscriberId != null && subscriberId.contains("-")) {
            subscriberId = subscriberId.replace("-", "");
        }
        return subscriberId;
    }

    public boolean haveNullParameter(Object... args) {
        for (Object value : args) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    public void setVisibleReportContainer() {
        hasMoreElements = false;

        scrollView.setOnBottomReachedListener(null);

        overflowElementsLayout.setVisibility(View.VISIBLE);
        if (FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class) != null) {
            ((CallDetailsFragment) FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class)).displaySendReportCard();
        }
    }

    private void resizeFragment(int fragmentHeight) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, fragmentHeight);
        v.setLayoutParams(params);
    }

    private void returnFragmentSize() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
    }

    public void hideReportContainer() {
        if (overflowElementsLayout.getVisibility() != View.GONE) {
            overflowElementsLayout.setVisibility(View.GONE);
        }
        if (FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class) != null) {
            ((CallDetailsFragment) FragmentUtils.getInstance(getActivity().getSupportFragmentManager(), CallDetailsFragment.class)).hideSendReportContainer();
        }
    }

    public void setVisibleErrorContainer() {
        hasMoreElements = false;
        callDetailsList.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.VISIBLE);
    }

    public void setVisibleNoResultContainer() {
        Log.d(TAG, "setVisibleNoResultContainer()");
        hasMoreElements = false;
        callDetailsList.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
        systemErrorLayout.setVisibility(View.GONE);
    }

    public void hideErrorContainer() {
        callDetailsList.setVisibility(View.VISIBLE);
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.GONE);
    }

    public void hideAllContainers() {
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.GONE);
    }

    public void setupListView() {
        Log.d(TAG, "setupListView()");
        callDetailLoadedItems = loadListData();
        Log.d(TAG, "callDetailsItems size: " + callDetailLoadedItems.size());
        addItemsToListView(callDetailLoadedItems);

        scrollView.setOnBottomReachedListener(
                new PagingScrollView.OnBottomReachedListener() {
                    @Override
                    public void onBottomReached() {
                        Log.d(TAG, "Got to the end of Scroll with: " + bootCounter + "    hasMoreElements: " + hasMoreElements);
                        if (hasMoreElements && !loadData) {
                            if (bootCounter >= callDetailsRows.size()) {
                                Log.d(TAG, "rows size: " + callDetailsRows.size());
                                Log.d(TAG, "bootCounter: " + bootCounter + "  >=  endIndex: " + endIndex);
                                startIndex = endIndex + 1;
                                endIndex += endIndex;

                                getCallDetails();

                            } else {
                                hideAllContainers();
                                customWidgetLoadingLayout.show();


                                Message message = mHandler.obtainMessage(0, loadListData());
                                mHandler.sendMessageDelayed(message, 3000);
                            }
                        } else {
                            Log.d(TAG, "Has no more elements or load data");
                        }
                    }
                }
        );
    }

    private class ListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Update date adapter and UI
                    callDetailsAdapter.addListItemToAdapterWhenHasMore((ArrayList<CallDetailsRow>) msg.obj);
                    //setListHeight(callDetailsList, callDetailsAdapter);
                    customWidgetLoadingLayout.hide();
                    loadData = false;
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callDetailsActivity.setCostIndicator(this.getTag());
        setupLabels();
        if(callDetailsActivity.isBillClosedDatesFailed()) {
            refreshWithBillDates();
        } else {
            getCallDetailsData();
        }
    }

    public void initAdapter() {
        callDetailsAdapter = new CallDetailsRecyclerViewAdapter(getContext(), new ArrayList<CallDetailsRow>(), callDetailsFilterModel.getCategory());
        callDetailsList.postDelayed(new Runnable() {
            @Override
            public void run() {
                callDetailsList.setAdapter(callDetailsAdapter);
            }
        }, 500);
    }

    public void addItemsToListView(List<CallDetailsRow> callDetailsItems) {
        callDetailsAdapter.addListItemToAdapter(callDetailsItems);
        //  setListHeight(callDetailsList, callDetailsAdapter);
    }

    public List<CallDetailsRow> loadListData() {
        loadData = true;
        Log.d(TAG, "loadListData()  - counter: " + bootCounter);
        List<CallDetailsRow> resultList = new ArrayList<>();
        if (bootCounter + pageItemsCount > callDetailsRows.size()) {
            for (int i = bootCounter; i < callDetailsRows.size(); i++) {
                resultList.add(callDetailsRows.get(i));
            }
        } else {
            for (int i = bootCounter; i < bootCounter + pageItemsCount; i++) {
                resultList.add(callDetailsRows.get(i));
            }
        }
        bootCounter += pageItemsCount;
        Log.d(TAG, "page of callDetailsRows: " + callDetailsRows.size() + "   result list: " + resultList.size());
        return resultList;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        scrollView.setOnBottomReachedListener(null);
    }
}
