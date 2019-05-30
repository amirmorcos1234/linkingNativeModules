package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.ion.IonModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public class IonUnlimitedAdapter extends RecyclerView.Adapter<IonUnlimitedAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private final LayoutInflater mInflater;
    private OnItemClickListener listener;
    private List<IonModel> msisdnList;

    public IonUnlimitedAdapter(Context mContext, Activity mActivity, List<IonModel> msisdnList, OnItemClickListener listener) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.msisdnList = msisdnList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.ion_msisdn_item, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msisdnCheckbox.setImageResource(msisdnList.get(position).isSelected()
                ? R.drawable.selector_checked_image
                : R.drawable.selector_unchecked_image);
        holder.msisdnItem.setText(msisdnList.get(position).getMsisdn());
        if (!TextUtils.isEmpty(msisdnList.get(position).getCountry())) {
            holder.msisdnCountry.setText("Număr de " + msisdnList.get(position).getCountry());
            holder.msisdnCountry.setVisibility(View.VISIBLE);
        }
        holder.bind(position, msisdnList.get(position), msisdnList, listener);
    }

    @Override
    public int getItemCount() {
        return msisdnList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView msisdnCheckbox;
        public VodafoneTextView msisdnItem;
        public VodafoneTextView msisdnCountry;
        private IonUnlimitedAdapter adapter;

        public ViewHolder(View itemView, IonUnlimitedAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            msisdnCheckbox = itemView.findViewById(R.id.msisdn_checkbox);
            msisdnItem = itemView.findViewById(R.id.msisdn_item);
            msisdnCountry = itemView.findViewById(R.id.msisdn_country);
        }

        public void bind(final int position, final IonModel item, final List<IonModel> msisdnList, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (IonModel ionModel : msisdnList)
                        ionModel.setSelected(false);
                    listener.onItemClick(position, item);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, IonModel item);
    }

}
