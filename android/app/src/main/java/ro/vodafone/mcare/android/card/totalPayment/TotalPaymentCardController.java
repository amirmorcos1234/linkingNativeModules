package ro.vodafone.mcare.android.card.totalPayment;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 10.04.2017.
 */

public class TotalPaymentCardController {

    public static String TAG = "TotalPaymentCardController";
    private static TotalPaymentCardController instance;

    private TotalPaymentCard mCard;
    private String ban;
    private String msisdn;

    private TotalPaymentCardController() {
    }

    public TotalPaymentCardController setup(TotalPaymentCard totalPaymentCard) {
        this.mCard = totalPaymentCard;
        return this;
    }

    public synchronized static TotalPaymentCardController getInstance() {
        if (instance == null) {
            instance = new TotalPaymentCardController();
        }
        return instance;
    }

    public TotalPaymentCard getCard(){
        return mCard;
    }


    public void requestData(){

        BillingServices billingServices = new BillingServices(getCard().getContext());

        final BillingOverviewActivity activity = (BillingOverviewActivity)VodafoneController.findActivity(BillingOverviewActivity.class);
        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        msisdn = VodafoneController.getInstance().getUserProfile() != null ? VodafoneController.getInstance().getUserProfile().getMsisdn() : null;

        billingServices.getInvoiceDetails(msisdn, ban).subscribe(new RequestSessionObserver<GeneralResponse<InvoiceDetailsSuccess>>() {
            @Override
            public void onNext(GeneralResponse<InvoiceDetailsSuccess> invoiceDetailsSuccessGeneralResponse) {
                if(invoiceDetailsSuccessGeneralResponse.getTransactionStatus() == 0 && invoiceDetailsSuccessGeneralResponse.getTransactionSuccess() != null){
                    mCard.onDataLoaded(invoiceDetailsSuccessGeneralResponse.getTransactionSuccess());
                }else{
                    mCard.hideCard();
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mCard.hideCard();
            }
        });
    }

}
