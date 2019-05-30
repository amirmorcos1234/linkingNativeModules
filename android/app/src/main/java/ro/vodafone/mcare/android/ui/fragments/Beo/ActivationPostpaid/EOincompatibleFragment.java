package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPostpaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.OfferBasicInfo;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPostPaidActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.EoIncompatibleOfferView;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Alex on 3/15/2017.
 */


public class EOincompatibleFragment  extends BaseFragment {

    public static final String TAG = "EOincompatibleFragment";
    View v;
    boolean isEmidiate= true;
    boolean isEtf;
    OfferRowInterface offer;
    List<OfferBasicInfo> incompatibleOffers;
    ImageView backImage;
    VodafoneButton incompatibleActivationContainer;
    VodafoneButton incompatibleActivationBackContainer;
    LinearLayout currentSelectedOptiosContainer;
    LinearLayout alreadyActiveOptiosContainer;
    Profile profile;

    public static EOincompatibleFragment create(OfferRowInterface offerRow, boolean isEmidiate,  List<OfferBasicInfo> incompatibleOffers){
        EOincompatibleFragment fragment = new EOincompatibleFragment();
        Log.d(TAG, "create fragment: offerRow: " + offerRow.getOfferName() + " is Emidiate: " + isEmidiate + " offerList: " );
        fragment.setEmidiate(isEmidiate);
        fragment.setOffer(offerRow);
        fragment.setIncompatibleOffers(incompatibleOffers);

        BeoActivationPostPaidActivity beoActivationPostPaidActivity = (BeoActivationPostPaidActivity) VodafoneController.findActivity(BeoActivationPostPaidActivity.class);
        boolean isETF = false;
        if(beoActivationPostPaidActivity != null){
            isETF = beoActivationPostPaidActivity.isETF();
        }
        fragment.setEtf(isETF);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");
        v = inflater.inflate(R.layout.overlay_eo_incompatible, null);

        backImage = (ImageView) v.findViewById(R.id.incompatible_activation_close);
        backImage.setOnClickListener(backListener);


        currentSelectedOptiosContainer = (LinearLayout) v.findViewById(R.id.current_selected_option_container);
        alreadyActiveOptiosContainer = (LinearLayout) v.findViewById(R.id.already_active_optios_container);



        incompatibleActivationContainer = (VodafoneButton) v.findViewById(R.id.incompatible_activation_container);
        incompatibleActivationContainer.setOnClickListener(activateOffer);

        incompatibleActivationBackContainer = (VodafoneButton) v.findViewById(R.id.incompatible_activation_back_container);
        incompatibleActivationBackContainer.setOnClickListener(backListener);

        profile = (Profile) RealmManager.getRealmObject(Profile.class);
        setupOffers();
        setupLabels();
        return  v;
    }


    View.OnClickListener activateOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "activation clicked");
            showLoadingDialog();
            if(isEtf){
                Log.d(TAG,"IS ETF");
                BeoActivationPostPaidActivity beoActivationPostPaidActivity = (BeoActivationPostPaidActivity) VodafoneController.findActivity(BeoActivationPostPaidActivity.class);

                if(beoActivationPostPaidActivity != null){
                    beoActivationPostPaidActivity.addFragment(new ETFappliedFragment());
                }else{
                    //Todo
                }
            }else{
                Log.d(TAG,"PUT ");
                ActivationRequest activationRequest = new ActivationRequest();
                activationRequest.setBillCycleDate(profile != null ? profile.getBillCycleDate() : null);
                activationRequest.setOfferName(offer != null ? offer.getOfferName() : null);
                activationRequest.setOfferType(offer != null ? offer.getOfferCategory() : null);

                BeoActivationPostPaidActivity beoActivationPostPaidActivity = (BeoActivationPostPaidActivity) VodafoneController.findActivity(BeoActivationPostPaidActivity.class);
                String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() :
                        VodafoneController.getInstance().getUserProfile().getMsisdn();
                String sid = UserSelectedMsisdnBanController.getInstance().getSubscriberSid() != null ? UserSelectedMsisdnBanController.getInstance().getSubscriberSid() :
                        VodafoneController.getInstance().getUserProfile().getSid();

                if(beoActivationPostPaidActivity != null){
                    beoActivationPostPaidActivity.putActivatePostPaidEligibleOffer(activationRequest, msisdn , sid , offer != null ? ((PostpaidOfferRow) offer).getMatrixId() : null, isEmidiate, offer != null ? offer.getOfferCategory() : null);
                }else{
                    //Todo
                }
            }

        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "back clicked");
            getActivity().finish();
        }
    };

    public void setupOffers(){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, ScreenMeasure.dpToPx(10), 0, 0);

        // for active offer

        EoIncompatibleOfferView currectSelectedOffers = new EoIncompatibleOfferView(getContext(), offer.getOfferName(), String.format(BEOLabels.getEuro_amount_currency(), NumbersUtils.twoDigitsAfterDecimal(offer.getOfferPrice())), new EOincompatibleFragment());
        currectSelectedOffers.addIncompatibleOfferView(currentSelectedOptiosContainer, layoutParams);

        if(incompatibleOffers != null) {
            for (int i = 0; i < incompatibleOffers.size(); i++) {
                OfferBasicInfo offerBasicInfo = incompatibleOffers.get(i);
                String offerPrice = "";

                if (offerBasicInfo.getOfferPrice() != null && !offerBasicInfo.getOfferPrice().isEmpty()){
                    offerPrice = NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(offerBasicInfo.getOfferPrice()));
                }

                EoIncompatibleOfferView alreadyActiveOffers = new EoIncompatibleOfferView(getContext(), offerBasicInfo.getOfferName(), String.format(BEOLabels.getEuro_amount_currency(), offerPrice), new EOincompatibleFragment());
                alreadyActiveOffers.addIncompatibleOfferView(alreadyActiveOptiosContainer, layoutParams);
            }
        }

    }

    private void setupLabels(){
        ((TextView)v.findViewById(R.id.incompatible_activation_tittle_text)).setText(BEOLabels.getIncompatible_activation_tittle());
        ((TextView)v.findViewById(R.id.activation_request_options_text)).setText(BEOLabels.getActivation_request_options());
        ((TextView)v.findViewById(R.id.current_options_text)).setText(BEOLabels.getCurrent_options_text());

        if(isEmidiate) {
            ((TextView)v.findViewById(R.id.incompatible_conflict_text_label)).setText(BEOLabels.getImmediate_incompatible_conflict_text_label());
        }else{
            ((TextView)v.findViewById(R.id.incompatible_conflict_text_label)).setText(BEOLabels.getNext_bill_incompatible_conflict_text_label());
        }

        incompatibleActivationContainer.setText(BEOLabels.getIncompatible_activation_offer());
        incompatibleActivationBackContainer.setText(BEOLabels.getIncompatible_back_button_offer());
    }

    public boolean isEmidiate() {
        return isEmidiate;
    }

    public void setEmidiate(boolean emidiate) {
        isEmidiate = emidiate;
    }

    public OfferRowInterface getOffer() {
        return offer;
    }

    public void setOffer(OfferRowInterface offer) {
        this.offer = offer;
    }

    public List<OfferBasicInfo> getIncompatibleOffers() {
        return incompatibleOffers;
    }

    public void setIncompatibleOffers(List<OfferBasicInfo> incompatibleOffers) {
        this.incompatibleOffers = incompatibleOffers;
    }

    public boolean isEtf() {
        return isEtf;
    }

    public void setEtf(boolean etf) {
        isEtf = etf;
    }
}