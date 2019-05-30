package ro.vodafone.mcare.android.card.loyaltyPoints;

import android.util.Log;

import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.LPSMessage;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyOptInStateFragment;

/**
 * Created by User on 21.04.2017.
 */

public class LoyaltyPointsCardController implements BaseCardControllerInterface {
    private LoyaltyPointsCard mCard;
    public static LoyaltyPointsCardController mInstance;
    private String totalPoints;
    private String lastUpdate;
    private List<LPSMessage> lpsMessages;
    private String loyaltyProgramSegment;

    public synchronized static LoyaltyPointsCardController getInstance(){
        if(mInstance == null){
            mInstance = new LoyaltyPointsCardController();
        }
        return mInstance;
    }

    public LoyaltyPointsCardController setup(LoyaltyPointsCard loyaltyPointsCard){
        this.mCard = loyaltyPointsCard;
        return this;
    }

    public void setupPointsData(ShopLoyaltyProgramSuccess loyaltyProgramSuccess){
        totalPoints = loyaltyProgramSuccess.getAccountBalance();
        lastUpdate = loyaltyProgramSuccess.getLastUpdate();
        lpsMessages = loyaltyProgramSuccess.getLPSMessageList();
        loyaltyProgramSegment = loyaltyProgramSuccess.getLpsSegment();

        mCard.buildCard(totalPoints, lastUpdate, lpsMessages,loyaltyProgramSegment);
    }

    public void requestData(){
        Log.d("", "requestData: ");
        LoyaltyOptInStateFragment optInStateFragment = (LoyaltyOptInStateFragment) VodafoneController.findFragment(LoyaltyOptInStateFragment.class);
        if(optInStateFragment != null)
            optInStateFragment.requestData(this);
    }

    @Override
    public void onDataLoaded(Object... args) {
        for(Object value : args){
            if(value!=null && value instanceof ShopLoyaltyProgramSuccess){
                setupPointsData((ShopLoyaltyProgramSuccess) value);
            }
        }
    }

    @Override
    public void onRequestFailed() {

    }
}
