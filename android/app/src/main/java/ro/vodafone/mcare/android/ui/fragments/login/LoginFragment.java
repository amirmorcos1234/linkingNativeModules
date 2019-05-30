package ro.vodafone.mcare.android.ui.fragments.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.service.LoginService;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.ConfirmProfileActivity;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.RecoverPasswordActivity;
import ro.vodafone.mcare.android.ui.activities.RecoverUsernameActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomSnackBar;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

public class LoginFragment extends BaseFragment implements InputEventsListenerInterface, LoginService.LoginResultInterface {

    public static String TAG = "LoginFragment";

    private LinearLayout generalErrorLayout;
    private VodafoneButton loginButton;
    private VodafoneButton registerButton;
    private LinearLayout resetAccountButton;
    private LinearLayout loginFragmentContainer;
    private CustomEditText usernameInput;
    private CustomEditText passwordInput;
    private TextView errorMessage;
    private TextView forgottenUsernameButton;
    private TextView forgottenPasswordButton;
    private CheckBox rememberMeCheckbox;
    private boolean isActiveLoginButton = false;
    private boolean isDisplayErrorMessage = false;
    private Context context;

    View.OnClickListener loginButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            trackEvent(TealiumConstants.loginScreen, TealiumConstants.loginFragment_login_button);
            onLogin();
        }
    };

    View.OnClickListener forgottenUsernameButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            trackEvent(TealiumConstants.loginScreen, TealiumConstants.loginFragment_forget_user_name);
            Intent intent = new Intent(getActivity(), RecoverUsernameActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener forgottenPasswordButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            trackEvent(TealiumConstants.loginScreen, TealiumConstants.loginFragment_forget_password);
            Intent intent = new Intent(getActivity(), RecoverPasswordActivity.class);
            startActivityForResult(intent, 1010);
        }
    };

    View.OnClickListener registerButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            trackEvent(TealiumConstants.loginScreen, TealiumConstants.loginFragment_register);
            onRegister();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.fragment_login, null);

        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.loginScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.loginJourney);
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);


        context = getContext();

        generalErrorLayout = (LinearLayout) v.findViewById(R.id.general_error_layout);

        registerButton = (VodafoneButton) v.findViewById(R.id.register_button);
        resetAccountButton = (LinearLayout) v.findViewById(R.id.reset_account_button);
        loginButton = (VodafoneButton) v.findViewById(R.id.login_button);
        loginButton.setEnabled(false);

        loginFragmentContainer = (LinearLayout) v.findViewById(R.id.login_fragment_container);

        usernameInput = (CustomEditText) v.findViewById(R.id.username_input);
        passwordInput = (CustomEditText) v.findViewById(R.id.password_input);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setTypeface(Typeface.DEFAULT);

        forgottenUsernameButton = (TextView) v.findViewById(R.id.forgotten_username_button);
        forgottenPasswordButton = (TextView) v.findViewById(R.id.forgotten_password_button);
        errorMessage = (TextView) v.findViewById(R.id.error_message);

        rememberMeCheckbox = (CheckBox) v.findViewById(R.id.keep_me_logged_in_checkbox);

        loginButton.setOnClickListener(loginButtonListner);
        forgottenUsernameButton.setOnClickListener(forgottenUsernameButtonListner);
        forgottenPasswordButton.setOnClickListener(forgottenPasswordButtonListner);
        registerButton.setOnClickListener(registerButtonListner);

        displayLoginForm();
        setRemeberMeStatus();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private SpannableStringBuilder existingUsernameSnackBarMessage(){
        SpannableStringBuilder myString = new SpannableStringBuilder(RegisterLabels.getRegisterErrorMessageAlredyRegisteredAccount());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d("", "Password Clicked");
                Intent intent = new Intent(getActivity(), RecoverPasswordActivity.class);
                startActivity(intent);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d("", "Username Clicked");
                Intent intent = new Intent(getActivity(), RecoverUsernameActivity.class);
                startActivity(intent);
            }
        };
        //For Click
        myString.setSpan(clickableSpan, 56, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For UnderLine
        myString.setSpan(new UnderlineSpan(), 56, 71, 0);
        //For Click
        myString.setSpan(clickableSpan2, 76, 96, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For UnderLine
        myString.setSpan(new UnderlineSpan(), 76, 96, 0);
        return myString;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean showToastMessage = getActivity().getIntent().getBooleanExtra("show_toast_message", false);
        if(showToastMessage)
            CustomSnackBar.make(getActivity(), existingUsernameSnackBarMessage(), false).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void displayErrorMessage() {
    }

    @Override
    public void hideErrorMessage() {
        hideGeneralError();
    }

    public void displayGeneralError(String label) {
        Log.d(TAG, " displayGeneralError ");
        if (!isDisplayErrorMessage) {
            isDisplayErrorMessage = true;
            generalErrorLayout.setVisibility(View.VISIBLE);
            errorMessage.setText(label);
            inactivateButton();
        }
    }

    public void hideGeneralError() {
        //Log.d(TAG , " hideGeneralError ");
        if (usernameInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && usernameInput.isHighlighted()) {
            usernameInput.removeHighlight();
        }

        if (passwordInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && passwordInput.isHighlighted()) {
            passwordInput.removeHighlight();
        }

        if (usernameInput.isValide() != CustomEditText.INVALID_FORMAT_FIELD_STATUS && passwordInput.isValide() != CustomEditText.INVALID_FORMAT_FIELD_STATUS) {
            isDisplayErrorMessage = false;
            generalErrorLayout.setVisibility(View.GONE);
            activateButton();
        }
    }

    @Override
    public void activateButton() {
        Log.d(TAG, "activateButton");
        if (!usernameInput.isEmpty() && !usernameInput.isHighlighted() && !passwordInput.isEmpty() && !passwordInput.isHighlighted()) {
            loginButton.setEnabled(true);
            isActiveLoginButton = true;
        }
    }

    @Override
    public void inactivateButton() {
        Log.d(TAG, "inactivateButton");
        if (isActiveLoginButton) {
            loginButton.setEnabled(false);
            isActiveLoginButton = false;
        }
    }

    public void displayLoginForm() {
        Log.d(TAG, "displayLoginForm");
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.fab_slide_in_from_right);

        getActivity().findViewById(R.id.title).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.back_button).setVisibility(View.VISIBLE);

        loginFragmentContainer.startAnimation(anim);
        loginFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void onLogin() {
        clearUserData();
        //hide keyboard
        KeyboardHelper.hideKeyboard(getActivity());

        loginButton.setEnabled(false);

        if (usernameInput.hasFocus()) {
            usernameInput.clearFocus();
        }

        if (passwordInput.hasFocus()) {
            passwordInput.clearFocus();
        }

        //validate typed credentials
        boolean isValidUserName = usernameInput.validateCustomEditText();
        boolean isValidPassword = passwordInput.validateCustomEditText();

        if (isValidUserName && isValidPassword) {

            LoginService loginService = new LoginService(this);
            loginService.performLogin(usernameInput.getText().toString(), passwordInput.getText().toString(),
                    rememberMeCheckbox.isChecked());

        } else {
            stopLoadingDialog();
        }
    }

    public void treatErrorCode(GeneralResponse response, String errorCode) {
        Log.d(TAG, "error codes format :" + ErrorCodes.API1_MISSING_USERNAME.getErrorCode());
        if (errorCode.equals(ErrorCodes.API1_MISSING_USERNAME.getErrorCode())) {
            usernameInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
            displayGeneralError(AppLabels.getError_login_invalid_username_or_password());
        } else if (errorCode.equals(ErrorCodes.API1_MISSING_PASSWORD.getErrorCode())) {

            passwordInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
            displayGeneralError(AppLabels.getError_login_invalid_username_or_password());
        } else if (errorCode.equals(ErrorCodes.API1_MISSING_REQUESTER_ID.getErrorCode())) {
            displayGeneralError(AppLabels.getError_login_invalid_username_or_password());
        } else if (errorCode.equals(ErrorCodes.API1_MISSING_REQUESTER_PASSWORD.getErrorCode())) {
            displayGeneralError(AppLabels.getError_login_invalid_username_or_password());
        } else if (errorCode.equals(ErrorCodes.API1_SYSTEM_NOT_AUTHORIZED.getErrorCode())) {
            displayGeneralError(AppLabels.getError_login_restricted_access());
        } else if (errorCode.equals(ErrorCodes.API1_AUTHORIZATION_FAILED.getErrorCode())) {
            usernameInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
            displayGeneralError(AppLabels.getErrorLOginAccountDisable());
        } else if (errorCode.equals(ErrorCodes.API1_INVALID_CREDENTIALS.getErrorCode())) {
            passwordInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
            displayGeneralError(AppLabels.getError_login_invalid_username_or_password());
        } else if (errorCode.equals(ErrorCodes.API1_LOCKED_ACCOUNT.getErrorCode())) {

            if (response.getTransactionFault().getFaultParam() != null && !response.getTransactionFault().getFaultParam().equals("")) {
                int lockedAccountTimeInMinutes = Integer.valueOf(response.getTransactionFault().getFaultParam()) / 60;
                displayGeneralError(AppLabels.getErrorLoginLimitsReached()
                        .replaceAll("placeholder", String.valueOf(lockedAccountTimeInMinutes)));
            } else {
                displayGeneralError(AppLabels.getErrorLoginLimitsReached());
            }
        } else if (errorCode.equals(ErrorCodes.API1_DISABLED_ACCOUNT.getErrorCode())) {
            displayGeneralError(AppLabels.getError_login_account_disabled());
        } else if (errorCode.equals(ErrorCodes.API1_INACTIVE_ACCOUNT.getErrorCode())) {
            displayGeneralError(AppLabels.getError_login_account_inactive_or_migrated());
        } else if (errorCode.equals(ErrorCodes.API1_NOT_ALLOWED_ACCOUNT.getErrorCode())) {
            usernameInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
            displayGeneralError(AppLabels.getError_login_restricted_access());
        }
    }

    @Override
    public void enterDashboard() {
        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);
    }

    @Override
    public void showCompleteProfile() {
        LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);

        if (loginActivity != null) {
            loginActivity.displayActivateAccountFragment();
        }
    }

    @Override
    public void showConfirmProfile() {
        Intent intent = new Intent(getActivity(), ConfirmProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void showIdentitySelector() {
        new NavigationAction(getContext()).startAction(IntentActionName.IDENTITY_SELECTOR);
    }

    @Override
    public void showRequestFailedMessage(Throwable e, String toastMessage, String apiKey) {
        try {
            stopLoadingDialog();
            if (apiKey.equals("API-10"))
                VodafoneController.setApi10Failed(true);

            new CustomToast.Builder(VodafoneController.getInstance()).message(toastMessage).success(false).show();

            if (e instanceof AuthenticationServiceException) {
                throw new AuthenticationServiceException(R.string.server_error, e);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void stopLoading() {
        stopLoadingDialog();
    }

    public void onRegister() {
        Log.d(TAG, "onRegister");
        Intent intent = new Intent(getActivity().getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void setRemeberMeStatus() {
        boolean isRememberMeTrue = true;
        try {
            isRememberMeTrue = VodafoneController.getInstance().getGeneralAppConfiguration().isKeepMeLoggedIn();
            Log.d(TAG, "isRememberMeTrue :" + isRememberMeTrue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        rememberMeCheckbox.setChecked(isRememberMeTrue);
    }

    public void clearUserData() {
        UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT).startAction();
    }

    private void trackEvent(String screenName, String eventName){
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, screenName);
        tealiumMapEvent.put(TealiumConstants.event_name, eventName);
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.LOGIN_PAGE);
    }
}
