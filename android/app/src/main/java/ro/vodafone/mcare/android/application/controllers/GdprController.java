package ro.vodafone.mcare.android.application.controllers;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.GdprService;
import ro.vodafone.mcare.android.ui.fragments.settings.PermissionsFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import android.app.ProgressDialog;

import com.flyco.animation.BounceEnter.BounceRightEnter;

/**
 * Created by user2 on 3/28/2018.
 */

public class GdprController {

    public static final int CHECK_SUCCESS_RESPONSE = 4;
    public static final int CHECK_FAULT_RESPONSE = 5;
    public static final int CHECK_IS_MINOR = 6;
    public static final int CHECK_RESCORP_SID_NOT_FOUND_RESPONSE = 7;

    public static final int PROGRESS_IS_SHOWING = 50;
    public static final int PROGRESS_IS_NOT_SHOWING = 51;

    public static final String EC00040 = "EC00040";
    public static final String EC07001 = "EC07001";
    public static final String EC07002 = "EC07002";

    public static boolean shoudPerformGetPermissions() {
        User user = VodafoneController.getInstance().getUser();
        return (user instanceof PrepaidUser
                && !(user instanceof SeamlessPrepaidUser)
                && !(user instanceof SeamlessPrepaidHybridUser))
                || user instanceof PrivateUser
                || user instanceof ResCorp
                || user instanceof ResSub
                || user instanceof ChooserUser
                || user instanceof DelegatedChooserUser
                || user instanceof AuthorisedPersonUser
                || user instanceof PowerUser
                || user instanceof SubUserMigrated;
    }

    public static boolean shoudPerformGetPermissionsForEbuMigrated() {
        User user = VodafoneController.getInstance().getUser();
        return user instanceof ChooserUser
                || user instanceof DelegatedChooserUser
                || user instanceof AuthorisedPersonUser
                || user instanceof PowerUser
                || user instanceof SubUserMigrated;
    }

    public static boolean shoudPerformGetPermissionsForCBUUserAndPrepaid() {
        User user = VodafoneController.getInstance().getUser();
        return (user instanceof PrepaidUser
                && !(user instanceof SeamlessPrepaidUser)
                && !(user instanceof SeamlessPrepaidHybridUser))
                || user instanceof PrivateUser
                || user instanceof ResCorp
                || user instanceof ResSub;
    }

    public static boolean checkProgressDialog(int type, ProgressDialog [] progressDialogs) {
        switch (type) {
            case PROGRESS_IS_SHOWING:
                return progressDialogs.length > 0 && progressDialogs[0] != null && progressDialogs[0].isShowing();
            case PROGRESS_IS_NOT_SHOWING:
                return progressDialogs.length > 0 && progressDialogs[0] != null && !progressDialogs[0].isShowing();
        }
        return false;
    }

    public static void getPermissionsAfterLogin(boolean tryAutoLoginIf401Error, final ProgressDialog... progressDialogs) {

        if (checkProgressDialog(PROGRESS_IS_NOT_SHOWING, progressDialogs))
            progressDialogs[0].show();

        RealmManager.deleteObjects(GdprGetResponse.class);
        RealmManager.deleteAfterKey(GdprGetResponse.class, 1);
        RealmManager.deleteAfterKey(GdprGetResponse.class, 2);

        User user = VodafoneController.getInstance().getUser();
        String vfPhoneNumber = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String vfSid = VodafoneController.getInstance().getUserProfile().getSid();

        if ((user instanceof PrepaidUser || user instanceof CBUUser)
                && (vfSid == null || vfSid.isEmpty())) {
            return;
        }

        if (VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser
                || VodafoneController.getInstance().getUser() instanceof EbuMigrated)
            vfSid = "";

        new GdprService(VodafoneController.getInstance()).getPermissions(vfPhoneNumber, vfSid, true, 1)
                .subscribe(new RequestSessionObserver<GeneralResponse<GdprGetResponse>>(tryAutoLoginIf401Error) {
            @Override
            public void onNext(GeneralResponse<GdprGetResponse> gdprGetResponseGeneralResponse) {
                if (checkProgressDialog(PROGRESS_IS_SHOWING, progressDialogs))
                    progressDialogs[0].dismiss();
                if (checkGetResponse(CHECK_SUCCESS_RESPONSE, gdprGetResponseGeneralResponse)) {
                    gdprGetResponseGeneralResponse.getTransactionSuccess().setId_gdpr(1);
                    RequestSaveRealmObserver.save(gdprGetResponseGeneralResponse);
                    return;
                }
            }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        if (checkProgressDialog(PROGRESS_IS_SHOWING, progressDialogs))
                            progressDialogs[0].dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (checkProgressDialog(PROGRESS_IS_SHOWING, progressDialogs))
                            progressDialogs[0].dismiss();
                    }
                });
    }

    public static boolean checkGetResponse(int checkType, GeneralResponse<? extends GdprGetResponse> gdprGetResponseGeneralResponse) {
        switch (checkType) {
            case CHECK_SUCCESS_RESPONSE:
                return gdprGetResponseGeneralResponse != null
                        && gdprGetResponseGeneralResponse.getTransactionStatus() == 0
                        && gdprGetResponseGeneralResponse.getTransactionSuccess() != null
                        && gdprGetResponseGeneralResponse.getTransactionSuccess().getGdprPermissions() != null;
            case CHECK_FAULT_RESPONSE:
                return gdprGetResponseGeneralResponse != null
                        && gdprGetResponseGeneralResponse.getTransactionFault() != null;
            case CHECK_RESCORP_SID_NOT_FOUND_RESPONSE:
                return gdprGetResponseGeneralResponse != null
                        && gdprGetResponseGeneralResponse.getTransactionStatus() == 2
                        && gdprGetResponseGeneralResponse.getTransactionFault() != null
                        && gdprGetResponseGeneralResponse.getTransactionFault().getFaultCode() != null
                        && gdprGetResponseGeneralResponse.getTransactionFault().getFaultCode().equals(GdprController.EC07001);
            case CHECK_IS_MINOR:
                return checkMinorStatus(gdprGetResponseGeneralResponse.getTransactionSuccess().getGdprPermissions());
            default:
                return false;
        }
    }

    public static boolean checkMinorStatus(GdprPermissions gdprPermissions) {
        return gdprPermissions != null && gdprPermissions.isMinor();
    }

    public static boolean isChecked(String status) {
        return status != null && status.equalsIgnoreCase(GdprPermissions.CHECKED);
    }

    public static boolean isSpecify(String status) {
        return status == null || status.equalsIgnoreCase(GdprPermissions.SPECIFY);
    }

    public static boolean checkPutResponse(int checkType, GeneralResponse gdprPutResponseGeneralResponse) {
        switch (checkType) {
            case CHECK_SUCCESS_RESPONSE:
                return gdprPutResponseGeneralResponse != null
                        && gdprPutResponseGeneralResponse.getTransactionStatus() == 0;
            case CHECK_FAULT_RESPONSE:
                return gdprPutResponseGeneralResponse != null
                        && gdprPutResponseGeneralResponse.getTransactionFault() != null;
            default:
                return false;
        }
    }

    public static void makeLink(VodafoneTextView textView, String link, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(textView.getText());

        int startIndexOfLink = textView.getText().toString().indexOf(link);
        if (startIndexOfLink != -1) {
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }

    public static boolean isOwnSubscriptionSelected() {
        return VodafoneController.getInstance()
                .getUserProfile()
                .getSid()
                .equalsIgnoreCase(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid());
    }

}
