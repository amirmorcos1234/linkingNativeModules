package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.billRecharges.MonthlyRecharge;
import ro.vodafone.mcare.android.client.model.billRecharges.OneTimeRecharge;
import ro.vodafone.mcare.android.client.model.billRecharges.WeeklyRecharge;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.ListViewSwipeAdapter;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

public class TopUpRecurrentRechargesFragment extends OffersFragment implements VodafoneSpinner.Callback{
    public static final String TAG = "TopUpRecurrentFragment";

    public static final String ALL_RECHARGES_LABEL = "Toate reîncărcările";
    public static final String WEEKLY_RECHARGES_LABEL = "Reîncărcări săptămânale";
    public static final String ON_TIME_RECHARGES_LABEL = "Reîncărcări la data";
    public static final String MONTHLY_RECHARGES_LABEL = "Reîncărcări lunare";
    private ListView mListView;
    private ListView dateListView;
    private ListView weekListView;
    private VodafoneSpinner spinner;
    private LinearLayout monthLayout;
    private LinearLayout weekLayout;
    private LinearLayout dateLayout;

    VodafoneTextView noResultsContentText;
    LinearLayout noResultsLayout;
    LinearLayout systemErrorLayout;
    VodafoneTextView systemErrorText;


    List<MonthlyRecharge> monthlyRecharges;
    List<WeeklyRecharge> weeklyRecharges;
    List<OneTimeRecharge> dateRecharges;

