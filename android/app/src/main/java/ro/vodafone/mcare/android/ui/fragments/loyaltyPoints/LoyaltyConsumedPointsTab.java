package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.adapters.LoyaltyPointsAdapter;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.LoyaltyPoints;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyPointsSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by User on 21.04.2017.
 */

public class LoyaltyConsumedPointsTab extends BaseFragment {
    private AdapterBackedLinearLayout usedPointsListView;
    private String TAG = "LoyaltyConsumePointsTab";
    private LinearLayout v;
    private VodafoneTextView totalConsumedPts;
    private boolean isFromFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = (LinearLayout) inflater.inflate(R.layout.loyalty_received_points_tab, container, false);

        Log.d(TAG, "onCreateView");

        usedPointsListView = (AdapterBackedLinearLayout) v.findViewById(R.id.loyalty_received_listview);
        totalConsumedPts = (VodafoneTextView) v.findViewById(R.id.loyalty_received_points);
        VodafoneTextView totalConsumedLabel = (VodafoneTextView) v.findViewById(R.id.total_points_label);
        totalConsumedLabel.setText(LoyaltyLabels.getLoyalty_total_consumed_points_label());
        isFromFilter = this.getArguments().getBoolean("isFromFilter");

        getConsumedPoints();
        return v;
    }

    private void setupListView(List<LoyaltyPoints> consumedPointsList) {
        LoyaltyPointsAdapter adapter = new LoyaltyPointsAdapter(consumedPointsList, getContext());

        usedPointsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void getConsumedPoints() {
        isFromFilter = this.getArguments().getBoolean("isFromFilter");
        ShopLoyaltyPointsSuccess shopLoyaltyPointsSuccess = (ShopLoyaltyPointsSuccess) RealmManager.getRealmObject(ShopLoyaltyPointsSuccess.class);
        if (shopLoyaltyPointsSuccess != null) {
            List<LoyaltyPoints> consumedPointsList = shopLoyaltyPointsSuccess.getConsumedPointsList();
            if (consumedPointsList.size() != 0) {
                totalConsumedPts.setText(String.valueOf(getTotalPoints(consumedPointsList)).concat("p"));
                setupListView(consumedPointsList);
            } else {
                showMessage();
            }
        }
    }

    private void showMessage() {
        v.removeAllViews();
        CardErrorLayout errorCard = new CardErrorLayout(getContext());
        if(isFromFilter)
            errorCard.setText(SettingsLabels.getLoyaltyConsumedPointsTabErrorCard());
        else
            errorCard.setText(SettingsLabels.getLoyaltyConsumedPointsErrorCard());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.addView(errorCard, layoutParams);
    }

    private int getTotalPoints(List<LoyaltyPoints> list) {
        int totalPoints = 0;
        for (LoyaltyPoints loyaltyPoints : list) {
            totalPoints = totalPoints + loyaltyPoints.getAmount();
        }
        return totalPoints;
    }
}
