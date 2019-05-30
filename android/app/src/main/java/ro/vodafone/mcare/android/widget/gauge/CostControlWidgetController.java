package ro.vodafone.mcare.android.widget.gauge;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Victor Radulescu on 2/13/2017.
 */

public class CostControlWidgetController<T> {

    private final static String TAG = "CostControl";

    private boolean dataCompleted = false;

    private boolean badData = false;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private static CostControlWidgetController instance;

    private ArrayList<CostControlRequestListener> costControlRequestListeners;

    private String currentMsisdn;

  /*  protected boolean isPendingOffers = false;

    public boolean isPendingOffers() {
        return isPendingOffers;
    }

    public void setPendingOffers(boolean pendingOffers) {
        isPendingOffers = pendingOffers;
    }*/

    private CostControlWidgetController() {
        costControlRequestListeners = new ArrayList<>();
    }

    public synchronized static CostControlWidgetController getInstance() {

        if (instance == null) {
            instance = new CostControlWidgetController();
        }
        return instance;
    }

    public CostControlWidgetController setup() {
        if (costControlRequestListeners != null) {
            costControlRequestListeners.clear();
        }
        dataCompleted = false;
        return this;
    }

    public synchronized static void deleteInstance() {
        getInstance().destroy();
    }

    public Observable requestData() {

        Observable<GeneralResponse<CostControl>> responseObservable = null;
        try {
            responseObservable = new UserDataService(VodafoneController.getInstance()).loadCostControl(getCurrentMsisdn());

            Subscription subscription = responseObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<CostControl>>() {
                @Override
                public void onCompleted() {
                    dataCompleted = true;

                    Log.d("CostControlWidgetCont", "CostControlWidgetController: finish");
                    CostControl costControl = (CostControl) RealmManager.getRealmObject(CostControl.class);
                    if (costControl != null && costControl.getCurrentExtraoptions() != null &&
                            costControl.getCurrentExtraoptions().getShortBalanceList() != null
                            && costControl.getCurrentExtraoptions().getShortBalanceList().isValid()) {
                        badData = false;
                        callOnCostControlRequestSucces(costControl);
                    } else {
                        badData = true;
                        callOnCostControlRequestFailed("Cost control null");
                    }
                }

                @Override
                public void onNext(GeneralResponse<CostControl> response) {
                    super.onNext(response);
                    dataCompleted = true;
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    callOnCostControlRequestFailed(e.getMessage());
                    badData = true;
                    dataCompleted = true;
                }
            });
            compositeSubscription.add(subscription);

            //Load ExtraOffers from API-22
            requestDataExtraOptions(null, true);

        } catch (Exception e) {
            dataCompleted = true;
            e.printStackTrace();
            callOnCostControlRequestFailed(e.getMessage());
        }
        return responseObservable;
    }

    private String getCurrentMsisdn() {
        if (currentMsisdn == null) {
            currentMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        }
        return currentMsisdn;
    }

    public void setCurrentMsisdn(String currentMsisdn) {
        this.currentMsisdn = currentMsisdn;
    }

    public void requestDataExtraOptions(EligibleOffersRequestListener listener, boolean updateCostControlWidget) {
        if (VodafoneController.getInstance().getUser() == null) {
            throw new RuntimeException("CostControlWidget: VodafoneController.getInstance().getUser()==null");
        }
        if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
            getEligibleOfferForCBU(listener, updateCostControlWidget);
        }else if (VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            getEligibleOfferForEBU(listener, updateCostControlWidget);
        }else if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            getEligibleOfferForPrepaid(listener, updateCostControlWidget);
        } else {
            safeCallOnSuccesEligibleOffersRequestFailed(listener);
        }
    }


    private void getEligibleOfferForCBU(final EligibleOffersRequestListener listener, final boolean updateCostControlWidget) {
        if (VodafoneController.getInstance().getUserProfile() == null) {
            safeCallOnSuccesEligibleOffersRequestFailed(listener);
            return;
        }

        final String sid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid()
                : VodafoneController.getInstance().getUserProfile().getSid();

        final String msisdn = getCurrentMsisdn();
        final Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        final Integer bcd = profile != null ? (profile.getBillCycleDate() != null ? profile.getBillCycleDate() : 0) : 0;

        OffersService offersService = new OffersService(VodafoneController.getInstance());
        Subscription subscription = offersService.getEligibleOffers4PostPaid(msisdn, sid, bcd).subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {

            @Override
            public void onCompleted() {
                if (updateCostControlWidget) {
                    updateExtraOptions();
                }
            }

            @Override
            public void onNext(GeneralResponse<EligibleOffersPostSuccess> response) {
                if (response == null) {
                    safeCallOnSuccesEligibleOffersRequestFailed(listener);
                } else {
                    if (response.getTransactionSuccess() != null) {
                        // Log.d(TAG, "UnicaOffersSuccess getTransactionSuccess: "+response.getTransactionSuccess());
                        super.onNext(response);

                        OffersService offersService = new OffersService(VodafoneController.getInstance());
                        offersService.getPendingOffers4PostPaid(sid, "offer").subscribe(new RequestSessionObserver<GeneralResponse>() {
                            @Override
                            public void onNext(GeneralResponse activeOffersSuccessResponse) {
                                Log.d(TAG, "getPendingOffers4PostPaid() onNext");
                                if (activeOffersSuccessResponse.getTransactionStatus() == 0) {
                                    Log.d(TAG, "getPendingOffers4PostPaid() Transaction Status = 0");
                                    safeCallOnSuccesEligibleOffersRequestSucces(listener);
                                } else if (activeOffersSuccessResponse.getTransactionStatus() == 1) {
                                    safeEligibleOffersRequestPendingOffers(listener);
                                    Log.e(TAG, "getPendingOffers4PostPaid() Transaction Status != 0 => ERROR");
                                } else {
                                    safeCallOnSuccesEligibleOffersRequestFailed(listener);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                safeCallOnSuccesEligibleOffersRequestFailed(listener);
                            }
                        });


                        //safeCallOnSuccesEligibleOffersRequestSucces(listener);
                    } else {
                        safeCallOnSuccesEligibleOffersRequestFailed(listener);
                        if (response.getTransactionFault() != null) {
                            Log.e(TAG, "EligibleOffersPostSuccess getTransactionFault: " + response.getTransactionFault().getFaultMessage());
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                safeCallOnSuccesEligibleOffersRequestFailed(listener);
            }
        });

        compositeSubscription.add(subscription);

    }

    private void getEligibleOfferForEBU(final EligibleOffersRequestListener listener, final boolean updateCostControlWidget){

        User user = VodafoneController.getInstance().getUser();
        if (!(user instanceof SeamlessEbuUser)) {
            if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() == null){
                safeCallOnSuccesEligibleOffersRequestFailed(listener);
                return;
            }

            if(isAccesRestricted() || isUserNotAllowedToActiveEO()){
                safeCallOnSuccesEligibleOffersRequestFailed(listener);
                return;
            }
        }


        final String vfCRMRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null
                ? EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole()
                : "";

        final String sid = UserSelectedMsisdnBanController.getInstance().getSubscriberSid() != null
                ? UserSelectedMsisdnBanController.getInstance().getSubscriberSid()
                : VodafoneController.getInstance().getUserProfile().getSid();

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn()
                : VodafoneController.getInstance().getUserProfile().getMsisdn();

        Subscription subscription = new OffersService(VodafoneController.getInstance())
                .getEligibleOffers4EBU(vfCRMRole, msisdn,null).subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {

            @Override
            public void onNext(GeneralResponse<EligibleOffersPostSuccess> eligibleOffersPostSuccessResponse) {
                super.onNext(eligibleOffersPostSuccessResponse);

                if(eligibleOffersPostSuccessResponse == null
                        || eligibleOffersPostSuccessResponse.getTransactionSuccess() == null){
                    safeCallOnSuccesEligibleOffersRequestFailed(listener);
                }else{
                    super.onNext(eligibleOffersPostSuccessResponse);

                    new OffersService(VodafoneController.getInstance()).getPendingOffers4EBU(vfCRMRole, sid,"").subscribe(new RequestSessionObserver<GeneralResponse>() {
                        @Override
                        public void onNext(GeneralResponse activeOffersSuccessResponse) {
                            if (activeOffersSuccessResponse.getTransactionStatus() == 0) {
                                safeCallOnSuccesEligibleOffersRequestSucces(listener);

                                if (updateCostControlWidget) {
                                    updateExtraOptions();
                                }
                            } else if (activeOffersSuccessResponse.getTransactionStatus() == 1) {
                                safeEligibleOffersRequestPendingOffers(listener);
                                /*setPendingOffers(true);*/
                            } else {
                                safeCallOnSuccesEligibleOffersRequestFailed(listener);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            safeCallOnSuccesEligibleOffersRequestFailed(listener);
                        }
                    });
                }
            }

            @Override
            public void onCompleted() {
                /*if (updateCostControlWidget) {
                    updateExtraOptions();
                }*/
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                safeCallOnSuccesEligibleOffersRequestFailed(listener);
            }
        });

        compositeSubscription.add(subscription);
    }

    private void getEligibleOfferForPrepaid(final EligibleOffersRequestListener listener, final boolean updateCostControlWidget) {
        Subscription subscription = new OffersService(VodafoneController.getInstance()).getEligibleOffers4PrePaid().subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersSuccess>>() {
            @Override
            public void onCompleted() {
                if (updateCostControlWidget) {
                    updateExtraOptions();
                }
            }

            @Override
            public void onNext(GeneralResponse<EligibleOffersSuccess> response) {
                if (response == null) {
                    safeCallOnSuccesEligibleOffersRequestFailed(listener);
                } else {
                    if (response.getTransactionSuccess() != null) {
                        // Log.d(TAG, "UnicaOffersSuccess getTransactionSuccess: "+response.getTransactionSuccess());
                        super.onNext(response);
                        safeCallOnSuccesEligibleOffersRequestSucces(listener);
                    } else {
                        safeCallOnSuccesEligibleOffersRequestFailed(listener);
                        if (response.getTransactionFault() != null) {
                            Log.e(TAG, "EligibleOffersSuccess getTransactionFault: " + response.getTransactionFault().getFaultMessage());
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                safeCallOnSuccesEligibleOffersRequestFailed(listener);
            }
        });
        compositeSubscription.add(subscription);
    }

    private boolean isAccesRestricted(){
        CustomerRestrictionsSuccess customerRestrictionsSuccess = (CustomerRestrictionsSuccess)
                RealmManager.getRealmObject(CustomerRestrictionsSuccess.class);

        return customerRestrictionsSuccess != null && (customerRestrictionsSuccess.getIsBlacklistForever()
                || customerRestrictionsSuccess.getIsCollectionRestricted() || customerRestrictionsSuccess.getIsDeviceBlacklist()
                || customerRestrictionsSuccess.getIsServiceBadDebt());
    }

    private boolean isUserNotAllowedToActiveEO(){
        return  EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null
                && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment() != null
                && AppConfiguration.getEbuMigratedIneligibleToDeleteOfferSegment()
                .contains(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment());
    }

    private void safeCallOnSuccesEligibleOffersRequestSucces(EligibleOffersRequestListener listener) {
        if (listener != null) {
            listener.onEligibleOffersRequestSucces();
        }
    }

    private void safeCallOnSuccesEligibleOffersRequestFailed(EligibleOffersRequestListener listener) {
        if (listener != null) {
            listener.onEligibleOffersRequestFailed();
        }
    }

    private void safeEligibleOffersRequestPendingOffers(EligibleOffersRequestListener listener) {
        if (listener != null) {
            listener.onEligibleOffersRequestPendingOffers();
        }
    }

    private void updateExtraOptions() {

        List<? extends RealmObject> extraCategories = getEligibleOffersCategoriesAfterUser();

        List<? extends RealmObject> extraServices = getEligibleOffersServicesAfterUser();

        if (extraCategories != null || extraServices != null) {
            callOnExtraOptionsRequestCompleted();
        }
    }

    private List<? extends RealmObject> getEligibleOffersCategoriesAfterUser() {
        try {
            if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);
                if (eligibleOffersPostSuccess != null) {
                    return eligibleOffersPostSuccess.getEligibleOptionsCategories();
                }
            } else if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                EligibleOffersSuccess eligibleOffersPostSuccess = (EligibleOffersSuccess) RealmManager.getRealmObject(EligibleOffersSuccess.class);
                if (eligibleOffersPostSuccess != null) {
                    return eligibleOffersPostSuccess.getEligibleOptionsCategories();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<? extends RealmObject> getEligibleOffersServicesAfterUser() {
        try {
            if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);
                if (eligibleOffersPostSuccess != null) {
                    return eligibleOffersPostSuccess.getEligibleServicesCategories();
                }
            } else if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                EligibleOffersSuccess eligibleOffersPostSuccess = (EligibleOffersSuccess) RealmManager.getRealmObject(EligibleOffersSuccess.class);
                if (eligibleOffersPostSuccess != null) {
                    return eligibleOffersPostSuccess.getEligibleServicesCategories();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isBadData() {
        return badData;
    }


    public boolean isDataCompleted() {
        return dataCompleted;
    }


    public void addCostControlRequestListener(CostControlRequestListener listener) {
        if (instanceAlreadyRegister(listener)) {
            return;
        }
        costControlRequestListeners.add(listener);
    }

    public void callOnCostControlRequestSucces(CostControl costControl) {
        for (CostControlRequestListener listener : costControlRequestListeners) {
            listener.onCostControlRequestSucces(costControl);
        }
    }

    /*public void callOnCostControlUpdate(CostControl costControl){
        for (CostControlRequestListener listener:costControlRequestListeners) {
            listener.onCostControlUpdate(costControl);
        }
    }*/
    public void callOnExtraOptionsRequestCompleted() {
        for (CostControlRequestListener listener : costControlRequestListeners) {
            listener.onExtraOptionsRequestCompleted();
        }
    }

    public void callOnCostControlRequestFailed(String error) {
        for (CostControlRequestListener listener : costControlRequestListeners) {
            listener.onCostControlRequestFailed(error);
        }
    }

    private boolean instanceAlreadyRegister(CostControlRequestListener newListener) {
        if (costControlRequestListeners == null || costControlRequestListeners.isEmpty()) {
            return false;
        }
        return costControlRequestListeners.contains(newListener);
    }

    public void destroy() {
        compositeSubscription.clear();
        instance = null;
    }

    public interface CostControlRequestListener {
        void onCostControlRequestSucces(CostControl costControl);

        void onCostControlRequestFailed(String error);

        void onExtraOptionsRequestCompleted();
//        void onCostControlUpdate(CostControl costControl);
    }

    public void clearData() {
        instance = null;
    }

    public interface EligibleOffersRequestListener {
        void onEligibleOffersRequestSucces();

        void onEligibleOffersRequestFailed();

        void onEligibleOffersRequestPendingOffers();
    }
}
