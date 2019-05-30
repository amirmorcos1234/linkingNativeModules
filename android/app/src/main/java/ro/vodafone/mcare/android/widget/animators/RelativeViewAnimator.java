package ro.vodafone.mcare.android.widget.animators;

import android.view.View;
import android.view.animation.Interpolator;

import com.github.florent37.viewanimator.AnimationBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 2/27/2017.
 */

public class RelativeViewAnimator {

    protected ArrayList<AnimationInfo> animationInfos;

    private boolean animationStarted;

    public RelativeViewAnimator() {
        this.animationInfos = new ArrayList<>();
    }

    final long GROUP_ANIMATION_DELAY = 1000;

    AnimationListener animatorListener;


    public void animateGroup(){
        if(animationStarted){
            return;
        }
        if(animatorListener!=null){
            animatorListener.onAnimationStart();
        }

        animationStarted = true;
        for (int i = 0; i <animationInfos.size() ; i++) {
            final AnimationInfo animation=animationInfos.get(i);
            if(animation.getAnimator()==null){
                break;
            }

//            if (animation.getAnimator().getView() == null)
//                continue;
            animation.getAnimator().getView().setAlpha(animation.getInitialAlfa());

            final int finalI = i;
            Observable.timer(animation.getStartDelay()+GROUP_ANIMATION_DELAY, TimeUnit.MILLISECONDS) .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    animation.getAnimator().start();
                    animation.getAnimator().onStop(new com.github.florent37.viewanimator.AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            if( finalI ==animationInfos.size()-1){
                                animationStarted = false;
                            }
                            if(animatorListener!=null){
                                animatorListener.onAnimationEnd(animation);
                            }
                        }
                    });
                }
            });
        }
    }


    public class AnimationInfo{


        public AnimationInfo(View view) {
            this.view = view;
        }

        protected View view;

        protected Interpolator interpolator = Interpolators.OUT_EXPO;

        protected long duration;

        protected long startDelay = 0;

        protected float initialAlfa = 255;

        protected AnimationBuilder  animator ;

        public Interpolator getInterpolator() {
            return interpolator;
        }

        public void setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getStartDelay() {
            return startDelay;
        }

        public void setStartDelay(long startDelay) {
            this.startDelay = startDelay;
        }

        public float getInitialAlfa() {
            return initialAlfa;
        }

        public void setInitialAlfa(float initialAlfa) {
            this.initialAlfa = initialAlfa;
        }

        public AnimationBuilder getAnimator() {
            return animator;
        }

        public void setAnimator(AnimationBuilder animator) {
            this.animator = animator;
        }

    }

    public void setAnimatorListener(DashboardAnimator.AnimationListener animatorListener) {
        this.animatorListener = animatorListener;
    }

    public interface AnimationListener{
        void onAnimationEnd(AnimationInfo animationInfo);
        void onAnimationStart();
    }
}
