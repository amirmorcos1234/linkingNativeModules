package ro.vodafone.mcare.android.ui.fragments.recover;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.RecoverLabels;
import ro.vodafone.mcare.android.client.model.recover.RecoverPasswordCorrectResponse;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.RecoverPasswordRequest;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.RecoverPasswordActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by user on 11.01.2017.
 */
public class RecoverPasswordFragmentFirstStep extends BaseFragment implements OnErrorIconClickListener {

    public static String TAG = "RecoverPassFirstStep";

    @BindView(R.id.phone_number_error_layout)
    TooltipError phone_number_error_layout;
    @BindView(R.id.email_error_layout)
    TooltipError email_error_layout;
    @BindView(R.id.username_error_layout)
    TooltipError username_error_layout;
    @BindView(R.id.page_description)
    VodafoneTextView page_description;

    @BindView(R.id.telephone_number_input)
    CustomEditTextCompat telephone_number_input;
    @BindView(R.id.email_adress_input)
    CustomEditTextCompat email_adress_input;
    @BindView(R.id.username_input)
    CustomEditTextCompat username_input;

    @BindView(R.id.send_username_button)
    VodafoneButton send_username_button;

    @BindView(R.id.stepA_view)
    LinearLayout stepA_view;
    @BindView(R.id.stepB_view)
    LinearLayout stepB_view;

    @BindView(R.id.recover_password_container)
    View recover_password_container;

    Animation animationStepARight;
    Animation animationStepBRight;
    Animation animationStepALeft;
    Animation animationStepBLeft;
    boolean unhandledError = false;
    boolean telephoneNumberFirstTimeFocus = false;

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

    View.OnClickListener newAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            D.d("READ CLICK");
            Intent intent = new Intent(getActivity().getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animationStepARight = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_in_from_right);
        animationStepBRight = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_out_to_right);
        animationStepALeft = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_in_from_left);
        animationStepBLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_out_to_left);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recover_password_first_step, null);
        ButterKnife.bind(this, v);

        initTracking();
        initViews();

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "*222"));
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RecoverPasswordActivity) getActivity()).setBackButtonVisibility(true);
    }

    void initViews() {

        ((RecoverPasswordActivity) getActivity()).setBackButtonVisibility(true);

        page_description.setText(RecoverLabels.getRecoverPasswordPageDescriptionStepA());

        telephone_number_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.d("we got focus ON telephone_number_input");
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true);
                } else {
                    checkIfValid(1);
                }
            }
        });

        email_adress_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
//                    D.d("we got focus ON email_adress_input");
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
                    firstTimeFocus(telephoneNumberFirstTimeFocus);
                } else {
                    checkIfValid(2);
                }
            }
        });

        username_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
//                    D.d("we got focus ON username_input");
                    displayCustomEditTextError(username_input, username_error_layout, "Nume de utilizator invalid!", true);
                } else {
                    checkIfValid(3);
                }
            }
        });

        telephone_number_input.addTextChangedListener(textWatcher);
        telephone_number_input.setOnErrorIconClickListener(this);
        email_adress_input.addTextChangedListener(textWatcher);
        email_adress_input.setOnErrorIconClickListener(this);
        username_input.setOnErrorIconClickListener(this);
        username_input.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (username_error_layout.getVisibility() == View.VISIBLE) {
                    username_error_layout.setVisibility(View.GONE);
                    username_input.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
                }
                if (!username_input.getText().toString().equalsIgnoreCase("")
                        || !username_input.getText().toString().replaceAll(" ", "").equalsIgnoreCase(""))
                    enableButtonStepB();
                else
                    disableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void firstTimeFocus(boolean telephoneNumber) {
        if (!telephoneNumber) {
            checkIfValid(1);
            telephoneNumberFirstTimeFocus = true;
        }
    }

    public void checkIfValid(int view) {
        switch (view) {
            case 1:
                if (telephone_number_input.getText().toString().equals("")
                        || !isValidPhoneNumber(telephone_number_input.getText().toString()))
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), false);
                else
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true);
                break;
            case 2:
                if (email_adress_input.getText().toString().equals(""))
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), false);
                else if (!isValidEmail(email_adress_input.getText().toString()))
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), false);
                else
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
                break;
            case 3:
                if (username_input.getText().toString().equals(""))
                    displayCustomEditTextError(username_input, username_error_layout, "Nume de utilizator invalid!", false);
                else if (!isValidEmail(username_input.getText().toString()))
                    displayCustomEditTextError(username_input, username_error_layout, "Nume de utilizator invalid!", false);
                else
                    displayCustomEditTextError(username_input, username_error_layout, "Nume de utilizator invalid!", true);
                break;
            default:
                break;
        }
    }

    boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value)) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

