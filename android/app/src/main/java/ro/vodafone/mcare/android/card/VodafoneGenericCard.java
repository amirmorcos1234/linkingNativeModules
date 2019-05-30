package ro.vodafone.mcare.android.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Bivol Pavel on 30.03.2017.
 *
 * Use this class in layouts
 */

public class VodafoneGenericCard extends VodafoneAbstractCard {
    public VodafoneGenericCard(Context context) {
        super(context);
    }

    public VodafoneGenericCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VodafoneGenericCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int setContent() {
        return 0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        for (int i = 1; i< getChildCount(); i++){
            View child = getChildAt(i);
            removeView(child);
            addViewToBottom(child);
        }
    }

}
