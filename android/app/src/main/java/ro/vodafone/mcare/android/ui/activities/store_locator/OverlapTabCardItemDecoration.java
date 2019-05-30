package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by Bogdan Marica on 7/14/2017.
 */

public class OverlapTabCardItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) == 2)
            outRect.set(0, 0, 0, ScreenMeasure.dpToPx(-11));
//        else if (parent.getChildAdapterPosition(view) == 1)
//            outRect.set(0, 0, 0, ScreenMeasure.dpToPx(-11));
    }
}
