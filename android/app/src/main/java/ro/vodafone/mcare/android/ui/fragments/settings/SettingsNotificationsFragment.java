package ro.vodafone.mcare.android.ui.fragments.settings;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.urbanairship.UAirship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.TemporaryFlagController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.NotificationRequest;
import ro.vodafone.mcare.android.service.NotificationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;

/**
 * Created by bogdan.marica on 2/27/2017.
 */

public class SettingsNotificationsFragment extends SettingsFragment {
    public static String TAG = "SettingsNotifications";

    private String GENERAL_NOTIFICATIONS_KEY = "general_notifications";
    private String LOYALTY_NOTIFICATIONS_KEY = "loyalty_notifications";

    SwitchButton button;
    SwitchButton loyaltyNotificationButton;
    VodafoneTextView title;
    VodafoneTextView notificationLoyaltyTitle;
    VodafoneTextView loyaltyNotificationsDescription;
    SettingsActivity activity;
    NotificationService notificationService;
    private LinearLayout voucherNotificationCard;
    boolean isErrorLayout;
    boolean isChecked;
    ViewGroup rootView;
    int sdkVersion = Build.VERSION.SDK_INT;
    private float x1, x2;
    private Dialog overlyDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isSettingsFragment = false;

        Bundle bundle = getArguments();
        if (bundle != null) {
            isErrorLayout = bundle.getBoolean("isError");
            isChecked = bundle.getBoolean("isChecked");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");

        rootView = container;
        return inflater.inflate(R.layout.fragment_settings_notifications, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        activity = (SettingsActivity) getActivity();
        notificationService = activity.getNotificationService();

        if (isErrorLayout)
            inflateErrorLayout();
        else
            inflateNotificationLayout();

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","notifications");
        tealiumMapView.put("journey_name","settings");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        SettingsNotificationsTrackingEvent event = new SettingsNotificationsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    private void toggleONtoOFF(String notificationType) {
        if(overlyDialog==null || !overlyDialog.isShowing())
            displayDisableNotificationsDialog(notificationType);
    }

    private void toggleOFFtoON(String notificationType) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put("screen_name","notifications");
        tealiumMapEvent.put("event_name","mcare:notifications:toggle:recomandari de la Vodafone");
        tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }
        if(notificationType.equals(LOYALTY_NOTIFICATIONS_KEY)){
            apiPost(true, true);
        }
        else {
            apiPost(true,false);
        }
    }


    public String getTitle() {
        return ((String) getResources().getText(R.string.notifications));
    }

    public void displayDisableNotificationsDialog(final String notificationType) {
        if(getContext() == null) {
            return;
        }

        overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonTurnOff = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);

