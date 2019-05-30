package ro.vodafone.mcare.android.service;

import android.content.Context;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.EbuOfferEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffersSuccess;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequestsSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Daniel Slavic
 */

public class OffersService extends BaseService {

    private Integer dashboardSize = 1;
    private String dashboardInteractionPoint = "dashboard_screen";

    public OffersService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<UnicaOffersSuccess>> getDialViewOffer(String msisdn){

        String interactSessionId = null;
        UnicaOffersSuccess cachedOffers = (UnicaOffersSuccess) RealmManager.getRealmObject(UnicaOffersSuccess.class);

        if(cachedOffers !=null) {
            interactSessionId = cachedOffers.getInteractSessionId();
        }

        Observable<GeneralResponse<UnicaOffersSuccess>> observable =
                RetrofitCall.getInstance().getOffersUnica(msisdn, dashboardSize, dashboardInteractionPoint, interactSessionId, msisdn)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<UnicaOffersSuccess>> putDialViewOffer(String msisdn, UnicaOffer unicaOffer){

        String interactSessionId = null;
        UnicaOffersSuccess cachedOffers = (UnicaOffersSuccess) RealmManager.getRealmObject(UnicaOffersSuccess.class);

        if(cachedOffers !=null) {
            interactSessionId = cachedOffers.getInteractSessionId();
        }

        Observable<GeneralResponse<UnicaOffersSuccess>> observable =
                RetrofitCall.getInstance().putOffersUnica(msisdn, dashboardInteractionPoint, interactSessionId, msisdn,unicaOffer)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return  observable;
    }

    public Observable<GeneralResponse<ActiveOffersSuccess>> getEligibleActiveOffers4PrePaid(String sid){

        Observable<GeneralResponse<ActiveOffersSuccess>> observable =
                RetrofitCall.getInstance().getEligibleActiveOffers4PrePaid(sid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<BannerOffersSuccess> getBannerOffers(){

        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        Observable<BannerOffersSuccess> observable =
                RetrofitCall.getInstance().getBannerOffers(userProfile, entityChildItem)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EligibleOffersSuccess>> getEligibleOffers4PrePaid(){

        Observable<GeneralResponse<EligibleOffersSuccess>> observable =
                RetrofitCall.getInstance().getEligibleOffers4PrePaid()
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse> getPendingOffers4PostPaid(String sid, String type){

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().getPendingOffers4PostPaid(sid, type)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse> getPendingOffers4EBU(String vfCRMRole, String sid, String type){

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().getPendingOffers4EBU(vfCRMRole, sid, type)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEtf(String sid, String promotionId, String excludedPromos){

        Observable<GeneralResponse<EbuOfferEligibilitySuccess>> observable =
                RetrofitCall.getInstance().checkEtf(sid, promotionId, excludedPromos)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4PostPaid(String vfPhoneNumber, String sid, int bcd){

        Observable<GeneralResponse<EligibleOffersPostSuccess>> observable =
                RetrofitCall.getInstance().getEligibleOffers4PostPaid(vfPhoneNumber, sid, bcd)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4EBU(String vfCRMRole, String vfPhoneNumber, String type){

        Observable<GeneralResponse<EligibleOffersPostSuccess>> observable =
                RetrofitCall.getInstance().getEligibleOffers4EBU(vfCRMRole, vfPhoneNumber, type)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> getActiveOffersPostpaid(String msisdn, String sid,String type){

        Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> observable =
                RetrofitCall.getInstance().getActiveOffersPostpaid(msisdn, sid,type)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<ActiveOffersPostpaidEbuSuccess>> getActiveOffersEbu(String vfOdsSid){

        Observable<GeneralResponse<ActiveOffersPostpaidEbuSuccess>> observable =
                RetrofitCall.getInstance().getActiveOffersEbu(vfOdsSid)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<EligibleOffersSuccess>> activateEligibleOffer(String offer_id, ActivatePrepaidOfferRequest activatePrepaidRequest){

        Observable<GeneralResponse<EligibleOffersSuccess>> observable =
                RetrofitCall.getInstance().activateEligibleOffer(offer_id, activatePrepaidRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }


    public Observable<GeneralResponse<ActivationEligibilitySuccess>> validateOfferActivationPostpaid(String msisdn, String sid, String matrixId){

        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                RetrofitCall.getInstance().validateOfferActivationPostpaid(msisdn, sid, matrixId)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<ActivationEligibilitySuccess>> getActivatePostPaidEligibleOffer(String msisd, String sid, String matrix_id ){

        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                RetrofitCall.getInstance().getActivatePostPaidEligibleOffer(msisd, sid, matrix_id)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<ActivationEligibilitySuccess>> putActivatePostPaidEligibleOffer(ActivationRequest activationRequest, String msisd, String sid, String matrix_id ){

        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                RetrofitCall.getInstance().putActivatePostPaidEligibleOffer(activationRequest, msisd, sid, matrix_id)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deletePostPaidOffer(String msisd, String sid, Long offerId, Long instanceId, ActivationRequest activationRequest){

        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                RetrofitCall.getInstance().deletePostPaidOffer(msisd, sid, offerId, instanceId, activationRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }


    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deactivateOfferForEbu(String vfCid, String vfContactId, String vfCRMRole, String vfOdsBan, String vfOdsSid,
                                                                                           String vfOdsBen, String vfSelectedMSISDN, Long promoId, String promoName){

        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                RetrofitCall.getInstance().deactivateOfferForEbu(vfCid, vfContactId, vfCRMRole, vfOdsBan,
                        vfOdsSid, vfOdsBen, vfSelectedMSISDN, promoId, promoName)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEbuOfferDeleteEligibility(String vfCRMRole, String vfOdsSid, String contactId, Long promoId){

        Observable<GeneralResponse<EbuOfferEligibilitySuccess>> observable =
                RetrofitCall.getInstance().checkEbuOfferDeleteEligibility(vfCRMRole, vfOdsSid, contactId, promoId)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse> activateOfferEbu(String msisdn, String sid, String promotionId, String crmRole, String offerName,
                                                                                      String vfOdsCid, String vfOdsBen, String vfOdsBan){

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().activateOfferEbu(msisdn, sid, promotionId, crmRole, offerName, vfOdsCid, vfOdsBen, vfOdsBan)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<GeneralResponse<UserRequestsSuccess>> getUserRequests(boolean vfEBUMigrated,
                                                                            String vfSsoUserRole,
                                                                            String vfOdsCid,
                                                                            String vfOdsBan,
                                                                            String crmRole,
                                                                            boolean pending,
                                                                            boolean accepted,
                                                                            boolean rejected){

        Observable<GeneralResponse<UserRequestsSuccess>> observable =
                RetrofitCall.getInstance().getUserRequests(vfEBUMigrated, vfSsoUserRole, vfOdsCid, vfOdsBan, crmRole)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }
}
