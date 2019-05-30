package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.app.Dialog;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEbuRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

/**
 * Created by Eliza Deaconescu on 4/11/2018.
 */

public class TravellingAdministrationPresenter implements  TravellingAbroadContract.AdministrationPresenter, TravellingAbroadContract.OnAdministrationListener{

    public static final String TAG = "TravellingAdministrationPresenter";

    TravellingAbroadInteractorImpl mTravellingAbroadInteractor;
    TravellingServiceAdministration mView;

    private boolean isPrepaid;
    boolean isRoamingOfferActiveFromList = false;
    private String msisdn;
    private String sid;
    Boolean isRoaming;
    Boolean isInternational;
    Boolean isEbuRoaming;
    Boolean isEbuInternational;
    AccessTypeSuccess accessTypeSuccess;
    AccessTypeRequest accessTypeRequest;
    AccessTypeEbuRequest accessTypeRequestEbu;
    Dialog overlayDialog;
    private long lastClickTime = 0;
    String alias = null;
    AccessTypeEBU accessTypeSuccessEBU;
    Profile profile;

    private int API_DELAY_TIME = 5000;
    private Subscription subscription;
    private Runnable redirectToDashboardRunnable;
    private long startTime;

    private Boolean noRoamingOffers = false;
    private Boolean hasRoamingCardsOptions = false;

    public TravellingAdministrationPresenter(TravellingAbroadContract.AdministrationView view){
        this.mTravellingAbroadInteractor = new TravellingAbroadInteractorImpl(this);
        this.mView = (TravellingServiceAdministration) view;

        msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        sid = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();

        isPrepaid = mTravellingAbroadInteractor.isPrepaid();
    }

    Profile profile() {
        return (Profile) RealmManager.getRealmObject(Profile.class);
    }

