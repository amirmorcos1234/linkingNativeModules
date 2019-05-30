package ro.vodafone.mcare.android.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import ro.vodafone.mcare.android.R;

/**
 * Created by user on 08.02.2017.
 */
public class DrawableUtils {

    public static void applyThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE,
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    public static Drawable generateCircleIcon(int backgroundColor, int iconColor, Drawable icon, Context context){

        LayerDrawable ld = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.button_icon);
        GradientDrawable gradientDrawable = (GradientDrawable) ld.findDrawableByLayerId(R.id.item_color);

        icon.mutate();
        icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);

        ld.setDrawableByLayerId(R.id.first_image, icon);

        gradientDrawable.mutate();
        gradientDrawable.setColor(backgroundColor);

        return ld;
    }

    private static final int MAX_LEVEL = 180;

    public static void rotateArrow(ImageView arrow, boolean shouldRotateUp) {

        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;

        RotateAnimation rotate = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearOutSlowInInterpolator());

        arrow.startAnimation(rotate);
    }
    public static void setContextDrawableColor(Context context,Drawable drawable,@ColorRes int colorRes){
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorRes));
            } else {
                drawable.setColorFilter(new
                        PorterDuffColorFilter(ContextCompat.getColor(context,colorRes), PorterDuff.Mode.SRC_IN));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
