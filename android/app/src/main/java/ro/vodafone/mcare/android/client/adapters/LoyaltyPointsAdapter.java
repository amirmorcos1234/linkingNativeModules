package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.LoyaltyPoints;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 21.04.2017.
 */

public class LoyaltyPointsAdapter extends BaseAdapter {
    private List<LoyaltyPoints> pointsList;
    private LayoutInflater inflater;
    private Context mContext;
    private VodafoneTextView cardTitle;
    private VodafoneTextView cardSubtext;
    private VodafoneTextView points;


    public LoyaltyPointsAdapter(List<LoyaltyPoints> list, Context context) {
        Log.d("", "AggregatedBenefitsAdapter: ");
        this.pointsList = list;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return pointsList.size();
    }

    @Override
    public Object getItem(int position) {
        return pointsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.card_layout_points_item, parent, false);
            cardTitle = (VodafoneTextView) convertView.findViewById(R.id.loyalty_item_header);
            cardSubtext = (VodafoneTextView) convertView.findViewById(R.id.loyalty_item_subtext);
            points = (VodafoneTextView) convertView.findViewById(R.id.points);
        }
        LoyaltyPoints loyaltyPointsObj = pointsList.get(position);

        cardTitle.setText(StringEscapeUtils.unescapeHtml4(loyaltyPointsObj.getReason()));
        cardSubtext.setText(String.format(LoyaltyLabels.getLoyaltyHistoryItemDate(), loyaltyPointsObj.getDisplayDate()));
        points.setText(String.valueOf(loyaltyPointsObj.getAmount()) + "p");

        return convertView;
    }
}
