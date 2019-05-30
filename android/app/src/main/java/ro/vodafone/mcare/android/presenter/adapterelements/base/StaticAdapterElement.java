package ro.vodafone.mcare.android.presenter.adapterelements.base;

import android.view.View;
import android.view.ViewGroup;

import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Victor Radulescu on 8/29/2017.
 */

public abstract class StaticAdapterElement extends AdapterElement {

    public StaticAdapterElement(int type, int order) {
        super(type, order);
    }

    @Override
    public Object getData() {
        return null;
    }
    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        View view = getView();
        if(view.getParent()!=null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        return new BasicViewHolder(view);
    }

    @Override
    protected final boolean isShouldBeCreatedFromData() {
        return false;
    }
}
