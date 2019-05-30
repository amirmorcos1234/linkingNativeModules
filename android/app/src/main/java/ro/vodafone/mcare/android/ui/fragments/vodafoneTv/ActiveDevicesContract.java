package ro.vodafone.mcare.android.ui.fragments.vodafoneTv;

import java.util.List;

import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyRequest;
import ro.vodafone.mcare.android.interfaces.MvpContract;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public interface ActiveDevicesContract {

    interface DevicesListView extends MvpContract.View  {
        void showLoading();
        void hideLoading();
        void setVisibilityResetPinCard(boolean showRestPin,String defaultUserID);
        void populateDevicesViews(List<DeviceFamiliesList> deviceFamiliesList);
        void populateVtvActiveOffers(List<OfferRowInterface> offersList);
        void showDevicesErrorCard(String message, boolean isApiError);
        void showOffersErrorCard(String message, boolean isApiError);
        void showOffersErrorToast(String message);
        void redirectToDashboardSuccessCase(String messageVov,String messageToast);
        void setupInformativeText(String message);
        void hideActiveOffers();
    }

    interface DeviceDetailsView extends MvpContract.View{
        void showErrorMessage();
        void redirectToDashboardSuccessCase(String message);
        void displayLimitExceededOverlay();
        void displayConfirmDeleteOverlay();
        void showLoading();
        void hideLoading();
    }

    interface Presenter {
        void resetPINRequest(String vfSsoUserRole,String defaultUserId);
        void getListOfActiveDevices(String vfSsoUserRole, String selectedMsisdn, String sid, String type);
        void getTvHierarchy(String vfSsoUsername, String vfPhoneNumber, String vfSsoUserRole,boolean vfEBUMigrated,String vfFixedBan, String vfFixedCid, String banList);
        void renameDevice(String vfSsoUserRole, String externalId, String udid);
        void deleteDevice(String vfSsoUserRole, String udid);
        void onDeleteDeviceClicked();
        void unsubscribe();
    }

}
