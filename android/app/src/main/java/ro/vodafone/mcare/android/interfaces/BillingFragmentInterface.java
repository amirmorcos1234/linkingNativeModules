package ro.vodafone.mcare.android.interfaces;

import android.support.v4.app.Fragment;

import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;

/**
 * Created by Bivol Pavel on 03.02.2017.
 */
public interface BillingFragmentInterface {
    public Fragment updateFragment(InvoiceDetailsSuccess invoiceDetailsSuccess);

    public void populateView();

    public String getAnonymousPhoneNumber();

    public String getAnonymousEmailAddress();

    public String getAnonymousInvoiceValue();

    public void manageAnonymousPaymentErrors(String errorCode);

}
