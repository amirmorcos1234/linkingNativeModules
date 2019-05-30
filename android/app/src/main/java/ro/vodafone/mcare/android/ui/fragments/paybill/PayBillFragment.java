package ro.vodafone.mcare.android.ui.fragments.paybill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.interfaces.BillingFragmentInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.MyCardsService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.PayBillActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews.PaybillAnonymousCardView;
import ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews.PaybillNoInvoiceView;
import ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews.PaybillOwnInvoiceCardView;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Bivol Pavel on 26.01.2017.
 */
public class PayBillFragment extends BaseFragment implements BillingFragmentInterface, InputEventsListenerInterface {
    public static String TAG = "PayBillFragment";
    private static long days14InMiliseconds = 1209600000;
    private PayBillActivity payBillActivity;

    private String accountNo;
    private String invoiceAmount;
    private String invoiceNo;
    private String issueDate;
    private PaybillAnonymousCardView anonymousCardView;
    private PaybillOwnInvoiceCardView ownInvoiceCardView;
    private CreditCardSelection selectedCreditCard=null;
    private SaveCreditCard saveCreditCard;
    @BindView(R.id.invoice_info_container)
    LinearLayout invoiceInfoContainer;
    @BindView(R.id.pay_another_bill_card)
    VodafoneGenericCard payAnotherBillCard;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pay_own_bill_button:
                    trackEvent(TealiumConstants.pay_own_bill_button);
                    UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
                    InvoiceDetailsSuccess invoiceDetails = (InvoiceDetailsSuccess) RealmManager.getRealmObject(InvoiceDetailsSuccess.class);
                    Log.d(TAG, "onClick: " + invoiceDetails);

