package ro.vodafone.mcare.android.ui.views.viewholders.billing;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ro.vodafone.mcare.android.card.billSummary.subscriptionSection.SubscriptionSectionCard;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

/**
 * Created by Victor Radulescu on 2/8/2018.
 */

public class SubscriptionBillSummaryViewHolder extends DynamicViewHolder<BillSummaryItem, SubscriptionSectionCard>  {

    private Long billClosedDate = null;
    private boolean olderThanLast3Months;


    public SubscriptionBillSummaryViewHolder(@NonNull View itemView, Long billClosedDate, boolean olderThanLast3Months) {
        super(itemView);
        this.billClosedDate = billClosedDate;
        this.olderThanLast3Months = olderThanLast3Months;
    }

    @Override
    public void setupWithData(Activity activity, final BillSummaryItem element) {
        getView().buildHeaderSection(element ,billClosedDate, olderThanLast3Months);
    }
}
