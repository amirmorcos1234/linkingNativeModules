package ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated;

import android.content.Context;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.EbuMigratedDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.NonVodafoneDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.AccountSpecialistFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Deaconescu Eliza on 28/204/2017.
 */

public class EbuMigrated extends PostPaidUser {

    private boolean entityVerifyed = false;
    @Override
    public int  getMenu() {
        return R.menu.ebu_migrated_user_menu;
    }


    @Override
    public BaseDashboardFragment getDashboardFragment() {

        return entityVerifyed? EbuMigratedDashboardFragment.newInstance(): NonVodafoneDashboardFragment.newInstance();
    }

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return false;
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {
        return null;

    }

    @Override
    public boolean isFullLoggedIn() {
        return true;
    }

    /**
     * Is PowerUser or Subuser, separated from the identity tree
     * @return
     */
    public boolean isSubscriber(){
        return this instanceof PowerUser || this instanceof SubUserMigrated;
    }

    public boolean isChooser(){
        return this instanceof ChooserUser;
    }

    public boolean isDelagatedChooser(){
        return this instanceof DelegatedChooserUser;
    }

    public boolean isAuthorisedPerson(){
        return this instanceof AuthorisedPersonUser;
    }

    public void setEntityVerifyed(boolean entityVerifyed) {
        this.entityVerifyed = entityVerifyed;
    }

    public boolean isEntityVerifyed() {
        return entityVerifyed;
    }

    public List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> getUserProfileOptions() {
        List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> list = new ArrayList<>();
        list.add(new ImmutableTriple<>(AccountSpecialistFragment.class, YourProfileLabels.getPayBillConfirmationCardTitle(), YourProfileLabels.getPayBillConfirmationCardSubTitle()));

        return list;

    }
}
