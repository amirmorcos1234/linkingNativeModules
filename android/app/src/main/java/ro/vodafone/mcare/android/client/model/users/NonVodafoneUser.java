package ro.vodafone.mcare.android.client.model.users;

import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.NonVodafoneDashboardFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Victor Radulescu on 12/21/2016.
 */

public class NonVodafoneUser extends User {

    @Override
    public int  getMenu() {
        return R.menu.non_vodafone_menu;
    }


    @Override
    public BaseDashboardFragment getDashboardFragment() {
        return NonVodafoneDashboardFragment.newInstance();
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {

   /* List<SelectionPageButton> topUpSelectionPageButtons = new ArrayList<>();

        topUpSelectionPageButtons.add(new SelectionPageButton(context, context.getResources().getColor(R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recharge_another_number(), TopUPLabels.getTop_up_recharge_with_card(), new TopUpAnonymousFragment(), null));

        return topUpSelectionPageButtons;*/

        return null;

    }

    @Override
    public boolean isFullLoggedIn() {
        return true;
    }

}
