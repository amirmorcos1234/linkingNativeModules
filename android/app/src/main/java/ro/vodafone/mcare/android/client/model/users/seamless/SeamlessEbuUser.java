package ro.vodafone.mcare.android.client.model.users.seamless;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.EbuMigratedDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.NonVodafoneDashboardFragment;

/**
 * Created by Bivol Pavel on 07.02.2018.
 */

public class SeamlessEbuUser extends EbuMigrated {

    @Override
    public int getMenu() {
        return R.menu.seamless_ebu_menu;
    }

    @Override
    public BaseDashboardFragment getDashboardFragment() {
        return EbuMigratedDashboardFragment.newInstance();
    }

    @Override
    public boolean isFullLoggedIn() {
        return false;
    }
}
