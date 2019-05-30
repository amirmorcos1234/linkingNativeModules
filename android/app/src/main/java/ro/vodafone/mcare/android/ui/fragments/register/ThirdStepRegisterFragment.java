package ro.vodafone.mcare.android.ui.fragments.register;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.LoginService;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

public class ThirdStepRegisterFragment extends BaseFragment implements LoginService.LoginResultInterface, OnErrorIconClickListener{

    public static String TAG = "ThirdRegisterFragment";
    public static final int HIDE_BUTTON_TIME = 15000;


    Context context;
    RegisterActivity activity;

    String msisdn;
    String customerType;
    String userName;
    String email;

    @BindView(R.id.send_cod_button)
    LinearLayout sendCodButton;

    @BindView(R.id.third_step_register_fragment_container)
    LinearLayout thirdStepRegisterFragmentContainer;

    @BindView(R.id.invalid_unique_code_layout)
    LinearLayout invalidUniqueCodeLayout;
    @BindView(R.id.invalid_password_layout)
    LinearLayout invalidPasswordLayout;
    @BindView(R.id.invalid_confirm_password_layout)
    LinearLayout invalidConfirmPasswordLayout;

    @BindView(R.id.invalid_password_message)
    TextView invalidPasswordMessage;
    @BindView(R.id.invalid_confirm_password_message)
    TextView invalidConfirmPasswordMessage;
    @BindView(R.id.invalid_unique_code_message)
    TextView invalidUniqueCodeMessage;

    @BindView(R.id.unique_code_input)
    CustomEditTextCompat uniqueCodeInput;
    @BindView(R.id.password_input)
    CustomEditTextCompat passwordInput;
    @BindView(R.id.confirm_password_input)
    CustomEditTextCompat confirmPasswordInput;

    @BindView(R.id.create_account_button)
    VodafoneButton createAccountButton;
    @BindView(R.id.dismiss_button)
    VodafoneButton dismissButton;

    @BindView(R.id.header_register_title_tv)
    TextView headerRegisterTitleTv;

