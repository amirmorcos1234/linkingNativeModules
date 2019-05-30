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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.ScrollableTabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;

/**
 * Created by Bogdan Marica on 9/1/2017.
 */

public class ScrollableTabCard extends HorizontalScrollView {

    LinearLayout contentHolder;

    int selectedIndex = -1;
    private ScrollableTabAdapterOnItemClickListener onItemClickListener;
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


    public ScrollableTabCard(Context context) {
        super(context);
        init();
    }

    public ScrollableTabCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollableTabCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollableTabCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        contentHolder = new LinearLayout(getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams slp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentHolder.setLayoutParams(lp);
        setLayoutParams(slp);
        this.addView(contentHolder);
        setHorizontalScrollBarEnabled(false);
        invalidate();
    }

    /**
     * WARNING this function does not provide the full functionality. The {@link AdapterView}
     * passed as parameter
     * is always null
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(@NonNull final ScrollableTabAdapterOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        for (int i = 0; i < contentHolder.getChildCount(); i++) {
            final int index = i;
            contentHolder.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, index, adapter.getItemId(index));
                }
            });
        }
    }


    void addTab(TabItem tab) {
        contentHolder.addView(tab);
    }

    void addTab(TabItem tab, int i) {
        contentHolder.addView(tab, i);
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


    private void updateViews() {
        if (adapter == null) {
            contentHolder.removeAllViews();
            return;
        }
        final boolean shouldRecycle = adapter.getViewTypeCount() > 1;
        for (int i = 0; i < adapter.getCount(); i++) {
            final TabItem recycleView = (TabItem) contentHolder.getChildAt(i);

            TabItem tab = (TabItem) adapter.getView(i, shouldRecycle ? recycleView : null, this);
            tab.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            if (recycleView == null)
                addTab(tab);
            else if (recycleView != tab) {
                contentHolder.removeViewAt(i);
                addTab(tab, i);
                recycleView.setMinimumWidth(ScreenMeasure.dpToPx(30));
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

//            recycleView.setMinimumWidth(ScreenMeasure.dpToPx(120));

        }
        for (int i = adapter.getCount(); i < contentHolder.getChildCount(); i++) {
            contentHolder.removeViewAt(i);
        }
        contentHolder.requestLayout();
        this.addView(contentHolder);
    }

    public int getSelectedTabIndex() {
        return selectedIndex;
    }

    public void setHighlighted(int index, boolean highlighted) {
        selectedIndex = index;
        ((TabItem) contentHolder.getChildAt(index)).setHighlighted(highlighted);
    }

    public void setDefaultHighlighted() {
        unselectAll();
        setHighlighted(0, true);
    }

    public void unselectAll() {
        for (int i = 0; i < contentHolder.getChildCount(); i++)
            ((TabItem) contentHolder.getChildAt(i)).setHighlighted(false);
    }

    public void setGreyMargins() {

        contentHolder.getChildAt(0).findViewById(R.id.grey_background_parent).setPadding(ScreenMeasure.dpToPx(12), 0, 0, 0);
        contentHolder.getChildAt(0).findViewById(R.id.grey_line_container).setPadding(ScreenMeasure.dpToPx(12), 0, 0, 0);
        contentHolder.getChildAt(0).findViewById(R.id.arrow_container).setPadding(ScreenMeasure.dpToPx(12), 0, 0, 0);

        contentHolder.getChildAt(contentHolder.getChildCount() - 1).findViewById(R.id.grey_background_parent).setPadding(0, 0, ScreenMeasure.dpToPx(12), 0);
        contentHolder.getChildAt(contentHolder.getChildCount() - 1).findViewById(R.id.grey_line_container).setPadding(0, 0, ScreenMeasure.dpToPx(12), 0);
        contentHolder.getChildAt(contentHolder.getChildCount() - 1).findViewById(R.id.arrow_container).setPadding(0, 0, ScreenMeasure.dpToPx(12), 0);
    }


}
