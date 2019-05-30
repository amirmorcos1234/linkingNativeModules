package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPostpaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;

/**
 * Created by Alex on 3/15/2017.
 */



public class ETFappliedFragment  extends BaseFragment {

    public static final String TAG = "ETFappliedFragment";
    View v;

    ImageView backImage;
    VodafoneButton etfAppliedActivationBackButtonContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");
        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);

        backImage = (ImageView) v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);


        etfAppliedActivationBackButtonContainer = (VodafoneButton) v.findViewById(R.id.buttonTurnOff);
        etfAppliedActivationBackButtonContainer.setOnClickListener(backListener);

        ((VodafoneButton)v.findViewById(R.id.buttonKeepOn)).setVisibility(View.GONE);

        setupLabels();
        return  v;
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "back clicked");
            OffersActivity activity = (OffersActivity) VodafoneController.findActivity(OffersActivity.class);
            if(activity!=null){
                activity.onBackPressed();
                activity.getChatBubble().displayBubble(true);
            }
            getActivity().finish();

        }
    };

    private void setupLabels(){

        ((TextView)v.findViewById(R.id.overlayTitle)).setText(BEOLabels.getEtf_applied_activation_tittle());
        ((TextView)v.findViewById(R.id.overlaySubtext)).setText(BEOLabels.getEtf_applied_activation_content() );
        etfAppliedActivationBackButtonContainer.setText(BEOLabels.getActivation_continue_text());

    }

}