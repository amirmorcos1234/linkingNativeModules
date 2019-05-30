package ro.vodafone.mcare.android.card.loyaltyPoints.segmentCard;

import android.view.View;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyOptInStateFragment;

/**
 * Created by User on 21.04.2017.
 */

public class CustomerSegmentCardController implements BaseCardControllerInterface {
    private CustomerSegmentCard mCard;

    public static CustomerSegmentCardController mInstance;

    public synchronized static CustomerSegmentCardController getInstance(){
        if(mInstance == null){
            mInstance = new CustomerSegmentCardController();
        }
        return mInstance;
    }

    public CustomerSegmentCardController setup(CustomerSegmentCard customerSegmentCard){
        this.mCard = customerSegmentCard;
        return this;
    }

    public void setupCustomerSegmentData(ShopLoyaltyProgramSuccess loyaltyProgramSuccess){
        String segment = loyaltyProgramSuccess.getLpsSegment();
        if(segment != null){
            mCard.buildCard(segment);
        } else {
            mCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDataLoaded(Object... args) {
        for(Object value : args){
            if(value!=null && value instanceof ShopLoyaltyProgramSuccess){
                setupCustomerSegmentData((ShopLoyaltyProgramSuccess) value);
            }
        }
    }

    @Override
    public void onRequestFailed() {

    }

    public void requestData(){
        LoyaltyOptInStateFragment optInStateFragment = (LoyaltyOptInStateFragment) VodafoneController.findFragment(LoyaltyOptInStateFragment.class);
        if(optInStateFragment != null)
            optInStateFragment.requestData(this);
    }
}
