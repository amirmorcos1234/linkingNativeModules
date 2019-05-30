package ro.vodafone.mcare.android.ui.myCards;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

public class AddCreditCardWebViewActivity extends WebviewActivity {
    private AddCardResponse addCardResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addCardResponse = new Gson().fromJson(IntentActionName.ADD_CREDIT_CARD_WEBVIEW.getOneUsageSerializedData(), AddCardResponse.class);
    }

    @Override
    public void initWebView() {
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
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
                if (webView.canGoBack())
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_enabled);
                else
                    webviewNavigationHeader.changeBackArrowColor(R.color.webview_back_button_disabled);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                stopLoadingDialog();
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleUri(url);
                    }
                });
                return super.shouldInterceptRequest(view, url);
            }
        });
        webView.loadData(addCardResponse.getHtmlForm(), "text/html", "UTF-8");
    }

    private boolean handleUri(final String url) {
        if (url != null && (url).contains(addCardResponse.getSuccessUrl())) {
            new CustomToast.Builder(this).message(R.string.add_card_success).success(true).show();
            new NavigationAction(AddCreditCardWebViewActivity.this).startAction(IntentActionName.DASHBOARD, true);
            return true;
        }
        return false;
    }
}
