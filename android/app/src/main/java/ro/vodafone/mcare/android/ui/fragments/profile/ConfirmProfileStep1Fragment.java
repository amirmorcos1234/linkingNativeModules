package ro.vodafone.mcare.android.ui.fragments.profile;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RecoverLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.ConfirmProfileActivity;
import ro.vodafone.mcare.android.ui.activities.settings.TermsAndConditionsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Alex on 1/10/2017.
 */

public class ConfirmProfileStep1Fragment extends BaseFragment implements OnErrorIconClickListener {

    public static String TAG = "ConfirmProfile1Fragment";
    Context context;

    @BindView(R.id.confirm_profile_step1_fragment_container)
    LinearLayout confirmProfileStep1FragmentContainer;

    @BindView(R.id.invalid_email_layout)
    LinearLayout invalidEmailLayout;
    @BindView(R.id.invalid_phone_layout)
    LinearLayout invalidPhoneLayout;
    @BindView(R.id.invalid_checkbox_layout)
    LinearLayout invalidCheckLayout;

    @BindView(R.id.phone_input)
    CustomEditTextCompat phoneInput;
    @BindView(R.id.email_input)
    CustomEditTextCompat emailInput;

    @BindView(R.id.confirm_profile_second_step_button)
    Button confirmProfileSecondStepButton;
    @BindView(R.id.confirm_profile_dismiss_button)
    Button confirmProfileDismissButton;

    @BindView(R.id.more_text_button)
    TextView moreTextButton;

    @BindView(R.id.confirm_profile_email_error_message)
    VodafoneTextView invalidEmailMessageView;
    @BindView(R.id.errorTextMessage)
    VodafoneTextView invalidPhoneMessageView;

    @BindView(R.id.terms_and_conditions_checkbox)
    CheckBox termsAndConditionsCheckBox;
    @BindView(R.id.terms_and_conditions_checkbox_label)
    TextView termsAndConditionsCheckBoxLabel;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (isValidEmail(emailInput.getText().toString())
                    && isValidPhoneNumber(phoneInput.getText().toString())
                    && termsAndConditionsCheckBox.isChecked())
                enableButton(confirmProfileSecondStepButton);
            else
                disableButton(confirmProfileSecondStepButton);
        }
    };
    ConfirmProfileActivity activity;
    View.OnClickListener confirmProfileSecondStepButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","confirm profile 1st step");
            tealiumMapEvent.put("event_name","mcare:confirm profile 1st step:button: pas urmator");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            if (startsWith07(phoneInput.getText().toString()))
                onSecondConfirmProfileStep();
//                activity.attachFragment(new ConfirmProfileStep2Fragment());
            else
                redirectToProfileOverlay();
        }
    };
    View.OnClickListener confirmProfileDismissButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","confirm profile 1st step");
            tealiumMapEvent.put("event_name","mcare:confirm profile 1st step:button: renunta");
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);


            activity.loadLogOutDialog();
        }
    };

    public boolean isValidEmail(String emailAdress) {
        return !TextUtils.isEmpty(emailAdress) && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(emailAdress).matches() && emailAdress.substring(emailAdress.lastIndexOf(".")).length() > 2;
    }

    void redirectToProfileOverlay() {

        final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        overlayDialog.show();

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(AppLabels.getConfirmProfileOverlayTitle());
        overlaySubtext.setText(AppLabels.getConfirmProfileOverlayText());

        Button callNow = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        Button overlayCancelButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        overlayDismissButton.setVisibility(View.GONE);

        callNow.setText(AppLabels.getOverlayCallBtn());
        overlayCancelButton.setText(AppLabels.getOverlayRefuseBtn());

        overlayCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "*222"));
                    startActivity(intent);
                    activity.finish();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 101);
                }
            }
        });


//        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                overlayDialog.dismiss();
//            }
//        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlayDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "*222"));
                    startActivity(intent);
                    activity.finish();
                }
            }
        }
    }

    public boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

