package ro.vodafone.mcare.android.ui.views.viewholders.identity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.checkboxes.GetterCheckedListenerAppCompatCheckBox;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Serban Radulescu on 7/17/2017.
 */

public class BillingCustomerViewHolder extends IdentityViewHolder{


    @BindView(R.id.identity_title_tv)
    VodafoneTextView textView;

    @BindView(R.id.identity_iv)
    CircleImageView circleImageView;

    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.identity_checkBox)
    GetterCheckedListenerAppCompatCheckBox checkBox;

    private int defaultMargin = 10;
    private Context context;

    public BillingCustomerViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this,itemView);
    }
    @Override
    public void setData(ExpandableListTree.Node data){
        super.setData(data);
        D.w("Node selected"+ data.isSelected()+ " node is Expandable "+ node.isExpandable()+" id"+ node.getItem().getEntityId());

        setText(data.getItem().getDisplayName());

        setExpandableArrowForChildren(false);


        int startMargin = computeStartMargin(node);
        startMargin = defaultMargin * startMargin;
        if(getNode().haveParent() && VodafoneController.getInstance()!=null){
            circleImageView.setImageResource(R.drawable.users);
            circleImageView.setCircleBackgroundColor(ContextCompat.getColor(VodafoneController.getInstance(),R.color.background_gray));
            circleImageView.invalidate();
        }else{
            circleImageView.setImageResource(R.drawable.default_avatar_icon);
        }
        setMargins(circleImageView, ScreenMeasure.dpToPx(startMargin), ScreenMeasure.dpToPx(defaultMargin), 0, ScreenMeasure.dpToPx(defaultMargin));
    }

    public AppCompatImageView getArrowIndicator() {
        return arrowIndicator;
    }

    public void setArrowIndicator(AppCompatImageView arrowIndicator) {
        this.arrowIndicator = arrowIndicator;
    }

    public GetterCheckedListenerAppCompatCheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(GetterCheckedListenerAppCompatCheckBox checkBox) {
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

    private void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
