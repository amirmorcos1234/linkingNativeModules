package ro.vodafone.mcare.android.card;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public abstract class VodafoneAbstractCard extends RelativeLayout {

    public static String TAG = VodafoneAbstractCard.class.getSimpleName();
    protected CustomWidgetLoadingLayout loadingView;
    protected CardErrorLayout errorView;
    private LayoutParams mCardViewParams;
    private CardView cardView;
    private View cardContent;
    private LinearLayout contentLayout;

    public VodafoneAbstractCard(Context context) {
        super(context);
        init(null);
    }

    public VodafoneAbstractCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VodafoneAbstractCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initCardView();
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        addContent();
    }

    public void reinit() {
        this.removeAllViews();

        if (cardView != null)
            cardView.removeAllViews();

        init(null);

        /*initCardView();

        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        getContentLayout().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        getCardView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));

        addContent();*/
    }

    protected abstract int setContent();

    private void initCardView() {

        LayoutInflater infalInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cardView = (CardView) infalInflater.inflate(R.layout.card_view, this, false);


        cardView.setCardElevation(0);
        mCardViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mCardViewParams.setMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
        cardView.setLayoutParams(mCardViewParams);
        cardView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_shadow, null));

        addView(cardView);
    }

    public void setStartPadding(int dpStartPadding) {
        contentLayout.setPaddingRelative(ScreenMeasure.dpToPx(dpStartPadding), getPaddingTop(), getPaddingEnd(), getPaddingBottom());
    }

    public void setCardPaddingsInDp(int dpStartPadding, int dpTopPadding, int dpEndPadding, int dpBottomPadding) {
        contentLayout.setPaddingRelative(ScreenMeasure.dpToPx(dpStartPadding),
                ScreenMeasure.dpToPx(dpTopPadding),
                ScreenMeasure.dpToPx(dpEndPadding)
                , ScreenMeasure.dpToPx(dpBottomPadding));
    }

    public void setCardPaddingsInPx(int dpStartPadding, int dpTopPadding, int dpEndPadding, int dpBottomPadding) {
        contentLayout.setPaddingRelative(dpStartPadding,
                dpTopPadding,
                dpEndPadding
                , dpBottomPadding);
    }

    private void addContent() {
        int resId = setContent();
        if (resId != 0) {
            cardContent = inflate(getContext(), resId, contentLayout);
            contentLayout = (LinearLayout) findViewById(R.id.content_layout);
//            contentLayout.addView(cardContent);
        }
    }

    protected void addViewsToBottom(List<View> viewsList) {
        int currentIndex = ((ViewGroup) this.getParent()).indexOfChild(this);

        if (viewsList != null && viewsList.size() != 0) {
            for (View view : viewsList) {
                currentIndex++;
                ((ViewGroup) this.getParent()).addView(view, currentIndex);
            }
        }
    }

    public void addViewToBottom(View view) {
        contentLayout.addView(view);
    }

    public VodafoneAbstractCard showError(boolean hideContent) {
        hideLoading();
        if (hideContent)
            hideContent();
        if (errorView == null) {
            errorView = new CardErrorLayout(getContext());
            contentLayout.addView(errorView);
        } else {
            errorView.setVisibility(VISIBLE);
        }
        return this;
    }

    public VodafoneAbstractCard showError(boolean hideContent, String errorText) {
        hideLoading();
        if (hideContent)
            hideContent();
        if (errorView == null) {
            errorView = new CardErrorLayout(getContext(), errorText);
            contentLayout.addView(errorView);
        } else {
            errorView.setVisibility(VISIBLE);
        }
        return this;

    }

    public VodafoneAbstractCard showError(boolean hideContent, String errorText, Drawable errorDrawable) {
        hideLoading();
        if (hideContent)
            hideContent();
        if (errorView == null) {
            errorView = new CardErrorLayout(getContext(), errorText, errorDrawable);
            contentLayout.addView(errorView);
        } else {
            errorView.setVisibility(VISIBLE);
        }
        return this;

    }

    public VodafoneAbstractCard showLoading(boolean hideContent) {
        hideError();
        if (hideContent)
            hideContent();
        if (loadingView == null) {
            loadingView = new CustomWidgetLoadingLayout(getContext()).build(
                    contentLayout,
                    Color.RED,
                    ViewGroupParamsEnum.card_params);
        }
        loadingView.show();
        return this;
    }

    public void hideError() {
        if (errorView != null && errorView.getVisibility() == VISIBLE) {
            showContent();
            contentLayout.removeView(errorView);
            errorView = null;
        }
    }

    public void hideLoading() {
        if (loadingView != null && loadingView.isVisible()) {
            showContent();
            //hideError();
            loadingView.removeFromParrent();
            loadingView = null;
        }
    }

    private View getSeparatorLine() {

        View separatorLine = new View(getContext());
        ViewGroup.LayoutParams separatorLineParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(1));

        separatorLine.setLayoutParams(separatorLineParams);
        separatorLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pay_bill_separator_line_color));

        return separatorLine;
    }

    public void addButton(View button) {
        if (cardContent != null) {
            contentLayout.addView(getSeparatorLine(), contentLayout.getChildCount());
        }
        contentLayout.addView(button, contentLayout.getChildCount());
    }

    public void addHeader(View header) {
        contentLayout.addView(header, 0);
        contentLayout.addView(getSeparatorLine(), 1);
    }

    public void setCardViewBackground(@ColorRes int color) {
        findViewById(R.id.card_view).setBackgroundResource(color);
    }

    public VodafoneAbstractCard addCardTitle(String text, @ColorRes int color, @Nullable Integer textSize) {
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        VodafoneTextView vodafoneTextView = new VodafoneTextView(getContext());
        vodafoneTextView.setText(text);
        if (textSize != null) {
            vodafoneTextView.setTextSize(textSize);
        } else {
            vodafoneTextView.setTextSize(20);
        }

        vodafoneTextView.setId(R.id.titleTextView);
        vodafoneTextView.setTextColor(ContextCompat.getColor(getContext(), color));
        //vodafoneTextView.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
        vodafoneTextView.setGravity(Gravity.CENTER);
        titleLayoutParams.addRule(ALIGN_PARENT_TOP, TRUE);
        int margins = ScreenMeasure.dpToPx(15);
        titleLayoutParams.setMargins(0, margins, 0, margins);
        addView(vodafoneTextView, titleLayoutParams);

        LayoutParams cardLayoutParams = (LayoutParams) cardView.getLayoutParams();
        cardLayoutParams.addRule(BELOW, R.id.titleTextView);
        return this;
    }

    public void hideContent() {
        Log.d(TAG, "hideContent + count :" + contentLayout.getChildCount());
        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            if (contentLayout.getChildAt(i).getVisibility() != View.GONE) {
                contentLayout.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public void showContent() {
        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            if (contentLayout.getChildAt(i).getVisibility() != View.VISIBLE) {
                View child = contentLayout.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        }
    }

    public VodafoneAbstractCard setCardMargins(int left, int top, int right, int bottom) {

        mCardViewParams = (LayoutParams) cardView.getLayoutParams();

        mCardViewParams.setMargins(left, top, right, bottom);
        cardView.setLayoutParams(mCardViewParams);
        return this;
    }

    public VodafoneAbstractCard setCardMarginsInDp(int left, int top, int right, int bottom) {

        mCardViewParams = (LayoutParams) cardView.getLayoutParams();

        mCardViewParams.setMargins(ScreenMeasure.dpToPx(left), ScreenMeasure.dpToPx(top), ScreenMeasure.dpToPx(right), ScreenMeasure.dpToPx(bottom));
        cardView.setLayoutParams(mCardViewParams);
        return this;
    }

    public void clearContent() {
        contentLayout.removeAllViews();
    }

    protected View getChildAtIndex(int index) {
        return contentLayout.getChildAt(index);
    }

    public CardView getCardView() {
        return cardView;
    }

    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    public CardErrorLayout getErrorView() {
        return errorView;
    }
}
