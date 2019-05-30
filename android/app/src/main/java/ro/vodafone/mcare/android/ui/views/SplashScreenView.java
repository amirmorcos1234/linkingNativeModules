package ro.vodafone.mcare.android.ui.views;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by Victor Radulescu on 12/13/2016.
 * TODO optimize class to use less resources and reuse canvas items
 */
@Deprecated
public class SplashScreenView extends View  implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {

    Paint paint = new Paint();
    int height;
    int width;
    ScreenMeasure screenMeasure ;
    Bitmap bmp;

    //circle cloackwise ( where 0 degree - is around 3 o clock)
    float angle = 0;
    final float startAngle = 313;
    float currentAngle = startAngle;

    final static float rotateAngleStart = 0;
    final static float rotateAngleEnd = 270;

    float finalAngle = 315+270;

    int iconFinalPositionX;
    int iconFinalPositionY;


    private ValueAnimator angleAnimator;

    Paint paintArc;
    RectF iconRect;
    Path arcPath;
    Canvas canvas;


    AnimationPhase currentAnimationPhase= AnimationPhase.SMALL_ROTATE_RHOMBUS;

    float iconPositionX;
    float iconPositionY;

    float iconInitialPositionX;
    float iconInitialPositionY;

    float currentIconSizeExtra= 0/100f;

    final long firstPhaseDuration = 3000;
    final long secondPhaseDuration = 670;

    final long textFadeOutTime = 150;
    final long textFadeInTime = 130;

    final long textFadeInDuration = 870;//870 in design
    final long textFadeOutDuration = 30;

    int alfaText = 0;

    boolean shouldDrawText = false;

    public OnSplashscreenViewAnimationListener onSplashscreenViewAnimationListener;
    /**
     * Distance from the center of the logo where arc is drawn.
     */
    float pointOnCircleRadius;


    public SplashScreenView(Context context) {
        super(context);
        init();
    }

