package ro.vodafone.mcare.android.client.model.users.postpaid.cbu;

import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;

/**
 * Created by user on 18.10.2017.
 */

public abstract class CBUUser extends PostPaidUser {

    @Override
    public boolean showSecondaryButtonInSuplimentaryCost() {
        return true;
    }
}
