package ro.vodafone.mcare.android.ui.activities;

/**
 * Created by Victor Radulescu on 1/27/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.interfaces.exceptions.HttpExceptionSessionExpireListener;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.toolbar.DynamicToolbar;
import ro.vodafone.mcare.android.ui.views.toolbar.ToolbarScrollChangeListner;
import ro.vodafone.mcare.android.utils.Logger;


public abstract class MenuActivity extends BaseMenuActivity implements HttpExceptionSessionExpireListener {

    public static final String FRAGMENT = "fragment";
    public static String TAG = "MenuActivity";
    public static Logger LOGGER = Logger.getInstance(MenuActivity.class);

    //    @BindView(R.id.drawer_layout)
    protected PagingScrollView menuScrollView;
    //    @BindView(R.id.content)
    CoordinatorLayout coordinatorLayout;
    Context context;

    private RelativeLayout save_button_container;
    private VodafoneButton save_button;

    public DynamicToolbar getToolbar() {
        return mToolbar;
    }

    public PagingScrollView getMenuScrollView() {
        return menuScrollView;
    }

    public void scrolltoTop() {
        Log.d(TAG, "scrollToTop");
        menuScrollView.smoothScrollTo(0, 0);
        mToolbar.showToolBar();
    }

    @Override
    protected void setLayout(){
        setContentView(R.layout.activity_menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ButterKnife.bind(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        save_button_container = findViewById(R.id.button_container);
        save_button = findViewById(R.id.saveButton);

        setupHttpExceptionSessionExpireListener();
        initMenuScrollView();

        addContent();

    }

//    public void changeSaveButtonContainerVisibility(int visibility) {
//        save_button_container.setVisibility(visibility);
//    }
//
//    public void changeSaveButtonAvailability(boolean availability) {
//        save_button.setClickable(availability);
//        save_button.setEnabled(availability);
//    }

    public VodafoneButton getSaveButton() {
        return save_button;
    }

    public RelativeLayout getSaveButtonContainer() {
        return save_button_container;
    }


    private void initMenuScrollView() {

        menuScrollView = (PagingScrollView) findViewById(R.id.scroll_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            menuScrollView.setTransitionGroup(true);

        menuScrollView.getViewTreeObserver().addOnScrollChangedListener(new ToolbarScrollChangeListner(menuScrollView) {
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


    private void setupHttpExceptionSessionExpireListener() {
        VodafoneController.getInstance().setHttpExceptionSessionExpireListener(this);
    }

    @Override
    protected void onResumeFragments() {
//        D.e("bad");
        super.onResumeFragments();
        LOGGER.d("MenuActivity onResumeFragments");
    }



    private void addContent() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int contentRes = setContent();
        View content = vi.inflate(contentRes, null);
        coordinatorLayout.addView(content, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    protected abstract int setContent();

    protected View getContent() {
        return coordinatorLayout.getChildAt(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void startActivity(Intent intent) {
        if (VodafoneController.getInstance().isSessionExpired()) {
            return;
        }
        super.startActivity(intent);
    }

    @Override
    public void onSessionExpire() {
        VodafoneController.getInstance().setSessionExpired(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
