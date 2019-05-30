package ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated;

import android.content.Context;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.UserRequestsLabels;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests.UserRequestsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Deaconescu Eliza on 28/204/2017.
 */

public class AuthorisedPersonUser extends EbuMigrated {

    @Override
    public int  getMenu() {
        return R.menu.authorised_person_menu;
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {
        return null;
    }

    @Override
    public boolean isFullLoggedIn() {
        return true;
    }

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return true;
    }

    @Override
    public List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> getUserProfileOptions()
    {
        List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> list = new ArrayList<>();
        list.add(new ImmutableTriple<>(UserRequestsFragment.class, UserRequestsLabels.getUserRequestsCardTitle(), UserRequestsLabels.getUserRequestsCardSubTitle()));

        return list;
    }
}
