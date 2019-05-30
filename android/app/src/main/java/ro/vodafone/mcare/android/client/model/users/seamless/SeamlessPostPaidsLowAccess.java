package ro.vodafone.mcare.android.client.model.users.seamless;


import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.PostpaidDashboardFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Victor Radulescu on 12/21/2016.
 */

public class SeamlessPostPaidsLowAccess extends PostPaidUser {

    @Override
    public int getMenu() {
        return R.menu.seamless_postpaid_menu_low_access;
    }

    @Override
    public BaseDashboardFragment getDashboardFragment() {
        return PostpaidDashboardFragment.newInstance();
    }

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return false;
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {

/*

        List<SelectionPageButton> topUpSelectionPageButtons = new ArrayList<>();

        topUpSelectionPageButtons.add(new SelectionPageButton(context, context.getResources().getColor(R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recharge_another_number(), TopUPLabels.getTop_up_recharge_with_card(), new TopUpSelectionPageFragment(), null));

        return topUpSelectionPageButtons;
*/
        return null;
    }

    @Override
    public boolean isFullLoggedIn() {
        return false;
    }
}
