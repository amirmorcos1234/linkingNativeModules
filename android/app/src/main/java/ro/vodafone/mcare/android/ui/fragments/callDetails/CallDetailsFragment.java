package ro.vodafone.mcare.android.ui.fragments.callDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.CallDetailsReportRequest;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.CallDetailsActivity;
import ro.vodafone.mcare.android.ui.activities.callDetailsFilter.CallDetailsFilter;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.callDetailsFilter.CallDetailsFilter.FILTER_OBJECT;

/**
 * Created by Bivol Pavel on 13.02.2017.
 */
public class CallDetailsFragment extends BaseFragment implements InputEventsListenerInterface {

    public static String TAG = "CallDetailsFragment";
    public static String REPORT_TYPE_KEY = "reportType";
    public static int FILTER_REQUEST_KEY = 5;
    public static String FILTER_TYPE_KEY = "filterType";
    public static final int BILLED_TAB = 0;
    public static final int UNBILLED_TAB = 1;

    private String format = "CSV";

    private Context mContext;
    private CallDetailsActivity callDetailsActivity;

    private View view;
    @BindView(R.id.filter_button)
    public VodafoneButton filterButton;
    @BindView(R.id.call_details_send_report_container)
    public VodafoneButton sendReportButton;
    @BindView(R.id.call_details_content)
    public VodafoneGenericCard callDetailsContent;

    @BindView(R.id.no_bill_issued_container)
    public VodafoneGenericCard noBillIssuedContainer;
    @BindView(R.id.send_report_card)
    public VodafoneGenericCard sendReportCard;

    @BindView(R.id.select_report_type_radiogroup_container)
    public RadioGroup radioGroup;
    @BindView(R.id.report_html_radio_button)
    public RadioButton reportHtmlRadioButton;
    @BindView(R.id.report_csv_radio_button)
    public RadioButton reportCSVRadioButton;
    @BindView(R.id.date_filter_button)
    public LinearLayout dataFilterButton;
    @BindView(R.id.calls_filter_button)
    public LinearLayout callFilterButton;
    @BindView(R.id.sms_filter_button)
    public LinearLayout smsFilterButton;
    @BindView(R.id.others_filter_button)
    public LinearLayout othersFilterButton;
    @BindView(R.id.invalid_email_layout)
    public LinearLayout invalidEmailLayout;

    @BindView(R.id.tab_host_container)
    public LinearLayout tabHostContainer;
    @BindView(R.id.send_report_tittle_layout)
    public LinearLayout sendReportTittleLayout;

    @BindView(R.id.send_report_email_input)
    public CustomEditText emailAddressInput;
    @BindView(R.id.email_error_message_text)
    public TextView emailErrorMessageText;



    public CallDetailsTabHost mTabHost;

    private List<LinearLayout> buttonsList;
    private NavigationHeader navigationHeader;

    private String lastSelectedTabId;

    @OnClick({R.id.date_filter_button,R.id.calls_filter_button,R.id.sms_filter_button,R.id.others_filter_button,})
    public void filterCategory(View view){
        callDetailsActivity.setCostIndicator(null);
        switch (view.getId()) {
            case R.id.date_filter_button:
                //Tealium Track Event
                Map<String, Object> tealiumMapEventDate = new HashMap(6);
                tealiumMapEventDate.put("screen_name", "current call details");
                tealiumMapEventDate.put("event_name", "mcare:current call details: button: date");
                tealiumMapEventDate.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEventDate);

                selectFilter(Category.DATE);
                break;
            case R.id.calls_filter_button:
                //Tealium Track Event
                Map<String, Object> tealiumMapEventDateVoice = new HashMap(6);
                tealiumMapEventDateVoice.put("screen_name", "current call details");
                tealiumMapEventDateVoice.put("event_name", "mcare:current call details: button: apeluri");
                tealiumMapEventDateVoice.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEventDateVoice);

