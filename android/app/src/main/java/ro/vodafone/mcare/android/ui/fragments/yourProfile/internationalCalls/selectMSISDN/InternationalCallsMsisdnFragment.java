package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ro.vodafone.mcare.android.TealiumConstants;
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
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.InternationCallsCostsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.InternationalCallsTokenFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.Validator;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
public class InternationalCallsMsisdnFragment extends YourProfileBaseFragment implements IcrContract.View {
	public static String EXTRA_NUMBER_TYPE = "EXTRA_NUMBER_TYPE";

	public enum NumberType implements Serializable {
		CALLER_NUMBER, RECIPIENT_NUMBER, SEND_TOKEN
	}

	private IcrContract.Presenter presenter;

	private List<NumberType> numberTypes = new ArrayList<>();

	@BindView(R.id.callerPhoneNumberError)
	protected TooltipError txtCallerError;

	@BindView(R.id.viewCaller)
	protected View viewCaller;

	@BindView(R.id.lblCaller)
	protected TextView lblCaller;

	@BindView(R.id.txtCaller)
	protected CustomEditTextCompat txtCaller;

	@BindView(R.id.recipientPhoneNumberError)
	protected TooltipError txtRecipientError;

	@BindView(R.id.viewRecipient)
	protected View viewRecipient;

	@BindView(R.id.lblRecipient)
	protected TextView lblRecipient;

	@BindView(R.id.txtRecipient)
	protected CustomEditTextCompat txtRecipient;


	@BindView(R.id.btnDisplayFee)
	protected TextView btnDisplayFee;


