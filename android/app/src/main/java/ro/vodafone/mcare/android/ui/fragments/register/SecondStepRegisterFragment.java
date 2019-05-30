package ro.vodafone.mcare.android.ui.fragments.register;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import ro.vodafone.mcare.android.rest.requests.SelfRegisterCreateRequest;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.settings.TermsAndConditionsActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;


public class SecondStepRegisterFragment extends BaseFragment implements OnErrorIconClickListener {

    public static String TAG = "SecondRegisterFragment";

    Context context;
    RegisterActivity activity;

    String msisdn;
    String customerType;
    String subscriberType;
    boolean isMigrated;

    String salutation;
    private Dialog updateExistingAccountOverlay;
    private String existingAccountUsername;

    @BindView(R.id.second_step_register_fragment_container)
    LinearLayout secondStepRegisterFragmentContainer;

    @BindView(R.id.usename_input)
    CustomEditTextCompat username_input;
    @BindView(R.id.first_name_input)
    CustomEditTextCompat first_name_input;
    @BindView(R.id.last_name_input)
    CustomEditTextCompat last_name_input;
    @BindView(R.id.email_address_input)
    CustomEditTextCompat email_address_input;
    @BindView(R.id.contact_phone_input)
    CustomEditTextCompat contact_phone_input;

    @BindView(R.id.invalid_phone_layout)
    TooltipError invalid_phone_layout;
    @BindView(R.id.invalid_first_name_layout)
    TooltipError invalid_first_name_layout;
    @BindView(R.id.invalid_last_name_layout)
    TooltipError invalid_last_name_layout;
    @BindView(R.id.invalid_email_layout)
    TooltipError invalid_email_layout;
    @BindView(R.id.invalid_contact_phone_layout)
    TooltipError invalid_contact_phone_layout;
    @BindView(R.id.invalid_terms_and_conditions_layout)
    TooltipError invalid_terms_and_conditions_layout;

    @BindView(R.id.terms_and_conditions_checkbox)
    CheckBox terms_and_conditions_checkbox;
    @BindView(R.id.news_checkbox)
    CheckBox news_checkbox;

    @BindView(R.id.terms_and_conditions_checkbox_label)
    TextView termsAndConditionsCheckBoxLabel;

    @BindView(R.id.header_title_tv)
    TextView headerTitleTv;

    @BindView(R.id.contact_phone_container)
    LinearLayout contactPhoneContainer;

