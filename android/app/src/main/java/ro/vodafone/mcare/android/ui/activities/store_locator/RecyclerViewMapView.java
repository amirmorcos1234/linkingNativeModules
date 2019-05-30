package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Created by Bogdan Marica on 7/27/2017.
 */

public class RecyclerViewMapView extends MapView {
    public RecyclerViewMapView(Context context) {
        super(context);
    }

    public RecyclerViewMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RecyclerViewMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }


    public RecyclerViewMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle MapView's touch events.
        return super.onTouchEvent(ev);

    }

}
