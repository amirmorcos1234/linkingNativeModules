package ro.vodafone.mcare.android.ui.fragments.recover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RecoverLabels;
import ro.vodafone.mcare.android.client.model.recover.RecoverUsernameResponse;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.RecoverUsernameRequest;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.RecoverUsernameActivity;
import ro.vodafone.mcare.android.ui.activities.registration.RegisterActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;


public class RecoverUsernameFragment extends BaseFragment implements OnErrorIconClickListener {

    public static String TAG = "RecoverUsernameFrag";

    @BindView(R.id.phone_number_error_layout)
    TooltipError phone_number_error_layout;
    @BindView(R.id.email_error_layout)
    TooltipError email_error_layout;

    @BindView(R.id.telephone_number_input)
    CustomEditTextCompat telephone_number_input;
    @BindView(R.id.email_adress_input)
    CustomEditTextCompat email_adress_input;

    @BindView(R.id.send_username_button)
    VodafoneButton send_username_button;

    boolean unhandledError = false;
    boolean telephoneNumberFirstTimeFocus = false;
    View.OnClickListener newAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            D.d("READ CLICK");
            Intent intent = new Intent(getActivity().getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };
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

        View v = inflater.inflate(R.layout.fragment_recover_username, null);
        ButterKnife.bind(this, v);

        initTracking();
        initViews();

