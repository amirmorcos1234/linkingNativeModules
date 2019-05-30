package ro.vodafone.mcare.android.presenter.adapterelements.base;

import android.view.View;

import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Victor Radulescu on 8/25/2017.
 */

public abstract class AdapterElement {

    protected int order;

    protected final int type;

    public AdapterElement(int type,int order) {
        this.type = type;
        this.order = order;
    }

    /**
     * View can be setted as a property for the instance or be created.
     * Avoid to set it as property for list elements but use it as such for static items as navigations items, maps, tab switchers

     */
    protected abstract View getView();

    /**
     * Optional could reflect no data.
     */
    public abstract Object getData();

    protected abstract boolean isShouldBeCreatedFromData();

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getType() {
        return type;
    }
    public abstract BasicViewHolder getViewHolder(int viewType);

    public boolean hasGeneralBackground() {
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdapterElement that = (AdapterElement) o;

        if (order != that.order) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = order;
        result = 31 * result + type;
        return result;
    }
}
