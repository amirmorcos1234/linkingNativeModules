package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseExpandableCard;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Benefit;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static android.view.View.GONE;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */

@Deprecated
public class YourServicesContractDetailsFragment extends OffersFragment {
    public static String TAG = "YourServicesContractD";
    public static String CARD_TYPE = "cardType";
    public static String ACTIVE_OPTIONS_CARD = "activeOptionsCard";
    public static String ACTIVE_SERVICES_CARD = "activeServicesCard";
    public static String PENDING_OFFERS_CARD = "pendingOffersCard";


    @BindView(R.id.option_name)
    VodafoneTextView abonamentName;

    @BindView(R.id.offer_cost)
    VodafoneTextView cost;

    @BindView(R.id.start_date)
    VodafoneTextView startDateText;

    @BindView(R.id.section_title_label)
    VodafoneTextView sectionTitleLabel;

    @BindView(R.id.cost_label)
    VodafoneTextView costLabel;

    @BindView(R.id.contract_period_label)
    VodafoneTextView priodLabel;

    @BindView(R.id.end_date)
    VodafoneTextView endDateText;

    @BindView(R.id.contract_period)
    VodafoneTextView contractPeriod;

    @BindView(R.id.go_to_accepted_offers)
    Button goToAcceptedOffersButton;

    @BindView(R.id.expandable_cards_container)
    LinearLayout expandableCardsContainer;

    @BindView(R.id.error_card)
    CardErrorLayout cardErrorLayout;

    private String cardType;

    private ActiveOfferPostpaid pricePlanOffer;
    private Promo promo;
    private BillingOffer billingOffer;
    private YourServicesActivity yourServicesActivity;
    private OfferRowInterface offerRowInterface;

    private Dialog etfOverlay;
    private VodafoneButton deleteOptionButton;

    public static YourServicesContractDetailsFragment create(String key , Serializable serializableObject) {
        YourServicesContractDetailsFragment fragment = new YourServicesContractDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable(key, serializableObject);
        fragment.setArguments(args);

        return fragment;
    }