        return v;
    }

    void initViews() {
        telephone_number_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.d("we got focus ON telephone_number_input");
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverUsernameTelephoneNumberInvalidFormat(), true);
                } else {
//                    D.i("we lost focus ON telephone_number_input");
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
//                    D.d("we lost focus ON email_adress_input");
                    checkIfValid(2);
                }
            }
        });

        telephone_number_input.addTextChangedListener(textWatcher);
        email_adress_input.addTextChangedListener(textWatcher);
        telephone_number_input.setOnErrorIconClickListener(this);
        email_adress_input.setOnErrorIconClickListener(this);
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
                if (telephone_number_input.getText().toString().equals("") || !isValidPhoneNumber(telephone_number_input.getText().toString()))
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverUsernameTelephoneNumberInvalidFormat(), false);
                else
                    displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverUsernameTelephoneNumberInvalidFormat(), true);
                break;
            case 2:
                if (email_adress_input.getText().toString().equals(""))
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), false);
                else if (!isValidEmail(email_adress_input.getText().toString()))
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), false);
                else
                    displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
                break;
            default:
                break;
        }
    }

    public void disableButton() {
        send_username_button.setClickable(false);
        send_username_button.setEnabled(false);
    }

    public void enableButton() {
        send_username_button.setClickable(true);
        send_username_button.setEnabled(true);
        send_username_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
    }

    public boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value) && !"".equals(value.replaceAll(" ", ""))) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

                isValid = matcher.matches() && value.startsWith("0");
            } else
                isValid = false;
        }
        return isValid;
    }

    public boolean isValidEmail(String email) {
        return (null != email && !TextUtils.isEmpty(email) && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(email).matches());
    }

    public boolean validFields() {
        return isValidPhoneNumber(telephone_number_input.getText().toString())
                && isValidEmail(email_adress_input.getText().toString());
    }

    public void displayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility) {

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
    }

    private void onButtonClick() {
        showLoadingDialog();

        email_error_layout.setVisibility(View.GONE);
        phone_number_error_layout.setVisibility(View.GONE);

        RecoverUsernameRequest recoverUsernameRequestCR = new RecoverUsernameRequest();
        recoverUsernameRequestCR.setEmail(email_adress_input.getText().toString());
        recoverUsernameRequestCR.setPhoneNumber(telephone_number_input.getText().toString());

        unhandledError = true;
        new AuthenticationService(getContext()).recoverUsername(recoverUsernameRequestCR)
                .subscribe(new RequestSessionObserver<GeneralResponse<RecoverUsernameResponse>>() {
                    @Override
                    public void onNext(GeneralResponse<RecoverUsernameResponse> response) {
                        unhandledError = false;
                        showLogs(response);
                        stopLoadingDialog();
                        if (response.getTransactionStatus() == 0) {
                            successfullCallTracking();
                            nextStep();
                        } else {
                            unsuccessfullCallTracking();
                            manageErrorCodes(response.getTransactionFault().getFaultCode());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (unhandledError)
                            displayServerErrorToast();
                        stopLoadingDialog();
                        onErrorTracking();
                    }
                });
    }

    void nextStep() {
        getActivity().finish();
        new CustomToast.Builder(getContext()).message("Numele de utilizator a fost trimis pe SMS si e-mail.").success(true).show();

    }

    void showLogs(GeneralResponse<RecoverUsernameResponse> response) {
//        D.v("response = " + response);
//        D.w("getTransactionStatus = " + response.getTransactionStatus());
//        D.d("response = " + response.getTransactionSuccess());
//        if (response.getTransactionSuccess() != null) {
//            D.d("getStatus = " + response.getTransactionSuccess().getStatus());
//            D.d("getStatus = " + response.getTransactionSuccess().getStep());
//        }
//        D.i("response = " + response.getTransactionFault());
//        if (response.getTransactionFault() != null) {
//            D.i("response = " + response.getTransactionFault().getFaultCode());
//            D.i("response = " + response.getTransactionFault().getFaultMessage());
//            D.i("response = " + response.getTransactionFault().getFaultParam());
//        }
    }

    void manageErrorCodes(String errorCode) {
        switch (errorCode) {
            case "EC01501"://incorect phone
                break;
            case "EC01502"://incorect email
                break;
            case "EC01503":
                forceDisplayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverUsernameEbuMigratedAccountMsisdnAndEmail());
                break;
            case "EC01504":
                new CustomToast.Builder(getContext()).message("Sistem momentan indisponibil!").success(false).show();
                break;
            case "EC01505":
                forceDisplayCustomEditTextError(telephone_number_input, phone_number_error_layout,
                        RecoverLabels.getRecoverPasswordNotMyVodafoneAccountFirstPart());
                phone_number_error_layout.addSpanableAfterText(RecoverLabels.getRecoverPasswordNotMyVodafoneAccountSecondPart(), newAccountClickListener);
                break;
            case "EC01506":
                forceDisplayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEnteredEmailNotEqualWithProfilMail());
                break;
            default:
                new CustomToast.Builder(getContext()).message("Sistem momentan indisponibil!").success(false).show();
                break;
        }
    }

    void displayServerErrorToast() {
        new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
    }

    void initTracking() {
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.recover_username_screen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.recover_data_journey);
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
    }

    void onErrorTracking() {
        RecoverUsernameActivity.RecoverUsernameTrackingEvent event = new RecoverUsernameActivity.RecoverUsernameTrackingEvent();
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

    void successfullCallTracking() {
        RecoverUsernameActivity.RecoverUsernameTrackingEvent event = new RecoverUsernameActivity.RecoverUsernameTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        journey.event10 = "event10";
        journey.getContextData().put("event10", journey.event10);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    void unsuccessfullCallTracking() {
        RecoverUsernameActivity.RecoverUsernameTrackingEvent event = new RecoverUsernameActivity.RecoverUsernameTrackingEvent();
        //Tealium tracking event
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event11 = "event11";
        journey.getContextData().put("event11", journey.event11);
        journey.prop16 = "prop16";
        journey.getContextData().put("prop16", journey.prop16);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    @Override
    public void onErrorIconClickListener() {
        if(telephone_number_input.isErrorIconTap())
            displayCustomEditTextError(telephone_number_input, phone_number_error_layout, RecoverLabels.getRecoverUsernameTelephoneNumberInvalidFormat(), true);
        else if(email_adress_input.isErrorIconTap())
            displayCustomEditTextError(email_adress_input, email_error_layout, RecoverLabels.getRecoverUsernameEmailInvalidFormat(), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RECOVER_USERNAME);
    }
}
