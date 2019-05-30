package ro.vodafone.mcare.android.ui.views.vodafoneSpinner;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ro.vodafone.mcare.android.R;

/**
 * Created by iSmTick on 3/2/2017.
 */

public class VodafoneSpinnerAdapter<T> extends BaseAdapter implements VodafoneSpinner.Callback {
    protected Context frContext;
    protected List<T> itemsList;
    protected int mSelectedIndex;
    protected int bgSelector;

    public VodafoneSpinnerAdapter(Context context, List<T> items, int bgSelector){
        frContext = context;
        itemsList = items;
        this.bgSelector = bgSelector;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public T getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        LinearLayout linearLayout;


        if(convertView == null) {
            convertView = View.inflate(frContext, R.layout.spinner_item, null);

            textView = (TextView) convertView.findViewById(R.id.spinner_recurrent_recharge);
            linearLayout = (LinearLayout) convertView.findViewById(R.id.spinner_item_container);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout.setBackground(ContextCompat.getDrawable(frContext, bgSelector));
            }

            convertView.setTag(new SpinnerViewHolder(textView, linearLayout));
        } else {
            textView = ((SpinnerViewHolder) convertView.getTag()).textViewH;
            linearLayout = ((SpinnerViewHolder) convertView.getTag()).container;
        }


        textView.setText(getItem(position).toString());

        return convertView;
    }

    public T getItemInDataset(int position){
        return itemsList.get(position);
    }


    @Override
    public void selectSpinnerElement(Object selectedValue) {

    }


    protected static class SpinnerViewHolder{
        public TextView textViewH;
        LinearLayout container;

        public SpinnerViewHolder(TextView textView, LinearLayout container){
            this.textViewH = textView;
            this.container = container;
        }
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
