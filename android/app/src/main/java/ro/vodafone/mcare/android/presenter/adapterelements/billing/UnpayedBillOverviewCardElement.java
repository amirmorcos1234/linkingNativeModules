package ro.vodafone.mcare.android.presenter.adapterelements.billing;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.card.billSummary.BillSummaryDataCard;
import ro.vodafone.mcare.android.card.billSummary.subscriptionSection.SubscriptionSectionCard;
import ro.vodafone.mcare.android.card.billSummary.termTotAmount.TermTotAmountCard;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.billing.HistoryDetailViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.billing.SubscriptionBillSummaryViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.billing.TermTotAmountViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

import static ro.vodafone.mcare.android.presenter.adapterelements.billing.UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem.Bill_SUMMARRY_ITEM;
import static ro.vodafone.mcare.android.presenter.adapterelements.billing.UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem.HISTORY_DETAIL_ITEM;
import static ro.vodafone.mcare.android.presenter.adapterelements.billing.UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem.TERM_ToT_AMOUNT_ITEM;

/*
*
 * Created by Victor Radulescu on 2/8/2018.

*/


public class UnpayedBillOverviewCardElement extends DynamicAdapterElement<UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem> {

    private Long billClosedDate;
    private List<BillSummaryItem> billSummaryItems;
    private List<HistoryDetail> historyDetails;
    private final boolean olderThanLast3Months;

    public UnpayedBillOverviewCardElement(int type, int order, Context context, List<BillSummaryItem> billSummaryItems, List<HistoryDetail> historyDetails, Long billClosedDate, boolean olderThanLast3Months) {
        super(type, order, context, createList(billSummaryItems, historyDetails));
        this.billSummaryItems = billSummaryItems;
        this.historyDetails = historyDetails;
        this.billClosedDate = billClosedDate;
        this.olderThanLast3Months = olderThanLast3Months;
    }

    private static List<UnpayedBillingOverViewItem> createList(List<BillSummaryItem> billSummaryItems, List<HistoryDetail> historyDetails) {
        ArrayList<UnpayedBillingOverViewItem> adapterList = new ArrayList<UnpayedBillingOverViewItem>();
        adapterList.addAll(billSummaryItems);
        adapterList.addAll(historyDetails);
        return adapterList;
    }

    @Override
    protected View getView(int viewType) {
        switch (viewType) {
            case Bill_SUMMARRY_ITEM:
                return new SubscriptionSectionCard(getContext());
            case HISTORY_DETAIL_ITEM:
                return new BillSummaryDataCard(getContext());
            case TERM_ToT_AMOUNT_ITEM:
                return new TermTotAmountCard(getContext());
            default:
                return new SubscriptionSectionCard(getContext());
        }
    }

    @Override
    protected View getView() {
        return new View(getContext());
    }

    @Override
    public DynamicViewHolder getViewHolder(int viewType) {
        switch (viewType) {
            case Bill_SUMMARRY_ITEM:
                return new SubscriptionBillSummaryViewHolder(getView(viewType), billClosedDate,olderThanLast3Months);
            case HISTORY_DETAIL_ITEM:
                return new HistoryDetailViewHolder(getView(viewType));
            case TERM_ToT_AMOUNT_ITEM:
                return new TermTotAmountViewHolder(getView(viewType), billClosedDate);
            default:
                return new SubscriptionBillSummaryViewHolder(getView(viewType), billClosedDate, olderThanLast3Months);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return getData(position).getType();
    }

    public List<BillSummaryItem> getBillSummaryItems() {
        return billSummaryItems;
    }

    public void setBillSummaryItems(List<BillSummaryItem> billSummaryItems) {
        this.billSummaryItems = billSummaryItems;
        updateAdapterList();
    }

    private void updateAdapterList() {
        super.setList(createList(billSummaryItems, historyDetails));
    }

    public List<HistoryDetail> getHistoryDetails() {
        return historyDetails;
    }

    public void setHistoryDetails(List<HistoryDetail> historyDetails) {
        this.historyDetails = historyDetails;
    }

    public interface UnpayedBillingOverViewItem {
        int Bill_SUMMARRY_ITEM = 0;
        int HISTORY_DETAIL_ITEM = 1;
        int TERM_ToT_AMOUNT_ITEM = 2;

        int getType();
    }

    public boolean isOlderThanLast3Months() {
        return olderThanLast3Months;
    }

}
