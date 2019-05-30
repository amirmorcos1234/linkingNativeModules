package ro.vodafone.mcare.android.card.aggregatedBenefits;

import android.content.Context;
import android.view.View;

import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;

/**
 * Created by Bivol Pavel on 04.04.2017.
 */
public class AggregatedBenefitsCardController implements BaseCardControllerInterface {

    private CostControl costControl;
    private AggregatedBenefitsCard mCard;
    private List<BalanceShowAndNotShown> extraOptions;

    public AggregatedBenefitsCardController setup(AggregatedBenefitsCard costControlCard) {
        this.mCard = costControlCard;
        return this;
    }

    public AggregatedBenefitsCardController(Context context) {
        this.mCard = new AggregatedBenefitsCard(context, this);
    }

    public AggregatedBenefitsCardController(AggregatedBenefitsCard mCard) {
        this.mCard = mCard;
    }

    public AggregatedBenefitsCard getCard(){
        return mCard;
    }

    public void requestData(){
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            mCard.showCardLoading();
           // getCostControlPrepaid();

            YourServicesFragment yourServicesFragment = (YourServicesFragment) VodafoneController
                    .findFragment(YourServicesFragment.class);

            if(yourServicesFragment != null){
                yourServicesFragment.requestData();
            }

        } else {
            mCard.setVisibility(View.GONE);
        }
    }

   /* private void getCostControlPrepaid() {
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        if (msisdn == null) {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        UserDataService userDataService = new UserDataService(mCard.getContext());
        final CostControl costControl = (CostControl) RealmManager.getRealmObject(CostControl.class);


        if (costControl != null) {
            setupAggregatedBenefitsData(costControl);
        } else {
            userDataService.getCostControl(msisdn).subscribe(new RequestSessionObserver<GeneralResponse<CostControl>>() {
                @Override
                public void onNext(GeneralResponse<CostControl> costControlGeneralResponse) {
                    setupAggregatedBenefitsData(costControlGeneralResponse.getTransactionSuccess());
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mCard.showCardError("Serviciu momentan indisponibil.\n Apasă pentru a reîncerca");
                }
            });
        }
    }*/

    private void setupAggregatedBenefitsData(CostControl costControl){
        if(costControl != null){

            extraOptions = costControl.getCurrentExtraoptions().getExtendedBalanceList();

            if(extraOptions == null ){
                mCard.showCardError(null);
            }else{
                if(extraOptions.size() == 0){
                      mCard.setVisibility(View.GONE);
                    //mCard.showCardError("Nu aveti servicii active pentru moment");
                }else{
                    mCard.buildPrepaidCard(extraOptions);
                }
            }
        } else {
            mCard.showCardError("Serviciu momentan indisponibil.\n Apasă pentru a reîncerca");
        }
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if(value instanceof CostControl){
                this.costControl = (CostControl)value;
            }
        }
        setupAggregatedBenefitsData(costControl);
    }

    @Override
    public void onRequestFailed() {
        mCard.showCardError("Serviciu momentan indisponibil.\n Apasă pentru a reîncerca");
    }
}