                    if (invoiceDetails != null) {

                        payBillActivity.doPaymentBill(null, invoiceAmount, userProfile.getEmail(),
                                accountNo, invoiceNo);
                    }
                    break;
                case R.id.pay_another_bill_button:
                    trackEvent(TealiumConstants.paybill_anonymous_button);
                    payBillActivity.addFragment(new AnonymousPayBillFragment());
                    break;
            }
        }
    };

    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.pay_bill_view);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }

    View.OnClickListener payOwnBillAnonymouslyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ban = null;
            if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
            }
            payBillActivity.getInvoiceDetails(PayBillFragment.this, null, ban, true);
        }
    };

    @Override
    public Fragment updateFragment(InvoiceDetailsSuccess invoiceDetailsSuccess) {
        Log.d(TAG, "updateFragment: ");
        if (invoiceDetailsSuccess != null) {

            this.accountNo = invoiceDetailsSuccess.getAccountNo();
            this.invoiceAmount = invoiceDetailsSuccess.getInvoiceAmount();
            this.invoiceNo = invoiceDetailsSuccess.getInvoiceNo();
            this.issueDate = invoiceDetailsSuccess.getIssueDate();

            Log.d(TAG, "updateFragment: accountNo " + accountNo);
            Log.d(TAG, "updateFragment: issuedate " + issueDate);
            Log.d(TAG, "updateFragment: invoiceAmount " + invoiceAmount);

        }
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_pay_bill, container, false);

        ButterKnife.bind(this, v);
        payBillActivity = (PayBillActivity) getActivity();

        getDataFromBundle();

        setNavigationHeader();
        populateView();

        setPayAnotherBillButtonListener();
        initTracking();
        return v;
    }

    private void setNavigationHeader() {
        payBillActivity.getNavigationHeader()
                .displayDefaultHeader()
                .setTitle(PayBillLabels.getPayBillTitle())
                .buildBanSelectorHeader();
    }

    private void initTracking() {
        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billing,TealiumConstants.pay_bill_view);

        PayYourBillTrackingEvent event = new PayYourBillTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public void populateView() {
        Log.d(TAG, "populateView()");
        Profile profile = null;
        try {
            profile = (Profile) RealmManager.getRealmObject(Profile.class);

            if (payBillActivity.isOwnBillPaid())
                addBillsArePaidMessageView();
            else if (payBillActivity.isNoBillIssued())
                addNoBillIsueedMessageView();
            else if (payBillActivity.isApiFailed())
                addAnonymousPaymentView();
            else
                displayOwnInvoiceView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNoBillIsueedMessageView() {
        PaybillNoInvoiceView noInvoiceView = new PaybillNoInvoiceView(getContext());
        noInvoiceView.setInvoiceMessage(PayBillLabels.getPayBillNoBillIssued());
        noInvoiceView.setIssuedDate(PayBillLabels.getPayBillNextInvoiceWillBeIssuedOn() + " " + getNextBillCycle());
        if (invoiceInfoContainer.getChildCount() > 0)
            invoiceInfoContainer.removeAllViews();
        invoiceInfoContainer.addView(noInvoiceView);
    }

    private void addBillsArePaidMessageView() {
        PaybillNoInvoiceView noInvoiceView = new PaybillNoInvoiceView(getContext());
        noInvoiceView.setInvoiceMessage(PayBillLabels.getPayBillAllBillIsPaid());
        noInvoiceView.setIssuedDate(PayBillLabels.getPayBillNextInvoiceWillBeIssuedOn() + " " + getNextBillCycle());
        if (invoiceInfoContainer.getChildCount() > 0)
            invoiceInfoContainer.removeAllViews();
        invoiceInfoContainer.addView(noInvoiceView);
    }

    private void addAnonymousPaymentView() {
        anonymousCardView = new PaybillAnonymousCardView(getContext());
        anonymousCardView.showApiFailedCardError();
        anonymousCardView.setPayAnotherBillButtonListener(payOwnBillAnonymouslyListener);
        anonymousCardView.fillEmailInput(VodafoneController.getInstance().getUserProfile().getEmail());
        if (invoiceInfoContainer.getChildCount() > 0)
            invoiceInfoContainer.removeAllViews();
        invoiceInfoContainer.addView(anonymousCardView);
    }

    private void displayOwnInvoiceView() {
        ownInvoiceCardView = new PaybillOwnInvoiceCardView(getContext());
        ownInvoiceCardView.setAmountToPay(String.format(AppLabels.getRONAmountCurrency(), invoiceAmount));
        ownInvoiceCardView.setClientCodeNumber(accountNo);
        ownInvoiceCardView.setBillNumberValue(PayBillLabels.getPayBillSeries() + invoiceNo);
        ownInvoiceCardView.setDueDateValue(getDate(issueDate));
        ownInvoiceCardView.setPayBillButtonClickListener(clickListener);
        ownInvoiceCardView.updateView();
        loadCards();
        if (!isUndeDueDate(issueDate))
            ownInvoiceCardView.displayNotPaidUnderDueDateMessage();
        if (invoiceInfoContainer.getChildCount() > 0)
            invoiceInfoContainer.removeAllViews();

        invoiceInfoContainer.addView(ownInvoiceCardView);
    }
    private void addCards(boolean is_max, List<Card> myCards) {
        if (myCards!=null&&!myCards.isEmpty()) {
            for (int i=0;i<myCards.size();i++) {
                Card card = myCards.get(i);
                final CreditCardSelection creditCardSelection =new CreditCardSelection(getActivity());
                creditCardSelection.setCard(card);
                if(i==0) {
                    creditCardSelection.selectCard(true);
                    payBillActivity.selectedCreditCard = creditCardSelection;
                } else
                    creditCardSelection.selectCard(false);

                creditCardSelection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(payBillActivity.selectedCreditCard!=null) {
                            payBillActivity.selectedCreditCard.selectCard(false);
                        }
                        payBillActivity.selectedCreditCard = creditCardSelection;
                        payBillActivity.selectedCreditCard.selectCard(true);
                        payBillActivity.saveCreditCard.selectRadioButton(false);
                    }
                });
                ownInvoiceCardView.getCardsContainer().addView(creditCardSelection);

            }
        }
        boolean isSelected = false;
        if(myCards==null||myCards.size()==0)isSelected =true;
        addNewCreditCard(is_max,isSelected);
    }
    private void addNewCreditCard(boolean is_max,boolean isSelected){
        payBillActivity.saveCreditCard =new SaveCreditCard(getContext());
        payBillActivity.saveCreditCard.selectRadioButton(isSelected);
        if(!is_max)
            payBillActivity.saveCreditCard.enableCheckBox(true);
        else
            payBillActivity.saveCreditCard.enableCheckBox(false);

        payBillActivity.saveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payBillActivity.saveCreditCard.selectRadioButton(true);
                if(payBillActivity.selectedCreditCard!=null)
                    payBillActivity.selectedCreditCard.selectCard(false);
                payBillActivity.selectedCreditCard = null;
            }
        });
        ownInvoiceCardView.getOtherCardContainer().addView(payBillActivity.saveCreditCard);

    }
    private void loadCards() {
        showLoadingDialog();
        MyCardsService myCardsService = new MyCardsService(getContext());
        myCardsService.getCards(VodafoneController.getInstance().getUserProfile().getUserName()).subscribe(new RequestSaveRealmObserver<GeneralResponse<CardsResponse>>(){
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                addCards(false,null);
            }

            @Override
            public void onNext(GeneralResponse<CardsResponse> response) {
                super.onNext(response);
                stopLoadingDialog();
                if(response!=null&&response.getTransactionSuccess()!=null&&response.getTransactionSuccess().getCardList().size()>0)
                    addCards(response.getTransactionSuccess().getMaxReached(),response.getTransactionSuccess().getCardList());
                else
                    addCards(false,null);
            }
        });
    }


    private boolean isUndeDueDate(String dueDate) {
        boolean isUndeDueDate = false;

        long currentDate = new Date().getTime();
        long invoiceDueDate = Long.valueOf(dueDate);

        if (currentDate >= invoiceDueDate + days14InMiliseconds) {
            isUndeDueDate = true;
        }

        return !isUndeDueDate;
    }

    private String getDate(String timeStampStr) {
        try {
            DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"));
            Date netDate = (new Date(Long.parseLong(timeStampStr)));
            String dateString = sdf.format(netDate);

            return dateString != null ? WordUtils.capitalize(dateString) : dateString;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_SELECTOR_UPDATED) {
                Ban selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedBan();
                Log.d(TAG, "Ban Selector number: " + selectedBan.getNumber());

                payBillActivity.getInvoiceDetails(this, null, selectedBan.getNumber(), false);
            }
        }
    }

    private String getNextBillCycle() {

        Calendar calendar;

        int billCycleDate = 0;
        int currentDayOfMonth;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        User user = VodafoneController.getInstance().getUser();
        if(user instanceof EbuMigrated)
            billCycleDate = billCycleForEbuMigrated();
        else
            billCycleDate = billCycleForOtherThanMigrated();

        if (billCycleDate < currentDayOfMonth || billCycleDate == currentDayOfMonth) {
            calendar.add(Calendar.MONTH, +1);
            calendar.add(Calendar.DAY_OF_MONTH, +(billCycleDate - currentDayOfMonth));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfMonth - billCycleDate));
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO")).format(calendar.getTime());

        return WordUtils.capitalize(date);
    }

    private int billCycleForEbuMigrated(){
        EntityDetailsSuccess entityDetailsSuccess = (EntityDetailsSuccess) RealmManager.getRealmObject(EntityDetailsSuccess.class);
        if(entityDetailsSuccess != null && entityDetailsSuccess.isValid()){
            return entityDetailsSuccess.getBillCycle();
        } else {
            Long issueDate = Long.valueOf(this.issueDate);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(issueDate));
            return calendar1.get(Calendar.DAY_OF_MONTH);
        }
    }

    private int billCycleForOtherThanMigrated(){
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if (profile != null && profile.getBillCycleDate() != null) {
            return profile.getBillCycleDate();
        } else {
            Long issueDate = Long.valueOf(this.issueDate);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(issueDate));
            return calendar1.get(Calendar.DAY_OF_MONTH);
        }
    }

    public void getDataFromBundle() {
        Bundle bungle = getArguments();
        if (bungle == null || !bungle.getBoolean("getInvoiceFromRealm")) {
            return;
        }
        InvoiceDetailsSuccess invoice = (InvoiceDetailsSuccess) RealmManager.getRealmObject(InvoiceDetailsSuccess.class);
        updateFragment(invoice);
    }

    private void setPayAnotherBillButtonListener() {
        VodafoneButton payAnotherBillButton = payAnotherBillCard.findViewById(R.id.pay_another_bill_button);
        payAnotherBillButton.setText(PayBillLabels.getPayBillButton());
        payAnotherBillButton.setOnClickListener(clickListener);
    }

    @Override
    public void displayErrorMessage() {
        if (anonymousCardView != null)
            anonymousCardView.displayInputError();
    }

    @Override
    public void hideErrorMessage() {
        if (anonymousCardView != null)
            anonymousCardView.hideInputError();
    }

    @Override
    public void activateButton() {
        if (anonymousCardView != null)
            anonymousCardView.activatePaymentButton();
    }

    @Override
    public void inactivateButton() {
        if (anonymousCardView != null)
            anonymousCardView.inactivatePaymentButton();
    }

    public static class PayYourBillTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "pay your bill";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "pay your bill");

            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "pay bill";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "pay your bill";
            s.getContextData().put("prop21", s.prop21);
        }
    }


    @Override
    public String getAnonymousEmailAddress() {
        return anonymousCardView.getEmail();
    }

    @Override
    public String getAnonymousInvoiceValue() {
        return anonymousCardView.getInvoiceValue();
    }

    @Override
    public void manageAnonymousPaymentErrors(String errorCode) {
        if (errorCode.equals(ErrorCodes.API23_INVOICE_ALREADY_PAID.getErrorCode())) {
            payBillActivity.setIsOwnBillPaid(true);
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillInvoiceAlreadyPaid()).success(false).show();
        } else if (errorCode.equals(ErrorCodes.API23_INVOICE_NOT_ISSUED.getErrorCode())) {
            payBillActivity.setNoBillIssued(true);
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillInvoiceNotAvailable()).success(false).show();
        } else if (errorCode.equals(ErrorCodes.API23_SUBSCRIBER_NOT_FOUND.getErrorCode())){
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillDistributionOnMultipleAccountsError()).success(false).show();
        } else if(errorCode.equals(ErrorCodes.API23_DISTRIBUTIONS_ON_MULTIPLE_ACCOUNTS.getErrorCode())){
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillDistributionOnMultipleAccountsError()).success(false).show();
        } else {
            payBillActivity.setApiFailed(true);
            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
        }
    }

    @Override
    public String getAnonymousPhoneNumber() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_PAYBILL_OWN);
    }
}
