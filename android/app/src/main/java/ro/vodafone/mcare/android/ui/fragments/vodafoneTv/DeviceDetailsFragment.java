package ro.vodafone.mcare.android.ui.fragments.vodafoneTv;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DevicesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.service.VodafoneTvService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.VodafoneTvActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Prodan Pavel on 19.06.2018.
 */

public class DeviceDetailsFragment extends BaseFragment implements ActiveDevicesContract.DeviceDetailsView, OnErrorIconClickListener {
    @BindView(R.id.device_name_input_label)
    VodafoneTextView deviceNameInputLabel;
    @BindView(R.id.device_name_input)
    CustomEditTextCompat deviceNameInput;
    @BindView(R.id.device_name_edit_btn)
    VodafoneButton deviceNameEditButton;
    @BindView(R.id.device_delete_btn)
    VodafoneButton deviceDeleteButton;
    @BindView(R.id.input_tooltip_error)
    TooltipError inputTooltipError;

    private String DEVICE_NAME_REGEX = "[a-zA-Z0-9 ]*";
    private Unbinder mBinder;
    private NavigationHeader mNavigationHeader;
    private DevicesList mDeviceObject;
    private String mDevicesFamily;
    private ActiveDevicesContract.Presenter mPresenter;
    View.OnClickListener deviceDeleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.vodafoneTv_delete_device);
            if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

            mPresenter.onDeleteDeviceClicked();
        }
    };
    private Dialog mOverlay;
    View.OnClickListener dismissOverlayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOverlay.isShowing())
                mOverlay.dismiss();
        }
    };
    private String mUserRole = VodafoneController.getInstance().getUser().getUserProfile().getUserRoleString();
    View.OnClickListener deviceRenameOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (deviceNameInput.getText().toString().equals("")) {
                displayEditTextError(VodafoneTvLabels.getVtvRenameInputRequiredError(), true);
                deviceNameEditButton.setEnabled(false);
            } else if (!deviceNameInput.getText().toString().matches(DEVICE_NAME_REGEX)) {
                displayEditTextError(VodafoneTvLabels.getVtvRenameInputFormatError(), true);
                deviceNameEditButton.setEnabled(false);
            } else
                mPresenter.renameDevice(mUserRole, deviceNameInput.getText().toString(), mDeviceObject.getUdid());
        }
    };
    View.OnClickListener confirmDeleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.deleteDevice(mUserRole, mDeviceObject.getUdid());
        }
    };
    private View.OnKeyListener inputOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                KeyboardHelper.hideKeyboard(getActivity());
                deviceNameInput.clearFocus();
                return true;
            }
            return false;
        }
    };
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.hasFocus()) {
                displayEditTextError(null, false);
            } else {
                KeyboardHelper.hideKeyboard(getActivity());
                if (deviceNameInput == null) return;

                String newDeviceName = deviceNameInput.getText().toString();
                validateInput(newDeviceName);

            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationHeader = ((VodafoneTvActivity) getActivity()).getNavigationHeader();
        mDevicesFamily = (String) getArguments().getSerializable("family_name");
        mDeviceObject = (DevicesList) getArguments().getSerializable("vtv_device");
        mPresenter = new ActiveDevicesPresenter(this, new VodafoneTvService(getContext()));
        mOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vtv_device_details, container, false);
        mBinder = ButterKnife.bind(this, v);

        VodafoneTvEditNameTrackingEvent event = new VodafoneTvEditNameTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv_device_details);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.vodafoneTv);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        configureHeader();
        deviceNameInputLabel.setText(VodafoneTvLabels.getVtvEditDeviceLabel());
        deviceNameEditButton.setText(VodafoneTvLabels.getVtvRenameDeviceButtonLabel());
        deviceDeleteButton.setText(VodafoneTvLabels.getVtvDeleteDeviceButtonLabel());
        deviceNameInput.setText(mDeviceObject.getName());
        deviceNameInput.setOnKeyListener(inputOnKeyListener);
        deviceNameInput.setOnFocusChangeListener(onFocusChangeListener);
        deviceNameInput.setOnErrorIconClickListener(this);

        deviceNameEditButton.setOnClickListener(deviceRenameOnClickListener);
        if (mDevicesFamily.equals("STB")) {
            deviceDeleteButton.setVisibility(View.GONE);
        } else {
            deviceDeleteButton.setVisibility(View.VISIBLE);
            deviceDeleteButton.setOnClickListener(deviceDeleteOnClickListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mNavigationHeader != null) {
            mNavigationHeader.removeViewFromContainer();
            mNavigationHeader.hideBannerView();
        }
        mPresenter.unsubscribe();
    }

    @Override
    public void showErrorMessage() {
        hideLoading();
        new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
    }

    @Override
    public void redirectToDashboardSuccessCase(String message) {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(22, 20, VoiceOfVodafoneCategory.Recharge, null, message, VodafoneTvLabels.getVtvVovSecondaryButton(), VodafoneTvLabels.getVtvVovPrimaryButton(), true, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.RedirectWithIntent);
        voiceOfVodafone.setIntentActionName(IntentActionName.VODAFONE_TV);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        new CustomToast.Builder(getActivity()).message(VodafoneTvLabels.getVtvSuccessRenameToastMessage()).success(true).show();
        new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
    }

    @Override
    public void displayLimitExceededOverlay() {
        mOverlay.setContentView(R.layout.overlay_dialog_notifications);
        Realm realm = Realm.getDefaultInstance();
        GetByOperatorSuccess operatorSuccess = (GetByOperatorSuccess) RealmManager.getRealmObject(realm, GetByOperatorSuccess.class);
        realm.close();
        ((VodafoneTextView) mOverlay.findViewById(R.id.overlayTitle)).setText(VodafoneTvLabels.getVtvLimitExceededOverlayTitle());
        ((VodafoneTextView) mOverlay.findViewById(R.id.overlaySubtext)).setText(VodafoneTvLabels.getVtvLimitExceededOverlayMessage().replace("{date}", formatTheDateWithHour(operatorSuccess.getDeleteNextDeviceAction())));
        ((VodafoneButton) mOverlay.findViewById(R.id.buttonKeepOn)).setVisibility(View.GONE);
        mOverlay.findViewById(R.id.overlayDismissButton).setOnClickListener(dismissOverlayOnClickListener);

        VodafoneButton overlayButton = mOverlay.findViewById(R.id.buttonTurnOff);
        overlayButton.setText(VodafoneTvLabels.getVtvLimitExceededOverlayButtonLabel());
        overlayButton.setOnClickListener(dismissOverlayOnClickListener);

        mOverlay.show();
    }

    @Override
    public void displayConfirmDeleteOverlay() {
        mOverlay.setContentView(R.layout.overlay_dialog_notifications);

        ((VodafoneTextView) mOverlay.findViewById(R.id.overlayTitle)).setText(VodafoneTvLabels.getVtvConfirmDeleteOverlayTitle());
        ((VodafoneTextView) mOverlay.findViewById(R.id.overlaySubtext)).setText(VodafoneTvLabels.getVtvConfirmDeleteOverlayMessage().replace("{device}", mDeviceObject.getName()));
        mOverlay.findViewById(R.id.overlayDismissButton).setOnClickListener(dismissOverlayOnClickListener);

        VodafoneButton confirmButton = mOverlay.findViewById(R.id.buttonKeepOn);
        confirmButton.setText(VodafoneTvLabels.getVtvConfirmDeleteButtonLabel());
        confirmButton.setOnClickListener(confirmDeleteOnClickListener);

        VodafoneButton dismissButton = mOverlay.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(VodafoneTvLabels.getVtvDeleteOverlayDismissButton());
        dismissButton.setOnClickListener(dismissOverlayOnClickListener);
        mOverlay.show();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        stopLoadingDialog();
    }

    private void configureHeader() {
        mNavigationHeader.setTitle(VodafoneTvLabels.getVtvDeviceDetailsPageTitle());
        mNavigationHeader.removeViewFromContainer();
        mNavigationHeader.hideSelectorView();
        mNavigationHeader.showBannerView();
        mNavigationHeader.showTriangleView();

        String iconUrl = getArguments().getString("icon_url");

        View headerView = View.inflate(getContext(), R.layout.vtv_device_details_header, null);
        CircleImageView deviceIcon = headerView.findViewById(R.id.vtv_device_icon);
        if (iconUrl != null && !iconUrl.isEmpty())
            Glide.with(getContext()).load(iconUrl).into(deviceIcon);
        VodafoneTextView deviceName = headerView.findViewById(R.id.vtv_device_name);
        VodafoneTextView deviceActivationDate = headerView.findViewById(R.id.vtv_activation_date);

        String addedOnLabel = String.format(VodafoneTvLabels.getVodafoneTvDevicesAddedLabel(), formatTheDate(mDeviceObject.getActivatedOn()), getTheHour(mDeviceObject.getActivatedOn()));
        deviceName.setText(mDeviceObject.getName());
        deviceActivationDate.setText(addedOnLabel);

        mNavigationHeader.addViewToContainer(headerView);
    }

    private String formatTheDate(Long timestamp) {
        Date activationDate = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
        return dateFormat.format(activationDate);
    }

    private String formatTheDateWithHour(Long timestamp) {
        Date activationDate = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("RO", "RO"));
        return dateFormat.format(activationDate);
    }

    private String getTheHour(Long timestamp) {
        Date activationHour = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", new Locale("RO", "RO"));
        return dateFormat.format(activationHour);
    }

    private void validateInput(String newDeviceName) {
        if (newDeviceName.equals(""))
            displayEditTextError(VodafoneTvLabels.getVtvRenameInputRequiredError(), true);
        else if (newDeviceName.length() < 4 || (newDeviceName.length() >= 4 && newDeviceName.trim().length() == 0))
            displayEditTextError(VodafoneTvLabels.getVtvRenameInputMinimumError(), true);
        else if (!deviceNameInput.getText().toString().matches(DEVICE_NAME_REGEX))
            displayEditTextError(VodafoneTvLabels.getVtvRenameInputFormatError(), true);
        else if (newDeviceName.equals(mDeviceObject.getName()))
            deviceNameEditButton.setEnabled(false);
        else deviceNameEditButton.setEnabled(true);
    }

    private void displayEditTextError(String message, boolean errorVisible) {
        inputTooltipError.setText(message);
        if (errorVisible)
            inputTooltipError.setVisibility(View.VISIBLE);
        else
            inputTooltipError.setVisibility(View.GONE);
        if (errorVisible) {
            deviceNameInput.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            deviceNameEditButton.setEnabled(false);
        } else {
            if (deviceNameInput.hasFocus())
                deviceNameInput.setBackgroundresourceAndFieldIcon(R.drawable.onfocus_input_border);
            else
                deviceNameInput.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        }
    }

    @Override
    public void onErrorIconClickListener() {
        displayEditTextError(null, false);
    }

    public static class VodafoneTvEditNameTrackingEvent extends TrackingEvent {
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "vodafone tv edit name";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "vodafone tv edit name");
            s.channel = "vodafone tv";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "vodafone tv edit name";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}
