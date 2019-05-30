package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.eligibility.UserPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.interfaces.TopUpFragmentTabInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.MyCardsService;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by user on 02.03.2017.
 */
public class TopUpPostpaidImmediateTabFragment extends BaseTopUpFragment implements InputEventsListenerInterface {

    private final String TAG = "ImmediateTabFragment";

    private LinearLayout tabContainer;

    private CustomWidgetLoadingLayout loadingView;

    TextView payWithInvoiceMessage;

    private final String tabId = "0";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_tab_top_up_postpaid_immediate, container, false);

        paymentType = (RadioGroup) view.findViewById(R.id.payment_type);
        paymentType.setOnCheckedChangeListener(paymentTypeListner);

        try {
            rechargeValueSection = (RechargeValueSection) FragmentUtils.getVisibleFragment((AppCompatActivity) getActivity(), false)
                    .getView().findViewById(R.id.recharge_value_section);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabContainer = (LinearLayout) view.findViewById(R.id.tab_container);
        paymentIconsLayout = (LinearLayout) view.findViewById(R.id.payment_icons_layout);
        payWithVoucherLayout = (LinearLayout) view.findViewById(R.id.pay_with_voucher_layout);
        payWithInvoiceLayout = (LinearLayout) view.findViewById(R.id.pay_with_invoice_layout);

        displaySelectedPayTypeLayout(paymentIconsLayout); // by default first radioo button in checked

        voucherCodeInput = (CustomEditText) view.findViewById(R.id.voucher_input);
        voucherErrorLayout = (LinearLayout) view.findViewById(R.id.voucher_error_layout);
        voucherErrorMessage = (VodafoneTextView) view.findViewById(R.id.voucher_error_message);

        payWithInvoiceMessage = (TextView) view.findViewById(R.id.pay_with_invoice_message);
        add_new_card_container = (LinearLayout)view.findViewById(R.id.add_new_card_container);
        cards_container = (LinearLayout)view.findViewById(R.id.cards_container);
        setupLabels();
        checkMsisdnPost4PreEligibility();
        loadCards();
 /*       view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
        Log.d(TAG, "height - " + view.getHeight());
        TealiumHelper.tealiumTrackView(TopUpPostpaidImmediateTabFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpPostpaidOtherNumberImmediateTap);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            ((TopUpFragmentTabInterface) FragmentUtils.getVisibleFragment((AppCompatActivity) getActivity(), false))
                    .fragmentViewInitialized(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupLabels() {
        ((RadioButton) view.findViewById(R.id.pay_with_card)).setText(TopUPLabels.getTop_up_pay_with_card());
        ((RadioButton) view.findViewById(R.id.pay_with_voucher)).setText(TopUPLabels.getTop_up_pay_with_voucher());
        ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setText(TopUPLabels.getTop_up_pay_with_invoice());
        ((CustomEditText) view.findViewById(R.id.voucher_input)).setHint(TopUPLabels.getTop_up_voucher_hint());

    }

    public void checkMsisdnPost4PreEligibility() {
        showLoading();
        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.checkMsisdnPost4PreEligibility(VodafoneController.getInstance().getUserProfile().getMsisdn())
                .subscribe(new RequestSessionObserver<GeneralResponse<UserPost4preEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<UserPost4preEligibilitySuccess> eligibilitySuccessGeneralResponse) {
                        if (eligibilitySuccessGeneralResponse.getTransactionStatus() == 0) {
                            if(isBanInEligibleList(eligibilitySuccessGeneralResponse.getTransactionSuccess())){
                                checkBanPost4PreEligibility();
                            } else {
                                hideLoading();
                                ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_user_is_not_eligible_for_post4pre());
                            }
                        } else {
                            ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);

                            hideLoading();
                            if (eligibilitySuccessGeneralResponse.getTransactionFault() != null) {
                                String errorCode = eligibilitySuccessGeneralResponse.getTransactionFault().getFaultCode();
                                ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                                if (errorCode.equals(ErrorCodes.API31_USER_NOT_ELIGIBLE.getErrorCode())) {
                                    payWithInvoiceMessage.setText(TopUPLabels.getTop_up_msisdn_is_not_eligible_for_post4pre());
                                } else if (errorCode.equals(ErrorCodes.API31_NO_ELIGIBLE_BAN.getErrorCode())) {
                                    payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());
                                } else if (errorCode.equals(ErrorCodes.API31_ACCOUNT_LIST_IS_NULL.getErrorCode())){
                                    payWithInvoiceMessage.setText(TopUPLabels.getTop_up_msisdn_is_not_eligible_for_post4pre());
                                } else {
                                    payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());
                                }

                                // Log.d(TAG, "height - " +view.getHeight());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideLoading();
                        //Log.d(TAG, "height - " +view.getHeight());
                        ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                        payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());
                    }
                });
    }

    public void checkBanPost4PreEligibility() {
        final RechargeService rechargeService = new RechargeService(getContext());
        String banId = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        if(banId != null && !banId.isEmpty()){
            rechargeService.checkBanPost4PreEligibility(banId).subscribe(new RequestSaveRealmObserver<GeneralResponse<BanPost4preEligibilitySuccess>>() {
                @Override
                public void onNext(GeneralResponse<BanPost4preEligibilitySuccess> response) {
                    super.onNext(response);
                    if (response.getTransactionStatus() == 0) {
                        if (response.getTransactionSuccess().getMaxRechargeSum() != 0) {
                            payWithInvoiceMessage.setText(buildEligibleMessage(response.getTransactionSuccess().getMaxRechargeSum(),
                                    response.getTransactionSuccess().getCurrentRechargeSum()));
                            ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(true);
                        } else {
                            ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                            payWithInvoiceMessage.setText(TopUPLabels.getTop_up_user_is_not_eligible_for_post4pre_due_to_max());
                        }

                        TopUpActivity.TopUpTrackingEvent event = new TopUpActivity.TopUpTrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event8 = "event8";
                        journey.getContextData().put("event8", journey.event8);
                        event.defineTrackingProperties(journey);
                        VodafoneController.getInstance().getTrackingService().trackCustom(event);

                    } else {
                        if (response.getTransactionFault() != null) {
                            String errorCode = response.getTransactionFault().getFaultCode();
                            ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                            if (errorCode.equals(ErrorCodes.API31_SUBSCRIBER_RESTRICTION.getErrorCode())) {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_account_age_insufficient());
                            } else if (errorCode.equals(ErrorCodes.API31_SUBSCRIBER_NOT_FOUND.getErrorCode())) {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_msisdn_is_not_eligible_for_post4pre());
                            } else if (errorCode.equals(ErrorCodes.API31_NO_ELIGIBLE_BAN.getErrorCode())) {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_account_age_insufficient());
                            } else if (errorCode.equals(ErrorCodes.API31_PLAN_NOT_ELIGIBLE.getErrorCode())) {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_account_age_insufficient());
                            } else if (errorCode.equals(ErrorCodes.API31_AGE_NOT_ELIGIBLE.getErrorCode())) {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_account_age_insufficient());
                            } else {
                                payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());

                            }
                        }
                    }
                    hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    hideLoading();
                    ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
                    payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());
                }
            });
        } else {
            hideLoading();
            ((RadioButton) view.findViewById(R.id.pay_with_invoice)).setEnabled(false);
            payWithInvoiceMessage.setText(TopUPLabels.getTop_up_api_call_fail());
        }
    }

    private String buildEligibleMessage(Float maxRechargeSum, Float currentRechargeSum) {

        String eligibleBanMessage = TopUPLabels.getTop_up_next_bill_message();
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

        Calendar calendar;

        int billCycleDate;
        int currentDayOfMonth;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (profile != null) {
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            billCycleDate = profile.getBillCycleDate();

            if (maxRechargeSum != null && currentRechargeSum != null) {
                if (billCycleDate != 0) {
                    if (billCycleDate < currentDayOfMonth) {
                        calendar.add(Calendar.MONTH, +1);
                        calendar.add(Calendar.DAY_OF_MONTH, +(billCycleDate - currentDayOfMonth));
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfMonth - billCycleDate));
                    }
                    eligibleBanMessage = String.format(TopUPLabels.getTop_up_selected_ban_eligible(), new SimpleDateFormat("d MMMM", new Locale("RO", "RO")).format(calendar.getTime()), NumbersUtils.twoDigitsAfterDecimal(maxRechargeSum + currentRechargeSum), NumbersUtils.twoDigitsAfterDecimal(currentRechargeSum));
                } else {
                    eligibleBanMessage = TopUPLabels.getTop_up_next_bill_message();
                }
            }
        } else {
            eligibleBanMessage = TopUPLabels.getTop_up_next_bill_message();

        }

        return eligibleBanMessage;
    }

    private void showLoading() {
        showRechargeButton(false);
        loadingView = new CustomWidgetLoadingLayout(getContext()).build(
                tabContainer,
                Color.RED,
                ViewGroupParamsEnum.relative_center);
        loadingView.show();

        if (paymentType.getVisibility() == View.VISIBLE) {
            paymentType.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        showRechargeButton(true);

        if (loadingView != null && loadingView.isVisible()) {
            loadingView.hide();
        }

        if (paymentType.getVisibility() == View.GONE) {
            paymentType.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayErrorMessage() {
        Log.d(TAG, "displayErrorMessage");
        if (voucherCodeInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && voucherCodeInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher_input());
        }
    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage");
        if (voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || voucherCodeInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            voucherErrorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void activateButton() {
        Log.d(TAG, "activateButton immediate");
        boolean isValideVoucher = true; // true because it is hidden
        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "activateButton: voucher check");
            isValideVoucher = !voucherCodeInput.isEmpty() && isVoucherCodeFormat(voucherCodeInput.getText().toString());
            //isVoucherCodeFormat(voucherCodeInput.getText().toString());
        }

        Log.d(TAG, "activateButton: isValideVoucher " + isValideVoucher);
        if (isValideVoucher) {
            TopUpPostpaidPost4PreFragment post4PreFragment = (TopUpPostpaidPost4PreFragment) VodafoneController.findFragment(TopUpPostpaidPost4PreFragment.class);
            if (post4PreFragment != null)
                post4PreFragment.activateButton();
        }
    }

    @Override
    public void inactivateButton() {
        Log.d(TAG, "inactivateButton");
        TopUpPostpaidPost4PreFragment post4PreFragment = (TopUpPostpaidPost4PreFragment) VodafoneController.findFragment(TopUpPostpaidPost4PreFragment.class);
        if (post4PreFragment != null)
            post4PreFragment.inactivateButton();
    }

    private boolean isVoucherCodeFormat(String voucherCode) {
        boolean isValid = false;
        if (voucherCode != null && !voucherCode.equals("")) {

            Pattern pattern = Pattern.compile("^[0-9]{14}$");
            Matcher matcher = pattern.matcher(voucherCode);

            if (matcher.matches()) {
                Log.d(TAG, "isVoucherCodeFormat: matches");
                isValid = true;
            } else {
                Log.d(TAG, "isVoucherCodeFormat: don't match");
                isValid = false;
            }
        }
        return isValid;
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
