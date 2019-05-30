package ro.vodafone.mcare.android.ui.fragments.loyaltyMarket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tealium.library.Tealium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.loyaltyMarket.CenteredImageWithTextsCard;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.realmhelper.SortingOption;
import ro.vodafone.mcare.android.client.model.realmhelper.loyalty.LoyaltyFilterOptions;
import ro.vodafone.mcare.android.client.model.realmhelper.loyalty.LoyaltyVoucherListsOptions;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceCenteredImageWithTextsCardFactory;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceErrorCardFactory;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceSortAndFilterFactory;
import ro.vodafone.mcare.android.interfaces.fragment.base.FragmentLifeCycleListener;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.presenter.adapterelements.CardTabElement;
import ro.vodafone.mcare.android.presenter.adapterelements.ErrorCardElement;
import ro.vodafone.mcare.android.presenter.adapterelements.FilterAndSortElement;
import ro.vodafone.mcare.android.presenter.adapterelements.NavigationHeaderElement;
import ro.vodafone.mcare.android.presenter.adapterelements.ServiceCardElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.AdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.ViewStaticAdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.loyalty.VoucherCardElement;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.filters.loyalty.LoyaltyOffersFilterActivity;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyMarketActivity;
import ro.vodafone.mcare.android.ui.activities.store_locator.OverlapTabCardItemDecoration;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.TabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.TabMenu.TabAdapter;
import ro.vodafone.mcare.android.widget.TabMenu.TabCard;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.CARD_ERROR_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.FILTER_AND_SORT_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.HEADER_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.LIST_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.SERVICE_CARD_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.TAB_CARD_TYPE;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.CAMPAIGN_EXPIRY_DATE_KEY;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.IS_RESERVED_KEY;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.PARTNER_NAME_KEY;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.PRIORITY_KEY;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.VOUCHER_EXPIRY_DATE_KEY;
import static ro.vodafone.mcare.android.client.model.realmhelper.loyalty.LoyaltyVoucherListsOptions.DEFAULT_SELECTED_SORT_TYPE;

/**
 * Created by Victor Radulescu on 8/28/2017.
 */

