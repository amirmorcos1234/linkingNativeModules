package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.realm.simDetails.SIMDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.NotificationService;
import ro.vodafone.mcare.android.service.SimDetailsService;
import ro.vodafone.mcare.android.service.SimStatusService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.settings.TermsAndConditionsActivity;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;



public class SettingsCardsAdapter extends ArrayAdapter<SettingsElement> {

    boolean flag;
    private SettingsActivity activity;
    private float x1, x2;
    private boolean unhandledError = true;
    private Dialog overlyDialog;

    public SettingsCardsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SettingsCardsAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SettingsCardsAdapter(Context context, int resource, SettingsElement[] objects) {
        super(context, resource, objects);
    }

    public SettingsCardsAdapter(Context context, int resource, int textViewResourceId, SettingsElement[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SettingsCardsAdapter(Context context, int resource, List<SettingsElement> objects) {
        super(context, resource, objects);
    }

    public SettingsCardsAdapter(Context context, int resource, int textViewResourceId, List<SettingsElement> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SettingsCardsAdapter(Context context, ArrayList<SettingsElement> objects) {
        super(context, 0, objects);

    }

    public SettingsCardsAdapter(Context context, SettingsActivity activity, ArrayList<SettingsElement> objects) {
        super(context, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SettingsActivity activity = (SettingsActivity) getContext();
        SettingsElement settingsElement = getItem(position);
        if (convertView == null) {
            if (settingsElement != null)
                if (settingsElement.type == 1) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, parent, false);
                    convertView = assignAndPopulateArrowCard(activity, convertView, settingsElement);
                } else if (settingsElement.type == 2) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_button, parent, false);
                    convertView = assignAndPopulateButtonCard(activity, convertView, settingsElement);

                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_title, parent, false);
                    convertView = assignAndPopulateTitleCard(activity, convertView, settingsElement);
                }

        }

