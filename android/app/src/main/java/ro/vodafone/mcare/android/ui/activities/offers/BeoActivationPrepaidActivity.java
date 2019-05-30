package ro.vodafone.mcare.android.ui.activities.offers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.billing.GetPaymentInputsResponse;
import ro.vodafone.mcare.android.client.model.payBill.BillingWebViewModel;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BalanceService;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid.ConfirmationActivationFragment;
import ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid.PayWayFragment;
import ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid.PendingExtraOptions;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.offers.ActivationPayTypeEnum.SIMPLE;
import static ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment.IS_OFFERS_IN_PENDING;
import static ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment.IS_SERVICE_KEY;

/**
 * Created by Alex on 3/14/2017.
 */

public class BeoActivationPrepaidActivity extends BaseActivity {

    public static String TAG = "BeoActivationPrepaid";

    private boolean isServices;
    private boolean isOffersInPending;
    private float availableCredit;
    private final static String ACTIVITY_IDENTIFIER = "BeoActivationPrepaid";

    private OfferRowInterface offerRow;
    private ActiveOffersSuccess activeOffersSuccess;
    private Float balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beo_activation_overlay_activity);
        Intent intent = getIntent();
        isServices = intent.getBooleanExtra(IS_SERVICE_KEY, false);
        isOffersInPending = intent.getBooleanExtra(IS_OFFERS_IN_PENDING, false);
        offerRow = getOfferRowFromRealm(getIntent().getLongExtra(OfferRowInterface.KEY_ID, 0));

        beginActivationFlow();

        /*try {
            //Tealium Track view
            Map<String, Object> tealiumMapView =new HashMap(6);
            tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.bonusServiceActivationOverlayPrepaid);
            tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
            if (VodafoneController.getInstance().getUserProfile().getUserRole() != null)
                tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        }catch (Exception e){
            e.printStackTrace();
        }
*/
        BeoActivationPrePaidTrackingEvent event = new BeoActivationPrePaidTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    private OfferRowInterface getOfferRowFromRealm(Long idOfferRow){
        if(idOfferRow == null || idOfferRow == 0){
            return null;
        }

        return (OfferRowInterface) RealmManager.getRealmObjectAfterLongField(
                PrepaidOfferRow.class, PrepaidOfferRow.OFFER_ID, idOfferRow);
    }

    public void addFragment(Fragment fragment) {
        //stopLoadingDialog();
        Log.d(TAG, "addFragment()");
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
                //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
                transaction.addToBackStack(null);
            }
            transaction.replace(R.id.beo_activation_prepaid_container, fragment, FragmentUtils.getTagForFragment(fragment));
            transaction.commitAllowingStateLoss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void beginActivationFlow() {
        try {
            Log.d(TAG, "beginActivationFlow: ");
            if (offerRow.getOfferPrice() != null && offerRow.getOfferPrice() == 0) {
                Log.d(TAG, "OfferPrice is = 0 " + offerRow.getOfferPrice());
                addFragment(ConfirmationActivationFragment.newInstance(isServices));
                return;
            }
            //make call
            Log.d(TAG, "OfferPrice not = 0 " + offerRow.getOfferPrice());
            getPrepaidBalanceCredit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OfferRowInterface getOfferRow() {
        return offerRow;
    }

    public void setOfferRow(OfferRowInterface offerRow) {
        this.offerRow = offerRow;
    }


    private void getPrepaidBalanceCredit() {
        Log.d(TAG, "reload() ");
        BalanceService balanceService = new BalanceService(this);

        balanceService.getBalanceCredit(false).subscribe(new RequestSaveRealmObserver<GeneralResponse<BalanceCreditSuccess>>() {

            @Override
            public void onNext(GeneralResponse<BalanceCreditSuccess> checkCreditSuccessResponse) {
                super.onNext(checkCreditSuccessResponse);
                manageGetPrepaidBalanceCredit(checkCreditSuccessResponse);
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "getBalanceCredit onCompleted");
                checkBalanceValue();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                finish();
            }
        });
    }

    private void manageGetPrepaidBalanceCredit(GeneralResponse<BalanceCreditSuccess> checkCreditSuccessResponse) {
        if (checkCreditSuccessResponse != null && checkCreditSuccessResponse.getTransactionStatus() == 0) {
            Log.d(TAG, "reload  TransactionStatus 0 ");
            if (checkCreditSuccessResponse.getTransactionSuccess() != null && checkCreditSuccessResponse.getTransactionSuccess().getBalance() != null) {
                Log.d(TAG, "reload Balance is NOT NULL: ");
                availableCredit = checkCreditSuccessResponse.getTransactionSuccess().getBalance();
                return;
            }
        }

        Log.d(TAG, "reload  TransactionStatus = " + checkCreditSuccessResponse.getTransactionStatus() + " reload Balance is " + checkCreditSuccessResponse.getTransactionSuccess().getBalance());
        new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
        finish();
    }

    private void checkBalanceValue() {
        Log.d(TAG, "checkBalanceValue() " + availableCredit);
        Log.d(TAG, "checkBalanceValue ? offer price : " + availableCredit + " ? " + offerRow.getOfferPrice());

        if (availableCredit >= offerRow.getOfferPrice()) {
            // load confimramtion fragment
            addFragment(ConfirmationActivationFragment.newInstance(isServices));
        } else {
            getEligibleActiveOffers4PrePaid();

        }

    }

    //api -35
    private void getEligibleActiveOffers4PrePaid() {
        Log.d(TAG, "getEligibleActiveOffers4PrePaid ");
        OffersService offersService = new OffersService(this);

        offersService.getEligibleActiveOffers4PrePaid(VodafoneController.getInstance().getUserProfile().getSid()).subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
                manageGetEligibleActiveOffers4PrePaid(activeOffersSuccessResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                finish();
            }
        });
    }

    private void manageGetEligibleActiveOffers4PrePaid(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
        if (activeOffersSuccessResponse != null && activeOffersSuccessResponse.getTransactionStatus() == 0) {
            if (activeOffersSuccessResponse.getTransactionSuccess() != null && activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList() != null && !activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList().isEmpty()) {
                activeOffersSuccess = activeOffersSuccessResponse.getTransactionSuccess();
                Log.d(TAG, "ActiveOffersList Not null and not empty ");
                checkMsisdnHasInactiveEO(true);
            } else {
                Log.d(TAG, "ActiveOffersList NULL or EMPTY ");
                checkMsisdnHasInactiveEO(false);
            }
            return;
        }
        new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
        finish();
    }

    public void checkMsisdnHasInactiveEO(boolean proveValue) {
        Log.d(TAG, "checkMsisdnHasInactiveEO() promvalue  - " + proveValue);

        if (proveValue && chechIfIsPendingOffers()) {
            addFragment(new PendingExtraOptions());
            return;
        }

        if (availableCredit > 0) {
            Log.d(TAG, "Available credit > 0 " + availableCredit);
            addFragment(new PayWayFragment());
        } else {
            Log.d(TAG, "Available credit NOT > 0 " + availableCredit);
            getRechargeAndActivate(SIMPLE);
        }

    }

    private boolean chechIfIsPendingOffers() {
        Log.d(TAG, "chechIfIsPendingOffers()");
        return VodafoneController.getInstance().getUser() instanceof PrepaidUser ? isPrepaidPendingOffers() : isOffersInPending;
    }

    private boolean isPrepaidPendingOffers() {
        Log.d(TAG, "isPrepaidPendingOffers()");
        boolean isPending = false;

        if (activeOffersSuccess != null) {

            for (ActiveOffer activeOffer : activeOffersSuccess.getActiveServicesList()) {
                if (activeOffer.getOfferStatus() != null && activeOffer.getOfferStatus().equals("0")) {
                    Log.d(TAG, "Offer with id :" + activeOffer.getOfferId() + " is in pending");
                    isPending = true;
                    break;
                }
            }

            for (ActiveOffer activeOffer : activeOffersSuccess.getActiveOffersList()) {
                if (activeOffer.getOfferStatus() != null && activeOffer.getOfferStatus().equals("0")) {
                    Log.d(TAG, "Offer with id :" + activeOffer.getOfferId() + " is in pending");
                    isPending = true;
                    break;
                }
            }

        }

        return isPending;
    }

    public void getRechargeAndActivate(ActivationPayTypeEnum paymentType) {
        Log.d(TAG, "getRechargeAndActivate() paymentType:  " + paymentType);
        double ammount = managePaymentType(paymentType);

        BillingServices billingServices = new BillingServices(this);
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        String phoneNumber = userProfile.getMsisdn();  //.substring(1)
        String email = userProfile.getEmail();
        billingServices.doRechargeAndActivate(phoneNumber.substring(1), (float) ammount, email,
                offerRow.getOfferId() + "", userProfile.getSid()).subscribe(new RequestSessionObserver<GeneralResponse<GetPaymentInputsResponse>>() {

            @Override
            public void onNext(GeneralResponse<GetPaymentInputsResponse> getPaymentInputsResponseGeneralResponse) {
                manageGetRechargeAndActivate(getPaymentInputsResponseGeneralResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "onError()");
                new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                finish();
            }
        });
    }

    private double managePaymentType(ActivationPayTypeEnum paymentType) {
        double ammount = 0d;
        switch (paymentType) {
            case SIMPLE:
                ammount = offerRow.getOfferPrice();
                Log.d(TAG, "SIMPLE ammount: " + ammount);
                break;
            case CREDIT:
                ammount = (offerRow.getOfferPrice() - availableCredit) > 1 ? (offerRow.getOfferPrice() - availableCredit) : 1;
                Log.d(TAG, "CREDIT ammount: " + ammount);
                break;
            case CARD:
                ammount = (offerRow.getOfferPrice()) > 1 ? offerRow.getOfferPrice() : 1;
                Log.d(TAG, "CARD ammount: " + ammount);
                break;
        }
        return ammount;
    }

    private void manageGetRechargeAndActivate(GeneralResponse<GetPaymentInputsResponse> getPaymentInputsResponseGeneralResponse) {
        if (getPaymentInputsResponseGeneralResponse != null && getPaymentInputsResponseGeneralResponse.getTransactionStatus() == 0) {
            Log.d(TAG, "Transaction 0");

            String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

            FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
            firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
            firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("eo_activation_MSISDN", msisdn);
            firebaseAnalyticsItem.addFirebaseAnalyticsParams("eo_activation_eo_name", offerRow.getOfferName(),
                    "eo_activation_id", offerRow.getOfferId()+"", "eo_activation_xxEUR", offerRow.getOfferPrice() + "EUR");

            BillingWebViewModel billingWebViewModel = new BillingWebViewModel();
            billingWebViewModel.setHtmlInputs(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getHtmlForm());
            billingWebViewModel.setSuccessUrl(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getSuccessUrl());
            billingWebViewModel.setSuccessMessage(getPaymentInputsResponseGeneralResponse.getTransactionSuccess().getSuccessMessage());
            billingWebViewModel.setActivityIdentifier(ACTIVITY_IDENTIFIER);
            billingWebViewModel.setOfferName(offerRow.getOfferName());
            billingWebViewModel.setServices(isServices);
            billingWebViewModel.setAnalyticsValue(firebaseAnalyticsItem);


            IntentActionName.BILLING_WEBVIEW.setOneUsageSerializedData(new Gson().toJson(billingWebViewModel));
            new NavigationAction(BeoActivationPrepaidActivity.this).startAction(IntentActionName.BILLING_WEBVIEW);
            finish();
        } else {
            new CustomToast.Builder(BeoActivationPrepaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
            finish();
        }
    }

    public static class BeoActivationPrePaidTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "bonus or service activation overlay prepaid";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "bonus or service activation overlay prepaid");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLoadingDialog();
    }

    public double getOfferPrice() {
        return offerRow.getOfferPrice();
    }

    public boolean isServices() {
        return isServices;
    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        super.onBackPressed();
        if (FragmentUtils.getVisibleFragment(this, false) instanceof PendingExtraOptions) {
            OffersActivity activity = (OffersActivity) VodafoneController.findActivity(OffersActivity.class);
            finish();
            if (activity != null)
                activity.onBackPressed();
        }
    }
}
