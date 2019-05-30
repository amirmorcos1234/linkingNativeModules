package ro.vodafone.mcare.android.card.billSummary.termTotAmount;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 17.06.2017.
 */
public class TermTotAmountController {

    private TermTotAmountCard mCard;

    public TermTotAmountController(TermTotAmountCard mCard) {
        this.mCard = mCard;
    }

    public void getTermBillSummary(Long billClosedDate){

        BillingServices billingServices = new BillingServices(VodafoneController.getInstance());

        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String userCid = VodafoneController.getInstance().getUserProfile().getCid();
        EntityChildItem selectedEntitiy = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        String vfodsCid = selectedEntitiy != null ? selectedEntitiy.getVfOdsCid() : null;
        RealmResults<ServiceDetails> serviceDetails = RealmManager.getDefaultInstance().where(ServiceDetails.class).findAll();
        mCard.onResponse(serviceDetails);

    }

}
