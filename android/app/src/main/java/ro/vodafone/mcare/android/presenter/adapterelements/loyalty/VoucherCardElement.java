package ro.vodafone.mcare.android.presenter.adapterelements.loyalty;

import android.content.Context;
import android.view.View;

import java.util.List;

import ro.vodafone.mcare.android.card.loyaltyMarket.LoyaltyVoucherCard;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.loyalty.VoucherViewHolder;

/**
 * Created by Victor Radulescu on 8/29/2017.
 */

public class VoucherCardElement extends DynamicAdapterElement<Promotion> {

    public VoucherCardElement(int type, int order, Context context, List<Promotion> vouchers) {
        super(type, order,context,vouchers);
    }

    @Override
    protected View getView() {
        return new LoyaltyVoucherCard(getContext());
    }

    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        return new VoucherViewHolder(getView(viewType));
    }
}
