package ro.vodafone.mcare.android.card.totalPayment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.BillingOverviewFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Bivol Pavel on 10.04.2017.
 */

public class TotalPaymentCard extends VodafoneAbstractCard{

    public static String TAG = "TotalPaymentCard";

    @BindView(R.id.current_date)
    VodafoneTextView currentDate;

    @BindView(R.id.cost)
    VodafoneTextView cost;

    @BindView(R.id.bill_due_date)
    VodafoneTextView billDueDate;

    @BindView(R.id.is_passed_due_date_message)
    VodafoneTextView isPassedDueDateMessage;

    @BindView(R.id.is_passed_due_date_layout)
    LinearLayout isPassedDueDateLayout;

    @BindView(R.id.pay_bill_button)
    VodafoneButton payBillButton;

    InvoiceDetailsSuccess invoiceDetailsSuccess;

    public TotalPaymentCard(Context context) {
        super(context);
        init(null);
    }

    public TotalPaymentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TotalPaymentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);
        setupController();
    }

    public void onDataLoaded(InvoiceDetailsSuccess invoiceDetailsSuccess){
        Log.d(TAG, "onDataLoaded()");
        this.invoiceDetailsSuccess = invoiceDetailsSuccess;

        setAttributes();
    }

    public void setAttributes(){
        Log.d(TAG, "buildCBUPricePlanCard()");
        if(invoiceDetailsSuccess.getInvoiceAmount() != null && Float.valueOf(invoiceDetailsSuccess.getInvoiceAmount()) > 0){
            showCard();

            payBillButton.setOnClickListener(payBillButtonClickListner);

            currentDate.setText(BillingOverviewLabels.getBilling_overview_current_date() + WordUtils.capitalize(DateUtils.getDate(String.valueOf(new Date().getTime()),
                    new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO")))));
            billDueDate.setText(BillingOverviewLabels.getBilling_overview_bill_due_date() + WordUtils.capitalize(DateUtils.getDate(invoiceDetailsSuccess.getIssueDate(),
                    new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO")))));
            cost.setText(NumbersUtils.twoDigitsAfterDecimal(new Float(invoiceDetailsSuccess.getInvoiceAmount())) + " "+ BillingOverviewLabels.getBilling_overview_ron_unit());

            if(isPassedLastBillCycleDate() && isPoziviteBalance()){
                displayIsPassedDueDateLayout();
            }

        }else{
            hideCard();
        }
    }

    public void hideCard(){
        Log.d(TAG, "hideCard()");
        if(this.getVisibility() == VISIBLE){
            this.setVisibility(GONE);
            ((View)this.getParent()).findViewById(R.id.total_payment_card_title).setVisibility(GONE);
            ((View)this.getParent()).findViewById(R.id.container).setVisibility(GONE);
        }
    }

    public void showCard(){
        Log.d(TAG, "showCard()");
        if(this.getVisibility() != VISIBLE){
            this.setVisibility(VISIBLE);
            ((View)this.getParent()).findViewById(R.id.total_payment_card_title).setVisibility(VISIBLE);
            ((View)this.getParent()).findViewById(R.id.container).setVisibility(VISIBLE);
            ((View)this.getParent()).findViewById(R.id.no_bill_container).setVisibility(GONE);

            final BillingOverviewFragment fragment = (BillingOverviewFragment) VodafoneController.findFragment(BillingOverviewFragment.class);

            if(fragment != null)
                fragment.setCardProfile();
        }
    }

    private void setupController(){
        TotalPaymentCardController.getInstance().setup(this).requestData();
    }

    private boolean isPassedLastBillCycleDate(){
        Log.d(TAG, "isPassedLastBillCycleDate()");
        boolean isPassed = false;

        Calendar currentDay;
        Calendar lastDueDate;

        lastDueDate = Calendar.getInstance();
        lastDueDate.setTime(new Date(Long.parseLong(invoiceDetailsSuccess.getIssueDate())));

        currentDay = Calendar.getInstance();
        currentDay.setTime(new Date());

        if(currentDay.compareTo(lastDueDate) == 1){
            isPassed = true;
        }
        Log.d(TAG, "isPassedLastBillCycleDate() return " +isPassed);
        return isPassed;
    }

    private boolean isPoziviteBalance(){
        Log.d(TAG, "isPoziviteBalance");
        boolean isPositive = false;

        if(invoiceDetailsSuccess.getInvoiceAmount() != null && Float.valueOf(invoiceDetailsSuccess.getInvoiceAmount()) > 0){
            isPositive = true;
        }
        Log.d(TAG, "isPoziviteBalance return "+isPositive);
        return  isPositive;
    }

    public void displayIsPassedDueDateLayout(){
        isPassedDueDateLayout.setVisibility(VISIBLE);
        isPassedDueDateMessage.setText(BillingOverviewLabels.getBilling_overview_is_passed_due_date_message());
    }

    @Override
    protected int setContent() {
        return R.layout.card_total_payment;
    }

    /*
    Click Listners
    */
    OnClickListener payBillButtonClickListner = new OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentActionName i=IntentActionName.PAY_BILL;
            i.setExtraParameter("pay_own_bill");
            new NavigationAction(getContext()).finishCurrent(true).startAction(i);
        }
    };

}
