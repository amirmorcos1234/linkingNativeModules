package ro.vodafone.mcare.android.ui.fragments.vodafoneTv;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeviceFamiliesList;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.VodafoneTvService;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;



/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class ActiveDevicesPresenter implements ActiveDevicesContract.Presenter {
    private VodafoneTvService mVodafoneTvService;
    private ActiveDevicesContract.DevicesListView mView;
    private ActiveDevicesContract.DeviceDetailsView mDeviceDetailsView;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private OffersService mOffersService;

    public ActiveDevicesPresenter(ActiveDevicesContract.DevicesListView view, VodafoneTvService vodafoneTvService, OffersService offersService) {
        mVodafoneTvService = vodafoneTvService;
        mView = view;
        mOffersService = offersService;
    }

    public ActiveDevicesPresenter(ActiveDevicesContract.DeviceDetailsView view, VodafoneTvService vodafoneTvService) {
        mVodafoneTvService = vodafoneTvService;
        mDeviceDetailsView = view;
    }

    @Override
    public void resetPINRequest(String vfSsoUserRole, String defaultUserId) {

        Subscription subscription = mVodafoneTvService.resetPIN(vfSsoUserRole, defaultUserId)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mView != null)
                            mView.showLoading();
                    }
                })
                .subscribe(new RequestSessionObserver<GeneralResponse<ResetPinSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ResetPinSuccess> generalResponse) {
                        if (generalResponse == null || generalResponse.getTransactionStatus() != 0) {
                            if (mView != null)
                                mView.showOffersErrorToast(VodafoneTvLabels.getVtvToastErrorMessage());
                        } else {
                            if (mView != null)
                                mView.redirectToDashboardSuccessCase(VodafoneTvLabels.getVtvSuccessResetPinVov()+generalResponse.getTransactionSuccess().getNewPin(),VodafoneTvLabels.getVtvSuccessResetPinToast());
                        }
                    }
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mView.hideLoading();
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.hideLoading();
                        if (mView != null)
                            mView.showOffersErrorToast(VodafoneTvLabels.getVtvToastErrorMessage());
                    }
                });
        compositeSubscription.add(subscription);
    }


    @Override
    public void getListOfActiveDevices(String vfSsoUserRole, String selectedMsisdn, String sid, String type) {
        if(selectedMsisdn != null)
            selectedMsisdn = selectedMsisdn.trim();
        if(sid != null)
            sid = sid.trim();

        Observable<GeneralResponse<GetByOperatorSuccess>> devicesByOperatorObservable = mVodafoneTvService.getDevicesByOperator(vfSsoUserRole, selectedMsisdn).onErrorReturn(new Func1<Throwable, GeneralResponse<GetByOperatorSuccess>>() {
            @Override
            public GeneralResponse<GetByOperatorSuccess> call(Throwable throwable) {
                return null;
            }
        });
        Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> activeOffersPostpaidObservable = Observable.just(null);
        if(!UserSelectedMsisdnBanController.getInstance().getIfFixedSelected()) {
            activeOffersPostpaidObservable = mOffersService.getActiveOffersPostpaid(selectedMsisdn, sid, type).onErrorReturn(new Func1<Throwable, GeneralResponse<ActiveOffersPostpaidSuccess>>() {
                @Override
                public GeneralResponse<ActiveOffersPostpaidSuccess> call(Throwable throwable) {
                    return null;
                }
            });
        }
        Observable<GeneralResponse<ActiveOffersSuccess>> activeOffersPrepaidObservable = mOffersService.getEligibleActiveOffers4PrePaid(sid).onErrorReturn(new Func1<Throwable, GeneralResponse<ActiveOffersSuccess>>() {
            @Override
            public GeneralResponse<ActiveOffersSuccess> call(Throwable throwable) {
                return null;
            }
        });

        User user = VodafoneController.getInstance().getUser();
        Observable<GeneralResponse<GetByOperatorSuccess>> resultObservable = null;
        if (user instanceof PrepaidUser) {
            resultObservable = Observable.zip(devicesByOperatorObservable, activeOffersPrepaidObservable, mPrepaidObservablesFunc);
        } else if (user instanceof CBUUser) {
            resultObservable = Observable.zip(devicesByOperatorObservable, activeOffersPostpaidObservable, mPostpaidObservablesFunc);
        }

        if (resultObservable == null)
            return;


        resultObservable.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (mView != null)
                    mView.showLoading();
            }
        }).subscribe(new RequestSaveRealmObserver<GeneralResponse<GetByOperatorSuccess>>() {
            @Override
            public void onNext(GeneralResponse<GetByOperatorSuccess> generalResponse) {
                super.onNext(generalResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                if (mView != null)
                    mView.hideLoading();
            }
        });
    }

    @Override
    public void getTvHierarchy(String vfSsoUsername, String vfPhoneNumber, String vfSsoUserRole, boolean vfEBUMigrated,
                               String vfFixedBan, String vfFixedCid, String banList) {


        UserDataService userDataService = new UserDataService(VodafoneController.getInstance());
        userDataService.getTvHierarchy(vfSsoUsername,vfPhoneNumber,vfSsoUserRole,vfEBUMigrated,vfFixedBan,vfFixedCid,banList)
                .subscribe((new RequestSaveRealmObserver<GeneralResponse<TvHierarchyResponse>>() {

                    @Override
                    public void onNext(GeneralResponse<TvHierarchyResponse> tvHierarchyResponse) {
                        super.onNext(tvHierarchyResponse);
                        if(tvHierarchyResponse.getTransactionStatus() != 0){
                            RealmManager.update(new TvHierarchyResponse());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RealmManager.update(new TvHierarchyResponse());
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));
    }

    private Func2<GeneralResponse<GetByOperatorSuccess>, GeneralResponse<ActiveOffersSuccess>, GeneralResponse<GetByOperatorSuccess>> mPrepaidObservablesFunc = new Func2<GeneralResponse<GetByOperatorSuccess>, GeneralResponse<ActiveOffersSuccess>, GeneralResponse<GetByOperatorSuccess>>() {
        @Override
        public GeneralResponse<GetByOperatorSuccess> call(GeneralResponse<GetByOperatorSuccess> getByOperatorSuccessGeneralResponse, GeneralResponse<ActiveOffersSuccess> activeOffersResponse) {
            checkGetByOperatorResponse(getByOperatorSuccessGeneralResponse);

            if (activeOffersResponse != null &&
                    activeOffersResponse.getTransactionSuccess() != null &&
                    activeOffersResponse.getTransactionStatus() == 0) {
                if (activeOffersResponse.getTransactionSuccess().getActiveServicesList().isEmpty() ||
                        getPrepaidListOfVtvActiveOffers(activeOffersResponse.getTransactionSuccess().getActiveServicesList()).isEmpty()) {
                    mView.showOffersErrorCard(VodafoneTvLabels.getVtvNoOffersError(), false);
                } else {
                    mView.populateVtvActiveOffers(getPrepaidListOfVtvActiveOffers(activeOffersResponse.getTransactionSuccess().getActiveServicesList()));
                }
            } else {
                mView.showOffersErrorCard(VodafoneTvLabels.getGenericRetryErrorMessage(), true);
            }
            return getByOperatorSuccessGeneralResponse;
        }
    };

    private Func2<GeneralResponse<GetByOperatorSuccess>, GeneralResponse<ActiveOffersPostpaidSuccess>, GeneralResponse<GetByOperatorSuccess>> mPostpaidObservablesFunc = new Func2<GeneralResponse<GetByOperatorSuccess>, GeneralResponse<ActiveOffersPostpaidSuccess>, GeneralResponse<GetByOperatorSuccess>>() {
        @Override
        public GeneralResponse<GetByOperatorSuccess> call(GeneralResponse<GetByOperatorSuccess> getByOperatorSuccessGeneralResponse, GeneralResponse<ActiveOffersPostpaidSuccess> activeOffersPostpaidResponse) {
            checkGetByOperatorResponse(getByOperatorSuccessGeneralResponse);
            if(UserSelectedMsisdnBanController.getInstance().getIfFixedSelected()){
                //hide active offers for fixed Net user
                mView.hideActiveOffers();
            }
            else if (activeOffersPostpaidResponse != null &&
                    activeOffersPostpaidResponse.getTransactionSuccess() != null &&
                    activeOffersPostpaidResponse.getTransactionStatus() == 0) {
                if (activeOffersPostpaidResponse.getTransactionSuccess().getActiveServicesList().isEmpty() ||
                        getPostpaidListOfVtvActiveOffers(activeOffersPostpaidResponse.getTransactionSuccess().getActiveServicesList()).isEmpty()) {
                    mView.showOffersErrorCard(VodafoneTvLabels.getVtvNoOffersError(), false);
                } else {
                    mView.populateVtvActiveOffers(getPostpaidListOfVtvActiveOffers(activeOffersPostpaidResponse.getTransactionSuccess().getActiveServicesList()));
                }
            } else {
                mView.showOffersErrorCard(VodafoneTvLabels.getGenericRetryErrorMessage(), true);
            }
            return getByOperatorSuccessGeneralResponse;
        }
    };

    private List<OfferRowInterface> getPostpaidListOfVtvActiveOffers(List<ActiveOfferPostpaid> totalOffers) {
        List<OfferRowInterface> activeVtvOffers = new ArrayList<>();
        for (OfferRowInterface offerRow : totalOffers) {
            if (AppConfiguration.getVodafoneTvCategories().contains(offerRow.getOfferCategory())) {
                activeVtvOffers.add(offerRow);
            }
        }
        return activeVtvOffers;
    }

    private List<OfferRowInterface> getPrepaidListOfVtvActiveOffers(List<ActiveOffer> totalOffers) {
        List<OfferRowInterface> activeVtvOffers = new ArrayList<>();
        for (ActiveOffer offerRow : totalOffers) {
            if (offerRow.getOfferCategoryCode().equals("OTTP")) {
                activeVtvOffers.add(offerRow);
            }
        }

        return activeVtvOffers;
    }

    private void checkGetByOperatorResponse(GeneralResponse<GetByOperatorSuccess> response) {
        if (response != null && response.getTransactionSuccess() != null) {
            if (response.getTransactionSuccess().getDeviceFamilies().isEmpty()) {
                mView.showDevicesErrorCard(VodafoneTvLabels.getVodafoneTvNoDevicesMessage(), false);
                mView.setVisibilityResetPinCard(false, null);
            } else {
                mView.populateDevicesViews(response.getTransactionSuccess().getDeviceFamilies());
                String informativeMessage = VodafoneTvLabels.getVtvInformativeText().replace("{devicesLimit}", response.getTransactionSuccess().getDevicesLimit().toString());
                if (response.getTransactionSuccess().getDevicesLimit() <= getTotalNumberOfActiveDevices(response.getTransactionSuccess())) {
                    informativeMessage = informativeMessage.concat(" " + VodafoneTvLabels.getVtvDeviceLimitExceededInformativeText());
                }
                mView.setupInformativeText(informativeMessage);
                if (response.getTransactionSuccess().getDefaultUserId() != null)
                    mView.setVisibilityResetPinCard(true, response.getTransactionSuccess().getDefaultUserId());
                else
                    mView.setVisibilityResetPinCard(false, null);
            }
        } else {
            if (response != null && response.getTransactionFault().getFaultCode()
                    .equals(ErrorCodes.API72_USER_NOT_EXISTS.getErrorCode())) {
                mView.showDevicesErrorCard(VodafoneTvLabels.getVodafoneTvNoDevicesMessage(), false);
            } else
                mView.showDevicesErrorCard(VodafoneTvLabels.getGenericRetryErrorMessage(), true);
            mView.setVisibilityResetPinCard(false,null);
        }
    }


    @Override
    public void renameDevice(String vfSsoUserRole, String deviceAlias, String udid) {
        Realm realm = Realm.getDefaultInstance();
        GetByOperatorSuccess getByOperatorObject = (GetByOperatorSuccess) RealmManager.getRealmObject(realm, GetByOperatorSuccess.class);
        realm.close();

        Subscription subscription = mVodafoneTvService.renameActiveDevice(vfSsoUserRole, deviceAlias, getByOperatorObject.getExternalId(), udid)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mDeviceDetailsView != null)
                            mDeviceDetailsView.showLoading();
                    }
                })
                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse generalResponse) {
                        if (generalResponse == null || generalResponse.getTransactionStatus() != 0) {
                            if (mDeviceDetailsView != null)
                                mDeviceDetailsView.showErrorMessage();
                        } else {
                            if (mDeviceDetailsView != null)
                                mDeviceDetailsView.redirectToDashboardSuccessCase(VodafoneTvLabels.getVtvSuccesRenameVovMessage());
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mDeviceDetailsView != null)
                            mDeviceDetailsView.showErrorMessage();
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void deleteDevice(String vfSsoUserRole, String udid) {
        Realm realm = Realm.getDefaultInstance();
        GetByOperatorSuccess getByOperatorObject = (GetByOperatorSuccess) RealmManager.getRealmObject(realm, GetByOperatorSuccess.class);
        realm.close();

        Subscription subscription = mVodafoneTvService.deleteActiveDevice(vfSsoUserRole, getByOperatorObject.getMasterUserId(), udid)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mDeviceDetailsView != null)
                            mDeviceDetailsView.showLoading();
                    }
                })
                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse generalResponse) {
                        if (generalResponse == null || generalResponse.getTransactionStatus() != 0) {
                            if (mDeviceDetailsView != null)
                                mDeviceDetailsView.showErrorMessage();
                        } else {
                            if (mDeviceDetailsView != null)
                                mDeviceDetailsView.redirectToDashboardSuccessCase(VodafoneTvLabels.getVtvSuccessDeleteVovMessage());
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mDeviceDetailsView != null)
                            mDeviceDetailsView.showErrorMessage();
                    }
                });
        compositeSubscription.add(subscription);

    }

    @Override
    public void onDeleteDeviceClicked() {
        Realm realm = Realm.getDefaultInstance();
        GetByOperatorSuccess getByOperatorObject = (GetByOperatorSuccess) RealmManager.getRealmObject(realm, GetByOperatorSuccess.class);
        realm.close();

        if (getByOperatorObject.getDeleteRestricted())
            mDeviceDetailsView.displayLimitExceededOverlay();
        else
            mDeviceDetailsView.displayConfirmDeleteOverlay();
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null && compositeSubscription.hasSubscriptions()
                && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }

    private int getTotalNumberOfActiveDevices(GetByOperatorSuccess operatorSuccess) {
        int totalActiveDevices = 0;
        for (DeviceFamiliesList deviceFamily : operatorSuccess.getDeviceFamilies()) {
            totalActiveDevices = totalActiveDevices + deviceFamily.getDevices().size();
        }
        return totalActiveDevices;
    }
}
