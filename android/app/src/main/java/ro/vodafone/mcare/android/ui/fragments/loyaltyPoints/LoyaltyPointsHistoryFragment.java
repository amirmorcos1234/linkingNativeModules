package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.loyaltyPoints.loyaltyHistory.LoyaltyHistoryHeaderCard;
import ro.vodafone.mcare.android.card.loyaltyPoints.loyaltyHistory.LoyaltyHistoryHeaderController;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyPointsSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyPointsActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

public class LoyaltyPointsHistoryFragment extends OffersFragment {
    protected LoyaltyHistoryTabHost mTabHost;
    private Date fromDateSelection;
    private Date lastBillCycleDate;
    private LoyaltyPointsFilter calendarView;
    private CustomEditText textFilterTo;
    private CustomEditText textFilterFrom;
    private static String TAG = "LoyaltyPointsHistory";
    private String longLastCycle;
    private String longMinusMonth;
    private VodafoneGenericCard layoutContainer;
    private VodafoneGenericCard loadingCard;
    LinearLayout view;
    private boolean isFirstTimeOpen;
    private Dialog filterDialog;
    private Date lastBillCycleInit;

    private LinearLayout tabHostContainer;

    private VodafoneGenericCard errorCard;

    private String visibleTabTag = "0";

    private LoyaltyPointsActivity activity;

    private Bundle args;

    private VodafoneGenericCard cardContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTitle() {
        return LoyaltyLabels.getLoyalty_history_title();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (LinearLayout) inflater.inflate(R.layout.loyalty_points_history_fragment, container, false);
        Log.d(TAG, "onCreateView");

        activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);
        isFirstTimeOpen = true;
        tabHostContainer = (LinearLayout) view.findViewById(R.id.loyalty_tab_container);
        cardContainer = (VodafoneGenericCard) view.findViewById(R.id.tabhost_container);
        layoutContainer = (VodafoneGenericCard) view.findViewById(R.id.loyalty_history_container);

        args = new Bundle();
        args.putBoolean("isFromFilter", false);

        setDataInHeaderCard();

