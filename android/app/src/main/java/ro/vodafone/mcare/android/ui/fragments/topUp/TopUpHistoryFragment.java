package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistoryMonthlyGroup;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistorySuccess;
import ro.vodafone.mcare.android.client.model.topUp.history.TopUpHistoryAdpter;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.SmallErrorView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.charts.GraphHistory;
import ro.vodafone.mcare.android.widget.charts.HistoryModel;
import ro.vodafone.mcare.android.widget.charts.RechargeHistoryMonthlyGroupToModelTranslater;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpHistoryFragment extends BaseFragment{
    public static final String TAG = "TopUpHistoryFragment";

    View v;

    @BindView(R.id.general_content_container)
    LinearLayout viewGroup;

    @BindView(R.id.no_history_results_content_text)
    VodafoneTextView noHistoryResultsContentText;

    @BindView(R.id.no_history_results_layout)
    LinearLayout noHistoryResultsLayout;

    @BindView(R.id.history_system_error_layout)
    CardErrorLayout historySystemErrorLayout;

    @BindView(R.id.top_up_history_list)
    ExpandableAdapterBackedLinearLayout topUpHistoryList;

    @BindView(R.id.instructional_message_container)
    LinearLayout instructionalMessageContainer;

    GraphHistory graphHistory;


    TopUpHistoryAdpter topUpHistoryAdpter;
    //List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryList;
   // RechargeHistorySuccess rechargeHistorySuccess;
    List<RechargeHistoryMonthlyGroup> refactoredList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");
        v = inflater.inflate(R.layout.fragment_top_up_history, container, false);

        if(Build.VERSION.SDK_INT >= 21){
            ViewUtils.changeVisibilityForTransitions(v, VodafoneController.currentActivity().getWindow());
        }
        showLoadingDialog();
        ButterKnife.bind(this,v);
        setupLabels();

        //loadHistoryTransactions();
        User user =  VodafoneController.getInstance().getUser();

        if(user instanceof PrepaidUser){
            Log.d(TAG,"PrepaidUser user");

            //@Serban Radulescu: DO NOT REMOVE
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            getHistoryRechargesPrePaid();
                        }
                    });



            TopUpTransactionHistoryPrePaidTrackingEvent event = new TopUpTransactionHistoryPrePaidTrackingEvent();
            VodafoneController.getInstance().getTrackingService().track(event);

        }else {
            Log.d(TAG,"PostPaid user");
            final String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
            Log.d(TAG,"Selected ban: " + ban);

            //@Serban Radulescu: DO NOT REMOVE
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            getHistoryRechargesPostPaid(ban);
                        }
                    });
            TopUpHistoryPostPaidTrackingEvent event = new TopUpHistoryPostPaidTrackingEvent();
            VodafoneController.getInstance().getTrackingService().track(event);
        }
        initHeader();
        initTealium();

        return  v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(TopUpHistoryFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpHistoryScreenName);
    }

    private void initTealium(){
        TopUpActivity.TopUpTrackingEvent event = new TopUpActivity.TopUpTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.prop21 = "mcare:" + "top up";
        journey.getContextData().put("prop21", journey.prop21);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void initHeader(){
        ((TopUpActivity)getActivity()).getNavigationHeader().hideSelectorViewWithoutTriangle();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        graphHistory = (GraphHistory) inflater.inflate(R.layout.history_chart, null);
        User user=VodafoneController.getInstance().getUser();
        if( user instanceof PostPaidUser)
        {
            ((TopUpActivity)getActivity()).getNavigationHeader().setTitle(getString(R.string.recharge_history_postpaid));
        }
        else
        {
            ((TopUpActivity)getActivity()).getNavigationHeader().setTitle(getString(R.string.recharge_history_prepaid));
        }
        ((TopUpActivity)getActivity()).getNavigationHeader().addExtraView(graphHistory);
        graphHistory.drawDefaultChart();


    }

  /*  public void loadHistoryTransactions(){
        Log.d(TAG,"loadHistoryTransactions()");
        rechargeHistorySuccess = new RechargeHistorySuccess();
        //RechargeHistoryMonthlyGroup List
        monthlyRechargeHistoryList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            Log.d(TAG,"Iteration: " + i);
            RechargeHistoryMonthlyGroup rechargeHistoryMonthlyGroup = new RechargeHistoryMonthlyGroup();
            rechargeHistoryMonthlyGroup.setAverageSpent(20);
            rechargeHistoryMonthlyGroup.setMonth(i+1);
                List<RechargeHistoryRow> rechargeHistoryRowList = new ArrayList<>();
                for(int j=0; j< 7; j ++){
                    RechargeHistoryRow rechargeHistoryRow = new RechargeHistoryRow();
                    rechargeHistoryRow.setDate(1280178000000l);
                    rechargeHistoryRow.setActionTypeId(j);
                    rechargeHistoryRow.setAmount(j+5);
                    rechargeHistoryRow.setPrepaidMsisdn("795656556" + j);
                    rechargeHistoryRow.setChannelTypeId(2);  // make enum Channel
                    rechargeHistoryRowList.add(rechargeHistoryRow);
                }
            rechargeHistoryMonthlyGroup.setRechargeHistoryRow(rechargeHistoryRowList);
            monthlyRechargeHistoryList.add(rechargeHistoryMonthlyGroup);
        }

        rechargeHistorySuccess.setMonthlyRechargeHistoryList(monthlyRechargeHistoryList);
        initAdapter(monthlyRechargeHistoryList);

    }*/

    public void initAdapter (List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryList){
        refactoredList = new ArrayList<>();
        Log.d(TAG,"initAdapter()");
        Log.d(TAG,"monthlyRechargeHistoryList: " + monthlyRechargeHistoryList.size());

        //remove empty RechargeHistoryRow lists from group to set adapter on necessar
        for(int i = 0; i<monthlyRechargeHistoryList.size(); i++){
            Log.d(TAG, "initAdapter: iteration " + i);
            Log.d(TAG, "initAdapter: " + monthlyRechargeHistoryList.get(i).getRechargeHistoryRow());
            if(!monthlyRechargeHistoryList.get(i).getRechargeHistoryRow().isEmpty()){
                refactoredList.add(monthlyRechargeHistoryList.get(i));
            }
        }

        topUpHistoryAdpter = new TopUpHistoryAdpter(getContext(),refactoredList);

        if(Build.VERSION.SDK_INT >= 21){
            ViewUtils.changeVisibilityForTransitions(topUpHistoryList, getActivity().getWindow());
        }

        //FIXME: this is a really ugly hack to prevent the app from crashing
        //it might be removed, if possible, when removing the scrollview at the root
        //of activities and replacing this "list" with an actual list/recycler view
        //unless the issue comes with the parent views having a background while being large themselves

        //suspected is the following:
        // - view is handled by a transition and its contents are drawn and cached to native memory (or possibly GPU)
        // - since the view is larger than what the OS will cache a native exception is thrown
        // - possible solution for this is to only keep only visible small views in memory
        // - this solution could not work if the views causing the issue are the large containers that
        //   have a background
        // - this solution could not work (or it might require extensive development) if
        //   the child views can't be discarded if offscreen (such as in listviews)
        ((ViewGroup)topUpHistoryList.getParent()).setVisibility(View.GONE);
        topUpHistoryList.setVisibility(View.GONE);
        //((ViewGroup)topUpHistoryList.getParent()).setVisibility(View.GONE);
        topUpHistoryList.postDelayed(new Runnable() {
            @Override
            public void run() {
                topUpHistoryList.setAdapter(topUpHistoryAdpter);
                for(int i=0; i< refactoredList.size();i++){
                    topUpHistoryList.expandGroup(i);
                }
                ((ViewGroup)topUpHistoryList.getParent()).setVisibility(View.VISIBLE);
                topUpHistoryList.setVisibility(View.VISIBLE);
            }
        }, 500);

        topUpHistoryList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    topUpHistoryList.expandGroup(groupPosition);
                return true;
            }
        });

        setInstructionalMessage();
    }

    private void setInstructionalMessage(){
        instructionalMessageContainer.setVisibility(View.VISIBLE);
        SmallErrorView instructionalText = new SmallErrorView(getContext(), TopUPLabels.getTop_up_history_instructional_text(), null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        instructionalText.addSmallErrorView(instructionalMessageContainer, params);
    }


    private void setupLabels(){
        ((VodafoneTextView)v.findViewById(R.id.no_history_results_content_text)).setText(TopUPLabels.getTop_up_history_no_results());
    }


    public void getHistoryRechargesPostPaid(String ban){
        Log.d(TAG,"getHistoryRechargesPostPaid");

        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.getHistoryRechargesPostPaid(ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<RechargeHistorySuccess>>() {
            @Override
            public void onNext(GeneralResponse<RechargeHistorySuccess> historyRechargesSuccessGeneralResponse) {
                super.onNext(historyRechargesSuccessGeneralResponse);
                Log.d(TAG,"getHistoryRechargesPostPaid - onNext()");
                stopLoadingDialog();

                if(historyRechargesSuccessGeneralResponse.getTransactionSuccess() != null) {
                    Log.d(TAG,"getHistoryRechargesPostPaid  - Transaction Success");
                    if (historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList()!=null &&
                            historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList().size() > 0) {

                        Log.d(TAG,"getHistoryRechargesPostPaid  - Transaction Success List size: " + historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList().size() );
                        initAdapter(historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList());
                        setupBarChart(historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList());
                    }else{
                        noHistoryResultsLayout.setVisibility(View.VISIBLE);
                        noHistoryResultsContentText.setText(TopUPLabels.getTop_up_history_no_results());
                    }
                    viewGroup.setVisibility(View.VISIBLE);
                }else{
                    if(historyRechargesSuccessGeneralResponse.getTransactionStatus()==0){
                        noHistoryResultsLayout.setVisibility(View.VISIBLE);
                        noHistoryResultsContentText.setText(TopUPLabels.getTop_up_history_no_results());
                        viewGroup.setVisibility(View.VISIBLE);
                    }else{
                        showErrorMessage();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                showErrorMessage();
            }

            @Override
            public void onCompleted() {}
        });
    }

    private void setupBarChart(List<RechargeHistoryMonthlyGroup>  list){
        List<HistoryModel> historyModels =
                new RechargeHistoryMonthlyGroupToModelTranslater(list).parse(6);
        graphHistory.setupChart(historyModels, refactoredList);
        graphHistory.startAnimation();
    }

    public void getHistoryRechargesPrePaid(){
        Log.d(TAG,"getHistoryRechargesPrePaid");
        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.getHistoryRechargesPrePaid().subscribe(new RequestSaveRealmObserver<GeneralResponse<RechargeHistorySuccess>>() {
            @Override
            public void onNext(GeneralResponse<RechargeHistorySuccess> historyRechargesSuccessGeneralResponse) {
                super.onNext(historyRechargesSuccessGeneralResponse);
                Log.d(TAG,"getHistoryRechargesPrePaid - onNext()");
                stopLoadingDialog();
                if(historyRechargesSuccessGeneralResponse.getTransactionSuccess()!=null &&
                        historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList() != null) {
                    Log.d(TAG,"getHistoryRechargesPrePaid  - Transaction Success");
                    if (historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList().size() > 0) {

                        Log.d(TAG,"getHistoryRechargesPrePaid  - Transaction Success List size: " + historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList().size() );
                        initAdapter(historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList());
                        setupBarChart(historyRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRechargeHistoryList());
                        viewGroup.setVisibility(View.VISIBLE);
                    }else{
                        noHistoryResultsLayout.setVisibility(View.VISIBLE);
                        noHistoryResultsContentText.setText(TopUPLabels.getTop_up_history_no_results());
                        viewGroup.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(historyRechargesSuccessGeneralResponse.getTransactionStatus()==0){
                        noHistoryResultsLayout.setVisibility(View.VISIBLE);
                        noHistoryResultsContentText.setText(TopUPLabels.getTop_up_history_no_results());
                        viewGroup.setVisibility(View.VISIBLE);
                    }else{
                        showErrorMessage();
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                showErrorMessage();
            }

            @Override
            public void onCompleted() {}
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((TopUpActivity)getActivity()).getNavigationHeader().removeExtraView();
        ((TopUpActivity)getActivity()).getNavigationHeader().displaySelectorView();
    }

    private void showErrorMessage(){
        Log.d(TAG, "showErrorMessage: ");
        //((VodafoneTextView) v.findViewById(R.id.history_system_error_text)).setText(TopUPLabels.getTop_up_api_call_fail());
        viewGroup.setVisibility(View.VISIBLE);
        historySystemErrorLayout.setVisibility(View.VISIBLE);
        historySystemErrorLayout.setText(AppLabels.getGenericRetryErrorMessage());
        historySystemErrorLayout.setOnClickListener(errorClickListener);
    }

    View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((TopUpActivity)getActivity()).getNavigationHeader().removeExtraView();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(FragmentUtils.getVisibleFragment((TopUpActivity) getActivity(), false));
            ft.attach(FragmentUtils.getVisibleFragment((TopUpActivity) getActivity(), false));
            ft.commit();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.TOP_UP_HISTORY);
    }

    public static class TopUpHistoryPostPaidTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "top up history";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"top up history");


            s.channel = "top up";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class TopUpTransactionHistoryPrePaidTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "transaction history";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"transaction history");


            s.channel = "top up";
            s.getContextData().put("&&channel", s.channel);
        }
    }




}
