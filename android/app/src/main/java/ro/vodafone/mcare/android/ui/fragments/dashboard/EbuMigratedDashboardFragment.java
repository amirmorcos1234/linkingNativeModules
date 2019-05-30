package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.UserIdentitiesService;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.CostControlWidgetEbuMigrated;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;
import ro.vodafone.mcare.android.widget.gauge.ExtraOptionsController;


/**
 * Created by Victor Radulescu on 10/11/2017.
 */

public class EbuMigratedDashboardFragment extends PostpaidDashboardFragment {

    public static EbuMigratedDashboardFragment newInstance() {

        Bundle args = new Bundle();
        EbuMigratedDashboardFragment fragment = new EbuMigratedDashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initExtraWidgets(RelativeLayout baseContent) {

        initCostControl(baseContent);
        initInfoText();
        //initCards(null);
        getBillCycleDateIfNeeded();
    }


    @Override
    protected void initCostControl(RelativeLayout viewGroup){
        costControlWidget= (CostControlWidgetEbuMigrated) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_cost_control_ebu_migrated_widget, viewGroup, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.dashboard_menu_avatar_widget);
        layoutParams.topMargin = -ScreenMeasure.dpToPx(ExtraOptionsController.EXTRA_RADIOS_IN_PX)/2;
        viewGroup.addView(costControlWidget,layoutParams);
        CostControlWidgetController.getInstance().addCostControlRequestListener(this);

    }


    @Override
    protected void setupDaysLeftUntilBillCycle() {
        EntityDetailsSuccess entityDetailsSuccess = (EntityDetailsSuccess) RealmManager.getRealmObject(realm,
                EntityDetailsSuccess.class);
        if((entityDetailsSuccess == null || entityDetailsSuccess.getBillCycle() == null)){
             initDaysLeftUntilBillCycle(-1);
        }else{
            initDaysLeftUntilBillCycle(entityDetailsSuccess.getBillCycle());
        }
    }

    @Override
    protected boolean shouldGetInvoicePayBillMessageVov(){
        //VoV message with PayBill is displayed only for CBU users. ResSub do not have access to pay own bil -> do not have VoV message.
        EbuMigrated user = (EbuMigrated) VodafoneController.getInstance().getUser();
        return user.isEntityVerifyed() && !user.isSubscriber();
    }

    private void getBillCycleDateIfNeeded(){
        EntityDetailsSuccess entityDetailsSuccess = (EntityDetailsSuccess) RealmManager.getRealmObject(realm,
                EntityDetailsSuccess.class);
        if(entityDetailsSuccess==null) {
            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
            if (entityChildItem != null) {
                getUserDetails(entityChildItem.getVfOdsCid());
            }
        }else{
            initCards(null);
            setDaysLeftUntilBillCycle(entityDetailsSuccess.getBillCycle());
        }
    }
    private void getUserDetails(String vfodsCid){
        if(getContext()==null){
            return;
        }
        compositeSubscription.add(new UserIdentitiesService(getContext()).getUserDetails(vfodsCid).subscribe(new RequestSaveRealmObserver<GeneralResponse<EntityDetailsSuccess>>(){
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showToastErrorMessage();
            }

            @Override
            public void onNext(GeneralResponse<EntityDetailsSuccess> response) {
                if(ResponseValidatorUtils.isValidGeneralRealmResponse(response)){
                    super.onNext(response);
                    setDaysLeftUntilBillCycle(response.getTransactionSuccess().getBillCycle());
                    initCards(null);
                }else{
                    showToastErrorMessage();
                }
            }
        }));
    }
    @Override
    protected BalanceCardHeader getBalanceCardHeaderWithData(AdditionalCost additionalCost, String textHigh, String headerTitle){
        BalanceCardHeader balanceCardHeader = super.getBalanceCardHeaderWithData(additionalCost,textHigh,headerTitle);
        User user = VodafoneController.getInstance().getUser();
        if(balanceCardHeader!=null){
            if(user instanceof EbuMigrated){
                balanceCardHeader.setShowSecondaryFooterButton(((EbuMigrated) user).showSecondaryButtonInSuplimentaryCost());
            }
        }
        return balanceCardHeader;
    }
}
