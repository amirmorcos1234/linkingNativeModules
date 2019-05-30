package ro.vodafone.mcare.android.rest;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.EbuOfferEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.billing.GetPaymentInputsResponse;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.callDetails.CallDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.costControl.CostControlDataVolatile;
import ro.vodafone.mcare.android.client.model.costControl.Extraoption;
import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.creditInAdvance.EligibilityInfo;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.eligibility.EBillSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.Post4preCountersSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.UserPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.identity.BenSuccessEbu;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRateSuccess;
import ro.vodafone.mcare.android.client.model.internationalCostRates.InternationalCallingRatesRequest;
import ro.vodafone.mcare.android.client.model.ion.IONPostpaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IONPrepaidRequest;
import ro.vodafone.mcare.android.client.model.ion.IonEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptInSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptOutSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyPointsSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.myCards.AddCardResponse;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.myCards.DeleteCardResponse;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.profile.ProfileSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.billDates.BillsDatesSuccess;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummarySuccess;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReserveSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomAcceptedOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrdersSuccess;
import ro.vodafone.mcare.android.client.model.realm.paymentAgreement.PaymentAgreementSuccess;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationRequest;
import ro.vodafone.mcare.android.client.model.realm.paymentConfirmation.PaymentConfirmationSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.seamless.InitialToken;
import ro.vodafone.mcare.android.client.model.realm.simDetails.SIMDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusEBUSuccess;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.Configurations;
import ro.vodafone.mcare.android.client.model.realm.system.Labels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.SystemStatusSuccess;
import ro.vodafone.mcare.android.client.model.recharge.RechargeVoucherSuccess;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRechargesSuccess;
import ro.vodafone.mcare.android.client.model.recover.RecoverPasswordCorrectResponse;
import ro.vodafone.mcare.android.client.model.recover.RecoverUsernameResponse;
import ro.vodafone.mcare.android.client.model.register.AccountCheck;
import ro.vodafone.mcare.android.client.model.shop.EmailRequest;
import ro.vodafone.mcare.android.client.model.shop.ShopCartRequest;
import ro.vodafone.mcare.android.client.model.shop.ShopCartSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginRequest;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlanSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.client.model.simReplace.SIMReplaceSuccess;
import ro.vodafone.mcare.android.client.model.topUp.TransferCreditTerms;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistorySuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEbuRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelRequest;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingLabelResponse;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.TravallingHintSuccess;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequestsSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeleteRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.RenameRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.rest.observers.SessionObserverBuilder;
import ro.vodafone.mcare.android.rest.observers.VerifyTimeToLeaveObserverBuilder;
import ro.vodafone.mcare.android.rest.requests.BaseRequest;
import ro.vodafone.mcare.android.rest.requests.BillRechargeRequest;
import ro.vodafone.mcare.android.rest.requests.CallDetailsReportRequest;
import ro.vodafone.mcare.android.rest.requests.CallDetailsRequest;
import ro.vodafone.mcare.android.rest.requests.CompleteProfileRequest;
import ro.vodafone.mcare.android.rest.requests.ConfirmProfileRequest;
import ro.vodafone.mcare.android.rest.requests.EBillRequest;
import ro.vodafone.mcare.android.rest.requests.EventReportRequest;
import ro.vodafone.mcare.android.rest.requests.LoginRequest;
import ro.vodafone.mcare.android.rest.requests.LogoutRequest;
import ro.vodafone.mcare.android.rest.requests.LoyaltyProgramRequest;
import ro.vodafone.mcare.android.rest.requests.LoyaltySegmentRequest;
import ro.vodafone.mcare.android.rest.requests.NotificationRequest;
import ro.vodafone.mcare.android.rest.requests.PaymentAgreementRequest;
import ro.vodafone.mcare.android.rest.requests.PaymentRequest;
import ro.vodafone.mcare.android.rest.requests.PhoneCheckRequest;
import ro.vodafone.mcare.android.rest.requests.RechargeRequest;
import ro.vodafone.mcare.android.rest.requests.RechargeVoucherRequest;
import ro.vodafone.mcare.android.rest.requests.RecoverPasswordRequest;
import ro.vodafone.mcare.android.rest.requests.RecoverUsernameRequest;
import ro.vodafone.mcare.android.rest.requests.ResendCodeRequest;
import ro.vodafone.mcare.android.rest.requests.ReserveVoucherRequest;
import ro.vodafone.mcare.android.rest.requests.ResetPasswordRequest;
import ro.vodafone.mcare.android.rest.requests.SIMChangeStatusRequest;
import ro.vodafone.mcare.android.rest.requests.SIMChangeStatusRequestEBU;
import ro.vodafone.mcare.android.rest.requests.SeamlessAddHeaderRequest;
import ro.vodafone.mcare.android.rest.requests.SeamlessFlagRequest;
import ro.vodafone.mcare.android.rest.requests.SeamlessLoginRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterActivateRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterCreateRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterResendRequest;
import ro.vodafone.mcare.android.rest.requests.SimReplaceBean;
import ro.vodafone.mcare.android.rest.requests.TransferCreditRequest;
import ro.vodafone.mcare.android.rest.requests.UpdateOfferRequest;
import ro.vodafone.mcare.android.service.exceptions.InvalidDataException;
import ro.vodafone.mcare.android.ui.activities.support.ChatStateResponse;
import ro.vodafone.mcare.android.ui.activities.support.JsonList;
import ro.vodafone.mcare.android.ui.activities.support.StartChatRequest;
import ro.vodafone.mcare.android.ui.activities.support.StartChatResponse;
import ro.vodafone.mcare.android.ui.activities.support.VodafoneMessage;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqPOJO;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessFlag;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.FuncN;

/**
 * Created by Victor Radulescu on 9/23/2016.
 */
public class RetrofitCall {

    private static RetrofitCall ourInstance = new RetrofitCall();

    private RetrofitCall() {
    }

    public static RetrofitCall getInstance() {
        return ourInstance;
    }

    public Observable<Response<GeneralResponse<UserProfile>>> retrieveUserProfile(LoginRequest loginRequest) {

        Observable<Response<GeneralResponse<UserProfile>>> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.retrieveUserProfile(loginRequest);
        return observable;
    }

    public Observable<GeneralResponse> logout(LogoutRequest logoutRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.logout(logoutRequest);

        return observable;
    }

    public Observable<GeneralResponse<InitialToken>> retrieveInitialToken(BaseRequest baseRequest) {


        Observable<GeneralResponse<InitialToken>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.retrieveInitialToken(baseRequest);
        return observable;
    }

    public Observable<GeneralResponse> seamlessAddHeader(SeamlessAddHeaderRequest seamlessAddHeaderRequest) {


        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_AUTO_HOST.seamlessAddHeader(seamlessAddHeaderRequest);

        return observable;
    }

    public Observable<GeneralResponse> seamlessAddHeader(SeamlessAddHeaderRequest seamlessAddHeaderRequest, String seamlessMsisdn) {


        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.seamlessAddHeaderPreviousMsisdn(seamlessAddHeaderRequest, seamlessMsisdn);

        return observable;
    }

    public Observable<Response<GeneralResponse<UserProfile>>> retrieveSeamlessUserProfile(SeamlessLoginRequest seamlessLoginRequest) {


        Observable<Response<GeneralResponse<UserProfile>>> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.retrieveSeamlessUserProfile(seamlessLoginRequest);

        return observable;
    }

    public Observable<GeneralResponse> sendSeamlessEvent(EventReportRequest eventReportRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.sendSeamlessEvent(eventReportRequest);

        return observable;
    }


    public Observable<GeneralResponse> setSeamlessFlag(@Body SeamlessFlagRequest seamlessFlagRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.setSeamlessFlag(seamlessFlagRequest);

        return observable;
    }

    public Observable<GeneralResponse<SeamlessFlag>> getSeamlessFlag() {

        Observable<GeneralResponse<SeamlessFlag>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSeamlessFlag();

        return observable;
    }


    public Observable<GeneralResponse<Labels>> retrieveLabels(String last_update) {

        Observable<GeneralResponse<Labels>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.retrieveLabels(last_update);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, Labels.class);
    }

    public Observable<GeneralResponse<Configurations>> retrieveConfigurations(String last_update) {


        Observable<GeneralResponse<Configurations>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.retrieveConfigurations(last_update);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, Configurations.class);
    }

    public Observable<GeneralResponse> registerAccount(SelfRegisterCreateRequest selfRegisterCreateRequest) {


        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.registerAccount(selfRegisterCreateRequest);

        return observable;
    }

    public Observable<GeneralResponse> activateUserAccount(SelfRegisterActivateRequest selfRegisterActivateRequest) {

        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.activateUserAccount(selfRegisterActivateRequest);

        return observable;
    }

    public Observable<GeneralResponse> resendActivationCode(SelfRegisterResendRequest selfRegisterResendRequest) {

        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.resendActivationCode(selfRegisterResendRequest);

        return observable;
    }

