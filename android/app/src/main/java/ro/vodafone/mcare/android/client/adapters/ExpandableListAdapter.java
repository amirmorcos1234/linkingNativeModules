package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.costControl.currentBill.ExpandableListItemModel;
import ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    int clickedGroupPosition = 999;
    private ExpandableListAdapter _adapter;
    private Context _context;
    // header titles
    private List<ExpandableListItemModel> _listDataHeader;
    // child data in format of header title, child title
    private HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> _listDataChild;
    private boolean costControlCard = false;

    public ExpandableListAdapter(Context context, List<ExpandableListItemModel> listDataHeader,
                                 HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listChildData,
                                 boolean costControlCard) {
        this._adapter = this;
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.costControlCard = costControlCard;
    }

    public boolean isCostControlCard() {
        return costControlCard;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ExpandableListItemModel child = (ExpandableListItemModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item, parent, false);
        }

        VodafoneTextView itemName = convertView.findViewById(R.id.item_name);
        VodafoneTextView cost = convertView.findViewById(R.id.cost);
        ImageView itemArrow = convertView.findViewById(R.id.item_arrow);

        if (!child.is_isClickable()) {
            itemArrow.setVisibility(View.INVISIBLE);
        }

        itemName.setText(child.get_itemName());
        cost.setText(NumbersUtils.twoDigitsAfterDecimal(child.get_itemParameter()) + " " + child.get_unit());
        setCostAndNameColor(child, cost, itemName);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childrenCount = 0;
        if (this._listDataChild.get(this._listDataHeader.get(groupPosition)) != null) {
            childrenCount = this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }
        return childrenCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {

        Log.d("ExpandableListAdapter", "getGroupView");
        ExpandableListItemModel header = (ExpandableListItemModel) getGroup(groupPosition);
        if (convertView == null) {
            convertView = View.inflate(_context, R.layout.expandable_list_group, null);
        }

        VodafoneTextView groupName = convertView.findViewById(R.id.group_name);
        VodafoneTextView cost = convertView.findViewById(R.id.cost);
        final ImageView arrow = convertView.findViewById(R.id.group_arrow);

        if (getChildrenCount(groupPosition) != 0) {
            arrow.setVisibility(View.VISIBLE);
            arrow.setColorFilter(Color.parseColor("#343434"));
            if (clickedGroupPosition != 999) {
                if (isExpanded) {
                    DrawableUtils.rotateArrow(arrow, true);
                    header.setExpanded(true);
                } else {
                    DrawableUtils.rotateArrow(arrow, false);
                    header.setExpanded(false);
                }
            }
            clickedGroupPosition = groupPosition;
        } else {
            if (header.is_isClickable()) {
                arrow.setVisibility(View.VISIBLE);
                arrow.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.chevron_right_48));
                //convertView.setOnClickListener(header.get_tabLinkListner());
            }
        }

        groupName.setText(header.get_itemName());
        cost.setText(NumbersUtils.twoDigitsAfterDecimal(header.get_itemParameter()) + " " + header.get_unit());
        setCostAndNameColor(header, cost, groupName);

        return convertView;
    }

    private void setCostAndNameColor(ExpandableListItemModel expandableListItemModel, VodafoneTextView cost, VodafoneTextView groupName) {
        //todo replace Suplimentar
        if (expandableListItemModel.get_itemName().equalsIgnoreCase("Suplimentar") || SummaryDetails.SUPLIMENTARY.equalsIgnoreCase(expandableListItemModel.getItemKey())) {
            if (expandableListItemModel.get_itemParameter() <= 0) {
                groupName.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
                cost.setTextColor(ContextCompat.getColor(_context, R.color.blackNormal));
            } else {
                groupName.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
                cost.setTextColor(ContextCompat.getColor(_context, R.color.purple));
            }
        } else {
            groupName.setFont(VodafoneTextView.TextStyle.VODAFONE_LT);
        }

        if (isCostControlCard()) {
            if (!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
                if (expandableListItemModel.get_itemParameter() != null && expandableListItemModel.get_itemParameter() > 0) {
                    cost.setTextColor(ContextCompat.getColor(_context, R.color.purple));
                }
            }
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}