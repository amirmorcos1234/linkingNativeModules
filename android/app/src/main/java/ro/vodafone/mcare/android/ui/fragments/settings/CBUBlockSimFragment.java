package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user2 on 4/11/2017.
 */

public class CBUBlockSimFragment extends BaseBlockSimFragment implements InputEventsListenerInterface {

    private final static String TAG = CBUBlockSimFragment.class.getSimpleName();

    public static CBUBlockSimFragment newInstance() {

        CBUBlockSimFragment fragment = new CBUBlockSimFragment();
        return fragment;
    }

    public CBUBlockSimFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        blockSimButton.setOnClickListener(onPutSimStatusListener);
    }

    public void setupUnblockedSimLayout() {
        simBLockStatusTextView.setText(SettingsLabels.getSimStatusUnblocked());
        simBLockStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.v_icon_green));

        blockSimTitleText.setText(SettingsLabels.getUnBlockSimConsequencesTitle());
        blockSimTextOne.setText(SettingsLabels.getUnBlockSimConsequencesMessageOne());
        blockSimTextTwo.setText(SettingsLabels.getUnBlockSimConsequencesMessageTwo());

        blockSimButton.setText(SettingsLabels.getSimLockButton());
    }

    public void setupBlockedSimLayout() {
        simBLockStatusTextView.setText(SettingsLabels.getSimStatusBlocked());
        simBLockStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.x_icon_red));

        blockSimTitleText.setText(SettingsLabels.getBlockSimConsequencesTitle());
        blockSimTextOne.setText(SettingsLabels.getBlockSimConsequencesMessageOne());
        blockSimTextTwo.setText(SettingsLabels.getBlockSimConsequencesMessageTwo());
        blockSimTextTwo.setTypeface(null, Typeface.BOLD);

        blockSimButton.setText(SettingsLabels.getSimUnlockButton());
    }

    private View.OnClickListener onPutSimStatusListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            KeyboardHelper.hideKeyboard(getActivity());

            if (alternativePhoneNumberEt != null)
                if (!alternativePhoneNumberEt.validateCustomEditText()) {
                    alternativePhoneNumberEt.setBackgroundResource(R.drawable.red_error_input_border);
                }

            boolean isAlternativePhoneNumberOk = checkAlternativePhoneNumber();

            if (isAlternativePhoneNumberOk) {
                if (simActive) {
                    blockSimTealiumEvent();

                    final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
                    overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
                    overlayDialog.show();

                    VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
                    VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);
                    overlayTitle.setText(SettingsLabels.getBlockSimOverlayTitle());
                    overlaySubtext.setText(SettingsLabels.getBlockOverlayMessage());

                    Button overlayBlockButton = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
                    Button overlayCancelBlockButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);

                    ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

                    overlayBlockButton.setText(SettingsLabels.getBlockSimOverlayBlockButton());
                    overlayCancelBlockButton.setText(SettingsLabels.getBlockSimOverlayCancelBlockButton());

                    overlayCancelBlockButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            overlayDialog.dismiss();
                        }
                    });

                    overlayBlockButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            overlayDialog.dismiss();
                            showLoadingDialog();
                            String alternativePhoneNumber = alternativePhoneNumberEt.getText().toString();
                            blockSimCommunicationListener.sendBlockSimRequest(alternativePhoneNumber);
                            disableButton();
                        }
                    });

                    overlayDismissButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            overlayDialog.dismiss();
                        }
                    });

                    overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            overlayDialog.dismiss();
                        }
                    });

                } else {
                    unblockSimTealiumEvent();

                    showLoadingDialog();
                    String alternativePhoneNumber = alternativePhoneNumberEt.getText().toString();
                    blockSimCommunicationListener.sendUnblockSimRequest(alternativePhoneNumber);
                    disableButton();
                }
            } else {
                badPhoneNumberLayout.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public void displayErrorMessage() {
        if (alternativePhoneNumberEt != null)
            if (alternativePhoneNumberEt.isValide() != CustomEditText.VALIDE_FIELD_STATUS && alternativePhoneNumberEt.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            if (!PhoneNumberUtils.isValidPhoneNumber(alternativePhoneNumberEt.getText().toString()) && alternativePhoneNumberEt.isValide() == CustomEditText.INVALID_FORMAT_FIELD_STATUS) {
                badPhoneNumberLayout.setVisibility(View.VISIBLE);
                disableButton();
            }
        }
    }

    @Override
    public void hideErrorMessage() {
        if(badPhoneNumberLayout != null) {
            badPhoneNumberLayout.setVisibility(View.GONE);
        }
        if (alternativePhoneNumberEt != null)
            if (PhoneNumberUtils.isValidPhoneNumber(alternativePhoneNumberEt.getText().toString()) && alternativePhoneNumberEt.isHighlighted()) {
                alternativePhoneNumberEt.removeHighlight();
                enableButton();
            }
    }

    @Override
    public void activateButton() {

    }

    @Override
    public void inactivateButton() {

    }
}