    @BindView(R.id.third_register_step_button)
    VodafoneButton thirdRegisterStepButton;

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.first_radio_button_mr)
    RadioButton firstRadioButtonMr;

    @BindView(R.id.second_radio_button_mrs)
    RadioButton secondRadioButtonMrs;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!username_input.getText().toString().equals("") && (username_input.getText().toString().length() >= 4) && isAlphaNumeric(username_input.getText().toString())
                    && !first_name_input.getText().toString().equals("")
                    && !last_name_input.getText().toString().equals("")
                    && !email_address_input.getText().toString().equals("")
                    && isValidEmail(email_address_input.getText().toString())
                    && terms_and_conditions_checkbox.isChecked())

                if (contactPhoneContainer.getVisibility() == View.VISIBLE)

                    if (!contact_phone_input.getText().toString().equals("") && isValidPhoneNumber(contact_phone_input.getText().toString()))
                        enableButton(thirdRegisterStepButton);
                    else
                        disableButton(thirdRegisterStepButton);

                else
                    enableButton(thirdRegisterStepButton);

            else
                disableButton(thirdRegisterStepButton);
        }
    };


    View.OnClickListener thirdRegisterStepButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","register 2nd step");
            tealiumMapEvent.put("event_name","mcare:register 2nd step:button: continua");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            onThirdRegisterStep(false);
        }
    };

    public void init() {
        if(getActivity() == null) {
            return;
        }

        isMigrated = ((RegisterActivity)getActivity()).isIS_MIGRATED();
        msisdn = ((RegisterActivity)getActivity()).getMSISDN();
        customerType = ((RegisterActivity)getActivity()).getCUSTOMER_TYPE();
        subscriberType = ((RegisterActivity)getActivity()).getSUBSCRIBER_TYPE();

        Log.d(TAG, "MSISDN got from the First Step:" + msisdn);
        Log.d(TAG, "customerType got from the First Step:" + customerType);
        Log.d(TAG, "subscriberType got from the First Step:" + subscriberType);
        Log.d(TAG, "isMigrated got from the First Step:" + isMigrated);

        username_input.setText(((RegisterActivity)getActivity()).getMSISDN());

        displayRegisterForm();

        if (subscriberType != null)
            if (subscriberType.equals("TOBE") || subscriberType.equals("VMB"))
                contactPhoneContainer.setVisibility(View.VISIBLE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.fragment_second_step_register, null);

        context = getContext();
        activity = (RegisterActivity) getActivity();
        ButterKnife.bind(this, v);

        thirdRegisterStepButton.setOnClickListener(thirdRegisterStepButtonListner);
        disableButton(thirdRegisterStepButton);
        init();

        username_input.setText(msisdn);
        headerTitleTv.setText(RegisterLabels.getRegisterSecondStepPageTitleLabel());

        addTextWatchers();
        addFocusChangedListeners();
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        setupRadioButtonsLabels();
        username_input.setOnErrorIconClickListener(this);
        first_name_input.setOnErrorIconClickListener(this);
        email_address_input.setOnErrorIconClickListener(this);
        last_name_input.setOnErrorIconClickListener(this);
        contact_phone_input.setOnErrorIconClickListener(this);

        return v;

    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(firstRadioButtonMr.isChecked()){
                    secondRadioButtonMrs.setChecked(false);
                    salutation = firstRadioButtonMr.getTag().toString();
                } else if(secondRadioButtonMrs.isChecked()){
                    firstRadioButtonMr.setChecked(false);
                    salutation = secondRadioButtonMrs.getTag().toString();
                }
            Log.d(TAG, "onCheckedChanged: " + salutation);
        }
    };

    public boolean isAlphaNumeric(String username) {

        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN ="^[_A-Za-z0-9]+$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();
    }

    void addTextWatchers() {

        username_input.addTextChangedListener(textWatcher);
        first_name_input.addTextChangedListener(textWatcher);
        last_name_input.addTextChangedListener(textWatcher);
        email_address_input.addTextChangedListener(textWatcher);
        contact_phone_input.addTextChangedListener(textWatcher);

    }

    void addFocusChangedListeners() {

        username_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    ((TextView) invalid_phone_layout.findViewById(R.id.errorTextMessage)).setText("Numele de utilizator nu este disponibil.");
                    displayEditTextError(username_input, invalid_phone_layout, true);
                } else {
//                    D.e("we lost focus");
                    if(!username_input.getText().toString().isEmpty() && !isAlphaNumeric(username_input.getText().toString())){
                        displayEditTextError(username_input, invalid_phone_layout, false);
                        ((TextView) invalid_phone_layout.findViewById(R.id.errorTextMessage)).setText(RegisterLabels.getRegisterErrorMessageInvalidCharactersUsername());
                    } else if ((username_input.getText().toString().length() < 4)){
                        displayEditTextError(username_input, invalid_phone_layout, false);
                        ((TextView) invalid_phone_layout.findViewById(R.id.errorTextMessage)).setText(RegisterLabels.getRegisterErrorMessageMinCharacterUsername());
                    } else
                        displayEditTextError(username_input, invalid_phone_layout, true);
                }
            }
        });

        first_name_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                  ((TextView) invalid_first_name_layout.findViewById(R.id.errorTextMessage)).setText("Te rugăm să introduci prenumele tău.");
                    displayEditTextError(first_name_input, invalid_first_name_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (first_name_input.getText().toString().equals(""))
                      //  displayEditTextError(first_name_input, invalid_first_name_layout, false);
                        displayEditTextErrorWithoutTooltip(first_name_input, false);
                    else
                        displayEditTextError(first_name_input, invalid_first_name_layout, true);
                }
            }
        });

        last_name_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                   ((TextView) invalid_last_name_layout.findViewById(R.id.errorTextMessage)).setText("Te rugăm să introduci numele tău. ");
                    displayEditTextError(last_name_input, invalid_last_name_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (last_name_input.getText().toString().equals(""))
                   //     displayEditTextError(last_name_input, invalid_last_name_layout, false);
                        displayEditTextErrorWithoutTooltip(last_name_input, false);
                    else
                        displayEditTextError(last_name_input, invalid_last_name_layout, true);
                }
            }
        });

        email_address_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    ((TextView) invalid_email_layout.findViewById(R.id.errorTextMessage)).setText("Te rugăm să introduci un email valid.");
                    displayEditTextError(email_address_input, invalid_email_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (email_address_input.getText().toString().equals("") || !isValidEmail(email_address_input.getText().toString()))
                        displayEditTextError(email_address_input, invalid_email_layout, false);
                    else
                        displayEditTextError(email_address_input, invalid_email_layout, true);
                }
            }
        });

        contact_phone_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    ((TextView) invalid_contact_phone_layout.findViewById(R.id.errorTextMessage)).setText(RegisterLabels.getRegisterErrorMessageInvalidPhoneNumber());
                    displayEditTextError(contact_phone_input, invalid_contact_phone_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (contact_phone_input.getText().toString().equals("") || !isValidPhoneNumber(contact_phone_input.getText().toString()))
                        displayEditTextError(contact_phone_input, invalid_contact_phone_layout, false);
                    else
                        displayEditTextError(contact_phone_input, invalid_contact_phone_layout, true);
                }
            }
        });

        terms_and_conditions_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","register 2nd step");
                tealiumMapEvent.put("event_name","mcare:register 2nd step:link: T&C");
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                ///     D.w("acceptTC.isChecked() = " + acceptTC.isChecked());
                if (!username_input.getText().toString().equals("")
                        && !first_name_input.getText().toString().equals("")
                        && !last_name_input.getText().toString().equals("")
                        && !email_address_input.getText().toString().equals("")
                        && isValidEmail(email_address_input.getText().toString())
                        && terms_and_conditions_checkbox.isChecked())
                    if (contactPhoneContainer.getVisibility() == View.VISIBLE)
                        if (!contact_phone_input.getText().toString().equals("") && isValidPhoneNumber(contact_phone_input.getText().toString()))
                            enableButton(thirdRegisterStepButton);
                        else
                            disableButton(thirdRegisterStepButton);
                    else
                        enableButton(thirdRegisterStepButton);
                else
                    disableButton(thirdRegisterStepButton);

                if (!terms_and_conditions_checkbox.isChecked()) {
                    invalid_terms_and_conditions_layout.setVisibility(View.VISIBLE);
                    terms_and_conditions_checkbox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_error));
                    disableButton(thirdRegisterStepButton);
                } else {
                    terms_and_conditions_checkbox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_checked));
                    invalid_terms_and_conditions_layout.setVisibility(View.GONE);
                }
            }
        });

    }



    public boolean isValidEmail(String emailAdress) {
        return !TextUtils.isEmpty(emailAdress) && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(emailAdress).matches() && emailAdress.substring(emailAdress.lastIndexOf(".")).length() > 2;
    }

    public boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value)) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

                isValid = matcher.matches() && value.substring(0, 2).equals("07");
            } else
                isValid = false;
        }
        return isValid;
    }

    public void disableButton(Button button) {
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {
        button.setClickable(true);
        button.setEnabled(true);
    }

    public void displayError(LinearLayout badLinearLayout, String errorText) {

        badLinearLayout.setVisibility(View.VISIBLE);
        ((TextView) badLinearLayout.findViewById(R.id.errorTextMessage)).setText(errorText);

    }


    public void displayEditTextErrorWithoutTooltip(CustomEditTextCompat target, boolean visibility) {

        if (!target.hasFocus())
            if (!visibility)
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);

        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
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

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        SpannableString myString = new SpannableString(RegisterLabels.getRegisterInputTermsAndConditionLabel());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d(TAG, "Terms  Text Clicked ");
                Intent intent = new Intent(getActivity().getApplicationContext(), TermsAndConditionsActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, getString(R.string.terms_and_conditions_url));
                startActivity(intent);
            }
        };

        //For Click
        myString.setSpan(clickableSpan, 17, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //For UnderLine
        myString.setSpan(new UnderlineSpan(), 17, 39, 0);

        //For Bold
        //myString.setSpan(new StyleSpan(Typeface.BOLD),startIndex,lastIndex,0);

        termsAndConditionsCheckBoxLabel.setText(myString);
        termsAndConditionsCheckBoxLabel.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void displayRegisterForm() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.fab_slide_in_from_right);

        secondStepRegisterFragmentContainer.startAnimation(anim);
        secondStepRegisterFragmentContainer.setVisibility(View.VISIBLE);

        if(!isMigrated){
            radioGroup.setVisibility(View.GONE);
        } else {
            radioGroup.setVisibility(View.VISIBLE);
            salutation = ((RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getTag().toString();
            Log.d(TAG, "displayRegisterForm: salutation " + salutation);
        }
        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","register 2nd step");
        tealiumMapView.put("journey_name","register");
        TealiumHelper.trackView("screen_name", tealiumMapView);

        RegisterSecondStepTrackingEvent event = new RegisterSecondStepTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void onThirdRegisterStep(final boolean isExistingAccount) {
        Log.d(TAG, "onThirdRegisterStep");
        //hide keyboard
        KeyboardHelper.hideKeyboard(getActivity());
        showLoadingDialog();

        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.registerAccount(buildSelfRegisterCreateRequest(isExistingAccount))
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>() {

                    @Override
                    public void onNext(GeneralResponse response) {
                        super.onNext(response);
                        Log.d(TAG, "OnNext");
                        stopLoadingDialog();

                        Log.d(TAG, "TransactionStatus: " + response.getTransactionStatus());
                        if (response.getTransactionStatus() == 0) {
                            Log.d(TAG, "TransactionStatus 0 ");
                            //hideGeneralError();

                            ((RegisterActivity) getActivity()).setMSISDN(msisdn);
                            ((RegisterActivity) getActivity()).setCUSTOMER_TYPE(customerType);
                            ((RegisterActivity) getActivity()).setEMAIL(email_address_input.getText().toString());
                            ((RegisterActivity) getActivity()).setUSERNAME(isExistingAccount ? existingAccountUsername : username_input.getText().toString());

                            if(isExistingAccount && updateExistingAccountOverlay != null && updateExistingAccountOverlay.isShowing())
                                updateExistingAccountOverlay.dismiss();

                            activity.attachFragment(new ThirdStepRegisterFragment());

                        } else {
                            try {
                                String errorCode = response.getTransactionFault().getFaultCode();

                                if (errorCode.equals(ErrorCodes.API12_MANDATORY_FIELD.getErrorCode())) {
                                    //phoneInput.showErrorInputStyle(R.string.register_error_message_invalid_phone_number);
                                    //displayGeneralError(generalErrorLayout, errorMessage, userNameValidationResult);
                                } else if (errorCode.equals(ErrorCodes.API12_INVALID_EMAIL_FORMAT.getErrorCode())) {
//                                    displayError(invalid_email_layout, getString(R.string.register_error_message_ebu_user));
                                } else if (errorCode.equals(ErrorCodes.API12_INVALID_PHONE_FORMAT.getErrorCode())) {
//                                    displayError(invalid_contact_phone_layout, getString(R.string.register_error_message_invalid_phone_number));
                                } else if (errorCode.equals(ErrorCodes.API12_ILLEGAL_CHARACTERS_USERNAME.getErrorCode())) {
                                    //contact_phone_input.showErrorInputStyle(R.string.register_error_message_invalid_phone_number);
                                    //displayGeneralError(R.string.register_error_message_alredy_registered_account);
                                } else if (errorCode.equals(ErrorCodes.API12_USERNAME_ALREADY_EXISTS.getErrorCode())) {
                                    displayError(invalid_phone_layout, "Numele de utilizator nu este disponibil.");
                                    username_input.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);

                                    RegisterSecondStepTrackingEvent event = new RegisterSecondStepTrackingEvent();
                                    TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                    journey.event9 = "event9";
                                    journey.getContextData().put("event9", journey.event9);
                                    journey.event11 = "event11";
                                    journey.getContextData().put("event11", journey.event11);
                                    journey.prop16 = "prop16";
                                    journey.getContextData().put("prop16", journey.prop16);
                                    event.defineTrackingProperties(journey);
                                    VodafoneController.getInstance().getTrackingService().trackCustom(event);
                                } else if (errorCode.equals(ErrorCodes.API12_ACCOUNT_REGISTERED.getErrorCode())){
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterErrorMessageAccountExists()).success(false).show();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                }else if (errorCode.equals(ErrorCodes.API12_MULTIPLE_CONTACT_IDS.getErrorCode())){
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getActivateAccountErrorMultipleContacts()).success(false).show();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                }else if (errorCode.equals(ErrorCodes.API12_NO_CONTACT_ID_IN_CRM.getErrorCode())){
                                   new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterErrorMessageNoContactIds()).success(false).show();
                                   Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else if (errorCode.equals(ErrorCodes.API12_MORE_ACCOUNTS_WITH_SAME_EMAIL_AND_MSISDN.getErrorCode())){
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterErrorMessageNoContactIds()).success(false).show();
//                                    displayUserExistsWithRedirectMessage();
                                } else if (errorCode.equals(ErrorCodes.API12_SINGLE_ACCOUNT_SAME_MSISDN.getErrorCode())){
                                    displayUserExistsWithRedirectMessage();
                               } else if (errorCode.equals(ErrorCodes.API12_CONFIRM_UPDATE_ACCOUNT.getErrorCode())){
                                    existingAccountUsername = response.getTransactionFault() != null ? response.getTransactionFault().getFaultMessage() : null;
                                    displayConfirmProfileOverlay();
                                } else if (errorCode.equals(ErrorCodes.API12_GEONUM_AND_BLACKLISTED_EMAIL.getErrorCode())){
                                    new CustomToast.Builder(getContext()).message(RegisterLabels.getRegisterActivationCodeFailed()).success(false).show();
                                } else {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                    RegisterSecondStepTrackingEvent event = new RegisterSecondStepTrackingEvent();
                                    TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                    journey.event9 = "event9";
                                    journey.getContextData().put("event9", journey.event9);
                                    journey.event11 = "event11";
                                    journey.getContextData().put("event11", journey.event11);
                                    journey.prop16 = "prop16";
                                    journey.getContextData().put("prop16", journey.prop16);
                                    event.defineTrackingProperties(journey);
                                    VodafoneController.getInstance().getTrackingService().trackCustom(event);
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

                        if(updateExistingAccountOverlay != null && updateExistingAccountOverlay.isShowing())
                            updateExistingAccountOverlay.dismiss();

                        RegisterSecondStepTrackingEvent event = new RegisterSecondStepTrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event9 = "event9";
                        journey.getContextData().put("event9", journey.event9);
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

    private SelfRegisterCreateRequest buildSelfRegisterCreateRequest(boolean isExistingAccount){
        String username = isExistingAccount ? existingAccountUsername : username_input.getText().toString();

        SelfRegisterCreateRequest selfRegisterCreateRequest = new SelfRegisterCreateRequest();
        selfRegisterCreateRequest.setUsername(username);
        selfRegisterCreateRequest.setPhoneNumber(msisdn);
        selfRegisterCreateRequest.setFirstName(first_name_input.getText().toString());
        selfRegisterCreateRequest.setLastName(last_name_input.getText().toString());
        selfRegisterCreateRequest.setEmail(email_address_input.getText().toString());
        selfRegisterCreateRequest.setContactPhone(contact_phone_input.getText().toString());
        selfRegisterCreateRequest.setCustomerType(customerType);
        selfRegisterCreateRequest.setAcceptTerm(terms_and_conditions_checkbox.isChecked());
        selfRegisterCreateRequest.setNewsletter(news_checkbox.isChecked());
        selfRegisterCreateRequest.setSalutation(salutation);
        selfRegisterCreateRequest.setUpdateWithoutUsernameFlag(isExistingAccount);

        return selfRegisterCreateRequest;
    }

    private void displayUserExistsWithRedirectMessage(){

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("show_toast_message", true);
        startActivity(intent);
        getActivity().finish();
    }

    private void displayConfirmProfileOverlay(){
        updateExistingAccountOverlay= new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        updateExistingAccountOverlay.setContentView(R.layout.overlay_dialog_notifications);
        updateExistingAccountOverlay.show();

        VodafoneButton continueButton = (VodafoneButton) updateExistingAccountOverlay.findViewById(R.id.buttonKeepOn);
        VodafoneButton dismissButton = (VodafoneButton) updateExistingAccountOverlay.findViewById(R.id.buttonTurnOff);

        continueButton.setText(RegisterLabels.getRegisterContinueOverlayButton());
        dismissButton.setText(RegisterLabels.getRegisterCancelOverlayButton());

        VodafoneTextView continueRegisterOverLayTitle = (VodafoneTextView) updateExistingAccountOverlay.findViewById(R.id.overlayTitle);
        continueRegisterOverLayTitle.setText(RegisterLabels.getRegisterContinueOverlayTitle());

        VodafoneTextView continueRegisterOverlayFirstSubtext = (VodafoneTextView) updateExistingAccountOverlay.findViewById(R.id.overlaySubtext);
        continueRegisterOverlayFirstSubtext.setText(String.format(RegisterLabels.getRegisterContinueOverlayFirstSubtext(), existingAccountUsername));

        ImageView closeButton = (ImageView) updateExistingAccountOverlay.findViewById(R.id.overlayDismissButton);

        closeButton.setOnClickListener(cancelButtonClickListener);
        continueButton.setOnClickListener(continueButtonClickListener);
        dismissButton.setOnClickListener(cancelButtonClickListener);
    }

    View.OnClickListener continueButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onThirdRegisterStep(true);
        }
    };

    View.OnClickListener cancelButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateExistingAccountOverlay.dismiss();
        }
    };

    @Override
    public void onErrorIconClickListener() {
        if(username_input.isErrorIconTap())
            displayEditTextError(username_input, invalid_phone_layout, true);
        else if (first_name_input.isErrorIconTap())
            displayEditTextError(first_name_input, invalid_first_name_layout, true);
        else if (last_name_input.isErrorIconTap())
            displayEditTextError(last_name_input, invalid_first_name_layout, true);
        else if (email_address_input.isErrorIconTap())
            displayEditTextError(email_address_input, invalid_email_layout, true);
        else if (contact_phone_input.isErrorIconTap())
            displayEditTextError(contact_phone_input, invalid_contact_phone_layout, true);
    }

    private void setupRadioButtonsLabels(){
        firstRadioButtonMr.setText(RegisterLabels.getMigratedMaleRadioButtonLabel());
        secondRadioButtonMrs.setText(RegisterLabels.getMigratedFemaleRadioButtonLabel());
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.REGISTER_SECOND);
    }

    public static class RegisterSecondStepTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "register 2nd step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "register 2nd step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "register";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Registration";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "register 2nd step";
            s.getContextData().put("prop21", s.prop21);
        }
    }
}
