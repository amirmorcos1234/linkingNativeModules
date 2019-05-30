package ro.vodafone.mcare.android.ui.fragments.Beo.activationEbuOverlays;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.ExcludedPromo;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationEbuActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 2/20/2018.
 */

public class BeoEBUActivationConfirmationOverlay extends BaseFragment {

    public static final String TAG = "EbuETFOverlay";

    private View v;
    @BindView(R.id.buttonTurnOff) VodafoneButton etfBackButtonContainer;
    @BindView(R.id.buttonKeepOn) VodafoneButton etfOkButtonContainer;
    @BindView(R.id.overlayTitle) TextView overlayTitle;
    @BindView(R.id.overlaySubtext) TextView overlaySubtext;

    OfferRowInterface offerRow;

    public void setOfferRow(OfferRowInterface offerRow) {
        this.offerRow = offerRow;
    }

    public static BeoEBUActivationConfirmationOverlay create(OfferRowInterface offerRow){
        BeoEBUActivationConfirmationOverlay overlay = new BeoEBUActivationConfirmationOverlay();
        overlay.setOfferRow(offerRow);
        return overlay;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);
        ButterKnife.bind(this,v);

        initDismissButton();
        setupOverlay();

        return  v;
    }

    private void initDismissButton(){
        ImageView backImage = v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);
    }

    private void setupOverlay(){
        overlayTitle.setText(BEOLabels.getConfirm_activation_tittle_text());
        overlaySubtext.setText(String.format(BEOLabels.getActivation_confirmation_label(), offerRow.getOfferPrice()));

        etfBackButtonContainer.setText(BEOLabels.getActivation_back_text());
        etfBackButtonContainer.setOnClickListener(backListener);

        etfOkButtonContainer.setText(BEOLabels.getActivate_offer_text());
        etfOkButtonContainer.setOnClickListener(confirmButtonListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    };

    View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity() != null){

                if(((PostpaidOfferRow)offerRow).getExcludedPromosList() == null
                        || ((PostpaidOfferRow)offerRow).getExcludedPromosList().isEmpty()){

                    EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

                    ((BeoActivationEbuActivity)getActivity())
                            .activateOffer(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                                    UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                                    String.valueOf(offerRow.getOfferId()), entityChildItem.getCrmRole(),
                                    StringEscapeUtils.escapeJava(offerRow.getOfferName()), entityChildItem.getVfOdsCid(),
                                    UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen(),
                                    UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());

                }else{
                    ((BeoActivationEbuActivity)getActivity())
                            .checkEtf(UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                                    String.valueOf(offerRow.getOfferId()),
                                    getConcatenatedExcludedPromos(((PostpaidOfferRow)offerRow).getExcludedPromosList()));
                }
            }
        }
    };

    private static String getConcatenatedExcludedPromos(List<ExcludedPromo> excludedPromos){

        if(excludedPromos == null || excludedPromos.isEmpty()){
            return null;
        }

        StringBuilder concatenatedExcludedPromos = new StringBuilder();
        for (int i = 0; i < excludedPromos.size(); i++){
            concatenatedExcludedPromos.append(excludedPromos.get(i).getPromoName());
            if(i != excludedPromos.size() - 1){
                concatenatedExcludedPromos.append(",");
            }
        }
        return concatenatedExcludedPromos.toString();
    }
}