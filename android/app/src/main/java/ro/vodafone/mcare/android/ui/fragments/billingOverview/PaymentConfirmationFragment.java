package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationRequest;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.PaymentConfirmationLabels;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.PaymentConfirmationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.DecimalDigitsInputFilter;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by George Bitoleanu on 8/9/2017.
 */
public class PaymentConfirmationFragment extends BaseFragment implements OnErrorIconClickListener {

    private static final String TAG = PaymentConfirmationFragment.class.getCanonicalName();

    @BindView(R.id.amount_input)
    CustomEditTextCompat amount_input;
    @BindView(R.id.payment_code_input)
    CustomEditTextCompat payment_code_input;
    @BindView(R.id.contact_input)
    CustomEditTextCompat contact_input;

    @BindView(R.id.bad_amount_layout)
    TooltipError bad_amount_layout;
    @BindView(R.id.bad_phone_layout)
    TooltipError bad_phone_layout;
    @BindView(R.id.bad_payment_layout)
    TooltipError bad_payment_layout;

    @BindView(R.id.payment_code_layout)
    LinearLayout payment_code_layout;

    @BindView(R.id.payment_button)
    VodafoneButton payment_button;

	@BindView(R.id.containerFrame)
	LinearLayout containerFrame;

	@BindView(R.id.scrollView)
	ScrollView scrollView;


    @BindView(R.id.navigation_header)
    NavigationHeader navigationHeader;
    String customerSegment = "null";
    String phone;
    String crmRole = "null";
    boolean opCodeFlag;
    boolean eligibilityFlag;
    private PaymentConfirmationService paymentConfirmationService;