        return convertView;
    }

    private View assignAndPopulateArrowCard(final SettingsActivity activity, View v, final SettingsElement settingsElement) {
        final VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        final VodafoneTextView cardSubtext = (VodafoneTextView) v.findViewById(R.id.cardSubtext);
        // ImageView arrow = (ImageView) v.findViewById(R.id.cardArrow);
        LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.arrowCardLayout);

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardTitle.getText().equals(getContext().getResources().getString(R.string.notifications))) {

                    final SettingsActivity activity = (SettingsActivity) getContext();
                    NotificationService notificationService = activity.getNotificationService();

                    activity.showLoadingDialog();
                    notificationService.getNotificationFlag().subscribe(new RequestSessionObserver<GeneralResponse<NotificationFlag>>() {
                        @Override
                        public void onNext(GeneralResponse<NotificationFlag> notificationFlagGeneralResponse) {
                            activity.stopLoadingDialog();


                            if (notificationFlagGeneralResponse.getTransactionSuccess() != null) {
                                D.w("SERVER NOTIFICATIONS FLAG = " + notificationFlagGeneralResponse.getTransactionSuccess().getNotificationsFlag());
                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowNotifications(notificationFlagGeneralResponse.getTransactionSuccess().getNotificationsFlag());
                                SettingsNotificationsFragment settingsNotificationsFragment = new SettingsNotificationsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isError", false);
                                bundle.putBoolean("isChecked", notificationFlagGeneralResponse.getTransactionSuccess().getNotificationsFlag());
                                settingsNotificationsFragment.setArguments(bundle);
                                activity.attachFragment(settingsNotificationsFragment);
                            } else {
                                SettingsNotificationsFragment settingsNotificationsFragment = new SettingsNotificationsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isError", true);
                                settingsNotificationsFragment.setArguments(bundle);
                                activity.attachFragment(settingsNotificationsFragment);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            D.d("on Completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            activity.stopLoadingDialog();
                            if(isActivityClosed())
                                return;

                            SettingsNotificationsFragment settingsNotificationsFragment = new SettingsNotificationsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isError", true);
                            settingsNotificationsFragment.setArguments(bundle);
                            activity.attachFragment(settingsNotificationsFragment);
                        }
                    });

                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.confidentiality))) {
                    activity.attachFragment(new PrivacyPolicyFragment());
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.electronicBill))) {
                    activity.attachFragment(new ElectronicBillFragment());
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.resetPassword))) {
                    activity.attachFragment(new ResetPasswordFragment());
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.blockSIM))) {
                    new NavigationAction(activity).finishCurrent(false).startAction(IntentActionName.SETTINGS_BLOCK_SIM);
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.terms))) {

                    Intent intent = new Intent(getContext(), TermsAndConditionsActivity.class);
                    intent.putExtra(WebviewActivity.KEY_URL, getContext().getString(R.string.terms_and_conditions_url));

                    activity.startActivity(intent);

                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.confidentialityPolicy))) {
                    new NavigationAction(activity).finishCurrent(false).startAction(IntentActionName.PRIVACY_NO_FLAGS);
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.customizeAccount))) {
                    activity.attachFragment(new CustomServicesFragment());
                } else if (cardTitle.getText().equals(SettingsLabels.getSettingsSimDetailsTitle())) {
//                    new NavigationAction(activity).finishCurrent(false).startAction(IntentActionName.SETTINGS_SIM_DETAILS);
                    activity.attachFragment(new SimDetailsFragment());
                } else if (cardTitle.getText().equals(getContext().getResources().getString(R.string.permissions))) {
                    VodafoneController.getInstance()
                            .getTrackingService()
                            .trackCustom(getTrackingEventForPermissions());

                    activity.attachFragment(new PermissionsFragment());
                }


            }

        });
        // Populate the data into the template view using the data object
        cardTitle.setText(settingsElement.title);

        if (settingsElement.subtext.equals(""))
            cardSubtext.setVisibility(View.GONE);
        else
            cardSubtext.setText(settingsElement.subtext);

        return v;
    }

    public TrackingEvent getTrackingEventForPermissions() {
        return new TrackingEvent() {
            @Override
            protected void defineTrackingProperties(TrackingAppMeasurement s) {
                super.defineTrackingProperties(s);

                if (getErrorMessage() != null) {
                    s.events = "event11";
                    s.getContextData().put("event11", s.event11);
                }

                s.pageName = s.prop21 + "settings";
                s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "settings");

                s.channel = "settings";
                s.getContextData().put("&&channel", s.channel);

                s.event65 = "button:" + "permissions";
                s.getContextData().put("event65", s.event65);

                s.prop21 = "mcare:" + "settings";
                s.getContextData().put("prop21", s.prop21);

                s.prop22 = "mcare:" + "settings";
                s.getContextData().put("prop22", s.prop22);

                s.prop23 = "mcare:" + "settings";
                s.getContextData().put("prop23", s.prop23);

                s.prop31 = "mcare";
                s.getContextData().put("prop31", s.prop31);

                s.eVar73 = "settings";
                s.getContextData().put("eVar73", s.eVar73);

                s.eVar82 = "mcare:permissions:button:" + "permissions";
                s.getContextData().put("eVar82", s.eVar82);

            }};
    }

    public void onToOff(final SwitchButton button) {
        D.w("ON to OFF  - set FALSE?");

        overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        //        overlyDialog.getWindow().setBackgroundDrawableResource(R.color.black_opacity_80);
        overlyDialog.show();

        Button buttonTurnOff = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        Button buttonKeepOn = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(SettingsLabels.getBlockSimOverlayTitle());
        overlaySubtext.setText(SettingsLabels.getSeamlessApprovalOverlaySubtitle());

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        buttonTurnOff.setText(SettingsLabels.getSettingsCardsAdapterButtonTurnOff());
        buttonKeepOn.setText(SettingsLabels.getSettingsCardsAdapterButtonKeepOn());

        buttonKeepOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.toggleImmediately(true);
                overlyDialog.dismiss();
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
                activity.authenticationService.setSeamlessFlag(false).subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse generalResponse) {
                        button.toggleImmediately(false);
                        if(generalResponse.getTransactionStatus() == 0) {
                            new CustomToast.Builder(getContext()).message("Ai dezactivat autentificarea automată.").success(true).show();
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(false);
                        }
                        else {
                            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                            button.toggleImmediately(true);
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(true);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
//                        new CustomToast(activity, getContext(), "Ai dezactivat autentificarea automată.", true).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        button.toggleImmediately(true);
                        ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(true);
                        new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
//                        new CustomToast(activity, getContext(), "Serviciu momentan indisponibil.", false).show();
                    }
                });
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.toggleImmediately(true);
                overlyDialog.dismiss();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                button.toggleImmediately(true);
                overlyDialog.dismiss();
            }
        });
    }

    private View assignAndPopulateButtonCard(final SettingsActivity activity, View v, SettingsElement settingsElement) {

        VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        AutoResizeTextView cardSubtext = (AutoResizeTextView) v.findViewById(R.id.cardSubtext);
        final SwitchButton button = (SwitchButton) v.findViewById(R.id.settingsCardButton);

        button.toggleImmediately(getAuthentificationCheckedState());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","retention");
                tealiumMapEvent.put("event_name","mcare:settings:toggle:autentificare automata");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                if (!button.isChecked()) {
                    if(overlyDialog == null || !overlyDialog.isShowing()){
                        onToOff(button);
                    }
                } else {

                    activity.authenticationService.setSeamlessFlag(true).subscribe(new RequestSessionObserver<GeneralResponse>() {
                        @Override
                        public void onNext(GeneralResponse generalResponse) {
                            button.setChecked(true);
                            if(generalResponse.getTransactionStatus() == 0){
                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(true);
                                new CustomToast.Builder(getContext()).message("Ai activat autentificarea automată.").success(true).show();
                            } else {
                                new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(false);
                                button.setChecked(false);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(false);
                            button.setChecked(false);
                        }
                    });
                    D.w("OFF to ON   - set TRUE");
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
                                    if(overlyDialog == null || !overlyDialog.isShowing()){
                                        onToOff(button);
                                    }
                                }
                            } else {
                                D.w("button  state = " + button.isChecked());
                                if (!button.isChecked()) {
                                    D.w("OFF to ON   - set TRUE");
                                    activity.authenticationService.setSeamlessFlag(true).subscribe(new RequestSessionObserver<GeneralResponse>() {
                                        @Override
                                        public void onNext(GeneralResponse generalResponse) {
                                            button.setChecked(true);

                                            if(generalResponse.getTransactionStatus() == 0){
                                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(true);
                                                new CustomToast.Builder(getContext()).message("Ai activat autentificarea automată.").success(true).show();
                                            } else {
                                                new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                                                ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(false);
                                                button.setChecked(false);
                                            }
                                        }

                                        @Override
                                        public void onCompleted() {
                                            super.onCompleted();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil.").success(false).show();
                                            ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).setAllowSeamless(false);
                                            button.setChecked(false);
                                        }
                                    });
                                }

                            }

                        }
                        break;
                }
                return false;
            }
        });

        // Populate the data into the template view using the data object
        cardTitle.setText(settingsElement.title);

        cardTitle.setClickable(false);
        cardTitle.setEnabled(false);
        cardTitle.setFocusable(false);

        if (settingsElement.subtext.equals(""))
            cardSubtext.setVisibility(View.GONE);
        else
            cardSubtext.setText(settingsElement.subtext);

        cardSubtext.setClickable(false);
        cardSubtext.setEnabled(false);
        cardSubtext.setFocusable(false);

        return v;
    }

    private boolean getAuthentificationCheckedState() {
        return ((AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class)).allowSeamless();
    }

    private View assignAndPopulateTitleCard(final SettingsActivity activity, View v, SettingsElement settingsElement) {
        VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        cardTitle.setText(settingsElement.title);

        return v;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private boolean isActivityClosed(){
        final Activity currentActivity = VodafoneController.currentActivity();
        return  (currentActivity == null) || (activity != currentActivity) || !(currentActivity instanceof SettingsActivity) || (activity.isDestroyed());
    }

}
