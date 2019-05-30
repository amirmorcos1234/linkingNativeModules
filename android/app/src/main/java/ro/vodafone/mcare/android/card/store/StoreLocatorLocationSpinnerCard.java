package ro.vodafone.mcare.android.card.store;

import android.content.Context;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;

/**
 * Created by Bogdan Marica on 7/25/2017.
 */

public class StoreLocatorLocationSpinnerCard extends VodafoneAbstractCard {
    public StoreLocatorLocationSpinnerCard(Context context) {
        super(context);
        init();
    }

    public StoreLocatorLocationSpinnerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoreLocatorLocationSpinnerCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected int setContent() {
         return R.layout.fragment_store_locator_spinner_card;
    }

    void init(){
    }

   public void setOnErrorClickListener(OnClickListener onClickListener){
       errorView.setOnClickListener(onClickListener);
    }

}
