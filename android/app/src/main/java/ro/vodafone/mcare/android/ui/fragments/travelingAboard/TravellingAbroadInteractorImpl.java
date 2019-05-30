package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryList;
import ro.vodafone.mcare.android.client.model.travellingAboard.OffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrepaidRealmLong;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelResponse;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Eliza Deaconescu on 4/11/2017.
 */

public class TravellingAbroadInteractorImpl implements TravellingAbroadContract.Interactor {
    public static final String TAG = "TravellingAbroadInteractorImpl";
    boolean nationalOnlyPP = false;
    Boolean isNationalOnly = false;
    String roamingLabel;

    private TravellingAbroadContract.OnAbroadListener mOnAbroadListener;
    private TravellingAbroadContract.OnAdministrationListener mOnAdminitrationListener;

    public TravellingAbroadInteractorImpl(TravellingAbroadContract.OnAbroadListener onAbroadListener) {
        this.mOnAbroadListener = onAbroadListener;
    }

    public TravellingAbroadInteractorImpl(TravellingAbroadContract.OnAdministrationListener onAdministrationListener) {
        this.mOnAdminitrationListener = onAdministrationListener;
    }

    public TravellingAbroadInteractorImpl() {
    }

    public RealmResults<CountryList> getCountryListObject(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {
        RealmResults<CountryList> countryList = null;
        if (roamingTariffsSuccess != null) {
            countryList = roamingTariffsSuccess.getCountryList().where().
                    contains(CountryList.COUNTRY_NAME, writtenCountryName).findAll();
        }
        return countryList;
    }

    public ZonesList loadZoneList(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {
        ZonesList zonesList = null;
        RealmResults<CountryList> countryList = getCountryListObject(roamingTariffsSuccess, writtenCountryName);
        if (countryList != null && !countryList.isEmpty()) {
            zonesList = getCorrespondingZone(roamingTariffsSuccess, countryList.get(0).getZoneId());
        }
        return zonesList;
    }

    public ZonesList getCorrespondingZone(RoamingTariffsSuccess roamingTariffsSuccess, int zoneId) {
        ZonesList selectedZone = roamingTariffsSuccess.getZonesList()
                .where().equalTo(ZonesList.ZONE_ID, zoneId).findFirst();
        return selectedZone;
    }
    public ArrayList<PrePaidOffersList> loadRelatedOffers(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {
        CountryList countryListObject = getCountryListObject(roamingTariffsSuccess, writtenCountryName).first();
        RealmList<PrepaidRealmLong>countryOfferList = countryListObject.getRelatedOffers();

        ArrayList<PrePaidOffersList> relatedRowOffers = new ArrayList<>();

        for (int i = 0; i < countryOfferList.size(); i++) {
            for (int j = 0; j < roamingTariffsSuccess.getOffersList().size(); j++) {
                if (countryOfferList.get(i).getOfferId().equals(roamingTariffsSuccess.getOffersList().get(j).getOfferId())) {
                    relatedRowOffers.add(roamingTariffsSuccess.getOffersList().get(j));
                }

            }
        }
        return relatedRowOffers;
    }
    public String[] getCountryList(RoamingTariffsSuccess roamingTariffsSuccess) {
        String[] countryListName = null;
        if (roamingTariffsSuccess != null) {
            RealmList<CountryList> countryList = roamingTariffsSuccess.getCountryList();
            countryListName = new String[countryList.size()];
            if (countryList != null && !countryList.isEmpty()) {
                for (int i = 0; i < countryList.size(); i++) {
                    countryListName[i] = countryList.get(i).getCountryName();
                }
            }
            return countryListName;
        }
        return countryListName;
    }

    public CountryList countryList(RoamingTariffsSuccess roamingTariffsSuccess, String countyName) {
        CountryList countryList = null;
        if (roamingTariffsSuccess != null) {
            countryList = roamingTariffsSuccess.getCountryList()
                    .where().equalTo(CountryList.COUNTRY_NAME, countyName).findFirst();
        }
        return countryList;
    }
    public RealmResults<CountryList> getCountryListObjectByCode(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryCode) {
        RealmResults<CountryList> countryList = null;
        if (roamingTariffsSuccess != null) {
            countryList = roamingTariffsSuccess.getCountryList().where().
                    contains(CountryList.COUNTRY_CODE_ISO, writtenCountryCode).findAll();
        }
        return countryList;
    }
    public void getAccessTypePostPaid(String sid, final boolean isFromTravelling) {

        sid = PhoneNumberUtils.checkSidFormat(sid);

        new TravellingAboardService(VodafoneController.getInstance()).getAccessType(sid).subscribe(
                new RequestSaveRealmObserver<GeneralResponse<AccessTypeSuccess>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        if (isFromTravelling)
                            mOnAbroadListener.onFailed();
                        else
                            mOnAdminitrationListener.onFailed();
                    }

                    @Override
                    public void onNext(GeneralResponse<AccessTypeSuccess> response) {
                        super.onNext(response);
                        if (response != null && response.getTransactionSuccess() != null) {
                            AccessTypeSuccess transactionSuccess = response.getTransactionSuccess();
                            if (isFromTravelling)
                                mOnAbroadListener.onSuccess(transactionSuccess.getIsRoaming(), transactionSuccess.getIsInternational());
                            else
                                mOnAdminitrationListener.onSuccess(transactionSuccess.getIsRoaming(), transactionSuccess.getIsInternational());
                        } else {
                            if (isFromTravelling)
                                mOnAbroadListener.onFailed();
                            else
                                mOnAdminitrationListener.onFailed();

                        }
                    }
                });
    }

    //get Roaming status for ebu
    public void getAccesTypeEbu(final String msisdn, final String sid, final boolean isFromTravelling) {

        new TravellingAboardService(VodafoneController.getInstance()).getAccessTypeEbu(sid).subscribe(
                new RequestSaveRealmObserver<GeneralResponse<AccessTypeEBU>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (isFromTravelling) {
                            mOnAbroadListener.onFailed();
                        } else {
                            mOnAdminitrationListener.onFailed();
                        }
                    }

                    @Override
                    public void onNext(GeneralResponse<AccessTypeEBU> response) {
                        super.onNext(response);
                        if (response != null && response.getTransactionSuccess() != null) {
                            RequestSaveRealmObserver.save(response);
                            AccessTypeEBU acessTypeSuccessEbu = response.getTransactionSuccess();
                            if (isFromTravelling) {
                                mOnAbroadListener.onSuccess(acessTypeSuccessEbu.getIsRoaming(), acessTypeSuccessEbu.getIsInternational());
                            } else {
                                mOnAdminitrationListener.onSuccess(acessTypeSuccessEbu.getIsRoaming(), acessTypeSuccessEbu.getIsInternational());
                            }
                        } else {
                            if (isFromTravelling) {
                                mOnAbroadListener.onFailed();
                            } else {
                                mOnAdminitrationListener.onFailed();
                            }
                        }
                    }

                });
    }

    public String getAlias(Profile profile) {
        String alias = null;

        if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null) {
            alias = UserSelectedMsisdnBanController.getInstance().getSubscriberAlias();
            return alias;

        } else if (profile != null && profile.getAlias() != null && !profile.getAlias().equals("")) {
            if (profile.getAlias().equals(profile.getHomeMsisdn()) || profile.getAlias().substring(1).equals(profile.getHomeMsisdn())) {
                if (profile.getAlias().startsWith("4")) {
                    alias = profile.getAlias().replaceFirst("4", "");
                }
            } else {
                alias = profile.getAlias();
            }
        }
        return alias;
    }

    public void setNationalOnlyPPFromRealm() {
        ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess = (ActiveOffersPostpaidSuccess) RealmManager.getRealmObject(ActiveOffersPostpaidSuccess.class);
        if (activeOffersPostpaidSuccess != null && activeOffersPostpaidSuccess.getPricePlan() != null)
            nationalOnlyPP = activeOffersPostpaidSuccess.getPricePlan().getIsNationalOnly();
    }

    public void getNationalPricePlan(String msisdn, String sid, final boolean isFromTravelling) {
        final String finalSid;
        if (msisdn != null && msisdn.startsWith("0"))
            msisdn = "4" + msisdn;
        if (sid != null && sid.startsWith("-"))
            sid = sid.replace("-", "");
        finalSid = sid;

        new OffersService(VodafoneController.getInstance()).getActiveOffersPostpaid(msisdn, sid,"").subscribe(new RequestSaveRealmObserver<GeneralResponse<ActiveOffersPostpaidSuccess>>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                getAccessTypePostPaid(finalSid, isFromTravelling);
            }

            @Override
            public void onNext(GeneralResponse<ActiveOffersPostpaidSuccess> response) {
                super.onNext(response);
                if (response != null && response.getTransactionSuccess() != null)
                    if (response.getTransactionSuccess().getPricePlan() != null)
                        nationalOnlyPP = response.getTransactionSuccess().getPricePlan().getIsNationalOnly();

                getAccessTypePostPaid(finalSid, isFromTravelling);
            }
        });
    }

    public String setRoamingStatusDescription(boolean isActive) {
        String roamingStatus = null;
        String status = isActive ? "activ" : "inactiv";

        if (VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.PREPAID) ||
                VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.SEAMLESS_PREPAID_USER) ||
                VodafoneController.getInstance().getUserProfile().getUserRole().equals(UserRole.HYBRID)) {
            if (getRoamingLabelFromApi() == null) {
                if (status.equalsIgnoreCase("activ")) {
                    roamingStatus = TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart1() + " <b>" + status + "</b> "
                            + TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart3();
                } else if (status.equalsIgnoreCase("inactiv")) {
                    roamingStatus = TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart1() + " <b>" + status + "</b> "
                            + TravellingAboardLabels.getTravellingAbroadRoamingStatusPrepaidTittlePart2();
                }
            } else {
                roamingStatus = getRoamingLabelFromApi();
            }
        } else if (isEbu() && isGeonumber()) {
            roamingStatus = TravellingAboardLabels.getTravellingAbroadRoamingStatusEbudTittlePart1() + " <b>" + status + "</b> "
                    + TravellingAboardLabels.getTravellingAbroadRoamingStatusPostpaiddTittlePart2();
        } else {
            roamingStatus = TravellingAboardLabels.getTravellingAbroadRoamingStatusPostpaidTittlePart1() + " <b>" + status + "</b> "
                    + TravellingAboardLabels.getTravellingAbroadRoamingStatusPostpaiddTittlePart2();
        }

        return roamingStatus;
    }

    public Boolean getNationalOnlyPP() {
        return nationalOnlyPP;
    }

    public Boolean isEbu() {
        return VodafoneController.getInstance().getUser() instanceof EbuMigrated;
    }

    public Boolean isGeonumber() {
        if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
            return true;
        } else {
            return false;
        }
    }

    public void getRoamingLabels(final Boolean roamingStatus, final boolean isFromRoaming) {

        TravellingAboardService travellingAbroadService = new TravellingAboardService(VodafoneController.getInstance());

        travellingAbroadService.getRoamingLabels(createRoamingLabelsRequest(roamingStatus)).subscribe(new RequestSaveRealmObserver<GeneralResponse<RoamingLabelResponse>>() {
            @Override
            public void onNext(GeneralResponse<RoamingLabelResponse> roamingLabelResponse) {
                super.onNext(roamingLabelResponse);
                if (roamingLabelResponse.getTransactionSuccess() != null) {
                    if (roamingLabelResponse.getTransactionStatus() == 0) {
                        roamingLabel = roamingLabelResponse.getTransactionSuccess().getRoamingLabel();
                        if (isFromRoaming) {
                            mOnAbroadListener.setRoaming(roamingStatus, roamingLabel, isNationalOnly);
                        } else {
                            mOnAdminitrationListener.setRoaming(roamingStatus, roamingLabel, isNationalOnly);
                        }
                    }
                } else {
                    if (isFromRoaming) {
                        mOnAbroadListener.showErrorCard();
                    } else {
                        mOnAdminitrationListener.showErrorCard();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public List<OffersList> createOfferList(ActiveOffersSuccess activeOffersSuccess) {

        List<OffersList> offersList = new ArrayList<OffersList>();

        if (activeOffersSuccess != null) {
            RealmList<ActiveOffer> activeOffers = activeOffersSuccess.getActiveOffersList();
            RealmList<ActiveOffer> servicesLists = activeOffersSuccess.getActiveServicesList();
            OffersList offer;

            for (ActiveOffer activeOffer : activeOffers) {
                offer = new OffersList();
                offer.setSrvId((int) (long) (activeOffer.getOfferId()));
                offer.setOfferCategory(activeOffer.getOfferCategory());
                offer.setOfferPrice(activeOffer.getOfferPrice());
                offersList.add(offer);
            }

            for (ActiveOffer servicesList : servicesLists) {
                offer = new OffersList();
                offer.setSrvId((int) (long) (servicesList.getOfferId()));
                offer.setOfferCategory(servicesList.getOfferCategory());
                offer.setOfferPrice(servicesList.getOfferPrice());
                offersList.add(offer);
            }
        }
        return offersList;
    }

    public RoamingLabelRequest createRoamingLabelsRequest(Boolean roamingStatus) {

        ActiveOffersSuccess activeOffersSuccess = (ActiveOffersSuccess) RealmManager.getRealmObject(ActiveOffersSuccess.class);
        isNationalOnly = activeOffersSuccess.getNationalOnly();

        RoamingLabelRequest roamingLabelRequest = new RoamingLabelRequest();
        roamingLabelRequest.setOfferList(createOfferList(activeOffersSuccess));
        roamingLabelRequest.setNationalOnly(isNationalOnly);
        if (roamingStatus != null)
            roamingLabelRequest.setRoaming(roamingStatus);
        else {
            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
            if (profile != null) {
                roamingLabelRequest.setRoaming(profile.getRoaming());
            }

        }
        return roamingLabelRequest;
    }

    public String getRoamingLabelFromApi() {

        return roamingLabel;
    }

    public Boolean isPrepaid() {

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser ||
                VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser ||
                VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)
            return true;
        else
            return false;
    }


    public Boolean isNationalOnly() {
        return isNationalOnly;
    }

    public ArrayList<PrePaidOffersList> getCountryRoamingOffers(RoamingTariffsSuccess roamingTariffsSuccess, String writtenCountryName) {
        ArrayList<PrePaidOffersList> prePaidOffersLists = new ArrayList<>();
        ArrayList<PrepaidRealmLong> prepaidRealmLongs = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        RealmResults<CountryList> countryList = getCountryListObject(roamingTariffsSuccess, writtenCountryName);
        if (countryList != null && !countryList.isEmpty()) {
            prepaidRealmLongs.addAll(countryList.get(0).getRelatedOffers());
            for (PrepaidRealmLong prepaidRealmLong :
                    prepaidRealmLongs) {
                RealmResults<PrePaidOffersList> offersLists = realm.where(PrePaidOffersList.class).equalTo("offerId", prepaidRealmLong.getOfferId()).findAll();
                prePaidOffersLists.addAll(offersLists);
            }
        }
        return prePaidOffersLists;
    }
}

