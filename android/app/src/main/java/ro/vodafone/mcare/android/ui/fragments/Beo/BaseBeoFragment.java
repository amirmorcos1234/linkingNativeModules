package ro.vodafone.mcare.android.ui.fragments.Beo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.adobe.mobile.TargetLocationRequest;

import java.lang.annotation.Target;
import java.util.List;

import io.realm.RealmList;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by user on 16.02.2018.
 */

public abstract class BaseBeoFragment extends OffersFragment{
    private OffersService offersService;
    AdobeTargetController adobeTargetController;
    protected boolean isServices;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offersService = new OffersService(getContext());
        adobeTargetController = new AdobeTargetController();
    }

    @Override
    public String getTitle() {
        return null;
    }

    public abstract void onEligibleOffersPrepaidRequestSucces(EligibleOffersSuccess activeOffersSuccess);

    public abstract void onEligibleOffersPrepaidRequestFailed();

    public abstract void onActiveOffersPrepaidRequestSucces(ActiveOffersSuccess activeOffersSuccess);

    public abstract void onActiveOffersPrepaidRequestFailed();

    public abstract void onEligibleOffersCBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess);

    public abstract void onEligibleOffersCBURequestFailed();

    public abstract void onPendingOffersCBURequestSucces(GeneralResponse generalResponse);

    public abstract void onPendingOffersCBURequestFailed();

    public abstract void onEligibleOffersEBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess);

    public abstract void onEligibleOffersEBURequestFailed();

    public abstract void onPendingOffersEBURequestSucces(GeneralResponse generalResponse);

    public abstract void onPendingOffersEBURequestFailed();

    protected void getEligibleOffers4PrePaid() {
        showLoadingDialog();
        Observable<GeneralResponse<EligibleOffersSuccess>> observableApi = offersService.getEligibleOffers4PrePaid();
        //Todo replace this obserwable
        Observable<List<String>> observableAdobe = new AdobeTargetService()
                .getOffersIdsFromTarget(adobeTargetController
                        .createAdobeRequestForOffers(getAdobeRequestName())).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        Observable<GeneralResponse<EligibleOffersSuccess>> resultObservable = Observable.zip(observableAdobe, observableApi, new Func2<List<String>, GeneralResponse<EligibleOffersSuccess>, GeneralResponse<EligibleOffersSuccess>>() {

            @Override
            public GeneralResponse<EligibleOffersSuccess> call(List<String> offersIds, GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccess) {
                if(eligibleOffersSuccess.getTransactionSuccess() == null){
                    return eligibleOffersSuccess;
                }

                if(isServices && eligibleOffersSuccess.getTransactionSuccess().getEligibleServicesCategories() != null){

                    EligibleCategories adobeOffersCategory =
                            matchOfferWithAdobeResponse4Prepaid(eligibleOffersSuccess.getTransactionSuccess().getEligibleServicesCategories(), null);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        eligibleOffersSuccess.getTransactionSuccess().getEligibleServicesCategories().add(0,adobeOffersCategory);
                    }
                }else if (eligibleOffersSuccess.getTransactionSuccess().getEligibleOptionsCategories() != null){

                    EligibleCategories adobeOffersCategory =
                            matchOfferWithAdobeResponse4Prepaid(eligibleOffersSuccess.getTransactionSuccess().getEligibleOptionsCategories(), offersIds);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        eligibleOffersSuccess.getTransactionSuccess().getEligibleOptionsCategories().add(0, adobeOffersCategory);
                    }
                }

                return eligibleOffersSuccess;
            }
        });

        resultObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersSuccess>>() {

            @Override
            public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                super.onNext(eligibleOffersSuccessResponse);
                if(eligibleOffersSuccessResponse.getTransactionStatus() == 0){
                    onEligibleOffersPrepaidRequestSucces(eligibleOffersSuccessResponse.getTransactionSuccess());
                }else{
                    onEligibleOffersPrepaidRequestFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                onEligibleOffersPrepaidRequestFailed();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                stopLoadingDialog();

            }
        });
    }

    protected void getEligibleActiveOffers4PrePaid(String sid) {
        offersService.getEligibleActiveOffers4PrePaid(sid)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<ActiveOffersSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActiveOffersSuccess> activeOffersSuccessResponse) {
                        super.onNext(activeOffersSuccessResponse);
                        if(activeOffersSuccessResponse.getTransactionStatus() == 0){
                            onActiveOffersPrepaidRequestSucces(activeOffersSuccessResponse.getTransactionSuccess());
                        }else{
                            onActiveOffersPrepaidRequestFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onActiveOffersPrepaidRequestFailed();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    protected void getEligibleOffers4PostPaid(String msisdn, String sid, int bcd) {
        OffersService offersService = new OffersService(getContext());

        Observable<GeneralResponse<EligibleOffersPostSuccess>> observableApi = offersService.getEligibleOffers4PostPaid(msisdn, sid, bcd);
        //Todo replace this obserwable
        Observable<List<String>> observableAdobe = new AdobeTargetService()
                .getOffersIdsFromTarget(adobeTargetController
                        .createAdobeRequestForOffers(getAdobeRequestName())).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<GeneralResponse<EligibleOffersPostSuccess>> resultObservable = Observable.zip(observableApi,observableAdobe, new Func2<GeneralResponse<EligibleOffersPostSuccess>, List<String>, GeneralResponse<EligibleOffersPostSuccess>>() {
            @Override
            public GeneralResponse<EligibleOffersPostSuccess> call(GeneralResponse<EligibleOffersPostSuccess> response, List<String> offersIds) {
                if(response.getTransactionSuccess() == null){
                    return response;
                }

                if(isServices && response.getTransactionSuccess().getEligibleServicesCategories() != null){

                    EligibleCategoriesPost adobeOffersCategory = matchOfferWithAdobeResponse4Postpaid(
                            response.getTransactionSuccess().getEligibleServicesCategories(), null);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        response.getTransactionSuccess().getEligibleServicesCategories().add(0, adobeOffersCategory);
                    }

                }else if (response.getTransactionSuccess().getEligibleOptionsCategories() != null){

                    EligibleCategoriesPost adobeOffersCategory = matchOfferWithAdobeResponse4Postpaid(
                            response.getTransactionSuccess().getEligibleOptionsCategories(), offersIds);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        response.getTransactionSuccess().getEligibleOptionsCategories().add(0, adobeOffersCategory);
                    }
                }

                return response;
            }
        });

        resultObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersPostSuccess> eligibleOffersPostSuccessResponse) {
                        super.onNext(eligibleOffersPostSuccessResponse);
                        if(eligibleOffersPostSuccessResponse.getTransactionStatus() == 0){
                            onEligibleOffersCBURequestSucces(eligibleOffersPostSuccessResponse.getTransactionSuccess());
                        }else {
                            onEligibleOffersCBURequestFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onEligibleOffersCBURequestFailed();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    protected void getPendingOffers4PostPaid(String sid, String type) {
        OffersService offersService = new OffersService(getContext());

        offersService.getPendingOffers4PostPaid(sid, type)
                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse activeOffersSuccessResponse) {
                        onPendingOffersCBURequestSucces(activeOffersSuccessResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onPendingOffersCBURequestFailed();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    protected void getPendingOffers4EBU(String vfCRMRole, String sid) {
        OffersService offersService = new OffersService(getContext());

        offersService.getPendingOffers4EBU(vfCRMRole, sid,"")
                .subscribe(new RequestSessionObserver<GeneralResponse>() {
                    @Override
                    public void onNext(GeneralResponse activeOffersSuccessResponse) {
                        onPendingOffersEBURequestSucces(activeOffersSuccessResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onPendingOffersEBURequestFailed();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    protected void getEligibleOffers4EBU(String vfCRMRole, String msisdn) {

        OffersService offersService = new OffersService(getContext());
        Observable<GeneralResponse<EligibleOffersPostSuccess>> observableApi = offersService.getEligibleOffers4EBU(vfCRMRole, msisdn,null);
        //Todo replace this obserwable
        Observable<List<String>> observableAdobe = new AdobeTargetService()
                .getOffersIdsFromTarget(adobeTargetController
                        .createAdobeRequestForOffers(getAdobeRequestName())).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        Observable<GeneralResponse<EligibleOffersPostSuccess>> resultObservable = Observable.zip(observableApi, observableAdobe, new Func2<GeneralResponse<EligibleOffersPostSuccess>, List<String>, GeneralResponse<EligibleOffersPostSuccess>>() {
            @Override
            public GeneralResponse<EligibleOffersPostSuccess> call(GeneralResponse<EligibleOffersPostSuccess> response, List<String> offersIds) {
                if(response.getTransactionSuccess() == null){
                    return response;
                }

                if(isServices && response.getTransactionSuccess().getEligibleServicesCategories() != null){

                    EligibleCategoriesPost adobeOffersCategory = matchOfferWithAdobeResponse4Postpaid(
                            response.getTransactionSuccess().getEligibleServicesCategories(), null);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        response.getTransactionSuccess().getEligibleServicesCategories().add(0, adobeOffersCategory);
                    }

                }else if (response.getTransactionSuccess().getEligibleOptionsCategories() != null){

                    EligibleCategoriesPost adobeOffersCategory = matchOfferWithAdobeResponse4Postpaid(
                            response.getTransactionSuccess().getEligibleOptionsCategories(), offersIds);

                    if(adobeOffersCategory != null
                            && adobeOffersCategory.getEligibleOffersList() != null
                            && !adobeOffersCategory.getEligibleOffersList().isEmpty()){
                        //Adobe category should be added to first pozition because other categories come sorted from server.
                        response.getTransactionSuccess().getEligibleOptionsCategories().add(0, adobeOffersCategory);
                    }
                }

                return response;
            }
        });

        resultObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<EligibleOffersPostSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersPostSuccess> eligibleOffersPostSuccessResponse) {
                        super.onNext(eligibleOffersPostSuccessResponse);
                        if (eligibleOffersPostSuccessResponse.getTransactionStatus() == 0 ||
                                (eligibleOffersPostSuccessResponse.getTransactionStatus() == 1
                                        && eligibleOffersPostSuccessResponse.getTransactionFault() != null
                                        && eligibleOffersPostSuccessResponse.getTransactionFault().getFaultCode().equalsIgnoreCase("WC04002"))){
                            onEligibleOffersEBURequestSucces(eligibleOffersPostSuccessResponse.getTransactionSuccess());
                        }else{
                            onEligibleOffersEBURequestFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onEligibleOffersEBURequestFailed();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    protected int getBillCycleDate() {
        try {
            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
            return profile.getBillCycleDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Adobe Target – TR
    private EligibleCategories matchOfferWithAdobeResponse4Prepaid(List<EligibleCategories> eligibleCategoriesList, List<String> offersIdsFromAdobe){
        EligibleCategories adobeOffersCategory = null;
        if(eligibleCategoriesList != null && !eligibleCategoriesList.isEmpty() && offersIdsFromAdobe != null && !offersIdsFromAdobe.isEmpty()){
            adobeOffersCategory = new EligibleCategories();
            adobeOffersCategory.setCategory(OffersLabels.getAdobeTargetCategoryName());
            adobeOffersCategory.setIsHidden(false);
            adobeOffersCategory.setEligibleOffersList(new RealmList<PrepaidOfferRow>());
            for (EligibleCategories eligibleCategories : eligibleCategoriesList){
                for(PrepaidOfferRow prepaidOfferRow : eligibleCategories.getEligibleOffersList()){
                    for (String offerId : offersIdsFromAdobe){
                        if(prepaidOfferRow.getOfferId().equals(Long.valueOf(offerId))){
                            adobeOffersCategory.getEligibleOffersList().add(prepaidOfferRow);
                        }
                    }
                }
            }
        }
        return adobeOffersCategory;
    }

    //Adobe Target – TR
    private EligibleCategoriesPost matchOfferWithAdobeResponse4Postpaid(List<EligibleCategoriesPost> eligibleCategoriesPosts, List<String> offersIdsFromAdobe){
        EligibleCategoriesPost adobeOffersCategory = null;
        if(eligibleCategoriesPosts != null && !eligibleCategoriesPosts.isEmpty() && offersIdsFromAdobe != null && !offersIdsFromAdobe.isEmpty()){
            adobeOffersCategory = new EligibleCategoriesPost();
            adobeOffersCategory.setCategory(OffersLabels.getAdobeTargetCategoryName());
            adobeOffersCategory.setIsHidden(false);
            adobeOffersCategory.setEligibleOffersList(new RealmList<PostpaidOfferRow>());
            for (EligibleCategoriesPost eligibleCategoriesPost : eligibleCategoriesPosts){
                for(PostpaidOfferRow postpaidOfferRow : eligibleCategoriesPost.getEligibleOffersList()){
                    for (String offerId : offersIdsFromAdobe){
                        if(postpaidOfferRow.getMatrixId().equals(offerId)){
                            adobeOffersCategory.getEligibleOffersList().add(postpaidOfferRow);
                        }
                    }
                }
            }
        }
        return adobeOffersCategory;
    }

    private String getAdobeRequestName(){
        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("release"))
            return AdobePageNamesConstants.ADOBE_OFFERS_CALL;
        else
            return AdobePageNamesConstants.ADOBE_OFFERS_CALL_UAT;
    }
}
