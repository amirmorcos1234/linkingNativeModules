package ro.vodafone.mcare.android.ui.activities.loyalty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.ReservedPromotion;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.LoyaltySegmentRequest;
import ro.vodafone.mcare.android.service.LoyaltyMarketService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;
import ro.vodafone.mcare.android.ui.views.banners.VoucherBanner;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.functions.Func1;

import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.IS_HERO_OFFER;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.IS_RESERVED_KEY;
import static ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion.PRIORITY_KEY;

/**
 * Created by Victor Radulescu on 8/24/2017.
 */

public class LoyaltyActivity extends MenuActivity {

    private static final String TAG = LoyaltyActivity.class.getSimpleName();

    private Class[] voucherDataClasses = {LoyaltyVoucherSuccess.class, LoyaltyVoucherReservedSuccess.class, Promotion.class,
            ReservedPromotion.class};

    private NavigationHeader navigationHeader;
    private List<SelectionPageButton> selectionPageButtons;
    private LinearLayout selectionPageLayout;
    private LoyaltyMarketService loyaltyMarketService;
    private VoucherBanner voucherBanner;

    private String crmRole;
    private String treatmentSegment = null;
    private LoyaltySegmentRequest loyaltySegmentRequest;

    private boolean isGetVouchersFinished;
    private boolean isGetReservedVouchersFinished;

