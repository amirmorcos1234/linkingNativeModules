package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.apache.commons.lang3.text.WordUtils;

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
 * Created by User on 26.07.2017.
 */

public class CallDetailsRecyclerViewAdapter extends RecyclerView.Adapter<CallDetailsRecyclerViewAdapter.CallDetailsRowTag> {

    private final static String GB_DATA_UNIT = "Gb";
    private final static String MB_DATA_UNIT = "mb";
    private final static String KB_DATA_UNIT = "kb";

    private static final String NATIONAL_ACCES_TYPE = "National";
    private static final String INTERNATIONAL_ACCES_TYPE = "International";
    private static final String NATIONAL_INTERNATIONAL_ACCES_TYPE = "National/Intl";
    private static final String ROAMING_ACCES_TYPE = "Roaming";

    private Category category;

    Context mContext;
    List<CallDetailsRow> callDetailsList;


    public CallDetailsRecyclerViewAdapter(Context context, List<CallDetailsRow> callDetailsRowList, Category category) {
        mContext = context;
        callDetailsList = callDetailsRowList;
        this.category = category;
    }


    @Override
    public CallDetailsRowTag onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.list_item_call_details, parent, false);

        return new CallDetailsRowTag(v);
    }

    @Override
    public void onBindViewHolder(CallDetailsRowTag holder, int position) {
        final CallDetailsRow callDetailsRow = callDetailsList.get(position);

        setRowImage(holder.image);
        holder.calledNumber.setText(callDetailsRow.getCalledNumber());
        holder.eventDate.setText(WordUtils.capitalize(DateUtils.getDate(String.valueOf(callDetailsRow.getEventDate()), new SimpleDateFormat("d MMMM yyyy HH:mm", new Locale("RO", "RO")))));
        holder.type.setText(getAccetType(callDetailsRow.getAccessType()));

        try {
            holder.cost.setText(NumbersUtils.twoDigitsAfterDecimal(Double.parseDouble(callDetailsRow.getCost())));
        } catch (Exception e) {
            e.printStackTrace();
            holder.cost.setText("0.00");
        }

        if (!category.equals(Category.SMS)) {
            holder.callDuration.setText(callDetailsRow.getUsage());
        }
    }

    @Override
    public int getItemCount() {
        return callDetailsList.size();
    }

    public static class CallDetailsRowTag extends RecyclerView.ViewHolder {

        ImageView image;
        VodafoneTextView calledNumber;
        VodafoneTextView cost;
        VodafoneTextView eventDate;
        VodafoneTextView callDuration;
        VodafoneTextView type;

        public CallDetailsRowTag(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.calledNumber = (VodafoneTextView) itemView.findViewById(R.id.called_number);
            this.cost = (VodafoneTextView) itemView.findViewById(R.id.cost);
            this.eventDate = (VodafoneTextView) itemView.findViewById(R.id.eventDate);
            this.callDuration = (VodafoneTextView) itemView.findViewById(R.id.call_duration);
            this.type = (VodafoneTextView) itemView.findViewById(R.id.type);

        }
    }

    private StringBuilder getAccetType(String accesType) {
        StringBuilder result = new StringBuilder();

        result.append(CallDetailsRowEnum.getAccesTypeFromCategory(category).getName());
        result.append(" ");

        switch (accesType) {
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

    private void setRowImage(ImageView imageView) {

        switch (category) {
            case DATE:
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.data_sharing));
                break;
            case VOCE:
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.minutes));
                break;
            case SMS:
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sms));
                break;
            case OTHER:
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apps_48));
                break;
        }

        imageView.setColorFilter(Color.parseColor("#999999"));
    }


    public void addListItemToAdapter(List<CallDetailsRow> list) {
        Log.d("", "addListItemToAdapter: calldetailslist" + callDetailsList.size());
        Log.d("", "addListItemToAdapter: list " + list.size());
        //Add list to current array list od data
        callDetailsList.addAll(list);
        if(list.size() != 0){
            this.notifyItemRangeInserted(callDetailsList.indexOf(list.get(0)), list.size());
        }

        //Notify UI
        //this.notifyDataSetChanged();
    }

    public void addListItemToAdapterWhenHasMore(List<CallDetailsRow> list) {
        Log.d("", "addListItemToAdapterWhenHasMore: calldetailsList " + callDetailsList.size());
        Log.d("", "addListItemToAdapterWhenHasMore: list " + list.size());
        callDetailsList.addAll(list);
            this.notifyItemRangeInserted(callDetailsList.indexOf(list.get(0)), list.size());
    }


    public void add(List<CallDetailsRow> callDetailsRow) {
        callDetailsList.addAll(callDetailsRow);
    }

}
