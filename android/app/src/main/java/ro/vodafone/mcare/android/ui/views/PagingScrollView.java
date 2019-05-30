package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Alex on 3/1/2017.
 */

public class PagingScrollView extends NestedScrollView {
    OnBottomReachedListener mListener;

    private boolean enableScrolling = true;

    @Override
    public void scrollTo(int x, int y) {
       super.scrollTo(x, y);
    }

    public PagingScrollView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    public PagingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public  PagingScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        if (diff == 0 && mListener != null) {
            mListener.onBottomReached();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isEnableScrolling())
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isEnableScrolling())
            return super.onTouchEvent(ev);
        else
            return false;
    }


    public void setEnableScrolling(boolean enableScrolling){
        this.enableScrolling = enableScrolling;
    }

    public boolean isEnableScrolling(){
        return enableScrolling;
    }

    /**
     * Event listener.
     */
    public interface OnBottomReachedListener{
        public void onBottomReached();
    }
}
