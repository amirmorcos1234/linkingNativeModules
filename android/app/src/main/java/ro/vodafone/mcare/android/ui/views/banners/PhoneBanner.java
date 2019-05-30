package ro.vodafone.mcare.android.ui.views.banners;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.adapters.ImageSlideAdapter;
import ro.vodafone.mcare.android.ui.views.CirclePageIndicator;
import ro.vodafone.mcare.android.ui.views.PageIndicator;

/**
 * Created by Victor Radulescu on 3/31/2017.
 */

public class PhoneBanner extends LinearLayout {


    private static final long ANIM_VIEWPAGER_DELAY = 2000;
    private Context mContext;
    private ViewPager mViewPager;
    private ViewPager fakeViewPager;
    private PageIndicator mFakeIndicator;
    private PageIndicator goodIndicator;
    private List<String> uriList;

    private Runnable animateViewPager;
    private Handler carouselEventHandler;
    boolean stopSliding = false;

    String title;

    public PhoneBanner(Context context) {
        super(context);
        this.mContext = context;

        init(null);

    }

    public PhoneBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    public PhoneBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (isInEditMode()) {
            init(attrs);
        }
    }

    private void init(AttributeSet attrs) {

        System.out.println("init banner");
        inflate(mContext, R.layout.phone_offer_banner, this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        fakeViewPager = (ViewPager) findViewById(R.id.view_pager_fake);
        mFakeIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        goodIndicator = (CirclePageIndicator) findViewById(R.id.indicatorGood);

    }

    public PhoneBanner setUriList(List<String> uriList) {
        this.uriList = uriList;
        //todo show any image if list is empty ?
        return this;
    }

    public PhoneBanner setTitle(String title) {
        this.title = title;
        return this;
    }


    public PhoneBanner buildBanner() {
        Log.d("Array size: ", "" + uriList.size());
        this.createSliderImage();
        return this;
    }

    private void createSliderImage() {
        ImageSlideAdapter imagePagerAdapter = new ImageSlideAdapter(getContext(), title != null ? title : "",
                uriList != null ? uriList : new ArrayList<String>());

        fakeViewPager.setAdapter(new ImageSlideAdapter(getContext(), title != null ? title : "",
                uriList != null ? uriList : new ArrayList<String>()));
        mFakeIndicator.setViewPager(fakeViewPager);

        mViewPager.setAdapter(imagePagerAdapter.setInfiniteLoop(true));

        goodIndicator.setViewPager(mViewPager);

        mViewPager.getParent().requestDisallowInterceptTouchEvent(false);
        mViewPager.setCurrentItem(100 / 2 - 100 / 2 % uriList.size());
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int value = ((position) % uriList.size());
                mFakeIndicator.setCurrentItem(value);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        goodIndicator.setOnPageChangeListener(listener);

        this.setupPageIndicator(uriList.size());
    }

    public void carouselRunnable(final int size) {
        carouselEventHandler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    carouselEventHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    private void setupPageIndicator(int size){
        try {
            if (size < 2) {
                ((CirclePageIndicator) mFakeIndicator).setVisibility(GONE);
                mViewPager.getParent().requestDisallowInterceptTouchEvent(false);
                mViewPager.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        return true;
                    }
                });

            } else {
                this.carouselRunnable(uriList.size());
                carouselEventHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
