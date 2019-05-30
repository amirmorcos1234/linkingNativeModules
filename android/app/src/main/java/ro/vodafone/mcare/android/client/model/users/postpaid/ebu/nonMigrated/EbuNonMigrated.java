package ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated;

import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;

/**
 * Created by Bivol Pavel on 08.05.2017.
 */

public abstract class EbuNonMigrated extends PostPaidUser {

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return false;
    }
}
