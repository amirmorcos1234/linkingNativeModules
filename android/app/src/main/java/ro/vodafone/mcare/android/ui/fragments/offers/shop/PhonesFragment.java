package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.adapters.shop.PhoneOffersAdapter;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.filters.PhonesFilterActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.comparators.ShopProductComparator;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import rx.Observable;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.activities.offers.OffersActivity.DEEP_LINK_KEY;

/**
 * Created by Victor Radulescu on 3/30/2017.
 */

public class PhonesFragment extends OffersFragment implements VodafoneSpinner.Callback {

    private static final String TAG = "PhonesFragment";


    public static final String SORT_AFTER_PRICE_RISING = "Preţ crescător";
    public static final String SORT_AFTER_PRICE_DESCENDING = "Preţ descrescător";

    public static final int FILTER_REQUEST_KEY =54;

    private final String TITLE_PHONES_RECOMANDED ="Telefon Recomandat";
    private final String TITLE_PHONES_OTHERS="Telefoane promoționale";

    private final String ID_SHOP_PRODUCT_SUCCESS_OTHERS = "others";
    private final String ID_SHOP_PRODUCT_SUCCESS_RECOMANDED = "recomended";

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

    private ArrayList<String> allBrands;
    private String pricePlanSkuId = "";
    private String oneUsageSerializedData;

    public PhonesFragment() {
    }

    public static PhonesFragment newInstance(Bundle args) {

        PhonesFragment fragment = new PhonesFragment();
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

        setTealiumTrackingEvent();

        oneUsageSerializedData = IntentActionName.RETENTION_PHONES_LISTING.getOneUsageSerializedData();
        if(oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY))
            checkShopEligibility();
        else
            requestData();

        setupHeader();
        hideFilter();
        registerSpinner();

