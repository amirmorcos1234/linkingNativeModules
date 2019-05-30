package ro.vodafone.mcare.android.card.billHistory;


import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.TransactionFault;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.BillingOverviewFragment;

/**
 * Created by user on 11.04.2017.
 */

public class BillHistoryController implements BaseCardControllerInterface {

    public static String TAG = "BillHistoryController";

    BillHistoryViewGroup viewGroup;

    private static BillHistoryController instance;

    public synchronized static BillHistoryController getInstance() {
        if (instance == null) {
            instance = new BillHistoryController();
        }
        return instance;
    }

    public BillHistoryController setup(BillHistoryViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        return this;
    }

    public void requestData(){
        viewGroup.displayLoadingCard();
        final BillingOverviewFragment fragment = (BillingOverviewFragment)(VodafoneController.findFragment(BillingOverviewFragment.class));
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
                    buildCardList((BillHistorySuccess) value);
                }

                if(value instanceof TransactionFault){
                    if(((TransactionFault)value).getFaultCode().equals("EC05501")){
                        viewGroup.hideCard();
                    }else{
                        onRequestFailed();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestFailed() {
        viewGroup.displayErrorCard();
    }

    private void buildCardList(BillHistorySuccess billHistorySuccess){

        List<BillHistoryCard> cardList = new ArrayList<>();

        for(int i = 0; i < billHistorySuccess.getBillHistoryList().size(); i++){

            boolean isOlderThanLast3Months;

            if(i < 3){
                isOlderThanLast3Months = false;
            }else{
                isOlderThanLast3Months = true;
            }

            BillHistoryCard billHistoryCard = new BillHistoryCard(viewGroup.getContext()).builCard(billHistorySuccess.getBillHistoryList().get(i), isOlderThanLast3Months);
            if(i==billHistorySuccess.getBillHistoryList().size()-1){
                cardList.add(billHistoryCard.displayLastCard());
            }else{
                cardList.add(billHistoryCard);
            }
        }
        viewGroup.atachCards(cardList);
    }
}
