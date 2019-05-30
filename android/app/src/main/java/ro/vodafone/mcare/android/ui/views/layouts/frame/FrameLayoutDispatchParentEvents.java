package ro.vodafone.mcare.android.ui.views.layouts.frame;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 **Implementation to disable parent touch events when moving on this view
 */
public class FrameLayoutDispatchParentEvents extends FrameLayout {

        public FrameLayoutDispatchParentEvents(@NonNull Context context) {
            super(context);
        }

        public FrameLayoutDispatchParentEvents(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public FrameLayoutDispatchParentEvents(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
        @Override
        public boolean dispatchTouchEvent(final MotionEvent motionEvent) {

            switch (motionEvent.getActionMasked()) {
                // Pressed on map: stop listview from scrolling
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                // Released on map or cancelled: listview can be normal again
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Process event as normal. If listview was disallowed touch events, the map will process the event.
            return super.dispatchTouchEvent(motionEvent);
        }
    }