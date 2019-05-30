package ro.vodafone.mcare.android.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by alexandrulepadatu on 3/8/18.
 */

public class CustomRecyclerView extends RecyclerView
{
    private GestureDetector detector;

    private AdapterView.OnItemClickListener itemClickListener;
    private AdapterView.OnItemLongClickListener itemLongClickListener;

    private View emptyView;         // View to be displayed if the dataset is empty
    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            if (emptyView != null)
                emptyView.setVisibility(getAdapter() != null && getAdapter().getItemCount() == 0? VISIBLE : GONE);
        }
    };

    public CustomRecyclerView(Context context)
    {
        super(context);

        init();
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init();
    }

    protected void init()
    {
//        addOnItemTouchListener(new OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
//            {
//                if (itemClickListener != null && e.getAction() == MotionEvent.ACTION_DOWN)
//                    itemClickListener.onItemClick(null, rv.findChildViewUnder(e.getX(),e.getY()), rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(),e.getY())), 0);
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                Log.e("rv", "touched item:" +
//                        rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(),e.getY())));
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
//        });
//
//
//        detector = new android.view.GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e)
//            {
//                if (itemClickListener != null)
//                    itemClickListener.onItemClick(null,
//                            CustomRecyclerView.this.findChildViewUnder(e.getX(),e.getY()),
//                            CustomRecyclerView.this.getChildAdapterPosition(CustomRecyclerView.this.findChildViewUnder(e.getX(),e.getY())),
//                            0);
//
//                return true;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent motionEvent) {}
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e)
//            {
//                if (itemLongClickListener != null)
//                    itemLongClickListener.onItemLongClick(null,
//                            CustomRecyclerView.this.findChildViewUnder(e.getX(),e.getY()),
//                            CustomRecyclerView.this.getChildAdapterPosition(CustomRecyclerView.this.findChildViewUnder(e.getX(),e.getY())),
//                0);
//            }
//
//            @Override
//            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//                return false;
//            }
//        });

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (itemLongClickListener != null)
                {
                    View child = findChildViewUnder(e.getX(), e.getY());
                    if (child != null)
                        itemLongClickListener.onItemLongClick(null, child, getChildAdapterPosition(child), 0);
                }
            }
        });

        addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
            {
                if (itemClickListener != null)
                {
                    View child = findChildViewUnder(e.getX(), e.getY());
                    if (child != null && gestureDetector.onTouchEvent(e))
                        itemClickListener.onItemClick(null, child, rv.getChildAdapterPosition(child), 0);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e)
//    {
//        return detector.onTouchEvent(e);
//    }

    public void setEmptyView(View emptyView)
    {
        this.emptyView = emptyView;

        adapterDataObserver.onChanged();
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        Adapter oldAdapter = getAdapter();
        super.setAdapter(adapter);
        if (oldAdapter != null)
            oldAdapter.unregisterAdapterDataObserver(adapterDataObserver);

        if (adapter != null)
            adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(AdapterView.OnItemLongClickListener itemLongClickListener)
    {
        this.itemLongClickListener = itemLongClickListener;
    }
}
