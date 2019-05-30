package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.adapters.shop.RetentionKeepOffersAdapter;
import ro.vodafone.mcare.android.client.adapters.shop.RetentionRecommendedOffersAdapter;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.shop.ShopCurrentSelection;
import ro.vodafone.mcare.android.client.model.shop.RecommendedPricePlan;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
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
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Victor Radulescu on 4/5/2017.
 */

public class PricePlansOtherFragment extends OffersFragment implements ExpandableTextViewGroup.ExpandableTextViewGroupListener{

    private static final String TAG = PricePlansOtherFragment.class.getSimpleName();

    private final String TITLE_SERVICE_RECOMANDED = "Abonament Recomandat";
    private final String TITLE_SERVICE_OTHERS = "Alte abonamente";
    private final String TITLE_SERVICE_CURRENT = "Cu abonamentul tău actual";

    private final String ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS = "others";
    private final String ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED = "recomended";

    @BindView(R.id.card_container)
    LinearLayout card_container;

    @BindView(R.id.spinner)
    VodafoneSpinner spinner;

    @BindView(R.id.filter_button)
    Button filterButton;

    PercentRelativeLayout viewGroup;

    String phoneSkuId;
    String pricePlanSkuId;

    private static final String SORT_PRICE_PLAN_CONTRACT_PERIOD_TAG = "pricePlanContractPeriod";
    private static final String SORT_PRICE_PLAN_MONTHLY_FEE_TAG = "pricePlanMothlyFee";

    private static final int FILTER_REQUEST_KEY = 52;

    private RealmList<BalanceShowAndNotShown> aggregatedBenefitsList;

    private ExpandableTextViewGroup expandableTextViewGroup;
    public PricePlansOtherFragment() {
    }



    public static PricePlansOtherFragment newInstance(Bundle args) {

        PricePlansOtherFragment fragment = new PricePlansOtherFragment();
        fragment.phoneSkuId =args.getString(ShopProduct.PHONE_SKU_ID);
        fragment.pricePlanSkuId =args.getString(ShopProduct.PRICE_PLAN_SKU_ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "create view");

        viewGroup = (PercentRelativeLayout) inflater.inflate(R.layout.fragment_retention_services,null);
        ButterKnife.bind(this,viewGroup);
        setupHeader();
        setupFilterButton();
        addCardWithDelayForPerformanceIssues();

        getActiveOffersPostpaid();

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);

        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        ServicesOtherTrackingEvent event = new ServicesOtherTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return viewGroup;
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

