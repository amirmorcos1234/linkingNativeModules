package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.settings.SimDetailsCard;
import ro.vodafone.mcare.android.card.settings.SimReplacementCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.simDetails.SIMDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.simReplace.SIMReplaceSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.SimReplaceBean;
import ro.vodafone.mcare.android.service.SimDetailsService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by user2 on 4/14/2017.
 */

public class SimDetailsFragment extends SettingsFragment {
    public static String TAG = SimDetailsFragment.class.getSimpleName();
    private NavigationHeader navigationHeader;
    private SimDetailsService simDetailsService;
    private Subscription subscriptionReplace;
    private boolean simDisplay;
    private String puk;
    private String sim;
    private ViewGroup rootView;
    private Dialog overlayDialog;
    private SimDetailsCard detailsCard;
    private SimReplacementCard replacementCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSettingsFragment = false;
        configureHeaderForUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings_sim_details, container, false);
        simDetailsService = new SimDetailsService(getContext());
        detailsCard = new SimDetailsCard(getContext());
        replacementCard = new SimReplacementCard(getContext()).setupForSimReplacement()
                .setConfirmationButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayConfirmationDialog();
                    }
                });
        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
            getSimDetailsForEbu(true);
        else
            getSimDetails(true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initTealium();
        if (!AppConfiguration.getShowSimReplacement())
            replacementCard.setVisibility(View.GONE);
    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_SIM_DETAILS);
    }

    private void initTealium() {
        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "sim details");
        tealiumMapView.put("journey_name", "settings");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        SettingsSimDetailsTrackingEvent event = new SettingsSimDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                rootView.removeView(replacementCard);
                rootView.removeView(detailsCard);
                if (replacementCard != null)
                    replacementCard.reload();
                if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
                    getSimDetailsForEbu(true);
                else
                    getSimDetails(true);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootView.removeAllViews();
        if (navigationHeader != null)
            navigationHeader.hideSelectorViewWithoutTriangle();
        if (subscriptionReplace != null)
            subscriptionReplace.unsubscribe();
        if (overlayDialog != null && overlayDialog.isShowing())
            overlayDialog.dismiss();
        ((SettingsActivity) getActivity()).setTitle();
    }

    public String getTitle() {
        return ((String) SettingsLabels.getSettingsSimDetailsTitle());
    }

    public void configureHeaderForUser() {
        navigationHeader = ((SettingsActivity) getActivity()).getNavigationHeader();
        navigationHeader.showMsisdnSelector();
        navigationHeader.displayDefaultHeader();
    }

    public void inflateErrorLayout() {
        detailsCard.showError(true, SettingsLabels.getSmallErrorMessage());
        detailsCard.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
                    getSimDetailsForEbu(false);
                else
                    getSimDetails(false);
            }
        });
        if (!detailsCard.isAttachedToWindow())
            rootView.addView(detailsCard);
        if (!replacementCard.isAttachedToWindow())
            rootView.addView(replacementCard);
    }

    public void inflateSimDetailsLayout() {
        detailsCard.hideError();
        if (simDisplay && sim != null && !sim.equals("")) {
            detailsCard.setTitle(SettingsLabels.getPukTitle())
                    .setDetails(puk);
            detailsCard.addExtraTextGroup(SettingsLabels.getSimSeriesTitle(), sim);
        } else {
            detailsCard
                    .setTitle(SettingsLabels.getPukTitle())
                    .setDetails(puk)
                    .removeExtraTextGroupIfDisplayed();
        }

        if (!detailsCard.isAttachedToWindow())
            rootView.addView(detailsCard);
        if (!replacementCard.isAttachedToWindow())
            rootView.addView(replacementCard);
    }

    public void showLoading() {
        detailsCard.showLoading(true);
        if (!detailsCard.isAttachedToWindow())
            rootView.addView(detailsCard);
        if (!replacementCard.isAttachedToWindow())
            rootView.addView(replacementCard);
    }

    public void getSimDetails(final boolean showFullScreenLoading) {
        if (showFullScreenLoading)
            showLoadingDialog();
        else
            showLoading();

        String userSid;
        if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null) {
            userSid = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();
        } else {
            userSid = VodafoneController.getInstance().getUserProfile().getSid();
        }
        simDetailsService.getSimDetails(userSid).subscribe(simDetailsobservable);
    }


    private void getSimDetailsForEbu(final boolean showFullScreenLoading) {
        if (showFullScreenLoading)
            showLoadingDialog();
        else
            showLoading();
        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String subscriberSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        simDetailsService.getEbuSimDetails(selectedMsisdn, subscriberSid).subscribe(simDetailsobservable);
    }

    private void displayConfirmationDialog() {
        if (getContext() == null)
            return;
        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle)).setText(SettingsLabels.getReplacementConfirmationTitle());
        ((VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext)).setText(SettingsLabels.getReplacementConfirmationSubTitle());
        overlayDialog.findViewById(R.id.overlaySubtext2).setVisibility(View.GONE);
        VodafoneButton buttonOn = overlayDialog.findViewById(R.id.buttonKeepOn);
        VodafoneButton buttonOff = overlayDialog.findViewById(R.id.buttonTurnOff);

        buttonOn.setText(getString(R.string.replacement_confirmation_on_button));
        buttonOff.setText(getString(R.string.replacement_confirmation_off_button));

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceSim();
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayDialog.findViewById(R.id.overlayDismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayDialog.show();
    }

    private void replaceSim() {
        if (subscriptionReplace != null)
            subscriptionReplace.unsubscribe();

        if (getContext() == null)
            return;

        showLoadingDialog();

        RequestSessionObserver<GeneralResponse<SIMReplaceSuccess>> sessionObserver
                = new RequestSessionObserver<GeneralResponse<SIMReplaceSuccess>>() {

            @Override
            public void onNext(GeneralResponse<SIMReplaceSuccess> generalResponse) {
                boolean responseIsSuccesful = generalResponse != null
                        && generalResponse.getTransactionStatus() == 0
                        && generalResponse.getTransactionSuccess() != null
                        && generalResponse.getTransactionSuccess().isSuccess();

                if (responseIsSuccesful) {
                    new CustomToast.Builder(getContext()).message(SettingsLabels.getReplacementToastConfirmation()).success(true).show();
                    new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);
                    //TODO code cleanup - replace String with labels
                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Sim_Replace, null,
                            SettingsLabels.getReplacementVovConfirmation(), "Ok, am înțeles", null,
                            true, false, VoiceOfVodafoneAction.Dismiss, null);
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                    return;
                }

                new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
                stopLoadingDialog();
            }

        };

        if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
            subscriptionReplace = simReplaceForCBU().subscribe(sessionObserver);
            return;
        }

        subscriptionReplace = simReplaceForEBU().subscribe(sessionObserver);
    }

    private Observable<GeneralResponse<SIMReplaceSuccess>> simReplaceForCBU() {
        Observable<GeneralResponse<SIMReplaceSuccess>> observable;
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();

        Realm realm = Realm.getDefaultInstance();
        Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);
        realm.close();

        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String vfEBUMigrated = userProfile != null ? String.valueOf(userProfile.isMigrated()) : null;
        String vfContactID = userProfile != null ? userProfile.getContactId() : null;
        String seriaSim = replacementCard != null ? replacementCard.getSimValue() : null;
        String contactNumber = replacementCard != null ? replacementCard.getPhoneNumberValue() : null;
        String banID = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String customerSegment = profile != null ? profile.getCustomerSegment() : null;
        String vfCid = userProfile != null ? userProfile.getCid() : null;
        String vfSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid()
                : (userProfile != null ? userProfile.getSid() : null);

        SimReplaceBean simReplaceBean= new SimReplaceBean(seriaSim, contactNumber, banID, null, customerSegment, null, null, null);
        observable = new SimDetailsService(getContext()).postReplaceSim(selectedMsisdn, vfEBUMigrated,
                (vfContactID == null || !vfContactID.isEmpty()) ? vfContactID : null, vfSid, vfCid, simReplaceBean);

        return observable;
    }

    private Observable<GeneralResponse<SIMReplaceSuccess>> simReplaceForEBU() {
        Observable<GeneralResponse<SIMReplaceSuccess>> observable;
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String vfEBUMigrated = userProfile != null ? String.valueOf(userProfile.isMigrated()) : null;
        String vfContactID = userProfile != null ? userProfile.getContactId() : null;
        String seriaSim = replacementCard != null ? replacementCard.getSimValue() : null;
        String contactNumber = replacementCard != null ? replacementCard.getPhoneNumberValue() : null;
        String banID = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String treatmentSegment = entityChildItem != null ? entityChildItem.getTreatmentSegment() : null;
        String crmRole = entityChildItem != null ? entityChildItem.getCrmRole() : null;
        String vfOdsCid = entityChildItem != null ? entityChildItem.getVfOdsCid() : null;
        String vfOdsSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid()
                : (userProfile != null ? userProfile.getSid() : null);

        SimReplaceBean simReplaceBean= new SimReplaceBean(seriaSim, contactNumber, banID, treatmentSegment, null, vfOdsCid, vfOdsSid, crmRole);

        observable = new SimDetailsService(getContext()).postReplaceSim(selectedMsisdn, vfEBUMigrated,
                (vfContactID == null || !vfContactID.isEmpty()) ? vfContactID : null,null,null,simReplaceBean);

        return observable;
    }

    RequestSessionObserver<GeneralResponse<SIMDetailsSuccess>> simDetailsobservable = new RequestSessionObserver<GeneralResponse<SIMDetailsSuccess>>() {
        @Override
        public void onNext(GeneralResponse<SIMDetailsSuccess> simDetailslResponse) {
            stopLoadingDialog();
            detailsCard.hideLoading();
            if (simDetailslResponse.getTransactionSuccess() != null &&
                    simDetailslResponse.getTransactionStatus() == 0) {
                simDisplay = simDetailslResponse.getTransactionSuccess().isDisplaySim();
                puk = simDetailslResponse.getTransactionSuccess().getPuk();
                sim = simDetailslResponse.getTransactionSuccess().getSim();
                if (replacementCard != null)
                    replacementCard.setSim(sim);
                inflateSimDetailsLayout();

            } else {
                onError(new Throwable("Server failed"));
            }

        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            if (replacementCard != null)
                replacementCard.setSim(null);
            stopLoadingDialog();
            detailsCard.hideLoading();
            inflateErrorLayout();
        }
    };



    public static class SettingsSimDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "sim details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "sim details");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
