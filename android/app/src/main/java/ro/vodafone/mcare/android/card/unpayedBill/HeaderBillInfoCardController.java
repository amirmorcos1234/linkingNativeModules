package ro.vodafone.mcare.android.card.unpayedBill;


import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;

/**
 * Created by user on 11.04.2017.
 */

public class HeaderBillInfoCardController implements BaseCardControllerInterface {

    public static String TAG = "HeaderBillInfoCC";

    private HeaderBillInfoCard mCard;

    private static HeaderBillInfoCardController instance;

    private BillHistoryDetails billHistoryDetails;

    public synchronized static HeaderBillInfoCardController getInstance() {
        if (instance == null) {
            instance = new HeaderBillInfoCardController();
        }
        return instance;
    }

    public HeaderBillInfoCard getmCard() {
        return mCard;
    }

    public HeaderBillInfoCardController setup(HeaderBillInfoCard card) {
        this.mCard = card;
        return this;
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value != null) {

                if (value instanceof BillHistoryDetails) {
                    billHistoryDetails = (BillHistoryDetails) value;
                }
            }
        }
        if (billHistoryDetails != null) {
            mCard.setAttributes(billHistoryDetails.getTotalAmountDue(),
                    billHistoryDetails.getBillClosedDate());
        } else {
            onRequestFailed();
        }
    }

    @Override
    public void onRequestFailed() {
        mCard.showError(true);
    }


    private static class BillDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "previous bills details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "previous bills details");


            s.channel = "billing overview";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "last bill overview";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "query";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "previous bills details";
            s.getContextData().put("prop21", s.prop21);
        }
    }

}
