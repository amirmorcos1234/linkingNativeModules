package ro.vodafone.mcare.android.ui.webviews;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

import ro.vodafone.mcare.android.R;

/**
 * Created by Andrei DOLTU on 5/8/2017.
 */

public class SurveyWebViewActivity extends WebviewActivity {


    public static String TAG = "SurveyWebView";

    private String surveyTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //webviewNavigationHeader.setBackButtonVisibility(false);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            surveyTitle = bundle.getString("surveyTitle");
            webviewUrl = bundle.getString("surveyUrl");
        }

        initWebView();
        webView.loadUrl(webviewUrl);

    }


    @Override
    public void initWebView() {
        super.initWebView();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG , " onPageStarted " + url);
                showLoadingDialog();

                try {
                    webviewNavigationHeader.setSubtitle(new URL(url).getHost());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopLoadingDialog();
                webviewNavigationHeader.setTitle(surveyTitle);

                if (webView.canGoBack())
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
                else
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("complete")) {
//                    Log.d(TAG, "url - " + url);
                    //redirect to dashboard and show toast
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                Log.e(TAG, "WebResourceError - " + error.toString());
                stopLoadingDialog();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initWebView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
