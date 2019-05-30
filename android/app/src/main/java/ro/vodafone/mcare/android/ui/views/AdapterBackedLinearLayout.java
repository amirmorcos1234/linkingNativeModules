package ro.vodafone.mcare.android.ui.views;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * This class is used to provide a LinearLayout that can manage its items similar to a ListView
 * and it's (unfortunately) used for lists of items inside of scrollviews
 */
public class AdapterBackedLinearLayout extends LinearLayout {
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

    private ListAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterBackedLinearLayout(Context context) {
        super(context);
        init();
    }

    public AdapterBackedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdapterBackedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public AdapterBackedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
    }

    /**
     * Used to provide instantiation of items similar to what a {@link android.widget.ListView} would accept as parameter
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        if((adapter != null) && (adapter.getViewTypeCount() > 1)){
            Log.w(AdapterBackedLinearLayout.class.getName(), "WARNING: using multiple view types here can have performance implications");
        }
        try {
            if(this.adapter != null)
                this.adapter.unregisterDataSetObserver(dataSetObserver);
            this.adapter = adapter;
            removeAllViews();
            if(adapter != null) {
                updateViews();
                adapter.registerDataSetObserver(dataSetObserver);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Used to provide adapter similar to a {@link android.widget.ListView}
     */
    public ListAdapter getAdapter(){
        return this.adapter;
    }

    private void updateViews() {
        if(adapter == null){
            removeAllViews();
            return;
        }
        final boolean shouldRecycle = adapter.getViewTypeCount() > 1;
        for(int i=0; i < adapter.getCount(); i++) {
            final View recycleView = getChildAt(i);
            final View view = adapter.getView(i, shouldRecycle?recycleView:null, this);
            if(recycleView == null)
                addView(view);
            else if(recycleView != view){
                removeViewAt(i);
                addView(view, i);
            }
            final int index = i;
            if(onItemClickListener != null)
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemClickListener != null)
                         onItemClickListener.onItemClick(null, v, index, adapter.getItemId(index));
                    }
                });
        }
        for(int i=adapter.getCount(); i< getChildCount(); i++){
            removeViewAt(i);
        }
        requestLayout();
    }

    /**
     * WARNING this function does not provide the full functionality. The {@link AdapterView}
     * passed as parameter to {@link AdapterView.android.widget.AdapterView.OnItemClickListener.onItemClick}
     * is always null
     * @param onItemClickListener
     */
    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
        for(int i=0; i<getChildCount(); i++) {
            final int index = i;
            if(onItemClickListener != null)
                getChildAt(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemClickListener != null)
                            onItemClickListener.onItemClick(null, v, index, adapter.getItemId(index));
                    }
                });
        }
    }
}
