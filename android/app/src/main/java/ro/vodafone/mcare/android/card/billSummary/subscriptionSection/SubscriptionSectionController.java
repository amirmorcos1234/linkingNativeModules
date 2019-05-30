package ro.vodafone.mcare.android.card.billSummary.subscriptionSection;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmQuery;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.card.costControl.currentBill.ExpandableListItemModel;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.UnpayedBillFragment;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.subscriptions.CompositeSubscription;

import static ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails.GSM_MONTHLY_FEE;
import static ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails.KEY_OF_DETAIL_KEY;
import static ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails.LABEL_OF_DETAIL_KEY;
import static ro.vodafone.mcare.android.client.model.realm.billSummary.SummaryDetails.PRIORITY_KEY;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */
public class SubscriptionSectionController implements BaseCardControllerInterface {

    public static String TAG = "SubscriptionSectionC";

    private static SubscriptionSectionController instance;

    private List<ExpandableListItemModel> listDataHeader;
    private HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild;

    private CompositeSubscription subscriptionsCompositeSubscription = new CompositeSubscription();


    public synchronized static SubscriptionSectionController getInstance() {
        if (instance == null) {
            instance = new SubscriptionSectionController();
        }
        return instance;
    }

    public SubscriptionSectionController setup() {
        return this;
    }

    @Override
    public void onDataLoaded(Object... args) {
    }

    @Override
    public void onRequestFailed() {
        Log.d(TAG, "onRequestFailed");
    }

    public void requestBillSummaryDetails(final SubscriptionSectionCard mCard, final BillSummaryItem billSummaryItem, Long billClosedDate) {
        subscriptionsCompositeSubscription.clear();
        if (mCard == null) {
            return;
        } else if (billClosedDate == null || billSummaryItem == null) {
            mCard.showBillSummaryError();
            return;
        }
        mCard.showBillSummaryLoading();
        BillingServices billingServices = new BillingServices(mCard.getContext());
        String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String userCid = VodafoneController.getInstance().getUserProfile().getCid();

        EntityChildItem selectedEntitiy = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        String vfodsCid = selectedEntitiy != null ? selectedEntitiy.getVfOdsCid() : null;

        subscriptionsCompositeSubscription.add(billingServices.getBillSumaryDetails(userCid, vfodsCid, selectedBan, billSummaryItem.getSid(), billClosedDate).subscribe(new RequestSaveRealmObserver<GeneralResponse<BillSummaryDetailsSuccess>>() {
            @Override
            public void onNext(GeneralResponse<BillSummaryDetailsSuccess> billSummarySuccessGeneralResponse) {
                if (billSummarySuccessGeneralResponse.getTransactionSuccess() != null) {
                    super.onNext(billSummarySuccessGeneralResponse);
                    prepareListData(allowRedirect());
                    mCard.displayBillSummaryData(listDataHeader, listDataChild);
                } else {
                    mCard.showBillSummaryError();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mCard.showBillSummaryError();
            }
        }));

    }

    public boolean allowRedirect() {
        return !EbuMigratedIdentityController.isEbuMigratedSubscriberOrNotVerified();
    }

    private void prepareListData( boolean allowRedirect) {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        BillSummaryDetailsSuccess billSummaryDetailsSuccess = (BillSummaryDetailsSuccess) RealmManager.getRealmObject(BillSummaryDetailsSuccess.class);
        RealmList<SummaryDetails> detailsList = billSummaryDetailsSuccess.getBillSummaryDetailsList();

        if (detailsList == null || !detailsList.isValid()) {
            return;
        }
        RealmQuery<SummaryDetails> detailsQuery = detailsList.where()
                .isNotNull(KEY_OF_DETAIL_KEY).isNotNull(LABEL_OF_DETAIL_KEY);
        List<SummaryDetails> details = detailsQuery.findAllSorted(PRIORITY_KEY);

        for (SummaryDetails detail : details) {
            if (GSM_MONTHLY_FEE.equalsIgnoreCase(detail.getKeyOfDetail())) {
                addPricePlanDetail(allowRedirect, detail);
            } else if (SummaryDetails.SUPLIMENTARY.equalsIgnoreCase(detail.getKeyOfDetail())) {
                addSuplimentaryCostDetail(detail, allowRedirect);
            }  else {
                addOtherDetails(detail);
            }
        }
    }

    private void addPricePlanDetail(boolean allowRedirect, SummaryDetails gsmMonthlyFee) {
        //In abonament
        if (gsmMonthlyFee != null) {
            listDataHeader.add(new ExpandableListItemModel(gsmMonthlyFee.getKeyOfDetail(), gsmMonthlyFee.getLabelOfDetail(),
                    gsmMonthlyFee.getValueOfDetail().floatValue(), BillingOverviewLabels.getBilling_overview_ron_unit(), allowRedirect));
        }
    }

    private void addOtherDetails(SummaryDetails summaryDetails) {
        if (listDataHeader == null || listDataChild == null || summaryDetails == null || !summaryDetails.isValid()) {
            return;
        }
        List<ExpandableListItemModel> items = new ArrayList<>();
        ExpandableListItemModel gsmBenefitsGroup = new ExpandableListItemModel(summaryDetails.getKeyOfDetail(), summaryDetails.getLabelOfDetail(),
                summaryDetails.getValueOfDetail().floatValue(), BillingOverviewLabels.getBilling_overview_ron_unit(), false);
        if (summaryDetails.getExtendedDetails() != null && summaryDetails.getExtendedDetails().isValid() && !summaryDetails.getExtendedDetails().isEmpty()) {
            for (ServiceDetails serviceDetails : summaryDetails.getExtendedDetails()) {
                items.add(new ExpandableListItemModel(serviceDetails.getServiceDesc(), serviceDetails.getBillingAmount().floatValue(), BillingOverviewLabels.getBilling_overview_ron_unit(), false));
            }
        }
        listDataHeader.add(gsmBenefitsGroup);
        listDataChild.put(gsmBenefitsGroup, items);
    }

    private void addSuplimentaryCostDetail(SummaryDetails summaryDetails, boolean allowRedirect) {
        if (listDataHeader != null && summaryDetails != null && summaryDetails.isValid() && summaryDetails.getValueOfDetail() != null) {
            listDataHeader.add(new ExpandableListItemModel(summaryDetails.getKeyOfDetail(), summaryDetails.getLabelOfDetail(),
                    summaryDetails.getValueOfDetail().floatValue(), BillingOverviewLabels.getBilling_overview_ron_unit(), allowRedirect));
        }
    }

}
