package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.support.ChatBubble;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.webviews.PhoneShopWebViewActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Victor Radulescu on 3/21/2017.
 */

public class RetentionFragment extends OffersFragment {

    private static final String TAG = "RetentionFragment";

    LinearLayout viewGroup;
    @Nullable
    GeneralCardsWithTitleBodyAndTwoButtons helpCard;
    boolean shopRequestInProcess = true;
    ChatBubble chatBubble;
    private NavigationHeader navigationHeader;
    OffersActivity activity;

    public RetentionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "create view");

        viewGroup = new LinearLayout(getContext());
        viewGroup.setOrientation(LinearLayout.VERTICAL);
        viewGroup.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));

        return viewGroup;
    }

    private void setHeaderText() {
        try {
            activity.getNavigationHeader().setTitle("Ofertă pentru tine");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = (OffersActivity) getActivity();
        chatBubble = activity.getChatBubble();
        chatBubble.forceHide();
        try {
            VodafoneController.getInstance().supportWindow(getActivity()).forceCloseDontHideBubble(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildNavigationHeader();

        requestData();

        callForAdobeTarget(AdobePageNamesConstants.RETENTION_ELIGIBIL);
    }

    private void buildNavigationHeader() {
        navigationHeader = activity.getNavigationHeader();
        navigationHeader.removeViewFromContainer();
        navigationHeader.hideBannerView();
        if (VodafoneController.getInstance().getUser() instanceof ResCorp || VodafoneController.getInstance().getUser() instanceof ResSub) {
            Log.d(TAG, "rescorp or ressub user");
            navigationHeader.buildMsisdnSelectorHeader();
            //navigationHeader.displaySelectorView();
        }
        setHeaderText();
    }

    private void showRetentionHistoryCard() {
        GeneralCardsWithTitleBodyAndTwoButtons historyCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        historyCard
                .setTitle(RetentionLabels.getRetentionHistoryRechargeTitle())
                .setTitleBold()
                .setTitleTextSize(17)
                .setSecondaryButtonMessage(RetentionLabels.getRetentionHistoryRechargeSecondaryBtn())
                .setSecondaryButtonClickListener(
                        new RedirectFragmentListener(getActivity(), new OrderHistoryFragment()))
                .setNormalPaddings()
                .build();
        addCard(historyCard);
    }

    private void showRetentionNotEmptyShopingCard(final String shoppingCartUrl) {
        GeneralCardsWithTitleBodyAndTwoButtons notEmptyShoping = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        notEmptyShoping
                .setTitle(RetentionLabels.getRetentionShoppingCartEmptyFalseTitle())
                .setTitleBold()
                .setBody(RetentionLabels.getRetentionShoppingCartEmptyFalseBody())
                .setPrimaryButtonMessage(RetentionLabels.getRetentionShoppingCartEmptyFalsePrimaryBtn())
                .setPrimaryButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(), PhoneShopWebViewActivity.class);
                        intent.putExtra(WebviewActivity.KEY_URL, shoppingCartUrl);
                        getActivity().startActivityForResult(intent, 10000);
                    }
                })
                .setNormalPaddings()
                .build();
        addCard(notEmptyShoping);
    }

    private void showRetentionEligibleForPhoneAndService(boolean showPhoneEligible, boolean showServiceEligible) {
        if (!showPhoneEligible && !showServiceEligible) {
            return;
        }
        GeneralCardsWithTitleBodyAndTwoButtons eligibleForPhoneAndService = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());

        if (showServiceEligible) {
            if (!showPhoneEligible) {
                eligibleForPhoneAndService.setPrimaryButtonMessage(RetentionLabels.getRetentionEligibleServices());
                eligibleForPhoneAndService.setPrimaryButtonClickListener(new RedirectFragmentListener(getActivity(), new PricePlansFragment())
                     {
                         @Override
                         public void onClick(View v) {
                             super.onClick(v);
                             //Tealium Track Event
                             Map<String, Object> tealiumMapEvent = new HashMap(6);
                             tealiumMapEvent.put("screen_name","retention");
                             tealiumMapEvent.put("event_name","mcare:retention:eligibility page:button:vezi oferta servicii");
                             tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                             TealiumHelper.trackEvent("event_name", tealiumMapEvent);
                         }
                     }
                );
            } else {
                eligibleForPhoneAndService.setSecondaryButtonMessage(RetentionLabels.getRetentionEligibleServices());
                eligibleForPhoneAndService.setSecondaryButtonClickListener(
                        new RedirectFragmentListener(getActivity(), new PricePlansFragment())
                        {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                //Tealium Track Event
                                Map<String, Object> tealiumMapEvent = new HashMap(6);
                                tealiumMapEvent.put("screen_name","retention");
                                tealiumMapEvent.put("event_name","mcare:retention:eligibility page:button:vezi oferta servicii");
                                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                                TealiumHelper.trackEvent("event_name", tealiumMapEvent);
                            }
                        }

                );
            }

        }
        if (showPhoneEligible) {
            eligibleForPhoneAndService.setPrimaryButtonMessage(RetentionLabels.getRetentionEligiblePhones());
            eligibleForPhoneAndService.setPrimaryButtonClickListener(
                    new RedirectFragmentListener(getActivity(), new PhonesFragment())

                    {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            //Tealium Track Event
                            Map<String, Object> tealiumMapEvent = new HashMap(6);
                            tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
                            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.eligibilityPageButtonVeziOfertaTelefoane);
                            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                            TealiumHelper.trackEvent(RetentionFragment.this.getClass().getSimpleName(), tealiumMapEvent);
                        }
                    }
            );

        }
        eligibleForPhoneAndService
                .setNormalPaddings()
                .build();
        addCard(eligibleForPhoneAndService);
    }

    private void showRetentionEligibleMsisdnNotEligible() {
        String text = RetentionLabels.getRetentionEligiblePhonesNotAuthorized();
        String textToReplace = "[MSISDN]";
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        if (msisdn == null) {
            return;
        }

        if (msisdn.startsWith("40")) {
            msisdn = msisdn.replaceFirst("4", "");
        }

        text = text.replace(textToReplace, msisdn);
        GeneralCardsWithTitleBodyAndTwoButtons notAllowedCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        notAllowedCard
                .setTitle(RetentionLabels.getRetentionShoppingCartEmptyFalseTitle())
                .setTitleBold()
                .setBody(text)
                .build();
        helpCard = notAllowedCard;
        addCard(notAllowedCard);

        chatBubble.displayBubble(true);
    }

    private void addCard(GeneralCardsWithTitleBodyAndTwoButtons card) {
        viewGroup.addView(card);
    }

    private void showInfo(ShopEligibilitySuccess shopEligibility) {
        removeViews();
        stopLoading();
        if (shopEligibility == null) {
            showError();
            return;
        }
        if (shopEligibility.getIsShoppingCartEmpty() && (shopEligibility.getAllowPhones() || shopEligibility.getAllowPricePlans())) {
            showRetentionEligibleForPhoneAndService(shopEligibility.getAllowPhones(),
                    shopEligibility.getAllowPricePlans());
        } else if (!shopEligibility.getIsShoppingCartEmpty()) {
            showRetentionNotEmptyShopingCard(shopEligibility.getShoppingCartUrl());
        } else {
            showRetentionEligibleMsisdnNotEligible();
        }
        showRetentionHistoryCard();

    }

    private void showError() {
        try {
            GeneralCardsWithTitleBodyAndTwoButtons errorCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
            errorCard.showError(true, "Serviciu momentan indisponibil\n" +
                    "Apasa pentru a reîncerca.");
            errorCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestData();
                }
            });
            addCard(errorCard);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestData() {
        showLoadingDialog();
        removeViews();

        Observable<GeneralResponse<ShopEligibilitySuccess>> observable = RetentionObservables
                .getInstance().getCheckUserEligibilityObservable();
        Subscription subscription = observable.subscribe(new RequestSessionObserver<GeneralResponse<ShopEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopEligibilitySuccess> shopEligibilitySuccessGeneralResponse) {
                if(getActivity()==null){
                    return;
                }
                RequestSaveRealmObserver.save(shopEligibilitySuccessGeneralResponse);
                if (shopEligibilitySuccessGeneralResponse != null && shopEligibilitySuccessGeneralResponse.getTransactionSuccess() != null) {
                    showInfo(shopEligibilitySuccessGeneralResponse.getTransactionSuccess());
                } else {
                    stopLoading();
                    showError();
                }
            }

            @Override
            public void onCompleted() {
                if(getActivity()==null){
                    return;
                }
                //Tealium Track View
                Map<String, Object> tealiumMapView =new HashMap(6);
                tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.retentionScreen);
                tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.retentionJourney);
                tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackView(RetentionFragment.this.getClass().getSimpleName(), tealiumMapView);

                RetentionEligibilityPageTrackingEvent event = new RetentionEligibilityPageTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);

                stopLoading();
            }

            @Override
            public void onError(Throwable e) {
                if(getActivity()==null){
                    return;
                }
                super.onError(e);
                stopLoading();
                showError();
            }
        });
        addToActivityCompositeSubcription(subscription);
        if (requestsInProgress()) {
            showLoadingDialog();
        }
    }

    private boolean requestsInProgress() {
        return shopRequestInProcess;
    }

    private void stopLoading() {
        super.stopLoadingDialog();
        shopRequestInProcess = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopLoading();
        if(navigationHeader != null) {
            navigationHeader.hideSelectorView();
        }

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            ((OffersActivity) getActivity()).getNavigationHeader().setTitle(OffersLabels.getOffers_for_you_page_prepaid_title());
        } else {
            ((OffersActivity) getActivity()).getNavigationHeader().setTitle(OffersLabels.getOffers_for_you_page_postpaid_title());
        }
    }


    private void removeViews() {
        if (viewGroup == null) {
            return;
        }
        viewGroup.removeAllViews();
    }

    @Override
    public String getTitle() {
        return OffersLabels.getOffers_for_you_services_button_label();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        try {

            if (resultCode == RESULT_SELECTOR_UPDATED) {

                if(getActivity() != null)
                     viewGroup.removeAllViews();
                else
                    return;

                Log.d(TAG, "update element");

                if (VodafoneController.getInstance().supportWindow(getActivity()) != null)
                    VodafoneController.getInstance().supportWindow(getActivity()).closeChatFromRetention();

                Subscriber subscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();
                buildNavigationHeader();

                if (subscriber != null) {
                    Log.d(TAG, "Subscriber Selector number: " + subscriber.getMsisdn());

                    Observable.timer(200, TimeUnit.MILLISECONDS)
                            .onBackpressureDrop()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    showLoadingDialog();
                                    requestData();
                                }
                            });
                }

                callForAdobeTarget(AdobePageNamesConstants.RETENTION_ELIGIBIL);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class RetentionEligibilityPageTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:eligibility page";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "retention:eligibility page");


            s.prop5 = "sales:landing page";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
