package ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated;

import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.NonVodafoneDashboardFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Deaconescu Eliza on 28/204/2017.
 */

public class SubUserNonMigrated extends EbuNonMigrated {

    @Override
    public int  getMenu() {
        return R.menu.sub_user_menu_nonmigrated;
    }


    @Override
    public BaseDashboardFragment getDashboardFragment() {
        return NonVodafoneDashboardFragment.newInstance();
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {
        return null;

    }

    @Override
    public boolean isFullLoggedIn() {
        return true;
    }

}
