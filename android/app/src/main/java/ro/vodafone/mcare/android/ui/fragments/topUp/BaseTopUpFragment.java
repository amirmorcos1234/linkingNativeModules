package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.billing.GetPaymentInputsResponse;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.recharge.RechargeVoucherSuccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.BillRechargeRequest;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.MyCardsService;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.ListViewSwipeAdapter;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.FavoriteNumbersSpinnerAdapter;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.webviews.TopUpWebViewFActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 05.03.2017.
 */

public class BaseTopUpFragment extends BaseFragment implements TopUpPostpaidProgrammedTabFragment.Callback {

    public Logger LOGGER = Logger.getInstance(BaseTopUpFragment.class);
    protected final int PICK_CONTACT = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    protected FragmentTabHost mTabHost;
    protected float amount;
    private boolean isOwnRecharge = false;
    public String ben;
    VoiceOfVodafone voiceOfVodafone;

    protected int MONTHLY_RECHARGE_ID = 4;
    protected int DATE_RECHARGE_ID = 2;
    protected int WEEKLY_RECHARGE_ID = 3;
    protected int IMMEDIATE_RECHARGE_ID = 1;
    protected int TRANSFER_CREDIT_ID = 5;

    protected View view;

    protected VodafoneButton rechargeButton;
    protected LinearLayout contactsButton;

    protected LinearLayout payWithInvoiceLayout;
    protected LinearLayout paymentIconsLayout;
    protected LinearLayout payWithVoucherLayout;
    protected LinearLayout favoriteNumbersLayout;

    protected LinearLayout phoneNumberErrorLayout;
    protected TextView phoneNumberErrorMessage;

    protected LinearLayout voucherErrorLayout;

    protected VodafoneTextView voucherErrorMessage;

    protected CustomEditText phoneNumberInput;
    protected CustomEditText emailAddressInput;
    protected CustomEditText voucherCodeInput;

    protected RechargeValueSection rechargeValueSection;
    protected LinearLayout cards_container;
    protected LinearLayout add_new_card_container;

    protected VodafoneSpinner spinner;
    protected RadioGroup paymentType;
    protected String msisdn;
    protected String ban;
    ImageView closeBtn;
    VodafoneButton confirmRechargeBtn;
    VodafoneButton modifyRechargeBtn;
    VodafoneTextView overlayTitle;
    VodafoneTextView overlayContext;
    List<FavoriteNumber> favoriteNumbers;
    Dialog overlayDialog;
    protected BillingServices billingServices;
    protected RechargeService rechargeService;
    private List<FavoriteNumber> favoriteNumbersList;
    private FavoriteNumber fv;
    private int selectedDayId;
    private String selectedSpinnerElement;
    private LocalDate selectedMonthDate;
    private LocalDate localDate;
    private Date selectedCalendarDate;

    private Dialog readContactsPermissionOverlay;
    private Dialog readPermissionFlagCheckedOverlay;

    boolean before;
    boolean after = true;
    private String PREFS_FILE_NAME;
    protected CreditCardSelection selectedCreditCard=null;
    protected SaveCreditCard saveCreditCard = null;


    View.OnClickListener contactsButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Tealium Track Event
           /* Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", "top up");
            tealiumMapEvent.put("event_name", "mcare: top up: button: contacte");
            tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

            if (Build.VERSION.SDK_INT >= 23) {
                requestReadContactsPermission();
            } else {
                opentContactsPage();
            }
        }
    };

    View.OnClickListener readContactsPermissionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    };

    RadioGroup.OnCheckedChangeListener paymentTypeListner = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            hideKeyboard();
            switch (checkedId) {
                case R.id.pay_with_card:
                    if (rechargeValueSection != null) {
                        rechargeValueSection.activateRadioButtons();
                    }
                    displaySelectedPayTypeLayout(paymentIconsLayout);
                    getFragmentActiveButton();
                    break;
                case R.id.pay_with_voucher:
                    if (rechargeValueSection != null) {
                        rechargeValueSection.disableRadioButtonKeepPreviousSelection();
                    }

                    if (voucherCodeInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && !voucherCodeInput.isEmpty()) {
                        voucherCodeInput.showErrorInputStyle(CustomEditText.INVALID_FORMAT_FIELD_STATUS);
                    }
                    displaySelectedPayTypeLayout(payWithVoucherLayout);
                    checkVoucherCode();
                    break;
                case R.id.pay_with_invoice:
                    if (rechargeValueSection != null) {
                        rechargeValueSection.activateRadioButtons();
                    }
                    displaySelectedPayTypeLayout(payWithInvoiceLayout);
                    getFragmentActiveButton();
                    break;
            }
        }
    };

    View.OnClickListener overlayRechargeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTabHost != null) {
                switch (mTabHost.getCurrentTabTag()) {
                    case "0":
                        doRechargeWithInvoice();
                        break;
                    case "1":
                        doRecurringRecharge();
                        break;
                }
            }
        }
    };

    View.OnClickListener rechargeButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
           /* Map<String, Object> tealiumMapEventVoucher = new HashMap(6);
            tealiumMapEventVoucher.put("screen_name", "top up");
            tealiumMapEventVoucher.put("event_name", "mcare: top up: button: reincarca cu voucher");
            tealiumMapEventVoucher.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEventVoucher);*/

            TealiumHelper.tealiumTrackEvent(BaseTopUpFragment.class.getSimpleName(),
                    TealiumConstants.rechargeBtn, TealiumConstants.topUpScreenName, "button=");

