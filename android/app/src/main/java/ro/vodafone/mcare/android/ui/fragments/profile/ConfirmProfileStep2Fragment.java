package ro.vodafone.mcare.android.ui.fragments.profile;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
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
import ro.vodafone.mcare.android.ui.activities.ConfirmProfileActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 1/12/2017.
 */


public class ConfirmProfileStep2Fragment extends BaseFragment implements LoginService.LoginResultInterface, OnErrorIconClickListener {

    public static String TAG = "ConfirmProfile2Fragment";

    Context context;
    ConfirmProfileActivity activity;

    String userName;
    String email;
    String msisdn;
    String customerType;

    @BindView(R.id.send_cod_button)
    LinearLayout sendCodButton;

    @BindView(R.id.third_step_register_fragment_container)
    LinearLayout confirmProfileStep2FragmentContainer;

    @BindView(R.id.invalid_unique_code_layout)
    LinearLayout invalidUniqueCodeLayout;
    @BindView(R.id.invalid_password_layout)
    LinearLayout invalidPasswordLayout;
    @BindView(R.id.invalid_confirm_password_layout)
    LinearLayout invalidConfirmPasswordLayout;

    @BindView(R.id.confirm_profile_invalid_password_message)
    TextView confirmProfileInvalidPasswordMessage;
    @BindView(R.id.confirm_profile_invalid_unique_code_layout)
    TextView confirmProfileInvalidUniqueCodeMessage;

    @BindView(R.id.unique_code_input)
    CustomEditTextCompat uniqueCodeInput;
    @BindView(R.id.password_input)
    CustomEditTextCompat passwordInput;
    @BindView(R.id.confirm_password_input)
    CustomEditTextCompat confirmPasswordInput;

    @BindView(R.id.create_account_button)
    Button confirmProfileButton;
    @BindView(R.id.dismiss_confirm_profile_button)
    Button dismissConfirmProfile;

