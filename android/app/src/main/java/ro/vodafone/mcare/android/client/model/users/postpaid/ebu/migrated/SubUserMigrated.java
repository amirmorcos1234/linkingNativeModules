package ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated;

import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Deaconescu Eliza on 28/204/2017.
 */

public class SubUserMigrated extends EbuMigrated {

    @Override
    public int  getMenu() {
        return R.menu.sub_user_menu_migrated;
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
