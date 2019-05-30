package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.italy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.IonController;
import ro.vodafone.mcare.android.card.loyaltyMarket.CenteredImageWithTextsCard;
import ro.vodafone.mcare.android.client.adapters.filters.ContainsFilterAutocompleteAdapter;
import ro.vodafone.mcare.android.client.model.ion.IonModel;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.custom.PrefixCustomAutoCompleteEditText;
import ro.vodafone.mcare.android.custom.SimpleDividerItemDecoration;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.adapters.IonUnlimitedAdapter;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base.UnlimitedBaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

public class UnlimitedItalyFragment extends UnlimitedBaseFragment {

    protected String selectedMsisdn = null;
    protected boolean isItemClicked = false;

    //Add Phone Number Card
    private LinearLayout phoneNumberParent;
    private VodafoneTextView instructionalText;
    private CustomEditTextCompat phoneNumberInput;
    private LinearLayout phoneNumberInputError;
    private PrefixCustomAutoCompleteEditText prefixInput;
    private View dividerPrefixAndMsisdn;
    private VodafoneButton addNumberButton;
    private boolean errorGeneral = false;
    private boolean errorShownForPrefix = false;
    private boolean errorShownForPhoneNumber = false;
    private boolean phoneNumberInputFocusFirst = false;

    // ION Inactive Card
    private VodafoneButton ionInactiveButton;

