package ro.vodafone.mcare.android.ui.views.datepickers;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 16.03.2017.
 */

public class WeeklyRechargesDayItem extends LinearLayout {

   private Context mContext;
   private VodafoneTextView textView;

    private LinearLayout dayBobble;
    private boolean isSelected = false;

    private String day;


    public WeeklyRechargesDayItem(Context context) {
        super(context);
        init(null);
    }

    public WeeklyRechargesDayItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public WeeklyRechargesDayItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeeklyRechargesDayItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        inflate(getContext(), R.layout.day_picker_booble_layout, this);
        textView = (VodafoneTextView) findViewById(R.id.day);
        dayBobble = (LinearLayout) findViewById(R.id.day_booble);
    }


    public void onSelect(){
        dayBobble.setBackgroundResource(R.drawable.red_circle);
        textView.setTextColor(getResources().getColor(R.color.white_text_color));
        isSelected = true;
    }

    public void unselect(){
        dayBobble.setBackgroundResource(R.drawable.white_circle);
        textView.setTextColor(getResources().getColor(R.color.dark_gray_text_color));
        isSelected = false;
    }

    public void setDay(String day){
        this.day = day;
        textView.setText(day);
    }

    public String getSelectedDay(){
        return day;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }
}