public class LoyaltyMarketVoucherListingsFragment extends BaseFragment implements
        InterfaceCenteredImageWithTextsCardFactory,
        InterfaceErrorCardFactory,
        InterfaceSortAndFilterFactory,
        VodafoneSpinner.Callback {

    private static final String TAG = "LoyaltyMarketListings";

    private final String KEY_ERROR_NO_VDF_SUBSCRIPTION = "show_error_no_vdf";

    public final String SORT_AFTER_RELEVANCE = LoyaltyLabels.getLoyaltySortRelevance();
    public final String SORT_ALFABETICALLY = LoyaltyLabels.getLoyaltySortAlphabetic();
    public final String SORT_AFTER_EXPIRY_CAMPAIGN_DATE = LoyaltyLabels.getLoyaltySortExpiry();
    public static final int FILTER_REQUEST_KEY = 55;

    private static String TAB_FIRST_OPTION = null;
    private static final String TAB_SECOND_OPTION = LoyaltyLabels.getLoyaltyMyOffers();
    private static int lastTabSelected = 0;
    private static LoyaltyVoucherListsOptions promotionListingOptions;
    private static LoyaltyVoucherListsOptions reservedListingOptions;

    final List<String> sortPromotionAdapterSortingList = new LinkedList<>(Arrays.asList(SORT_AFTER_RELEVANCE, SORT_ALFABETICALLY, SORT_AFTER_EXPIRY_CAMPAIGN_DATE));
    final List<String> sortReservedAdapterSortingList = new LinkedList<>(Arrays.asList(SORT_AFTER_RELEVANCE, SORT_ALFABETICALLY));

    RecyclerView recyclerView;
    NavigationHeader navigationHeader;
    FragmentLifeCycleListener fragmentLifeCycleListener;
    OnRecycleScrollViewCreatedListener scrollViewCreatedListener;
    @Nullable
    @BindView(R.id.tab_container)
    TabCard tabCardView;
    @Nullable
    @BindView(R.id.spinner)
    VodafoneSpinner spinner;
    @Nullable
    @BindView(R.id.filter_button)
    Button filterButton;
    private RecyclerElementsAdapter recyclerElementsAdapter;
    private List currentList;
    private ArrayList<String> promotionsCategories;
    private ArrayList<String> reservedPromotionsCategories;

    private Long serverSysDate;
    private boolean showErrorNoVdfSub = false;

    private Realm realm;
    String treatmentSegment;
    String lpsSegment;

    public LoyaltyMarketVoucherListingsFragment() {
    }

    public static LoyaltyMarketVoucherListingsFragment newInstance() {
        lastTabSelected = 0;

        LoyaltyMarketVoucherListingsFragment fragment = new LoyaltyMarketVoucherListingsFragment();
        return fragment;
    }

    public static void resetOptions() {
        promotionListingOptions = null;
        reservedListingOptions = null;
    }

    public static LoyaltyVoucherListsOptions getPromotionListingOptions() {
        if (promotionListingOptions == null) {
            promotionListingOptions = new LoyaltyVoucherListsOptions("promotions");
        }
        return promotionListingOptions;
    }

    public static LoyaltyVoucherListsOptions getReservedListingOptions() {
        if (reservedListingOptions == null) {
            reservedListingOptions = new LoyaltyVoucherListsOptions("reservedPromotions");
        }
        return reservedListingOptions;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, null);
        ButterKnife.bind(this, recyclerView);
        realm = Realm.getDefaultInstance();

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty, TealiumConstants.loyalty_market_vouchers);
        triggerNonTelcoQuestionnaire();
        checkTabTypeFromBannerRedirect();

        Bundle bundle;
        if(savedInstanceState != null) {
            Log.d(TAG, "is savedInstance");
            bundle = savedInstanceState;
        } else {
            Log.d(TAG, "is normal bundle");
            bundle = getArguments();
        }

        if(bundle != null) {
            showErrorNoVdfSub = bundle.getBoolean(KEY_ERROR_NO_VDF_SUBSCRIPTION);
            Log.d(TAG, "show error " + showErrorNoVdfSub);

            if(showErrorNoVdfSub) {
                populateAdapterElementsError();
            } else {
                populateAdapterElements();
            }
        } else {
            populateAdapterElements();
        }

        return this.recyclerView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (scrollViewCreatedListener != null) {
            scrollViewCreatedListener.onRecycleScrollViewCreated(recyclerView);
        }

        Bundle bundle;
        if(savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }

        if(bundle != null) {
            showErrorNoVdfSub = bundle.getBoolean(KEY_ERROR_NO_VDF_SUBSCRIPTION);
            if(!showErrorNoVdfSub) {
                loadDataIfRequestsFinished();
            }
        } else {
            loadDataIfRequestsFinished();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            fragmentLifeCycleListener = (FragmentLifeCycleListener) context;
            scrollViewCreatedListener = (OnRecycleScrollViewCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentLifeCycleListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.stopScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ERROR_NO_VDF_SUBSCRIPTION, showErrorNoVdfSub);
    }

    private void checkTabTypeFromBannerRedirect(){
        String redirectType = null;
        try {
            redirectType = IntentActionName.LOYALTY_MARKET.getOneUsageSerializedData();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(redirectType == null){
            return;
        } else if(redirectType.equals("new_voucher_list")){
            lastTabSelected = 0;
        } else if(redirectType.equals("reserved_voucher_list")) {
            lastTabSelected = 1;
        }


    }

    private void loadDataIfRequestsFinished() {
        if (areRequestFinished()) {
            stopLoadingDialog();
            setupViews();
        } else {
            showLoadingDialog();
        }
    }

    private boolean areRequestFinished() {
        if (getActivity() != null) {
            return ((LoyaltyMarketActivity) getActivity()).isGetReservedVouchersFinished() &&
                    ((LoyaltyMarketActivity) getActivity()).isGetVouchersFinished();
        }

        return true;
    }

    public NavigationHeader getNavigationView() {
        if (navigationHeader == null && getContext() != null) {
            navigationHeader = new NavigationHeader(getContext());
            navigationHeader.setId(R.id.navigation_header);
            ButterKnife.bind(this, navigationHeader);
            navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            navigationHeader.displayDefaultHeader();
            setTitle();
        }
        setBackgroundViewColor(R.color.general_background_light_gray);
        return navigationHeader;
    }

    public TabCard getTabCardView() {

             if(getLpsSegment()!=null && AppConfiguration.getSuperRedKey().equalsIgnoreCase(getLpsSegment())) {
                 TAB_FIRST_OPTION = LoyaltyLabels.getLoyaltyExclusivOffers();
             }else
                 TAB_FIRST_OPTION = LoyaltyLabels.getLoyaltyNewOffers();

        if (tabCardView == null && getContext() != null) {
            tabCardView = new TabCard(getContext());

            ArrayList<String> tabArray = new ArrayList<>();
            tabArray.add(TAB_FIRST_OPTION);
            tabArray.add(TAB_SECOND_OPTION);

            TabAdapter adapter = new TabAdapter(getActivity(), tabArray, true);
            tabCardView.setAdapter(adapter);
            tabCardView.setPosition(lastTabSelected);
            tabCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));

            tabCardView.setOnItemClickListener(new TabAdapterOnItemClickListener(tabCardView) {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    super.onItemClick(adapterView, view, i, l);
                    setupTabCardsTrackingEvent(i);

                    //avoid extra work
                    if (lastTabSelected == i) {
                        return;
                    }
                    lastTabSelected = i;
                    setSpinnerAdapterAfterCurrentTab();
                    setupViews();
                }
            });
            tabCardView.setGreyMargins();
        }
        tabCardView.bringToFront();
        return tabCardView;
    }

    private void setSpinnerAdapterAfterCurrentTab() {
        if (spinner != null && filterButton!=null) {
            if(!AppConfiguration.isSorteaza_Displayed()){
                spinner.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams)filterButton.getLayoutParams());
                layoutParams.weight = 5;
                filterButton.setLayoutParams(layoutParams);
                spinner = null;
                return;
            }
            VodafoneSpinnerAdapter<String> adapter = new VodafoneSpinnerAdapter<>(getContext(), lastTabSelected == 0 ?
                    sortPromotionAdapterSortingList : sortReservedAdapterSortingList
                    , R.drawable.selector);
            spinner.setAdapter(adapter);
            spinner.setText(getCurrentSelectedSorting());
        }
    }

    private void showNoVouchersCard() {
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        elementHashMap.remove(LIST_TYPE);
        elementHashMap.put(RecyclerElementsAdapter.CARD_ERROR_TYPE, getNoVouchersElement());
        updateLastElementPosition();
        recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), elementHashMap);
        recyclerView.setAdapter(recyclerElementsAdapter);
    }

    public void setupNewOffers(List<Promotion> vouchers) {
        if (vouchers == null || vouchers.isEmpty()) {
            showNoVouchersCard();
            return;
        }   //  TODO : okso CR 1 - you can make new method for below code, increases readability
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        if (elementHashMap != null) {
            DynamicAdapterElement voucherCardElement = (DynamicAdapterElement) elementHashMap.get(LIST_TYPE);
            if (elementHashMap.get(CARD_ERROR_TYPE) != null) {
                elementHashMap.remove(CARD_ERROR_TYPE);
                recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), elementHashMap);
                recyclerView.setAdapter(recyclerElementsAdapter);
            }
            if (voucherCardElement != null && voucherCardElement instanceof VoucherCardElement) {
                voucherCardElement.setList(vouchers);
            } else {
                voucherCardElement = new VoucherCardElement(LIST_TYPE, 3, getContext(), vouchers);
                elementHashMap.put(LIST_TYPE, voucherCardElement);
                updateLastElementPosition();
            }
            recyclerElementsAdapter.notifyElementsDataChange();

        }
    }

    public void setupMyOffers(List<Promotion> reservedPromotions) {
        if (reservedPromotions == null || reservedPromotions.isEmpty()) {
            showNoVouchersCard();
            return;
        }   //  TODO : okso CR >2< - you can make new method for below code, increases readability - duplicate code for tag >1<
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        if (elementHashMap != null) {
            DynamicAdapterElement voucherCardElement = (DynamicAdapterElement) elementHashMap.get(LIST_TYPE);

            if (elementHashMap.get(CARD_ERROR_TYPE) != null) {
                elementHashMap.remove(CARD_ERROR_TYPE);
                recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), elementHashMap);
                recyclerView.setAdapter(recyclerElementsAdapter);
            }

            if (voucherCardElement != null && voucherCardElement instanceof VoucherCardElement) {
                voucherCardElement.setList(reservedPromotions);

            } else {
                voucherCardElement = new VoucherCardElement(LIST_TYPE, 3, getContext(), reservedPromotions);
                elementHashMap.put(LIST_TYPE, voucherCardElement);
                updateLastElementPosition();
            }

            recyclerElementsAdapter.notifyElementsDataChange();

        }

    }

    @Override
    public LinearLayout getSortAndFilterView() {
        @SuppressLint("RestrictedApi") LinearLayout linearLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout.element_filter_and_sort, null);
        filterButton = (Button) linearLayout.findViewById(R.id.filter_button);
        spinner = (VodafoneSpinner) linearLayout.findViewById(R.id.spinner);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));
        return linearLayout;
    }

    public NavigationHeaderElement getNavigationHeaderElement() {
        return new NavigationHeaderElement(HEADER_TYPE, 0, getNavigationView());
    }

    public CardTabElement getCardTabElement() {
        return new CardTabElement(TAB_CARD_TYPE, 1, getTabCardView());
    }

    public FilterAndSortElement getFilterAndSortElement() {
        LinearLayout linearLayout = getSortAndFilterView();
        setupFilterView();
        setupSortView();
        return new FilterAndSortElement(linearLayout, FILTER_AND_SORT_TYPE, 2);
    }

    public ServiceCardElement getServiceCardElement() {
        ServiceCard serviceCard = new ServiceCard(getContext());
        serviceCard.setSingleTitleText(LoyaltyLabels.getMarketCampaignTitle(), ScreenMeasure.dpToPx(80));
        serviceCard.setBackgroundResource(R.color.general_background_light_gray);
        ServiceCardElement serviceCardElement = new ServiceCardElement(serviceCard, SERVICE_CARD_TYPE, getListCount());

        serviceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_market_vouchers);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.loyalty_campaign);
                if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                    tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

                TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

                initTCTrackingEvent();
                if (fragmentLifeCycleListener != null) {
                    String url = LoyaltyLabels.getCampaignDetailsActivity();
                    IntentActionName.WEBVIEW.setOneUsageSerializedData(url);
                    new NavigationAction(getContext(), IntentActionName.WEBVIEW).startAction();
                }
            }
        });
        return serviceCardElement;
    }

    public void updateLastElementPosition() {
        try {
            HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
            elementHashMap.get(SERVICE_CARD_TYPE).setOrder(getPositionForServiceCard());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateAdapterElementsError() {
        if (getContext() == null) {
            return;
        }
        HashMap<Integer, AdapterElement> hashMap = new HashMap<>();
        hashMap.put(HEADER_TYPE, getNavigationHeaderElement());
        initializeAdapter(hashMap);
        String errorMessage = LoyaltyLabels.getLoyaltyNoVdfSubscriptionErrorMessage();
        showErrorCard(errorMessage);
    }

    public void populateAdapterElements() {
        if (getContext() == null) {
            return;
        }
        HashMap<Integer, AdapterElement> hashMap = new HashMap<>();
        hashMap.put(HEADER_TYPE, getNavigationHeaderElement());
        hashMap.put(TAB_CARD_TYPE, getCardTabElement());
        hashMap.put(FILTER_AND_SORT_TYPE, getFilterAndSortElement());
        hashMap.put(SERVICE_CARD_TYPE, getServiceCardElement());
        initializeAdapter(hashMap);
    }

    private void initializeAdapter(HashMap<Integer, AdapterElement> hashMap) {
        recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), hashMap);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(recyclerElementsAdapter);
        recyclerView.addItemDecoration(new OverlapTabCardItemDecoration());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    void setBackgroundViewColor(final int color) {
        if (getActivity() != null && navigationHeader != null) {
            ViewTreeObserver vto = navigationHeader.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (navigationHeader.getViewTreeObserver().isAlive()) {
                        // only need to calculate once, so remove listener
                        navigationHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if(getActivity()==null){
                        return;
                    }
                    if(getActivity() instanceof LoyaltyMarketActivity){
                        boolean present =((LoyaltyMarketActivity)getActivity()).isVoucherCurrentPresent(LoyaltyMarketVoucherListingsFragment.this);
                        if(!present){
                            return;
                        }
                    }
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (ViewUtils.getWindowHeight() - navigationHeader.getHeight()));
                    lp.height = (ViewUtils.getWindowHeight() - navigationHeader.getHeight());
                    lp.gravity = Gravity.BOTTOM;
                    LinearLayout backgroundView = (LinearLayout) getActivity().findViewById(R.id.background_view);
                    backgroundView.setGravity(Gravity.BOTTOM);
                    backgroundView.setLayoutParams(lp);
                    backgroundView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
                    D.w();
                }
            });
        }
    }

     private String getLpsSegment() {

        LoyaltySegmentSuccess loyaltySegment = (LoyaltySegmentSuccess) RealmManager.getRealmObject(realm, LoyaltySegmentSuccess.class);

        if (loyaltySegment != null) {
            lpsSegment = loyaltySegment.getLpsSegment();
        }
        return lpsSegment;

    }

    private void setTitle() {
        if (navigationHeader != null) {
            if (AppConfiguration.getSuperRedKey().equalsIgnoreCase(getLpsSegment()) )
                navigationHeader.setTitle(LoyaltyLabels.getLoyaltyMarketSuperRedTitle());
            else
            navigationHeader.setTitle(LoyaltyLabels.getLoyaltyMarketTitle());
        }
    }

    @Override
    public void setupSortView() {
        if (spinner == null) {
            return;
        }

        spinner.setCallback(this);
        spinner.setTextColor(Color.BLACK);
        setSpinnerAdapterAfterCurrentTab();
    }

    @Override
    public void setupFilterView() {
        if (filterButton != null) {
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupFilterTrackingEvent();
                    openFilter(v);
                    filterButton.setOnClickListener(null);
                }
            });
        }
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        if (selectedValue instanceof String) {
            setupSelectSortOptionTrackingEvent();

            String selectedSortType = (String) selectedValue;
            SortingOption sortingOption = null;
            if (SORT_AFTER_RELEVANCE.equals(selectedSortType)) {
                sortingOption = getSortingOptionAfterRelevancePriority();
            } else if (SORT_ALFABETICALLY.equals(selectedSortType)) {
                sortingOption = getSortedOptionAsAlphabeticallyPriority();
            } else if (SORT_AFTER_EXPIRY_CAMPAIGN_DATE.equals(selectedSortType)) {
                sortingOption = getSortingOptionAfterExpiredDatePriority();
            } else {
                return;
            }

            updateCurrentSortingOptions(selectedSortType, sortingOption);
            filterAndSort(getFilteredQuery(), sortingOption);

            if (spinner != null) {
                spinner.setText(selectedSortType);
            }
        }
    }

    private void filterAndSort(@Nullable RealmQuery filteredQuery, @Nullable SortingOption sortingOption) {
        if (tabCardView == null) {
            return;
        }

        if (filteredQuery != null) {
            if (tabCardView.getSelectedTabIndex() == 0) {
                filteredQuery = filteredQuery.greaterThan(Promotion.CAMPAIGN_EXPIRY_DATE_KEY, serverSysDate)
                                            .greaterThan(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate);
            }
            if (isDefaultSortingOptionForMyOffers()) {
                setupListForMyOffersPromotionsSortedWithRelevance(getFilteredQuery(), getFilteredQuery());
            } else if (sortingOption != null) {
                currentList = filteredQuery.findAllSorted(sortingOption.getSortFieldNames(), sortingOption.getSort());
            } else {//Should not be posible
                currentList = filteredQuery.findAll();
            }
        } else {

            if (isDefaultSortingOptionForMyOffers()) {
                RealmQuery filterExpiredNewVouchers = realm.where(Promotion.class)
                        .equalTo(IS_RESERVED_KEY, true);
                RealmQuery filterNotExpiredNewVouchers = realm.where(Promotion.class)
                        .equalTo(IS_RESERVED_KEY, true);
                setupListForMyOffersPromotionsSortedWithRelevance(filterNotExpiredNewVouchers, filterExpiredNewVouchers);
                D.w("Final list: " + currentList.size());

            } else {
                RealmQuery filteredVouchers = realm.where(Promotion.class);
                if (sortingOption != null) {

                    if (tabCardView.getSelectedTabIndex() == 0) {
                        filteredVouchers = filteredVouchers.greaterThan(Promotion.CAMPAIGN_EXPIRY_DATE_KEY, serverSysDate)
                                                            .greaterThan(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate);
                    }
                    currentList = filteredVouchers
                            .equalTo(IS_RESERVED_KEY, lastTabSelected == 1)
                            .findAllSorted(sortingOption.getSortFieldNames(), sortingOption.getSort());
                } else {//Should not be posible
                    currentList = filteredVouchers
                            .equalTo(IS_RESERVED_KEY, lastTabSelected == 1).findAll();
                }
            }
        }

        if (tabCardView.getSelectedTabIndex() == 0) {
            setupNewOffers(currentList);
        } else if (tabCardView.getSelectedTabIndex() == 1) {
            setupMyOffers(currentList);
        }
    }

    private boolean isDefaultSortingOptionForMyOffers() {
        return lastTabSelected == 1 && (SORT_AFTER_RELEVANCE.equals(getReservedListingOptions().getSelectedSortType()) ||
                DEFAULT_SELECTED_SORT_TYPE.equals(getReservedListingOptions().getSelectedSortType()));
    }

    private void setupListForMyOffersPromotionsSortedWithRelevance(RealmQuery queryForNotExpired, RealmQuery queryForExpired) {
        SortingOption notExpiredSortingOption = getSortingOptionAfterExpDataVoucherPriorityPartnerName();
        SortingOption expiredSortingOption = getSortingOptionAfterPriorityPartnerName();
        List<Promotion> expiredVouchers = queryForExpired
                .lessThanOrEqualTo(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate)
                .findAllSorted(expiredSortingOption.getSortFieldNames(), expiredSortingOption.getSort());

        List<Promotion> notExpiredVouchers = queryForNotExpired
                .greaterThan(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate)
                .findAllSorted(notExpiredSortingOption.getSortFieldNames(), notExpiredSortingOption.getSort());

        currentList = new ArrayList();
        currentList.addAll(notExpiredVouchers);
        currentList.addAll(expiredVouchers);
    }

    private SortingOption getSortingOptionAfterExpDataVoucherPriorityPartnerName() {
        String[] sortFieldNames = {VOUCHER_EXPIRY_DATE_KEY, PRIORITY_KEY, PARTNER_NAME_KEY};
        Sort sort[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        return new SortingOption(sortFieldNames, sort);
    }

    private SortingOption getSortingOptionAfterPriorityPartnerName() {
        String[] sortFieldNames = {PRIORITY_KEY, PARTNER_NAME_KEY};
        Sort sort[] = {Sort.ASCENDING, Sort.ASCENDING};
        return new SortingOption(sortFieldNames, sort);
    }

    private SortingOption getSortingOptionAfterRelevancePriority() {
        //filterAndSort first after relevance than after alphatically, than after the expired date (if equal) :
        String[] sortFieldNames = {PRIORITY_KEY, PARTNER_NAME_KEY, CAMPAIGN_EXPIRY_DATE_KEY};
        Sort sort[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        return new SortingOption(sortFieldNames, sort);
    }

    private SortingOption getSortedOptionAsAlphabeticallyPriority() {
        //filterAndSort first after alphatically than after relevance,than after the expired date (if equal)
        String[] sortFieldNames = {PARTNER_NAME_KEY, PRIORITY_KEY, CAMPAIGN_EXPIRY_DATE_KEY};
        Sort sort[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        return new SortingOption(sortFieldNames, sort);
    }

    private SortingOption getSortingOptionAfterExpiredDatePriority() {
        //filterAndSort first after expire date than after relevance, alphatically (if equal)
        String[] sortFieldNames = {CAMPAIGN_EXPIRY_DATE_KEY, PRIORITY_KEY, PARTNER_NAME_KEY};
        Sort sort[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        return new SortingOption(sortFieldNames, sort);
    }

    public int getListCount() {
        return ((currentList != null && !currentList.isEmpty()) ? currentList.size() : 0) + 3;
    }

    public int getPositionForServiceCard() {
        return ((currentList != null && !currentList.isEmpty()) ? currentList.size() : 1) + 3;
    }

    public void openFilter(View view) {

        if (getCurrentCategories() == null || getCurrentCategories().isEmpty()) {
            setupAllCategoriesForFilter();
        }
        if (getCurrentCategories().isEmpty() || tabCardView == null) {
            return;
        }
        Intent intent = new Intent(getContext(), LoyaltyOffersFilterActivity.class);
        intent.putExtra(LoyaltyOffersFilterActivity.FILTER_INPUT_CATEGORIES, getCurrentCategories());

        LoyaltyFilterOptions currentFilterOptions = getCurrentFilterOptions();
        intent.putExtra(LoyaltyOffersFilterActivity.FILTER_SAVED_INPUT_CATEGORIES,
                currentFilterOptions != null ? currentFilterOptions.getCategories() : null);

        intent.putExtra(LoyaltyOffersFilterActivity.FILTER_INPUT_SHOW_EXPIRE_OPTION,
                tabCardView.getSelectedTabIndex() != 0 && anyReservedVoucherWithVoucherExpiredDateValid());

        intent.putExtra(LoyaltyOffersFilterActivity.FILTER_SAVED_INPUT_SHOW_EXPIRE_OPTION,
                currentFilterOptions != null ? currentFilterOptions.getShowExpireOptions() : null);
        startActivityForResult(intent, FILTER_REQUEST_KEY);
    }

    private ArrayList<String> getCurrentCategories() {
        if (lastTabSelected == 0) {
            return promotionsCategories;
        } else {
            return reservedPromotionsCategories;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* if (resultCode == Activity.RESULT_CANCELED) {
             //Write your code if there's no result
         }*/
        if (requestCode == FILTER_REQUEST_KEY) if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> categories = data.getStringArrayListExtra(LoyaltyOffersFilterActivity.FILTER_RESULT_BRANDS);
            boolean[] showExpireOptions = data.getBooleanArrayExtra(LoyaltyOffersFilterActivity.FILTER_RESULT_SHOW_EXPIRE_OPTION);
            updateCurrentFilterOptions(new LoyaltyFilterOptions(categories, showExpireOptions));
            filterAndSortAfterCurrentSelected();
        }
        setupFilterView();
    }

    private void updateFilterButtonTextWithNumberOfFilters(int numberOfCategoriesSelected) {
        if (filterButton == null) {
            return;
        }
        int expiredOptionsCategoriesSelected = 0;
        int numberOfExpiredOptionsCategories = 0;
        if(lastTabSelected==1){
            LoyaltyFilterOptions filterOptions = getCurrentFilterOptions();
            if(filterOptions!=null){
                expiredOptionsCategoriesSelected = filterOptions.getNumberOfExpiredOptionsFlaggedTrue();
                numberOfExpiredOptionsCategories = filterOptions.getSafeLenghtOfExpiredOptions();
            }
        }

        ArrayList<String> categories = getCurrentCategories();
        if (categories != null) {
            int numberOfCategories = categories.size() +numberOfExpiredOptionsCategories;
            numberOfCategoriesSelected += expiredOptionsCategoriesSelected;
            if (numberOfCategoriesSelected < numberOfCategories && numberOfCategoriesSelected != 0) {
                filterButton.setText(numberOfCategoriesSelected +
                        (numberOfCategoriesSelected == 1 ? LoyaltyLabels.getLoyaltySingleFilter() : " " + LoyaltyLabels.getLoyaltyFilters()));
                return;
            }
        }
        filterButton.setText(LoyaltyLabels.getLoyaltyFiltering());
    }

    public void filterAndSortAfterCurrentSelected() {
        //selectSpinnerElement(selectedSortType);
        if (lastTabSelected == 0 && !LoyaltyVoucherSuccess.haveValidData()) {
            hideFilterAndSortElementAndShowNoVouchersCard();

            return;
        } else if (lastTabSelected == 1 && !LoyaltyVoucherReservedSuccess.haveValidData()) {
            hideFilterAndSortElementAndShowNoVouchersCard();
            return;
        }
        showExistentFilterAndSortElement(true);
        LoyaltyFilterOptions filterOptions = getCurrentFilterOptions();
        if (filterOptions != null) {
            updateFilterButtonTextWithNumberOfFilters(filterOptions.getCategories() != null ?
                    filterOptions.getCategories().size() : 0);
        } else {
            updateFilterButtonTextWithNumberOfFilters(0);
        }
        filterAndSort(getFilteredQuery(), getCurrentSortingOptions());
    }

    private void hideFilterAndSortElementAndShowNoVouchersCard() {
        showExistentFilterAndSortElement(false);
        showNoVouchersCard();
    }

    private void showExistentFilterAndSortElement(boolean show) {
        if (filterButton != null) {
            filterButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (spinner != null) {
            spinner.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private RealmQuery getFilteredQuery() {
        if (getCurrentFilterOptions() == null) {
            return null;
        }
        ArrayList<String> categories = getCurrentFilterOptions().getCategories();

        if (tabCardView != null) {
            if (tabCardView.getSelectedTabIndex() == 0) {
                return getFilteredPromotions(categories);
            } else {
                return getFilteredReservedPromotions(categories, getCurrentFilterOptions().getShowExpireOptions());
            }
        }
        return null;
    }

    private RealmQuery<Promotion> getFilteredPromotions(ArrayList<String> categories) {
        if (categories == null) {
            return null;
        }
        return realm.where(Promotion.class)
                .equalTo(IS_RESERVED_KEY, false).in(Promotion.CATEGORY_KEY, categories.toArray(new String[categories.size()]));
    }

    private RealmQuery<Promotion> getFilteredReservedPromotions(ArrayList<String> categories, boolean[] showExpireOptions) {
        if (categories == null) {
            return realm.where(Promotion.class)
                    .equalTo(IS_RESERVED_KEY, true);
        }
        RealmQuery<Promotion> promotionRealmQueryCategory = realm.where(Promotion.class)
                .equalTo(IS_RESERVED_KEY, true)
                .in(Promotion.CATEGORY_KEY, categories.toArray(new String[categories.size()]));
        if (showExpireOptions != null && showExpireOptions.length > 1) {
            boolean showActive = showExpireOptions[0];
            boolean showExpired = showExpireOptions[1];
            boolean showActiveAndExpired = showActive && showExpired;
            if (!showActiveAndExpired) {
                if (showActive) {
                    promotionRealmQueryCategory = promotionRealmQueryCategory.greaterThan(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate);
                }
                if (showExpired) {
                    promotionRealmQueryCategory = promotionRealmQueryCategory.lessThanOrEqualTo(Promotion.VOUCHER_EXPIRY_DATE_KEY, serverSysDate);
                }
            }
        }
        return promotionRealmQueryCategory;
    }

    @Override
    public VodafoneGenericCard getErrorCardView() {
        if (getContext() != null) {
            VodafoneGenericCard vodafoneGenericCard = new VodafoneGenericCard(getContext());
            vodafoneGenericCard.showError(true, LoyaltyLabels.getLoyaltyVoucherErrorMessage());
            vodafoneGenericCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        showLoadingDialog();
                        ((LoyaltyMarketActivity) getActivity()).getLoyaltySegment();
                        ((LoyaltyMarketActivity) getActivity()).getReservedVouchers();
                    }
                }
            });
            vodafoneGenericCard.setCardMarginsInDp(12, 12, 12, 12);
            return vodafoneGenericCard;
        }
        return null;
    }

    @Override
    public VodafoneGenericCard getErrorCardWithTextView(String errorMessage) {
        if (getContext() != null) {
            VodafoneGenericCard vodafoneGenericCard = new VodafoneGenericCard(getContext());
            vodafoneGenericCard.showError(true, errorMessage);
            vodafoneGenericCard.setBackgroundResource(R.color.general_background_light_gray);
            RecyclerView.LayoutParams param = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            vodafoneGenericCard.setLayoutParams(param);
            vodafoneGenericCard.setCardMarginsInDp(12, 12, 12, 12);
            return vodafoneGenericCard;
        }
        return null;
    }

    public ErrorCardElement getErrorCardElementWithText(String errorMessage) {
        if (getContext() != null) {
            return new ErrorCardElement(CARD_ERROR_TYPE, 3, getErrorCardWithTextView(errorMessage));
        }
        return null;
    }

    public ErrorCardElement getErrorCardElement() {
        if (getContext() != null) {
            return new ErrorCardElement(CARD_ERROR_TYPE, 3, getErrorCardView());
        }
        return null;
    }

    public boolean isRequestSuccessful(int selectedTab) {
        if (getActivity() != null) {
            if (selectedTab == 0) {
                return !((LoyaltyMarketActivity) getActivity()).isGetVouchersError();
            } else if (selectedTab == 1) {
                return !((LoyaltyMarketActivity) getActivity()).isGetReservedVouchersError();
            }
        }

        return false;
    }

    public void setupViews() {

        setupServerDate();
        setupAllCategoriesForFilter();
        if (isRequestSuccessful(lastTabSelected)) {
            filterAndSortAfterCurrentSelected();
        } else {
            showErrorCard("");
        }
    }

    private void setupServerDate(){
        LoyaltyVoucherReservedSuccess loyaltyVoucherReservedSuccess = (LoyaltyVoucherReservedSuccess) RealmManager.getRealmObject(realm, LoyaltyVoucherReservedSuccess.class);
        LoyaltyVoucherSuccess loyaltyVoucherSuccess = (LoyaltyVoucherSuccess) RealmManager.getRealmObject(realm, LoyaltyVoucherSuccess.class);

        if(loyaltyVoucherReservedSuccess != null){
            serverSysDate = loyaltyVoucherReservedSuccess.getSysdate();
        }
        else if(loyaltyVoucherSuccess != null){
            serverSysDate = loyaltyVoucherSuccess.getSysdate();
        }
        else {
            serverSysDate = System.currentTimeMillis();
        }
    }

    public void showErrorCard(String errorMessage) {
        hideFilterAndSortElementAndShowNoVouchersCard();
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        elementHashMap.remove(LIST_TYPE);
        if(errorMessage.equals("")) {
            elementHashMap.put(RecyclerElementsAdapter.CARD_ERROR_TYPE, getErrorCardElement());
        } else {
            elementHashMap.put(RecyclerElementsAdapter.CARD_ERROR_TYPE, getErrorCardElementWithText(errorMessage));
        }
        updateLastElementPosition();
        recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), elementHashMap);
        recyclerView.setAdapter(recyclerElementsAdapter);
    }

    @Override
    public CenteredImageWithTextsCard getCenteredImageWithTextsCard() {
        if (getContext() == null) {
            return null;
        }
        return new CenteredImageWithTextsCard(getContext());
    }

    public ViewStaticAdapterElement getNoVouchersElement() {
        CenteredImageWithTextsCard centeredImageWithTextsCard = getCenteredImageWithTextsCard();
        if (tabCardView != null) {
            int index = tabCardView.getSelectedTabIndex();
            if (index == 0) {
                centeredImageWithTextsCard
                        .setFirstText(LoyaltyLabels.getLoyaltyVoucherNoNewVouchersMessage())
                        .setSecondText(null);
            } else {
                centeredImageWithTextsCard
                        .setFirstText(LoyaltyLabels.getLoyaltyVoucherNoMyVouchersFirstMessage())
                        .setSecondText(LoyaltyLabels.getLoyaltyVoucherNoMyVouchersSecondMessage());
            }
        }
        centeredImageWithTextsCard.setCardMarginsInDp(12, 12, 12, 12);
        return new ViewStaticAdapterElement(CARD_ERROR_TYPE, 3, centeredImageWithTextsCard);
    }
    private boolean anyReservedVoucherWithVoucherExpiredDateValid(){
        List<Promotion> promotions = realm.where(Promotion.class)
                .equalTo(IS_RESERVED_KEY, true).not().equalTo(Promotion.VOUCHER_EXPIRY_DATE_KEY,Long.MAX_VALUE).findAll();
        return promotions!=null && !promotions.isEmpty();
    }
    private void setupAllCategoriesForFilter() {
        RealmResults<Promotion> promotions = realm.where(Promotion.class)
                .equalTo(IS_RESERVED_KEY, false).distinct(Promotion.CATEGORY_KEY).sort(Promotion.CATEGORY_KEY);
        promotionsCategories = new ArrayList<>();
        for (Promotion promotion :
                promotions) {
            if (promotion.getCategory() == null || promotion.getCategory().isEmpty()) {
                promotion.setCategory("Altele");
            }
            if(promotion.getCampaignExpiryDate() > serverSysDate) {
//                promotion.setLowerCaseCategoryForSort(promotion.getCategory());
                promotionsCategories.add(promotion.getCategory());
            }
        }

        Collections.sort(promotionsCategories, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        RealmResults<Promotion> reservedPromotions = realm.where(Promotion.class)
                .equalTo(IS_RESERVED_KEY, true).distinct(Promotion.CATEGORY_KEY).sort(Promotion.CATEGORY_KEY);
        reservedPromotionsCategories = new ArrayList<>();
        for (Promotion promotion :
                reservedPromotions) {
            if (promotion.getCategory() == null || promotion.getCategory().isEmpty()) {
                promotion.setCategory("Altele");
            }
            reservedPromotionsCategories.add(promotion.getCategory());
        }

        Collections.sort(reservedPromotionsCategories, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    private LoyaltyFilterOptions getCurrentFilterOptions() {
        if (lastTabSelected == 0) {
            return getPromotionListingOptions().getLoyaltyFilterOptions();
        } else {
            return getReservedListingOptions().getLoyaltyFilterOptions();
        }
    }

    private void updateCurrentFilterOptions(LoyaltyFilterOptions options) {
        if (lastTabSelected == 0) {
            getPromotionListingOptions().setLoyaltyFilterOptions(options);
        } else {
            getReservedListingOptions().setLoyaltyFilterOptions(options);
        }
    }

    public SortingOption getCurrentSortingOptions() {
        SortingOption sortingOption;
        if (lastTabSelected == 0) {
            sortingOption = getPromotionListingOptions().getSortingOption();
        } else {
            sortingOption = getReservedListingOptions().getSortingOption();
        }
        if (sortingOption == null) {
            if (lastTabSelected == 0) {
                sortingOption = getSortingOptionAfterRelevancePriority();
            } else {
                sortingOption = getSortingOptionAfterExpDataVoucherPriorityPartnerName();
            }

        }
        return sortingOption;
    }

    public String getCurrentSelectedSorting() {
        if (lastTabSelected == 0) {
            return getPromotionListingOptions().getSelectedSortType();
        } else {
            return getReservedListingOptions().getSelectedSortType();
        }
    }

    private void updateCurrentSortingOptions(String selectedSortType, SortingOption sortingOption) {
        if (lastTabSelected == 0) {
            getPromotionListingOptions().setSortingOption(sortingOption);
            getPromotionListingOptions().setSelectedSortType(selectedSortType);
        } else {
            getReservedListingOptions().setSortingOption(sortingOption);
            getReservedListingOptions().setSelectedSortType(selectedSortType);
        }
    }

    private void triggerNonTelcoQuestionnaire() {
        rx.Observable.timer(AppConfiguration.getNonTelcoQuestionnaireTime(), TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //Tealium Track view
                        Map<String, Object> tealiumMapView = new HashMap<>(4);
                        tealiumMapView.put("screen_name", "nontelco");
                        //add Qualtrics survey
                        TealiumHelper.addQualtricsCommand();
                        //track
                        TealiumHelper.trackView("nontelco", tealiumMapView);
                    }
                });
    }

    public static void setLastTabSelected(int lastTabSelected) {
        LoyaltyMarketVoucherListingsFragment.lastTabSelected = lastTabSelected;
    }
    public static void setLastTabSelectedToMyOffer() {
        setLastTabSelected(1);
    }

    public static class VodafoneMarketTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty nontelco";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty nontelco");


            s.channel = "loyalty";
            s.getContextData().put("&&channel", s.channel);
            s.events = "event3";
            s.getContextData().put("event3", s.event3);
            s.events = "event5";
            s.getContextData().put("event5", s.event5);
            s.events = "event6";
            s.getContextData().put("event6", s.event6);

            s.prop5 = "content";
            s.getContextData().put("prop5", s.prop5);

            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            s.eVar73 = "loyalty";
            s.getContextData().put("eVar73", s.eVar73);
        }
    }

    private void setupTabCardsTrackingEvent(int i){
        VodafoneMarketTrackingEvent event = new VodafoneMarketTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        if(i == 0) {
            journey.event65 = "Oferte Noi";
        }
        else if(i == 1) {
            journey.event65 = "Ofertele mele";
        }
        journey.getContextData().put("event65", journey.event65);

        if(i == 0) {
            journey.eVar82 = "mcare:loyalty program:button:oferte noi";
        } else if(i == 1) {
            journey.eVar82 = "mcare:loyalty program:button:ofertele mele";
        }
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setupFilterTrackingEvent(){
        VodafoneMarketTrackingEvent event = new VodafoneMarketTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.event65 = "Filtreaza";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = " mcare:loyalty program:button:filtreaza";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setupSelectSortOptionTrackingEvent() {
        VodafoneMarketTrackingEvent event = new VodafoneMarketTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.event65 = "Sorteaza";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = "mcare:loyalty program:button:sorteaza";
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public static class VodafoneMarketTCTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty t&c";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty t&c");


            s.channel = "loyalty";
            s.getContextData().put("&&channel", s.channel);
            s.events = "event3";
            s.getContextData().put("event3", s.event3);
            s.events = "event5";
            s.getContextData().put("event5", s.event5);
            s.events = "event6";
            s.getContextData().put("event6", s.event6);

            s.prop5 = "content";
            s.getContextData().put("prop5", s.prop5);

            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            s.eVar73 = "loyalty";
            s.getContextData().put("eVar73", s.eVar73);
        }
    }

    private void initTCTrackingEvent() {
        VodafoneMarketTCTrackingEvent event = new VodafoneMarketTCTrackingEvent();
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }
}
