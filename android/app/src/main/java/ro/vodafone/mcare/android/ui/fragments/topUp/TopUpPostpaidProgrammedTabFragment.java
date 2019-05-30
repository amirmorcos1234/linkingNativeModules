package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.wefika.horizontalpicker.HorizontalPicker;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.eligibility.Post4preCountersSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.UserPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.datepickers.CalendarPickerView;
import ro.vodafone.mcare.android.ui.views.datepickers.WeeklyRechargesDaySelectorSection;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by user on 02.03.2017.
 */
public class TopUpPostpaidProgrammedTabFragment extends BaseFragment implements VodafoneSpinner.Callback, WeeklyRechargesDaySelectorSection.Callback, CalendarPickerView.Callback {

    boolean isFirsTime;
    private View view;
    private VodafoneSpinner vdfSpinner;
    public static Logger LOGGER = Logger.getInstance(TopUpPostpaidProgrammedTabFragment.class);
    private LinearLayout tabContainer;
    private LinearLayout errorMessageLayout;

    private LinearLayout loadingLayout;
    private VodafoneTextView rechargesAvailableLabel;

    private HorizontalPicker monthlyRechargeSelectorLayout;
    private WeeklyRechargesDaySelectorSection weeklyRechargeSelectorLayout;
    private CalendarPickerView dateRechargeSelectorLayout;

    private Integer weeklyMaximumRecharges;
    private Integer weeklyAvailableRecharges;
    private Integer dateMaximumRecharges;
    private Integer dateAvailableRecharges;
    private Integer monthlyMaximumRecharges;
    private Integer monthlyAvailableRecharges;

    private VodafoneButton recurrentRechargesBtn;

    private CustomWidgetLoadingLayout loadingView;

    private RadioGroup payBillRadioButton;

    private Calendar currentDate = Calendar.getInstance();

    private RelativeLayout horizontalPickerContainer;
    private View inflatedHorizontalPicker;

    protected int selectedDayId;
    private static final String TAG = "TopUpPostpaidProgrammed";
    private LocalDate selectedDate;

