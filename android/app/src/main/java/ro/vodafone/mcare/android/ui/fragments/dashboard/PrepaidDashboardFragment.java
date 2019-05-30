package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BalanceService;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.nonscrollable.NonScrollExpandableListView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.widget.CostControlWidget;
import ro.vodafone.mcare.android.widget.CostControlWidgetPrepaid;
import ro.vodafone.mcare.android.widget.animators.DashboardAnimator;
import ro.vodafone.mcare.android.widget.animators.RelativeViewAnimator;
import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;
import ro.vodafone.mcare.android.widget.avatar.DashboardMenuAvatar;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardDetail;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;
import ro.vodafone.mcare.android.widget.creditplus.CreditPlusWidget;
import ro.vodafone.mcare.android.widget.creditplus.CreditPlusWidgetController;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;
import ro.vodafone.mcare.android.widget.gauge.ExtraOptionsController;
import rx.Subscription;

/**
 * Created by Victor Radulescu on 3/1/2017.
 */

public class PrepaidDashboardFragment extends BaseDashboardFragment implements DashboardAnimator.AnimationListener, CostControlWidgetController.CostControlRequestListener{

    private CostControlWidgetPrepaid costControlWidget;

    ImageView refresh;
    VodafoneTextView last_update;

    private CreditPlusWidget creditPlusCenterWidget;
    private CreditPlusWidget creditPlusRightWidget;

    boolean makeMakeCostControlGone = false;
    boolean allAnimationsCompleted = false;

    private static final long durationFadeInOut = 1000;

    public PrepaidDashboardFragment() {
    }

    public static PrepaidDashboardFragment newInstance() {

        PrepaidDashboardFragment dashboardFragment = new PrepaidDashboardFragment();

        Bundle bundle = new Bundle();
        dashboardFragment.setArguments(bundle);
        return dashboardFragment;
    }


    @Override
    protected void initExtraWidgets(RelativeLayout baseContent) {

        initCostControl(baseContent);
        initCreditPlus(baseContent);
        initLastUpdate(baseContent);
    }

    @Override
    protected void addExtraViewsInAnimator(DashboardAnimator dashboardAnimator) {
        dashboardAnimator.setAnimatorListener(this);
        dashboardAnimator.addCostControlAnimation(costControlWidget);

    }

    @Override
    protected void setCardsLayoutParams(NonScrollExpandableListView expandableListView) {
       // RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
        ((RelativeLayout.LayoutParams)expandableListView.getLayoutParams()).addRule(RelativeLayout.BELOW,R.id.info_last_updated);

    }

    @Override
    protected void startGroupAnimation() {
        super.startGroupAnimation();

        if(!animateElements){
            creditPlusRightWidget.setVisibility(View.VISIBLE);
            creditPlusRightWidget.startAnimation(true);
            allAnimationsCompleted = true;

            CostControl costControl = (CostControl) RealmManager.getRealmObject(CostControl.class);
            if(costControl==null || !costControl.isShortBalanceListValid()){
                if(makeMakeCostControlGone){
                    hideGaugeAndAnimate();
                    makeMakeCostControlGone = false;
                }
            }
        }
    }

