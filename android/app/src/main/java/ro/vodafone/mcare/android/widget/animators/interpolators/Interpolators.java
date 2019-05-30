package ro.vodafone.mcare.android.widget.animators.interpolators;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

/**
 * Created by sekl on 23.11.16.
 */

public class Interpolators {
    // Sine
    public static final Interpolator IN_SINE = PathInterpolatorCompat.create(0.47f, 0.0f, 0.745f, 0.715f);
    public static final Interpolator OUT_SINE = PathInterpolatorCompat.create(0.39f, 0.575f, 0.565f, 1.0f);
    public static final Interpolator IN_OUT_SINE = PathInterpolatorCompat.create(0.445f, 0.05f, 0.55f, 0.95f);
    // Quad
    public static final Interpolator IN_QUAD = PathInterpolatorCompat.create(0.55f, 0.085f, 0.68f, 0.53f);
    public static final Interpolator OUT_QUAD = PathInterpolatorCompat.create(0.25f, 0.46f, 0.45f, 0.94f);
    public static final Interpolator IN_OUT_QUAD = PathInterpolatorCompat.create(0.455f, 0.03f, 0.515f, 0.955f);
    // Cubic
    public static final Interpolator IN_CUBIC = PathInterpolatorCompat.create(0.55f, 0.055f, 0.675f, 0.19f);
    public static final Interpolator OUT_CUBIC = PathInterpolatorCompat.create(0.215f, 0.61f, 0.355f, 1.0f);
    public static final Interpolator IN_OUT_CUBIC = PathInterpolatorCompat.create(0.645f, 0.045f, 0.355f, 1.0f);
    // Quart
    public static final Interpolator IN_QUART = PathInterpolatorCompat.create(0.895f, 0.03f, 0.685f, 0.22f);
    public static final Interpolator OUT_QUART = PathInterpolatorCompat.create(0.165f, 0.84f, 0.44f, 1.0f);
    public static final Interpolator IN_OUT_QUART = PathInterpolatorCompat.create(0.77f, 0.0f, 0.175f, 1.0f);
    // Quint
    public static final Interpolator IN_QUINT = PathInterpolatorCompat.create(0.755f, 0.05f, 0.855f, 0.06f);
    public static final Interpolator OUT_QUINT = PathInterpolatorCompat.create(0.23f, 1.0f, 0.32f, 1.0f);
    public static final Interpolator IN_OUT_QUINT = PathInterpolatorCompat.create(0.86f, 0.0f, 0.07f, 1.0f);
    // Expo
    public static final Interpolator IN_EXPO = PathInterpolatorCompat.create(0.95f, 0.05f, 0.795f, 0.035f);
    public static final Interpolator OUT_EXPO = PathInterpolatorCompat.create(0.19f, 1.0f, 0.22f, 1.0f);
    public static final Interpolator IN_OUT_EXPO = PathInterpolatorCompat.create(1.0f, 0.0f, 0.0f, 1.0f);
    // Circ
    public static final Interpolator IN_CIRC = PathInterpolatorCompat.create(0.6f, 0.04f, 0.98f, 0.335f);
    public static final Interpolator OUT_CIRC = PathInterpolatorCompat.create(0.075f, 0.82f, 0.165f, 1.0f);
    public static final Interpolator IN_OUT_CIRC = PathInterpolatorCompat.create(0.785f, 0.135f, 0.15f, 0.86f);
    // Back
    public static final Interpolator IN_BACK = PathInterpolatorCompat.create(0.6f, -0.28f, 0.735f, 0.045f);
    public static final Interpolator OUT_BACK = PathInterpolatorCompat.create(0.175f, 0.885f, 0.32f, 1.275f);
    public static final Interpolator IN_OUT_BACK = PathInterpolatorCompat.create(0.68f, -0.55f, 0.265f, 1.55f);
    //Elastic
    public static final Interpolator EASE_OUT_ELASTIC = new Interpolator() {
        @Override
        public float getInterpolation(float input) {
            return (float)Math.pow(2, -10 * input) * (float)Math.sin((input - K) * J) + 1;
        }

        @Override public String toString () { return "EASE_OUT_ELASTIC"; }

        private static final float K = 0.3f/4;
        private static final float J = (float)(2*Math.PI/0.3);
    };
}
