package ro.vodafone.mcare.android.deprecated;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by user1 on 16.08.2016.
 */
public class CustomVodafoneLtTextView extends android.support.v7.widget.AppCompatTextView {


    public CustomVodafoneLtTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface =  Typeface.createFromAsset(context.getAssets(), "fonts/vodafonergbd.ttf");
        this.setTypeface(typeface);
    }
}
