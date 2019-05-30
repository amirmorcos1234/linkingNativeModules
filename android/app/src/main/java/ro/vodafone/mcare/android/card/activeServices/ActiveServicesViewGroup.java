package ro.vodafone.mcare.android.card.activeServices;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

public class ActiveServicesViewGroup extends LinearLayout {

    Context mContext;

    private ActiveServicesCardController activeServicesCardController;

    public ActiveServicesViewGroup(Context context) {
        super(context);
        this.mContext = context;
    }

    public ActiveServicesViewGroup(Context context, ActiveServicesCardController activeServicesCardController) {
        super(context);
        mContext = context;
        this.activeServicesCardController = activeServicesCardController;
        init(null);
    }

    public ActiveServicesViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setOrientation(VERTICAL);
    }

    public ActiveServicesCardController getController() {
        return activeServicesCardController;
    }

    public void displayErrorCard() {
        removeAllViews();
        addView(new ActiveServicesCard(mContext)
                .setViewGroup(this)
                .showError(true, LoyaltyLabels.getLoyalty_error_message(), true));
    }

    public void displayNoResultCard() {
        removeAllViews();
        addView(new ActiveServicesCard(mContext)
                .setViewGroup(this)
                .showError(true, "Momentan nu ai servicii active.", false));
    }

    public void displayLoadingCard() {
        removeAllViews();
        if(!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            addView(new ActiveServicesCard(mContext).showLoading(true));
        }
    }

    public void atachCards(List<ActiveServicesCard> cardList) {
        removeAllViews();
        if (cardList.size() > 0)
            for (ActiveServicesCard card : cardList) {
                this.addView(card);
            }
        else
            displayNoResultCard();
    }
}
