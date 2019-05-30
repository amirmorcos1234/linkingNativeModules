package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;

import com.adobe.mobile.Config;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.billing.GetPaymentInputsResponse;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.payBill.BillingWebViewModel;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.TransactionFault;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.interfaces.BillingFragmentInterface;
import ro.vodafone.mcare.android.interfaces.PayBillServicesInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.paybill.AnonymousPayBillFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.BillOptionFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.PayBillFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.BillingWebViewActivity;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Bivol Pavel on 25.01.2017.
 */
public class PayBillActivity extends MenuActivity implements PayBillServicesInterface {

    public static String TAG = "PayBillActivity";
    public static final String FRAGMENT = "fragment";
    public static final String EXTRA_PARAMETER_BUNDLE_KEY = "extraParameter";
    public static final String ACTIVITY_IDENTIFIER = "PayBillActivity";

    private VodafoneTextView title;
    private NavigationHeader navigationHeader;

    private boolean isOwnBillPaid;
    private boolean noBillIssued;
    private boolean isApiFailed;
    public CreditCardSelection selectedCreditCard=null;
    public SaveCreditCard saveCreditCard=null;
    public boolean isOwnBillPaid() {
        return isOwnBillPaid;
    }

    public void setIsOwnBillPaid(boolean isOwnBillPaid) {
        this.isOwnBillPaid = isOwnBillPaid;
    }

    public boolean isNoBillIssued() {
        return noBillIssued;
    }

    public void setNoBillIssued(boolean noBillIssued) {
        this.noBillIssued = noBillIssued;
    }

    public boolean isApiFailed() {
        return isApiFailed;
    }

    public void setApiFailed(boolean apiFailed) {
        isApiFailed = apiFailed;
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

        initNavigationFragment();
        initTracking();
        onBundleReceived();
    }

    private void initTracking() {
        PayBillTrackingEvent event = new PayBillTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        getToolbar().showToolBar();
        if (FragmentUtils.getVisibleFragment(this, false) instanceof BillOptionFragment || FragmentUtils.getVisibleFragment(this, false) instanceof AnonymousPayBillFragment) {
            Log.d(TAG, "onResume() hideSelector View");
            navigationHeader.hideSelectorView();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        Config.pauseCollectingLifecycleData();
    }

    public void onBundleReceived() {
        Log.d(TAG, "onBundleReceived");
        if (getIntent() != null && getIntent().getExtras() != null) {

            String value = getIntent().getExtras().getString(EXTRA_PARAMETER_BUNDLE_KEY);
            Log.d(TAG, "onBundleReceived - " + value);
            if (value != null) {
                Log.d(TAG, value);
                if (value.equals("pay_own_bill")) {
                    clearIntent();
                    if (UserSelectedMsisdnBanController.getInstance().getSelectedBan() != null) {
                        getInvoiceDetails(new PayBillFragment(), null, UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber(), false);
                    } else {
                        getInvoiceDetails(new PayBillFragment(), null, UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), false);
                    }
                    return;
                }
            }
        }
        redirectToFragment();
    }

    private void clearIntent() {
        Log.d(TAG, "clearIntent");
        IntentActionName i = IntentActionName.PAY_BILL;
        i.setExtraParameter(null);
        getIntent().removeExtra(EXTRA_PARAMETER_BUNDLE_KEY);
    }

    private void redirectToFragment() {
        Log.d(TAG, "redirectToFragment");
        User user = VodafoneController.getInstance().getUser();
        Log.d(TAG, "redirectToFragment: user: " + user);

        if (allotToDisplayBillOptionPage()) {
            displayPage(new BillOptionFragment(), null);
        } else {
            displayPage(new AnonymousPayBillFragment(), null);
        }
    }

    private boolean allotToDisplayBillOptionPage() {
        User user = VodafoneController.getInstance().getUser();
        return (user instanceof SeamlessPostPaidHighAccess || user instanceof PrivateUser || user instanceof ResCorp ||
                user instanceof PrepaidHybridUser || user instanceof CorpSubUser || user instanceof CorpUser ||
                (user instanceof EbuMigrated && !EbuMigratedIdentityController.isEbuMigratedSubscriberOrNotVerified()
                        && !(user instanceof SeamlessEbuUser)));
    }

    @Override
    protected int setContent() {
        return R.layout.activity_pay_bill;
    }