            if (getChildFragmentManager().getFragments() != null && mTabHost != null) {
                Log.d("", "onClick: fragments : " + getChildFragmentManager().getFragments());
                switch (mTabHost.getCurrentTabTag()) {
                    case "0":
                        if (payWithVoucherLayout.getVisibility() == View.VISIBLE) {
                            doRechargeWithVouchers();
                        } else if (paymentIconsLayout.getVisibility() == View.VISIBLE) {
                            doPaymentWithCard();
                        } else {
                            checkAmountToPay();

                        }
                        break;
                    case "1":
                        checkAmountToPay();
                        break;
                }

            } else {
                Log.d("", "onClick: fragments are null  ");
                if (payWithVoucherLayout.getVisibility() == View.VISIBLE) {
                    doRechargeWithVouchers();
                } else if (paymentIconsLayout.getVisibility() == View.VISIBLE) {
                    doPaymentWithCard();
                } else if (payWithInvoiceLayout.getVisibility() == View.VISIBLE) {
                    doRechargeWithInvoice();
                }
            }
        }
    };

    TopUpActivity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //default date
        selectedCalendarDate = new Date();

        billingServices = new BillingServices(getContext());
        rechargeService = new RechargeService(getContext());

        activity = (TopUpActivity) getActivity();
        String ownMsisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
    }

    public void displayError(LinearLayout errorLayout, TextView errorTextView, String errorMessage) {
        errorLayout.setVisibility(View.VISIBLE);
        errorTextView.setText(errorMessage);
    }

    private void doRechargeWithVouchers() {
        //Tealium Track Event
        /*Map<String, Object> tealiumMapEventVoucher = new HashMap(6);
        tealiumMapEventVoucher.put("screen_name", "top up");
        tealiumMapEventVoucher.put("event_name", "mcare: top up: button: reincarca cu voucher");
        tealiumMapEventVoucher.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEventVoucher);*/

        if (phoneNumberInput != null && phoneNumberInput.getVisibility() == View.VISIBLE) {
            msisdn = phoneNumberInput.getText().toString();
        } else {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn().substring(1);
        }

        if (validateInputs()) {
            showLoadingDialog();
            rechargeService.validateSubscriber(formatMsisdn(msisdn)).subscribe(new RequestSessionObserver<GeneralResponse>() {
                @Override
                public void onNext(GeneralResponse generalResponse) {
                    if (generalResponse.getTransactionStatus() == 0) {
                        addRechargeWithVoucher();
                    } else {
                        stopLoadingDialog();
                        overlayDialog.dismiss();

                        String errorCode = generalResponse.getTransactionFault().getFaultCode();
                        if (errorCode.equals(ErrorCodes.API30_PREPAID_NUMBER_ERROR.getErrorCode())) {
                            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                    }
                    TealiumHelper.tealiumTrackView(BaseTopUpFragment.class.getSimpleName(),
                            TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                    CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                    customToast.show();
                    stopLoadingDialog();
                    TealiumHelper.tealiumTrackView(BaseTopUpFragment.class.getSimpleName(),
                            TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);
                }
            });
        } else {
            stopLoadingDialog();
        }
    }

    private boolean validateInputs() {
        if (phoneNumberInput != null && phoneNumberInput.getVisibility() == View.VISIBLE) {
            if (!phoneNumberInput.validateCustomEditText()) {
                return false;
            }
        }
        if (emailAddressInput != null && emailAddressInput.getVisibility() == View.VISIBLE) {
            if (!emailAddressInput.validateCustomEditText()) {
                return false;
            }
        }
        if (rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput != null) {
            if (!rechargeValueSection.otherValueInput.validateCustomEditText()) {
                return false;
            }
        }
        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE &&
                voucherCodeInput != null && voucherCodeInput.getVisibility() == View.VISIBLE) {
            if (!voucherCodeInput.validateCustomEditText()) {
                return false;
            }
        }
        return true;
    }

    private void doPayment() {
        //validate typed credentials
        boolean isValidOtherValue = true;
        boolean isValidEmailAdress = emailAddressInput.validateCustomEditText();
        final String phoneNumber;
        boolean isSave = false;
        String token = null;
        if(activity.saveCreditCard!=null){
            token = null;
            isSave=activity.saveCreditCard.getCheckBox().isChecked();
        }
        if(activity.selectedCreditCard!=null){
            token = activity.selectedCreditCard.getCard().getToken();
            isSave = false;
        }
        isOwnRecharge = FragmentUtils.getVisibleFragment((TopUpActivity) getActivity(), false) instanceof TopUpPrepaidOwnNumberFragment;

        if (rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput != null) {
            isValidOtherValue = rechargeValueSection.otherValueInput.validateCustomEditText();

            try {
                amount = Float.valueOf(rechargeValueSection.otherValueInput.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (phoneNumberInput != null && phoneNumberInput.getVisibility() == View.VISIBLE) {
            phoneNumber = phoneNumberInput.getText().toString();
        } else {
            phoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn().substring(1);
        }

        if (isValidOtherValue && isValidEmailAdress) {
            showLoadingDialog();
            final String finalToken = token;
            billingServices.doPaymentRecharge(phoneNumber, amount, emailAddressInput.getText().toString(),
                    null, null,token,isSave).subscribe(new RequestSessionObserver<GeneralResponse<GetPaymentInputsResponse>>() {
                @Override
                public void onNext(GeneralResponse<GetPaymentInputsResponse> getPaymentInputsResponseGeneralResponse) {
                    stopLoadingDialog();
                    LOGGER.d("onNext()");

                    if (getPaymentInputsResponseGeneralResponse.getTransactionStatus() == 0) {

                        if(finalToken !=null){
                            displayConfirmationDialogOnDashboard();
                        }else {
                            Intent topUpWebViewIntent = new Intent(getActivity(), TopUpWebViewFActivity.class);
                            String htmlInputs = getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getHtmlForm();
                            String successUrl = getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getSuccessUrl();
                            topUpWebViewIntent.putExtra("htmlInputs", htmlInputs);
                            topUpWebViewIntent.putExtra("successUrl", successUrl);
                            topUpWebViewIntent.putExtra("msisdn", phoneNumber);
                            topUpWebViewIntent.putExtra("amount", String.valueOf(amount));
                            topUpWebViewIntent.putExtra("isOwnRecharge", isOwnRecharge);
                            topUpWebViewIntent.putExtra(WebviewActivity.KEY_URL, htmlInputs);
                            startActivity(topUpWebViewIntent);
                        }
                    } else {
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                        CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                        customToast.show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    LOGGER.d("onError()");
                    stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                    CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                    customToast.show();
                }
            });
        }
    }
    private void displayConfirmationDialogOnDashboard() {
        showLoadingDialog();
        final String phoneNumber = phoneNumberInput.getText().toString();;
        RechargeService rechargeService = new RechargeService(getActivity());
        rechargeService.getFavoriteNumbers().subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                super.onNext(favoriteNumbersSuccessGeneralResponse);
                stopLoadingDialog();
                if (favoriteNumbersSuccessGeneralResponse.getTransactionStatus() == 0 && RealmManager.getRealmObject(FavoriteNumbersSuccess.class) != null) {
                    List<FavoriteNumber> favoriteNumbersList = ((FavoriteNumbersSuccess) RealmManager.getRealmObject(FavoriteNumbersSuccess.class)).getFavoriteNumbers();

                    if (favoriteNumbersList.size() == 0){
                        if(!VodafoneController.getInstance().getUserProfile().getMsisdn().equals(formatMsisdn(phoneNumber))
                                && !isOwnRecharge) {
                            addCardToVOV(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(msisdn, String.valueOf(amount), false), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                        } else {
}
                    } else {
                        for (FavoriteNumber favoriteNumber : favoriteNumbersList) {
                            if (phoneNumber.equalsIgnoreCase(favoriteNumber.getPrepaidMsisdn())
                                    || VodafoneController.getInstance().getUserProfile().getMsisdn().equals(formatMsisdn(phoneNumber))
                                    || isOwnRecharge) {
                                addCardToVOV(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(phoneNumber, String.valueOf(amount), true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                                break;
                            } else {
                                addCardToVOV(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(phoneNumber,String.valueOf(amount),false), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                            }
                        }
                    }
                } else {
                    addCardToVOV(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(phoneNumber,String.valueOf(amount),true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                addCardToVOV(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(phoneNumber,String.valueOf(amount),true), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                }
            @Override
            public void onCompleted() {
                stopLoadingDialog();
                new CustomToast.Builder(getActivity()).message(TopUPLabels.getTop_up_successfull_toast_message()).success(true).show();
                new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD,true);
            }
        });
    }
    private void addCardToVOV(int id_vov,int priority,VoiceOfVodafoneCategory category,String title,String message,String leftButtonTitle,String rightButtonTitle,boolean showLeftBtn,boolean showRightBtn,VoiceOfVodafoneAction leftAction,VoiceOfVodafoneAction rightAction){
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(id_vov, priority, category, title,message, leftButtonTitle, rightButtonTitle, showLeftBtn, showRightBtn, leftAction, rightAction);
        Log.d("vov widget", "insertAuto");
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

    }
    private String createVovMessage(String msisdn, String amount, boolean isInFavorites){
        String message = null;
        if(!isInFavorites && !isOwnRecharge){
            message = String.format(TopUPLabels.getTop_up_immediate_recharge_vov_message(), msisdn, NumbersUtils.getIntegerPart(amount));
        } else {
            message = StringUtils.substringBefore(String.format(TopUPLabels.getTop_up_immediate_recharge_vov_message(),msisdn,NumbersUtils.getIntegerPart(amount)),",") + ".";
        }
        return message;
    }

    protected void deleteFavoriteNumber(final int position) {

        RechargeService rechargeService = new RechargeService(getContext());

        final String msisdnToDelete = favoriteNumbers.get(position).getPrepaidMsisdn();

        rechargeService.deleteFavoriteNumber(VodafoneController.getInstance().getUserProfile().getUserName(), msisdnToDelete)
                .subscribe(new RequestSessionObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                        if (favoriteNumbersSuccessGeneralResponse.getTransactionStatus() == 0) {
                            if (spinner.getAdapter() != null) {
                                Realm realm = RealmManager.getDefaultInstance();

                                realm.beginTransaction();
                                favoriteNumbers.remove(position);
                                realm.commitTransaction();
                                realm.close();

                                if (spinner.getText().toString().contains(msisdnToDelete)) {

                                    spinner.setText(null);
                                    spinner.setHint(TopUPLabels.getTop_up_select_favorite_number());
                                }
                                spinner.getAdapter().notifyDataSetChanged();

                                if (spinner.getAdapter().getCount() < 1) {
                                    favoriteNumbersLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                        CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                        customToast.show();
                    }
                });

    }

    protected void initSpinner() {

        favoriteNumbers = new ArrayList<>();
        if (RealmManager.getRealmObject(FavoriteNumbersSuccess.class) != null &&
                !(VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidHighAccess) &&
                !(VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidsLowAccess)) {
            favoriteNumbers = ((FavoriteNumbersSuccess) RealmManager.getRealmObject(FavoriteNumbersSuccess.class)).getFavoriteNumbers();
        }

        if (favoriteNumbers != null && favoriteNumbers.size() != 0) {

            favoriteNumbersLayout.setVisibility(View.VISIBLE);
            spinner = (VodafoneSpinner) view.findViewById(R.id.spinner);
            spinner.setCallback((VodafoneSpinner.Callback) this);
            spinner.setSelectedValue("");

            FavoriteNumbersSpinnerAdapter adapter = new FavoriteNumbersSpinnerAdapter(getContext(), favoriteNumbers, R.drawable.selector, spinner);
            spinner.setAdapter(adapter);
        }
    }

    protected void displaySelectedPayTypeLayout(LinearLayout linearLayout) {
        if (paymentIconsLayout != null && paymentIconsLayout.getVisibility() == View.VISIBLE) {
            paymentIconsLayout.setVisibility(View.GONE);
        }

        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            payWithVoucherLayout.setVisibility(View.GONE);
        }

        if (linearLayout.getVisibility() != View.VISIBLE) {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void opentContactsPage() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
                    Cursor c = getActivity().getContentResolver().query(contactData, projection, null, null, null);
                    c.moveToFirst();

                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name = c.getString(nameIdx);
                    String phoneNumber = c.getString(phoneNumberIdx);

                    if (phoneNumber != null) {

                        if (phoneNumber.contains(" ") || phoneNumber.contains(")") || phoneNumber.contains("(") || phoneNumber.contains("-")) {
                            phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
                        }

                        if (phoneNumber.length() > 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                    }

                    phoneNumberInput.setText(phoneNumber);

                    c.close();

                    if (spinner != null && spinner.getText() != null) {
                        spinner.setText(null);
                        spinner.setHint(TopUPLabels.getTop_up_select_favorite_number());

                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestReadContactsPermission() {
        int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            before = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
            if (isFirstTimeAskingPermission(getContext(), Manifest.permission.READ_CONTACTS)) {
                firstTimeAskingPermission(getContext(), Manifest.permission.READ_CONTACTS, false);
                displayReadContactsPermissionOverlay();
            } else if (!before && !after) {
                displayPermissionCheckedFlagOverlay();
            } else {
                displayReadContactsPermissionOverlay();
            }
        } else opentContactsPage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("", "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("", "Permision granted");
                    opentContactsPage();
                    readContactsPermissionOverlay.dismiss();
                } else {
                    if (readContactsPermissionOverlay.isShowing()) {
                        readContactsPermissionOverlay.dismiss();
                    } else if (readPermissionFlagCheckedOverlay.isShowing()) {
                        readPermissionFlagCheckedOverlay.dismiss();
                    }
                    after = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                }
            }
        }
    }

    public void doRechargeWithInvoice() {
        showLoadingDialog();
        if (phoneNumberInput != null && !phoneNumberInput.isEmpty()) {
            msisdn = phoneNumberInput.getText().toString();
        }

        rechargeService.validateSubscriber(formatMsisdn(msisdn)).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse generalResponse) {
                if (generalResponse.getTransactionStatus() == 0) {
                    addImmediatelyRecharge();
                } else {
                    stopLoadingDialog();
                    overlayDialog.dismiss();
                    String errorCode = generalResponse.getTransactionFault().getFaultCode();
                    if (errorCode.equals(ErrorCodes.API30_PREPAID_NUMBER_ERROR.getErrorCode())) {
                        displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
                    } else {
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                stopLoadingDialog();
                super.onError(e);
                new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                customToast.show();
            }
        });
    }

    protected void doPaymentWithCard() {
        //Tealium Track Event
        /*Map<String, Object> tealiumMapEventCard = new HashMap(6);
        tealiumMapEventCard.put("screen_name", "top up");
        tealiumMapEventCard.put("event_name", "mcare: top up: button: plateste cu cardul");
        tealiumMapEventCard.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEventCard);*/

        if (phoneNumberInput != null && !phoneNumberInput.isEmpty()) {
            msisdn = phoneNumberInput.getText().toString();
        } else {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        Log.d("", "Validate prepaid subscriber ");

        if (validateInputs()) {
            showLoadingDialog();
            rechargeService.validateSubscriber(formatMsisdn(msisdn)).subscribe(new RequestSessionObserver<GeneralResponse>() {

                @Override
                public void onNext(GeneralResponse response) {
                    Log.d("", "onNext in isValidaPe");
                    stopLoadingDialog();
                    if (response.getTransactionStatus() == 0) {

                        doPayment();
                    } else {
                        stopLoadingDialog();
                        if (overlayDialog != null) {
                            overlayDialog.dismiss();
                        }
                        String errorCode = response.getTransactionFault().getFaultCode();
                        if (errorCode.equals(ErrorCodes.API30_PREPAID_NUMBER_ERROR.getErrorCode())) {
                            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                        TealiumHelper.tealiumTrackView(BaseTopUpFragment.class.getSimpleName(),
                                TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    stopLoadingDialog();
                    super.onError(e);
                    new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                    CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                    customToast.show();
                    TealiumHelper.tealiumTrackView(BaseTopUpFragment.class.getSimpleName(),
                            TealiumConstants.topUpJourney,TealiumConstants.topUpScreenName);
                }
            });
        } else {
            LOGGER.d("Not Valide inputs");
        }
    }

    protected void displayRechargeConfirmation() {

        TealiumHelper.tealiumTrackView(BaseTopUpFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.transferCreditOverLayScreenName);

        if (phoneNumberInput != null && !phoneNumberInput.isEmpty()) {
            msisdn = phoneNumberInput.getText().toString();
        } else {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlayDialog.show();

        confirmRechargeBtn = (VodafoneButton) overlayDialog.findViewById(R.id.buttonKeepOn);
        confirmRechargeBtn.setOnClickListener(overlayRechargeBtnListener);

        modifyRechargeBtn = (VodafoneButton) overlayDialog.findViewById(R.id.buttonTurnOff);
        modifyRechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                stopLoadingDialog();
            }
        });

        closeBtn = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                stopLoadingDialog();
            }
        });

        overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        overlayContext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        setupOverlayLabels();

    }

    private void setupOverlayLabels() {
        boolean isValidOtherValue = true;
        if (rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput != null) {
            isValidOtherValue = rechargeValueSection.otherValueInput.validateCustomEditText();
            try {
                amount = Float.valueOf(rechargeValueSection.otherValueInput.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mTabHost != null) {
            switch (mTabHost.getCurrentTabTag()) {
                case "0":
                    overlayContext.setText(String.format(TopUPLabels.getTop_up_confirmation_context(), msisdn, NumbersUtils.getIntegerPart(String.valueOf(amount))));
                    break;
                case "1":
                    if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getWeekly_programmed_recharge())) {
                        overlayContext.setText(String.format(TopUPLabels.getTop_up_weekly_programmed_confirmation_context(), msisdn, NumbersUtils.getIntegerPart(String.valueOf(amount)), String.valueOf(ListViewSwipeAdapter.DaysOfWeek.getDaysFromId(selectedDayId))));
                    } else if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getMonthly_programmed_recharge())) {
                        overlayContext.setText(String.format(TopUPLabels.getTop_up_montly_programmed_confirmation_context(), msisdn, NumbersUtils.getIntegerPart(String.valueOf(amount)), selectedMonthDate.getDayOfMonth()));

                    } else if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getDate_programmed_recharge())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
                        String formattedDate = sdf.format(selectedCalendarDate);
                        overlayContext.setText(String.format(TopUPLabels.getTop_up_date_programmed_confirmation_context(), msisdn, NumbersUtils.getIntegerPart(String.valueOf(amount)), formattedDate));
                    }
                    break;
            }
        }

        overlayTitle.setText(TopUPLabels.getTop_up_confirmation_title());
        confirmRechargeBtn.setText(TopUPLabels.getTop_up_recharge_button_label());
        modifyRechargeBtn.setText(TopUPLabels.getTop_up_confirmation_modify_button());
    }

    protected void checkAmountToPay() {
        BanPost4preEligibilitySuccess eligibilitySuccess = (BanPost4preEligibilitySuccess) RealmManager.getRealmObject(BanPost4preEligibilitySuccess.class);

        if (eligibilitySuccess != null) {
            ben = eligibilitySuccess.getBen();
            boolean isValidOtherValue = true;
            if (rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput != null) {
                isValidOtherValue = rechargeValueSection.otherValueInput.validateCustomEditText();
                try {
                    amount = Float.valueOf(rechargeValueSection.otherValueInput.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (amount > eligibilitySuccess.getMaxRechargeSum()) {
                rechargeValueSection.showRechargeError(String.format(TopUPLabels.getTop_up_max_recharge_value_error(),
                        NumbersUtils.twoDigitsAfterDecimal(eligibilitySuccess.getMaxRechargeSum())));
            } else {
                if (isValidOtherValue) {
                    rechargeValueSection.hideRechargeError();
                    displayRechargeConfirmation();
                }
            }
        }
    }


    private void addImmediatelyRecharge() {
        showLoadingDialog();

        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        rechargeService.addBillBanRechargesImmediatelyPrepaid(amount, BillRechargeRequest.RechargeTypeEnum.imd, ben, ban, msisdn).subscribe(new RequestSessionObserver<GeneralResponse<BillRechargesSuccess>>() {
            @Override
            public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                if (billRechargesSuccessGeneralResponse.getTransactionStatus() == 0) {
                    stopLoadingDialog();
                    displayConfirmationDialogOnDashboard(String.valueOf(amount), IMMEDIATE_RECHARGE_ID);
                } else {
                    stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                    CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                    customToast.show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                customToast.show();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }
        });

    }

    private void doRecurringRecharge() {
        showLoadingDialog();

        if (phoneNumberInput != null && !phoneNumberInput.isEmpty()) {
            msisdn = phoneNumberInput.getText().toString();
        } else {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }
        rechargeService.validateSubscriber(formatMsisdn(msisdn)).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse generalResponse) {
                stopLoadingDialog();
                if (generalResponse.getTransactionStatus() == 0) {
                    if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getWeekly_programmed_recharge())) {
                        addWeeklyScheduledRecharge();
                    } else if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getMonthly_programmed_recharge())) {
                        addMonthlyScheduledRecharge();
                    } else if (selectedSpinnerElement.equalsIgnoreCase(BEOLabels.getDate_programmed_recharge())) {
                        addDateScheduledRecharge();
                    }
                } else {
                    stopLoadingDialog();
                    overlayDialog.dismiss();

                    String errorCode = generalResponse.getTransactionFault().getFaultCode();
                    if (errorCode.equals(ErrorCodes.API30_PREPAID_NUMBER_ERROR.getErrorCode())) {
                        displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
                    } else {
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                stopLoadingDialog();
                super.onError(e);
                new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                customToast.show();
            }
        });
    }

    private void addWeeklyScheduledRecharge() {

        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        RechargeService rechargeService = new RechargeService(getContext());

        rechargeService.addWeeklyProgrammedRecharge(amount, BillRechargeRequest.RechargeTypeEnum.ewr, ban, msisdn, selectedDayId, ben)
                .subscribe(new RequestSessionObserver<GeneralResponse<BillRechargesSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                        if (billRechargesSuccessGeneralResponse.getTransactionStatus() == 0) {
                            displayConfirmationDialogOnDashboard(String.valueOf(amount), WEEKLY_RECHARGE_ID);
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                        CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                        customToast.show();
                    }
                });

    }

    private void addMonthlyScheduledRecharge() {
        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        Log.d("", "addMonthlyScheduledRecharge: " + selectedMonthDate.getDayOfMonth());


        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.addMonthlyProgrammedRecharge(amount, BillRechargeRequest.RechargeTypeEnum.emr, ban, msisdn, selectedMonthDate.getDayOfMonth(), ben)
                .subscribe(new RequestSessionObserver<GeneralResponse<BillRechargesSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                        if (billRechargesSuccessGeneralResponse.getTransactionStatus() == 0) {
                            displayConfirmationDialogOnDashboard(String.valueOf(amount), MONTHLY_RECHARGE_ID);
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                        CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                        customToast.show();
                    }
                });

    }

    private void addDateScheduledRecharge() {
        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        RechargeService rechargeService = new RechargeService(getContext());
        rechargeService.addDateProgrammedRecharge(amount, BillRechargeRequest.RechargeTypeEnum.otr, ban, msisdn, selectedCalendarDate.getTime(), ben)
                .subscribe(new RequestSessionObserver<GeneralResponse<BillRechargesSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                        if (billRechargesSuccessGeneralResponse.getTransactionStatus() == 0) {
                            displayConfirmationDialogOnDashboard(String.valueOf(amount), DATE_RECHARGE_ID);
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                        CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                        customToast.show();
                    }
                });
    }

    private void addRechargeWithVoucher() {
        String voucher = voucherCodeInput.getText().toString();

        LOGGER.d("doRechargeWithVouchers for msisdn " + msisdn + " and voucher " + voucher);
        rechargeService.rechargeWithVoucher(msisdn, voucher).subscribe(new RequestSessionObserver<GeneralResponse<RechargeVoucherSuccess>>() {
            @Override
            public void onNext(GeneralResponse<RechargeVoucherSuccess> response) {
                stopLoadingDialog();
                LOGGER.d("onNext()");

                if (response.getTransactionStatus() == 0) {

                    displayConfirmationDialogOnDashboard(response.getTransactionSuccess().getRechargedCredit(), IMMEDIATE_RECHARGE_ID);

                } else {
                    if (response.getTransactionFault() != null) {
                        String errorCode = response.getTransactionFault().getFaultCode();

                        LOGGER.d("eror code :" + errorCode);
                        if (errorCode.equals(ErrorCodes.API34_VOUCHER_INVALID.getErrorCode())) {
                            LOGGER.d("eror code : 1111");
                            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher());
                            //voucherCodeInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS);
                            voucherCodeInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS, true);
                        } else if (errorCode.equals(ErrorCodes.API34_VOUCHER_EXPIRED.getErrorCode())) {
                            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_expired_voucher());
                            voucherCodeInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS, true);
                        } else if (errorCode.equals(ErrorCodes.API34_VOUCHER_USED.getErrorCode())) {
                            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_used_voucher());
                            voucherCodeInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS, true);
                        } else if (errorCode.equals(ErrorCodes.API34_VOUCHER_INVALID.getErrorCode())) {
                            displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher());
                            voucherCodeInput.showErrorInputStyle(CustomEditText.VALIDE_FIELD_STATUS, true);
                        } else {
                            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                            customToast.show();
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LOGGER.d("onError()");
                new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_api_call_fail()).success(false).show();
