package ro.vodafone.mcare.android.card.costControl;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 15.03.2017.
 */
public class CostControlCard extends VodafoneAbstractCard {

    VodafoneTextView avaibleCreditValue;
    VodafoneTextView secondaryBalanceValue;
    VodafoneTextView avaibleCreditExpirationDate;
    VodafoneTextView secondaryBalanceExpirationDate;
    VodafoneTextView totalBalanceButtonLabel;
    VodafoneTextView secondaryBalanceDescription;

    VodafoneTextView expirationDate;
    VodafoneTextView availableAmount;

    RelativeLayout totalBalanceButton;
    RelativeLayout availableLayout;
    RelativeLayout fullBalanceLayout;
    private LinearLayout balanceExpandedLayout;
    private VodafoneTextView totalBalance;
    boolean isExpired = false;

    CostControlCardController costControlCardController;

    public CostControlCard(Context context, CostControlCardController costControlCardController) {
        super(context);
        this.costControlCardController = costControlCardController;
        init(null);
    }

    public CostControlCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInDp(16, 0, 16, 0);
        //setupController();
        setOnErrorClickListner();
        //costControlCardController.requestData();
    }

    public CostControlCard setOnErrorClickListner(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loadingView == null || !loadingView.isVisible()){
                    if(errorView != null && errorView.getVisibility() == VISIBLE){
                        costControlCardController.requestData();
                    }
                }
            }
        });
        return this;
    }

    public void buildPrepaidCard(Float availableBalance, Float secondaryBalance, String balanceExpirationDate, String secondaryBalanceExpiration) {
        availableLayout = (RelativeLayout) findViewById(R.id.available_balance_layout);
        fullBalanceLayout = (RelativeLayout) findViewById(R.id.full_balance_layout);
        availableLayout.setVisibility(GONE);
        fullBalanceLayout.setVisibility(GONE);

        Log.d(TAG, "secondaryBalanceExpiration = " + secondaryBalanceExpiration);

        if (secondaryBalance != null && secondaryBalance != 0 && !verifyExpirationDate(secondaryBalanceExpiration)) {

            fullBalanceLayout.setVisibility(getVisibility());
            totalBalanceButton = (RelativeLayout) fullBalanceLayout.findViewById(R.id.expand_balance_button);
            balanceExpandedLayout = (LinearLayout) fullBalanceLayout.findViewById(R.id.balance_expanded_layout);

            totalBalance = (VodafoneTextView) totalBalanceButton.findViewById(R.id.total_balance);
            totalBalanceButtonLabel = (VodafoneTextView) totalBalanceButton.findViewById(R.id.button_name);
            balanceExpandedLayout.setVisibility(VISIBLE);

            avaibleCreditValue = (VodafoneTextView) findViewById(R.id.available_credit_value);
            avaibleCreditValue.setText(String.format(getResources().getString(R.string.your_services_amount_eur),NumbersUtils.twoDigitsAfterDecimal(availableBalance)));

            secondaryBalanceValue = (VodafoneTextView) findViewById(R.id.secondary_balance_value);
            secondaryBalanceValue.setText(String.format(getResources().getString(R.string.your_services_amount_eur),NumbersUtils.twoDigitsAfterDecimal(secondaryBalance)));

            avaibleCreditExpirationDate = (VodafoneTextView) findViewById(R.id.available_credit_expiration_date);
            avaibleCreditExpirationDate.setText(String.format(getContext().getResources().getString(R.string.your_services_cost_control_expiration), balanceExpirationDate));

            secondaryBalanceExpirationDate = (VodafoneTextView) findViewById(R.id.secondary_balance_expiration_date);
            secondaryBalanceExpirationDate.setText(String.format(getContext().getResources().getString(R.string.your_services_cost_control_expiration), secondaryBalanceExpiration));

            secondaryBalanceDescription = (VodafoneTextView) findViewById(R.id.secondary_balance_description);
            secondaryBalanceDescription.setText(ServicesLabels.getSecondary_balance_description());


            totalBalance.setText(String.format(getResources().getString(R.string.your_services_amount_eur),NumbersUtils.twoDigitsAfterDecimal(availableBalance + secondaryBalance)));
        } else {
            availableLayout.setVisibility(VISIBLE);
            expirationDate = (VodafoneTextView) availableLayout.findViewById(R.id.expiration_date);

            if (verifyExpirationDate(balanceExpirationDate)) {
                expirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.red_color_message_circle));
                expirationDate.setText(String.format(getContext().getResources().getString(R.string.your_services_details_is_expired), balanceExpirationDate));
            } else {
                expirationDate.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_button_text_color));
                expirationDate.setText(String.format(getContext().getResources().getString(R.string.your_services_cost_control_expiration), balanceExpirationDate));
            }

            availableAmount = (VodafoneTextView) availableLayout.findViewById(R.id.available_amount);
            availableAmount.setText(String.format(getResources().getString(R.string.your_services_amount_eur),NumbersUtils.twoDigitsAfterDecimal(availableBalance)));

        }

        hideLoading();
        hideError();

    }

    @Override
    protected int setContent() {
        return R.layout.card_cost_control;
    }

    private void setupController() {
        costControlCardController = new CostControlCardController(this);
    }

    public CostControlCardController getController() {
        return costControlCardController;
    }

    @Override
    public VodafoneAbstractCard showError(boolean hideContent, String errorMessage) {
        super.showError(hideContent, errorMessage);
        errorView.setMargins(0,16,0,16);
        return this;
    }

    private static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"));
    private static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("d MMMM yyyy", new Locale("EN", "RO"));

    private boolean verifyExpirationDate(String date) {
        try {
            Date today = new Date();
            Date expirationDate;
            try {
                expirationDate = DATE_FORMAT_1.parse(date);
            } catch (ParseException e) {
                expirationDate = DATE_FORMAT_2.parse(date);
            }
            if (expirationDate.before(today)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
