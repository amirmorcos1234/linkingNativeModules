package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
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

public class LoyaltyReceivedPointsTab extends BaseFragment {
    private AdapterBackedLinearLayout receivedPointsListView;
    private LinearLayout view;
    private VodafoneTextView totalPoints;
    LoyaltyPointsAdapter adapter;
    private boolean isFromFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("", "onCreateView: ");
        view = (LinearLayout) inflater.inflate(R.layout.loyalty_received_points_tab, container, false);
        receivedPointsListView = (AdapterBackedLinearLayout) view.findViewById(R.id.loyalty_received_listview);
        totalPoints = (VodafoneTextView) view.findViewById(R.id.loyalty_received_points);
        VodafoneTextView totalReceivedLabel = (VodafoneTextView) view.findViewById(R.id.total_points_label);
        totalReceivedLabel.setText(LoyaltyLabels.getLoyalty_total_received_points_label());
        isFromFilter = this.getArguments().getBoolean("isFromFilter");

        getReceivedPoints();
        return view;
    }

    private void setupListView(List<LoyaltyPoints> list) {
        adapter = new LoyaltyPointsAdapter(list, getContext());

        receivedPointsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void getReceivedPoints() {
        isFromFilter = this.getArguments().getBoolean("isFromFilter");

        List<LoyaltyPoints> receivedPointsList = new ArrayList<>();
        ShopLoyaltyPointsSuccess shopLoyaltyPointsSuccess = (ShopLoyaltyPointsSuccess) RealmManager.getRealmObject(ShopLoyaltyPointsSuccess.class);
        if (shopLoyaltyPointsSuccess != null) {
            receivedPointsList = shopLoyaltyPointsSuccess.getReceivedPointsList();
        }
        if (receivedPointsList.size() != 0) {
            totalPoints.setText(String.valueOf(getTotalPoints(receivedPointsList)).concat("p"));
            setupListView(receivedPointsList);
        } else {
            showMessage();
        }
    }

    private int getTotalPoints(List<LoyaltyPoints> list) {
        int totalPointsAmount = 0;
        for (LoyaltyPoints loyaltyPoints : list) {
            totalPointsAmount = totalPointsAmount + loyaltyPoints.getAmount();
        }
        return totalPointsAmount;
    }

    private void showMessage() {
        view.removeAllViews();
        CardErrorLayout errorCard = new CardErrorLayout(getContext());
        if(isFromFilter)
            errorCard.setText(SettingsLabels.getLoyaltyConsumedPointsTabErrorCard());
        else
            errorCard.setText(SettingsLabels.getLoyaltyConsumedPointsErrorCard());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.addView(errorCard, layoutParams);
    }
}
