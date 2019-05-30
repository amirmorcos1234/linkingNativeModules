package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.IonController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.custom.PrefixCustomAutoCompleteEditText;
import ro.vodafone.mcare.android.ui.activities.yourProfile.YourProfileActivity;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.mvp.BaseMVPFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

public abstract class UnlimitedBaseFragment extends BaseMVPFragment<Contract.ContractPresenter> implements Contract.ContractView {

    protected LinearLayout parent;
    protected Dialog overlayDialog;
    private NavigationHeader navigationHeader;

    protected abstract void initFlow();

    protected abstract void inflateIonInactive();

    protected abstract void displayPhoneNumberInputError(String errorMessage);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((YourProfileActivity) getActivity()).getNavigationHeader().setTitle(YourProfileLabels.getUnlimitedIonCardTitle());
    }

    @Override
    public View onCreateFragmentView() {
        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.parent_padding);

        getPresenter().initRealm();

        ViewGroup.LayoutParams paramsParent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent = new LinearLayout(getContext());
        parent.setLayoutParams(paramsParent);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, 0);
        parent.setVisibility(View.GONE);

        navigationHeader = ((YourProfileActivity) getActivity()).getNavigationHeader();
        showMsisdnSelector();

        initFlow();

        return parent;
    }

    @Override
    public Contract.ContractPresenter bindPresenter() {
        return new UnlimitedBasePresenter(this);
    }

    private void showMsisdnSelector() {
        navigationHeader.displayDefaultHeader();
        if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
            navigationHeader.showMsisdnSelector();
            return;
        }
        navigationHeader.hideSelectorViewWithoutTriangle();
    }


    @Override
    public void manageErrorCodes(String code, int error_type) {
        switch (code) {
            case "EC07301":
                inflateIonInactive();
                break;
            case "EC07302":
                displayPhoneNumberInputError(YourProfileLabels.getUnlimitedMsisdnNotVodafoneError());
                break;
            default:
                if (error_type == IonController.LAYOUT_ION_ERROR) {
                    inflateErrorOrWarningLayout(YourProfileLabels.getUnlimitedIonSystemErrorLayout(), true);
                    return;
                }
                new CustomToast.Builder(getContext()).message(YourProfileLabels.getUnlimitedIonSystemErrorToast()).success(false).show();
        }
    }

    @Override
    public void redirectVovInDashboardAfterPut() {
        new CustomToast.Builder(getContext()).message(YourProfileLabels.getUnlimitedRegisteredRequestForPutToast()).success(true).show();
        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Ion_Unlimited, null,
                YourProfileLabels.getUnlimitedRegisteredRequestForPutVov(), "Ok, am înţeles", null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }

    @Override
    public void redirectVovInDashboardAfterDelete() {
        new CustomToast.Builder(getContext()).message(YourProfileLabels.getUnlimitedRegisteredRequestForDeleteToast()).success(true).show();
        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Ion_Unlimited, null,
                YourProfileLabels.getUnlimitedRegisteredRequestForDeleteVov(), "Ok, am înţeles", null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }

    @Override
    public void dismissDialogIfSuccessful() {
        if (overlayDialog != null)
            overlayDialog.dismiss();
    }

    @Override
    public void setParentVisibility(int visibility) {
        parent.setVisibility(visibility);
    }

    protected void inflateErrorOrWarningLayout(String message, final boolean isSystemError) {
        parent.removeAllViews();

        if (getActivity() == null)
            return;

        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());

        errorCard.setCardMarginsInDp(0,0,0,20);

        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSystemError)
                    initFlow();
            }
        });

        errorCard.showError(true, message);
        parent.addView(errorCard);
    }

    protected void displayCustomEditTextError(CustomEditTextCompat target, View view, LinearLayout errorLayout, String errorText, boolean visibility, boolean otherError) {
        if (!visibility || otherError)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.add_msisdn_padding);

        if (!target.hasFocus()) {
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border_ion_phone_number, R.drawable.error_icon);
                ((TextView) errorLayout.findViewById(R.id.error_text_message)).setText(errorText);
                view.setVisibility(otherError ? View.GONE : View.VISIBLE);
            } else {
                target.setBackgroundresourceAndFieldIcon(R.drawable.ion_msisdn_background);
                target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                view.setVisibility(View.VISIBLE);
            }
        } else {
            target.setBackgroundresourceAndFieldIcon(R.drawable.ion_msisdn_focus);
            view.setVisibility(otherError ? View.GONE : View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected void displayCustomEditTextWhenPrefixChanged(CustomEditTextCompat target, View view, LinearLayout errorLayout, String errorText, boolean visibility) {
        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.add_msisdn_padding);

        if (!target.hasFocus()) {
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border_ion_phone_number, R.drawable.error_icon);
                ((TextView) errorLayout.findViewById(R.id.error_text_message)).setText(errorText);
                view.setVisibility(View.GONE);
            } else {
                target.setBackgroundresourceAndFieldIcon(R.drawable.ion_msisdn_background);
                target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void displayCustomAutoCompleteEditTextError(PrefixCustomAutoCompleteEditText target, View view, LinearLayout errorLayout, String errorText, boolean visibility, boolean otherError) {
        if (!visibility || otherError)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.add_msisdn_padding);

        if (!target.hasFocus()) {
            if (!visibility) {
                target.setBackgroundResource(R.drawable.red_error_input_border_prefix);
                ((TextView) errorLayout.findViewById(R.id.error_text_message)).setText(errorText);
                view.setVisibility(otherError ? View.GONE : View.VISIBLE);
            } else {
                target.setBackgroundResource(R.drawable.prefix_background);
                target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                view.setVisibility(View.VISIBLE);
            }
        } else {
            target.setBackgroundResource(R.drawable.prefix_focus);
            view.setVisibility(otherError ? View.GONE : View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected void displayCustomError(CustomEditTextCompat target, PrefixCustomAutoCompleteEditText target2, View view, LinearLayout errorLayout, String errorText, boolean visibility) {
        if (target == null || target2 == null || errorLayout == null)
            return;

        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.add_msisdn_padding);

        if (!target.hasFocus() && !target2.hasFocus()) {
            if (!visibility) {
                target2.setBackgroundResource(R.drawable.red_error_input_border_prefix_inset);
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border_ion_phone_number, R.drawable.error_icon);
                target2.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                ((TextView) errorLayout.findViewById(R.id.error_text_message)).setText(errorText);
                view.setVisibility(View.GONE);
            } else {
                target2.setBackgroundResource(R.drawable.prefix_background);
                target.setBackgroundresourceAndFieldIcon(R.drawable.ion_msisdn_background);
                target2.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
                view.setVisibility(View.VISIBLE);
            }
        } else {
            target2.setBackgroundResource(R.drawable.prefix_background);
            target.setBackgroundresourceAndFieldIcon(R.drawable.ion_msisdn_background);
            target2.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
            target.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);
            view.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected void displayConfirmationDialog(final String ionPhoneNumber) {
        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle)).setText(YourProfileLabels.getUnlimitedConfirmationOverlayTitle());
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext)).setText(YourProfileLabels.getUnlimitedConfirmationOverlaySubTitle());
        overlayDialog.findViewById(R.id.overlaySubtext2).setVisibility(View.GONE);
        VodafoneButton buttonOn = overlayDialog.findViewById(R.id.buttonKeepOn);
        VodafoneButton buttonOff = overlayDialog.findViewById(R.id.buttonTurnOff);

        buttonOn.setText(getString(R.string.unlimited_ion_confirmation_on_button));
        buttonOff.setText(getString(R.string.unlimited_ion_confirmation_off_button));

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().deleteUnlimitedIon(getContext(), ionPhoneNumber);
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayDialog.findViewById(R.id.overlayDismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED)
            initFlow();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    protected void setButtonAvailability(VodafoneButton button, boolean availability) {
        button.setEnabled(availability);
        button.setClickable(availability);
    }

}