//    public Observable<GeneralResponse> recoverUsername(RecoverUsernameRequest recoverUsernameRequest) {
//
//        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_30s_GENERAL_TIMEOUT.recoverUsername(recoverUsernameRequest);
//
//        return observable;
//    }

    public Observable<GeneralResponse<RecoverPasswordCorrectResponse>> recoverPassword(RecoverPasswordRequest recoverPasswordRequest) {

        Observable<GeneralResponse<RecoverPasswordCorrectResponse>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT
                .recoverPassword(recoverPasswordRequest);

        return observable;
    }

    public Observable<GeneralResponse<AccountCheck>> registerAccountCheck(PhoneCheckRequest accountCheckRequest) {


        Observable<GeneralResponse<AccountCheck>> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.registerAccountCheck(/*cookie,*/ accountCheckRequest);

        return observable;
    }

    public Observable<GeneralResponse> confirmProfile(ConfirmProfileRequest confirmProfileRequest) {


        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.confirmProfile(confirmProfileRequest);

        return observable;
    }

    public Observable<GeneralResponse> completeProfile(CompleteProfileRequest completeProfileRequest) {


        Observable<GeneralResponse> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.completeProfile(completeProfileRequest);

        return observable;
    }

    public Observable<GeneralResponse> activateAccount(SelfRegisterActivateRequest selfRegisterActivateRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.activateAccount(selfRegisterActivateRequest);

        return observable;
    }

    public Observable<GeneralResponse> resendCode(ResendCodeRequest resendCodeRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.resendCode(resendCodeRequest);

        return observable;
    }

    public Observable<GeneralResponse<InvoiceDetailsSuccess>> getInvoiceDetails(String msisdn, String ban) {

        // String channel = "mCare";

        Observable<GeneralResponse<InvoiceDetailsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getInvoiceDetails(msisdn, ban);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentBill(PaymentRequest paymentRequest) {

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.doPaymentBill(paymentRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentRecharge(PaymentRequest paymentRequest) {

        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.doPaymentRecharge(paymentRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 19
    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy(boolean refreshData) {
        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSubscriberHierarchy();

        return refreshData ? observable : new VerifyTimeToLeaveObserverBuilder().build(observable, UserProfileHierarchy.class);
    }

    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy(boolean refreshData, String vfOdsBan) {
        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSubscriberHierarchy(vfOdsBan);

        return refreshData ? observable : new VerifyTimeToLeaveObserverBuilder().build(observable, UserProfileHierarchy.class);
    }

    public Observable<GeneralResponse<UserProfileHierarchy>> getSessionSubscriberHierarchy(boolean refreshData, String vfOdsBan) {
        Observable<GeneralResponse<UserProfileHierarchy>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSubscriberHierarchy(vfOdsBan);

        return refreshData ? SessionObserverBuilder.build(observable) : new VerifyTimeToLeaveObserverBuilder().build(observable, UserProfileHierarchy.class);
    }

    //API 20
    public Observable<GeneralResponse<CostControl>> getCostControl(String msisdn) {
        final Observable<GeneralResponse<CostControl>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCostControl(msisdn);
        return new VerifyTimeToLeaveObserverBuilder().build(observable, CostControl.class);
    }

    public Observable<GeneralResponse<CostControl>> loadCostControl(String msisdn) {
        final Observable<GeneralResponse<CostControl>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCostControl(msisdn);
        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<CostControl>> observable(String msisdn) {
        final Observable<GeneralResponse<CostControl>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCostControl(msisdn);
        return SessionObserverBuilder.build(observable);
    }

    public Observable<ArrayList<AdditionalCost>> getAdditionalCostForOtherMsidns(List<String> msisdnList) {
        ArrayList<Observable<GeneralResponse<CostControl>>> observables = new ArrayList<>();
        for (String msisdn : msisdnList) {
            Observable<GeneralResponse<CostControl>> observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCostControl(msisdn);
            observables.add(observable);
        }
        Observable observable = Observable.combineLatest((Iterable<? extends Observable<?>>) observables, new FuncN<ArrayList<AdditionalCost>>() {

            @Override
            public ArrayList<AdditionalCost> call(Object... args) {

                ArrayList<AdditionalCost> additionalCosts = new ArrayList<>();
                for (Object value : args) {
                    try {
                        if (value instanceof GeneralResponse) {
                            if (((GeneralResponse) value).getTransactionSuccess() != null) {
                                if (((CostControl) ((GeneralResponse) value).getTransactionSuccess()).getAdditionalCost() != null) {
                                    additionalCosts.add(((CostControl) ((GeneralResponse) value).getTransactionSuccess()).getAdditionalCost());
                                } else
                                    throw new InvalidDataException("getAdditionalCost is null");
                            } else if (((GeneralResponse) value).getTransactionStatus() == 2 && ((GeneralResponse) value).getTransactionFault() != null) {
                                throw new InvalidDataException(((GeneralResponse) value).getTransactionFault().getFaultMessage());
                            }
                        } else {
                            throw new InvalidDataException("Not a general response type");
                        }
                    } catch (InvalidDataException e) {
                        e.printStackTrace();
                        throw Exceptions.propagate(e);

                    }
                }
                return additionalCosts;
            }
        });
        return SessionObserverBuilder.build(observable);
    }

    public Observable<CostControlDataVolatile> getCostControlForOtherMsidns(List<String> msisdnList) {
        ArrayList<Observable<GeneralResponse<CostControl>>> observables = new ArrayList<>();
        for (String msisdn : msisdnList) {
            Observable<GeneralResponse<CostControl>> observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCostControl(msisdn);
            observables.add(observable);
        }
        Observable observable = Observable.combineLatest((Iterable<? extends Observable<?>>) observables, new FuncN<CostControlDataVolatile>() {

            @Override
            public CostControlDataVolatile call(Object... args) {
                HashMap<String, ArrayList<Object>> costControlDataClassNameValue = new HashMap<>();

                ArrayList<AdditionalCost> additionalCosts = new ArrayList<>();
                ArrayList<Extraoption> extraOptions = new ArrayList<>();
                for (Object value : args) {
                    try {
                        if (value instanceof GeneralResponse) {
                            if (((GeneralResponse) value).getTransactionSuccess() != null) {
                                if (((GeneralResponse) value).getTransactionSuccess() != null) {
                                    CostControl costControl = (CostControl) ((GeneralResponse) value).getTransactionSuccess();
                                    if (costControl.getAdditionalCost() != null) {
                                        additionalCosts.add(costControl.getAdditionalCost());
                                    }
                                    if (costControl.getCurrentExtraoptions() != null) {
                                        extraOptions.add((costControl).getCurrentExtraoptions());
                                    }
                                } else
                                    throw new InvalidDataException("costControl is null");
                            } else if (((GeneralResponse) value).getTransactionStatus() == 2 && ((GeneralResponse) value).getTransactionFault() != null) {
                                throw new InvalidDataException(((GeneralResponse) value).getTransactionFault().getFaultMessage());
                            }
                        } else {
                            throw new InvalidDataException("Not a general response type");
                        }
                    } catch (InvalidDataException e) {
                        e.printStackTrace();
                        throw Exceptions.propagate(e);

                    }
                }
                CostControlDataVolatile costControlDataVolatile = new CostControlDataVolatile(additionalCosts, extraOptions);

                //costControlDataClassNameValue.put(AdditionalCost.class.toString(),);
                //costControlDataClassNameValue.put(Extraoption.class.toString(),extraOptions);
                return costControlDataVolatile;
            }
        });
        return SessionObserverBuilder.build(observable);
    }

    //API 10
    public Observable<GeneralResponse<Profile>> getUserProfile(boolean refreshData) {
        Observable<GeneralResponse<Profile>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getUserProfile();

        return refreshData ? observable : new VerifyTimeToLeaveObserverBuilder().build(observable, Profile.class);
    }

    public Observable<GeneralResponse<ProfileSubscriptionSuccess>> getVdfSubscription(String vfPhoneNumber) {
        Observable<GeneralResponse<ProfileSubscriptionSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getVdfSubscription(vfPhoneNumber);

        return SessionObserverBuilder.build(observable);
    }

    //API 21 - /prepaid/balance/credit/
    public Observable<GeneralResponse<BalanceCreditSuccess>> getPrepaidBalanceCredit(boolean verifyTimeToLeave) {
        Observable<GeneralResponse<BalanceCreditSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPrepaidBalanceCredit();

        return verifyTimeToLeave ? new VerifyTimeToLeaveObserverBuilder().build(observable, BalanceCreditSuccess.class) : SessionObserverBuilder.build(observable);
    }

    //API 21 - /prepaid/balance/secondary/
    public Observable<GeneralResponse<BalanceSecondarySuccess>> getPrepaidBalanceSecondary() {
        Observable<GeneralResponse<BalanceSecondarySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPrepaidBalanceSecondary();

        return new VerifyTimeToLeaveObserverBuilder().build(observable, BalanceSecondarySuccess.class);
    }

    //API 22 - /offers/unica

    public Observable<GeneralResponse<UnicaOffersSuccess>> getOffersUnica(String msisdn, Integer offerLimit,
                                                                          String interactionPoint,
                                                                          String interactionSessionId,
                                                                          String webSessionId) {
        Observable<GeneralResponse<UnicaOffersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getOffersUnica(msisdn, offerLimit, interactionPoint, interactionSessionId, webSessionId);

        return SessionObserverBuilder.build(observable);
    }


    //API 22 PUT /offers/unica/{msisdn}
    public Observable<GeneralResponse<UnicaOffersSuccess>> putOffersUnica(String msisdn,
                                                                          String interactionPoint,
                                                                          String interactionSessionId,
                                                                          String webSessionId,
                                                                          UnicaOffer unicaOffer) {
        Observable<GeneralResponse<UnicaOffersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putOffersUnica(msisdn, interactionPoint, interactionSessionId, webSessionId, unicaOffer);

        return SessionObserverBuilder.build(observable);
    }


    //API 25 - /subscriber/calldetails
    public Observable<GeneralResponse<CallDetailsSuccess>> getCallDetails(CallDetailsRequest callDetailsRequest,
                                                                          String vfOdsCid,
                                                                          String vfCRMRole) {
        Observable<GeneralResponse<CallDetailsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCallDetails(callDetailsRequest,
                        vfOdsCid,
                        vfCRMRole);

        return SessionObserverBuilder.build(observable);
    }

    //API 26 - /bills/dates/{account}
    public Observable<GeneralResponse<BillsDatesSuccess>> getBillDates(String account,
                                                                       String vfCid,
                                                                       String vfOdsCid,
                                                                       int range,
                                                                       String vfCRMRole) {
        Observable<GeneralResponse<BillsDatesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillDates(account,
                        vfCid,
                        vfOdsCid,
                        range,
                        vfCRMRole);

        return SessionObserverBuilder.build(observable);
    }

    //API 27 - /subscriber/calldetails/report
    public Observable<GeneralResponse> sendCallDetailsReport(CallDetailsReportRequest callDetailsReportRequest,
                                                             String vfOdsCid) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.sendCallDetailsReport(callDetailsReportRequest,
                        vfOdsCid);

        return SessionObserverBuilder.build(observable);
    }

    //API 28 - /api-gateway/profile/recharges/favorite-numbers/ - (GET)
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> getFavoriteNumbers() {
        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getFavoriteNumbers();

        return SessionObserverBuilder.build(observable);
    }

    //API 28 - /api-gateway/profile/recharges/favorite-numbers/ - (PUT)
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> addFavoriteNumber(String msisdn, String nickname) {
        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addFavoriteNumber(msisdn, nickname);

        return SessionObserverBuilder.build(observable);
    }

    //API 28 - /api-gateway/profile/recharges/favorite-numbers/ - (DELETE)
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> deleteFavoriteNumber(String vfSsoUsername, String msisdn) {
        Observable<GeneralResponse<FavoriteNumbersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteFavoriteNumber(vfSsoUsername, msisdn);

        return SessionObserverBuilder.build(observable);
    }

    //API 29 - /api-gateway/subscriber/{msisdn}/validation?user_type=prepaid
    public Observable<GeneralResponse> validateSubscriber(String msisdn) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.validateSubscriber(msisdn);

        return SessionObserverBuilder.build(observable);
    }

    //API 30 - /profile/recharges/recommended-values/
    public Observable<GeneralResponse<RecommendedRechargesSuccess>> getRecommendedRechargesValues(Integer rechargeNumbers, Integer minAmount) {
        Observable<GeneralResponse<RecommendedRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getRecommendedRechargesValues(rechargeNumbers, minAmount);
        //TODO set correct sid

        return new VerifyTimeToLeaveObserverBuilder().build(observable, RecommendedRechargesSuccess.class);
    }

    //API 31 - /api-gateway/profile/bill/recharges/eligibility/

    public Observable<GeneralResponse<UserPost4preEligibilitySuccess>> checkMsisdnPost4PreEligibility(String vfPhoneNumber) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<UserPost4preEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.checkMsisdnPost4PreEligibility(vfPhoneNumber);

        return SessionObserverBuilder.build(observable);
    }

    //API 31 - /api-gateway/profile/bill/{ban}/recharges/eligibility/

    public Observable<GeneralResponse<BanPost4preEligibilitySuccess>> checkBanPost4PreEligibility(String ban) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<BanPost4preEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.checkBanPost4PreEligibility(ban);

        return SessionObserverBuilder.build(observable);
    }

    //API 31 - /api-gateway/profile/bill/recharges/recurring/counter
    public Observable<GeneralResponse<Post4preCountersSuccess>> getPost4PreCounters() {
        Log.d("", "Retrofit call: getPost4PreCounters");
        Observable<GeneralResponse<Post4preCountersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPost4PreCounters();
        return new VerifyTimeToLeaveObserverBuilder().build(observable, Post4preCountersSuccess.class);
    }


    //API 32 - /api-gateway/profile/bill/recharges/recurring/  (GET)
    public Observable<GeneralResponse<BillRechargesSuccess>> getBillRecharges() {
        Log.d("", "Retrofit call: getBillRecharges");
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillRecharges();

        return SessionObserverBuilder.build(observable);
    }


    public Observable<GeneralResponse<BillRechargesSuccess>> getBillRecharges(String ban) {
        Log.d("", "Retrofit call: getBillRecharges");
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillRecharges(ban);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, BillRechargesSuccess.class);
    }


    //API 32 - /api-gateway/profile/bill/{ban}/recharges/recurring/{prepaid_msisdn} (DELETE)

    public Observable<GeneralResponse<BillRechargesSuccess>> deleteBillRecharges(String ban, String msisdn, BillRechargeRequest billRechargeRequest) {
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteBillRecharges(ban, msisdn, billRechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<BillRechargesSuccess>> addBillBanRechargesImmediatelyPrepaid(String ban, String msisdn, BillRechargeRequest billRechargeRequest) {
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addBillBanRechargesImmediatelyPrepaid(ban, msisdn, billRechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 32 - /api-gateway/profile/bill/{ban}/recharges/recurring/{prepaid_msisdn} (PUT) weeklyrecharges
    public Observable<GeneralResponse<BillRechargesSuccess>> addWeeklyRecurringRecharge(String ban, String msisdn, BillRechargeRequest billRechargeRequest) {
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addWeeklyRecurringRecharge(ban, msisdn, billRechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 32 - /api-gateway/profile/bill/{ban}/recharges/recurring/{prepaid_msisdn} (PUT) monthlyrecharges
    public Observable<GeneralResponse<BillRechargesSuccess>> addMonthlyRecurringRecharge(String ban, String msisdn, BillRechargeRequest billRechargeRequest) {
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addMonthlyRecurringRecharge(ban, msisdn, billRechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 32 - /api-gateway/profile/bill/{ban}/recharges/recurring/{prepaid_msisdn} (PUT) monthlyrecharges
    public Observable<GeneralResponse<BillRechargesSuccess>> addDateRecurringRecharge(String ban, String msisdn, BillRechargeRequest billRechargeRequest) {
        Observable<GeneralResponse<BillRechargesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addDateRecurringRecharge(ban, msisdn, billRechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 33 - api-gateway/profile/bill/{ban}/recharges/history/?last_months_no=6/  (GET - PostPaid)
    public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPostPaid(String ban) {
        Log.d("", "Retrofit call: getHistoryRecharges for ban : " + ban);

        //workaround for scenarios where API-19 fails
        if (ban == null) {
            ban = "";
        }

        Observable<GeneralResponse<RechargeHistorySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getHistoryRechargesPostPaid(ban);
        return new VerifyTimeToLeaveObserverBuilder().build(observable, RechargeHistorySuccess.class);

    }

    //API 33 - api-gateway/profile/bill/{ban}/recharges/history/?last_months_no=6/  (GET - PrePaid)

    public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPrePaid() {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<RechargeHistorySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getHistoryRechargesPrePaid();
        return new VerifyTimeToLeaveObserverBuilder().build(observable, RechargeHistorySuccess.class);
    }


    //API 34 - /api-gateway/payment/recharge/voucher/ (POST)
    public Observable<GeneralResponse<RechargeVoucherSuccess>> postRechargeWithVoucher(RechargeVoucherRequest rechargeVoucherRequest) {
        Observable<GeneralResponse<RechargeVoucherSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postRechargeWithVoucher(rechargeVoucherRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 36 - /api-gateway/notifications/flag/ - (NOTIFICATION)
    public Observable<GeneralResponse<NotificationFlag>> getNotificationFlag() {
        Observable<GeneralResponse<NotificationFlag>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getNotificationFlag();

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<NotificationFlag>> setNotificationFlag(NotificationRequest notificationRequest) {
        Observable<GeneralResponse<NotificationFlag>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.setNotificationFlag(notificationRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API-42 VOS Offers for Ancom
    public Observable<GeneralResponse<AncomPendingOffersSuccess>> getAncomPendingOffers(String msisdn, String listType) {
        Observable<GeneralResponse<AncomPendingOffersSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getAncomPendingOffers(msisdn, listType);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, AncomPendingOffersSuccess.class);

    }

    public Observable<GeneralResponse<AncomAcceptedOffersSuccess>> getAncomAcceptedOffers(String msisdn, String listType) {
        Observable<GeneralResponse<AncomAcceptedOffersSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getAncomAcceptedOffers(msisdn, listType);

        return SessionObserverBuilder.build(observable);

    }

    public Observable<GeneralResponse<AncomPendingOffersSuccess>> setAncomOffers(String msisdn, UpdateOfferRequest updateOfferRequest) {
        Observable<GeneralResponse<AncomPendingOffersSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.setAncomOffers(msisdn, updateOfferRequest);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, AncomPendingOffersSuccess.class);
    }

    //API 43 - /api-gateway/reset/password/ - (RESET PASSWORD)
    public Observable<GeneralResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {

        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.resetPassword(resetPasswordRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 35 - /api-gateway/offers/prepaid/

    public Observable<GeneralResponse<ActiveOffersSuccess>> getEligibleActiveOffers4PrePaid(String sid) {
        Log.d("", "Retrofit call: getEligibleOffers4PrePaid");
        Observable<GeneralResponse<ActiveOffersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEligibleActiveOffers4PrePaid(sid);

        return SessionObserverBuilder.build(observable);
    }

    //API 37 - /api-gateway/offers/prepaid/

    public Observable<GeneralResponse<EligibleOffersSuccess>> getEligibleOffers4PrePaid() {
        Log.d("", "Retrofit call: getEligibleOffers4PrePaid");
        Observable<GeneralResponse<EligibleOffersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEligibleOffers4PrePaid();

        return SessionObserverBuilder.build(observable);
    }

    //API 38 - /api-gateway/offers/eligibility/{sid}
    public Observable<GeneralResponse> getPendingOffers4PostPaid(String sid, String type) {   //RestApiResponse
        Log.d("", "Retrofit call: getEligibleOffers4PostPaid");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPendingOffers4PostPaid(sid, type);

        return SessionObserverBuilder.build(observable);   //RestApiResponse
    }

    //API 38 - /api-gateway/offers/eligibility/{sid}
    public Observable<GeneralResponse> getPendingOffers4EBU(String vfCRMRole, String sid, String type) {   //RestApiResponse
        Log.d("", "Retrofit call: getEligibleOffers4PostPaid");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPendingOffers4EBU(vfCRMRole, sid, type);

        return SessionObserverBuilder.build(observable);   //RestApiResponse
    }

    //API 38 - /offers/eligibility/ebu/checketf/{sid}
    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEtf(String sid, String promotionId, String excludedPromos) {   //RestApiResponse
        Log.d("", "Retrofit call: getEligibleOffers4PostPaid");
        Observable<GeneralResponse<EbuOfferEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.checkEtf(sid, promotionId, excludedPromos);

        return SessionObserverBuilder.build(observable);   //RestApiResponse
    }

    //API 39 - /api-gateway/offers/postpaid/current/{msisdn}/{sid}
    public Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> getActiveOffersPostpaid(String msisdn, String sid, String type) {
        Log.d("", "Retrofit call: getActiveOffersPostpaid");
        Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> observable = null;

        if(type.toLowerCase().contains("fixed")) {
         observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getFixedActiveOffersPostpaid(msisdn, sid, type);
        } else {
           observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getActiveOffersPostpaid(msisdn, sid);
        }
        return SessionObserverBuilder.build(observable);
    }

    //API 39 - /api-gateway/offers/postpaid/current/{msisdn}/{sid}
    public Observable<GeneralResponse<ActiveOffersPostpaidEbuSuccess>> getActiveOffersEbu(String vfOdsSid) {
        Log.d("", "Retrofit call: getActiveOffersPostpaid");
        Observable<GeneralResponse<ActiveOffersPostpaidEbuSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getActiveOffersEbu(vfOdsSid);

        return SessionObserverBuilder.build(observable);
    }

    //API 40 - /api-gateway/offers/postpaid/{msisdn}/{sid}
    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4PostPaid(String vfPhoneNumber, String sid, int bcd) {
        Log.d("", "Retrofit call: getEligibleOffers4PostPaid");
        Observable<GeneralResponse<EligibleOffersPostSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEligibleOffers4PostPaid(vfPhoneNumber, sid, bcd);

        return SessionObserverBuilder.build(observable);
    }

    //API 40 - /api-gateway/offers/postpaid/ebu/{msisdn}/
    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4EBU(String vfCRMRole, String vfPhoneNumber, String type) {
        Log.d("", "Retrofit call: getEligibleOffers4PostPaid");
        Observable<GeneralResponse<EligibleOffersPostSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEligibleOffers4EBU(vfCRMRole, vfPhoneNumber, type);

        return SessionObserverBuilder.build(observable);
    }

    //API 41 GET - /api-gateway/offers/postpaid/current/{msisdn}/{sid}/{matrix_id}
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> validateOfferActivationPostpaid(String msisdn, String sid, String matrixId) {
        Log.d("", "Retrofit call: getActiveOffersPostpaid");
        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.validateOfferActivationPostpaid(msisdn, sid, matrixId);

        return SessionObserverBuilder.build(observable);
    }

    //CMS Banner Offers - /consumption/groups/public/documents/web_asset/mcare_banner JSON files
    public Observable<BannerOffersSuccess> getBannerOffers(UserProfile userProfile, EntityChildItem entityChildItem) {

        Log.d("getBannerOffers", "getBannerOffers for userRole : " + userProfile.getUserRoleString());

        Observable<BannerOffersSuccess> observable = null;

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBannerOffersPrepaidUser();
        } else if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
            observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBannerOffers(userProfile.getUserRoleString().toLowerCase());
        } else if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBannerOffers(entityChildItem.getCrmRole().toLowerCase());
        }

        if (observable == null) {
            observable =
                    ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBannerOffers("nonvdf");
        }

        return SessionObserverBuilder.build(observable);
    }

    public Observable<List<JsonList>> getChatFaqJsonList() {

        Observable<List<JsonList>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getChatFaqJsonList();

        return SessionObserverBuilder.build(observable);

    }

    public Observable<String> getLocationsList() {

        Observable<String> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getLocationsList();

        return observable;

    }

    public Observable<CountryByIp> getCountryListJson() {

        Observable<CountryByIp> observable =
                ServiceGenerator.createServiceCustomUrl(false, "http://ip-api.com").create(IRetrofitMethods.class).getCountryListJson();

        return observable;

    }

    //API 45 - /api-gateway/billing/ebill/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse<EBillSuccess>> getEBillBan(String ban, String vfSsoUserRole, String vfSsoUsername) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<EBillSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEBillBan(ban, vfSsoUserRole, vfSsoUsername);

        return SessionObserverBuilder.build(observable);
    }

    //API 45 - /api-gateway/billing/ebill/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse<EBillSuccess>> putEBillBan(EBillRequest eBillRequest, String ban, String vfSsoUserRole, String vfSsoUsername) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<EBillSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putEBillBan(eBillRequest, ban, vfSsoUserRole, vfSsoUsername);

        return SessionObserverBuilder.build(observable);
    }

    //API 45 - /api-gateway/billing/ebill/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse> patchEBillBan(EBillRequest eBillRequest, String ban, String vfSsoUserRole, String vfSsoUsername) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.patchEBillBan(eBillRequest, ban);

        return SessionObserverBuilder.build(observable);
    }

    //API 45 - /api-gateway/billing/ebill/ebu/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse<EBillSuccess>> getEBillEBUBan(String ban,
                                                                    String vfSsoUserRole,
                                                                    String vfCRMRole,
                                                                    String vfOdsCid,
                                                                    String vfOdsEntityType,
                                                                    String vfOdsEntityId,
                                                                    String vfContactID,
                                                                    String vfSsoLastName,
                                                                    String vfSsoFirstName) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<EBillSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT
                        .getEBillEBUBan(ban, vfSsoUserRole, vfCRMRole, vfOdsCid, vfOdsEntityType,
                                vfOdsEntityId, vfContactID, vfSsoLastName, vfSsoFirstName);

        return SessionObserverBuilder.build(observable);
    }

    //API 45 - /api-gateway/billing/ebill/ebu/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse<EBillSuccess>> putEBillEBUBan(EBillRequest eBillRequest,
                                                                    String ban,
                                                                    String vfSsoUserRole,
                                                                    String vfCRMRole,
                                                                    String vfSsoUsername,
                                                                    String vfOdsCid,
                                                                    String vfOdsEntityType,
                                                                    String vfOdsEntityId,
                                                                    String vfContactID,
                                                                    String vfSsoFirstName,
                                                                    String vfSsoLastName,
                                                                    String vfOdsSsn,
                                                                    String vfOdsBen) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse<EBillSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT
                        .putEBillEBUBan(eBillRequest, ban, vfSsoUserRole, vfCRMRole, vfSsoUsername,
                                vfOdsCid, vfOdsEntityType, vfOdsEntityId, vfContactID, vfSsoFirstName,
                                vfSsoLastName, vfOdsSsn, vfOdsBen);

        return SessionObserverBuilder.build(observable);
    }

    //API 45 - /api-gateway/billing/ebill/ebu/{ban} - (ELECTRONIC BILL)
    public Observable<GeneralResponse> patchEBillEBUBan(EBillRequest eBillRequest,
                                                        String ban,
                                                        String vfSsoUserRole,
                                                        String vfCRMRole,
                                                        String vfSsoUsername,
                                                        String vfOdsCid,
                                                        String vfOdsEntityType,
                                                        String vfOdsEntityId,
                                                        String vfContactID,
                                                        String vfSsoLastName,
                                                        String vfSsoFirstName) {
        Log.d("", "Retrofit call: getHistoryRecharges");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT
                        .patchEBillEBUBan(eBillRequest, ban, vfSsoUserRole, vfCRMRole,
                                vfSsoUsername, vfOdsCid, vfOdsEntityType, vfOdsEntityId,
                                vfContactID, vfSsoLastName, vfSsoFirstName);

        return SessionObserverBuilder.build(observable);
    }

    //API 37 PUT - /api-gateway/offers/eligibility/{sid}
    public Observable<GeneralResponse<EligibleOffersSuccess>> activateEligibleOffer(String offer_id, ActivatePrepaidOfferRequest activatePrepaidRequest) {
        Log.d("", "Retrofit call: activateEligibleOffer");
        Observable<GeneralResponse<EligibleOffersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.activateEligibleOffer(offer_id, activatePrepaidRequest);

        return SessionObserverBuilder.build(observable);
    }


    public Observable<GeneralResponse<GetPaymentInputsResponse>> doRechargeAndActivate(RechargeRequest rechargeRequest) {


        Observable<GeneralResponse<GetPaymentInputsResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.doRechargeAndActivate(rechargeRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API 41 GET - /api-gateway/offers/postpaid/available/{msisdn}/{sid}/{matrix_id}
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> getActivatePostPaidEligibleOffer(String msisd, String sid, String matrix_id) {
        Log.d("", "Retrofit call: getActivatePostPaidEligibleOffer");
        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getActivatePostPaidEligibleOffer(msisd, sid, matrix_id);

        return SessionObserverBuilder.build(observable);
    }

    //API 41 PUT - /api-gateway/offers/postpaid/available/{msisdn}/{sid}/{matrix_id}
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> putActivatePostPaidEligibleOffer(ActivationRequest activationRequest, String msisd, String sid, String matrix_id) {
        Log.d("", "Retrofit call: putActivatePostPaidEligibleOffer");
        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putActivatePostPaidEligibleOffer(activationRequest, msisd, sid, matrix_id);

        return SessionObserverBuilder.build(observable);
    }

    //API 41 DELETE  - /api-gateway/offers/postpaid/current/{msisdn}/{sid}/{offer_id}
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deletePostPaidOffer(String msisdn, String sid, Long offerId, Long instanceId, ActivationRequest activationRequest) {
        Log.d("", "Retrofit call: deletePostPaidOffer: ");
        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deletePostpaidActiveOffer(msisdn, sid, offerId, instanceId, activationRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deactivateOfferForEbu(String vfCid, String vfContactId, String vfCRMRole, String vfOdsBan, String vfOdsSid,
                                                                                           String vfOdsBen, String vfSelectedMSISDN, Long promoId, String promoName) {
        Log.d("", "Retrofit call: deactivateOfferForEbu: ");
        Observable<GeneralResponse<ActivationEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deactivateOfferForEbu(vfCid, vfContactId, vfCRMRole, vfOdsBan,
                        vfOdsSid, vfOdsBen, vfSelectedMSISDN, promoId, promoName);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEbuOfferDeleteEligibility(String vfCRMRole, String vfOdsSid, String contactId, Long promoId) {
        Log.d("", "Retrofit call: deletePostPaidOffer: ");
        Observable<GeneralResponse<EbuOfferEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.checkEbuOfferDeleteEligibility(vfCRMRole, vfOdsSid, contactId, promoId);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> activateOfferEbu(String msisdn, String sid, String promotionId, String crmRole, String offerName,
                                                        String vfOdsCid, String vfOdsBen, String vfOdsBan) {
        Log.d("", "Retrofit call: deletePostPaidOffer: ");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.activateOfferEbu(msisdn, sid, promotionId, crmRole, offerName, vfOdsCid, vfOdsBen, vfOdsBan);

        return SessionObserverBuilder.build(observable);
    }

    //API 46 shop login
    public Observable<GeneralResponse<ShopLoginSuccess>> postShopLogin(ShopLoginRequest shopLoginRequest) {
        Log.d("", "Retrofit call: postShopLogin");
        Observable<GeneralResponse<ShopLoginSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postShopLogin(shopLoginRequest);

        return SessionObserverBuilder.build(observable);

    }

    //API 47 - Shop Eligibility
    public Observable<GeneralResponse<ShopEligibilitySuccess>> getShopEligibility(String msisdn, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopEligibility");
        Observable<GeneralResponse<ShopEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopEligibility(msisdn, shopSessionToken);

        return SessionObserverBuilder.build(observable);

    }

    //API 48 - Shop Products
    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsRecommended(String pricePlanSkuId, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopProductsRecommended");
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopProductsRecommended(pricePlanSkuId, shopSessionToken);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, ShopProductsSuccess.class);

    }

    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsListings(String pricePlanSkuId, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopProductsListings");
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopProductsListings(pricePlanSkuId, shopSessionToken);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, ShopProductsSuccess.class);
    }

    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsDetails(String pricePlanSkuId, String phoneSkuId, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopProductsDetails");
        Observable<GeneralResponse<ShopProductsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopProductsDetails(pricePlanSkuId, phoneSkuId, shopSessionToken);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, ShopProductsSuccess.class);
    }

    //API 49 - Shop Price Plans
    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansRecommended(String phoneSkuId, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopPricePlansRecommended");
        Observable<GeneralResponse<ShopPricePlanSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopPricePlansRecommended(phoneSkuId, shopSessionToken);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, ShopPricePlanSuccess.class);
    }

    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansListings(String phoneSkuId, String shopSessionToken) {
        Log.d("", "Retrofit call: getShopPricePlansListings");
        Observable<GeneralResponse<ShopPricePlanSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getShopPricePlansListings(phoneSkuId, shopSessionToken);

        return new VerifyTimeToLeaveObserverBuilder().build(observable, ShopPricePlanSuccess.class);
    }

    //API 50 - Shop Cart
    public Observable<GeneralResponse<ShopCartSuccess>> putShopCart(ShopCartRequest shopCartRequest, String shopSessionToken) {
        Log.d("", "Retrofit call: putShopCart");
        Observable<GeneralResponse<ShopCartSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putShopCart(shopCartRequest, shopSessionToken);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ShopCartSuccess>> deleteShopCart(ShopCartRequest shopCartRequest, String shopSessionToken) {
        Log.d("", "Retrofit call: deleteShopCart");
        Observable<GeneralResponse<ShopCartSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteShopCart(shopCartRequest, shopSessionToken);

        return SessionObserverBuilder.build(observable);
    }


    public Observable<GeneralResponse<BillHistorySuccess>> getBillHistory(String ben, String vfCid, String vfOdsCid, String crmRole, String ban, int months) {

        Observable<GeneralResponse<BillHistorySuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillHistory(ben, vfCid, vfOdsCid, crmRole, ban, months);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<BillSummarySuccess>> getBillSummary(String vfCid, String vfodsCid, String ban, long bcd, boolean term) {

        Observable<GeneralResponse<BillSummarySuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillSummary(vfCid, vfodsCid, ban, bcd, term);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<BillSummaryDetailsSuccess>> getBillSummaryDetails(String vfCid, String vfodsCid, String ban, String sid, long bcd) {

        Observable<GeneralResponse<BillSummaryDetailsSuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getBillSummaryDetails(vfCid, vfodsCid, ban, sid, bcd);

        return SessionObserverBuilder.build(observable);
    }


    public Observable<GeneralResponse> postEmail(EmailRequest emailRequest, MultipartBody.Part file) {

        Observable<GeneralResponse> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postEmail(emailRequest, file);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> getChatEligibility(boolean isChatAO,
                                                          boolean isChatBlackList,
                                                          boolean isChatRole,
                                                          boolean user,
                                                          boolean time) {

        Observable<GeneralResponse> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getChatEligibility(isChatAO, isChatBlackList, isChatRole, user, time);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<Response<ChatStateResponse>> getChatState(boolean searchDB, boolean isNewMcare) {

        Observable<Response<ChatStateResponse>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getChatState(searchDB, isNewMcare);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> updateChatState(int state) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.updateChatState(state, "mCare");

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> sendTextMessage(String myCookie,
                                              String contactId,
                                              String sessionKey,
                                              VodafoneMessage message,
                                              boolean userFromEnroll,
                                              String lastReadTime,
                                              String channel) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.sendTextMessage(myCookie, contactId, sessionKey, message, userFromEnroll, lastReadTime, channel);

        return observable;
    }

    public Observable<Response<GeneralResponse<StartChatResponse>>> postStartChat(String msisdn, StartChatRequest startChatRequest) {

        Observable<Response<GeneralResponse<StartChatResponse>>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postStartChat(msisdn, startChatRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> postStartPoll(String contactId, String sessionKey, String userEmail, boolean userFromEnroll, String lastReadTime, String chanel) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.pollSession(contactId, sessionKey, userEmail, userFromEnroll, lastReadTime, chanel);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> pollMessages(String contactId,
                                           String sessionKey,
                                           boolean isTyping,
                                           boolean userFromEnroll,
                                           String lastReadTime,
                                           String channel) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.pollMessages(contactId, sessionKey, isTyping, userFromEnroll, lastReadTime, channel);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> logOutChatMyCookie(String myCookie,
                                                 String contactId,
                                                 String sessionKey,
                                                 String email,
                                                 String channel) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.logOutChat(myCookie, contactId, sessionKey, email, channel);

        return observable;
    }

    public Observable<String> logOutChatWithSurvey(String myCookie,
                                                   String contactId,
                                                   String sessionKey,
                                                   String email,
                                                   String channel,
                                                   boolean pSurveyEligible) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.logOutChatWithSurvey(myCookie, contactId, sessionKey, email, channel, pSurveyEligible);

        return observable;
    }

    public Observable<String> sendMediaMessage(String myCookie,
                                               String contactId,
                                               String sessionKey,
                                               MultipartBody.Part file) {

        Observable<String> observable;


        D.w("sendMediaMessage activity.WLPJSESSIONID = " + contactId);
        D.w("sendMediaMessage activity.WLPJSESSIONID = " + sessionKey);


        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.sendMediaMessage(myCookie, contactId, sessionKey, file, "descrierea fisierului");

        return SessionObserverBuilder.build(observable);
    }

    public Observable<String> switchToPC(String contactId,
                                         String sessionKey,
                                         boolean userFromEnroll,
                                         String userEmail,
                                         String message) {

        Observable<String> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.switchToPC(contactId, sessionKey, userFromEnroll, "mCare", userEmail, message);

        return SessionObserverBuilder.build(observable);
    }


    //api 58 sim details
    public Observable<GeneralResponse<SIMDetailsSuccess>> getSimDetails(String sid) {
        Log.d("", "Retrofit call: getSimDetails");
        Observable<GeneralResponse<SIMDetailsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSimDetails(sid);

        return SessionObserverBuilder.build(observable);
    }


    //api 59 sim status
    public Observable<GeneralResponse<SIMStatusSuccess>> getSimStatus(String sid) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse<SIMStatusSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getSimStatus(sid);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<SIMStatusEBUSuccess>> getEbuSimStatus(String sid) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse<SIMStatusEBUSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEbuSimStatus(sid);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putSimBlock(SIMChangeStatusRequest simChangeStatusRequest) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putSimBlock(simChangeStatusRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putEbuSimBlock(SIMChangeStatusRequestEBU simChangeStatusRequestEBU,
                                                      String vfCRMRole) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putEbuSimBlock(simChangeStatusRequestEBU,
                        vfCRMRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putSimUnblock(SIMChangeStatusRequest simChangeStatusRequest) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putSimUnblock(simChangeStatusRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putEbuSimUnblock(SIMChangeStatusRequestEBU simChangeStatusRequestEBU,
                                                        String vfCRMRole) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putEbuSimUnblock(simChangeStatusRequestEBU,
                        vfCRMRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<ResponseBody> downloadBill(String ban, long date, String detailed) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<ResponseBody> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.downloadBill(ban, date, detailed);

        return SessionObserverBuilder.build(observable);
    }

    //api 74 transfer credit
    public Observable<GeneralResponse> transferCredit(TransferCreditRequest transferCreditRequest, String vfSid, String vfPhoneNumber, String vfSsoUserRole) {
        Log.d("", "Retrofit call: transferCredit");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.transferCredit(transferCreditRequest, vfSid, vfPhoneNumber, vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<TransferCreditTerms> getTransferCreditTerms() {
        Log.d("", "Retrofit call: getTransferCreditTerms");
        Observable<TransferCreditTerms> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getTransferCreditTerms();

        return SessionObserverBuilder.build(observable);
    }

    //api 51 orders history
    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrdersHistory(String shopSessionToken, String vfSsoId, String vfSsoUserRole, String vfSsoUsername) {
        Log.d("", "Retrofit call: getOrdersHistory");
        Observable<GeneralResponse<ShopOrdersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getOrdersList(shopSessionToken, vfSsoId, vfSsoUserRole, vfSsoUsername);

        return SessionObserverBuilder.build(observable);
    }


    //api 57 payment agreement
    public Observable<GeneralResponse<PaymentAgreementSuccess>> getPaymentAgreement(String segment, String phone, String crmRole) {
        Log.d("", "Retrofit call: getPaymentAgreement");
        Observable<GeneralResponse<PaymentAgreementSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPaymentAgreement(segment, phone, crmRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<PaymentAgreementSuccess>> putPaymentAgreement(PaymentAgreementRequest paymentAgreementRequest) {
        Log.d("", "Retrofit call: getSimStatus");
        Observable<GeneralResponse<PaymentAgreementSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putPaymentAgreement(paymentAgreementRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<PaymentAgreementSuccess>> postPaymentAgreement(String ban) {
        Log.d("", "Retrofit call: postPaymentAgreement");
        Observable<GeneralResponse<PaymentAgreementSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postPaymentAgreement(ban);
        return SessionObserverBuilder.build(observable);
    }

    //API-60 GET
    public Observable<GeneralResponse<ShopLoyaltyProgramSuccess>> getLoyaltyProgram(String selectedBan, String shopSessionToken) {
        Log.d("", "getLoyaltyProgram: ");
        Observable<GeneralResponse<ShopLoyaltyProgramSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getLoyaltyProgram(selectedBan, shopSessionToken);
        return SessionObserverBuilder.build(observable);
    }

    //API-60 POST
    public Observable<GeneralResponse<OptInSuccess>> postEnrollLoyaltyProgram(String shopSessionToken, LoyaltyProgramRequest loyaltyProgramRequest) {
        Log.d("", "postEnrollLoyaltyProgram: ");
        Observable<GeneralResponse<OptInSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postEnrollLoyaltyProgram(shopSessionToken, loyaltyProgramRequest);
        return SessionObserverBuilder.build(observable);
    }

    //API-60 delete
    public Observable<GeneralResponse<OptOutSuccess>> deleteQuitLoyaltyProgram(String shopSessionToken, LoyaltyProgramRequest loyaltyProgramRequest) {
        Log.d("", "deleteQuitLoyaltyProgram: ");
        Observable<GeneralResponse<OptOutSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.quitLoyaltyProgram(shopSessionToken, loyaltyProgramRequest);
        return SessionObserverBuilder.build(observable);
    }

    //API - 61 get
    public Observable<GeneralResponse<ShopLoyaltyPointsSuccess>> getLoyaltyPoints(String ban, String dateTo, String dateFrom, String shopSessionToken) {
        Log.d("", "getLoyaltyPoints: ");
        Observable<GeneralResponse<ShopLoyaltyPointsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getLoyaltyPoints(ban, dateTo, dateFrom, shopSessionToken);

        return SessionObserverBuilder.build(observable);
    }

    //API 10
    public Observable<GeneralResponse<ProfileSuccess>> postProfileAvatar(String selectedMsisdn,
                                                                         String alias,
                                                                         String type,
                                                                         MultipartBody.Part file) {


        Observable<GeneralResponse<ProfileSuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postProfileAvatar(selectedMsisdn, alias, type, file, "");

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ProfileSuccess>> postProfileAlias(String selectedMsisdn,
                                                                        String alias,
                                                                        String type) {


        Observable<GeneralResponse<ProfileSuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postProfileAlias(selectedMsisdn, alias, type);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ProfileSuccess>> deleteAvatar(String selectedMsisdn,
                                                                    String type) {


        Observable<GeneralResponse<ProfileSuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteAvatar(selectedMsisdn, type);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ProfileSuccess>> putDefaultProfile(
            String selectedMsisdn
    ) {


        Observable<GeneralResponse<ProfileSuccess>> observable;
        observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putDefaultProfile(selectedMsisdn);

        return SessionObserverBuilder.build(observable);
    }

    //API 62 GET - /api-gateway/offers/postpaid/roaming/{sid}/
    public Observable<GeneralResponse<AccessTypeSuccess>> getAccessType(String sid) {
        Log.d("", "Retrofit call: getAccessType");
        Observable<GeneralResponse<AccessTypeSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getAccessType(sid);

        return SessionObserverBuilder.build(observable);
    }

    //API 62 PUT - /api-gateway/offers/postpaid/roaming/{sid}/
    public Observable<GeneralResponse<AccessTypeSuccess>> putAccessType(String msisdn, String sid, AccessTypeRequest accessTypeRequest) {
        Log.d("", "Retrofit call: putAccessType");
        Observable<GeneralResponse<AccessTypeSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putAccessType(msisdn, sid, accessTypeRequest);

        return SessionObserverBuilder.build(observable);
    }


    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrderHistoryDetails(String shopSessionToken, String vfSsoUserRole, String vfSsoUsername, String orderId) {
        Log.d("", "Retrofit call: getOrderHistoryDetails");
        Observable<GeneralResponse<ShopOrdersSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getOrderDetails(shopSessionToken, vfSsoUserRole, vfSsoUsername, orderId);

        return SessionObserverBuilder.build(observable);
    }

    // - /consumption/groups/public/documents/web_asset/mcare_roaming_tariffs_postpaid.json
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsPostpaid() {
        Log.d("", "Retrofit call: getRoamingTariffsPostpaid");
        Observable<GeneralResponse<RoamingTariffsSuccess>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.getRoamingTariffsPostpaid();

        return SessionObserverBuilder.build(observable);
    }

    // - /consumption/groups/public/documents/web_asset/mcare_roaming_tariffs_ebu.json
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsEbu() {
        Log.d("", "Retrofit call: getRoamingTariffsEbu");
        Observable<GeneralResponse<RoamingTariffsSuccess>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.getRoamingTariffsEbu();

        return SessionObserverBuilder.build(observable);
    }

    // - /consumption/groups/public/documents/web_asset/mcare_roaming_tariffs_postpaid.json
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsPrepaid() {
        Log.d("", "Retrofit call: getRoamingTariffsPrepaid");
       Observable<GeneralResponse<RoamingTariffsSuccess>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.getRoamingTariffsPrepaid();
        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<SystemStatusSuccess>> getSystemStatus() {
        Observable<GeneralResponse<SystemStatusSuccess>> observable = ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.getSystemStatus();

        return SessionObserverBuilder.build(observable);
    }

    //API xx @GET("/consumption/groups/public/documents/web_asset/mcare_roaming_hints.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHints() {
        Log.d("", "Retrofit call: getTravellingHints");
        Observable<GeneralResponse<TravallingHintSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getTravellingHints();

        return SessionObserverBuilder.build(observable);
    }

    //API xx @GET("/consumption/groups/public/documents/web_asset/mcare_roaming_hints.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHintsPrepaid() {
        Log.d("", "Retrofit call: getTravellingHintsPrepaid");
        Observable<GeneralResponse<TravallingHintSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getTravellingHintsPrepaid();

        return SessionObserverBuilder.build(observable);
    }

    //API 63 @GET("/api-gateway/profile/identities")
    public Observable<GeneralResponse<UserEntitiesSuccess>> getIdentities(String vfContactID, boolean vfEBUMigrated, String vfSsoUserRole, String vfMaCustomer) {
        Log.d("", "Retrofit call: getUserIdentity");
        Observable<GeneralResponse<UserEntitiesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIdentities(vfContactID, vfEBUMigrated, vfSsoUserRole, vfMaCustomer);

        return SessionObserverBuilder.build(observable);
    }

    //API 63 @PUT("/api-gateway/profile/identity/default-entity")
    public Observable<GeneralResponse<UserEntitiesSuccess>> putDefaultIdentity(String entityId) {
        Log.d("", "Retrofit call: getUserIdentity");
        Observable<GeneralResponse<UserEntitiesSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putDefaultIdentity(entityId);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<EntityDetailsSuccess>> getUserDetails(String vfodsCid) {

        Observable<GeneralResponse<EntityDetailsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getUserDetails(vfodsCid);

        return observable;
    }

    public Observable<FaqPOJO> getFAQJson() {

        Observable<FaqPOJO> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getFAQJson();

        return observable;
    }

    //API - 64 @GET ("/creditinadvance/checkeligibility")

    public Observable<GeneralResponse<CreditInAdvanceSuccess>> getCreditInAdvanceEligibility() {
        Log.d("", "Retrofit call: getCreditInAdvanceEligibility: ");

        Observable<GeneralResponse<CreditInAdvanceSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCreditInAdvanceEligibility();
        return SessionObserverBuilder.build(observable);
    }


    //API 66 User Requests @GET("/support/user/requests")
    public Observable<GeneralResponse<UserRequestsSuccess>> getUserRequests(boolean vfEBUMigrated,
                                                                            String vfSsoUserRole,
                                                                            String vfOdsCid,
                                                                            String vfOdsBan,
                                                                            String crmRole
//                                                                            boolean pending,
//                                                                            boolean accepted,
//                                                                            boolean rejected
    ) {
        Observable<GeneralResponse<UserRequestsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getUserRequests(/*vfEBUMigrated, vfSsoUserRole, */vfOdsCid, vfOdsBan, crmRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<UserRequestsSuccess>> postUserRequest(String action,
                                                                            String requestId,
                                                                            String requesterEmail,
                                                                            boolean vfEBUMigrated,
                                                                            String vfSsoUserRole,
                                                                            String vfOdsCid,
                                                                            String vfOdsBan,
                                                                            String crmRole,
                                                                            String requesterName) {
        Observable<GeneralResponse<UserRequestsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postUserRequest(action,
                        requestId,
                        requesterEmail,
                        vfEBUMigrated,
                        vfSsoUserRole,
                        vfOdsCid,
                        vfOdsBan,
                        crmRole,
                        requesterName);

        return SessionObserverBuilder.build(observable);
    }


    // API 67 (ICR)
    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOwn(String vfPhoneNumber,
                                                                                  String vfSsoUserRole,
                                                                                  String phone,
                                                                                  String calledPhone,
                                                                                  String selectedMsisdn,
                                                                                  String crmRole,
                                                                                  boolean vfEBUMigrated) {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIcrOwn(vfPhoneNumber,
                vfSsoUserRole,
                phone,
                calledPhone,
                selectedMsisdn,
                crmRole,
                vfEBUMigrated);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOther(String vfSsoUserRole,
                                                                                    String callerPhone,
                                                                                    String calledPhone,
                                                                                    String crmRole) {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIcrOther(vfSsoUserRole,
                callerPhone,
                calledPhone,
                crmRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcr(String crmRole,
                                                                               String vfSsoUserRole,
                                                                               String vfPhoneNumber,
                                                                               InternationalCallingRatesRequest body) {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIcr(crmRole,
                vfSsoUserRole,
                vfPhoneNumber,
                body);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrSms(String phone) {
        Observable<GeneralResponse<InternationalCallingRateSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIcrSms(phone);
        return SessionObserverBuilder.build(observable);
    }

    //API 68 Loyalty segment @GET("/api/shop/loyalty/segment/")
    public Observable<GeneralResponse<LoyaltySegmentSuccess>> getLoyaltySegment(String crmRole, boolean session) {

        Observable<GeneralResponse<LoyaltySegmentSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getLoyaltySegment(crmRole);

        return session ? SessionObserverBuilder.build(observable) : observable;
    }

    //API 68 Loyalty segment @POST("/api/shop/loyalty/segment/")
    public Observable<GeneralResponse<LoyaltySegmentSuccess>> postEbuLoyaltySegment(String crmRole,
                                                                                    LoyaltySegmentRequest loyaltySegmentRequest,
                                                                                    boolean session) {

        Observable<GeneralResponse<LoyaltySegmentSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postEbuLoyaltySegment(crmRole, loyaltySegmentRequest);

        return session ? SessionObserverBuilder.build(observable) : observable;
    }

    //API 69 Loyalty vouchers @GET("/api/shop/loyalty/voucher/")
    public Observable<GeneralResponse<LoyaltyVoucherSuccess>> getLoyaltyVoucherList(String loyaltySegment,
                                                                                    String treatmentSegment,
                                                                                    String crmRole) {
        Observable<GeneralResponse<LoyaltyVoucherSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.
                        getLoyaltyVoucherList(loyaltySegment, treatmentSegment, crmRole);

        return SessionObserverBuilder.build(observable);
    }


    //API 69 Loyalty reserved vouchers @GET("/api/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReservedSuccess>> getReservedLoyaltyVoucherList() {

        Observable<GeneralResponse<LoyaltyVoucherReservedSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getReservedLoyaltyVoucherList();
        return SessionObserverBuilder.build(observable);
    }

    //API 69 Loyalty reserved vouchers @POST("/api/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReserveSuccess>> postReservedVoucher(ReserveVoucherRequest reserveVoucherRequest) {

        Observable<GeneralResponse<LoyaltyVoucherReserveSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postReservedVoucher(reserveVoucherRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<EntityChildItem>> getInverseHierarchy(String entityId, String entityType, @Nullable String vfOdsPhoneNumber) {

        Observable<GeneralResponse<EntityChildItem>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getInverseHierarchy(entityId, vfOdsPhoneNumber, entityType);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<CustomerRestrictionsSuccess>> getIdentityCostumerRestriction(String vfodsCid) {

        Observable<GeneralResponse<CustomerRestrictionsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getIdentityCostumerRestriction(vfodsCid);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<BenSuccessEbu>> getEbuBen(String vfOdsBan) {
        Observable<GeneralResponse<BenSuccessEbu>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEbuBen(vfOdsBan);

        return SessionObserverBuilder.build(observable);
    }


    //API-64  @GET("/api-gateway//creditinadvance/performcredit/") - perform credit in advance

    public Observable<GeneralResponse<CreditInAdvanceSuccess>> performCreditInAdvance(EligibilityInfo eligibilityInfo) {
        Log.d("", "Retrofit call: performCreditInAdvance: ");
        Log.d("", "performCreditInAdvance: eligibility info " + eligibilityInfo);
        Realm realm = RealmManager.getDefaultInstance();

        Observable<GeneralResponse<CreditInAdvanceSuccess>> observable = ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT
                .performCreditInAdvance(realm.copyFromRealm(eligibilityInfo));
        return SessionObserverBuilder.build(observable);

    }

    //API 65 - GET:/billing/payment/confirmation
    public Observable<GeneralResponse<PaymentConfirmationSuccess>> getPaymentConfirmation(String segment, String phone, String crmRole) {
        Log.d("", "Retrofit call: getPaymentConfirmation");
        Observable<GeneralResponse<PaymentConfirmationSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPaymentConfirmation(segment, phone, crmRole);
        return SessionObserverBuilder.build(observable);
    }

    // API - 65 - POST:/billing/payment/confirmation
    public Observable<GeneralResponse<PaymentConfirmationSuccess>> postPaymentConfirmation(PaymentConfirmationRequest paymentConfirmationRequest, String vfOdsCid, String vfOdsSid) {
        Log.d("", "Retrofit call: getPaymentConfirmation");
        Observable<GeneralResponse<PaymentConfirmationSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.postPaymentConfirmation(paymentConfirmationRequest, vfOdsCid, vfOdsSid);
        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<RecoverUsernameResponse>> recoverUsername(RecoverUsernameRequest recoverUsernameRequest) {

        Observable<GeneralResponse<RecoverUsernameResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.recoverUsername(recoverUsernameRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<VoiceOfVodafoneResponse> retrieveVoVs() {

        Observable<VoiceOfVodafoneResponse> observable =
                ServiceGenerator.RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT.getVovS();
        return observable;
    }

    public Observable<GeneralResponse<SIMDetailsSuccess>> getEbuSimDetails(String subscriberMsisdn, String vfOdsSid) {
        Observable<GeneralResponse<SIMDetailsSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getEbuSimDetails(subscriberMsisdn, vfOdsSid);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<GdprGetResponse>> getPermissions(String vfPhoneNumber, String vfSid, boolean refreshData, int key) {
        Observable<GeneralResponse<GdprGetResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getPermissions(vfPhoneNumber, vfSid);

        Observable observableTimeToLeave = new VerifyTimeToLeaveObserverBuilder().build(observable, GdprGetResponse.class, key);
//
//        if (refreshData || observableTimeToLeave == null) {
//            return SessionObserverBuilder.build(observable);
//        }
//
//        return observableTimeToLeave;
        return refreshData ? SessionObserverBuilder.build(observable) : observableTimeToLeave;


    }

    public Observable<GeneralResponse> setPermissions(GdprPermissions gdprPermissions, String vfSsoUsername, String customerId, String requestType, String phone) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.setPermissions(gdprPermissions, vfSsoUsername, customerId, requestType, phone);

        return SessionObserverBuilder.build(observable);
    }


    //API xx @GET("/consumption/groups/public/documents/web_asset/mcare_roaming_hints.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHintsEBU() {
        Log.d("", "Retrofit call: getTravellingHints");
        Observable<GeneralResponse<TravallingHintSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getTravellingHintsEbu();

        return SessionObserverBuilder.build(observable);
    }

    //API 62 GET - /api-gateway/offers/postpaid/ebu/roaming/{sid}/
    public Observable<GeneralResponse<AccessTypeEBU>> getAccessTypeEbu(String sid) {
        Log.d("", "Retrofit call: getAccessTypeEBU");
        Observable<GeneralResponse<AccessTypeEBU>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getAccessTypeEbu(sid);

        return SessionObserverBuilder.build(observable);
    }

    //API 62 @PUT("/api-gateway/offers/postpaid/roaming/ebu")
    public Observable<GeneralResponse> putAccessTypeEbu(String vfCRMRole, AccessTypeEbuRequest accessTypeEbuRequest) {
        Log.d("", "Retrofit call: putAccessTypeEbu");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putAccessTypeEbu(vfCRMRole, accessTypeEbuRequest);

        return SessionObserverBuilder.build(observable);
    }

    //@GET("/api-gateway/offers/roaming/ebu/eligibility")
    public Observable<GeneralResponse> checkEligibilityEbu(String vfOdsCid, String vfOdsSid) {
        Log.d("", "Retrofit call: checkEligibilityEbu");
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.checkEligibilityEbu(vfOdsCid, vfOdsSid);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPrepaid(String vfSid, String vfSsoUserRole) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getUnlimitedIonPrepaid(vfSid, vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPostpaid(String vfSsoUserRole, String selectedMsisdn) {
        Observable<GeneralResponse<IonEligibilitySuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getUnlimitedIonPostpaid(vfSsoUserRole, selectedMsisdn);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putUnlimitedIonPrepaid(String vfSid, String vfCid, String vfSsoUsername,
                                                              String vfPhoneNumber, String vfSsoEmail, String vfSsoUserRole,
                                                              IONPrepaidRequest ionPrepaidRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putUnlimitedIonPrepaid(ionPrepaidRequest, vfSid, vfCid, vfSsoUsername,
                        vfPhoneNumber, vfSsoEmail, vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> putUnlimitedIonPostpaid(String vfCid, String vfSsoUsername, String vfSsoEmail,
                                                               String vfSsoUserRole, IONPostpaidRequest ionPostpaidRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.putUnlimitedIonPostpaid(ionPostpaidRequest, vfCid, vfSsoUsername, vfSsoEmail,
                        vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> deleteUnlimitedIonPrepaid(String vfSid, String vfCid, String vfSsoUsername, String vfPhoneNumber,
                                                                 String vfSsoEmail, String vfSsoUserRole, IONPrepaidRequest ionPrepaidRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteUnlimitedIonPrepaid(ionPrepaidRequest, vfSid, vfCid, vfSsoUsername,
                        vfPhoneNumber, vfSsoEmail, vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> deleteUnlimitedIonPostpaid(String vfCid, String vfSsoUsername, String vfSsoEmail,
                                                                  String vfSsoUserRole, IONPostpaidRequest ionPostpaidRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteUnlimitedIonPostpaid(ionPostpaidRequest, vfCid,
                        vfSsoUsername, vfSsoEmail, vfSsoUserRole);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<GetByOperatorSuccess>> getDevicesByOperator(String vfSsoUserRole, String msisdn) {
        Observable<GeneralResponse<GetByOperatorSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getDeviceByOperator(vfSsoUserRole, msisdn);
        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> renameActiveDevice(String vfSsoUserRole, RenameRequest renameRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.renameActiveDevice(vfSsoUserRole, renameRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse> deleteActiveDevice(String vfSsoUserRole, DeleteRequest deleteRequest) {
        Observable<GeneralResponse> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteActiveDevice(vfSsoUserRole, deleteRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<ResetPinSuccess>> resetPIN(String vfSsoUserRole, ResetPinRequest resetPinRequest) {
        Observable<GeneralResponse<ResetPinSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.resetPIN(vfSsoUserRole, resetPinRequest);

        return SessionObserverBuilder.build(observable);
    }

    public Observable<GeneralResponse<SIMReplaceSuccess>> postReplaceSim(String selectedMsisdn, String vfEBUMigrated,
                                                                         String vfContactID, String vfSid,
                                                                         String vfCid, SimReplaceBean simReplaceBean) {

        Observable<GeneralResponse<SIMReplaceSuccess>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT
                        .postReplaceSim(selectedMsisdn, vfEBUMigrated, vfContactID, vfSid, vfCid, simReplaceBean);

        return SessionObserverBuilder.build(observable);
    }

    //API-35
    @POST("/api-gateway/offers/prepaid/roaming/label")
    public Observable<GeneralResponse<RoamingLabelResponse>> getRoamingLabels(RoamingLabelRequest roamingLabelRequest) {

        Observable<GeneralResponse<RoamingLabelResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getRoamingLabels(roamingLabelRequest);

        return SessionObserverBuilder.build(observable);
    }

    //API-100
    public Observable<GeneralResponse<TvHierarchyResponse>> getTvHierarchy(String vfSsoUsername, String vfPhoneNumber, String vfSsoUserRole,
                                                                           boolean vfEBUMigrated,String vfFixedBan,String vfFixedCid, String banList){
        Observable<GeneralResponse<TvHierarchyResponse>> observable =
                ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getTvHierarchy(vfSsoUsername,vfPhoneNumber,vfSsoUserRole,
                        vfEBUMigrated,vfFixedBan, vfFixedCid,banList);

        return observable;
    }

    //API-101
    public Observable<GeneralResponse<CardsResponse>> getCards(String vfSsoUsername) {

        return ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.getCards(vfSsoUsername);
//        return ServiceGenerator.createServiceCustomUrl(false, "https://vodafonero.getsandbox.com").create(IRetrofitMethods.class).getCards(vfSsoUsername);
    }

    public Observable<GeneralResponse<AddCardResponse>> addCard(String vfSsoUsername, String vfPhoneNumber, String vfSsoEmail) {
        return ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.addCard(vfSsoUsername, vfPhoneNumber, vfSsoEmail);
//        return ServiceGenerator.createServiceCustomUrl(false, "https://vodafonero.getsandbox.com").create(IRetrofitMethods.class).addCard(vfSsoUsername, vfPhoneNumber, vfSsoEmail);
    }

    public Observable<GeneralResponse<DeleteCardResponse>> deleteCard(String token) {
        return ServiceGenerator.RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT.deleteCard(token);
    }
}

