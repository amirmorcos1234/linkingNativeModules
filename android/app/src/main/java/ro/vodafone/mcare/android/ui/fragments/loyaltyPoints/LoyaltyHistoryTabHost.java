package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;

/**
 * Created by User on 08.05.2017.
 */

public class LoyaltyHistoryTabHost extends FragmentTabHost {
    public LoyaltyHistoryTabHost(Context context) {
        super(context);
        init();
    }

    public LoyaltyHistoryTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.loyalty_history_tab_host, this);
    }

}
