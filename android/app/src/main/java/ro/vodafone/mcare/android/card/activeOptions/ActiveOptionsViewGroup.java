package ro.vodafone.mcare.android.card.activeOptions;

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

public class ActiveOptionsViewGroup extends LinearLayout {

    private Context mContext;
    private ActiveOptionsCardController activeOptionsCardController;

    public ActiveOptionsCardController getController() {
        return activeOptionsCardController;
    }

    public ActiveOptionsViewGroup(Context context, ActiveOptionsCardController activeOptionsCardController) {
        super(context);
        mContext = context;
        this.activeOptionsCardController = activeOptionsCardController;
        init(null);
    }

    public ActiveOptionsViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setOrientation(VERTICAL);
    }

    public void displayErrorCard() {
        removeAllViews();
        addView(new ActiveOptionsCard(mContext)
                .setViewGroup(this)
                .showError(true, LoyaltyLabels.getLoyalty_error_message(), true));
    }

    public void displayLoadingCard() {
        removeAllViews();
        if(!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            addView(new ActiveOptionsCard(mContext)
                    .setViewGroup(this)
                    .showLoading(true));
        }
    }

    public void displayNoResultCard() {
        removeAllViews();
        addView(new ActiveOptionsCard(mContext).showError(true, "Momentan nu ai opțiuni active.", false));
    }

    public void atachCards(List<ActiveOptionsCard> cardList) {
        removeAllViews();

        if (cardList.size() > 0)
            for (ActiveOptionsCard card : cardList) {
                this.addView(card);
            }
        else
            displayNoResultCard();
    }
}
