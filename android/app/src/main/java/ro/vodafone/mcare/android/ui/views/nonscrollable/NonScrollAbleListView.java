package ro.vodafone.mcare.android.ui.views.nonscrollable;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by bogdan marica on 2/27/2017.
 */


/**
 * This listview is used as a linear layout, however, the measured height for it is not computed correctly
 * Please avoid as this needs to be rewritten
 *
 * An alternative for this, with very limited functionality is provided by {@link ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout}
 */
@Deprecated
public class NonScrollAbleListView extends ListView {
    public NonScrollAbleListView(Context context) {

        super(context);
    }

    public NonScrollAbleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollAbleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NonScrollAbleListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
