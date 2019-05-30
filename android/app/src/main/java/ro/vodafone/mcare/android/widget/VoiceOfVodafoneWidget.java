package ro.vodafone.mcare.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easyandroidanimations.library.FadeOutAnimation;
import com.easyandroidanimations.library.ParallelAnimator;
import com.easyandroidanimations.library.ParallelAnimatorListener;
import com.easyandroidanimations.library.ScaleOutAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.VoiceOfVodafonePageAdapter;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneType;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.ResizeViewPager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;
import ro.vodafone.mcare.android.widget.voiceofvodafone.VoiceOfVodafoneBasic;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Victor Radulescu on 1/26/2017.
 */

public class VoiceOfVodafoneWidget extends RelativeLayout{

    @BindView(R.id.voiceofvodafone_viewpager)
    ResizeViewPager vovPager;

    private VoiceOfVodafonePageAdapter voiceOfVodafonePageAdapter;

    @BindView(R.id.viewPagerCountDots)
    LinearLayout dotsLayout;

    private int dotsCount;
    private List<ImageView> dots;

    private int switchVovsTimeInSeconds = 5;

    private boolean allowAutoSwitch = true;

    private boolean removeCurrentWhenComplete = false;

    private int positionToRemove;

    private long lastTimeWhenUserTouchedView;

    private final static int DOT_DRAWALE_ID = R.drawable.dot_white;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    public VoiceOfVodafoneWidget(Context context) {
        super(context);
        init();
    }

