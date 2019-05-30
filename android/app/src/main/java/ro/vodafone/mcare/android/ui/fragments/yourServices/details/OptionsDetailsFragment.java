package ro.vodafone.mcare.android.ui.fragments.yourServices.details;

import android.os.Bundle;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
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

public class OptionsDetailsFragment extends YourServicesBaseFragment {

    @Override
    public String getTitle() {
        return "Detalii opțiune";
    }

    @Override
    void getParametersFromBundle(Bundle bundle) {
        offerRow = (OfferRowInterface) bundle.getSerializable(OfferRowInterface.class.getCanonicalName());
        promo = (Promo) bundle.getSerializable(Promo.class.getCanonicalName());
        billingOffer = (BillingOffer) bundle.getSerializable(BillingOffer.class.getCanonicalName());
        //activeOffer = (ActiveOffer) bundle.getSerializable(ActiveOffer.class.getCanonicalName());
    }

    @Override
    void setLabels() {
        sectionTitleLabel.setText(getResources().getString(R.string.your_services_details_option_label));
        if(user instanceof EbuMigrated){
            costLabel.setText(getResources().getString(R.string.your_option_monthly_fee));
        }else{
            costLabel.setText(getResources().getString(R.string.your_services_details_cost_label));
        }
        durationLabel.setText(getResources().getString(R.string.your_services_details_duration_label));
        contractPeriodLabel.setText(getResources().getString(R.string.your_option_contract_period));
    }

    @Override
    void setAttributes() {
        if(offerRow != null){
            setOfferName(offerRow.getOfferName());

            if(offerRow.getOfferPrice() != null){
                setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                        NumbersUtils.twoDigitsAfterDecimal(offerRow.getOfferPrice())));
            } else {
                setCost(null);
            }

            Long startDate = offerRow instanceof ActiveOffer?
                    ((ActiveOffer) offerRow).getStartDate():
                    ((ActiveOfferPostpaid) offerRow).getStartDate();
            Long endDate = offerRow instanceof ActiveOffer?
                    ((ActiveOffer) offerRow).getEndDate():
                    ((ActiveOfferPostpaid) offerRow).getEndDate();

            String formatedStartDateText = null;
            String formatedEndDateText = null;
            String formatedDuration = null;

            if(startDate != null){
                String offerStartDate = WordUtils.capitalize(DateUtils.getDate(startDate, new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                formatedStartDateText = String.format(getContext().getResources().getString(R.string.your_services_details_start_date), offerStartDate);
            }
            if(endDate != null){
                String offerEndDate = WordUtils.capitalize(DateUtils.getDate(endDate, new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                formatedEndDateText = String.format(getContext().getResources().getString(R.string.your_services_details_end_date), offerEndDate);
            }

            if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){

                if(((ActiveOffer)offerRow).getPeriod() != null){
                    formatedDuration = String.format(getContext().getResources().getString(R.string.your_option_days),
                            (String.valueOf(((ActiveOffer)offerRow).getPeriod())));
                }
            }else{
                if (startDate != null && endDate != null) {

                    LocalDate start = LocalDate.fromDateFields(new Date(startDate));
                    LocalDate end = LocalDate.fromDateFields(new Date(endDate));

                    formatedDuration = String.format(getContext().getResources().getString(R.string.your_option_days),
                            String.valueOf(Days.daysBetween(start, end).getDays()));
                } else if (startDate != null) {
                    formatedDuration = ServicesLabels.getServices_unlimited_label();
                }
            }

            setLimits(formatedDuration, formatedStartDateText, formatedEndDateText);

        }else if(promo != null){
            setOfferName(promo.getPromoName());
            if(promo.getPromoPrice() != null)
                setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                        NumbersUtils.twoDigitsAfterDecimal(promo.getPromoPrice())));
            else
                setCost(null);

            setContractPeriod(getContractPeriodText(promo.getContractualPeriod()));
            setLimits(null, getActivationDate(promo.getPromoActivationDate()), getPromoDeactivationDate(promo.getPromoDeactivationDate()));
        }else if (billingOffer != null){
            setOfferName(billingOffer.getBoName());
            if(billingOffer.getBoPrice() != null)
                setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                        NumbersUtils.twoDigitsAfterDecimal(billingOffer.getBoPrice()!=null?Float.valueOf(billingOffer.getBoPrice()):null)));
            else
                setCost(null);
            setContractPeriod(getContractPeriodText(billingOffer.getContractualPeriod()));
            setLimits(null, getActivationDate(billingOffer.getActivationDate()), getPromoDeactivationDate(billingOffer.getDeactivationDate()));
        }
    }

    @Override
    void addingAdditionalView() {
        if (user instanceof PrepaidUser) {
            if(((ActiveOffer) offerRow).getRoamingInfoUrl() != null &&
                    !((ActiveOffer) offerRow).getRoamingInfoUrl().equals("")){
                addExpandableWebViewCard(((ActiveOffer) offerRow).getRoamingInfoUrl());
            }

            if (((ActiveOffer) offerRow).getIsRenewable()
                    || ((ActiveOffer) offerRow).getAllowOfferDelete()) {
                if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.PREPAID) {
                    addDeleteOptionButton(ServicesLabels.getServices_extra_options_deactivation_label_option_card_button());
                } else {
                    addDeleteOptionButton(ServicesLabels.getServices_delete_option_card_button());
                }
            }
        } else if(user instanceof PostPaidUser){
            if(user instanceof CBUUser){
                if(((ActiveOfferPostpaid)offerRow).getAllowOfferDelete())
                     addDeleteOptionButton(ServicesLabels.getServices_delete_option_card_button());
            }else if(user instanceof EbuMigrated){
                addOfferBenefitsCard(ServicesLabels.getOptionDetailsBenefitsSectionTitle());
                addOfferDisciption(ServicesLabels.getOptionDetailsDescriptionSectionTitle(), promo);
                if(promo != null && promo.getAllowPromoDelete())
                   checkEbuOfferDeleteEligibility();
            }
        }
    }

    @Override
    void trackView() {
        Map<String, Object> tealiumMapViewCallDetails = new HashMap(6);
        tealiumMapViewCallDetails.put("screen_name","option details");
        tealiumMapViewCallDetails.put("journey_name","your services");
        tealiumMapViewCallDetails.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapViewCallDetails);

        OptionsDetailsFragment.YourServicesOptionDetailsTrackingEvent event = new OptionsDetailsFragment.YourServicesOptionDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_OPTION_DETAILS);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityFragmentInterface)getActivity()).getNavigationHeader().hideSelectorView();
        ((ActivityFragmentInterface) getActivity()).getNavigationHeader().setTitle("Detalii opțiune");
    }

    private static class YourServicesOptionDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "option details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"option details");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
