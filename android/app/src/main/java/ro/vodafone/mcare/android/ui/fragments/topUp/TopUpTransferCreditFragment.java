package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRecharge;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.TransferCreditRequest;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.FavoriteNumbersSpinnerAdapter;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Subscription;


public class TopUpTransferCreditFragment extends BaseTopUpFragment implements InputEventsListenerInterface, RechargeValueSection.Callback, VodafoneSpinner.Callback, FavoriteNumbersSpinnerAdapter.Callback {

    public static final String TAG = "TopUpTransferCredit";
    boolean isResponsePending = true;
    private VodafoneButton transferButton;
    private LinearLayout termsAndConditions;
    private LinearLayout containerLayout;
    private Subscription subscription;
    private String errorCode = "";
    private String phoneNumber = "";
    private String phoneNumberBeforeOnPauseFragment = "";
    private int amountValue;
    private boolean isPhoneNumberNotTheSame = false;
    private boolean isPendingErrorReceived = false;
    private boolean isErrorCodeNumberInvalid = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditTransferTrackingEvent event = new CreditTransferTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_top_up_transfer_credit, container, false);

        containerLayout = (LinearLayout) view.findViewById(R.id.container_ll);
        favoriteNumbersLayout = (LinearLayout) view.findViewById(R.id.favorite_numbers_layout);

        phoneNumberErrorLayout = (LinearLayout) view.findViewById(R.id.phone_number_error_layout);
        phoneNumberErrorMessage = (VodafoneTextView) view.findViewById(R.id.phone_number_error_message);

        transferButton = (VodafoneButton) view.findViewById(R.id.transfer_button);
        inactivateButton();
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TealiumHelper.tealiumTrackEvent(TopUpTransferCreditFragment.class.getSimpleName(),
                        transferButton.getText().toString(), TealiumConstants.topUpTransferCreditScreenName, "button=");
                displayTransferConfirmation();
            }
        });

        contactsButton = (LinearLayout) view.findViewById(R.id.contacts_button);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountValue = (int) amount;
                phoneNumberBeforeOnPauseFragment = phoneNumber;
                if (Build.VERSION.SDK_INT >= 23) {
                    requestReadContactsPermission();
                } else {
                    opentContactsPage();
                }
            }
        });

        rechargeValueSection = (RechargeValueSection) view.findViewById(R.id.recharge_value_section);

        phoneNumberInput = (CustomEditText) view.findViewById(R.id.telephone_number_input);
        termsAndConditions = (LinearLayout) view.findViewById(R.id.use_conditions_ll);

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideKeyboard(getActivity());
                ((TopUpActivity) getActivity()).addFragment(new TopUpTransferCreditTermsConditionsFragment());
                amountValue = (int) amount;
                phoneNumberBeforeOnPauseFragment = phoneNumber;
                TealiumHelper.tealiumTrackEvent(TopUpTransferCreditFragment.class.getSimpleName(),
                        TopUPLabels.getTop_up_transfer_credit_view_conditions_of_use(), TealiumConstants.topUpTransferCreditScreenName, "button=");
            }
        });
        setupLabels();
        initSpinner();
        checkPhoneNumber();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveChangesOnResume();
        if (isPhoneNumberValid() && !isPhoneNumberMsisdn() && isPhoneNumberNotTheSame && !phoneNumberInput.getText().toString().equals(phoneNumberBeforeOnPauseFragment))
            transferButton.setEnabled(true);
        else
            isPhoneNumberNotTheSame = true;
        if (errorCode == "" && isPhoneNumberValid() && !isPhoneNumberMsisdn())
            transferButton.setEnabled(true);
        if (isPendingErrorReceived) {
            if (phoneNumberErrorLayout.getVisibility() == View.GONE)
                transferButton.setEnabled(true);
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_pending_transfer());
        }
    }

    public void retrieveChangesOnResume() {
        if (phoneNumberInput != null && phoneNumberInput.getText().toString().length() > 0)
            displayErrorPhoneNumber();
        if (errorCode != null)
            checkErrorCode();
        if (amountValue == AppConfiguration.getPrepaidTransferCreditValueOne())
            rechargeValueSection.setDefaultSelectedBobble(0);
        else if (amountValue == AppConfiguration.getPrepaidTransferCreditValueTwo())
            rechargeValueSection.setDefaultSelectedBobble(1);
        else if (amountValue == AppConfiguration.getPrepaidTransferCreditValueThree())
            rechargeValueSection.setDefaultSelectedBobble(2);
        else
            rechargeValueSection.setDefaultSelectedBobble(1);
    }

    private void checkPhoneNumber() {
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (before > 1 && start == 0) {
                    phoneNumberErrorLayout.setVisibility(View.GONE);
                    phoneNumberInput.removeHighlight();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    if (isPhoneNumberValid() && !isPhoneNumberMsisdn()) {
                        if (!s.toString().equals(phoneNumberBeforeOnPauseFragment)) {
                            errorCode = "";
                            isPhoneNumberNotTheSame = true;
                            isErrorCodeNumberInvalid = false;
                            phoneNumber = s.toString();
                            checkHideErrorApi();
                            phoneNumberBeforeOnPauseFragment = "null";
                        } else {
                            isPhoneNumberNotTheSame = false;
                        }
                        phoneNumberErrorLayout.setVisibility(View.GONE);
                    } else {
                        inactivateButton();
                        displayErrorPhoneNumber();
                        isPhoneNumberNotTheSame = false;
                    }
                } else {
                    inactivateButton();
                    phoneNumberBeforeOnPauseFragment = "null";
                }
            }
        });
        phoneNumberInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (displayErrorPhoneNumber()) {
                        isPhoneNumberNotTheSame = false;
                    } else {
                        if(!isErrorCodeNumberInvalid) {
                            phoneNumberErrorLayout.setVisibility(View.GONE);
                            phoneNumberInput.removeHighlight();
                            isPhoneNumberNotTheSame = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void selectElement(int amount) {
        if (this.amount != amount && isPhoneNumberNotTheSame && isPhoneNumberValid() && !isPhoneNumberMsisdn() && !isErrorCodeNumberInvalid) {
            checkHideErrorApi();
            errorCode = "";
            phoneNumberBeforeOnPauseFragment = "null";
        }
        if (!phoneNumberInput.getText().toString().equals(phoneNumberBeforeOnPauseFragment) && isPhoneNumberValid() && !isPhoneNumberMsisdn() && !isErrorCodeNumberInvalid ) {
            if (!errorCode.equals(ErrorCodes.API74_PENDING_TRANSACTION.getErrorCode()))
                hideErrorApi();
            phoneNumberBeforeOnPauseFragment = "null";
        }
        this.amount = amount;
    }

    public void checkHideErrorApi() {
        if (errorCode != null && (errorCode.equals("") || !errorCode.equals(ErrorCodes.API74_PENDING_TRANSACTION.getErrorCode()))) {
            hideErrorApi();
        }
    }

    public void hideErrorApi() {
        if (!isPendingErrorReceived)
            containerRemoveAllViews();
        transferButton.setEnabled(true);
    }

    public void containerRemoveAllViews() {
        containerLayout.post(new Runnable() {
            @Override
            public void run() {
                containerLayout.removeAllViews();
            }
        });
    }

    private boolean displayErrorPhoneNumber() {
        if (isPhoneNumberMsisdn()) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_transfer_credit_error_same_msisdn_added());
            phoneNumberInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS, true);
            return true;
        } else if (!isPhoneNumberValid()) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_transfer_credit_error_wrong_recipient_msisdn());
            phoneNumberInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS, true);
            return true;
        }
        return false;
    }

    private boolean isPhoneNumberValid() {
        if (phoneNumberInput.getText().toString().length() == 10 && phoneNumberInput.getText().toString().subSequence(0, 2).equals("07"))
            return true;
        else
            return false;
    }

    private boolean isPhoneNumberMsisdn() {
        if (phoneNumberInput.getText().toString().length() == 10 && VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getMsisdn().contains(phoneNumberInput.getText().toString()))
            return true;
        else
            return false;
    }

    private void inflateErrorCard(String text) {
        if (getActivity() == null)
            return;
        containerLayout.removeAllViews();
        View instructionalCard = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
        VodafoneTextView instructionalCardText = instructionalCard.findViewById(R.id.instructional_text);
        instructionalCardText.setText(text);
        containerLayout.addView(instructionalCard);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TopUpActivity) getActivity()).getNavigationHeader().displaySelectorView();
        ((TopUpActivity) getActivity()).getNavigationHeader().setTitle(TopUPLabels.getTop_up_transfer_credit_page_title());
        ((TopUpActivity) getActivity()).scrolltoTop();
        TealiumHelper.tealiumTrackView(TopUpTransferCreditFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpTransferCreditScreenName);
    }

    private void setupLabels() {
        phoneNumberInput.setHint(TopUPLabels.getTop_up_phone_number_hint());
        transferButton.setText(TopUPLabels.getTop_up_transfer_credit_transfer_button());
        ((TextView) view.findViewById(R.id.cardTitle)).setText(TopUPLabels.getTop_up_transfer_credit_view_conditions_of_use());

        List recommendedRecharges = new ArrayList<>();
        recommendedRecharges.add(new RecommendedRecharge(AppConfiguration.getPrepaidTransferCreditValueOne(), AppConfiguration.getPrepaidTransferCreditAvailabilityOne()));
        recommendedRecharges.add(new RecommendedRecharge(AppConfiguration.getPrepaidTransferCreditValueTwo(), AppConfiguration.getPrepaidTransferCreditAvailabilityTwo()));
        recommendedRecharges.add(new RecommendedRecharge(AppConfiguration.getPrepaidTransferCreditValueThree(), AppConfiguration.getPrepaidTransferCreditAvailabilityThree()));
        rechargeValueSection.setRecommendedRechargeValues(recommendedRecharges, false, false);
        rechargeValueSection.setupLabels(TopUPLabels.getTop_up_transfer_credit_title_value_card());
        rechargeValueSection.setMarginsRadioGroupLayout((int) (getActivity().getResources().getDisplayMetrics().density * 20), 0, (int) (getActivity().getResources().getDisplayMetrics().density * 20), 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestReadContactsPermission() {
        super.requestReadContactsPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void displayOtherValueField() {
    }

    @Override
    public void hideOtherValueField() {
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        phoneNumberInput.setText(((FavoriteNumber) selectedValue).getPrepaidMsisdn());
    }

    @Override
    public void deleteFavoriteNumeber(int position) {
        deleteFavoriteNumber(position);
    }

    @Override
    public void displayErrorMessage() {
    }

    @Override
    public void hideErrorMessage() {
        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isHighlighted()) {
            phoneNumberInput.removeHighlight();
        }
        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || phoneNumberInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            phoneNumberErrorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void activateButton() {
    }

    @Override
    public void inactivateButton() {
        transferButton.setEnabled(false);
    }

    private void displayTransferConfirmation() {

        CreditTransferOverlayTrackingEvent event = new CreditTransferOverlayTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlayDialog.show();
        TealiumHelper.tealiumTrackView(TopUpTransferCreditFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.transferCreditOverLayScreenName);


        confirmRechargeBtn = (VodafoneButton) overlayDialog.findViewById(R.id.buttonKeepOn);
        confirmRechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TealiumHelper.tealiumTrackEvent(TopUpTransferCreditFragment.class.getSimpleName(),
                        confirmRechargeBtn.getText().toString(), TealiumConstants.transferCreditOverLayScreenName, "button=");

                transferCreditRequest();

                TealiumHelper.tealiumTrackView(TopUpTransferCreditFragment.class.getSimpleName(),
                        TealiumConstants.topUpJourney,TealiumConstants.topUpTransferCreditScreenName);
            }
        });
        modifyRechargeBtn = (VodafoneButton) overlayDialog.findViewById(R.id.buttonTurnOff);
        modifyRechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                TealiumHelper.tealiumTrackEvent(TopUpTransferCreditFragment.class.getSimpleName(),
                        modifyRechargeBtn.getText().toString(), TealiumConstants.transferCreditOverLayScreenName, "button=");

                TealiumHelper.tealiumTrackView(TopUpTransferCreditFragment.class.getSimpleName(),
                        TealiumConstants.topUpJourney,TealiumConstants.topUpTransferCreditScreenName);

            }
        });
        closeBtn = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        overlayContext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        String subtitle = TopUPLabels.getTop_up_transfer_credit_overlay_subtitle().replace("{x}", Integer.toString((int) amount));
        msisdn = phoneNumberInput.getText().toString();

        overlayContext.setText(subtitle.replace("{07xxxxxxxx}", msisdn));
        overlayTitle.setText(TopUPLabels.getTop_up_transfer_credit_overlay_title());
        confirmRechargeBtn.setText(TopUPLabels.getTop_up_transfer_credit_overlay_confirm_button());
        modifyRechargeBtn.setText(TopUPLabels.getTop_up_transfer_credit_overlay_back_button());

    }

    public String getBalance() {
        BalanceCreditSuccess balanceCreditSuccess = (BalanceCreditSuccess) RealmManager.getRealmObject(BalanceCreditSuccess.class);
        if (balanceCreditSuccess != null && balanceCreditSuccess.isValid())
            if (balanceCreditSuccess.getBalance() != null)
                return NumbersUtils.twoDigitsAfterDecimal(balanceCreditSuccess.getBalance());
            else if (balanceCreditSuccess.getBalance() == 0)
                return "0";
        return "";
    }

    public void transferCreditRequest() {
        showLoadingDialog();
        isResponsePending = true;
        delayNavigationDashboard();

        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSsoUserRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();

        subscription = rechargeService.transferCredit(new TransferCreditRequest(msisdn, (int) amount), vfSid, vfPhoneNumber, vfSsoUserRole).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse generalResponse) {
                if (overlayDialog != null)
                    overlayDialog.dismiss();
                if (generalResponse.getTransactionStatus() == 0) {
                    displayConfirmationDialogOnDashboard(String.valueOf(amount), TRANSFER_CREDIT_ID);
//                    errorCode = ErrorCodes.API74_PENDING_TRANSACTION.getErrorCode();
//                    checkErrorCode();

                } else if (generalResponse.getTransactionStatus() != 0) {
                    if (isResponsePending) {
                        errorCode = generalResponse.getTransactionFault().getFaultCode();
                        if (!checkErrorCode())
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
                    } else {
                        sendVOVMessage(TopUPLabels.getTop_up_transfer_credit_Vov_request_fail(), VoiceOfVodafoneCategory.TransferCredit, 15);
                    }
                }
                isResponsePending = false;
                stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
                stopLoadingDialog();
                isResponsePending = false;
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                unSubscribeSubscriptionFromController();
            }
        });
        VodafoneController.getInstance().addSubscription(subscription);
    }

    public boolean checkErrorCode() {
        if (errorCode.equals(ErrorCodes.API74_SIM_BARRED.getErrorCode())) {
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_1());
            inactivateButton();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = false;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_SIM_SUSPENDED.getErrorCode())) {
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_2());
            inactivateButton();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = false;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_CREDIT_EXPIRED.getErrorCode())) {
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_2());
            inactivateButton();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = false;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_BALANCE_INSUFFICIENT.getErrorCode())) {
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_3().replace("{x}", getBalance()));
            inactivateButton();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = false;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_NUMBER_INVALID.getErrorCode())) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_transfer_credit_error_wrong_recipient_msisdn());
            phoneNumberInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS, true);
            inactivateButton();
            containerRemoveAllViews();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = true;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_SAME_NUMBERS.getErrorCode())) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_transfer_credit_error_same_msisdn_added());
            phoneNumberInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS, true);
            inactivateButton();
            containerRemoveAllViews();
            isPendingErrorReceived = false;
            isErrorCodeNumberInvalid = true;
            return true;
        } else if (errorCode.equals(ErrorCodes.API74_PENDING_TRANSACTION.getErrorCode())) {
            inflateErrorCard(TopUPLabels.getTop_up_transfer_credit_error_pending_transfer());
            isPendingErrorReceived = true;
            isErrorCodeNumberInvalid = false;
            return true;
        } else return false;
    }

    public void unSubscribeSubscriptionFromController() {
        if (VodafoneController.getInstance().getCompositeSubscription() != null) {
            VodafoneController.getInstance().removeSubscription(subscription);
        }
    }

    public void delayNavigationDashboard() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isResponsePending) {
                    sendVOVMessage(TopUPLabels.getTop_up_transfer_credit_Vov_request_received(), VoiceOfVodafoneCategory.TransferCredit, 16);
                    stopLoadingDialog();
                    overlayDialog.dismiss();
                    new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_transfer_credit_toast_ttl_succes_message()).success(true).show();
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
                    isResponsePending = false;
                }
            }
        }, AppConfiguration.getTransferCreditWaitingTime() * 1000);
    }

    public void sendVOVMessage(String vovMessage, VoiceOfVodafoneCategory voiceOfVodafoneCategory, int vovId) {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(vovId, 20, voiceOfVodafoneCategory,
                null, vovMessage, TopUPLabels.getTop_up_transfer_credit_vov_mesaje_ok(), null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }


    public static class CreditTransferTrackingEvent extends TrackingEvent {
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put(TrackingVariable.EV_ERROR_EVENT, s.event11);
            }
            s.pageName = "mcare:credit transfer";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, s.pageName);
            s.channel = "credit transfer";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:credit transfer";
            s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_2, s.prop21);
            s.eVar73 = "credit transfer";
            s.getContextData().put(TrackingVariable.P_PROP73, s.eVar73);
        }
    }


    public static class CreditTransferOverlayTrackingEvent extends TrackingEvent {
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put(TrackingVariable.EV_ERROR_EVENT, s.event11);
            }
            s.pageName = "mcare:credit transfer overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, s.pageName);
            s.channel = "credit transfer";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:credit transfer overlay";
            s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_2, s.prop21);
            s.eVar73 = "credit transfer";
            s.getContextData().put(TrackingVariable.P_PROP73, s.eVar73);
        }
    }

}