    public VoiceOfVodafoneWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceOfVodafoneWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VoiceOfVodafoneWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        inflate(getContext(),R.layout.voice_of_vodafone_widget,this);
        ButterKnife.bind(this);

    }
    public void initialize(){

        VoiceOfVodafoneController.getInstance().setVoiceOfVodafoneWidget(this);
        setViewPagerItemsWithAdapter();
        setUiPageViewController();
        initDots();
        switchVOVs();
        registerExpireTime();

    }
    public void refreshIfNeeded(){

        if(getContext()==null){
            return;
        }
        setLastTimeWhenUserTouchedView();

        setViewPagerItemsWithAdapter();
        setUiPageViewController();
        initDots();
        registerExpireTime();
        VoiceOfVodafoneController.getInstance().setShouldRefresh(false);
        vovPager.setAutoResizeAfterFirstOne(true);
        //take a while until auto moving
        setLastTimeWhenUserTouchedView();
    }

    private void setUiPageViewController() {

        dotsCount = voiceOfVodafonePageAdapter.getCount();
        dotsLayout.removeAllViews();
        dots = new ArrayList<>();

        for (int i = 0; i < dotsCount; i++) {
            dots.add(i, drawDot(0.4f));
            dotsLayout.addView(dots.get(i));
        }
        dots.get(0).setAlpha(1f);
    }

    private void setViewPagerItemsWithAdapter() {
        voiceOfVodafonePageAdapter = new VoiceOfVodafonePageAdapter(getContext(),VoiceOfVodafoneController.getInstance().getVoiceOfVodafones());
        vovPager.setAdapter(voiceOfVodafonePageAdapter);
        vovPager.setCurrentItem(0);
        vovPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void setDots(int position){

        for (int i = 0; i < dotsCount; i++) {
            dots.get(i).setAlpha(0.4f);
        }
        if(position<dotsCount){
            dots.get(position).setAlpha(1f);
        }
        if (dotsCount == 1) {
            dotsLayout.setVisibility(GONE);
        }else{
            dotsLayout.setVisibility(VISIBLE);
        }
    }
    private void initDots(){
        setDots(0);
    }

    public void switchVOVs(){
       Subscription subscription = Observable.interval(switchVovsTimeInSeconds, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Long aLong) {
                if(allowAutoSwitch && hasTimePassedFromLastUserTouch()){
                    int nextItem = (vovPager.getCurrentItem()+1) % voiceOfVodafonePageAdapter.getItems().size();
                    vovPager.setCurrentItem(nextItem,true);
                }
            }
        });
        compositeSubscription.add(subscription);
    }

    public synchronized void dismissCurent(){
        if(!removeCurrentWhenComplete){
            final int position = vovPager.getCurrentItem();
            Log.d("VOV","position to dismiss: "+position);
            dismiss(position);
        }
    }
    public synchronized void dismiss(int positionToDismiss) {
        try {
            stopSwitching();

            final int position = positionToDismiss;
            final int lenght = voiceOfVodafonePageAdapter.getItems().size();
            //final View view = findViewWithTag("vov" + position);
            final View view = voiceOfVodafonePageAdapter.getItems().get(position);

            //swichToNextOrPrevious(lenght - 1, position);
            new ParallelAnimator().add(new ScaleOutAnimation(view))
                    .add(new FadeOutAnimation(view))
                    .setDuration(330)
                    .setInterpolator(Interpolators.OUT_SINE)
                    .setListener(new ParallelAnimatorListener() {
                        @Override
                        public void onAnimationEnd(ParallelAnimator parallelAnimator) {
                            Log.d("vov", "Current pos" + position);
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setVisibility(GONE);
                                    positionToRemove = position;
                                    removeCurrentWhenComplete = true;
                                    swichToNextOrPrevious(lenght, position,true);
                                }
                            });
                        }
                    }).animate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int findNextPosition(int nextPosition,int length) {
        if (length != 1) {
            int nextModulo = nextPosition + 1 % voiceOfVodafonePageAdapter.getItems().size();
            if (nextModulo > nextPosition && nextModulo!=voiceOfVodafonePageAdapter.getItems().size()) {
                return nextModulo;
            } else {
                return nextPosition - 1;
            }
        }
        return 0;
    }

    private int swichToNextOrPrevious(int lenght,int position,boolean shouldRemoveCurrent){

        int nextItem = findNextPosition(shouldRemoveCurrent?positionToRemove:position,lenght);
        vovPager.setCurrentItem(nextItem, true);

        if(shouldRemoveCurrent){
            lenght--;
        }
        Log.d("vov", "NextItem" + nextItem);
        Log.d("VOV lenght", String.valueOf(lenght));
        Log.d("VOV pos", String.valueOf(position));

        int nextPosition = position;
        if(!shouldRemoveCurrent){
            nextPosition= nextItem;
        }else if(position == lenght){
            nextPosition = lenght >= 2 ? position - 1 : 1;
        }
        if(lenght ==1 ){
            nextPosition=0;
        }
        refreshDots(lenght,nextPosition);
        return nextItem;
    }
    private void refreshDots(int lenght,int position){
        dotsLayout.removeAllViews();
        dotsCount = lenght;
        if (dotsCount>1) {
            dots = new ArrayList<>();

            for (int i = 0; i < dotsCount; i++) {
                dots.add(i, drawDot(1f));
                dotsLayout.addView(dots.get(i));
            }
            dots.get(0).setAlpha(0.4f);

            setDots(position);
        }
    }
    public void stopSwitching(){
        allowAutoSwitch = false;
    }
    public void startSwitching(){
        allowAutoSwitch = true;
    }


    private void registerExpireTime(){
        for(int i=0;i<voiceOfVodafonePageAdapter.getItems().size();i++){
            if(voiceOfVodafonePageAdapter.getItems().get(i).getVoiceOfVodafone().getTimeToLive()>0){
                registerVOV(voiceOfVodafonePageAdapter.getItems().get(i));
            }
        }
    }
    private void registerVOV(final VoiceOfVodafoneBasic voiceOfVodafoneView){
        Subscription subscription = Observable.timer(voiceOfVodafoneView.getVoiceOfVodafone().getTimeToLive(),TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        int positionToDismiss = voiceOfVodafonePageAdapter.getItemPosition(voiceOfVodafoneView);
                        if(positionToDismiss==vovPager.getCurrentItem()){
                            dismissCurent();
                        }else{
                            voiceOfVodafonePageAdapter.removeView(vovPager,positionToDismiss,vovPager.getCurrentItem());
                            refreshDots(voiceOfVodafonePageAdapter.getCount(),vovPager.getCurrentItem());
                        }
                    }
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
        compositeSubscription.add(subscription);
    }

    private int getPositionAfterDismissAnimation(int positionToRemove,int nextPosition){
        if(positionToRemove==0){
            if(nextPosition==1){
                return 0;
            }else if(nextPosition==2){
                return 1;
            }
            return nextPosition;
        }else
        if(positionToRemove==1){
              if(nextPosition==0){
                  return 0;
              }else if(nextPosition==2){
                  return 1;
              }
        }else
        if(positionToRemove==2){
            return nextPosition;
        }
        return 0;
    }
    public int getSize(){
        return voiceOfVodafonePageAdapter.getCount();
    }
    public void dimissWithoutTransition(VoiceOfVodafone voiceOfVodafone){
        voiceOfVodafonePageAdapter.removeViewNoTransation(vovPager,voiceOfVodafone);
    }

    public LinearLayout getDotsLayout() {
        return dotsLayout;
    }

    private ImageView drawDot(float alfa){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),DOT_DRAWALE_ID));

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(alfa!=1f){
            GradientDrawable drawable = (GradientDrawable)imageView.getDrawable();
            drawable.setStroke(ScreenMeasure.dpToPx(1), Color.WHITE);
            imageView.setImageDrawable(drawable);
        }

        imageView.setPadding(ScreenMeasure.dpToPx(2),0,ScreenMeasure.dpToPx(2),0);
        imageView.setLayoutParams(layoutParams);
        imageView.setAlpha(alfa);
        return imageView;
    }
    private boolean hasTimePassedFromLastUserTouch(){
        return System.currentTimeMillis()-lastTimeWhenUserTouchedView > switchVovsTimeInSeconds*1000;
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            setDots(position);
        }

        @Override
        public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
            // Log.d("VOV"+            vovPager.getCurrentItem(), String.valueOf(position) +"position offset "+ positionOffset + " offset px"+ positionOffsetPixels);
            if(removeCurrentWhenComplete){
                if(positionOffset==0){
                    removeCurrentWhenComplete = false;
                    Subscription subscription = Observable.timer(100,TimeUnit.MILLISECONDS)
                            .onBackpressureDrop()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {
                                    int positionNext = getPositionAfterDismissAnimation(positionToRemove,position);

                                    Log.d("vov","Position Next after animator for dimiss "+positionNext);
                                    Log.d("vov","Position to remove after animator for dimiss "+positionToRemove);
                                    voiceOfVodafonePageAdapter.removeView(vovPager,positionToRemove,positionNext);
                                    startSwitching();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Long aLong) {

                                }
                            });
                    compositeSubscription.add(subscription);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                allowAutoSwitch = false;
                setLastTimeWhenUserTouchedView();
                // User has dragged
            }else if(state == ViewPager.SCROLL_STATE_IDLE){
                allowAutoSwitch = true;
            }
        }
    };

    public void setLastTimeWhenUserTouchedView() {
        lastTimeWhenUserTouchedView = System.currentTimeMillis();

    }

    public void destroy(){
        clearSubcriptions();
    }
    public void clearSubcriptions(){
        compositeSubscription.clear();

    }
}