        intent.putExtra(ServicesFilterActivity.ELIGIBLE_SIM_ONLY,true);
        intent.putExtra(ServicesFilterActivity.ELIGIBLE_WITH_DEVICE, false);
        startActivityForResult(intent,FILTER_REQUEST_KEY);
    }

    private void setupHeader(){
        try {
            OffersActivity offersActivity = (OffersActivity) getActivity();
            offersActivity.getNavigationHeader().removeViewFromContainer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupFilterButton(){
        spinner.setVisibility(View.GONE);
    }

    //added a 0.5 seconds delay to prevent a crash on some devices
    //DO NOT REMOVE THIS!!!!!
    private void addCardWithDelayForPerformanceIssues(){
        if(getActivity()==null){
            return;
        }
        showLoadingDialog();

        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        addCards();
                    }
                });
    }


    private void showError(){
        if(getActivity()==null){
            return;
        }
        card_container.removeAllViews();
        GeneralCardsWithTitleBodyAndTwoButtons errorCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card_container.addView(errorCard,layoutParams);

        errorCard.showError();
    }

    private void addCards(){
        if(getActivity()==null){
            return;
        }
        card_container.removeAllViews();

        ShopProduct shopProduct = (ShopProduct) RealmManager.getRealmObjectAfterStringField(ShopProduct.class,ShopProduct.PHONE_SKU_ID,phoneSkuId);
        RecommendedPricePlan recommendedPricePlan = shopProduct.getRecommendedPricePlans().where().equalTo(RecommendedPricePlan.ELIGIBLE_WITH_DEVICE, true).findFirst();
        if(shopProduct!=null){
            addRecommendedServiceCard(shopProduct, recommendedPricePlan);
            addOthersAndCurrentServiceCards(shopProduct);
        }else{
            stopLoadingDialog();
            showError();
            hideFilter();
        }
    }

    private void addRecommendedServiceCard(ShopProduct shopProduct, RecommendedPricePlan recommendedPricePlan){
        if(getActivity()==null){
            return;
        }
        if(recommendedPricePlan==null){
            return;
        }
        Float monthlyFee = getSmallestPrice(recommendedPricePlan.getBundlePrice(),
                recommendedPricePlan.getDiscountedPrice());
        String formattedPrice = getAfterPhonePriceText(monthlyFee);
        formattedPrice = formattedPrice.replaceAll(",", ".");
        final String recommendedPricePlanSkuId = recommendedPricePlan.getPricePlanSkuId();

        ServiceCard serviceCard = (ServiceCard) new ServiceCard(getContext())
                .setTitle(recommendedPricePlan.getPricePlanDisplayName())
                .setDetailsFromHtml(recommendedPricePlan.getPricePlanBenefits(),false, false)
                .setPrice(formattedPrice)
                .setPeriod(getAfterPhonePeriodText(recommendedPricePlan.getPricePlanMothlyFee(),
                        recommendedPricePlan.getPricePlanContractPeriod() ) )
                .hideSpacer()
                .hideArrow()
                .addCardTitle("Abonament recomandat",R.color.purple_title_color, null);
        if(shopProduct.getPricePlanSkuId().equalsIgnoreCase(recommendedPricePlan.getPricePlanSkuId())){
            serviceCard.addSelectedButton("Abonament selectat", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        setCurrentShopInfo(recommendedPricePlanSkuId);
                        ((OffersActivity) getContext()).onBackPressed();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else{
            serviceCard.addUnselectedButton("Selectează abonament",  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        setCurrentShopInfo(recommendedPricePlanSkuId);
                        ((OffersActivity) getContext()).onBackPressed();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            });
        }

        serviceCard.setTag(TITLE_SERVICE_RECOMANDED);
        card_container.addView(serviceCard);

    }
    private void addOthersAndCurrentServiceCards(ShopProduct shopProduct) {
        if(getActivity()==null){
            return;
        }
        if(!areAnyOffers(shopProduct)){
            stopLoadingDialog();
            return;
        }
        setupOffers(shopProduct);
    }

    private boolean areAnyOffers(ShopProduct shopProduct) {
        return shopProduct!=null && shopProduct.getRecommendedPricePlans().isValid() && !shopProduct.getRecommendedPricePlans().isEmpty();
    }

    @Override
    public String getTitle() {
        return "Ofertă pentru tine";
    }
    @Override
    public void onExpandableTextViewGroupMeasured(int height) {
        if(card_container!=null){
            ((PercentRelativeLayout.LayoutParams)card_container.getLayoutParams()).bottomMargin = height;
            card_container.invalidate();
        }
    }

    private void setupOffers(ShopProduct shopProduct){

        if(shopProduct!=null && shopProduct.getRecommendedPricePlans()!=null){

            List<RecommendedPricePlan> keepList = getKeepList(shopProduct.getRecommendedPricePlans());
            List<RecommendedPricePlan> otherList = getNotKeepList(shopProduct.getRecommendedPricePlans());
            addCardListOthers(otherList,TITLE_SERVICE_OTHERS, shopProduct.getPricePlanSkuId());
            addCardListKeep(keepList,TITLE_SERVICE_CURRENT,shopProduct.getPricePlanSkuId());
            boolean[] resultPeriod = ServicesFilterActivity.getSavedInstancesPeriod();
            filterCards(resultPeriod);
            stopLoadingDialog();
        }
    }

    private List<RecommendedPricePlan> getKeepList(RealmList<RecommendedPricePlan> recommendedPricePlans){
        RealmResults<RecommendedPricePlan> auxRecommendedPricePlan = recommendedPricePlans
                .where()
                .notEqualTo(RecommendedPricePlan.ELIGIBLE_WITH_DEVICE, true)
                .findAll();

         return auxRecommendedPricePlan.where().contains(
                RecommendedPricePlan.PRICE_PLAN_SKUID,
                RecommendedPricePlan.PRICE_PLAN_SKUID_KEEP_ID).findAll();
    }

    private List<RecommendedPricePlan> getNotKeepList(RealmList<RecommendedPricePlan> recommendedPricePlans){
        String[] sortOptions = {SORT_PRICE_PLAN_CONTRACT_PERIOD_TAG, SORT_PRICE_PLAN_MONTHLY_FEE_TAG};
        Sort[] sortOrders = {Sort.DESCENDING, Sort.ASCENDING};
        List<RecommendedPricePlan> auxRecommendedPricePlan = recommendedPricePlans.sort(sortOptions, sortOrders);

        List<RecommendedPricePlan> otherList = new ArrayList<>();
        for (RecommendedPricePlan recommendedPricePlan:auxRecommendedPricePlan) {
            if(!recommendedPricePlan.isEligibleWithDevice()) {
                if (!recommendedPricePlan.getPricePlanSkuId().contains(RecommendedPricePlan.PRICE_PLAN_SKUID_KEEP_ID)) {
                    otherList.add(recommendedPricePlan);
                }
            }
        }

        return otherList;
    }

    private void addCardListOthers(List<RecommendedPricePlan> pricePlansList, String title, String pricePlanSkuId) {
        if(getActivity()==null){
            return;
        }
        AdapterBackedLinearLayout cardListView  = new AdapterBackedLinearLayout(getContext());
        cardListView.setTag(title);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        cardListView.setLayoutParams(layoutParams);
        RetentionRecommendedOffersAdapter retentionOffersAdapter = new RetentionRecommendedOffersAdapter(getActivity(),
                pricePlansList,title, pricePlanSkuId);
        cardListView.setAdapter(retentionOffersAdapter);

        card_container.addView(cardListView);
    }
    private void addCardListKeep(List<RecommendedPricePlan> pricePlansList, String title, String pricePlanSkuId) {
//
//        for (RecommendedPricePlan pricePlan: pricePlansList) {
//            Log.d(TAG, pricePlan.getPricePlanSkuId());
//        }
//
//        for (RecommendedPricePlan pricePlan: pricePlanListKeep) {
//            Log.d(TAG, pricePlan.getPricePlanSkuId());
//        }
        if(getActivity()==null){
            return;
        }
        AdapterBackedLinearLayout cardListView  = new AdapterBackedLinearLayout(getContext());
        cardListView.setTag(title);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        cardListView.setLayoutParams(layoutParams);
        RetentionKeepOffersAdapter retentionOffersAdapter = new RetentionKeepOffersAdapter(getActivity(),
                pricePlansList, title, pricePlanSkuId);
        cardListView.setAdapter(retentionOffersAdapter);
        card_container.addView(cardListView);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILTER_REQUEST_KEY) {
            if(resultCode == Activity.RESULT_OK){
                boolean[] resultPeriod = data.getBooleanArrayExtra(ServicesFilterActivity.FILTER_RESULT_PERIOD);
                filterCards(resultPeriod);
            }
           /* if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }*/
        }
    }
    private void filterCards(boolean[] resultPeriod){
        try{
            filterRecommended(resultPeriod);
            ShopProduct shopProduct = (ShopProduct) RealmManager.getRealmObjectAfterStringField(
                    ShopProduct.class,
                    ShopProduct.PHONE_SKU_ID,
                    phoneSkuId);
            filterList(resultPeriod,getNotKeepList(shopProduct.getRecommendedPricePlans()),TITLE_SERVICE_OTHERS);
            filterKeep(resultPeriod, getKeepList(shopProduct.getRecommendedPricePlans()), TITLE_SERVICE_CURRENT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void filterList(boolean[] resultPeriod, List<RecommendedPricePlan> pricePlansList, String title){
        ShopProduct product= (ShopProduct) RealmManager.getRealmObjectAfterStringField(ShopProduct.class,ShopProduct.PHONE_SKU_ID,phoneSkuId);

        AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(title);
        List<RecommendedPricePlan> filteredPricePlanList = new ArrayList<>();
        for (RecommendedPricePlan shopPricePlan: pricePlansList) {
            boolean passedPeriod = (shopPricePlan.getPricePlanContractPeriod()==12 && resultPeriod[0]) ||
                    (shopPricePlan.getPricePlanContractPeriod()==24 && resultPeriod[1]);

            if(passedPeriod){
                filteredPricePlanList.add(shopPricePlan);
            }
        }
        RetentionRecommendedOffersAdapter retentionOffersAdapter = new RetentionRecommendedOffersAdapter(getActivity(), filteredPricePlanList, title, product.getPricePlanSkuId());
        otherScrollAble.setAdapter(retentionOffersAdapter);
    }
    private void filterKeep(boolean[] resultPeriod, List<RecommendedPricePlan> pricePlansList, String title){
        AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(title);
        ShopProduct product= (ShopProduct) RealmManager.getRealmObjectAfterStringField(ShopProduct.class,ShopProduct.PHONE_SKU_ID,phoneSkuId);



        RealmList<RecommendedPricePlan> realmList = new RealmList<>();
        realmList.addAll(pricePlansList);

        List<RecommendedPricePlan> filteredPricePlanList = new ArrayList<>();

        for (RecommendedPricePlan shopPricePlan: realmList) {
            boolean passedPeriod = (shopPricePlan.getPricePlanContractPeriod()==24 && resultPeriod[1]);

            if(passedPeriod){
                filteredPricePlanList.add(shopPricePlan);
            }
        }

        for (RecommendedPricePlan shopPricePlan: realmList) {
            boolean passedPeriod = (shopPricePlan.getPricePlanContractPeriod()==12 && resultPeriod[0]);

            if(passedPeriod){
                filteredPricePlanList.add(shopPricePlan);
            }
        }

        RetentionKeepOffersAdapter retentionOffersAdapter = new RetentionKeepOffersAdapter(getActivity(), filteredPricePlanList, title,product.getPricePlanSkuId());
        otherScrollAble.setAdapter(retentionOffersAdapter);
    }

    private void filterRecommended(boolean[] resultPeriod){
        View serviceCard = viewGroup.findViewWithTag(TITLE_SERVICE_RECOMANDED);
        ShopProduct product= (ShopProduct) RealmManager.getRealmObjectAfterStringField(ShopProduct.class,ShopProduct.PHONE_SKU_ID,phoneSkuId);
        if(product==null || product.isKeep()){
            return;
        }
        boolean passedPeriod = (product.getPricePlanContractPeriod()==12 && resultPeriod[0]) ||
                (product.getPricePlanContractPeriod()==24 && resultPeriod[1]);

        if(passedPeriod){
            serviceCard.setVisibility(View.VISIBLE);
        }else{
            serviceCard.setVisibility(View.GONE);
        }
    }

    private void hideFilter(){
        filterButton.setVisibility(View.GONE);
    }
    void showFilter(){
        filterButton.setVisibility(View.VISIBLE);
    }

    private boolean isFromCurrentServiceOffer(String pricePlanSkuId){
        return pricePlanSkuId!=null && pricePlanSkuId.contains("keep");
    }
    private String getAfterPhonePriceText(Float price){
        if(price==null){
            return "";
        }
        String priceFormatted = "Preţ telefon "+new DecimalFormat("0.00").format(price)+" € ";
        priceFormatted = priceFormatted.replaceAll(",", ".");
        return priceFormatted;
    }
    private Float getSmallestPrice(Float fullPrice,Float discount){
        return fullPrice>discount?discount:fullPrice;
    }

    private SpannableStringBuilder getAfterPhonePeriodText(Float monthlyFee, Integer period){
        if(monthlyFee==null){
            return new SpannableStringBuilder("");
        }
        String priceText = new DecimalFormat("0.00").format(monthlyFee)+"€ pe lună";
        priceText = priceText.replaceAll(",", ".");
        period = period/12;
        String finalText;
        if(period <= 1) {
            finalText = priceText + " cu contract pe " + period + " an";
        }
        else {
            finalText = priceText + " cu contract pe " + period + " ani";
        }
        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 0, priceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
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

    private void getActiveOffersPostpaid(){
        if(! (VodafoneController.getInstance().getUser() instanceof PostPaidUser)){
            return;
        }
//        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
//        String sid = VodafoneController.getInstance().getUserProfile().getSid();
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
        String sid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
        new OffersService(getContext()).getActiveOffersPostpaid(msisdn,sid,"").subscribe(new RequestSessionObserver<GeneralResponse<ActiveOffersPostpaidSuccess>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(GeneralResponse<ActiveOffersPostpaidSuccess> response) {
                //super.onNext(response);
                if(response!=null && response.getTransactionSuccess()!=null && response.getTransactionSuccess().getPricePlan()!=null){
                    ActiveOfferPostpaid activeOfferPostpaid=  response.getTransactionSuccess().getPricePlan();
                    RealmList<ActiveOfferPostpaid> otherOffers =  response.getTransactionSuccess().getActiveOffersList();

                    CostControl currentCostControl = (CostControl) RealmManager.getRealmObject(CostControl.class);
                    if(currentCostControl.getCurrentExtraoptions().getShortBalanceList() != null && currentCostControl.getCurrentExtraoptions().getShortBalanceList().size() > 0){
                        setupExpandableView(activeOfferPostpaid,currentCostControl);
                    }
                }
            }
        });
    }

    private void setupExpandableView(ActiveOfferPostpaid activeOfferPostpaid, CostControl currentCostControl){
        if(getActivity()==null){
            return;
        }
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
        if (getActivity() == null) {
            return;
        }
        if (currentCostControl == null) {
            return;
        }

        aggregatedBenefitsList = currentCostControl.getCurrentExtraoptions().getExtendedBalanceList();
        RealmResults<BalanceShowAndNotShown> sortedAggregatedBenefitsList = aggregatedBenefitsList.sort("amountTypeIdString", Sort.DESCENDING);

        int noBenefitsDisplayed = 0;
        for (int i = 0; i < currentCostControl.getCurrentExtraoptions().getExtendedBalanceList().size(); i++) {
            if (noBenefitsDisplayed == 4) {
                break;
            }

            Log.d(TAG, "Benefit: " + sortedAggregatedBenefitsList.get(i).getNameRO());
            Log.d(TAG, "Amount: " + sortedAggregatedBenefitsList.get(i).getRemainingAmount());
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

    private ExpandableTextViewGroup addToBottomExpandableTextViewGroup(){
        ExpandableTextViewGroup expandableTextViewGroup = new ExpandableTextViewGroup(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.BOTTOM;
        FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_layout_container);
        frameLayout.addView(expandableTextViewGroup,layoutParams);
        expandableTextViewGroup.setVisibility(View.VISIBLE);
        return expandableTextViewGroup;
    }

    private void setCurrentShopInfo(String pricePlanSkuId){
        RealmManager.startTransaction();
        ShopCurrentSelection shopCurrentSelection = new ShopCurrentSelection();
        shopCurrentSelection.setPricePlanSkuId(pricePlanSkuId);
        RealmManager.update(shopCurrentSelection);
    }

    private void setTealiumForTrackingFilterEvent() {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:other priceplans:button:filtre");
        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_PRICEPLANS_OTH);
    }

    public static class ServicesOtherTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:other priceplans";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:other priceplans");


            s.prop5 = "sales:all priceplans";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