    private void initCostControl(RelativeLayout viewGroup){
        costControlWidget= (CostControlWidgetPrepaid) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_cost_control_prepaid_widget, null, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.dashboard_menu_avatar_widget);
        layoutParams.topMargin = -ScreenMeasure.dpToPx(ExtraOptionsController.EXTRA_RADIOS_IN_PX)/3;
        //LOGGER.d("cost control marginTop "+ -ScreenMeasure.dpToPx(ExtraOptionsController.EXTRA_RADIOS_IN_PX)/2);
        viewGroup.addView(costControlWidget,layoutParams);
        CostControlWidgetController.getInstance().addCostControlRequestListener(this);
    }


    private void initLastUpdate(RelativeLayout viewGroup) {
        RelativeLayout lastUpdateGroupView= (RelativeLayout) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_last_update, null, false);

        last_update = (VodafoneTextView) lastUpdateGroupView.findViewById(R.id.last_update);
        refresh = (ImageView) lastUpdateGroupView.findViewById(R.id.refresh);
        refresh.setColorFilter(Color.parseColor("#ffffff"));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.cost_control_widget);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        viewGroup.addView(lastUpdateGroupView,layoutParams);
    }
    private void reinitLastUpdateAfterCostControlGone(){
        View view =getActivity().findViewById(R.id.info_last_updated);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW,R.id.dashboard_menu_avatar_widget);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        view.setLayoutParams(layoutParams);
    }

    private void setInfoLastUpdate() {
        try {
            long lastUpdateDateUnix = ((CostControl) RealmManager.getRealmObject(CostControl.class)).getLastUpdateDate();
            String s = DateUtils.getTime(lastUpdateDateUnix);
            String infoLastUpdate = String.format(AppLabels.getLast_update(), s);
            last_update.setText(infoLastUpdate);
            refresh.setVisibility(View.VISIBLE);
            last_update.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reinitPositionForCardList(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.last_update);
        expListView.setLayoutParams(layoutParams);
        expListView.invalidate();
    }

    protected void initCards(@Nullable CostControl costControl) {
        prepareListData(costControl);
        if(getActivity() == null) {
            return;
        }
        getPrepaidBalanceSecondary();
    }

    /*
    * Preparing the list data
    */
    @Override
    protected void prepareListData(@Nullable CostControl newCostControl) {
        listDataHeader = listDataHeader==null ? new ArrayList<BalanceCardHeader>():listDataHeader;
        listDataChild = listDataChild==null? new HashMap<BalanceCardHeader,List<BalanceCardDetail>>() :listDataChild;
        CostControl costControl = newCostControl!=null? newCostControl : (CostControl) RealmManager.getRealmObject(CostControl.class);

        try {
            if (costControl != null && costControl.getAdditionalCost() != null ) {
                initCostControlCardDetailsList(costControl, costControl.getAdditionalCost().getTotalCost());
            }
        } catch (Exception ex){
            LOGGER.d("Exception initCostControlCardDetailsList", ex);
        }


    }

    private void initSecondaryBalanceCard(Float secondarybalance, Long expirationDate){
        BalanceCardHeader secondaryBalanceCard = null;
        List<BalanceCardDetail> secondaryBalanceCardDetails = null;
        LOGGER.d("initSecondaryBalanceCard with Secondary balance : " + secondarybalance );

        if(expirationDate != null)
        {
            Date today = new Date();
            Date expiration = new Date(expirationDate);
            if(expiration.before(today))
                return;
        }

        if(secondarybalance != null && secondarybalance > 0f) {
            //create prepaid secondary balance
            String textHigh = dashboardActivity.getResources().getString(R.string.text_high_prepaid_secondary_balance_card);
            String textMiddle = getActivity().getResources().getString(R.string.text_amount_balance_card,NumbersUtils.twoDigitsAfterDecimal(secondarybalance));
            SpannableString textLow = null;
            secondaryBalanceCard = new BalanceCardHeader("Costuri în afara planului tău",textHigh, textMiddle, textLow);
        }
        if(secondaryBalanceCard != null) {
            secondaryBalanceCard.setArrowVisible(false);
            listDataHeader.add(secondaryBalanceCard);
            listDataChild.put(secondaryBalanceCard, secondaryBalanceCardDetails);
            syncronizeCard();
        }
    }

    private void initCostControlCardDetailsList(CostControl costControl, Float totalCost){
        if(listDataHeader.size()>0 && listDataHeader.get(0).getTextHigh().equals("Credit utilizat") ||
                listDataHeader.size()>1 && listDataHeader.get(1).getTextHigh().equals("Credit utilizat")     ){
            return;
        }
        //check if another exists
        BalanceCardHeader costControlCardOverspent = null;

        List<BalanceCardDetail> costControlCardDetailsList = null;

        if(totalCost != null ) {
            //create postpaid cost control overspent card data
            String textHigh = "Credit utilizat";
            String textMiddle = dashboardActivity.getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(totalCost));
            SpannableString textLow = new SpannableString("În ultimele 30 de zile");
            costControlCardOverspent = new BalanceCardHeader("Costuri în afara planului tău",textHigh, textMiddle.replaceAll(",","."),textLow);

            costControlCardDetailsList= new ArrayList<>();
            if(costControl.getAdditionalCost().getVoiceCost() != null /* && costControl.getAdditionalCost().getVoiceCost() != 0f*/) {
                String textLeft = dashboardActivity.getResources().getString(R.string.voice_prepaid_text_left);
                String textRight = dashboardActivity.getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(costControl.getAdditionalCost().getVoiceCost()));
                BalanceCardDetail voiceDetail = new BalanceCardDetail(textLeft, R.drawable.landline_or_call, textRight.replaceAll(",","."), Category.VOCE);
                costControlCardDetailsList.add(voiceDetail);
            }
            if(costControl.getAdditionalCost().getDataCost() != null /* && costControl.getAdditionalCost().getVoiceCost() != 0f*/) {
                String textLeft = dashboardActivity.getResources().getString(R.string.data_prepaid_text_left);
                String textRight = dashboardActivity.getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(costControl.getAdditionalCost().getDataCost()));
                BalanceCardDetail  dataDetail = new BalanceCardDetail(textLeft, R.drawable.ic_mobile_data, textRight, Category.DATE);
                costControlCardDetailsList.add(dataDetail);
            }
            if(costControl.getAdditionalCost().getSmsCost() != null /* && costControl.getAdditionalCost().getVoiceCost() != 0f*/) {
                String textLeft = dashboardActivity.getResources().getString(R.string.sms_prepaid_text_left);
                String textRight = dashboardActivity.getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(costControl.getAdditionalCost().getSmsCost()));
                BalanceCardDetail  smsDetail = new BalanceCardDetail(textLeft, R.drawable.text, textRight, Category.SMS);
                costControlCardDetailsList.add(smsDetail);
            }
            if(costControl.getAdditionalCost().getOtherCost() != null  /*&& costControl.getAdditionalCost().getVoiceCost() != 0f*/) {
                String textLeft = dashboardActivity.getResources().getString(R.string.other_postpaid_text_left);
                String textRight = dashboardActivity.getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(costControl.getAdditionalCost().getOtherCost()));
                BalanceCardDetail otherDetail = new BalanceCardDetail(textLeft, R.drawable.view_grid_white_48, textRight, Category.OTHER);
                costControlCardDetailsList.add(otherDetail);
            }
            //0:1 add to the last
            listDataHeader.add(costControlCardOverspent);
            listDataChild.put(costControlCardOverspent, costControlCardDetailsList);

            syncronizeCard();

        }
    }

    private void initCreditPlus(RelativeLayout viewGroup){
        try {
            if(!CreditPlusWidgetController.getInstance().isValidData()){
                //TODO show error
            }
            creditPlusRightWidget= (CreditPlusWidget) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_credit_plus_widget, null, false);
            ViewTreeObserver vto = creditPlusRightWidget.getViewTreeObserver();

            vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    creditPlusRightWidget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int imageW = creditPlusRightWidget.getMeasuredWidth();
                    int height = creditPlusRightWidget.getMeasuredHeight();
                    int conscontrolTopMargin = ((RelativeLayout.LayoutParams)costControlWidget.getLayoutParams()).topMargin;
                    int marginTopBottom = (avatarWidget.getHeight() - conscontrolTopMargin -height-costControlWidget.getPlusButtonHeight())/2;
                    int marginBottm = marginTopBottom + costControlWidget.getPlusButtonHeight()/2;
                    LOGGER.d("marginTopBottom "+marginTopBottom+" marginBottm "+marginBottm+" cost control plus"+conscontrolTopMargin);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) creditPlusRightWidget.getLayoutParams();
                    layoutParams.setMargins(0,marginTopBottom,0,marginBottm);
                    creditPlusRightWidget.setLayoutParams(layoutParams);
                }
            });
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW,R.id.dashboard_vov_widget);
            layoutParams.addRule(RelativeLayout.END_OF,R.id.center_right_to_dashboard_separator);
            layoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.center_right_to_dashboard_separator);

            viewGroup.addView(creditPlusRightWidget,layoutParams);

        }catch (Exception ex){
            LOGGER.e( "Exception on initCreditPlus", ex);
        }
    }

    @Override
    public void onAnimationEnd(RelativeViewAnimator.AnimationInfo animationInfo) {
        if(animationInfo.getView() instanceof DashboardMenuAvatar){
            creditPlusRightWidget.setVisibility(View.VISIBLE);
            creditPlusRightWidget.startAnimation(false);
        }
        if(animationInfo.getView() instanceof CostControlWidget){
            allAnimationsCompleted = true;

            CostControl costControl = (CostControl) RealmManager.getRealmObject(CostControl.class);
            if(costControl==null || !costControl.isShortBalanceListValid()){
                if(makeMakeCostControlGone){
                    hideGaugeAndAnimate();
                    makeMakeCostControlGone = false;
                }
            }
        }
    }

    @Override
    public void onAnimationStart() {
        if(creditPlusRightWidget!=null){
            creditPlusRightWidget.setVisibility(View.INVISIBLE);
            // creditPlusCenterWidget.reset();
        }
    }

    @Override
    public void onCostControlRequestSucces(@NonNull CostControl costControl)
    {
        if(costControlWidget!=null){
            costControlWidget.onCostControlRequestCompleted(costControl);
        }
        setInfoLastUpdate();
        getInvoicePayBillMessageVov();
        if(!costControl.isShortBalanceListValid()){
            showWarningToast(AppLabels.getToastErrorSomeInfoNotLoaded());
            if(allAnimationsCompleted && !makeMakeCostControlGone){
                hideGaugeAndAnimate();
            }else{
                makeMakeCostControlGone = true;
            }
            //TODO start hide gauge animations
        }
        initCards(costControl);

        //getAdditionalCostForOtherMsidns();
    }

    @Override
    public void onCostControlRequestFailed(String error) {
        if(costControlWidget!=null){
            costControlWidget.onCostControlRequestFailed(error);
        }
        getInvoicePayBillMessageVov();
        addErrorCard();
        showToastErrorMessage();
        refresh.setVisibility(View.GONE);
        last_update.setVisibility(View.GONE);
    }

    @Override
    public void onExtraOptionsRequestCompleted() {
        if(costControlWidget!=null){
            costControlWidget.onExtraOptionsRequestCompleted();
        }
    }

   /* @Override
    public void onCostControlUpdate(CostControl costControl) {
        if(costControlWidget!=null){
            costControlWidget.onCostControlRequestCompleted(costControl);
        }
    }*/

    private void initCreditPlusCredit(RelativeLayout viewGroup){
        try {

            creditPlusCenterWidget= (CreditPlusWidget) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_credit_plus_center_prepaid_widget, null, false);
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

    private void hideGaugeAndAnimate(){
        if(costControlWidget==null){
            return;
        }
        //if(CreditPlusWidgetController.getInstance().isValidData()){
           hideCreditPlusRight();
        //}
        costControlWidget.setClickable(false);
        hideCostControlWidget();

    }

    private void showCreditPlusCenter(){

        if(getActivity() == null)
            return;

        RelativeLayout viewGroup = ((RelativeLayout) getActivity().findViewById(R.id.inside_scrollView_dashboard));
        initCreditPlusCredit(viewGroup);
        creditPlusCenterWidget.startAnimation(animateElements);
        creditPlusCenterWidget.setAlpha(0);
        //creditPlusCenterWidget.setAlpha(0);
        ViewAnimator
                .animate(creditPlusCenterWidget)
                .scale(0,1)
                .alpha(0,1)
                .interpolator(Interpolators.OUT_EXPO)
                .duration(durationFadeInOut)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        //TODO reposition
                        //costControlWidget.setAlpha(0);
                        //reinitLastUpdateAfterCostControlGone();
                        //removeView(costControlWidget);
                    }
                })
                .startDelay(250)
                .start();
    }

    private void removeView(View viewToRemove){

        if (getActivity() == null)
            return;

        try {
            ((RelativeLayout) getActivity().findViewById(R.id.inside_scrollView_dashboard)).removeView(viewToRemove);
        }catch (Exception e){
            if(((RelativeLayout) viewToRemove.findViewById(R.id.inside_scrollView_dashboard)) != null)
            ((RelativeLayout) viewToRemove.findViewById(R.id.inside_scrollView_dashboard)).removeView(viewToRemove);
            Log.d("PrepaidDashboard" ,"error in removeView" +e);
            e.printStackTrace();
        }
    }

    private void hideCreditPlusRight(){
        ViewAnimator
                .animate(creditPlusRightWidget)
                .interpolator(Interpolators.IN_EXPO)
                .scale(1,0)
                .alpha(1,0)
                .duration(durationFadeInOut)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        removeView(creditPlusRightWidget);
                        //TODO reposition
                        //costControlWidget.setAlpha(0);
                        //reinitLastUpdateAfterCostControlGone();
                        //removeView(costControlWidget);
                    }
                })
                .startDelay(500)
                .start();
    }
    private void hideCostControlWidget(){
        ViewAnimator
                .animate(costControlWidget)
                .scale(1,0)
                .alpha(1,0)
                .interpolator(Interpolators.IN_EXPO)
                //.interpolator(Interpolators.OUT_EXPO)
                .duration(durationFadeInOut)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        costControlWidget.setAlpha(0);
                        costControlWidget.setClickable(false);
                        showCreditPlusCenter();

                    }
                })
                .startDelay(1000)
                .start();
    }
    private void getPrepaidBalanceSecondary(){

        LOGGER.d("getPrepaidBalanceSecondary: ");

        Subscription subscription = new BalanceService(getActivity()).getBalanceSecondary().subscribe(new RequestSaveRealmObserver<GeneralResponse<BalanceSecondarySuccess>>() {

            @Override
            public void onCompleted() {
                LOGGER.d("getBalanceSecondary onCompleted");
                BalanceSecondarySuccess balanceCreditSuccess = (BalanceSecondarySuccess) RealmManager.getRealmObject(BalanceSecondarySuccess.class);
                if(balanceCreditSuccess!=null)
                    initSecondaryBalanceCard(balanceCreditSuccess.getBalance(), balanceCreditSuccess.getBalanceValidity());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(GeneralResponse<BalanceSecondarySuccess> response) {
                super.onNext(response);
            }
        });
        compositeSubscription.add(subscription);
    }
    public void getInvoicePayBillMessageVov() {
        if(getActivity()==null){
            return;
        }
        if(!(VodafoneController.getInstance().getUser() instanceof PrepaidHybridUser)){
            return;
        }
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        Subscription subscription = new BillingServices(getActivity()).getInvoiceDetails(msisdn, ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<InvoiceDetailsSuccess>>() {
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
        compositeSubscription.add(subscription);
    }

}
