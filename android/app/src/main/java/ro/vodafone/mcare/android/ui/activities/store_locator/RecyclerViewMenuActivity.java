package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.views.toolbar.ToolbarOnScrollViewChangeListener;
import ro.vodafone.mcare.android.ui.views.toolbar.ToolbarScrollWithRecycleViewChangeListener;
import ro.vodafone.mcare.android.utils.Logger;

/**
 * Created by Bogdan Marica on 7/13/2017.
 */


public abstract class RecyclerViewMenuActivity extends BaseMenuActivity {

    public static final String FRAGMENT = "fragment";
    public static Logger LOGGER = Logger.getInstance(ro.vodafone.mcare.android.ui.activities.MenuActivity.class);

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;

    abstract protected int getContentLayoutResource();

    @Override
    protected void setLayout(){
        setContentView(R.layout.activity_menu_recycler_view);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout activity_container = (FrameLayout) findViewById(R.id.activity_container);
        View.inflate(this, getContentLayoutResource(), activity_container);
        ButterKnife.bind(this);
    }

    protected void setupRecycleScrollViewForCurrentView(RecyclerView menuScrollView) {
        if (menuScrollView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            menuScrollView.setTransitionGroup(true);

        menuScrollView.getViewTreeObserver().addOnScrollChangedListener(new ToolbarScrollWithRecycleViewChangeListener(menuScrollView) {
            @Override
            public void setColor(int color) {
                mToolbar.setToolBarColor(color);
            }

            @Override
            public void showViews() {
                mToolbar.showToolBar();
            }

            @Override
            public void hideViews() {
                mToolbar.hideToolBar();
            }
        });

        menuScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //Log.d(TAG, "onLayoutChange: ");
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                if (bottom <= height) {
                    getToolbar().showToolBar();
                }
            }
        });
    }

    protected void setupScrollViewForCurrentView(ScrollView menuScrollView) {
        if (menuScrollView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            menuScrollView.setTransitionGroup(true);

        menuScrollView.getViewTreeObserver().addOnScrollChangedListener(new ToolbarOnScrollViewChangeListener(menuScrollView) {
            @Override
            public void setColor(int color) {
                mToolbar.setToolBarColor(color);
            }

            @Override
            public void showViews() {
                mToolbar.showToolBar();
            }

            @Override
            public void hideViews() {
                mToolbar.hideToolBar();
            }
        });

        menuScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //Log.d(TAG, "onLayoutChange: ");
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                if (bottom <= height) {
                    getToolbar().showToolBar();
                }
            }
        });
    }

}

