package ro.vodafone.mcare.android.client.model.users;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.UserRequestsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.BasicUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests.UserRequestsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.pojoconvertors.VovPojoToViewModelConvertor;


/**
 * Created by Victor Radulescu on 12/21/2016.
 */

public abstract class User {

    public static final String TAG = User.class.getSimpleName();
    public static User getNewUser(UserProfile userProfile) {
        UserRole userRole = roleFromProfile(userProfile);

        Log.d("UserRole", "roleFromString is : " + userRole);
        if (userRole != null) {
            userProfile.setUserRole(userRole);
            switch (userRole) {
                case NON_VF_USER:
                    return new NonVodafoneUser();
                case SEAMLESS_EBU_USER:
                    return new SeamlessEbuUser();
                case AUTHORISED_PERSON:
                    return new AuthorisedPersonUser();
                case CHOOSER:
                    return new ChooserUser();
                case DELEGATED_CHOOSER:
                    return new DelegatedChooserUser();
                case POWER_USER:
                    return new PowerUser();
                case RES_SUB:
                    return new ResSub();
                case PREPAID:
                    return new PrepaidUser();
                case RES_CORP:
                    return new ResCorp();
                case PRIVATE_USER:
                    return new PrivateUser();
                case SEAMLESS_LOW_ACCESS:
                    return new SeamlessPostPaidsLowAccess();
                case SEAMLESS_HIGH_ACCESS:
                    return new SeamlessPostPaidHighAccess();
                case SEAMLESS_PREPAID_USER:
                    return new SeamlessPrepaidUser();
                case SEAMLESS_HYBRID:
                    return new SeamlessPrepaidHybridUser();
                case BASIC_USER:
                    return new BasicUser();
                case EBU_Migrated:
                    return new EbuMigrated();
                case SUB_USER:
                    return new SubUserNonMigrated();
                case Corp_User:
                    return new CorpUser();
                case CORP_SUB_USER:
                    return new CorpSubUser();
                case HYBRID:
                    return new PrepaidHybridUser();
                case SUB_USER_CRMROLE:
                    return new SubUserMigrated();

                default:
                    return new NonVodafoneUser();

            }
        }
        return new NonVodafoneUser();
    }

    public static UserRole roleFromProfile(UserProfile profile) {
        String roleString = profile.getUserRoleString();
        String customerType = profile.getCustomerType();
        boolean isSeamless = VodafoneController.getInstance().isSeamless();
        Boolean isMigrated = null;
        isMigrated = profile.isMigrated();
        String cRMRole = null;
        if (EbuMigratedIdentityController.getInstance() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null) {
            cRMRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            Log.d(TAG, " " + cRMRole);
        }
        if ("hybrid".equalsIgnoreCase(customerType)) {
            return isSeamless ? UserRole.SEAMLESS_HYBRID : UserRole.HYBRID;
        } else if (("PrepaidUser").equalsIgnoreCase(roleString)) {
            return isSeamless ? UserRole.SEAMLESS_PREPAID_USER : UserRole.PREPAID;
        } else if (("nonVFUser").equalsIgnoreCase(roleString) && isMigrated) {
            if(isSeamless) {
                return UserRole.SEAMLESS_EBU_USER;
            } else if (("AuthorizedPerson").equalsIgnoreCase(cRMRole)) {
                return UserRole.AUTHORISED_PERSON;
            } else if (("Chooser").equalsIgnoreCase(cRMRole)) {
                return UserRole.CHOOSER;
            } else if (("DelegatedChooser").equalsIgnoreCase(cRMRole)) {
                return UserRole.DELEGATED_CHOOSER;
            } else if (("PowerUser").equalsIgnoreCase(cRMRole)) {
                return UserRole.POWER_USER;
            } else if (("SubUser").equalsIgnoreCase(cRMRole)) {
                return UserRole.SUB_USER_CRMROLE;
            } else
                // return UserRole.NON_VF_USER;
                return UserRole.EBU_Migrated;
        } else if (("PrivateUser").equalsIgnoreCase(roleString)) {
            return isSeamless ? UserRole.SEAMLESS_HIGH_ACCESS : UserRole.PRIVATE_USER;
        } else if (("ResCorp").equalsIgnoreCase(roleString)) {
            return isSeamless ? UserRole.SEAMLESS_HIGH_ACCESS : UserRole.RES_CORP;
        } else if (("ResSub").equalsIgnoreCase(roleString)) {
            return isSeamless ? UserRole.SEAMLESS_LOW_ACCESS : UserRole.RES_SUB;
        } else if (("CorpUser").equalsIgnoreCase(roleString)) {
            return UserRole.Corp_User;
        } else if (("SubUser").equalsIgnoreCase(roleString) && !isMigrated) {
            return UserRole.SUB_USER;
        } else if (("BasicUser").equalsIgnoreCase(roleString)) {
            return UserRole.BASIC_USER;
        } else if (("CorpSubUser").equalsIgnoreCase(roleString)) {
            return UserRole.CORP_SUB_USER;
        }
        return UserRole.NON_VF_USER;
    }

