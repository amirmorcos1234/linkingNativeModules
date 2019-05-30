package ro.vodafone.mcare.android.ui.fragments.Beo.activationEbuOverlays;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationEbuActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 2/20/2018.
 */

public class EbuETFOverlay extends BaseFragment {

    public static final String TAG = "EbuETFOverlay";

    private View v;
    @BindView(R.id.buttonTurnOff) VodafoneButton etfBackButtonContainer;
    @BindView(R.id.buttonKeepOn) VodafoneButton etfOkButtonContainer;
    @BindView(R.id.overlayTitle) TextView overlayTitle;
    @BindView(R.id.overlaySubtext) TextView overlaySubtext;

    OfferRowInterface offerRow;
    Double etfValue;

    public void setOfferRow(OfferRowInterface offerRow) {
        this.offerRow = offerRow;
    }

    public void setEtfValue(Double etfValue) {
        this.etfValue = etfValue;
    }

    public static EbuETFOverlay create(OfferRowInterface offerRow, Double etfValue){
        EbuETFOverlay etfOverlay = new EbuETFOverlay();
        etfOverlay.setOfferRow(offerRow);
        etfOverlay.setEtfValue(etfValue);
        return etfOverlay;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);
        ButterKnife.bind(this,v);

        initDismissButton();

        if(!(VodafoneController.getInstance().getUser() instanceof SubUserMigrated)){
            setupOverlay();
        }else{
            setupSubUserOverlay();
        }

        return  v;
    }

    private void initDismissButton(){
        ImageView backImage = v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);
    }

    private void setupOverlay(){
        overlayTitle.setText(BEOLabels.getEtf_overlay_title());

        if(isMultipleETFOptions(((PostpaidOfferRow)offerRow).getExcludedPromosList())){
            overlaySubtext.setText(String.format(BEOLabels.getEtf_multiple_options_overlay_message(),
                    getConcatenatedExcludedPromos(((PostpaidOfferRow)offerRow).getExcludedPromosList()), etfValue));
        }else{
            overlaySubtext.setText(String.format(BEOLabels.getEtf_overlay_message(),
                    getConcatenatedExcludedPromos(((PostpaidOfferRow)offerRow).getExcludedPromosList()), etfValue));
        }

        etfBackButtonContainer.setText(BEOLabels.getEtf_dismiss_button_text());
        etfBackButtonContainer.setOnClickListener(backListener);

        etfOkButtonContainer.setText(BEOLabels.getEtf_ok_button_text());
        etfOkButtonContainer.setOnClickListener(confirmButtonListener);
    }

    private void setupSubUserOverlay(){
        overlayTitle.setText(BEOLabels.getEtf_overlay_title());
        overlaySubtext.setText(BEOLabels.getSubUser_etf_overlay_message());

        etfBackButtonContainer.setText(BEOLabels.getActivation_back_text());
        etfBackButtonContainer.setOnClickListener(backListener);

        etfOkButtonContainer.setVisibility(View.GONE);
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
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            if(getActivity() != null && entityChildItem != null){
                
                ((BeoActivationEbuActivity)getActivity())
                        .activateOffer(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                                UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                                String.valueOf(offerRow.getOfferId()), entityChildItem.getCrmRole(),
                                StringEscapeUtils.escapeJava(offerRow.getOfferName()), entityChildItem.getVfOdsCid(),
                                UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen(),
                                UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
            }

        }
    };

    private boolean isMultipleETFOptions(List<ExcludedPromo> excludedPromos) {
        return !(excludedPromos == null || excludedPromos.isEmpty()) && !(excludedPromos.size() == 1);

    }

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