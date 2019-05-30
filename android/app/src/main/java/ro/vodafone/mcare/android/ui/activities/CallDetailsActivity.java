package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.billDates.BillsDatesSuccess;
import ro.vodafone.mcare.android.client.model.realm.billDates.RealmBillDates;
import ro.vodafone.mcare.android.client.model.realm.billDates.RealmLong;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFilterModel;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFragmentTab;
import ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsSelectionPageFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.callDetailsFilter.CallDetailsFilter.FILTER_OBJECT;
import static ro.vodafone.mcare.android.ui.activities.callDetailsFilter.CallDetailsFilter.RESET_FLAG;
import static ro.vodafone.mcare.android.ui.activities.callDetailsFilter.CallDetailsFilter.RESULT_FILTER;
import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;
import static ro.vodafone.mcare.android.ui.fragments.callDetails.CallDetailsFragment.UNBILLED_TAB;

/**
 * Created by Bivol Pavel on 13.02.2017.
 */
public class CallDetailsActivity extends MenuActivity {

    public static String TAG = "CallDetailsActivity";

    public static final String REPORT_TYPE_BILLED = "BILLED";
    public static final String REPORT_TYPE_UNBILLED = "UNBILLED";
    public static final String REPORT_TYPE_PREPAID = "PREPAID";
    public static final String EXTRA_PARAMETER_BUNDLE_KEY = "extraParameter";

    private RealmList<RealmLong> billClosedDatesList;
    private boolean billClosedDatesFailed = false;

    private String costIndicator;
    private Category selectedFilter;
    private String reportType;

    private boolean roamingCheckbox = true;
    private boolean internationalCheckbox = true;
    private boolean nationalCheckbox = true;

    private long startDate;
    private long endDate;
    private long lastBillClosedDate;
    private String billLastDateFromBillHistory;

