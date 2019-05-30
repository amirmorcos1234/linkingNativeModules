package ro.vodafone.mcare.android.presenter.adapterelements;

import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;
import ro.vodafone.mcare.android.widget.TabMenu.TabCard;

/**
 * Created by Victor Radulescu on 8/28/2017.
 */

public class CardTabElement extends StaticAdapterElement {

    TabCard tabCard;

    public CardTabElement(int type, int order, TabCard tabCard) {
        super(type, order);
        this.tabCard = tabCard;
    }

    @Override
    protected TabCard getView() {
        return tabCard;
    }


    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        return new BasicViewHolder(getView());
    }

}
