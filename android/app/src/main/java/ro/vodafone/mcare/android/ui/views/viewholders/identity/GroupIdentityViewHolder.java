package ro.vodafone.mcare.android.ui.views.viewholders.identity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import ro.vodafone.mcare.android.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by Victor Radulescu on 11/23/2017.
 */

public abstract class GroupIdentityViewHolder extends IdentityViewHolder {

    public GroupIdentityViewHolder(View itemView) {
        super(itemView);
    }
    public abstract ImageView getArrowIndicator();

    public void animateArrowState(Animation.AnimationListener animationListener){
        if(getNode().isExpanded()){
            animateExpand(animationListener);
        }else{
            animateCollapse(animationListener);
        }
    }
    private void animateExpand(Animation.AnimationListener animationListener) {
        RotateAnimation rotate =
                new RotateAnimation(180, 0, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setAnimationListener(animationListener);
        getArrowIndicator().setAnimation(rotate);
    }

    private void animateCollapse(Animation.AnimationListener animationListener) {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setAnimationListener(animationListener);
        getArrowIndicator().setAnimation(rotate);
    }
    protected void setExpandDrawable(){
       getArrowIndicator().setImageResource(R.drawable.arrow_selector_up);
       getArrowIndicator().invalidate();
    }
    protected void setCollapseDrawable(){
       getArrowIndicator().setImageResource(R.drawable.arrow);
        getArrowIndicator().invalidate();
    }
    public void toggleExpanded(){
        node.setExpanded(!node.isExpanded());

    }
    public void setArrow(){
        if(getNode().isExpanded()){
            setExpandDrawable();
        }else{
            setCollapseDrawable();
        }
    }

    @Override
    public void setData(ExpandableListTree.Node node) {
        super.setData(node);
        setArrow();
    }
}
