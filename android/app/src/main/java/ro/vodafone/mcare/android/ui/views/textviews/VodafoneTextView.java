package ro.vodafone.mcare.android.ui.views.textviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.Fonts;


/**
 * Created by Victor Radulescu on 12/15/2016.
 */

public class VodafoneTextView extends android.support.v7.widget.AppCompatTextView {

    public VodafoneTextView(Context context) {
        super(context);
        setAttributes(context,null);
    }

    public VodafoneTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context,attrs);
    }

    public VodafoneTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     * Set Vodafone font
     */
    public void setFont(int textStyle){

        if (isInEditMode())
            return;

        switch (TextStyle.fromId(textStyle)) {
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
    }    /**
     * Set Vodafone font
     */
    public void setFont(TextStyle textStyle){
        if (isInEditMode())
            return;

        switch (textStyle) {
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

    public enum TextStyle {
        VODAFONE_RG(0), VODAFONE_LT(1),CANARO_EXTRA_BOLD(2),VODAFONE_RGBD(3);
        private int id;

        TextStyle(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static TextStyle fromId(int id) {
            for (TextStyle ts : values()) {
                if (ts.id == id) return ts;
            }
            throw new IllegalArgumentException();
        }
    }

}
