package ro.vodafone.mcare.android.ui.views.viewholders.billing;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import ro.vodafone.mcare.android.card.billSummary.termTotAmount.TermTotAmountCard;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

/**
 * Created by Victor Radulescu on 2/23/2018.
 */

public class TermTotAmountViewHolder extends DynamicViewHolder<HistoryDetail, TermTotAmountCard> {
    private Long billClosedDate = null;

    public TermTotAmountViewHolder(@NonNull View itemView, Long billClosedDate) {
        super(itemView);
        this.billClosedDate = billClosedDate;

    }

    @Override
    public void setupWithData(Activity activity, final HistoryDetail historyDetail) {
        getView().setData(historyDetail.getLabelOfDetail(), "(TVA inclus)", historyDetail.getValueTextOfDetail(), billClosedDate)
                .requestBillSummaryTermData();
    }
}
