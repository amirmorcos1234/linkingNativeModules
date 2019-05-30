package ro.vodafone.mcare.android.ui.views;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import ro.vodafone.mcare.android.R;

/**
 * This class is used to provide a LinearLayout that can manage its items similar to a ListView
 * and it's (unfortunately) used for lists of items inside of scrollviews
 */
public class ExpandableAdapterBackedLinearLayout extends LinearLayout {
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

    private ExpandableListAdapter adapter;
    private ExpandableListView.OnGroupClickListener onGroupClickListener;
    private ExpandableListView.OnChildClickListener onChildClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;
    /**
     * Childrens {@link LinearLayout} divider resource.
     */
    private int childrenDividerResource;

    /**
     *
     * @see LinearLayout#setShowDividers(int) for more details for his values
     *
     **/
    private int childrenDividerMethod;


    public ExpandableAdapterBackedLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public ExpandableAdapterBackedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableAdapterBackedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(21)
    public ExpandableAdapterBackedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setAttributes(attrs);
        setOrientation(VERTICAL);
    }
    private void setAttributes(@Nullable AttributeSet attrs){
        if(attrs==null){
            return;
        }
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                R.styleable.ExpandableAdapterBackedLinearLayout);
        try {
            childrenDividerResource = attributes.getResourceId(
                    R.styleable.ExpandableAdapterBackedLinearLayout_childrenDivider,-1);
            childrenDividerMethod = attributes.getInt(
                    R.styleable.ExpandableAdapterBackedLinearLayout_childrenDividerMode,LinearLayout.SHOW_DIVIDER_NONE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            attributes.recycle();
        }
    }

    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException(
                "For ExpandableListView, use setAdapter(ExpandableListAdapter) instead of setAdapter(ListAdapter)");
    }

    public void setAdapter(android.widget.ExpandableListAdapter adapter) {
        if(this.adapter != null)
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        this.adapter = adapter;
        removeAllViews();
        if(adapter != null){
            updateViews();
            adapter.registerDataSetObserver(dataSetObserver);
        }
    }

    private void updateViews(){
        if(adapter == null){
            removeAllViews();
            return;
        }
        for(int gi=0; gi<adapter.getGroupCount(); gi++){
            final View recycleHeaderView = getChildAt(gi*2);
            final View headerView = adapter.getGroupView(gi, false, recycleHeaderView, this);
            if(recycleHeaderView == null)
                addView(headerView);
            else if(recycleHeaderView != headerView) {
                removeViewAt(gi*2);
                addView(headerView, gi*2);
            }
            final int groupIndex = gi;
            headerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandGroup(groupIndex);
                    if(onGroupClickListener != null)
                        onGroupClickListener.onGroupClick(null, v, groupIndex, adapter.getGroupId(groupIndex));
                }
            });

            final LinearLayout recycleItemsView = (LinearLayout) getChildAt(gi*2 + 1);
            final LinearLayout itemsView;
            if(recycleItemsView == null) {
                itemsView = new LinearLayout(getContext());
                itemsView.setOrientation(VERTICAL);
                itemsView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                setDivider(itemsView);
            } else {
                itemsView = recycleItemsView;
                itemsView.removeAllViews();
            }
            if(recycleItemsView == null) {
                addView(itemsView);
            } else if(recycleItemsView != itemsView){
                removeViewAt(gi*2 + 1);
                addView(itemsView, gi*2 + 1);
            }
        }
        for(int i=adapter.getGroupCount()*2; i<getChildCount(); i++)
            removeViewAt(i);
    }

    private void setDivider(LinearLayout itemsView) {
        if(childrenDividerResource!=-1 && childrenDividerMethod !=-1){
            itemsView.setShowDividers(childrenDividerMethod);
            itemsView.setDividerDrawable(ContextCompat.getDrawable(getContext(),childrenDividerResource));
        }
    }

    public boolean expandGroup(final int groupPos) {
        if((groupPos*2+1) >= getChildCount())
            throw new ArrayIndexOutOfBoundsException("Trying to expand group #"+groupPos+ " only have " + (getChildCount()/2));

        final View recycleHeaderView = getChildAt(groupPos*2);
        final LinearLayout recycleItemsView = (LinearLayout) getChildAt(groupPos*2 + 1);
        final LinearLayout itemsView;
        if(recycleItemsView == null) {
            itemsView = new LinearLayout(getContext());
            itemsView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setDivider(itemsView);
        } else {
            itemsView = recycleItemsView;
        }

        final View headerView = adapter.getGroupView(groupPos, true, recycleHeaderView, this);
        if(recycleHeaderView == null)
            addView(headerView);
        else if(recycleHeaderView != headerView) {
            removeViewAt(groupPos*2);
            addView(headerView, groupPos*2);
        }

        headerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseGroup(groupPos);
                if(onGroupClickListener != null)
                    onGroupClickListener.onGroupClick(null, v, groupPos, adapter.getGroupId(groupPos));
            }
        });

        if(itemsView.getChildCount() != 0)
            return false;

        for(int i=0; i < adapter.getChildrenCount(groupPos); i++) {
            final View itemView = adapter.getChildView(groupPos, i, i == (adapter.getChildrenCount(groupPos) - 1), null, itemsView);
            if(itemView != null){
                itemsView.addView(itemView);
                final int index = i;
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onChildClickListener != null)
                            onChildClickListener.onChildClick(null, v, groupPos, index, adapter.getChildId(groupPos, index));
                        if(onItemClickListener != null)
                            onItemClickListener.onItemClick(null, v, -1, adapter.getChildId(groupPos, index));
                    }
                });
            }
        }
        return true;
    }

    public boolean collapseGroup(final int groupPos) {
        if((groupPos*2+1) >= getChildCount())
            throw new ArrayIndexOutOfBoundsException("Trying to collapse group #"+groupPos+ " only have " + (getChildCount()/2));

        final View recycleHeaderView = getChildAt(groupPos*2);
        final LinearLayout recycleItemsView = (LinearLayout) getChildAt(groupPos*2 + 1);
        final LinearLayout itemsView;
        if(recycleItemsView == null) {
            itemsView = new LinearLayout(getContext());
            itemsView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setDivider(itemsView);

        } else {
            itemsView = recycleItemsView;
        }

        final View headerView = adapter.getGroupView(groupPos, false, recycleHeaderView, this);
        if(recycleHeaderView == null)
            addView(headerView);
        else if(recycleHeaderView != headerView) {
            removeViewAt(groupPos*2);
            addView(headerView, groupPos*2);
        }

        headerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expandGroup(groupPos);
                if(onGroupClickListener != null)
                    onGroupClickListener.onGroupClick(null, v, groupPos, adapter.getGroupId(groupPos));
            }
        });

        if(itemsView.getChildCount() == 0)
            return false;

        itemsView.removeAllViews();
        return true;
    }

    /**
     * This has limited functionality. Calls to the listener will be done without a parent view (null)
     * @param onGroupClickListener
     */
    public void setOnGroupClickListener(ExpandableListView.OnGroupClickListener onGroupClickListener){
        this.onGroupClickListener = onGroupClickListener;
    }

    /**
     * This has limited functionality. Calls to the listener will be done without a parent view (null)
     * @param onChildClickListener
     */
    public void setOnChildClickListener(ExpandableListView.OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    /**
     * This has limited functionality. Calls to the listener will be done without a parent view (null) or position (-1)
     * @param l
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public int getChildrenDividerResource() {
        return childrenDividerResource;
    }

    public void setChildrenDividerResource(int childrenDividerResource) {
        this.childrenDividerResource = childrenDividerResource;
    }

    public int getChildrenDividerMethod() {
        return childrenDividerMethod;
    }

    public void setChildrenDividerMethod(int childrenDividerMethod) {
        this.childrenDividerMethod = childrenDividerMethod;
    }
}
