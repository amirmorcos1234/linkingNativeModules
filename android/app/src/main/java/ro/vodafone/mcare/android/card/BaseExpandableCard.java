package ro.vodafone.mcare.android.card;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;

/**
 * Created by Bivol Pavel on 24.10.2017.
 */

public class BaseExpandableCard extends VodafoneAbstractCard{

    private static final int MAX_LEVEL = 10000;

    @BindView(R.id.view_container)
    LinearLayout viewContainer;

    @BindView(R.id.card_title_tv)
    AutoResizeTextView titleTextView;

    @BindView(R.id.arrow)
    AppCompatImageView arrow;

    @BindView(R.id.separator_line)
    View separatorLine;

    private String title;
    private View contentView;

    Drawable arrowDrawable;

    public BaseExpandableCard(Context context) {
        super(context);
        init();
    }

    public BaseExpandableCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseExpandableCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        initArrow();
    }

    @Override
    protected int setContent() {
        return R.layout.card_expandable;
    }

    @OnClick(R.id.expandable_card_header)
    public void toggle(View view) {
        if (isExpanded()) {
            unExpand();
        } else {
            expand();
        }
    }

    private boolean isExpanded(){
        return viewContainer.getVisibility() == VISIBLE;
    }

    public BaseExpandableCard expand(){
        animateArrow(true);
        viewContainer.setVisibility(VISIBLE);
        separatorLine.setVisibility(VISIBLE);
        return this;
    }

    public BaseExpandableCard unExpand(){
        animateArrow(false);
        viewContainer.setVisibility(GONE);
        separatorLine.setVisibility(GONE);
        return this;
    }

    public BaseExpandableCard setTitle(String title){
        this.title = title;
        return this;
    }

    public BaseExpandableCard setContent(View view){
        this.contentView = view;
        return this;
    }

    public BaseExpandableCard build(){
        titleTextView.setText(title);
        viewContainer.addView(contentView);
        return this;
    }

    public BaseExpandableCard setExpandableSectionPaddings(int l, int t, int r, int b){
        viewContainer.setPadding(l, t, r, b);
        return this;
    }

    private void initArrow(){
        Drawable basicDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow);
        arrowDrawable = DrawableCompat.wrap(basicDrawable);
        DrawableCompat.setTint(arrowDrawable, ContextCompat.getColor(getContext(), R.color.product_cards_red_color_primary));
        arrow.setImageDrawable(arrowDrawable);
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }
}