    public SplashScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        drawImage(canvas);
    }

    private void drawImage(Canvas canvas){
        int iconWidth = bmp.getWidth();
        int iconHeight = bmp.getHeight();


        iconRect = new RectF(
                iconPositionX,
                iconPositionY,
                iconPositionX + iconHeight + (int) (iconWidth * currentIconSizeExtra ),
                iconPositionY + iconHeight + (int) (iconWidth * currentIconSizeExtra ));

        pointOnCircleRadius = (50f/100f*iconRect.width());

        if(currentAnimationPhase.equals(AnimationPhase.ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER)){
            drawArc(canvas);
           // rotatePath();
        }else{
            drawArc(canvas);
        }
       canvas.drawBitmap(bmp, null, iconRect, paint);

        if(shouldDrawText){
            drawText(iconRect.right+ScreenMeasure.dpToPx(24),iconRect.centerY());
        }

    }
    private void init(){
        screenMeasure = new ScreenMeasure(getContext());
       // setLayerType(LAYER_TYPE_SOFTWARE,null);
        paintArc = new Paint();
        iconInit();
        initArc();
    }
    private void iconInit(){
        DisplayMetrics displayMetrics = screenMeasure.getDisplayMetrics();
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        iconPositionX = 2 * width / 10;
        iconPositionY = 2 * height / 5;

        iconInitialPositionX =iconPositionX;
        iconInitialPositionY =iconPositionY;

        paint.setColor(Color.argb(255, 230, 0, 0));

        bmp = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_round_launcher);
        iconFinalPositionX = ScreenMeasure.dpToPx(12);
        iconFinalPositionY = ScreenMeasure.dpToPx(12);

        int iconWidth = bmp.getWidth();
        int iconHeight = bmp.getHeight();

        iconRect = new RectF(
                iconPositionX,
                iconPositionY,
                iconPositionX + iconHeight + (int) (iconWidth * currentIconSizeExtra ),
                iconPositionY + iconHeight + (int) (iconWidth * currentIconSizeExtra));
    }

    private void initArc() {

        paintArc.setColor(ContextCompat.getColor(getContext(), R.color.vodafoneRed));
        //paintArc.setStyle(Paint.Style.FILL);
        //paintArc.setStrokeWidth(1);
        paintArc.setStrokeCap(Paint.Cap.ROUND);
        paintArc.setStyle(Paint.Style.FILL);


        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);

       // Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

       // paint.setStrokeWidth(2);
       // paint.setColor(android.graphics.Color.RED);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
    }

    private void drawText(float x, float y){

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.splashscreen_text_size));
        paint.setAlpha(alfaText);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("My", x, y, paint);
        canvas.drawText("Vodafone", x+ScreenMeasure.dpToPx(35), y, paint);
    }

    private void drawArc(Canvas canvas) {

        float radius=width*1.5f;

        PointF pointOnCircle = getPointOnCircle(pointOnCircleRadius,-startAngle+currentAngle+180,iconRect.centerX(),iconRect.centerY());

        PointF pointOnCircle2 = getPointOnCircle(pointOnCircleRadius,-startAngle+currentAngle+135,iconRect.centerX(),iconRect.centerY());
        PointF pointOnCircle3 = getPointOnCircle(pointOnCircleRadius,-startAngle+currentAngle+225,iconRect.centerX(),iconRect.centerY());

        RectF rectArc = new RectF(pointOnCircle.x-radius, pointOnCircle.y-radius,pointOnCircle.x+radius, pointOnCircle.y + radius);

        RectF rectArc2 = new RectF(pointOnCircle2.x-radius, pointOnCircle2.y-radius,pointOnCircle2.x+radius, pointOnCircle2.y + radius);

        arcPath = new Path();
       // path.setFillType(Path.FillType.EVEN_ODD);

        arcPath.moveTo(pointOnCircle2.x,pointOnCircle2.y);
        arcPath.lineTo(pointOnCircle3.x,pointOnCircle3.y); //----
        arcPath.lineTo(pointOnCircle3.x+radius,pointOnCircle3.y-radius);
        arcPath.lineTo(pointOnCircle2.x+radius,pointOnCircle2.y+radius);
        arcPath.lineTo(pointOnCircle2.x,pointOnCircle2.y);
        arcPath.close();

        //if(currentAnimationPhase.equals(AnimationPhase.ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER)){
            rotatePath();
       // }

        canvas.drawPath(arcPath, paintArc);
       // canvas.rotate(angle, x, y);



        //canvas.drawArc(rectArc,currentAngle,angle,true,paintArc);
        //canvas.drawArc(rectArc2,currentAngle,angle,true,paintArc);
       /* final RectF ovalOuter = new RectF(iconRect.centerX()-iconRect.width(), iconRect.centerY()+iconRect.height(),
                iconRect.centerX()-iconRect.width()/4, iconRect.centerY()-iconRect.height());*/
        //ovalOuter.inset(1,1);
     //   Paint invisiblePaint = new Paint();
     //   invisiblePaint.setStyle(Paint.Style.FILL);
       // invisiblePaint.setColor(Color.GREEN);
      //  invisiblePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
       // ColorFilter filter = new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        //invisiblePaint.setColorFilter(filter);
      //  canvas.drawRect(ovalOuter,invisiblePaint);
    }
    private void rotatePath(){

      /*  canvas.save();
        canvas.rotate(angle);
        canvas.save();
       // canvas.translate(x, y);
        canvas.drawPath(arcPath, paint);
        canvas.restore();
        canvas.restore();*/
        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        arcPath.computeBounds(bounds, true);
        mMatrix.postRotate(angle,iconRect.centerX(), iconRect.centerY());
        arcPath.transform(mMatrix);

    }

    private PointF getPointOnCircle(float radius,float angleInDegrees,float xOrigin,float yOrigin){

        float x = (float)(radius * Math.cos(angleInDegrees * Math.PI / 180F)) + xOrigin;
        float y = (float)(radius * Math.sin(angleInDegrees * Math.PI / 180F)) + yOrigin;

        //Log.d("getPointOnCircle arc","Radius "+radius+" ,angleInDegrees "+angleInDegrees+" ,xOrigin is "+xOrigin+" ,yOrigin is "+yOrigin);
        //Log.d("getPointOnCircle arc","x is "+ x+" ,y is "+y);

        return new PointF(x,y);
    }

    public void startAnimation(OnSplashscreenViewAnimationListener onSplashscreenViewAnimationListener){
        this.onSplashscreenViewAnimationListener = onSplashscreenViewAnimationListener;
        animateFirstPhase();
    }
    private void animateFirstPhase(){
        angleAnimator =  ValueAnimator.ofFloat(startAngle, startAngle+4f);
        angleAnimator.setDuration(firstPhaseDuration);
        angleAnimator.addUpdateListener(this);
        angleAnimator.addListener(this);
        angleAnimator.start();

        ValueAnimator textFadeInAnimator =  ValueAnimator.ofInt(0, 255);
        textFadeInAnimator.setDuration(textFadeInDuration);
        textFadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shouldDrawText = true;
                alfaText= (int) animation.getAnimatedValue();
            }
        });
        textFadeInAnimator.setStartDelay(textFadeInTime);
        textFadeInAnimator.start();
    }
    private void animateSecondPhase(){
        currentAnimationPhase = AnimationPhase.ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER;
        //rotate to 270
        TimeInterpolator interpolator = new FastOutSlowInInterpolator();
        angleAnimator =  ValueAnimator.ofFloat(rotateAngleStart,rotateAngleEnd);
        angleAnimator.setDuration(secondPhaseDuration);
        angleAnimator.setInterpolator(interpolator);
        angleAnimator.addUpdateListener(this);
        angleAnimator.addListener(this);
        angleAnimator.start();

        ValueAnimator mAnimator2 =  ValueAnimator.ofFloat(iconInitialPositionX,iconFinalPositionX);
        mAnimator2.setDuration(secondPhaseDuration);
        mAnimator2.setInterpolator(interpolator);

        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iconPositionX = (float) animation.getAnimatedValue();
            }
        });
        mAnimator2.start();

        ValueAnimator mAnimator3 =  ValueAnimator.ofFloat(iconInitialPositionY,iconFinalPositionY);
        mAnimator3.setDuration(secondPhaseDuration);
        mAnimator3.setInterpolator(interpolator);
        mAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iconPositionY = (float) animation.getAnimatedValue();
            }
        });
        mAnimator3.start();

        //
        ValueAnimator resizeAnimator =  ValueAnimator.ofFloat(currentIconSizeExtra,0);
        resizeAnimator.setDuration(secondPhaseDuration);
        resizeAnimator.setInterpolator(interpolator);
        resizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentIconSizeExtra = (float) animation.getAnimatedValue();
               // iconInit();
            }
        });
        resizeAnimator.start();

        ValueAnimator textFadeOutAnimator =  ValueAnimator.ofInt(255, 0);
        textFadeOutAnimator.setDuration(textFadeOutDuration);
        textFadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alfaText= (int) animation.getAnimatedValue();
            }
        });
        textFadeOutAnimator.setStartDelay(textFadeOutTime);
        textFadeOutAnimator.start();
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        switch (currentAnimationPhase){
            case SMALL_ROTATE_RHOMBUS:
                currentAngle = (float) animation.getAnimatedValue();
                break;

            case ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER:
                angle = (float) animation.getAnimatedValue();
                if(animation.getCurrentPlayTime()>=textFadeOutTime+textFadeOutDuration && shouldDrawText){
                    //Log.d("VodafoneTriangle","Hide Text");
                    shouldDrawText = false;
                }
                //Log.d("animation", String.valueOf(animation.getCurrentPlayTime()));
                //realignArc(animation.getCurrentPlayTime());
                //moveIcon(animation.getCurrentPlayTime());
                break;
        }
        invalidate();

    }

    @Override
    public void onAnimationStart(Animator animation) {

        switch (currentAnimationPhase){

            case SMALL_ROTATE_RHOMBUS:
                break;

            case ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER:
                break;
        }

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        switch (currentAnimationPhase){

            case SMALL_ROTATE_RHOMBUS:
                animateSecondPhase();
                break;

            case ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER:
                onSplashscreenViewAnimationListener.onSplashScreenAnimationDone();
                break;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    enum AnimationPhase {
        SMALL_ROTATE_RHOMBUS,
        ROTATE_RHOMBUS_MOVE_ICON_TOP_LEFT_CORNER

    }

    /**
     * Use it to set the splashscreen view as a toolbar icon in your view parent.
     */
    public void setAsToolbarIcon(){
        currentIconSizeExtra =0;
        //currentAngle = finalAngle;
        angle = rotateAngleEnd;
        iconPositionX = iconFinalPositionX;
        iconPositionY = iconFinalPositionY;

        invalidate();


    }


    public interface  OnSplashscreenViewAnimationListener{
         void onSplashScreenAnimationDone();
    };


    private void moveIcon(float time){
        iconPositionX = (int) (iconPositionX + (time/670)*(iconInitialPositionX-iconFinalPositionX));
        iconPositionY = (int) (iconPositionY + (time/670)*(iconInitialPositionY-iconFinalPositionY));
    }

    private void drawImage(int x, int y, int width, int height, boolean inverted, Paint paint, Canvas canvas){

        int edge =  x + height;
        int degree = 90;
        double radians = screenMeasure.degreesToRadians(degree/2);

        Point p1 = new Point(x,y);
        int pointX = edge;
        int pointY2 = y +(int) (Math.tan(radians)*edge);
        int pointY3 = y + (int) (-Math.tan(radians)*edge);

        Point p2 = new Point(pointX, pointY2);
        Point p3 = new Point(pointX, pointY3);


        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x,p1.y);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.close();

        canvas.drawPath(path, paint);


        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_round_launcher);


        Paint paint2 = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(getContext(),R.color.red_button_color), PorterDuff.Mode.SRC_IN);
        paint2.setColorFilter(filter);
        canvas.drawBitmap(bmp,x,y,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wDesired = getPaddingLeft() + getPaddingRight() +
                Math.max(screenMeasure.getWidth(), getSuggestedMinimumWidth());
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = 0;
        switch(wSpecMode){
            case MeasureSpec.EXACTLY:
                w = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                w = Math.min(wDesired, MeasureSpec.getSize(widthMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
                w = wDesired;
                break;
        }

        int hDesired = (getPaddingTop() + getPaddingBottom() +
                        Math.max(screenMeasure.getHeight()+(int)iconRect.height()/2, getSuggestedMinimumHeight()));
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = 0;
        switch(hSpecMode){
            case MeasureSpec.EXACTLY:
                h = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                h = Math.min(hDesired, MeasureSpec.getSize(heightMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
                h = hDesired;
                break;
        }

        setMeasuredDimension(w, h);
    }
}