    String banID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_top_up_reccurent_recharges, container, false);

        ((TopUpActivity)getActivity()).getNavigationHeader().setTitle(getTitle());
        ((TopUpActivity)getActivity()).getNavigationHeader().hideSelectorView();

        showLoadingDialog();

        monthLayout = (LinearLayout) view.findViewById(R.id.month_recurrent_layout);
        weekLayout = (LinearLayout) view.findViewById(R.id.week_recurrent_layout);
        dateLayout = (LinearLayout) view.findViewById(R.id.date_recurrent_layout);

        noResultsLayout = (LinearLayout) view.findViewById(R.id.recurrent_no_results_layout);
        noResultsContentText = (VodafoneTextView) view.findViewById(R.id.no_results_content_text);
        systemErrorLayout = (LinearLayout) view.findViewById(R.id.recurrent_system_error_layout);
        systemErrorText = (VodafoneTextView) view.findViewById(R.id.history_system_error_text);


        weekListView = (ListView) view.findViewById(R.id.week_recurrent_list);
        mListView = (ListView) view.findViewById(R.id.month_recurrent_list);
        dateListView = (ListView) view.findViewById(R.id.date_recurrent_list);

        spinner = (VodafoneSpinner) view.findViewById(R.id.spinner);
        spinner.setCallback(this);
        final List<String> dataset = new LinkedList<>(Arrays.asList(ALL_RECHARGES_LABEL, WEEKLY_RECHARGES_LABEL, MONTHLY_RECHARGES_LABEL,ON_TIME_RECHARGES_LABEL));

        VodafoneSpinnerAdapter adapter = new VodafoneSpinnerAdapter(getContext(), dataset, R.drawable.selector);
        spinner.setAdapter(adapter);

        getBillingRecharges(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());

        TopUpRecurrentTrackingEvent event = new TopUpRecurrentTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(TopUpRecurrentRechargesFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpRecurrentRechargesScreenName);
    }

    /* public void getRecurrentRecharges() {
        Log.d(TAG, "getRecurrentRecharges");

        weeklyRecharges = new ArrayList<WeeklyRecharge>();
        monthlyRecharges = new ArrayList<MonthlyRecharge>();
        dateRecharges = new ArrayList<OneTimeRecharge>();
        Log.d(TAG, "getRecurrentRecharges: " + RealmManager.getRealmObject(BillRechargesSuccess.class));
        if (RealmManager.getRealmObject(BillRechargesSuccess.class) != null) {
            weeklyRecharges.addAll(((BillRechargesSuccess) RealmManager.getRealmObject(BillRechargesSuccess.class)).getWeeklyRecharges());
            monthlyRecharges.addAll(((BillRechargesSuccess) RealmManager.getRealmObject(BillRechargesSuccess.class)).getMonthlyRecharges());
            dateRecharges.addAll(((BillRechargesSuccess) RealmManager.getRealmObject(BillRechargesSuccess.class)).getOneTimeRecharges());

            initListView();
        } else {
            initErrorLayout();
        }
    }*/

    public void getBillingRecharges(String ban){
        Log.d(TAG, "getBillingRecharges: ban " + ban);
        if(ban!=null){
            Log.d(TAG, "getBillingRecharges service");
            RechargeService rechargeService = new RechargeService(getContext());
            rechargeService.getBillRecharges(ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<BillRechargesSuccess>>() {
                @Override
                public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                    super.onNext(billRechargesSuccessGeneralResponse);
                    if(billRechargesSuccessGeneralResponse.getTransactionStatus() == 0){
                        Log.d(TAG, "onNext: ");
                        weeklyRecharges = new ArrayList<WeeklyRecharge>();
                        monthlyRecharges = new ArrayList<MonthlyRecharge>();
                        dateRecharges = new ArrayList<OneTimeRecharge>();

                        weeklyRecharges = billRechargesSuccessGeneralResponse.getTransactionSuccess().getWeeklyRecharges();
                        monthlyRecharges = billRechargesSuccessGeneralResponse.getTransactionSuccess().getMonthlyRecharges();
                        dateRecharges = billRechargesSuccessGeneralResponse.getTransactionSuccess().getOneTimeRecharges();
                        Log.d(TAG, "onNext:  " + weeklyRecharges);
                        Log.d(TAG, "onNext: " + dateRecharges);

                        initListView();
                        Log.d(TAG, "next service");
                    } else {
                        initErrorLayout();
                        stopLoadingDialog();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    initErrorLayout();
                    Log.d(TAG, "error service");
                    super.onError(e);
                }

                @Override
                public void onCompleted() {
                    stopLoadingDialog();
                    Log.d(TAG, "completed service");
                }
            });
        } else{
            stopLoadingDialog();
            initErrorLayout();
        }
    }

    public void initErrorLayout(){
        Log.d(TAG, "initErrorLayout: ");
        weekLayout.setVisibility(View.GONE);
        monthLayout.setVisibility(View.GONE);
        dateLayout.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.VISIBLE);
        systemErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBillingRecharges(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
            }
        });
    }

    public void initListView() {
        Log.d(TAG, "initListView: ");
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.GONE);
        if (weeklyRecharges != null && weeklyRecharges.size()!=0) {
            Log.d(TAG, "weeklyRecharges size : " + weeklyRecharges.size());
            ListViewSwipeAdapter weekSwipeListAdapter = new ListViewSwipeAdapter(getContext(), weeklyRecharges, ListViewSwipeAdapter.WEEKLY_RECHARGE, weekListView, weekLayout, this);
            weekListView.setAdapter(weekSwipeListAdapter);
            weekSwipeListAdapter.notifyDataSetChanged();
            weekLayout.setVisibility(View.VISIBLE);

            ScreenMeasure.setListHeight(weekListView, weekSwipeListAdapter);
        }

        if (monthlyRecharges != null && monthlyRecharges.size()!=0) {
            Log.d(TAG, "monthlyRecharges size : " + monthlyRecharges.size());
            ListViewSwipeAdapter monthSwipeListAdapter = new ListViewSwipeAdapter(getContext(), monthlyRecharges, ListViewSwipeAdapter.MONTHLY_RECHARGE, mListView, monthLayout, this);
            mListView.setAdapter(monthSwipeListAdapter);
            monthSwipeListAdapter.notifyDataSetChanged();
            monthLayout.setVisibility(View.VISIBLE);
            ScreenMeasure.setListHeight(mListView, monthSwipeListAdapter);
        }

        if (dateRecharges != null && dateRecharges.size()!=0) {
            Log.d(TAG, "dateRecharges size : " + dateRecharges.size());
            ListViewSwipeAdapter dateSwipeListAdapter = new ListViewSwipeAdapter(getContext(), dateRecharges, ListViewSwipeAdapter.ONDATE_RECHARGE, dateListView, dateLayout, this);
            dateListView.setAdapter(dateSwipeListAdapter);
            dateSwipeListAdapter.notifyDataSetChanged();
            dateLayout.setVisibility(View.VISIBLE);
            ScreenMeasure.setListHeight(dateListView, dateSwipeListAdapter);
        }
        if(dateRecharges !=null && weeklyRecharges !=null && monthlyRecharges !=null) {
            Log.d(TAG, "null lists");
            if (dateRecharges.size() == 0 && monthlyRecharges.size() == 0 && weeklyRecharges.size() == 0) {
                weekLayout.setVisibility(View.GONE);
                monthLayout.setVisibility(View.GONE);
                dateLayout.setVisibility(View.GONE);
                systemErrorLayout.setVisibility(View.GONE);

                noResultsLayout.setVisibility(View.VISIBLE);
            }
        }
        else{
            weekLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.GONE);
            dateLayout.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.GONE);

            initErrorLayout();
        }
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {

        Log.d(TAG, "selectFragment - hide all Layouts and show only necessary ones");
        weekLayout.setVisibility(View.GONE);
        monthLayout.setVisibility(View.GONE);
        dateLayout.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.GONE);
        systemErrorLayout.setVisibility(View.GONE);

       if(RealmManager.getRealmObject(BillRechargesSuccess.class) != null){
           if(selectedValue instanceof String) {

               String selectedString = (String) selectedValue;
               if (selectedString.equals(WEEKLY_RECHARGES_LABEL)) {
                   spinner.setText(WEEKLY_RECHARGES_LABEL);
                   if (weeklyRecharges != null && !weeklyRecharges.isEmpty()) {
                       weekLayout.setVisibility(View.VISIBLE);
                   } else  {
                       noResultsLayout.setVisibility(View.VISIBLE);
                   }

               }
               else if (selectedString.equals(MONTHLY_RECHARGES_LABEL)) {
                   spinner.setText(MONTHLY_RECHARGES_LABEL);
                   if (monthlyRecharges!=null && !monthlyRecharges.isEmpty()) {
                       monthLayout.setVisibility(View.VISIBLE);
                   } else {
                       noResultsLayout.setVisibility(View.VISIBLE);
                   }
               } else if (selectedString.equals(ON_TIME_RECHARGES_LABEL)) {
                   spinner.setText(ON_TIME_RECHARGES_LABEL);
                   if ( dateRecharges != null && !dateRecharges.isEmpty()){
                       dateLayout.setVisibility(View.VISIBLE);
                   } else{
                       noResultsLayout.setVisibility(View.VISIBLE);
                   }
               } else if (selectedString.equals(ALL_RECHARGES_LABEL)) {
                   if (weeklyRecharges != null && monthlyRecharges != null && dateRecharges != null) {
                       if (!weeklyRecharges.isEmpty()) {
                           weekLayout.setVisibility(View.VISIBLE);
                       }

                       if (!monthlyRecharges.isEmpty()) {
                           monthLayout.setVisibility(View.VISIBLE);
                       }

                       if (!dateRecharges.isEmpty()) {
                           dateLayout.setVisibility(View.VISIBLE);
                       }
                   } else {
                       noResultsLayout.setVisibility(View.VISIBLE);
                   }
                   if (weeklyRecharges != null && monthlyRecharges != null && dateRecharges != null) {
                       if (weeklyRecharges.isEmpty() && monthlyRecharges.isEmpty() && dateRecharges.isEmpty()) {
                           weekLayout.setVisibility(View.GONE);
                           monthLayout.setVisibility(View.GONE);
                           dateLayout.setVisibility(View.GONE);
                           noResultsLayout.setVisibility(View.VISIBLE);
                       }
                   } else{initErrorLayout();}
               }
           }
       } else {
           initErrorLayout();
       }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data ==null){return;}
        Ban selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedBan();
        Log.d(TAG, "Ban Selector number: " + selectedBan.getNumber());
        if(selectedBan.getNumber()!=null)
            getBillingRecharges(selectedBan.getNumber());


    }

    @Override
    public String getTitle() {
        return "Reîncărcări Recurente";
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.TOP_UP_RECURRENT);
    }

    public static class TopUpRecurrentTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "recurrent top-ups";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"recurrent top-ups");


            s.channel = "top up";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "recurrent top-ups";
            s.getContextData().put("prop21", s.prop21);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TopUpActivity) getActivity()).getNavigationHeader().setTitle(getTitle());
        ((TopUpActivity) getActivity()).getNavigationHeader().hideSelectorView();
    }

    public void checkIfListsAreEmpty(){
        if (dateRecharges.size() == 0 && monthlyRecharges.size() == 0 && weeklyRecharges.size() == 0) {
            weekLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.GONE);
            dateLayout.setVisibility(View.GONE);
            systemErrorLayout.setVisibility(View.GONE);

            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }

}
