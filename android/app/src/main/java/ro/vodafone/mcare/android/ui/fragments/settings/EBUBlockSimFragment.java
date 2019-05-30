package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Serban Radulescu on 3/29/2018.
 */

public class EBUBlockSimFragment extends BaseBlockSimFragment {

	public static EBUBlockSimFragment newInstance() {

		EBUBlockSimFragment fragment = new EBUBlockSimFragment();
		return fragment;
	}

	public EBUBlockSimFragment() {
	}


	@Override
	public void onStart() {
		super.onStart();
		alternativePhoneNumberTextView.setVisibility(View.GONE);
		badPhoneNumberLayout.setVisibility(View.GONE);
		alternativePhoneNumberEt.setVisibility(View.GONE);
		blockSimButton.setOnClickListener(onPutSimStatusListener);
	}

	public void setupUnblockedSimLayout() {
		simBLockStatusTextView.setText(SettingsLabels.getSimStatusUnblocked());
		simBLockStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.v_icon_green));

		blockSimTitleText.setText(SettingsLabels.getEbuUnBlockSimConsequencesTitle());
		blockSimTextOne.setText(SettingsLabels.getEbuUnBlockSimConsequencesMessageOne());
		blockSimTextTwo.setText(SettingsLabels.getEbuUnBlockSimConsequencesMessageTwo());
		blockSimTextTwo.setTypeface(Fonts.getVodafoneRG());

		blockSimButton.setText(SettingsLabels.getSimLockButton());
		enableButton();
	}

	public void setupBlockedSimLayout() {
		simBLockStatusTextView.setText(SettingsLabels.getSimStatusBlocked());
		simBLockStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.x_icon_red));

		blockSimTitleText.setText(SettingsLabels.getEbuBlockSimConsequencesTitle());
		blockSimTextOne.setText(SettingsLabels.getEbuBlockSimConsequencesMessageOne());
		blockSimTextTwo.setText(SettingsLabels.getEbuBlockSimConsequencesMessageTwo());
		blockSimTextTwo.setTypeface(null, Typeface.BOLD);


		blockSimButton.setText(SettingsLabels.getSimUnlockButton());
		enableButton();
	}

	private View.OnClickListener onPutSimStatusListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			KeyboardHelper.hideKeyboard(getActivity());
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
						blockSimCommunicationListener.sendEbuBlockSimRequest();
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
				blockSimCommunicationListener.sendEbuUnblockSimRequest();
				disableButton();
			}
		}
	};
}
