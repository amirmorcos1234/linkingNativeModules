package ro.vodafone.mcare.android.ui.activities.offers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.StringEscapeUtils;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.EbuOfferEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.activationEbuOverlays.BeoEBUActivationConfirmationOverlay;
import ro.vodafone.mcare.android.ui.fragments.Beo.activationEbuOverlays.EbuETFOverlay;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment.IS_SERVICE_KEY;

/**
 * Created by user on 20.02.2018.
 */

public class BeoActivationEbuActivity extends BaseActivity {

    private static final int WAITING_TIME = 5000;
    private static final String ANALYTICS_LOG_EO_ACTIVATION_KEY = "eo_activation";

    private long startTime;

    private boolean isServices;

    private Runnable redirectToDashboardAndConfirmationMessageRunnable;

    private OfferRowInterface offerRow;

    OffersService offersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beo_activation_overlay_activity);

        getDataFromIntent();
        showLoadingDialog();
        addEBUActivationOverlay();
    }

    private void getDataFromIntent(){
        offersService = new OffersService(this);
        isServices = getIntent().getBooleanExtra(IS_SERVICE_KEY, false);
        offerRow = getOfferRowFromRealm(getIntent().getLongExtra(OfferRowInterface.KEY_ID, 0));
    }


    private OfferRowInterface getOfferRowFromRealm(Long offerId) {
        if(offerId == null){
            return null;
        }
        return (PostpaidOfferRow) RealmManager.getRealmObjectAfterLongField(
                PostpaidOfferRow.class, PostpaidOfferRow.OFFER_ID, offerId);
    }

    public void addEBUActivationOverlay(){
        addFragment(BeoEBUActivationConfirmationOverlay.create(offerRow));
    }

    public void addFragment(Fragment fragment) {
        stopLoadingDialog();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null
                && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()){
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.beo_activation_prepaid_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
    }

    public void checkEtf(String sid, String promotionId, String excludedPromos){
        offersService.checkEtf(sid, promotionId, excludedPromos)
                .subscribe(new RequestSessionObserver<GeneralResponse<EbuOfferEligibilitySuccess>>() {

            @Override
            public void onNext(GeneralResponse<EbuOfferEligibilitySuccess> ebuOfferEligibilitySuccessGeneralResponse) {
                if(ebuOfferEligibilitySuccessGeneralResponse.getTransactionStatus() == 0){
                    if(ebuOfferEligibilitySuccessGeneralResponse.getTransactionSuccess().getTotalCostAmount() != null
                            && ebuOfferEligibilitySuccessGeneralResponse.getTransactionSuccess().getTotalCostAmount() > 0){
                        addFragment(EbuETFOverlay.create(offerRow, ebuOfferEligibilitySuccessGeneralResponse.getTransactionSuccess().getTotalCostAmount()));
                    }else{
                        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
                        if(entityChildItem != null){

                            activateOffer(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                                    UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                                    String.valueOf(offerRow.getOfferId()), entityChildItem.getCrmRole(),
                                    StringEscapeUtils.escapeJava(offerRow.getOfferName()), entityChildItem.getVfOdsCid(),
                                    UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen(),
                                    UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
                        }
                    }
                }else{
                    new CustomToast.Builder(VodafoneController.currentActivity())
                            .message(CallDetailsLabels.getCall_details_system_error_text())
                            .success(false)
                            .show();
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(VodafoneController.currentActivity())
                        .message(CallDetailsLabels.getCall_details_system_error_text())
                        .success(false)
                        .show();
                finish();
            }
        });
    }

    public void activateOffer(String msisdn, String sid, String promotionId, String crmRole, String offerName, String vfOdsCid, String vfOdsBen, String vfOdsBan){
        showLoadingDialog();
        Subscription subscription = offersService.activateOfferEbu(msisdn, sid, promotionId, crmRole, offerName, vfOdsCid, vfOdsBen, vfOdsBan)
                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse activationEligibilitySuccessGeneralResponse) {

                        if(VodafoneController.getInstance().getUser() instanceof SubUserMigrated){
                            manageActivateOfferResponseForSubUser(activationEligibilitySuccessGeneralResponse);
                        }else{
                            manageActivateOfferResponseForNonSubUser(activationEligibilitySuccessGeneralResponse);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(VodafoneController.getInstance().getUser() instanceof SubUserMigrated){
                            manageActivateOfferResponseForSubUser(null);
                        }else{
                            manageActivateOfferResponseForNonSubUser(null);
                        }
                        stopLoadingDialog();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        stopLoadingDialog();
                    }
                });

        initRunnableIfIsNotSubUser(subscription);
    }

    private void manageActivateOfferResponseForSubUser(GeneralResponse activationEligibilitySuccessGeneralResponse){
        if(activationEligibilitySuccessGeneralResponse != null
                && activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0){
			logGoogleAnalytics();

            addingEoActivationVovToController(BEOLabels.getSubUser_deactivate_offer_success_vov_message());

            new CustomToast.Builder(VodafoneController.currentActivity())
                    .message(BEOLabels.getOffer_activation_success_toast_message())
                    .success(true)
                    .show();

            new NavigationAction(VodafoneController.currentActivity())
                    .startAction(IntentActionName.DASHBOARD, true);
        }else{
            //Failed request, Display error message as toast in option Detailed Page (Close Overlay)
            new CustomToast.Builder(VodafoneController.currentActivity())
                    .message(BEOLabels.getGeneral_error_mesage())
                    .success(false)
                    .show();

            finish();
        }
    }

    private void manageActivateOfferResponseForNonSubUser(GeneralResponse activationEligibilitySuccessGeneralResponse){
        unsubscribeSubscriptionFromController();
        VodafoneController.getInstance().handler.removeCallbacks(redirectToDashboardAndConfirmationMessageRunnable);

        if(getApiWaitingTime() < WAITING_TIME){

            if(activationEligibilitySuccessGeneralResponse != null &&
                    activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0){
				logGoogleAnalytics();

				addingEoActivationVovToController(isServices
                        ?String.format(BEOLabels.getActivate_service_success_vov_message(), offerRow.getOfferName())
                        :String.format(BEOLabels.getActivate_option_success_vov_message(), offerRow.getOfferName()));

                new CustomToast.Builder(VodafoneController.currentActivity())
                        .message(BEOLabels.getOffer_activation_success_toast_message())
                        .success(true)
                        .show();

                new NavigationAction(VodafoneController.currentActivity())
                        .startAction(IntentActionName.DASHBOARD, true);
            }else{
                new CustomToast.Builder(VodafoneController.currentActivity())
                        .message(BEOLabels.getGeneral_error_mesage())
                        .success(false)
                        .show();
                finish();
            }
        }else{
            if(activationEligibilitySuccessGeneralResponse != null
                    && activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0){
				logGoogleAnalytics();

				addingEoActivationVovToController(isServices
                        ?String.format(BEOLabels.getActivate_service_success_vov_message_delayed(), offerRow.getOfferName())
                        :String.format(BEOLabels.getActivate_option_success_vov_message_delayed(), offerRow.getOfferName()));
            }else{
                addingEoActivationVovToController(isServices
                        ?String.format(BEOLabels.getService_activation_failed_vov_message(), offerRow.getOfferName())
                        :String.format(BEOLabels.getOption_activation_failed_vov_message(), offerRow.getOfferName()));
            }
        }
    }

    private void initRunnableIfIsNotSubUser(final Subscription subscription){
        if(!(VodafoneController.getInstance().getUser() instanceof SubUserMigrated)){
            startTime = System.currentTimeMillis();
            VodafoneController.getInstance()
                    .handler
                    .postDelayed(getDelayedRunnable(subscription), WAITING_TIME);
        }
    }

    private Runnable getDelayedRunnable(final Subscription subscription){
        redirectToDashboardAndConfirmationMessageRunnable = new Runnable() {
            @Override
            public void run() {
                VodafoneController.getInstance().setSubscription(subscription);

                addingEoActivationVovToController(BEOLabels.getSend_request_confirmation_vov_message());

                new CustomToast.Builder(VodafoneController.currentActivity())
                        .message(BEOLabels.getSend_request_confirmation_vov_message())
                        .success(true)
                        .show();

                new NavigationAction(VodafoneController.currentActivity())
                        .startAction(IntentActionName.DASHBOARD, true);
            }
        };

        return redirectToDashboardAndConfirmationMessageRunnable;
    }

    private Long getApiWaitingTime(){
        return System.currentTimeMillis() - startTime;
    }

    private void unsubscribeSubscriptionFromController(){
        if(VodafoneController.getInstance().getSubscription() != null){
            VodafoneController.getInstance().getSubscription().unsubscribe();
            VodafoneController.getInstance().setSubscription(null);
        }
    }

    private void addingEoActivationVovToController(String vovMessage){
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(19, 20, VoiceOfVodafoneCategory.EO_Activation,
                null, vovMessage, BEOLabels.getDeactivate_offer_vov_button_message(), "",
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
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
}
