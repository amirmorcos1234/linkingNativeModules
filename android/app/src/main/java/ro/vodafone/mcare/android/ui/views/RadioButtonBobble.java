package ro.vodafone.mcare.android.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 01.03.2017.
 */
public class RadioButtonBobble extends LinearLayout{

    private Context mContext;

    private VodafoneTextView textView;

    private LinearLayout bobble;

    private int amount;
    private int validity;

    private boolean isSelected = false;
    private boolean haveAmount = false;

    public RadioButtonBobble(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public RadioButtonBobble(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public RadioButtonBobble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadioButtonBobble(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs){
        inflate(mContext, R.layout.bobble_radio_button, this);
        textView = (VodafoneTextView) findViewById(R.id.amount);
        bobble = (LinearLayout) findViewById(R.id.booble);

    }

    public void setLabel(String label){
        textView.setText(label);
    }

    public void onSelect(){
        bobble.setBackgroundResource(R.drawable.red_circle);
        textView.setTextColor(getResources().getColor(R.color.white_text_color));
        isSelected = true;
        bobble.requestFocus();
    }

    public void unselect(){
        bobble.setBackgroundResource(R.drawable.gray_circle);
        textView.setTextColor(getResources().getColor(R.color.dark_gray_text_color));
        isSelected = false;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        this.haveAmount = true;
        textView.setText(String.valueOf(amount) + "€");
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public boolean haveAmount() {
        return haveAmount;
    }

    public void setHaveAmount(boolean haveAmount) {
        this.haveAmount = haveAmount;
    }
}
