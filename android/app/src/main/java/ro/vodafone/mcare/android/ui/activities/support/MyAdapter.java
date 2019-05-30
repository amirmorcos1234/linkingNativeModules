package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by bogdan marica on 4/2/2017.
 */

public class MyAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<JsonItem> objects;

    MyAdapter(Context context, List<JsonItem> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        JsonItem item = (JsonItem) getGroup(groupPosition);

        final RotateAnimation animRotateUp = new RotateAnimation(0.0f, 180.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateUp.setFillAfter(true);
        animRotateUp.setDuration(120);

        final RotateAnimation animRotateDown = new RotateAnimation(0.0f, 0.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);

        final RotateAnimation animRotateRight = new RotateAnimation(0.0f, -90.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateRight.setFillAfter(true);
        animRotateRight.setDuration(120);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.chat_option_card, null);
        }

        DynamicColorImageView currentarrow = (DynamicColorImageView) convertView.findViewById(R.id.expandButton);
        VodafoneTextView title = (VodafoneTextView) convertView.findViewById(R.id.optionName);
        title.setText(item.getTitle());

        if (getChildrenCount(groupPosition) == 0) {
            currentarrow.setAnimation(animRotateRight);
        } else if (isExpanded) {
            currentarrow.setAnimation(animRotateUp);
        } else {
            currentarrow.setAnimation(animRotateDown);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        JsonItem item = ((JsonItem) getChild(groupPosition, childPosition));

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_option_content, parent, false);

        if (item != null)
            ((VodafoneTextView) convertView.findViewById(R.id.optionTitle)).setText((item).getContent());

        return convertView;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return this.objects.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return (objects.get(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return this.objects.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

}
