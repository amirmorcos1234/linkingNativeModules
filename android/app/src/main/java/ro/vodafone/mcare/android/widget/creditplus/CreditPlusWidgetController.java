package ro.vodafone.mcare.android.widget.creditplus;

import android.content.Context;
import android.util.Log;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BalanceService;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Daniel Slavic
 */

public class CreditPlusWidgetController  {

    private static String TAG = "CreditPlusWidget";

    private CreditPlusWidget widget;

    Boolean validData = null;

    float balanceCredit;

    private static CreditPlusWidgetController instance;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private CreditPlusWidgetController() {
    }

    public synchronized static CreditPlusWidgetController getInstance() {

        if (instance == null) {
            instance = new CreditPlusWidgetController();
        }
        return instance;
    }

    public CreditPlusWidgetController setup(Context context, CreditPlusWidget creditPlusWidget) {
        this.widget = creditPlusWidget;
        return this;
    }


    public CreditPlusWidget getWidget() {
        return widget;
    }

    public synchronized static void deleteInstance() {
        instance = null;
    }

    public void refresh() {
        if(widget==null){
            return ;
        }
        try {
            BalanceCreditSuccess balanceCreditSuccess = ((BalanceCreditSuccess) RealmManager.getRealmObject(BalanceCreditSuccess.class));
            if(balanceCreditSuccess==null || balanceCreditSuccess.getBalance()==null ){
                showError();
                return ;
            }
            balanceCredit= balanceCreditSuccess.getBalance();
            Log.d(TAG, "balanceCredit : " + balanceCredit);
            widget.textValue.setText(String.format("%s €", NumbersUtils.twoDigitsAfterDecimal(balanceCredit)));
            validData = true;
            widget.showError(false);

        } catch (Exception ex){
            Log.e(TAG, "Exception : ", ex);
            validData = false;

        }
    }

    private void setIfValidData(){
        try {
            Float balanceCredit = ((BalanceCreditSuccess) RealmManager.getRealmObject(BalanceCreditSuccess.class)).getBalance();
            validData = true;
        }catch (Exception e){
            validData = false;
            e.printStackTrace();
        }
    }

    public boolean isValidData() {
        if(validData==null){
            setIfValidData();
        }
        return validData;
    }
    public void reloadRefreshBalance(){
        destroy();
        //CreditPlusWidgetController.getInstance().loadDataToWidget();
        if(!(VodafoneController.getInstance().getUser() instanceof NonVodafoneUser
                || VodafoneController.getInstance().getUser() instanceof EbuNonMigrated
                || VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            reload();
        }
    }

    public void showError(){
        if(widget!=null){
            widget.showError(true);
            widget.showLoading(false);
        }
    }

    public float getBalanceCredit() {
        return balanceCredit;
    }

    public void load(){

        getBalanceCreditSucces(true);

    }

    public void reload() {
        cleanDataFromMemory();
        getBalanceCreditSucces(false);
        Log.d(TAG, "reload: ");

    }
    public void getBalanceCreditSucces(boolean refresh){
        if(widget!=null){
            widget.showLoading(true);
        }
        BalanceService balanceService = new BalanceService(VodafoneController.getInstance());

        Subscription balanceCreditSubcription = balanceService.getBalanceCredit(refresh).subscribe(new RequestSaveRealmObserver<GeneralResponse<BalanceCreditSuccess>>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "getBalanceCredit onCompleted");
                if(widget!=null){
                    widget.showLoading(false);
                }

                refresh();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showError();
            }
        });
        compositeSubscription.add(balanceCreditSubcription);
    }

    public void cleanDataFromMemory(){
        RealmManager.delete(BalanceCreditSuccess.class);
    }

    //User when dashboard activity is destory  or enters in reload mode
    public void destroy() {
       compositeSubscription.clear();
        //subscription.unsubscribe();
        //view = null;
    }
}
