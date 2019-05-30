package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.activeOptions.ActiveOptionsCardController;
import ro.vodafone.mcare.android.card.activeServices.ActiveServicesCardController;
import ro.vodafone.mcare.android.card.additionalPromos.AdditionalPromosCardController;
import ro.vodafone.mcare.android.card.aggregatedBenefits.AggregatedBenefitsCardController;
import ro.vodafone.mcare.android.card.costControl.CostControlCardController;
import ro.vodafone.mcare.android.card.pricePlan.PricePlanCardController;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequestsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.system.MenuLabels;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

public class YourServicesFragment extends OffersFragment {

    public static String TAG = "YourServicesFragment";

    @BindView(R.id.container)
    LinearLayout containter;

    @BindView(R.id.bottom_view_container)
    LinearLayout bottomViewContainer;

    private VodafoneTextView pricePlanCardTitle;
    private VodafoneTextView activeOptionsCardTitle;
    private VodafoneTextView activeServiceCardTitle;
    private VodafoneTextView pendingOffersCardTitle;
    private VodafoneButton seeMoreOptionsButton;

    private PricePlanCardController pricePlanCardController;
    private CostControlCardController costControlCardController;
    private AggregatedBenefitsCardController aggregatedBenefitsCardController;
    private ActiveOptionsCardController activeOptionsCardController;
    private ActiveServicesCardController activeServicesCardController;
    private AdditionalPromosCardController additionalPromosCardController;

    private ActiveOffersSuccess eligibleActiveOffersPrepaid;
    private ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;
    private CostControl costControl;
    private UserRequestsSuccess userRequestsSuccess;

    private List<BaseCardControllerInterface> controllerList;

    private int runningTasks;

    @Override
    public void onResume() {
        super.onResume();
        ((YourServicesActivity) getActivity()).getNavigationHeader().displaySelectorView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_your_services, null);
        ButterKnife.bind(this, v);

