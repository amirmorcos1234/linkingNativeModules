package ro.vodafone.mcare.android.ui.views.expandabales;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;

/**
 * Created by Victor Radulescu on 1/15/2018.
 */

public abstract class ExpandableViewGroup extends RelativeLayout {

    public final static int NOT_EXPANDED_GROUP_ID = R.id.notExpandedGroup;

    public ExpandableViewGroup(Context context) {
        super(context);
        init(null);
    }

    public ExpandableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    protected void init(AttributeSet attributes){
        setAttributes(attributes);
        setContent();
    }
    public void toggle(View view){
        if(isExpanded()){
            unExpand(120);
        }else{
            expand(120);
        }
    }
    protected boolean isExpanded(){
        return getExpandedView() != null && getExpandedView().getVisibility() == VISIBLE;
    }

    protected abstract void setContent();

    protected void setAttributes(AttributeSet attributes){
    }
    protected abstract View getArrowView();
    protected void unExpand(long durationMillis){
        try {
            final RotateAnimation animRotateUp = new RotateAnimation(0.0f, -180.0f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            animRotateUp.setFillAfter(true);
            animRotateUp.setDuration(durationMillis);
            getArrowView().startAnimation(animRotateUp);
            hideExpandedView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected abstract View getExpandedView();


    protected void expand(long durationMillis){

        try {
            final RotateAnimation animRotateDown = new RotateAnimation(-180.0f, 0.0f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            animRotateDown.setFillAfter(true);
            animRotateDown.setDuration(durationMillis);
            getArrowView().startAnimation(animRotateDown);
            showExpandedView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void hideExpandedView(){
        getExpandedView().setVisibility(GONE);

    }
    protected void showExpandedView(){
        getExpandedView().setVisibility(VISIBLE);

    }
}
