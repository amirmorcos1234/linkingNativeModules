package ro.vodafone.mcare.android.ui.fragments.yourServices.details;

import android.os.Bundle;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by user on 27.10.2017.
 */

public class ServiceDetailsFragment extends YourServicesBaseFragment {

    @Override
    public String getTitle() {
        return "Detalii serviciu";
    }

    @Override
    void getParametersFromBundle(Bundle bundle) {
        offerRow = (OfferRowInterface) bundle.getSerializable(OfferRowInterface.class.getCanonicalName());
        promo = (Promo) bundle.getSerializable(Promo.class.getCanonicalName());
    }

    @Override
    void setLabels() {
        sectionTitleLabel.setText(getResources().getString(R.string.your_services_details_service_label));
        costLabel.setText(getResources().getString(R.string.your_services_details_cost_label));
        durationLabel.setText(getResources().getString(R.string.your_services_details_duration_label));
    }

    @Override
    void setAttributes() {
        if(offerRow != null){
            setOfferName(offerRow.getOfferName());
            setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                    NumbersUtils.twoDigitsAfterDecimal(offerRow.getOfferPrice())));

            Long startDate = offerRow instanceof ActiveOffer?
                    ((ActiveOffer) offerRow).getStartDate():
                    ((ActiveOfferPostpaid) offerRow).getStartDate();
            Long endDate = offerRow instanceof ActiveOffer?
                    ((ActiveOffer) offerRow).getEndDate():
                    ((ActiveOfferPostpaid) offerRow).getEndDate();

            String formatedStartDate = null;
            String formatedEndDate = null;
            String duration = null;


            if(startDate != null){
                String offerStartDate = WordUtils.capitalize(DateUtils.getDate(startDate,
                        new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                formatedStartDate = String.format(getContext().getResources().getString(R.string.your_services_details_start_date),offerStartDate);
            }

            if(endDate != null){
                String offerEndDate = WordUtils.capitalize(DateUtils.getDate(endDate,
                        new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                formatedEndDate = String.format(getContext().getResources().getString(R.string.your_services_details_end_date),offerEndDate);
            }

            if (startDate != null && endDate != null) {
                duration = String.format(getContext().getResources().getString(R.string.your_option_days),
                        String.valueOf(TimeUnit.MILLISECONDS.toDays(endDate - startDate)));
            } else if (startDate != null) {
                duration = ServicesLabels.getServices_unlimited_label();
            }

            setLimits(duration, formatedStartDate, formatedEndDate);

        }else if (promo != null){
            setOfferName(promo.getPromoName());
            setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                    NumbersUtils.twoDigitsAfterDecimal(promo.getPromoPrice())));
            setLimits(null, getActivationDate(promo.getPromoActivationDate()), getPromoDeactivationDate(promo.getPromoDeactivationDate()));

            if(promo.getContractualPeriod() != null){
                setContractPeriod(getContractPeriodText(promo.getContractualPeriod()));
            }
        }
    }

    @Override
    void addingAdditionalView() {
        if(user instanceof EbuMigrated){
            addOfferBenefitsCard(ServicesLabels.getServiceDetailsBenefitsSectionTitle());
            addOfferDisciption(ServicesLabels.getServiceDetailsDescriptionSectionTitle(), promo);
            if(promo != null && promo.getAllowPromoDelete())
                checkEbuOfferDeleteEligibility();
        } else if(offerRow != null){
            if (user instanceof PrepaidUser) {
                if (((ActiveOffer) offerRow).getIsRenewable() || ((ActiveOffer) offerRow).getAllowOfferDelete()) {
                    addDeleteOptionButton(ServicesLabels.getServices_delete_service_card_button());
                }
            } else if (user instanceof PostPaidUser){
                    if(!(user instanceof CorpUser || user instanceof CorpSubUser || user instanceof SubUserNonMigrated)){
                        if (((ActiveOfferPostpaid) offerRow).getAllowOfferDelete()) {
                            addDeleteOptionButton(ServicesLabels.getServices_delete_service_card_button());
                        }
                    }
                }
            }
    }

    @Override
    void trackView() {
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name","services details");
        tealiumMapView.put("journey_name","your services");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        ServiceDetailsFragment.YourServicesDetailsTrackingEvent event = new ServiceDetailsFragment.YourServicesDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_Y_SERVICES_DETAILS);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityFragmentInterface)getActivity()).getNavigationHeader().setTitle("Detalii serviciu");
        ((ActivityFragmentInterface)getActivity()).getNavigationHeader().hideSelectorView();
    }

    public static class YourServicesDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "services details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "services details");


            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
