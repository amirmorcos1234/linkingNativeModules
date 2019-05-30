package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;

/**
 * Created by Alex on 3/16/2017.
 */

public class EoIncompatibleOfferView extends LinearLayout {

    private Context context;

    private View view;
    TextView incompatibleOfferQuantity;
    TextView incompatibleOfferPrice;

    private Object target;

    public EoIncompatibleOfferView(Context context) {
        super(context);
    }

    public EoIncompatibleOfferView(Context context, String offerQuantity, String offerAmmount, Object target) {
        super(context);
        this.context = context;
        init(offerQuantity, offerAmmount, target);
    }

    private void init( String offerQuantity, String offerAmmount, Object target ){
        view = inflate(context, R.layout.eo_incompatible_offer_view, null);

        this.incompatibleOfferQuantity = (TextView) view.findViewById(R.id.incompatible_offer_quantity);
        this.incompatibleOfferPrice = (TextView) view.findViewById(R.id.incompatible_offer_price);


        if(offerQuantity != null){
            this.incompatibleOfferQuantity.setVisibility(VISIBLE);
            this.incompatibleOfferQuantity.setText(offerQuantity);
        }

        if(offerAmmount != null){
            this.incompatibleOfferPrice.setVisibility(VISIBLE);
            this.incompatibleOfferPrice.setText("pentru: " + offerAmmount);

        }

        setTarget(target);
    }

    public void addIncompatibleOfferView(ViewGroup viewGroup, LayoutParams layoutParams){
        viewGroup.addView(view, layoutParams);
    }

    public View getView() {
        return view;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
