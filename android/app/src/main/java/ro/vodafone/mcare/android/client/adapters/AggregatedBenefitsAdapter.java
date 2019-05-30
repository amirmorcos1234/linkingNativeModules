package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;


/**
 * Created by Bivol Pavel on 04.04.2017.
 */

@Deprecated
public class AggregatedBenefitsAdapter extends BaseAdapter {
    List<BalanceShowAndNotShown> aggregatedBenefitsList;
    LayoutInflater inflater;
    AmountTypeIdEnum category;
    Context mContext;

    private static final String TAG = "AggregatedBenefAdapter";

    private final static String GB_DATA_UNIT = "Gb";
    private final static String MB_DATA_UNIT = "mb";
    private final static String KB_DATA_UNIT = "kb";

    private final static String SEC_UNIT = "sec";
    private final static String MIN_UNIT = "min";

    private final static String UNLIMITED = "Nelimitat";

    private final static String SMS = "SMS-uri";

    public AggregatedBenefitsAdapter(List<BalanceShowAndNotShown> list, Context context) {
        Log.d("", "AggregatedBenefitsAdapter: ");
        this.aggregatedBenefitsList = list;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return aggregatedBenefitsList.size();
    }

    @Override
    public Object getItem(int position) {
        return aggregatedBenefitsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("AggregatedBenefitsAdapter and position :" + position);

        VodafoneTextView benefitName = null;
        VodafoneTextView benefitAmount = null;
        ImageView benefitIcon = null;
        VodafoneTextView amountUnit = null;

        category = aggregatedBenefitsList.get(position).getAmountTypeId();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.aggregated_benefit_list_item, parent, false);
            benefitName = (VodafoneTextView) convertView.findViewById(R.id.benefit_name);
            benefitAmount = (VodafoneTextView) convertView.findViewById(R.id.benefit_amount);
            benefitIcon = (ImageView) convertView.findViewById(R.id.benefit_icon);
            amountUnit = (VodafoneTextView) convertView.findViewById(R.id.amount_unit);
        }

        BalanceShowAndNotShown balance = aggregatedBenefitsList.get(position);
        AmountUnitModel amountUnitModel = getAmountUnitObject(balance);

        benefitName.setText(balance.getNameRO());
        if(amountUnitModel.getAmount() != null){
            if(balance.getAmountUnit() == AmountUnitEnum.kb || balance.getAmountUnit() == AmountUnitEnum.mb || balance.getAmountUnit() == AmountUnitEnum.gb){
                benefitAmount.setText(NumbersUtils.twoDigitsAfterDecimal(amountUnitModel.getAmount()));
            }else{
                benefitAmount.setText(String.valueOf(Math.round(amountUnitModel.getAmount())));
            }
        }
        amountUnit.setText(amountUnitModel.getUnit());

        setRowImage(benefitIcon);

        return convertView;
    }

    private void setRowImage(ImageView imageView) {
        if (category != null) {
            switch (category) {
                case data:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.data_sharing));
                    break;
                case voice:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.minutes));
                    break;
                case sms:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sms));
                    break;
                case vas:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sync_48));
                    break;
                case cvt:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.community_or_foundation_48));
                    break;
            }
        }else{
            //Set default image for other .
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.minutes));
        }
    }

    private AmountUnitModel getAmountUnitObject(BalanceShowAndNotShown balance){
        AmountUnitModel amountUnit = new AmountUnitModel();

        if(balance != null){
            switch (balance.getAmountUnit()) {
                case num:
                    amountUnit.setUnit(SMS);
                    amountUnit.setAmount(Double.valueOf(balance.getRemainingAmount()));
                    break;
                case sec:
                    Log.d(TAG, "Sec and remaining amoiunt is :"+balance.getRemainingAmount());
                    if(balance.getRemainingAmount() != null && balance.getRemainingAmount() >= 60){
                        amountUnit = secToMin(Double.valueOf(balance.getRemainingAmount()));
                    }else{
                        amountUnit = new AmountUnitModel(Double.valueOf(balance.getRemainingAmount()), SEC_UNIT);
                    }

                    break;
                case min:
                    amountUnit.setAmount(Double.valueOf(balance.getRemainingAmount()));
                    amountUnit.setUnit(MIN_UNIT);
                    break;
                case kb:
                    amountUnit = formatData(balance.getRemainingAmount(), new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case mb:
                    amountUnit = formatData(balance.getRemainingAmount(), new String[] {MB_DATA_UNIT, GB_DATA_UNIT});
                    break;
                case gb:
                    amountUnit.setAmount(Double.valueOf(balance.getRemainingAmount()));
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
        Log.d(TAG, "secToMin()");
        return new AmountUnitModel(value/60, MIN_UNIT);
    }

    public static AmountUnitModel formatData(Double value, String[] units) {

        int digitGroups = (int) (Math.log10(value)/Math.log10(1024));
        String formatedDataValue = NumbersUtils.twoDigitsAfterDecimal(value/Math.pow(1024, digitGroups));
        formatedDataValue = formatedDataValue.replaceAll(",", ".");
        D.w(TAG + " " + formatedDataValue);

        return new AmountUnitModel(Double.valueOf(formatedDataValue) ,  units[digitGroups]);
    }
}
