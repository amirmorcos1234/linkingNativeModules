package ro.vodafone.mcare.android.widget.TabMenu;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.TabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;

/**
 * Created by Bogdan Marica on 7/12/2017.
 */

public class TabCard extends LinearLayout {
    int selectedIndex = -1;
    private TabAdapterOnItemClickListener onItemClickListener;
    private ListAdapter adapter;
    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    public TabCard(Context context) {
        super(context);
        init();
    }

    public TabCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TabCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0,0,ScreenMeasure.dpToPx(-12),0);
        lp.setMargins(0,0,0,0);
        setLayoutParams(lp);
    }

    public void unselectAll() {
        for (int i = 0; i < getChildCount(); i++)
            ((TabItem) getChildAt(i)).setHighlighted(false);
    }

    void addTab(TabItem tab) {
        addView(tab);
    }

    void addTab(TabItem tab, int i) {
        addView(tab, i);
    }

    /**
     * Used to provide adapter similar to a {@link android.widget.ListView}
     */
    public ListAdapter getAdapter() {
        return this.adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        if ((adapter != null) && (adapter.getViewTypeCount() > 1)) {
            Log.w(AdapterBackedLinearLayout.class.getName(), "WARNING: using multiple view types here can have performance implications");
        }
        if (this.adapter != null)
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        this.adapter = adapter;
        removeAllViews();
        if (adapter != null) {
            updateViews();
            adapter.registerDataSetObserver(dataSetObserver);
        }
    }

    /**
     * WARNING this function does not provide the full functionality. The {@link AdapterView}
     * passed as parameter
     * is always null
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(@NonNull final TabAdapterOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, index, adapter.getItemId(index));
                }
            });
        }
    }

    private void updateViews() {
        if (adapter == null) {
            removeAllViews();
            return;
        }
        final boolean shouldRecycle = adapter.getViewTypeCount() > 1;
        for (int i = 0; i < adapter.getCount(); i++) {
            final TabItem recycleView = (TabItem) getChildAt(i);

            TabItem tab = (TabItem) adapter.getView(i, shouldRecycle ? recycleView : null, this);
            setWeightSum(getChildCount() + 1);
            tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

//            recycleView.findViewById(R.id.grey_background_parent).setPadding(0,0,0,0);

            if (recycleView == null)
                addTab(tab);
            else if (recycleView != tab) {
                removeViewAt(i);
                addTab(tab, i);
            }
            final int index = i;
            if (onItemClickListener != null)
                tab.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(null, v, index, adapter.getItemId(index));
                    }
                });
        }
        for (int i = adapter.getCount(); i < getChildCount(); i++) {
            removeViewAt(i);
        }
        requestLayout();
    }

    public int getSelectedTabIndex() {
        return selectedIndex;
    }

    public void setHighlighted(int index, boolean highlighted) {
        selectedIndex=index;
        ((TabItem) getChildAt(index)).setHighlighted(highlighted);
    }
    public void setPosition(int index){
        selectedIndex=index;
        unselectAll();
        setHighlighted(index,true);

        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, getChildAt(index), index, adapter.getItemId(index));

//        if (onItemClickListener != null)
//            onItemClickListener.onItemClick(null, getChildAt(selectedIndex), selectedIndex, adapter.getItemId(selectedIndex));
    }

    public void setDefaultHighlighted() {
        unselectAll();
        setHighlighted(0, true);
    }


    public void setGreyMargins(){

        getChildAt(0).findViewById(R.id.grey_background_parent).setPadding(ScreenMeasure.dpToPx(12),0,0,0);
        getChildAt(0).findViewById(R.id.grey_line_container).setPadding(ScreenMeasure.dpToPx(12),0,0,0);
        getChildAt(0).findViewById(R.id.arrow_container).setPadding(ScreenMeasure.dpToPx(12),0,0,0);

        getChildAt(getChildCount()-1).findViewById(R.id.grey_background_parent).setPadding(0,0,ScreenMeasure.dpToPx(12),0);
        getChildAt(getChildCount()-1).findViewById(R.id.grey_line_container).setPadding(0,0,ScreenMeasure.dpToPx(12),0);
        getChildAt(getChildCount()-1).findViewById(R.id.arrow_container).setPadding(0,0,ScreenMeasure.dpToPx(12),0);
    }


}
