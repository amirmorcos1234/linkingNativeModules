package ro.vodafone.mcare.android.ui.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.RecoverLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.custom.ProgressBarHandler;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.ConfirmProfileActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.settings.TermsAndConditionsActivity;
import ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;


public class ActivateAccountFragment extends BaseFragment implements InputEventsListenerInterface {

    public static String TAG = "ActivateAccountFragment";

    LinearLayout passwordErrorLayout;
    LinearLayout confirmPasswordErrorLayout;
    LinearLayout emailErrorLayout;


    VodafoneTextView passwordErrorMessage;
    VodafoneTextView confirmPasswordErrorMessage;
    VodafoneTextView emailErrorMessage;

    VodafoneTextView termsAndConditionsLabel;

    CustomEditText passwordInput;
    CustomEditText confirmPasswordInput;
    CustomEditText emailInput;

    VodafoneButton activateAccountButton;
    VodafoneButton dismissButton;

    //VodafoneTextView buttonLabel;

    CheckBox termsAndConditionsCheckBox;

    private boolean isActiveActivateAccountButton = false;
    private boolean isDisplayErrorMessage = false;
    private boolean isUserActivated = false;

    ProgressBarHandler progressBarHandler;

    SpannableString myString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_activate_account, null);

        progressBarHandler = new ProgressBarHandler(getContext());

        passwordErrorLayout = (LinearLayout) v.findViewById(R.id.new_password_error_layout);
        confirmPasswordErrorLayout = (LinearLayout) v.findViewById(R.id.confirm_password_error_layout);
        emailErrorLayout = (LinearLayout) v.findViewById(R.id.email_error_layout);

        passwordErrorMessage = (VodafoneTextView) v.findViewById(R.id.new_password_error_message);
        confirmPasswordErrorMessage = (VodafoneTextView) v.findViewById(R.id.confirm_account_error_message);
        emailErrorMessage = (VodafoneTextView) v.findViewById(R.id.email_error_message);

        passwordInput = (CustomEditText) v.findViewById(R.id.new_password_input);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput  = (CustomEditText) v.findViewById(R.id.confirm_password_input);
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setNewPasswordInput(passwordInput);

        emailInput  = (CustomEditText) v.findViewById(R.id.email_adress);

        if(VodafoneController.getInstance().getUser() != null) {
            User user = VodafoneController.getInstance().getUser();
            if (user != null && user.getUserProfile() != null && user.getUserProfile().getEmail() != null) {
                emailInput.setText(user.getUserProfile().getEmail());
            }
        }

        //buttonLabel = (VodafoneTextView) v.findViewById(R.id.activate_account_button_label);

        termsAndConditionsLabel = (VodafoneTextView) v.findViewById(R.id.terms_and_conditions_button);
        clicableErrorMessage();

        termsAndConditionsCheckBox = (CheckBox) v.findViewById(R.id.terms_and_conditions_checkbox);

        activateAccountButton = (VodafoneButton) v.findViewById(R.id.activate_account_button);
        //activateAccountButton.setEnabled(false);

        dismissButton = (VodafoneButton) v.findViewById(R.id.dismiss_button);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        activateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeProfile();
            }
        });

        termsAndConditionsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked){
                   // hideErrorMessage();
                    activateButton();
                    termsAndConditionsCheckBox.setButtonDrawable(R.drawable.checkbox_checked);
                }else{
                    termsAndConditionsCheckBox.setButtonDrawable(R.drawable.checkbox_error);
                    inactivateButton();
                }
            }
        });

        watchEdiTexts();

        return v;
    }

    private void watchEdiTexts(){
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())
                        && confirmPasswordInput.validateCustomEditText()){
                    passwordErrorLayout.setVisibility(View.GONE);
                    confirmPasswordInput.removeHighlight();
                }
            }
        });
    }

    @Override
    public void displayErrorMessage() {
        Log.d(TAG,"displayErrorMessage");

        if (passwordInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && passwordInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            passwordErrorLayout.setVisibility(View.VISIBLE);
            passwordErrorMessage.setText(R.string.activate_account_invalid_password);
            if (!passwordInput.hasFocus()) {
                passwordInput.showErrorInputStyle(R.string.activate_account_invalid_password, false);
            }
        }

        if (confirmPasswordInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS ) {
            confirmPasswordErrorLayout.setVisibility(View.VISIBLE);
            confirmPasswordErrorMessage.setText(R.string.activate_account_password_do_not_coincide);
            if (!confirmPasswordInput.hasFocus()) {
                confirmPasswordInput.showErrorInputStyle(R.string.activate_account_invalid_password, false);
            }
        }

        if (emailInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && emailInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            emailErrorLayout.setVisibility(View.VISIBLE);
            emailErrorMessage.setText(R.string.activate_account_invalid_email_format);
            if (!emailInput.hasFocus()) {
                emailInput.showErrorInputStyle(R.string.activate_account_invalid_password, false);
            }

        }
    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG,"hideErrorMessage");

        if(passwordInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && passwordInput.isHighlighted()){
            passwordInput.removeHighlight();
        }

        if(confirmPasswordInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && confirmPasswordInput.isHighlighted()){
            confirmPasswordInput.removeHighlight();
        }

        if(emailInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailInput.isHighlighted()){
            emailInput.removeHighlight();
        }

        if(passwordInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || passwordInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            passwordErrorLayout.setVisibility(View.GONE);
        }

        if(confirmPasswordInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || confirmPasswordInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            confirmPasswordErrorLayout.setVisibility(View.GONE);
        }

        if(emailInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS){
            emailErrorLayout.setVisibility(View.GONE);
        }

        activateButton();
    }

    @Override
    public void activateButton() {
        Log.d(TAG,"activateButton");
        if(!passwordInput.isEmpty()  && !passwordInput.isHighlighted()
                && !confirmPasswordInput.isEmpty() && !confirmPasswordInput.isHighlighted()
                && !emailInput.isEmpty() && !emailInput.isHighlighted() && termsAndConditionsCheckBox.isChecked()){
            activateAccountButton.setEnabled(true);
        }
    }

    @Override
    public void inactivateButton() {
        Log.d(TAG,"inactivateButton");
        activateAccountButton.setEnabled(false);
    }

    private void completeProfile(){
        Log.d(TAG,"activateUserAccount");
        //progressBarHandler.show(false, R.string.loading_text);
        showLoadingDialog();
        AuthenticationService authenticationService = new AuthenticationService(getContext());

        boolean isValidEmail = emailInput.validateCustomEditText();
        boolean isValidPassword = passwordInput.validateCustomEditText();
        boolean isValidConfirmPassword = confirmPasswordInput.validateCustomEditText();

        if(isValidEmail && isValidPassword && isValidConfirmPassword && isTermsAndConditionChecked()){

            authenticationService.completeProfile(emailInput.getText().toString(), passwordInput.getText().toString(),
                    confirmPasswordInput.getText().toString(), termsAndConditionsCheckBox.isChecked())
                    .subscribe(new RequestSessionObserver<GeneralResponse>() {
                        @Override
                        public void onNext(GeneralResponse response) {
                            stopLoadingDialog();

                            if(response.getTransactionStatus() == 0){

                                isUserActivated = true;

                                //Save tracking info for reporting
                                sendSeamlessEvent();

                                //Update user status
                                RealmManager.startTransaction();

                                UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
                                userProfile.setUserStatus("active");

                                RealmManager.update(userProfile);

                                if(isInconsistentProfile(userProfile)){
                                    //Navigate to confirmProfile flow
                                    Intent intent = new Intent(getActivity(), ConfirmProfileActivity.class);
                                    startActivity(intent);
                                }else{
                                    User user = VodafoneController.getInstance().getUser();

                                    if (user instanceof EbuMigrated) {
                                        stopLoadingDialog();
                                        setupIdentitySelector();
                                    }else{
                                        //Navigate to dashboard
                                        new CustomToast.Builder(getContext()).message(RecoverLabels.getRecoverPasswordAccountSuccessfullyActivated()).success(false).show();
                                        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);
                                    }
                                }
                            }else{
                                if(response.getTransactionFault().getFaultCode().equals(ErrorCodes.API18_VALIDATION_ERROR.getErrorCode())){
                                    emailErrorLayout.setVisibility(View.VISIBLE);
                                    emailErrorMessage.setText(R.string.activate_account_email_blacklisted);
                                } else
                                    new CustomToast.Builder(getContext()).message(R.string.activate_account_failed_api_call).success(false).show();

                                LoginActivity.LoginTrackingEvent event = new LoginActivity.LoginTrackingEvent();
                                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                journey.event11 = "event11";
                                journey.getContextData().put("event11", journey.event11);
                                journey.prop16 = "prop16";
                                journey.getContextData().put("prop16", journey.prop16);
                                event.defineTrackingProperties(journey);
                                VodafoneController.getInstance().getTrackingService().trackCustom(event);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            stopLoadingDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            stopLoadingDialog();
                            if(getContext() != null) {
                                new NavigationAction(getContext()).startAction(IntentActionName.LOGOUT, true);
                            }
                        }
                    });
        }else{
            stopLoadingDialog();
        }
    }


    private void sendSeamlessEvent(){

        AuthenticationService authenticationService = new AuthenticationService(getContext());
        //TODO may need to change the parameter value
        authenticationService.sendSeamlessEvent("menu_login")
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>() {

                    @Override
                    public void onNext(GeneralResponse generalResponse) {
                        Log.d(TAG, "sendSeamlessEvent onNext()");
                        super.onNext(generalResponse);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "sendSeamlessEvent - onError");
                    }
                });
    }


    private boolean isInconsistentProfile(UserProfile userProfile){
        boolean isInconsistent = false;

        if(userProfile != null){
            if(userProfile.getCid() == null && userProfile.getSid() == null &&
                    (userProfile.getUserRole() == UserRole.PREPAID || userProfile.getUserRole() == UserRole.RES_CORP
                            || userProfile.getUserRole() == UserRole.RES_SUB || userProfile.getUserRole() == UserRole.PRIVATE_USER)){
                isInconsistent = true;
            }
        }
        return isInconsistent;
    }

    public  void clicableErrorMessage(){
        Log.d(TAG, "Error text Clicked()");

        myString = new SpannableString( getResources().getString(R.string.activate_account_accept_terms_and_conditions_label));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TermsAndConditionsActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, getString(R.string.terms_and_conditions_url));
                startActivity(intent);
            }
        };

        //For Click
        myString.setSpan(clickableSpan,17,myString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsAndConditionsLabel.setText(myString);
        termsAndConditionsLabel.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private boolean isTermsAndConditionChecked(){
        boolean isChecked = false;

        if(termsAndConditionsCheckBox.isChecked()){
            isChecked = true;
        }else{
            termsAndConditionsCheckBox.setBackgroundResource(R.drawable.terms_and_conditions_checkbox_selector);
        }

        return isChecked;
    }

    private void setupIdentitySelector() {
        Log.d(TAG, "ebu migrated user - start flow");
        Intent intent = new Intent(getActivity(), LoginIdentitySelectorActivity.class);
        startActivity(intent);
    }

}
