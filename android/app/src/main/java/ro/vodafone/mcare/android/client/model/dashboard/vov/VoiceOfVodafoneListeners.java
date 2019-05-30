package ro.vodafone.mcare.android.client.model.dashboard.vov;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.dashboard.HaveVoiceOfVodafoneListener;
import ro.vodafone.mcare.android.client.model.dashboard.RedirectClickListener;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.listeners.RedirectIntentClickListener;
import ro.vodafone.mcare.android.utils.Go;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.navigation.notification.LinkDispatcherActivity;
import ro.vodafone.mcare.android.widget.VoiceOfVodafoneWidget;


/**
 * Created by Victor Radulescu on 1/27/2017.
 */

public class VoiceOfVodafoneListeners {

    private VoiceOfVodafoneWidget voiceOfVodafoneWidget;
    public View.OnClickListener dismissClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (voiceOfVodafoneWidget != null) {
                voiceOfVodafoneWidget.dismissCurent();
            }
        }
    };
    
    public VoiceOfVodafoneListeners(VoiceOfVodafoneWidget voiceOfVodafoneWidget) {
        this.voiceOfVodafoneWidget = voiceOfVodafoneWidget;
    }

    public View.OnClickListener getListenerByAction(final Context context, VoiceOfVodafoneAction voiceOfVodafoneAction, @Nullable VoiceOfVodafoneParameter voiceOfVodafoneParameter, final VoiceOfVodafone voiceOfVodafone, final boolean isLeft) {
        switch (voiceOfVodafoneAction) {
            case Dismiss:
                return dismissClickListener;
            case Redirect:
                return new RedirectClickListener(voiceOfVodafoneParameter);
            case Activate_Offer:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(VodafoneController.getInstance(), "Redirect to Activate Offer", Toast.LENGTH_SHORT).show();
                    }
                };
            case GoToSupportChat:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("VoV", "confersatie noua pressed");
                        VodafoneController.getInstance().supportWindow(VodafoneController.currentActivity()).show(SupportWindow.DisplayType.CHAT);
                        if (voiceOfVodafoneWidget != null) {
                            voiceOfVodafoneWidget.dismissCurent();
                        }
//                        new NavigationAction(context).startAction(IntentActionName.SUPPORT_24_7_CHAT, false);
                    }
                };
            case AddToFavoritesNumbers:

                return new HaveVoiceOfVodafoneListener(voiceOfVodafone) {
                    @Override
                    public void onClick(View v) {
                        if(context != null && context instanceof BaseMenuActivity){
                            ((MenuActivity) context).displayDialog(VoiceOfVodafoneType.AddToFavoritesNumbers, voiceOfVodafone);
                        }
                    }
                };

            case RedirectToRoaming:

                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(context != null && context instanceof BaseMenuActivity){
                            new NavigationAction(context).startAction(IntentActionName.TRAVELLING_ABOARD, false);
                        }
                    }
                };
            case RedirectWithIntent:
                return new RedirectIntentClickListener(new NavigationAction(context,voiceOfVodafone.getIntentActionName()));
            case OpenUrl:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentActionName intent = voiceOfVodafone.getIntentActionName();
                        intent.setOneUsageSerializedData(isLeft ? voiceOfVodafone.getLeftActionUrl() : voiceOfVodafone.getRightActionUrl());
                        new NavigationAction(context).startAction(intent);
                    }
                };

            case RedirectToBrowser:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Go.browser(context, isLeft ? voiceOfVodafone.getLeftActionUrl() : voiceOfVodafone.getRightActionUrl());
                    }
                };

            case DeepLinkAction:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent deepLinkIntent = new Intent(context, LinkDispatcherActivity.class)
                                .setData(Uri.parse(isLeft ? voiceOfVodafone.getLeftActionUrl() : voiceOfVodafone.getRightActionUrl()));
                        context.startActivity(deepLinkIntent);
                    }
                };
            case ExternalAppRedirect:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(isLeft ? voiceOfVodafone.getLeftActionUrl() : voiceOfVodafone.getRightActionUrl()));
                        context.startActivity(intent);
                    }
                };
            case ReloadAPI19:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (context != null && context instanceof DashboardActivity) {
                            VoiceOfVodafoneController.getInstance().removeVoiceOfVodafone(voiceOfVodafone, true, true);
                            DashboardActivity activity = (DashboardActivity) context;
                            activity.showLoadingDialog();
                            getSubscriberHierarchy(context, activity);
                        }
                    }
                };
            default:
                return dismissClickListener;
        }

    }

    private void getSubscriberHierarchy(final Context context, final DashboardActivity activity) {

        UserDataService userDataService = new UserDataService(context);
        userDataService.getSubscriberHierarchy(true)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>() {

                    @Override
                    public void onCompleted() {
                        activity.refreshAvatarWidget();
                    }

                    @Override
                    public void onNext(GeneralResponse<UserProfileHierarchy> response) {
                        activity.stopLoadingDialog();

                        boolean responseIsSuccessful = response != null
                                && response.getTransactionStatus() == 0
                                && response.getTransactionSuccess() != null;

                        if (context != null)
                            context.getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                    .putInt("errorDetectedFlag", responseIsSuccessful
                                            ? response.getTransactionSuccess().getErrorDetectedFlag()
                                            : DashboardController.API19_ERROR_FLAG).apply();

                        if (responseIsSuccessful) {
                            super.onNext(response);
                            if (response.getTransactionSuccess().ifApi19CallFailed())
                                VoiceOfVodafoneController.getInstance().createApi19FailedVov();
                            else if (response.getTransactionSuccess().ifApi19CallTimeout())
                                VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
                            return;
                        }

                        VoiceOfVodafoneController.getInstance().createApi19FailedVov();
                        new CustomToast.Builder(VodafoneController.getInstance()).message(AppLabels.getToastErrorSomeInfoNotLoaded()).success(false).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        activity.stopLoadingDialog();
                        new CustomToast.Builder(VodafoneController.getInstance()).message(AppLabels.getToastErrorSomeInfoNotLoaded()).success(false).show();
                        if (context != null)
                            context.getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                    .putInt("errorDetectedFlag", DashboardController.API19_TIMEOUT_FLAG)
                                    .apply();
                        VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
                    }
                });
    }

}
