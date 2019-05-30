package ro.vodafone.mcare.android.client.model.users.seamless;


import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.gauge.GaugeOptionsType;
import ro.vodafone.mcare.android.client.model.realm.system.CreditInAdvanceLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.dashboard.PrepaidDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpCreditInAdvanceFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpHistoryFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPrepaidOtherNumberFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpPrepaidOwnNumberFragment;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpTransferCreditFragment;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Deaconescu Eliza on 28/204/2017.
 */

public class SeamlessPrepaidHybridUser extends PrepaidUser {

    protected List<GaugeOptionsType> balanceList;


    @Override
    public int getMenu() {
        return R.menu.seamless_prepaid_hybrid_menu;
    }

    public List<GaugeOptionsType> getBalanceList() {
        if (balanceList == null) {
            balanceList = new ArrayList<>();
            balanceList.add(GaugeOptionsType.VOICE);
            balanceList.add(GaugeOptionsType.VAS);
            balanceList.add(GaugeOptionsType.CVT);
        }
        return balanceList;
    }

    @Override
    public PrepaidDashboardFragment getDashboardFragment() {
        return PrepaidDashboardFragment.newInstance();
    }

    @Override
    public List<SelectionPageButton> getTopUpSelectionPageButtons(Context context) {
        List<SelectionPageButton> topUpSelectionPageButtons = new ArrayList<>();

        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recharge_own_number(), TopUPLabels.getTop_up_recharge_with_card(), new TopUpPrepaidOwnNumberFragment(), null));
        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_recharge_another_number(), TopUPLabels.getTop_up_recharge_with_card(), new TopUpPrepaidOtherNumberFragment(), null));
        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_transfer_credit_title(),  TopUPLabels.getTop_up_transfer_credit_subtitle(), new TopUpTransferCreditFragment(), null));
        if(VodafoneController.getInstance().getAppConfiguration().isCreditInAdvancePageDisplayed()) {
            topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                    CreditInAdvanceLabels.getTop_up_credit_in_advance(), CreditInAdvanceLabels.getTop_up_credit_in_advance_button_description(), new TopUpCreditInAdvanceFragment(), null));
        }
        topUpSelectionPageButtons.add(new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                TopUPLabels.getTop_up_open_recharge_history_prepaid(), TopUPLabels.getTop_up_see_recharge_history_prepaid(), new TopUpHistoryFragment(), null));


        return topUpSelectionPageButtons;
    }

    @Override
    public boolean isFullLoggedIn() {
        return false;
    }
}
