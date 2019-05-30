package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.widget.dialogs.PickerDateDialog2;

/**
 * Created by cosmin deliu on 3/31/2018.
 */

public class MinorAccountFragment extends PermissionDetailsFragment {

    @Override
    protected void createCards() {
        parent.removeAllViews();
        inflateMinorCard();
    }

    private void inflateMinorCard() {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_minor, null);
        minorCardTitle = v.findViewById(R.id.minorCardTitle);
        minorCardTitle.setText(getMinorAccountTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(getMinorAccountSubText());

        // TODO -> Logica de afisare din documentatie

        if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
            inflateMinorCardForRescorp(v);
            parent.addView(v);
            return;
        } else if (isResSubGeoNumber()) {
            inflateMinorCardForResSubGeoNumberUsers(v);
            parent.addView(v);
            return;
        }

        inflateMinorCardForOtherCBUUsers(v);
        parent.addView(v);
    }

    private void inflateMinorCardForRescorp(View view) {
        //TODO to avoid futher development please check if gdprPermissions!=null and fragment is attached
        //TODO getResources may call
        //TODO throw new IllegalStateException("Fragment " + this + " not attached to Activity");


        switchButtonMinor = view.findViewById(R.id.minorCardSwitchButton);
        editLayoutContainer = view.findViewById(R.id.editLayoutContainer);
        // own subscription is minor
        if (own_subscription_is_minor) {

            ((VodafoneTextView) view.findViewById(R.id.cardSubtext2)).setText(SettingsLabels.getMinorAccountSecondCardSubText2());
            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditMinorAccountSubtext1()+ "\n\n" + SettingsLabels.getEditMinorAccountSubtext2());

            ((VodafoneTextView) view.findViewById(R.id.phoneNumberLabel))
                    .setText(SettingsLabels.getPhoneNumberLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.emailAddressLabel))
                    .setText(SettingsLabels.getEmailAddressLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.birthdayLabel))
                    .setText(SettingsLabels.getMinorBirthdateLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.phoneNumber))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianContactPhone())
                            ? getResources().getString(R.string.placeholder_phone)
                            : gdprPermissions.getGuardianContactPhone());

            ((VodafoneTextView) view.findViewById(R.id.emailAddress))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianEmailAddress())
                            ? getResources().getString(R.string.placeholder_email)
                            : gdprPermissions.getGuardianEmailAddress());

            ((VodafoneTextView) view.findViewById(R.id.birthday))
                    .setText(TextUtils.isEmpty(gdprPermissions.getMinorBirthDate())
                            ? getResources().getString(R.string.placeholder_birthdate)
                            : getStringDateFormated(gdprPermissions.getMinorBirthDate()));

            if (GdprController.isOwnSubscriptionSelected()) {
                switchButtonMinor.setChecked(true);
                editLayoutContainer.setVisibility(View.VISIBLE);
                view.findViewById(R.id.editGardian).setVisibility(View.VISIBLE);
                view.findViewById(R.id.editGardian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO editeaza gardianul

                        TrackingAppMeasurement journey = new TrackingAppMeasurement();

                        journey.event65 = "button:" + "edit guardian details";
                        journey.getContextData().put("event65", journey.event65);
                        journey.eVar82 = "mcare:permissions:minor account:button:" + "edit guardian details";
                        journey.getContextData().put("eVar82", journey.eVar82);

                        trackEvent(card_type, journey);

                        displayEditMinorAccountOverlay(getString(R.string.overlay_save_button));
                    }
                });
            } else if (GdprController.checkMinorStatus(gdprPermissions)) {
                switchButtonMinor.setChecked(true);
                editLayoutContainer.setVisibility(View.VISIBLE);
                view.findViewById(R.id.editGardian).setVisibility(View.GONE);
                int padding = ScreenMeasure.dpToPx(15);
                editLayoutContainer.setPadding(padding, 0, padding, 0);
            } else {
                switchButtonMinor.setChecked(false);
                editLayoutContainer.setVisibility(View.GONE);
            }
            view.findViewById(R.id.cardSubtext2).setVisibility(View.VISIBLE);
            setNormalSwitchAvailability(switchButtonMinor, false);
            return;
        }

        // own subscription is not minor
        if (GdprController.checkMinorStatus(gdprPermissions)) {//TODO see above, do not keep constants localy
            switchButtonMinor.setChecked(true);
            editLayoutContainer.setVisibility(View.VISIBLE);
            view.findViewById(R.id.editGardian).setVisibility(View.VISIBLE);
//            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditGuardianDescription());
            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditMinorAccountSubtext1()+ "\n\n" + SettingsLabels.getEditMinorAccountSubtext2());

            ((VodafoneTextView) view.findViewById(R.id.phoneNumberLabel))
                    .setText(SettingsLabels.getPhoneNumberLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.emailAddressLabel))
                    .setText(SettingsLabels.getEmailAddressLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.birthdayLabel))
                    .setText(SettingsLabels.getMinorBirthdateLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.phoneNumber))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianContactPhone())
                            ? getResources().getString(R.string.placeholder_phone)
                            : gdprPermissions.getGuardianContactPhone());

            ((VodafoneTextView) view.findViewById(R.id.emailAddress))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianEmailAddress())
                            ? getResources().getString(R.string.placeholder_email)
                            : gdprPermissions.getGuardianEmailAddress());

            ((VodafoneTextView) view.findViewById(R.id.birthday))
                    .setText(TextUtils.isEmpty(gdprPermissions.getMinorBirthDate())
                            ? getResources().getString(R.string.placeholder_birthdate)
                            : getStringDateFormated(gdprPermissions.getMinorBirthDate()));

            view.findViewById(R.id.editGardian).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO editeaza gardianul

                    TrackingAppMeasurement journey = new TrackingAppMeasurement();

                    journey.event65 = "button:" + "edit guardian details";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor account:button:" + "edit guardian details";
                    journey.getContextData().put("eVar82", journey.eVar82);

                    trackEvent(card_type, journey);

                    displayEditMinorAccountOverlay(getString(R.string.overlay_save_button));
                }
            });
        } else {
            switchButtonMinor.setChecked(false);
            editLayoutContainer.setVisibility(View.GONE);
        }

        switchButtonMinor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // daca se activeaza
                if (switchButtonMinor.isChecked()) {

                    TrackingAppMeasurement journey = new TrackingAppMeasurement();

                    journey.event65 = "button:" + "activate minor";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor account:button:" + "activate minor";
                    journey.getContextData().put("eVar82", journey.eVar82);

                    trackEvent(card_type, journey);

                    switchButtonMinor.setChecked(false);
                    gdprPermissionsChanged.setMinorStatus(GdprPermissions.MINOR.toUpperCase());
                    // TODO daca se activeaza deschide EditMinorOverlayDialog
                    displayEditMinorAccountOverlay(getString(R.string.overlay_activate_button));
                    return;
                }

                TrackingAppMeasurement journey = new TrackingAppMeasurement();

                journey.event65 = "button:" + "deactivate minor";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:minor account:button:" + "deactivate minor";
                journey.getContextData().put("eVar82", journey.eVar82);

                trackEvent(card_type, journey);

                switchButtonMinor.setChecked(true);
                gdprPermissionsChanged.setMinorStatus("GUARDIAN");
                gdprPermissionsChanged.setToSendMinorAccountDeactivate();
                // TODO -> daca se dezactiveaza se face call-ul de set permissions
                setPermissions(false, DEACTIVATE, "IMCB");

            }
        });

    }

    private void inflateMinorCardForResSubGeoNumberUsers(View view) {
        switchButtonMinor = view.findViewById(R.id.minorCardSwitchButton);
        editLayoutContainer = view.findViewById(R.id.editLayoutContainer);
        setNormalSwitchAvailability(switchButtonMinor, false);

        ((VodafoneTextView) view.findViewById(R.id.cardSubtext2)).setText(SettingsLabels.getMinorAccountSecondCardSubText1());
        view.findViewById(R.id.cardSubtext2).setVisibility(View.VISIBLE);

        if (GdprController.checkMinorStatus(gdprPermissions)) {//TODO see above, do not keep constants localy
            switchButtonMinor.setChecked(true);
            editLayoutContainer.setVisibility(View.VISIBLE);
            view.findViewById(R.id.editGardian).setVisibility(View.GONE);

            int padding = ScreenMeasure.dpToPx(15);
            editLayoutContainer.setPadding(padding, 0, padding, 0);

//            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditGuardianDescription());
            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditMinorAccountSubtext1()+ "\n\n" + SettingsLabels.getEditMinorAccountSubtext2());

            ((VodafoneTextView) view.findViewById(R.id.phoneNumberLabel))
                    .setText(SettingsLabels.getPhoneNumberLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.emailAddressLabel))
                    .setText(SettingsLabels.getEmailAddressLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.birthdayLabel))
                    .setText(SettingsLabels.getMinorBirthdateLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.phoneNumber))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianContactPhone())
                            ? getResources().getString(R.string.placeholder_phone)
                            : gdprPermissions.getGuardianContactPhone());

            ((VodafoneTextView) view.findViewById(R.id.emailAddress))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianEmailAddress())
                            ? getResources().getString(R.string.placeholder_email)
                            : gdprPermissions.getGuardianEmailAddress());

            ((VodafoneTextView) view.findViewById(R.id.birthday))
                    .setText(TextUtils.isEmpty(gdprPermissions.getMinorBirthDate())
                            ? getResources().getString(R.string.placeholder_birthdate)
                            : getStringDateFormated(gdprPermissions.getMinorBirthDate()));

            return;
        }

        switchButtonMinor.setChecked(false);
        editLayoutContainer.setVisibility(View.GONE);
    }

    private void inflateMinorCardForOtherCBUUsers(View view) {
        switchButtonMinor = view.findViewById(R.id.minorCardSwitchButton);
        editLayoutContainer = view.findViewById(R.id.editLayoutContainer);

        //For ResSub
        ((VodafoneTextView) view.findViewById(R.id.cardSubtext2)).setText(SettingsLabels.getMinorAccountSecondCardSubText1());

        if (GdprController.checkMinorStatus(gdprPermissions)) {//TODO see above, do not keep constants localy
            switchButtonMinor.setChecked(true);
            setNormalSwitchAvailability(switchButtonMinor, false);
            editLayoutContainer.setVisibility(View.VISIBLE);
            view.findViewById(R.id.editGardian).setVisibility(View.VISIBLE);

            // For users that are not ResSub: PrivateUser, PrepaidUser
            if (!(VodafoneController.getInstance().getUser() instanceof ResSub))
                ((VodafoneTextView) view.findViewById(R.id.cardSubtext2)).setText(SettingsLabels.getMinorAccountSecondCardSubText2());

            view.findViewById(R.id.cardSubtext2).setVisibility(View.VISIBLE);
//            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditGuardianDescription());
            ((VodafoneTextView) view.findViewById(R.id.editGuardianDescription)).setText(SettingsLabels.getEditMinorAccountSubtext1()+ "\n\n" + SettingsLabels.getEditMinorAccountSubtext2());

            ((VodafoneTextView) view.findViewById(R.id.phoneNumberLabel))
                    .setText(SettingsLabels.getPhoneNumberLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.emailAddressLabel))
                    .setText(SettingsLabels.getEmailAddressLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.birthdayLabel))
                    .setText(SettingsLabels.getMinorBirthdateLabel() + ":");

            ((VodafoneTextView) view.findViewById(R.id.phoneNumber))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianContactPhone())
                            ? getResources().getString(R.string.placeholder_phone)
                            : gdprPermissions.getGuardianContactPhone());

            ((VodafoneTextView) view.findViewById(R.id.emailAddress))
                    .setText(TextUtils.isEmpty(gdprPermissions.getGuardianEmailAddress())
                            ? getResources().getString(R.string.placeholder_email)
                            : gdprPermissions.getGuardianEmailAddress());

            ((VodafoneTextView) view.findViewById(R.id.birthday))
                    .setText(TextUtils.isEmpty(gdprPermissions.getMinorBirthDate())
                            ? getResources().getString(R.string.placeholder_birthdate)
                            : getStringDateFormated(gdprPermissions.getMinorBirthDate()));

            view.findViewById(R.id.editGardian).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO editeaza gardianul

                    TrackingAppMeasurement journey = new TrackingAppMeasurement();

                    journey.event65 = "button:" + "edit guardian details";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor account:button:" + "edit guardian details";
                    journey.getContextData().put("eVar82", journey.eVar82);

                    trackEvent(card_type, journey);

                    displayEditMinorAccountOverlay(getString(R.string.overlay_save_button));
                }
            });
            return;
        }

        switchButtonMinor.setChecked(false);

        // For ResSub
        if (VodafoneController.getInstance().getUser() instanceof ResSub)
            view.findViewById(R.id.cardSubtext2).setVisibility(View.VISIBLE);

        editLayoutContainer.setVisibility(View.GONE);
        switchButtonMinor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchButtonMinor.isChecked()) {
                    // TODO editeaza gardianul

                    TrackingAppMeasurement journey = new TrackingAppMeasurement();

                    journey.event65 = "button:" + "activate minor";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor account:button:" + "activate minor";
                    journey.getContextData().put("eVar82", journey.eVar82);

                    trackEvent(card_type, journey);

                    gdprPermissionsChanged.setMinorStatus(GdprPermissions.MINOR.toUpperCase());
                    displayEditMinorAccountOverlay(getString(R.string.overlay_activate_button));
                }
                switchButtonMinor.setChecked(false);
            }
        });
    }

    private View.OnClickListener getOnClickListenerForBirthdate(final CustomEditTextCompat minor_birthdate_input) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO -> deschidere calendar pentru alegerea datei (calendar nativ)
                openDatePickerCalendar(minor_birthdate_input);
            }
        };
    }

    private void displayEditMinorAccountOverlay(final String saveOrActivate) {

        final boolean checkIfSaveButton = saveOrActivate.equals(getString(R.string.overlay_save_button));

        initTracking(ACTIVATE_MINOR_OVERLAY);

        //TODO getContext() may be null. Please close method if context is null
        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_permissions);
        overlayDialog.show();

        VodafoneButton cancelButton = overlayDialog.findViewById(R.id.buttonTurnOff);
        final VodafoneButton saveOrActivateButton = overlayDialog.findViewById(R.id.buttonSaveOrActivate);

        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle)).setText(SettingsLabels.getEditMinorAccountTitle());
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext)).setText(SettingsLabels.getEditMinorAccountSubtext1());
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext2)).setText(SettingsLabels.getEditMinorAccountSubtext2());
        ((ScrollView) overlayDialog.findViewById(R.id.scrollViewDialog)).setVerticalScrollBarEnabled(false);

        final CustomEditTextCompat minor_birthdate_input = overlayDialog.findViewById(R.id.minorBirthDateInput);
        final CustomEditTextCompat phone_number_guardian_input = overlayDialog.findViewById(R.id.phoneNumberGuardianInput);
        final CustomEditTextCompat email_address_input = overlayDialog.findViewById(R.id.emailAddressInput);
        final TooltipError email_error_layout = overlayDialog.findViewById(R.id.emailErrorLayout);
        final TooltipError phone_number_error_layout = overlayDialog.findViewById(R.id.phoneNumberErrorLayout);

        minor_birthdate_input.setHint(SettingsLabels.getMinorBirthdateLabel());
        phone_number_guardian_input.setHint(SettingsLabels.getPhoneNumberLabel());
        email_address_input.setHint(SettingsLabels.getEmailAddressLabel());

        if (checkIfSaveButton) {
            minor_birthdate_input.setText(!TextUtils.isEmpty(gdprPermissions.getMinorBirthDate()) ? getStringDateFormated(gdprPermissions.getMinorBirthDate()) : "");
            phone_number_guardian_input.setText(gdprPermissions.getGuardianContactPhone());
            email_address_input.setText(gdprPermissions.getGuardianEmailAddress());
        }

        minor_birthdate_input.setFocusable(false);
        minor_birthdate_input.setFocusableInTouchMode(false);

        minor_birthdate_input.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) { }

        });

        minor_birthdate_input.setLongClickable(false);
        minor_birthdate_input.setTextIsSelectable(false);

        // TODO -> Salveaza/Activeaza: afisare in functie de caz
        saveOrActivateButton.setText(saveOrActivate);
        cancelButton.setText(getString(R.string.overlay_discard_button));

        if (!isValidPhoneNumber(phone_number_guardian_input.getText().toString())
                || !isValidEmail(email_address_input.getText().toString())) {
            setButtonAvailability(saveOrActivateButton, false);
        }

        OnErrorIconClickListener onErrorIconClickListener = new OnErrorIconClickListener() {
            @Override
            public void onErrorIconClickListener() {
                if(phone_number_guardian_input.isErrorIconTap())
                    displayCustomEditTextError(phone_number_guardian_input, phone_number_error_layout, SettingsLabels.getEditMinorAccountTelephoneNumberInvalidFormat(), true);
                else if (email_address_input.isErrorIconTap())
                    displayCustomEditTextError(email_address_input, email_error_layout, SettingsLabels.getEditMinorAccountEmailInvalidFormat(), true);
            }
        };

        // onFocusListener pe EditText-uri
        email_address_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // TODO -> validare email (show tooltip error if needed)
                if (hasFocus) {
                    displayCustomEditTextError(email_address_input, email_error_layout, SettingsLabels.getEditMinorAccountEmailInvalidFormat(), true);
                    return;
                }

                if (!isValidEmail(email_address_input.getText().toString())) {
                    displayCustomEditTextError(email_address_input, email_error_layout, SettingsLabels.getEditMinorAccountEmailInvalidFormat(), false);
                    return;
                }

                displayCustomEditTextError(email_address_input, email_error_layout, SettingsLabels.getEditMinorAccountEmailInvalidFormat(), true);
            }
        });

        phone_number_guardian_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO -> validare phone number (show tooltip error if needed)
                if (hasFocus) {
                    displayCustomEditTextError(phone_number_guardian_input, phone_number_error_layout, SettingsLabels.getEditMinorAccountTelephoneNumberInvalidFormat(), true);
                    return;
                }

                if (!isValidPhoneNumber(phone_number_guardian_input.getText().toString())) {
                    displayCustomEditTextError(phone_number_guardian_input, phone_number_error_layout, SettingsLabels.getEditMinorAccountTelephoneNumberInvalidFormat(), false);
                    return;
                }

                displayCustomEditTextError(phone_number_guardian_input, phone_number_error_layout, SettingsLabels.getEditMinorAccountTelephoneNumberInvalidFormat(), true);
            }
        });


        // textChangedListener pe EditText-uri
        email_address_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO -> validare email (show tooltip error if needed)
                if (isValidEmail(email_address_input.getText().toString()) && isValidPhoneNumber(phone_number_guardian_input.getText().toString())) {
                    setButtonAvailability(saveOrActivateButton, true);
                    return;
                }
                setButtonAvailability(saveOrActivateButton, false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone_number_guardian_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO -> validare phone number (show tooltip error if needed)
                if (isValidPhoneNumber(phone_number_guardian_input.getText().toString()) && isValidEmail(email_address_input.getText().toString())) {
                    setButtonAvailability(saveOrActivateButton, true);
                    return;
                }
                setButtonAvailability(saveOrActivateButton, false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        minor_birthdate_input.setOnClickListener(getOnClickListenerForBirthdate(minor_birthdate_input));

        phone_number_guardian_input.setOnErrorIconClickListener(onErrorIconClickListener);
        email_address_input.setOnErrorIconClickListener(onErrorIconClickListener);

        saveOrActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackingAppMeasurement journey = new TrackingAppMeasurement();

                if (checkIfSaveButton) {
                    journey.event65 = "button:" + "save";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor overlay:button:" + "save";
                    journey.getContextData().put("eVar82", journey.eVar82);
                } else {
                    journey.event65 = "button:" + "activate";
                    journey.getContextData().put("event65", journey.event65);
                    journey.eVar82 = "mcare:permissions:minor overlay:button:" + "activate";
                    journey.getContextData().put("eVar82", journey.eVar82);
                }

                trackEvent(ACTIVATE_MINOR_OVERLAY, journey);

                gdprPermissionsChanged.setMinorBirthDate(TextUtils.isEmpty(minor_birthdate_input.getText().toString()) ? null : getTimeStampDate(minor_birthdate_input.getText().toString()));
                gdprPermissionsChanged.setGuardianContactPhone(TextUtils.isEmpty(phone_number_guardian_input.getText().toString()) ? null : phone_number_guardian_input.getText().toString());
                gdprPermissionsChanged.setGuardianEmailAddress(TextUtils.isEmpty(email_address_input.getText().toString()) ? null : email_address_input.getText().toString());

                if (checkIfSaveButton)
                    gdprPermissionsChanged.setToSendMinorAccountSave();
                else
                    gdprPermissionsChanged.setToSendMinorAccountActivate();

                setPermissions(true, checkIfSaveButton ? SAVE : ACTIVATE, checkIfSaveButton ? "EMCB" : "AMCB");

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackingAppMeasurement journey = new TrackingAppMeasurement();

                journey.event65 = "button:" + "cancel";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:minor overlay:button:" + "cancel";
                journey.getContextData().put("eVar82", journey.eVar82);

                trackEvent(ACTIVATE_MINOR_OVERLAY, journey);

                overlayDialog.dismiss();
                //TODO code cleanup - what if  gdprPermissionsChanged is null or the object is mananged and invalid?
                //TODO code cleaunp - please replace gdprPermissionsChanged is a safe getter
                gdprPermissionsChanged.setMinorBirthDate(gdprPermissions.getMinorBirthDate());
                gdprPermissionsChanged.setGuardianContactPhone(gdprPermissions.getGuardianContactPhone());
                gdprPermissionsChanged.setGuardianEmailAddress(gdprPermissions.getGuardianEmailAddress());
            }
        });

        overlayDialog.findViewById(R.id.overlayDismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackingAppMeasurement journey = new TrackingAppMeasurement();

                journey.event65 = "button:" + "cancel";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:minor overlay:button:" + "cancel";
                journey.getContextData().put("eVar82", journey.eVar82);

                trackEvent(ACTIVATE_MINOR_OVERLAY, journey);

                overlayDialog.dismiss();
                //TODO code cleanup - what if  gdprPermissionsChanged is null or the object is mananged and invalid?
                //TODO code cleaunp - please replace gdprPermissionsChanged is a safe getter
                gdprPermissionsChanged.setMinorBirthDate(gdprPermissions.getMinorBirthDate());
                gdprPermissionsChanged.setGuardianContactPhone(gdprPermissions.getGuardianContactPhone());
                gdprPermissionsChanged.setGuardianEmailAddress(gdprPermissions.getGuardianEmailAddress());
            }
        });
    }

    protected String getMinorAccountSubText() {
        return SettingsLabels.getMinorAccountCardSubtextMinor();
    }

    private boolean isValidEmail(String email) {
        return (TextUtils.isEmpty(email)) || (!TextUtils.isEmpty(email)
                && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(email).matches());
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber))
            return true;

