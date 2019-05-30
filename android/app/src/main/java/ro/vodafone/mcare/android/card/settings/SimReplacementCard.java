package ro.vodafone.mcare.android.card.settings;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;

public class SimReplacementCard extends VodafoneAbstractCard {

    @BindView(R.id.title) VodafoneTextView replacementTitle;
    @BindView(R.id.sub_title) VodafoneTextView replacementSubTitle;
    @BindView(R.id.sim_error) TooltipError simError;
    @BindView(R.id.sim_label) VodafoneTextView simLabel;
    @BindView(R.id.sim_value_input) CustomEditTextCompat simValueInput;
    @BindView(R.id.phone_number_error) TooltipError phoneNumberError;
    @BindView(R.id.phone_number_label) VodafoneTextView phoneNumberLabel;
    @BindView(R.id.phone_number_value_input) CustomEditTextCompat phoneNumberValueInput;
    @BindView(R.id.card_button) VodafoneButton cardButton;

    private Context mContext;
    private String simFromAPI;

    public SimReplacementCard(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SimReplacementCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SimReplacementCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected int setContent() {
        return R.layout.card_sim_replacement;
    }

    private void init() {
        ButterKnife.bind(this);
    }

    public SimReplacementCard setupForSimReplacement() {
        Realm realm = Realm.getDefaultInstance();
        Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);
        realm.close();

        setTitle(SettingsLabels.getReplacementTitleCard())
                .setSubTitle(getSubtitle())
                .setSimLabel(SettingsLabels.getReplacementSimLabel())
                .setPhoneNumberLabel(SettingsLabels.getReplacementPhoneNumberLabel())
                .setPhoneNumberValue(profile != null ? profile.getHomeMsisdnWithout4Prefix() : null)
                .setButtonText(SettingsLabels.getReplacementButtonText())
                .setTextInputListeners();
        return this;
    }

    public void setSim(String sim) {
        this.simFromAPI = sim;
    }

    public void reload() {
        Realm realm = Realm.getDefaultInstance();
        Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);
        realm.close();

        setPhoneNumberValue(profile != null ? profile.getHomeMsisdnWithout4Prefix() : null)
                .setSimValue(null);