                selectFilter(Category.VOCE);
                break;
            case R.id.sms_filter_button:
                //Tealium Track Event
                Map<String, Object> tealiumMapEventDateSMS = new HashMap(6);
                tealiumMapEventDateSMS.put("screen_name", "current call details");
                tealiumMapEventDateSMS.put("event_name", "mcare:current call details: button: sms");
                tealiumMapEventDateSMS.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEventDateSMS);

                selectFilter(Category.SMS);
                break;
            case R.id.others_filter_button:
                //Tealium Track Event
                Map<String, Object> tealiumMapEventOthers = new HashMap(6);
                tealiumMapEventOthers.put("screen_name", "current call details");
                tealiumMapEventOthers.put("event_name", "mcare:current call details: button: altele");
                tealiumMapEventOthers.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEventOthers);
                selectFilter(Category.OTHER);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callDetailsActivity = (CallDetailsActivity) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_call_details, container, false);
        ButterKnife.bind(this, view);

        navigationHeader = ((CallDetailsActivity) getActivity()).getNavigationHeader();
        navigationHeader.setTitle(CallDetailsLabels.getCall_details_activity_title());
        ((CallDetailsActivity)getActivity()).setBanListOnSelector();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (reportHtmlRadioButton.isChecked()) {
                    if (emailAddressInput.getVisibility() == View.VISIBLE)
                        emailAddressInput.clearFocus();
                    format = "AHTML";
                }
                if (reportCSVRadioButton.isChecked()) {
                    if (emailAddressInput.getVisibility() == View.VISIBLE)
                        emailAddressInput.clearFocus();
                    format = "CSV";
                }
            }
        });
        sendReportButton.setOnClickListener(sendReport);

        if(callDetailsActivity.isBillClosedDatesFailed()) {
            filterButton.setEnabled(false);
        } else {
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Tealium Track Event
                   /* Map<String, Object> tealiumMapEvent = new HashMap(6);
                    tealiumMapEvent.put("screen_name", "current call details");
                    tealiumMapEvent.put("event_name", "mcare:current call details: button: aplica filtre");
                    tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

                    if (emailAddressInput.getVisibility() == View.VISIBLE)
                        emailAddressInput.clearFocus();
                    startActivityForResult(getCallDetailsFilterIntent(), FILTER_REQUEST_KEY);
                }
            });
        }


        callDetailsActivity.setInitialData();
        if (!(VodafoneController.getInstance().getUser() instanceof PrepaidUser))
            callDetailsActivity.setSubscriberListOnSelector();

        //Tealium trigger survey
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(4);
        tealiumMapView.put("screen_name", "usage");
        //add Qualtrics survey
        TealiumHelper.addQualtricsCommand();
        //track
        TealiumHelper.trackView("usage", tealiumMapView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupButtonsList();
        setFilter();
        setupContentByUserType();
        setupLabels();
    }

    public String getLastSelectedTabId() {
        return lastSelectedTabId;
    }

    private Intent getCallDetailsFilterIntent() {
        Log.d(TAG, "getCallDetailsFilterIntent()");
        Intent intent = new Intent(getActivity().getApplicationContext(), CallDetailsFilter.class);

        CallDetailsFilterModel callDetailsFilterModel = callDetailsActivity.getCallDetailsFilterObject();
        intent.putExtra(FILTER_OBJECT, callDetailsFilterModel);

        return intent;
    }

    private void setFilter() {
        Log.d(TAG, "setInitialFilter()");
        CallDetailsFilterModel callDetailsFilterModel = callDetailsActivity.getCallDetailsFilterObject();

        if (callDetailsFilterModel.getCategory() != null) {
            selectFilter(callDetailsFilterModel.getCategory());

        } else {
            //set filter by default
            selectFilter(Category.VOCE);
        }
    }

    private void setupLabels() {
        Log.d(TAG, "setupLabels()");
        setCardTitle();
        ((TextView) view.findViewById(R.id.email_error_message_text)).setText(CallDetailsLabels.getCall_details_report_emil_error_message());
        ((VodafoneButton) view.findViewById(R.id.filter_button)).setText(CallDetailsLabels.getCall_details_filter_button_label());
        ((VodafoneTextView) view.findViewById(R.id.data_filter_label)).setText(CallDetailsLabels.getCall_details_data_filter());
        ((VodafoneTextView) view.findViewById(R.id.calls_filter_label)).setText(CallDetailsLabels.getCall_details_calls_filter());
        ((VodafoneTextView) view.findViewById(R.id.sms_filter_label)).setText(CallDetailsLabels.getCall_details_sms_filter());
        ((VodafoneTextView) view.findViewById(R.id.others_filter_label)).setText(CallDetailsLabels.getCall_details_the_others_filter());
        ((VodafoneTextView) view.findViewById(R.id.no_results_error_heder_text)).setText(CallDetailsLabels.getCall_details_no_results_error_heder_text());
        ((VodafoneTextView) view.findViewById(R.id.send_report_tittle_label)).setText(CallDetailsLabels.getCall_details_send_report_tittle_label());
        ((VodafoneTextView) view.findViewById(R.id.select_report_type_label)).setText(CallDetailsLabels.getCall_details_select_report_type_label());
        ((CustomEditText) view.findViewById(R.id.send_report_email_input)).setHint(CallDetailsLabels.getCall_details_report_email_adress_hint());

        User user = VodafoneController.getInstance().getUser();
        String email = "";
        if(user.getUserProfile() != null && user.getUserProfile().getEmail() != null) {
            email = user.getUserProfile().getEmail();
            if(email != null && !email.isEmpty()) {
                emailAddressInput.setText(email);
            }
        }

        reportCSVRadioButton.setText(CallDetailsLabels.getCall_details_report_type_csv_label());
        reportHtmlRadioButton.setText(CallDetailsLabels.getCall_details_report_type_html_label());
        sendReportButton.setText(CallDetailsLabels.getCall_details_report_send_report_label());
    }

    private void setCardTitle() {
        Log.d(TAG, "setCardTitle()");
        if (callDetailsActivity.getReportType() != null && callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_BILLED)) {
            ((VodafoneTextView) view.findViewById(R.id.section_name)).setText(CallDetailsLabels.getCall_billed_calls_card_title());
        } else if (callDetailsActivity.getReportType() != null && callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_UNBILLED)) {
            ((VodafoneTextView) view.findViewById(R.id.section_name)).setText(CallDetailsLabels.getCall_unbilled_calls_card_title());
        } else if (callDetailsActivity.getReportType() != null && callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_PREPAID)) {
            ((VodafoneTextView) view.findViewById(R.id.section_name)).setText(CallDetailsLabels.getCall_prepaid_calls_card_title());
        }
    }

    public void setupTabs() {
        Log.d(TAG, "setupTabs()");
        if(getActivity()==null){
            return;
        }

        if (mTabHost != null) {
            tabHostContainer.removeView(mTabHost);
        }

        mTabHost = new CallDetailsTabHost(getContext());
        tabHostContainer.addView(mTabHost);

        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.getTabWidget().setDividerDrawable(null);

        //Tarifate has tab=costIndicator 1
        mTabHost.addTab(
                mTabHost.newTabSpec("1")
                        .setIndicator(createTabIndicator(CallDetailsLabels.getCall_details_charged_calls())),
                CallDetailsFragmentTab.class, null);

        //Incluse has tab=costIndicator 0
        mTabHost.addTab(
                mTabHost.newTabSpec("0")
                        .setIndicator(createTabIndicator(CallDetailsLabels.getCall_details_includet_calls())),
                CallDetailsFragmentTab.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                lastSelectedTabId = tabId;
                setTabColor(mTabHost);
                mTabHost.getTabWidget().setEnabled(false);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity()==null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mTabHost != null)
                                    mTabHost.getTabWidget().setEnabled(true);
                            }
                        });
                    }
                }, 300);
            }
        });

        mTabHost.setFocusable(false);
        mTabHost.setFocusableInTouchMode(false);
        setTabColor(mTabHost);

        Log.d(TAG, "cost indicator " + callDetailsActivity.getCostIndicator());
        Log.d(TAG, "current tab" + (callDetailsActivity.getCostIndicator() != null && callDetailsActivity.getCostIndicator().equals("0") ? 1 : 0));

        if (lastSelectedTabId != null) {
            setCurrentTab("1".equals(lastSelectedTabId) ? 0 : 1);
        } else {
            setCurrentTab(callDetailsActivity.getCostIndicator() != null && callDetailsActivity.getCostIndicator().equals("0") ? 1 : 0);
        }
    }

    public void setCurrentTab(int index) {
        Log.d(TAG, "setCurrentTab with index = " + index);
        mTabHost.setCurrentTab(index);
    }

    public void setTabColor(TabHost tabhost) {
        if (getContext() == null) {
            return;
        }
        Log.d(TAG, "setTabColor()");
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.unselected_tab_shape); // unselected
            TextView tabTitle = tabhost.getTabWidget().getChildAt(i).findViewById(R.id.label);
            tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_title_color));

        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.drawable.selected_tab_shape); // selected
        TextView tabTitle = tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(R.id.label);
        tabTitle.setTextColor(ContextCompat.getColor(mContext, R.color.selected_tab_indicator_color));
    }

    private View createTabIndicator(String label) {
        View tabIndicator = getActivity().getLayoutInflater().inflate(R.layout.tab_indicator_background, null);
        TextView tv = tabIndicator.findViewById(R.id.label);
        tv.setText(label);
        return tabIndicator;
    }

    private void setupButtonsList() {

        buttonsList = new ArrayList<>();
        buttonsList.add(dataFilterButton);
        buttonsList.add(callFilterButton);
        buttonsList.add(smsFilterButton);
        buttonsList.add(othersFilterButton);
    }

    private void selectFilter(Category category) {
        switch (category) {
            case DATE:
                changeButtonFocus(dataFilterButton);
                callDetailsActivity.setSelectedFilter(Category.DATE);
                setupTabs();
                break;
            case VOCE:
                changeButtonFocus(callFilterButton);
                callDetailsActivity.setSelectedFilter(Category.VOCE);
                setupTabs();
                break;
            case SMS:
                changeButtonFocus(smsFilterButton);
                callDetailsActivity.setSelectedFilter(Category.SMS);
                setupTabs();
                break;
            case OTHER:
                changeButtonFocus(othersFilterButton);
                callDetailsActivity.setSelectedFilter(Category.OTHER);
                setupTabs();
                break;
        }
    }

    private void changeButtonFocus(LinearLayout button) {
        Log.d(TAG, "changeButtonFocus()");
        for (int i = 0; i < buttonsList.size(); i++) {
            buttonsList.get(i).setBackgroundResource(R.drawable.gray_circle);
            try {
                ((TextView) buttonsList.get(i).getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_gray_text_color));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        button.setBackgroundResource(R.drawable.red_circle);
        try {
            ((TextView) button.getChildAt(0)).setTextColor(getResources().getColor(R.color.white_text_color));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupContentByUserType() {
        stopLoadingDialog();

        if(callDetailsActivity.isBillClosedDatesFailed()) {
            return;
        }

        if (callDetailsActivity.getBillClosedDatesList() == null || callDetailsActivity.getBillClosedDatesList().isEmpty()) {  //1280178000000
            if (callDetailsActivity.getReportType() != null && callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_BILLED)) {
                noBillIssuedContainer.setVisibility(View.VISIBLE);
                callDetailsContent.setVisibility(View.GONE);
            }
        }
    }

    public void displaySendReportCard() {
        sendReportTittleLayout.setVisibility(View.VISIBLE);
        sendReportCard.setVisibility(View.VISIBLE);
    }

    public void hideSendReportContainer() {
        if (sendReportTittleLayout.getVisibility() != View.GONE) {
            sendReportTittleLayout.setVisibility(View.GONE);
        }
        if (sendReportCard.getVisibility() != View.GONE) {
            sendReportCard.setVisibility(View.GONE);
        }
    }

    View.OnClickListener sendReport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            /*Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "current call details");
            tealiumMapEvent.put("event_name", "mcare:current call details: button: trimite raport");
            tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

            sendReport();
        }
    };

    public void sendReport() {
        if (emailAddressInput.validateCustomEditText()) {
            sendCallDetailsReport();
        }
    }

    public void sendCallDetailsReport() {
        Log.d(TAG, "sendCallDetailsReport()");
        if (getContext() == null || !(getActivity() instanceof CallDetailsActivity)) {
            return;
        }
        showLoadingDialog();

        CallDetailsActivity callDetailsActivity = (CallDetailsActivity) getActivity();

        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest();

        CallDetailsFilterModel callDetailsFilterModel = callDetailsActivity.getCallDetailsFilterObject();

        String banId = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        callDetailsReportRequest.setBanId(banId);
        callDetailsReportRequest.setReportType(callDetailsActivity.getReportType());
        callDetailsReportRequest.setEmail(emailAddressInput.getText().toString());
        callDetailsReportRequest.setStartDate(callDetailsFilterModel.getStartDate());
        callDetailsReportRequest.setEndDate(callDetailsFilterModel.getEndDate());
        callDetailsReportRequest.setOutputFormat(format);
        callDetailsReportRequest.setSubscriberId(UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
        callDetailsReportRequest.setPhoneNumber(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn());
        callDetailsReportRequest.setCategory(callDetailsFilterModel.getCategory().getName());
        callDetailsReportRequest.setBillClosedDate(callDetailsFilterModel.getLastBillClosedDate());
        callDetailsReportRequest.setNationalInd(callDetailsActivity.isNationalCheckbox());
        callDetailsReportRequest.setInternationaInd(callDetailsActivity.isInternationalCheckbox());
        callDetailsReportRequest.setRoamingInd(callDetailsActivity.isRoamingCheckbox());

        String vfOdsCid = null;

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            vfOdsCid = entityChildItem.getVfOdsCid();
        }

        UserDataService userDataService = new UserDataService(getContext());
        userDataService.sendCallDetailsReport(callDetailsReportRequest, vfOdsCid).subscribe(new RequestSessionObserver<GeneralResponse>() {

            @Override
            public void onNext(GeneralResponse callDetailsSuccessGeneralResponse) {
                Log.d(TAG, "onNext() getCallDetails ");
                stopLoadingDialog();

                if (callDetailsSuccessGeneralResponse.getTransactionStatus() == 0) {

                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(4, 20, VoiceOfVodafoneCategory.Call_Details, null, CallDetailsLabels.getCall_details_success_send_email_text(), "OK", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                    Log.d("vov widget", "insertAuto");
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);

                } else {
                    showSafeToast(LoginLabels.getLoginFailedApiCall());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "onError() getCallDetails");
                stopLoadingDialog();
                showSafeToast(LoginLabels.getLoginFailedApiCall());
            }
        });
    }

    private void showSafeToast(String message) {
        if (getContext() == null) {
            return;
        }
        new CustomToast.Builder(getContext()).message(message)
                .success(false).duration(Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayErrorMessage() {
        Log.d(TAG, "displayErrorMessage()");
        invalidEmailLayout.setVisibility(View.VISIBLE);
        emailErrorMessageText.setText(CallDetailsLabels.getCall_details_report_emil_error_message());
        inactivateButton();
    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage() " + emailAddressInput.isValide());


        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isHighlighted()) {
            emailAddressInput.removeHighlight();
        }

        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailAddressInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            invalidEmailLayout.setVisibility(View.GONE);
        }

        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS) {
            activateButton();
        }
    }

    @Override
    public void activateButton() {
        Log.d(TAG, "activateButton()");
        if (!emailAddressInput.isEmpty() && !emailAddressInput.isHighlighted()) {
            sendReportButton.setEnabled(true);
        }
    }

    @Override
    public void inactivateButton() {
        Log.d(TAG, "inactivateButton()");
        sendReportButton.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
        ((CallDetailsActivity) getActivity()).setTitle();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupAdobeTargetByType();
    }

    private void setupAdobeTargetByType(){
        if(callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_PREPAID))
            callForAdobeTarget(AdobePageNamesConstants.CALL_DETAILS_PREPAID);
        else if (callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_UNBILLED))
            callForAdobeTarget(AdobePageNamesConstants.CALL_DETAILS_CURRENT);
        else if (callDetailsActivity.getReportType().equals(CallDetailsActivity.REPORT_TYPE_BILLED))
            callForAdobeTarget(AdobePageNamesConstants.CALL_DETAILS_BILLED);
    }
}