//        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 10 && phoneNumber.length() <= 15) {
//            Pattern pattern = Pattern.compile("^0[0-9]{9}");
//            Matcher matcher = pattern.matcher(phoneNumber);
//            return matcher.matches();
//        }

        return !TextUtils.isEmpty(phoneNumber)
                && phoneNumber.length() >= 10
                && phoneNumber.length() <= 15
                && !phoneNumber.startsWith("0040");
    }

    private Calendar getCalendarDateFormated(String stringDateFormated) {
        try {

            SimpleDateFormat tf = new SimpleDateFormat("dd.MM.yyyy");

            // String formated "dd-MM-yyyy" to Calendar formated
            Calendar calendarFormated = Calendar.getInstance();
            if (stringDateFormated != null && !stringDateFormated.isEmpty()) {
                Date parseTime = tf.parse(stringDateFormated);
                calendarFormated.setTime(parseTime);
            }
            return calendarFormated;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getStringDateFormated(String timeStamp) {
        SimpleDateFormat tf = new SimpleDateFormat("dd.MM.yyyy");

        // timestamp to String formated "dd-MM-yyyy"
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(Long.valueOf(timeStamp));
        Date d = new Date(Long.parseLong(timeStamp) * 1000L);
        return tf.format(d);
    }

    private String getTimeStampDate(String stringDateFormated) {
        try {

            SimpleDateFormat tf = new SimpleDateFormat("dd.MM.yyyy");

            // String formated "dd-MM-yyyy" to timestamp
            Date date = tf.parse(stringDateFormated);
            return String.valueOf(date.getTime()/1000L);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void displayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility) {
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

    private void openDatePickerCalendar(final CustomEditTextCompat minor_birthdate_input) {
        Calendar calendarDateFormat = getCalendarDateFormated(minor_birthdate_input.getText().toString());

        Calendar calendarDateFormatCurrent = Calendar.getInstance();
        Calendar calendarDateFormatMin = Calendar.getInstance();
        calendarDateFormatMin.add(Calendar.YEAR, -18);

        if (calendarDateFormat == null)
            return;

        if (calendarDateFormat.compareTo(calendarDateFormatCurrent) > 0)
            calendarDateFormat = calendarDateFormatCurrent;
        else if (calendarDateFormat.compareTo(calendarDateFormatMin) < 0)
            calendarDateFormat = calendarDateFormatMin;

        final int year = calendarDateFormat.get(Calendar.YEAR);
        final int day = calendarDateFormat.get(Calendar.DAY_OF_MONTH);
        final int month = calendarDateFormat.get(Calendar.MONTH);

        final PickerDateDialog2.OnDateSetListener listener = new PickerDateDialog2.OnDateSetListener() {
            @Override
            public void onDateSet(PickerDateDialog2 view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                calendar.set(year, monthOfYear, dayOfMonth);
                minor_birthdate_input.setText(format.format(calendar.getTime()));
                //TODO code cleanup - what if  gdprPermissionsChanged is null or the object is mananged and invalid?
                //TODO code cleaunp - please replace gdprPermissionsChanged is a safe getter
                gdprPermissionsChanged.setMinorBirthDate(String.valueOf(calendar.getTimeInMillis()));
            }
        };

        final PickerDateDialog2 datePickerDialog = PickerDateDialog2.newInstance(listener, year, month, day);

        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePickerDialog.setTitle("Dată naştere minor");
        datePickerDialog.setOkText("Ok");
        datePickerDialog.setCancelText("Ștergeți");
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (dialog == null) {
                    gdprPermissionsChanged.setMinorBirthDate(null);
                    minor_birthdate_input.setText(gdprPermissionsChanged.getMinorBirthDate());
                    if (!minor_birthdate_input.hasOnClickListeners())
                        minor_birthdate_input.setOnClickListener(getOnClickListenerForBirthdate(minor_birthdate_input));
                }
            }
        });

        datePickerDialog.setMaxDate(calendarDateFormatCurrent);
        datePickerDialog.setMinDate(calendarDateFormatMin);
        datePickerDialog.show(getActivity().getFragmentManager(), "DialogPicker");
    }


}
