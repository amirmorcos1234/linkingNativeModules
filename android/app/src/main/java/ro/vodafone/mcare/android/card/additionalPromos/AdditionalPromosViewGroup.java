package ro.vodafone.mcare.android.card.additionalPromos;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by user on 18.10.2017.
 */

public class AdditionalPromosViewGroup extends LinearLayout {

    Context mContext;

    private AdditionalPromosCardController additionalPromosCardController;

    public AdditionalPromosViewGroup(Context context, AdditionalPromosCardController additionalPromosCardController) {
        super(context);
        mContext = context;
        this.additionalPromosCardController = additionalPromosCardController;
        init(null);
    }

    public AdditionalPromosViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setOrientation(VERTICAL);
    }

    public void displayLoadingCard() {
        removeAllViews();
        //addView(new ActiveServicesCard(mContext).showLoading(true));
    }

    public void atachCards(List<AdditionalPromosCard> cardList) {
        //removeAllViews();
        if (cardList.size() > 0)
            for (AdditionalPromosCard card : cardList) {
                this.addView(card);
            }
    }
}
