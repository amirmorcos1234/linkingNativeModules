package ro.vodafone.mcare.android.widget.TabMenu;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;

/**
 * Created by Bogdan Marica on 7/13/2017.
 */

public class TabAdapter extends ArrayAdapter<String> {

    private boolean isLoyaltyLayout = false;


    public TabAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public TabAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public TabAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }

    public TabAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public TabAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    public TabAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }
    public TabAdapter(Context context, FragmentActivity activity, ArrayList<String> objects) {
        super(context, 0, objects);
    }
    public TabAdapter(Activity activity, ArrayList<String> objects, boolean isLoyaltyLayout) {
        super(activity, 0, objects);
        this.isLoyaltyLayout = isLoyaltyLayout;
    }

    public TabAdapter(Activity activity, ArrayList<String> objects) {
        super(activity, 0, objects);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String element = getItem(position);
        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tab_content, parent, false);
            if(isLoyaltyLayout) {
                convertView =new TabItem(getContext(), isLoyaltyLayout);
            } else {
                convertView =new TabItem(getContext());
            }
        }

        ((TextView) convertView.findViewById(R.id.tab_namezz)).setText(element);
        ((TextView) convertView.findViewById(R.id.tab_namezz)).setTextColor(ContextCompat.getColor(getContext(),R.color.vodafonegrey));

        return convertView;
    }



}
