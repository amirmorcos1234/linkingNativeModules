package ro.vodafone.mcare.android.card.billSummary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail;

import static ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail.KEY_OF_DETAIL_KEY;
import static ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail.LABEL_OF_DETAIL_KEY;
import static ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail.PRIORITY_KEY;
import static ro.vodafone.mcare.android.client.model.realm.billHistory.HistoryDetail.VALUE_OF_DETAIL_KEY;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

public class BillSummaryDataController {

    public static String TAG = "BSummaryDataController";

    BillSummaryDataViewGroup viewGroup;

    private static BillSummaryDataController instance;

    public synchronized static BillSummaryDataController getInstance() {
        if (instance == null) {
            instance = new BillSummaryDataController();
        }
        return instance;
    }

    public BillSummaryDataController setup(BillSummaryDataViewGroup viewGroup) {
        Log.d(TAG, "setup()");
        this.viewGroup = viewGroup;
        return this;
    }

    public List<HistoryDetail> getHistoryDetailsForDisplay(BillHistoryDetails billHistoryDetails) {
        if (billHistoryDetails.getHistoryDetails() == null || !billHistoryDetails.getHistoryDetails().isValid()) {
            return new ArrayList<>();//TODO throw exception ?
        }

        Log.d(TAG, "date " + billHistoryDetails.getBillClosedDate());
        return billHistoryDetails.getHistoryDetails()
                .where().isNotNull(KEY_OF_DETAIL_KEY)
                .isNotNull(VALUE_OF_DETAIL_KEY)
                .isNotNull(LABEL_OF_DETAIL_KEY)
                .findAll()
                .sort(PRIORITY_KEY);
    }
}
