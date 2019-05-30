package ro.vodafone.mcare.android.card.billingGraphSection;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.realm.TransactionFault;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistoryDetails;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.BillingOverviewFragment;


/**
 * Created by Bivol Pavel on 28.03.2017.
 */

public class BillingGraphCardController implements BaseCardControllerInterface {

    public static String TAG = "BillingGraphController";

    private static final int LAST_BILL_NUMBER_DISPLAYED = 6;

    private BillingGraphCard mCard;

    private static BillingGraphCardController instance;

    BillHistorySuccess billHistorySuccess;

    DecimalFormat df = new DecimalFormat("#0.00");

       BillingOverviewFragment fragment;

    public synchronized static BillingGraphCardController getInstance() {
        if (instance == null) {
            instance = new BillingGraphCardController();
        }
        return instance;
    }

    public BillingGraphCard getCard(){
        return mCard;
    }

    private BillingGraphCardController() {
    }

    public BillingGraphCardController setup(BillingGraphCard billingGraphCard) {
        this.mCard = billingGraphCard;
        return this;
    }

    public void requestData(){
        mCard.showLoading();
        fragment = (BillingOverviewFragment)VodafoneController.findFragment(BillingOverviewFragment.class);
        if(fragment != null)
            fragment.requestBillHistoryData(this);
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if(value == null){
                onRequestFailed();
            }else{
                if (value instanceof BillHistorySuccess) {
                    this.billHistorySuccess = (BillHistorySuccess) value;
                    if(isEmptyBillHistoryList()){
                        mCard.hideCard();
                    }else{
                        prepareData();
                    }
                }

                if(value instanceof TransactionFault){
                    if(((TransactionFault)value).getFaultCode().equals("EC05501")) {
                        if (fragment != null){
                            fragment.setNoBillContainer();
                        }
                        mCard.hideCard();
                    }else{
                        if (fragment != null){
                            fragment.hiddenNoBillContainer();
                        }
                        onRequestFailed();
                    }
                }
            }
        }
    }

    private boolean isEmptyBillHistoryList(){
        boolean isEmpty = true;
        if(billHistorySuccess.getBillHistoryList() != null && !billHistorySuccess.getBillHistoryList().isEmpty()){
            isEmpty = false;
        }
        return isEmpty;
    }

    private void prepareData(){
        try {
            mCard.buildCard(getSortedBillHistoryList(), calculateTotalPlanCost(), calculateAverageAditionalCost());
        } catch (Exception e) {
            onRequestFailed();
            e.printStackTrace();
        }
    }

    private List<BillHistoryDetails> getSortedBillHistoryList(){

        List<BillHistoryDetails> finalBillHistoryList = new ArrayList<>();
        List<BillHistoryDetails> sertedBillHistoryList = sortBillHistoryList();


        if(sertedBillHistoryList.size() == LAST_BILL_NUMBER_DISPLAYED){
            finalBillHistoryList = sertedBillHistoryList;
        }else{

            int emptyBills = LAST_BILL_NUMBER_DISPLAYED - sertedBillHistoryList.size();

            for(int i = 0 ; i < emptyBills; i++){
                Log.d(TAG,"i ="+i);
                Date netDate;
                if(sertedBillHistoryList.size() != 0){
                    netDate = new Date(sertedBillHistoryList.get(0).getBillClosedDate());
                }else{
                    netDate = new Date();
                }

                Calendar calendar = toCalendar(netDate);

                calendar.add(Calendar.MONTH,-(emptyBills -i));

                BillHistoryDetails bill = new BillHistoryDetails();

                bill.setBillClosedDate(toDate(calendar).getTime());

                finalBillHistoryList.add(bill);
            }
            finalBillHistoryList.addAll(sertedBillHistoryList);
        }

        return finalBillHistoryList;
    }

    private List<BillHistoryDetails> sortBillHistoryList(){

        List<BillHistoryDetails> billHistoryList = new ArrayList<>();

        for(BillHistoryDetails bill : billHistorySuccess.getBillHistoryList()){
            billHistoryList.add(bill);
        }

        Collections.sort(billHistoryList, new Comparator<BillHistoryDetails>()
        {
            @Override
            public int compare(BillHistoryDetails lhs, BillHistoryDetails rhs) {

                return lhs.getBillClosedDate().compareTo(rhs.getBillClosedDate());
            }
        });

        return billHistoryList;
    }

    private Double calculateTotalPlanCost() throws NullPointerException{
        Double totalPriecPlanCost = new Double(0);

        for(BillHistoryDetails bill : billHistorySuccess.getBillHistoryList()){
            totalPriecPlanCost += bill.getTotalMonthlyFee();
        }

        return totalPriecPlanCost/billHistorySuccess.getBillHistoryList().size();
    }

    private Double calculateAverageAditionalCost() throws NullPointerException{

        Double totalAditionalCost = new Double(0);

        for(BillHistoryDetails bill : billHistorySuccess.getBillHistoryList()){
            totalAditionalCost += bill.getGsmUtilizedServices();
        }

        return totalAditionalCost/billHistorySuccess.getBillHistoryList().size();
    }

    @Override
    public void onRequestFailed() {
        mCard.showError(null);
    }

    public static Date toDate(Calendar calendar){
        return calendar.getTime();
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
