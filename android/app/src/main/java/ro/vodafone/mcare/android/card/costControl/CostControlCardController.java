package ro.vodafone.mcare.android.card.costControl;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BalanceService;
import ro.vodafone.mcare.android.utils.DateUtils;

/**
 * Created by Bivol Pavel on 28.03.2017.
 */

public class CostControlCardController {
    private CostControlCard mCard;
    private Float currentBalance;
    private Float secondaryBalance;
    private String balanceValidity;
    private String secondaryBalanceValidity;
    private String[] monthsArray;
    private Context context;
    private BalanceCreditSuccess balanceCreditSuccess;
    private BalanceSecondarySuccess balanceSecondarySuccess;

    public CostControlCardController(Context context){
        this.context = context;
        this.mCard = new CostControlCard(context, this);
    }

    public CostControlCardController(CostControlCard mCard){
        this.mCard = mCard;
    }

    public CostControlCard getCard() {
        return mCard;
    }

    public void requestData() {
        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            mCard.showLoading(true);
            getPrepaidBalance();
        } else {
            mCard.setVisibility(View.GONE);
        }
    }

    private void getPrepaidBalance() {
        BalanceService balanceService = new BalanceService(mCard.getContext());
        balanceService.getBalanceCredit(true)
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<BalanceCreditSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<BalanceCreditSuccess> balanceCreditSuccessGeneralResponse) {
                        if (balanceCreditSuccessGeneralResponse != null) {
                            super.onNext(balanceCreditSuccessGeneralResponse);
                            if (balanceCreditSuccessGeneralResponse.getTransactionStatus() == 0) {
                                balanceCreditSuccess = balanceCreditSuccessGeneralResponse.getTransactionSuccess();

                            } else {
                                mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d("CostControlController", "getPrepaidBalance() - onCompleted()");
                        getPrepaidSecondaryBalance();
                    }
                });
    }

    private void getPrepaidSecondaryBalance() {


        BalanceService balanceService = new BalanceService(mCard.getContext());
        balanceService.getBalanceSecondary()
                .subscribe(new RequestSaveRealmObserver<GeneralResponse<BalanceSecondarySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<BalanceSecondarySuccess> response) {
                        Log.d("CostControlController", "onNext");
                        if (response.getTransactionSuccess() != null) {
                            super.onNext(response);
                            balanceSecondarySuccess = response.getTransactionSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d("CostControlController", "onError");
                        mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        setupCostControl();
                        Log.d("CostControlController", "getPrepaidSecondaryBalance() - onCompleted()");
                    }
                });
    }

    public void setupCostControl() {

        setData();

        if (balanceCreditSuccess == null || balanceSecondarySuccess == null) {
            mCard.showError(true, LoyaltyLabels.getLoyalty_error_message());
        } else {
            mCard.buildPrepaidCard(currentBalance, secondaryBalance, balanceValidity, secondaryBalanceValidity);
        }
    }

    public void setData() {
        SimpleDateFormat formatter = new SimpleDateFormat();
        DateFormatSymbols symbols;
        monthsArray = context.getResources().getStringArray(R.array.months_array);

        if (monthsArray != null && monthsArray.length == 12) {
            for (int i = 0; i <= 11; i++) {
                String month = monthsArray[i];
                month = month.substring(0, 3);
                monthsArray[i] = month;
            }


            symbols = new DateFormatSymbols(new Locale("RO", "RO"));
            symbols.setShortMonths(monthsArray);
            formatter = new SimpleDateFormat("d MMM yyyy", symbols);
        }

        if (balanceCreditSuccess != null) {
            currentBalance = balanceCreditSuccess.getBalance();
            if (formatter != null) {
                balanceValidity = formatter.format(balanceCreditSuccess.getBalanceValidity());
            } else {
                balanceValidity = DateUtils.getDate(String.valueOf(balanceCreditSuccess.getBalanceValidity()), new SimpleDateFormat("d MMM yyyy", new Locale("RO", "RO")));
            }
        }

        if (balanceSecondarySuccess != null) {
            secondaryBalance = balanceSecondarySuccess.getBalance();
            if (formatter != null) {
                if(balanceSecondarySuccess.getBalanceValidity()!=null)
                    secondaryBalanceValidity = formatter.format(balanceSecondarySuccess.getBalanceValidity());
                else
                    secondaryBalanceValidity = DateUtils.getDate(String.valueOf(balanceSecondarySuccess.getBalanceValidity()), new SimpleDateFormat("d MMM yyyy", new Locale("RO", "RO")));
            } else {
                secondaryBalanceValidity = DateUtils.getDate(String.valueOf(balanceSecondarySuccess.getBalanceValidity()), new SimpleDateFormat("d MMM yyyy", new Locale("RO", "RO")));
            }
        }
    }
}