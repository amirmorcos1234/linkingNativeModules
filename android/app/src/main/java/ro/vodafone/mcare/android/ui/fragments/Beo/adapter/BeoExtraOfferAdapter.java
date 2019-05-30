package ro.vodafone.mcare.android.ui.fragments.Beo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.fragments.Beo.BeoFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Alex on 3/13/2017.
 */



public class BeoExtraOfferAdapter extends BaseAdapter {

    private List<ActiveOffer> items;

    private Context context;

    public static final String TAG = "Selectro List Adapter";

    public BeoExtraOfferAdapter(Context context, List<ActiveOffer> items) {

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public OfferRowInterface getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup root) {

        final OfferRowInterface item = getItem(position);

        String offerPrice = String.format(BEOLabels.getEuro_price_currency(), NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));

        offerPrice = offerPrice.replaceAll(",", ".");
        SelectionPageButton spb = new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                item.getOfferName(), offerPrice, new BeoFragment(), null);
        /*DrawableUtils.generateCircleIcon(ContextCompat.getColor(context, R.color.product_card_primary_oceanblue),
                Color.WHITE, ContextCompat.getDrawable(context, R.drawable.clock_or_time), context)*/

        return spb.getLayoutView();

    }



}
