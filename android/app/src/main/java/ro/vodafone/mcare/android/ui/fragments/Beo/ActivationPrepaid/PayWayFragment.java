package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPrepaidActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static ro.vodafone.mcare.android.ui.activities.offers.ActivationPayTypeEnum.CARD;
import static ro.vodafone.mcare.android.ui.activities.offers.ActivationPayTypeEnum.CREDIT;

/**
 * Created by Alex on 3/14/2017.
 */




public class PayWayFragment  extends BaseFragment {

    public static final String TAG = "PayWayFragment";
    View v;
    ImageView backImage;
    VodafoneButton payWayCreditActivationContainer;
    VodafoneButton payWayCardActivationContainer;
    VodafoneButton payWayBackButtonContainer;


    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");
        v = inflater.inflate(R.layout.pay_way_fragment, null);

        backImage = (ImageView) v.findViewById(R.id.pay_way_close);
        backImage.setOnClickListener(backListener);

        payWayCreditActivationContainer = (VodafoneButton) v.findViewById(R.id.pay_way_credit_activation_container);
        payWayCreditActivationContainer.setOnClickListener(activateViaCreditOffer);

        payWayCardActivationContainer = (VodafoneButton) v.findViewById(R.id.pay_way_card_activation_container);
        payWayCardActivationContainer.setOnClickListener(activateViaCardOffer);

        payWayBackButtonContainer = (VodafoneButton) v.findViewById(R.id.pay_way_back_button_container);
        payWayBackButtonContainer.setOnClickListener(backListener);


        setupLabels();

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.bonusServicePaymentOverlay);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        PaymentOverlayTrackingEvent event = new PaymentOverlayTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return  v;
    }


    View.OnClickListener activateViaCreditOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity() == null) {
                return;
            }

            Log.d(TAG, "activation Credit clicked");
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.bonusServicePaymentOverlay);
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.bonusPaymentOverlayButtonCreditDisponibil);
            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(PayWayFragment.TAG, tealiumMapEvent);
*/
            ((BeoActivationPrepaidActivity)getActivity()).getRechargeAndActivate(CREDIT);
        }
    };

    View.OnClickListener activateViaCardOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity() == null) {
                return;
            }

            Log.d(TAG, "activation Card clicked");
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.bonusServicePaymentOverlay);
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.bonusPaymentOverlayCardBancar);
            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(PayWayFragment.TAG, tealiumMapEvent);
*/

            ((BeoActivationPrepaidActivity)getActivity()).getRechargeAndActivate(CARD);
        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "back clicked");
            if(getActivity() == null) {
                return;
            }
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.bonusServicePaymentOverlay);
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.bonusPaymentOverlayInapoi);
            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(PayWayFragment.TAG, tealiumMapEvent);

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent2 = new HashMap(6);
            tealiumMapEvent2.put(TealiumConstants.screen_name, TealiumConstants.bonusServicePaymentOverlay);
            tealiumMapEvent2.put(TealiumConstants.event_name,TealiumConstants.bonusPaymentOverlayButtonCreditDisponibil);
            tealiumMapEvent2.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(PayWayFragment.TAG, tealiumMapEvent2);
*/
            getActivity().finish();
        }
    };



    private void setupLabels(){
        if(getActivity() == null) {
            return;
        }

        double offerPrice = ((BeoActivationPrepaidActivity) getActivity()).getOfferPrice();
        Log.d(TAG, "setupLabels: " + offerPrice);
        ((TextView)v.findViewById(R.id.pay_way_tittle_text)).setText(BEOLabels.getPay_way_tittle());
        ((TextView)v.findViewById(R.id.pay_way_content_text)).setText(BEOLabels.getPay_way_activate_offer_first_part() + NumbersUtils.twoDigitsAfterDecimal(offerPrice) + BEOLabels.getPay_way_activate_offer_second_part() );

        payWayCreditActivationContainer.setText(BEOLabels.getPay_way_credit_and_card());
        payWayCardActivationContainer.setText(BEOLabels.getActivation_bank_card_text());

        payWayBackButtonContainer.setText(BEOLabels.getActivation_back_text());

    }

    public static class PaymentOverlayTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "bonus or service payment overlay";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"bonus or service payment overlay");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
