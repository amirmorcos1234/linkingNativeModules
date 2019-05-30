package ro.vodafone.mcare.android.card.store;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;

/**
 * Created by Bogdan Marica on 7/14/2017.
 */

public class StoreLocatorLocationCard extends VodafoneAbstractCard {
    public StoreLocatorLocationCard(Context context) {
        super(context);
    }

    public StoreLocatorLocationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoreLocatorLocationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int setContent() {
        return R.layout.fragment_store_locator_location_card;
    }


    public void showBasicErrorCard(boolean hideContent, String errorText, Drawable errorDrawable) {

        hideLoading();
        if (hideContent)
            hideContent();
        if (errorView == null) {
            errorView = new CardErrorLayout(getContext(), errorText, errorDrawable);

            RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int defaultMargin = ScreenMeasure.dpToPx(10);
            lp.setMargins(defaultMargin, defaultMargin, defaultMargin, 0);
            ((RelativeLayout) errorView.findViewById(R.id.card_error_container)).setLayoutParams(lp);

            errorView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
            getContentLayout().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
            getCardView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));


            getContentLayout().addView(errorView);
        } else {
            errorView.setVisibility(VISIBLE);
        }
    }

    public void showNewBasicErrorCard(boolean hideContent, String errorText, Drawable errorDrawable) {

        hideLoading();
        if (hideContent)
            hideContent();
//     errorView=null;
        errorView = new CardErrorLayout(getContext(), errorText, errorDrawable);

        RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int defaultMargin = ScreenMeasure.dpToPx(10);
        lp.setMargins(defaultMargin, defaultMargin, defaultMargin, 0);
        ((RelativeLayout) errorView.findViewById(R.id.card_error_container)).setLayoutParams(lp);

        errorView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        getContentLayout().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        getCardView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.card_background_gray));
        errorView.setVisibility(VISIBLE);

        getContentLayout().removeAllViews();
        getContentLayout().addView(errorView);
    }

    public void setCurrentAdress(String adress) {
//        if (getCardView() == null)
            reinit();
        setCardMargins(0, 0, 0, 0);
        if (errorView != null)
            errorView.setVisibility(GONE);

        ((TextView) this.findViewById(R.id.location_label)).setText("Locația ta");
        ((TextView) this.findViewById(R.id.actual_location)).setText(adress);
    }

}
