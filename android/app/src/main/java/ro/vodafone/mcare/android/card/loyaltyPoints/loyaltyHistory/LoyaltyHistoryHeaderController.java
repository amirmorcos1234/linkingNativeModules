package ro.vodafone.mcare.android.card.loyaltyPoints.loyaltyHistory;

import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;

/**
 * Created by User on 25.04.2017.
 */

public class LoyaltyHistoryHeaderController implements BaseCardControllerInterface {
    LoyaltyHistoryHeaderCard mCard;
    String points;
    String date;

    private static LoyaltyHistoryHeaderController instance;

    public synchronized static LoyaltyHistoryHeaderController getInstance() {
        if (instance == null) {
            instance = new LoyaltyHistoryHeaderController();
        }
        return instance;
    }

    public LoyaltyHistoryHeaderCard getmCard() {
        return mCard;
    }

    public LoyaltyHistoryHeaderController setup(LoyaltyHistoryHeaderCard card) {
        this.mCard = card;
        return this;
    }

    private void setupCardData(ShopLoyaltyProgramSuccess programSuccess) {
        points = programSuccess.getAccountBalance();
        date = programSuccess.getLastUpdate();
        mCard.buildCard(points, date);
    }


    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value != null && value instanceof ShopLoyaltyProgramSuccess) {
                setupCardData((ShopLoyaltyProgramSuccess) value);
            }
        }
    }

    @Override
    public void onRequestFailed() {

    }
}
