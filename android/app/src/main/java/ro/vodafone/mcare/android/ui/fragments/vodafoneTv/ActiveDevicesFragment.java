package ro.vodafone.mcare.android.ui.fragments.vodafoneTv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.activeServices.ActiveServicesCard;
import ro.vodafone.mcare.android.card.activeServices.ActiveServicesViewGroup;
import ro.vodafone.mcare.android.client.adapters.ActiveDevicesAdapter;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DevicesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.VodafoneTvService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.VodafoneTvActivity;
import ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class ActiveDevicesFragment extends BaseFragment implements ActiveDevicesContract.DevicesListView {
    private Unbinder binder;

    @BindView(R.id.active_devices_list)
    ExpandableAdapterBackedLinearLayout activeDevicesList;
    @BindView(R.id.base_fragment_view)
    RelativeLayout fragmentView;
    @BindView(R.id.active_offers_container)
    LinearLayout activeOffersContainer;
    @BindView(R.id.active_offers_label)
    TextView activeOffersLabel;
    @BindView(R.id.active_offers_viewgroup)
    ActiveServicesViewGroup mActiveServicesViewGroup;
    @BindView(R.id.reset_pin_label)
    TextView resetPinLabel;
    @BindView(R.id.reset_pin_card)
    VodafoneGenericCard resetPinCard;
    @BindView(R.id.reset_pin_button)
    VodafoneButton resetPinButton;
    @BindView(R.id.reset_pin_message)
    VodafoneTextView resetPinMessage;
    @BindView(R.id.active_devices_container)
    RelativeLayout activeDevicesContainer;
    @BindView(R.id.vtv_informative_card)
    VodafoneGenericCard informativeCard;
    @BindView(R.id.vtv_informative_message)
    VodafoneTextView informativeMessage;
    @BindView(R.id.beo_button_redirect)
    VodafoneButton beoPageRedirectButton;
    @BindView(R.id.vodafone_tv_app_redirect_button)
    VodafoneButton vodafoneTvAppButton;

    private final static String TAG = ActiveDevicesFragment.class.getSimpleName();

    private ActiveDevicesContract.Presenter presenter;
    private VodafoneGenericCard devicesErrorCard;
    private VodafoneGenericCard offersErrorCard;
    private String mUserRole = VodafoneController.getInstance().getUser().getUserProfile().getUserRoleString();
    private String defaultUserId;
    private boolean vodafoneTv = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void getFixedNetSubscriptions() {
        String fixedBan = VodafoneController.getInstance().getUser().getUserProfile().getVfFixedBan();
        String fixedCid = VodafoneController.getInstance().getUser().getUserProfile().isVfFixedCid();
        String stringBanList = getStringBanList();

        if ((fixedBan != null && !fixedBan.equals("") && !fixedBan.isEmpty())
                || (fixedCid != null) && !fixedCid.equals("") && !fixedCid.isEmpty()) {
            presenter.getTvHierarchy(VodafoneController.getInstance().getUser().getUserProfile().getUserName(),
                    VodafoneController.getInstance().getUser().getUserProfile().getMsisdn(),
                    mUserRole, VodafoneController.getInstance().getUser().getUserProfile().isMigrated(),
                    fixedBan, fixedCid, stringBanList);
            vodafoneTv = true;
        } else {
            RealmManager.update(new TvHierarchyResponse());
            vodafoneTv = false;
        }
    }

    private String getStringBanList() {
        List<Ban> banList = UserSelectedMsisdnBanController.getInstance().getBanList();
        String stringBanList = null;
        if (banList != null) {
            for (Ban ban : banList) {
                if (stringBanList == null)
                    stringBanList = ban.getNumber();
                else
                    stringBanList = stringBanList + "," + ban.getNumber();
            }
        }
        return stringBanList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_active_devices, container, false);
        binder = ButterKnife.bind(this, v);

        VodafoneTvTrackingEvent event = new VodafoneTvTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.vodafoneTv);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);



        presenter = new ActiveDevicesPresenter(this, new VodafoneTvService(getContext()), new OffersService(getContext()));
        getListOfDevices();
        if (VodafoneController.getInstance().getUser() instanceof PostPaidUser)
            getFixedNetSubscriptions();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((VodafoneTvActivity) getActivity()).getNavigationHeader()
                .setTitle(VodafoneTvLabels.getVtvActiveDevicePageTitle())
                .setVodafoneTv(vodafoneTv)
                .displayDefaultHeader()
                .buildMsisdnSelectorHeader();
        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser)
            ((VodafoneTvActivity) getActivity()).getNavigationHeader().hideSelectorView();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        stopLoadingDialog();
    }


    @Override
    public void setVisibilityResetPinCard(boolean showRestPin, String defaultUserID) {
        if (showRestPin) {
            resetPinLabel.setVisibility(View.VISIBLE);
            resetPinCard.setVisibility(View.VISIBLE);
            resetPinLabel.setText(VodafoneTvLabels.getVTVResetPinTitleCard());
            resetPinMessage.setText(VodafoneTvLabels.getVTVResetPinTextCard());
            resetPinButton.setText(VodafoneTvLabels.getVTVResetPinButtonText());

            this.defaultUserId = defaultUserID;
        } else {
            resetPinLabel.setVisibility(View.GONE);
            resetPinCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void populateDevicesViews(final List<DeviceFamiliesList> deviceFamiliesList) {
        clearDevicesContainerIfHasChilds();

        final ActiveDevicesAdapter devicesAdapter = new ActiveDevicesAdapter(getContext(), deviceFamiliesList);
        activeDevicesList.setAdapter(devicesAdapter);
        for (int i = 0; i < deviceFamiliesList.size(); i++) {
            activeDevicesList.expandGroup(i);
        }

        activeDevicesList.setOnGroupClickListener(onGroupClickListener);
        activeDevicesList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.vodafoneTv_device_card);
                if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                    tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

                TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

                DevicesList device = deviceFamiliesList.get(groupPosition).getDevices().get(childPosition);
                Bundle bundle = new Bundle();
                bundle.putSerializable("family_name", devicesAdapter.getGroup(groupPosition).getName());
                bundle.putSerializable("vtv_device", device);
                bundle.putString("icon_url", devicesAdapter.getGroup(groupPosition).getIcon());
                DeviceDetailsFragment deviceDetailsFragment = new DeviceDetailsFragment();
                deviceDetailsFragment.setArguments(bundle);

                ((VodafoneTvActivity) getActivity()).attachFragment(deviceDetailsFragment);
                return false;
            }
        });
    }

    @Override
    public void populateVtvActiveOffers(List<OfferRowInterface> offersList) {
        clearOffersContainerIfHasChilds();
        activeOffersLabel.setText(ServicesLabels.getServices_active_services_label());
        activeOffersLabel.setVisibility(View.VISIBLE);
        List<ActiveServicesCard> activeServicesCards = new ArrayList<>();
        User user = VodafoneController.getInstance().getUser();
        for (OfferRowInterface offerRow : offersList) {
            if (user instanceof PrepaidUser)
                activeServicesCards.add(new ActiveServicesCard(getContext()).buildPrepaidCard((ActiveOffer) offerRow));
            else if (user instanceof CBUUser) {
                activeServicesCards.add(new ActiveServicesCard(getContext()).buildCBUCard((ActiveOfferPostpaid) offerRow));
            }
        }
        mActiveServicesViewGroup.atachCards(activeServicesCards);
        setupButtons();
    }


    @Override
    public void showDevicesErrorCard(String message, boolean isApiError) {
        clearDevicesContainerIfHasChilds();

        devicesErrorCard = new VodafoneGenericCard(getContext());
        devicesErrorCard.showError(true, message);
        if (isApiError)
            devicesErrorCard.getErrorView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   getListOfDevices();
                }
            });

        activeDevicesContainer.addView(devicesErrorCard);
    }

    @Override
    public void showOffersErrorCard(String message, boolean isApiError) {
        clearOffersContainerIfHasChilds();
        activeOffersLabel.setText(ServicesLabels.getServices_active_services_label());
        activeOffersLabel.setVisibility(View.VISIBLE);
        offersErrorCard = new VodafoneGenericCard(getContext());
        offersErrorCard.showError(true, message);
        if (isApiError) {
            offersErrorCard.getErrorView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getListOfDevices();
                }
            });
        }
        activeOffersContainer.addView(offersErrorCard, 1);
        setupButtons();
    }

    @Override
    public void showOffersErrorToast(String message) {
        new CustomToast.Builder(getActivity()).message(message).success(false).show();
    }

    @Override
    public void redirectToDashboardSuccessCase(String messageVov, String messageToast) {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(23, 20, VoiceOfVodafoneCategory.Recharge, null, messageVov, null, VodafoneTvLabels.getVtvVovPrimaryButtonResetPin(), false, true, VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.Dismiss);
        voiceOfVodafone.setIntentActionName(IntentActionName.VODAFONE_TV);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        new CustomToast.Builder(getActivity()).message(messageToast).success(true).show();
        new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
    }


    @Override
    public void setupInformativeText(String message) {
        informativeMessage.setText(message);
        informativeCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideActiveOffers() {
        activeOffersLabel.setVisibility(View.GONE);
        mActiveServicesViewGroup.setVisibility(View.GONE);
        beoPageRedirectButton.setVisibility(View.GONE);
        if (offersErrorCard != null)
            offersErrorCard.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetTvSelector();
        binder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getListOfDevices();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.unsubscribe();
    }

    private void resetTvSelector() {
        SelectorDialogActivity.setSelectedObject(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber());
        UserSelectedMsisdnBanController.getInstance().setSelectedFixedNet(null);
        UserSelectedMsisdnBanController.getInstance().setSelectedSubscriberId(null);
        if(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null)
            UserSelectedMsisdnBanController.getInstance().setTvSelectedMsisdn(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn());
    }

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            activeDevicesList.expandGroup(groupPosition);
            return false;
        }
    };

    private void clearDevicesContainerIfHasChilds() {
        if (activeDevicesList.getChildCount() > 0)
            activeDevicesList.removeAllViews();
        if (devicesErrorCard != null && devicesErrorCard.getVisibility() == View.VISIBLE) {
            activeDevicesContainer.removeView(devicesErrorCard);
            devicesErrorCard = null;
        }

        if (informativeCard.getVisibility() == View.VISIBLE) {
            informativeCard.setVisibility(View.GONE);
        }
    }

    private void clearOffersContainerIfHasChilds() {
        if (mActiveServicesViewGroup.getChildCount() > 0)
            mActiveServicesViewGroup.removeAllViews();
        if (offersErrorCard != null && offersErrorCard.getVisibility() == View.VISIBLE) {
            activeOffersContainer.removeView(offersErrorCard);
            offersErrorCard = null;
        }
    }


    @OnClick(R.id.reset_pin_button)
    public void resetPinButton(View view) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv);
        tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.vodafoneTv_reset_pin);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

        presenter.resetPINRequest(mUserRole, defaultUserId);
    }

    private void setupButtons() {
        beoPageRedirectButton.setText(VodafoneTvLabels.getVtvBeoRedirectButtonLabel());
        vodafoneTvAppButton.setText(VodafoneTvLabels.getVtvAppRedirectButtonLabel());
        beoPageRedirectButton.setOnClickListener(mBeoRedirectClickListener);
        vodafoneTvAppButton.setOnClickListener(mVodafoneAppRedirect);
        beoPageRedirectButton.setVisibility(View.VISIBLE);
        vodafoneTvAppButton.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener mBeoRedirectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.vodafoneTv);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.vodafoneTv_beo_redirect);
            if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

            otherOptionsButtonTrackingEvent();
            new NavigationAction(getContext()).finishCurrent(true).startAction(IntentActionName.OFFERS_SERVICES);
        }
    };

    private View.OnClickListener mVodafoneAppRedirect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // TODO: 28.06.2018 insert deeplink to VodafoneTV app
            goToVtvButtonTrackingEvent();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(AppConfiguration.getVTVListingExternalDeeplink()));
            ActivityInfo activityInfo = intent.resolveActivityInfo(getActivity().getPackageManager(), intent.getFlags());
            if (activityInfo != null && activityInfo.exported) {
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "There are no Apps installed on this device to handle the intent!");
                }
            }
        }
    };

    private void otherOptionsButtonTrackingEvent() {
        VodafoneTvTrackingEvent event = new VodafoneTvTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "activate other options";
        journey.eVar82 = "mcare:vodafone tv:button:activate other options";
        journey.getContextData().put("eVar82", journey.eVar82);
        journey.getContextData().put("event65", journey.event65);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void goToVtvButtonTrackingEvent() {
        VodafoneTvTrackingEvent event = new VodafoneTvTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "go to vtv";
        journey.eVar82 = "mcare:vodafone tv:button:go to vtv";
        journey.getContextData().put("eVar82", journey.eVar82);
        journey.getContextData().put("event65", journey.event65);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public static class VodafoneTvTrackingEvent extends TrackingEvent {
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "vodafone tv";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "vodafone tv");
            s.channel = "vodafone tv";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "vodafone tv";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
    public void getListOfDevices() {
        //if fixed net selected
        if (UserSelectedMsisdnBanController.getInstance().tvServiceSelector()) {
            if (UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getServiceType().toLowerCase().contains("fixed"))
                presenter.getListOfActiveDevices(mUserRole, UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getServiceId(),
                        UserSelectedMsisdnBanController.getInstance().getSelectedSubscriberId(), "fixed");
            else
                presenter.getListOfActiveDevices(mUserRole, UserSelectedMsisdnBanController.getInstance().getSelectedFixedNet().getServiceId(),
                        UserSelectedMsisdnBanController.getInstance().getSelectedSubscriberId(), "");
        } // if voice selected from new api
        else if (UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn() != null &&
                UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber(UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn()) != null)
         {
            String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn();
            Subscriber s = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber(selectedMsisdn);
            presenter.getListOfActiveDevices(mUserRole, selectedMsisdn, s.getSid(), "");
        } // if voice selected from old api
        else {
            presenter.getListOfActiveDevices(mUserRole, UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(), UserSelectedMsisdnBanController.getInstance().getSubscriberSid(), "");
        }
    }

}
