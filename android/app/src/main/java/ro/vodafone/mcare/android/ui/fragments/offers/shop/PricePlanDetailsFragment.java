package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import io.realm.RealmObject;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.PricePlanPayCard;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.AdditionalBenefits;
import ro.vodafone.mcare.android.client.model.shop.ShopCartSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlan;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlanSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.PhoneShopWebViewActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ro.vodafone.mcare.android.ui.activities.offers.OffersActivity.DEEP_LINK_KEY;

/**
 * Created by Victor Radulescu on 4/4/2017.
 */

public class PricePlanDetailsFragment extends OffersFragment {

    private static final String TAG = PricePlanDetailsFragment.class.getSimpleName();

    RelativeLayout viewGroup;
    NavigationHeader navigationHeader;

    public static final String PRICE_PLAN_SKU_ID_KEY = "pricePlanSkuId";
    private final String ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS = "others";
    private final String ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED = "recomended";
    private static final String PRICE_PLAN_KEY = "pricePlanValue";


    ShopPricePlan shopPricePlan;

    String pricePlanSkuId;
    String shopSessionToken;

    private String oneUsageSerializedData;
    private String secondSerializedData;

    private boolean requestListingsInProgress = false;
    private boolean requestsRecomendedInProgress = false;

    private boolean requestListingsFailed = false;
    private boolean requestRecomendedFailed = false;
    private Semaphore semaphore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        semaphore = new Semaphore(1, true);
    }

    public PricePlanDetailsFragment() {
    }

    public static PricePlanDetailsFragment newInstance(String pricePlanSkuId) {
        Bundle bundle = new Bundle();
        bundle.putString("pricePlanSkuId",pricePlanSkuId);
        PricePlanDetailsFragment fragment = new PricePlanDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "create view");

        showLoadingDialog();

        setHeaderTitle();

        viewGroup = new RelativeLayout(getContext());
        viewGroup.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));
        viewGroup.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        oneUsageSerializedData = IntentActionName.RETENTION_PRICEPLAN_DETAILS.getOneUsageSerializedData();
        secondSerializedData = IntentActionName.RETENTION_PRICEPLAN_DETAILS.getOneUsageSecondSerializedData();

        if (oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
            pricePlanSkuId = secondSerializedData;
            checkShopEligibility();
        } else {
            setShopPricePlan();
            setHeaderText();
        }

        navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();

        try {
            ((OffersActivity) getActivity()).getToolbar().showToolBar();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (oneUsageSerializedData == null || !oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {

            /**workaround for a crash
             *do not remove!!!
             */
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (getActivity() != null) {
                                showDetailsCards();
                            }
                        }
                    });

        }

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);

        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        ServiceDetailsTrackingEvent event = new ServiceDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return viewGroup;
    }

    private void setHeaderTitle() {
        OffersActivity offersActivity = (OffersActivity) getActivity();
        offersActivity.getNavigationHeader().removeViewFromContainer();
        offersActivity.getNavigationHeader().hideSelectorView();
        offersActivity.getNavigationHeader().setTitle("Ofertă pentru tine");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((OffersActivity) getActivity()).scrolltoTop();
    }

    private void checkShopEligibility() {
        Observable<GeneralResponse<ShopEligibilitySuccess>> observable = RetentionObservables
                .getInstance().getCheckUserEligibilityObservable();
        Subscription subscription = observable.subscribe(new RequestSessionObserver<GeneralResponse<ShopEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopEligibilitySuccess> generalResponse) {
                RequestSaveRealmObserver.save(generalResponse);
                if(ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
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
                    getRecomendedOffers();
                    getOffers();
                    return;
                }

                stopLoadingDialog();
                showError(null);
            }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                showError(null);
            }
        });
        addToActivityCompositeSubcription(subscription);
    }

    private void getOffers() {

        new ShopService(getContext()).getShopPricePlansListings(null,
                ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class)).getShopSessionToken()).subscribe(
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
                        requestListingsFailed =true;
                        if(!requestsInProgress() && requestRecomendedFailed)
                            showError(null);
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopPricePlanSuccess> response) {
                        try {
                            semaphore.acquire();
                            requestListingsInProgress = false;

                            if(response != null && response.getTransactionSuccess() != null
                                    && response.getTransactionSuccess().getPricePlansList() != null) {
                                RealmManager.startTransaction();
                                response.getTransactionSuccess().setId(ID_SHOP_PRICE_PLAN_SUCCESS_OTHERS);
                                RealmManager.commitTransaction();
                                super.onNext(response);
                                if (!requestsInProgress())
                                    processPricePlan();

                                semaphore.release();
                                return;
                            }
                            requestListingsFailed = true;
                            stopLoadingDialog();
                            if (!requestsInProgress() && requestRecomendedFailed)
                                showError(null);

                            semaphore.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
                        if(!requestListingsInProgress & requestListingsFailed)
                            showError(null);
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopPricePlanSuccess> response) {
                        try {
                            semaphore.acquire();
                            requestsRecomendedInProgress = false;

                            if (response!=null && response.getTransactionSuccess()!=null
                                    && response.getTransactionSuccess().getPricePlansList() != null) {
                                RealmManager.startTransaction();
                                response.getTransactionSuccess().setId(ID_SHOP_PRICE_PLAN_SUCCESS_RECOMANDED);
                                RealmManager.commitTransaction();
                                super.onNext(response);
                                if (!requestsInProgress())
                                    processPricePlan();

                                semaphore.release();
                                return;
                            }

                            requestRecomendedFailed =true;
                            if (!requestsInProgress() && requestListingsFailed)
                                showError(null);

                            semaphore.release();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                });
    }

    private void processPricePlan() {
        setShopPricePlan();
        if (shopPricePlan != null) {
            showDetailsCards();
            setHeaderText();
            return;
        }
        stopLoading();
        showError(RetentionLabels.getPricePlanNotInList());
    }

    private void showError(String message) {
        viewGroup.removeAllViews();
        if (getContext() != null) {
            VodafoneGenericCard errorCard = new VodafoneGenericCard(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            errorCard.setOnClickListener(message != null ? null : new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoadingDialog();
                    viewGroup.removeAllViews();
                    if (oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
                        checkShopEligibility();
                        return;
                    }
                    setShopPricePlan();
                    setHeaderText();
                }
            });
            viewGroup.addView(errorCard, layoutParams);
            errorCard.showError(true, message != null ? message :  AppLabels.getGenericRetryErrorMessage());
        }
    }

    private boolean requestsInProgress(){
        return requestsRecomendedInProgress || requestListingsInProgress;
    }

    private void setShopPricePlan() {
        pricePlanSkuId = getArguments().getString(PRICE_PLAN_SKU_ID_KEY);
        if (oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY))
            pricePlanSkuId = secondSerializedData;

        shopPricePlan = (ShopPricePlan) RealmManager.getRealmObjectAfterStringField(ShopPricePlan.class,PRICE_PLAN_SKU_ID_KEY,pricePlanSkuId);
    }
    private void setHeaderText() {
        OffersActivity offersActivity = (OffersActivity) getActivity();
        try{
            Log.d(TAG, "set header");

            int margins = ScreenMeasure.dpToPx(10);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            //linearLayout.setBackgroundResource(R.color.semi_transparent_background_color);

            LinearLayout.LayoutParams generalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams pricePeriodParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            generalParams.setMargins(margins,margins,margins,margins);
            pricePeriodParams.setMargins(margins,margins,margins,margins);

            linearLayout.setLayoutParams(generalParams);

            String name = shopPricePlan.getPricePlanDisplayName();
            VodafoneTextView nameTextView = new VodafoneTextView(getContext());
            nameTextView.setLayoutParams(generalParams);
            nameTextView.setText(name);
            nameTextView.setTextColor(Color.WHITE);
            nameTextView.setTextSize(21);
            nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.addView(nameTextView,0);

            View separatorLine = new View(getContext());
            separatorLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pay_bill_separator_line_color));
            LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(1));
            separatorParams.setMargins((2*margins), margins, (2*margins), margins);
            separatorLine.setLayoutParams(separatorParams);
            linearLayout.addView(separatorLine, 1);

            SpannableStringBuilder priceTextSpannable = getFormatPhonePrice(shopPricePlan.getDiscountedPrice(),shopPricePlan.getPricePlanMothlyFee());
            String period = shopPricePlan.getPricePlanContractPeriod()!=null? String.valueOf(shopPricePlan.getPricePlanContractPeriod()):" ";

            if(priceTextSpannable!=null && period!=null){
                String extraInfo = priceTextSpannable + " €" +" pe lună / " +period+" luni";
                VodafoneTextView pricePeriodTextView = new VodafoneTextView(getContext());
                pricePeriodTextView.setText(extraInfo);
                pricePeriodTextView.setTextColor(Color.WHITE);
                pricePeriodTextView.setTextSize(15);
                pricePeriodTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                pricePeriodTextView.setLayoutParams(pricePeriodParams);
                linearLayout.addView(pricePeriodTextView, 2);
            }
            ((OffersActivity) getActivity()).getNavigationHeader().addViewToContainer(linearLayout);
            ((OffersActivity) getActivity()).getNavigationHeader().showBannerView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private SpannableStringBuilder getFormatPhonePrice(Float discountedPrice, Float bundlePrice) {

        if(discountedPrice==null && bundlePrice==null){
            return null;
        }

        float discountedPricePrimitive = discountedPrice!=null?discountedPrice:0;
        float bundlePricePrimitive = bundlePrice!=null?bundlePrice:0;
        String bundlePriceText = new DecimalFormat("0.00").format(bundlePricePrimitive);
        String discountedPriceText = new DecimalFormat("0.00").format(discountedPricePrimitive);

        bundlePriceText = bundlePriceText.replaceAll(",", ".");
        discountedPriceText = discountedPriceText.replaceAll(",", ".");

        if(discountedPrice==null && bundlePrice!=null){
            return new SpannableStringBuilder(bundlePriceText);
        }

        if(discountedPricePrimitive < bundlePricePrimitive){
            String finalText = bundlePriceText+ " "+discountedPriceText;

            SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

            StrikethroughSpan strykeSpannable = new StrikethroughSpan();
            sb.setSpan(strykeSpannable, finalText.indexOf(bundlePriceText), bundlePriceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            return sb;
        }
        return new SpannableStringBuilder(bundlePriceText);
    }

    private void showDetailsCards(){
        addDetailsServiceCard();
        addExtraDetailsServiceCard();

        showRetentionEligibleForPhoneAndService(shopPricePlan.getEligibleWithDevice(),shopPricePlan.getEligibleSimOnly());
        stopLoadingDialog();
    }

    private void addDetailsServiceCard(){
        if(shopPricePlan==null){
            return;
        }

        RealmList<AdditionalBenefits> additionalBenefits = shopPricePlan.getAdditionalBenefits();


        ServiceCard serviceCard2 = new ServiceCard(getContext())
                .setDetailsFromHtml(shopPricePlan.getPricePlanBenefits(), false, false)
                .setGridImages(additionalBenefits)
                .hideMainTextViews()
                .hideArrow();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        serviceCard2.setId(10);
        serviceCard2.setLayoutParams(layoutParams);
        viewGroup.addView(serviceCard2,0);
        navigationHeader.getParent().requestChildFocus(navigationHeader, navigationHeader);
    }

    private void addExtraDetailsServiceCard(){
        if(shopPricePlan==null){
            return;
        }

        if (shopPricePlan.getPricePlanDetailsHtml() != null) {
            ServiceCard serviceCard2 = new ServiceCard(getContext())
                    .setDetailsFromHtml(shopPricePlan.getPricePlanDetailsHtml(), false, true)
                    .hideMainTextViews()
                    .hideSpacer()
                    .hideArrow();
            serviceCard2.setStartPadding(5);

            serviceCard2.addCardTitle("Detalii Abonament", R.color.purple_title_color, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, 10);
//            layoutParams.addRule(RelativeLayout.ABOVE, 30);
            serviceCard2.setLayoutParams(layoutParams);
            serviceCard2.setId(20);
            viewGroup.addView(serviceCard2, 1);
            navigationHeader.getParent().requestChildFocus(navigationHeader, navigationHeader);
        }
    }


    private void showRetentionEligibleForPhoneAndService(boolean showPhoneEligible,boolean showServiceEligible){
        if(!showPhoneEligible && !showServiceEligible){
            return;
        }
        PricePlanPayCard eligibleForPhoneAndService =
                new PricePlanPayCard(getContext()).setBackground(R.color.blackNormal);

        if(showServiceEligible){
            if(showPhoneEligible){
                eligibleForPhoneAndService.setSecondaryButtonMessage(RetentionLabels.getRetentionAddPhone());
                Bundle bundle= new Bundle();
                bundle.putString(ShopProduct.PRICE_PLAN_SKU_ID,pricePlanSkuId);
                eligibleForPhoneAndService.setSecondaryButtonClickListener(
                        new RedirectFragmentListener(getActivity(),PhonesFragment.newInstance(bundle))
                        {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                //Tealium Track Event
                                Map<String, Object> tealiumMapEvent = new HashMap(6);
                                tealiumMapEvent.put("screen_name","retention");
                                tealiumMapEvent.put("event_name","mcare:retention:" + pricePlanSkuId + ":adauga telefon");
                                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                                TealiumHelper.trackEvent("event_name", tealiumMapEvent);
                            }
                        }
                );
            }
            eligibleForPhoneAndService.setPrimaryButtonMessage(RetentionLabels.getRetentionAddIn());
            Bundle bundle= new Bundle();
            bundle.putString(ShopProduct.PRICE_PLAN_SKU_ID,pricePlanSkuId);
            eligibleForPhoneAndService.setPrimaryButtonClickListener(primaryClickListenerPayCard);
        }else if(showPhoneEligible){
            Bundle bundle= new Bundle();
            bundle.putString(ShopProduct.PRICE_PLAN_SKU_ID,pricePlanSkuId);
            eligibleForPhoneAndService.setPrimaryButtonMessage(RetentionLabels.getRetentionAddPhone());
            eligibleForPhoneAndService.setPrimaryButtonClickListener(
                    new RedirectFragmentListener(getActivity(),PhonesFragment.newInstance(bundle))
                    {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            //Tealium Track Event
                            Map<String, Object> tealiumMapEvent = new HashMap(6);
                            tealiumMapEvent.put("screen_name","retention");
                            tealiumMapEvent.put("event_name","mcare:retention:" + pricePlanSkuId + ":adauga telefon");
                            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                            TealiumHelper.trackEvent("event_name", tealiumMapEvent);
                        }
                    }
            );

        }
        eligibleForPhoneAndService.removePaddings();
        eligibleForPhoneAndService.build();
        eligibleForPhoneAndService.setCardViewBackground(R.color.blackNormal);

        ViewCompat.setElevation(eligibleForPhoneAndService,0);
        eligibleForPhoneAndService.setPadding(0,0,0,0);
        eligibleForPhoneAndService.setBackgroundResource(R.color.blackNormal);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ScreenMeasure.dpToPx(10);
        layoutParams.setMarginStart(0);
        layoutParams.leftMargin=0;
        layoutParams.rightMargin=0;
        layoutParams.setMarginEnd(0);
        layoutParams.addRule(RelativeLayout.BELOW, 20);
        eligibleForPhoneAndService.setId(30);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        eligibleForPhoneAndService.setLayoutParams(layoutParams);

        addCard(eligibleForPhoneAndService);
    }


    private void addCard(PricePlanPayCard card){
        viewGroup.addView(card);
        navigationHeader.getParent().requestChildFocus(navigationHeader, navigationHeader);
    }

    private void shopLogin() {

        D.w("in shopLogin");

        new ShopService(getContext()).postShopLogin(null, null)
                .doOnNext(new Action1<GeneralResponse<? extends RealmObject>>(){
                    @Override
                    public void call(GeneralResponse<? extends RealmObject> shopLoginGeneralResponse) {
                        shopSessionToken = ((ShopLoginSuccess) shopLoginGeneralResponse.getTransactionSuccess()).getShopSessionToken();
                        RealmManager.update(shopLoginGeneralResponse.getTransactionSuccess());
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
                        stopLoading();
                    }
                })
                .flatMap(new Func1<GeneralResponse<? extends RealmObject>, Observable<GeneralResponse<? extends RealmObject>>>() {
                    @Override
                    public Observable<GeneralResponse<? extends RealmObject>> call( GeneralResponse<? extends RealmObject>  shopLoginSuccessGeneralResponse) {
                        if(shopSessionToken != null){
                            return (new ShopService(getContext()).putShopCart(shopPricePlan.getPhoneSkuId(),
                                    shopPricePlan.getPricePlanSkuId(),
                                    shopPricePlan.getProductId(),
                                    shopPricePlan.getCfgSkuId(),
                                    shopSessionToken))
                                    .doOnNext(new Action1<GeneralResponse<? extends RealmObject>>() {
                                        @Override
                                        public void call(GeneralResponse<? extends RealmObject> shopCartSuccessGeneralResponse) {

                                        }
                                    });
                        } else {
                            stopLoading();
                            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
                            return null;
                        }

                    }
                }).subscribe(new RequestSessionObserver<GeneralResponse<? extends RealmObject>>() {
            @Override
            public void onNext(GeneralResponse<? extends RealmObject> generalResponse) {
                if(generalResponse.getTransactionStatus() != 0){
                    stopLoading();
                    new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
                }
                else {
                    if(getActivity() == null) {
                        return;
                    }

                    String url = ((ShopCartSuccess)generalResponse.getTransactionSuccess()).getShoppingCartUrl();

                    ((ShopCartSuccess)generalResponse.getTransactionSuccess()).getShopCookieName();///shop/mcareui/cart/mcareShoppingCart.jsp

                    AddPricePlanToCartTrackingEvent event = new AddPricePlanToCartTrackingEvent();
                    VodafoneController.getInstance().getTrackingService().trackCustom(event);

                    String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

                    FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
                    firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
                    firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("pp_retention_MSISDN", msisdn);
                    firebaseAnalyticsItem.addFirebaseAnalyticsParams("pp_retention_pp_name",
                            shopPricePlan.getPricePlanDisplayName(), "pp_retention_skuid", shopPricePlan.getPricePlanSkuId(),
                            "pp_retention_xxEUR", shopPricePlan.getPricePlanMothlyFee() + "EUR");

                    Intent intent = new Intent(getContext(), PhoneShopWebViewActivity.class);
                    intent.putExtra(WebviewActivity.KEY_URL, url);
                    intent.putExtra(PRICE_PLAN_KEY, firebaseAnalyticsItem);
                    getActivity().startActivityForResult(intent, 10000);
                    stopLoading();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
                stopLoading();
            }
        });


    }

    private void putInCartWithLoading(){
        shopLogin();
        startLoading();
    }


    private void startLoading(){
        showLoadingDialog();
    }


    @Override
    public String getTitle() {
        return "Ofertă pentru tine";
    }

    private void stopLoading(){
        stopLoadingDialog();
    }

    private View.OnClickListener primaryClickListenerPayCard= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

           /* //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","retention");
            tealiumMapEvent.put("event_name","mcare:retention:" + pricePlanSkuId + ":adauga in cos");
            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

            putInCartWithLoading();
        }
    };
    @Override
    public void onDetach() {
        super.onDetach();
        OffersActivity offersActivity = (OffersActivity) getActivity();
        offersActivity.getNavigationHeader().removeViewFromContainer();
        offersActivity.getNavigationHeader().hideBannerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_PRICEPLAN_NAME.concat(pricePlanSkuId));
    }

    public class ServiceDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "mcare:"+"retention:" + pricePlanSkuId;
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:" + pricePlanSkuId);
            s.prop5 = "sales:priceplan details";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:"+"retention:" + pricePlanSkuId;
            s.getContextData().put("prop21", s.prop21);
        }
    }

    public class AddPricePlanToCartTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }

            s.events = "scAdd";
            s.getContextData().put("scAdd", s.scAdd);

            s.events = "scOpen";
            s.getContextData().put("scOpen", s.scOpen);
        }
    }
}