	private OnScrollViewCreatedListener scrollViewCreatedListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        configureHeader();
    initializeNavigationHeader();
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			scrollViewCreatedListener = (OnScrollViewCreatedListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement FragmentLifeCycleListener");
		}
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = View.inflate(getContext(), R.layout.fragment_payment_confirmation, null);
        ButterKnife.bind(this, v);

        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.paymentConfirmation);

        init();
        PaymentConfirmationTrackingEvent event = new PaymentConfirmationTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);


		if (scrollViewCreatedListener != null) {
			scrollViewCreatedListener.onScrollViewCreated((ScrollView) scrollView);
		}
        return v;
    }
    void init() {

        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

        User user = VodafoneController.getInstance().getUser();

        if (profile == null)
            inflateErrorLayout();
        else {

            opCodeFlag = false;
            phone =  UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

            if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() != null)
                crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();

            if (user instanceof EbuMigrated) {
                if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment() != null)
                    customerSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
            } else if (user instanceof CBUUser) {
                customerSegment = profile.getCustomerSegment();
            }

            if ((crmRole.equalsIgnoreCase("Chooser") || crmRole.equalsIgnoreCase("DelegatedChooser") || crmRole.equalsIgnoreCase("AuthorizedPerson"))
                    && (AppConfiguration.getEbuMigratedIneligibleCustomerSegments().contains(customerSegment))) {
                inflateMessageCard();
            } else {
                showLoadingDialog();
                paymentConfirmationService = new PaymentConfirmationService(getContext());
                Subscription subscription = paymentConfirmationService.getPaymentConfirmation(customerSegment, phone, crmRole)
                        .subscribe(new RequestSessionObserver<GeneralResponse<PaymentConfirmationSuccess>>() {

                            @Override
                            public void onNext(GeneralResponse<PaymentConfirmationSuccess> paymentConfirmationSuccessGeneralResponse) {

                                if (getContext() == null) {
                                    return;
                                }

                                stopLoadingAfterDuration(1);

                                if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 2) {

                                    D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());

                                    if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00040")
                                            || paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00003")) {

                                        inflateErrorLayout();
                                    } else {

                                        inflateErrorLayout();
                                    }

                                } else if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 1) {

                                    D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());


                                    if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC06502")) {

                                        Log.i(TAG, "paymentConfirmationSuccessGeneralResponse.getTransactionFault() " + paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode());

                                        inflateNoEligibleCard();
                                    } else if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC06501")) {

                                        inflateIneligibleRoleCard();
                                    } else {

                                        inflateErrorLayout();
                                    }
                                } else if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 0) {

                                    if (paymentConfirmationSuccessGeneralResponse.getTransactionSuccess() != null) {

                                        D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());

                                        eligibilityFlag = paymentConfirmationSuccessGeneralResponse.getTransactionSuccess().getPaymentCodeFlag();
                                        Log.i(TAG, "eligibilityFlag: " + eligibilityFlag);

                                        if (eligibilityFlag == true) {
                                            opCodeFlag = true;
                                        }
                                        inflateLayout();
                                    }
                                } else {
                                    inflateErrorLayout();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                D.e("e = " + e);
                                if (getContext() == null) {
                                    return;
                                }

                                stopLoadingDialog();
                                inflateErrorLayout();

                            }
                        });
                addToActivityCompositeSubcription(subscription);
            }
        }
    }

    public void disableButton(Button button) {
        if (button == null) {
            return;
        }
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {
        if (button == null) {
            return;
        }

        button.setClickable(true);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                D.w("BUTTON CLICK EXPECTING ACTION");
                inflateOverlay();
                trackConfirmButton();
            }
        });
    }

    boolean isValidPaymentCode() {

        boolean isValid = false;

        if (payment_code_input == null) {
            return false;
        }

        String value = payment_code_input.getText().toString();
        Log.i(TAG,"paymentCode: " + value);

        if (value != null && !"".equals(value)) {
            int valueConverted = Integer.parseInt(value);

            if (valueConverted > 0) {
                isValid = true;
            } else {
                isValid = false;
            }
        }

        Log.i(TAG, "isValidPaymentCode is: " + isValid);

        return isValid;
    }

    public boolean isValidPhoneNumber() {

        boolean isValid = false;

        if (payment_code_input == null) {
            return false;
        }
        String value = contact_input.getText().toString();

        if (null != value && !"".equals(value)) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

                isValid = matcher.matches() && value.substring(0, 2).equals("07");
            } else
                isValid = false;
        }
        return isValid;
    }

    public boolean isValidAmount() {

            if (payment_code_input == null) {
            return false;
        }
        String value = amount_input.getText().toString();

        if(value == null)
            return false;

        value = value.trim();

        if(value.isEmpty())
            return false;

        double valueConverted = 0;

        try {
            valueConverted = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.i(TAG, "Exception " + e);
            return false;
        }

        if (valueConverted >= 5.00) {
            return true;
        } else {
           return false;
        }

    }

    boolean validFields() {

        boolean isPhoneNumberCompleted =  contact_input.getText().toString().isEmpty();
        Log.i(TAG, "isPhoneNumberCompleted: " + !isPhoneNumberCompleted);

        if (!opCodeFlag) {
            return isValidAmount() && (isValidPhoneNumber() || isPhoneNumberCompleted);
        } else {
            return (isValidPhoneNumber() || isPhoneNumberCompleted)
                    && isValidAmount() && isValidPaymentCode();

        }

    }

    public void displayEditTextError(CustomEditTextCompat target, TooltipError errorLayout, boolean visibility) {
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

    public void displayEditTextErrorValidation(CustomEditTextCompat target, TooltipError errorLayout) {

        errorLayout.setVisibility(View.VISIBLE);
        target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
    }

    void inflateLayout() {

        amount_input.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        disableButton(payment_button);

        if (!eligibilityFlag) {
            payment_code_layout.setVisibility(View.GONE);
        }

        contact_input.addTextChangedListener(getToolTipTextWatcher(bad_phone_layout));
        amount_input.addTextChangedListener(getToolTipTextWatcher(bad_amount_layout));
        payment_code_input.addTextChangedListener(getToolTipTextWatcher(bad_payment_layout));

        contact_input.setOnErrorIconClickListener(this);
        amount_input.setOnErrorIconClickListener(this);
        payment_code_input.setOnErrorIconClickListener(this);

        contact_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        contact_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    String contactErrorLabel = PaymentConfirmationLabels.getPaymentConfirmationContactError();
                    bad_phone_layout.setText(contactErrorLabel);
                    displayEditTextError(contact_input, bad_phone_layout, true);
                } else {
                    if (contact_input.getText().toString().equals("") || !isValidPhoneNumber())
                        displayEditTextError(contact_input, bad_phone_layout, false);
                    else
                        displayEditTextError(contact_input, bad_phone_layout, true);
                }
            }
        });

        amount_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    String amountErrorLabel = PaymentConfirmationLabels.getPaymentConfirmationAmountError();
                    bad_amount_layout.setText(amountErrorLabel);
                    displayEditTextError(amount_input, bad_amount_layout, true);
                } else {
                    if (!isValidAmount())
                        displayEditTextError(amount_input, bad_amount_layout, false);
                    else
                        displayEditTextError(amount_input, bad_amount_layout, true);
                }
            }
        });

        payment_code_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    String codeErrorLabel = PaymentConfirmationLabels.getPaymentConfirmationCodeError();
                    bad_payment_layout.setText(codeErrorLabel);
                    displayEditTextError(payment_code_input, bad_payment_layout, true);
                } else {
                    if (payment_code_input.getText().toString().equals("") || !isValidPaymentCode())
                        displayEditTextError(payment_code_input, bad_payment_layout, false);
                    else
                        displayEditTextError(payment_code_input, bad_payment_layout, true);
                }
            }
        });


    }

    private ToolTipTextWatcher getToolTipTextWatcher(TooltipError tooltipError){
        return new ToolTipTextWatcher(tooltipError) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (validFields())
                    enableButton(payment_button);
                else
                    disableButton(payment_button);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO validate field
                if(isValidAmount()){
                    displayEditTextError(amount_input, bad_amount_layout, true);
                }if(isValidPaymentCode()){
                    displayEditTextError(payment_code_input, bad_payment_layout, true);
                }if(isValidPhoneNumber()){
                    displayEditTextError(contact_input, bad_phone_layout, true);
                }

            }
        };
    }
    private void inflateMessageCard() {

		containerFrame.removeAllViews();
        VodafoneGenericCard messageCard = new VodafoneGenericCard(getActivity());
        messageCard.showError(true, PaymentConfirmationLabels.getMessageCardText(), ContextCompat.getDrawable(getContext(), R.drawable.yellow_error_triangle));
		containerFrame.addView(messageCard);
    }

    private void inflateErrorLayout() {
		containerFrame.removeAllViews();
        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());
        errorCard.showError(true, PaymentConfirmationLabels.getGeneralErrorMessageWithRetry());
		containerFrame.addView(errorCard);
        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void inflateNoEligibleCard() {
		containerFrame.removeAllViews();

        VodafoneGenericCard noEligible = new VodafoneGenericCard(getActivity());
        noEligible.showError(true, PaymentConfirmationLabels.getNoEligibleMessage(), ContextCompat.getDrawable(getContext(), R.drawable.yellow_error_triangle));
		containerFrame.addView(noEligible);
    }

    private void inflateIneligibleRoleCard() {
		containerFrame.removeAllViews();

        VodafoneGenericCard ineligibleRole = new VodafoneGenericCard(getActivity());
        ineligibleRole.showError(true, PaymentConfirmationLabels.getIneligibleRoleMessage(), ContextCompat.getDrawable(getContext(), R.drawable.yellow_error_triangle));
		containerFrame.addView(ineligibleRole);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (navigationHeader != null) {
            navigationHeader.hideSelectorViewWithoutTriangle();
            ((BillingOverviewActivity) getActivity()).setTitle(PaymentConfirmationLabels.getPaymentConfirmationTitle());
        }
    }

    public String getTitle() {
        return PaymentConfirmationLabels.getPaymentConfirmationTitle();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                init();
            }
        }
    }

    void inflateOverlay() {
        if (getContext() == null) {
            return;
        }
        final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlayDialog.show();

        final Double valuePaid = Double.parseDouble(amount_input.getText().toString());
        final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);
        overlayTitle.setText(PaymentConfirmationLabels.getConfirmDialogTitle());
        overlaySubtext.setText(PaymentConfirmationLabels.getDialogMessageFirstPart() + decimalFormatter.format(valuePaid) + " " + PaymentConfirmationLabels.getDialogMessageSecondPart());

        Button overlayConfirmButton = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        Button overlayCancelBlockButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

        overlayConfirmButton.setText(PaymentConfirmationLabels.getPaymentConfirmationConfirmButton());
        overlayCancelBlockButton.setText(PaymentConfirmationLabels.getPaymentConfirmationCancelButton());

        overlayCancelBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                showLoadingDialog();

                PaymentConfirmationRequest paymentConfirmationRequest = new PaymentConfirmationRequest();

                paymentConfirmationRequest.setAmount(valuePaid);
                paymentConfirmationRequest.setPaymentCodeFlag(eligibilityFlag);

                if (VodafoneController.getInstance().isSeamless()) {
                    paymentConfirmationRequest.setUserStatus("seamless");
                    Log.i(TAG, "user is seamless");


                } else {
                    paymentConfirmationRequest.setUserStatus(((UserProfile) RealmManager.getRealmObject(UserProfile.class)).getUserStatus());
                    Log.i(TAG, "user is full login");
                }

                paymentConfirmationRequest.setPhoneNumber("4" + contact_input.getText().toString());
                /*if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null &&
                        EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsBan() != null)*/
                paymentConfirmationRequest.setBan(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "");

                if (opCodeFlag == true) {
                    paymentConfirmationRequest.setPaymentCode(payment_code_input.getText().toString());
                    Log.i(TAG, "PaymentCode >>POST<< " + payment_code_input.getText().toString());
                }


                Log.i(TAG, "Amount >>POST<< " + valuePaid);
                Log.i(TAG, "Eligibility >>POST<< " + eligibilityFlag);
                Log.i(TAG, "UserStatus >>POST<< " + ((UserProfile) RealmManager.getRealmObject(UserProfile.class)).getUserStatus());
                Log.i(TAG, "PhoneNumber >>POST<< " + ("4" + contact_input.getText().toString()));

              /*  if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null &&
                        EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsBan() != null)*/
                Log.i(TAG, "BAN >>POST<< " + UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());

                Subscriber subscriber =  UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();
                EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

                paymentConfirmationService = new PaymentConfirmationService(getContext());
                paymentConfirmationService.postPaymentConfirmation(paymentConfirmationRequest,
                        entityChildItem != null?entityChildItem.getVfOdsCid():null,
                        subscriber != null?subscriber.getSid():null)
                        .subscribe(new RequestSessionObserver<GeneralResponse<PaymentConfirmationSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<PaymentConfirmationSuccess> paymentConfirmationSuccessGeneralResponse) {
                        if (getContext() == null) {
                            return;
                        }
                        stopLoadingDialog();
                        if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 2) {

                            D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());

                            if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00040") ||
                                    paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00001") ||
                                    paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00003")) {

                                //Sistem momentan indisponibil!
                                new CustomToast.Builder(getContext()).message(PaymentConfirmationLabels.getPaymentConfirmationErrorMessage()).success(false).show();

                            } else if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC06503")) {
                                //Sistem momentan indisponibil! -> paymentCode < 0
                                new CustomToast.Builder(getContext()).message(PaymentConfirmationLabels.getPaymentConfirmationErrorMessage()).success(false).show();
                            } else {
                                inflateErrorLayout();
                            }
                        } else if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 1) {

                            D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());

                            if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC06504")) {
                                //Ne pare rau, plata nu a putut fi confirmata
                                new CustomToast.Builder(getContext()).message(PaymentConfirmationLabels.getNotConfirmedErrorMessage()).success(false).show();

                            } else if (paymentConfirmationSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC06505")) {
                                //Codul de   operatie   nu   este   corect. Te   rugam   sa   contactezi serviciul clienti la *222
                                payment_code_input.clearFocus();
                                bad_payment_layout.setText(R.string.paymentConfirmationCodeError);
                                displayEditTextErrorValidation(payment_code_input, bad_payment_layout);

                            } else {

                                inflateErrorLayout();
                            }
                        } else if (paymentConfirmationSuccessGeneralResponse.getTransactionStatus() == 0) {

                            //  if (paymentConfirmationSuccessGeneralResponse.getTransactionSuccess() != null) {

                            D.i("getTransactionSuccess(): " + paymentConfirmationSuccessGeneralResponse.getTransactionSuccess());

                            redirectToDashboard();
                            // }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        D.e("e = " + e);

                        //TODO check context is ! null
                        if (getContext() == null) {
                            return;
                        }
                        stopLoadingDialog();
                        new CustomToast.Builder(getContext()).message(PaymentConfirmationLabels.getPaymentConfirmationErrorMessage()).success(false).show();
                    }
                });
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() == null) {
                    return;
                }
                overlayDialog.dismiss();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlayDialog.dismiss();
            }
        });
    }

    public void redirectToDashboard() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(3, 20, VoiceOfVodafoneCategory.Pay_Bill, null, PaymentConfirmationLabels.getPaymentConfirmationVoV(), PaymentConfirmationLabels.getPaymentConfirmationVoVButton(), null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

        new CustomToast.Builder(getContext()).message(PaymentConfirmationLabels.getSuccessPaymentToastMessage()).success(true).show();
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
    }

    public void initializeNavigationHeader() {

        navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        navigationHeader.setTitle(YourProfileLabels.getPayBillConfirmationCardTitle());
        navigationHeader.hideSelectorView();
        navigationHeader.showTriangleView();

    }

    @Override
    public void onErrorIconClickListener() {
        if (amount_input.isErrorIconTap())
            displayEditTextError(amount_input, bad_amount_layout, true);
        else if (payment_code_input.isErrorIconTap())
            displayEditTextError(payment_code_input, bad_payment_layout, true);
        else if (contact_input.isErrorIconTap())
            displayEditTextError(contact_input, bad_phone_layout, true);

    }

    private void trackConfirmButton(){

        //track event
        trackEvent(TealiumConstants.payment_confirmation_button);

        PaymentConfirmationTrackingEvent event = new PaymentConfirmationTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "confirm payment";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = " mcare:payment confirmation:button:confirm payment";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    abstract class  ToolTipTextWatcher implements TextWatcher {
       TooltipError tooltipError;

        public ToolTipTextWatcher(TooltipError tooltipError) {
            this.tooltipError = tooltipError;
        }

         public TooltipError getTooltipError() {
             return tooltipError;
         }

         public void setTooltipError(TooltipError tooltipError) {
             this.tooltipError = tooltipError;
         }
     }

     public static class PaymentConfirmationTrackingEvent extends TrackingEvent{
         @Override
         protected void defineTrackingProperties(TrackingAppMeasurement s) {
             super.defineTrackingProperties(s);
             if (getErrorMessage() != null) {
                 s.event11 = "event11";
                 s.getContextData().put("event11", s.event11);
             }
             s.pageName = s.prop21 + "payment confirmation";
             s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "payment confirmation");
             s.channel = "payment confirmation";
             s.getContextData().put("&&channel", s.channel);
             s.prop21 = "mcare:" + "payment confirmation";
             s.getContextData().put("prop21", s.prop21);
             s.eVar5 = "content";
             s.getContextData().put("eVar5", s.eVar5);
         }
     }
    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.paymentConfirmation);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }

}

