package ro.vodafone.mcare.android.service;

import android.content.Context;

import java.util.Calendar;

import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomAcceptedOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.UpdateOfferRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Eliza Deaconescu
 */


public class AncomOffersService extends BaseService {
    private final static String TYPE_ACCEPTED = "accepted";
    private final static String TYPE_PENDING = "proposed";
    private final static Boolean ACCEPTED_OFFER = true;
    private final static Boolean REFUSED_OFFERS = false;

    Calendar cal = Calendar.getInstance();
    int day = cal.get(Calendar.DAY_OF_MONTH);

    public AncomOffersService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<AncomPendingOffersSuccess>> getVOSPendingOffers(String msisdn){

        Observable<GeneralResponse<AncomPendingOffersSuccess>> observable =
                RetrofitCall.getInstance().getAncomPendingOffers(msisdn, TYPE_PENDING)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<AncomAcceptedOffersSuccess>> getVOSAcceptedOffers(String msisdn){

        Observable<GeneralResponse<AncomAcceptedOffersSuccess>> observable =
                RetrofitCall.getInstance().getAncomAcceptedOffers(msisdn, TYPE_ACCEPTED)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<AncomPendingOffersSuccess>> setVOSOAcceptedOffers(String msisdn, String proposalId){

        UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
        updateOfferRequest.setProposalId(proposalId);
        updateOfferRequest.setOfferMsisdn(msisdn);
        updateOfferRequest.setCustomerAnswer(ACCEPTED_OFFER);
        updateOfferRequest.setBillCycleDate(day);
        Observable<GeneralResponse<AncomPendingOffersSuccess>> observable =
                RetrofitCall.getInstance().setAncomOffers(msisdn,updateOfferRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }

    public Observable<GeneralResponse<AncomPendingOffersSuccess>> setVOSRefusedOffers(String msisdn, String proposalId, String offerMsisdn){

        UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
        updateOfferRequest.setProposalId(proposalId);
        updateOfferRequest.setOfferMsisdn(msisdn);
        updateOfferRequest.setCustomerAnswer(REFUSED_OFFERS);
        updateOfferRequest.setBillCycleDate(day);
        Observable<GeneralResponse<AncomPendingOffersSuccess>> observable =
                RetrofitCall.getInstance().setAncomOffers(msisdn,updateOfferRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }
}