        if (isValidPhoneNumber(getPhoneNumberValue()))
            displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, null, true);
        else
            displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, SettingsLabels.getReplacementPhoneNumberError(), false);

        displayCustomEditTextError(simValueInput, simError, null, true);
    }

    private String getSubtitle() {
        String customerSegment;
        if (checkCustomerSegmentForEBU()) {
            customerSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
            if (customerSegment != null && AppConfiguration.getSimReplacementEbuMigratedCustomerSegments1() != null
                    && AppConfiguration.getSimReplacementEbuMigratedCustomerSegments1().contains(customerSegment))
                return SettingsLabels.getReplacementSubTitleEbuCustomerSegment1();
            if (customerSegment != null && AppConfiguration.getSimReplacementEbuMigratedCustomerSegments2() != null
                    && AppConfiguration.getSimReplacementEbuMigratedCustomerSegments2().contains(customerSegment))
                return SettingsLabels.getReplacementSubTitleEbuCustomerSegment2();
        }
        return SettingsLabels.getReplacementSubTitleCBU();
    }

    private boolean checkCustomerSegmentForEBU() {
        User user = VodafoneController.getInstance().getUser();
        return (user instanceof ChooserUser
                || user instanceof DelegatedChooserUser
                || user instanceof AuthorisedPersonUser
                || user instanceof SubUserMigrated
                || user instanceof PowerUser)
                && EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null
                && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment() != null;
    }

    public SimReplacementCard setTitle(String title) {
        replacementTitle.setText(title);
        return this;
    }

    public SimReplacementCard setSubTitle(String subTitle) {
        replacementSubTitle.setText(subTitle);
        return this;
    }

    public SimReplacementCard setSimLabel(String label) {
        simLabel.setText(label);
        return this;
    }

    public SimReplacementCard setPhoneNumberLabel(String label) {
        phoneNumberLabel.setText(label);
        return this;
    }

    public SimReplacementCard setButtonText(String buttonText) {
        cardButton.setText(buttonText);
        return this;
    }

    public SimReplacementCard setSimValue(String value) {
        simValueInput.setText(value);
        if (!isValidSim(value))
            displayCustomEditTextError(simValueInput, simError, SettingsLabels.getReplacementSimError(), false);
        return this;
    }

    public SimReplacementCard setPhoneNumberValue(String value) {
        phoneNumberValueInput.setText(value);
        if (!isValidPhoneNumber(value))
            displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, SettingsLabels.getReplacementPhoneNumberError(), false);
        return this;
    }

    public String getSimValue() {
        return simValueInput.getText().toString();
    }

    public String getPhoneNumberValue() {
        return phoneNumberValueInput.getText().toString();
    }

    public SimReplacementCard setConfirmationButtonOnClickListener(OnClickListener buttonOnClickListener) {
        cardButton.setOnClickListener(buttonOnClickListener);
        return this;
    }

    public SimReplacementCard setTextInputListeners() {
        setSimValueInputListeners();
        setPhoneNumberInputListeners();
        return this;
    }

    private void setSimValueInputListeners() {

        simValueInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mContext == null)
                    return false;

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    KeyboardHelper.hideKeyboard((Activity) mContext);
                    simValueInput.clearFocus();
                    return true;
                }
                return false;
            }
        });

        simValueInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    displayCustomEditTextError(simValueInput, simError, SettingsLabels.getReplacementSimError(), true);
                    return;
                }

                if (!isValidSim(simValueInput.getText().toString())) {
                    displayCustomEditTextError(simValueInput, simError, SettingsLabels.getReplacementSimError(), false);
                    return;
                }

                displayCustomEditTextError(simValueInput, simError, SettingsLabels.getReplacementSimError(), true);
            }
        });

        simValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidSim(simValueInput.getText().toString()) && isValidPhoneNumber(phoneNumberValueInput.getText().toString())) {
                    setButtonAvailability(true);
                    return;
                }
                setButtonAvailability(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        simValueInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        simValueInput.setLongClickable(false);
        simValueInput.setTextIsSelectable(false);

        simValueInput.setOnErrorIconClickListener(new OnErrorIconClickListener() {
            @Override
            public void onErrorIconClickListener() {
                displayCustomEditTextError(simValueInput, simError, null, true);
            }
        });
    }

    private void setPhoneNumberInputListeners() {

        phoneNumberValueInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mContext == null)
                    return false;

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    KeyboardHelper.hideKeyboard((Activity) mContext);
                    phoneNumberValueInput.clearFocus();
                    return true;
                }
                return false;
            }
        });

        phoneNumberValueInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, SettingsLabels.getReplacementPhoneNumberError(), true);
                    return;
                }

                if (!isValidPhoneNumber(phoneNumberValueInput.getText().toString())) {
                    displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, SettingsLabels.getReplacementPhoneNumberError(), false);
                    return;
                }

                displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, SettingsLabels.getReplacementPhoneNumberError(), true);
            }
        });

        phoneNumberValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidPhoneNumber(phoneNumberValueInput.getText().toString()) && isValidSim(simValueInput.getText().toString())) {
                    setButtonAvailability(true);
                    return;
                }
                setButtonAvailability(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberValueInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        phoneNumberValueInput.setLongClickable(false);
        phoneNumberValueInput.setTextIsSelectable(false);

        phoneNumberValueInput.setOnErrorIconClickListener(new OnErrorIconClickListener() {
            @Override
            public void onErrorIconClickListener() {
                displayCustomEditTextError(phoneNumberValueInput, phoneNumberError, null, true);
            }
        });
    }

    private boolean isValidSim(String sim) {
        if (sim == null || sim.length() != 19)
            return false;

        boolean checkAPISim = simFromAPI != null && !simFromAPI.isEmpty();

        if (checkAPISim && simFromAPI.equalsIgnoreCase(sim))
            return false;

        return simLuhnCheck(sim);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty())
            return true;

        if (phoneNumber.length() == 10) {
            Pattern pattern = Pattern.compile("^0[0-9]{9}");
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }

        return false;
    }

    public boolean simLuhnCheck(String card) {
        if (card == null || card.isEmpty())
            return false;
        char checkDigit = card.charAt(card.length() - 1);
        String digit = calculateCheckDigit(card.substring(0, card.length() - 1));
        return checkDigit == digit.charAt(0);
    }

    public String calculateCheckDigit(String card) {
        if (card == null || card.isEmpty())
            return null;
        String digit;
        //convert to array of int for simplicity
        int[] digits = new int[card.length()];
        for (int i = 0; i < card.length(); i++) {
            digits[i] = Character.getNumericValue(card.charAt(i));
        }

        // double every other starting from right - jumping from 2 in 2
        for (int i = digits.length - 1; i >= 0; i -= 2)	{
            digits[i] += digits[i];

            // taking the sum of digits grater than 10 - simple trick by substract 9
            if (digits[i] >= 10) {
                digits[i] = digits[i] - 9;
            }
        }
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i];
        }
        // multiply by 9 step
        sum = sum * 9;

        // convert to string to be easier to take the last digit
        digit = sum + "";
        return digit.substring(digit.length() - 1);
    }

    private void setButtonAvailability(boolean availability) {
        cardButton.setClickable(availability);
        cardButton.setEnabled(availability);
    }

    private void displayCustomEditTextError(CustomEditTextCompat target, TooltipError errorLayout, String errorText, boolean visibility) {
        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
                errorLayout.setText(errorText);
            } else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.onfocus_input_border);

    }

}
