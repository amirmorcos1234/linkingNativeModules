package ro.vodafone.mcare.android.ui.views.viewholders.identity;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Serban Radulescu on 7/17/2017.
 */

public class AccountHoldingViewHolder extends GroupIdentityViewHolder {

    @BindView(R.id.identity_title_tv)
    VodafoneTextView textView;

    @BindView(R.id.identity_iv)
    CircleImageView circleImageView;

    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.identity_checkBox)
    CheckBox checkBox;

    private boolean arrowAnimated = false;
    private int defaultMargin = 10;
    private Context context;



    @OnCheckedChanged(R.id.identity_checkBox)
    public void comute(android.widget.CompoundButton compoundButton, boolean checked){
    }
    public AccountHoldingViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this,itemView);
    }
    @Override
    public void setData(ExpandableListTree.Node data){
        super.setData(data);
        setText(data.getItem().getDisplayName());
        if(data.getChildren().size() > 0){
            setExpandableArrowForChildren(true);
        }
        else
        {
            setExpandableArrowForChildren(false);
        }

        int startMargin = computeStartMargin(node);
        startMargin = defaultMargin * startMargin;

        Glide.with(context)
                .load(R.drawable.country_or_international_black_bg)
                .placeholder(R.drawable.country_or_international_black_bg)
                .into(circleImageView);

        setMargins(circleImageView, ScreenMeasure.dpToPx(startMargin), ScreenMeasure.dpToPx(defaultMargin), 0, ScreenMeasure.dpToPx(defaultMargin));
    }
/*    @Override
    public void toggleExpanded(){
        node.setExpanded(!node.isExpanded());
        if(arrowAnimated) {
            unExpandArrow();
        }
        else {
            expandArrow();
        }
    }*/
    @Override
    public AppCompatImageView getArrowIndicator() {
        return arrowIndicator;
    }

    public void setArrowIndicator(AppCompatImageView arrowIndicator) {
        this.arrowIndicator = arrowIndicator;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public void setText(String text) {
        if(textView!=null){
            textView.setText(text);
        }
    }

    private void setExpandableArrowForChildren(boolean hasChildren){
        if(hasChildren){
            arrowIndicator.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);
        }
        else {
            arrowIndicator.setVisibility(View.GONE);
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void expandArrow(){
        arrowAnimated = true;
        final RotateAnimation animRotateUp = new RotateAnimation(90.0f, 270.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateUp.setFillAfter(true);
        animRotateUp.setDuration(120);
        arrowIndicator.startAnimation(animRotateUp);
    }

    private void unExpandArrow() {
        arrowAnimated = false;
        final RotateAnimation animRotateDown = new RotateAnimation(270.0f, 90.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);
        arrowIndicator.startAnimation(animRotateDown);
    }

    private static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


}