    public void activationConfirmationOverlay(final String serviceType) {

        String activationType;

        if (serviceType.equals("Roaming")) {

			TravellingAbroadStatusOverlayTrackingEvent event = new TravellingAbroadStatusOverlayTrackingEvent();
			VodafoneController.getInstance().getTrackingService().track(event);

            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                activationType = hasInternationalStatus(isEbuRoaming);
            } else {
                activationType = hasNoInternationalStatus(isRoaming);
            }
        }
        else {
            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                activationType = hasInternationalStatus(isEbuInternational);
            } else {
                activationType = hasInternationalStatus(isInternational);
            }
        }

        if (overlayDialog == null) {
            overlayDialog = new Dialog(mView.getActivityInAdministration(), android.R.style.Theme_Black_NoTitleBar);
            overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        }

        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle)).setText(TravellingAboardLabels.getOverlayAreYouSureTitle());
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext)).setVisibility(android.view.View.GONE);
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext2)).setVisibility(android.view.View.VISIBLE);
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext2)).setText(String.format(TravellingAboardLabels.getTravelling_aboard_activation_overlay_subtext(), activationType, serviceType));
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext2)).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        Button dismissButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(TravellingAboardLabels.getTravelling_aboard_cancel_button_label());
        final Dialog finalOverlayDialog = overlayDialog;
        Map<String, Object> tealiumMapViewOverlay = new HashMap(6);
        tealiumMapViewOverlay.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusOverlay);
        tealiumMapViewOverlay.put(TealiumConstants.journey_name, TealiumConstants.roaming);
        tealiumMapViewOverlay.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapViewOverlay);

        dismissButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                finalOverlayDialog.dismiss();
            }
        });

        Button okButton = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        okButton.setText(TravellingAboardLabels.getTravelling_aboard_confirmation_button_label());
        okButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {


                lastClickTime = SystemClock.elapsedRealtime();

                //Tealium Track Event
/*
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusOverlayButtonConfirmare);
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent(TravellingAdministrationPresenter.TAG, tealiumMapEvent);
*/

                if(mTravellingAbroadInteractor.isEbu()){
                    accessTypeSuccessEBU = (AccessTypeEBU) RealmManager.getRealmObject(AccessTypeEBU.class);
                    accessTypeRequestEbu = new AccessTypeEbuRequest();

                    accessTypeRequestEbu.setIsRoaming(isEbuRoaming);
                    accessTypeRequestEbu.setIsInternational(isEbuInternational);
                    accessTypeRequestEbu.setOperation(setOperation(isEbuRoaming, isEbuInternational, serviceType));
                    accessTypeRequestEbu.setPhoneNumber(msisdn);
                    accessTypeRequestEbu.setVfOdsSid(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid());
                    accessTypeRequestEbu.setVfOdsBan(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
                    accessTypeRequestEbu.setVfOdsBen(UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen());
                    accessTypeRequestEbu.setVfOdsCid(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid());
                    accessTypeRequestEbu.setProductId(accessTypeSuccessEBU.getProductId());
                    accessTypeRequestEbu.setProductSpecName(accessTypeSuccessEBU.getProductSpecName());
                    if (serviceType.equals("Roaming")){
                        activateAccessTypeEbu(accessTypeRequestEbu, isEbuRoaming, serviceType);
                    } else if (serviceType.equals(TravellingAboardLabels.getTravelling_aboard_international_tittle())) {
                        activateAccessTypeEbu(accessTypeRequestEbu, isEbuInternational, serviceType);
                    }
                } else if (isPrepaid) {
                    activateOrInactivateRoaming(isRoaming);
                } else {
                    accessTypeRequest.setIsRoaming(isRoaming);
                    accessTypeRequest.setIsInternational(isInternational);
                    accessTypeRequest.setOperation(setOperation(isRoaming, isInternational, serviceType));
                    if (serviceType.equals("Roaming"))
                        activateAccessPostPaid(msisdn, sid, accessTypeRequest, isRoaming, serviceType);
                    else if (serviceType.equals(TravellingAboardLabels.getTravelling_aboard_international_tittle()))
                        activateAccessPostPaid(msisdn, sid, accessTypeRequest, isInternational, serviceType);
                }
                overlayDialog.dismiss();
                mView.showLoadingDialog();
            }
        });

        ImageView closeButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        final Dialog finalOverlayDialog1 = overlayDialog;
        closeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finalOverlayDialog1.dismiss();
            }
        });

        if (!mView.getActivityInAdministration().isFinishing()) {
            overlayDialog.show();
        }

    }

    public String hasNoInternationalStatus(Boolean isRoaming) {
        String activationType;
        if (!isRoaming) {
            activationType = "activezi";
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusButtonActivate);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
        } else {
            activationType = "dezactivezi";
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusButtonDeactivate);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
        }
        return activationType;
    }

    public String getInternationalDescription(boolean isInternationalActive){
        String status = isInternationalActive ? "activ" : "inactiv";
        String internationalStatusDescription =TravellingAboardLabels.getTravelling_aboard_inernational_status_tittle_part1() + " <b>" + status + "</b> "
                + TravellingAboardLabels.getTravelling_aboard_inernational_status_tittle_part2();

        return internationalStatusDescription;
    }

    public String hasInternationalStatus(Boolean isInternational) {
        String activationType;

        if (!isInternational) {
            activationType = "activezi";
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusInternationalButtonActivate);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
        } else {
            activationType = "dezactivezi";
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusInternationalButtonDeactivate);
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
        }
        return activationType;
    }

    public String setOperation(final boolean isRoaming, final boolean isInternational, final String serviceType) {
        String operation = null;

        if (serviceType.equals("Roaming") && isRoaming == true)
            operation = "deactivateRoaming";
        else if (serviceType.equals("Roaming") && isRoaming == false)
            operation = "activateRoaming";
        else if (serviceType.equals(TravellingAboardLabels.getTravelling_aboard_international_tittle()) && isInternational == true)
            operation = "deactivateInternational";
        else if (serviceType.equals(TravellingAboardLabels.getTravelling_aboard_international_tittle()) && isInternational != true)
            operation = "activateInternational";

        return operation;

    }

    //API 38 - /api-gateway/ooffers/eligibility/ebu/{sid}
    public void checkPending(String sid, String type, boolean doNotDisplayLoading) {
        OffersService offersService = new OffersService(mView.getActivityInAdministration());
        Subscription subscription = offersService.getPendingOffers4PostPaid(sid, type).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse response) {
                if (response.getTransactionStatus() != 0) {
                    String errorCodoe = response.getTransactionFault().getFaultCode();
                    if (errorCodoe != null && errorCodoe.equals(ErrorCodes.API38_USER_HAS_OFFER_IN_PENDING.getErrorCode())) {
                        mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_roaming_pending(), "Roaming", false,true);
                        mView.setupInternationalCard(isInternational, true);
                        showBubbleChat();
                    } else {
                        mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming",false, true);
                        mView.setupInternationalCard(isInternational, true);
                    }
                } else {
                    mView.setupRoamingCardForUsersNotPrepaid(isRoaming,false, isPrepaid, alias);
                    mView.setupInternationalCard(isInternational, false);
                }
                mView.administrationRoamingCard.hideLoading();
                mView.administrationInternationalCard.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.administrationRoamingCard.hideLoading();
                mView.administrationInternationalCard.hideLoading();
                showBubbleChat();
                mView.setupInternationalCard(false, true);
                mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming",false,true);
            }
        });
        mView.addToActivityCompositeSubcription(subscription);
    }

    private void activateAccessPostPaid(String msisdn, String sid, AccessTypeRequest accessTypeRequest, final boolean isRoaming, final String serviceType) {

        final String sidValidated = PhoneNumberUtils.checkSidFormat(sid);

        new TravellingAboardService(mView.getActivityInAdministration()).putAccessType(msisdn, sidValidated, accessTypeRequest).subscribe(new RequestSessionObserver<GeneralResponse<AccessTypeSuccess>>() {
            @Override
            public void onCompleted() {
                mView.stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.stopLoadingDialog();
                new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
            }

            @Override
            public void onNext(GeneralResponse<AccessTypeSuccess> response) {
                if (response != null && response.getTransactionStatus() == 0) {
                    String activation = TravellingAboardLabels.getTravelling_aboard_confirmation_activation_roaming();
                    String activation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_activate_postpaid_vov();
                    String inActivation = TravellingAboardLabels.getTravelling_aboard_confirmation_inactivate_roaming();
                    String inActivation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_inactivate_postpaid_vov();

                    new NavigationAction(mView.getActivityInAdministration()).startAction(IntentActionName.DASHBOARD, true);

                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Chat, null,
                            "Mulțumim, am preluat cererea de " + (isRoaming ? inActivation_vov : activation_vov) + " a serviciului " + serviceType + "!",
                            "Ok", null,
                            true, false,
                            VoiceOfVodafoneAction.Dismiss, null);
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

                    new CustomToast.Builder(mView.getActivityInAdministration()).message(TravellingAboardLabels.getTravelling_aboard_service() + serviceType +
                            TravellingAboardLabels.getTravelling_aboard_will_be() + (isRoaming ? inActivation : activation)
                            + TravellingAboardLabels.getTravelling_aboard_in_shot_time()).success(true).show();
                } else {
                    new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                }
            }
        });
    }

    public void activateOrInactivateRoaming(final boolean isActivtion) {
        ActivatePrepaidOfferRequest activatePrepaidRequest = new ActivatePrepaidOfferRequest();
        if (isActivtion) {
            activatePrepaidRequest.setOperation("0");
        } else {
            activatePrepaidRequest.setOperation("1");
        }
        String offerCode = AppConfiguration.getRoamingOfferId();

        OffersService offersService = new OffersService(mView.getActivityInAdministration());
        offersService.activateEligibleOffer(offerCode, activatePrepaidRequest).subscribe(new RequestSessionObserver<GeneralResponse<EligibleOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {

                if (eligibleOffersSuccessResponse.getTransactionStatus() == 0) {
                    String activation = TravellingAboardLabels.getTravelling_aboard_confirmation_activation_roaming();
                    String inActivation = TravellingAboardLabels.getTravelling_aboard_confirmation_inactivate_roaming();
                    VoiceOfVodafone vov = new VoiceOfVodafone(21, 10, VoiceOfVodafoneCategory.Roaming, null,
                            "Ai " + (!isActivtion ? activation : inActivation) + " cu success serviciul Roaming!"
                                    + (!isActivtion ? " Te rugăm sa reporneşti telefonul." : "")
                            , "Ok", null,
                            true, false, VoiceOfVodafoneAction.Dismiss, null);
                    VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
                    new NavigationAction(mView.getActivityInAdministration()).startAction(IntentActionName.DASHBOARD, true);

                    new CustomToast.Builder(mView.getActivityInAdministration()).message(TravellingAboardLabels.getTravelling_aboard_roaming_service() +
                            (!isActivtion ? activation : inActivation)).success(true).show();
                } else {
                    new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.stopLoadingDialog();
                new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
            }

            @Override
            public void onCompleted() {
                mView.stopLoadingDialog();
            }
        });

    }

    public void reload(String msisdn, String sid) {
        ((TravelingAboardActivity) mView.getActivityInAdministration()).scrolltoTop();
        if (mTravellingAbroadInteractor.isEbu()) {
            mTravellingAbroadInteractor.getAccesTypeEbu(msisdn,sid, false);
        } else if (!isPrepaid) {
            mTravellingAbroadInteractor.getNationalPricePlan(msisdn, sid,false);
        } else {
            runAdministrationFlow(msisdn, sid);
        }
    }

    public void runAdministrationFlow(String msisdn, String sid) {
        mTravellingAbroadInteractor.setNationalOnlyPPFromRealm();
        if(mTravellingAbroadInteractor.isEbu()){
            runAdministrationFlowEbu(msisdn,sid);
        }else if (isPrepaid) {
            checkActiveRoamingOffers4PrePaid();
        } else {
            runAdministrationFlowPostpaid(sid);
        }
    }

    public void runAdministrationFlowPostpaid(String sid) {
        try {
            accessTypeSuccess = (AccessTypeSuccess) RealmManager.getRealmObject(AccessTypeSuccess.class);
            if (accessTypeSuccess != null) {
                isRoaming = accessTypeSuccess.getIsRoaming();
                isInternational = accessTypeSuccess.getIsInternational();
            }
            if (!mTravellingAbroadInteractor.getNationalOnlyPP())
                checkPending(sid, "roaming", false);
            else {
                mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_administration_pp(), "Roaming", true, false);
                mView.setupInternationalCard(false, false);
                mView.administrationRoamingCard.hideLoading();
                mView.administrationInternationalCard.hideLoading();
                showBubbleChat();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAdministrationFlowEbu(String msisdn, String sid) {
         accessTypeSuccessEBU = (AccessTypeEBU) RealmManager.getRealmObject(AccessTypeEBU.class);

        if(mTravellingAbroadInteractor.isGeonumber()) {
            mView.hideRoamingCard();
        }

        if (accessTypeSuccessEBU != null) {
            isEbuRoaming = accessTypeSuccessEBU.getIsRoaming();
            isEbuInternational = accessTypeSuccessEBU.getIsInternational();

            final String sidValidated = PhoneNumberUtils.checkSidFormat(sid);

            if (isEbuRoaming || mTravellingAbroadInteractor.isGeonumber()) {
                //Api 38
                getPendingOffers4EBU(sidValidated);
                setHasRoamingCardsOptions(false);
            } else if(!isEbuRoaming){
                //Api 40
                getEligibleOffers4EBU(msisdn, sid);
            }
        } else {
            mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming",false,false);
            mView.setupInternationalCard(false, true);
            mView.administrationRoamingCard.hideLoading();
            mView.administrationInternationalCard.hideLoading();
        }
    }

    private void getProfileFromApi10() {
        new UserDataService(VodafoneController.getInstance()).getUserProfile(true).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
            @Override
            public void onNext(GeneralResponse<Profile> response) {
                if (response.getTransactionSuccess() != null)
                    super.onNext(response);
                profile = response.getTransactionSuccess();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.showErrorCard();
            }
        });
    }

    public void runAdministrationFlowPrepaid(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly) {
        isRoaming = roamingStatus;

        mView.setupRoamingCardPrepaid(roamingStatus, isRoamingOfferActiveFromList, alias, roamingLabel, isNationalOnly);

    }

    public void checkActiveRoamingOffers4PrePaid() {

        OffersService offersService = new OffersService(VodafoneController.getInstance());
        String sid = VodafoneController.getInstance().getUserProfile().getSid();
        profile  = (Profile) RealmManager.getRealmObject(Profile.class);

        if(profile == null)
            getProfileFromApi10();

        final String sidValidated = PhoneNumberUtils.checkSidFormat(sid);

        mView.showLoadingDialog();
        offersService.getEligibleActiveOffers4PrePaid(sidValidated).subscribe(new RequestSaveRealmObserver<GeneralResponse<ActiveOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
                super.onNext(activeOffersSuccessResponse);
                List<String> roamingOffersIds = AppConfiguration.getRoamingOfferIdList();

                if (activeOffersSuccessResponse.getTransactionSuccess() != null && activeOffersSuccessResponse.getTransactionStatus() == 0) {

                        mTravellingAbroadInteractor.getRoamingLabels(profile.getRoaming(),false);
                        if (activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList() != null && activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList().size() > 0) {
                            for (ActiveOffer activeOffer : activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList()) {
                                for (int j=0; j<roamingOffersIds.size(); j++) {
                                    if (roamingOffersIds.get(j).equals(String.valueOf(activeOffer.getOfferId()))) {
                                        isRoamingOfferActiveFromList = true;
                                    }
                                }
                            }
                        }

                }
                else
                {
                    isRoamingOfferActiveFromList = false;
//                runAdministrationFlowPrepaid();
                    mView.stopLoadingDialog();
                    mView.showErrorCard();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isRoamingOfferActiveFromList = false;
//                runAdministrationFlowPrepaid();
                mView.stopLoadingDialog();
                mView.showErrorCard();
            }
        });
    }

    public void initTealium() {
        //Tealium Track AbroadView
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.roaming);
        tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
