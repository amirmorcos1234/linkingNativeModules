package ro.vodafone.mcare.android.ui.fragments.recover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RecoverLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.client.model.recover.RecoverUsernameResponse;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.LoginService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.RecoverPasswordActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Bogdan M. on 11.01.2017.
 */
public class RecoverPasswordFragmentSecondStep extends BaseFragment implements LoginService.LoginResultInterface, OnErrorIconClickListener{

    public static String TAG = "RecoverPassSecondStep";

    @BindView(R.id.recover_password_container)
    View recover_password_container;

    @BindView(R.id.activation_code_error_layout)
    TooltipError activation_code_error_layout;

    @BindView(R.id.new_password_error_layout)
    TooltipError new_password_error_layout;
    @BindView(R.id.confirmation_password_error_layout)
    TooltipError confirmation_password_error_layout;

    @BindView(R.id.activation_code_input)
    CustomEditTextCompat activation_code_input;

    @BindView(R.id.new_password_input)
    CustomEditTextCompat new_password_input;
    @BindView(R.id.confirmation_password_input)
    CustomEditTextCompat confirmation_password_input;

    @BindView(R.id.resend_activation_code_button)
    LinearLayout resend_activation_code_button;

    @BindView(R.id.activate_account_button)
    VodafoneButton activate_account_button;
    @BindView(R.id.recover_password_refuse_button)
    VodafoneButton recover_password_refuse_button;

