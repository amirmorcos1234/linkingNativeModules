package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by User on 25.04.2017.
 */

public class LoyaltyOptInNotAuthorizedState extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loyalty_unauthorized_fragment, container, false);
        VodafoneTextView message = (VodafoneTextView) v.findViewById(R.id.unauthorized_msisdn_message);
        message.setText(LoyaltyLabels.getLoyalty_unauthorized_msisdn_message());

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_opt_in_not_authorized_state);
        return v;
    }
}
