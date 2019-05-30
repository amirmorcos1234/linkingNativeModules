package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.ExpandableListAdapter;
import ro.vodafone.mcare.android.ui.views.nonscrollable.NonScrollExpandableListView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.animators.DashboardAnimator;
import ro.vodafone.mcare.android.widget.animators.RelativeViewAnimator;
import ro.vodafone.mcare.android.widget.avatar.DashboardMenuAvatar;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;
import ro.vodafone.mcare.android.widget.creditplus.NonVodafoneCreditPlus;
import rx.Subscription;

/**
 * Created by Victor Radulescu on 3/1/2017.
 */

public class NonVodafoneDashboardFragment extends BaseDashboardFragment implements DashboardAnimator.AnimationListener{


    private NonVodafoneCreditPlus creditPlusCenterWidget;


    public NonVodafoneDashboardFragment() {
    }

    public static NonVodafoneDashboardFragment newInstance() {

        NonVodafoneDashboardFragment dashboardFragment = new NonVodafoneDashboardFragment();

        Bundle bundle = new Bundle();
        dashboardFragment.setArguments(bundle);
        return dashboardFragment;
    }

    @Override
    protected void initExtraWidgets(RelativeLayout baseContent) {
        initCreditPlus(baseContent);
        initCards();
        getInvoicePayBillMessageVov();
    }

    @Override
    protected void startGroupAnimation() {
        super.startGroupAnimation();
        if(!animateElements){
            creditPlusCenterWidget.setVisibility(View.VISIBLE);
            creditPlusCenterWidget.startAnimation(true);
            expListView.setVisibility(View.VISIBLE);
        }
    }

