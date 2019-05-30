package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.realm.RealmResults;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.GdprService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static android.support.annotation.Dimension.SP;
import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by cosmin deliu on 2/8/2018.
 */

public class PermissionsFragment extends SettingsFragment {

    public static final String MINOR_ACCOUNT_CARD = "minor_card";
    public static final String VODAFONE_PERMISSIONS_CARD = "vodafone_card";
    public static final String VODAFONE_PARTENERS_CARD = "parteners_card";

    private LinearLayout parent;
    private NavigationHeader navigationHeader;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SettingsActivity) getActivity()).getNavigationHeader().setTitle(getTitle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSettingsFragment = false;
        navigationHeader = ((SettingsActivity) getActivity()).getNavigationHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.parent_padding);

        parent = new LinearLayout(getContext());
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setVisibility(View.GONE);

        parent.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);

        initTracking();

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        D.w("Permission Fragment onStart !!!!");
        showMsisdnSelector();
        getPermissions();
    }

    void initTracking() {
        PermissionsTrackingEvent event = new PermissionsTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public String getTitle() {
        return SettingsLabels.getSetingsPermissionsTitle();
    }

    private void showMsisdnSelector() {
        navigationHeader.displayDefaultHeader();
        if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
            navigationHeader.showMsisdnSelector();
            return;
        }
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    private void createCards() {
        parent.removeAllViews();
        inflateCardLayout(VODAFONE_PERMISSIONS_CARD);
        inflateCardLayout(VODAFONE_PARTENERS_CARD);
        inflateCardLayout(MINOR_ACCOUNT_CARD);
    }

    private void inflateCardLayout(final String card_type) {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_selection, null);
        v.findViewById(R.id.cardSubtext).setVisibility(View.GONE);

        switch (card_type) {
            case MINOR_ACCOUNT_CARD:
                if (VodafoneController.getInstance().getUser() instanceof EbuMigrated)
                    return;
                ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getMinorAccountTitle());
                break;
            case VODAFONE_PERMISSIONS_CARD:
                ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getVodafonePermissionsTitle());
                break;
            case VODAFONE_PARTENERS_CARD:
                ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getVodafonePartenersTitle());
                break;
            default:
                break;
        }

        v.findViewById(R.id.arrowCardLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    openPermissionDetails(card_type);
            }
        });

        parent.addView(v);
    }

    private void openPermissionDetails(String card_type) {

        if (getActivity() == null)
            return;

        PermissionsTrackingEvent event = new PermissionsTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        switch (card_type) {
            case MINOR_ACCOUNT_CARD:
                journey.event65 = "button:" + "minor account";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:button:" + "minor account";
                journey.getContextData().put("eVar82", journey.eVar82);
                break;
            case VODAFONE_PERMISSIONS_CARD:
                journey.event65 = "button:" + "vodafone permissions";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:button:" + "vodafone";
                journey.getContextData().put("eVar82", journey.eVar82);
                break;
            case VODAFONE_PARTENERS_CARD:
                journey.event65 = "button:" + "vodafone partners permissions";
                journey.getContextData().put("event65", journey.event65);
                journey.eVar82 = "mcare:permissions:button:" + "vodafone partners";
                journey.getContextData().put("eVar82", journey.eVar82);
                break;
        }

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        Bundle bundle = new Bundle();
        bundle.putString(NavigationAction.EXTRA_PARAMETER_BUNDLE_KEY, card_type);
        PermissionDetailsFragment permissionDetailsFragment;

        switch (card_type) {
            case MINOR_ACCOUNT_CARD:
                permissionDetailsFragment = new MinorAccountFragment();
                break;
            default:
                permissionDetailsFragment = new VodafoneAndPartenersFragment();
                break;
        }

        permissionDetailsFragment.setArguments(bundle);
        ((SettingsActivity) getActivity()).attachFragment(permissionDetailsFragment);
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
                getPermissions();
            }
        });

        errorCard.showError(true, message);
        parent.addView(errorCard);

    }

    protected void getPermissions() {
        if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
            getPermissionsForRescorpOwner();
            return;
        }
        getPermissionsForOtherTypeOfUsers();
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
                    onNextNotCalled(isOwnSubscriptionSelected ? 1 : 2);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
                manageErrorCodes("default");
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
                                new GdprService(getContext()).getPermissions(vfPhoneNumberSelectedSubscription, vfSidSelectedSubscription, selectedSubscriptionNotSaved, 2)
                                        .subscribe(requestSaveRealmObserver);
                                return;
                            }

                            if (GdprController.checkGetResponse(GdprController.CHECK_RESCORP_SID_NOT_FOUND_RESPONSE, gdprGetResponseOwnerResponse)) {
                                new GdprService(getContext()).getPermissions(vfPhoneNumberSelectedSubscription, vfSidSelectedSubscription, selectedSubscriptionNotSaved, 2)
                                        .subscribe(requestSaveRealmObserver);
                                return;
                            }

                            manageErrorCodes(GdprController.checkGetResponse(GdprController.CHECK_FAULT_RESPONSE, gdprGetResponseOwnerResponse)
                                    ? gdprGetResponseOwnerResponse.getTransactionFault().getFaultCode()
                                    : "default");
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
                            manageErrorCodes("default");
                        }

                    });
        }
    }

    private void getPermissionsForOtherTypeOfUsers() {
        showLoadingDialog();

        final User user = VodafoneController.getInstance().getUser();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();

        boolean checkIfPowerUserOrSubUserMigrated = user instanceof PowerUser || user instanceof SubUserMigrated;
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
                onNextCalled = true;
                if (GdprController.checkGetResponse(GdprController.CHECK_SUCCESS_RESPONSE, gdprGetResponseGeneralResponse)) {
                    gdprGetResponseGeneralResponse.getTransactionSuccess().setId_gdpr(keyForResponse);
                    super.onNext(gdprGetResponseGeneralResponse);
                    createCards();
                    return;
                }

                if (user instanceof ResCorp && !GdprController.isOwnSubscriptionSelected()
                        && GdprController.checkGetResponse(GdprController.CHECK_RESCORP_SID_NOT_FOUND_RESPONSE, gdprGetResponseGeneralResponse)) {
                    createCards();
                    return;
                }

                manageErrorCodes(GdprController.checkGetResponse(GdprController.CHECK_FAULT_RESPONSE, gdprGetResponseGeneralResponse) ? gdprGetResponseGeneralResponse.getTransactionFault().getFaultCode() : "default");
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                if (!onNextCalled)
                    onNextNotCalled(keyForResponse);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                parent.setVisibility(View.VISIBLE);
                manageErrorCodes("default");
            }
        });
    }

    private boolean onNextNotCalledForOwner() {
        RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
        GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;

        if (gdprGetResponseOwner != null)
            return true;

        stopLoadingDialog();
        parent.setVisibility(View.VISIBLE);
        manageErrorCodes("default");
        return false;
    }

    private void onNextNotCalled(int key) {
        RealmResults<GdprGetResponse> gdprResponses = RealmManager.getRealmObjectQuery(GdprGetResponse.class, key);
        GdprGetResponse gdprGetResponse = (gdprResponses != null && gdprResponses.size() > 0) ? gdprResponses.first() : null;
        if (gdprGetResponse != null) {
            createCards();
            return;
        }
        manageErrorCodes("default");
    }

    private void manageGetPermissionsGeneral(GeneralResponse<? extends GdprGetResponse> gdprGetResponseSelectedSubscriptionResponse, boolean isOwnSubscriptionSelected, int key) {
        if (GdprController.checkGetResponse(GdprController.CHECK_SUCCESS_RESPONSE, gdprGetResponseSelectedSubscriptionResponse)) {
            gdprGetResponseSelectedSubscriptionResponse.getTransactionSuccess().setId_gdpr(key);
            RequestSaveRealmObserver.save(gdprGetResponseSelectedSubscriptionResponse);
            createCards();
            return;
        }
        manageErrorCodes(GdprController.checkGetResponse(GdprController.CHECK_FAULT_RESPONSE,
                gdprGetResponseSelectedSubscriptionResponse)
                ? gdprGetResponseSelectedSubscriptionResponse.getTransactionFault().getFaultCode()
                : "default");
    }

    private void manageErrorCodes(String code) {
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
                inflateErrorLayout(SettingsLabels.getRetryButton());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED)
            getPermissions();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationHeader.hideSelectorViewWithoutTriangle();
        if (getActivity() != null)
            ((SettingsActivity) getActivity()).setTitle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    @Override
    public void makeAdobeRequest() {
        //don't remove or set Adobe Target Request
    }

    public static class PermissionsTrackingEvent extends TrackingEvent {

        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }

            s.pageName = s.prop21 + "permissions";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "permissions");

            s.channel = "gdpr permissions";
            s.getContextData().put("&&channel", s.channel);

            s.prop21 = "mcare:" + "settings";
            s.getContextData().put("prop21", s.prop21);

            s.prop22 = "mcare:" + "permissions";
            s.getContextData().put("prop22", s.prop22);

            s.prop23 = "mcare:" + "permissions";
            s.getContextData().put("prop23", s.prop23);

            s.prop31 = "mcare";
            s.getContextData().put("prop31", s.prop31);

            s.eVar73 = "gdpr permissions";
            s.getContextData().put("eVar73", s.eVar73);

        }
    }

}
