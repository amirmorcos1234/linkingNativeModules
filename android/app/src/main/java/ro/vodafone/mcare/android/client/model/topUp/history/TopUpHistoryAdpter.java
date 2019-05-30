package ro.vodafone.mcare.android.client.model.topUp.history;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Alex on 15.02.2017.
 */

public class TopUpHistoryAdpter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    private Context context;
    private List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryListGroup;
    private String[] monthsArray;

    public TopUpHistoryAdpter(Context context, List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryListGroup) {
        this.context = context;
        this.monthlyRechargeHistoryListGroup = monthlyRechargeHistoryListGroup;
        monthsArray = context.getResources().getStringArray(R.array.months_array);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<RechargeHistoryRow> rechargeHistoryRow = monthlyRechargeHistoryListGroup.get(groupPosition).getRechargeHistoryRow();
        return rechargeHistoryRow.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public long getChildCount(int groupPosition) {
        return monthlyRechargeHistoryListGroup.get(groupPosition).getRechargeHistoryRow().size();
    }


    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("ChildVIew", "getChildView ");
        RechargeHistoryRow child = (RechargeHistoryRow) getChild(groupPosition, childPosition);

        D.v("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//        D.v("child.getActionTypeId()  = " + child.getActionTypeId());
        D.v("child.getChannelTypeId() = " + child.getChannelTypeId());
        D.v("child.getIconUrl()       = " + child.getIconUrl());
//        D.v("child.getDate()          = " + child.getDate());
//        D.v("child.getAmount()        = " + child.getAmount());
//        D.v("child.getSummary()       = " + child.getSummary());
//        D.v("child.getPrepaidMsisdn() = " + child.getPrepaidMsisdn());
        D.v("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.top_up_month_details_history_item, parent, false);
            convertView.setTag(R.id.topup_child_holder_id, new ChildViewHolder(convertView));
        }

        final ChildViewHolder vh = (ChildViewHolder) convertView.getTag(R.id.topup_child_holder_id);

        if (childPosition == 0)
            vh.topSeparator.setVisibility(View.GONE);

        if (isLastChild)
            vh.bottomSeparator.setVisibility(View.GONE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", new Locale("RO", "RO"));
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", new Locale("RO", "RO"));//dd.MM.yyyy
        if (child.getDate() != null) {
            Log.d("ChildVIew", "child.getDate() " + child.getDate());
            String datetext = "";
            try {
                datetext = dateFormat.format(new Date(child.getDate())) +
                        " pe " + new Date(child.getDate()).getDate() +
                        " " + monthsArray[new Date(child.getDate()).getMonth()] +
                        " " + yearFormat.format(new Date(child.getDate()));
            } catch (Exception ex) {
                Log.e("datetext", "Exception datetext = dateFormat.format", ex);
            }
            vh.date.setText(datetext);
        }

        if (child.getAmount() != null) {
            Log.d("ChildVIew", "child.getAmount() " + child.getAmount());
            if (Float.valueOf(child.getAmount()) > 0) {
                vh.ammount.setTextColor(ContextCompat.getColor(context, R.color.v_icon_green));
            } else if (Float.valueOf(child.getAmount()) < 0) {
                vh.ammount.setTextColor(ContextCompat.getColor(context, R.color.red_button_color));
            }
            vh.ammount.setText(String.format(AppLabels.getEURAmountCurrency(), String.valueOf(child.getAmount())));
        }

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            vh.msisdn.setVisibility(View.GONE);
            vh.chanel.setVisibility(View.GONE);
            vh.actionType.setVisibility(View.GONE);

            Log.d("", "getChildView: " + child.getSummary());
            vh.prepaidDescription.setText(child.getSummary());
        } else {
            vh.prepaidDescription.setVisibility(View.GONE);
            if (child.getPrepaidMsisdn() != null) {
                Log.d("ChildVIew", "child.getPrepaidMsisdn() " + child.getPrepaidMsisdn());
                String prepaidMsisdn = child.getPrepaidMsisdn();
                if (prepaidMsisdn.startsWith("7")) {
                    prepaidMsisdn = prepaidMsisdn.replaceFirst("7", "07");
                }
                vh.msisdn.setText(TextUtils.fromHtml(String.format(TopUPLabels.getTopUp_history_recharged_msisdn(), prepaidMsisdn)));
            }
            if (child.getChannelTypeId() != null) {
                Log.d("ChildVIew", "child.getChannelTypeId() " + child.getChannelTypeId());
                vh.chanel.setText(String.format(TopUPLabels.getTop_up_history_channel(), child.getChannelTypeId()));
            }

            if (child.getActionTypeId() != null) {
                Log.d("ChildVIew", "child.getActionTypeId() " + child.getActionTypeId());
                vh.actionType.setText(String.format(TopUPLabels.getTop_up_history_payment_method(), child.getActionTypeId()));
            }


            if (child.getIconUrl() != null)
                switch (child.getIconUrl()) {
                    case "ic_top_up":
                        vh.history_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.top_up_history_image));
                    case "ic_auto_top_up":
                        vh.history_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.auto_top_up));
                        vh.history_image.setDrawableColor(R.color.green_check_mark_color);
                        break;
                    case "ic_sos_credit":
                        vh.history_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.sos_credit));
                        vh.history_image.setDrawableColor(R.color.green_check_mark_color);
                        break;
                    default:
                        vh.history_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.top_up_history_image));
                        break;
                }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<RechargeHistoryRow> rechargeHistoryRow = monthlyRechargeHistoryListGroup.get(groupPosition).getRechargeHistoryRow();
        return rechargeHistoryRow.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return monthlyRechargeHistoryListGroup.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return monthlyRechargeHistoryListGroup.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        RechargeHistoryMonthlyGroup group = (RechargeHistoryMonthlyGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.top_up_month_history_item, parent, false);
        }
        if (groupPosition == 0) {
            convertView.findViewById(R.id.top_separator).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.top_separator).setVisibility(View.VISIBLE);
        }
        if (isExpanded) {
            convertView.findViewById(R.id.bottom_separator).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.bottom_separator).setVisibility(View.GONE);
        }
        TextView monthText = (TextView) convertView.findViewById(R.id.history_month_tittle);
        monthText.setText(monthsArray[group.getMonth() - 1] + " " + new SimpleDateFormat("yyyy").format(group.getRechargeHistoryRow().get(0).getDate()));


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class ChildViewHolder {
        @BindView(R.id.history_details_date)
        TextView date;
        @BindView(R.id.recharge_for_msisnd)
        TextView msisdn;
        @BindView(R.id.chanale_mode_label)
        TextView chanel;
        @BindView(R.id.action_type)
        TextView actionType;
        @BindView(R.id.history_amount)
        TextView ammount;
        @BindView(R.id.prepaid_description)
        TextView prepaidDescription;
        @BindView(R.id.bottom_separator)
        View bottomSeparator;
        @BindView(R.id.top_separator)
        View topSeparator;
        @BindView(R.id.history_image)
        DynamicColorImageView history_image;

        private ChildViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }

}