    @Override
    public void getInvoiceDetails(final BillingFragmentInterface fragment, final String msisdn, final String ban, final boolean isAnonymousPayment) {
        if (msisdn == null && ban == null) {
            Log.d(TAG, "get msisdn");
            UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);

            if (UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null) {
                getInvoiceDetails(fragment, null, UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), isAnonymousPayment);
            } else {
                if (userProfile != null && !(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
                    getInvoiceDetails(fragment, userProfile.getMsisdn(), null, isAnonymousPayment);
                }
            }
        } else {
            showLoadingDialog();
            BillingServices billingServices = new BillingServices(getApplicationContext());

            billingServices.getInvoiceDetails(msisdn, ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<InvoiceDetailsSuccess>>() {
                @Override
                public void onNext(GeneralResponse<InvoiceDetailsSuccess> generalResponse) {
                    super.onNext(generalResponse);
                    stopLoadingDialog();

                    //Reset Parameters
                    setNoBillIssued(false);
                    setIsOwnBillPaid(false);
                    setApiFailed(false);

                    if (generalResponse.getTransactionStatus() == 0 && generalResponse.getTransactionSuccess() != null) {
                        if (isAnonymousPayment) {
                            doPaymentBill(fragment.getAnonymousPhoneNumber(),
                                    fragment.getAnonymousInvoiceValue(),
                                    fragment.getAnonymousEmailAddress(),
                                    generalResponse.getTransactionSuccess().getAccountNo(),
                                    generalResponse.getTransactionSuccess().getInvoiceNo());
                        } else {
                            if (isNegativeAmount(generalResponse.getTransactionSuccess())) {
                                setIsOwnBillPaid(true);
                            }
                            displayPage(fragment, generalResponse.getTransactionSuccess());
                        }
                    } else {
                        TransactionFault transactionFault = generalResponse.getTransactionFault();

                        if (transactionFault != null && transactionFault.getFaultCode() != null) {
                            manageFaultCodes(transactionFault.getFaultCode());
                            if (isAnonymousPayment)
                                fragment.manageAnonymousPaymentErrors(transactionFault.getFaultCode());
                        }
                        if (!isAnonymousPayment)
                            displayPage(fragment, null);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    stopLoadingDialog();
                    setApiFailed(true);
                    if (isAnonymousPayment)
                        new CustomToast.Builder(PayBillActivity.this).message("Serviciu momentan indisponibil!").success(false).show();
                    else
                        displayPage(fragment, null);

                }
            });
        }
    }

    private void manageFaultCodes(String faultCode) {
        if (faultCode.equals(ErrorCodes.API23_INVOICE_ALREADY_PAID.getErrorCode())
                || faultCode.equals(ErrorCodes.API23_INVOICE_NOT_AVAILABLE.getErrorCode())) {
            setIsOwnBillPaid(true);
        } else if (faultCode.equals(ErrorCodes.API23_INVOICE_NOT_ISSUED.getErrorCode())) {
            setNoBillIssued(true);
        } else {
            setApiFailed(true);
        }
    }


    private boolean isNegativeAmount(InvoiceDetailsSuccess invoiceDetailsSuccess) {
        return Float.valueOf(invoiceDetailsSuccess.getInvoiceAmount()) <= 0;
    }

