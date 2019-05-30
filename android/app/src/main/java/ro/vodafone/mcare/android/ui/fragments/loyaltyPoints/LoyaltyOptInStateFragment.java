package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptOutSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyPointsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static android.view.View.GONE;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyOptInStateFragment extends BaseFragment {
    private String TAG = "LoyaltyOptInState";

    private RelativeLayout transactionsHistory;
    private RelativeLayout gainLoyaltyPoints;
    private VodafoneButton offersButton;
    private VodafoneButton refuseProgram;
    private Dialog dialogOverlay;
    private List<BaseCardControllerInterface> controllers;
    private boolean isBanSelectorShow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loyality_program_authorized_layout, null);

        Log.d(TAG, "onCreateView");

        transactionsHistory = (RelativeLayout) v.findViewById(R.id.loyalty_transactions_history);
        gainLoyaltyPoints = (RelativeLayout) v.findViewById(R.id.gain_loyalty_points);
        offersButton = (VodafoneButton) v.findViewById(R.id.loyalty_offers_button);
        refuseProgram = (VodafoneButton) v.findViewById(R.id.refuse_loyalty_program);

        checkQuitProgramAvailability();

        setupArrowButtonsCards();
        setupClickListeners();

        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.loyalty,TealiumConstants.loyalty_opt_in_state);

        LoyaltyOptInTrackingEvent event = new LoyaltyOptInTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        isBanSelectorShow = ((LoyaltyPointsActivity) getActivity()).getNavigationHeader().getVisibility() == View.VISIBLE;

        return v;
    }

    private void setupArrowButtonsCards() {
        View transactionHistoryView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, transactionsHistory, false);
        VodafoneTextView transactionHistoryTitle = (VodafoneTextView) transactionHistoryView.findViewById(R.id.cardTitle);
        VodafoneTextView subtext = (VodafoneTextView) transactionHistoryView.findViewById(R.id.cardSubtext);
        subtext.setVisibility(GONE);
        transactionHistoryTitle.setText(LoyaltyLabels.getLoyaltyTransactionHistoryTitle());
        transactionsHistory.addView(transactionHistoryView);

        View gainPointsView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, gainLoyaltyPoints, false);
        VodafoneTextView pointsCardTitle = (VodafoneTextView) gainPointsView.findViewById(R.id.cardTitle);
        VodafoneTextView pointsSubtext = (VodafoneTextView) gainPointsView.findViewById(R.id.cardSubtext);
        pointsSubtext.setVisibility(GONE);
        pointsCardTitle.setText(LoyaltyLabels.getLoyalty_points_gain());
        gainLoyaltyPoints.addView(gainPointsView);
    }

    private void setupClickListeners() {
        final LoyaltyPointsActivity activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);

        gainLoyaltyPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackEvent(TealiumConstants.loyalty_gain_points);
                if (activity != null)
                    activity.addFragment(new LoyaltyPointsReceiveFragment());
            }
        });

        transactionsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackEvent(TealiumConstants.loyalty_opt_in_transaction_history);
                if (activity != null)
                    activity.addFragment(new LoyaltyPointsHistoryFragment());
            }
        });

        offersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                trackEvent(TealiumConstants.loyalty_opt_in_offers);
                new NavigationAction(getContext(), IntentActionName.OFFERS).finishCurrent(true).startAction();

            }
        });

        refuseProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConfirmationRefuseDialog();
            }
        });
    }
    private void trackEvent(String event){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_opt_in_state);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
    private void displayConfirmationRefuseDialog() {
        dialogOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogOverlay.setContentView(R.layout.overlay_dialog_notifications);

        Button dismissButton = (Button) dialogOverlay.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(AppLabels.getGiveUpButton());
        dismissButton.setOnClickListener(dismissListener);

        Button okButton = (Button) dialogOverlay.findViewById(R.id.buttonKeepOn);
        okButton.setText(LoyaltyLabels.getLoyalty_quit_program());
        okButton.setOnClickListener(agreeListener);

        VodafoneTextView etfOverlayTitle = (VodafoneTextView) dialogOverlay.findViewById(R.id.overlayTitle);
        etfOverlayTitle.setText(LoyaltyLabels.getLoyalty_quit_title());
        VodafoneTextView etfOverlayMessage = (VodafoneTextView) dialogOverlay.findViewById(R.id.overlaySubtext);
        etfOverlayMessage.setText(LoyaltyLabels.getLoyalty_quit_overlay_message());

        ImageView closeButton = (ImageView) dialogOverlay.findViewById(R.id.overlayDismissButton);
        closeButton.setOnClickListener(dismissListener);

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","loyalty opt out overlay");
        tealiumMapView.put("journey_name","loyalty program");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        LoyaltyOptOutTrackingEvent event = new LoyaltyOptOutTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        dialogOverlay.show();
    }

    View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEventDismiss = new HashMap(6);
            tealiumMapEventDismiss.put("screen_name","loyalty opt out overlay");
            tealiumMapEventDismiss.put("event_name","mcare:loyalty opt out overlay:button:anuleaza");
            tealiumMapEventDismiss.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEventDismiss);

            //Tealium Track Event
            Map<String, Object> tealiumMapEventX = new HashMap(6);
            tealiumMapEventX.put("screen_name","loyalty opt out overlay");
            tealiumMapEventX.put("event_name","mcare:loyalty opt out overlay:button:(x)");
            tealiumMapEventX.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEventX);

            dialogOverlay.dismiss();
        }
    };
    View.OnClickListener agreeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","loyalty opt out overlay");
            tealiumMapEvent.put("event_name","mcare:loyalty opt out overlay:button:renunta la program");
            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            quitProgram();
        }
    };

    private void getLoyaltyProgram() {
        ShopLoyaltyProgramSuccess shopLoyaltyProgramSuccess = (ShopLoyaltyProgramSuccess) RealmManager.getRealmObject(ShopLoyaltyProgramSuccess.class);
        Log.d("", "getLoyaltyProgram: " + shopLoyaltyProgramSuccess);
        if (shopLoyaltyProgramSuccess != null) {
            for (BaseCardControllerInterface controler : controllers) {
                controler.onDataLoaded(shopLoyaltyProgramSuccess);
            }
        }

    }

    public void requestData(BaseCardControllerInterface controller) {
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        controllers.add(controller);
        getLoyaltyProgram();
    }

    private void quitProgram() {
        final LoyaltyPointsActivity activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);

        ShopService shopService = new ShopService(getContext());
        if (activity != null) {
            shopService.quitLoyaltyProgram(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), activity.getShopSessionToken())
                    .subscribe(new RequestSessionObserver<GeneralResponse<OptOutSuccess>>() {
                        @Override
                        public void onNext(GeneralResponse<OptOutSuccess> optOutSuccessGeneralResponse) {
                            if (optOutSuccessGeneralResponse.getTransactionStatus() == 0 &&
                                    optOutSuccessGeneralResponse.getTransactionSuccess().getSetOptOutState().equals(LoyaltyPointsActivity.SERVICE_SUCCES)) {
                                VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(16, 20, VoiceOfVodafoneCategory.LP, null, LoyaltyLabels.getLoyalty_quit_program_success(), "Ok, am înțeles", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                                Log.d("vov widget", "insertAuto");

                                VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                                new CustomToast.Builder(getContext()).message(LoyaltyLabels.getLoyalty_quit_program_success()).success(true).show();
//                                CustomToast customToast = new CustomToast(getActivity(), getContext(), LoyaltyLabels.getLoyalty_quit_program_success(),true);
//                                customToast.show();

                                new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
                            } else {
                                dialogOverlay.dismiss();
                                new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
//                                CustomToast customToast = new CustomToast(getActivity(), getContext(), AppLabels.getToastErrorMessage(), false);
//                                customToast.show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            dialogOverlay.dismiss();
                            new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
//                            CustomToast customToast = new CustomToast(getActivity(), getContext(), AppLabels.getToastErrorMessage(), false);
//                            customToast.show();
                        }
                    });
        }
    }

    private void checkQuitProgramAvailability() {
        ShopLoyaltyProgramSuccess shopLoyaltyProgramSuccess = (ShopLoyaltyProgramSuccess) RealmManager.getRealmObject(ShopLoyaltyProgramSuccess.class);
        if (!shopLoyaltyProgramSuccess.getAllowedToUnsubscribe()) {
            refuseProgram.setVisibility(GONE);
        } else {
            refuseProgram.setVisibility(View.VISIBLE);
        }
    }

    public static class LoyaltyOptOutTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty opt out overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty opt out overlay");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class LoyaltyOptInTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty opt in";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty opt in");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSHow", isBanSelectorShow);

    }

    @Override
    public void onResume() {
        super.onResume();
        User user = VodafoneController.getInstance().getUser();
        if (isBanSelectorShow) {
            if (user instanceof ResCorp ||
                    user instanceof CorpUser ||
                    user instanceof CorpSubUser) {
                ((LoyaltyPointsActivity) getActivity()).getNavigationHeader().showBanSelector();
            }
        }
    }
}