//                CustomToast customToast = new CustomToast(getActivity(), getContext(), TopUPLabels.getTop_up_api_call_fail(), false);
//                customToast.show();
                stopLoadingDialog();
            }
        });
    }

    private String createVovMessage(int actionID, String value) {
        value = NumbersUtils.getIntegerPart(value);
        String message = null;
        switch (actionID) {
            case 1:
                message = String.format(TopUPLabels.getTop_up_immediate_recharge_vov_message(), msisdn, value);
                break;
            case 2:
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
                String formattedDate = sdf.format(selectedCalendarDate);
                message = String.format(TopUPLabels.getTop_up_date_recharge_vov_message(), msisdn, value, formattedDate);
                break;
            case 3:
                message = String.format(TopUPLabels.getTop_up_weekly_recharge_vov_message(), msisdn, value, String.valueOf(ListViewSwipeAdapter.DaysOfWeek.getDaysFromId(selectedDayId)));
                break;
            case 4:
                message = String.format(TopUPLabels.getTop_up_monthly_recharge_vov_message(), msisdn, value, selectedMonthDate.getDayOfMonth());
                break;
            case 5:
                String s = TopUPLabels.getTop_up_transfer_credit_Vov_succes_save_number().replace("{x}", value);
                message = s.replace("{07xxxxxxxx}", msisdn);
                break;
        }

        return message;
    }

    private String createVovMessageIsFavorite(int rechargeType, String value) {
        if (rechargeType == 5) {
            String message = TopUPLabels.getTop_up_transfer_credit_Vov_succes().replace("{x}", value);
            return message.replace("{07xxxxxxxx}", msisdn);
        }
        String message = StringUtils.substringBeforeLast(createVovMessage(rechargeType, value), ",");
        return message + ".";
    }

    protected void displayConfirmationDialogOnDashboard(final String value, final int rechargeType) {


        if (phoneNumberInput != null && phoneNumberInput.getVisibility() == View.VISIBLE) {
            RechargeService rechargeService = new RechargeService(getContext());
            rechargeService.getFavoriteNumbers().subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
                @Override
                public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                    super.onNext(favoriteNumbersSuccessGeneralResponse);
                    if (favoriteNumbersSuccessGeneralResponse.getTransactionStatus() == 0 && RealmManager.getRealmObject(FavoriteNumbersSuccess.class) != null) {
                        favoriteNumbersList = ((FavoriteNumbersSuccess) RealmManager.getRealmObject(FavoriteNumbersSuccess.class)).getFavoriteNumbers();
                        if (favoriteNumbersList.size() == 0
                                && RealmManager.getRealmObject(FavoriteNumbersSuccess.class) != null
                                && !VodafoneController.getInstance().getUserProfile().getMsisdn().equals(formatToVdfNumber(msisdn))
                                && !isOwnRecharge) {
                            if (rechargeType == TRANSFER_CREDIT_ID)
                                voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.TransferCredit, null, createVovMessage(rechargeType, value), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                            else
                                voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(rechargeType, value), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);

                            VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                            VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                            //Tealium trigger survey
                            //add Qualtrics survey
                            TealiumHelper.addQualtricsCommand();
                        } else {
                            for (FavoriteNumber favoriteNumber : favoriteNumbersList) {
                                if (msisdn.equalsIgnoreCase(favoriteNumber.getPrepaidMsisdn())
                                        || VodafoneController.getInstance().getUserProfile().getMsisdn().equals(msisdn)
                                        || isOwnRecharge) {
                                    if (rechargeType == TRANSFER_CREDIT_ID)
                                        voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.TransferCredit, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                                    else
                                        voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                                    Log.d("vov widget", "insertAuto");
                                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                                    //Tealium trigger survey
                                    //add Qualtrics survey
                                    TealiumHelper.addQualtricsCommand();

                                    break;
                                } else {
                                    if (rechargeType == TRANSFER_CREDIT_ID)
                                        voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.TransferCredit, null, createVovMessage(rechargeType, value), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                                    else
                                        voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessage(rechargeType, value), "Da", "Mai târziu", true, true, VoiceOfVodafoneAction.AddToFavoritesNumbers, VoiceOfVodafoneAction.Dismiss);
                                    Log.d("vov widget", "insertAuto");
                                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                                    //Tealium trigger survey
                                    //add Qualtrics survey
                                    TealiumHelper.addQualtricsCommand();

                                }
                            }
                        }

                    } else {
                        if (rechargeType == TRANSFER_CREDIT_ID)
                            voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.TransferCredit, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                        else
                            voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);

                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                        //Tealium trigger survey
                        //add Qualtrics survey
                        TealiumHelper.addQualtricsCommand();

                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                    Log.d("vov widget", "insertAuto");
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                    displaySuccessToast(rechargeType);
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
                }

                @Override
                public void onCompleted() {
                    Log.d("favorite numbers", "onCompleted");
                    displaySuccessToast(rechargeType);
                    if (overlayDialog != null) {
                        //Todo can delete dispmis.
                        overlayDialog.dismiss();
                    }
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);

                }
            });
        } else {
            VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.Recharge, null, createVovMessageIsFavorite(rechargeType, value), "Ok", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
            Log.d("vov widget", "insertAuto");
            VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
            VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

            displaySuccessToast(rechargeType);
            new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
        }
    }

    private void displaySuccessToast(final int rechargeType) {
        if (rechargeType == MONTHLY_RECHARGE_ID || rechargeType == WEEKLY_RECHARGE_ID || rechargeType == DATE_RECHARGE_ID) {
            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_succes_recurrent_recharge()).success(true).show();
        } else if (rechargeType == IMMEDIATE_RECHARGE_ID) {
            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_successfull_toast_message()).success(true).show();
        } else if (rechargeType == TRANSFER_CREDIT_ID) {
            new CustomToast.Builder(getContext()).message(TopUPLabels.getTop_up_transfer_credit_toast_succes_message()).success(true).show();

        }
    }

    private String formatMsisdn(String msisdn) {
        if (msisdn != null) {
            if (msisdn.startsWith("4")) {
                msisdn = msisdn.substring(1);
            }
        }
        return msisdn;
    }

    private String formatToVdfNumber(String msisdn) {
        String formatedMsisdn = msisdn;
        if (msisdn.startsWith("0"))
            formatedMsisdn = "4" + msisdn;

        return formatedMsisdn;
    }

    @Override
    public void getSelectedDayId(int selectedDayId) {
        this.selectedDayId = selectedDayId;
        Log.d("BaseTopUpFr", "selectElement: " + selectedDayId);
    }

    @Override
    public void getSelectedSpinnerElement(Object selectedValue) {
        this.selectedSpinnerElement = (String) selectedValue;
        Log.d("BaseTopUpFr", "selectSpinnerElement: " + selectedSpinnerElement);
    }

    @Override
    public void getSelectedMonthDay(LocalDate selectedDate) {
        this.selectedMonthDate = selectedDate;
        Log.d("BaseTopUpFr", "getSelectedMonthDay: " + selectedMonthDate);
    }


    @Override
    public void getSelectedCalendarDate(Date selectedDate) {
        this.selectedCalendarDate = selectedDate;
        Log.d("", "getSelectedCalendarDate" + selectedCalendarDate);
    }

    private void closeOverlay() {
        if (mTabHost != null) {
            switch (mTabHost.getCurrentTabTag()) {
                case "0":
                    overlayDialog.dismiss();
                    break;
                case "1":
                    mTabHost.setCurrentTabByTag("0");
                    overlayDialog.dismiss();
            }
        }
        overlayDialog.dismiss();
    }

    private void checkVoucherCode() {
        Log.d("", "checkVoucherCode: " + voucherCodeInput.isValide());
        if (voucherCodeInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            voucherCodeInput.removeHighlight();
            getFragmentInactiveButton();
        }
    }

    private void getFragmentActiveButton() {

        Fragment fragment = FragmentUtils.getVisibleFragment((TopUpActivity) getActivity(), false);
        if (fragment instanceof TopUpPostpaidPost4PreFragment) {
            TopUpPostpaidPost4PreFragment post4PreFragment = (TopUpPostpaidPost4PreFragment) VodafoneController.findFragment(TopUpPostpaidPost4PreFragment.class);
            if (post4PreFragment != null)
                post4PreFragment.activateButton();
        } else if (fragment instanceof TopUpPrepaidOwnNumberFragment) {
            TopUpPrepaidOwnNumberFragment ownNumberFragment = (TopUpPrepaidOwnNumberFragment) VodafoneController.findFragment(TopUpPrepaidOwnNumberFragment.class);
            if (ownNumberFragment != null)
                ownNumberFragment.activateButton();
        } else if (fragment instanceof TopUpAnonymousFragment) {
            TopUpAnonymousFragment topUpAnonymousFragment = (TopUpAnonymousFragment) VodafoneController.findFragment(TopUpAnonymousFragment.class);
            if (topUpAnonymousFragment != null)
                topUpAnonymousFragment.activateButton();
        } else if (fragment instanceof TopUpPrepaidOtherNumberFragment) {
            TopUpPrepaidOtherNumberFragment otherNumberFragment = (TopUpPrepaidOtherNumberFragment) VodafoneController.findFragment(TopUpPrepaidOtherNumberFragment.class);
            if (otherNumberFragment != null)
                otherNumberFragment.activateButton();
        }
    }

    private void getFragmentInactiveButton() {
        Fragment fragment = FragmentUtils.getVisibleFragment((TopUpActivity) getActivity(), false);
        if (fragment instanceof TopUpPostpaidPost4PreFragment) {
            TopUpPostpaidPost4PreFragment post4PreFragment = (TopUpPostpaidPost4PreFragment) VodafoneController.findFragment(TopUpPostpaidPost4PreFragment.class);
            if (post4PreFragment != null)
                post4PreFragment.inactivateButton();
        } else if (fragment instanceof TopUpAnonymousFragment) {
            TopUpAnonymousFragment topUpAnonymousFragment = (TopUpAnonymousFragment) VodafoneController.findFragment(TopUpAnonymousFragment.class);
            if (topUpAnonymousFragment != null)
                topUpAnonymousFragment.inactivateButton();
        } else if (fragment instanceof TopUpPrepaidOtherNumberFragment) {
            TopUpPrepaidOtherNumberFragment otherNumberFragment = (TopUpPrepaidOtherNumberFragment) VodafoneController.findFragment(TopUpPrepaidOtherNumberFragment.class);
            if (otherNumberFragment != null)
                otherNumberFragment.inactivateButton();
        } else if (fragment instanceof TopUpPrepaidOwnNumberFragment) {
            TopUpPrepaidOwnNumberFragment ownNumberFragment = (TopUpPrepaidOwnNumberFragment) VodafoneController.findFragment(TopUpPrepaidOwnNumberFragment.class);
            if (ownNumberFragment != null)
                ownNumberFragment.inactivateButton();
        }
    }

    private void displayPermissionCheckedFlagOverlay() {
        readPermissionFlagCheckedOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        readPermissionFlagCheckedOverlay.setContentView(R.layout.overlay_dialog_notifications);
        readPermissionFlagCheckedOverlay.show();

        VodafoneButton acceptPermissionBtn = (VodafoneButton) readPermissionFlagCheckedOverlay.findViewById(R.id.buttonKeepOn);
        acceptPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPermissionFlagCheckedOverlay.dismiss();
            }
        });

        VodafoneButton refusePermisionBtn = (VodafoneButton) readPermissionFlagCheckedOverlay.findViewById(R.id.buttonTurnOff);
        refusePermisionBtn.setVisibility(View.GONE);

        ImageView closeBtn = (ImageView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlayDismissButton);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPermissionFlagCheckedOverlay.dismiss();
            }
        });

        VodafoneTextView overlayTitle = (VodafoneTextView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlayContext = (VodafoneTextView) readPermissionFlagCheckedOverlay.findViewById(R.id.overlaySubtext);

        overlayContext.setText(AppLabels.getOverlay_contacts_permission_context());
        overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());

        acceptPermissionBtn.setText(AppLabels.getOk_label());
    }

    private void displayReadContactsPermissionOverlay() {

        readContactsPermissionOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        readContactsPermissionOverlay.setContentView(R.layout.overlay_dialog_notifications);
        readContactsPermissionOverlay.show();

        VodafoneButton acceptPermissionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonKeepOn);
        acceptPermissionBtn.setText(AppLabels.getPayBillOverlayAcceptBtn());
        acceptPermissionBtn.setOnClickListener(readContactsPermissionListener);

        VodafoneButton refusePermisionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonTurnOff);
        refusePermisionBtn.setText(AppLabels.getPayBillOverlayRefuseBtn());
        refusePermisionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContactsPermissionOverlay.dismiss();
            }
        });

        ImageView closeBtn = (ImageView) readContactsPermissionOverlay.findViewById(R.id.overlayDismissButton);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContactsPermissionOverlay.dismiss();
            }
        });

        VodafoneTextView overlayTitle = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlayTitle);
        overlayTitle.setText(AppLabels.getPayBillOverlayTitle());
        VodafoneTextView overlayContext = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());
        overlayContext.setText(AppLabels.getOverlay_contacts_permission_context());
        acceptPermissionBtn.setText(AppLabels.getAccept_button_label());
        refusePermisionBtn.setText(AppLabels.getDo_later_button_label());
    }

    private void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    private boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }


    public String topUpErrorMessage() {

        String message = TopUPLabels.getTop_up_error_min_value();
        message = message.replace("{min_value}", AppConfiguration.getMinTopUpAmountForVerify());
        return message;

    }
    public ArrayList<CreditCardSelection>creditCardSelections = new ArrayList<>();
    int index = 0;
    protected void addCards(boolean is_max, List<Card> myCards) {
        cards_container.removeAllViews();
        if (myCards!=null&&!myCards.isEmpty()) {
            for (int i=0;i<myCards.size();i++) {
                Card card = myCards.get(i);
                final CreditCardSelection creditCardSelection = new CreditCardSelection(getActivity());
                creditCardSelection.setCard(card);
                if(i==0) {
                    creditCardSelection.selectCard(true);
                    activity.selectedCreditCard = creditCardSelection;
                    index =0;
                } else
                    creditCardSelection.selectCard(false);

                final int finalI = i;
                creditCardSelection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.selectedCreditCard!=null) {
                            activity.selectedCreditCard.selectCard(false);
                        }
                        activity.selectedCreditCard = creditCardSelection;
                        activity.selectedCreditCard.selectCard(true);
                        activity.saveCreditCard.selectRadioButton(false);
                        index = finalI;
                    }
                });
                creditCardSelections.add(creditCardSelection);
                cards_container.addView(creditCardSelection);

            }
        }
        boolean isSelected = false;
        if(myCards==null||myCards.size()==0)isSelected =true;
        addNewCreditCard(is_max,isSelected);
    }
    protected void addNewCreditCard(boolean is_max,boolean isSelected){
        activity.saveCreditCard =new SaveCreditCard(getContext());
        activity.saveCreditCard.selectRadioButton(isSelected);
        if(!is_max)
            activity.saveCreditCard.enableCheckBox(true);
        else
            activity.saveCreditCard.enableCheckBox(false);

        activity.saveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.saveCreditCard.selectRadioButton(true);
                if(activity.selectedCreditCard!=null)
                    activity.selectedCreditCard.selectCard(false);
                activity.selectedCreditCard = null;
            }
        });
        add_new_card_container.addView(activity.saveCreditCard);


    }
    protected void loadCards() {
        showLoadingDialog();
        MyCardsService myCardsService = new MyCardsService(getContext());
        myCardsService.getCards(VodafoneController.getInstance().getUserProfile().getUserName()).subscribe(new RequestSaveRealmObserver<GeneralResponse<CardsResponse>>(){
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                addCards(false,null);
            }

            @Override
            public void onNext(GeneralResponse<CardsResponse> response) {
                super.onNext(response);
                stopLoadingDialog();
                if(response!=null&&response.getTransactionSuccess()!=null&&response.getTransactionSuccess().getCardList().size()>0)
                    addCards(response.getTransactionSuccess().getMaxReached(),response.getTransactionSuccess().getCardList());
                else
                    addCards(false,null);
            }
        });
    }

}
