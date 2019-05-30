package ro.vodafone.mcare.android.application.controllers;

import android.content.Context;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.ion.IonEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.ion.IonModel;
import ro.vodafone.mcare.android.client.model.ion.PhoneNumberPrefix;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;

public class IonController {

    public static final int TOAST_ION_ERROR = 1;
    public static final int LAYOUT_ION_ERROR = 2;
    public static final int CHECK_ION_SUCCESS_RESPONSE = 4;
    public static final int CHECK_ION_FAULT_RESPONSE = 5;
    public static final String SYSTEM_ION_ERROR = "default";
    public static final String DEFAULT_COUNTRY_PREFIX = "italia";

    public static boolean shouldPerformUnlimitedIonPage() {
        User user = VodafoneController.getInstance().getUser();
        return user instanceof PrepaidUser
                || user instanceof ResCorp
                || user instanceof ResSub
                || user instanceof PrivateUser;
    }

    public static boolean isPrepaidUser() {
        return VodafoneController.getInstance().getUser() instanceof PrepaidUser;
    }

    public static boolean isCBUPostpaidUser() {
        User user = VodafoneController.getInstance().getUser();
        return user instanceof ResCorp
                || user instanceof ResSub
                || user instanceof PrivateUser;
    }

    public static boolean isTobeOrVmb(Profile profile) {
        return profile != null
                && ((isPrepaidUser() && profile.isTobe())
                || (isCBUPostpaidUser() && profile.isVMB()));
    }

    public static boolean checkGetResponse(int checkType, GeneralResponse<IonEligibilitySuccess> unlimitedIonGeneralResponse) {
        switch (checkType) {
            case CHECK_ION_SUCCESS_RESPONSE:
                return unlimitedIonGeneralResponse != null
                        && unlimitedIonGeneralResponse.getTransactionStatus() == 0
                        && unlimitedIonGeneralResponse.getTransactionSuccess() != null;
            case CHECK_ION_FAULT_RESPONSE:
                return unlimitedIonGeneralResponse != null
                        && unlimitedIonGeneralResponse.getTransactionStatus() == 2
                        && unlimitedIonGeneralResponse.getTransactionFault() != null;
            default:
                return false;
        }
    }

    public static boolean checkPutAndDeleteResponse(int checkType, GeneralResponse unlimitedIonGeneralResponse) {
        switch (checkType) {
            case CHECK_ION_SUCCESS_RESPONSE:
                return unlimitedIonGeneralResponse != null
                        && unlimitedIonGeneralResponse.getTransactionStatus() == 0;
            case CHECK_ION_FAULT_RESPONSE:
                return unlimitedIonGeneralResponse != null
                        && unlimitedIonGeneralResponse.getTransactionStatus() == 2
                        && unlimitedIonGeneralResponse.getTransactionFault() != null;
            default:
                return false;
        }
    }

    public static boolean checkListFnfNumbersNotEmpty(List<String> fnfNumbers) {
        return fnfNumbers != null && !fnfNumbers.isEmpty();
    }

    public static boolean checkListPrefixesNotEmpty(List<PhoneNumberPrefix> prefixes) {
        return prefixes != null && !prefixes.isEmpty();
    }

    public static boolean checkListMsisdnNotEmpty(List<IonModel> msisdnList) {
        return msisdnList != null && !msisdnList.isEmpty();
    }

    public static String getIonInactiveDescriptionText() {
        return isPrepaidUser()
                ? YourProfileLabels.getUnlimitedPrepaidIonInactiveText()
                : getUnlimitedPostpaidCBUIonInactiveText();
    }

    public static String getUnlimitedPostpaidCBUIonInactiveText() {
        return YourProfileLabels.getUnlimitedCBUIonInactiveText1()
                + "\n\n" + YourProfileLabels.getUnlimitedCBUIonInactiveText2()
                + "\n\n" + YourProfileLabels.getUnlimitedCBUIonInactiveText3()
                + "\n\n" + YourProfileLabels.getUnlimitedCBUIonInactiveText4();
    }

    public static String getIonInactiveButtonText(Context context) {
        return isPrepaidUser()
                ? context.getResources().getString(R.string.unlimited_prepaid_ion_inactive_text)
                : context.getResources().getString(R.string.unlimited_postpaid_cbu_ion_inactive_text);
    }

    public static String getMaximumValueReachedText(int value) {
        String textToReturn = YourProfileLabels.getUnlimitedMsisdnListMaximumLimitTextPart1()
                + " " + value + " ";

        if (Math.abs(value) == 1)
            return textToReturn + YourProfileLabels.getUnlimitedMsisdnListMaximumLimitTextIsOne();

        if (Math.abs(value) < 20)
            return textToReturn + YourProfileLabels.getUnlimitedMsisdnListMaximumLimitTextLessThanTwenty();

        return textToReturn + YourProfileLabels.getUnlimitedMsisdnListMaximumLimitTextMoreThanTwenty();
    }

    public static String getMsisdnListCardSubTitle(int value) {
        String textToReturn = YourProfileLabels.getUnlimitedMsisdnListCardSubTitlePart1()
                + " " + value + " ";

        if (Math.abs(value) == 1)
            return textToReturn + YourProfileLabels.getUnlimitedMsisdnListCardSubTitleTextIsOne();

        if (Math.abs(value) < 20)
            return textToReturn + YourProfileLabels.getUnlimitedMsisdnListCardSubTitleTextLessThanTwenty();

        return textToReturn + YourProfileLabels.getUnlimitedMsisdnListCardSubTitleTextMoreThanTwenty();
    }

}
