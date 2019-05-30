package ro.vodafone.mcare.android.ui.views.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/23/2017.
 */

public class VodafoneButton extends AppCompatButton {

    public VodafoneButton(Context context) {
        super(context);
        setAttributes(context,null);

    }

    public VodafoneButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context,attrs);

    }

    public VodafoneButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context,attrs);

    }


    private void setAttributes(Context context,AttributeSet attributes){
        TypedArray ta = attributes != null? context.obtainStyledAttributes(attributes, R.styleable.VodafoneText)
                : context.obtainStyledAttributes(R.styleable.VodafoneText);
        try {
            setFont(ta.getInt(R.styleable.VodafoneText_vodafoneTextStyle, 0));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ta.recycle();
        }
    }
    private void setFont(int textStyle){

        switch (VodafoneTextView.TextStyle.fromId(textStyle)) {
            case VODAFONE_RG:
                setTypeface(Fonts.getVodafoneRG());
                break;

            case VODAFONE_LT:
                setTypeface(Fonts.getVodafoneLT());
                break;

            case CANARO_EXTRA_BOLD:
                setTypeface(Fonts.getCanaroExtraBold());
                break;

            case VODAFONE_RGBD:
                setTypeface(Fonts.getVodafoneRGBD());
                break;

        }
    }



}
