package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Alex on 3/14/2017.
 */

public class PendingExtraOptions extends BaseFragment {

    public static final String TAG = "PendingExtraOptions";
    View v;
    ImageView backImage;
    VodafoneButton pendingExtraoptionActivationContainer;
    VodafoneButton pendingExtraoptionBackButtonContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");
        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);

        backImage = (ImageView) v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);

        pendingExtraoptionActivationContainer = (VodafoneButton) v.findViewById(R.id.buttonKeepOn);
        pendingExtraoptionActivationContainer.setOnClickListener(pendingActivateOffer);


        pendingExtraoptionBackButtonContainer = (VodafoneButton) v.findViewById(R.id.buttonTurnOff);
        pendingExtraoptionBackButtonContainer.setOnClickListener(backListener);


        setupLabels();
        return v;
    }


    View.OnClickListener pendingActivateOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "activation Credit clicked");
            new NavigationAction(getContext()).startAction(IntentActionName.TOP_UP_PREPAID_OWN_NUMBER, true);
        }
    };


    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "back clicked");
            getActivity().onBackPressed();
        }
    };


    private void setupLabels() {
        ((TextView) v.findViewById(R.id.overlayTitle)).setText(BEOLabels.getPending_extraoption_tittle());
        ((TextView) v.findViewById(R.id.overlaySubtext)).setText(BEOLabels.getPending_extraoption_content());
        pendingExtraoptionActivationContainer.setText(BEOLabels.getActivation_continue_text());
        pendingExtraoptionBackButtonContainer.setText(BEOLabels.getActivation_back_text());

    }
}