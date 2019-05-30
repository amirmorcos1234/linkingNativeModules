package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 4/6/2017.
 */

public class ExpandableWebViewCard extends VodafoneAbstractCard {

    @BindView(R.id.card_title_tv)
    VodafoneTextView titleTextView;

    @BindView(R.id.card_html_content)
    VodafoneTextView htmlTextView;

    @BindView(R.id.arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.card_webView)
    WebView webView;

    @BindView(R.id.spacer)
    View spacer;

    @BindView(R.id.loading_widget)
    LinearLayout loadingWidgetLayout;

    @BindView(R.id.expandable_card_header)
    RelativeLayout expandableCardHeader;

    CustomWidgetLoadingLayout customLoadingWidget;
    String htmlContent;
    boolean loadHtmlContent = false;
    private String title;
    private String webViewUrl;
    private boolean isLineHide = false;
    private boolean isHtmlInTextView = false;
    private boolean isImageArrowDirectionDown = false;
    private PagingScrollView pagingScrollView;
    private boolean isContentLoading = false;
    private Integer defaultFontDimension;

    public ExpandableWebViewCard(Context context) {
        super(context);
    }

    public ExpandableWebViewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableWebViewCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static void clickify(TextView view, final String clickableText,
                                final ClickSpan.OnClickListener listener) {

        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);

        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @OnClick(R.id.arrow)
    public void toggle(View view) {
        if (isHtmlInTextView) {
            if (isHtmlTextViewExpanded()) {
                unExpand();
            } else {
                expand();
            }
        } else {
            if (isExpanded()) {
                unExpand();
            } else {
                expand();
                webView.requestFocus();
            }
        }
    }

    public ExpandableWebViewCard build() {
        ButterKnife.bind(this);
        setStartPadding(0);
        titleTextView.setText(title);
        if (isHtmlInTextView) {
            /**
            *Serban Radulescu: va rog nu mai schimbati dimensiunea acestui titlu de aici
            *impacteaza in multe locuri ale aplicatiei
            *daca aveti nevoie de un font diferit pentru o anumita pagina va rog sa folositi setTextSize()
             */
            //titleTextView.setTextSize(20);
            htmlTextView.setText(TextUtils.fromHtml(htmlContent));
        }
        webView.setVisibility(GONE);
        if (isLineHide)
            spacer.setVisibility(GONE);

        if (isImageArrowDirectionDown)
            arrowIndicator.setRotation(90);

        if (defaultFontDimension != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setDefaultFontSize(defaultFontDimension);
        }

        return this;

    }

    @Override
    protected int setContent() {
        return R.layout.card_expandable_webview;
    }

    private void unExpand() {
        final RotateAnimation animRotateDown = new RotateAnimation(90.0f, 0.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);
        arrowIndicator.startAnimation(animRotateDown);
        hideDetails();
    }

