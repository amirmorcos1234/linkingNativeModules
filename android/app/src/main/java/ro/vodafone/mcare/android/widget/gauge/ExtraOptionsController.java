package ro.vodafone.mcare.android.widget.gauge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 2/3/2017.
 */

public class ExtraOptionsController {

    private View viewToDrawArcFrom;

    private int startingAngle = 300;

    private final int STARTING_SWEEP_ANGLE = 40;

    private int spacedAngleBetweenElements = 40;

    private Context context;

    public  static int EXTRA_RADIOS_IN_PX= ScreenMeasure.dpToPx(28);

    private final long ANIMATION_ADD_DELAY = 100;

    private final long ANIMATION_REMOVE_DELAY = 50;

    private final long ANIMATION_ADD_DURATION = 300;

    private final long ANIMATION_REMOVE_DURATION = 150;

    private List<View> viewListToAnimated;

    private View rotateView;

    private boolean isActivated = false;

    boolean canAnimate = true;

    public ExtraOptionsController(Context context, View viewToDrawArcFrom, List<View> viewListToAnimated) {
        this.context = context;
        this.viewToDrawArcFrom = viewToDrawArcFrom;
        this.viewListToAnimated = viewListToAnimated;
    }

    /**
     * Toggle after a delay
     * @param delay
     */
    public void toggle(long delay){

        Observable.timer(delay, TimeUnit.MILLISECONDS) .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {
                    toggle();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void toggle(){
        if(!canAnimate){
            return;
        }
        canAnimate = false;

        if(isActivated){
            animateBack();
        }else{
            drawAndAnimate();
        }
        isActivated = !isActivated;

    }
    private void animateBack(){
        long totalDuration = ANIMATION_REMOVE_DELAY + viewListToAnimated.size()/2* ANIMATION_REMOVE_DELAY + ANIMATION_REMOVE_DURATION;
        rotateView(rotateView,45,0,totalDuration);
        for (int i = 0; i < viewListToAnimated.size(); i++) {
            long delay = ANIMATION_REMOVE_DELAY + ANIMATION_REMOVE_DELAY /2*i;
            removeViewInArc(i,delay);
        }
    }

    private void drawAndAnimate(){
        //clearListViews();
        try {
            long totalDuration = ANIMATION_ADD_DELAY + viewListToAnimated.size() / 2 * ANIMATION_ADD_DELAY + ANIMATION_ADD_DURATION;
            rotateView(rotateView, 0, 45, totalDuration);
            for (int i = 0; i < viewListToAnimated.size(); i++) {
                long delay = ANIMATION_ADD_DELAY + ANIMATION_ADD_DELAY / 2 * i;
                animateViewInArc(i, delay);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void animateViewInArc(final int position, long delay){
        Observable.timer(delay+position*delay, TimeUnit.MILLISECONDS) .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                registerAddAnimation(position);
            }
        });
    }
    private void removeViewInArc(final int position, long delay){
        Observable.timer(delay+position*delay, TimeUnit.MILLISECONDS) .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                registerRemoveAnimation(position);
            }
        });
    }

    public void rotateView(View view, float startPosition, float degree,long duration){
        if(view==null){
            return;
        }
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(view ,
                "rotation", startPosition, degree);
        imageViewObjectAnimator.setDuration(duration); // miliseconds
        imageViewObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                canAnimate =true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        try {
            imageViewObjectAnimator.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void drawInArc(View view,int dpWidth,int dpHeigth){
        //Path path = getDefaultPath(ScreenMeasure.dpToPx(64),ScreenMeasure.dpToPx(64),ScreenMeasure.dpToPx(150),sweepAngle);
        dpWidth = ScreenMeasure.dpToPx(dpWidth);
        dpHeigth = ScreenMeasure.dpToPx(dpHeigth);
        Path path = getArcPathWithDimens(STARTING_SWEEP_ANGLE,dpWidth,dpHeigth);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float[] point = new float[2];
        pathMeasure.getPosTan(0, point, null);
        Log.d("Point draw","x "+point[0] +" and y"+ point[1]);
        point[0]=point[0];
        point[1]=point[1];
        view.setX(point[0]);
        view.setY(point[1]);
        Log.d("View draw","x "+ view.getX() +" and y"+ view.getY());
    }

    private void registerAddAnimation(int position){
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        pathAnimator.setDuration(ANIMATION_ADD_DURATION);
        final View view = viewListToAnimated.get(position);
        view.setVisibility(View.VISIBLE);
        int sweepAngle = STARTING_SWEEP_ANGLE + spacedAngleBetweenElements*(viewListToAnimated.size()-position-1);
        final Path path = getArcPath(view,startingAngle,sweepAngle);

        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float[] point = new float[2];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = animation.getAnimatedFraction();
                PathMeasure pathMeasure = new PathMeasure(path, false);
                pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
                view.setX(point[0]);
                view.setY(point[1]);
            }
        });
        pathAnimator.start();
    }
    private void registerRemoveAnimation(int position){
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0f, 1f);
        pathAnimator.setDuration(ANIMATION_REMOVE_DURATION);
        final View view = viewListToAnimated.get(position);
       // view.setVisibility(View.VISIBLE);
        int startAngle = this.startingAngle  + spacedAngleBetweenElements + (viewListToAnimated.size()-position-1)*spacedAngleBetweenElements;

        int sweepAngle =  -(STARTING_SWEEP_ANGLE + spacedAngleBetweenElements * (viewListToAnimated.size()-position-1));
        
        final Path path = getArcPath(view,startAngle,sweepAngle);

        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float[] point = new float[2];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = animation.getAnimatedFraction();
                PathMeasure pathMeasure = new PathMeasure(path, false);
                pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
                view.setX(point[0]);
                view.setY(point[1]);
            }
        });
        pathAnimator.start();
    }

    private Path getArcPath(View viewToAnimate,int startingAngle,int sweepAngle){

        int xCenter =  (int)viewToDrawArcFrom.getX()+ viewToDrawArcFrom.getWidth()/2 - viewToAnimate.getWidth()/2;
        int yCenter =  (int)viewToDrawArcFrom.getY()+ viewToDrawArcFrom.getHeight()/2 - viewToAnimate.getHeight()/2;

        //Log.d("xCenter", String.valueOf(xCenter));
        //Log.d("yCenter", String.valueOf(yCenter));
        int radius = viewToDrawArcFrom.getHeight()/2 + EXTRA_RADIOS_IN_PX;

        Point point1 = new Point(xCenter-radius,yCenter-radius);
        Point point2 = new Point(xCenter+radius,yCenter+radius);

        final RectF oval = new RectF();
        oval.set(point1.x,point1.y,point2.x,point2.y);
        Path arcPath = new Path();

        arcPath.arcTo(oval, startingAngle, sweepAngle, true);
        return arcPath;
    }
    private Path getArcPathWithDimens(int sweepAngle,int viewWidth, int viewHeigth){

        int xCenter =  (int)viewToDrawArcFrom.getX()+ viewToDrawArcFrom.getWidth()/2 - viewWidth/2;
        int yCenter =  (int)viewToDrawArcFrom.getY()+ viewToDrawArcFrom.getHeight()/2 - viewHeigth/2;

        //Log.d("xCenter", String.valueOf(xCenter));
        Log.d("yCenter", String.valueOf(yCenter));
        int radius = viewToDrawArcFrom.getHeight()/2 + EXTRA_RADIOS_IN_PX;

        Point point1 = new Point(xCenter-radius,yCenter-radius);
        Point point2 = new Point(xCenter+radius,yCenter+radius);

        final RectF oval = new RectF();
        oval.set(point1.x,point1.y,point2.x,point2.y);
        Path arcPath = new Path();

        arcPath.arcTo(oval, startingAngle, sweepAngle, true);
        return arcPath;
    }
    public void clearListViews(){
        if(viewListToAnimated==null || viewListToAnimated.isEmpty() || !(viewListToAnimated.get(0).getParent() instanceof ViewGroup) ){
            return;
        }
        ViewGroup viewParent = (ViewGroup) viewListToAnimated.get(0).getParent();

        for (int i = 0; i <viewListToAnimated.size() ; i++) {
           if(viewParent!=null){
               viewParent.removeView(viewListToAnimated.get(i));
           }
        }
        viewListToAnimated.clear();
    }

    public void setRotateView(ImageView rotateView) {
        this.rotateView = rotateView;
    }


    public void setViewListToAnimated(List<View> viewListToAnimated) {
        this.viewListToAnimated = viewListToAnimated;
    }
}
