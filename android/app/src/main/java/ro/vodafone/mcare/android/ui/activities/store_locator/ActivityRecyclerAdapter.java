package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.stores.VodafoneJsonShop;
import ro.vodafone.mcare.android.ui.views.layouts.frame.FrameLayoutDispatchParentEvents;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bogdan Marica on 7/13/2017.
 */

class ActivityRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER_TYPE = 101;
    private static final int LOCATION_CARD_TYPE = 102;
    private static final int TAB_CARD_TYPE = 103;
    private static final int MAP_TYPE = 104;
    private static final int STORE_ENTRY_TYPE = 105;
    private static final int EMPTY_LIST_TYPE = 106;

    private StoreLocatorActivity activity;
    private List<VodafoneJsonShop> storeList;

    private boolean mapShown = false;

    ActivityRecyclerAdapter(StoreLocatorActivity activity, List<VodafoneJsonShop> storeList) {
        this.activity = activity;
        this.storeList = storeList;
    }

    private boolean isMapShown() {
        return mapShown;
    }

    void setMapShown(boolean isMapShown) {
        if (this.mapShown != isMapShown) {
            this.mapShown = isMapShown;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (storeList != null)
            return (!isMapShown() ? (storeList.size() + getHeaderItemsCount()) : (getHeaderItemsCount() + 1));
        else
            return (getHeaderItemsCount() + 1);
    }

    @Override
    public int getItemViewType(int position) {

        switch (position) {
            case 0:
                return HEADER_TYPE;
            case 1:
                return LOCATION_CARD_TYPE;
            case 2:
                return TAB_CARD_TYPE;
            case 3:
                if (isMapShown())
                    return MAP_TYPE;
                if (storeList == null)
                    return EMPTY_LIST_TYPE;
            default:
                return STORE_ENTRY_TYPE;

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new EmptyViewHolder(activity.getNavigationView());

            case LOCATION_CARD_TYPE:
                return new EmptyViewHolder(activity.getLocationCardView());

            case TAB_CARD_TYPE:
                return new EmptyViewHolder(activity.getTabCardView());

            case MAP_TYPE:
                FrameLayoutDispatchParentEvents frameLayout = new FrameLayoutDispatchParentEvents(activity);
                frameLayout.addView(activity.getMapCardView());
                return new EmptyViewHolder(frameLayout);
            case EMPTY_LIST_TYPE:
                return new EmptyViewHolder(activity.getEmptyViewFillScreen());

            case STORE_ENTRY_TYPE:
                View storeItemView = View.inflate(activity, R.layout.fragment_store_locator_list_item, null);
                return new StoreLocationViewHolder(storeItemView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder uncastHolder, final int position) {

        if (uncastHolder instanceof StoreLocationViewHolder) {
            final StoreLocationViewHolder storeHolder = (StoreLocationViewHolder) uncastHolder;

            VodafoneTextView store_name = storeHolder.store_name;
            store_name.setText(storeList.get(position - getHeaderItemsCount()).getN());
            store_name.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);

            VodafoneTextView store_street_adress = storeHolder.store_street_adress;
            store_street_adress.setText(storeList.get(position - getHeaderItemsCount()).getA());

            VodafoneTextView store_distance = storeHolder.store_distance;
            if (!activity.getPermissionGranted())
                store_distance.setVisibility(View.INVISIBLE);
            else {
                store_distance.setVisibility(View.VISIBLE);
                store_distance.setText((storeList.get(position - getHeaderItemsCount()).getDistanceToUserInKm() + "km"));
            }

            storeHolder.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    IntentActionName.STORE_LOCATOR_DETAILS.setOneUsageSerializedData("permission=" + activity.getPermissionGranted() + "~~~" + storeList.get(position).toString());
//                    new NavigationAction(activity).startAction(IntentActionName.STORE_LOCATOR_DETAILS);

                    Intent intent = new Intent(activity, StoreLocatorDetailsActivity.class);
                    intent.putExtra("extras", "permission=" + activity.getPermissionGranted() + "~~~" + storeList.get(position - getHeaderItemsCount()).toString());
                    activity.startActivityForResult(intent, 102);
                }
            });
        }
    }

    private int getHeaderItemsCount() {
        return 3;
    }

    void setStoreList(List<VodafoneJsonShop> newStoreList) {
//        D.e("SET STORE LIST");
        this.storeList = newStoreList;
    }

    private class StoreLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        VodafoneTextView store_name;
        VodafoneTextView store_street_adress;
        VodafoneTextView store_distance;
        private View.OnClickListener mListener;

        StoreLocationViewHolder(View itemView) {
            super(itemView);
            store_name = (VodafoneTextView) itemView.findViewById(R.id.store_name);
            store_street_adress = (VodafoneTextView) itemView.findViewById(R.id.store_street_adress);
            store_distance = (VodafoneTextView) itemView.findViewById(R.id.store_distance);

            itemView.setOnClickListener(this);
        }

        void setClickListener(View.OnClickListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view);
        }

    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        View customView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            customView = itemView;
        }
    }
}