/*
        //Tealium Track AbroadView
        Map<String, Object> tealiumMapViewOverlay = new HashMap(6);
        tealiumMapViewOverlay.put("screen_name", "roaming status overlay");
        tealiumMapViewOverlay.put("journey_name", "roaming");
        tealiumMapViewOverlay.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapViewOverlay);
*/
        TravellingServiceAdministration.TravellingAbroadStatusTrackingEvent event = new TravellingServiceAdministration.TravellingAbroadStatusTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    public void setUpCardView() {
        android.view.View view = LayoutInflater.from(mView.getActivityInAdministration()).inflate(R.layout.settings_card_arrow, mView.administrationTravellingAboadCardView, false);

        final VodafoneTextView cardTitle = (VodafoneTextView) view.findViewById(R.id.cardTitle);
        final VodafoneTextView cardSubtext = (VodafoneTextView) view.findViewById(R.id.cardSubtext);

        cardTitle.setText(TravellingAboardLabels.getTravelling_aboard_administration_card_tittle());
        cardSubtext.setVisibility(android.view.View.GONE);

        mView.administrationTravellingAboadCardView.addView(view);
        mView.administrationTravellingAboadCardView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.roamingStatusView);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusButtonOptiuniActive);
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent(TravellingAdministrationPresenter.TAG, tealiumMapEvent);

                if (VodafoneController.getInstance().getUser() instanceof PrepaidUser)
                    new NavigationAction(mView.getActivityInAdministration(), IntentActionName.COST_CONTROL).finishCurrent(true).startAction();
                else
                    new NavigationAction(mView.getActivityInAdministration(), IntentActionName.SERVICES_PRODUCTS).finishCurrent(true).startAction();

            }
        });

    }

    public void errorStep() {
        mView.administrationRoamingCard.showLoading(true);
        mView.administrationInternationalCard.showLoading(true);
        new UserDataService(mView.getActivity()).getUserProfile(false).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
            @Override
            public void onNext(GeneralResponse<Profile> response) {
                super.onNext(response);
                mView.administrationRoamingCard.hideLoading();
                mView.administrationInternationalCard.hideLoading();
                mView.mTravellingAdministrationPresenter.checkActiveRoamingOffers4PrePaid();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.administrationRoamingCard.hideLoading();
                mView.administrationInternationalCard.hideLoading();
                mView.mTravellingAdministrationPresenter.checkActiveRoamingOffers4PrePaid();
            }
        });
    }

    public void setUpVariables() {
        accessTypeRequest = new AccessTypeRequest();

        Subscriber subscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();

        if (subscriber != null) {
            if (sid == null) sid = subscriber.getSid();
            if (msisdn == null) msisdn = subscriber.getMsisdn();
        } else {
            sid = VodafoneController.getInstance().getUserProfile().getSid();
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        if (!isPrepaid)
            mView.administrationRoamingCard.showLoading(true);
            mView.administrationInternationalCard.showLoading(true);

        runAdministrationFlow(msisdn,sid);

        setUpCardView();
        initTealium();

        alias = isPrepaid ? profile().getAlias() : UserSelectedMsisdnBanController.getInstance().getSubscriberAlias();

    }

    @Override
    public void onSuccess(boolean isRoamingActive, boolean isInternationalActive) {
        mView.onTypeAccessSuccess(isRoamingActive,isInternationalActive);
    }

    @Override
    public void onFailed() {
         mView.onTypeAccessFailed();
    }

    //API 38 - /api-gateway/offers/eligibility/{sid}
    protected void getPendingOffers4EBU(String sid) {

        String vfCRMRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();

        OffersService offersService = new OffersService(mView.getActivityInAdministration());
        Subscription subscription = offersService.getPendingOffers4EBU(vfCRMRole,sid, "roaming").subscribe(new RequestSessionObserver<GeneralResponse>() {

                    @Override
                    public void onNext(GeneralResponse response) {
                        if (response.getTransactionStatus() == 1) {
                            String errorCode = response.getTransactionFault().getFaultCode();
                            if (errorCode != null && errorCode.equals(ErrorCodes.API38_USER_HAS_OFFER_IN_PENDING.getErrorCode())) {

                                if (!hasRoamingCardsOptions) {

                                    if (mTravellingAbroadInteractor.isGeonumber()) {
                                        mView.showPPForGeonumbers();
                                    } else {
                                        mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_roaming_pending(), "Roaming", false, false);
                                        mView.administrationRoamingCard.setActivationButton(isEbuRoaming);
                                        setHasRoamingCardsOptions(true);
                                    }
                                    mView.administrationRoamingCard.hideLoading();
                                }

                                mView.setupInternationalCard(isEbuInternational, true);
                                mView.administrationInternationalCard.hideLoading();
                                mView.administrationRoamingCard.hideLoading();
                                showBubbleChat();

                            } else {
                                if (!hasRoamingCardsOptions) {
                                    mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false, false);
                                    mView.administrationRoamingCard.hideLoading();
                                    setHasRoamingCardsOptions(true);
                                }
                                mView.setupInternationalCard(isEbuInternational, true);
                                mView.administrationRoamingCard.hideLoading();
                                mView.administrationInternationalCard.hideLoading();
                            }

                        } else if (response.getTransactionStatus() == 0) {

                            if (mTravellingAbroadInteractor.isGeonumber()) {
                                mView.hideRoamingCard();
                                mView.administrationRoamingCard.hideLoading();
                                mView.setupInternationalCard(isEbuInternational, false);
                                mView.administrationInternationalCard.hideLoading();
                            } else if (isEbuRoaming) {
                                if (!hasRoamingCardsOptions) {
                                    mView.setupRoamingCardForUsersNotPrepaid(isEbuRoaming, false, isPrepaid, alias);
                                    mView.administrationRoamingCard.hideLoading();
                                    setHasRoamingCardsOptions(true);
                                }
                                mView.setupInternationalCard(isEbuInternational, false);
                                mView.administrationRoamingCard.hideLoading();
                                mView.administrationInternationalCard.hideLoading();
                            } else if (!isEbuRoaming) {
                                checkEligibilityForEbu();
                            }

                        } else {
                            if (!hasRoamingCardsOptions) {
                                mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false, false);
                                mView.administrationRoamingCard.hideLoading();
                                setHasRoamingCardsOptions(true);
                            }
                            mView.administrationRoamingCard.hideLoading();
                            mView.setupInternationalCard(isEbuInternational, true);
                            mView.administrationInternationalCard.hideLoading();

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        if(mTravellingAbroadInteractor.isGeonumber()){
                            new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                        }else  if(!hasRoamingCardsOptions){
                            mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false,false);
                            mView.administrationRoamingCard.hideLoading();
                            setHasRoamingCardsOptions(true);
                        }

                        mView.setupInternationalCard(isEbuInternational, true);
                        mView.administrationInternationalCard.hideLoading();
                        showBubbleChat();

                    }
                });

        mView.addToActivityCompositeSubcription(subscription);
    }

    //API 40
    protected void getEligibleOffers4EBU(final String msisdn,final String sid) {

        String vfCRMRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();

        new OffersService(VodafoneController.getInstance()).getEligibleOffers4EBU(vfCRMRole, msisdn, "roaming")
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersPostSuccess> response) {
                        super.onNext(response);
                        if (response.getTransactionStatus() == 0) {
                            EligibleOffersPostSuccess eligibleOffersPostSuccess = response.getTransactionSuccess();
                            if(eligibleOffersPostSuccess != null) {
                                RealmList<EligibleCategoriesPost> eligibleCatagories = eligibleOffersPostSuccess.getEligibleOptionsCategories();

                                noRoamingOffers = false;
                                    for (int i = 0; i < eligibleCatagories.size(); i++) {
                                        if (eligibleCatagories.get(i).getCategory().equals("Roaming")) {
                                            getPendingOffers4EBU(sid);
                                            setHasRoamingCardsOptions(false);
                                            noRoamingOffers = true;
                                            return;
                                        }
                                }

                                if(!noRoamingOffers) {
                                    mView.stopLoadingDialog();
                                    mView.administrationRoamingCard.hideLoading();
                                    if(!mTravellingAbroadInteractor.isGeonumber()) {
                                        mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_administration_pp(), "Roaming", true, false);
                                        getPendingOffers4EBU(sid);
                                        setHasRoamingCardsOptions(true);
                                    }else {
                                        mView.setupInternationalCard(isEbuInternational, false);
                                        mView.administrationInternationalCard.hideLoading();
                                        showBubbleChat();
                                    }
                                }
                            }
                        }else if(response.getTransactionStatus() == 1){
                                mView.stopLoadingDialog();
                                mView.administrationRoamingCard.hideLoading();
                                mView.administrationInternationalCard.hideLoading();
                                if(!mTravellingAbroadInteractor.isGeonumber()) {
                                    mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_administration_pp(), "Roaming", true, false);
                                    getPendingOffers4EBU(sid);
                                    setHasRoamingCardsOptions(true);
                                }else {
                                    mView.administrationInternationalCard.hideLoading();
                                    mView.setupInternationalCard(isEbuInternational, false);

                                    showBubbleChat();
                                }
