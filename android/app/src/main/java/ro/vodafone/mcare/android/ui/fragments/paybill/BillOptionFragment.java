package ro.vodafone.mcare.android.ui.fragments.paybill;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.interfaces.BillingFragmentInterface;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.PayBillActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 26.01.2017.
 */
public class BillOptionFragment extends BaseFragment implements BillingFragmentInterface {
    private static String TAG = "BillOptionFragment";
    private PayBillActivity payBillActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_bill_option, null);

        payBillActivity = (PayBillActivity) getActivity();
        LinearLayout payOwnBillButton = (LinearLayout) v.findViewById(R.id.pay_own_bill_button);
        LinearLayout payAnotherBillButton = (LinearLayout) v.findViewById(R.id.pay_another_bill_button);

        payOwnBillButton.setOnClickListener(clickListener);
        payAnotherBillButton.setOnClickListener(clickListener);

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billing,TealiumConstants.billOptions);

        return v;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pay_own_bill_button: {
                    //Tealium Track Event
                    trackEvent(TealiumConstants.pay_own_bill);

                    payBillActivity.getInvoiceDetails(new PayBillFragment(), null, null, false);
                }
                break;
                case R.id.pay_another_bill_button: {
                    //Tealium Track Event
                    trackEvent(TealiumConstants.pay_anoymous_bill);

                    payBillActivity.getInvoiceDetails(new AnonymousPayBillFragment(), null, null, false);
                } break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        payBillActivity.getNavigationHeader().hideSelectorView();
    }

    @Override
    public Fragment updateFragment(InvoiceDetailsSuccess invoiceDetailsSuccess) {
        return this;
    }

    @Override
    public void populateView() {

    }

    @Override
    public String getAnonymousPhoneNumber() {
        return null;
    }

    @Override
    public String getAnonymousEmailAddress() {
        return null;
    }

    @Override
    public String getAnonymousInvoiceValue() {
        return null;
    }

    @Override
    public void manageAnonymousPaymentErrors(String errorCode) {

    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_PAYBILL);
    }

    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.billOptions);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
}