    View.OnClickListener createAccountButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "register 3rd step");
            tealiumMapEvent.put("event_name", "mcare:register 3rd step:button: create account");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            KeyboardHelper.hideKeyboard(getActivity());
            createAccount();
        }
    };

    View.OnClickListener dismissButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        }
    };

    View.OnClickListener sendCodButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showLoadingDialog();
            Log.d(TAG, " sendCodButtonListner ");
            onResendCode();
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!passwordInput.getText().toString().equals("")
                    && !confirmPasswordInput.getText().toString().equals("")
                    && !uniqueCodeInput.getText().toString().equals("")
                    && newPasswordIsValid(passwordInput.getText().toString())
                    && confirmPasswordInput.getText().toString().equals(passwordInput.getText().toString()))
                enableButton(createAccountButton);
            else
                disableButton(createAccountButton);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.fragment_third_step_register, null);

        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "register 3rd step");
        tealiumMapView.put("journey_name", "register");
        TealiumHelper.trackView("screen_name", tealiumMapView);

        context = getContext();
        activity = (RegisterActivity) getActivity();
        ButterKnife.bind(this, v);

        headerRegisterTitleTv.setText(RegisterLabels.getRegisterThirdStepPageTitleLabel());

        activity.getBackButton().setVisibility(View.INVISIBLE);
        activity.getBackButton().setOnClickListener(null);

        createAccountButton.setOnClickListener(createAccountButtonListner);
        disableButton(createAccountButton);
        dismissButton.setOnClickListener(dismissButtonListener);

        sendCodButton.setOnClickListener(sendCodButtonListner);

        uniqueCodeInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
        confirmPasswordInput.addTextChangedListener(textWatcher);
        uniqueCodeInput.setOnErrorIconClickListener(this);
        passwordInput.setOnErrorIconClickListener(this);
        confirmPasswordInput.setOnErrorIconClickListener(this);

        uniqueCodeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayEditTextError(uniqueCodeInput, invalidUniqueCodeLayout, true);
                    uniqueCodeInput.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
                } else {
                    if (uniqueCodeInput.getText().toString().equals(""))
                        uniqueCodeInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
                }
            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    invalidPasswordMessage.setText(RegisterLabels.getRegisterErrorMessageRegisterPassword());
                    displayEditTextError(passwordInput, invalidPasswordLayout, true);
                } else {
//                    D.e("we lost focus");
                    if (passwordInput.getText().toString().equals("") || !newPasswordIsValid(passwordInput.getText().toString()))
                        displayEditTextError(passwordInput, invalidPasswordLayout, false);
                    else
                        displayEditTextError(passwordInput, invalidPasswordLayout, true);

                }
            }
        });

        confirmPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    invalidConfirmPasswordMessage.setText("Câmpurile nu coincid.");
                    displayEditTextError(confirmPasswordInput, invalidConfirmPasswordLayout, true);
                } else {
                    if (confirmPasswordInput.getText().toString().equals("") || !confirmPasswordInput.getText().toString().equals(passwordInput.getText().toString()))
                        displayEditTextError(confirmPasswordInput, invalidConfirmPasswordLayout, false);
                    else
                        displayEditTextError(confirmPasswordInput, invalidConfirmPasswordLayout, true);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        displayRegisterForm();

        if(getActivity() != null) {
            msisdn = ((RegisterActivity) getActivity()).getMSISDN();
            customerType = ((RegisterActivity) getActivity()).getCUSTOMER_TYPE();
            userName = ((RegisterActivity) getActivity()).getUSERNAME();
            email = ((RegisterActivity) getActivity()).getEMAIL();
        }
    }

    public void displayRegisterForm() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.slid_up_fragment);

        thirdStepRegisterFragmentContainer.startAnimation(anim);
        thirdStepRegisterFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void displayError(LinearLayout badLinearLayout, TextView messageView, String errorText) {

        badLinearLayout.setVisibility(View.VISIBLE);
        messageView.setText(errorText);
    }

    public void createAccount() {
        onCreateAccount();
    }

    public void onCreateAccount() {
        showLoadingDialog();
        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.activateAccount(userName, email, uniqueCodeInput.getText().toString(),
                passwordInput.getText().toString(), confirmPasswordInput.getText().toString(), false)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        Log.d(TAG, "OnNext");
                        stopLoadingDialog();

                        Log.d(TAG, "TransactionStatus: " + response.getTransactionStatus());
                        if (response.getTransactionStatus() == 0) {
                            Log.d(TAG, "TransactionStatus 0 ");

                            new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterInputAccountActivated()).success(true).show();
                            tryAutoLogin();
                            //redirect to dashboard ofter autologin, or going to login page

                            RegisterThirdStepTrackingEvent event = new RegisterThirdStepTrackingEvent();
                            TrackingAppMeasurement journey = new TrackingAppMeasurement();
                            journey.event8 = "event8";
                            journey.getContextData().put("event8", journey.event8);
                            journey.event10 = "event10";
                            journey.getContextData().put("event10", journey.event10);
                            journey.event17 = "event17";
                            journey.getContextData().put("event17", journey.event17);

                            event.defineTrackingProperties(journey);
                            VodafoneController.getInstance().getTrackingService().trackCustom(event);

                        } else {
                            try {
                                Log.d(TAG, "TransactionStatus Error!! " + response.getTransactionFault().getFaultCode());
                                String errorCode = response.getTransactionFault().getFaultCode();

                                RegisterThirdStepTrackingEvent event = new RegisterThirdStepTrackingEvent();
                                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                journey.event11 = "event11";
                                journey.getContextData().put("event11", journey.event11);
                                journey.prop16 = "prop16";
                                journey.getContextData().put("prop16", journey.prop16);
                                event.defineTrackingProperties(journey);
                                VodafoneController.getInstance().getTrackingService().trackCustom(event);

                                if (errorCode.equals(ErrorCodes.API13_MANDATORY_FIELD.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                } else if (errorCode.equals(ErrorCodes.API13_PASSWORD_MIN_LENGTH.getErrorCode())) {
                                    displayError(invalidPasswordLayout, invalidPasswordMessage, RegisterLabels.getRegisterErrorMessageRegisterPassword());
                                } else if (errorCode.equals(ErrorCodes.API13_PASSWORD_MATCH.getErrorCode())) {
                                    displayError(invalidConfirmPasswordLayout, invalidConfirmPasswordMessage, RegisterLabels.getRegisterErrorMessageConfirmPassword());
                                } else if (errorCode.equals(ErrorCodes.API13_CODE_EXPIRED.getErrorCode())) {
                                    displayError(invalidUniqueCodeLayout, invalidUniqueCodeMessage, RegisterLabels.getRegisterErrorMessageExpiredCode());
                                } else if (errorCode.equals(ErrorCodes.API13_CODE_INCORRECT.getErrorCode())) {
                                    displayError(invalidUniqueCodeLayout, invalidUniqueCodeMessage, RegisterLabels.getRegisterErrorMessageInvalidCode());
                                }else if (errorCode.equals(ErrorCodes.API13_NO_STATUS_ERROR.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterFailedAccountActivation()).success(false).show();
                                } else if (errorCode.equals(ErrorCodes.API13_ACTIVATION_MULTIPLE_CONTACT_ID.getErrorCode())){
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getActivateAccountErrorMessageMultipleContactIds()).success(false).show();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else  {
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterFailedAccountActivation()).success(false).show();
                                }

                            } catch (Exception e) {
                                Log.d(TAG, "Some exceptions occured" + e);
                            }
                        }
                        Log.d(TAG, "Exit from onNext");
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "OnError");
                        stopLoadingDialog();
                        new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterFailedAccountActivation()).success(false).show();
                        RegisterThirdStepTrackingEvent event = new RegisterThirdStepTrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event11 = "event11";
                        journey.getContextData().put("event11", journey.event11);
                        journey.prop16 = "prop16";
                        journey.getContextData().put("prop16", journey.prop16);
                        event.defineTrackingProperties(journey);
                        VodafoneController.getInstance().getTrackingService().trackCustom(event);

                        try {
                            if (e instanceof AuthenticationServiceException) {
                                throw new AuthenticationServiceException(R.string.server_error, e);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public void onResendCode() {
        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.resendCode(userName, msisdn, email, customerType)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        Log.d(TAG, "OnNext");

                        Log.d(TAG, "TransactionStatus: " + response.getTransactionStatus());
                        if (response.getTransactionStatus() == 0) {
                            Log.d(TAG, "TransactionStatus 0 ");

                            new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterInputActivationCodeSend()).success(true).show();
                            stopLoadingDialog();

                            hideButton(HIDE_BUTTON_TIME, sendCodButton);
                        } else {
                            String errorCode = response.getTransactionFault().getFaultCode();
                            if (errorCode.equals(ErrorCodes.API12_GEONUM_AND_BLACKLISTED_EMAIL.getErrorCode()))
                                new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterActivationCodeFailed()).success(false).show();
                            else {
                                new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).errorIcon(true).show();

                                RegisterThirdStepTrackingEvent event = new RegisterThirdStepTrackingEvent();
                                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                journey.event11 = "event11";
                                journey.getContextData().put("event11", journey.event11);
                                journey.prop16 = "prop16";
                                journey.getContextData().put("prop16", journey.prop16);
                                event.defineTrackingProperties(journey);
                                VodafoneController.getInstance().getTrackingService().trackCustom(event);
                            }
                            stopLoadingDialog();
                        }
                        Log.d(TAG, "Exit from onNext");
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        Log.d(TAG, "OnError");

                        RegisterThirdStepTrackingEvent event = new RegisterThirdStepTrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event11 = "event11";
                        journey.getContextData().put("event11", journey.event11);
                        journey.prop16 = "prop16";
                        journey.getContextData().put("prop16", journey.prop16);
                        event.defineTrackingProperties(journey);
                        VodafoneController.getInstance().getTrackingService().trackCustom(event);

                        try {
                            new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();

                            if (e instanceof AuthenticationServiceException) {
                                throw new AuthenticationServiceException(R.string.server_error, e);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public boolean newPasswordIsValid(String newPassword) {

        boolean hasNumber = false;
        boolean hasUpper = false;
        boolean hasLower = false;

        for (int i = 0; i < newPassword.length(); i++) {
            Character s = newPassword.charAt(i);

            if (Character.isDigit(s)) hasNumber = true;
            if (Character.isUpperCase(s)) hasUpper = true;
            if (Character.isLowerCase(s)) hasLower = true;
        }

        return hasNumber && hasUpper && hasLower && (newPassword.length() >= 8);
    }

    public void disableButton(Button button) {
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {
        button.setClickable(true);
        button.setEnabled(true);
    }

    public void displayEditTextError(CustomEditTextCompat target, LinearLayout errorLayout, boolean visibility) {

        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility)
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);

        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
    }

    public void tryAutoLogin() {
        Log.d(TAG, " tryAutoLogin ");

        final String username = userName; //userName took from step2
        final String password = passwordInput.getText().toString();

        Log.d(TAG, "username  from preference :" + username);
        Log.d(TAG, "password  from preference :" + password);

        if (null != username) {
            new LoginService(this).performLogin(username, password,
                    VodafoneController.getInstance().getGeneralAppConfiguration().isKeepMeLoggedIn());
        } else {
            stopLoadingDialog();
            new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        }
    }

    @Override
    public synchronized void enterDashboard() {
        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);
    }

    @Override
    public void showCompleteProfile() {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
    }

    @Override
    public void showConfirmProfile() {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
    }

    @Override
    public void showIdentitySelector() {
        new NavigationAction(getActivity()).finishCurrent(false).startAction(IntentActionName.IDENTITY_SELECTOR);
    }

    @Override
    public void treatErrorCode(GeneralResponse response, String errorCode) {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
    }

    @Override
    public void showRequestFailedMessage(Throwable e, String toastMessage, String apiKey) {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void stopLoading() {
        stopLoadingDialog();
    }

    @Override
    public void onErrorIconClickListener() {
        if(uniqueCodeInput.isErrorIconTap())
            displayEditTextError(uniqueCodeInput, invalidUniqueCodeLayout, true);
        else if (passwordInput.isErrorIconTap())
            displayEditTextError(passwordInput, invalidPasswordLayout, true);
        else if (confirmPasswordInput.isErrorIconTap())
            displayEditTextError(confirmPasswordInput, invalidConfirmPasswordLayout, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.REGISTER_THIRD);
    }

    public static class RegisterThirdStepTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "register 3rd step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "register 3rd step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "register";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Registration";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop22 = "mcare:" + "register 3rd step";
            s.getContextData().put("prop22", s.prop22);
        }
    }

    private void hideButton(int delay, final LinearLayout layout) {
        layout.setEnabled(false);
        layout.setVisibility(View.GONE);
        /* Duration of wait */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
                layout.setEnabled(true);
            }
        }, delay);
    }

}
