package ro.vodafone.mcare.android.ui.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;


public class SettingsFragment extends BaseFragment {
    public static String TAG = "SettingsFragment";

    int scrollX = 0;
    boolean isSettingsFragment = true;
    int scrollY = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_settings, null);

        final AdapterBackedLinearLayout nonScrollAbleListView = (AdapterBackedLinearLayout) v.findViewById(R.id.settingsListview);

        ArrayList<SettingsElement> array = createArrayList();
        SettingsCardsAdapter adapter = new SettingsCardsAdapter(getContext(), (SettingsActivity) getActivity(), array);

        nonScrollAbleListView.setAdapter(adapter);
        nonScrollAbleListView.setClickable(false);
        nonScrollAbleListView.setFocusable(false);

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","settings");
        tealiumMapView.put("journey_name","settings");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        SettingsTrackingEvent event = new SettingsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        if (isSettingsFragment) {
            ((SettingsActivity) getActivity()).getNavigationHeader().displayDefaultHeader();
            ((SettingsActivity) getActivity()).getNavigationHeader().hideSelectorView();
        } else {
            ((SettingsActivity) getActivity()).getMenuScrollView().scrollTo(0,0);
            try {
                ((SettingsActivity) getActivity()).getToolbar().showToolBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return v;
    }

    public ArrayList<SettingsElement> createArrayList() {
        ArrayList<SettingsElement> array = new ArrayList<>();

        SettingsElement accountSettings = new SettingsElement(SettingsLabels.getSettingsYourAccountTitle(), "", 3);
        SettingsElement autoAutentif = new SettingsElement(SettingsLabels.getSettingsAuthentificationTitle(), SettingsLabels.getSettingsAuthentificationSubTitle(), 2);
        SettingsElement resetPassword = new SettingsElement(getResources().getString(R.string.resetPassword), "", 1);

        SettingsElement electronicBill = new SettingsElement(getResources().getString(R.string.electronicBill), "", 1);
        SettingsElement blockSIM = new SettingsElement(getResources().getString(R.string.blockSIM), "", 1);
        SettingsElement simDetails = new SettingsElement(SettingsLabels.getSettingsSimDetailsTitle(), "", 1);

        SettingsElement customizationSettings = new SettingsElement(SettingsLabels.getSettingsCustomizeYourAppTitle(), "", 3);
        SettingsElement services = new SettingsElement(getResources().getString(R.string.customizeAccount), SettingsLabels.getSettingsPersonalizeYourServicesSubtitle(), 1);
        SettingsElement confidentiality = new SettingsElement(getResources().getString(R.string.confidentiality), "", 1);
        SettingsElement notifications = new SettingsElement(getResources().getString(R.string.notifications), "", 1);
        SettingsElement permissions = new SettingsElement(getResources().getString(R.string.permissions), "", 1);

        SettingsElement infoSettings = new SettingsElement(SettingsLabels.getSettingsUtilsInfoTitle(), "", 3);
        SettingsElement terms = new SettingsElement(getResources().getString(R.string.terms), "", 1);
        SettingsElement confidentialityPolicy = new SettingsElement(getResources().getString(R.string.confidentialityPolicy), "", 1);

        array.add(accountSettings);

        User user = VodafoneController.getInstance().getUser();

        if(!VodafoneController.getApi9Failed()) {
            array.add(autoAutentif);
        }

        if(!(user instanceof SeamlessEbuUser)) {
            array.add(resetPassword);
        }

        if (checkUserRights(1, user)) {
            if (!(user instanceof ResSub)) {
                array.add(electronicBill);
            } else {
                D.w();
            }
        }


        if (user instanceof PostPaidUser) {
            if (!(user instanceof EbuNonMigrated) && !(user instanceof SeamlessPostPaidsLowAccess)
                    && !(user instanceof SeamlessPostPaidHighAccess)) {
                array.add(blockSIM);
            }
        }

        if (user instanceof PostPaidUser) {
            if (!(user instanceof EbuNonMigrated) && !(user instanceof SeamlessPostPaidsLowAccess)
                    && !(user instanceof SeamlessPostPaidHighAccess)) {
                array.add(simDetails);
            }
        }

        array.add(customizationSettings);

        if(!(user instanceof SeamlessEbuUser)) {
            array.add(services);
        }

        array.add(confidentiality);
        array.add(notifications);

        if (GdprController.shoudPerformGetPermissions()) {
            array.add(permissions);
        }

        array.add(infoSettings);
        array.add(terms);
        array.add(confidentialityPolicy);

        return array;

    }

    private boolean checkUserRights(int type, User user) {
        boolean userRights = false;

        switch (type) {
            case 1: // for Electronic Bill
                userRights = user instanceof ChooserUser
                        || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser
                        || user instanceof ResCorp
                        || user instanceof PrivateUser;
//                || user instanceof PostPaidUser
                break;
            default:
                break;
        }

        return userRights;
    }

    public String getTitle() {
        return ((String) SettingsLabels.getSettingsPageTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((SettingsActivity) getActivity()).getMenuScrollView() != null) {
            ((SettingsActivity) getActivity()).getMenuScrollView().post(new Runnable() {
                @Override
                public void run() {
                    if (isSettingsFragment && getActivity() != null && !getActivity().isFinishing())
                        ((SettingsActivity) getActivity()).getMenuScrollView().scrollTo(scrollX, scrollY);
                }
            });
        }

        ((SettingsActivity) getActivity()).getNavigationHeader().setTitle(getTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (((SettingsActivity) getActivity()).getMenuScrollView() != null) {
            scrollX = ((SettingsActivity) getActivity()).getMenuScrollView().getScrollX();
            scrollY = ((SettingsActivity) getActivity()).getMenuScrollView().getScrollY();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        makeAdobeRequest();
    }

    public void makeAdobeRequest(){
        callForAdobeTarget(AdobePageNamesConstants.PG_SETTINGS);
    }

    public static class SettingsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "settings";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "settings");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }


}


