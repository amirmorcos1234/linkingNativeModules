package ro.vodafone.mcare.android.custom;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class ProgressBarHandler {

    private Context mContext;
    private ProgressBar mProgressBar;
    private LinearLayout rl;
    private VodafoneTextView loadingTextView;
    private boolean isVisible = false;

    public ProgressBarHandler(Context context) {
        mContext = context;

        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();
        //((Activity) context).addContentView();
        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setClickable(false);

        //Personalize loading text
        loadingTextView = new VodafoneTextView(context);

        loadingTextView.setTextSize(25);
        loadingTextView.setTextColor(context.getResources().getColor(R.color.whiteNormalTextColor));
        loadingTextView.setGravity(CENTER_HORIZONTAL);
        loadingTextView.setTypeface(Fonts.getVodafoneRG());

        LinearLayout.LayoutParams progressBarrSizeParams = new LinearLayout.LayoutParams(ScreenMeasure.pxToDp(96), ScreenMeasure.pxToDp(96));
        progressBarrSizeParams.setMargins(0, ScreenMeasure.pxToDp(20), 0, ScreenMeasure.pxToDp(70));

        mProgressBar.setLayoutParams(progressBarrSizeParams);

        rl = new LinearLayout(context);

        rl.setOrientation(LinearLayout.VERTICAL);
        rl.setGravity(Gravity.CENTER);

        //Adding progress bar and text to layout
        rl.addView(loadingTextView);
        rl.addView(mProgressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        //Adding layout to viewGroup
        layout.addView(rl, params);
        hide();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void show(boolean isTransparentBackground, int loadingMessage) {
        if (mContext == null) {
            return;
        }

        setVisible(true);
        if (isTransparentBackground) {
            rl.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        } else {
            rl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.progress_bar_background_color));
        }

        mProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setText(mContext.getResources().getString(loadingMessage));
        loadingTextView.setVisibility(View.VISIBLE);
        ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void show(FrameLayout content, boolean isTransparentBackground, int loadingMessage) {


        //((Activity) context).addContentView();
        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setClickable(false);

        //Personalize loading text
        loadingTextView = new VodafoneTextView(mContext);

        loadingTextView.setTextSize(25);
        loadingTextView.setTextColor(mContext.getResources().getColor(R.color.whiteNormalTextColor));
        loadingTextView.setGravity(CENTER_HORIZONTAL);
        loadingTextView.setTypeface(Fonts.getVodafoneRG());

        LinearLayout.LayoutParams progressBarrSizeParams = new LinearLayout.LayoutParams(ScreenMeasure.pxToDp(96), ScreenMeasure.pxToDp(96));
        progressBarrSizeParams.setMargins(0, ScreenMeasure.pxToDp(20), 0, ScreenMeasure.pxToDp(70));

        mProgressBar.setLayoutParams(progressBarrSizeParams);

        rl = new LinearLayout(mContext);

        rl.setOrientation(LinearLayout.VERTICAL);
        rl.setGravity(Gravity.CENTER);

        //Adding progress bar and text to layout
        rl.addView(loadingTextView);
        rl.addView(mProgressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        //Adding layout to viewGroup
        content.addView(rl, params);
        hide();


        if (mContext == null) {
            return;
        }

        setVisible(true);
        if (isTransparentBackground) {
            rl.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        } else {
            rl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.progress_bar_background_color));
        }

        mProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setText(mContext.getResources().getString(loadingMessage));
        loadingTextView.setVisibility(View.VISIBLE);
        ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hide() {
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        setVisible(false);
        rl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        mProgressBar.setVisibility(View.INVISIBLE);
        loadingTextView.setVisibility(View.INVISIBLE);
    }
}