    View.OnClickListener createAccountButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "confirm profile 2nd step");
            tealiumMapEvent.put("event_name", "mcare:confirm profile 2nd step:button: confirmare profil");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            createAccount();
        }
    };

    View.OnClickListener dismissConfirmProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };


    View.OnClickListener sendCodButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendCodButton.setClickable(false);
            Log.d(TAG, " sendCodButtonListner ");
            showLoadingDialog();
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
                enableButton(confirmProfileButton);
            else
                disableButton(confirmProfileButton);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.confirm_profile_step2_fragment, null);
        context = getContext();
        ButterKnife.bind(this, v);
        activity = ((ConfirmProfileActivity) getActivity());
        activity.first_step_back_button.setVisibility(View.INVISIBLE);

        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "confirm profile 2nd step");
        tealiumMapView.put("journey_name", "confirm profile");
        TealiumHelper.trackView("screen_name", tealiumMapView);


        confirmProfileButton.setOnClickListener(createAccountButtonListner);
        disableButton(confirmProfileButton);
        dismissConfirmProfile.setOnClickListener(dismissConfirmProfileListener);
        sendCodButton.setOnClickListener(sendCodButtonListner);

        uniqueCodeInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
        confirmPasswordInput.addTextChangedListener(textWatcher);

        uniqueCodeInput.setOnErrorIconClickListener(this);
        passwordInput.setOnErrorIconClickListener(this);
        uniqueCodeInput.setOnErrorIconClickListener(this);

        uniqueCodeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayEditTextError(uniqueCodeInput, invalidUniqueCodeLayout, true);
                } else {
                    if (uniqueCodeInput.getText().toString().equals(""))
                        uniqueCodeInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
                    else
                        displayEditTextError(uniqueCodeInput, invalidUniqueCodeLayout, true);
                }
            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    displayEditTextError(passwordInput, invalidPasswordLayout, true);
                } else {
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
    public void onDestroyView() {
        activity.first_step_back_button.setVisibility(View.INVISIBLE);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        displayRegisterForm();

        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
        if (userProfile != null) {
            userName = userProfile.getUserName();
            email = userProfile.getEmail();
            msisdn = userProfile.getMsisdn();
            customerType = userProfile.getCustomerType();
        }
    }

    public void displayRegisterForm() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.slid_up_fragment);

        confirmProfileStep2FragmentContainer.startAnimation(anim);
        confirmProfileStep2FragmentContainer.setVisibility(View.VISIBLE);
    }

    public void createAccount() {
        Log.d(TAG, "createAccount");
        onActivateAccount();
    }

    public void tryAutoLogin(String username, final String password) {
        Log.d(TAG, " tryAutoLogin ");

        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
        if (userProfile != null) {
            VodafoneController.getInstance().setUser(userProfile);
        }

        if (null != username && null != password) {

            Log.d(TAG, "username  from preference :" + username);
            Log.d(TAG, "password  from preference :" + password);

            LoginService loginService = new LoginService(this);
            loginService.performLogin(username, password,
                    VodafoneController.getInstance().getGeneralAppConfiguration().isKeepMeLoggedIn());

        } else {
            stopLoadingDialog();
            new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.LOGIN);
        }
    }

    public void onResendCode() {
        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.resendCode(userName, msisdn, email, null)  //customerType = null won't send this parameter
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        Log.d(TAG, "OnNext");
                        stopLoadingDialog();

                        Log.d(TAG, "TransactionStatus: " + response.getTransactionStatus());
                        if (response.getTransactionStatus() == 0) {
                            Log.d(TAG, "TransactionStatus 0 ");

                            new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterInputActivationCodeSend())
                                    .success(true).duration(Toast.LENGTH_SHORT).show();

                            sendCodButton.setVisibility(View.GONE);


                            rx.Observable.timer(15, TimeUnit.SECONDS)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Long>() {
                                        @Override
                                        public void call(Long aLong) {
                                            sendCodButton.setVisibility(View.VISIBLE);
                                            sendCodButton.setClickable(true);
                                        }
                                    });
                        } else {
                            try {
                                Log.d(TAG, "TransactionStatus Error!! " + response.getTransactionFault().getFaultCode());
                                String errorCodoe = response.getTransactionFault().getFaultCode();

                                if (errorCodoe.equals(ErrorCodes.API14_ERROR_CODE_SEND.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(ConfirmProfileLabels.getConfirmProfileAccountApiCallCodeSendFailed()).success(false).show();
                                } else {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
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
                        try {
                            stopLoadingDialog();

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

    public void onActivateAccount() {
        Log.d(TAG, "onActivateAccount");
        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.activateUserAccount(userName, email, uniqueCodeInput.getText().toString(), passwordInput.getText().toString(), confirmPasswordInput.getText().toString())
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        Log.d(TAG, "onActivateAccount OnNext");
                        stopLoadingDialog();

                        Log.d(TAG, "TransactionStatus: " + response.getTransactionStatus());
                        if (response.getTransactionStatus() == 0) {
                            Log.d(TAG, "TransactionStatus 0 ");

                            new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterInputAccountActivated()).success(true).show();
                            tryAutoLogin(userName, passwordInput.getText().toString());

                            ConfirmProfileStep2TrackingEvent event = new ConfirmProfileStep2TrackingEvent();
                            TrackingAppMeasurement journey = new TrackingAppMeasurement();
                            journey.event8 = "event8";
                            journey.getContextData().put("event8", journey.event8);
                            journey.event10 = "event10";
                            journey.getContextData().put("event10", journey.event10);
                            event.defineTrackingProperties(journey);
                            VodafoneController.getInstance().getTrackingService().trackCustom(event);

                        } else {
                            try {
                                Log.d(TAG, "TransactionStatus Error!! " + response.getTransactionFault().getFaultCode());
                                String errorCodoe = response.getTransactionFault().getFaultCode();

                                if (errorCodoe.equals(ErrorCodes.API13_MANDATORY_FIELD.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                } else if (errorCodoe.equals(ErrorCodes.API13_PASSWORD_MIN_LENGTH.getErrorCode())) {
                                    displayError(invalidPasswordLayout, confirmProfileInvalidPasswordMessage, RegisterLabels.getRegisterErrorMessageRegisterPassword());
                                } else if (errorCodoe.equals(ErrorCodes.API13_PASSWORD_MATCH.getErrorCode())) {
                                    displayError(invalidConfirmPasswordLayout, confirmProfileInvalidPasswordMessage, RegisterLabels.getRegisterErrorMessageConfirmPassword());
                                } else if (errorCodoe.equals(ErrorCodes.API13_CODE_EXPIRED.getErrorCode())) {
                                    displayError(invalidUniqueCodeLayout, confirmProfileInvalidUniqueCodeMessage, RegisterLabels.getRegisterErrorMessageExpiredCode());
                                } else if (errorCodoe.equals(ErrorCodes.API13_CODE_INCORRECT.getErrorCode())) {
                                    displayError(invalidUniqueCodeLayout, confirmProfileInvalidUniqueCodeMessage, RegisterLabels.getRegisterErrorMessageInvalidCode());
                                } else if (errorCodoe.equals(ErrorCodes.API13_IDM_ERROR.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                } else if (errorCodoe.equals(ErrorCodes.API13_NO_STATUS_ERROR.getErrorCode())) {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
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
                        try {
                            stopLoadingDialog();

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

    public void displayError(LinearLayout badLinearLayout, TextView errorTextView, String errorText) {

        badLinearLayout.setVisibility(View.VISIBLE);
        errorTextView.setText(errorText);
    }

    public void disableButton(Button button) {
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {
        button.setClickable(true);
        button.setEnabled(true);
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
        callForAdobeTarget(AdobePageNamesConstants.CONFIRM_SECOND);
    }

    public static class ConfirmProfileStep2TrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "confirm profile 2nd step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "confirm profile 2nd step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "confirm profile";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Confirm Profile";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "confirm profile 2nd step";
            s.getContextData().put("prop21", s.prop21);
        }
    }
}

