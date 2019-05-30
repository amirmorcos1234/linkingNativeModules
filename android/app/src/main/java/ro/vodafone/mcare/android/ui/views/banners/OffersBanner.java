package ro.vodafone.mcare.android.ui.views.banners;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.BannerImageSlideAdapter;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOfferType;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.ui.views.CirclePageIndicator;
import ro.vodafone.mcare.android.ui.views.PageIndicator;
import ro.vodafone.mcare.android.ui.views.WrapContentHeightViewPager;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 14.03.2017.
 */
public class OffersBanner extends LinearLayout {
    private static final String TAG = "OffersBanner";

    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private Context mContext;

    private WrapContentHeightViewPager mViewPager;
    private PageIndicator mIndicator;

    private List<BannerOffer> configuredOffersList;
    private List<BannerOffer> finalOffersList;
    private List<OfferRowInterface> offerRowInterfaceList;

    private Runnable animateViewPager;
    private Handler handler;
    boolean stopSliding = false;

    public OffersBanner(Context context) {
        super(context);
        this.mContext = context;
        init(null);

    }

    public OffersBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    public OffersBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OffersBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    private void init(AttributeSet attrs) {

        System.out.println("init banner");
        inflate(mContext, R.layout.offers_banner, this);


        finalOffersList = new ArrayList<>();
        offerRowInterfaceList = new ArrayList<>();
        mViewPager = (WrapContentHeightViewPager) findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        this.setVisibility(GONE);
    }

    public OffersBanner setConfiguredOffersList(List<BannerOffer> offersList) {
        this.configuredOffersList = offersList;
        return this;
    }

    public OffersBanner buildBanner() {
        getOffers();
        return this;
    }

