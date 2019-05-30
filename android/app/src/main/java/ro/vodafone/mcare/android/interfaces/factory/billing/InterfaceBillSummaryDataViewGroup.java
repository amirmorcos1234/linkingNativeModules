package ro.vodafone.mcare.android.interfaces.factory.billing;

import ro.vodafone.mcare.android.card.billSummary.BillSummaryDataViewGroup;

/**
 * Created by Victor Radulescu on 2/6/2018.
 */

public interface InterfaceBillSummaryDataViewGroup {

    BillSummaryDataViewGroup getBillSummaryDataViewGroup();

    void displayBillSummaryDataViewGroup();

    void displayErrorBillSummaryDataViewGroup();
}
