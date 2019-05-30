package ro.vodafone.mcare.android.card.billHistory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import ro.vodafone.mcare.android.R;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

public class BillHistoryViewGroup extends LinearLayout {

    Context mContext;

    public BillHistoryViewGroup(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public BillHistoryViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        this.setOrientation(VERTICAL);
        setupController();
    }

    private void setupController(){
        BillHistoryController.getInstance().setup(this).requestData();
    }

    public void displayErrorCard(){
        removeAllViews();
        addView(new BillHistoryCard(mContext).showError(null));
    }

    public void displayLoadingCard(){
        removeAllViews();
        addView(new BillHistoryCard(mContext).showLoading());
    }

    public void atachCards(List<BillHistoryCard> cardList){
        removeAllViews();
        if(cardList.size() != 0){
            for(BillHistoryCard card : cardList){
                this.addView(card);
            }
        }else{
            ((View)getParent()).findViewById(R.id.previvios_bills_card_title).setVisibility(GONE);
        }
    }

    public void hideCard(){
        ((View)getParent()).findViewById(R.id.previvios_bills_card_title).setVisibility(GONE);
        removeAllViews();
    }
}
