package ro.vodafone.mcare.android.card.pricePlan;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;

/**
 * Created by Bivol Pavel on 28.03.2017.
 */
public class PricePlanCardController implements BaseCardControllerInterface{

    public static String TAG = "PricePlanCardController";

    private PricePlanCard mCard;

    private List<BalanceShowAndNotShown> balance;

    private static PricePlanCardController instance;
    private ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;
    private CostControl costControl;

    public PricePlanCardController(Context context) {
        mCard = new PricePlanCard(context, this);
    }

    public PricePlanCardController setup(PricePlanCard pricePlanCard) {
        this.mCard = pricePlanCard;
        return this;
    }

    public void requestData(){

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            mCard.setVisibility(View.GONE);
            return;
        }

        if(!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            mCard.showLoading(true);
        }

        if(VodafoneController.findFragment(YourServicesFragment.class) != null){
            ((YourServicesFragment) VodafoneController.findFragment(YourServicesFragment.class)).requestData();
        }
    }

    public PricePlanCard getCard() {
        return mCard;
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value instanceof ActiveOffersPostpaidSuccess) {
                activeOffersPostpaidSuccess = (ActiveOffersPostpaidSuccess) value;
            }
            if(value instanceof CostControl){
                costControl = (CostControl)value;
            }
            if(value instanceof ActiveOffersPostpaidEbuSuccess){
                activeOffersPostpaidEbuSuccess = (ActiveOffersPostpaidEbuSuccess) value;
            }
        }

        if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            if(activeOffersPostpaidSuccess == null){
                mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
            } else {
                mCard.buildCBUPricePlanCard(activeOffersPostpaidSuccess,
                        setupPricePlanData(costControl),
                        costControl != null ? costControl.getAdditionalCost():null);
            }
        }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            if(activeOffersPostpaidEbuSuccess == null && costControl == null){
                mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
            } else {
                mCard.buildEBUPricePlanCard(activeOffersPostpaidEbuSuccess, setupPricePlanData(costControl),
                        costControl != null ? costControl.getAdditionalCost() : null);
            }
        }
    }

    @Override
    public void onRequestFailed() {
        mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
    }



    private List<BalanceShowAndNotShown> setupPricePlanData(CostControl costControl) {
        if (costControl != null) {
            Log.d(TAG, "setupPricePlanData: ");
            balance = costControl.getCurrentExtraoptions().getExtendedBalanceList();
        } else{
            Log.d(TAG, "setupDataCBU: balance " + balance);
            //mCard.showError(true);
            return null;
        }
        return balance;
    }
}