    private void initCreditPlus(RelativeLayout viewGroup){
        try {

            creditPlusCenterWidget= (NonVodafoneCreditPlus) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_credit_plus_center_widget, null, false);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW,R.id.dashboard_menu_avatar_widget);
            layoutParams.topMargin = ScreenMeasure.dpToPx(20);
            layoutParams.addRule(RelativeLayout.ALIGN_START,R.id.center_right_to_dashboard_separator);
            layoutParams.setMarginStart((int) -(4* creditPlusCenterWidget.getComponentsSize()/3));
            viewGroup.addView(creditPlusCenterWidget,layoutParams);

        }catch (Exception ex){
            LOGGER.e( "Exception on initCreditPlus", ex);
        }
    }
    @Override
    protected void addExtraViewsInAnimator(DashboardAnimator dashboardAnimator) {
        dashboardAnimator.setAnimatorListener(this);

        //TODO add animation for cards
    }

    @Override
    protected void setCardsLayoutParams(NonScrollExpandableListView expandableListView) {
        ((RelativeLayout.LayoutParams)expListView.getLayoutParams()).addRule(RelativeLayout.BELOW,R.id.credit_plus_center);

    }


    @Override
    public void onAnimationEnd(RelativeViewAnimator.AnimationInfo animationInfo) {
        if(animationInfo.getView() instanceof DashboardMenuAvatar && creditPlusCenterWidget!=null){
            creditPlusCenterWidget.setVisibility(View.VISIBLE);
            creditPlusCenterWidget.startAnimation(false);
            expListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationStart() {
        if(creditPlusCenterWidget!=null){
            creditPlusCenterWidget.setVisibility(View.INVISIBLE);
            expListView.setVisibility(View.INVISIBLE);
            //prepareListData();
           // creditPlusCenterWidget.reset();
        }
    }

    protected void initCards() {

        final ViewTreeObserver obs = expListView.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int [] position = new int[2];
                expListView.getLocationOnScreen(position);
                LOGGER.d("Postion is screen"+position[1]);
                ScreenMeasure screenMeasure = new ScreenMeasure(getContext());
                screenMeasure.getHeight();

                int [] positionCreditPlus = new int[2];
                creditPlusCenterWidget.getLocationOnScreen(positionCreditPlus);
                //check if we can move it further down ( add top margin)
                int cardYposition = screenMeasure.getHeight() - position[1] + expListView.getHeight();
                LOGGER.d("Postion cardYposition"+(cardYposition));

                if(cardYposition > 0){
                    int topMargin = screenMeasure.getHeight() - positionCreditPlus[1] - expListView.getHeight() - 4*ScreenMeasure.dpToPx(20) - creditPlusCenterWidget.getHeight() ;
                    LOGGER.d("Postion top margin"+(topMargin));

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) expListView.getLayoutParams();
                    layoutParams.topMargin= topMargin>0 ? topMargin : 0;
                    expListView.setLayoutParams(layoutParams);
                }
                expListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add(new BalanceCardHeader("Plăteşte factura celor dragi", null, null));
        listDataHeader.get(0).setShowJustHighTextFlag(true);
        listDataChild.put(listDataHeader.get(0),null);

        listAdapter = new ExpandableListAdapter(getActivity(),listDataHeader,listDataChild,null);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                new NavigationAction(getContext(), IntentActionName.PAY_BILL_ANONYMOUS).startAction();
               // startActivity(new Intent(getContext(),PayBillActivity.class));
                return false;
            }
        });
    }

    public void getInvoicePayBillMessageVov() {
        if (getActivity() == null) {
            return;
        }

        User user = VodafoneController.getInstance().getUser();
        if(!(user instanceof CorpSubUser) && !(user instanceof CorpUser)){
            return;
        }

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();

        Subscription subscription = new BillingServices(getActivity().getApplicationContext()).getInvoiceDetails(msisdn, ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<InvoiceDetailsSuccess>>() {
            @Override
            public void onNext(GeneralResponse<InvoiceDetailsSuccess> generalResponse) {
                super.onNext(generalResponse);
                //{"invoiceNo":"277706838","accountNo":"235368857","invoiceAmount":0.0,"issueDate":1501102800000}
                //Factură scadentă la [DD.MMMM.YYYY], total estimate[ bill_amount], din care [extra cost_amount] costuri suplimentare.
                if (generalResponse.getTransactionStatus() == 0 && generalResponse.getTransactionSuccess() != null) {
                    InvoiceDetailsSuccess invoiceDetailsSuccess = generalResponse.getTransactionSuccess();
                    VoiceOfVodafone vov = null;
                    try {
                        Float invoiceAmount = Float.valueOf(invoiceDetailsSuccess.getInvoiceAmount());
                        if (invoiceAmount > 0) {
                            String message = "Factură scadentă la " + DateUtils.getDate(invoiceDetailsSuccess.getIssueDate(),
                                    new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"))) + ", total estimat ";

                            String secondMessage = NumbersUtils.twoDigitsAfterDecimal(invoiceAmount) + " " + BillingOverviewLabels.getBilling_overview_ron_unit() + ".";// din care ";
                            //String finalMessage = NumbersUtils.twoDigitsAfterDecimal(costControl.getAdditionalCost().getTotalCost()) + " " + BillingOverviewLabels.getBilling_overview_ron_unit() + " costuri suplimentare";

                            vov = new VoiceOfVodafone(3, 10, VoiceOfVodafoneCategory.Pay_Bill, null, message + secondMessage, "Plăteşte factura", "Mai târziu",
                                    true, true, VoiceOfVodafoneAction.RedirectWithIntent, VoiceOfVodafoneAction.Dismiss);
                            vov.setIntentActionName(IntentActionName.PAY_BILL_OWN);
                        }/* else {
                            String message = "Plata facturii tale a fost înregistrată";
                            vov = new VoiceOfVodafone(3, 10, "bill_ready_informative_message", null, message, "Mai târziu", null,
                                    true, false, VoiceOfVodafoneAction.Dismiss, null);
                        }*/

                        VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
                        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        }
    }
