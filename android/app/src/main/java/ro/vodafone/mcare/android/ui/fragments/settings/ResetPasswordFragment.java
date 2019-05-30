package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.ResetPasswordRequest;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by bogdan.marica on 3/10/2017.
 */

public class ResetPasswordFragment extends SettingsFragment implements OnErrorIconClickListener {

    Context context;
    UserProfile userProfile;
    boolean changePasword = true;
    boolean oldPassFirstTimeFocus = false;
    boolean newPassFirstTimeFocus = false;

    @BindView(R.id.oldPassword_input)
    CustomEditTextCompat oldPasswordInput;
    @BindView(R.id.newPassword_input)
    CustomEditTextCompat newPasswordInput;
    @BindView(R.id.confirmPassword_input)
    CustomEditTextCompat confirmPasswordInput;

    @BindView(R.id.badPasswordLayout)
    LinearLayout badPasswordLayout;
    @BindView(R.id.badNewPasswordLayout)
    LinearLayout badNewPasswordLayout;
    @BindView(R.id.badConfirmPasswordLayout)
    LinearLayout badConfirmPasswordLayout;

    @BindView(R.id.changePassword)
    Button changePasswordButton;
    @BindView(R.id.back)
    Button backButton;

    View.OnClickListener onChangeButtonLIstener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "password reset");
            tealiumMapEvent.put("event_name", "mcare:password reset:button:schimba parola");
            tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            KeyboardHelper.hideKeyboard(getActivity());

            String oldPassword = oldPasswordInput.getText().toString();
            final String newPassword = newPasswordInput.getText().toString();
            showLoadingDialog();
            if (changePasword) {
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
                resetPasswordRequest.setOldPassword(oldPassword);
                resetPasswordRequest.setNewPassword(newPassword);
                ((SettingsActivity) getActivity()).authenticationService.resetPassword(resetPasswordRequest).subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse generalResponse) {
                        stopLoadingDialog();
                        D.w("response.getTransactionSuccess:" + generalResponse.getTransactionSuccess());
                        if (generalResponse.getTransactionStatus() == 0) {
                            userProfile.setPassword(newPassword);
                            new CustomToast.Builder(getContext()).message("Parola a fost resetată").success(true).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), "Parola ta a fost resetată", true);
