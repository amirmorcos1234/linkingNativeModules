package ro.vodafone.mcare.android.interfaces;

/**
 * Created by Bivol Pavel on 03.02.2017.
 */
public interface PayBillServicesInterface {

    public void getInvoiceDetails(BillingFragmentInterface fragment, String msisdn, String ban, boolean isAnonymousInvoice);

    public void doPaymentBill(String phoneNumber, String amount, String invoiceNo, String accountNo , String email);

}