//                                showBubbleChat();
                        } else  {
                            mView.stopLoadingDialog();
                            mView.administrationRoamingCard.hideLoading();
                            new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                            getPendingOffers4EBU(sid);
                            if(!mTravellingAbroadInteractor.isGeonumber()) {
                                setHasRoamingCardsOptions(true);
                                mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false, false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        mView.administrationRoamingCard.hideLoading();
                        mView.showRoamingCardWithError(CallDetailsLabels.getCall_details_system_error_text(),"Roaming",false,false);
                        new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                        getPendingOffers4EBU(sid);
                        setHasRoamingCardsOptions(true);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    //API 62 @GET("/api-gateway/offers/roaming/ebu/eligibility") -- credit Vetting
    public void checkEligibilityForEbu(){
        new TravellingAboardService(VodafoneController.getInstance()).checkEligibilityForEbu(
                EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid(),
                UserSelectedMsisdnBanController.getInstance().getSubscriberSid())
                .subscribe(new RequestSaveRealmObserver<GeneralResponse>(){
                    @Override
                    public void onError(Throwable e){
                        super.onError(e);

                        if(!hasRoamingCardsOptions) {
                            mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming",false,false);
                            mView.administrationRoamingCard.hideLoading();
                            setHasRoamingCardsOptions(true);
                        }
                            mView.setupInternationalCard(isEbuInternational, true);
                            mView.administrationInternationalCard.hideLoading();
                            showBubbleChat();
                    }

                    @Override
                    public void onNext(GeneralResponse  response) {
                        super.onNext(response);
                        if (response != null && response.getTransactionStatus() == 0) {
                            if(!hasRoamingCardsOptions) {
                                mView.setupRoamingCardForPPYDepositNotRequired(isEbuRoaming, alias);
                                mView.administrationRoamingCard.hideLoading();
                                setHasRoamingCardsOptions(true);
                            }
                            mView.administrationInternationalCard.hideLoading();
                            mView.setupInternationalCard(isEbuInternational, false);

                        } else if(response.getTransactionStatus() == 2) {

                            String errorCode = response.getTransactionFault().getFaultCode();
                            if (errorCode.equals("EC06203") ) {
                                if(!hasRoamingCardsOptions) {
                                    mView.showRoamingCardWithError(TravellingAboardLabels.getTravellingAboardRoamingEbuPPY(), "Roaming", false, false);
                                    mView.administrationRoamingCard.hideLoading();
                                    setHasRoamingCardsOptions(true);
                                }
                                showBubbleChat();
                            }else{

                                if(!hasRoamingCardsOptions) {
                                    mView.showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false, false);
                                    mView.administrationRoamingCard.hideLoading();
                                    setHasRoamingCardsOptions(true);
                                }
                            }

                            mView.setupInternationalCard(isEbuInternational, false);
                            mView.administrationInternationalCard.hideLoading();

                        }
                    }

                });
    }


    private void activateAccessTypeEbu(AccessTypeEbuRequest accessTypeEbuRequest, final boolean isEbuRoaming, final String serviceType) {

        String vfCRMRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();

        subscription = new TravellingAboardService(mView.getActivityInAdministration()).putAccessTypeEbu(vfCRMRole, accessTypeEbuRequest).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onCompleted() {
                removeSubscriptionAndCallBacks();
                if(mView != null) {
                    mView.stopLoadingDialog();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                String activation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_activation_roaming_vov();
                String inActivation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_inactivate_roaming_vov();

                if (isSubUser()) {
                    new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                    mView.stopLoadingDialog();
                } else {
                    if (getApiWaitingTime() < API_DELAY_TIME) {
                        new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                        mView.stopLoadingDialog();
                    } else {
                        redirectToDashboard(("Serviciul de " + serviceType.toLowerCase() + " nu a fost " +
                                (isEbuRoaming ? inActivation_vov : activation_vov) + ". Va rugam reincercati."), null);
                    }

                    removeSubscriptionAndCallBacks();
                }
            }

            @Override
            public void onNext(GeneralResponse response) {

                String activation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_activation_roaming_vov();
                String inActivation_vov = TravellingAboardLabels.getTravelling_aboard_confirmation_inactivate_roaming_vov();
                String forSubUser = TravellingAboardLabels.getTravellingAboardRoamingSubuser();
                String forEbu = TravellingAboardLabels.getTravellingAboardRoamingEbu();
                String retry = TravellingAboardLabels.getTravellingAbroadRetry();

                if (response != null && response.getTransactionStatus() == 0) {

                    if(isSubUser()) {
                        redirectToDashboard(forSubUser,
                                TravellingAboardLabels.getTravellingAboardRoamingToast());
                    }else{
                        if(getApiWaitingTime() < API_DELAY_TIME) {
                                String vovMessage = forEbu + " " + serviceType.toLowerCase() + " va fi " +
                                        (isEbuRoaming ? inActivation_vov : activation_vov) + "!";
                                redirectToDashboard(vovMessage,
                                        TravellingAboardLabels.getTravellingAboardRoamingToast());
                        } else {
                            String vovMessage = forEbu + " " + serviceType.toLowerCase() + " va fi " +
                                    (isEbuRoaming ? inActivation_vov : activation_vov) + "!";
                            redirectToDashboard(vovMessage, null);
                        }
                    }
                } else {
                    if (isSubUser()) {
                        new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                        mView.stopLoadingDialog();
                    }else {
                        if (getApiWaitingTime() < API_DELAY_TIME) {
                            mView.stopLoadingDialog();
                            new CustomToast.Builder(mView.getActivityInAdministration()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                        } else {
                            redirectToDashboard(("Serviciul de " + serviceType.toLowerCase() + " nu a fost " +
                                    (isEbuRoaming ? inActivation_vov : activation_vov) + "." + retry), null);
                        }
                    }
                }
            }
        });
        mView.stopLoadingDialog();
        if (!isSubUser()) {
            initRunnableWithDelay(subscription);
        }
    }

    public Boolean isSubUser(){

        if(VodafoneController.getInstance().getUser() instanceof SubUserMigrated) {
            return true;
        }else{
            return false;
        }
    }

    private Long getApiWaitingTime(){
        return System.currentTimeMillis() - startTime;
    }

    private void initRunnableWithDelay(final Subscription subscription){
        startTime = System.currentTimeMillis();
        VodafoneController.getInstance()
                .handler
                .postDelayed(getDelayedRunnable(subscription), API_DELAY_TIME);
    }

    private Runnable getDelayedRunnable(final Subscription subscription){
        redirectToDashboardRunnable = new Runnable() {
            @Override
            public void run() {
                VodafoneController.getInstance().addSubscription(subscription);

                redirectToDashboard(TravellingAboardLabels.getTravellingAbroadDelayVov(),
                        TravellingAboardLabels.getTravellingAbroadDelayToast());

            }
        };
        return redirectToDashboardRunnable;
    }

    private void unSubscribeSubscriptionFromController(Subscription subscription){
        if(VodafoneController.getInstance().getCompositeSubscription() != null){
            VodafoneController.getInstance().removeSubscription(subscription);
        }
    }

    private void removeSubscriptionAndCallBacks() {
        unSubscribeSubscriptionFromController(subscription);
        VodafoneController.getInstance().handler.removeCallbacks(redirectToDashboardRunnable);
    }

    public void redirectToDashboard(String vovMessage, String toastMessage) {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Roaming,
                null, vovMessage, TravellingAboardLabels.getTravellingAboardRoamingButtonVov(), null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

        if(toastMessage != null) {
            new CustomToast.Builder(mView.getActivityInAdministration()).message(toastMessage).success(true).show();
        }

        new NavigationAction(mView.getActivityInAdministration()).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
    }


    public void showBubbleChat() {

        if (mView.getActivityInAdministration() == null)
            return;
        if (mView.getActivityInAdministration() instanceof ChatBubbleActivity)
            ((ChatBubbleActivity) mView.getActivityInAdministration()).getChatBubble().displayBubble(true);

    }

    public void setHasRoamingCardsOptions(Boolean value){
        hasRoamingCardsOptions = value;
    }

    public static class TravellingAbroadStatusOverlayTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "roaming status overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "roaming status overlay");
            s.channel = "roaming";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "roaming";
            s.getContextData().put("prop21", s.prop21);

			s.prop22 = "mcare:" + "roaming status";
			s.getContextData().put("prop22", s.prop22);

			s.prop23 = "mcare:" + "roaming status overlay";
			s.getContextData().put("prop23", s.prop23);
        }
    }

    @Override
    public void setRoaming(Boolean roamingStatus, String roamingLabel, Boolean isNationalOnly){
        mView.stopLoadingDialog();
        runAdministrationFlowPrepaid(roamingStatus,roamingLabel,isNationalOnly);
    }

    @Override
    public void showErrorCard() {
        mView.stopLoadingDialog();
        mView.showErrorCard();
    }

}
