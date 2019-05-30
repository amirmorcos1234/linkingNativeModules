package ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.AdapterView;

import java.lang.ref.WeakReference;

import ro.vodafone.mcare.android.widget.TabMenu.TabCard;


public class TabAdapterOnItemClickListener implements AdapterView.OnItemClickListener {

    WeakReference<TabCard> tabCardWeakReference;

    public TabAdapterOnItemClickListener(TabCard tabCardWeakReference) {
        this.tabCardWeakReference = new WeakReference<>(tabCardWeakReference);
    }

    @CallSuper
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(tabCardWeakReference==null || tabCardWeakReference.get()==null){
            return;

        }
        tabCardWeakReference.get().unselectAll();
        tabCardWeakReference.get().setHighlighted(position, true);
    }
    }