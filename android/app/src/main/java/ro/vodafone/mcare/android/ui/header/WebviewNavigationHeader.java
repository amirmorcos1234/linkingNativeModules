package ro.vodafone.mcare.android.ui.header;

import android.content.Context;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by bogdan marica on 4/30/2017.
 */

public class WebviewNavigationHeader extends LinearLayout {
    TextView title;
    TextView subTitle;
    ImageView closeButton;
    ImageView refreshButton;
    DynamicColorImageView backButton;
    BaseActivity activity;
    WebView webview;

    public WebviewNavigationHeader(Context context) {
        super(context);
    }

    public WebviewNavigationHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WebviewNavigationHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebviewNavigationHeader init() {


        inflate(getContext(), R.layout.webview_navigation_header, this);


        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.subTitle);
        closeButton = (DynamicColorImageView) findViewById(R.id.closeButton);
        backButton = (DynamicColorImageView) findViewById(R.id.backButton);
        refreshButton = (DynamicColorImageView) findViewById(R.id.refreshButton);

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity instanceof WebviewActivity)
                    ((WebviewActivity)activity).close();
                else
                    activity.onBackPressed();
                D.w("CLOSE");
            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webview.canGoBack())
                    webview.goBack();
                D.w("BACK");
            }
        });

        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBarHandler.show(false, R.string.loading_text);
                D.w();
                D.w("activity = " + activity);
                activity.showLoadingDialog();
                activity.stopLoadingAfterDuration(10);
                webview.loadUrl("javascript:window.location.reload( true )");
                D.w("refresh");
            }
        });

        setTaskBarColored();
//        setPadding(0, getStatusBarHeight(), 0, 0);

        return this;
    }

    public void setTitle(String pageTitle) {
        title.setText(pageTitle);

        title.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setSubtitle(String subtitle) {
        subTitle.setText(subtitle);
    }

    public WebviewNavigationHeader setActivity(BaseActivity activity) {
        this.activity = activity;
        return this;
    }

    public ImageView getCloseButton(){
        return closeButton;
    }

    public WebviewNavigationHeader setWebview(WebView webview) {
        this.webview = webview;
        return this;
    }

    public void setTaskBarColored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.simple_red));
        }
    }

    public void changeBackArrowColor(int colorId)
    {
        backButton.setDrawableColor(colorId);
    }

    public void setBackButtonVisibility(boolean visible)
    {
        backButton.setVisibility(visible ? VISIBLE : GONE);
    }
}
