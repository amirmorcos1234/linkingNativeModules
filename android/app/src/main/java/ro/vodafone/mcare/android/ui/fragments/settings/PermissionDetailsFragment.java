package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import io.realm.RealmResults;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.gdpr.SessionIdMap;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.GdprService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

/**
 * Created by cosmin deliu on 2/8/2018.
 */

public abstract class PermissionDetailsFragment extends SettingsFragment {

    protected static final int NONE = 0;
    protected static final int COMMERCIAL_COMMUNICATIONS_TYPE = 10;
    protected static final int SURVEY_TYPE = 11;
    protected static final int CREATE_PROFILE_TYPE = 12;

    protected static final int SMS_MMS_PUSH = 14;
    protected static final int EMAIL = 15;
    protected static final int POST = 16;
    protected static final int OUTBOUND_CALL = 17;
    protected static final int ADVANCED_PROFILE_NETWORK_DATA_TYPE = 18;
    protected static final int ADVANCED_PROFILE_ONLINE_DATA_TYPE = 19;

    protected static final String SAVE = "save";
    protected static final String ACTIVATE = "activate";
    protected static final String DEACTIVATE = "deactivate";
    protected static final String SAVE_PERMISSIONS_OVERLAY= "save_permissions_overlay";
    protected static final String ACTIVATE_MINOR_OVERLAY= "activate_minor_overlay";

    protected static final int TOAST_ERROR = 23;
    protected static final int LAYOUT_ERROR = 24;

    protected LinearLayout parent;

    //TODO code cleanup - why activity field exists? Please remove it ->
    // TODO getActivity() is more than enough. Please disconnect logic with an interface is you want to keep activity logic as a field.
    protected GdprPermissions gdprPermissions;
    protected GdprPermissions gdprPermissionsChanged;
    protected SettingsActivity settingsActivity;

    protected Subscription subscription;

    protected String card_type;
    protected boolean own_subscription_is_minor;
    protected boolean rescorp_sid_not_found = false;

    protected Dialog overlayDialog;
    protected SwitchButton switchButtonMinor;
    protected LinearLayout editLayoutContainer;
    protected VodafoneTextView minorCardTitle;
    protected RelativeLayout save_button_card;
    protected VodafoneButton save_button;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        settingsActivity = (SettingsActivity) getActivity();

