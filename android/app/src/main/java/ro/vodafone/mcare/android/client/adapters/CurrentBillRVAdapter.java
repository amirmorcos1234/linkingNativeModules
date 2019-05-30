package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.costControl.currentBill.CurrentBillCard;
import ro.vodafone.mcare.android.client.model.billing.CurrentBillAdapterModel;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceNavigationLayoutFactory;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.CostControlCurrentBillFragment;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Bivol Pavel on 14.04.2017.
 */

public class CurrentBillRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<String> msisdnList;
    private HashMap<String,CurrentBillAdapterModel> adapterModelHashMap = new HashMap<>();

    InterfaceNavigationLayoutFactory navigationHeaderInterface;
    final int HEADER_TYPE = 0;
    final int LIST_TYPE = 1;
    final int EMPTY_LIST_TYPE = 2;

    public CurrentBillRVAdapter(InterfaceNavigationLayoutFactory navigationHeaderInterface, Context context, List<String> currentBillCardList) {
        mContext = context;
        this.msisdnList = currentBillCardList;
        this.navigationHeaderInterface = navigationHeaderInterface;
    }

    @Override
    public int getItemViewType(int position) {

        switch (position) {
            case 0:
                return HEADER_TYPE;
            case 1:
                return EMPTY_LIST_TYPE;
            case 2:
                if (isListShown())
                    return LIST_TYPE;
                if (msisdnList == null)
                    return EMPTY_LIST_TYPE;
            default:
                return LIST_TYPE;

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new BasicViewHolder(navigationHeaderInterface.getNavigationHeader());
            case EMPTY_LIST_TYPE:{
                View view = new View(mContext);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(50)));
                view.setBackgroundResource(R.color.general_background_light_gray);
                return new BasicViewHolder(view);
            }
            default: {

                CurrentBillCard cardView = new CurrentBillCard(mContext);
                cardView.setBackgroundResource(R.color.general_background_light_gray);

                CostControlCurrentBillFragment.CostControlCurrentBillTrackingEvent event = new CostControlCurrentBillFragment.CostControlCurrentBillTrackingEvent();
                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                journey.event8 = "event8";
                journey.getContextData().put("event8", journey.event8);

                event.defineTrackingProperties(journey);
                VodafoneController.getInstance().getTrackingService().trackCustom(event);

                return new CurrentBillViewHolder(cardView);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CurrentBillViewHolder) {
            String formattedMsisdn = msisdnList.get(getListPosition(position));
            if (formattedMsisdn.startsWith("4"))
                formattedMsisdn = msisdnList.get(getListPosition(position)).substring(1);

            CurrentBillAdapterModel currentBillAdapterModel;
            if(adapterModelHashMap.containsKey(formattedMsisdn)){
                currentBillAdapterModel = adapterModelHashMap.get(formattedMsisdn);
            }else{
                currentBillAdapterModel = new CurrentBillAdapterModel(formattedMsisdn);
                adapterModelHashMap.put(formattedMsisdn,currentBillAdapterModel);
            }
            ((CurrentBillViewHolder) holder).setupData(currentBillAdapterModel);

        }
    }

    private int getListPosition(int position) {
        return position - getHeaderViewsCount();
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + msisdnList.size();
    }

    private int getHeaderViewsCount() {
        return 2;
    }

    public boolean isListShown() {
        return true;
    }


    public static class CurrentBillViewHolder extends RecyclerView.ViewHolder {

        VodafoneTextView msisdnTV;

        CurrentBillViewHolder(CurrentBillCard itemView) {
            super(itemView);
            this.msisdnTV = (VodafoneTextView) itemView.findViewById(R.id.msisdn);
        }
        void setupData(CurrentBillAdapterModel currentBillAdapterModel){
            msisdnTV.setText(currentBillAdapterModel.getMsisdn());
            ((CurrentBillCard)itemView).setCurrentBillAdapterModel(currentBillAdapterModel);
        }
    }
}
