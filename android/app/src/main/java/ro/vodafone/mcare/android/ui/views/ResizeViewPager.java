package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Victor Radulescu on 1/26/2017.
 */

public class ResizeViewPager extends ViewPager {

    public ResizeViewPager(Context context) {
        super(context);
        init();
    }

    public ResizeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    Point dimens;

    private int max_height;

    private int currentHeight;

    private List<Integer> heights = new ArrayList<>();

    boolean autoResizeAfterFirstOne = true;

    private static final float thresholdOffset = 0.5f;

    float lastPosition;

    private boolean scrollStarted, checkDirection;
    OnPageChangeListener onPageChangeListener;

    boolean reachedNextPosition =true;

    private void initPageChangeLister(){
        onPageChangeListener = new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("direction", "position "+position+" position offset "+positionOffset+ " px offset "+positionOffsetPixels);

               /* if(positionOffset==0 && currentDirection == Direction.right && lastPosition<0.1){
                    currentDirection = Direction.left;
                }else if(positionOffset==0 && currentDirection == Direction.left && lastPosition>0.9){
                    currentDirection = Direction.right;
                }*/

                if (thresholdOffset > positionOffset) {
                    Log.i("direction", "position "+position+" position offset "+positionOffset+" going right");
                    currentDirection = Direction.right;
                } else {
                    Log.i("direction", "position "+position+" position offset "+positionOffset+" going left");
                    currentDirection = Direction.left;
                }
                checkDirection = false;
            }
            @Override
            public void onPageSelected(int position) {
                try {
                /*    currentPosition = position;
                    currentHeight = heights.get(currentPosition);
                    autoResizeAfterFirstOne = false;
                    requestLayout();*/
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                currentState = state;
                if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
                    scrollStarted = true;
                    checkDirection = true;
                } else {
                    scrollStarted = false;
                }
            }
        };
        addOnPageChangeListener(onPageChangeListener);
    }

    private void init(){
        initDimens();
        initPageChangeLister();
        setOffscreenPageLimit(3);

    }
    private void initDimens(){
        WindowManager windowManager = (WindowManager) (getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        dimens = new Point();
        windowManager.getDefaultDisplay().getRealSize(dimens);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        heights.clear();

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            //int h =measureViewHeight(child);
            //Log.d("resizeV height", i+" " + h );

            heights.add(i,h);

            if(h > height) {
                height = h;
                max_height = height;
            }
        }
        max_height = heights.isEmpty()?max_height:heights.get(0);
        currentHeight = autoResizeAfterFirstOne ? max_height : currentHeight;


        //Log.d("resizeV height","currentHeight"+currentHight);
        //Log.d("resizeV height","maxHeight"+max_height);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(currentHeight, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private Integer currentPosition = 0;

    private Integer currentState = 0;

    private Direction currentDirection = Direction.none;

    private enum Direction{
        none,
        left,
        right
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);

        Log.d("Listener","current offset"+ offset+ " position "+position+" \n" +
                "direction"+ currentDirection + " before offset "+lastPosition);

        try {
            if(offset==0 && lastPosition!=0){
                autoResizeAfterFirstOne = false;
                currentHeight = heights.get(position);
                requestLayout();
            }

           /* if(offset==0 && currentDirection == Direction.right && lastPosition<0.1 && lastPosition!=0){
                currentHight = heights.get(position);
                requestLayout();

            }else if(offset==0 && currentDirection == Direction.left && lastPosition>0.9){
                currentHight = heights.get(position);
                requestLayout();
            }*/
            /*if(currentDirection==Direction.right && currentPosition == getChildCount()-1){
                return;
            }
            if(currentDirection==Direction.left && currentPosition == 0){
                return;
            }*/
            lastPosition = offset;

            if(offset==0){
                currentPosition = position;
                return;
            }
            int nextPostion = findSwitchPosition(currentPosition);

            Log.d("Go", "ON page scrolled" + position +" currentPosition " +currentPosition+ " next"
                    + nextPostion + " offset: " + offset + " \n /n offset pixels " + offsetPixels
                    + " direction "+currentDirection);


            /*if(offset==0 || currentDirection.equals(Direction.none) || !reachedNextPosition){
               return;
           }*/

           if(currentHeight < heights.get(nextPostion) ){
               currentHeight =  heights.get(nextPostion);
               autoResizeAfterFirstOne = false;

               requestLayout();
               Log.d("Go measure","");
           }

            /*if (!scrollStarted) {
                currentHight = heights.get(currentPosition);
                requestLayout();
                return;
            }*/
            /*if (offset <= 0.1) {
                int nextPostion = findSwitchPosition(currentPosition);
                Log.d("Go", "nextposition " + nextPostion+" offset "+offset);

                if (currentHight < heights.get(nextPostion) && currentDirection == Direction.right) {
                    currentHight = heights.get(nextPostion);
                    autoResizeAfterFirstOne = false;
                    requestLayout();
                    Log.d("Go", "ON page scrolled" + position +" currentPosition " +currentPosition+ " next"
                            + nextPostion + " offset: " + offset + " \n /n offset pixels " + offsetPixels
                            + " direction "+currentDirection);
                } else if (currentHight > heights.get(nextPostion) && currentDirection == Direction.left) {
                    currentHight = heights.get(nextPostion);
                    autoResizeAfterFirstOne = false;
                    requestLayout();
                    Log.d("Go", "ON page scrolled" + position +" currentPosition " +currentPosition+ " next"
                            + nextPostion + " offset: " + offset + " \n /n offset pixels " + offsetPixels
                            + " direction "+currentDirection);
                }
            } else if (offset > 0.9) {
                int nextPostion = findSwitchPosition(currentPosition);
                Log.d("Go", "nextposition " + nextPostion+" offset "+offset);

                if (currentHight > heights.get(nextPostion) && currentDirection == Direction.right) {
                    currentHight = heights.get(nextPostion);
                    autoResizeAfterFirstOne = false;
                    requestLayout();
                    Log.d("Go", "ON page scrolled" + position +" currentPosition " +currentPosition+ " next"
                            + nextPostion + " offset: " + offset + " \n /n offset pixels " + offsetPixels
                            + " direction "+currentDirection);
                } else if (currentHight < heights.get(nextPostion) && currentDirection == Direction.left) {
                    currentHight = heights.get(nextPostion);
                    autoResizeAfterFirstOne = false;
                    requestLayout();
                    Log.d("Go", "ON page scrolled" + position +" currentPosition " +currentPosition+ " next"
                            + nextPostion + " offset: " + offset + " \n /n offset pixels " + offsetPixels
                            + " direction "+currentDirection);
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private int findNextPosition(int nextPosition) {
        int nextModulo = (nextPosition + 1) % getChildCount();
        if (nextModulo > nextPosition && nextModulo != getChildCount() ) {
            return nextModulo;
        } else {
            return nextPosition - 1;
        }

    }
    private int findSwitchPosition(int position) {
        Log.i("Go", "going "+currentDirection);

        if(currentDirection==Direction.right){
           Log.d("Go", "forward \n");
            return findNextPosition(position);
        }else if(currentDirection==Direction.left){
           Log.d("Go", "back" +" \n");

            return position!=0? position-1 : getChildCount()-1 ;
        }else{
            return position;
        }
    }

    public void setAutoResizeAfterFirstOne(boolean autoResizeAfterFirstOne) {
        this.autoResizeAfterFirstOne = autoResizeAfterFirstOne;
        invalidate();
        requestLayout();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 0 && getChildCount() == 0) {
            return false;
        }
        try {
            return super.onTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 0 && getChildCount() == 0) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public abstract class OnPageChangeListener implements ViewPager.OnPageChangeListener {
   }


    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            return super.onFilterTouchEventForSecurity(event);
        } else { return true; }
    }
}