    private void expand() {

        if (loadHtmlContent) {
//                webView.loadData(htmlContent, "text/html", "UTF-8");
            webView.setVisibility(GONE);
            isHtmlInTextView = true;
            htmlTextView.setVisibility(VISIBLE);
            htmlTextView.setText(TextUtils.fromHtml(htmlContent));
            htmlTextView.setLinkTextColor(Color.BLUE);
            htmlTextView.setLinksClickable(true);

            if(!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
                clickify(htmlTextView, "Blocheaza SIM", new ClickSpan.OnClickListener() {
                    @Override
                    public void onClick() {
                        new NavigationAction(getContext()).startAction(IntentActionName.SETTINGS_BLOCK_SIM, true);
                    }
                });
            }

            Linkify.addLinks(htmlTextView, Linkify.ALL);

        } else {

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(false);
            webView.setFocusable(true);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

//            webViewUrl = webViewUrl.replace("https","http");

            webView.loadUrl(webViewUrl);

//            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.d(TAG, "WEB VIEW ERROR");
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Log.d(TAG, "onPageStarted: ");
                    showLoadingWidget();

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "onPageFinished: ");
                    Observable.timer(300, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    hideLoadingWidget();
                                }
                            });

                }
            });
        }

        if (!isImageArrowDirectionDown) {
            final RotateAnimation animRotateUp = new RotateAnimation(0.0f, 90.0f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            animRotateUp.setFillAfter(true);
            animRotateUp.setDuration(120);
            arrowIndicator.startAnimation(animRotateUp);
        } else {

            final RotateAnimation animRotateUp = new RotateAnimation(90.0f, 180.0f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            animRotateUp.setFillAfter(true);
            animRotateUp.setDuration(120);
            arrowIndicator.startAnimation(animRotateUp);
        }
        showDetails();
    }

    private void hideDetails() {
        if (isHtmlInTextView) {
            htmlTextView.setVisibility(GONE);
        } else {
            webView.setVisibility(GONE);
        }


    }

    public ExpandableWebViewCard setPagingScrollView(PagingScrollView pagingScrollView) {
        this.pagingScrollView = pagingScrollView;
        return this;
    }

    private void showDetails() {
        if (isHtmlInTextView) {
            htmlTextView.setVisibility(VISIBLE);
            htmlTextView.getParent().requestChildFocus(htmlTextView, htmlTextView);
        } else {
            webView.setVisibility(VISIBLE);
        }

    }

    private boolean isExpanded() {
        return webView.getVisibility() == VISIBLE;
    }

    private boolean isHtmlTextViewExpanded() {
        return htmlTextView.getVisibility() == VISIBLE;
    }

    public String getTitle() {
        return title;
    }

    public ExpandableWebViewCard setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getWebViewUrl() {
        return webViewUrl;
    }

    public ExpandableWebViewCard setWebViewUrl(String webViewUrl) {
        this.webViewUrl = webViewUrl;
        return this;
    }

    public ExpandableWebViewCard setDefaultFontDimension(int defaultFontDimension) {
        this.defaultFontDimension = defaultFontDimension;
        return this;
    }

    public ExpandableWebViewCard setImageArrowDirectionDown() {
        this.isImageArrowDirectionDown = true;
        return this;
    }

    public ExpandableWebViewCard hideLine() {
        this.isLineHide = true;
        return this;
    }

    public ExpandableWebViewCard setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    public ExpandableWebViewCard setHtmlInTextView() {
        isHtmlInTextView = true;
        return this;
    }

    public ExpandableWebViewCard setLoadHtmlContent(boolean loadHtmlContent) {
        this.loadHtmlContent = loadHtmlContent;
        return this;
    }

    public void showLoadingWidget() {
        Log.d(TAG, "showLoading: ");
        webView.setVisibility(GONE);
        if (customLoadingWidget == null)
            customLoadingWidget = new CustomWidgetLoadingLayout(getContext()).build(loadingWidgetLayout,
                    Color.RED,
                    ViewGroupParamsEnum.relative_center);
        loadingWidgetLayout.setVisibility(VISIBLE);
        customLoadingWidget.show();
    }

    public void hideLoadingWidget() {
        Log.d(TAG, "hideLoading: ");
        webView.setVisibility(VISIBLE);
        if (customLoadingWidget != null && customLoadingWidget.getVisibility() == VISIBLE) {
            customLoadingWidget.hide();
            loadingWidgetLayout.removeView(customLoadingWidget);
            loadingWidgetLayout.setVisibility(GONE);
        }
    }

    public void setExpandableViewClickListener(){
        expandableCardHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHtmlInTextView) {
                    if (isHtmlTextViewExpanded()) {
                        unExpand();
                    } else {
                        expand();
                    }
                } else {
                    if (isExpanded()) {
                        unExpand();
                    } else {
                        expand();
                        webView.requestFocus();
                    }
                }
            }
        });
    }

    public void setHeaderPadding(int start, int top, int end, int bottom){
        expandableCardHeader.setPaddingRelative(ScreenMeasure.dpToPx(start), ScreenMeasure.dpToPx(top), ScreenMeasure.dpToPx(end), ScreenMeasure.dpToPx(bottom));
    }



    public static class ClickSpan extends ClickableSpan {

        private OnClickListener mListener;

        public ClickSpan(OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) mListener.onClick();
        }

        public interface OnClickListener {
            void onClick();
        }
    }


}
