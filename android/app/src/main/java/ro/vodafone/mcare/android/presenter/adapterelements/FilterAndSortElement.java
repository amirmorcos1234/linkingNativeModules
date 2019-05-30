package ro.vodafone.mcare.android.presenter.adapterelements;

import android.widget.LinearLayout;

import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Victor Radulescu on 8/28/2017.
 */

public class FilterAndSortElement extends StaticAdapterElement {

    LinearLayout linearLayout;

    public FilterAndSortElement(LinearLayout linearLayout,int type, int order) {
        super(type, order);
        this.linearLayout = linearLayout;
    }

    @Override
    public LinearLayout getView() {
        return linearLayout;
    }

    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        return new BasicViewHolder(getView());
    }

}
