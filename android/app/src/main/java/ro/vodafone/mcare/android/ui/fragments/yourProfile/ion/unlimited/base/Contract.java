package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base;

import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.client.model.ion.IonEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.ion.IonModel;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.mvp.BaseMVPContract;

class Contract {

    public interface ContractView extends BaseMVPContract.ContractView {
        void showLoadingDialog();
        void stopLoadingDialog();
        void manageErrorCodes(String code, int error_type);
        void createCards();
        void redirectVovInDashboardAfterPut();
        void redirectVovInDashboardAfterDelete();
        void dismissDialogIfSuccessful();
        void setParentVisibility(int visibility);
    }

    public interface ContractPresenter extends BaseMVPContract.ContractPresenter {
        int getMaximumLimit();
        int getNumberOfMsisdns();
        int getMaxLengthMsisdnAfterPrefix();
        int getMinLengthMsisdnAfterPrefix();
        void initRealm();
        void getUnlimitedIon(Context context);
        void changePhoneNumberRestrictions(String prefix);
        void putUnlimitedIon(Context context, String ionPhoneNumber);
        void deleteUnlimitedIon(Context context, String ionPhoneNumber);
        boolean prefixIsValid(String prefix);
        String getDefaultPrefix();
        List<String> getPrefixes();
        List<IonModel> getMsisdnList();
        Profile getProfileFromRealm();
    }

}
