package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.interfaces.fragment.settings.BlockSimCommunicationListener;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.BlockSimActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Serban Radulescu on 3/29/2018.
 */

public class BaseBlockSimFragment extends BaseFragment {

	private final static String TAG = BaseBlockSimFragment.class.getSimpleName();

	BlockSimCommunicationListener blockSimCommunicationListener;

	private ViewGroup blockSimContainer;

	private NavigationHeader navigationHeader;

	@BindView(R.id.changeSimStateButton)
	Button blockSimButton;

	@BindView(R.id.alternativePhoneNumberText)
	VodafoneTextView alternativePhoneNumberTextView;

	@BindView(R.id.badPhoneNumberLayout)
	LinearLayout badPhoneNumberLayout;

	@BindView(R.id.alternativePhoneNumber_input)
	CustomEditText alternativePhoneNumberEt;


	@BindView(R.id.simBlockStatus)
	VodafoneTextView simBLockStatusTextView;


	@BindView(R.id.blockSimTextOne)
	VodafoneTextView blockSimTextOne;

	@BindView(R.id.blockSimTextTwo)
	VodafoneTextView blockSimTextTwo;

	@BindView(R.id.blockSimTitle)
	VodafoneTextView blockSimTitleText;

	@BindView(R.id.error_loading_card)
	VodafoneGenericCard vodafoneGenericCard;

	private Unbinder unbinder;

	boolean simActive;

	public static BaseBlockSimFragment newInstance() {

		BaseBlockSimFragment fragment = new BaseBlockSimFragment();
		return fragment;
	}

	public BaseBlockSimFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_settings_block_sim, container, false);
		blockSimContainer = v.findViewById(R.id.block_sim_container);

		unbinder = ButterKnife.bind(this, v);


		configureHeaderForUser();
		showLoadingDialog();
		if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
			blockSimCommunicationListener.checkEbuSimStatus();
		} else {
			blockSimCommunicationListener.checkSimStatus();
		}

		initTealium();

		return v;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		try {
			blockSimCommunicationListener = (BlockSimCommunicationListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement LoyaltyVoucherCommunicationListener");
		}
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public void inflateBlockSimGeneralLayout(boolean simActive) {
		vodafoneGenericCard.setVisibility(View.GONE);
		blockSimContainer.setVisibility(View.VISIBLE);
		this.simActive = simActive;

		alternativePhoneNumberTextView.setText(SettingsLabels.getAlternativePhoneMessage());
		alternativePhoneNumberTextView.setTypeface(Fonts.getVodafoneRG());
		alternativePhoneNumberEt.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//When BACKSPACE is pressed, validate field. (delete a character from editTextField
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					alternativePhoneNumberEt.validateCustomEditText();
				}
				return false;
			}
		});

		alternativePhoneNumberEt.setInputType(InputType.TYPE_CLASS_PHONE);

		disableButton();

		alternativePhoneNumberEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (PhoneNumberUtils.isValidPhoneNumber(charSequence.toString())) {
					enableButton();
				} else {
					disableButton();
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		if (simActive)
			setupUnblockedSimLayout();
		else
			setupBlockedSimLayout();

		makeInputFocusOffOnOutsideTap(blockSimContainer);
		stopLoadingDialog();
	}


	public void inflateErrorLayout() {
		stopLoadingDialog();
		blockSimContainer.setVisibility(View.GONE);
		vodafoneGenericCard.setVisibility(View.VISIBLE);
		vodafoneGenericCard.showError(true, SettingsLabels.getRetryButton());
		vodafoneGenericCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showLoadingDialog();
				configureHeaderForUser();
				if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
					blockSimCommunicationListener.checkEbuSimStatus();
				} else {
					blockSimCommunicationListener.checkSimStatus();
				}
			}
		});

	}

	public void setupUnblockedSimLayout() {
	}

	public void setupBlockedSimLayout() {
	}

	public boolean checkAlternativePhoneNumber() {

		if (alternativePhoneNumberEt != null) {
			String alternativePhoneNumber = alternativePhoneNumberEt.getText().toString();
			return !alternativePhoneNumber.equals("") && alternativePhoneNumber.trim().length() == 10 && alternativePhoneNumber.substring(0, 2).equals("07") && android.text.TextUtils.isDigitsOnly(alternativePhoneNumber);
		} else
			return false;
	}

	public void disableButton() {
		blockSimButton.setEnabled(false);
	}

	public void enableButton() {
		blockSimButton.setEnabled(true);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult() with code: " + requestCode);

		if (data != null) {

			if (resultCode == RESULT_SELECTOR_UPDATED) {
				if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
					blockSimCommunicationListener.checkEbuSimStatus();
				} else {
					blockSimCommunicationListener.checkSimStatus();
				}

				callForAdobeTarget(AdobePageNamesConstants.SETTINGS_SIM_BAR);
			}
		}
	}

	public void configureHeaderForUser() {
		navigationHeader = ((BlockSimActivity) getActivity()).getNavigationHeader();
		if (VodafoneController.getInstance().getUser() instanceof ResCorp
				|| VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
			navigationHeader.showMsisdnSelector();
			navigationHeader.displayDefaultHeader();
		} else {
			navigationHeader.displayDefaultHeader();
			navigationHeader.hideSelectorView();
		}
	}

	private void makeInputFocusOffOnOutsideTap(View v){
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				alternativePhoneNumberEt.clearFocus();
				KeyboardHelper.hideKeyboard(getActivity());
				return true;
			}
		});
	}

	private void initTealium() {
		//Tealium Track View
		Map<String, Object> tealiumMapView =new HashMap(6);
		tealiumMapView.put("screen_name","sim bar");
		tealiumMapView.put("journey_name","settings");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		BaseBlockSimFragment.BlockSimTrackingEvent event = new BaseBlockSimFragment.BlockSimTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);
	}

	@Override
	public void onStart() {
		super.onStart();
     	callForAdobeTarget(AdobePageNamesConstants.SETTINGS_SIM_BAR);
	}

	public static class BlockSimTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "sim bar";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "sim bar");


			s.channel = "settings";
			s.getContextData().put("&&channel", s.channel);
		}
	}

	public void blockSimTealiumEvent() {
		//Tealium Track Event
		Map<String, Object> tealiumMapEvent = new HashMap(6);
		tealiumMapEvent.put("screen_name","sim bar");
		tealiumMapEvent.put("event_name","mcare:sim bar:button:blocheaza");
		tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackEvent("event_name", tealiumMapEvent);
	}

	public void unblockSimTealiumEvent() {
		//Tealium Track Event
		Map<String, Object> tealiumMapEvent = new HashMap(6);
		tealiumMapEvent.put("screen_name", "sim bar");
		tealiumMapEvent.put("event_name", "mcare:sim bar:button:deblocheaza");
		tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackEvent("event_name", tealiumMapEvent);
	}
}