    private final String tabId = "1";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_tab_top_up_postpaid_programmed, container, false);
        Log.d(TAG, "onCreateView: ");
        isFirsTime = true;
        horizontalPickerContainer = (RelativeLayout) view.findViewById(R.id.horizontal_container);

        initMonthlyRechargePicker();
        payBillRadioButton = (RadioGroup) view.findViewById(R.id.payment_type);

        weeklyRechargeSelectorLayout = (WeeklyRechargesDaySelectorSection) view.findViewById(R.id.weekly_recharge_selector);
        weeklyRechargeSelectorLayout.setCallback(this);
        weeklyRechargeSelectorLayout.setDefaultSelectedDay();

        dateRechargeSelectorLayout = (CalendarPickerView) view.findViewById(R.id.calendar_picker_view);
        dateRechargeSelectorLayout.setBackgroundColor(getResources().getColor(R.color.white));
        dateRechargeSelectorLayout.setCallback(this);

        List<String> rechargesTypesList = new LinkedList<>(Arrays.asList(BEOLabels.getWeekly_programmed_recharge(), BEOLabels.getDate_programmed_recharge(), BEOLabels.getMonthly_programmed_recharge()));
        VodafoneSpinnerAdapter programmedRechargesadapter = new VodafoneSpinnerAdapter(getContext(), rechargesTypesList, R.drawable.selector);

        rechargesAvailableLabel = (VodafoneTextView) view.findViewById(R.id.x_recharges_available_label);
        recurrentRechargesBtn = (VodafoneButton) view.findViewById(R.id.recurrent_recharges_button);
        recurrentRechargesBtn.setOnClickListener(recurrentPageListener);

        tabContainer = (LinearLayout) view.findViewById(R.id.tab_container);

        VodafoneTextView payWithInvoiceMessage = (VodafoneTextView) view.findViewById(R.id.top_up_pay_invoice_message);
        payWithInvoiceMessage.setText(setNextPayBillMessage());

        checkMsisdnPost4PreEligibility();

        vdfSpinner = (VodafoneSpinner) view.findViewById(R.id.spinner);
        vdfSpinner.setCallback(this);
        vdfSpinner.setSelectedValue(BEOLabels.getMonthly_programmed_recharge());
        vdfSpinner.setAdapter(programmedRechargesadapter);

        //set default selected dropdown element
        ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedSpinnerElement(BEOLabels.getMonthly_programmed_recharge());


   /*     view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "height in Observable - " +view.getHeight());
                if(view.getHeight()!=0 ) {
                    TopUpPostpaidPost4PreFragment.getInstance().setTabHeight(view.getHeight());
                }
            }
        });*/

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(TopUpPostpaidProgrammedTabFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpPostpaidOtherNumberProgramedTap);
    }

    private void getWeeklyPost4PreCounters() {
        Log.d(TAG, "getWeeklyPost4PreCounters: ");
        final RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.getPost4PreCounters().subscribe(new RequestSessionObserver<GeneralResponse<Post4preCountersSuccess>>() {

            @Override
            public void onNext(GeneralResponse<Post4preCountersSuccess> post4preCountersSuccessGeneralResponse) {
                hideLoading();
                if (post4preCountersSuccessGeneralResponse.getTransactionStatus() == 0) {

                    weeklyMaximumRecharges = post4preCountersSuccessGeneralResponse.getTransactionSuccess().getWeeklyMax();
                    weeklyAvailableRecharges = weeklyMaximumRecharges - post4preCountersSuccessGeneralResponse.getTransactionSuccess().getWeeklyCurrent();

                    monthlyMaximumRecharges = post4preCountersSuccessGeneralResponse.getTransactionSuccess().getMonthlyMax();
                    monthlyAvailableRecharges = monthlyMaximumRecharges - post4preCountersSuccessGeneralResponse.getTransactionSuccess().getMonthlyCurrent();

                    dateMaximumRecharges = post4preCountersSuccessGeneralResponse.getTransactionSuccess().getFixedDateMax();
                    dateAvailableRecharges = dateMaximumRecharges - post4preCountersSuccessGeneralResponse.getTransactionSuccess().getFixedDateCurrent();

                    replaceCountersInLabel(TopUPLabels.getTop_up_x_date_recharges_avalaible(), monthlyMaximumRecharges, monthlyAvailableRecharges);

                    //added if default monthlyAvaibleRecharges == 0
                    if (monthlyAvailableRecharges == 0) {
                        horizontalPickerContainer.setVisibility(View.GONE);
                        showRecurrentRechargesButton();
                    }

                    if (weeklyAvailableRecharges == 0 && monthlyAvailableRecharges == 0 && dateAvailableRecharges == 0) {
                        showErrorMessage(TopUPLabels.getTop_up_user_is_not_eligible_due_to_max_programmed(), false, false);
                    } else {
                        //Tealium trigger survey
                        //add Qualtrics survey
                        TealiumHelper.addQualtricsCommand();

                        TopUpActivity.TopUpTrackingEvent event = new TopUpActivity.TopUpTrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event8 = "event8";
                        journey.getContextData().put("event8", journey.event8);
                        event.defineTrackingProperties(journey);
                        VodafoneController.getInstance().getTrackingService().trackCustom(event);

                    }
                } else {
                    showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
            }
        });
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        horizontalPickerContainer.setVisibility(View.GONE);
        weeklyRechargeSelectorLayout.setVisibility(View.GONE);
        dateRechargeSelectorLayout.setVisibility(View.GONE);
        hideRecurrentRechargesButton();

        if (selectedValue instanceof String) {
            String selectedString = (String) selectedValue;

            if (selectedString.equals(BEOLabels.getMonthly_programmed_recharge())) {
                replaceCountersInLabel(TopUPLabels.getTop_up_x_month_recharges_available(), monthlyMaximumRecharges, monthlyAvailableRecharges);
                vdfSpinner.setText(BEOLabels.getMonthly_programmed_recharge());
                if (monthlyAvailableRecharges != 0) {
                    horizontalPickerContainer.setVisibility(View.VISIBLE);
                } else {
                    showRecurrentRechargesButton();
                }
                //TopUpPostpaidPost4PreFragment.getInstance().setTabHeight(view.getHeight());
            } else if (selectedString.equals(BEOLabels.getDate_programmed_recharge())) {
                replaceCountersInLabel(TopUPLabels.getTop_up_x_date_recharges_avalaible(), dateMaximumRecharges, dateAvailableRecharges);
                vdfSpinner.setText(BEOLabels.getDate_programmed_recharge());
                if (dateAvailableRecharges != 0) {
                    dateRechargeSelectorLayout.setVisibility(View.VISIBLE);
                } else {
                    showRecurrentRechargesButton();
                }
                //TopUpPostpaidPost4PreFragment.getInstance().setTabHeight(view.getHeight());
            } else if (selectedString.equals(BEOLabels.getWeekly_programmed_recharge())) {
                replaceCountersInLabel(TopUPLabels.getTop_up_x_weekly_recharges_available(), weeklyMaximumRecharges, weeklyAvailableRecharges);
                vdfSpinner.setText(BEOLabels.getWeekly_programmed_recharge());
                if (weeklyAvailableRecharges != 0) {
                    weeklyRechargeSelectorLayout.setVisibility(View.VISIBLE);
                } else {
                    showRecurrentRechargesButton();
                }
                //TopUpPostpaidPost4PreFragment.getInstance().setTabHeight(view.getHeight());
            }
        }
        ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedSpinnerElement(selectedValue);
    }

    @Override
    public void selectElement(int selectDayId) {
        this.selectedDayId = selectDayId;
        ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedDayId(selectDayId);
    }


    private void replaceCountersInLabel(String message, Integer maximRecharges, Integer avaibleRecharges) {
        rechargesAvailableLabel.setText(String.format(message, maximRecharges, avaibleRecharges));
    }

    public void initMonthlyRechargePicker() {
        Log.d(TAG, "initMonthlyRechargePicker: ");
        if (inflatedHorizontalPicker != null)
            horizontalPickerContainer.removeView(inflatedHorizontalPicker);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflatedHorizontalPicker = inflater.inflate(R.layout.horizontal_picker_layout, null, false);
        horizontalPickerContainer.addView(inflatedHorizontalPicker);

        final LocalDate ld = LocalDate.now();
        int todayDate = ld.getDayOfMonth();

        selectedDate = ld;

        List<String> dates = new ArrayList<String>();
        for (int i = 0; i < 31; i++) {
            String s = String.format("%02d", i + 1);
            dates.add(s);
        }

        CharSequence[] ar = dates.toArray(new CharSequence[dates.size()]);

        VodafoneTextView magnifier = (VodafoneTextView) inflatedHorizontalPicker.findViewById(R.id.magnifiedText);
        magnifier.setVisibility(View.INVISIBLE);

        monthlyRechargeSelectorLayout = (HorizontalPicker) inflatedHorizontalPicker.findViewById(R.id.horizontal_date_picker);
        monthlyRechargeSelectorLayout.setSaveEnabled(false);
        monthlyRechargeSelectorLayout.setValues(ar);
        monthlyRechargeSelectorLayout.setSideItems(4);
        monthlyRechargeSelectorLayout.setSelectedItem(todayDate - 1);
        monthlyRechargeSelectorLayout.setFadingEdgeLength(400);

        HorizontalPicker.OnItemSelected onItemSelectedListener = new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {

                LOGGER.d("selected item index" + index);
                Log.d(TAG, "onItemSelected: getSelectedItem " + monthlyRechargeSelectorLayout.getSelectedItem());
                if (ld.dayOfMonth().withMaximumValue().getDayOfMonth() == 30) {
                    if (index == 30) {
                        selectedDate = ld.withDayOfMonth(index).plusMonths(1).plusDays(1);
                    } else {
                        selectedDate = ld.withDayOfMonth(monthlyRechargeSelectorLayout.getSelectedItem() + 1);

                        selectedDate = ld.withDayOfMonth(monthlyRechargeSelectorLayout.getSelectedItem() + 1);

                        if (index + 1 < ld.getDayOfMonth()) {
                            selectedDate = ld.withDayOfMonth(index + 1).plusMonths(1);
                        } else {
                            selectedDate = ld.withDayOfMonth(index + 1);
                        }
                        LOGGER.d("date selected " + selectedDate);
                    }
                } else {
                    selectedDate = ld.withDayOfMonth(monthlyRechargeSelectorLayout.getSelectedItem() + 1);

                    if (index + 1 < ld.getDayOfMonth()) {
                        selectedDate = ld.withDayOfMonth(index + 1).plusMonths(1);
                    } else {
                        selectedDate = ld.withDayOfMonth(index + 1);
                    }
                    LOGGER.d("date selected " + selectedDate);
                }
                ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedMonthDay(selectedDate);
            }
        };

        onItemSelectedListener.onItemSelected(todayDate - 1);

        monthlyRechargeSelectorLayout.setOnItemSelectedListener(onItemSelectedListener);

        if (isFirsTime) {
            ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedMonthDay(selectedDate);
            isFirsTime = false;
        }
    }

    @Override
    public void selectedCalendarDate(Date date) {
        Date selectedCalendarDate = date;
        ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) getContext(), false)).getSelectedCalendarDate(selectedCalendarDate);
    }

    private void checkMsisdnPost4PreEligibility() {
        Log.d(TAG, "checkMsisdnPost4PreEligibility: ");
        hideErrorMessage();
        showLoading();
        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.checkMsisdnPost4PreEligibility(VodafoneController.getInstance().getUserProfile().getMsisdn())
                .subscribe(new RequestSessionObserver<GeneralResponse<UserPost4preEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<UserPost4preEligibilitySuccess> eligibilityResponse) {
                        if (eligibilityResponse.getTransactionStatus() == 0) {
                            if(isBanInEligibleList(eligibilityResponse.getTransactionSuccess())){
                                checkBanPost4PreEligibility();
                            } else {
                                showErrorMessage(TopUPLabels.getTop_up_user_is_not_eligible_first_part(), false, true);
                            }
                        } else {
                            hideLoading();
                            if (eligibilityResponse.getTransactionFault() != null) {
                                String errorCode = eligibilityResponse.getTransactionFault().getFaultCode();
                                if (errorCode.equals(ErrorCodes.API31_USER_NOT_ELIGIBLE.getErrorCode())) {
                                    showErrorMessage(TopUPLabels.getTop_up_msisdn_not_eligible_programmed(), false,false);
                                } else if (errorCode.equals(ErrorCodes.API31_NO_ELIGIBLE_BAN.getErrorCode())) {
                                    showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                                } else if (errorCode.equals(ErrorCodes.API31_ACCOUNT_LIST_IS_NULL.getErrorCode())){
                                    showErrorMessage(TopUPLabels.getTop_up_msisdn_not_eligible_programmed(), false,false);
                                } else {
                                    showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                    }
                });
    }

    private void checkBanPost4PreEligibility() {
        Log.d(TAG, "checkBanPost4PreEligibility: ");

        final RechargeService rechargeService = new RechargeService(getContext());
        String banId = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        if(banId != null && !banId.isEmpty()){
            rechargeService.checkBanPost4PreEligibility(banId).subscribe(new RequestSaveRealmObserver<GeneralResponse<BanPost4preEligibilitySuccess>>() {
                @Override
                public void onNext(GeneralResponse<BanPost4preEligibilitySuccess> response) {
                    super.onNext(response);
                    if(getContext()==null){
                        return;
                    }
                    if (response.getTransactionStatus() == 0) {
                        getWeeklyPost4PreCounters();
                    } else {
                        hideLoading();
                        if (response.getTransactionFault() != null) {
                            String errorCode = response.getTransactionFault().getFaultCode();
                            if (errorCode.equals(ErrorCodes.API31_SUBSCRIBER_RESTRICTION.getErrorCode())) {
                                showErrorMessage(TopUPLabels.getTop_up_restriction_or_barring_offer(), false, false);
                            } else if (errorCode.equals(ErrorCodes.API31_SUBSCRIBER_NOT_FOUND.getErrorCode())) {
                                showErrorMessage(TopUPLabels.getTop_up_restriction_or_barring_offer(), false, false);
                            } else if (errorCode.equals(ErrorCodes.API31_NO_ELIGIBLE_BAN.getErrorCode())) {
                                showErrorMessage(TopUPLabels.getTop_up_restriction_or_barring_offer(), false, false);
                            } else if (errorCode.equals(ErrorCodes.API31_PLAN_NOT_ELIGIBLE.getErrorCode())) {
                                showErrorMessage(TopUPLabels.getTop_up_restriction_or_barring_offer(), false, false);
                            } else if (errorCode.equals(ErrorCodes.API31_AGE_NOT_ELIGIBLE.getErrorCode())) {
                                showErrorMessage(TopUPLabels.getTop_up_account_age_insufficient(), false, false);
                            } else {
                                showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
                }
            });
        } else {
            showErrorMessage(TopUPLabels.getTop_up_api_call_fail(), true, false);
        }
    }


    public interface Callback {
        public void getSelectedDayId(int selectedDayId);

        public void getSelectedSpinnerElement(Object selectedValue);

        public void getSelectedMonthDay(LocalDate selectedDate);

        public void getSelectedCalendarDate(Date selectedDate);
    }

    private void showLoading() {
        showRechargeButton(false);

        loadingLayout = (LinearLayout) view.findViewById(R.id.loading_spinner_layout);
        if (loadingLayout.getChildCount() > 0) {
            for (int i = 0; i < loadingLayout.getChildCount(); i++) {
                loadingLayout.removeView(loadingLayout.getChildAt(i));
            }
        }
        if (tabContainer.getVisibility() == View.VISIBLE) {
            tabContainer.setVisibility(View.GONE);
        }
        if (loadingLayout.getVisibility() == View.GONE) {
            loadingLayout.setVisibility(View.VISIBLE);
        }

        loadingView = new CustomWidgetLoadingLayout(getContext()).build(
                loadingLayout,
                Color.RED,
                ViewGroupParamsEnum.relative_center);
        loadingView.show();
    }

    private void hideLoading() {
        showRechargeButton(true);

        if (loadingLayout.getVisibility() == View.VISIBLE) {
            loadingLayout.setVisibility(View.GONE);
        }

        if (tabContainer.getVisibility() == View.GONE) {
            tabContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorMessage(String message, boolean apiFailed, boolean isUserEligibilityError) {
        if(getContext()==null){
            return;
        }
        if (tabContainer.getVisibility() == View.VISIBLE) {
            tabContainer.setVisibility(View.GONE);
        }

        if (loadingLayout.getVisibility() == View.VISIBLE) {
            loadingLayout.setVisibility(View.GONE);
            if (loadingView != null) {
                loadingLayout.removeView(loadingView);
            }
        }

        showRechargeButton(false);

        errorMessageLayout = (LinearLayout) view.findViewById(R.id.top_up_error_message_container);
        ImageView errorIcon = (ImageView) view.findViewById(R.id.top_up_error_message_icon);
        VodafoneTextView errorMessage = (VodafoneTextView) view.findViewById(R.id.top_up_error_message);
        VodafoneTextView errorSubTitle = (VodafoneTextView) view.findViewById(R.id.top_up_error_subtitle);
        errorMessageLayout.setVisibility(View.VISIBLE);

        if (apiFailed) {
            errorIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_alert));
            errorSubTitle.setText(TopUPLabels.getTop_up_tap_to_refresh());
            errorMessageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkMsisdnPost4PreEligibility();
                }
            });
        } else {
            if (errorMessageLayout.hasOnClickListeners()) {
                errorMessageLayout.setOnClickListener(null);
            }
            errorIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.info_circle));
            errorIcon.setClickable(false);
            if(isUserEligibilityError)
                errorSubTitle.setText(TopUPLabels.getTop_up_user_is_not_eligible_second_part());
            else
                errorSubTitle.setText(TopUPLabels.getTop_up_no_eligible_message());
        }
        errorMessage.setText(message);
    }

    private void hideErrorMessage() {
        if (errorMessageLayout != null && errorMessageLayout.getVisibility() == View.VISIBLE) {
            errorMessageLayout.setVisibility(View.GONE);
        }

        showRechargeButton(true);

    }

    private String setNextPayBillMessage() {
        String message;
        Calendar calendar;
        Profile profile;

        int billCycleDate;
        int currentDayOfMonth;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        profile = (Profile) RealmManager.getRealmObject(Profile.class);

        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (profile != null && profile.getBillCycleDate() != null) {
            billCycleDate = profile.getBillCycleDate();
            if (billCycleDate != 0) {
                if (billCycleDate < currentDayOfMonth) {
                    calendar.add(Calendar.MONTH, +1);
                    calendar.add(Calendar.DAY_OF_MONTH, +(billCycleDate - currentDayOfMonth));
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfMonth - billCycleDate));
                }
                message = String.format(TopUPLabels.getTop_up_pay_bill_message(), new SimpleDateFormat("dd MMMM", new Locale("RO", "RO")).format(calendar.getTime()));

            } else {
                message = TopUPLabels.getTop_up_next_bill_message();
            }
        } else {
            message = TopUPLabels.getTop_up_next_bill_message();
        }
        return message;
    }

    View.OnClickListener recurrentPageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((TopUpActivity) getActivity()).addFragment(new TopUpRecurrentRechargesFragment());
        }
    };

    private void showRecurrentRechargesButton() {
        recurrentRechargesBtn.setVisibility(View.VISIBLE);
        showRechargeButton(false);
        payBillRadioButton.setVisibility(View.GONE);
    }

    private void hideRecurrentRechargesButton() {
        recurrentRechargesBtn.setVisibility(View.GONE);
        showRechargeButton(true);
        payBillRadioButton.setVisibility(View.VISIBLE);
    }

    private boolean isBanInEligibleList(UserPost4preEligibilitySuccess eligibilitySuccess){
        List <String> eligibleBanList = eligibilitySuccess.getEligibleBanList();
        for(String ban : eligibleBanList){
            if(ban.equals(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan())){
                return true;
            }
        }
        return false;
    }

    private void showRechargeButton(boolean isVisible){
        TopUpPostpaidPost4PreFragment post4PreFragment = (TopUpPostpaidPost4PreFragment) FragmentUtils.getInstance(VodafoneController.currentSupportFragmentManager(), TopUpPostpaidPost4PreFragment.class);
        if(post4PreFragment != null)
            post4PreFragment.showOrNotRechargeButton(tabId, isVisible);
    }

}
