package ro.vodafone.mcare.android.ui.fragments.offers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.Hosts;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AncomOffersService;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;
import ro.vodafone.mcare.android.ui.views.banners.OffersBanner;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Go;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class OffersSelectionPageFragment extends OffersFragment {

    public static final String TAG = "OffersSelectionPageF";
    //public static final String EXTRA_PARAMETER_BUNDLE_KEY = "extraParameter";
    //public RealmList<AncomOffer> listOffers;
    List<SelectionPageButton> selectionPageButtons;
    //VodafoneTextView title;
    boolean redirectToBeoFromGauge;
    boolean redirectToBeoFromGaugeForCPCOfferFlag;
    AncomPendingOffersSuccess ancomPendingOffersSuccess;
    private Context context;
    private LinearLayout fragmentContainer;
    private NavigationHeader navigationHeader;
    //private long mLastClickTime = 0;
    private int runningTasks;
    private View inflatedView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OffersActivity offersActivity = (OffersActivity) getActivity();
        navigationHeader = offersActivity.getNavigationHeader();
        navigationHeader.buildMsisdnSelectorHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        showLoadingDialog();
        inflatedView = inflater.inflate(R.layout.selection_page_fragment, container, false);

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.offersJourney);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.offersScreen);
        if(VodafoneController.getInstance().getUserProfile().getUserRole() != null  &&
                VodafoneController.getInstance().getUserProfile().getUserRole().getDescription() != null )
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        String msisdn = null;
        if (!(VodafoneController.getInstance().getUser() instanceof PrepaidUser)
                && !(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
            if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null) {
                msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
            }
            if (msisdn != null)
                getAncomOffers(msisdn);
            else
                getAncomOffers(VodafoneController.getInstance().getUserProfile().getMsisdn());
        } else {
            inflateLayout();
        }
        return inflatedView;

    }

    private boolean isDataReady() {
        return (--runningTasks == 0);
    }

    public void inflateLayout() {

        context = getContext();

        fragmentContainer = (LinearLayout) inflatedView.findViewById(R.id.selection_page_container);

        if(fragmentContainer.getChildCount() > 0){
            fragmentContainer.removeAllViews();
        }

        buildButtonsList();
        setupButtons();
        getBannerOffers();

        Log.d(TAG, "onCreateView" + redirectToBeoFromGauge);

        ExtraOfferTrackingEvent event = new ExtraOfferTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        navigationHeader.hideSelectorView();
        navigationHeader.showBannerView();
        //mLastClickTime = SystemClock.elapsedRealtime();

        ((OffersActivity)getActivity()).setTitle(getTitle());
    }

    private List<SelectionPageButton> buildButtonsList() {

        selectionPageButtons = new ArrayList<>();

        if (canSeePhoneAndSubscriptionButton()) {
            //Telefoane si Abonament

            Object target;
            if (VodafoneController.getInstance().getUser()instanceof EbuMigrated){
                target = AppConfiguration.getNewEshopMsiteUrl();
            }else{
                target = IntentActionName.RETENTION;
            }

            selectionPageButtons.add(
                    new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                            OffersLabels.getOffers_for_you_phone_and_subscription_button_label(), null, target,
                            DrawableUtils.generateCircleIcon(ContextCompat.getColor(context, R.color.product_card_primary_oceanblue),
                                    Color.WHITE, ContextCompat.getDrawable(context, R.drawable.shopping_trolley_01), context)));
        }

        //Bonusuri si optiuni
        selectionPageButtons.add(
                new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                        OffersLabels.getOffers_for_you_beo_button_label(), null, IntentActionName.OFFERS_BEO_NO_SEAMLESS,
                        DrawableUtils.generateCircleIcon(ContextCompat.getColor(context, R.color.product_card_primary_oceanblue),
                                Color.WHITE, ContextCompat.getDrawable(context, R.drawable.shopping), context)));


        //Servicii
        selectionPageButtons.add(
                new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                        OffersLabels.getOffers_for_you_services_button_label(), null, IntentActionName.OFFERS_SERVICES,
                        DrawableUtils.generateCircleIcon(ContextCompat.getColor(context, R.color.product_card_primary_oceanblue),
                                Color.WHITE, ContextCompat.getDrawable(context, R.drawable.play_circle), context)));

        //Oferte in asteptare
        if (canSeePendingOffers()) {
            selectionPageButtons.add(
                    new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                            OffersLabels.getOffers_for_you_pending_offers(), null, IntentActionName.ANCOM_PENDING_OFFERS,
                            DrawableUtils.generateCircleIcon(ContextCompat.getColor(context, R.color.product_card_primary_oceanblue),
                                    Color.WHITE, ContextCompat.getDrawable(context, R.drawable.clock_or_timed_48), context)));
        }

        return null;
    }

    private void setupButtons() {

        for (final SelectionPageButton button : selectionPageButtons) {
            button.addButton(fragmentContainer);

            button.getLayoutView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // click for SelectionPageButton
                    Map<String, Object> tealiumMapEvent = new HashMap(6);
                    tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.offersScreen);
                    tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.offersExtrasSelectionButton+button.getButtonTittle());
                    tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackEvent(OffersSelectionPageFragment.this.getClass().getSimpleName(), tealiumMapEvent);

                    if (button.getTarget() instanceof IntentActionName) {
                        new NavigationAction(getContext()).startAction((IntentActionName) button.getTarget());
                    }else if(button.getTarget() instanceof String){
                        Go.browser(getContext(), (String) button.getTarget());
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        navigationHeader.removeViewFromContainer();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getBannerOffers() {
        runningTasks++;
        if (redirectToBeoFromGaugeForCPCOfferFlag) {
            redirectToBeoFromGaugeForCPCOfferFlag = false;
        } else if (redirectToBeoFromGauge) {
            redirectToBeoFromGauge = false;
        } else {
            if(VodafoneController.getInstance().getUserProfile() == null) {
                if(isDataReady()) {
                    stopLoadingDialog();
                }
                return;
            }

            OffersService offersService = new OffersService(context);

            offersService.getBannerOffers().subscribe(new RequestSaveRealmObserver<BannerOffersSuccess>() {

                @Override
                public void onNext(BannerOffersSuccess bannerOffersSuccess) {
                    super.onNext(bannerOffersSuccess);
                    Log.d(TAG, "bannerOffersSuccess - onNext()");

                    if (bannerOffersSuccess != null) {
                        Log.d(TAG, "bannerOffersSuccess  - Transaction Success");
                        if (!bannerOffersSuccess.getBannerOffers().isEmpty()) {

                            List<BannerOffer> bannerOffers = bannerOffersSuccess.getBannerOffers();
                            Log.d(TAG, "bannerOffersSuccess  - Transaction Success List size: " + bannerOffers.size());
                            setupBanner(bannerOffers);

                        } else {
                            Log.e(TAG, "bannerOffersSuccess but no offers");
                        }
                    } else {
                        Log.e(TAG, "bannerOffersSuccess but no offers, null response");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Log.e(TAG, "bannerOffersSuccess errors", e);
                    if(isDataReady())
                        stopLoadingDialog();
                }

                @Override
                public void onCompleted() {
                    if(isDataReady())
                        stopLoadingDialog();
                }
            });
        }
    }

    private void setupBanner(List<BannerOffer> bannerOffers) {
        /*List<String> imageList = new ArrayList<>();
        for (BannerOffer bannerOffer : bannerOffers) {
            imageList.add(bannerOffer.getBannerImg());
        }*/
        navigationHeader.removeViewFromContainer();
        navigationHeader.addViewToContainer(
                new OffersBanner(context)
                        .setConfiguredOffersList(bannerOffers)
                        .buildBanner()
        );
    }

    public String getTitle() {
        String title;
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            title = OffersLabels.getOffers_for_you_page_prepaid_title();
        }else{
            title = OffersLabels.getOffers_for_you_page_postpaid_title();
        }
        return title;
    }

    public boolean canSeePendingOffers() {
        ancomPendingOffersSuccess = (AncomPendingOffersSuccess) RealmManager.getRealmObject(AncomPendingOffersSuccess.class);

        if (accessToPendingOffersEvenEmpty())
            return true;

        boolean canSeePendingOffersVar = false;

        if (accessToPendingOffersNotEmpty()) {
            canSeePendingOffersVar = true;
        }
        return canSeePendingOffersVar;
    }

    private boolean canSeePhoneAndSubscriptionButton(){
        User user = VodafoneController.getInstance().getUser();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        return (user instanceof CBUUser || user instanceof PrepaidHybridUser
                || user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPostPaidsLowAccess
                || user instanceof SeamlessPrepaidHybridUser)
                || (user instanceof EbuMigrated && AppConfiguration.isAbleToAccesRetention(entityChildItem.getCrmRole()));
    }

    private boolean accessToPendingOffersNotEmpty() {
        return ancomPendingOffersSuccess != null
                && !ancomPendingOffersSuccess.getAncomOffers().isEmpty()
                && ((VodafoneController.getInstance().getUser() instanceof PrivateUser) ||
                (VodafoneController.getInstance().getUser() instanceof ResSub) ||
                (VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidsLowAccess) ||
                (VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidHighAccess));
    }

    private boolean accessToPendingOffersEvenEmpty() {
        return VodafoneController.getInstance().getUser() instanceof ResCorp
                || VodafoneController.getInstance().getUser() instanceof SeamlessPostPaidHighAccess;
    }

    public void getAncomOffers(String msisdn) {
        runningTasks++;
        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }
        final AncomOffersService ancomOffersService = new AncomOffersService(VodafoneController.getInstance());
        showLoadingDialog();

        ancomOffersService.getVOSPendingOffers(msisdn).subscribe(new RequestSaveRealmObserver<GeneralResponse<AncomPendingOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<AncomPendingOffersSuccess> ancomOffersSuccessGeneralResponse) {
                super.onNext(ancomOffersSuccessGeneralResponse);
                if(isDataReady())
                    stopLoadingDialog();
                if(getActivity() != null &&
                        FragmentUtils.getVisibleFragment((OffersActivity) getActivity(), false) instanceof OffersSelectionPageFragment )
                    inflateLayout();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("getAncomOffers", "onError");
                if(isDataReady())
                    stopLoadingDialog();
                if(getActivity() != null &&
                        FragmentUtils.getVisibleFragment((OffersActivity) getActivity(), false) instanceof OffersSelectionPageFragment )
                    inflateLayout();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_OFFERS_EXTRAS);
    }

    public static class ExtraOfferTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "offers and extras";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "offers and extras");


            s.channel = "offers and extras";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "buy options";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