    @Override
    public void doPaymentBill(String phoneNumber, final String amount, String email, String accountNo, String invoiceNo) {

        showLoadingDialog();
        boolean isSave = false;
        String token = null;
        if(saveCreditCard!=null){
            isSave=saveCreditCard.getCheckBox().isChecked();
            token = null;
        }
        if(selectedCreditCard!=null){
            token = selectedCreditCard.getCard().getToken();
            isSave = false;
        }
        BillingServices billingServices = new BillingServices(getApplicationContext());

        final String finalToken = token;
        billingServices.doPaymentBill(phoneNumber, Float.valueOf(amount), email, accountNo, invoiceNo,token,isSave).subscribe(new RequestSessionObserver<GeneralResponse<GetPaymentInputsResponse>>() {
            @Override
            public void onNext(GeneralResponse<GetPaymentInputsResponse> getPaymentInputsResponseGeneralResponse) {
                stopLoadingDialog();

                if (getPaymentInputsResponseGeneralResponse.getTransactionStatus() == 0) {

                    if(finalToken !=null){
                        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(2, 20, VoiceOfVodafoneCategory.Pay_Bill, null, CallDetailsLabels.getBilled_success_message_vov(), "Ok, am înțeles.", null,
                                true, false, VoiceOfVodafoneAction.Dismiss, null);
                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                        new CustomToast.Builder(PayBillActivity.this).message(CallDetailsLabels.getBilled_success_message())
                                .success(true).show();
                        new NavigationAction(PayBillActivity.this).startAction(IntentActionName.DASHBOARD,true);

                    }else {
                        String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

                        FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
                        firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
                        firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("pay_bill_MSISDN", msisdn);
                        firebaseAnalyticsItem.addFirebaseAnalyticsParams("pay_bill_XXRON", amount + "RON");

                        BillingWebViewModel billingWebViewModel = new BillingWebViewModel();
                        billingWebViewModel.setHtmlInputs(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getHtmlForm());
                        billingWebViewModel.setSuccessUrl(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getSuccessUrl());
                        billingWebViewModel.setSuccessMessage(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getSuccessMessage());
                        billingWebViewModel.setActivityIdentifier(ACTIVITY_IDENTIFIER);
                        billingWebViewModel.setAnalyticsValue(firebaseAnalyticsItem);

                        IntentActionName.BILLING_WEBVIEW.setOneUsageSerializedData(new Gson().toJson(billingWebViewModel));
                        new NavigationAction(PayBillActivity.this).startAction(IntentActionName.BILLING_WEBVIEW);
                    }
                } else {
                    new CustomToast.Builder(PayBillActivity.this).message("Serviciu momentan indisponibil!").success(false).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(PayBillActivity.this).message("Serviciu momentan indisponibil!").success(false).show();
            }
        });
    }

    public void displayPage(BillingFragmentInterface fragment, InvoiceDetailsSuccess invoiceDetailsSuccess) {
        Log.d(TAG, "displayPage: ");
        if (fragment instanceof BillOptionFragment || fragment instanceof PayBillFragment || fragment instanceof AnonymousPayBillFragment) {
            title.setText(PayBillLabels.getPayBillTitle());
        } else {
            title.setText(PayBillLabels.getNetopiaWebViewTittle());
        }
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() == fragment.getClass()) {
            fragment = (BillingFragmentInterface) FragmentUtils.getVisibleFragment(this, false);
            fragment.updateFragment(invoiceDetailsSuccess);
            fragment.populateView();
        } else {
            if (!isFinishing())
                addFragment(fragment.updateFragment(invoiceDetailsSuccess));
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));

        if (VodafoneController.isActivityVisible())
            transaction.commit();
        else
            transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Current fragment" + FragmentUtils.getVisibleFragment(this, false));
        KeyboardHelper.hideKeyboard(this);
        VodafoneController.getInstance().setFromBackPress(true);

        if (FragmentUtils.getVisibleFragment(this, false) instanceof PayBillFragment ||
                FragmentUtils.getVisibleFragment(this, false) instanceof AnonymousPayBillFragment) {
            if (!FragmentUtils.isFragmentInBackStack(BillOptionFragment.class, this) && allotToDisplayBillOptionPage()) {
                closeDrawers();
                displayPage(new BillOptionFragment(), null);
            } else {
                super.onBackPressed();
            }
        } else {
            finish();
        }
    }

    @Override
    public void switchFragmentOnCreate(String fragmentName, String extraParameter) {

        if (fragmentName != null) {
            if (fragmentName.equals(IntentActionName.PAY_BILL_OWN.getFragmentClassName())) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("getInvoiceFromRealm", true);
                Fragment fragment = FragmentUtils.newInstance(PayBillFragment.class, bundle);
                addFragment(fragment);
            } else {
                addFragment((Fragment) FragmentUtils.newInstanceByClassName(fragmentName));
            }
        }
    }

    private void initNavigationFragment() {
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this)
                .setTitle(PayBillLabels.getPayBillTitle())
                .displayDefaultHeader()
                .buildBanSelectorHeader();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() with code: " + requestCode);

        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                if (FragmentUtils.getVisibleFragment(this, false) instanceof PayBillFragment) {
                    getInvoiceDetails(new PayBillFragment(), null, UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), false);
                }
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "onAttachFragment()");
        scrolltoTop();
    }

    public static class PayBillTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "pay bill";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "pay bill");


            s.channel = "pay bill";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "pay bill";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
