package ro.vodafone.mcare.android.card.costControl.billingOverview;

import java.util.ArrayList;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 07.04.2017.
 */

public class BillingCostControlCardController{
    public static String TAG = "CCCardController";

    private BillingCostControlCard mCard;

    private static BillingCostControlCardController instance;

    private BillingCostControlCardController() {
    }

    public BillingCostControlCardController setup(BillingCostControlCard billingCostControlCard) {
        this.mCard = billingCostControlCard;
        return this;
    }

    public void requestData(){
        mCard.showLoading();

        getCostControl();
    }

    private void getCostControl(){

        ArrayList<String> msisdnList = new ArrayList<String>();
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn();
        msisdnList.add(msisdn);

        UserDataService userDataService = new UserDataService(mCard.getContext());

        userDataService.getAdditionalCostForOtherMsidns(msisdnList).subscribe(new RequestSessionObserver<ArrayList<AdditionalCost>>() {
            @Override
            public void onNext(ArrayList<AdditionalCost> additionalCosts) {
                if(mCard==null){
                    return;
                }
                if(additionalCosts != null && !additionalCosts.isEmpty()){
                    mCard.buildCard(additionalCosts.get(0));
                }else{
                    mCard.showError(null);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(mCard==null){
                    return;
                }
                mCard.showError(null);
            }
        });
    }

    public synchronized static BillingCostControlCardController getInstance() {
        if (instance == null) {
            instance = new BillingCostControlCardController();
        }
        return instance;
    }

    public BillingCostControlCard getCard(){
        return mCard;
    }

    public boolean shouldDisplayCurrentNumber(){
        return EbuMigratedIdentityController.isUserVerifiedEbuMigrated();
    }
}