    RecoverPasswordActivity rpActivity;
    boolean unhandledError = false;
    boolean activationFirstTimeFocus = false;
    boolean newPassFirstTimeFocus = false;

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (validFields())
                enableButton();
            else
                disableButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recover_password_second_step, null);
        ButterKnife.bind(this, v);

        rpActivity = (RecoverPasswordActivity) getActivity();

        initTracking();
        initViews();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null) {
            ((RecoverPasswordActivity) getActivity()).setBackButtonVisibility(false);
        }
    }

    void initTracking() {
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "recover password 2nd step");
        tealiumMapView.put("journey_name", "recover data");
        TealiumHelper.trackView("screen_name", tealiumMapView);

        RecoverPasswordSecondStepTrackingEvent event = new RecoverPasswordSecondStepTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        journey.event10 = "event10";
        journey.getContextData().put("event10", journey.event10);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

    }

    void initViews() {
        if(getActivity() != null) {
            ((RecoverPasswordActivity) getActivity()).setBackButtonVisibility(false);
        }

        activation_code_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCustomEditTextError(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true, true);
                } else {
                    checkIfValid(1);
                }
            }
        });

        new_password_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCustomEditTextError(new_password_input, new_password_error_layout, RecoverLabels.getRecoverPasswordPasswordInvalidFormat(), true, false);
                    firstTimeFocus(activationFirstTimeFocus, true);
                } else {
                    checkIfValid(2);
                }
            }
        });

        confirmation_password_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayCustomEditTextError(confirmation_password_input, confirmation_password_error_layout, RecoverLabels.getRecoverUsernameNewPasswordAndConfirmPasswordDoNotMatch(), true, false);
                    firstTimeFocus(activationFirstTimeFocus, newPassFirstTimeFocus);
                } else {
                    checkIfValid(3);
                }
            }
        });

        new_password_input.addTextChangedListener(textWatcher);
        confirmation_password_input.addTextChangedListener(textWatcher);
        activation_code_input.setOnErrorIconClickListener(this);
        new_password_input.setOnErrorIconClickListener(this);
        confirmation_password_input.setOnErrorIconClickListener(this);

        recover_password_refuse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideKeyboard(getActivity());
                ((RecoverPasswordActivity) getActivity()).displayRecoverPasswordFirstStepFragment();
            }
        });

        resend_activation_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideKeyboard(getActivity());
                onResendCodeButtonClick();
            }
        });
    }

    public void firstTimeFocus(boolean activation, boolean newPass) {
        if (!activation) {
            checkIfValid(1);
            activationFirstTimeFocus = true;
        }

        if (!newPass) {
            checkIfValid(2);
            newPassFirstTimeFocus = true;
        }
    }

    public void checkIfValid(int view) {
        switch(view) {
            case 1:
                if (activation_code_input.getText().toString().equals("") || (activation_code_input.getText().toString().replaceAll(" ", "")).equals(""))
                    displayCustomEditTextError(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), false, true);
                else
                    displayCustomEditTextError(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true, true);
                break;
            case 2:
                if (new_password_input.getText().toString().equals("") || !isValidPassword(new_password_input.getText().toString()))
                    displayCustomEditTextError(new_password_input, new_password_error_layout, RecoverLabels.getRecoverPasswordPasswordInvalidFormat(), false, false);
                else
                    displayCustomEditTextError(new_password_input, new_password_error_layout, RecoverLabels.getRecoverPasswordPasswordInvalidFormat(), true, false);
                break;
            case 3:
                if (confirmation_password_input.getText().toString().equals("") || !isValidConfirmedPassword(confirmation_password_input.getText().toString()))
                    displayCustomEditTextError(confirmation_password_input, confirmation_password_error_layout, RecoverLabels.getRecoverUsernameNewPasswordAndConfirmPasswordDoNotMatch(), false, false);
                else
                    displayCustomEditTextError(confirmation_password_input, confirmation_password_error_layout, RecoverLabels.getRecoverUsernameNewPasswordAndConfirmPasswordDoNotMatch(), true, false);
                break;
            default:
                break;
        }
    }

    void onResendCodeButtonClick() {
        showLoadingDialog();
        new AuthenticationService(getActivity().getApplicationContext())
                .resendCode(rpActivity.getUsername(), rpActivity.getPhoneNumber(), rpActivity.getEmail(), rpActivity.getCustomerType())  //customerType = null won't send this parameter
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {
                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        stopLoadingDialog();

                        if (response.getTransactionStatus() == 0) {
                            onCodeResentSuccess();
                        } else
                        if(response.getTransactionStatus() == 2 &&
                                response.getTransactionFault().getFaultCode().equals("EC01212")) {
                            new CustomToast.Builder(getContext())
                                    .message(RecoverLabels.getRecoverPasswordErrorBlacklisted()).success(false).show();
                        } else {
                            onCodeResentFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                    }

                });
    }

    void onCodeResentSuccess() {
        resend_activation_code_button.setVisibility(View.GONE);

        rx.Observable.timer(15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        resend_activation_code_button.setVisibility(View.VISIBLE);
                        resend_activation_code_button.setClickable(true);
                    }
                });
        new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext()).message(RecoverLabels.getRecoverPasswordActivationCodeSuccessfullySendMessage()).success(true).show();
    }

    void onCodeResentFailed() {
        displayServerErrorToast();
    }

    public void disableButton() {
        activate_account_button.setClickable(false);
        activate_account_button.setEnabled(false);
    }

    public void enableButton() {
        activate_account_button.setClickable(true);
        activate_account_button.setEnabled(true);
        activate_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivateAccountButtonClick();
            }
        });
    }

    public boolean isValidPassword(String newPassword) {

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

    public boolean isValidConfirmedPassword(String confirmedPassword) {
        return confirmedPassword.equals(new_password_input.getText().toString());
    }

    public boolean validFields() {
        return isValidPassword(new_password_input.getText().toString())
                && isValidConfirmedPassword(confirmation_password_input.getText().toString());
    }

    public void displayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility, boolean ifActivationCode) {

        if (!ifActivationCode && !visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
                if (!ifActivationCode)
                    ((TextView) errorLayout.findViewById(R.id.errorTextMessage)).setText(errorText);
            } else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
    }

    void onActivateAccountButtonClick() {
        showLoadingDialog();

        unhandledError = true;
        new AuthenticationService(getContext()).activateUserAccount(rpActivity.getUsername(),
                rpActivity.getEmail(),
                activation_code_input.getText().toString(),
                new_password_input.getText().toString(),
                confirmation_password_input.getText().toString(),
                rpActivity.getIsMigrated())

                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                               @Override
                               public void onNext(GeneralResponse response) {

                                   unhandledError = false;
                                   showLogs(response);
                                   stopLoadingDialog();
                                   if (response.getTransactionStatus() == 0) {
                                       //onActivationSuccess();
                                       tryLogin();
                                   } else {
                                       manageErrorCode(response.getTransactionFault().getFaultCode());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   super.onError(e);
                                   if (unhandledError)
                                       displayServerErrorToast();
                                   stopLoadingDialog();
                               }
                           }
                );
    }

    private void tryLogin(){
        LoginService loginService = new LoginService(this);
        loginService.performLogin(rpActivity.getUsername(), new_password_input.getText().toString(),
                VodafoneController.getInstance().getGeneralAppConfiguration().isKeepMeLoggedIn());
    }

    void displayServerErrorToast() {
        new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext())
                .message(RecoverLabels.getRecoverPasswordFailedApiCall())
                .success(false)
                .show();
    }

    void showLogs(GeneralResponse<RecoverUsernameResponse> response) {
        D.v("response = " + response);
        D.w("getTransactionStatus = " + response.getTransactionStatus());
        D.d("response = " + response.getTransactionSuccess());
        if (response.getTransactionSuccess() != null) {
            D.d("getStatus = " + response.getTransactionSuccess().getStatus());
            D.d("getStatus = " + response.getTransactionSuccess().getStep());
        }
        D.i("response = " + response.getTransactionFault());
        if (response.getTransactionFault() != null) {
            D.i("response = " + response.getTransactionFault().getFaultCode());
            D.i("response = " + response.getTransactionFault().getFaultMessage());
            D.i("response = " + response.getTransactionFault().getFaultParam());
        }
    }

    void manageErrorCode(String errorCode) {
        switch (errorCode) {
            case "EC01304":
                displayErrorFocusAfterSend(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoveryPasswordExpiredCode(), false);
                break;
            case "EC01305":
                displayErrorFocusAfterSend(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoverPasswordInvalidCode(), false);
                break;
            case "EC01307":
                displayFailedActivationMessage();
                break;
            default:
                displayServerErrorToast();
        }
    }

    public void displayErrorFocusAfterSend(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility) {

        if (!visibility) {
            errorLayout.setVisibility(View.VISIBLE);
            target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            ((TextView) errorLayout.findViewById(R.id.errorTextMessage)).setText(errorText);
        } else {
            errorLayout.setVisibility(View.GONE);
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        }

    }

    private void displayFailedActivationMessage(){
        new CustomToast.Builder(getContext()).message(RegisterLabels.getActivateAccountErrorMessageMultipleContactIds()).success(false).show();
        Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void enterDashboard() {
        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
    }

    @Override
    public void showCompleteProfile() {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
    }

    @Override
    public void showConfirmProfile() {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
    }

    @Override
    public void showIdentitySelector() {
        new NavigationAction(getActivity()).finishCurrent(false).startAction(IntentActionName.IDENTITY_SELECTOR);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
    }

    @Override
    public void treatErrorCode(GeneralResponse response, String errorCode) {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
    }

    @Override
    public void showRequestFailedMessage(Throwable e, String toastMessage, String apiKey) {
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        new CustomToast.Builder(getContext()).message(
                RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(true).show();
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
        if(activation_code_input.isErrorIconTap())
            displayCustomEditTextError(activation_code_input, activation_code_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true, true);
        else if(new_password_input.isErrorIconTap())
            displayCustomEditTextError(new_password_input, new_password_error_layout, RecoverLabels.getRecoverPasswordPasswordInvalidFormat(), true, false);
        else if(confirmation_password_input.isErrorIconTap())
            displayCustomEditTextError(confirmation_password_input, confirmation_password_error_layout, RecoverLabels.getRecoverUsernameNewPasswordAndConfirmPasswordDoNotMatch(), true, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RECOVER_PASS_2);
    }

    private static class RecoverPasswordSecondStepTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "recover password 2nd step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "recover password 2nd step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "recover data";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Recover Password";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "recover password 2nd step";
            s.getContextData().put("prop21", s.prop21);
        }
    }
}