//                            customToast.show();

                            PasswordResetTrackingEvent event = new PasswordResetTrackingEvent();
                            TrackingAppMeasurement journey = new TrackingAppMeasurement();
                            journey.event10 = "event10";
                            journey.getContextData().put("event10", journey.event10);
                            event.defineTrackingProperties(journey);
                            VodafoneController.getInstance().getTrackingService().trackCustom(event);

                            getActivity().onBackPressed();
                        } else {
                            new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
//                            new CustomToast(getActivity(), getContext(), "Serviciu momentan indisponibil", false).show();
                            PasswordResetTrackingEvent event = new PasswordResetTrackingEvent();
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
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        D.e("ERROR : " + e);
                        new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
//                        new CustomToast(getActivity(), getContext(), "Serviciu momentan indisponibil", false).show();
                        PasswordResetTrackingEvent event = new PasswordResetTrackingEvent();
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
                });

            } else {
                D.e();
            }
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
            if (!newPasswordInput.getText().toString().equals("")
                    && !oldPasswordInput.getText().toString().equals("")
                    && !confirmPasswordInput.getText().toString().equals("")
                    && newPasswordIsValid(newPasswordInput.getText().toString())
                    && confirmPasswordInput.getText().toString().equals(newPasswordInput.getText().toString()))
                enableButton();
            else
                disableButton();
        }
    };

    public ResetPasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSettingsFragment = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_reset_password, null);

        context = getContext();
        userProfile = ((UserProfile) RealmManager.getRealmObject(UserProfile.class));
        ButterKnife.bind(this, v);

        changePasswordButton.setText(SettingsLabels.getChangePassword());
        changePasswordButton.setTypeface(Fonts.getVodafoneRG());
        backButton.setText(getContext().getResources().getText(R.string.back));
        backButton.setTypeface(Fonts.getVodafoneRG());

        oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name", "password reset");
                tealiumMapEvent.put("event_name", "mcare:password reset:button:inapoi");
                tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                getActivity().onBackPressed();
            }
        });

        disableButton();

        oldPasswordInput.addTextChangedListener(textWatcher);
        newPasswordInput.addTextChangedListener(textWatcher);
        confirmPasswordInput.addTextChangedListener(textWatcher);
        oldPasswordInput.setOnErrorIconClickListener(this);
        newPasswordInput.setOnErrorIconClickListener(this);
        confirmPasswordInput.setOnErrorIconClickListener(this);

        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "password reset");
        tealiumMapView.put("journey_name", "settings");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        PasswordResetTrackingEvent event = new PasswordResetTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        oldPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(oldPasswordInput, badPasswordLayout, true);
                } else {
//                    D.e("we lost focus");
                    checkIfValid(1);
                }
            }
        });

        newPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(newPasswordInput, badNewPasswordLayout, true);
                    firstTimeFocus(oldPassFirstTimeFocus, true);
                } else {
//                    D.e("we lost focus");
                    checkIfValid(2);

                }
            }
        });


        confirmPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    confirmPasswordInput.requestLayout();
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                    displayEditTextError(confirmPasswordInput, badConfirmPasswordLayout, true);
                    firstTimeFocus(oldPassFirstTimeFocus, newPassFirstTimeFocus);
                } else {
//                    D.e("we lost focus");
                    checkIfValid(3);
                }
            }
        });


        return v;
    }

    public String getTitle() {
        return ((String) getResources().getText(R.string.resetPassword));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((SettingsActivity) getActivity()).setTitle();
    }

    public void firstTimeFocus(boolean oldPass, boolean newPass) {
        if (!oldPass) {
            checkIfValid(1);
            oldPassFirstTimeFocus = true;
        }

        if (!newPass) {
            checkIfValid(2);
            newPassFirstTimeFocus = true;
        }
    }

    public void checkIfValid(int view) {
        switch(view) {
            case 1:
                if (oldPasswordInput.getText().toString().equals("") || !oldPaswordIsValid(oldPasswordInput.getText().toString()))
                    displayEditTextError(oldPasswordInput, badPasswordLayout, false);
                else
                    displayEditTextError(oldPasswordInput, badPasswordLayout, true);
                break;
            case 2:
                if (newPasswordInput.getText().toString().equals("") || !newPasswordIsValid(newPasswordInput.getText().toString()))
                    displayEditTextError(newPasswordInput, badNewPasswordLayout, false);
                else
                    displayEditTextError(newPasswordInput, badNewPasswordLayout, true);
                break;
            case 3:
                if (confirmPasswordInput.getText().toString().equals("") || !confirmPasswordInput.getText().toString().equals(newPasswordInput.getText().toString()))
                    displayEditTextError(confirmPasswordInput, badConfirmPasswordLayout, false);
                else
                    displayEditTextError(confirmPasswordInput, badConfirmPasswordLayout, true);
                break;
            default:
                break;
        }
    }

    public boolean oldPaswordIsValid(String oldPassword) {
        try {
            if(userProfile == null || userProfile.getPassword() == null || userProfile.getPassword().isEmpty()){
                return true;
            } else {
                String realmPassword = userProfile.getPassword();
                return oldPassword.equals(realmPassword);
            }
        } catch (Exception e) {
            D.e("should never");
        }
        return false;
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

    public void disableButton() {
        changePasswordButton.setClickable(false);
        changePasswordButton.setEnabled(false);
    }

    public void enableButton() {
        changePasswordButton.setClickable(true);
        changePasswordButton.setEnabled(true);
        changePasswordButton.setOnClickListener(onChangeButtonLIstener);
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
    public void onErrorIconClickListener() {
        if(oldPasswordInput.isErrorIconTap())
            displayEditTextError(oldPasswordInput, badPasswordLayout, true);
        else if (newPasswordInput.isErrorIconTap())
            displayEditTextError(newPasswordInput, badNewPasswordLayout, true);
        else if (confirmPasswordInput.isErrorIconTap())
            displayEditTextError(confirmPasswordInput, badConfirmPasswordLayout, true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_PASS_RESET);
    }

    public static class PasswordResetTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "password reset";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "password reset");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
