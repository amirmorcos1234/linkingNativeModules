package ro.vodafone.mcare.android.widget.gauge;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.CompatView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.widget.CostControlWidget;
import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;

/**
 * Created by Victor Radulescu on 2/8/2017.
 */

public class SeriesController {

    private static final float DIFFERENCE_BETWEEN_CURRENT_AND_FINAL_VALUE_ANIMATION_ERROR = 0.01f;
    private final float POINT_STARTING_DEGREE =270;
    private final float CIRCLE_SWEEP_DEGREE = 359.9f;

    private Context context;

    private CostControlWidget costControlWidget;

    private DecoView arcView;

    private Path circlePath;

    private ImageView pointView;
    private ImageView arrowView;

    private final int pointWidth =5;

    private final int pointHeight =5;

    public static final int CIRCLE_LINE_WIDTH_IN_DP = 10;

    private final int ARROW_SIZE_IN_DP = 7;

    private final int drawableDotRes = R.drawable.black_dot;
    private final int drawableTriangleRes = R.drawable.black_triangle;

    private final long ANIMATION_DURATION_IN_MS = 1000;

    private final Interpolator animationInterpolator= Interpolators.OUT_EXPO;

    private final float FINAL_MAX_ARROW_ANGLE = 359.9f;
    private final float FINAL_MIN_ARROW_ANGLE = 90;

    SeriesUpdate seriesUpdate;


    public SeriesController(Context context, @NonNull CostControlWidget costControlWidget,@NonNull SeriesUpdate seriesUpdate) {
        this.context = context;
        this.costControlWidget = costControlWidget;
        this.arcView = costControlWidget.getArcView();
        this.seriesUpdate = seriesUpdate;
    }

    /**
     * Initilize starting point on arc
     */
    public void setup(){
        /*if(arrowView==null){
            drawArrow();
        }*/
        if(pointView==null){
            drawStartingPoint();
        }
    }

    public void animateSeries(float percentfinalPostion, float maxValue){

        drawAndAnimateTriangle(1-percentfinalPostion);
        addSeries(percentfinalPostion,maxValue);
        if(percentfinalPostion==maxValue){
            hideArrow();
        }else{
            showArrow();
        }

    }

    private void drawStartingPoint(){
        pointView = new ImageView(context);
        pointView.setLayoutParams(new RelativeLayout.LayoutParams(ScreenMeasure.dpToPx(pointWidth), ScreenMeasure.dpToPx(pointHeight)));
        pointView.setImageDrawable(ContextCompat.getDrawable(context,drawableDotRes));
        costControlWidget.addView(pointView);

        CompatView.bringToFront(pointView);
        //if(circlePath==null){
        generateCirclePath(CIRCLE_SWEEP_DEGREE,pointHeight);
       // }
        moveView(pointView);
    }

    private void drawArrow(){
        arrowView = new ImageView(context);
        arrowView.setLayoutParams(new RelativeLayout.LayoutParams(ScreenMeasure.dpToPx(ARROW_SIZE_IN_DP), ScreenMeasure.dpToPx(ARROW_SIZE_IN_DP)));
        arrowView.setImageDrawable(ContextCompat.getDrawable(context,drawableTriangleRes));
        costControlWidget.addView(arrowView);
        //CompatView.bringToFront(arrowView);

    }

    private void drawAndAnimateTriangle(float percentValue){
        if(arrowView==null && percentValue!=0){
            drawArrow();
        }
        float sweepAngle = percentValue * CIRCLE_SWEEP_DEGREE;
        Log.d("Series","sweepAngle "+ -sweepAngle);
        generateCirclePath(-sweepAngle,ARROW_SIZE_IN_DP);
        animateArrow(/*percentValue>0.5? percentValue+180:*/percentValue);
    }

    private void animateArrow(final float percent){
        if(percent==0){
            hideArrow();
            return;
        }

        final float finalAngle =  getAngleForArrowWithProcentValue(percent);
        Log.d("Series","final angle to rotate"+finalAngle+" procent "+percent);

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(arrowView,
                "rotation",FINAL_MIN_ARROW_ANGLE + FINAL_MAX_ARROW_ANGLE,finalAngle);
        rotationAnimator.setDuration(ANIMATION_DURATION_IN_MS);
        rotationAnimator.setInterpolator(animationInterpolator);

        arrowView.setVisibility(View.VISIBLE);
        rotationAnimator.start();

    }
    private float getAngleForArrowWithProcentValue(float percent){
        return FINAL_MAX_ARROW_ANGLE * (1-percent) + FINAL_MIN_ARROW_ANGLE;
    }

    public void drawWhiteSeriesNoAnimation(){
            //clear elements from the DecoView element
        arcView.executeReset();
        hideArrow();

        final SeriesItem seriesItem= new SeriesItem.Builder(Color.argb(255,255,255,255))
                .setRange(0, 1f, 0.9999f)
                .setInitialVisibility(true)
                .setLineWidth(ScreenMeasure.dpToPx(CIRCLE_LINE_WIDTH_IN_DP))
                .build();
        int whiteIndex = arcView.addSeries(seriesItem);
        DecoEvent.Builder builder = new DecoEvent.Builder(0.99f).setIndex(whiteIndex);
        arcView.addEvent(builder.build());


    }

