package ro.vodafone.mcare.android.ui.views.expandabales;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 1/15/2018.
 */

public class ExpandableLinearButtonsViewGroup extends ExpandableViewGroup {

    private final static int EXPANDABLE_LL_BUTTONS_GROUP_ID = R.id.expandable_buttons_group_ll_buttons_group;

    @BindView(R.id.arrow_indicator)
    DynamicColorImageView arrowImageView;

    @BindView(R.id.nameTextView)
    VodafoneTextView nameTextView;

    public ExpandableLinearButtonsViewGroup(Context context) {
        super(context);
    }

    public ExpandableLinearButtonsViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableLinearButtonsViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setContent() {
        inflate(getContext(), R.layout.expandable_buttons_group,this);
        ButterKnife.bind(this);
        hideExpandedView();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(v);
            }
    });
        getExpandedView().setOnClickListener(null);
    }
    public void build(){
        unExpand(0);
    }

    @Override
    protected View getArrowView() {
        return arrowImageView;
    }

    @Override
    protected View getExpandedView() {
        return this.<LinearLayout>findViewById(EXPANDABLE_LL_BUTTONS_GROUP_ID);
    }
    public void setNameTextView(String name){
        nameTextView.setText(name);
    }
    public VodafoneButton addButtonViewToExpandableGroup(String text,int styleResource, WeakReference<OnClickListener> onClickListenerWeakReference){
        VodafoneButton button = new VodafoneButton(new ContextThemeWrapper(getContext(),styleResource),null,styleResource);
        button.setText(text);
        if(onClickListenerWeakReference!=null){
            button.setOnClickListener(onClickListenerWeakReference.get());
        }
        this.addButtonViewToExpandableGroup(button);
        return button;
    }
    public ExpandableLinearButtonsViewGroup addButtonViewToExpandableGroup(Button button){
        if(button==null || button.getParent()!=null){
            //TODO throw exception ??
            return this;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(48));
        int margins = getResources().getDimensionPixelSize(R.dimen.general_margin);
        layoutParams.setMargins(margins,margins,margins,0);
        button.setMaxHeight(ScreenMeasure.dpToPx(32));
        this.<LinearLayout>findViewById(EXPANDABLE_LL_BUTTONS_GROUP_ID).addView(button,layoutParams);
        return this;
    }
}
