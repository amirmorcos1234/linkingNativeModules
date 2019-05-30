package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DevicesList;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class ActiveDevicesAdapter extends BaseExpandableListAdapter implements android.widget.ExpandableListAdapter {
    private List<DeviceFamiliesList> mDevicesFamiliesList;
    private Context mContext;

    public ActiveDevicesAdapter(Context context, List<DeviceFamiliesList> devicesFamiliesList) {
        mContext = context;
        mDevicesFamiliesList = devicesFamiliesList;
    }

    @Override
    public int getGroupCount() {
        return mDevicesFamiliesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getDevices().size();
    }

    @Override
    public DeviceFamiliesList getGroup(int groupPosition) {
        return mDevicesFamiliesList.get(groupPosition);
    }

    @Override
    public DevicesList getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getDevices().get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.active_devices_category_item, parent, false);
        TextView categoryTitle = convertView.findViewById(R.id.category_item_title);
        TextView categoryDescription = convertView.findViewById(R.id.category_item_description);
        DeviceFamiliesList groupObject = getGroup(groupPosition);
        if (groupObject.getName() != null && !groupObject.getName().isEmpty())
            categoryTitle.setText(groupObject.getName());

       /* if (groupObject.getDeviceLimit() != null) {
            String categoryDescriptionLabel = String.valueOf(groupObject.getDeviceLimit() - getChildrenCount(groupPosition))
                    + " " + VodafoneTvLabels.getVodafoneTvAvailableDevicesForActivation();
            categoryDescription.setText(categoryDescriptionLabel);
            categoryDescription.setVisibility(View.VISIBLE);
        }*/

        String categorySubtitleLabel = VodafoneTvLabels.getVodafoneTvCategorySubtitle();
        if(!categorySubtitleLabel.isEmpty()) {
            categoryDescription.setText(categorySubtitleLabel);
            categoryDescription.setVisibility(View.VISIBLE);
        }



        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        DevicesList device = getChild(groupPosition, childPosition);
        DeviceFamiliesList deviceFamiliesList = getGroup(groupPosition);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View buttonView = inflater.inflate(R.layout.selection_page_button, parent, false);
        View buttonLine = buttonView.findViewById(R.id.button_line);
        VodafoneTextView buttonName = buttonView.findViewById(R.id.button_name);
        VodafoneTextView buttonDescription = buttonView.findViewById(R.id.button_description);
        ImageView buttonIcon = buttonView.findViewById(R.id.button_icon);

        buttonName.setTypeface(Fonts.getVodafoneRGBD());

        if (device.getName() != null) {
            buttonName.setText(device.getName());
        }
        if (deviceFamiliesList.getIcon() != null && !deviceFamiliesList.getIcon().isEmpty()) {
            Glide.with(mContext).load(deviceFamiliesList.getIcon()).into(buttonIcon);
            buttonIcon.setVisibility(View.VISIBLE);
        }
        buttonDescription.setText( String.format(VodafoneTvLabels.getVodafoneTvDevicesAddedLabel(), formatTheDate(device.getActivatedOn()), getTheHour(device.getActivatedOn())));
        buttonDescription.setVisibility(View.VISIBLE);
        buttonLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.product_card_primary_dark_blue));

        return buttonView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private String formatTheDate(Long timestamp) {
        Date activationDate = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
        return dateFormat.format(activationDate);
    }

    private String getTheHour(Long timestamp) {
        Date activationHour = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", new Locale("RO", "RO"));
        return dateFormat.format(activationHour);
    }
}
