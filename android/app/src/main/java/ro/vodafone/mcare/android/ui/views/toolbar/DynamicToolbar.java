package ro.vodafone.mcare.android.ui.views.toolbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.widget.BubbleMenuButton;

/**
 * Created by Bivol Pavel on 29.04.2017.
 */

public class DynamicToolbar extends Toolbar {

    private Context mContext;

    private ImageView leftButton;
    private BubbleMenuButton menuButton;

    public DynamicToolbar(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public DynamicToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        includeButtonsLayout();
        setBackButton();
        setTitle(null);
    }

    private void includeButtonsLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbar_content, this);

        leftButton = (ImageView) view.findViewById(R.id.left_button);
        menuButton = (BubbleMenuButton) view.findViewById(R.id.menu_button);
        ViewCompat.setZ(menuButton, 1000000);

        leftButton.setColorFilter(Color.parseColor("#ffffff"));

    }

    public DynamicToolbar setBackButton() {
        Log.d("dynamicToolbar", "setBackButton");
        leftButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_left_48));
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dynamicToolbar", "back button click");
                ((Activity) mContext).onBackPressed();
            }
        });
        return this;

    }

    public DynamicToolbar setCloseButton() {
        leftButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.close_48));
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).onBackPressed();
            }
        });
        return this;
    }


    public DynamicToolbar setMenuButtonCLickListner(OnClickListener onClickListener) {
        menuButton.setOnClickListener(onClickListener);
        return this;
    }

    public DynamicToolbar hideLeftButton() {
        leftButton.setVisibility(INVISIBLE);
        return this;
    }

    public DynamicToolbar hideMenuButton() {
        menuButton.setVisibility(INVISIBLE);
        return this;
    }

    public DynamicToolbar showDefaultToolBar() {
        setToolBarColor(Color.TRANSPARENT);
        setBackButton();
        setMenuButtonCLickListner(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseMenuActivity) mContext).openDrawer();
            }
        });
        return this;
    }

    public DynamicToolbar showDashboardToolBar() {
        hideLeftButton();
        setMenuButtonCLickListner(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseMenuActivity) mContext).openDrawer();
            }
        });
        return this;
    }

    public DynamicToolbar showWebViewToolBar() {
        setCloseButton();
        hideMenuButton();
        return this;
    }

    public void setToolBarColor(int color) {
        setBackgroundColor(color);
    }

    public void hideToolBar() {
        this.animate().translationY(-this.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void hideToolbarWithoutAnimation() {
        if (((AppCompatActivity) mContext).getSupportActionBar() != null) {
            ((AppCompatActivity) mContext).getSupportActionBar().hide();
        }
    }

    public void showToolBar() {
        this.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public void showToolbarWithoutAnimation() {
        if (((AppCompatActivity) mContext).getSupportActionBar() != null) {
            ((AppCompatActivity) mContext).getSupportActionBar().show();
        }
    }

}