        if (bundle != null)
            card_type = bundle.getString(NavigationAction.EXTRA_PARAMETER_BUNDLE_KEY);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.parent_padding);

        ViewGroup.LayoutParams paramsParent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent = new LinearLayout(getContext());
        parent.setLayoutParams(paramsParent);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, 0);
        parent.setVisibility(View.GONE);

        initTracking(card_type);

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPermissions();
    }

    @Override
    public void makeAdobeRequest() {
        //don't remove or set a Adobe Request
    }

    void initTracking(String type) {
        PermissionsDetailsTrackingEvent event = new PermissionsDetailsTrackingEvent();
        event.setCard_type(type);
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    void trackEvent(String type, TrackingAppMeasurement journey) {
        PermissionsDetailsTrackingEvent event = new PermissionsDetailsTrackingEvent();
        event.setCard_type(type);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    protected void getPermissions() {
        if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
            getPermissionsForRescorpOwner();
            return;
        }
        getPermissionsForOtherTypeOfUsers();
    }

    protected abstract void createCards();

    protected SpannableStringBuilder getMinorAccountTitle() {
        //TODO code cleanup - check getMinorStatus()!=null and title!=null as method title.lenght() is called. Index out exception may be called at sb.setSpan..
        String title = GdprController.checkMinorStatus(gdprPermissionsChanged) ? SettingsLabels.getActiveMinor() : SettingsLabels.getInactiveMinor();
        SpannableStringBuilder sb = new SpannableStringBuilder(SettingsLabels.getMinorAccountCardTitle()+ ": " + title);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - title.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    protected void setExpandableView(View view, String textToShow) {
        final RelativeLayout expandableContainer = view.findViewById(R.id.expandableContainer);
        final ImageView image = view.findViewById(R.id.extendSeeDetails);
        final LinearLayout toBeShown = view.findViewById(R.id.toBeShown);

        ((VodafoneTextView) view.findViewById(R.id.textToShow)).setText(textToShow);

        expandableContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeShown.getVisibility() == View.GONE) {
                    toBeShown.setVisibility(View.VISIBLE);
                    image.setImageResource(R.drawable.right_red_arrow_up);
                } else {
                    toBeShown.setVisibility(View.GONE);
                    image.setImageResource(R.drawable.right_red_arrow_down);
                }
            }
        });
    }

    public void inflateErrorLayout(String message) {
        parent.removeAllViews();
        stopLoadingAfterDuration(1);

        if (getActivity() == null)
            return;

        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());

        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscription != null)
                    subscription.unsubscribe();
                getPermissions();
            }
        });


        errorCard.showError(true, message);

        parent.addView(errorCard);
    }

    private void getPermissionsForRescorpOwner() {
        showLoadingDialog();

        String vfPhoneNumberOwnSubscription = VodafoneController.getInstance().getUserProfile().getMsisdn();
        final String vfSidOwnSubscription = VodafoneController.getInstance().getUserProfile().getSid();

        final String vfPhoneNumberSelectedSubscription = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
        final String vfSidSelectedSubscription = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        final boolean isOwnSubscriptionSelected = GdprController.isOwnSubscriptionSelected();

        final RequestSaveRealmObserver requestSaveRealmObserver = new RequestSaveRealmObserver<GeneralResponse<GdprGetResponse>>() {
            private boolean onNextCalled = false;

            @Override
            public void onNext(GeneralResponse<GdprGetResponse> gdprGetResponseSelectedSubscriptionResponse) {
                super.onNext(null);
                onNextCalled = true;
                manageGetPermissionsGeneral(gdprGetResponseSelectedSubscriptionResponse, isOwnSubscriptionSelected, isOwnSubscriptionSelected ? 1 : 2);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                if (!onNextCalled)
                    onNextNotCalled(isOwnSubscriptionSelected);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
                manageErrorCodes("default", LAYOUT_ERROR);
            }

        };

        if (isOwnSubscriptionSelected) {
            RealmManager.deleteRealmObjectQuery(GdprGetResponse.class, 2);
            RealmManager.deleteAfterKey(GdprGetResponse.class, 2);
            new GdprService(getContext()).getPermissions(vfPhoneNumberOwnSubscription, vfSidOwnSubscription, false, 1)
                    .subscribe(requestSaveRealmObserver);
        } else {

            RealmResults<GdprGetResponse> gdprResponses = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 2);
            GdprGetResponse gdprGetResponseSelected = (gdprResponses != null && gdprResponses.size() > 0) ? gdprResponses.first() : null;
            final boolean selectedSubscriptionNotSaved = gdprGetResponseSelected == null || (gdprGetResponseSelected != null
                    && !gdprGetResponseSelected.getGdprPermissions().getVfSid().equals(vfSidSelectedSubscription));

            if (gdprGetResponseSelected != null && !gdprGetResponseSelected.getGdprPermissions().getVfSid().equals(vfSidSelectedSubscription)) {
                RealmManager.deleteRealmObjectQuery(GdprGetResponse.class, 2);
                RealmManager.deleteAfterKey(GdprGetResponse.class, 2);
            }

            new GdprService(getContext()).getPermissions(vfPhoneNumberOwnSubscription, vfSidOwnSubscription, false, 1)
                    .subscribe(new RequestSaveRealmObserver<GeneralResponse<GdprGetResponse>>() {
                        private boolean onNextCalled = false;

                        @Override
                        public void onNext(GeneralResponse<GdprGetResponse> gdprGetResponseOwnerResponse) {
                            onNextCalled = true;
                            if (GdprController.checkGetResponse(GdprController.CHECK_SUCCESS_RESPONSE, gdprGetResponseOwnerResponse)) {
                                super.onNext(gdprGetResponseOwnerResponse);
                                own_subscription_is_minor = GdprController.checkGetResponse(GdprController.CHECK_IS_MINOR, gdprGetResponseOwnerResponse);
                                rescorp_sid_not_found = false;
                                new GdprService(getContext()).getPermissions(vfPhoneNumberSelectedSubscription, vfSidSelectedSubscription, selectedSubscriptionNotSaved, 2)
                                        .subscribe(requestSaveRealmObserver);
                                return;
                            }

                            if (GdprController.checkGetResponse(GdprController.CHECK_RESCORP_SID_NOT_FOUND_RESPONSE, gdprGetResponseOwnerResponse)) {
                                own_subscription_is_minor = false;
                                rescorp_sid_not_found = true;
                                new GdprService(getContext()).getPermissions(vfPhoneNumberSelectedSubscription, vfSidSelectedSubscription, selectedSubscriptionNotSaved, 2)
                                        .subscribe(requestSaveRealmObserver);
                                return;
                            }

                            manageErrorCodes(GdprController.checkGetResponse(GdprController.CHECK_FAULT_RESPONSE,
                                    gdprGetResponseOwnerResponse)
                                    ? gdprGetResponseOwnerResponse.getTransactionFault().getFaultCode()
                                    : "default", LAYOUT_ERROR);
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            if (!onNextCalled && onNextNotCalledForOwner())
                                new GdprService(getContext()).getPermissions(vfPhoneNumberSelectedSubscription, vfSidSelectedSubscription, selectedSubscriptionNotSaved, 2)
                                        .subscribe(requestSaveRealmObserver);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            stopLoadingDialog();
                            parent.setVisibility(View.VISIBLE);
                            manageErrorCodes("default", LAYOUT_ERROR);
                        }

                    });
        }
    }

    private void getPermissionsForOtherTypeOfUsers() {
        showLoadingDialog();

        User user = VodafoneController.getInstance().getUser();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();

        final boolean checkIfPowerUserOrSubUserMigrated = user instanceof PowerUser || user instanceof SubUserMigrated;
        final int keyForResponse = checkIfPowerUserOrSubUserMigrated ? 2 : 1;

        if (VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser
                || VodafoneController.getInstance().getUser() instanceof EbuMigrated)
            vfSid = "";

        if (checkIfPowerUserOrSubUserMigrated) {
            vfPhoneNumber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
            vfSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        }

        new GdprService(getContext()).getPermissions(vfPhoneNumber, vfSid, false, keyForResponse).subscribe(new RequestSaveRealmObserver<GeneralResponse<GdprGetResponse>>() {
            private boolean onNextCalled = false;
            @Override
            public void onNext(GeneralResponse<GdprGetResponse> gdprGetResponseGeneralResponse) {
                super.onNext(null);
                onNextCalled = true;
                manageGetPermissionsGeneral(gdprGetResponseGeneralResponse, false, keyForResponse);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                if (!onNextCalled) {
                    if (checkIfPowerUserOrSubUserMigrated)
                        onNextNotCalledForSubUserAndPowerUser();
                    else
                        onNextNotCalled(true);
                }
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
                manageErrorCodes("default", LAYOUT_ERROR);
            }
        });
    }

    private void onNextNotCalledForSubUserAndPowerUser() {
        RealmResults<GdprGetResponse> gdprResponses = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 2);
        GdprGetResponse gdprGetResponse = (gdprResponses != null && gdprResponses.size() > 0) ? gdprResponses.first() : null;
        if (gdprGetResponse != null) {
            own_subscription_is_minor = GdprController.checkGetResponse(GdprController.CHECK_IS_MINOR, new GeneralResponse<GdprGetResponse>(gdprGetResponse));
            rescorp_sid_not_found = false;
            gdprPermissions = gdprGetResponse.getGdprPermissions();
            gdprPermissionsChanged = GdprPermissions.copyObject(gdprPermissions);
            createCards();
            return;
        }
        manageErrorCodes("default", LAYOUT_ERROR);
    }

    private boolean onNextNotCalledForOwner() {
        RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
        GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;
        if (gdprGetResponseOwner != null) {
            own_subscription_is_minor = GdprController.checkMinorStatus(gdprGetResponseOwner.getGdprPermissions());
            rescorp_sid_not_found = false;
            return true;
        }
        stopLoadingDialog();
        parent.setVisibility(View.VISIBLE);
        manageErrorCodes("default", LAYOUT_ERROR);
        return false;
    }

    private void onNextNotCalled(boolean ifSameSubscription) {
        RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
        GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;
        if (ifSameSubscription) {
            if (gdprGetResponseOwner != null) {
                own_subscription_is_minor = GdprController.checkGetResponse(GdprController.CHECK_IS_MINOR, new GeneralResponse<GdprGetResponse>(gdprGetResponseOwner));
                rescorp_sid_not_found = false;
                gdprPermissions = gdprGetResponseOwner.getGdprPermissions();
                gdprPermissionsChanged = GdprPermissions.copyObject(gdprPermissions);
                createCards();
                return;
            }
        } else {
            RealmResults<GdprGetResponse> gdprResponsesSubscription = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 2);
            GdprGetResponse gdprGetResponseSubscription = (gdprResponsesSubscription != null && gdprResponsesSubscription.size() > 0) ? gdprResponsesSubscription.first() : null;
            if (gdprGetResponseSubscription != null) {
                if (rescorp_sid_not_found)
                    own_subscription_is_minor = false;
                else if (gdprGetResponseOwner != null)
                    own_subscription_is_minor = GdprController.checkGetResponse(GdprController.CHECK_IS_MINOR, new GeneralResponse<GdprGetResponse>(gdprGetResponseOwner));
                gdprPermissions = gdprGetResponseSubscription.getGdprPermissions();
                gdprPermissionsChanged = GdprPermissions.copyObject(gdprPermissions);
                createCards();
                return;
            }
        }
        manageErrorCodes("default", LAYOUT_ERROR);
    }

    protected void setPermissions(final boolean isFromOverlayDialog, final String saveOrActivate, String requestType) {
        showLoadingDialog();

        User user = VodafoneController.getInstance().getUser();

        String customerId = VodafoneController.getInstance().getUserProfile().getCid();
        String vfSsoUsername = VodafoneController.getInstance().getUserProfile().getUserName();
        String phoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();

        boolean checkIfResCorpOrPowerUserOrSubUserMigrated = user instanceof ResCorp || user instanceof PowerUser || user instanceof SubUserMigrated;
        if (checkIfResCorpOrPowerUserOrSubUserMigrated)
            phoneNumber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();

        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
            customerId = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid();

        String sessionId = SessionIdMap.getSessionIdForSid(gdprPermissionsChanged.getVfSid());
        if (TextUtils.isEmpty(sessionId)) {
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            sessionId = gdprPermissionsChanged.getVfSid() + timeStamp;
            RealmManager.startTransaction();
            SessionIdMap timeToLeaveMap = new SessionIdMap(gdprPermissionsChanged.getVfSid(), sessionId, true);
            RealmManager.update(timeToLeaveMap);
        }

        gdprPermissionsChanged.setSessionId(sessionId);

        subscription = new GdprService(getContext()).setPermissions(gdprPermissionsChanged, vfSsoUsername, customerId, requestType, phoneNumber).subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse gdprPutResponseGeneralResponse) {
                stopLoadingDialog();
                manageSetPermissionsResponse(gdprPutResponseGeneralResponse, isFromOverlayDialog, saveOrActivate);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                if (saveOrActivate.equalsIgnoreCase(DEACTIVATE))
                    gdprPermissionsChanged.setMinorStatus(GdprPermissions.MINOR.toUpperCase());

                manageErrorCodes("default", TOAST_ERROR);
            }
        });

    }

    private void manageGetPermissionsGeneral(GeneralResponse<? extends GdprGetResponse> gdprGetResponseSelectedSubscriptionResponse, boolean isOwnSubscriptionSelected, int key) {
        if (GdprController.checkGetResponse(GdprController.CHECK_SUCCESS_RESPONSE, gdprGetResponseSelectedSubscriptionResponse)) {
            gdprGetResponseSelectedSubscriptionResponse.getTransactionSuccess().setId_gdpr(key);
            RequestSaveRealmObserver.save(gdprGetResponseSelectedSubscriptionResponse);
            if (isOwnSubscriptionSelected)
                own_subscription_is_minor = GdprController.checkGetResponse(GdprController.CHECK_IS_MINOR, gdprGetResponseSelectedSubscriptionResponse);
            gdprPermissions = gdprGetResponseSelectedSubscriptionResponse.getTransactionSuccess().getGdprPermissions();
            gdprPermissionsChanged = GdprPermissions.copyObject(gdprPermissions);
            createCards();
            return;
        }
        manageErrorCodes(GdprController.checkGetResponse(GdprController.CHECK_FAULT_RESPONSE,
                gdprGetResponseSelectedSubscriptionResponse)
                ? gdprGetResponseSelectedSubscriptionResponse.getTransactionFault().getFaultCode()
                : "default", LAYOUT_ERROR);
    }

    private void manageSetPermissionsResponse(GeneralResponse gdprPutResponseGeneralResponse,
                                              boolean isFromOverlayDialog, String saveOrActivate) {

        if (GdprController.checkPutResponse(GdprController.CHECK_SUCCESS_RESPONSE, gdprPutResponseGeneralResponse)) {
            RealmManager.deleteObjects(GdprGetResponse.class);
            RealmManager.deleteAfterKey(GdprGetResponse.class, 1);
            RealmManager.deleteAfterKey(GdprGetResponse.class, 2);
            if (isFromOverlayDialog) {

                switch (saveOrActivate) {
                    case SAVE:
                        overlayDialog.dismiss();
                        if (getActivity() != null)
                             getActivity().onBackPressed();
                        return;
                    case ACTIVATE:
                        //TODO code cleanup - replace String with labels
                        new CustomToast.Builder(getContext()).message(SettingsLabels.getRegisteredRequest1()).success(true).show();
                        IntentActionName.DASHBOARD.setExtraParameter("getPermissions");
                        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);
                        //TODO code cleanup - replace String with labels
                        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Permissions, null,
                                SettingsLabels.getRegisteredRequest3(), "Ok", "Meniu permisiuni",
                                true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.RedirectWithIntent);
                        voiceOfVodafone.setIntentActionName(IntentActionName.SETTINGS_PERMISSIONS_FRAGMENT);
                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                        return;
                    default:
                        return;
                }
            }

            switch (saveOrActivate) {
                case DEACTIVATE:
                    switchButtonMinor.setCheckedNoEvent(false);
                    minorCardTitle.setText(getMinorAccountTitle());
                    editLayoutContainer.setVisibility(View.GONE);
                    new CustomToast.Builder(getContext()).message(SettingsLabels.getRegisteredRequest2()).success(true).show();
                    return;
                default:
                    new CustomToast.Builder(getContext()).message(SettingsLabels.getRegisteredRequest1()).success(true).show();
                    IntentActionName.DASHBOARD.setExtraParameter("getPermissions");
                    new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);

                    VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Permissions, null,
                            SettingsLabels.getRegisteredRequest3(), "Ok", "Meniu permisiuni",
                            true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.RedirectWithIntent);
                    voiceOfVodafone.setIntentActionName(IntentActionName.SETTINGS_PERMISSIONS_FRAGMENT);
                    VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                    return;
            }
        }

        RealmManager.deleteValues(SessionIdMap.class, SessionIdMap.KEY, gdprPermissionsChanged.getVfSid());

        if (saveOrActivate.equalsIgnoreCase(DEACTIVATE))
            gdprPermissionsChanged.setMinorStatus(GdprPermissions.MINOR.toUpperCase());

        manageErrorCodes("default", TOAST_ERROR);
    }

    protected void setSpecifyAndSwitchVisibility(final RelativeLayout specify, final SwitchButton switchButton,
                                               final ImageView imageView, final LinearLayout tapContainer,
                                               View tapLeft, View tapRight, int type, final boolean enabled) {
        if (checkForSpecify(type)) {

            switchButton.post(new Runnable() {
                @Override
                public void run() {
                    final int width = switchButton.getWidth() + 40;
                    final int height = switchButton.getHeight() + 5; //height is ready

                    switchButton.setVisibility(View.GONE);
                    specify.setVisibility(View.VISIBLE);
                    imageView.setImageResource(enabled ? R.drawable.specify_state_enabled : R.drawable.specify_state_disabled);
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                    tapContainer.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                }
            });

            tapLeft.setClickable(enabled);
            tapLeft.setEnabled(enabled);
            tapRight.setClickable(enabled);
            tapRight.setEnabled(enabled);

            if (enabled) {

                tapLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchButton.setCheckedAndCallListener(false);
                        switchButton.setVisibility(View.VISIBLE);
                        specify.setVisibility(View.GONE);
                    }
                });

                tapRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchButton.setCheckedAndCallListener(true);
                        switchButton.setVisibility(View.VISIBLE);
                        specify.setVisibility(View.GONE);
                    }
                });
            }

        } else {
            specify.setVisibility(View.GONE);
            switchButton.setVisibility(View.VISIBLE);
            switchButton.setChecked(isSwitchChecked(type));
            setSwitchAvailability(switchButton, imageView, tapLeft, tapRight, enabled);
        }
    }

    private void setSwitchAvailability(SwitchButton switchButton, ImageView image, View tapLeft, View tapRight, boolean availability) {
        if (switchButton.getVisibility() == View.VISIBLE)
            setNormalSwitchAvailability(switchButton, availability);
        else
            setSpecifyAvailability(image, tapLeft, tapRight, availability);
    }

    // Normal switch -> only with 2 states(on/off)
    protected void setNormalSwitchAvailability(SwitchButton switchButton, boolean availability) {
        switchButton.setClickable(availability);
        switchButton.setEnabled(availability);
    }

    private void setSpecifyAvailability(ImageView imageMiddleState, View tapLeft, View tapRight, boolean availability) {
        imageMiddleState.setClickable(availability);
        imageMiddleState.setEnabled(availability);
        imageMiddleState.setImageResource(availability ? R.drawable.specify_state_enabled : R.drawable.specify_state_disabled);
        tapLeft.setClickable(availability);
        tapLeft.setEnabled(availability);
        tapRight.setClickable(availability);
        tapRight.setEnabled(availability);
    }

    protected void setButtonAvailability(VodafoneButton vodafoneButton, boolean availability) {
        vodafoneButton.setClickable(availability);
        vodafoneButton.setEnabled(availability);
    }

    protected boolean isSwitchChecked(int type) {
        switch (type) {
            case SMS_MMS_PUSH:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfSmsMmsPush())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtSmsMmsPush());
            case EMAIL:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfEmail())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtEmail());
            case POST:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfPost())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtPost());
            case OUTBOUND_CALL:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfOutboundCall())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtOutboundCall());
            case SURVEY_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfSurveyCategory())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtSurveyCategory());
            case CREATE_PROFILE_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfBasicProfileCustServiceData())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtBasicProfileCustServiceData());
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfAdvancedProfileNetworkData())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtAdvancedProfileNetworkData());
            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isChecked(gdprPermissionsChanged.getVfAdvancedProfileOnlineData())
                        : GdprController.isChecked(gdprPermissionsChanged.getExtAdvancedProfileOnlineData());
            default:
                return false;
        }
    }

    protected boolean checkForSpecify(int type) {
        switch (type) {
            case SMS_MMS_PUSH:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfSmsMmsPush())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtSmsMmsPush());
            case EMAIL:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfEmail())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtEmail());
            case POST:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfPost())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtPost());
            case OUTBOUND_CALL:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfOutboundCall())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtOutboundCall());
            case SURVEY_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfSurveyCategory())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtSurveyCategory());
            case CREATE_PROFILE_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfBasicProfileCustServiceData())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtBasicProfileCustServiceData());
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfAdvancedProfileNetworkData())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtAdvancedProfileNetworkData());
            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                return card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD
                        ? GdprController.isSpecify(gdprPermissionsChanged.getVfAdvancedProfileOnlineData())
                        : GdprController.isSpecify(gdprPermissionsChanged.getExtAdvancedProfileOnlineData());
            default:
                return false;
        }
    }

    protected boolean isBasicProfilingDisablingAdvancedProfiling() {
        User user = VodafoneController.getInstance().getUser();

        if (!isSwitchChecked(CREATE_PROFILE_TYPE)) {
            if (user instanceof ResCorp) {
                return !own_subscription_is_minor;
            } else if (user instanceof EbuMigrated) {
                return ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && !GdprController.checkMinorStatus(gdprPermissions))
                        || ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && !UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber());
            } else if ((user instanceof PrepaidUser || user instanceof CBUUser) && !isResSubGeoNumber()) {
                return !GdprController.checkMinorStatus(gdprPermissions);
            }
        }

        return false;
    }

    protected boolean isResSubGeoNumber() {
        return VodafoneController.getInstance().getUser() instanceof ResSub
                && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber();
    }

    private void manageErrorCodes(String code, int error_type) {
        switch (code) {
            case GdprController.EC00040:
                inflateErrorLayout(SettingsLabels.getRetryButton());
                break;
            case GdprController.EC07001:
                inflateErrorLayout(SettingsLabels.getSidErrorMessage());
                break;
            case GdprController.EC07002:
                inflateErrorLayout(SettingsLabels.getRetryButton());
                break;
            default:
                if (error_type == LAYOUT_ERROR) {
                    inflateErrorLayout(SettingsLabels.getRetryButton());
                    return;
                }
                new CustomToast.Builder(getContext()).message(SettingsLabels.getSimpleSmallError()).success(false).show();
                break;
        }
    }

    public String getTitle() {
        switch (card_type) {
            case PermissionsFragment.VODAFONE_PERMISSIONS_CARD:
                return SettingsLabels.getVodafonePermissionsTitle();
            case PermissionsFragment.VODAFONE_PARTENERS_CARD:
                return SettingsLabels.getVodafonePartenersTitle();
            default:
                return SettingsLabels.getSetingsPermissionsTitle();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (save_button_card != null)
            save_button_card.setVisibility(View.GONE);
        if (subscription != null)
            subscription.unsubscribe();
        if (overlayDialog != null && overlayDialog.isShowing())
            overlayDialog.dismiss();
        ((SettingsActivity) getActivity())
                .getNavigationHeader()
                .setTitle(SettingsLabels.getSetingsPermissionsTitle());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoadingDialog();
    }

    public static class PermissionsDetailsTrackingEvent extends TrackingEvent {

        private String card_type;

        protected void setCard_type(String card_type) {
            this.card_type = card_type;
        }

        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }

            switch (card_type) {
                case PermissionsFragment.MINOR_ACCOUNT_CARD:
                    s.pageName = s.prop21 + "minor account";
                    s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:permissions:" + "minor account");
                    s.prop23 = "mcare:permissions:" + "minor account";
                    s.getContextData().put("prop23", s.prop23);
                    break;
                case PermissionsFragment.VODAFONE_PERMISSIONS_CARD:
                    s.pageName = s.prop21 + "vodafone";
                    s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:permissions:" + "vodafone");
                    s.prop23 = "mcare:permissions:" + "vodafone";
                    s.getContextData().put("prop23", s.prop23);
                    break;
                case PermissionsFragment.VODAFONE_PARTENERS_CARD:
                    s.pageName = s.prop21 + "vodafone partners";
                    s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:permissions:" + "vodafone partners");
                    s.prop23 = "mcare:permissions:" + "vodafone";
                    s.getContextData().put("prop23", s.prop23);

                    s.event65 = "button:" + "save permission";
                    s.getContextData().put("event65", s.event65);
                    s.eVar82 = "mcare:permissions:vodafone partners:button:" + "save";
                    s.getContextData().put("eVar82", s.eVar82);
                    break;
                case ACTIVATE_MINOR_OVERLAY:
                    s.pageName = s.prop21 + "minor overlay";
                    s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:permissions:" + "minor overlay");
                    s.prop23 = "mcare:permissions:" + "minor overlay";
                    s.getContextData().put("prop23", s.prop23);
                    break;
            }

            s.channel = "gdpr permissions";
            s.getContextData().put("&&channel", s.channel);

            s.prop21 = "mcare:" + "settings";
            s.getContextData().put("prop21", s.prop21);

            s.prop22 = "mcare:" + "permissions";
            s.getContextData().put("prop22", s.prop22);

            s.prop31 = "mcare";
            s.getContextData().put("prop31", s.prop31);

            s.eVar73 = "gdpr permissions";
            s.getContextData().put("eVar73", s.eVar73);

        }
    }

}
