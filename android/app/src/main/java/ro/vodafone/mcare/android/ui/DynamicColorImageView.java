package ro.vodafone.mcare.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;

/**
 * Created by bogdan marica on 3/16/2017.
 *
 * Class created in case you want to change the color of a drowable in a ImageView
 * Needed for instance in Enroll in YOU
 *
 * If same drawable is used in more places the color might be changed there aswell
 */

public class DynamicColorImageView extends android.support.v7.widget.AppCompatImageView {

    int colorRes = -1;


    public DynamicColorImageView(Context context) {
        super(context);
        init();
    }

    public DynamicColorImageView(Context context, @ColorRes int colorRes) {
        super(context);
        this.colorRes = colorRes;

        init();
    }

    public DynamicColorImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public DynamicColorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init() {
        if (colorRes != -1)
            setDrawableColor(colorRes);

    }
    private void init(AttributeSet attributeSet) {
        setAttributes(attributeSet);
        if (colorRes != -1)
            setDrawableColor(colorRes);

    }

    private void setAttributes(AttributeSet attrs){
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                R.styleable.DynamicColorImageView);
        try {
            colorRes = attributes.getResourceId(
                    R.styleable.DynamicColorImageView_drawableColor,
                    ContextCompat.getColor(getContext(), R.color.white));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            attributes.recycle();
        }
    }

    public void setDrawableColor(@ColorRes int colorRes) {
        setContextDrawableColor(this.getDrawable(), colorRes);
    }

    public void setBackgroundColorWithRes(@ColorRes int colorRes){
        setContextDrawableColor(this.getBackground(),colorRes);
    }

    public void setContextDrawableColor(Drawable drawable,@ColorRes int colorRes){
        try {
            if(drawable != null) {
                Drawable dr = drawable.mutate();
                if (Build.VERSION.SDK_INT >= 21) {
                    DrawableCompat.setTint(dr, ContextCompat.getColor(getContext(), colorRes));
                } else {
                    dr.setColorFilter(new
                            PorterDuffColorFilter(ContextCompat.getColor(getContext(), colorRes), PorterDuff.Mode.SRC_IN));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
