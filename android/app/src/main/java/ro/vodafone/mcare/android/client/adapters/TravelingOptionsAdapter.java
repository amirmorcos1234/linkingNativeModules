package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.BalanceInfo;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */
public class TravelingOptionsAdapter extends BaseAdapter {

    private final static String TAG = TravelingOptionsAdapter.class.getSimpleName();

    private Context mContext;
    private OfferRowInterface activeOffer;
    private LayoutInflater inflater;
    private List<BalanceShowAndNotShown> balanceShowAndNotShownList;

    private final static String GB_DATA_UNIT = "Gb";
    private final static String MB_DATA_UNIT = "mb";
    private final static String KB_DATA_UNIT = "kb";

    private final static String SEC_UNIT = "sec";
    private final static String MIN_UNIT = "min";

    private final static String UNLIMITED = "Nelimitat";

    private final static String SMS = "SMS-uri";

    public TravelingOptionsAdapter(OfferRowInterface activeOffer, Context context){
        this.mContext = context;
        this.activeOffer = activeOffer;
        this.inflater = LayoutInflater.from(context);
    }

    public TravelingOptionsAdapter(OfferRowInterface activeOffer, Context context, List<BalanceShowAndNotShown> balance){
        this.mContext = context;
        this.activeOffer = activeOffer;
        this.inflater = LayoutInflater.from(context);
        this.balanceShowAndNotShownList = balance;
    }

    @Override
    public int getCount() {
        if (activeOffer instanceof ActiveOffer) {
            ActiveOffer activeOffer1 = (ActiveOffer) activeOffer;
            if (activeOffer1.getBalances() != null) {
                return activeOffer1.getBalances().size();
            }
        }
        return balanceShowAndNotShownList.size();
    }

    @Override
    public Object getItem(int i) {
        if (activeOffer instanceof ActiveOffer) {
            ActiveOffer activeOffer1 = (ActiveOffer) activeOffer;
            if (activeOffer1.getBalances() != null) {
                return activeOffer1.getBalances().get(i);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ActiveOffersItemTag activeOffersItemTag = new ActiveOffersItemTag();
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

        BalanceShowAndNotShown balanceShowAndNotShown;

        if (activeOffer instanceof ActiveOffer) {

            BalanceInfo balanceInfo = (BalanceInfo) getItem(i);

            activeOffersItemTag.offerName.setText(balanceInfo.getBalanceDescription());
            balanceShowAndNotShown = new BalanceShowAndNotShown();
            balanceShowAndNotShown.setAmountUnitString(balanceInfo.getBalanceUnit() != null ? balanceInfo.getBalanceUnit().toString() : null);

            Double balanceTotal = balanceInfo.getBalanceTotal();
            Double balanceRemaining = balanceInfo.getBalanceRemaining();
            Double balanceUsed = balanceTotal != null && balanceRemaining != null ? balanceTotal - balanceRemaining : null;

            balanceShowAndNotShown.setTotalAmount(balanceTotal);
            balanceShowAndNotShown.setRemainingAmount(balanceRemaining);
            balanceShowAndNotShown.setUsedAmount(balanceUsed);

            amountUnitModel = getAmountUnitObject(balanceShowAndNotShown);

            setArcView(activeOffersItemTag.arcView, balanceShowAndNotShown);

        } else {

            balanceShowAndNotShown = balanceShowAndNotShownList.get(i);

            amountUnitModel = getAmountUnitObject(balanceShowAndNotShown);

            final OfferRowInterface activeOfferPostpaid = activeOffer;

            activeOffersItemTag.offerName.setText(activeOfferPostpaid.getOfferName());

            setArcView(activeOffersItemTag.arcView, balanceShowAndNotShown);
        }

        if (balanceShowAndNotShown != null) {
            if (balanceShowAndNotShown.getAmountUnit() == AmountUnitEnum.kb || balanceShowAndNotShown.getAmountUnit() == AmountUnitEnum.mb || balanceShowAndNotShown.getAmountUnit() == AmountUnitEnum.gb) {
                activeOffersItemTag.offerPrice.setText(NumbersUtils.twoDigitsAfterDecimal(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit().toUpperCase());
            } else {
                activeOffersItemTag.offerPrice.setText(String.valueOf(Math.round(amountUnitModel.getAmount())) + " " + amountUnitModel.getUnit().toUpperCase());
            }
        }

        return view;
    }


    private void setArcView(DecoView arcView, BalanceShowAndNotShown balance){
        if(balance != null){
            // Create background track
            arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#ebebeb"))
                    .setRange(0, 100, 100)
                    .setInitialVisibility(true)
                    .setLineWidth(ScreenMeasure.dpToPx(Math.round(3f)))
                    .build());

            //Create data series trac

            try {
                SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00afca"))
                        .setRange(0, balance.getTotalAmount().floatValue(), balance.getRemainingAmount().floatValue())
                        .setLineWidth(ScreenMeasure.dpToPx(Math.round(4f)))
                        .build();

                int series1Index = arcView.addSeries(seriesItem1);
            }catch (Exception e){
                Log.e("TravelingOptionsAdapter", "error is :" + e );
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
        formatedDataValue = formatedDataValue.replaceAll(",", ".");
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
