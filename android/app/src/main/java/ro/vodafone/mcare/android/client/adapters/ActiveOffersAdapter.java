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
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.BalanceInfo;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */
@Deprecated
public class ActiveOffersAdapter extends BaseAdapter {

    private OfferRowInterface offerRowInterface;
    private LayoutInflater inflater;
    private BalanceShowAndNotShown balance;
    private List<BalanceInfo> balances;

    private final static String GB_DATA_UNIT = "GB";
    private final static String MB_DATA_UNIT = "MB";
    private final static String KB_DATA_UNIT = "KB";

    private final static String SEC_UNIT = "sec";
    private final static String MIN_UNIT = "min";

    private final static String UNLIMITED = "Nelimitat";

    private final static String SMS = "SMS-uri";

    public ActiveOffersAdapter(List<BalanceInfo> balances, Context context){
        this.balances = balances;
        this.inflater = LayoutInflater.from(context);
    }

    public ActiveOffersAdapter (BalanceShowAndNotShown balance, Context context){
        this.inflater = LayoutInflater.from(context);
        this.balance = balance;
    }

    @Override
    public int getCount() {
        int count = 0;

        if(balances != null){
            Log.d("ActiveOffersAdapter", "getCount balances not null");
            count = balances.size();
        }else if(balance != null){
            Log.d("ActiveOffersAdapter", "getCount balance not null");
            count = 1;
        }
        Log.d("ActiveOffersAdapter", "getCount count = " + count);
        return count;
    }

    @Override
    public Object getItem(int i) {
        if(balances != null){
            return balances.get(i);
        }else if(balance != null){
            return balance;
        }

        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        System.out.println("ActiveOffersAdapter and position :" + i);
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


        if(balances != null){
            BalanceInfo balanceInfo = balances.get(i);

            amountUnitModel = getAmountUnitObject(null, balanceInfo);

            activeOffersItemTag.offerName.setText(balanceInfo.getBalanceDescription());
            if(balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.kb || balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.mb || balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.gb){
                activeOffersItemTag.offerPrice.setText(NumbersUtils.twoDigitsAfterDecimal(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit());
            } else {
                activeOffersItemTag.offerPrice.setText(Math.round(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit());
            }

            setArcView(activeOffersItemTag.arcView, null, balanceInfo);

        }else if(balance != null){
            amountUnitModel = getAmountUnitObject(balance, null);

            activeOffersItemTag.offerName.setText(balance.getNameRO());
            if(balance.getAmountUnit() == AmountUnitEnum.kb || balance.getAmountUnit() == AmountUnitEnum.mb || balance.getAmountUnit() == AmountUnitEnum.gb){
                activeOffersItemTag.offerPrice.setText(NumbersUtils.twoDigitsAfterDecimal(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit());
            } else {
                activeOffersItemTag.offerPrice.setText(Math.round(amountUnitModel.getAmount()) + " " + amountUnitModel.getUnit());
            }

            setArcView(activeOffersItemTag.arcView, balance, null);
        }

        return view;
    }


    private void setArcView(DecoView arcView, BalanceShowAndNotShown balance, BalanceInfo balanceInfo){
        if(balance != null){
            try{
                // Create background track
                arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#ebebeb"))
                        .setRange(0, 100, 100)
                        .setInitialVisibility(true)
                        .setLineWidth(8f)
                        .build());

                //Create data series trac
                if(balance.getTotalAmount() > 0){
                    SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00afca"))
                            .setRange(0, balance.getTotalAmount().floatValue(), balance.getUsedAmount().floatValue())
                            .setLineWidth(10f)
                            .build();

                    int series1Index = arcView.addSeries(seriesItem1);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(balanceInfo != null){
            try {
                // Create background track
                arcView.addSeries(new SeriesItem.Builder(Color.parseColor("#ebebeb"))
                        .setRange(0, 100, 100)
                        .setInitialVisibility(true)
                        .setLineWidth(8f)
                        .build());

                //Create data series trac
                if(balanceInfo.getBalanceTotal()!=null && balanceInfo.getBalanceRemaining()!=null && balanceInfo.getBalanceTotal() > 0){
                    SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00afca"))
                            .setRange(0, balanceInfo.getBalanceTotal().floatValue(),balanceInfo.getBalanceRemaining().floatValue())
                            .setLineWidth(10f)
                            .build();

                    int series1Index = arcView.addSeries(seriesItem1);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private AmountUnitModel getAmountUnitObject(BalanceShowAndNotShown balance, BalanceInfo balanceInfo){
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
        }else if(balanceInfo != null){
            switch (balanceInfo.getBalanceUnit()) {
                case num:
                    amountUnit.setUnit(SMS);
                    amountUnit.setAmount(balanceInfo.getBalanceRemaining());
                    break;
                case sec:
                    amountUnit = secToMin(balanceInfo.getBalanceRemaining());
                    break;
                case min:
                    amountUnit.setAmount(balanceInfo.getBalanceRemaining());
                    amountUnit.setUnit(MIN_UNIT);
                    break;
                case kb:
                    amountUnit = formatData(balanceInfo.getBalanceRemaining(), new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case mb:
                    amountUnit = formatData(balanceInfo.getBalanceRemaining(), new String[] {MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case gb:
                    amountUnit.setAmount(balanceInfo.getBalanceRemaining());
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

        String formatedDataValue = NumbersUtils.twoDigitsAfterDecimal(value / Math.pow(1024, digitGroups));

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
