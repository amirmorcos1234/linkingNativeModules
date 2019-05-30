package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsRow;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsRowEnum;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;

/**
 * Created by Bivol Pavel on 15.02.2017.
 */
@Deprecated
public class CallDetailsAdapter extends BaseAdapter {

    private List<CallDetailsRow> callDetailsRowList;
    private LayoutInflater inflater;
    private Context context;
    private Category category;

    private final static String GB_DATA_UNIT = "Gb";
    private final static String MB_DATA_UNIT = "mb";
    private final static String KB_DATA_UNIT = "kb";

    private static final String  NATIONAL_ACCES_TYPE = "National";
    private static final String  INTERNATIONAL_ACCES_TYPE = "International";
    private static final String  NATIONAL_INTERNATIONAL_ACCES_TYPE = "National/Intl";
    private static final String  ROAMING_ACCES_TYPE = "Roaming";

    private String[] dataUnitArray = new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT};

    public CallDetailsAdapter(Context context, List<CallDetailsRow> callDetailsRowList, Category category) {
        this.context = context;
        this.callDetailsRowList = callDetailsRowList;
        this.inflater = LayoutInflater.from(context);
        this.category = category;
    }

    @Override
    public int getCount() {
        return callDetailsRowList.size();
    }

    public void add(List<CallDetailsRow> callDetailsRow){
        this.callDetailsRowList.addAll(callDetailsRow);
    }

    @Override
    public Object getItem(int position) {
        return callDetailsRowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallDetailsRowTag callDetailsRowTag = new CallDetailsRowTag();
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_item_call_details, parent, false);

            callDetailsRowTag.image = (ImageView)convertView.findViewById(R.id.image);
            callDetailsRowTag.calledNumber = (VodafoneTextView) convertView.findViewById(R.id.called_number);
            callDetailsRowTag.cost = (VodafoneTextView) convertView.findViewById(R.id.cost);
            callDetailsRowTag.eventDate = (VodafoneTextView) convertView.findViewById(R.id.eventDate);
            callDetailsRowTag.callDuration = (VodafoneTextView) convertView.findViewById(R.id.call_duration);
            callDetailsRowTag.type = (VodafoneTextView) convertView.findViewById(R.id.type);

            convertView.setTag(callDetailsRowTag);
        }else{
            callDetailsRowTag = (CallDetailsRowTag) convertView.getTag();
        }


        final CallDetailsRow callDetailsRow = callDetailsRowList.get(position);

        setRowImage(callDetailsRowTag.image);
        callDetailsRowTag.calledNumber.setText(callDetailsRow.getCalledNumber());
        callDetailsRowTag.eventDate.setText(DateUtils.getDate(String.valueOf(callDetailsRow.getEventDate()), new SimpleDateFormat("d MMMM yyyy hh:mm", new Locale("RO", "RO"))));
        callDetailsRowTag.type.setText(getAccetType(callDetailsRow.getAccessType()));

        try {
            callDetailsRowTag.cost.setText(NumbersUtils.twoDigitsAfterDecimal(Double.parseDouble(callDetailsRow.getCost())));
        }catch (Exception e){
            e.printStackTrace();
            callDetailsRowTag.cost.setText("0.00");
        }

        if(!category.equals(Category.SMS)){
            callDetailsRowTag.callDuration.setText(callDetailsRow.getUsage());
        }

        return convertView;
    }

    private class CallDetailsRowTag{

        ImageView image;
        VodafoneTextView calledNumber;
        VodafoneTextView cost;
        VodafoneTextView eventDate;
        VodafoneTextView callDuration;
        VodafoneTextView type;

    }

    private StringBuilder getAccetType(String accesType){
        StringBuilder result = new StringBuilder();

        result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getName());
        result.append(" ");

        switch (accesType){
            case NATIONAL_ACCES_TYPE:
                result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getNationalAccesType());
                break;
            case INTERNATIONAL_ACCES_TYPE:
                result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getInternationalAccesType());
                break;
            case ROAMING_ACCES_TYPE:
                result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getRoamingAccesType());
                break;
            case NATIONAL_INTERNATIONAL_ACCES_TYPE:
                result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getNationalInternationalAccesType());
                break;
            default:
                result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getInternationalAccesType());
        }
        return result;
    }

    private void setRowImage(ImageView imageView){

        switch (category){
            case DATE:
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.data_sharing));
                break;
            case VOCE:
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.minutes));
                break;
            case SMS:
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sms));
                break;
            case OTHER:
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.apps_48));
                break;
        }

        imageView.setColorFilter(Color.parseColor("#999999"));
    }


    public void addListItemToAdapter(List<CallDetailsRow> list){
        //Add list to current array list od data
        callDetailsRowList.addAll(list);

        //Notify UI
        this.notifyDataSetChanged();
    }


/*    public AmountUnitModel formatData(Float value, String[] units) {

        int digitGroups = (int) (Math.log10(value)/Math.log10(1024));
        String formatedDataValue = new DecimalFormat("#,##0.#").format(value/Math.pow(1024, digitGroups));
        formatedDataValue = formatedDataValue.replaceAll(",", ".");

        return new AmountUnitModel(new Float(formatedDataValue) ,  units[digitGroups]);
    }*/
}
