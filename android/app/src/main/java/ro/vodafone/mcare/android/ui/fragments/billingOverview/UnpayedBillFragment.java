package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import io.realm.RealmQuery;
import okhttp3.ResponseBody;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.billSummary.BillSummaryDataController;
import ro.vodafone.mcare.android.card.billSummary.BillSummaryDataViewGroup;
import ro.vodafone.mcare.android.card.billSummary.subscriptionSection.SubscriptionSectionController;
import ro.vodafone.mcare.android.card.unpayedBill.HeaderBillInfoCard;
import ro.vodafone.mcare.android.card.unpayedBill.HeaderBillInfoCardController;
import ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceErrorCardFactory;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceNavigationLayoutFactory;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceVdfAutoCompleteFactory;
import ro.vodafone.mcare.android.interfaces.factory.billing.InterfaceBillSummaryDataViewGroup;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.presenter.adapterelements.ErrorCardElement;
import ro.vodafone.mcare.android.presenter.adapterelements.NavigationHeaderElement;
import ro.vodafone.mcare.android.presenter.adapterelements.VdfAutoCompleteTvElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.AdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.ViewStaticAdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.billing.UnpayedBillOverviewCardElement;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.activities.store_locator.OverlapTabCardItemDecoration;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CompatView;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ExternalStorageUtils;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.utils.textwatcher.AutoCompleteTextWatcher;
import ro.vodafone.mcare.android.ui.views.autocomplete.VodafoneAutoCompleteTextView;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.expandabales.ExpandableLinearButtonsViewGroup;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.VodafoneNotificationManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.CARD_ERROR_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.HEADER_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.LIST_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.SEARCH_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.VIEW_GROUP_TYPE;

/**
 * Created by Victor Radulescu on 2/5/2018.
 * //TODO to be renamed in the deprecated class UnpayedBillFragment
 */

