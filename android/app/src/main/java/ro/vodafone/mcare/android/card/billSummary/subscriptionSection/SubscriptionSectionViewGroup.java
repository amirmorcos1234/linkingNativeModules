package ro.vodafone.mcare.android.card.billSummary.subscriptionSection;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

public class SubscriptionSectionViewGroup extends LinearLayout {

    Context mContext;

    public SubscriptionSectionViewGroup(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public SubscriptionSectionViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        this.setOrientation(VERTICAL);
        setupController();
    }

    private void setupController(){
       // SubscriptionSectionController.getInstance().setup(this);
    }

    public void displayErrorCard(){
        removeAllViews();
        addView(new SubscriptionSectionCard(mContext).showError(true));
    }

    public void displayLoadingCard(){
        removeAllViews();
        addView(new SubscriptionSectionCard(mContext).showLoading(true));
    }

    public void atachCards(List<SubscriptionSectionCard> cardList){
        Log.d("SubscriptionSection", "cardList size -" +cardList.size());
        removeAllViews();
        for(SubscriptionSectionCard card : cardList){
            this.addView(card);
        }
    }
}
