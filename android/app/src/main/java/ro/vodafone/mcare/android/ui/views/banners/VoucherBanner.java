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
import ro.vodafone.mcare.android.client.adapters.VoucherBannerSliderAdapter;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.ui.views.CirclePageIndicator;
import ro.vodafone.mcare.android.ui.views.PageIndicator;

/**
 * Created by Serban Radulescu on 9/6/2017.
 */

public class VoucherBanner extends LinearLayout {

    private static final String TAG = "VoucherBanner";

    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private static final String DEFAULT_PROMOTION_ID = "default_banner";
    private Context mContext;

    private ViewPager mViewPager;
    private PageIndicator mIndicator;

    private List<Promotion> loyaltyVoucherBannerList;
    private List<Promotion> finalLoyaltyVoucherBannerList;


    private Runnable animateViewPager;
    private Handler handler;
    private boolean stopSliding = false;

    private boolean redirectToNewVoucherList = false;
    private boolean redirectToReservedVoucher = false;


    public VoucherBanner(Context context) {
        super(context);
        this.mContext = context;
        init(null);

    }

    public VoucherBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    public VoucherBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VoucherBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    private void init(AttributeSet attrs) {

        System.out.println("init banner");
        inflate(mContext, R.layout.voucher_banner, this);


        loyaltyVoucherBannerList = new ArrayList<>();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        this.setVisibility(GONE);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attributeSet) {
    }

    public VoucherBanner setRedirectToNewVoucherListing(boolean redirectToNewVoucherListFlag){
        redirectToNewVoucherList = redirectToNewVoucherListFlag;
        return this;
    }

    public VoucherBanner setRedirectToOwnVoucherListing(boolean redirectToOwnVoucherListingFlag){
        redirectToReservedVoucher = redirectToOwnVoucherListingFlag;
        return this;
    }

    public VoucherBanner setConfiguredOffersList(List<Promotion> offersList) {
        this.loyaltyVoucherBannerList = offersList;
        setFinalVoucherBannerList();
        return this;
    }

    public VoucherBanner buildBanner() {
        setupViewPager();
        return this;
    }

    private void setupViewPager(){
        Log.d(TAG, "setupViewPager");

        if(!finalLoyaltyVoucherBannerList.isEmpty()){
            Log.d(TAG, "not empty list");

            //sort List
            sortBannerList();

            this.setVisibility(VISIBLE);

            mViewPager.setAdapter(new VoucherBannerSliderAdapter(getContext(), finalLoyaltyVoucherBannerList,
                                                                redirectToNewVoucherList,
                                                                redirectToReservedVoucher));
            mViewPager.getParent().requestDisallowInterceptTouchEvent(false);

            setupPageIndicator(finalLoyaltyVoucherBannerList.size());

            runnable(finalLoyaltyVoucherBannerList.size());
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
            Collections.sort(finalLoyaltyVoucherBannerList,new Comparator<Promotion>() {
                @Override
                public int compare(Promotion s1, Promotion s2) {
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
                    try {
                        if (mViewPager.getCurrentItem() == size - 1) {
                            mViewPager.setCurrentItem(0);
                        } else {
                            mViewPager.setCurrentItem(
                                    mViewPager.getCurrentItem() + 1, true);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    public boolean isStopSliding() {
        return stopSliding;
    }

    public void setStopSliding(boolean stopSliding) {
        this.stopSliding = stopSliding;
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

    private void setFinalVoucherBannerList(){
        finalLoyaltyVoucherBannerList = new ArrayList<>();

        Promotion defaultVoucherBanner = new Promotion();
        defaultVoucherBanner.setPromotionId(DEFAULT_PROMOTION_ID);
        defaultVoucherBanner.setPriority(0L);
        finalLoyaltyVoucherBannerList.add(defaultVoucherBanner);

        String maxMarketBanners = AppConfiguration.getMarketMaxNumberBanners();
        int parsedMaxMarketBanners = -1;
        try {
            parsedMaxMarketBanners = Integer.parseInt(maxMarketBanners);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(parsedMaxMarketBanners < 0 || parsedMaxMarketBanners > 5){
            parsedMaxMarketBanners = 3;
        }

        if(loyaltyVoucherBannerList != null){
            for (int i = 0 ; i < loyaltyVoucherBannerList.size() ; i++){
                if(finalLoyaltyVoucherBannerList.size() >= parsedMaxMarketBanners){
                    break;
                }
                addingVoucherToList(loyaltyVoucherBannerList.get(i));
            }
        }

    }

    private void addingVoucherToList(Promotion voucherItem){
        Log.d(TAG, "addingVoucherToList id - " + voucherItem.getPromotionId());
        finalLoyaltyVoucherBannerList.add(voucherItem);
    }

}
