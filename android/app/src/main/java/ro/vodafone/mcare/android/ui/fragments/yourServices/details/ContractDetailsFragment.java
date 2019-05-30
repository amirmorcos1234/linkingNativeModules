package ro.vodafone.mcare.android.ui.fragments.yourServices.details;

import android.os.Bundle;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 27.10.2017.
 */

public class ContractDetailsFragment extends YourServicesBaseFragment {

    ActiveOfferPostpaid activeOfferPostpaid;
    /* Promo promo;*/

    @Override
    void getParametersFromBundle(Bundle bundle) {
        activeOfferPostpaid = (ActiveOfferPostpaid) bundle.getSerializable(ActiveOfferPostpaid.class.getCanonicalName());
        promo = (Promo) bundle.getSerializable(Promo.class.getCanonicalName());
    }

    @Override
    void setLabels() {
        if (user instanceof CBUUser) {
            sectionTitleLabel.setText(ServicesLabels.getPricePlanCardTitle());
            costLabel.setText(getResources().getString(R.string.your_services_details_cost_label));
            durationLabel.setText(getResources().getString(R.string.your_services_details_duration_label));
        } else if (user instanceof EbuMigrated) {
            sectionTitleLabel.setText(getResources().getString(R.string.your_services_price_plan));
            costLabel.setText(getResources().getString(R.string.your_option_monthly_fee));
            contractPeriodLabel.setText(getResources().getString(R.string.your_option_contract_period));
        }
    }

    @Override
    void setAttributes() {
        if (user instanceof CBUUser) {
            if (activeOfferPostpaid != null) {
                if (user instanceof ResCorp) {
                    setConfigurableButtonOferteleMele(ServicesLabels.getServicesRescorpButtonLabel(),AppConfiguration.getRescorpDeeplink(),AppConfiguration.getRescorpShowButton());
                } else if (user instanceof PrivateUser) {
                    setConfigurableButtonOferteleMele(ServicesLabels.getServicesPrivateUserButtonLabel(),AppConfiguration.getPrivateUserDeeplink(),AppConfiguration.getPrivateUserShowButton());
                } else if (user instanceof ResSub) {
                    setConfigurableButtonOferteleMele(ServicesLabels.getServicesResubButtonLabel(),AppConfiguration.getResubDeeplink(),AppConfiguration.getResubShowButton());
                }

                setOfferName(activeOfferPostpaid.getOfferName());
                setCost(String.format(getContext().getString(R.string.your_services_contract_cost_description),
                        NumbersUtils.twoDigitsAfterDecimal(activeOfferPostpaid.getOfferPrice())));

                String formatedStartDateText = null;
                String formatedEndDateText = null;

                if (activeOfferPostpaid.getStartDate() != null && activeOfferPostpaid.getStartDate()>0) {
                    formatedStartDateText = WordUtils.capitalize(DateUtils.getDate(activeOfferPostpaid.getStartDate(),
                            new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                }

                Boolean contractExpired = false;
                if (activeOfferPostpaid.getEndDate() != null && activeOfferPostpaid.getEndDate()>0) {
                    formatedEndDateText = WordUtils.capitalize(DateUtils.getDate(activeOfferPostpaid.getEndDate(), new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"))));
                    if (activeOfferPostpaid.getCurrentDate() > activeOfferPostpaid.getEndDate())
                        contractExpired = true;
                }
                setLimitsContractDetails(contractExpired, formatedStartDateText, formatedEndDateText);
            }
        } else if (user instanceof EbuMigrated) {
            if (promo != null) {
                setOfferName(promo.getPromoName());
                if (promo.getPromoPrice() != null) {
                    setCost(String.format(getContext().getString(R.string.your_services_ebu_contract_cost_description),
                            NumbersUtils.twoDigitsAfterDecimal(promo.getPromoPrice())));
                } else {
                    hideOfferPriceAndCostLabel();
                }
                setContractPeriod(getContractPeriodText(promo.getContractualPeriod()));
            }
        }
    }

    @Override
    void addingAdditionalView() {
        if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
            initGoToAcceptedOffersButton();
        }
        addOfferBenefitsCard("Detalii abonament");
        addOfferDisciption("Descriere abonament", promo);
    }

    @Override
    void trackView() {
        Map<String, Object> tealiumMapViewCallDetails = new HashMap(6);
        tealiumMapViewCallDetails.put("screen_name", "contract details");
        tealiumMapViewCallDetails.put("journey_name", "your services");
        tealiumMapViewCallDetails.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapViewCallDetails);

        ContractDetailsFragment.YourServicesContractDetailsTrackingEvent event = new ContractDetailsFragment.YourServicesContractDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        //use this method after fragment's view was created to check if containers has visible childs.
        checkIfContainersHasVisibleChilds();

        callForAdobeTarget(AdobePageNamesConstants.PG_CONTRACT_DETAILS);
    }

    @Override
    public String getTitle() {
        return "Detalii contract";
    }

    private static class YourServicesContractDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "contract details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "contract details");


            s.channel = "your services";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
