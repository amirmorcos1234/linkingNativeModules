package ro.vodafone.mcare.android.ui.fragments.settings;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vodafone.netperform.NetPerformContext;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.ServiceListener;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;
/**
 * Created by Deaconescu Eliza on 24.02.2017.
 */
public class PrivacyPolicyFragment extends SettingsFragment {

    public static String TAG = "PrivacyPolicyFragment";

    LinearLayout toBeShown;
    LinearLayout toBeShownOptimization;
    LinearLayout infoContainer;
    LinearLayout infoContainerOptimization;
    ImageView image;
    ImageView imageOptimization;
    SwitchButton buttonPolicy;
    SwitchButton buttonOptimization;
    int card = 1;

    int sdkVersion = Build.VERSION.SDK_INT;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isSettingsFragment = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_settings_privacy_policy, null);

        VodafoneTextView informationSection = (VodafoneTextView) v.findViewById(R.id.confidentiality_information_section);
        informationSection.setText(SettingsLabels.getConfidentialityInformationSection());

        toBeShown = (LinearLayout) v.findViewById(R.id.toBeShownSP);
        toBeShownOptimization = (LinearLayout) v.findViewById(R.id.toBeShown2SP);
        infoContainer = (LinearLayout) v.findViewById(R.id.info_container_sp);
        infoContainerOptimization = (LinearLayout) v.findViewById(R.id.info_container_optimization);

        setupPolicySRButton(v);
        setupOptimizationButton(v);

        image = (ImageView) v.findViewById(R.id.extend_privacy_policy);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeShown.getVisibility() == View.GONE) {
                    toBeShown.setVisibility(View.VISIBLE);
                    image.setImageResource(R.drawable.right_red_arrow_up);
                } else {
                    toBeShown.setVisibility(View.GONE);
                    image.setImageResource(R.drawable.right_red_arrow_down);
                }
            }
        });

        imageOptimization = (ImageView) v.findViewById(R.id.extend_optimization);

        imageOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeShownOptimization.getVisibility() == View.GONE) {
                    toBeShownOptimization.setVisibility(View.VISIBLE);
                    imageOptimization.setImageResource(R.drawable.right_red_arrow_up);
                } else {
                    toBeShownOptimization.setVisibility(View.GONE);
                    imageOptimization.setImageResource(R.drawable.right_red_arrow_down);
                }
            }
        });

        return v;
    }

    private void setupPolicySRButton(View v) {
        buttonPolicy = (SwitchButton) v.findViewById(R.id.settingsCardButtonSP);
        buttonPolicy.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        
                        changeButtonPolicyState((SwitchButton) buttonView);

                        infoContainer.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    }
                });
        checkForPermission(buttonPolicy, getButtonPolicyState(), infoContainer);
    }

    private void changeButtonPolicyState(SwitchButton v) {
        if (!v.isChecked()) {
            card =1;
            displayDisableNotificationsDialog(buttonPolicy, infoContainer,card);
        }else{
            if(checkAndRequestPermissions()){
                enableServicesAndRecomandations();
            }
        }
    }

    private void enableServicesAndRecomandations() {

        //toggleOFFtoON(infoContainer);
        VodafoneController.getInstance().getAppConfiguration().setPrivacyFlag(true);
        VodafoneController.getInstance().getAppConfiguration().setOptimizationFlag(true);
        buttonOptimization.setCheckedNoEvent(true);
        infoContainerOptimization.setVisibility(View.GONE);
        NetPerformContext.start(ServiceListener.getInstance());
        new CustomToast.Builder(getContext()).message(SettingsLabels.getEnableServicesAndRecomandationsToast()).success(true).show();
    }


    private void setupOptimizationButton(View v) {
        buttonOptimization = (SwitchButton) v.findViewById(R.id.settingsCardButtonOptimization);
        buttonOptimization.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                      modifyButtonOptimizationState((SwitchButton) buttonView);

                      infoContainerOptimization.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                  }
        });
        checkForPermission(buttonOptimization, getOptimizationState(), infoContainerOptimization);
    }

    private void modifyButtonOptimizationState(SwitchButton v) {
        boolean isChecked = v.isChecked();
        if (!isChecked) {
            card = 2;
            displayDisableNotificationsDialog(buttonOptimization,infoContainerOptimization, card);
        } else {
            if(toggleOFFtoONAndRequestPermissions(infoContainerOptimization)){
                enableOptimizeNetwork();
            }

        }
    }

    private void checkForPermission(SwitchButton switchButton,boolean configState, LinearLayout linearLayout) {
        if(haveLocationPermission() && havePhonePermission()){
            switchButton.setCheckedNoEvent(configState);
            linearLayout.setVisibility(configState ? View.GONE : View.VISIBLE);
        }else{
            switchButton.setChecked(false);
        }
    }

    public boolean getButtonPolicyState(){
        boolean isPolicyTrue = true;
        try{
            isPolicyTrue = VodafoneController.getInstance().getAppConfiguration().getPrivacyFlag();

        }catch (Exception e){
            e.printStackTrace();
        }
        return isPolicyTrue;
    }

    public boolean getOptimizationState(){
        boolean isOptimizationTrue = true;
        try{
            isOptimizationTrue = VodafoneController.getInstance().getAppConfiguration().getOtimizationFlag();

        }catch (Exception e){
            e.printStackTrace();
        }
        return isOptimizationTrue;
    }

    protected void displayDisableNotificationsDialog(final SwitchButton button, final LinearLayout liniarLayout, final int card) {

        final Dialog overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_secondary_primary);
        //        overlyDialog.getWindow().setBackgroundDrawableResource(R.color.black_opacity_80);
        overlyDialog.show();

        Button buttonTurnOff = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);
        Button buttonKeepOn = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);



        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);
        VodafoneTextView overlaySubtext2 = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext2);




        overlaySubtext2.setVisibility(View.VISIBLE);
        overlayTitle.setText(getResources().getString(R.string.overlayTitle));
        overlaySubtext.setText(getResources().getString(R.string.overlaySubtext));
        overlaySubtext2.setText(SettingsLabels.getOverlaySubtext2());


        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        buttonKeepOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(true);
                overlyDialog.dismiss();
