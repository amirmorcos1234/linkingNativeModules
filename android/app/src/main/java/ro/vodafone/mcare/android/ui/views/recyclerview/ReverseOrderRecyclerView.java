package ro.vodafone.mcare.android.ui.views.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Bogdan Marica on 7/27/2017.
 */

public class ReverseOrderRecyclerView extends RecyclerView {
    public ReverseOrderRecyclerView(Context context) {
        super(context);
    }

    public ReverseOrderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReverseOrderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        //The standard implementation just retuns i
        return childCount - i - 1;
    }

}