public class UnpayedBillFragment extends BaseFragment implements
        InterfaceNavigationLayoutFactory,
        InterfaceErrorCardFactory,
        InterfaceBillSummaryDataViewGroup,
        InterfaceVdfAutoCompleteFactory {

    public static final int NUMBER_OF_SUBSCRIPTIONS_TO_SHOW_AUTOCOMPLETE = 20;
    public static String TAG = "UnpayedBillFragment";
    private static final String BILL_HISTORY_OBJECT = "billHistoryObject";
    private static final String IS_OLDER_PARAMETER = "isOlderThanLast3Months";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 22;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.parent_fragment)
    RelativeLayout parentViewGroup;

    ExpandableLinearButtonsViewGroup expandableLinearButtonsViewGroup;

    private VodafoneButton downloadBillButton;
    private VodafoneButton downloadDetailedBillButton;

    private HeaderBillInfoCard headerBillInfoCard;
    private BillHistoryDetails billHistoryDetails;
    private BillSummarySuccess billSummarySuccess;
    private ResponseBody rsBody;

    private boolean isDetailed;
    private boolean isOlderThanLast3Months;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    NavigationHeader navigationHeader;

    OnRecycleScrollViewCreatedListener scrollViewCreatedListener;

    CompositeSubscription fragmentCompositeSubscription = new CompositeSubscription();

    RecyclerElementsAdapter recyclerElementsAdapter;

    BillSummaryDataViewGroup billSummaryDataViewGroup;


    public UnpayedBillFragment() {
    }

    public static UnpayedBillFragment newInstance(BillHistoryDetails billHistoryDetails, boolean isOlderThanLast3Months) {
        UnpayedBillFragment fragment = new UnpayedBillFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BILL_HISTORY_OBJECT, billHistoryDetails);//TODO change it with id ?
        bundle.putBoolean(IS_OLDER_PARAMETER, isOlderThanLast3Months);
        fragment.setArguments(bundle);

        return fragment;
    }

    public boolean isOlderThanLast3Months() {
        return isOlderThanLast3Months;
    }

    public synchronized static UnpayedBillFragment getInstance() {
        return (UnpayedBillFragment) VodafoneController.findFragment(UnpayedBillFragment.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.billHistoryDetails = (BillHistoryDetails) getArguments().getSerializable(BILL_HISTORY_OBJECT);
        this.isOlderThanLast3Months = getArguments().getBoolean(IS_OLDER_PARAMETER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_unpayed_bill_fragment, null);
        ButterKnife.bind(this, layout);

        setupTelium();
        populateAdapterElementsWithoutListType();
        getBillSummary();
        setBackgroundViewColor(R.color.general_background_light_gray);
        if (scrollViewCreatedListener != null) {
            scrollViewCreatedListener.onRecycleScrollViewCreated(recyclerView);
        }
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragmentCompositeSubscription != null) {
            fragmentCompositeSubscription.clear();
            fragmentCompositeSubscription = null;
        }
        removeExpandableLinearButtonsViewGroup();
    }

    private void removeExpandableLinearButtonsViewGroup() {
        try {
            FrameLayout frameLayout = getActivity().findViewById(R.id.fragment_layout_container);
            frameLayout.removeView(expandableLinearButtonsViewGroup);
            expandableLinearButtonsViewGroup = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupViewAfterData() {
        if (getContext() == null) {
            return;
        }
        populateAdapterElements();
        if (billHistoryDetails == null) {
            setupFailedViewList();
        } else {
            setupSuccesfullViewList();
        }
    }


    private void setupSuccesfullViewList() {
        displayLoadedPage();
    }

    private void setupFailedViewList() {
        displayErrorPage();
    }

    public void populateAdapterElements() {
        billSummarySuccess = getBillSummarySuccess();
        if (getContext() == null || billSummarySuccess == null) {
            return;
        }
        boolean showAutoCompleteTextView = isNumberOfSubscriptionsEnoghToShowAutocomplete(billSummarySuccess);
        HashMap<Integer, AdapterElement> hashMap = new HashMap<>();
        hashMap.put(HEADER_TYPE, createNavigationHeaderElement());

        if (showAutoCompleteTextView) {
            hashMap.put(SEARCH_TYPE, createAutoCompleteElement(1));
        }
        List<BillSummaryItem> billSummaryItems =
                getFirstsBillSummaryList(billSummarySuccess.getBillSummaryList(), showAutoCompleteTextView);
        if (billSummaryItems != null) {
            hashMap.put(LIST_TYPE,
                    getSubscriptionSummmaryDynamicElement(showAutoCompleteTextView ? 2 : 1,
                            billSummaryItems,
                            BillSummaryDataController.getInstance().getHistoryDetailsForDisplay(billHistoryDetails),
                            billHistoryDetails.getBillClosedDate(), isOlderThanLast3Months()));

            hashMap.put(CARD_ERROR_TYPE, createErrorCardElement(showAutoCompleteTextView ? 3 : 2));
            initializeAdapter(hashMap);
            getErrorCardElement().makeCardInvisible();
            getErrorCardElement().showError();
        }
    }

    private boolean isNumberOfSubscriptionsEnoghToShowAutocomplete(BillSummarySuccess billSummarySuccess) {
        return billSummarySuccess != null && billSummarySuccess.getBillSummaryList().size() > getNumberOfSubscriptionsToShowAutocomplete();
    }

    private List<BillSummaryItem> getFirstsBillSummaryList(RealmList<BillSummaryItem> billSummaryItems, boolean showAutoCompleteTextView) {
        if (billSummaryItems != null) {

            List<BillSummaryItem> billSummaryItemsArray = getValidBillSummaryList(billSummaryItems).findAllSorted(BillSummaryItem.PHONE_NUMBER);
            return (showAutoCompleteTextView ? billSummaryItemsArray.subList(0, getNumberOfSubscriptionsToShowAutocomplete()) : billSummaryItemsArray);
        } else {
            return new ArrayList<>();
        }
    }

    private int getNumberOfSubscriptionsToShowAutocomplete() {
        String maxSubscriberWillBeDisplayed = AppConfiguration.getBillingOverViewBillMaxSubscriberWillBeDisplayed();
        return NumberUtils.isNumber(maxSubscriberWillBeDisplayed) ?
                NumberUtils.createInteger(maxSubscriberWillBeDisplayed) :
                NUMBER_OF_SUBSCRIPTIONS_TO_SHOW_AUTOCOMPLETE;
    }

    private RealmQuery<BillSummaryItem> getValidBillSummaryList(RealmList<BillSummaryItem> billSummaryItems) {
        return billSummaryItems.where().isNotNull(BillSummaryItem.PHONE_NUMBER).isNotNull(BillSummaryItem.TOTAL_AMOUNT);
    }

    public void populateAdapterElementsWithoutListType() {
        if (getContext() == null) {
            return;
        }
        HashMap<Integer, AdapterElement> hashMap = new HashMap<>();
        hashMap.put(HEADER_TYPE, createNavigationHeaderElement());
        hashMap.put(CARD_ERROR_TYPE, createErrorCardElement(2));
        initializeAdapter(hashMap);
    }

    /**
     * Method designed for page with autocompletetextview
     *
     * @param billSummaryItems
     */
    public void updateListTypeForFilter(List<BillSummaryItem> billSummaryItems) {
        if (recyclerElementsAdapter == null || recyclerElementsAdapter.getElementHashMap() == null) {
            return;
        }
        HashMap<Integer, AdapterElement> hashMap = recyclerElementsAdapter.getElementHashMap();
        if (hashMap.containsKey(LIST_TYPE)) {
            UnpayedBillOverviewCardElement billSummaryCardElement = (UnpayedBillOverviewCardElement) hashMap.get(LIST_TYPE);
            billSummaryCardElement.setBillSummaryItems(billSummaryItems);
            recyclerElementsAdapter.updateListType(billSummaryCardElement, billSummaryCardElement.getList());
        } else if (billSummaryItems != null && !billSummaryItems.isEmpty()) {
            hashMap.put(LIST_TYPE,
                    getSubscriptionSummmaryDynamicElement(2,
                            billSummaryItems,
                            BillSummaryDataController.getInstance().getHistoryDetailsForDisplay(billHistoryDetails),
                            billHistoryDetails.getBillClosedDate(), isOlderThanLast3Months()));
            recyclerElementsAdapter.notifyElementsDataChange();
        }
    }

    private void initializeAdapter(HashMap<Integer, AdapterElement> hashMap) {
        recyclerElementsAdapter = new RecyclerElementsAdapter(getActivity(), hashMap);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerElementsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(recyclerElementsAdapter);
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupDownloadPayBill() {

        if (getActivity() == null) {
            return;
        }
        if (expandableLinearButtonsViewGroup == null) {
            createToBottomExpandableLinearButttonsViewGroup();
        }
        expandableLinearButtonsViewGroup.build();

        CompatView.bringToFront(expandableLinearButtonsViewGroup);

        expandableLinearButtonsViewGroup.setNameTextView(BillingOverviewLabels.getBillingOverviewDownloadBillTitle());
        if (downloadBillButton == null) {
            downloadBillButton = expandableLinearButtonsViewGroup.
                    addButtonViewToExpandableGroup(BillingOverviewLabels.getBillingOverviewDownloadBillPrimaryButtonTitle(), R.style.CardPrimaryButton, new WeakReference<>(downloadBillButtonListner));
            downloadBillButton.setTag("download_bill");
        }
        if (downloadDetailedBillButton == null) {
            downloadDetailedBillButton = expandableLinearButtonsViewGroup.
                    addButtonViewToExpandableGroup(BillingOverviewLabels.getBillingOverviewDownloadBillSecondaryButtonTitle(), R.style.OverlaySecondaryButton, new WeakReference<>(downloadBillButtonListner));
            downloadDetailedBillButton.setTag("download_detailed_bill");
        }
    }

    private void createToBottomExpandableLinearButttonsViewGroup() {
        expandableLinearButtonsViewGroup = new ExpandableLinearButtonsViewGroup(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        FrameLayout frameLayout = getActivity().findViewById(R.id.fragment_layout_container);
        expandableLinearButtonsViewGroup.setGravity(Gravity.BOTTOM);
        frameLayout.addView(expandableLinearButtonsViewGroup, layoutParams);
        expandableLinearButtonsViewGroup.setVisibility(View.VISIBLE);
    }

    public void getBillSummary() {
        cleanDataOnBan();

        displayLoadingPage();

        BillingServices billingServices = new BillingServices(getContext());

        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String userCid = VodafoneController.getInstance().getUserProfile().getCid();
        EntityChildItem selectedEntitiy = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        String vfodsCid = selectedEntitiy != null ? selectedEntitiy.getVfOdsCid() : null;
        long date = billHistoryDetails.getBillClosedDate();

        Subscription billSummaryCall = billingServices.getBillSummary(userCid, vfodsCid, selectedBan, date, true).subscribe(new RequestSaveRealmObserver<GeneralResponse<BillSummarySuccess>>() {
            @Override
            public void onNext(GeneralResponse<BillSummarySuccess> billSummarySuccessGeneralResponse) {
                if (billSummarySuccessGeneralResponse.getTransactionSuccess() != null) {
                    super.onNext(billSummarySuccessGeneralResponse);
                    billSummarySuccess = billSummarySuccessGeneralResponse.getTransactionSuccess();
                    setupViewAfterData();
                    hideDownloadButton(billSummarySuccess.isHideDownloadButton());

                    BillDetailsTrackingEvent event = new BillDetailsTrackingEvent();
                    TrackingAppMeasurement journey = new TrackingAppMeasurement();
                    journey.event8 = "event8";
                    journey.getContextData().put("event8", journey.event8);
                    event.defineTrackingProperties(journey);
                    VodafoneController.getInstance().getTrackingService().trackCustom(event);

                } else {
                    displayErrorPage();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                displayErrorPage();
            }
        });
        if (fragmentCompositeSubscription != null && billSummaryCall != null) {
            fragmentCompositeSubscription.add(billSummaryCall);
        }
    }

    public BillSummarySuccess getBillSummarySuccess() {
        if (billSummarySuccess != null && billSummarySuccess.isValid() && billSummarySuccess.isManaged())
            return billSummarySuccess;
        else {
            return (BillSummarySuccess) RealmManager.getRealmObject(BillSummarySuccess.class);
        }
    }

    private void cleanDataOnBan() {
        RealmManager.delete(BillSummarySuccess.class);
        RealmManager.delete(BillSummaryItem.class);
        RealmManager.delete(ServiceDetails.class);
    }

    private void displayLoadedPage() {
        setupDownloadPayBill();
        displayDownloadBillButtons();
    }

    private void displayErrorPage() {
        SubscriptionSectionController.getInstance().onRequestFailed();
        showErrorCard();
        hideDownloadBillButtons();
    }

    private void displayLoadingPage() {
        showLoadingCard();
    }

    private void displayDownloadBillButtons() {
        if (expandableLinearButtonsViewGroup == null) {
            return;
        }
        expandableLinearButtonsViewGroup.setVisibility(View.VISIBLE);
    }

    private void hideDownloadBillButtons() {
        if (expandableLinearButtonsViewGroup == null) {
            return;
        }
        expandableLinearButtonsViewGroup.setVisibility(View.INVISIBLE);

    }


    private void downloadBill() {

        showLoadingDialog();
        BillingServices billingServices = new BillingServices(getContext());

        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        long date = billHistoryDetails.getBillClosedDate();

        billingServices.downloadBill(selectedBan, date, isDetailed ? "yes" : "no").subscribe(new RequestSessionObserver<ResponseBody>() {
            @Override
            public void onNext(ResponseBody responseBody) {
                rsBody = responseBody;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //Tealium Track View
                TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.billingOverview_previous_bill);

                stopLoadingDialog();
                new CustomToast.Builder(getContext()).message("Momentan factura nu este disponibilă in format pdf, vă rugăm reveniți mai târziu.").success(false).show();
                UnpayedBillFragment.BillDetailsTrackingEvent event = new UnpayedBillFragment.BillDetailsTrackingEvent();
                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                journey.event11 = "event11";
                journey.getContextData().put("event11", journey.event11);
                journey.prop16 = "prop16";
                journey.getContextData().put("prop16", journey.prop16);
                event.defineTrackingProperties(journey);
                VodafoneController.getInstance().getTrackingService().trackCustom(event);

            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                stopLoadingDialog();
                //Tealium Track View
                TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.billingOverview_previous_bill);

                if (Build.VERSION.SDK_INT >= 23) {
                    requestWriteExternalStoragePermission();
                } else {
                    saveBillToExternalStorage();
                }
            }
        });
    }

    private void saveBillToExternalStorage() {

        File file = ExternalStorageUtils.saveResponseToExternalStorage(getActivity(), rsBody, getBillName().toString());
        if (file != null) {
            VodafoneNotificationManager.displayDownloadBillNotification(getActivity(), file);
        } else {
            new CustomToast.Builder(getContext()).message("Momentan factura nu este disponibilă in format pdf, vă rugăm reveniți mai târziu.").success(false).show();

            UnpayedBillFragment.BillDetailsTrackingEvent event = new UnpayedBillFragment.BillDetailsTrackingEvent();
            TrackingAppMeasurement journey = new TrackingAppMeasurement();
            journey.event11 = "event11";
            journey.getContextData().put("event11", journey.event11);
            event.defineTrackingProperties(journey);
            VodafoneController.getInstance().getTrackingService().trackCustom(event);
        }
    }

    private StringBuilder getBillName() {
        StringBuilder name = new StringBuilder();

        if (isDetailed) {
            name.append("Detailed_");
        }
        name.append("Bill_");

        String bcd = WordUtils.capitalize(DateUtils.getDate(String.valueOf(billHistoryDetails.getBillClosedDate()),
                new SimpleDateFormat("yyyy MMM dd", new Locale("RO", "RO"))));

        Log.d(TAG, "bcd =" + bcd);

        if (bcd.contains(".")) {
            bcd = bcd.replace(".", "");
        }

        if (bcd.contains(" ")) {
            bcd = bcd.replaceAll("\\s", "");
        }

        Log.d(TAG, "bcd =" + bcd);
        name.append(bcd);
        name.append(".pdf");

        return name;
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestWriteExternalStoragePermission() {
        int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            saveBillToExternalStorage();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBillToExternalStorage();
                }
                return;
            }
        }
    }

    private void setupTelium() {
        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.billingOverview_previous_bill);

        BillDetailsTrackingEvent event = new BillDetailsTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public ErrorCardElement createErrorCardElement(int position) {
        return new ErrorCardElement(CARD_ERROR_TYPE, position, getErrorCardView());
    }

    public ErrorCardElement getErrorCardElement() {
        if (recyclerElementsAdapter == null) {
            return null;
        }
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        if (elementHashMap != null && elementHashMap.containsKey(CARD_ERROR_TYPE) && elementHashMap.get(CARD_ERROR_TYPE) instanceof ErrorCardElement) {
            return (ErrorCardElement) elementHashMap.get(CARD_ERROR_TYPE);
        } else {
            return null;
        }
    }

    @Override
    public VodafoneGenericCard getErrorCardView() {
        VodafoneGenericCard vodafoneGenericCard = new VodafoneGenericCard(getContext());
        vodafoneGenericCard.setBackgroundResource(R.color.general_background_light_gray);
        return vodafoneGenericCard;
    }

    @Override
    public VodafoneGenericCard getErrorCardWithTextView(String errorMessage) {
        return getErrorCardView();
    }

    @Override
    public NavigationHeader initializeNavigationHeader() {
        navigationHeader = new NavigationHeader(getContext());
        navigationHeader.setId(R.id.navigation_header);
        navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        navigationHeader.hideSelectorView();
        navigationHeader.setTitle(BillingOverviewLabels.getBillingOverviewUnpaidTitle());
        navigationHeader.showTriangleView();
        headerBillInfoCard = new HeaderBillInfoCard(getContext()).setOnErrorClickListner(new WeakReference<View.OnClickListener>(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBillSummary();
            }
        }));
        headerBillInfoCard.setCardTitle(BillingOverviewLabels.getBillingBanHeaderTitle() + " " + UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
        navigationHeader.addViewToContainer(headerBillInfoCard);
        displayBillSummaryDataViewGroup();
        return navigationHeader;
    }

    @Override
    public NavigationHeader getNavigationHeader() {
        if (navigationHeader == null) {
            navigationHeader = initializeNavigationHeader();
        }
        return navigationHeader;
    }

    public AdapterElement createNavigationHeaderElement() {
        return new NavigationHeaderElement(HEADER_TYPE, 0, getNavigationHeader());
    }

    @Override
    public BillSummaryDataViewGroup getBillSummaryDataViewGroup() {
        if (this.billSummaryDataViewGroup == null && getActivity() != null) {
            BillSummaryDataViewGroup billSummaryDataViewGroup = new BillSummaryDataViewGroup(getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            billSummaryDataViewGroup.setPaddingRelative(0, 0, 0, ScreenMeasure.dpToPx(
                    getActivity().getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)));
            billSummaryDataViewGroup.setBackgroundResource(R.color.card_background_gray);
            this.billSummaryDataViewGroup = billSummaryDataViewGroup;
        }
        return billSummaryDataViewGroup;
    }

    public StaticAdapterElement createBillSummaryDataViewGroupElement(int position) {
        return new ViewStaticAdapterElement(VIEW_GROUP_TYPE, position, getBillSummaryDataViewGroup());
    }

    public UnpayedBillOverviewCardElement getSubscriptionSummmaryDynamicElement(int position, List<BillSummaryItem> billSummaryItems, List<HistoryDetail> historyDetails, Long billClosedDate, boolean olderThanLast3Months) {
        return new UnpayedBillOverviewCardElement(LIST_TYPE, position, getContext(), billSummaryItems, historyDetails, billClosedDate, olderThanLast3Months);
    }

    @Override
    public void displayBillSummaryDataViewGroup() {
        if ( billHistoryDetails != null) {
            HeaderBillInfoCardController.getInstance().onDataLoaded(getBillSummarySuccess(), billHistoryDetails);
        } else {
            HeaderBillInfoCardController.getInstance().onRequestFailed();
        }
    }

    @Override
    public void displayErrorBillSummaryDataViewGroup() {
        HeaderBillInfoCardController.getInstance().onRequestFailed();
    }

    public void showLoadingCard() {
        ErrorCardElement errorCardElement = getErrorCardElement();
        if (errorCardElement != null) {
            errorCardElement.showLoading();
        }
    }

    public void showErrorCard() {
        ErrorCardElement errorCardElement = getErrorCardElement();
        if (errorCardElement != null) {
            errorCardElement.showError(AppLabels.getGenericRetryErrorMessage());
            errorCardElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBillSummary();
                }
            });
        }
    }

    @Override
    public VodafoneAutoCompleteTextView getAutoCompleteView() {
        final VodafoneAutoCompleteTextView vodafoneAutoCompleteTextView = new VodafoneAutoCompleteTextView(getContext());
        vodafoneAutoCompleteTextView.setInputType(InputType.TYPE_CLASS_PHONE);
        vodafoneAutoCompleteTextView.setBackgroundResource(R.color.card_background_gray);
        vodafoneAutoCompleteTextView.setHint(BillingOverviewLabels.getBilling_overview_SAYT());
        vodafoneAutoCompleteTextView.setDrawableToEditText(R.drawable.search_48);
        AutoCompleteTextWatcher autoCompleteTextWatcher = new AutoCompleteTextWatcher(vodafoneAutoCompleteTextView) {
            @Override
            public void onInvalidTextChanged() {
                if (getActivity() == null) {
                    return;
                }
                showAllSubscribers();

                VodafoneController.getInstance().handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            vodafoneAutoCompleteTextView.setDrawableToEditText(R.drawable.search_48);
                        }

                    }
                }, 50);
            }

            @Override
            public void onValidTextChanged(String validText, int start, int before, int count) {
                filterSubscriberList(validText);
            }


        };
        autoCompleteTextWatcher.setMinCharFilter(4);
        vodafoneAutoCompleteTextView.addTextChangedListener(autoCompleteTextWatcher);

        return vodafoneAutoCompleteTextView;
    }

    private void filterSubscriberList(String validText) {
        try {
            BillSummarySuccess billSummarySuccess = getBillSummarySuccess();
            List<BillSummaryItem> billSummaryItems = getValidBillSummaryList(billSummarySuccess.getBillSummaryList()).contains(BillSummaryItem.PHONE_NUMBER, validText).findAll();
            if (billSummaryItems.size() > getNumberOfSubscriptionsToShowAutocomplete()) {
                updateListTypeForFilter(billSummaryItems.subList(0, getNumberOfSubscriptionsToShowAutocomplete()));
            } else {
                updateListTypeForFilter(billSummaryItems);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            showAllSubscribers();
            e.printStackTrace();
        }
    }

    private void showAllSubscribers() {
        BillSummarySuccess billSummarySuccess = getBillSummarySuccess();
        if (billSummarySuccess != null) {
            List<BillSummaryItem> billSummaryItems = getFirstsBillSummaryList(billSummarySuccess.getBillSummaryList(),
                    isNumberOfSubscriptionsEnoghToShowAutocomplete(billSummarySuccess));

            updateListTypeForFilter(billSummaryItems);
        }
    }

    @Override

    public VdfAutoCompleteTvElement createAutoCompleteElement(int positionInAdapter) {
        return new VdfAutoCompleteTvElement(SEARCH_TYPE, positionInAdapter, getAutoCompleteView());
    }

    @Override
    public VdfAutoCompleteTvElement getAutoCompleteElement() {
        if (recyclerElementsAdapter == null) {
            return null;
        }
        HashMap<Integer, AdapterElement> elementHashMap = recyclerElementsAdapter.getElementHashMap();
        if (elementHashMap != null && elementHashMap.containsKey(SEARCH_TYPE) && elementHashMap.get(SEARCH_TYPE) instanceof VdfAutoCompleteTvElement) {
            return (VdfAutoCompleteTvElement) elementHashMap.get(SEARCH_TYPE);
        } else {
            return null;
        }
    }

    public void hideDownloadButton(boolean hide) {
        if (downloadDetailedBillButton != null) {
            downloadDetailedBillButton.setVisibility(hide ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            scrollViewCreatedListener = (OnRecycleScrollViewCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentLifeCycleListener");
        }
    }

    void setBackgroundViewColor(final int color) {
        if (getActivity() != null && navigationHeader != null) {
            getNavigationHeader().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int widthWas = right - oldRight; // right exclusive, left inclusive
                    if (v.getWidth() != widthWas) {
                        // width has changed
                    }
                    int heightWas = oldBottom - oldTop; // bottom exclusive, top inclusive
                    if (v.getHeight() != heightWas) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (getActivity() instanceof BillingOverviewActivity) {
                            boolean present = ((BillingOverviewActivity) getActivity()).isFragmentPresent(UnpayedBillFragment.this);
                            if (!present) {
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
                    }
                }
            });
        }
    }

    View.OnClickListener downloadBillButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag() == null) {
                return;
            }
            switch (view.getTag().toString()) {
                case "download_bill":
                    //Tealium Track Event
                    trackEvent(TealiumConstants.previous_bill_download_bill);
                    isDetailed = false;
                    downloadBill();
                    break;
                case "download_detailed_bill":
                    //Tealium Track Event
                    trackEvent(TealiumConstants.previous_bill_download_bill_details);
                    isDetailed = true;
                    downloadBill();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PREVIOUS_BILLS);
    }

    private static class BillDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "previous bills details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "previous bills details");


            s.channel = "billing overview";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "last bill overview";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "query";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "previous bills details";
            s.getContextData().put("prop21", s.prop21);
        }
    }
    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.billingOverview_previous_bill);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
}