        activity.getNavigationHeader().setTitle(getTitle());

        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if(profile != null) {
            initialViews();
            initialLoyaltyPoints();
        } else {
            showError();
        }

        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_transaction_history);

        LoyaltyHistoryTrackingEvent event = new LoyaltyHistoryTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return view;
    }

    private void setupTabs() {
        Log.d(TAG, "setupTabs");

        if (mTabHost != null) {
            mTabHost.clearAllTabs();
            tabHostContainer.removeView(mTabHost);
        }

        mTabHost = new LoyaltyHistoryTabHost(getContext());
        tabHostContainer.addView(mTabHost);

        mTabHost.setup(getContext(), getChildFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        mTabHost.addTab(
                mTabHost.newTabSpec("0")
                        .setIndicator(createTabIndicator(LoyaltyLabels.getLoyaltyReceivedPointsTab())),
                LoyaltyReceivedPointsTab.class, args);

        mTabHost.addTab(
                mTabHost.newTabSpec("1")
                        .setIndicator(createTabIndicator(LoyaltyLabels.getLoyaltyUsedPointsTab())),
                LoyaltyConsumedPointsTab.class, args);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                getChildFragmentManager().executePendingTransactions();
                visibleTabTag = mTabHost.getCurrentTabTag();
                setTabColor(mTabHost);
            }
        });

        mTabHost.setFocusable(false);
        mTabHost.setFocusableInTouchMode(false);
        setTabColor(mTabHost);
    }

    private void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.unselected_tab_shape); // unselected
            TextView tabTitle = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(R.id.label);
            tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_title_color));

        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.drawable.selected_tab_shape); // selected
        TextView tabTitle = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(R.id.label);
        tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.selected_tab_indicator_color));
    }

    private View createTabIndicator(String label) {
        View tabIndicator = getActivity().getLayoutInflater().inflate(R.layout.tab_indicator_background, null);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
        tv.setText(label);
        return tabIndicator;
    }

    View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            displayLoyaltyHistoryFilter();
        }
    };
    private void displayLoyaltyHistoryFilter() {
        VodafoneButton resetFilter;
        final VodafoneButton applyFilter;

        calendarView = (LoyaltyPointsFilter) filterDialog.findViewById(R.id.calendar_view);

        textFilterFrom = (CustomEditText) filterDialog.findViewById(R.id.filter_text_from);
        textFilterTo = (CustomEditText) filterDialog.findViewById(R.id.filter_text_to);
        textFilterFrom.setKeyListener(null);
        textFilterTo.setKeyListener(null);

        applyFilter = (VodafoneButton) filterDialog.findViewById(R.id.apply_filters_button);


        if (isFirstTimeOpen) {
            defaultFilterInit();
            isFirstTimeOpen = false;
        } else {
            if (lastBillCycleDate == null && fromDateSelection == null) {
                defaultFilterInit();
            }
            calendarView.initControl(getContext(), lastBillCycleDate, fromDateSelection, lastBillCycleInit);
        }

        if (lastBillCycleDate != null)
            textFilterTo.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(lastBillCycleDate));
        textFilterFrom.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(fromDateSelection));

        filterDialog.show();

        ImageView closeOverlayBtn = (ImageView) filterDialog.findViewById(R.id.close_overlay);
        closeOverlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        calendarView.setEventHandler(new LoyaltyPointsFilter.EventHandler() {
            @Override
            public void onDayLongPress(Date fromDate, Date toDate) {
                fromDateSelection = fromDate;
                lastBillCycleDate = toDate;
                Log.d(TAG, "onDayLongPress: onSelection fromDate: " + fromDateSelection);
                Log.d(TAG, "onDayLongPress: onSelection toDate: " + lastBillCycleDate);
                // show returned day
                if (fromDateSelection != null) {
                    textFilterFrom.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(fromDate));
                } else {
                    applyFilter.setEnabled(false);
                    textFilterFrom.setText(null);
                }

                if (lastBillCycleDate != null) {
                    textFilterTo.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(toDate));
                } else {
                    applyFilter.setEnabled(false);
                    textFilterTo.setText(null);
                }

                if (fromDateSelection != null && lastBillCycleDate != null)
                    applyFilter.setEnabled(true);
            }
        });

        resetFilter = (VodafoneButton) filterDialog.findViewById(R.id.reset_filters_button);
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultFilterInit();
                String fromDate = String.valueOf(fromDateSelection.getTime());
                String toDate = String.valueOf(lastBillCycleDate.getTime());

                getLoyaltyPoints(toDate, fromDate);
                LoyaltyReceivedPointsTab receivedPointsTab = (LoyaltyReceivedPointsTab) VodafoneController.findFragment(LoyaltyReceivedPointsTab.class);

                args.putBoolean("isFromFilter", true);

                if (receivedPointsTab != null) {
                    receivedPointsTab.setArguments(args);
                    receivedPointsTab.getReceivedPoints();
                }

                LoyaltyConsumedPointsTab consumedPointsTab = (LoyaltyConsumedPointsTab) VodafoneController.findFragment(LoyaltyConsumedPointsTab.class);
                if (consumedPointsTab != null) {
                    consumedPointsTab.setArguments(args);
                    consumedPointsTab.getConsumedPoints();
                }
                filterDialog.dismiss();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastBillCycleDate != null
                        && fromDateSelection != null) {

                    String fromDate = String.valueOf(fromDateSelection.getTime());
                    String toDate = String.valueOf(lastBillCycleDate.getTime());

                    getLoyaltyPoints(toDate, fromDate);
                    LoyaltyReceivedPointsTab receivedPointsTab = (LoyaltyReceivedPointsTab) VodafoneController.findFragment(LoyaltyReceivedPointsTab.class);
                    args.putBoolean("isFromFilter",true);
                    if (receivedPointsTab != null) {
                        receivedPointsTab.setArguments(args);
                        receivedPointsTab.getReceivedPoints();
                    }

                    LoyaltyConsumedPointsTab consumedPointsTab = (LoyaltyConsumedPointsTab) VodafoneController.findFragment(LoyaltyConsumedPointsTab.class);
                    if (consumedPointsTab != null) {
                        receivedPointsTab.setArguments(args);
                        consumedPointsTab.getConsumedPoints();
                    }
                    filterDialog.hide();
                }
            }
        });



        LoyaltyHistoryFilterTrackingEvent event = new LoyaltyHistoryFilterTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    private void defaultFilterInit() {
        Log.d(TAG, "defaultFilterInit: ");
        longLastCycle = String.valueOf(getLastBillCycleDate().getTime());
        longMinusMonth = String.valueOf(dateMinusMonth(getLastBillCycleDate()).getTime());

        lastBillCycleDate = getLastBillCycleDate();
        lastBillCycleInit = lastBillCycleDate;
        fromDateSelection = LoyaltyPointsFilter.dateMinusMonth(lastBillCycleDate);

        textFilterFrom.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(fromDateSelection));
        textFilterTo.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO")).format(lastBillCycleInit));

        calendarView.initControl(getContext(), lastBillCycleDate, fromDateSelection, lastBillCycleInit);
    }

    private Date getLastBillCycleDate() {
        Calendar calendar;
        Profile profile;

        int billCycleDate;
        int currentDayOfMonth;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        profile = (Profile) RealmManager.getRealmObject(Profile.class);

        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        billCycleDate = profile.getBillCycleDate();

        if (billCycleDate > currentDayOfMonth) {
            calendar.add(Calendar.MONTH, -1);
            calendar.add(Calendar.DAY_OF_MONTH, +(billCycleDate - currentDayOfMonth));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfMonth - billCycleDate));
        }

        return calendar.getTime();
    }

    private void atachCardToHeader() {
        LoyaltyHistoryHeaderCard headerCard = new LoyaltyHistoryHeaderCard(getContext());
        activity.getNavigationHeader().removeViewFromContainer();
        activity.getNavigationHeader().addViewToContainer(headerCard);
    }

    private void getLoyaltyPoints(final String toDate, final String fromDate) {
        showLoading();
        Log.d(TAG, "getLoyaltyPoints: from:" + fromDateSelection);
        Log.d(TAG, "getLoyaltyPoints: to: " + lastBillCycleDate);
        Log.d(TAG, "getLoyaltyPoints: fromDate" + fromDate);
        Log.d(TAG, "getLoyaltyPoints: toDate" + toDate);
        ShopService shopService = new ShopService(getContext());
        shopService.getLoyaltyPoints(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), toDate, fromDate, activity.getShopSessionToken())
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<ShopLoyaltyPointsSuccess>>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideLoading();
                        showError();
                    }

                    @Override
                    public void onNext(GeneralResponse<ShopLoyaltyPointsSuccess> response) {
                        super.onNext(response);
                        if (response.getTransactionStatus() != 0) {
                            hideLoading();
                            showError();
                        } else {
                            //determineTotalPoints(response.getTransactionSuccess(), toDate, fromDate);
                            setupTabs();
                            Log.d(TAG, "onNext: " + visibleTabTag);
                            mTabHost.setCurrentTabByTag(visibleTabTag);
                            hideLoading();
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

    }

    private void setDataInHeaderCard() {
        activity.getNavigationHeader().hideSelectorView();

        ShopLoyaltyProgramSuccess shopLoyaltyProgramSuccess = (ShopLoyaltyProgramSuccess) RealmManager.getRealmObject(ShopLoyaltyProgramSuccess.class);
        if (shopLoyaltyProgramSuccess != null) {
            if (shopLoyaltyProgramSuccess.getAccountBalance() != null && !shopLoyaltyProgramSuccess.getAccountBalance().equals("0")) {
                LoyaltyHistoryHeaderController headerCardController = LoyaltyHistoryHeaderController.getInstance();
                LoyaltyHistoryHeaderController.getInstance().setup(new LoyaltyHistoryHeaderCard(getContext()));
                atachCardToHeader();
                headerCardController.onDataLoaded(shopLoyaltyProgramSuccess);
            }
        }
    }

    private Date dateMinusMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private void showLoading() {
        Log.d(TAG, "showLoading: ");
        if (view.getChildCount() != 0) {
            for (int i = 0; i < view.getChildCount(); i++) {
                view.getChildAt(i).setVisibility(View.GONE);
            }
        }

        loadingCard = new VodafoneGenericCard(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingCard.setLayoutParams(layoutParams);
        view.addView(loadingCard);
        loadingCard.showLoading(true);

    }

    private void hideLoading() {
        if (view.getChildCount() != 0) {
            for (int i = 0; i < view.getChildCount(); i++) {
                view.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
        loadingCard.hideLoading();
        view.removeView(loadingCard);

    }

    View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            hideError();

            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
            if(profile != null) {
                initialViews();
                initialLoyaltyPoints();
            } else {
                showError();
                return;
            }

            if (fromDateSelection != null && lastBillCycleDate != null) {
                String fromDate = String.valueOf(fromDateSelection.getTime());
                String toDate = String.valueOf(lastBillCycleDate.getTime());
                getLoyaltyPoints(toDate, fromDate);
            } else {
                initialLoyaltyPoints();
            }


        }
    };

    private void initialViews() {
        Log.d(TAG, "initialViews: ");
        Button filterButton = (VodafoneButton) view.findViewById(R.id.filter_button);

        filterButton.setOnClickListener(filterListener);

        filterDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        filterDialog.setContentView(R.layout.loyalty_history_filter_layout);
    }

    private void initialLoyaltyPoints() {

        longLastCycle = String.valueOf(getLastBillCycleDate().getTime());
        longMinusMonth = String.valueOf(dateMinusMonth(getLastBillCycleDate()).getTime());

        getLoyaltyPoints(longLastCycle, longMinusMonth);
    }

    private void showError() {
        try {
            cardContainer.setVisibility(View.GONE);
            layoutContainer.setVisibility(View.GONE);
            errorCard = new VodafoneGenericCard(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.addView(errorCard, layoutParams);

            errorCard.showError(true, "Serviciu momentan indisponibil.\n Apasă pentru a reîncerca");
            errorCard.getErrorView().setOnClickListener(errorClickListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideError() {
        cardContainer.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.VISIBLE);
        errorCard.hideError();
        view.removeView(errorCard);
        initialViews();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getNavigationHeader().setTitle(LoyaltyLabels.getLoyaltyPointsActivityPageTitle());
        activity.getNavigationHeader().removeViewFromContainer();
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.LOYALTY_TRANSACTION_HISTORY);
    }

    public static class LoyaltyHistoryTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty transaction history";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty transaction history");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class LoyaltyHistoryFilterTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty transaction filters overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty transaction filters overlay");


            s.channel = "loyalty program";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}

