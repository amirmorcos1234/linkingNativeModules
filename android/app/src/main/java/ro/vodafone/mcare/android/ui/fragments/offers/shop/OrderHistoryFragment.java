package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.client.adapters.OrdersHistoryAdapter;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrder;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrdersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OrderHistoryService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by user2 on 4/19/2017.
 */

public class OrderHistoryFragment extends OffersFragment implements VodafoneSpinner.Callback{

    private static String TAG = "OrderHistoryFragment";

    public static final String DROPDOWN_ALL_ORDERS = "Toate comenzile";
    public static final String DROPDOWN_LAST_THREE_MONTHS = "Ultimele 3 luni";
    public static final String DROPDOWN_LAST_SIX_MONTHS = "Ultimele 6 luni";
    public static final String DROPDOWN_LAST_YEAR = "Ultimul an";


    LinearLayout rootView;
    NavigationHeader navigationHeader;

    RealmList<ShopOrder> shopOrderRealmList;


    @BindView(R.id.card_container)
    LinearLayout card_container;

    @BindView(R.id.spinner)
    VodafoneSpinner spinner;

    public OrderHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = (LinearLayout) inflater.inflate(R.layout.fragment_order_history, container, false);

        Log.d(TAG, "create view");
        ButterKnife.bind(this, rootView);
        shopOrderRealmList = new RealmList<>();
        configureHeaderForUser();
        getOrderHistoryList();
        //inflateErrorLayout(false);
        registerSpinner();
/*
        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
*/
        OrderHistoryTrackingEvent event = new OrderHistoryTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return rootView;
    }

    @Override
    public String getTitle() {
        return OffersLabels.getOrdersHistoryTitle();
    }

    private void registerSpinner(){
        spinner.setCallback(this);
        final List<String> dataset = new LinkedList<>(Arrays.asList(DROPDOWN_ALL_ORDERS, DROPDOWN_LAST_THREE_MONTHS,
                DROPDOWN_LAST_SIX_MONTHS, DROPDOWN_LAST_YEAR));

        VodafoneSpinnerAdapter adapter = new VodafoneSpinnerAdapter(getContext(), dataset, R.drawable.selector);
        spinner.setAdapter(adapter);
        spinner.setText(DROPDOWN_ALL_ORDERS);
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        if(selectedValue instanceof String) {
            String selectedString = (String) selectedValue;
            if (selectedString.equals(DROPDOWN_ALL_ORDERS)) {
                spinner.setText(DROPDOWN_ALL_ORDERS);
                showOrderListFiltered(DROPDOWN_ALL_ORDERS);

            }else if(selectedString.equals(DROPDOWN_LAST_THREE_MONTHS)){
                spinner.setText(DROPDOWN_LAST_THREE_MONTHS);
                showOrderListFiltered(DROPDOWN_LAST_THREE_MONTHS);

            }else if(selectedString.equals(DROPDOWN_LAST_SIX_MONTHS)){
                spinner.setText(DROPDOWN_LAST_SIX_MONTHS);
                showOrderListFiltered(DROPDOWN_LAST_SIX_MONTHS);

            }else if(selectedString.equals(DROPDOWN_LAST_YEAR)){
                spinner.setText(DROPDOWN_LAST_YEAR);
                showOrderListFiltered(DROPDOWN_LAST_YEAR);
            }

        }
    }
    private void showOrderListFiltered(String ordersLastDate){

        RealmList<ShopOrder> filteredShopOrderList = new RealmList<>();
        card_container.removeAllViews();
        int index = 0;


        AdapterBackedLinearLayout cardListView  = new AdapterBackedLinearLayout(getContext());

        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardListView.setLayoutParams(layoutParams);
        card_container.addView(cardListView,index<card_container.getChildCount()?index:card_container.getChildCount());

        if(ordersLastDate.equals(DROPDOWN_ALL_ORDERS)) {
            if(shopOrderRealmList.size() == 0){
                inflateOrderHistoryListLayout(false);
            }
            else {
                OrdersHistoryAdapter ordersHistoryAdapter = new OrdersHistoryAdapter(getActivity(),
                        shopOrderRealmList);
                cardListView.setAdapter(ordersHistoryAdapter);
            }
        }
        else
        {
            filteredShopOrderList = makeFilteredList(ordersLastDate);
            if(filteredShopOrderList.size() == 0)
            {
                inflateErrorLayout(true);
            }
            else {
                OrdersHistoryAdapter ordersHistoryAdapter = new OrdersHistoryAdapter(getActivity(),
                        filteredShopOrderList);
                cardListView.setAdapter(ordersHistoryAdapter);
            }

        }

    }

    public RealmList<ShopOrder> makeFilteredList(String ordersLastDate) {
        RealmList<ShopOrder> filteredShopOrderList = new RealmList<>();
        Calendar currentCalendar = Calendar.getInstance();
        Log.d(TAG, currentCalendar.get(Calendar.MONTH) +"");

        for(int i = 0 ; i < shopOrderRealmList.size() ; i++){
            ShopOrder shopOrderItem = shopOrderRealmList.get(i);
            String dateString = shopOrderItem.getDeliveryInfo().getDelivered();

            if(dateString != null) {
                Date date = null;
                Calendar shopOrderCalendar = new GregorianCalendar();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(dateString);
                    shopOrderCalendar.setTime(date);
                    Log.d(TAG, date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (ordersLastDate.equals(DROPDOWN_LAST_THREE_MONTHS)) {
                    shopOrderCalendar.add(Calendar.MONTH, 3);
                } else if (ordersLastDate.equals(DROPDOWN_LAST_SIX_MONTHS)) {
                    shopOrderCalendar.add(Calendar.MONTH, 6);
                } else {
                    shopOrderCalendar.add(Calendar.MONTH, 12);
                }

                if (shopOrderCalendar.after(currentCalendar)) {
                    filteredShopOrderList.add(shopOrderItem);
                }
            }
        }

        return filteredShopOrderList;
    }

    //if no orders to display, it will still show the spinner
    //if there is an error on the call to API 51 only the error message is shown
    public void inflateErrorLayout(boolean displayNoOrders) {
        try {

            Log.d(TAG, displayNoOrders + " bool");
            if (displayNoOrders) {
                card_container.removeAllViews();

                VodafoneGenericCard orderHistoryCard;
                orderHistoryCard = new VodafoneGenericCard(getActivity());
                orderHistoryCard.showError(true, OffersLabels.getNoOrdersInHistory());

                card_container.addView(orderHistoryCard, card_container.getChildCount());
            } else {
                //rootView.removeAllViews();
                spinner.setVisibility(View.GONE);
                card_container.removeAllViews();
                GeneralCardsWithTitleBodyAndTwoButtons errorCard;
                errorCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                errorCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOrderHistoryList();
                    }
                });

                errorCard.showError(true, SettingsLabels.getSmallErrorMessage());

                card_container.addView(errorCard, layoutParams);
                //rootView
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(navigationHeader != null)
            navigationHeader.hideSelectorViewWithoutTriangle();
        ((OffersActivity) getActivity()).setTitle();
    }

    private void setHeaderText() {
        try{
            ((OffersActivity) getActivity()).getNavigationHeader().setTitle(OffersLabels.getOrdersHistoryTitle());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void configureHeaderForUser() {
        navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();
        navigationHeader.removeViewFromContainer();
        if(VodafoneController.getInstance().getUser() instanceof ResSub || VodafoneController.getInstance().getUser() instanceof ResCorp) {
            navigationHeader.buildMsisdnSelectorHeader();
            navigationHeader.displaySelectorView();
        }
        setHeaderText();
    }

    public void inflateOrderHistoryListLayout(boolean displayOrdersHistory) {

        if(!displayOrdersHistory)
        {
            inflateErrorLayout(true);
        }
    }


    public void getOrderHistoryList() {

        showLoadingDialog();
        String userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String userName = VodafoneController.getInstance().getUserProfile().getUserName();
        String shopSessionToken = ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class)).getShopSessionToken();
        String userId = VodafoneController.getInstance().getUserProfile().getSsoId();

        new OrderHistoryService(getContext()).getOrderHistoryList(shopSessionToken, userId, userRole, userName).subscribe(new RequestSessionObserver<GeneralResponse<ShopOrdersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopOrdersSuccess> ordersHistoryResponse) {
                if (ordersHistoryResponse.getTransactionSuccess() != null) {
                    shopOrderRealmList.clear();
                    D.w("ORDERS LISTING = " + ordersHistoryResponse.getTransactionSuccess());
                    Log.d(TAG, ordersHistoryResponse.getTransactionSuccess().getOrdersList().size()+"");
                    if(ordersHistoryResponse.getTransactionSuccess().getOrdersList().size() == 0) {
                        inflateOrderHistoryListLayout(false);
                        spinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        spinner.setVisibility(View.VISIBLE);

                        shopOrderRealmList = ordersHistoryResponse.getTransactionSuccess().getOrdersList();
                        addCardList(shopOrderRealmList ,0);
                    }

                } else {
                    onError(new Throwable("Server failed"));
                }
                stopLoadingDialog();
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
                D.d("on Completed");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                inflateErrorLayout(false);
                stopLoadingDialog();
            }
        });


    }

    private void addCardList(RealmList<ShopOrder> ordersHistoryList, int index) {
        card_container.removeAllViews();

        AdapterBackedLinearLayout cardListView  = new AdapterBackedLinearLayout(getContext());

        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardListView.setLayoutParams(layoutParams);
        card_container.addView(cardListView,index<card_container.getChildCount()?index:card_container.getChildCount());
        OrdersHistoryAdapter ordersHistoryAdapter = new OrdersHistoryAdapter(getActivity(),
                ordersHistoryList);
        cardListView.setAdapter(ordersHistoryAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "history on activity result");
        if (data == null) {
            return;
        }
        try {
            Fragment currentFragment = FragmentUtils.getVisibleFragment((AppCompatActivity) getActivity(), false);
            if (currentFragment instanceof OrderHistoryFragment) {

                if (resultCode == RESULT_SELECTOR_UPDATED) {
                    Log.d(TAG, "update element");
                    spinner.setText(DROPDOWN_ALL_ORDERS);
                    getOrderHistoryList();

                    callForAdobeTarget(AdobePageNamesConstants.RETENTION_ORDER_HISTORY);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_ORDER_HISTORY);
    }

    public static class OrderHistoryTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:order history";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:order history");


            s.prop5 = "sales:order history";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