    // Remove Phone Number Card
    private RecyclerView msisdnRecyclerView;
    private IonUnlimitedAdapter unlimitedAdapter;
    private VodafoneButton removeNumberButton;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UnlimitedItalyTrackingEvent event = new UnlimitedItalyTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
        TealiumHelper.tealiumTrackView(UnlimitedItalyFragment.class.getSimpleName(), TealiumConstants.yourProfileJourney,TealiumConstants.unlimitedItalyScreenName);
    }

    @Override
    protected void initFlow() {
        Profile profile = getPresenter().getProfileFromRealm();

        parent.removeAllViews();

        if (IonController.isTobeOrVmb(profile)) {
            inflateErrorOrWarningLayout(YourProfileLabels.getUnlimitedIsTobeOrIsVmbText(), false);
            parent.setVisibility(View.VISIBLE);
            return;
        }

        getPresenter().getUnlimitedIon(getContext());
    }

    @Override
    public void createCards() {
        inflateAddPhoneNumberCard();
        inflatePhoneNumberListCard();
    }

    @Override
    protected void inflateIonInactive() {
        if (getActivity() == null || getContext() == null)
            return;

        View viewCard = View.inflate(getActivity(), R.layout.card_ion_inactive, null);

        ((VodafoneTextView) viewCard.findViewById(R.id.card_description))
                .setText(IonController.getIonInactiveDescriptionText());
        ionInactiveButton = viewCard.findViewById(R.id.card_button);
        ionInactiveButton.setText(IonController.getIonInactiveButtonText(getContext()));
        ionInactiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() == null)
                    return;
                new NavigationAction(getContext(), IntentActionName.OFFERS_BEO_NO_SEAMLESS).finishCurrent(true).startAction();
            }
        });

        parent.addView(viewCard);
    }

    @Override
    protected void displayPhoneNumberInputError(String errorMessage) {
        errorGeneral = true;
        errorShownForPhoneNumber = false;
        errorShownForPrefix = false;
        displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, errorMessage, false);
    }

    private void inflateAddPhoneNumberCard() {
        if (getActivity() == null || getContext() == null)
            return;

        if (getPresenter() == null)
            return;

        if (getPresenter().getNumberOfMsisdns() >= getPresenter().getMaximumLimit()) {
            inflateErrorOrWarningLayout(IonController.getMaximumValueReachedText(getPresenter().getMaximumLimit()), false);
            return;
        }

        View viewCard = View.inflate(getActivity(), R.layout.card_ion_add_msisdn, null);
        instructionalText = viewCard.findViewById(R.id.instructional_text);
        phoneNumberParent = viewCard.findViewById(R.id.phone_number_parent);
        phoneNumberInput = viewCard.findViewById(R.id.phone_number_input);
        phoneNumberInputError = viewCard.findViewById(R.id.phone_number_input_error);
        prefixInput = viewCard.findViewById(R.id.prefix_input);
        dividerPrefixAndMsisdn = viewCard.findViewById(R.id.divider_prefix_and_msisdn);
        addNumberButton = viewCard.findViewById(R.id.card_button);
        addNumberButton.setText(getString(R.string.unlimited_ion_add_msisdn_text));
        instructionalText.setText(YourProfileLabels.getUnlimitedInstructionalText());
        instructionalText.setVisibility(TextUtils.isEmpty(YourProfileLabels.getUnlimitedInstructionalText()) ? View.GONE : View.VISIBLE);
        ((TextView) viewCard.findViewById(R.id.add_msisdn_title)).setText(YourProfileLabels.getUnlimitedAddMsisdnCardTitle());

        ContainsFilterAutocompleteAdapter<String> adapter = new ContainsFilterAutocompleteAdapter<>(getContext(), getPresenter().getPrefixes());
        prefixInput.setAdapter(adapter);
        prefixInput.setText(getPresenter().getDefaultPrefix().replaceFirst("\\+", ""));
        prefixInput.setSelection(prefixInput.getText().length());

        setEventListenersForPrefixInput();
        setEventListenersForPhoneNumberInput();

        setButtonAvailability(addNumberButton, false);
        addNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call api 73 put
                phoneNumberParent.requestFocus();

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(phoneNumberInput.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(prefixInput.getWindowToken(), 0);

                String phoneNumber = "+" + prefixInput.getText().toString() + phoneNumberInput.getText().toString();
                if (phoneNumberIsAlreadyInList(phoneNumber)) {
                    displayPhoneNumberInputError(YourProfileLabels.getUnlimitedMsisdnNotInListError());
                    return;
                }
                getPresenter().putUnlimitedIon(getContext(), phoneNumber);
                setupAddNumberTrackingEvent();
                TealiumHelper.tealiumTrackEvent(UnlimitedItalyFragment.class.getSimpleName(), addNumberButton.getText().toString(), TealiumConstants.unlimitedItalyScreenName, "button=");
            }
        });

        parent.addView(viewCard);
    }

    private boolean phoneNumberIsAlreadyInList(String phoneNumber) {
        if (IonController.checkListMsisdnNotEmpty(getPresenter().getMsisdnList())) {
            for (IonModel model : getPresenter().getMsisdnList())
                if (model.getMsisdn().equalsIgnoreCase(phoneNumber))
                    return true;
        }
        return false;
    }

    private void setEventListenersForPhoneNumberInput() {
        phoneNumberInput.setDisplayBorder(false);
        phoneNumberInput.setLongClickable(false);
        phoneNumberInput.setTextIsSelectable(false);

        phoneNumberParent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && errorShownForPrefix && errorShownForPhoneNumber)
                    displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), false);
                else if (hasFocus && !errorShownForPrefix && errorShownForPhoneNumber) {
                    if (!isValidPrefix()) {
                        displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), false);
                        errorShownForPrefix = true;
                    }
                } else if (hasFocus && !errorShownForPhoneNumber && errorShownForPrefix) {
                    if (!isValidPhoneNumber()) {
                        displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), false);
                        errorShownForPhoneNumber = true;
                    }
                } else if (!hasFocus && errorShownForPrefix && errorShownForPhoneNumber)
                    prefixInput.setBackgroundResource(R.drawable.red_error_input_border_prefix);
            }
        });

        phoneNumberInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        phoneNumberInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (errorGeneral) {
                        displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMsisdnNotVodafoneError(), true);
                        errorGeneral = false;
                    }
                    displayCustomEditTextError(phoneNumberInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true, errorShownForPrefix);
                    errorShownForPhoneNumber = false;
                    phoneNumberInputFocusFirst = true;
                    return;
                }

                if (!isValidPhoneNumber() || !isValidPrefix()) {
                    setButtonAvailability(addNumberButton, false);
                    displayCustomEditTextError(phoneNumberInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), false, errorShownForPrefix);
                    errorShownForPhoneNumber = true;
                    return;
                }

                displayCustomEditTextError(phoneNumberInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true, errorShownForPrefix);
                errorShownForPhoneNumber = false;

                setButtonAvailability(addNumberButton, true);
            }
        });

        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidPrefix() && isValidPhoneNumber()) {
                    setButtonAvailability(addNumberButton, true);
                    return;
                }
                setButtonAvailability(addNumberButton, false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberInput.setOnErrorIconClickListener(new OnErrorIconClickListener() {
            @Override
            public void onErrorIconClickListener() {
                if(phoneNumberInput.isErrorIconTap()) {
                    if (errorGeneral) {
                        displayCustomError(phoneNumberInput, prefixInput,dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMsisdnNotVodafoneError(), true);
                        errorGeneral = false;
                        return;
                    }
                    if (errorShownForPrefix && errorShownForPhoneNumber) {
                        displayCustomError(phoneNumberInput, prefixInput,dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMsisdnNotVodafoneError(), true);
                        errorShownForPrefix = false;
                        errorShownForPhoneNumber = false;
                        return;
                    }
                    displayCustomEditTextError(phoneNumberInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true, errorShownForPrefix);
                    errorShownForPhoneNumber = false;
                }
            }
        });
    }

    private void setEventListenersForPrefixInput() {
        prefixInput.setLongClickable(false);
        prefixInput.setTextIsSelectable(false);

        prefixInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        prefixInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    prefixInput.clearFocus();
                    return true;
                }
                return false;
            }
        });

        prefixInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isItemClicked = false;
                    if (errorGeneral) {
                        displayCustomError(phoneNumberInput, prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true);
                        errorGeneral = false;
                    }
                    displayCustomAutoCompleteEditTextError(prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true, errorShownForPhoneNumber);
                    errorShownForPrefix = false;
                    return;
                }

                if (!isValidPrefix()) {
                    setButtonAvailability(addNumberButton, false);
                    displayCustomAutoCompleteEditTextError(prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), false, errorShownForPhoneNumber);
                    errorShownForPrefix = true;
                    return;
                }

                displayCustomAutoCompleteEditTextError(prefixInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), true, errorShownForPhoneNumber);
                errorShownForPrefix = false;

                if (isValidPhoneNumber()) {
                    setButtonAvailability(addNumberButton, true);
                    return;
                }
                setButtonAvailability(addNumberButton, false);
            }
        });

        prefixInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceFirst("\\+", "");
                if (!s.toString().equals(result)) {
                    prefixInput.setText(result); // "edit" being the EditText on which the TextWatcher was set
                    prefixInput.setSelection(result.length()); // to set the cursor at the end of the current text
                    return;
                }

                if (isValidPrefix() && isValidPhoneNumber())
                    setButtonAvailability(addNumberButton, true);
                else
                    setButtonAvailability(addNumberButton, false);

                if (phoneNumberInputFocusFirst) {
                    errorShownForPhoneNumber = !(isValidPhoneNumber() && isValidPrefix());
                    displayCustomEditTextWhenPrefixChanged(phoneNumberInput, dividerPrefixAndMsisdn, phoneNumberInputError, YourProfileLabels.getUnlimitedMandatoryFieldError(), isValidPhoneNumber() && isValidPrefix());
                }

            }
        });

        prefixInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isItemClicked = true;

                if (getContext() != null) {
                    InputMethodManager in = (InputMethodManager) parent.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(parent.getApplicationWindowToken(), 0);
                }

                prefixInput.clearFocus();

                if (isValidPhoneNumber()) {
                    setButtonAvailability(addNumberButton, true);
                    return;
                }

                setButtonAvailability(addNumberButton, false);
            }
        });
    }

    private void inflateNoPhoneNumberRegisteredCard() {
        if (getContext() == null)
            return;

        CenteredImageWithTextsCard centeredImageWithTextsCard = new CenteredImageWithTextsCard(getContext());
        centeredImageWithTextsCard
                .setFirstText(YourProfileLabels.getUnlimitedNoPhoneNumberRegisteredText())
                .makeFirstTextNotBold()
                .setSecondText(null);
        centeredImageWithTextsCard.setCardMarginsInDp(0,0,0, 20);

        parent.addView(centeredImageWithTextsCard);
    }

    private void inflatePhoneNumberListCard() {
        if (getActivity() == null || getContext() == null)
            return;

        if (getPresenter() == null)
            return;

        if (getPresenter().getNumberOfMsisdns() <= 0) {
            inflateNoPhoneNumberRegisteredCard();
            return;
        }

        View viewCard = View.inflate(getActivity(), R.layout.card_ion_msisdn_list, null);
        ((VodafoneTextView) viewCard.findViewById(R.id.msisdn_title)).setText(YourProfileLabels.getUnlimitedMsisdnListCardTitle());
        ((VodafoneTextView) viewCard.findViewById(R.id.msisdn_sub_title)).setText(IonController.getMsisdnListCardSubTitle(getPresenter().getMaximumLimit()));
        removeNumberButton = viewCard.findViewById(R.id.card_button);
        msisdnRecyclerView = viewCard.findViewById(R.id.msisdn_recycler_view);
        msisdnRecyclerView.setHasFixedSize(true);
        unlimitedAdapter = new IonUnlimitedAdapter(getContext(), getActivity(), getPresenter().getMsisdnList(), new IonUnlimitedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, IonModel item) {
                selectedMsisdn = item.getMsisdn();
                item.setSelected(item.isSelected() ? false : true);
                setButtonAvailability(removeNumberButton, item.isSelected());
            }
        });

        msisdnRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        msisdnRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        msisdnRecyclerView.setAdapter(unlimitedAdapter);

        removeNumberButton.setText(getString(R.string.unlimited_ion_delete_msisdn_text));
        setButtonAvailability(removeNumberButton, false);
        removeNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call api 73 delete
                if (!TextUtils.isEmpty(selectedMsisdn))
                    displayConfirmationDialog(selectedMsisdn);
            }
        });

        parent.addView(viewCard);
    }

    private boolean isValidPhoneNumber() {

        String phoneNumber = (phoneNumberInput != null && phoneNumberInput.getText() != null)
                ? phoneNumberInput.getText().toString()
                : null;

        boolean valid = !TextUtils.isEmpty(phoneNumber)
                && phoneNumber.length() >= getPresenter().getMinLengthMsisdnAfterPrefix()
                && phoneNumber.length() <= getPresenter().getMaxLengthMsisdnAfterPrefix();

        return valid;
    }

    private boolean isValidPrefix() {

        String prefix = (prefixInput != null && prefixInput.getText() != null)
                ? "+" + prefixInput.getText().toString()
                : null;

        boolean valid = getPresenter().prefixIsValid(prefix);

        if (phoneNumberInput != null)
            phoneNumberInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(getPresenter().getMaxLengthMsisdnAfterPrefix()) });

        return valid;
    }

    private void setupAddNumberTrackingEvent(){
        UnlimitedItalyTrackingEvent event = new UnlimitedItalyTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "add number";
        journey.eVar82 = "mcare:unlimited italy:button:add number";
        journey.getContextData().put("eVar82", journey.eVar82);
        journey.getContextData().put("event65", journey.event65);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public static class UnlimitedItalyTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "unlimited italy";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "unlimited spain");
            s.channel = "unlimited spain and italy";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "unlimited italy";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }

}
