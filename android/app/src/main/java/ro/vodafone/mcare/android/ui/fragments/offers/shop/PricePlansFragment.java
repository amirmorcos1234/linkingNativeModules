package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.adapters.shop.RetentionOffersAdapter;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlan;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlanSuccess;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.filters.ServicesFilterActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.utils.AmountUnitUtils;
import ro.vodafone.mcare.android.ui.utils.CompatView;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.expandabales.ExpandableTextViewGroup;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.comparators.ShopPricePlanComparator;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.activities.offers.OffersActivity.DEEP_LINK_KEY;

/**
 * Created by Victor Radulescu on 3/24/2017.
 */

public class PricePlansFragment extends OffersFragment implements VodafoneSpinner.Callback,ExpandableTextViewGroup.ExpandableTextViewGroupListener{

    private static final String TAG = PricePlansFragment.class.getSimpleName();

    public static final String SORT_AFTER_PRICE_RISING = "Preţ crescător";
    public static final String SORT_AFTER_PRICE_DESCENDING = "Preţ descrescător";

    private final String TITLE_SERVICE_RECOMANDED ="Abonament Recomandat";
    private final String TITLE_SERVICE_OTHERS="Alte abonamente";
    private final String TITLE_SERVICE_OTHERS_WITHOUT_PHONE="Abonamente fără telefon";

    public final String ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS = "others";
    private final String ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED = "recomended";

    @BindView(R.id.card_container)
    LinearLayout card_container;

    @BindView(R.id.spinner)
    VodafoneSpinner spinner;

    @BindView(R.id.filter_button)
    Button filterButton;

    PercentRelativeLayout viewGroup;

    private boolean requestListingsInProgress = false;
    private boolean requestsRecomendedInProgress = false;

    private boolean requestListingsFailed =false;
    private boolean requestRecomendedFailed =false;

    VodafoneGenericCard errorCard;

    private final int FILTER_REQUEST_KEY = 55;
    private RealmList<BalanceShowAndNotShown> aggregatedBenefitsList;

