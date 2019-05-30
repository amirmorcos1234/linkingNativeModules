package ro.vodafone.mcare.android.ui.activities.offers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationTimeEnum;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.OfferBasicInfo;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPostpaid.ImmediateOrNextBillActivationFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory.EO_Activation;
import static ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment.IS_SERVICE_KEY;

/**
 * Created by Alex on 3/15/2017.
 */

public class BeoActivationPostPaidActivity extends BaseActivity {

    public static String TAG = "BeoActivationPostpaid";
    private static final String ANALYTICS_LOG_EO_ACTIVATION_KEY = "eo_activation";

    private OfferRowInterface offerRow;
    private boolean isServices;
    private List<OfferBasicInfo> incompatibleOffers;
    private boolean isETF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beo_activation_overlay_activity);

        offerRow = getOfferRowFromRealm(getIntent().getStringExtra(OfferRowInterface.KEY_ID));
        isServices = getIntent().getBooleanExtra(IS_SERVICE_KEY, false);

        String subscriberId = UserSelectedMsisdnBanController.getInstance().getSubscriberSid() != null
                ? UserSelectedMsisdnBanController.getInstance().getSubscriberSid()
                    : VodafoneController.getInstance().getUserProfile().getSid();
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn()
                    : VodafoneController.getInstance().getUserProfile().getMsisdn();

        //showLoadingDialog();

        getActivatePostPaidEligibleOffer(msisdn, subscriberId, ((PostpaidOfferRow) offerRow).getMatrixId());

        BeoActivationPostPaidTrackingEvent event = new BeoActivationPostPaidTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        if(isServices){
            tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.serviceActivationOverlayPostpaid);
        }else{
            tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.bonusActivationOverlayPostpaid);
        }
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.bonusesOptionsJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
    }

    private OfferRowInterface getOfferRowFromRealm(String offerId) {
        if(offerId == null){
            return null;
        }
        return (PostpaidOfferRow) RealmManager.getRealmObjectAfterStringField(
                PostpaidOfferRow.class, PostpaidOfferRow.MATRIX_ID, offerId);
    }

    public void addFragment(Fragment fragment) {
        //stopLoadingDialog();
        Log.d(TAG, "addFragment()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass())
            transaction.addToBackStack(null);
        transaction.replace(R.id.beo_activation_prepaid_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
    }

    private void replaceFirst(String msisdn) {
        if (msisdn != null && msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
            Log.d(TAG, " concat msisdn " + msisdn);
        }
    }

    private void getActivatePostPaidEligibleOffer(String msisdn, String sid, String matrix_id) {
        Log.d(TAG, "getActivatePostPaidEligibleOffer() number: " + msisdn + "   sid: " + sid + "  matrix_id: " + matrix_id);

        replaceFirst(msisdn);

        OffersService offersService = new OffersService(this);
        offersService.getActivatePostPaidEligibleOffer(msisdn, sid, matrix_id).subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActivationEligibilitySuccess> eligibleOffersPostSuccessResponse) {
                manageGetActivatePostPaidEligibleOffer(eligibleOffersPostSuccessResponse);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                manageErrorCodes("");
            }
        });
    }

    private void manageGetActivatePostPaidEligibleOffer(GeneralResponse<ActivationEligibilitySuccess> eligibleOffersPostSuccessResponse) {
        Log.d(TAG, "TransactionStatus " + eligibleOffersPostSuccessResponse.getTransactionStatus());
        if (eligibleOffersPostSuccessResponse != null && eligibleOffersPostSuccessResponse.getTransactionStatus() == 0) {
            if (eligibleOffersPostSuccessResponse.getTransactionSuccess() != null) {

                if (eligibleOffersPostSuccessResponse.getTransactionSuccess().getIncompatbileOffers() != null &&
                        !eligibleOffersPostSuccessResponse.getTransactionSuccess().getIncompatbileOffers().isEmpty()) {
                    Log.d(TAG, "incompatibleOffers ");
                    incompatibleOffers = eligibleOffersPostSuccessResponse.getTransactionSuccess().getIncompatbileOffers();
                    Log.d(TAG, "incompatibleOffers " + incompatibleOffers);
                }
                if (eligibleOffersPostSuccessResponse.getTransactionSuccess().getHasETF() != null) {
                    Log.d(TAG, "ETF ");
                    isETF = eligibleOffersPostSuccessResponse.getTransactionSuccess().getHasETF();
                    Log.d(TAG, "ETF " + isETF);
                }
                if (eligibleOffersPostSuccessResponse.getTransactionSuccess().getActivationTime() != null) {
                    Log.d(TAG, "Goid to comapre value: " + ActivationTimeEnum.NOW);
                    if (eligibleOffersPostSuccessResponse.getTransactionSuccess().getActivationTime().equals(ActivationTimeEnum.NOW.name())) {
                        Log.d(TAG, "getActivatePostPaidEligibleOffer() activation time : NOW");
                        addFragment((Fragment) ImmediateOrNextBillActivationFragment.create(offerRow, true, incompatibleOffers));
                    } else {
                        Log.d(TAG, "getActivatePostPaidEligibleOffer() activation time : BC");
                        addFragment((Fragment) ImmediateOrNextBillActivationFragment.create(offerRow, false, incompatibleOffers));
                    }
                }
            }
            return;
        }
        stopLoadingDialog();
        manageErrorCodes(eligibleOffersPostSuccessResponse.getTransactionFault() != null ? eligibleOffersPostSuccessResponse.getTransactionFault().getFaultCode() : "");

    }

    private void manageErrorCodes(String errorCode) {
        switch (errorCode) {
            case "EC04102":
                displayIncompatibleOverlay();
                D.w("Offer is incompatible with current price plan");
                break;
            default:
                new CustomToast.Builder(BeoActivationPostPaidActivity.this).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                finish();
                break;
        }
    }

    public void displayIncompatibleOverlay() {

        final Dialog overlayDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlayDialog.show();

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);
        overlayTitle.setText("Extraopțiune incompatibilă");
        overlaySubtext.setText("Opțiunea nu a fost activată pentru că nu este compatibilă cu abonamentul tău. Pentru mai multe detalii te rugăm să iei legătura cu noi pe chat.");

        VodafoneButton overlayCancelBlockButton = (VodafoneButton) overlayDialog.findViewById(R.id.buttonKeepOn);
        VodafoneButton  overlayBlockButton= (VodafoneButton) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        overlayBlockButton.setText("Înapoi");
        overlayCancelBlockButton.setVisibility(View.GONE);

        overlayBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                OffersActivity activity = (OffersActivity) VodafoneController.findActivity(OffersActivity.class);
                if(activity!=null){
                    activity.onBackPressed();
                }
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                OffersActivity activity = (OffersActivity) VodafoneController.findActivity(OffersActivity.class);
                if(activity!=null){
                    activity.onBackPressed();
                }
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlayDialog.dismiss();
                finish();
            }
        });
    }


    public void putActivatePostPaidEligibleOffer(ActivationRequest activationRequest, String msisdn, String sid, String matrix_id, final boolean isNow, final String offerCategory) {
        Log.d(TAG, "putActivatePostPaidEligibleOffer() number: " + msisdn + "   sid: " + sid + "  matrix_id: " + matrix_id);
        OffersService offersService = new OffersService(this);

        replaceFirst(msisdn);
        Log.d(TAG, " is now?: " + isNow);

        offersService.putActivatePostPaidEligibleOffer(activationRequest, msisdn, sid, matrix_id).subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActivationEligibilitySuccess> eligibleOffersPostSuccessResponse) {
                managePutActivatePostPaidEligibleOffer(eligibleOffersPostSuccessResponse, isNow, offerCategory);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                manageErrorCodes("");
            }
        });
    }

    private void managePutActivatePostPaidEligibleOffer(GeneralResponse<ActivationEligibilitySuccess> eligibleOffersPostSuccessResponse, boolean isNow, String offerCategory) {
        stopLoadingDialog();
        if (eligibleOffersPostSuccessResponse != null && eligibleOffersPostSuccessResponse.getTransactionStatus() == 0) {
            if (isNow) {
                Log.d(TAG, "activate now");
                VoiceOfVodafone vov;
                if(AppConfiguration.getVodafoneTvCategories().contains(offerCategory) && !(VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
                  vov = new VoiceOfVodafone(11,20,EO_Activation, null, String.format(isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part1():BEOLabels.getBeo_activate_prepaid_offer_part1(),
                          offerRow.getOfferName()), VodafoneTvLabels.getVtvActivationVovDismissButton(), VodafoneTvLabels.getVtvAppVovLabel(), true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.ExternalAppRedirect);
                  vov.setRightActionUrl(AppConfiguration.getVodafoneTvVovExternalLink());
                } else {
                    vov = new VoiceOfVodafone(11, 20, EO_Activation, null, String.format(isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part1():BEOLabels.getBeo_activate_prepaid_offer_part1(),
                            offerRow.getOfferName()), "Ok", null,
                            true, false, VoiceOfVodafoneAction.Dismiss, null);
                }
                DashboardController.reloadDashboardOnResume();
                VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
                new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext()).message(isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part2() : BEOLabels.getBeo_now_success_activation()).success(true).show();
            } else {
                Log.d(TAG, "activate after next bill");
                VoiceOfVodafone vov = new VoiceOfVodafone(12, 20, EO_Activation, null, String.format(isServices ? BEOLabels.getBeo_success_services_activation_next_billCycle() : BEOLabels.getBeo_success_activation_next_billcycle(),
                        offerRow.getOfferName()), "Ok", null,
                        true, false, VoiceOfVodafoneAction.Dismiss, null);
                DashboardController.reloadDashboardOnResume();
                VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
                new CustomToast.Builder(BeoActivationPostPaidActivity.this).message(isServices ? BEOLabels.getBeo_service_next_billCycle() : BEOLabels.getBeo_next_bill_cycle()).success(true).show();
            }

            logGoogleAnalytics();

            new NavigationAction(BeoActivationPostPaidActivity.this).startAction(IntentActionName.DASHBOARD,true);
            trackView();
            return;

        }
        manageErrorCodes("");
    }

    /*
    * Tealium trigger survey
    * Tealium Track view
    */
    private void trackView(){
        Map<String, Object> tealiumMapView = new HashMap(4);
        tealiumMapView.put("screen_name", "buyaddon");
        //add Qualtrics survey
        TealiumHelper.addQualtricsCommand();
        //track
        TealiumHelper.trackView("buyaddon", tealiumMapView);
    }

    public List<OfferBasicInfo> getIncompatibleOffers() {
        return incompatibleOffers;
    }

    public void setIncompatibleOffers(List<OfferBasicInfo> incompatibleOffers) {
        this.incompatibleOffers = incompatibleOffers;
    }

    public boolean isETF() {
        return isETF;
    }

    public void setETF(boolean ETF) {
        isETF = ETF;
    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        super.onBackPressed();
    }

    private FirebaseAnalyticsItem getBeoValue() {
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

        FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
        firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
        firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("eo_activation_MSISDN", msisdn);
        firebaseAnalyticsItem.addFirebaseAnalyticsParams("eo_activation_eo_name", offerRow.getOfferName(),
                "eo_activation_id", ((PostpaidOfferRow)offerRow).getMatrixId()+"",
                "eo_activation_xxEUR", offerRow.getOfferPrice() + "EUR");

        return firebaseAnalyticsItem;
    }

    private void logGoogleAnalytics() {
        FirebaseAnalyticsItem firebaseAnalyticsItem = getBeoValue();
        sendFirebaseEvent(firebaseAnalyticsItem.getFirebaseAnalyticsEvent(),
                FirebaseAnalyticsUtils.getBundleFromParams(firebaseAnalyticsItem.getFirebaseAnalyticsParams()));
    }

    public static class BeoActivationPostPaidTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "bonus or service activation overlay postpaid";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "bonus or service activation overlay postpaid");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);

        }
    }
}
