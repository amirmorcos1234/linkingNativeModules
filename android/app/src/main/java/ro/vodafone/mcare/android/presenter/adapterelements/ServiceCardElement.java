package ro.vodafone.mcare.android.presenter.adapterelements;

import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Victor Radulescu on 8/29/2017.
 */

public class ServiceCardElement extends StaticAdapterElement {

    ServiceCard serviceCard;
    public ServiceCardElement(ServiceCard serviceCard,int type, int order) {
        super(type, order);
        this.serviceCard = serviceCard;
    }

    @Override
    public ServiceCard getView() {
        return serviceCard;
    }


    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        return new BasicViewHolder(serviceCard);
    }

}
