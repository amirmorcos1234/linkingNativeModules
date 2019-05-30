package ro.vodafone.mcare.android.card.costControl.currentBill;

import java.util.ArrayList;

import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.interfaces.expandable.ExpandableCostControlInterfaceCard;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserDataService;

/**
 * Created by Bivol Pavel on 13.04.2017.
 */

public class CurrentBillCardController{

    public static String TAG = "BillHistoryController";

    private static CurrentBillCardController instance;

    public synchronized static CurrentBillCardController getInstance() {
        if (instance == null) {
            instance = new CurrentBillCardController();
        }
        return instance;
    }

    public void requestData(final ExpandableCostControlInterfaceCard currentBillCard, final String msisdn){
        currentBillCard.showLoading(true);

        final ArrayList<String> msidnList =  new ArrayList<>();
        msidnList.add(msisdn);
        UserDataService userDataService = new UserDataService(currentBillCard.getContext());

        userDataService.getAdditionalCostForOtherMsidns(msidnList).subscribe(new RequestSessionObserver<ArrayList<AdditionalCost>>() {
            @Override
            public void onNext(ArrayList<AdditionalCost> additionalCosts) {
                if(additionalCosts != null && !additionalCosts.isEmpty()){
                    currentBillCard.onDataLoaded(additionalCosts.get(0));
                }else{
                    currentBillCard.showError(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                currentBillCard.showError(true);
            }
        });
    }
}
