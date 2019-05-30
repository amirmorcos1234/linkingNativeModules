package ro.vodafone.mcare.android.deprecated;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dragos.ivanov on 26.08.2016.
 */
public class HideFloatingButtonOnScroll extends FloatingActionButton.Behavior {

    public HideFloatingButtonOnScroll(Context context, AttributeSet attributeSet){
        super();
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);

        //child -> Floating Action Button
        if (child.getVisibility() == View.VISIBLE && dy > 0) {
            child.hide();
        } else if (child.getVisibility() == View.GONE && dy < 0) {
            child.show();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