    private Long serverSysDate;
    public Realm realm;
    String lpsSegment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty);
        realm = Realm.getDefaultInstance();
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        selectionPageLayout = (LinearLayout) findViewById(R.id.selection_page_container);

        loyaltyMarketService = new LoyaltyMarketService(LoyaltyActivity.this);

        initNavigationHeader();
        buildButtonsList();
        setupButtons();
        getUserInfoForRequests();
        isGetVouchersFinished = false;
        isGetReservedVouchersFinished = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopSlider();
        if (checkIfEbuIsVdfSubscriptionOrCbu()) {
            clearVoucherData();
            getLoyaltySegment();
            getReservedVouchers();
        } else {
            if (navigationHeader != null) {
                navigationHeader.removeViewFromContainer();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty);

        if(realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        if (navigationHeader != null) {
            navigationHeader.removeViewFromContainer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
    }

    private void initNavigationHeader() {
        Log.d(TAG, "initNavigationFragment ");
        navigationHeader.setActivity(this).displayDefaultHeader();
        setTitle();
    }

    @Override
    protected int setContent() {
        return R.layout.activity_loyalty;
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {

    }

    public void setTitle() {
        String title = LoyaltyLabels.getLoyaltyActivitySelectionPageTitle();
        setTitle(title);
    }

    public void setTitle(String text) {
        try {
            navigationHeader.setTitle(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");
        }
    }

    void getUserInfoForRequests() {
        crmRole = "";
        treatmentSegment = "";
        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            treatmentSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
            Log.d(TAG, "crmRole: " + crmRole);
            Log.d(TAG, "treatmentSegment: " + treatmentSegment);


            List<Ban> banList = new ArrayList<>();
            List<String> banListString = new ArrayList<>();
            banList = UserSelectedMsisdnBanController.getInstance().getBanList();
            if (banList != null) {
                for (Ban ban : banList) {
                    banListString.add(ban.getNumber());
                }
                String[] ebuBanList = banListString.toArray(new String[banList.size()]);
                loyaltySegmentRequest = new LoyaltySegmentRequest(ebuBanList);
            } else {
                loyaltySegmentRequest = new LoyaltySegmentRequest(null);
            }
        }
    }

    void clearVoucherData() {
        RealmManager.deleteMultiple(realm, voucherDataClasses);
    }

    private void getLoyaltySegment() {
        loyaltyMarketService.getLoyaltySegment(crmRole, loyaltySegmentRequest)
                .flatMap(new Func1<GeneralResponse<LoyaltySegmentSuccess>, Observable<GeneralResponse<LoyaltyVoucherSuccess>>>() {
                    @Override
                    public Observable<GeneralResponse<LoyaltyVoucherSuccess>> call(GeneralResponse<LoyaltySegmentSuccess> loyaltySegmentSuccessGeneralResponse) {
                        RequestSaveRealmObserver.save(loyaltySegmentSuccessGeneralResponse);
                        if (loyaltySegmentSuccessGeneralResponse.getTransactionSuccess().getLpsSegment() != null) {
                            lpsSegment = loyaltySegmentSuccessGeneralResponse.getTransactionSuccess().getLpsSegment();
                        }
                        return loyaltyMarketService.getLoyaltyVoucherList(lpsSegment, treatmentSegment, crmRole);
                    }
                }).subscribe(new RequestSessionObserver<GeneralResponse<LoyaltyVoucherSuccess>>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                Log.d(TAG, "onCompleted getLoyaltySegment");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "onError getLoyaltySegment");
                isGetVouchersFinished = true;
                if (VodafoneController.isActivityVisible(LoyaltyActivity.this)) {
                    if (isGetReservedVouchersFinished) {
                        checkVouchersForBanner();
                    }
                }
            }

            @Override
            public void onNext(GeneralResponse<LoyaltyVoucherSuccess> response) {
                isGetVouchersFinished = true;
                if (VodafoneController.isActivityVisible(LoyaltyActivity.this)) {
                    RequestSaveRealmObserver.save(response);
                    if (isGetReservedVouchersFinished) {
                        checkVouchersForBanner();
                    }
                }
                Log.d(TAG, "onNext getLoyaltySegment");
            }
        });
    }

    void getReservedVouchers() {
        loyaltyMarketService.getReservedLoyaltyVoucherList()
                .subscribe(new RequestSessionObserver<GeneralResponse<LoyaltyVoucherReservedSuccess>>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "onCompleted getReservedLoyaltyVoucherList");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "onError getReservedLoyaltyVoucherList");
                        isGetReservedVouchersFinished = true;
                        if (VodafoneController.isActivityVisible(LoyaltyActivity.this)) {
                            if (isGetVouchersFinished) {
                                checkVouchersForBanner();
                            }
                        }
                    }

                    @Override
                    public void onNext(GeneralResponse<LoyaltyVoucherReservedSuccess> response) {
                        isGetReservedVouchersFinished = true;
                        if (VodafoneController.isActivityVisible(LoyaltyActivity.this)) {
                            RequestSaveRealmObserver.save(response);
                            if (isGetVouchersFinished) {
                                checkVouchersForBanner();
                            }
                        }
                        Log.d(TAG, "onNext getReservedLoyaltyVoucherList");
                    }
                });
    }

    private void checkVouchersForBanner() {
        setupServerDate();
        String maxMarketBanners = AppConfiguration.getMarketMaxNumberBanners();
        int parsedMaxMarketBanners = -1;
        try {
            parsedMaxMarketBanners = Integer.parseInt(maxMarketBanners);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parsedMaxMarketBanners == 0) {
            return;
        }

        LoyaltyVoucherSuccess loyaltyVoucherSuccess;
        RealmList<Promotion> loyaltyVoucherList = null;

        LoyaltyVoucherReservedSuccess loyaltyVoucherReservedSuccess;
        RealmList<ReservedPromotion> loyaltyReservedVoucherList = null;

        try {
            loyaltyVoucherSuccess = (LoyaltyVoucherSuccess) RealmManager.getRealmObject(realm, LoyaltyVoucherSuccess.class);
            if (loyaltyVoucherSuccess != null && loyaltyVoucherSuccess.getPromotions() != null) {
                loyaltyVoucherList = loyaltyVoucherSuccess.getPromotions();
            }

            loyaltyVoucherReservedSuccess = (LoyaltyVoucherReservedSuccess) RealmManager.getRealmObject(realm,
                    LoyaltyVoucherReservedSuccess.class);
            if (loyaltyVoucherReservedSuccess != null && loyaltyVoucherReservedSuccess.getPromotions() != null) {
                loyaltyReservedVoucherList = loyaltyVoucherReservedSuccess.getPromotions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (loyaltyVoucherList != null && loyaltyVoucherList.size() != 0) {

            RealmResults<Promotion> promotions = realm
                    .where(Promotion.class)
                    .equalTo(IS_HERO_OFFER, "true")
                    .equalTo(IS_RESERVED_KEY, false)
                    .greaterThan(Promotion.CAMPAIGN_EXPIRY_DATE_KEY, serverSysDate)
                    .findAllSorted(PRIORITY_KEY, Sort.ASCENDING);

            List<Promotion> heroVoucherList = new ArrayList<>(promotions);

            setupBanner(heroVoucherList, true, false);

        } else if (loyaltyReservedVoucherList != null && loyaltyReservedVoucherList.size() != 0) {
            setupBanner(null, false, true);
        } else {
            setupBanner(null, false, false);
        }
    }

    private void setupBanner(List<Promotion> heroVoucherList, boolean redirectToNewVoucher, boolean redirectToReservedVoucher) {

        if (heroVoucherList != null || redirectToNewVoucher || redirectToReservedVoucher) {

            if (navigationHeader != null) {
                voucherBanner = new VoucherBanner(this)
                        .setRedirectToNewVoucherListing(redirectToNewVoucher)
                        .setRedirectToOwnVoucherListing(redirectToReservedVoucher)
                        .setConfiguredOffersList(heroVoucherList)
                        .buildBanner();

                navigationHeader.removeViewFromContainer();
                navigationHeader.addViewToContainer(voucherBanner);
                startSlider();
            }
        }
    }

    private void startSlider() {
        if (voucherBanner != null && voucherBanner.isStopSliding()) {
            voucherBanner.setStopSliding(false);
        }
    }

    public void stopSlider() {
        if (voucherBanner != null && !voucherBanner.isStopSliding()) {
            voucherBanner.setStopSliding(true);
        }
    }

    private void buildButtonsList() {

        selectionPageButtons = new ArrayList<>();
        String pageTitle = LoyaltyLabels.getLoyaltyMarketActivityPageTitle();
        if(AppConfiguration.getSuperRedKey().equalsIgnoreCase(getLpsSegment())) {
            pageTitle = LoyaltyLabels.getLoyaltyMarketSuperRedPageTitle();
        }
        //Vodafone Market
        selectionPageButtons.add(
                new SelectionPageButton(this, ContextCompat.getColor(this, R.color.product_card_primary_dark_blue),
                        pageTitle,
                        LoyaltyLabels.getLoyaltyMarketCardDescription(),
                        IntentActionName.LOYALTY_MARKET,
                        ContextCompat.getDrawable(this, R.drawable.icon_vdf_market)));

        if (!(VodafoneController.getInstance().getUser() instanceof PrepaidUser)
                && !(VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)
                && !(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
            //Puncte de loialitate
            selectionPageButtons.add(
                    new SelectionPageButton(this, ContextCompat.getColor(this, R.color.product_card_primary_dark_blue),
                            LoyaltyLabels.getLoyaltyPointsPageCardTitle(),
                            LoyaltyLabels.getLoyaltyPointsCardDescription(),
                            IntentActionName.LOYALTY_PROGRAM,
                            ContextCompat.getDrawable(this, R.drawable.icon_loyalty)));
        }
    }

    private void setupButtons() {

        for (final SelectionPageButton button : selectionPageButtons) {
            button.addButton(selectionPageLayout);

            button.getLayoutView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    D.d("CLICKED " + selectionPageButtons.indexOf(button));

                    trackEvent(TealiumConstants.loyalty_selection_button+button.getButtonTittle().toLowerCase());
                    if (button.getTarget() instanceof IntentActionName) {
                        stopSlider();
                        new NavigationAction(LoyaltyActivity.this).startAction((IntentActionName) button.getTarget(), false);
                    }
                }


            });
        }
    }

    private void trackEvent(String event){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
    private boolean checkIfEbuIsVdfSubscriptionOrCbu() {
        User user = VodafoneController.getInstance().getUser();
        ProfileSubscriptionSuccess profileSubscriptionSuccess = (ProfileSubscriptionSuccess) RealmManager
                .getRealmObject(realm, ProfileSubscriptionSuccess.class);

        if (user instanceof EbuMigrated) {
            if (((EbuMigrated) user).isAuthorisedPerson()
                    || ((EbuMigrated) user).isChooser()
                    || ((EbuMigrated) user).isDelagatedChooser()) {
                if (profileSubscriptionSuccess != null
                        && profileSubscriptionSuccess.getIsVDFSubscription() != null
                        && profileSubscriptionSuccess.getIsVDFSubscription()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupServerDate() {
        LoyaltyVoucherReservedSuccess loyaltyVoucherReservedSuccess = (LoyaltyVoucherReservedSuccess) RealmManager
                .getRealmObject(realm, LoyaltyVoucherReservedSuccess.class);
        LoyaltyVoucherSuccess loyaltyVoucherSuccess = (LoyaltyVoucherSuccess) RealmManager
                .getRealmObject(realm, LoyaltyVoucherSuccess.class);

        if (loyaltyVoucherReservedSuccess != null) {
            serverSysDate = loyaltyVoucherReservedSuccess.getSysdate();
        } else if (loyaltyVoucherSuccess != null) {
            serverSysDate = loyaltyVoucherSuccess.getSysdate();
        } else {
            serverSysDate = System.currentTimeMillis();
        }
    }

    public static class LoyaltyProgramTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty program";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty program");

            s.channel = "loyalty";
            s.getContextData().put("&&channel", s.channel);

            s.events = "event3";
            s.getContextData().put("event3", s.event3);
            s.events = "event5";
            s.getContextData().put("event5", s.event5);
            s.events = "event6";
            s.getContextData().put("event6", s.event6);
            s.events = "event7";
            s.getContextData().put("event7", s.event7);

            s.prop5 = "content";
            s.getContextData().put("prop5", s.prop5);

            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            s.eVar73 = "loyalty";
            s.getContextData().put("eVar73", s.eVar73);
        }
    }

    private String getLpsSegment() {

        LoyaltySegmentSuccess loyaltySegment = (LoyaltySegmentSuccess) RealmManager.getRealmObject(realm, LoyaltySegmentSuccess.class);

        if (loyaltySegment != null) {
            return loyaltySegment.getLpsSegment();
        }
         return  null;
    }
}