        buttonTurnOff.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_button_background_overlay_secondary));
        buttonTurnOff.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));

        Button buttonKeepOn = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);
        buttonKeepOn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_button_background_overlay_primary));
        buttonKeepOn.setTextColor(new ColorStateList(
                new int[][]{//STATES
                        new int[]{-android.R.attr.state_enabled}, // unchecked
                        new int[]{android.R.attr.state_enabled}, // checked
                },
                new int[]{//COLORS
                        ContextCompat.getColor(getContext(), R.color.dark_gray_text_color),
                        ContextCompat.getColor(getContext(), R.color.white_text_color),
                }
        ));

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(getResources().getString(R.string.overlayTitle));
        overlaySubtext.setText(SettingsLabels.getNotificationsOverlayText());

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
        buttonTurnOff.setText(SettingsLabels.getNotificationsOverlayButtonTurnOff());

        buttonKeepOn.setText(SettingsLabels.getOverlayButtonKeepOn());

        buttonKeepOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(true);
                if(notificationType.equals(LOYALTY_NOTIFICATIONS_KEY)){
                    loyaltyNotificationButton.setChecked(true);
                }
                overlyDialog.dismiss();
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();

                if(notificationType.equals(LOYALTY_NOTIFICATIONS_KEY)){
                    loyaltyNotificationButton.setChecked(false);
                    apiPost(true, false);
                }
                else {
                    loyaltyNotificationButton.setChecked(false);
                    button.setChecked(false);
                    apiPost(false, false);
                }

            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(true);
                if(notificationType.equals(LOYALTY_NOTIFICATIONS_KEY)) {
                    loyaltyNotificationButton.setChecked(true);
                }
                overlyDialog.dismiss();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                button.setChecked(true);
                if(notificationType.equals(LOYALTY_NOTIFICATIONS_KEY)) {
                    loyaltyNotificationButton.setChecked(true);
                }
                overlyDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkAndRequestPermissions() {
        if(getActivity() != null) {
            int notificationPolicy = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NOTIFICATION_POLICY);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (notificationPolicy != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootView.removeAllViews();
        ((SettingsActivity) getActivity()).setTitle();
    }

    public void inflateErrorLayout() {

        rootView.removeAllViews();


        VodafoneGenericCard vodafoneGenericCard = new VodafoneGenericCard(getActivity());
        vodafoneGenericCard.showError(true, SettingsLabels.getSettingsNotificationsErrorText1());


        vodafoneGenericCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                notificationService.getNotificationFlag().subscribe(new RequestSessionObserver<GeneralResponse<NotificationFlag>>() {
                    @Override
                    public void onNext(GeneralResponse<NotificationFlag> notificationFlagGeneralResponse) {
                        stopLoadingDialog();
                        if (notificationFlagGeneralResponse.getTransactionSuccess() != null) {

                            D.w("getNotificationFlag = " + notificationFlagGeneralResponse.getTransactionSuccess().getNotificationsFlag());
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class))
                                    .setAllowNotifications(notificationFlagGeneralResponse.getTransactionSuccess().getNotificationsFlag());
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class))
                                    .setAllowVoucherNotifications(notificationFlagGeneralResponse.getTransactionSuccess().getVouchersFlag());

                            inflateNotificationLayout();
                        } else
                            onError(new Throwable("Server failed"));
                        D.w("getTransactionFault = " + notificationFlagGeneralResponse.getTransactionFault());
                    }

                    @Override
                    public void onCompleted() {
                        D.d("on Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();

                    }
                });
            }
        });


        rootView.addView(vodafoneGenericCard);

    }

    public void inflateNotificationLayout() {

        rootView.removeAllViews();

        View v = View.inflate(activity, R.layout.fragment_settings_notifications, null);

        if(TemporaryFlagController.getInstance().isHideNonTelco()){
            voucherNotificationCard = (LinearLayout) v.findViewById(R.id.voucher_notification_card);
            voucherNotificationCard.setVisibility(View.GONE);
        }

        title = (VodafoneTextView) v.findViewById(R.id.notificationTitle);
        title.setText(SettingsLabels.getNotificationTitle());

        notificationLoyaltyTitle = (VodafoneTextView) v.findViewById(R.id.notificationLoyaltyTitle);
        notificationLoyaltyTitle.setText(SettingsLabels.getLoyaltyNotificationTitle());

        loyaltyNotificationsDescription = (VodafoneTextView) v.findViewById(R.id.loyalty_notifications_description_tv);
        loyaltyNotificationsDescription.setText(SettingsLabels.getLoyaltyNotificationDescrption());

        button = (SwitchButton) v.findViewById(R.id.notificationCardButton);
        loyaltyNotificationButton = (SwitchButton) v.findViewById(R.id.notificationLoyaltyButton);

        boolean allowNotifications = ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).allowNotifications();
        if(!TemporaryFlagController.getInstance().isHideNonTelco()) {
            boolean allowVoucherNotifications = ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class))
                    .allowVoucherNotifications();
            loyaltyNotificationButton.setChecked(allowVoucherNotifications);
        }

        Log.d(TAG, "allowNotifications : " + allowNotifications);
        button.setChecked(allowNotifications);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!button.isChecked()) {
                    toggleONtoOFF(GENERAL_NOTIFICATIONS_KEY);
                    UAirship.shared().getPushManager().setUserNotificationsEnabled(false);
                    UAirship.shared().getPushManager().editTags()
                            .removeTag("Recommandations")
                            .apply();
                } else {
                    toggleOFFtoON(GENERAL_NOTIFICATIONS_KEY);
                    UAirship.shared().getPushManager().setUserNotificationsEnabled(true);
                }
            }
        });

        loyaltyNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!loyaltyNotificationButton.isChecked()) {
                    toggleONtoOFF(LOYALTY_NOTIFICATIONS_KEY);
                    UAirship.shared().getPushManager().editTags()
                            .removeTag("Recommandations")
                            .apply();
                } else {
                    toggleOFFtoON(LOYALTY_NOTIFICATIONS_KEY);
                    if (!button.isChecked()) {
                        button.setChecked(true);
                        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);
                    }

                    UAirship.shared().getPushManager().editTags()
                            .addTag("Recommandations")
                            .apply();
                }
            }
        });

        final int MIN_DISTANCE = (button.getWidth() * 2 / 3);


        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
