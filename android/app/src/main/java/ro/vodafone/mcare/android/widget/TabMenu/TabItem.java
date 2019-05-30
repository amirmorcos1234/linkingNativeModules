package ro.vodafone.mcare.android.widget.TabMenu;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;

/**
 * Created by Bogdan Marica on 7/12/2017.
 */

public class TabItem extends LinearLayout {

    private boolean isLoyaltyLayout;

    public TabItem(Context context) {
        super(context);
        init();
    }

    public TabItem(Context context, boolean isLoyaltyLayout) {
        super(context);
        init();
        this.isLoyaltyLayout = isLoyaltyLayout;
    }

    public TabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        if(isLoyaltyLayout) {
            inflate(getContext(), R.layout.tab_content, this);
        } else {
            inflate(getContext(), R.layout.tab_content_loyalty, this);
        }
    }

    void setTabName(String tabName) {
        ((TextView) findViewById(R.id.tab_namezz)).setText(tabName);
    }

    public void setHighlighted(boolean isHighlighted) {
        if (isHighlighted) {

            ((TextView)findViewById(R.id.tab_namezz)).setTextColor(ContextCompat.getColor(getContext(),R.color.vodafoneRed));
            (findViewById(R.id.arrow_container)).setVisibility(VISIBLE);
            (findViewById(R.id.arrow_view)).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.vodafoneRed));

        } else {

            ((TextView)findViewById(R.id.tab_namezz)).setTextColor(ContextCompat.getColor(getContext(),R.color.grey_600));
            (findViewById(R.id.arrow_container)).setVisibility(INVISIBLE);
            (findViewById(R.id.arrow_view)).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.general_background_light_gray));

        }
    }
}
