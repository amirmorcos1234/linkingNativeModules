package ro.vodafone.mcare.android.widget.animators.interpolators;

import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by Victor Radulescu on 11/7/2017.
 */

public class AccelerateInterpolator implements Interpolator {
    private final float mFactor;
    private final double mDoubleFactor;

    public AccelerateInterpolator() {
        mFactor = 1.0f;
        mDoubleFactor = 2.0;
    }
    public AccelerateInterpolator(float factor) {
        mFactor = factor;
        mDoubleFactor = 2 * mFactor;
    }
    @Override
    public float getInterpolation(float input) {
        TranslateAnimation animation = new TranslateAnimation(0,1,0,1);

        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float)Math.pow(input, mDoubleFactor);
        }
    }

}
