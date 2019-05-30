package ro.vodafone.mcare.android.client.model.users.postpaid.cbu;


import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpHistoryFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPostpaidPost4PreFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpRecurrentRechargesFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;


/**
 * Created by Victor Radulescu on 12/21/2016.
 */

public class ResSub extends CBUUser {

    @Override
    public int getMenu() {
        return R.menu.postpaid_res_sub_menu;
    }


    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {

        List<SelectionPageButton> topUpSelectionPageButtons = new ArrayList<>();

        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recharge_another_number(), TopUPLabels.getTop_up_recharge_card_code_bill(), new TopUpPostpaidPost4PreFragment(), null));

        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_open_recharge_history_postpaid(), TopUPLabels.getTop_up_see_recharge_history(), new TopUpHistoryFragment(), null));

        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recurent_recharge(), null, new TopUpRecurrentRechargesFragment(), null));

        return topUpSelectionPageButtons;
    }

    @Override
    public boolean isFullLoggedIn() {
        return true;
    }

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return false;
    }
}
