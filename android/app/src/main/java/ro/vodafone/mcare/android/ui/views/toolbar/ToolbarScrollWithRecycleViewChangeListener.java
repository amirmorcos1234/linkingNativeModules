package ro.vodafone.mcare.android.ui.views.toolbar;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

/**
 * Created by Victor Radulescu on 10/5/2017.
 */

public abstract class ToolbarScrollWithRecycleViewChangeListener implements ViewTreeObserver.OnScrollChangedListener {

    //constants
    private static final int HIDE_THRESHOLD = 20;
    private static final int ONLY_TRANSPARENT_HEYGHT = 50;
    private static final int COLOR_TRANSITION_HEYGHT = 256;
    private static final float FINAL_TOOLBAR_ALPHA = new Float(0.7);
    private static final int TOOLBAR_COLOR = Color.parseColor("#FF2D2D2D");

    private int scrolledDistance = 0;
    private int oldScrollY = 0;
    private boolean controlsVisible = false;
    private RecyclerView nestedScrollView;

    protected ToolbarScrollWithRecycleViewChangeListener(RecyclerView nestedScrollView) {
        this.nestedScrollView = nestedScrollView;
    }

    @Override
    public void onScrollChanged() {

        Float coeff = getcoefficient(FINAL_TOOLBAR_ALPHA, ONLY_TRANSPARENT_HEYGHT, COLOR_TRANSITION_HEYGHT);
        int scrollY = nestedScrollView.computeVerticalScrollOffset();

        if(scrollY <= ONLY_TRANSPARENT_HEYGHT){
            setColor(Color.TRANSPARENT);
        }else if (scrollY > ONLY_TRANSPARENT_HEYGHT && scrollY < COLOR_TRANSITION_HEYGHT){
            float alpha  = (float) coeff * (scrollY - ONLY_TRANSPARENT_HEYGHT);
            setColor(getColorWithAlpha(alpha, TOOLBAR_COLOR));
        }else{
            setColor(getColorWithAlpha(FINAL_TOOLBAR_ALPHA, TOOLBAR_COLOR));
        }

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            showViews();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            if(scrollY > ONLY_TRANSPARENT_HEYGHT){
                hideViews();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }

        if((controlsVisible && oldScrollY-scrollY>0) || (!controlsVisible && oldScrollY-scrollY<0)) {
            scrolledDistance += oldScrollY-scrollY;
        }

        oldScrollY = scrollY;
    }


    private float getcoefficient(float finalAlpha, int onlyTransparentHeight, int colorTransitionHeight){
        return (float) finalAlpha/(colorTransitionHeight - onlyTransparentHeight);
    }

    private static int getColorWithAlpha(float alpha, int baseColor) {
        //Log.d("Scrooling", "getColorWithAlpha from alpha " +alpha);
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    public abstract void setColor(int color);

    public abstract void showViews();

    public abstract void hideViews();

}
