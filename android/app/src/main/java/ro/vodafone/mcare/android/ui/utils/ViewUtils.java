package ro.vodafone.mcare.android.ui.utils;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.transition.Transition;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicInteger;

import ro.vodafone.mcare.android.application.VodafoneController;

/**
 * Created by user2 on 6/20/2017.
 */

public class ViewUtils {
    public static void postOnPreDraw(final View view, final Runnable runnable) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (runnable != null)
                    runnable.run();
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    /**
     * @param windowManager
     * @param window
     * @return window padding. x is top, y is bottom
     */
    public static Point getWindowPadding(WindowManager windowManager, Window window) {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        final Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);

        return new Point(rect.top, size.y - rect.bottom);
    }

    public static int getScreenHeight() {
        Display display = VodafoneController.currentActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    public static int getWindowHeight() {
        Display display = VodafoneController.currentActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final Point windowPadding = getWindowPadding(VodafoneController.currentActivity().getWindowManager(), VodafoneController.currentActivity().getWindow());
        return ((size.y - windowPadding.x) - windowPadding.y);
    }

public static int getScreenWidth() {
        Display display = VodafoneController.currentActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    //this is a work in progress to avoid having a work-around for moving large views with background
    //in and out of the screen. it's left disabled since it doesn't work yet and it can cause side effects
    @Deprecated
    public static void changeVisibilityForTransitions(final View view, final Window window) {
        final boolean disable = true;
        if (disable)
            return;
        if (Build.VERSION.SDK_INT < 21)
            return;
        final Transition.TransitionListener listener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(ViewUtils.class.getSimpleName(), "On transition start, setting visibility");
                view.setVisibility(View.GONE);
            }

            @Override
            @TargetApi(21)
            public void onTransitionEnd(Transition transition) {
                Log.d(ViewUtils.class.getSimpleName(), "On transition end, setting visibility");
                view.setVisibility(View.VISIBLE);
                window.getEnterTransition().removeListener(this);
                window.getExitTransition().removeListener(this);
                window.getReenterTransition().removeListener(this);
                window.getReturnTransition().removeListener(this);
                window.getSharedElementEnterTransition().removeListener(this);
                window.getSharedElementExitTransition().removeListener(this);
                window.getSharedElementReenterTransition().removeListener(this);
                window.getSharedElementReturnTransition().removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        if (window.getEnterTransition() != null) window.getEnterTransition().addListener(listener);
        if (window.getExitTransition() != null) window.getExitTransition().addListener(listener);
        if (window.getReenterTransition() != null)
            window.getReenterTransition().addListener(listener);
        if (window.getReenterTransition() != null)
            window.getReturnTransition().addListener(listener);
        if (window.getSharedElementEnterTransition() != null)
            window.getSharedElementEnterTransition().addListener(listener);
        if (window.getSharedElementExitTransition() != null)
            window.getSharedElementExitTransition().addListener(listener);
        if (window.getSharedElementReenterTransition() != null)
            window.getSharedElementReenterTransition().addListener(listener);
        if (window.getSharedElementReturnTransition() != null)
            window.getSharedElementReturnTransition().addListener(listener);

        if (!(view instanceof ViewGroup))
            return;
        final ViewGroup viewGroup = (ViewGroup) view;
        final LayoutTransition transition = new LayoutTransition();
        transition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if ((container == viewGroup) || (view == viewGroup)) {
                    Log.d(ViewUtils.class.getSimpleName(), "On layout transition start, setting visibility");
                    viewGroup.setVisibility(View.GONE);
                }
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if ((container == viewGroup) || (view == viewGroup)) {
                    Log.d(ViewUtils.class.getSimpleName(), "On layout transition end, setting visibility");
                    viewGroup.setVisibility(View.VISIBLE);
                    transition.removeTransitionListener(this);
                }

            }
        });
        viewGroup.setLayoutTransition(transition);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