//        if (null != value && !"".equals(value)) {
//
//            if (value.length() == 10) {
//                Pattern pattern = Pattern.compile("^0[0-9]{9}");
//                Matcher matcher = pattern.matcher(value);
//
//                isValid = matcher.matches();
//            } else
//                isValid = false;
//        }

        if (null != value && !"".equals(value) && value.length() == 10)
            isValid = true;

        return isValid;
    }

    boolean startsWith07(String value) {
        return value.startsWith("07");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView ");
        View v = inflater.inflate(R.layout.confirm_profile_step1_fragment, null);
        ButterKnife.bind(this, v);
        context = getContext();
        activity = ((ConfirmProfileActivity) getActivity());
        activity.first_step_back_button.setVisibility(View.INVISIBLE);

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","confirm profile 1st step");
        tealiumMapView.put("journey_name","confirm profile");
        TealiumHelper.trackView("screen_name", tealiumMapView);

        termsAndConditionsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isValidEmail(emailInput.getText().toString()) && isValidPhoneNumber(phoneInput.getText().toString()) && termsAndConditionsCheckBox.isChecked()) {
                    invalidCheckLayout.setVisibility(View.GONE);
                    enableButton(confirmProfileSecondStepButton);
                } else
                    disableButton(confirmProfileSecondStepButton);


                if (!isValidEmail(emailInput.getText().toString())) {
                    disableButton(confirmProfileSecondStepButton);
//                    displayDefaultEditTextError(emailInput, invalidEmailLayout, false);
                }
                if (!isValidPhoneNumber(phoneInput.getText().toString())) {
                    disableButton(confirmProfileSecondStepButton);
//                    displayDefaultEditTextError(phoneInput, invalidPhoneLayout, false);
                }

                if (!termsAndConditionsCheckBox.isChecked()) {
                    invalidCheckLayout.setVisibility(View.VISIBLE);
                    termsAndConditionsCheckBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_error));
                    disableButton(confirmProfileSecondStepButton);
                } else {
                    termsAndConditionsCheckBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_checked));

                    invalidCheckLayout.setVisibility(View.GONE);
                }
            }
        });
        setupCheckboxLabel(v);

        disableButton(confirmProfileSecondStepButton);

        confirmProfileSecondStepButton.setOnClickListener(confirmProfileSecondStepButtonListner);
        confirmProfileDismissButton.setOnClickListener(confirmProfileDismissButtonListner);

        phoneInput.addTextChangedListener(textWatcher);
        emailInput.addTextChangedListener(textWatcher);
        phoneInput.setOnErrorIconClickListener(this);
        emailInput.setOnErrorIconClickListener(this);

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
        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(emailInput, invalidEmailLayout, true);
                } else {
//                    D.e("we lost focus");
                    if (emailInput.getText().toString().equals(""))
                        displayEditTextError(emailInput, invalidEmailLayout, false);
                    else if (!isValidEmail(emailInput.getText().toString()))
                        displayEditTextError(emailInput, invalidEmailLayout, false);
                    else
                        displayEditTextError(emailInput, invalidEmailLayout, true);
                }
            }
        });

        SpannableString myString = new SpannableString(ConfirmProfileLabels.getConfirmProfileFirstStepPageTittleLabel());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d(TAG, "More Text Clicked ");
                activity.loadTBDProfileInstructionDialog();
            }
        };


        myString.setSpan(clickableSpan, 64, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myString.setSpan(new UnderlineSpan(), 64, 73, 0);

        moreTextButton.setText(myString);
        moreTextButton.setMovementMethod(LinkMovementMethod.getInstance());

        displayConfirmProfileForm();
        return v;

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

    public void displayConfirmProfileForm() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.fab_slide_in_from_right);
        confirmProfileStep1FragmentContainer.startAnimation(anim);
        confirmProfileStep1FragmentContainer.setVisibility(View.VISIBLE);
    }

    public void displayError(LinearLayout badLinearLayout, VodafoneTextView errorTextView, String errorText) {

        badLinearLayout.setVisibility(View.VISIBLE);
        errorTextView.setText(errorText);
    }

    private void onSecondConfirmProfileStep() {

        showLoadingDialog();
        AuthenticationService authenticationService = new AuthenticationService(getContext());

        final User user = VodafoneController.getInstance().getUser();

        authenticationService.confirmProfile(phoneInput.getText().toString(), emailInput.getText().toString())
                .subscribe(new RequestSessionObserver<GeneralResponse>() {

                    @Override
                    public void onNext(GeneralResponse response) {

                        stopLoadingDialog();

                        if (response.getTransactionStatus() == 0) {

                            UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
                            RealmManager.startTransaction();

                            userProfile.setMsisdn(phoneInput.getText().toString());
                            userProfile.setEmail(emailInput.getText().toString());

                            RealmManager.update(userProfile);
                            RealmManager.disconnect();

                            activity.attachFragment(new ConfirmProfileStep2Fragment());
                        } else {
                            String errorCode = response.getTransactionFault().getFaultCode();

                            if (errorCode.equals(ErrorCodes.API17_CONTACT_CUSTOMER_SERVICES_ERROR.getErrorCode())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, ConfirmProfileLabels.getConfirmProfilePleaseContactAdministrator());
                            }else if (errorCode.equals(ErrorCodes.API17_EMAIL_NOT_MATCH_ERROR.getErrorCode())) {
                                displayError(invalidEmailLayout, invalidEmailMessageView, ConfirmProfileLabels.getConfirmProfileEnteredEmailNotEqualWithProfilMail());
                            } else if (errorCode.equals(ErrorCodes.API17_CONTACT_ACCOUNT_ADMIN_ERROR.getErrorCode())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, ConfirmProfileLabels.getConfirmProfilePleaseContactAdministrator());//aaa
                            } else if (errorCode.equals(ErrorCodes.API17_ACCOUNT_IS_ALREADY_REGISTERED_ERROR.getErrorCode())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, ConfirmProfileLabels.getConfirmProfileAccountAlreadyExistWithThisNumber());
                            } else if (errorCode.equals(ErrorCodes.API17_UNABLE_TO_UPDATE_PROFILE_ERROR.getErrorCode())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, ConfirmProfileLabels.getConfirmProfilePleaseContactClientRealitonalService());//aaa
                            } else if (errorCode.equals(ErrorCodes.API17_VALIDATION_ERROR.getErrorCode())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, RegisterLabels.getRegisterErrorMessageInvalidPhoneNumber());//aaa
                            } else if (!user.getUserProfile().getMsisdn().trim().equals("4" + phoneInput.getText().toString().trim())) {
                                displayError(invalidPhoneLayout, invalidPhoneMessageView, RegisterLabels.getRegisterErrorMessageInvalidPhoneNumber());
                            } else {
                                new CustomToast.Builder(getContext()).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
//                                new CustomToast(getActivity(), getContext(), getResources().getString(R.string.login_failed_api_call), false).show();
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();

                        ConfirmProfileActivity.ConfirmProfileStep1TrackingEvent event = new ConfirmProfileActivity.ConfirmProfileStep1TrackingEvent();
                        TrackingAppMeasurement journey = new TrackingAppMeasurement();
                        journey.event9 = "event9";
                        journey.getContextData().put("event9", journey.event9);
                        event.defineTrackingProperties(journey);
                        VodafoneController.getInstance().getTrackingService().trackCustom(event);

                        displayToastError(RecoverLabels.getRecoverUsernameFailedApiCall());
                    }

                });

    }

    private void displayToastError(String message) {
        new CustomToast.Builder(getContext()).message(message).success(false).show();
//        new CustomToast(getActivity(), getContext(), message, false).show();
    }

    private void setupCheckboxLabel(View view) {
        termsAndConditionsCheckBoxLabel = (TextView) view.findViewById(R.id.terms_and_conditions_checkbox_label);

        SpannableString myString = new SpannableString(ConfirmProfileLabels.getConfirmProfileTermsAndConditionsCheckboxLabel()); //getResources().getString(R.string.register_input_terms_and_conditions_label)

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

    @Override
    public void onErrorIconClickListener() {
        if(emailInput.isErrorIconTap())
            displayEditTextError(emailInput, invalidEmailLayout, true);
        else if (phoneInput.isErrorIconTap())
            displayEditTextError(phoneInput, invalidPhoneLayout, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.CONFIRM_FIRST);
    }


}
