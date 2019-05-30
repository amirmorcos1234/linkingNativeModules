package ro.vodafone.mcare.android.card.billSummary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

public class BillSummaryDataViewGroup extends LinearLayout {

    Context mContext;

    public BillSummaryDataViewGroup(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public BillSummaryDataViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        this.setOrientation(VERTICAL);
        setupController();
    }

    private void setupController(){
        BillSummaryDataController.getInstance().setup(this);
    }

    public void atachCards(List<View> cardList){
        removeAllViews();
        for(View card : cardList){
            Log.d("BSummaryDataViewGroup", "addView()");
            this.addView(card);
        }
    }
}
