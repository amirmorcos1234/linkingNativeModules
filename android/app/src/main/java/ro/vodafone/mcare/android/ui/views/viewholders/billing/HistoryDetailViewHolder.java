package ro.vodafone.mcare.android.ui.views.viewholders.billing;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import ro.vodafone.mcare.android.card.billSummary.BillSummaryDataCard;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

/**
 * Created by Victor Radulescu on 2/23/2018.
 */

public class HistoryDetailViewHolder extends DynamicViewHolder<HistoryDetail, BillSummaryDataCard> {

    public HistoryDetailViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setupWithData(Activity activity, final HistoryDetail element) {
        getView().setAttributes(element.getLabelOfDetail(), null, element.getValueTextOfDetail(), false);
        getView().invalidate();
    }

}