    public abstract int getMenu();

    public UserProfile getUserProfile() {
        return (UserProfile) RealmManager.getRealmObject(UserProfile.class);
    }

    public void setUserProfile(UserProfile userProfile) {
        RealmManager.update(userProfile);
    }

    public abstract BaseDashboardFragment getDashboardFragment();

    public abstract List<SelectionPageButton> getTopUpSelectionPageButtons(Context context);


    public abstract boolean isFullLoggedIn();

    public ArrayList<VoiceOfVodafone> getInitialVoiceOfVodafones() {
        ArrayList<VoiceOfVodafone> voiceOfVodafones = new ArrayList<>();
        try {
            voiceOfVodafones = getJsonVoiceOfVodafones();
        } catch (Exception e) {
            e.printStackTrace();
            voiceOfVodafones.add(getNotificationVoiceOfVodafone());
        }
        if (voiceOfVodafones == null || voiceOfVodafones.isEmpty()) {
            voiceOfVodafones = new ArrayList<>();
            voiceOfVodafones.add(getNotificationVoiceOfVodafone());
        }
        return voiceOfVodafones;
    }

    public ArrayList<VoiceOfVodafone> getJsonVoiceOfVodafones() throws Exception {
        if (VodafoneController.getInstance().getVoiceOfVodafoneResponse() == null) {

            throw new Exception("No voice of Vodafone response");
        }

        String userRole = null;
        try {
            if (EbuMigratedIdentityController.getInstance() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null) {
                userRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
            } else if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.HYBRID) {
                userRole = VodafoneController.getInstance().getUserProfile().getCustomerType();
            } else {
                userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
            }

        } catch (Exception e) {
            userRole = "nonvfuser";
            e.printStackTrace();
        }

        return VovPojoToViewModelConvertor.getVoiceOfVodafonesViewModelAfterRestPojo(VodafoneController.getInstance().getVoiceOfVodafoneResponse().getVoVs(), userRole, getUserProfile().getFirstName(), VodafoneController.getInstance().isSeamless());
    }


    private VoiceOfVodafone getNotificationVoiceOfVodafone() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(1, 10, VoiceOfVodafoneCategory.Welcome, "Buna ziua", "Bine ai venit in My Vodafone.");
        try {
            boolean isSeamless = VodafoneController.getInstance().getGeneralAppConfiguration().isSeamless();

            UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
            System.out.println("Username " + userProfile.getFirstName());
            System.out.println("VfSsoProfile " + String.valueOf(userProfile.getUserRole()));
            //dimineața / ziua / seara
            String name = !isSeamless ? (" " + userProfile.getFirstName()) : "";
            String title = "Bună" + name + ",";
            String message = "Bine ai venit în My Vodafone!";

            voiceOfVodafone = new VoiceOfVodafone(1, 10, VoiceOfVodafoneCategory.Welcome, title, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voiceOfVodafone;
    }

    public List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> getUserProfileOptions()
    {
        List<ImmutableTriple<? extends Class<? extends YourProfileBaseFragment>, String, String>> list = new ArrayList<>();
        list.add(new ImmutableTriple<>(UserRequestsFragment.class, UserRequestsLabels.getUserRequestsCardTitle(), UserRequestsLabels.getUserRequestsCardSubTitle()));

        return list;

//        return Collections.emptyList();       TODO enable this, remove above
    }
}