//                isValid = matcher.matches() && value.startsWith("0");
                isValid = matcher.matches();
            } else
                isValid = false;
        }
        return isValid;
    }

    boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email)
                && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(email).matches()
        );
    }

    boolean validFields() {
        return !telephone_number_input.getText().toString().equals("")
                && isValidPhoneNumber(telephone_number_input.getText().toString())
                && !email_adress_input.getText().toString().equals("")
                && isValidEmail(email_adress_input.getText().toString());
    }

    void disableButton() {
        send_username_button.setClickable(false);
        send_username_button.setEnabled(false);
    }

    void enableButton() {
        send_username_button.setClickable(true);
        send_username_button.setEnabled(true);
        send_username_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonCLick();
            }
        });
    }

    void enableButtonStepB() {
        send_username_button.setClickable(true);
        send_username_button.setEnabled(true);
        send_username_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonStepBCLick();
            }
        });
    }

    void onButtonCLick() {
        email_error_layout.setVisibility(View.GONE);
        phone_number_error_layout.setVisibility(View.GONE);
        if (telephone_number_input.getText().toString().startsWith("0"))
            recoverPasswordCall(false);
    }

    void onButtonStepBCLick() {
        username_error_layout.setVisibility(View.GONE);
        recoverPasswordCall(true);
    }

    void displayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility) {

        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
                ((TextView) errorLayout.findViewById(R.id.errorTextMessage)).setText(errorText);
            } else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
    }

    void forceDisplayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText) {
        errorLayout.setVisibility(View.VISIBLE);
        ((TextView) errorLayout.findViewById(R.id.errorTextMessage)).setText(errorText);
//        if(stepB_view.getVisibility()==View.VISIBLE)
        target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
    }

    void recoverPasswordCall(boolean setUsername) {
        showLoadingDialog();

        RecoverPasswordRequest recoverPasswordRequest = new RecoverPasswordRequest();
        recoverPasswordRequest.setPhoneNumber(telephone_number_input.getText().toString());
        recoverPasswordRequest.setEmail(email_adress_input.getText().toString());

        if (setUsername) {
            recoverPasswordRequest.setUsername(username_input.getText().toString());
        }

        unhandledError = true;
        new AuthenticationService(getContext()).recoverPassword(recoverPasswordRequest)
                .subscribe(new RequestSessionObserver<GeneralResponse<RecoverPasswordCorrectResponse>>() {
                    @Override
                    public void onNext(GeneralResponse<RecoverPasswordCorrectResponse> response) {
                        unhandledError = false;
                        stopLoadingDialog();
                        showLogs(response);
                        if (response.getTransactionStatus() == 0) {
                            if (response.getTransactionSuccess() != null &&
                                    response.getTransactionSuccess().getUsername().equalsIgnoreCase("step2c")) {
                                inflateStepBView();
                            } else {
                                nextStep(response.getTransactionSuccess());
                            }
                        } else {
                            manageErrorCodes(response.getTransactionFault().getFaultCode());
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (unhandledError) {
                            displayServerErrorToast();
                        }
                        stopLoadingDialog();
                    }
                });
    }

    void showLogs(GeneralResponse<RecoverPasswordCorrectResponse> response) {
        D.v("response = " + response);
        D.w("getTransactionStatus = " + response.getTransactionStatus());
        D.d("response = " + response.getTransactionSuccess());
        if (response.getTransactionSuccess() != null) {
            D.d("getUsername = " + response.getTransactionSuccess().getUsername());
            D.d("getCustomerType = " + response.getTransactionSuccess().getCustomerType());
            D.d("getIsMigrated = " + response.getTransactionSuccess().getIsMigrated());
        }
        D.i("response = " + response.getTransactionFault());
        if (response.getTransactionFault() != null) {
            D.i("response = " + response.getTransactionFault().getFaultCode());
            D.i("response = " + response.getTransactionFault().getFaultMessage());
            D.i("response = " + response.getTransactionFault().getFaultParam());
        }
    }

    void manageErrorCodes(String errorCode) {
        switch (errorCode) {
            case "EC01603":
                forceDisplayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverPasswordEbuNonMigratedAccountMsisdnAndEmail());
                break;
            case "EC01604":
                displayServerErrorToast();
                break;
            case "EC01605":
                if (stepA_view.getVisibility() == View.VISIBLE) {
                    forceDisplayCustomEditTextError(telephone_number_input, phone_number_error_layout,
                            RecoverLabels.getRecoverPasswordNotMyVodafoneAccountFirstPart());
                    phone_number_error_layout.addSpanableAfterText(RecoverLabels.getRecoverPasswordNotMyVodafoneAccountSecondPart(), newAccountClickListener);
                } else {
                    forceDisplayCustomEditTextError(username_input, username_error_layout, RecoverLabels.getRecoverPasswordNotMyVodafoneAccountFirstPart());
                }
                break;
            case "EC01606":
                forceDisplayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEnteredEmailNotEqualWithProfilMail());
                break;
            case "EC01607":
                forceDisplayCustomEditTextError(username_input, username_error_layout, RecoverLabels.getRecoverPasswordNotMyVodafoneAccountFirstPart());
                break;
            case "EC01212":
                new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext())
                        .message(RecoverLabels.getRecoverPasswordErrorBlacklisted()).success(false).show();
                break;
            case "EC01602"://incorect email
            case "EC01601"://incorect phone
            default:
                displayServerErrorToast();
                break;
        }
    }

    void inflateStepBView() {
        page_description.setText(RecoverLabels.getRecoverPasswordPageDescriptionStepB());
        username_input.setText("");
        displayCustomEditTextError(username_input, username_error_layout, "Nume de utilizator invalid!", true);

        recover_password_container.startAnimation(animationStepBRight);
        stepA_view.setVisibility(View.GONE);
        recover_password_container.startAnimation(animationStepARight);
        stepB_view.setVisibility(View.VISIBLE);
        send_username_button.setText(RecoverLabels.getRecoveryButtonNextStepLabel());

        KeyboardHelper.hideKeyboard(getActivity());

//        email_error_layout.setVisibility(View.GONE);
//        phone_number_error_layout.setVisibility(View.GONE);

        disableButton();

    }

    public boolean isStepBVisible() {
        return stepB_view != null && stepB_view.getVisibility() == View.VISIBLE;
    }

    public void inflateStepAViewAfterSteptB() {
        page_description.setText(RecoverLabels.getRecoverPasswordPageDescriptionStepA());

        recover_password_container.startAnimation(animationStepBLeft);
        stepB_view.setVisibility(View.GONE);
        recover_password_container.startAnimation(animationStepALeft);
        stepA_view.setVisibility(View.VISIBLE);
        send_username_button.setText(RecoverLabels.getRecoverPasswordNextStepButtonLabel());


        enableButton();
    }

    void displayServerErrorToast() {
        new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext())
                .message(RecoverLabels.getRecoverUsernameFailedApiCall()).success(false).show();
    }

    void nextStep(RecoverPasswordCorrectResponse recoverPasswordCorrectResponse) {
        RecoverPasswordActivity rpActivity = ((RecoverPasswordActivity) getActivity());

        rpActivity.setEmail(email_adress_input.getText().toString());
        rpActivity.setPhoneNumber(telephone_number_input.getText().toString());
        rpActivity.setUsername(recoverPasswordCorrectResponse.getUsername());
        rpActivity.setCustomerType(recoverPasswordCorrectResponse.getCustomerType());
        rpActivity.setIsMigrated(recoverPasswordCorrectResponse.getIsMigrated());

        rpActivity.displayRecoverPasswordSecondStepFragment();

        KeyboardHelper.hideKeyboard(getActivity());
    }

    void initTracking() {
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.recover_password_1st_screen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.recover_data_journey);
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        RecoverPasswordTrackingEvent event = new RecoverPasswordTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    @Override
    public void onErrorIconClickListener() {
        if(telephone_number_input.isErrorIconTap())
            displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverPasswordTelephoneNumberInvalidFormat(), true);
        else if (email_adress_input.isErrorIconTap())
            displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
        else if (username_input.isErrorIconTap())
            displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RECOVER_PASS_1);
    }

    private static class RecoverPasswordTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "recover password 1st step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "recover password 1st step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "recover data";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Recover Password";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
