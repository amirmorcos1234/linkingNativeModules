package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.travelling.RoamingStatusCard;
import ro.vodafone.mcare.android.card.travelling.TravellingTextExpandCard;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Eliza Deaconescu on 4/11/2017.
 */

public class TravellingServiceAdministration extends BaseFragment implements TravellingAbroadContract.AdministrationView {

    public static final String TAG = "TravelingServiceAdmin";

    TravellingAbroadInteractorImpl mTravellingInteractorImpl;
    TravellingAdministrationPresenter mTravellingAdministrationPresenter;

    @BindView(R.id.administration_roaming_card)
    RoamingStatusCard administrationRoamingCard;
    @BindView(R.id.administration_international_card)
    RoamingStatusCard administrationInternationalCard;
    @BindView(R.id.administration_travelling_aboad_card_desciption_view)
    RelativeLayout administrationTravellingAboadCardDesciptionView;
    @BindView(R.id.administration_travelling_aboad_card_view)
    RelativeLayout administrationTravellingAboadCardView;

    android.view.View.OnClickListener activationRoamingListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            if (getContext() != null && FragmentUtils.getVisibleFragment((TravelingAboardActivity) getActivity(), false) instanceof TravellingServiceAdministration)
                mTravellingAdministrationPresenter.activationConfirmationOverlay("Roaming");

        }
    };
    android.view.View.OnClickListener activationInternationalListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            if (getContext() != null && FragmentUtils.getVisibleFragment((TravelingAboardActivity) getActivity(), false) instanceof TravellingServiceAdministration)
                mTravellingAdministrationPresenter.activationConfirmationOverlay(TravellingAboardLabels.getTravelling_aboard_international_tittle());

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTravellingAdministrationPresenter = new TravellingAdministrationPresenter(this);
        mTravellingInteractorImpl = new TravellingAbroadInteractorImpl();
    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        android.view.View view = inflater.inflate(R.layout.travelling_service_administration_fragment, container, false);
        ButterKnife.bind(this, view);

        mTravellingAdministrationPresenter.setUpVariables();

        return view;
    }

    android.view.View.OnClickListener onPrepaidErrorCardListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            mTravellingAdministrationPresenter.errorStep();
        }
    };

    @Override
    public void showErrorCard() {
        administrationRoamingCard.setVisibility(View.VISIBLE);
        administrationRoamingCard.showError(true, TravellingAboardLabels.getTravelling_aboard_system_error_text());
        administrationRoamingCard.addOnErrorClickListener(onPrepaidErrorCardListener);
    }

    protected void setupRoamingCardForUsersNotPrepaid(boolean isRoamingActive, boolean isOffersFromList,boolean isPrepaid, String alias) {
        administrationRoamingCard.setIsNationalOnly(mTravellingInteractorImpl.nationalOnlyPP)
                .setIsActive(isRoamingActive)
                .setIsPrepaid(isPrepaid)
                .setIsOffersFromList(isOffersFromList)
                .setCardTitle(TravellingAboardLabels.getTravelling_aboard_roaming_tittle())
                .setButtonClickListener(activationRoamingListener)
                .setActivationButton(isRoamingActive)
                .setAliasOrStatus(alias)
                .setRoamingDescription(mTravellingInteractorImpl.setRoamingStatusDescription(isRoamingActive))
                .activateButton()
                .buildStatusCard(false);
        administrationRoamingCard.setVisibility(View.VISIBLE);

    }

    protected void setupRoamingCardPrepaid(boolean isRoamingActive, boolean isOffersFromList, String alias, String roamingLabel, Boolean isNationalOnly) {

        administrationRoamingCard.hideBubleForNationalOnly(isNationalOnly);
        administrationRoamingCard.setIsNationalOnly(mTravellingInteractorImpl.nationalOnlyPP)
                                .setIsPrepaid(true)
                                .setIsActive(isRoamingActive)
                                .setIsOffersFromList(isOffersFromList)
                                .setCardTitle(TravellingAboardLabels.getTravelling_aboard_roaming_tittle())
                                .setButtonClickListener(activationRoamingListener)
                                .setActivationButton(isRoamingActive)
                                .setAliasOrStatus(alias)
                                .setRoamingDescriptionFromtml(roamingLabel)
                                .activateButton()
                                .buildStatusCard(false);

        administrationRoamingCard.setVisibility(View.VISIBLE);

    }

    protected void setupInternationalCard(boolean isInternationalActive, boolean inactivateButton) {
        administrationInternationalCard.setVisibility(android.view.View.VISIBLE);
        administrationInternationalCard.setIsNationalOnly(mTravellingInteractorImpl.nationalOnlyPP)
                .setIsActive(isInternationalActive)
                .setCardTitle(TravellingAboardLabels.getTravelling_aboard_international_tittle())
                .setButtonClickListener(activationInternationalListener)
                .setActivationButton(isInternationalActive)
                .setAliasOrStatus(UserSelectedMsisdnBanController.getInstance().getSubscriberAlias())
                .setRoamingDescription(mTravellingAdministrationPresenter.getInternationalDescription(isInternationalActive))
                .activateButton()
                .buildStatusCard(false);
        if (inactivateButton)
            administrationInternationalCard.inactivateButton(isInternationalActive);
    }

    public void setUpNavigationHeader() {
        if(getActivity() == null) {
            return;
        }
        NavigationHeader navigationHeader = ((TravelingAboardActivity) getActivity()).getNavigationHeader();
        navigationHeader.setTitle(TravellingAboardLabels.getTravelling_aboard_administration_tittle());
        navigationHeader.buildMsisdnSelectorHeader();
        if (VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.RES_CORP) ||
                VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            navigationHeader.displayDefaultHeader();
        } else {
            navigationHeader.hideSelectorView();
        }
    }

    public String getTitle() {
        return TravellingAboardLabels.getTravelling_aboard_administration_tittle();
    }

    protected TravellingTextExpandCard addExpandableTextViewCard(String title, String description, String msisdn) {

        if (getContext() == null)
            return null;

        TravellingTextExpandCard expandableTextViewCard = new TravellingTextExpandCard(getContext())
                .setTitle(title)
                .setDescription(description)
                .setMsisdn(msisdn)
                .build();
        administrationTravellingAboadCardDesciptionView.addView(expandableTextViewCard);
        return expandableTextViewCard;
    }

    protected void showRoamingCardWithError(String errorMessage, String cardTitle, boolean showDescription, boolean reload) {
        administrationRoamingCard.setVisibility(View.VISIBLE);
        administrationRoamingCard.setCardTitle(cardTitle);
        administrationRoamingCard.showInsideErrorCard(errorMessage, showDescription);
        if(reload) {
            administrationRoamingCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoadingDialog();
                    mTravellingAdministrationPresenter.reload(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                            UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
                }
            });
        }
    }

    protected void hideRoamingCard() {
        administrationRoamingCard.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
            mTravellingAdministrationPresenter.reload(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                    UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpNavigationHeader();
        callForAdobeTarget(AdobePageNamesConstants.ROAMING_STATUS);
    }

    @Override
    public void onTypeAccessSuccess(boolean isRoamingActive, boolean isInternationalActive) {
        stopLoadingDialog();
        administrationRoamingCard.showLoading(true);
        administrationRoamingCard.setVisibility(View.VISIBLE);
        administrationInternationalCard.showLoading(true);
        mTravellingAdministrationPresenter.runAdministrationFlow(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                                                                 UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
    }

    @Override
    public void onTypeAccessFailed() {
        stopLoadingDialog();
        if(mTravellingInteractorImpl.isGeonumber()) {
            showRoamingCardWithError(TravellingAboardLabels.getTravellingAbroadErrorRetry(), null, false,false);
            administrationInternationalCard.setVisibility(View.GONE);
        } else {
            showRoamingCardWithError(TravellingAboardLabels.getTravelling_aboard_system_error(), "Roaming", false,false);
            setupInternationalCard(false, true);
        }
    }

    public static class TravellingAbroadStatusTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "roaming status";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "roaming status");
            s.channel = "roaming";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "roaming status";
            s.getContextData().put("prop21", s.prop21);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public AppCompatActivity getActivityInAdministration() {
        return (AppCompatActivity) getActivity();
    }

    protected void setupRoamingCardForPPYDepositNotRequired(boolean isRoamingActive, String alias) {

        String status = isRoamingActive ? "activ" : "inactiv";

        String roamingStatus = TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart1() + " <b>" + status + "</b> "
                + TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart2();

        administrationRoamingCard.setVisibility(View.VISIBLE);

        administrationRoamingCard
                .setIsActive(isRoamingActive)
                .setCardTitle(TravellingAboardLabels.getTravelling_aboard_roaming_tittle())
                .setButtonClickListener(activationRoamingListener)
                .setActivationButton(isRoamingActive)
                .setAliasOrStatus(alias)
                .setRoamingDescription(roamingStatus)
                .buildInactiveCardForPPYDepositNotRequired();
    }

    public void showPPForGeonumbers() {
        administrationRoamingCard.setVisibility(View.VISIBLE);
        administrationRoamingCard.showError(true, TravellingAboardLabels.getTravelling_aboard_roaming_pending());
    }

}