    private NavigationHeader navigationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        deleteBillDates();
        initNavigationFragment();
        selectFragment();
    }

    private void deleteBillDates() {
        Realm realm = Realm.getDefaultInstance();
        RealmManager.delete(realm, RealmBillDates.class);
        realm.close();
    }

    public void setInitialData() {
        nationalCheckbox = true;
        internationalCheckbox = true;
        roamingCheckbox = true;

        Calendar c = Calendar.getInstance();

        if (reportType != null && reportType.equals(REPORT_TYPE_UNBILLED)) {
            //From [Last Billcycle date] to [Current Date]
            setLastBillClosedDate();
            startDate = lastBillClosedDate;
            endDate = c.getTimeInMillis();
        } else if (reportType != null && reportType.equals(REPORT_TYPE_BILLED)) {

            //First and last day of most recent month with bill closed date
            setLastBillClosedDate();

            if (billClosedDatesList != null && billClosedDatesList.isValid() && !billClosedDatesList.isEmpty()) {
                c.setTime(new Date(billClosedDatesList.get(0).getValue()));
                startDate = DateUtils.getFirstDayOfMonth(c).getTime();
                c.setTime(new Date(billClosedDatesList.get(0).getValue()));
                endDate = DateUtils.getLastDayOfMonth(c).getTime();
            }

        } else if (reportType != null && reportType.equals(REPORT_TYPE_PREPAID)) {

            //Most recent month
            c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            startDate = DateUtils.getFirstDayOfMonth(c).getTime();
            c = Calendar.getInstance();
            endDate = c.getTimeInMillis();//current time
        }
    }

    private void initNavigationFragment() {
        navigationHeader = findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this)
                .setTitle(CallDetailsLabels.getCall_details_activity_title())
                .displayDefaultHeader();
        setBanListOnSelector();
    }

    public void setTitle() {
        if (navigationHeader != null) {
            navigationHeader.setTitle(CallDetailsLabels.getCall_details_activity_title())
                    .displayDefaultHeader();
        }
    }

    private void selectFragment() {
        Log.d(TAG, "selectFragment()");
        User user = VodafoneController.getInstance().getUser();

        if (hasParameterOnBundle()) {
            Log.d(TAG, "hasParameterOnBundle");
            String serializedObject = getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_UNBILLED.getExtraParameter());
            switchFragmentOnCreate(CallDetailsFragment.class.getCanonicalName(), serializedObject);
        } else {
            Log.d(TAG, "no CallDetailsFilterModel on budle");
            if (user instanceof PostPaidUser) {
                addFragment(new CallDetailsSelectionPageFragment());
            } else if (user instanceof PrepaidUser) {
                setReportType(REPORT_TYPE_PREPAID);
                addFragment(new CallDetailsFragment());

                //Tealium Track View
                Map<String, Object> tealiumMapView = new HashMap(6);
                tealiumMapView.put("screen_name", "prepaid call details");
                tealiumMapView.put("journey_name", "call details");
                tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackView("screen_name", tealiumMapView);

                CallDetailsSelectionPageFragment.CallDetailsPrepaidTrackingEvent event = new CallDetailsSelectionPageFragment.CallDetailsPrepaidTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);
            }
        }
    }

    public void getBillingDate(int range) {

        showLoadingDialog();
        UserDataService userDataService = new UserDataService(this);

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

        if (selectedBan != null) {
            Log.d(TAG, "ban is not null");
            userDataService.getBillDates(selectedBan, cid, vfOdsCid, range, vfCRMRole).subscribe(new RequestSessionObserver<GeneralResponse<BillsDatesSuccess>>() {
                @Override
                public void onNext(GeneralResponse<BillsDatesSuccess> billsDatesSuccessGeneralResponse) {
                    stopLoadingDialog();

                    if (billsDatesSuccessGeneralResponse.getTransactionStatus() == 0) {
                        saveBillsDatesToRealm(billsDatesSuccessGeneralResponse.getTransactionSuccess());
                        if (billsDatesSuccessGeneralResponse.getTransactionSuccess().getBillClosedDatesList() != null) {
                            setbillsClosedDateList();
                            setLastBillClosedDate();
                        }
                        setBillClosedDatesFailed(false);
                    } else {
                        clearBillClosedDate();
                        setBillClosedDatesFailed(true);
                    }
                    addFragment(new CallDetailsFragment());
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    stopLoadingDialog();
                    setBillClosedDatesFailed(true);
                    addFragment(new CallDetailsFragment());
                }
            });
        } else {
            Log.d(TAG, "null");
            stopLoadingDialog();
            addFragment(new CallDetailsFragment());
        }

    }

    public void saveBillsDatesToRealm(BillsDatesSuccess billsDates) {

        Log.d(TAG, "saveBillsDatesToRealm()");
        RealmList billDatesList = new RealmList<RealmLong>();

        if (billsDates != null && billsDates.getBillClosedDatesList() != null && !billsDates.getBillClosedDatesList().isEmpty()) {
            for (int i = 0; i < billsDates.getBillClosedDatesList().size(); i++) {
                billDatesList.add(new RealmLong(billsDates.getBillClosedDatesList().get(i)));
            }
        }

        RealmManager.update(new RealmBillDates(billDatesList));

        RealmBillDates billDates = (RealmBillDates) RealmManager.getRealmObject(RealmBillDates.class);
        if (billDates != null) {
            Log.d(TAG, "billDates is not null");
            if (billDates.getBillClosedDatesList() != null && !billDates.getBillClosedDatesList().isEmpty()) {
                Log.d(TAG, ":list size " + billDates.getBillClosedDatesList().size());
            }
        }
    }

    private boolean hasParameterOnBundle() {
        boolean hasParameter = false;
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getString(EXTRA_PARAMETER_BUNDLE_KEY) != null) {
            hasParameter = true;
        }
        Log.d(TAG, "hasParameterOnBundle return " + hasParameter);
        return hasParameter;
    }

    public void addFragment(Fragment fragment) {
        Log.d(TAG, "addFragment()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commitAllowingStateLoss();
    }

    public void setBanListOnSelector() {
        Log.d(TAG, "setBanListOnSelector");
        navigationHeader.buildBanSelectorHeader();
    }

    public void setSubscriberListOnSelector() {
        Log.d(TAG, "setSubscriberListOnSelector");
        navigationHeader.buildMsisdnSelectorHeader();
    }

    public void hideSelector() {
        Log.d(TAG, "setSubscriberListOnSelector");
        navigationHeader.hideSelectorViewWithoutTriangle();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() with code: " + resultCode);

        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                updateHeaderData();
            } else if (resultCode == RESULT_FILTER) {
                updateFilteringData(data);
            }
        }
    }

    private void updateHeaderData() {
        if (FragmentUtils.getVisibleFragment(this, false) instanceof CallDetailsFragment) {
            refreshFragment();
        }
    }

    public void refreshFragment() {
        if (FragmentUtils.getVisibleFragment(this, false) instanceof CallDetailsFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(FragmentUtils.getVisibleFragment(this, false));
            transaction.attach(FragmentUtils.getVisibleFragment(this, false));
            transaction.commitAllowingStateLoss();
        }
    }

    public CallDetailsFilterModel getCallDetailsFilterObject() {
        Log.d(TAG, "getCallDetailsFilterObject()");
        CallDetailsFilterModel callDetailsFilter = new CallDetailsFilterModel();

        callDetailsFilter.setStartDate(startDate);
        callDetailsFilter.setEndDate(endDate);
        callDetailsFilter.setLastBillClosedDate(lastBillClosedDate);
        callDetailsFilter.setInternational(internationalCheckbox);
        callDetailsFilter.setNational(nationalCheckbox);
        callDetailsFilter.setRoaming(roamingCheckbox);
        callDetailsFilter.setReportType(reportType);
        callDetailsFilter.setCategory(selectedFilter);

        return callDetailsFilter;
    }

    private void clearData() {
        reportType = null;
        selectedFilter = null;
        costIndicator = null;
        lastBillClosedDate = 0;
    }

    private void updateFilteringData(Intent data) {

        boolean resetFlag = data.getBooleanExtra(RESET_FLAG, false);

        if (resetFlag) {
            setInitialData();
            refreshTab();
        } else {
            CallDetailsFilterModel callDetailsFilterModel = (CallDetailsFilterModel) data.getSerializableExtra(FILTER_OBJECT);

            if (isFilterChanged(callDetailsFilterModel)) {

                roamingCheckbox = callDetailsFilterModel.isRoaming();
                internationalCheckbox = callDetailsFilterModel.isInternational();
                nationalCheckbox = callDetailsFilterModel.isNational();

                Log.d(TAG, "national checkbox" + callDetailsFilterModel.isNational());
                Log.d(TAG, "national checkbox" + callDetailsFilterModel.isInternational());
                Log.d(TAG, "national checkbox" + callDetailsFilterModel.isRoaming());

                this.startDate = callDetailsFilterModel.getStartDate();
                this.endDate = callDetailsFilterModel.getEndDate();
                this.lastBillClosedDate = callDetailsFilterModel.getLastBillClosedDate();

                refreshTab();

            } else {
                Log.d(TAG, "The values are not changed");
            }
        }
    }

    private void refreshTab() {
        if (FragmentUtils.getVisibleFragment(this, false) instanceof CallDetailsFragment) {
            try {
                CallDetailsFragment callDetailsFragment = ((CallDetailsFragment) FragmentUtils.getVisibleFragment(this, false));
                CallDetailsFragmentTab fragmentTab = (CallDetailsFragmentTab) callDetailsFragment.getChildFragmentManager()
                        .findFragmentByTag(callDetailsFragment.mTabHost.getCurrentTabTag());
                fragmentTab.refresh();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (FragmentUtils.getVisibleFragment(this, false) instanceof CallDetailsFragmentTab) {
            CallDetailsFragmentTab fragmentTab = (CallDetailsFragmentTab) FragmentUtils.getVisibleFragment(this, false);
            if(fragmentTab != null) {
                fragmentTab.refresh();
            }
        } else {
            Log.d(TAG, "Current Fragment is  NOT instance of Both  ");
        }
    }

    public void changeTabIfNoResult() {
        Log.d(TAG, "changeTabIfNoResult()");
        try {
            if(getIntent().getExtras() != null &&
                    getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_BILLED.getExtraParameter()) != null) {
                String serializedObject = getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_UNBILLED.getExtraParameter());
                CallDetailsFilterModel callDetailsFilterModel = new Gson().fromJson(serializedObject, CallDetailsFilterModel.class);
                if(callDetailsFilterModel != null && callDetailsFilterModel.getCostIndicator() != null)
                    return;
            }

            CallDetailsFragment callDetailsFragment = ((CallDetailsFragment) FragmentUtils.getVisibleFragment(this, false));
            if(callDetailsFragment == null) {
                return;
            }
            if (callDetailsFragment.mTabHost.getCurrentTabTag().equals("1") && callDetailsFragment.getLastSelectedTabId() == null) {
                callDetailsFragment.setCurrentTab(UNBILLED_TAB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFilterChanged(CallDetailsFilterModel callDetailsFilterModel) {
        if (roamingCheckbox != callDetailsFilterModel.isRoaming() || internationalCheckbox != callDetailsFilterModel.isInternational()
                || nationalCheckbox != callDetailsFilterModel.isNational() || startDate != callDetailsFilterModel.getStartDate()
                || endDate != callDetailsFilterModel.getEndDate() || lastBillClosedDate != callDetailsFilterModel.getLastBillClosedDate()) {
            return true;
        }
        return false;
    }

    @Override
    protected int setContent() {
        return R.layout.activity_call_details;
    }

    @Override
    public void switchFragmentOnCreate(String fragmentName, String extraParameter) {
        Log.d(TAG, "switch fragment on create");
        if (fragmentName == null) {
            return;
        }
        if (fragmentName.equalsIgnoreCase(IntentActionName.CALL_DETAILS_UNBILLED.getFragmentClassName())) {
            try {
                Log.d(TAG, "IntentActionName.CALL_DETAILS_UNBILLED selected");

                String serializedObject;
                if(getIntent().getExtras() != null &&
                        getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_UNBILLED.getExtraParameter()) != null) {
                    serializedObject = getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_UNBILLED.getExtraParameter());
                } else {
                    return;
                }

                CallDetailsFilterModel callDetailsFilterModel = new Gson().fromJson(serializedObject, CallDetailsFilterModel.class);


                if (callDetailsFilterModel != null) {
                    Log.d(TAG, "from bundle: " + callDetailsFilterModel.getReportType());
                    Log.d(TAG, "from bundle: " + callDetailsFilterModel.getCostIndicator());

                    reportType = callDetailsFilterModel.getReportType();
                    selectedFilter = callDetailsFilterModel.getCategory();
                    costIndicator = callDetailsFilterModel.getCostIndicator();
                    lastBillClosedDate = callDetailsFilterModel.getLastBillClosedDate();

                    Log.d(TAG, "lastBillClosedDate" + lastBillClosedDate);
                    getBillingDate(1);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setLastBillClosedDate() {
        Log.d(TAG, "setLastBillCycleDay");
        lastBillClosedDate = 0;
        CallDetailsFilterModel callDetailsFilterModel = null;

        Bundle b = this.getIntent().getExtras();
        if (b != null) {

            try {
                billLastDateFromBillHistory = getIntent().getExtras().getString(IntentActionName.CALL_DETAILS_BILLED.getExtraParameter());
                Log.d(TAG, billLastDateFromBillHistory);
                callDetailsFilterModel = new Gson().fromJson(billLastDateFromBillHistory, CallDetailsFilterModel.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (billLastDateFromBillHistory != null && callDetailsFilterModel != null && callDetailsFilterModel.getLastBillClosedDate() != 0) {
            lastBillClosedDate = callDetailsFilterModel.getLastBillClosedDate();
        } else {
            RealmBillDates billDates = (RealmBillDates) RealmManager.getRealmObject(RealmBillDates.class);
            if (billDates != null && billDates.getBillClosedDatesList() != null
                    && !billDates.getBillClosedDatesList().isEmpty()) {
                if (lastBillClosedDate == 0) {
                    lastBillClosedDate = billDates.getBillClosedDatesList().get(0).getValue();
                }

            }
        }

        if (lastBillClosedDate == 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            lastBillClosedDate = c.getTimeInMillis();
        }

        Log.d(TAG, "lastBillClosedDate - " + lastBillClosedDate);
    }

    public void clearBillClosedDate() {
        RealmManager.delete(RealmBillDates.class);
        billClosedDatesList = null;
    }

    public long getLastBillClosedDate() {
        return lastBillClosedDate;
    }

    public void setbillsClosedDateList() {
        Log.d(TAG, "setBillsDatesList()");

        RealmBillDates billDates = (RealmBillDates) RealmManager.getRealmObject(RealmBillDates.class);
        if (billDates != null) {
            billClosedDatesList = billDates.getBillClosedDatesList();
        }
    }

    public boolean isBillClosedDatesFailed() {
        return billClosedDatesFailed;
    }

    public void setBillClosedDatesFailed(boolean billClosedDatesFailed) {
        if(getReportType() != null && billClosedDatesFailed && getReportType().equalsIgnoreCase(REPORT_TYPE_UNBILLED)) {
            this.billClosedDatesFailed = false;
            setLastBillClosedDate();
        } else {
            this.billClosedDatesFailed = billClosedDatesFailed;
        }
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    public RealmList<RealmLong> getBillClosedDatesList() {
        return billClosedDatesList;
    }

    public String getCostIndicator() {
        return costIndicator;
    }

    public void setCostIndicator(String costIndicator) {
        this.costIndicator = costIndicator;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Category getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(Category selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isRoamingCheckbox() {
        return roamingCheckbox;
    }

    public void setRoamingCheckbox(boolean roamingCheckbox) {
        this.roamingCheckbox = roamingCheckbox;
    }

    public boolean isInternationalCheckbox() {
        return internationalCheckbox;
    }

    public void setInternationalCheckbox(boolean internationalCheckbox) {
        this.internationalCheckbox = internationalCheckbox;
    }

    public boolean isNationalCheckbox() {
        return nationalCheckbox;
    }

    public void setNationalCheckbox(boolean nationalCheckbox) {
        this.nationalCheckbox = nationalCheckbox;
    }

    @Override
    public void onBackPressed() {
        KeyboardHelper.hideKeyboard(this);
        VodafoneController.getInstance().setFromBackPress(true);

        if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
            if (FragmentUtils.getVisibleFragment(this, false) instanceof CallDetailsFragment) {
                if(this.getIntent().getExtras() != null) {
                    this.getIntent().removeExtra(IntentActionName.CALL_DETAILS_BILLED.getExtraParameter());
                }

                addFragment(new CallDetailsSelectionPageFragment());
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentUtils.getVisibleFragment(this, false))
                        .commit();
            } else {
                finish();
            }
        } else {
            super.onBackPressed();
        }
        clearData();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