    public YourServicesContractDetailsFragment setArgsOnBundle(String key , Serializable serializableObject){

        Bundle args = new Bundle();
        args.putSerializable(key, serializableObject);
        setArguments(args);

        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(yourServicesActivity != null){
            yourServicesActivity.getNavigationHeader().hideSelectorView();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardType = (String) getArguments().getSerializable(CARD_TYPE);
        pricePlanOffer = (ActiveOfferPostpaid) getArguments().getSerializable(ActiveOfferPostpaid.class.getCanonicalName());
        promo = (Promo) getArguments().getSerializable(Promo.class.getCanonicalName());
        billingOffer = (BillingOffer) getArguments().getSerializable(BillingOffer.class.getCanonicalName());
        //activeOffer = (ActiveOffer) getArguments().getSerializable(ActiveOffer.class.getCanonicalName());;
        //activeOfferPostpaid = (ActiveOfferPostpaid) getArguments().getSerializable(ActiveOfferPostpaid.class.getCanonicalName());
        offerRowInterface = (OfferRowInterface) getArguments().getSerializable(OfferRowInterface.class.getCanonicalName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.card_contract_details, container, false);
        ButterKnife.bind(this, v);



        yourServicesActivity = (YourServicesActivity) getActivity();

        trackWaitingOffersView();
        initGoToAcceptedOffersButton();
        setAttributes();
        trackYourServicesView();


        addPricePlanDetailsCard();
        addPricePlanDesciption();
        return v;
    }

    private void setAttributes(){

        if(offerRowInterface != null){

            sectionTitleLabel.setText(getResources().getString(R.string.your_services_details_service_label));
            costLabel.setText(getResources().getString(R.string.your_services_details_cost_label));
            priodLabel.setText(getResources().getString(R.string.your_services_details_duration_label));

            setPricePlanName(offerRowInterface.getOfferName());
            setCost(NumbersUtils.twoDigitsAfterDecimal(offerRowInterface.getOfferPrice()) + " €");

            User user = VodafoneController.getInstance().getUser();
            Long startDate = offerRowInterface instanceof ActiveOffer?
                    ((ActiveOffer) offerRowInterface).getStartDate():
                    ((ActiveOfferPostpaid) offerRowInterface).getStartDate();
            Long endDate = offerRowInterface instanceof ActiveOffer?
                    ((ActiveOffer) offerRowInterface).getEndDate():
                    ((ActiveOfferPostpaid) offerRowInterface).getEndDate();

            setStartDate(DateUtils.getDate(startDate,
                    new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
            setEndDate(DateUtils.getDate(endDate,
                    new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));

            if (startDate != null && endDate != null) {
                setContractPeriod(String.format(getContext().getResources().getString(R.string.your_option_days),
                        String.valueOf(TimeUnit.MILLISECONDS.toDays(endDate - startDate))));
            } else if (startDate != null) {
                setContractPeriod(ServicesLabels.getServices_unlimited_label());
            }

            if (user instanceof PrepaidUser) {
                if(((ActiveOffer) offerRowInterface).getRoamingInfoUrl() != null &&
                        !((ActiveOffer) offerRowInterface).getRoamingInfoUrl().equals("")){
                    addExpandableWebViewCard(((ActiveOffer) offerRowInterface).getRoamingInfoUrl());
                }

                if(((ActiveOffer) offerRowInterface).getIsRenewable()
                        || ((ActiveOffer) offerRowInterface).getAllowOfferDelete()){
                    addDeleteOptionButton();
                }
            } else {
                if(user instanceof CBUUser){
                    addDeleteOptionButton();
                }
            }
        }
    }

    private void setPricePlanName(String offerName){
        abonamentName.setText(offerName);
    }

    private void setCost(String cost){
        this.cost.setText(String.format(
                getContext().getString(R.string.your_services_contract_cost_description), cost));
    }

    private void setStartDate(String startDate){
        if(startDate!=null) {
            startDateText.setText(String.format(getContext().getString(R.string.your_services_details_start_date), startDate));
            startDateText.setVisibility(View.VISIBLE);
        }
    }

    private void setEndDate(String endDate){
        if(endDate!=null) {
            endDateText.setText(String.format(getContext().getString(R.string.your_services_details_end_date), endDate));
            endDateText.setVisibility(View.VISIBLE);
        }
    }

    private void setContractPeriod(String contractPeriod){
        if(contractPeriod!=null) {
            this.contractPeriod.setText(contractPeriod);
            this.contractPeriod.setVisibility(View.VISIBLE);
        }else{
            priodLabel.setVisibility(GONE);
        }
    }

    public String getTitle() {
        return ((String) getResources().getText(R.string.accepted_offers_contract_details));
    }

    private void trackWaitingOffersView(){
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","distance contract details");
        tealiumMapView.put("journey_name","waiting offers");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);
    }

    private void trackYourServicesView(){
        Map<String, Object> tealiumMapViewCallDetails =new HashMap(6);
        tealiumMapViewCallDetails.put("screen_name","contract details");
        tealiumMapViewCallDetails.put("journey_name","your services");
        tealiumMapViewCallDetails.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapViewCallDetails);

        YourServicesContractDetailsTrackingEvent event = new YourServicesContractDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    private static class YourServicesContractDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "contract details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"contract details");


            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void initGoToAcceptedOffersButton() {
        if(VodafoneController.getInstance().getUser() instanceof CBUUser) {
            goToAcceptedOffersButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.go_to_accepted_offers)
    public void goToacceptedOffersClick(){
        ((YourServicesActivity) getContext()).attachFragment(new AncomAcceptedOffersFragment());
    }

    private void addPricePlanDetailsCard(){

        if(promo == null || promo.getBenefitList() == null || promo.getBenefitList().isEmpty())
            return;

       LinearLayout linearLayout = new LinearLayout(getContext());
       linearLayout.setOrientation(LinearLayout.VERTICAL);
       linearLayout.setPadding(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10));

       for(Benefit benefit : promo.getBenefitList()){
           linearLayout.addView(makeBenefitsItem(benefit));
       }

        BaseExpandableCard pricePlanDetailsCard = new BaseExpandableCard(getContext());
        pricePlanDetailsCard.setTitle("Detalii abonament")
                .setContent(linearLayout)
                .build();

        expandableCardsContainer.addView(pricePlanDetailsCard);
    }

    private View makeBenefitsItem(Benefit benefit){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.benefits_list_item, null);
        ((TextView)view.findViewById(R.id.beneffit_description)).setText(benefit.getBenefitDescription());
        return view;
    }

    private void addPricePlanDesciption(){
        if(promo == null){
            return;
        }

        TextView textView = new TextView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(ScreenMeasure.dpToPx(16),ScreenMeasure.dpToPx(16),ScreenMeasure.dpToPx(16),ScreenMeasure.dpToPx(16));
        textView.setLayoutParams(params);
        textView.invalidate();
        textView.setTextColor(Color.BLACK);
        textView.setText("The description for main promotion.If no description available, it will not be displayed. Data source:  eShopDescription. ODS9.getAssignedPromotions\n");

        BaseExpandableCard pricePlanDetailsCard = new BaseExpandableCard(getContext());
        pricePlanDetailsCard.setTitle("Descriere abonament")
                .setContent(textView)
                .build();

        expandableCardsContainer.addView(pricePlanDetailsCard);
    }

    private void addExpandableWebViewCard(String webUrl){
        ExpandableWebViewCard expandableCardInfo = new ExpandableWebViewCard(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        expandableCardInfo.setLayoutParams(params);

        expandableCardInfo.setVisibility(View.VISIBLE);
        VodafoneTextView expandableCardTitleView = (VodafoneTextView) expandableCardInfo.findViewById(R.id.card_title_tv);
        expandableCardTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        expandableCardTitleView.setTextColor(ContextCompat.getColor(getContext(),R.color.dark_gray_text_color));
        expandableCardInfo.setVisibility(View.VISIBLE);
        expandableCardInfo.setTitle("Informații tarife");
        expandableCardInfo.hideLine();
        expandableCardInfo.setWebViewUrl(webUrl);

        expandableCardInfo.setImageArrowDirectionDown();
        expandableCardInfo.build();

        expandableCardInfo.setCardPaddingsInDp(0,16,16,16);

        expandableCardsContainer.addView(expandableCardInfo);
    }

    private void addDeleteOptionButton() {
        Log.d(TAG, "addDeleteOptionButton: ");
        final VodafoneAbstractCard deleteOptionBtnCard = new VodafoneAbstractCard(getContext()) {
            @Override
            protected int setContent() {
                return 0;
            }
        };

        // ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.OverlaySecondaryButton);
        deleteOptionButton = new VodafoneButton(getContext());
        deleteOptionButton.setTransformationMethod(null);
        deleteOptionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        deleteOptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        deleteOptionButton.setTypeface(Fonts.getVodafoneRG());
        deleteOptionButton.setBackgroundResource(R.drawable.selector_button_background_card_secondary);
        deleteOptionButton.setText(ServicesLabels.getServices_delete_option_card_button());

        deleteOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","option details");
                tealiumMapEvent.put("event_name","mcare:option details:button:sterge optiunea");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                if (VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser ||
                        VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                    displayStopOptionPrepaidOverlay();
                } else if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                    displayStopOptionPostPaidOverlay();
                }
            }
        });

        deleteOptionBtnCard.addButton(deleteOptionButton);
        expandableCardsContainer.addView(deleteOptionBtnCard);
    }

    private void displayStopOptionPrepaidOverlay() {
        Log.d(TAG, "displayStopOptionPrepaidOverlay()");
        final Dialog confirmationOverlay;
        confirmationOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        confirmationOverlay.setContentView(R.layout.overlay_dialog_notifications);
        confirmationOverlay.show();

        Button buttonAccept = (Button) confirmationOverlay.findViewById(R.id.buttonKeepOn);
        Button buttonRefuse = (Button) confirmationOverlay.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlaySubtext);

        buttonAccept.setText(ServicesLabels.getServices_delete_option_button());
        buttonRefuse.setText(AppLabels.getGiveUpButton());

        overlayTitle.setText(ServicesLabels.getServices_delete_option_page_title());
        overlaySubtext.setText(ServicesLabels.getServices_delete_option_page_content());

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOptionPrepaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void deleteOptionPrepaid() {
        Log.d(TAG, "deleteOptionPrepaid()");

        if(offerRowInterface == null)
            return;

        ActivatePrepaidOfferRequest activatePrepaidOfferRequest = new ActivatePrepaidOfferRequest();
        activatePrepaidOfferRequest.setOperation("4");

        OffersService offersService = new OffersService(getContext());
        offersService.activateEligibleOffer(String.valueOf(offerRowInterface.getOfferId()), activatePrepaidOfferRequest)
                .subscribe(new RequestSessionObserver<GeneralResponse<EligibleOffersSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                        if (eligibleOffersSuccessResponse.getTransactionStatus() == 0) {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status 0");

                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD,true);
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getService_stop_offer()).success(true).show();
                        } else {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status !0");
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "activateOrInactivateRoaming onError");
                        new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                    }
                });
    }

    private void deleteOptionPostpaid() {

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn() : null;
        String subscriberId = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid() : null;

        if(offerRowInterface == null)
            return;

        ActiveOfferPostpaid activeOfferPostpaid = (ActiveOfferPostpaid) offerRowInterface;
        OffersService offersService = new OffersService(getContext());
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        int bilCycleDate = 0;

        if (msisdn == null) {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        if (subscriberId == null) {
            subscriberId = VodafoneController.getInstance().getUserProfile().getSid();
        }

        if(profile != null){
            bilCycleDate = profile.getBillCycleDate();
        }

        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }
        Log.d(TAG," concat msisdn " +  msisdn);

        offersService.deletePostPaidOffer(msisdn, subscriberId, activeOfferPostpaid.getOfferId(), null,
                new ActivationRequest(activeOfferPostpaid.getOfferName(), bilCycleDate, activeOfferPostpaid.getOfferType()))
                .subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActivationEligibilitySuccess> response) {
                        if (response.getTransactionStatus() == 0) {
                            ((BaseActivity)getContext()).stopLoadingDialog();
                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD,true);
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_option_inactivated_succes_message()).success(true).show();
                        } else {
                            ((BaseActivity)getContext()).stopLoadingDialog();
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ((BaseActivity)getContext()).stopLoadingDialog();
                    }
                });
    }

    private void displayStopOptionPostPaidOverlay() {
        Log.d(TAG, "displayStopOptionPostPaidOverlay()");
        final Dialog confirmationOverlay;
        confirmationOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        confirmationOverlay.setContentView(R.layout.overlay_dialog_notifications);
        confirmationOverlay.show();

        Button buttonAccept = (Button) confirmationOverlay.findViewById(R.id.buttonKeepOn);
        Button buttonRefuse = (Button) confirmationOverlay.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlaySubtext);

        buttonAccept.setText(ServicesLabels.getServices_inactivate_option_button());
        buttonRefuse.setText(AppLabels.getGiveUpButton());

        overlayTitle.setText(ServicesLabels.getServices_inactivate_option_page_title());
        overlaySubtext.setText(ServicesLabels.getServices_inactivate_option_page_content());

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPendingOffers4PostPaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void getPendingOffers4PostPaid(){
        Log.d(TAG, "getPendingOffers4PostPaid()");
        ((BaseActivity)getContext()).showLoadingDialog();
        OffersService offersService = new OffersService(getContext());
        ;
        offersService.getPendingOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid()
                : VodafoneController.getInstance().getUserProfile().getSid(), "offer").subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse activeOffersSuccessResponse) {
                Log.d(TAG, "getPendingOffers4PostPaid() Transaction Status = " + activeOffersSuccessResponse.getTransactionStatus());
                if(activeOffersSuccessResponse.getTransactionStatus() == 0){
                    checkETF();
                }else if(activeOffersSuccessResponse.getTransactionStatus() == 1){
                    ((BaseActivity)getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_pending_request_message()).success(false).show();
                }else if(activeOffersSuccessResponse.getTransactionStatus() == 2){
                    ((BaseActivity)getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "getPendingOffers4PostPaid() onError");
                ((BaseActivity)getContext()).stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
            }
        });
    }

    private void checkETF(){
        Log.d(TAG, "checkETF()");
        try {

            if(offerRowInterface == null)
                return;

            Boolean hasETF = ((ActiveOfferPostpaid) offerRowInterface).getHasETF();

            if(hasETF != null && hasETF){
                ((BaseActivity)getContext()).stopLoadingDialog();
                displayEtfOverlay();
            }else{
                deleteOptionPostpaid();
            }
        }catch (Exception e){
            e.printStackTrace();;
            ((BaseActivity)getContext()).stopLoadingDialog();
            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
        }
    }

    private void displayEtfOverlay() {
        etfOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        etfOverlay.setContentView(R.layout.overlay_dialog_notifications);
        Button dismissButton = (Button) etfOverlay.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(AppLabels.getGiveUpButton());
        dismissButton.setOnClickListener(eftOverlayButtonsListener);

        Button okButton = (Button) etfOverlay.findViewById(R.id.buttonKeepOn);
        okButton.setVisibility(GONE);

        VodafoneTextView etfOverlayTitle = (VodafoneTextView) etfOverlay.findViewById(R.id.overlayTitle);
        etfOverlayTitle.setText(ServicesLabels.getServices_etf_overlay_title());
        VodafoneTextView etfOverlayMessage = (VodafoneTextView) etfOverlay.findViewById(R.id.overlaySubtext);
        etfOverlayMessage.setText(ServicesLabels.getServices_etf_overlay_message());

        ImageView closeButton = (ImageView) etfOverlay.findViewById(R.id.overlayDismissButton);
        closeButton.setOnClickListener(eftOverlayButtonsListener);

        etfOverlay.show();
    }

    View.OnClickListener eftOverlayButtonsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etfOverlay.dismiss();
            ((ChatBubbleActivity)getContext()).getChatBubble().displayBubble(true);
        }
    };

    private void displayPendingRequestMessage(String message){
        cardErrorLayout.setMargins(0,16,0,0);
        cardErrorLayout.setText(message);
        cardErrorLayout.setVisibility(View.VISIBLE);
    }
}