    private void addSeries(final float finalPostion, float maxValue){

        //Create data series track
        final SeriesItem seriesWhite = new SeriesItem.Builder(Color.argb(255, 255, 255, 255))
                .setRange(0, maxValue, maxValue)
                .setLineWidth(ScreenMeasure.dpToPx(CIRCLE_LINE_WIDTH_IN_DP))
                .setInitialVisibility(false)
                .build();

        seriesWhite.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            float[] point = new float[2];

            @Override
            public void onSeriesItemAnimationProgress(float v, float currentPosition) {

                PathMeasure pathMeasure = new PathMeasure(circlePath, false);
                pathMeasure.getPosTan(pathMeasure.getLength() * v, point, null);

                if(arrowView!=null){
                    ///point[1]=point[1] + ScreenMeasure.dpToPx(ARROW_SIZE_IN_DP)/2;
                   // point[0]=point[0] + ScreenMeasure.dpToPx(ARROW_SIZE_IN_DP)/2;

                    arrowView.setX(point[0] /*+ ScreenMeasure.dpToPx(ARROW_SIZE_IN_DP)/2*/);
                    arrowView.setY(point[1] );

                    if(currentPosition<0.01){
                        arrowView.setVisibility(View.INVISIBLE);
                    }

                }
                if(differenceBetweenFinalValueIsBelowThreshold(currentPosition, finalPostion)){
                    currentPosition = finalPostion;
                }
                seriesUpdate.onSeriesUpdate(currentPosition);

            }
            @Override
            public void onSeriesItemDisplayProgress(float v) {
            }
        });

        int seriesRotate = arcView.addSeries(seriesWhite);
        arcView.addEvent(new DecoEvent.Builder(finalPostion).setIndex(seriesRotate).setInterpolator(animationInterpolator).setDuration(ANIMATION_DURATION_IN_MS).build());
        //arcView.addEvent(new DecoEvent.Builder(finalPostion).setIndex(point).setDelay(3000).build());
    }

    private boolean differenceBetweenFinalValueIsBelowThreshold(float currentPosition, float finalPostion) {
        return Math.abs(currentPosition-finalPostion) < DIFFERENCE_BETWEEN_CURRENT_AND_FINAL_VALUE_ANIMATION_ERROR;
    }



    public void moveView(View view){

        PathMeasure pathMeasure = new PathMeasure(circlePath, false);
        float[] point = new float[2];
        pathMeasure.getPosTan(0, point, null);
        Log.d("Point draw","path length "+pathMeasure.getLength()+" x "+point[0] +" and y"+ point[1]);
        point[0]=point[0]/* + ScreenMeasure.dpToPx(pointWidth)/2*/;
        point[1]=point[1] ;
        view.setX(point[0]);
        view.setY(point[1]);
        Log.d("View draw","x "+ view.getX() +" and y"+ view.getY());

    }

    public void generateCirclePath(float sweepAngle,int sizeInDp){
        circlePath = getArcPath(POINT_STARTING_DEGREE,sweepAngle,sizeInDp);

    }
    private Path getArcPath(float startingAngle,float sweepAngle,int sizeInDp){

        int xCenter =  (int)arcView.getX()+ arcView.getWidth()/2 - ScreenMeasure.dpToPx(sizeInDp)/2;
        int yCenter =  (int)arcView.getY()+ arcView.getHeight()/2 - ScreenMeasure.dpToPx(sizeInDp)/2;

        Log.d("xCenter", String.valueOf(xCenter));
        Log.d("yCenter", String.valueOf(yCenter));
        int radius = arcView.getHeight()/2  - ScreenMeasure.dpToPx(CIRCLE_LINE_WIDTH_IN_DP/2);

        Point point1 = new Point(xCenter-radius,yCenter-radius);
        Point point2 = new Point(xCenter+radius,yCenter+radius);

        final RectF oval = new RectF();
        oval.set(point1.x,point1.y,point2.x,point2.y);
        Path arcPath = new Path();
        Log.d("SeriesController","sweep angle"+sweepAngle+" starting angle"+startingAngle);
        arcPath.arcTo(oval, startingAngle, sweepAngle, true);
        return arcPath;
    }
    public void clearAnimations(){
        if(arcView!=null ){
            arcView.clearAnimation();
            arcView.executeReset();

        }
    }

    public void hideGauge() {
        arcView.setVisibility(View.INVISIBLE);
        if(pointView!=null){
            pointView.setVisibility(View.INVISIBLE);
        }
        if(arrowView!=null){
            arrowView.setVisibility(View.INVISIBLE);
        }
    }
    public void showGauge(){
        arcView.setVisibility(View.VISIBLE);
        if(pointView!=null){
            pointView.setVisibility(View.VISIBLE);
        }
        if(arrowView!=null){
            arrowView.setVisibility(View.VISIBLE);
        }
    }
    public void showArrow() {
        if(arrowView!=null){
            arrowView.setVisibility(View.VISIBLE);
        }
    }
    public void hideArrow() {
        if(arrowView!=null){
            arrowView.setVisibility(View.INVISIBLE);
        }
    }

}