    private ExpandableTextViewGroup expandableTextViewGroup;
    private String oneUsageSerializedData;
    public PricePlansFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "create view");

        viewGroup = (PercentRelativeLayout) inflater.inflate(R.layout.fragment_retention_services,null);
        ButterKnife.bind(this,viewGroup);
        setupHeader();
        showLoadingDialog();

        oneUsageSerializedData = IntentActionName.RETENTION_PRICEPLANS_LISTING.getOneUsageSerializedData();
        if (oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY))
            checkShopEligibility();
        else
            requestData();

        registerSpinner();
        getActiveOffersPostpaid();
        hideFilter();

        settealiumTrackingEvent();

        return viewGroup;
    }

    private void settealiumTrackingEvent()
    {
        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        ServicesTrackingEvent event = new ServicesTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilter();
                filterButton.setOnClickListener(null);
            }
        });
    }

    public void openFilter(){
        setTealiumForTrackingFilterEvent();

        Intent intent = new Intent(getActivity().getApplicationContext(), ServicesFilterActivity.class);
        ShopEligibilitySuccess shopEligibilitySuccess = (ShopEligibilitySuccess) RealmManager.getRealmObject(ShopEligibilitySuccess.class);
        if (shopEligibilitySuccess != null) {
            intent.putExtra(ServicesFilterActivity.ELIGIBLE_SIM_ONLY, shopEligibilitySuccess.getAllowPricePlans());
            intent.putExtra(ServicesFilterActivity.ELIGIBLE_WITH_DEVICE, shopEligibilitySuccess.getAllowPhones());
        }
        startActivityForResult(intent,FILTER_REQUEST_KEY);
    }

    private void registerSpinner(){
        spinner.setCallback(this);
        final List<String> dataset = new LinkedList<>(Arrays.asList(SORT_AFTER_PRICE_RISING, SORT_AFTER_PRICE_DESCENDING));

        VodafoneSpinnerAdapter adapter = new VodafoneSpinnerAdapter(getContext(), dataset, R.drawable.selector);
        spinner.setAdapter(adapter);
        spinner.setText("Sortare după preţ");
    }

    private void checkShopEligibility() {
        Observable<GeneralResponse<ShopEligibilitySuccess>> observable = RetentionObservables
                .getInstance().getCheckUserEligibilityObservable();
        showLoadingDialog();
        Subscription subscription = observable.subscribe(new RequestSessionObserver<GeneralResponse<ShopEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopEligibilitySuccess> generalResponse) {
                RequestSaveRealmObserver.save(generalResponse);
                if (ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
                    if (!generalResponse.getTransactionSuccess().getAllowPricePlans()) {
                        stopLoadingDialog();
                        showError(RetentionLabels.getNotEligiblePricePlan());
                        return;
                    }
                    if (!generalResponse.getTransactionSuccess().getIsShoppingCartEmpty()) {
                        stopLoadingDialog();
                        showError(RetentionLabels.getRetentionShoppingCartEmptyFalseBody());
                        return;
                    }
                    requestsRecomendedInProgress = true;
                    requestListingsInProgress = true;
                    getOffers();
                } else {
                    stopLoadingDialog();
                    showError(null);
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                showError(null);
            }
        });
        addToActivityCompositeSubcription(subscription);
    }

    private void getOffers(){

        getRecomendedOffers();
        new ShopService(getContext()).getShopPricePlansListings(null,
                ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class))
                        .getShopSessionToken()).subscribe(
                        new RequestSaveRealmObserver<GeneralResponse<ShopPricePlanSuccess>>() {
            @Override
            public void onCompleted() {
                requestListingsInProgress = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                requestListingsInProgress = false;
                hideFilter();
                requestListingsFailed =true;
                if(!requestsInProgress() && requestRecomendedFailed){
                    showError(null);
                }
            }

            @Override
            public void onNext(GeneralResponse<ShopPricePlanSuccess> response) {
                requestListingsInProgress = false;
                if(response!=null && response.getTransactionSuccess()!=null && response.getTransactionSuccess().getPricePlansList()!=null){
                    RealmManager.startTransaction();
                    response.getTransactionSuccess().setId(ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS);
                    RealmManager.commitTransaction();
                    super.onNext(response);
                    if(!response.getTransactionSuccess().getPricePlansList().isEmpty()){
                        showFilter();
                        setupOtherCardsList(response.getTransactionSuccess().getPricePlansList());
                    } else {
                        stopLoadingDialog();
                    }

                    checkInitialFilters();

                }else{
                    requestListingsFailed =true;
                    stopLoadingDialog();
                    if(!requestsInProgress() && requestRecomendedFailed){
                        hideFilter();
                        showError(null);
                    }
                }
            }
        });
    }

    private void setupOtherCardsList(RealmList<ShopPricePlan> pricePlansList) {
        if(getActivity()==null){
            return;
        }
        addCardList(pricePlansList,TITLE_SERVICE_OTHERS,1);

        filterOthers(ServicesFilterActivity.getSavedInstancesType(),ServicesFilterActivity.getSavedInstancesPeriod());
    }

    private void setupRecommendedCardsList(RealmList<ShopPricePlan> pricePlansList) {
        if(getActivity()==null){
            return;
        }
        if(pricePlansList==null || !pricePlansList.isValid()){
            return;
        }
        addCardList(pricePlansList, TITLE_SERVICE_RECOMANDED,0);
        filterCardsRecommended(ServicesFilterActivity.getSavedInstancesType(),ServicesFilterActivity.getSavedInstancesPeriod());
    }

    private void getRecomendedOffers(){
        new ShopService(getContext()).getShopPricePlansRecommended(null,
                ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class))
                        .getShopSessionToken()).subscribe(
                new RequestSaveRealmObserver<GeneralResponse<ShopPricePlanSuccess>>() {
                    @Override
                    public void onCompleted() {
                        requestsRecomendedInProgress = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        requestRecomendedFailed =true;
                        requestsRecomendedInProgress = false;
                        if(!requestListingsInProgress & requestListingsFailed){
                            showError(null);
                        }
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopPricePlanSuccess> response) {
                        requestsRecomendedInProgress = false;

                        if(response!=null && response.getTransactionSuccess()!=null){
                            RealmManager.startTransaction();
                            response.getTransactionSuccess().setId(ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED);
                            RealmManager.commitTransaction();
                            super.onNext(response);
                            showFilter();
                            setupRecommendedCardsList(response.getTransactionSuccess().getPricePlansList());

                            checkInitialFilters();
                        }else {
                            requestRecomendedFailed =true;
                            if(!requestsInProgress() && requestListingsFailed){
                                showError(null);
                                hideFilter();
                            }
                        }
                    }


                });
    }

    private void checkInitialFilters(){
        if(getActivity()==null){
            return;
        }
        if(!requestsRecomendedInProgress && !requestListingsInProgress){
            filterCards(ServicesFilterActivity.getSavedInstancesType(), ServicesFilterActivity.getSavedInstancesPeriod());
        }
    }

    private void showError(String message){
        card_container.removeAllViews();
        if (getContext() != null) {
            VodafoneGenericCard errorCard = new VodafoneGenericCard(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            card_container.addView(errorCard, layoutParams);
            errorCard.setOnClickListener(message != null ? null : new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    card_container.removeAllViews();
                    if (oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
                        checkShopEligibility();
                        return;
                    }
                    requestData();
                }
            });
            errorCard.showError(true, message != null ? message :  AppLabels.getGenericRetryErrorMessage());
        }
    }
    private void showError(String message,boolean removeAllViews){
        if(removeAllViews){
            card_container.removeAllViews();
        }
        if (getContext() != null) {
            errorCard = new VodafoneGenericCard(getContext());
            errorCard.setVisibility(View.VISIBLE);
            card_container.addView(errorCard);
            errorCard.showError(true, message);
        }
    }

    private void hideError(){
        if(errorCard != null){
            errorCard.setVisibility(View.GONE);
        }
    }

    private void addCardList(RealmList<ShopPricePlan> pricePlansList,String title,int index) {

        for (int i = 0; i < card_container.getChildCount(); i++) {
            if (!(card_container.getChildAt(i) instanceof AdapterBackedLinearLayout)) {
                card_container.removeViewAt(i);
                card_container.invalidate();
            }
        }
        if (getContext() != null) {
            AdapterBackedLinearLayout cardListView = new AdapterBackedLinearLayout(getContext());
            cardListView.setTag(title);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            cardListView.setLayoutParams(layoutParams);
            card_container.addView(cardListView, index < card_container.getChildCount() ? index : card_container.getChildCount());
            RetentionOffersAdapter retentionOffersAdapter = new RetentionOffersAdapter(getActivity(),
                    pricePlansList, title);
            cardListView.setAdapter(retentionOffersAdapter);

            if (!requestsInProgress()) {
                stopLoadingDialog();
            }
        }
    }

    private void setupCardListWithoutPhones(List<ShopPricePlan> pricePlansList,String title,int index){

        for (int i = 0; i < card_container.getChildCount(); i++) {
            if (!(card_container.getChildAt(i) instanceof AdapterBackedLinearLayout)) {
                card_container.removeViewAt(i);
                card_container.invalidate();
            }
        }
        if (getContext() != null) {
            if (pricePlansList.size() != 0) {

                AdapterBackedLinearLayout otherListViewWithoutPhone = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_OTHERS_WITHOUT_PHONE);
                card_container.removeView(otherListViewWithoutPhone);
                otherListViewWithoutPhone = otherListViewWithoutPhone != null ? otherListViewWithoutPhone : new AdapterBackedLinearLayout(getContext());
                otherListViewWithoutPhone.setTag(title);
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                otherListViewWithoutPhone.setLayoutParams(layoutParams);
                card_container.addView(otherListViewWithoutPhone, index < card_container.getChildCount() ? index : card_container.getChildCount());
                RetentionOffersAdapter retentionOffersAdapter = new RetentionOffersAdapter(getActivity(),
                        pricePlansList, title);
                otherListViewWithoutPhone.setAdapter(retentionOffersAdapter);
            } else {
                AdapterBackedLinearLayout otherListViewWithoutPhone = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_OTHERS_WITHOUT_PHONE);
                card_container.removeView(otherListViewWithoutPhone);
            }
        }

    }


    private void requestData(){
        requestsRecomendedInProgress = true;
        requestListingsInProgress = true;
        shopLogin();
    }

    private void shopLogin() {

        showLoadingDialog();
        D.w("in shopLogin");

        new ShopService(getContext()).postShopLogin(null, null).subscribe(new RequestSessionObserver<GeneralResponse<ShopLoginSuccess>>() {
            @Override
            public void onCompleted() {
                D.w("completed shopLogin");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                D.w("error shopLogin");
                stopLoadingDialog();
                showError(null);
            }

            @Override
            public void onNext(GeneralResponse<ShopLoginSuccess> shopLoginSuccessGeneralResponse) {
                D.w("next shopLogin");

                if (shopLoginSuccessGeneralResponse != null && shopLoginSuccessGeneralResponse.getTransactionSuccess() != null) {
                    if (shopLoginSuccessGeneralResponse.getTransactionSuccess().getShopSessionToken() != null) {
                        RealmManager.update(shopLoginSuccessGeneralResponse.getTransactionSuccess());
                        getOffers();

                    } else {
                        stopLoadingDialog();
                        showError(null);

                    }
                } else {
                    stopLoadingDialog();
                    showError(null);
                }
            }
        });
    }

    private boolean requestsInProgress(){
        return requestsRecomendedInProgress|| requestListingsInProgress;
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        if(selectedValue instanceof String) {
            String selectedString = (String) selectedValue;
            if (selectedString.equals(SORT_AFTER_PRICE_RISING)) {
                spinner.setText(SORT_AFTER_PRICE_RISING);
                sortCards(true);

            }else if(selectedString.equals(SORT_AFTER_PRICE_DESCENDING)){
                spinner.setText(SORT_AFTER_PRICE_DESCENDING);

                sortCards(false);
            }
        }
    }

    private void getActiveOffersPostpaid() {

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
        String sid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();

        new OffersService(getContext()).getActiveOffersPostpaid(msisdn,sid,"").subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersPostpaidSuccess>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "active offers completed");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "active offers error");
            }

            @Override
            public void onNext(GeneralResponse<ActiveOffersPostpaidSuccess> response) {
                //super.onNext(response);
                Log.d(TAG, "active offers next");
                if(response!=null && response.getTransactionSuccess()!=null && response.getTransactionSuccess().getPricePlan()!=null){
                    ActiveOfferPostpaid activeOfferPostpaid=  response.getTransactionSuccess().getPricePlan();

                    CostControl currentCostControl = (CostControl) RealmManager.getRealmObject(CostControl.class);

                    if(currentCostControl.getCurrentExtraoptions().getExtendedBalanceList() != null &&
                            currentCostControl.getCurrentExtraoptions().getExtendedBalanceList().size() > 0){
                        setupExpandableView(activeOfferPostpaid,currentCostControl);
                    }

                }
            }
        });

    }

    private void setupExpandableView(ActiveOfferPostpaid activeOfferPostpaid, CostControl currentCostControl){
        String offerName = activeOfferPostpaid.getOfferName();
        if(expandableTextViewGroup==null){
            expandableTextViewGroup = addToBottomExpandableTextViewGroup();
        }
        expandableTextViewGroup.build(this);
        expandableTextViewGroup.setNameTextView(offerName);
        setExpandableTextViewGroupFirst4Offers(currentCostControl);
        CompatView.bringToFront(expandableTextViewGroup);
    }

    private void setExpandableTextViewGroupFirst4Offers(CostControl currentCostControl) {
        if (currentCostControl == null) {
            return;
        }

        aggregatedBenefitsList = currentCostControl.getCurrentExtraoptions().getExtendedBalanceList();
        RealmResults<BalanceShowAndNotShown> sortedAggregatedBenefitsList = aggregatedBenefitsList.sort("amountTypeIdString", Sort.DESCENDING);

        int noBenefitsDisplayed = 0;
        for (int i = 0; i < sortedAggregatedBenefitsList.size(); i++) {
            if (noBenefitsDisplayed == 4) {
                break;
            }

            Log.d(TAG, "Benefit: " + sortedAggregatedBenefitsList.get(i).getNameRO());
            Log.d(TAG, "Amount: " + sortedAggregatedBenefitsList.get(i).getRemainingAmount());
            Log.d(TAG, "AmountUnit: " + sortedAggregatedBenefitsList.get(i).getAmountUnitString());
            BalanceShowAndNotShown balance = sortedAggregatedBenefitsList.get(i);

            AmountUnitModel amountUnitModel = AmountUnitUtils
                    .getAmountUnitObject(balance,
                            balance.getRemainingAmount() != null
                                    ? Double.valueOf(balance.getRemainingAmount())
                                    : balance.getRemainingAmount());

            String benefitAmount = "";
            if (amountUnitModel.getAmount() != null) {
                benefitAmount = String.valueOf(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(),2));
                if(benefitAmount.endsWith(".00")) {
                    benefitAmount = benefitAmount.substring(0, benefitAmount.length()-3);
                }
            }

            expandableTextViewGroup.addToDetails(balance.getNameRO(), benefitAmount, amountUnitModel.getUnit(), true);
            noBenefitsDisplayed++;
        }
    }

    private ExpandableTextViewGroup addToBottomExpandableTextViewGroup() {
        ExpandableTextViewGroup expandableTextViewGroup = new ExpandableTextViewGroup(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.BOTTOM;

        FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_layout_container);
        expandableTextViewGroup.setGravity(Gravity.BOTTOM);
        frameLayout.addView(expandableTextViewGroup,layoutParams);
        expandableTextViewGroup.setVisibility(View.VISIBLE);
        return expandableTextViewGroup;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILTER_REQUEST_KEY) {
            if(resultCode == Activity.RESULT_OK){
                boolean[] resultType = data.getBooleanArrayExtra(ServicesFilterActivity.FILTER_RESULT_TYPE);
                boolean[] resultPeriod = data.getBooleanArrayExtra(ServicesFilterActivity.FILTER_RESULT_PERIOD);
                filterCards(resultType,resultPeriod);
            }
        }

        settealiumTrackingEvent();
    }

    private void filterCards(boolean[] resultType, boolean[] resultPeriod){
      boolean anyRecommended =  filterCardsRecommended(resultType,resultPeriod);
      boolean anyOthers = filterOthers(resultType,resultPeriod);
        if(!anyOthers && !anyRecommended){
            showError("Nu există abonamente conform selecţiei",false);
        } else {
            hideError();
        }
    }

    private boolean filterOthers(boolean[] resultType, boolean[] resultPeriod){
        try{
            AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_OTHERS);
            ShopPricePlanSuccess pricePlanSuccess = (ShopPricePlanSuccess) RealmManager.getRealmObjectAfterStringField(ShopPricePlanSuccess.class,ShopPricePlanSuccess.ID,ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS);

            String[] defaultSortingFields = {ShopPricePlan.PRICE_PLAN_CONTRACT_PERIOD, ShopPricePlan.PRICE_PLAN_MONTHLY_FEE};
            Sort[] defaultSortingOrders = {Sort.DESCENDING, Sort.ASCENDING};
            List<ShopPricePlan> pricePlansList = pricePlanSuccess.getPricePlansList().sort(defaultSortingFields, defaultSortingOrders);

            List<ShopPricePlan> filteredPricePlanList = new ArrayList<>();
            List<ShopPricePlan> filteredWithoutPhonePricePlanList = new ArrayList<>();
            for (ShopPricePlan shopPricePlan: pricePlansList) {
                boolean passedPeriod = (shopPricePlan.getPricePlanContractPeriod()==12 && resultPeriod[0]) ||
                        (shopPricePlan.getPricePlanContractPeriod()==24 && resultPeriod[1]);
                boolean passedType = false;
                boolean belongToTypeWithoutPhone =false;

                boolean eligibleBoth = shopPricePlan.getEligibleSimOnly() && shopPricePlan.getEligibleWithDevice();
                boolean withPhoneSelected = resultType == null || resultType[0];
                boolean withNoPhoneSelected =resultType == null ||  resultType[1];
                boolean bothTypesSelected = withPhoneSelected && withNoPhoneSelected;

                if(resultType==null){
                    passedType = true;
                    if(!shopPricePlan.getEligibleWithDevice() && shopPricePlan.getEligibleSimOnly()){
                        belongToTypeWithoutPhone =true;
                    }
                }else{
                    passedType = (eligibleBoth && bothTypesSelected) ||
                            (withPhoneSelected && shopPricePlan.getEligibleWithDevice()) ||
                            (withNoPhoneSelected && shopPricePlan.getEligibleSimOnly() && !shopPricePlan.getEligibleWithDevice());
                    if(withNoPhoneSelected && !shopPricePlan.getEligibleWithDevice() && shopPricePlan.getEligibleSimOnly()){
                        belongToTypeWithoutPhone =true;
                        passedType=true;
                    }
                }

                if(shopPricePlan.getEligibleSimOnly() || shopPricePlan.getEligibleWithDevice()) {
                    if (passedPeriod && passedType) {
                        if (belongToTypeWithoutPhone) {
                            filteredWithoutPhonePricePlanList.add(shopPricePlan);
                        } else {
                            filteredPricePlanList.add(shopPricePlan);
                        }
                    }
                }
            }

            setupCardListWithoutPhones(filteredWithoutPhonePricePlanList,TITLE_SERVICE_OTHERS_WITHOUT_PHONE,2);

            if(filteredPricePlanList.size() == 0 && filteredWithoutPhonePricePlanList.size() == 0){
                otherScrollAble.setVisibility(View.GONE);
                return false;
            }

            RetentionOffersAdapter retentionOffersAdapter = new RetentionOffersAdapter(getActivity(), filteredPricePlanList, TITLE_SERVICE_OTHERS);
            otherScrollAble.setAdapter(retentionOffersAdapter);

            String selectedString = spinner.getText().toString();
            Log.d(TAG, selectedString);
            if (selectedString.equals(SORT_AFTER_PRICE_RISING)) {
                spinner.setText(SORT_AFTER_PRICE_RISING);
                sortCards(true);

            }else if(selectedString.equals(SORT_AFTER_PRICE_DESCENDING)){
                spinner.setText(SORT_AFTER_PRICE_DESCENDING);
                sortCards(false);
            }

            if(! (filteredPricePlanList.isEmpty()&& filteredWithoutPhonePricePlanList.isEmpty())){
                otherScrollAble.setVisibility(View.VISIBLE);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean filterCardsRecommended(boolean[] resultType, boolean[] resultPeriod){
        try{
            AdapterBackedLinearLayout recommendedScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_RECOMANDED);
            ShopPricePlanSuccess pricePlanSuccess = (ShopPricePlanSuccess) RealmManager.getRealmObjectAfterStringField(ShopPricePlanSuccess.class,ShopPricePlanSuccess.ID,ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED);
            List<ShopPricePlan> pricePlansList = pricePlanSuccess.getPricePlansList();


            for (ShopPricePlan shopPricePlan: pricePlansList) {
                boolean passedPeriod = (shopPricePlan.getPricePlanContractPeriod()==12 && resultPeriod[0]) ||
                        (shopPricePlan.getPricePlanContractPeriod()==24 && resultPeriod[1]);

                boolean eligibleBoth = shopPricePlan.getEligibleSimOnly() && shopPricePlan.getEligibleWithDevice();
                boolean withPhoneSelected = resultType == null || resultType[0];
                boolean withNoPhoneSelected =resultType == null ||  resultType[1];
                boolean bothTypesSelected = withPhoneSelected && withNoPhoneSelected;

                boolean passType = true;

                if(!shopPricePlan.getEligibleSimOnly() && !shopPricePlan.getEligibleWithDevice()) {
                    recommendedScrollAble.setVisibility(View.GONE);
                    return false;
                }

                    if(resultType!=null){
                    passType= (eligibleBoth && bothTypesSelected) ||
                            (withPhoneSelected && shopPricePlan.getEligibleWithDevice()) ||
                            (withNoPhoneSelected && shopPricePlan.getEligibleSimOnly() && !shopPricePlan.getEligibleWithDevice());
                }

                if(passedPeriod &&  passType){
                    recommendedScrollAble.setVisibility(View.VISIBLE);
                    return true;
                }else{
                    recommendedScrollAble.setVisibility(View.GONE);
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private void sortCards(final boolean ascending){
        try{
            AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_OTHERS);
            List<ShopPricePlan> shopPricePlanList= ((RetentionOffersAdapter)otherScrollAble.getAdapter()).getPricePlanList();
            Collections.sort(shopPricePlanList,new ShopPricePlanComparator(ascending));
            RetentionOffersAdapter retentionOffersAdapter = new RetentionOffersAdapter(getActivity(), shopPricePlanList, TITLE_SERVICE_OTHERS);
            otherScrollAble.setAdapter(retentionOffersAdapter);

            AdapterBackedLinearLayout withoutPhoneScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_SERVICE_OTHERS_WITHOUT_PHONE);
            List<ShopPricePlan> withoutPhoneshopPricePlanList= ((RetentionOffersAdapter)withoutPhoneScrollAble.getAdapter()).getPricePlanList();
            Collections.sort(withoutPhoneshopPricePlanList,new ShopPricePlanComparator(ascending));
            RetentionOffersAdapter withoutPhoneRetentionOffersAdapter = new RetentionOffersAdapter(getActivity(), withoutPhoneshopPricePlanList, TITLE_SERVICE_OTHERS_WITHOUT_PHONE);
            withoutPhoneScrollAble.setAdapter(withoutPhoneRetentionOffersAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hideFilter(){
        if(getActivity()==null){
            return;
        }
        spinner.setVisibility(View.GONE);
        filterButton.setVisibility(View.GONE);
    }

    private void showFilter(){
        if(getActivity()==null){
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.VISIBLE);
    }


    private void hideExpandableTextViewGroup(){
        if(expandableTextViewGroup==null){
            return;
        }
        expandableTextViewGroup.setVisibility(View.GONE);
    }

    @Override
    public void onExpandableTextViewGroupMeasured(int height) {
        Log.d("ExpandableTextView","ExpandableTextViewGroupMeasured height "+height);

    }

    @Override
    public String getTitle() {
        return "Ofertă pentru tine";
    }

    private void setupHeader(){
        try {
            OffersActivity offersActivity = (OffersActivity) getActivity();
            offersActivity.getNavigationHeader().removeViewFromContainer();
            offersActivity.getNavigationHeader().hideSelectorView();
            offersActivity.getNavigationHeader().setTitle(getTitle());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{
            FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_layout_container);

            frameLayout.removeView(expandableTextViewGroup);
            expandableTextViewGroup = null;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTealiumForTrackingFilterEvent() {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:all priceplans:button:filtre");
        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_PRICEPLAN_ALL);
    }

    public static class ServicesTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:all priceplans";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:all priceplans");


            s.prop5 = "sales:all priceplans";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "buy services";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