        return viewGroup;
    }

    private void setTealiumTrackingEvent()
    {
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        PhonesTrackingEvent event = new PhonesTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
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
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_PHONES);
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

    @Override
    public void onStop() {
        super.onStop();
        try{
            stopLoading();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openFilter(){
        setTealiumForTrackingFilterEvent();

        if(allBrands==null || allBrands.isEmpty()){
            return;
        }

        Intent intent = new Intent(getContext(), PhonesFilterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(PhonesFilterActivity.FILTER_INPUT_BRANDS, allBrands);
        startActivityForResult(intent, FILTER_REQUEST_KEY);
    }

    private void registerSpinner(){
        spinner.setCallback(this);
        final List<String> dataset = new LinkedList<>(Arrays.asList(SORT_AFTER_PRICE_RISING,SORT_AFTER_PRICE_DESCENDING));

        VodafoneSpinnerAdapter adapter = new VodafoneSpinnerAdapter(getContext(), dataset, R.drawable.selector);
        spinner.setAdapter(adapter);
        spinner.setText(SettingsLabels.getPhonesSpinner());
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

    private void sortCards(final boolean ascending) {
        try{
            AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_PHONES_OTHERS);
            List<ShopProduct> shopProductList= ((PhoneOffersAdapter)otherScrollAble.getAdapter()).getShopProducts();
            Collections.sort(shopProductList,new ShopProductComparator(ascending));

            PhoneOffersAdapter retentionOffersAdapter = new PhoneOffersAdapter(getContext(), shopProductList, TITLE_PHONES_OTHERS);
            otherScrollAble.setAdapter(retentionOffersAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Float getSmallestPrice(Float bundlePrice,Float discountedPrice){
        return bundlePrice>=discountedPrice? bundlePrice:discountedPrice;
    }


    private void getOtherPhones(){
        ShopLoginSuccess shopLoginSuccess = (ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class);
        String shopSessionToken = shopLoginSuccess.getShopSessionToken();
        Log.d(TAG, shopSessionToken);

        new ShopService(getContext()).getShopProductsListings(pricePlanSkuId!=null?pricePlanSkuId:"",
                shopSessionToken).subscribe(
                new RequestSaveRealmObserver<GeneralResponse<ShopProductsSuccess>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideFilter();
                        requestListingsInProgress = false;
                        requestListingsFailed = true;
                        if(!requestsInProgress() & requestRecomendedFailed){
                            showError(null);
                        }
                        stopLoadingIfRequestsCompleted();
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopProductsSuccess> response) {

                        requestListingsInProgress = false;
                        stopLoadingIfRequestsCompleted();


                        if(response != null && response.getTransactionStatus() == 2){
                            requestListingsFailed = true;
                            if(!requestsInProgress() & requestRecomendedFailed){
                                showError(null);
                            }
                        }
                        if(response!=null && response.getTransactionSuccess()!=null){
                            if(response.getTransactionSuccess().getProductsList().size() == 0){
                                hideFilter();
                                requestListingsFailed = true;
                                if(!requestsInProgress() && requestRecomendedFailed) {
                                    showError("Nu există telefoane disponibile.", true);
                                }
                            }
                            else {
                                RealmManager.startTransaction();
                                response.getTransactionSuccess().setId(ID_SHOP_PRODUCT_SUCCESS_OTHERS);
                                RealmManager.commitTransaction();
                                super.onNext(response);
                                if (!requestsInProgress()) {
                                    showFilter();
                                    addCardList(response.getTransactionSuccess().getProductsList(), TITLE_PHONES_OTHERS);
                                    filterDefaultOthers();
                                }
                            }
                        }else{
                            hideFilter();
                            requestListingsFailed = true;
                            if(!requestsInProgress() & requestRecomendedFailed){
                                showError("Nu există telefoane disponibile.",true);
                            }
                        }
                    }
                });
    }

    private void getRecommendedPhones(){
        new ShopService(getContext()).getShopProductsRecommended(pricePlanSkuId!=null?pricePlanSkuId:"",
                ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class))
                        .getShopSessionToken()).subscribe(
                new RequestSaveRealmObserver<GeneralResponse<ShopProductsSuccess>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        requestsRecomendedInProgress = false;
                        requestRecomendedFailed =true;
                        if(!requestsInProgress() & requestListingsFailed){
                            showError(null);
                        }
                        stopLoadingIfRequestsCompleted();
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopProductsSuccess> response) {

                        requestsRecomendedInProgress = false;
                        stopLoadingIfRequestsCompleted();

                        if(response != null && response.getTransactionStatus() == 2){
                            requestRecomendedFailed = true;
                            if(!requestsInProgress() & requestListingsFailed){
                                showError(null);
                            }
                            stopLoadingIfRequestsCompleted();
                        }
                        else
                        if(response!=null && response.getTransactionSuccess()!=null){
                            if(response.getTransactionSuccess().getProductsList().size() == 0){
                                requestRecomendedFailed = true;

                                if(!requestsInProgress() && requestListingsFailed)
                                    showError("Nu există telefoane disponibile.",true);
                            } else {
                                RealmManager.startTransaction();
                                response.getTransactionSuccess().setId(ID_SHOP_PRODUCT_SUCCESS_RECOMANDED);
                                RealmManager.commitTransaction();
                                super.onNext(response);
                                addCardList(response.getTransactionSuccess().getProductsList(), TITLE_PHONES_RECOMANDED);
                                filterDefaultRecommended();
                            }
                        } else {
                            requestRecomendedFailed = true;
                            if(!requestsInProgress() & requestListingsFailed){
                                showError("Nu există telefoane disponibile.",true);
                            }
                        }
                    }
                });
    }

    private void shopLogin() {

        showLoading();
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
                stopLoading();
                showError(null);
            }

            @Override
            public void onNext(GeneralResponse<ShopLoginSuccess> shopLoginSuccessGeneralResponse) {
                D.w("next shopLogin");

                if (shopLoginSuccessGeneralResponse != null && shopLoginSuccessGeneralResponse.getTransactionSuccess() != null) {
                    if (shopLoginSuccessGeneralResponse.getTransactionSuccess().getShopSessionToken() != null) {
                        RealmManager.update(shopLoginSuccessGeneralResponse.getTransactionSuccess());

                        getRecommendedPhones();
                        getOtherPhones();

                    } else {
                        stopLoading();
                        showError(null);

                    }
                } else {
                    stopLoading();
                    showError(null);
                }
            }
        });
    }

    private void showLoading(){
        showLoadingDialog();
    }


    private void showError(String message) {
        if(getActivity()==null){
            return;
        }
        card_container.removeAllViews();
        VodafoneGenericCard errorCard = new VodafoneGenericCard(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        errorCard.showError(true, message != null ? message :  AppLabels.getGenericRetryErrorMessage());
        errorCard.setOnClickListener(message != null ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_container.removeAllViews();
                if(oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
                    checkShopEligibility();
                    return;
                }
                requestData();
            }
        });
        card_container.addView(errorCard,layoutParams);

        errorCard.showError(true);
    }

    private void showError(String message,boolean removeAllViews){
        if(getActivity()==null){
            return;
        }
        if(removeAllViews){
            card_container.removeAllViews();
        }

        VodafoneGenericCard errorCard = new VodafoneGenericCard(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card_container.addView(errorCard,layoutParams);
        errorCard.setOnClickListener(null);
        errorCard.showError(true, message);
    }


    private void requestData(){
        requestsRecomendedInProgress = true;
        requestListingsInProgress = true;
        showLoadingDialog();
        shopLogin();
    }

    private void addCardList(RealmList<ShopProduct> pricePlansList, String title) {
        if(getActivity()==null){
            return;
        }
        setupBrands(pricePlansList);
        try {
            for (int i = 0; i < card_container.getChildCount(); i++) {
                if (!(card_container.getChildAt(i) instanceof AdapterBackedLinearLayout)) {
                    card_container.removeViewAt(i);
                    card_container.invalidate();
                }
            }
            AdapterBackedLinearLayout cardListView = new AdapterBackedLinearLayout(getContext());
            card_container.addView(cardListView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            PhoneOffersAdapter retentionOffersAdapter = new PhoneOffersAdapter(getContext(), pricePlansList, title);
            cardListView.setTag(title);
            cardListView.setAdapter(retentionOffersAdapter);
            stopLoadingIfRequestsCompleted();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean requestsInProgress(){
        return requestsRecomendedInProgress || requestListingsInProgress;
    }


    private void hideFilter(){
        spinner.setVisibility(View.GONE);
        filterButton.setVisibility(View.GONE);
    }

    private void showFilter(){
        spinner.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_KEY) {
            if(resultCode == Activity.RESULT_OK){
                ArrayList<String> brands= data.getStringArrayListExtra(PhonesFilterActivity.FILTER_RESULT_BRANDS);
                //Log.d("PhoneResult",brands.toString());
                filterCards(brands);
                filterRecommended(brands);

            }
           /* if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }*/
        }
        setTealiumTrackingEvent();
    }

    private void filterDefaultRecommended(){
        if(PhonesFilterActivity.getSavedBrands()==null || PhonesFilterActivity.getSavedBrands().length<=0){
            return;
        }
        ArrayList<String> brands= new ArrayList<>(Arrays.asList(PhonesFilterActivity.getSavedBrands()));
        filterRecommended(brands);
    }

    private void filterDefaultOthers(){
        sortCards(true);
        if(PhonesFilterActivity.getSavedBrands()==null || PhonesFilterActivity.getSavedBrands().length<=0){
            return;
        }
        ArrayList<String> brands= new ArrayList<>(Arrays.asList(PhonesFilterActivity.getSavedBrands()));
        filterCards(brands);
    }

    private void filterCards(ArrayList<String> brands) {
        try{
            boolean showAll = brands==null;
            AdapterBackedLinearLayout otherScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_PHONES_OTHERS);
            ShopProductsSuccess shopProductSuccess = (ShopProductsSuccess) RealmManager.getRealmObjectAfterStringField(ShopProductsSuccess.class,ShopProductsSuccess.ID,ID_SHOP_PRODUCT_SUCCESS_OTHERS);
            List<ShopProduct> productsList = shopProductSuccess.getProductsList();

            List<ShopProduct> filteredProducts = new ArrayList<>();
            for (ShopProduct shopProduct: productsList) {
                if( showAll || ((shopProduct.getPhoneBrand()!=null && brands.contains(shopProduct.getPhoneBrand())))){
                    filteredProducts.add(shopProduct);
                }
            }
            PhoneOffersAdapter retentionOffersAdapter = new PhoneOffersAdapter(getActivity(), filteredProducts, TITLE_PHONES_OTHERS);
            otherScrollAble.setAdapter(retentionOffersAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }

        String sortType = spinner.getText().toString();
        if (sortType.equals(SORT_AFTER_PRICE_RISING)) {
            spinner.setText(SORT_AFTER_PRICE_RISING);
            sortCards(true);
        }else if(sortType.equals(SORT_AFTER_PRICE_DESCENDING)){
            spinner.setText(SORT_AFTER_PRICE_DESCENDING);
            sortCards(false);
        }
    }

    private void filterRecommended(ArrayList<String> brands){
        boolean showAll = brands==null;

        AdapterBackedLinearLayout recomendedScrollAble = (AdapterBackedLinearLayout) viewGroup.findViewWithTag(TITLE_PHONES_RECOMANDED);
        if(recomendedScrollAble==null){
            return;
        }

        ShopProductsSuccess shopProductSuccess = (ShopProductsSuccess) RealmManager.getRealmObjectAfterStringField(ShopProductsSuccess.class,ShopProductsSuccess.ID,ID_SHOP_PRODUCT_SUCCESS_RECOMANDED);
        List<ShopProduct> productsList = shopProductSuccess.getProductsList();
        List<ShopProduct> filteredProducts = new ArrayList<>();
        for (ShopProduct shopProduct: productsList) {
            if(  showAll || ((shopProduct.getPhoneBrand()!=null && brands.contains(shopProduct.getPhoneBrand())))   ){
                filteredProducts.add(shopProduct);
            }
        }
        if(filteredProducts.isEmpty()){
            recomendedScrollAble.setVisibility(View.GONE);
        }else{
            PhoneOffersAdapter retentionOffersAdapter = new PhoneOffersAdapter(getActivity(), filteredProducts, TITLE_PHONES_RECOMANDED);
            recomendedScrollAble.setAdapter(retentionOffersAdapter);
            recomendedScrollAble.setVisibility(View.VISIBLE);
        }

    }

    private void setupBrands(RealmList<ShopProduct> pricePlansList){
        allBrands =  allBrands==null?new ArrayList<String>():allBrands;
        for (ShopProduct shopProduct:
             pricePlansList) {
            if(shopProduct.getPhoneBrand()!=null && !allBrands.contains(shopProduct.getPhoneBrand())){
                allBrands.add(shopProduct.getPhoneBrand());
            }
        }
    }

    @Override
    public String getTitle() {
        return "Ofertă pentru tine";
    }

    private void stopLoadingIfRequestsCompleted() {
        if (requestsInProgress()) {
            return;
        }
        // shopRequestInProcess =false;
        stopLoading();
    }

    private void stopLoading(){
        if(getActivity()==null){
            return;
        }
        stopLoadingDialog();
    }

    private void checkShopEligibility() {
        Observable<GeneralResponse<ShopEligibilitySuccess>> observable = RetentionObservables
                .getInstance().getCheckUserEligibilityObservable();
        showLoading();
        Subscription subscription = observable.subscribe(new RequestSessionObserver<GeneralResponse<ShopEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopEligibilitySuccess> generalResponse) {
                RequestSaveRealmObserver.save(generalResponse);
                if(ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
                    if (!generalResponse.getTransactionSuccess().getAllowPhones()) {
                        stopLoadingDialog();
                        showError(RetentionLabels.getNotEligiblePhone());
                        return;
                    }
                    if (!generalResponse.getTransactionSuccess().getIsShoppingCartEmpty()) {
                        stopLoadingDialog();
                        showError(RetentionLabels.getRetentionShoppingCartEmptyFalseBody());
                        return;
                    }
                    requestsRecomendedInProgress = true;
                    requestListingsInProgress = true;
                    getRecommendedPhones();
                    getOtherPhones();
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

    private void setTealiumForTrackingFilterEvent() {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","retention");
        tealiumMapEvent.put("event_name","mcare:retention:all phones:button:filtre");
        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);
    }

    public static class PhonesTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:all phones";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:all phones");


            s.prop5 = "sales:phone listing";
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