//                D.w("motionEvent = " + event);
//                D.w("motionEvent getAction = " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            D.w("SWIPE " + (deltaX > 0 ? "LEFT TO RIGHT" : "RIGHT TO LEFT"));

                            if (deltaX < 0) {

                                D.w("button  state = " + button.isChecked());
                                if (button.isChecked()) {
                                    D.w("LEFT TO RIGHT = ON");
                                    toggleONtoOFF(GENERAL_NOTIFICATIONS_KEY);
                                }
                            } else {
                                D.w("button  state = " + button.isChecked());
                                if (!button.isChecked()) {
                                    D.w("OFF to ON   - set TRUE");
                                    ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(button.isChecked());
                                    activity.authenticationService.setSeamlessFlag(true).subscribe(new RequestSessionObserver<GeneralResponse>() {
                                        @Override
                                        public void onNext(GeneralResponse generalResponse) {
                                        }

                                        @Override
                                        public void onCompleted() {
                                            super.onCompleted();
                                            //new CustomToast.Builder(getContext()).message("Ai activat autentificarea automată.").success(true).show();

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            //new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                                        }
                                    });
                                    ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(true);
                                }

                            }

                        }
                        break;
                }
                return false;
            }
        });

        loyaltyNotificationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
//                D.w("motionEvent = " + event);
//                D.w("motionEvent getAction = " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            D.w("SWIPE " + (deltaX > 0 ? "LEFT TO RIGHT" : "RIGHT TO LEFT"));

                            if (deltaX < 0) {

                                D.w("button  state = " + loyaltyNotificationButton.isChecked());
                                if (loyaltyNotificationButton.isChecked()) {
                                    D.w("LEFT TO RIGHT = ON");
                                    toggleONtoOFF(LOYALTY_NOTIFICATIONS_KEY);
                                }
                            } else {
                                D.w("button  state = " + loyaltyNotificationButton.isChecked());
                                if (!loyaltyNotificationButton.isChecked()) {
                                    D.w("OFF to ON   - set TRUE");
                                    toggleOFFtoON(LOYALTY_NOTIFICATIONS_KEY);
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });

        rootView.addView(v);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    D.w("PERMISSION_GRANTED");
                } else {
                    D.w("PERMISSION_DENIED");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class SettingsNotificationsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "notifications";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "notifications");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public void apiPost(final boolean postGeneralFlag, final boolean postLoyaltyFlag) {

        UAirship.shared(new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship uAirship) {
                String channelId = uAirship.getPushManager().getChannelId();
                Log.d(TAG,"Channel id: " + channelId);
                sendNotificationRequest(channelId, postGeneralFlag, postLoyaltyFlag);
            }
        });
    }

    private void sendNotificationRequest(String channelId, final boolean postGeneralFlag, final boolean postLoyaltyFlag) {
        NotificationRequest request;

        final boolean originalGeneralNotifications = ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).allowNotifications();
        final boolean originalLoyaltyNotifications = ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).allowVoucherNotifications();

        String username = VodafoneController.getInstance().getUserProfile().getUserName();
        String os = "Android";
        request = new NotificationRequest(postGeneralFlag, postLoyaltyFlag, username, os, channelId);

        new NotificationService(getContext()).setNotificationFlag(request)
                .subscribe(new RequestSessionObserver<GeneralResponse<NotificationFlag>>() {
                    @Override
                    public void onNext(GeneralResponse<NotificationFlag> notificationFlagGeneralResponse) {
                        if(notificationFlagGeneralResponse.getTransactionStatus() == 0) {
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowNotifications(postGeneralFlag);

                            if (!TemporaryFlagController.getInstance().isHideNonTelco()) {
                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowVoucherNotifications(postLoyaltyFlag);
                            }
                            if(getActivity() != null) {
                                button.setChecked(postGeneralFlag);
                                loyaltyNotificationButton.setChecked(postLoyaltyFlag);
                            }

                        } else {
                            if(getActivity() != null) {
                                if (originalGeneralNotifications != postGeneralFlag) {
                                    button.setChecked(!postGeneralFlag);
                                }
                                if (originalLoyaltyNotifications != postLoyaltyFlag) {
                                    loyaltyNotificationButton.setChecked(!postLoyaltyFlag);
                                }
                            }
                            new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        D.d("POST Completed - " + postGeneralFlag);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(getActivity() != null) {
                            if (originalGeneralNotifications != postGeneralFlag) {
                                button.setChecked(!postGeneralFlag);
                            }
                            if (originalLoyaltyNotifications != postLoyaltyFlag) {
                                loyaltyNotificationButton.setChecked(!postLoyaltyFlag);
                            }
                        }
                        new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
                    }
                });
    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_NOTIFICATIONS);
    }
}