    private void setupViewPager(){
        Log.d(TAG, "setupViewPager");

        if(!finalOffersList.isEmpty()){
            Log.d(TAG, "not empty list");

			//if size of offerList is bigger than 3 then return 3 else return offerList size .
			if(finalOffersList.size() > 3)
                finalOffersList = finalOffersList.subList(0, 3);

            //sort List
            sortBannerList();

            this.setVisibility(VISIBLE);

            mViewPager.setAdapter(new BannerImageSlideAdapter(getContext(), finalOffersList));
            mViewPager.getParent().requestDisallowInterceptTouchEvent(false);

            setupPageIndicator(finalOffersList.size());

            runnable(finalOffersList.size());
            handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);

            mViewPager.addOnPageChangeListener(pageChangeListener);
        }
    }
    private void setupPageIndicator(int size){
        mIndicator.setViewPager(mViewPager);
        try {
            if (size < 2) {
                ((CirclePageIndicator) mIndicator).setVisibility(GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sortBannerList(){
        try {
            Collections.sort(finalOffersList,new Comparator<BannerOffer>() {
                @Override
                public int compare(BannerOffer s1, BannerOffer s2) {
                    return s1.getPriority().compareTo(s2.getPriority());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            handler.removeCallbacks(animateViewPager);
            handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void getOffers(){
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser || VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser) {
            EligibleOffersSuccess eligibleOffersSuccess = (EligibleOffersSuccess) RealmManager.getRealmObject(EligibleOffersSuccess.class);
            if(eligibleOffersSuccess != null){
                prepareOffer4PrepaidList(eligibleOffersSuccess);
            }else{
                getEligibleOffers4PrePaid();
            }
        } else if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);
            if(eligibleOffersPostSuccess != null){
                preparePostOffersList(eligibleOffersPostSuccess);
            }else{
                getEligibleOffers4PostPaid(VodafoneController.getInstance().getUserProfile().getMsisdn(), VodafoneController.getInstance().getUserProfile().getSid(), profile.getBillCycleDate());
            }
        } else if (VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);
            if(eligibleOffersPostSuccess != null){
                preparePostOffersList(eligibleOffersPostSuccess);
            }else{
                if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null)
                getEligibleOffers4EBU(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole(),
                        UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn());
            }
        }
    }

    private void getEligibleOffers4PostPaid(String vfPhoneNumber, final String sid, Integer bcd) {
        Log.d(TAG, "getEligibleOffers4PostPaid() number: " + vfPhoneNumber + "   sid: " + sid + "  bdc: " + bcd);

        if(bcd == null) {
            preparePostOffersList(null);
            return;
        }

        OffersService offersService = new OffersService(mContext);
        offersService.getEligibleOffers4PostPaid(vfPhoneNumber, sid, bcd).subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {
            @Override
            public void onNext(GeneralResponse<EligibleOffersPostSuccess> eligibleOffersPostSuccessResponse) {
                super.onNext(eligibleOffersPostSuccessResponse);
				preparePostOffersList(eligibleOffersPostSuccessResponse.getTransactionSuccess());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "getEligibleOffers4PostPaid ERROR");
                preparePostOffersList(null);
            }
        });
    }

    private void preparePostOffersList(EligibleOffersPostSuccess eligibleOffersPostSuccess){
        Log.d(TAG, "preparePostOffersList()");

        List<EligibleCategoriesPost> allOffersList = new ArrayList<>();

        if (eligibleOffersPostSuccess != null && eligibleOffersPostSuccess.getEligibleServicesCategories()
                != null && !eligibleOffersPostSuccess.getEligibleServicesCategories().isEmpty()) {
            allOffersList.addAll(eligibleOffersPostSuccess.getEligibleServicesCategories());
        }

        if (eligibleOffersPostSuccess != null && eligibleOffersPostSuccess.getEligibleOptionsCategories()
                != null && !eligibleOffersPostSuccess.getEligibleOptionsCategories().isEmpty()) {
            allOffersList.addAll(eligibleOffersPostSuccess.getEligibleOptionsCategories());
        }

        if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            checkCBUOfferIsEligible(allOffersList);
        }else {
            checkEBUOfferIsEligible(allOffersList);
        }
    }

    private void checkCBUOfferIsEligible(List<EligibleCategoriesPost> allOffersList) {
        Log.d(TAG, "checkIPostfOfferIsEligible receive allOffersList with size = " + allOffersList.size());

        if(configuredOffersList != null && !configuredOffersList.isEmpty()){
            for(BannerOffer bannerOffer : configuredOffersList){
                if(BannerOfferType.WEBVIEW.getType().equalsIgnoreCase(bannerOffer.getOfferType()) || BannerOfferType.WEBVIEW_CAMPAIGN.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                    addingOfferToList(bannerOffer);
                }else if (BannerOfferType.CPC.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                    if (!allOffersList.isEmpty()) {
                        for(EligibleCategoriesPost eligibleCategoriesPost : allOffersList){
                            for(PostpaidOfferRow offer : eligibleCategoriesPost.getEligibleOffersList()){
                                Log.d(TAG, "addingOfferRowToList category = " + eligibleCategoriesPost.getCategory());
                                Log.d(TAG, "addingOfferRowToList offer matrixId = " + offer.getMatrixId());
                                Log.d(TAG, "addingOfferRowToList bannerOffer offerId = " + bannerOffer.getOfferId());

                                if(offer.getMatrixId().toString().equals(bannerOffer.getOfferId())){
                                    addingOfferToList(bannerOffer);
                                }
                            }
                        }
                    }
                }
            }
        }

        setupViewPager();
    }

    private void checkEBUOfferIsEligible(List<EligibleCategoriesPost> allOffersList){
        Log.d(TAG, "checkEBUOfferIsEligible() receive allOffersList with size = " + allOffersList.size());

        if(configuredOffersList != null && !configuredOffersList.isEmpty()){
            for(BannerOffer bannerOffer : configuredOffersList){
                if(BannerOfferType.WEBVIEW.getType().equalsIgnoreCase(bannerOffer.getOfferType())
                        || BannerOfferType.WEBVIEW_CAMPAIGN.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                    addingOfferToList(bannerOffer);
                }else if(BannerOfferType.EPC.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                    for(EligibleCategoriesPost eligibleCategoriesPost : allOffersList){
                        for(PostpaidOfferRow offer : eligibleCategoriesPost.getEligibleOffersList()){
                            if(offer.getMatrixId().equals(bannerOffer.getOfferId())){
                                addingOfferToList(bannerOffer);
                                break;
                            }
                        }
                    }
                }
            }
        }
        setupViewPager();
    }

    private void getEligibleOffers4PrePaid() {
        Log.d(TAG, "getEligibleOffers4PrePaid ");

        OffersService offersService = new OffersService(mContext);
        offersService.getEligibleOffers4PrePaid().subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                super.onNext(eligibleOffersSuccessResponse);
                prepareOffer4PrepaidList(eligibleOffersSuccessResponse.getTransactionSuccess());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "getEligibleOffers4PrePaid ERROR");
				prepareOffer4PrepaidList(null);
            }
        });
    }

    private void prepareOffer4PrepaidList(EligibleOffersSuccess eligibleOffersSuccess){
        Log.d(TAG, "prepareOffer4PrepaidList()");
        List<EligibleCategories> allOffersList = new ArrayList<>();

        if (eligibleOffersSuccess != null && eligibleOffersSuccess.getEligibleOptionsCategories() != null && !eligibleOffersSuccess.getEligibleOptionsCategories().isEmpty()) {
            allOffersList.addAll(eligibleOffersSuccess.getEligibleOptionsCategories());
        } else {
            Log.d(TAG, "getEligibleOffers4PrePaid ERROR");
        }

        if (eligibleOffersSuccess != null && eligibleOffersSuccess.getEligibleServicesCategories() != null && !eligibleOffersSuccess.getEligibleServicesCategories().isEmpty()) {
            allOffersList.addAll(eligibleOffersSuccess.getEligibleServicesCategories());
        } else {
            Log.d(TAG, "getEligibleOffers4PrePaid ERROR");
        }
        checkPrepaidOfferIsEligible(allOffersList);
    }

    private void checkPrepaidOfferIsEligible(List<EligibleCategories> offers) {
        Log.d(TAG, "checkIfOfferIsEligible");

        Log.d(TAG, "offers size = " + offers.size());

        for(BannerOffer bannerOffer : configuredOffersList){
            if(BannerOfferType.WEBVIEW.getType().equalsIgnoreCase(bannerOffer.getOfferType()) || BannerOfferType.WEBVIEW_CAMPAIGN.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                addingOfferToList(bannerOffer);
            }else if (BannerOfferType.LM.getType().equalsIgnoreCase(bannerOffer.getOfferType())){
                if (!offers.isEmpty()) {
                    for(EligibleCategories eligibleCategories : offers){
                        for(PrepaidOfferRow offer : eligibleCategories.getEligibleOffersList()){
                            if(offer.getOfferId().toString().equals(bannerOffer.getOfferId())){
                                addingOfferToList(bannerOffer);
                            }
                        }
                    }
                }
            }
        }

        setupViewPager();
    }

    private void buildHardcodedBannersList(){
        for(BannerOffer bannerOffer : configuredOffersList){
            addingOfferToList(bannerOffer);
        }
    }

    private void addingOfferToList(BannerOffer bannerOffer){
        Log.d(TAG, "addingOfferToList id - " + bannerOffer.getOfferId());
        finalOffersList.add(bannerOffer);
    }

    protected void getEligibleOffers4EBU(String crmRole, String msisdn) {
        OffersService offersService = new OffersService(getContext());

        offersService.getEligibleOffers4EBU(crmRole, msisdn,null)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersPostSuccess> eligibleOffersPostSuccessResponse) {
                        super.onNext(eligibleOffersPostSuccessResponse);
                        preparePostOffersList(eligibleOffersPostSuccessResponse.getTransactionSuccess());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        preparePostOffersList(null);
                    }
                });
    }
}