        init();
        setupTelium();
        return v;
    }

    public void reload(){
        if (runningTasks == 0)
            init();
    }

    private void init(){

        createControllers();
        addControllersToList();
        if(!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            setupUI();
        }else{
            showLoadingDialog();
        }
        requestDataFromControllers();
    }

    private void setupUI(){
        containter.removeAllViews();

        addOptionButton();
        createCardsTitles();
        addCards();
    }

    private void createControllers(){

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            costControlCardController = new CostControlCardController(getContext());
            aggregatedBenefitsCardController = new AggregatedBenefitsCardController(getContext());

        }else{
            pricePlanCardController = new PricePlanCardController(getContext());
        }
        activeOptionsCardController = new ActiveOptionsCardController(getContext());
        activeServicesCardController = new ActiveServicesCardController(getContext());
        additionalPromosCardController = new AdditionalPromosCardController(getContext());
    }

    private void addControllersToList(){
        if (controllerList == null) {
            controllerList = new ArrayList<>();
        }

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            controllerList.add(aggregatedBenefitsCardController);
        }else{
            controllerList.add(pricePlanCardController);
        }

        controllerList.add(activeOptionsCardController);
        controllerList.add(activeServicesCardController);
        controllerList.add(additionalPromosCardController);
    }

    private void createCardsTitles(){
        pricePlanCardTitle = makeCardTitleView("Abonament");

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            activeOptionsCardTitle = makeCardTitleView(ServicesLabels.getServices_my_offers_label());
        }else{
            activeOptionsCardTitle = makeCardTitleView(ServicesLabels.getServices_active_offers_label());
        }
        activeServiceCardTitle = makeCardTitleView(ServicesLabels.getServices_active_services_label());
        pendingOffersCardTitle = makeCardTitleView(ServicesLabels.getServices_pending_offers_label());
    }

    private VodafoneTextView makeCardTitleView(String text){

        VodafoneTextView title = new VodafoneTextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, ScreenMeasure.dpToPx(8), 0, ScreenMeasure.dpToPx(8));
        title.setLayoutParams(layoutParams);
        title.setText(text);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F);
        title.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.purple));
        title.setGravity(Gravity.CENTER);

        return title;
    }

    private void addCards(){
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            addCard(costControlCardController.getCard(), null);
            addCard(aggregatedBenefitsCardController.getCard(), null);
        }else{
            addCard(pricePlanCardController.getCard(), pricePlanCardTitle);
        }
        addCard(activeOptionsCardController.getViewGroup(), activeOptionsCardTitle);
        addCard(activeServicesCardController.getViewGroup(), activeServiceCardTitle);

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            addCard(additionalPromosCardController.getViewGroup(), pendingOffersCardTitle);
        }
    }

    private void addCard(View card, View title){
        if(card != null){
            if(title != null){
                containter.addView(title);
            }
            containter.addView(card);
        }
    }

    public void hideActiveOptionsCards(){
        containter.removeView(activeOptionsCardTitle);
        containter.removeView(activeOptionsCardController.getViewGroup());
    }

    public void hideActiveServicesCards(){
        containter.removeView(activeServiceCardTitle);
        containter.removeView(activeServicesCardController.getViewGroup());
    }

    public void hidePendingOffers(){
        containter.removeView(pendingOffersCardTitle);
        containter.removeView(additionalPromosCardController.getViewGroup());
    }

    private void requestDataFromControllers(){
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            costControlCardController.requestData();
        }else{
            pricePlanCardController.requestData();
        }
        activeOptionsCardController.requestData();
        activeServicesCardController.requestData();
        additionalPromosCardController.requestData();
    }


    public void requestData() {

        if (runningTasks == 0) {
            if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                getEligibleActiveOffers4PrePaid();
                getCostControl();
            } else if (VodafoneController.getInstance().getUser() instanceof CBUUser){
                getActiveOffersPostpaid();
                getCostControl();
            } else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
                showLoadingDialog();
                getCostControl();
                getActiveOffersEbu();
            }
        }
    }

    private void sendResponseToCardsControllers(){
        if(isDataReady()){

            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){

                stopLoadingDialog();
                setupUI();

                if(activeOffersPostpaidEbuSuccess == null && costControl == null){
                    containter.removeAllViews();
                    containter.addView(makeCardTitleView("Abonament"));
                    showErrorCard("Sistem momentan indisponibil. Apasă pentru a reîncerca.");
                }else {

                    if(costControl == null){
                        showErrorCard("Ne pare rău nu am reuşit să încărcăm toate detaliile, apasă aici pentru a reîncerca.");
                    }else{
                        if(odsCallFailed()){
                            showErrorCard("Ne pare rău nu am reuşit să încărcăm toate detaliile, apasă aici pentru a reîncerca.");
                        }
                    }

                    for (BaseCardControllerInterface cardController : controllerList) {
                        cardController.onDataLoaded(activeOffersPostpaidSuccess,
                                activeOffersPostpaidEbuSuccess, costControl, userRequestsSuccess);
                    }
                }
            }else{
                for (BaseCardControllerInterface cardController : controllerList) {
                    cardController.onDataLoaded(activeOffersPostpaidSuccess,
                            activeOffersPostpaidEbuSuccess, costControl,
                            eligibleActiveOffersPrepaid, userRequestsSuccess);
                }
            }
        }
    }

    private boolean odsCallFailed(){
        return activeOffersPostpaidEbuSuccess != null && activeOffersPostpaidEbuSuccess.getOdsCallFailed()
                != null && activeOffersPostpaidEbuSuccess.getOdsCallFailed();
    }

    @Override
    public String getTitle() {
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            return getResources().getString(R.string.costcontrol);
        } else {
            return MenuLabels.getItemServices();
        }
    }

    private void addOptionButton() {

        final User user = VodafoneController.getInstance().getUser();
        seeMoreOptionsButton = makeSeeMoreOptionButton();
		bottomViewContainer.removeAllViews();
        bottomViewContainer.addView(seeMoreOptionsButton);
        seeMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VodafoneController.getInstance().isSeamless()){
                    new NavigationAction(getContext()).finishCurrent(true).startAction(IntentActionName.OFFERS_BEO_WITH_SEAMLESS);
                } else{
                    new NavigationAction(getContext()).finishCurrent(true).startAction(IntentActionName.OFFERS_BEO_NO_SEAMLESS);
                }
            }
        });

       /* if(user instanceof CBUUser || user instanceof PrepaidUser){

        }else if(user instanceof EbuMigrated){
            //Please note that in SP10, no redirect will be functional.
            //Button hidden in SP10.
            //bottomViewContainer.addView(seeMoreOptionsButton);
            //seeMoreOptionsButton.setEnabled(false);
        }*/
    }

    private VodafoneButton makeSeeMoreOptionButton(){
        VodafoneButton seeMoreOptionButton = new VodafoneButton(getContext());


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(16),
                ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(16));
        seeMoreOptionButton.setLayoutParams(layoutParams);
        seeMoreOptionButton.setTransformationMethod(null);
        seeMoreOptionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        seeMoreOptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        seeMoreOptionButton.setTypeface(Fonts.getVodafoneRG());
        seeMoreOptionButton.setBackgroundResource(R.drawable.selector_button_background_card_secondary);
        seeMoreOptionButton.setText("Vezi mai multe opțiuni");
        return seeMoreOptionButton;
    }

    private void getEligibleActiveOffers4PrePaid() {
        runningTasks++;
        OffersService offersService = new OffersService(getContext());

        Subscription subscription = offersService.getEligibleActiveOffers4PrePaid(VodafoneController.getInstance().getUserProfile().getSid()).subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
                eligibleActiveOffersPrepaid = activeOffersSuccessResponse.getTransactionSuccess();
//                runningTasks--;
//                for (BaseCardControllerInterface cardController : controllerList) {
//                    cardController.onDataLoaded(costControl, activeOffersSuccessResponse.getTransactionSuccess());
//                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                sendResponseToCardsControllers();
//                runningTasks--;
//                for (BaseCardControllerInterface cardController : controllerList) {
//                    cardController.onRequestFailed();
//                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                sendResponseToCardsControllers();
            }
        });

        addToActivityCompositeSubcription(subscription);
    }

    private void getActiveOffersPostpaid() {
        Log.d(TAG, "getActiveOffersPostpaid()");
        runningTasks++;
        OffersService offersService = new OffersService(VodafoneController.getInstance());

        Subscription subscription = offersService.getActiveOffersPostpaid(getSelectedSubscriber().getMsisdn(), getSelectedSubscriber().getSid(),"")
                .subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersPostpaidSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActiveOffersPostpaidSuccess> activeOffersPostpaidSuccessGeneralResponse) {
                Log.d(TAG, "onNext()");
                activeOffersPostpaidSuccess = activeOffersPostpaidSuccessGeneralResponse.getTransactionSuccess();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                activeOffersPostpaidSuccess = null;
                sendResponseToCardsControllers();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                sendResponseToCardsControllers();
            }
        });

        addToActivityCompositeSubcription(subscription);
    }

    private void getActiveOffersEbu() {
        Log.d(TAG, "getActiveOffersEbu()");
        runningTasks++;
        Subscriber subscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();

        if(subscriber == null)
            return;

        OffersService offersService = new OffersService(VodafoneController.getInstance());
        Subscription subscription = offersService.getActiveOffersEbu(subscriber.getSid())
                .subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersPostpaidEbuSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActiveOffersPostpaidEbuSuccess> activeOffersPostpaidEbuSuccessGeneralResponse) {
                        Log.d(TAG, "onNext()");
                        activeOffersPostpaidEbuSuccess = activeOffersPostpaidEbuSuccessGeneralResponse.getTransactionSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        activeOffersPostpaidEbuSuccess = null;
                        sendResponseToCardsControllers();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        sendResponseToCardsControllers();
                    }
                });

        addToActivityCompositeSubcription(subscription);
    }

    private Subscriber getSelectedSubscriber(){
        Subscriber selectedSubscriber;

        if(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber()!=null){
            selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();
        }else{
            selectedSubscriber = new Subscriber();
            selectedSubscriber.setMsisdn(VodafoneController.getInstance().getUserProfile().getMsisdn());
            selectedSubscriber.setSid(VodafoneController.getInstance().getUserProfile().getSid());
        }

        return selectedSubscriber;
    }

    private void getCostControl(){
        runningTasks++;
        UserDataService userDataService = new UserDataService(getContext());

        Subscription subscription = userDataService.reloadCostControl(getSelectedSubscriber().getMsisdn())
                .subscribe(new RequestSessionObserver<GeneralResponse<CostControl>>() {
            @Override
            public void onNext(GeneralResponse<CostControl> costControlGeneralResponse) {
                costControl = costControlGeneralResponse.getTransactionSuccess();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                costControl = null;
                sendResponseToCardsControllers();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                sendResponseToCardsControllers();
            }
        });
        addToActivityCompositeSubcription(subscription);
    }

  /*  private void getUsersRequests(){
        runningTasks++;
        OffersService offersService = new OffersService(getContext());

        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        if (userProfile == null || entityChildItem == null)
            return;

        //Todo Remove Hardcode
        Subscription subscription = offersService.getUserRequests(userProfile.isMigrated(), userProfile.getUserRoleString(),
                entityChildItem.getVfOdsCid(), entityChildItem.getVfOdsBan(),
                entityChildItem.getCrmRole(), false, false, false)
                .subscribe(new RequestSessionObserver<GeneralResponse<UserRequestsSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<UserRequestsSuccess> costControlGeneralResponse) {
                        userRequestsSuccess = (UserRequestsSuccess) costControlGeneralResponse.getTransactionSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        sendResponseToCardsControllers();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        sendResponseToCardsControllers();
                    }
                });
        addToActivityCompositeSubcription(subscription);
    }*/

    private String getFormatedMsisdn(String msisdn){
        if(msisdn != null && msisdn.startsWith("0")){
            msisdn = "4" +msisdn;
        }
        return msisdn;
    }

    private void setupTelium(){
        try{
            //Tealium Track View
            Map<String, Object> tealiumMapView = new HashMap(6);
            tealiumMapView.put("screen_name","your services");
            tealiumMapView.put("journey_name","your services");
            tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackView("screen_name", tealiumMapView);

            YourServicesTrackingEvent event = new YourServicesTrackingEvent();
            VodafoneController.getInstance().getTrackingService().track(event);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static class YourServicesTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "your services";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "your services");


            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private boolean isDataReady() {
        return (--runningTasks == 0);
    }

    private void showErrorCard(String message){
        CardErrorLayout cardErrorLayout = new CardErrorLayout(getContext());
        cardErrorLayout.setMargins(16, 16, 16, 16);

        cardErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //containter.removeAllViews();
                //bottomViewContainer.removeAllViews();
                init();
            }
        });

        cardErrorLayout.setText(message);

        final VodafoneAbstractCard errorCard = new VodafoneAbstractCard(getContext()) {
            @Override
            protected int setContent() {
                return 0;
            }
        };

        errorCard.addHeader(cardErrorLayout);
        containter.addView(errorCard);
    }

    @Override
    public void onStart() {
        super.onStart();
        makeAdobeRequest();
    }

    protected void makeAdobeRequest(){
        callForAdobeTarget(AdobePageNamesConstants.PG_Y_SERVICES);
    }
}