	public static InternationalCallsMsisdnFragment newInstance(NumberType... types) {
		Bundle args = new Bundle();

		if (types != null)
			args.putSerializable(EXTRA_NUMBER_TYPE, new ArrayList<>(Arrays.asList(types)));

		InternationalCallsMsisdnFragment fragment = new InternationalCallsMsisdnFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		basePresenter = presenter = new IcrPresenter(this);

		View v = inflater.inflate(R.layout.fragment_international_calls_msisdn, container, false);
		unbinder = ButterKnife.bind(this, v);

		lblCaller.setText(InternationalCallsLabels.callerNumber());
		lblRecipient.setText(InternationalCallsLabels.recipientNumber());


		txtCaller.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					KeyboardHelper.hideKeyboard(getActivity());
					txtCaller.clearFocus();
					return true;
				}
				return false;
			}
		});

		txtCaller.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (v.hasFocus()) {
					displayEditTextError(txtCallerError, txtCaller, null, false);
				} else {
					KeyboardHelper.hideKeyboard(getActivity());
					if (txtCaller == null) return;

					String errorMessage = validateCaller();
					if (errorMessage != null) {
						KeyboardHelper.hideKeyboard(getActivity());
						displayEditTextError(txtCallerError, txtCaller, errorMessage, true);
					}

				}
			}
		});

		txtCaller.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				btnDisplayFee.setEnabled(validFields());
			}
		});

		txtCaller.setOnErrorIconClickListener(new OnErrorIconClickListener() {
			@Override
			public void onErrorIconClickListener() {
				displayEditTextError(txtCallerError, txtCaller, null, false);
			}
		});

		txtRecipient.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					KeyboardHelper.hideKeyboard(getActivity());
					txtRecipient.clearFocus();
					return true;
				}
				return false;
			}
		});

		txtRecipient.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (v.hasFocus()) {
					displayEditTextError(txtRecipientError, txtRecipient, null, false);
				} else {
					KeyboardHelper.hideKeyboard(getActivity());
					if (txtRecipient == null) return;

					String errorMessage = validateRecipient();
					if (errorMessage != null) {
						displayEditTextError(txtRecipientError, txtRecipient, errorMessage, true);
					}

				}
			}
		});

		txtRecipient.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				btnDisplayFee.setEnabled(validFields());
			}
		});

		txtRecipient.setOnErrorIconClickListener(new OnErrorIconClickListener() {
			@Override
			public void onErrorIconClickListener() {
				displayEditTextError(txtRecipientError, txtRecipient, null, false);
			}
		});


		btnDisplayFee.setText(InternationalCallsLabels.showFee());

		return v;
	}

	boolean validFields() {

		if (!numberTypes.contains(NumberType.CALLER_NUMBER))
			return validateRecipient() == null;
		else
			return validateRecipient() == null && validateCaller() == null;

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getArguments() != null && getArguments().containsKey(EXTRA_NUMBER_TYPE)) {
			Bundle args = getArguments();

			setupView((ArrayList<NumberType>) args.getSerializable(EXTRA_NUMBER_TYPE));

			User user = VodafoneController.getInstance().getUser();

			if (!numberTypes.contains(NumberType.CALLER_NUMBER)) {
				if (user instanceof ResCorp) {
					ResCorp resCorpUser = ((ResCorp) user);
					if (resCorpUser.isFullLoggedIn() && UserSelectedMsisdnBanController.getInstance().getNumberOfSubscribers() > 1)
						activityInterface.getNavigationHeader().buildMsisdnSelectorHeader();

				} else if (user instanceof ChooserUser || user instanceof DelegatedChooserUser || user instanceof AuthorisedPersonUser)
					activityInterface.getNavigationHeader().buildMsisdnSelectorHeader();
			}

		}
	}

	@Override
	public void onResume() {
		super.onResume();

		btnDisplayFee.setEnabled(validFields());
	}

	@Override
	public void setupView(List<NumberType> numberTypes) {
		stopLoadingDialog();

		this.numberTypes.clear();
		this.numberTypes.addAll(numberTypes);

		viewCaller.setVisibility(numberTypes.contains(NumberType.CALLER_NUMBER) ? View.VISIBLE : View.GONE);
		viewRecipient.setVisibility(numberTypes.contains(NumberType.RECIPIENT_NUMBER) ? View.VISIBLE : View.GONE);

		btnDisplayFee.setEnabled(validFields());

		InternationalCallsMsisdnTrackingEvent event = new InternationalCallsMsisdnTrackingEvent(numberTypes);
		VodafoneController.getInstance().getTrackingService().track(event);
		trackView(viewRecipient.isShown(), viewCaller.isShown());
	}

	@Override
	public String getTitle() {
		return InternationalCallsLabels.internationalCallsCardTitle();
	}


	private String validateRecipient() {
		String input = txtRecipient.getText().toString();

		if (input.length() > 20 || input.length() < 4)
			return InternationalCallsLabels.invalidPrefixTooltip();

		if (!input.startsWith("00"))
			return InternationalCallsLabels.invalidPrefixTooltip();

		if (input.startsWith("0040"))
			return InternationalCallsLabels.invalidNumberTooltip();

		return null;
	}

	private String validateCaller() {
		String input = txtCaller.getText().toString();


		if (new Validator().isPhoneValid(input) != Validator.VALIDE_FIELD_STATUS)
			return SettingsLabels.getBadPhoneNumber();

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
		txtCaller.clearFocus();
		txtRecipient.clearFocus();

		showLoadingDialog();

		if (!numberTypes.contains(NumberType.CALLER_NUMBER)) {      // own
			presenter.getRatesOwn(txtRecipient.getText().toString());
			TealiumHelper.tealiumTrackEvent(InternationalCallsMsisdnFragment.class.getSimpleName(),
					btnDisplayFee.getText().toString(), TealiumConstants.yourAccountInternationalCallsScreenName, "button=");
		} else {                                                        // other
			presenter.getRatesOther(txtCaller.getText().toString(), txtRecipient.getText().toString());
			TealiumHelper.tealiumTrackEvent(InternationalCallsMsisdnFragment.class.getSimpleName(),
					btnDisplayFee.getText().toString(), TealiumConstants.otherAccountInternationalCallsScreenName, "button=");
		}

		setupShowTarrifButtonTracking();
	}

	@Override
	public void showCosts(String callerPhone, String calledPhone, Float rate, boolean isOwn) {
		stopLoadingDialog();
		KeyboardHelper.hideKeyboard(getActivity());
		activityInterface.attachFragment(InternationCallsCostsFragment.newInstance(callerPhone, calledPhone, rate, isOwn));
	}


	@Override
	public void showToken(String callerPhone, String calledPhone) {
		stopLoadingDialog();
		KeyboardHelper.hideKeyboard(getActivity());

		activityInterface.attachFragment(InternationalCallsTokenFragment.newInstance(callerPhone, calledPhone));
	}

	@Override
	public void showToast(String message, boolean isSuccess, boolean isError) {
		stopLoadingDialog();
		new CustomToast.Builder(getActivity()).message(message).success(isSuccess).errorIcon(isError).show();
	}

	@Override
	public void showCallerError(String message) {
		stopLoadingDialog();
		displayEditTextError(txtCallerError, txtCaller, message, true);
	}

	@Override
	public void showSMSTokenError(String message) {

	}

	@Override
	public void trackView(boolean own, boolean other) {
		if (other) {
			TealiumHelper.tealiumTrackView(InternationalCallsMsisdnFragment.class.getSimpleName(),
					TealiumConstants.yourProfileJourney, TealiumConstants.otherAccountInternationalCallsScreenName);
		} else if (own) {
			TealiumHelper.tealiumTrackView(InternationalCallsMsisdnFragment.class.getSimpleName(),
					TealiumConstants.yourProfileJourney, TealiumConstants.yourAccountInternationalCallsScreenName);
		}
	}

	private void setupShowTarrifButtonTracking(){
        InternationalCallsMsisdnTrackingEvent event = new InternationalCallsMsisdnTrackingEvent(numberTypes);
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "show tarrif";
        journey.getContextData().put("event65", journey.event65);

        if(numberTypes.contains(NumberType.CALLER_NUMBER)){
            journey.eVar82 = "mcare:international calling rates another account:button:show tarrif";
        } else if (numberTypes.contains(NumberType.RECIPIENT_NUMBER)) {
            journey.eVar82 = "mcare:international calling rates your account:button:show tarrif";
        }
        journey.getContextData().put("eVar82", journey.eVar82);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }


    public static class InternationalCallsMsisdnTrackingEvent extends TrackingEvent{
        private List<NumberType> mNumberTypes;
        public InternationalCallsMsisdnTrackingEvent(List<NumberType> numberTypes) {
            mNumberTypes = numberTypes;
        }

        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            if(mNumberTypes.contains(NumberType.CALLER_NUMBER)){
                s.pageName = s.prop21 + "international calling rates another account";
                s.prop21 = "mcare:" + "international calling rates another account";
            } else if (mNumberTypes.contains(NumberType.RECIPIENT_NUMBER)) {
                s.pageName = s.prop21 + "international calling rates your account";
                s.prop21 = "mcare:" + "international calling rates your account";
            }
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, s.pageName);
            s.getContextData().put("prop21", s.prop21);

            s.channel = "international calling rates";
            s.getContextData().put("&&channel", s.channel);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}
