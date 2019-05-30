package ro.vodafone.mcare.android.card.loyaltyPoints.expandableList;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 25.04.2017.
 */

public class LoyaltyExpandableListAdapter extends BaseExpandableListAdapter {
    private LoyaltyExpandableListAdapter _adapter;
    private Context _context;
    // header titles
    private List<LoyaltyPointsListItem> _listDataHeader;
    // child data in format of header title, child title
    private HashMap<LoyaltyPointsListItem, List<LoyaltyPointsListItem>> _listDataChild;
    private boolean isGroupExpanded = false;
    VodafoneTextView groupName;
    ImageView itemArrow;
    ImageView arrow;

    private ExpandableAdapterBackedLinearLayout _expandableListView;

    int clickedGroupPosition = 999;

    public LoyaltyExpandableListAdapter(Context context, List<LoyaltyPointsListItem> listDataHeader,
                                        HashMap<LoyaltyPointsListItem, List<LoyaltyPointsListItem>> listChildData, ExpandableAdapterBackedLinearLayout expandableListView) {
        this._adapter = this;
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._expandableListView = expandableListView;

        setClickListners();

    }

    private void setClickListners() {
        _expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (arrow.getVisibility() == View.VISIBLE){
                    if (isGroupExpanded) {
                        DrawableUtils.rotateArrow(arrow, false);
                        groupName.setTypeface(Fonts.getVodafoneRG());
                        view.setBackgroundColor(ContextCompat.getColor(_context, R.color.card_background_white));
                        _expandableListView.collapseGroup(i);
                        isGroupExpanded = false;
                    } else {
                        DrawableUtils.rotateArrow(arrow, true);
                        groupName.setTypeface(Fonts.getVodafoneRGBD());
                        view.setBackgroundColor(ContextCompat.getColor(_context, R.color.expandable_list_view_background));

                        _expandableListView.expandGroup(i);
                        isGroupExpanded = true;
                    }
                }

                return false;
            }
        });

    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
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
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {

        LoyaltyPointsListItem header = (LoyaltyPointsListItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item_model, null);
        }

        groupName = (VodafoneTextView) convertView.findViewById(R.id.item_name);
        VodafoneTextView cost = (VodafoneTextView) convertView.findViewById(R.id.cost);
        VodafoneTextView itemSubtext = (VodafoneTextView) convertView.findViewById(R.id.item_subtext);
        arrow = (ImageView) convertView.findViewById(R.id.item_arrow);
        View customDivider = convertView.findViewById(R.id.custom_divider);

        if (getChildrenCount(groupPosition) != 0) {
            arrow.setVisibility(View.VISIBLE);
            arrow.setColorFilter(Color.parseColor("#343434"));
            //setClickListners();
            //arrow.setVisibility(View.VISIBLE);
            itemSubtext.setVisibility(View.GONE);
           /* if (clickedGroupPosition == groupPosition) {
                if (!groupName.getText().equals("")) {
                    if (isExpanded) {
                        DrawableUtils.rotateArrow(arrow, true);
                        groupName.setTypeface(Fonts.getVodafoneRGBD());
                        convertView.setBackgroundColorWithRes(ContextCompat.getColor(_context,R.color.expandable_list_view_background));
                        setDividerMargins(customDivider,false);
                    } else {
                        DrawableUtils.rotateArrow(arrow, false);
                        groupName.setTypeface(Fonts.getVodafoneRG());
                        convertView.setBackgroundColorWithRes(ContextCompat.getColor(_context,R.color.card_background_white));
                        setDividerMargins(customDivider,true);
                    }
                }
            }*/
        } else {
            if (header.get_tabLinkListner() != null) {
                arrow.setVisibility(View.VISIBLE);
                arrow.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.chevron_right_48));
                convertView.setOnClickListener(header.get_tabLinkListner());
            }

            if (header.get_itemSubtext() == null || header.get_itemSubtext().equals("")) {
                itemSubtext.setVisibility(View.GONE);
            } else {
                itemSubtext.setText(header.get_itemSubtext());
            }
        }

        groupName.setText(header.get_itemName());
        cost.setText(header.get_itemParameter() + "p");
        //  setCostColor(header, cost);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final LoyaltyPointsListItem child = (LoyaltyPointsListItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item_model, null);
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.expandable_list_view_background));
        }

        VodafoneTextView itemName = (VodafoneTextView) convertView.findViewById(R.id.item_name);
        VodafoneTextView cost = (VodafoneTextView) convertView.findViewById(R.id.cost);
        VodafoneTextView itemSubtext = (VodafoneTextView) convertView.findViewById(R.id.item_subtext);
        View customDivider = convertView.findViewById(R.id.custom_divider);

        itemName.setPadding(ScreenMeasure.dpToPx(21), 0, 0, 0);
        setDividerMargins(customDivider, false);

        itemArrow = (ImageView) convertView.findViewById(R.id.item_arrow);
        if (child.get_tabLinkListner() != null) {
            itemArrow.setVisibility(View.VISIBLE);
            convertView.setOnClickListener(child.get_tabLinkListner());
        }
        if (child.get_itemSubtext() == null || child.get_itemSubtext().equals("")) {
            itemSubtext.setVisibility(View.GONE);
        } else {
            itemSubtext.setText(child.get_itemSubtext());
            itemSubtext.setPadding(ScreenMeasure.dpToPx(21), 0, 0, 0);
        }

        itemName.setText(child.get_itemName());
        cost.setText(child.get_itemParameter() + "p");


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void setDividerMargins(View v, boolean noMargins) {
        if (!noMargins) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(1));
            params.setMarginStart(ScreenMeasure.dpToPx(14));
            params.setMarginEnd(ScreenMeasure.dpToPx(14));
            v.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(1));
            params.setMarginStart(ScreenMeasure.dpToPx(0));
            params.setMarginEnd(ScreenMeasure.dpToPx(0));
            v.setLayoutParams(params);
        }

    }


}
