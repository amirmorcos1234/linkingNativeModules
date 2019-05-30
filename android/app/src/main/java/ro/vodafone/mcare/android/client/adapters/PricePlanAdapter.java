package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */
@Deprecated
public class PricePlanAdapter extends BaseAdapter {

    private Context mContext;
    private List<BalanceShowAndNotShown> pricePlanList;
    private LayoutInflater inflater;

    private static float DEFAULT_ARC_VALUE = 0;
    private static float DEFAULT_MAX_ARC_VALUE = 100;

    private final static String GB_DATA_UNIT = "Gb";
    private final static String MB_DATA_UNIT = "mb";
    private final static String KB_DATA_UNIT = "kb";

    private final static String SEC_UNIT = "sec";
    private final static String MIN_UNIT = "min";

    private final static String UNLIMITED = "Nelimitat";

    private final static String SMS = "SMS-uri";

    public PricePlanAdapter(List<BalanceShowAndNotShown> activeOffersList, Context context){
        this.mContext = context;
        this.pricePlanList = activeOffersList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pricePlanList.size();
    }

    @Override
    public Object getItem(int i) {
        return pricePlanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ActiveOffersItemTag activeOffersItemTag = new ActiveOffersItemTag();
        final BalanceShowAndNotShown balance;
        AmountUnitModel amountUnitModel;

        if (view == null) {
            view = inflater.inflate(R.layout.list_item_active_offers, viewGroup, false);
            activeOffersItemTag.offerName = (VodafoneTextView) view.findViewById(R.id.offer_name);
            activeOffersItemTag.offerPrice = (VodafoneTextView) view.findViewById(R.id.offer_price);
            activeOffersItemTag.arcView = (DecoView) view.findViewById(R.id.dynamicArcView);

            view.setTag(activeOffersItemTag);
        }else{
            activeOffersItemTag = (ActiveOffersItemTag) view.getTag();
        }

        balance = pricePlanList.get(i);
        amountUnitModel = getAmountUnitObject(balance);

        activeOffersItemTag.offerName.setText(balance.getNameRO());

        if(balance.getAmountUnit() == AmountUnitEnum.kb || balance.getAmountUnit() == AmountUnitEnum.mb || balance.getAmountUnit() == AmountUnitEnum.gb){
            activeOffersItemTag.offerPrice.setText(NumbersUtils.twoDigitsAfterDecimal(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit());
        }else{
            activeOffersItemTag.offerPrice.setText(String.valueOf(Math.round(amountUnitModel.getAmount())) + " " + amountUnitModel.getUnit());
        }

        setArcView(activeOffersItemTag.arcView, balance);
        return view;
    }

    private void setArcView(DecoView arcView, BalanceShowAndNotShown balance){

// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#ebebeb"))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(8f)
                .build());

//Create data series track
        Float totalAmount = DEFAULT_MAX_ARC_VALUE;
        Float remainingAmount = DEFAULT_ARC_VALUE;

        if(balance.getTotalAmount() != null){
            totalAmount = balance.getTotalAmount().floatValue();
        }

        if(balance.getRemainingAmount() != null){
            remainingAmount = balance.getRemainingAmount().floatValue();
        }


        if(balance.getAmountUnit() != AmountUnitEnum.unl){
            try {
                SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00afca"))
                        .setRange(0, Float.valueOf(String.valueOf(totalAmount)), Float.valueOf(String.valueOf(remainingAmount)))
                        .setLineWidth(10f)
                        .build();

                arcView.addSeries(seriesItem1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private AmountUnitModel getAmountUnitObject(BalanceShowAndNotShown balance){
        AmountUnitModel amountUnit = new AmountUnitModel();

        if(balance != null){
            switch (balance.getAmountUnit()) {
                case num:
                    amountUnit.setUnit(SMS);
                    amountUnit.setAmount(balance.getRemainingAmount());
                    break;
                case sec:
                    amountUnit = secToMin(balance.getRemainingAmount());
                    break;
                case min:
                    amountUnit.setAmount(balance.getRemainingAmount());
                    amountUnit.setUnit(MIN_UNIT);
                    break;
                case kb:
                    amountUnit = formatData(balance.getRemainingAmount(), new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case mb:
                    amountUnit = formatData(balance.getRemainingAmount(), new String[] {MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case gb:
                    amountUnit.setAmount(balance.getRemainingAmount());
                    amountUnit.setUnit(GB_DATA_UNIT);
                    break;
                case unl:
                    amountUnit.setUnit(UNLIMITED);
                    break;
            }
        }
        return amountUnit;
    }

    private static AmountUnitModel secToMin(Double value){
        return new AmountUnitModel(value/60, MIN_UNIT);
    }

    public static AmountUnitModel formatData(Double value, String[] units) {

        int digitGroups = (int) (Math.log10(value)/Math.log10(1024));
        String formatedDataValue = NumbersUtils.twoDigitsAfterDecimal(value/Math.pow(1024, digitGroups));
        if(digitGroups>0 && digitGroups< units.length){
            return new AmountUnitModel(Double.valueOf(formatedDataValue) ,  units[digitGroups]);
        }
        return new AmountUnitModel(Double.valueOf(formatedDataValue) ,  units[0]);
    }

    private class ActiveOffersItemTag{

        VodafoneTextView offerName;
        VodafoneTextView offerPrice;
        DecoView arcView;
    }
}
