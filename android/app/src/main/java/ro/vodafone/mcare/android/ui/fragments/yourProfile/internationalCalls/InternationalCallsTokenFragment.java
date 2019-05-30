package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.InternationalCallsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN.IcrContract;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN.IcrPresenter;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN.InternationalCallsMsisdnFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.utils.Validator;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

public class InternationalCallsTokenFragment extends YourProfileBaseFragment implements IcrContract.View {
	public static final String EXTRA_CALLER_PHONE = "EXTRA_CALLER_PHONE";
	public static final String EXTRA_CALLED_PHONE = "EXTRA_CALLED_PHONE";


	private IcrContract.Presenter presenter;

	private String callerPhone;
	private String calledPhone;


	@BindView(R.id.lblToken)
	protected TextView lblToken;

	@BindView(R.id.txtToken)
	protected CustomEditTextCompat txtToken;

	@BindView(R.id.txtTokenError)
	protected TooltipError txtTokenError;

	@BindView(R.id.btnResendToken)
	protected TextView btnResendToken;

	@BindView(R.id.btnDisplayFee)
	protected TextView btnDisplayFee;


	public static InternationalCallsTokenFragment newInstance(String callerPhone, String calledPhone) {
		Bundle args = new Bundle();

		if (callerPhone != null)
			args.putString(EXTRA_CALLER_PHONE, callerPhone);

		if (calledPhone != null)
			args.putString(EXTRA_CALLED_PHONE, calledPhone);

		InternationalCallsTokenFragment fragment = new InternationalCallsTokenFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (activityInterface != null)
			activityInterface.getNavigationHeader().hideSelectorView();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		basePresenter = presenter = new IcrPresenter(this);

		View v = inflater.inflate(R.layout.fragment_international_calls_token, container, false);
		unbinder = ButterKnife.bind(this, v);

		txtToken.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					KeyboardHelper.hideKeyboard(getActivity());
					txtToken.clearFocus();
					return true;
				}
				return false;
			}
		});

		txtToken.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (v.hasFocus()) {
					displayEditTextError(txtTokenError, txtToken, null, false);
				} else {
					KeyboardHelper.hideKeyboard(getActivity());
					if (txtToken == null) return;

					String errorMessage = validateToken();
					if (errorMessage != null) {
						KeyboardHelper.hideKeyboard(getActivity());
						displayEditTextError(txtTokenError, txtToken, errorMessage, true);
					}

				}
			}
		});

		txtToken.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				btnDisplayFee.setEnabled(validateToken() == null);
			}
		});

		txtToken.setOnErrorIconClickListener(new OnErrorIconClickListener() {
			@Override
			public void onErrorIconClickListener() {
				displayEditTextError(txtTokenError, txtToken, null, false);
			}
		});


		lblToken.setText(InternationalCallsLabels.enterSmsToken());
		txtToken.setHint(InternationalCallsLabels.smsCode());
		btnResendToken.setText(InternationalCallsLabels.resendToken());

		btnDisplayFee.setText(InternationalCallsLabels.showFee());

		InternationalCallsTokenTrackingEvent event = new InternationalCallsTokenTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);
		return v;
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		Bundle args = getArguments();


		if (args.containsKey(EXTRA_CALLER_PHONE))
			callerPhone = args.getString(EXTRA_CALLER_PHONE);
		else
			callerPhone = null;

		if (args.containsKey(EXTRA_CALLED_PHONE))
			calledPhone = args.getString(EXTRA_CALLED_PHONE);
		else
			calledPhone = null;
	}

	@Override
	public void onResume() {
		super.onResume();

		btnDisplayFee.setEnabled(validateToken() == null);

	}

	@Override
	public void setupView(List<InternationalCallsMsisdnFragment.NumberType> numberTypes) {
		stopLoadingDialog();

		txtToken.setText("");
		btnDisplayFee.setEnabled(false);

	}

	@Override
	public String getTitle() {
		return InternationalCallsLabels.internationalCallsCardTitle();
	}


	private String validateToken() {
		String input = txtToken.getText().toString();


		if (new Validator().isICRToeknValid(input) != Validator.VALIDE_FIELD_STATUS)
			return InternationalCallsLabels.invalidSmsToken();

		return null;
	}


	private void displayEditTextError(TooltipError inputTooltipError, CustomEditTextCompat targetEditText, String message, boolean errorVisible) {
		inputTooltipError.setText(message);
		if (errorVisible)
			inputTooltipError.setVisibility(View.VISIBLE);
		else
			inputTooltipError.setVisibility(View.GONE);

		if (errorVisible) {
			targetEditText.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
			btnDisplayFee.setEnabled(false);
		} else {
			if (targetEditText.hasFocus())
				targetEditText.setBackgroundresourceAndFieldIcon(R.drawable.onfocus_input_border);
			else
				targetEditText.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
		}

	}


	@OnClick(R.id.btnDisplayFee)
	protected void displayFeeClicked() {
		KeyboardHelper.hideKeyboard(getActivity());
		showLoadingDialog();
		txtToken.clearFocus();
		presenter.getRates(callerPhone, calledPhone, txtToken.getText().toString());
		setupShowTarrifButtonTracking();
	}

	@Override
	public void showCosts(String callerPhone, String calledPhone, Float rate, boolean isOwn) {
		stopLoadingDialog();
		KeyboardHelper.hideKeyboard(getActivity());

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.popBackStack();

		activityInterface.attachFragment(InternationCallsCostsFragment.newInstance(callerPhone, calledPhone, rate, isOwn));
	}

	@OnClick(R.id.btnResendToken)
	protected void resendTokenClicked() {
		showLoadingDialog();
		presenter.resendToken(callerPhone);
		resendTokenButtonTracking();
	}

	@Override
	public void showToken(String callerPhone, String calledPhone) {

	}

	@Override
	public void showToast(String message, boolean isSuccess, boolean isError) {
		stopLoadingDialog();
		new CustomToast.Builder(getActivity()).message(message).success(isSuccess).errorIcon(isError).show();
	}

	@Override
	public void showCallerError(String message) {

	}

	@Override
	public void showSMSTokenError(String message) {
		stopLoadingDialog();
		displayEditTextError(txtTokenError, txtToken, message, true);
	}

	@Override
	public void trackView(boolean own, boolean other) {

	}

	public static class InternationalCallsTokenTrackingEvent extends TrackingEvent {
		@Override
		protected void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);
			if (getErrorMessage() != null) {
				s.event11 = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "international calling rates token page";
			s.prop21 = "mcare:" + "international calling rates token page";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, s.pageName);
			s.getContextData().put("prop21", s.prop21);

			s.channel = "international calling rates";
			s.getContextData().put("&&channel", s.channel);
			s.eVar5 = "content";
			s.getContextData().put("eVar5", s.eVar5);
		}
	}

	private void resendTokenButtonTracking(){
		InternationalCallsTokenTrackingEvent event = new InternationalCallsTokenTrackingEvent();
		TrackingAppMeasurement journey = new TrackingAppMeasurement();
		journey.event65 = "resend token";
		journey.getContextData().put("event65", journey.event65);
		journey.eVar82 = "mcare:international calling rates token page:button:resend token";
		journey.getContextData().put("eVar82", journey.eVar82);
		event.defineTrackingProperties(journey);
		VodafoneController.getInstance().getTrackingService().trackCustom(event);
	}

	private void setupShowTarrifButtonTracking(){
		InternationalCallsTokenTrackingEvent event = new InternationalCallsTokenTrackingEvent();
		TrackingAppMeasurement journey = new TrackingAppMeasurement();
		journey.event65 = "show tarrif";
		journey.getContextData().put("event65", journey.event65);
		journey.eVar82 = "mcare:international calling rates token page:button:show tarrif";
		journey.getContextData().put("eVar82", journey.eVar82);
		event.defineTrackingProperties(journey);
		VodafoneController.getInstance().getTrackingService().trackCustom(event);
	}
}
