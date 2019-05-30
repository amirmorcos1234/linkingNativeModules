package ro.vodafone.mcare.android.ui.views.vodafoneSpinner;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;

/**
 * Created by Bivol Pavel on 02/27/2017.
 */


public class FavoriteNumbersSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private int mSelectedIndex;
    private int mBackgroundSelector;
    private final List<FavoriteNumber> mItems;
    private VodafoneDialog vodafoneDialog = null;
    VodafoneSpinner spinner;

    public FavoriteNumbersSpinnerAdapter(Context context, List<FavoriteNumber> items, int backgroundSelector, VodafoneSpinner spinner) {
        mContext = context;
        mBackgroundSelector = backgroundSelector;
        mItems = items;
        this.spinner = spinner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView favoriteNumber;
        RelativeLayout container;
        LinearLayout removeFavoriteNumberButton;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.top_up_favorite_number_item, null);

            favoriteNumber = (TextView) convertView.findViewById(R.id.favorite_number_item);
            container = (RelativeLayout) convertView.findViewById(R.id.favorite_number_container_item);
            removeFavoriteNumberButton = (LinearLayout) convertView.findViewById(R.id.remove_favorite_number_button);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                container.setBackground(ContextCompat.getDrawable(mContext, mBackgroundSelector));
            }

            convertView.setTag(new ViewHolder(favoriteNumber, container, removeFavoriteNumberButton));
        } else {
            favoriteNumber = ((ViewHolder) convertView.getTag()).textView;
            container = ((ViewHolder) convertView.getTag()).container;
            removeFavoriteNumberButton = ((ViewHolder) convertView.getTag()).removeFavoriteNumberButton;
        }

        favoriteNumber.setText("(" + getItem(position).getNickname() + ")" + getItem(position).getPrepaidMsisdn());

        removeFavoriteNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vodafoneDialog = new VodafoneDialog((Activity)mContext, TopUPLabels.getTop_up_delete_favorite_number_popup_message())
                        .setPositiveMessage(TopUPLabels.getTop_up_popup_positive_message())
                        .setNegativeMessage(TopUPLabels.getTop_up_popup_negative_message())
                        .setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{
                                    ((Callback) FragmentUtils.getVisibleFragment((AppCompatActivity)mContext, false))
                                            .deleteFavoriteNumeber(position);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                spinner.dismissDropDown();
                                vodafoneDialog.dismiss();
                            }
                        })
                        .setNegativeAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vodafoneDialog.dismiss();
                            }
                        });
                vodafoneDialog.setCancelable(true);
                vodafoneDialog.show();
            }
        });

        return convertView;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public FavoriteNumber getItem(int position) {
        return mItems.get(position);

    }



    public FavoriteNumber getItemInDataset(int position) {
        return mItems.get(position);
    }

    protected static class ViewHolder {

        public TextView textView;
        RelativeLayout container;
        LinearLayout removeFavoriteNumberButton;

        public ViewHolder(TextView textView, RelativeLayout container, LinearLayout removeFavoriteNumberButton) {
            this.textView = textView;
            this.container = container;
            this.removeFavoriteNumberButton = removeFavoriteNumberButton;
        }
    }

    public interface Callback{
        public void deleteFavoriteNumeber(int position);
    }
}
