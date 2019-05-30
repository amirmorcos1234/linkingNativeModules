package ro.vodafone.mcare.android.ui.views.images;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Px;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

public class CheckableImageView extends AppCompatImageView implements Checkable, View.OnClickListener {
    private boolean mChecked;

    private float radius = ScreenMeasure.dpToPx(8);
    private RectF outlineRect;
    private final Path path = new Path();
    private final int checkedPadding = ScreenMeasure.dpToPx(4);
    private final int uncheckedPadding = ScreenMeasure.dpToPx(1);

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableImageView(Context context) {
        super(context);
        init();
    }

    public CheckableImageView(Context context, int rectSize) {
        super(context);
        init();
    }

    public CheckableImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(final boolean checked) {
        //this is a hack to set the outline on the views
        if(checked)
            setPadding(checkedPadding, checkedPadding, checkedPadding, checkedPadding);
        else
            //this is a hack to avoid non-highlighted/non-outlined views being larger than highlighted ones
            setPadding(uncheckedPadding, uncheckedPadding, uncheckedPadding, uncheckedPadding);
        if (mChecked == checked)
            return;
        mChecked = checked;
        refreshDrawableState();
    }

    @Override
    public void onClick(View v) {
      toggle();
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        //this is a hack to store the padding given by the app compat view to our view so that the highlight views are shown correctly
        super.setPadding(left, top, right, bottom);
    }
}