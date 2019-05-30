package ro.vodafone.mcare.android.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.profile.ConfirmProfileLabels;
import ro.vodafone.mcare.android.ui.fragments.profile.ConfirmProfileStep1Fragment;
import ro.vodafone.mcare.android.ui.utils.WallpaperConfig;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;


/**
 * Created by Alex on 1/10/2017.
 */


public class ConfirmProfileActivity extends BaseActivity {

    public static String TAG = "ConfirmProfileActivity";
    BitmapDrawable drawableBitmap;
    VodafoneDialog vodafoneDialogMoreInfo;
    VodafoneDialog vodafoneDialogQuit;
    public ImageView first_step_back_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.confirm_profile_step1);
        first_step_back_button = (ImageView) findViewById(R.id.first_step_back_button);
        first_step_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //Tealium Track view
        init();
        attachFragment(new ConfirmProfileStep1Fragment());
    }

    void init() {
        //Tealium Track view
        initTracking();
        setVodafoneBackgroundOnWindow();
    }

    void initTracking() {
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(4);
        tealiumMapView.put("confirm profile 1st step", "mcare:confirm profile 1st step");
        tealiumMapView.put("confirm profile", "mcare:confirm profile 1st step");
        TealiumHelper.trackView("mcare:confirm profile 1st step", tealiumMapView);

        ConfirmProfileStep1TrackingEvent event = new ConfirmProfileStep1TrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public void attachFragment(BaseFragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, null);
        transaction.commit();
        fragmentManager.executePendingTransactions();

    }

    public void loadTBDProfileInstructionDialog() {
        vodafoneDialogMoreInfo = new VodafoneDialog(this, ConfirmProfileLabels.getConfirmProfileDialogTbdProfileMessage())
                .setOnePositiveButton(ConfirmProfileLabels.getConfirmProfileDialogNegative())
//            .invertColors()
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Negative()");
                        vodafoneDialogMoreInfo.dismiss();
                    }
                });
        vodafoneDialogMoreInfo.setCancelable(true);
        vodafoneDialogMoreInfo.show();
    }

    public void loadLogOutDialog() {
        Log.d(TAG, "loadLogOutDialog()");
        vodafoneDialogQuit = new VodafoneDialog(this, ConfirmProfileLabels.getConfirmProfileDialogLogoutMessage())
                .setNegativeMessage(ConfirmProfileLabels.getConfirmProfileDialogLogoutPositive())
                .setPositiveMessage(ConfirmProfileLabels.getConfirmProfileDialogLogoutNegative())
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Negative2");
                        vodafoneDialogQuit.dismiss();

                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Positive2");
                        vodafoneDialogQuit.dismiss();

                        new AuthenticationService(VodafoneController.getInstance()).logout().subscribe(new RequestSessionObserver<GeneralResponse>() {
                            @Override
                            public void onCompleted() {
                                Log.d("logout", "completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                Log.d("logout", "onError " + e.getMessage());
                                ConfirmProfileActivity.this.finish();
                            }

                            @Override
                            public void onNext(GeneralResponse generalResponse) {
                                Log.d("logout", "onNext " + generalResponse.getTransactionStatus());
                                ConfirmProfileActivity.this.finish();
                            }
                        });
                    }
                });
        vodafoneDialogQuit.setCancelable(true);
        vodafoneDialogQuit.show();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ConfirmProfileStep1Fragment)
            loadLogOutDialog();
        else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    public static class ConfirmProfileStep1TrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "confirm profile 1st step";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "confirm profile 1st step");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "confirm profile";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "MyVodafone Confirm Profile";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}
