package ro.vodafone.mcare.android.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Path;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.costControl.CostControlDataVolatile;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.profile.ProfileSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.billDates.BillsDatesSuccess;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.CallDetailsReportRequest;
import ro.vodafone.mcare.android.rest.requests.CallDetailsRequest;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 2/3/2017.
 */

public class UserDataService extends BaseService {

    public UserDataService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<CostControl>> getCostControl(String msisdn) {

        Observable<GeneralResponse<CostControl>> observable =
                RetrofitCall.getInstance().getCostControl(msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse<CostControl>> loadCostControl(String msisdn) {

        Observable<GeneralResponse<CostControl>> observable =
                RetrofitCall.getInstance().loadCostControl(msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse<CostControl>> reloadCostControl(String msisdn) {
        RealmManager.delete(CostControl.class);
        return loadCostControl(msisdn);
    }

    public Observable<ArrayList<AdditionalCost>> getAdditionalCostForOtherMsidns(List<String> msisdn) {

        Observable<ArrayList<AdditionalCost>> observable =
                RetrofitCall.getInstance().getAdditionalCostForOtherMsidns(msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }
    public Observable<CostControlDataVolatile> getCostControlForOtherMsidns(List<String> msisdn) {

        Observable<CostControlDataVolatile> observable =
                RetrofitCall.getInstance().getCostControlForOtherMsidns(msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<Profile>> getUserProfile(boolean refreshData) {

        Observable<GeneralResponse<Profile>> observable =
                RetrofitCall.getInstance().getUserProfile(refreshData)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return observable;
    }

    public Observable<GeneralResponse<ProfileSubscriptionSuccess>> getVdfSubscription(String vfPhoneNumber) {

        Observable<GeneralResponse<ProfileSubscriptionSuccess>> observable =
                RetrofitCall.getInstance().getVdfSubscription(vfPhoneNumber)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return observable;
    }

    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy(boolean refreshData) {

        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                RetrofitCall.getInstance().getSubscriberHierarchy(refreshData)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy(String vfOdsBan) {

        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                RetrofitCall.getInstance().getSubscriberHierarchy(false,vfOdsBan)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse<UserProfileHierarchy>> reloadSubscriberHierarchy(String vfOdsBan) {

        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                RetrofitCall.getInstance().getSubscriberHierarchy(true,vfOdsBan)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
    public Observable<GeneralResponse<UserProfileHierarchy>> reloadSessionSubscriberHierarchy(String vfOdsBan) {

        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                RetrofitCall.getInstance().getSessionSubscriberHierarchy(true,vfOdsBan)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }



    public Observable<GeneralResponse<CallDetailsSuccess>> getCallDetails(CallDetailsRequest callDetailsRequest,
                                                                          String vfOdsCid,
                                                                          String vfCRMRole) {

        Observable<GeneralResponse<CallDetailsSuccess>> observable =
                RetrofitCall.getInstance().getCallDetails(callDetailsRequest,
                                                            vfOdsCid, vfCRMRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<BillsDatesSuccess>> getBillDates(String account,
                                                                       String vfCid,
                                                                       String vfOdsCid,
                                                                       int range,
                                                                       String vfCRMRole) {

        Observable<GeneralResponse<BillsDatesSuccess>> observable =
                RetrofitCall.getInstance().getBillDates(account,
                                                        vfCid,
                                                        vfOdsCid,
                                                        range,
                        vfCRMRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> sendCallDetailsReport(CallDetailsReportRequest callDetailsReportRequest,
                                                             String vfOdsCid) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().sendCallDetailsReport(callDetailsReportRequest,
                                                                vfOdsCid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ProfileSuccess>> postProfileAvatar(String selectedMsisdn,
                                                                         String alias,
                                                                         String type,
                                                                         MultipartBody.Part file) {

        Observable<GeneralResponse<ProfileSuccess>> observable =
                RetrofitCall.getInstance().postProfileAvatar(selectedMsisdn, alias, type, file)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ProfileSuccess>> postProfileAlias(String selectedMsisdn,
                                                                        String alias,
                                                                        String type) {

        Observable<GeneralResponse<ProfileSuccess>> observable =
                RetrofitCall.getInstance().postProfileAlias(selectedMsisdn, alias, type)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ProfileSuccess>> deleteAvatar(String selectedMsisdn,
                                                                    String type) {

        Observable<GeneralResponse<ProfileSuccess>> observable =
                RetrofitCall.getInstance().deleteAvatar(selectedMsisdn, type)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<ProfileSuccess>> putDefaultProfile(String defaultMsisdn) {

        Observable<GeneralResponse<ProfileSuccess>> observable =
                RetrofitCall.getInstance().putDefaultProfile(defaultMsisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<TvHierarchyResponse>> getTvHierarchy(String vfSsoUsername, String vfPhoneNumber, String vfSsoUserRole,
                                                                           boolean vfEBUMigrated, String vfFixedBan, String vfFixedCid, String banList){
        Observable<GeneralResponse<TvHierarchyResponse>> observable =
                RetrofitCall.getInstance().getTvHierarchy(vfSsoUsername,vfPhoneNumber,vfSsoUserRole,
                        vfEBUMigrated,vfFixedBan,vfFixedCid,banList)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

}
