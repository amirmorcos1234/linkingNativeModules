package ro.vodafone.mcare.android.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import ro.vodafone.mcare.android.R;

/**
 * @author Bivol Pavel
 *
 */
public class DrawingTriangle extends View {
    private static String TAG = "DrawingTriangle";

    Context context;

    int screenWidth;
    int screenHeight;

    Paint trianglePaint;
    Path trianglePath;

    /**
     * @param context
     */
    public DrawingTriangle(Context context) {
        super(context);
        this.context = context;
        getScreenSize();
        commonConstructor(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public DrawingTriangle(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        getScreenSize();
        commonConstructor(context);

    }

    /**
     * @param context
     */
    private void commonConstructor(Context context) {
        trianglePaint = new Paint();
        trianglePaint.setStyle(Style.FILL);
        trianglePaint.setColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));
        trianglePaint.setAntiAlias(true);


        trianglePath = getRectangleWithTransparentTriangle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(trianglePath, trianglePaint);
    }

    private Path getRectangleWithTransparentTriangle() {

        int triangleHeigth = ScreenMeasure.dpToPx(18);
        int triangleWidth =  ScreenMeasure.dpToPx(30);


        Point p1 = new Point(0, 0);
        Point p2 = new Point(screenWidth/2 - triangleWidth/2, 0);
        Point p3 = new Point(screenWidth/2, triangleHeigth);
        Point p4 = new Point(screenWidth/2 +triangleWidth/2, 0);
        Point p5 = new Point(screenWidth, 0);
        Point p6 = new Point(screenWidth, triangleHeigth);
        Point p7 = new Point(0, triangleHeigth);


        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p4.x, p4.y);
        path.lineTo(p5.x, p5.y);
        path.lineTo(p6.x, p6.y);
        path.lineTo(p7.x, p7.y);

        return path;
    }


    public void getScreenSize(){

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

    }
}