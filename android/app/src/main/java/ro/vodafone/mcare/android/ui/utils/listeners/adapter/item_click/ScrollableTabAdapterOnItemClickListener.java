package ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.AdapterView;

import java.lang.ref.WeakReference;

import ro.vodafone.mcare.android.widget.TabMenu.ScrollableTabCard;

/**
 * Created by Bogdan Marica on 9/4/2017.
 */

public class ScrollableTabAdapterOnItemClickListener implements AdapterView.OnItemClickListener {

    WeakReference<ScrollableTabCard> tabCardWeakReference;

    public ScrollableTabAdapterOnItemClickListener(ScrollableTabCard tabCardWeakReference) {
        this.tabCardWeakReference = new WeakReference<>(tabCardWeakReference);
    }

    @CallSuper
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (tabCardWeakReference == null || tabCardWeakReference.get() == null) {
            return;

        }
        tabCardWeakReference.get().unselectAll();
        tabCardWeakReference.get().setHighlighted(position, true);
    }
}