//                liniarLayout.setVisibility(View.GONE);
                NetPerformContext.start(ServiceListener.getInstance());
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
                //liniarLayout.setVisibility(View.VISIBLE);
                if(card == 1) {
                    VodafoneController.getInstance().getAppConfiguration().setPrivacyFlag(false);
                    NetPerformContext.stop(ServiceListener.getInstance());
                }else{
                    VodafoneController.getInstance().getAppConfiguration().setOptimizationFlag(false);
                    NetPerformContext.stop(ServiceListener.getInstance());
                }
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(true);
                overlyDialog.dismiss();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                button.setChecked(true);
                overlyDialog.dismiss();
            }
        });
    }

    protected void toggleOFFtoON(final LinearLayout liniarLayout) {
        liniarLayout.setVisibility(View.GONE);
    }

    protected boolean toggleOFFtoONAndRequestPermissions(final LinearLayout liniarLayout) {
        if(sdkVersion >= 23){
           return checkAndRequestPermissions();
        }
        //liniarLayout.setVisibility(View.GONE);
        return true;
    }


    private boolean checkAndRequestPermissions() {
        int permissionPhone = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        int locationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }
    private boolean havePhonePermission(){
        if(sdkVersion<23){
            return true;
        }
        int permissionPhone = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        return permissionPhone == PackageManager.PERMISSION_GRANTED;
    }
    private boolean haveLocationPermission(){
        if(sdkVersion<23){
            return true;
        }
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionLocation == PackageManager.PERMISSION_GRANTED;
    }


    public String getTitle() {
        return ((String) getResources().getText(R.string.confidentiality));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((SettingsActivity) getActivity()).setTitle();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(MY_PERMISSIONS_REQUEST == requestCode){
        // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                haveLocationPermission() &&
                havePhonePermission()) {
                enableOptimizeNetwork();
                enableServicesAndRecomandations();
            } else {
                buttonOptimization.setCheckedNoEvent(false);
                buttonPolicy.setCheckedNoEvent(false);
                infoContainer.setVisibility(View.VISIBLE);
                infoContainerOptimization.setVisibility(View.VISIBLE);
            }
            return;
        }
    }

    private void enableOptimizeNetwork() {
        VodafoneController.getInstance().getAppConfiguration().setOptimizationFlag(true);
        NetPerformContext.start(ServiceListener.getInstance());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_NET_PERFORM_PRIVACY);
    }
}

