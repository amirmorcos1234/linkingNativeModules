package ro.vodafone.mcare.android.ui.fragments.register;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.client.model.register.AccountCheck;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.RecoverPasswordActivity;
import ro.vodafone.mcare.android.ui.activities.RecoverUsernameActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.CallStateListener;
import ro.vodafone.mcare.android.utils.TealiumHelper;


public class FirstStepRegisterFragment extends BaseFragment implements OnErrorIconClickListener {

    public static String TAG = "FirstRegisterFragment";

    Context context;
    RegisterActivity activity;
    SpannableString myString;
    Dialog overlayDialog;

    @BindView(R.id.phone_input)
    CustomEditTextCompat phoneInput;
    @BindView(R.id.phone_number_error_layout)
    TooltipError invalidPhoneLayout;
    View.OnClickListener secondRegisterStepButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "register 1st step");
            tealiumMapEvent.put("event_name", "mcare:register 1st step:button: continua");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);
            secondRegisterStepButton.requestFocus();
            onSecondRegisterStep();
        }
    };
    @BindView(R.id.first_step_register_fragment_container)
    LinearLayout firstStepRegisterFragmentContainer;
    @BindView(R.id.second_register_step_button)
    VodafoneButton secondRegisterStepButton;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (invalidPhoneLayout.getVisibility() == View.VISIBLE) {
                invalidPhoneLayout.setVisibility(View.GONE);
                phoneInput.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
            }
            if (phoneInput.getText().toString().length() > 0)
                enableButton(secondRegisterStepButton);
            else
                disableButton(secondRegisterStepButton);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.first_fragment_register, null);
        activity = ((RegisterActivity) getActivity());
        context = getContext();
        ButterKnife.bind(this, v);

        displayRegisterForm();

        secondRegisterStepButton.setOnClickListener(secondRegisterStepButtonListner);
        disableButton(secondRegisterStepButton);

        phoneInput.addTextChangedListener(textWatcher);
        phoneInput.setOnErrorIconClickListener(this);

        phoneInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(phoneInput, invalidPhoneLayout, true);
                } else {
//                    D.e("we lost focus");
                    if (phoneInput.getText().toString().equals(""))
                        displayEditTextError(phoneInput, invalidPhoneLayout, false);
                    else if (!isValidPhoneNumber(phoneInput.getText().toString()))
                        displayEditTextError(phoneInput, invalidPhoneLayout, false);
                    else
                        displayEditTextError(phoneInput, invalidPhoneLayout, true);
                }
            }
        });

        return v;
    }

    public boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value)) {
            if (value.length() == 10)          //VNM-7258            VF - Androit - Self Register - 'Pasul urmator' button not enabled after one character is entered
                return true;
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

    public void displayEditTextError(CustomEditTextCompat target, TooltipError errorLayout, boolean visibility) {

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

    public void displayError(TooltipError badLinearLayout, String errorText) {
        badLinearLayout.setVisibility(View.VISIBLE);
        ((TextView) badLinearLayout.findViewById(R.id.errorTextMessage)).setText(errorText);
        phoneInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
    }

    public void displayErrorAccountAlreadyExists(TooltipError badLinearLayout) {

        myString = new SpannableString(RegisterLabels.getRegisterErrorMessageAlredyRegisteredAccount());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d(TAG, "Password Clicked");
                Intent intent = new Intent(getActivity().getApplicationContext(), RecoverPasswordActivity.class);
                startActivity(intent);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d(TAG, "Username Clicked");
                Intent intent = new Intent(getActivity().getApplicationContext(), RecoverUsernameActivity.class);
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

        badLinearLayout.setVisibility(View.VISIBLE);
        ((TextView) badLinearLayout.findViewById(R.id.errorTextMessage)).setText(myString);
        ((TextView) badLinearLayout.findViewById(R.id.errorTextMessage)).setMovementMethod(LinkMovementMethod.getInstance());
        phoneInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
    }

    public void displayErrorWithLink(TooltipError badLinearLayout, String errorText) {
        SpannableString message = new SpannableString(errorText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getActivity().getApplicationContext(), WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, RegisterLabels.getRegisterLink());
                startActivity(intent);
            }
        };

        message.setSpan(clickableSpan, 67, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setSpan(new UnderlineSpan(), 67, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        badLinearLayout.setVisibility(View.VISIBLE);

        ((TextView) invalidPhoneLayout.findViewById(R.id.errorTextMessage)).setText(message);
        ((TextView) invalidPhoneLayout.findViewById(R.id.errorTextMessage)).setMovementMethod(LinkMovementMethod.getInstance());
        phoneInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
    }

    public void displayRegisterForm() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.fab_slide_in_from_right);
        firstStepRegisterFragmentContainer.startAnimation(anim);
        firstStepRegisterFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void onSecondRegisterStep() {
        if (phoneInput.getText().toString().startsWith("0") && phoneInput.getText().toString().length() == 10)
            onSecondStep();
        else {
            displayError(invalidPhoneLayout, RegisterLabels.getRegisterErrorMessageInvalidPhoneNumber());
            disableButton(secondRegisterStepButton);
        }
    }

    public void onSecondStep() {
        //hide keyboard
        KeyboardHelper.hideKeyboard(getActivity());
        showLoadingDialog();

        AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
        authenticationService.registerAccountCheck(phoneInput.getText().toString())
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<AccountCheck>>(false) {

                    public void onNext(GeneralResponse<AccountCheck> response) {
                        super.onNext(response);
                        stopLoadingDialog();

                        if (response.getTransactionStatus() == 0) {

                            if(getActivity() != null) {
                                ((RegisterActivity) getActivity()).setMSISDN(phoneInput.getText().toString());
                                ((RegisterActivity) getActivity()).setCUSTOMER_TYPE(response.getTransactionSuccess().getCustomerType());
                                ((RegisterActivity) getActivity()).setSUBSCRIBER_TYPE(response.getTransactionSuccess().getSubscriberType());
                                ((RegisterActivity) getActivity()).setIS_MIGRATED(response.getTransactionSuccess().isMigrated());
                            }
                            activity.attachFragment(new SecondStepRegisterFragment());
                        } else {
                            try {
                                String errorCode = response.getTransactionFault().getFaultCode();

                                RegisterActivity.RegisterActivityTrackingEvent event = new RegisterActivity.RegisterActivityTrackingEvent();
                                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                                journey.event11 = "event11";
                                journey.getContextData().put("event11", journey.event11);
                                journey.prop16 = "prop16";
                                journey.getContextData().put("prop16", journey.prop16);
                                event.defineTrackingProperties(journey);
                                VodafoneController.getInstance().getTrackingService().trackCustom(event);

                                Log.d(TAG, "error codes format :" + ErrorCodes.API1_MISSING_USERNAME.getErrorCode());
                                if (errorCode.equals(ErrorCodes.API11_INVALID_MSISDN.getErrorCode())) {
                                    displayError(invalidPhoneLayout, RegisterLabels.getRegisterErrorMessageInvalidPhoneNumber());
                                    disableButton(secondRegisterStepButton);
                                } else if (errorCode.equals(ErrorCodes.API11_EBU_USER.getErrorCode())) {
                                    displayErrorWithLink(invalidPhoneLayout, RegisterLabels.getRegisterErrorMessageEbuUser());
                                    journey.event9 = "event9";
                                    journey.getContextData().put("event9", journey.event9);
                                    event.defineTrackingProperties(journey);
                                    VodafoneController.getInstance().getTrackingService().trackCustom(event);
                                    disableButton(secondRegisterStepButton);

                                } else if (errorCode.equals(ErrorCodes.API11_ACCOUNT_IS_ALREADY_REGISTERED.getErrorCode())) {
                                    displayErrorAccountAlreadyExists(invalidPhoneLayout);
                                    journey.event9 = "event9";
                                    journey.getContextData().put("event9", journey.event9);
                                    event.defineTrackingProperties(journey);
                                    VodafoneController.getInstance().getTrackingService().trackCustom(event);
                                    disableButton(secondRegisterStepButton);
                                } else {
                                    new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                    journey.event9 = "event9";
                                    journey.getContextData().put("event9", journey.event9);
                                    event.defineTrackingProperties(journey);
                                    VodafoneController.getInstance().getTrackingService().trackCustom(event);

                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Some exceptions occurred" + e);
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

                            RegisterActivity.RegisterActivityTrackingEvent event = new RegisterActivity.RegisterActivityTrackingEvent();
                            TrackingAppMeasurement journey = new TrackingAppMeasurement();
                            journey.event9 = "event9";
                            journey.getContextData().put("event9", journey.event9);
                            journey.event11 = "event11";
                            journey.getContextData().put("event11", journey.event11);
                            journey.prop16 = "prop16";
                            journey.getContextData().put("prop16", journey.prop16);
                            event.defineTrackingProperties(journey);
                            VodafoneController.getInstance().getTrackingService().trackCustom(event);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCallStateListener();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "*222"));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayDialog != null && overlayDialog.isShowing())
            overlayDialog.dismiss();
    }

    private void setCallStateListener() {
        CallStateListener endCallListener = new CallStateListener(getActivity());
        TelephonyManager tlM = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (tlM != null) {
            Log.d(TAG, "onClick: listener");
            tlM.listen(endCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public void onErrorIconClickListener() {
        if (phoneInput.isErrorIconTap())
            displayEditTextError(phoneInput, invalidPhoneLayout, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.REGISTER_FIRST);
    }
}