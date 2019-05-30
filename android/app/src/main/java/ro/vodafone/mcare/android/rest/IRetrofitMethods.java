package ro.vodafone.mcare.android.rest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
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
import ro.vodafone.mcare.android.client.model.vodafoneTv.DeleteRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.RenameRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinRequest;
import ro.vodafone.mcare.android.client.model.vodafoneTv.ResetPinSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
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
import ro.vodafone.mcare.android.rest.requests.RegisterAccountRequest;
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
import ro.vodafone.mcare.android.ui.activities.support.ChatStateResponse;
import ro.vodafone.mcare.android.ui.activities.support.JsonList;
import ro.vodafone.mcare.android.ui.activities.support.StartChatRequest;
import ro.vodafone.mcare.android.ui.activities.support.StartChatResponse;
import ro.vodafone.mcare.android.ui.activities.support.VodafoneMessage;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqPOJO;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessFlag;
import rx.Observable;

/**
 * Created by Victor Radulescu on 9/23/2016.
 */

public interface IRetrofitMethods {

    @POST("/api-gateway/authentication/login/")
    public Observable<Response<GeneralResponse<UserProfile>>> retrieveUserProfile(@Body LoginRequest loginRequest);

    @POST("/api-gateway/authentication/logout/")
    public Observable<GeneralResponse> logout( @Body LogoutRequest logoutRequest);

    //API 3 SEAMLESS Login - 3 step logic
    @POST("/api-gateway/authentication/seamless/initialtoken/")
    public Observable<GeneralResponse<InitialToken>> retrieveInitialToken(@Body BaseRequest baseRequest);

    @POST("/api-gateway/seamless-header/") //called for Header Enrichment logic
    public Observable<GeneralResponse> seamlessAddHeader(@Body SeamlessAddHeaderRequest seamlessAddHeaderRequest);

    @POST("/api-gateway/authentication/seamless/addheader/")
    public Observable<GeneralResponse> seamlessAddHeaderPreviousMsisdn(@Body SeamlessAddHeaderRequest seamlessAddHeaderRequest, @Header("seamlessMsisdn") String seamlessMsisdn);

    //API 4
    @POST("/api-gateway/authentication/seamless/login/")
    public Observable<Response<GeneralResponse<UserProfile>>> retrieveSeamlessUserProfile(@Body SeamlessLoginRequest seamlessLoginRequest);

    @POST("/api-gateway/system/seamless/event/")
    public Observable<GeneralResponse> sendSeamlessEvent(@Body EventReportRequest eventReportRequest);

    //Api9
    @PUT("/api-gateway/system/seamless/flag/")
    public Observable<GeneralResponse> setSeamlessFlag(@Body SeamlessFlagRequest seamlessFlagRequest);

    //Api9
    @GET("/api-gateway/system/seamless/flag/")
    public Observable<GeneralResponse<SeamlessFlag>> getSeamlessFlag();

    //SYSTEM APIs
    @GET("/api-gateway/system/labels/")
    public Observable<GeneralResponse<Labels>> retrieveLabels(@Query("last_update") String last_update);

    @GET("/api-gateway/system/configurations/")
    public Observable<GeneralResponse<Configurations>> retrieveConfigurations(@Query("last_update") String last_update);

    @POST("/api-gateway/identity/register/account/")
    public Observable<GeneralResponse> registerAccount(@Body SelfRegisterCreateRequest selfRegisterCreateRequest);

    @POST("/api-gateway/identity/register/account/activation/")
    public Observable<GeneralResponse> activateUserAccount(@Body SelfRegisterActivateRequest selfRegisterActivateRequest);

    @POST("/api-gateway/identity/register/account/resend/")
    public Observable<GeneralResponse> resendActivationCode(@Body SelfRegisterResendRequest selfRegisterResendRequest);

//    @POST("/api-gateway/identity/username/recover/")
//    public Observable<GeneralResponse> recoverUsername(@Body RecoverUsernameRequest recoverUsernameRequest);

//    @POST("/api-gateway/identity/password/recover/")
//    public Observable<GeneralResponse<RecoverPasswordTransactionSuccess>> recoverPassword(@Body RecoverPasswordRequest recoverPasswordRequest);

    @POST("/api-gateway/identity/register/account/check/")
    public Observable<GeneralResponse<AccountCheck>> registerAccountCheck(@Body PhoneCheckRequest accountCheckRequest);

    @POST("/api-gateway/identity/profile/confirm/")
    public Observable<GeneralResponse> confirmProfile(@Body ConfirmProfileRequest confirmProfileRequest);

    @POST("/api-gateway/identity/profile/complete/")
    public Observable<GeneralResponse> completeProfile(@Body CompleteProfileRequest completeProfileRequest);

    @POST("/api-gateway/identity/register/account/")
    public Observable<GeneralResponse> registerAccount(@Body RegisterAccountRequest registerAccountRequest);

    @POST("/api-gateway/identity/register/account/activation/")
    public Observable<GeneralResponse> activateAccount(@Body SelfRegisterActivateRequest selfRegisterActivateRequest);

    @POST("/api-gateway/identity/register/account/resend/")
    public Observable<GeneralResponse> resendCode(@Body ResendCodeRequest resendCodeRequest);

    //API 10
    @GET("/api-gateway/profile/")
    public Observable<GeneralResponse<Profile>> getUserProfile();

    @GET("/api-gateway/profile/subscription/{msisdn}")
    public Observable<GeneralResponse<ProfileSubscriptionSuccess>> getVdfSubscription(@Path(value = "msisdn", encoded = true) String msisdn);


    //API 19
    @GET("/api-gateway/profile/postpaid/hierarchy/")
    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy();

    @GET("/api-gateway/profile/postpaid/hierarchy/")
    public Observable<GeneralResponse<UserProfileHierarchy>> getSubscriberHierarchy(@Header("vfOdsBan") String vfOdsBan);

    @GET("/api-gateway/billing/invoice/")
    public Observable<GeneralResponse<InvoiceDetailsSuccess>> getInvoiceDetails(@Query("phone") String msisdn, @Query("ban") String ban);

    @POST("/api-gateway/payment/bill/")
    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentBill(@Body PaymentRequest paymentRequest);

    @POST("/api-gateway/payment/recharge/")
    public Observable<GeneralResponse<GetPaymentInputsResponse>> doPaymentRecharge(@Body PaymentRequest paymentRequest);

    @POST("/api-gateway/payment/recharge/")  //&activate
    public Observable<GeneralResponse<GetPaymentInputsResponse>> doRechargeAndActivate(@Body RechargeRequest rechargeRequest); //api 24

    //API 20
    @GET("/api-gateway/subscriber/cost-control/")
    public Observable<GeneralResponse<CostControl>> getCostControl(@Query("msisdn") String msisdn);

    @GET("/api-gateway/subscriber/prepaid/balance/credit/")
    public Observable<GeneralResponse<BalanceCreditSuccess>> getPrepaidBalanceCredit();

    @GET("/api-gateway/subscriber/prepaid/balance/secondary/")
    public Observable<GeneralResponse<BalanceSecondarySuccess>> getPrepaidBalanceSecondary();

    @GET("/api-gateway/offers/unica/{msisdn}/")
    public Observable<GeneralResponse<UnicaOffersSuccess>> getOffersUnica(
            @Path(value = "msisdn", encoded = true) String msisdn,
            @Query("offerLimit") Integer offerLimit,
            @Query("interactionPoint") String interactionPoint,
            @Query("interactionSessionId") String interactionSessionId,
            @Query("webSessionId") String webSessionId);

    @PUT("/api-gateway/offers/unica/{msisdn}/")
    public Observable<GeneralResponse<UnicaOffersSuccess>> putOffersUnica(
            @Path(value = "msisdn", encoded = true) String msisdn,
            @Query("interactionPoint") String interactionPoint,
            @Query("interactSessionId") String interactionSessionId,
            @Query("webSessionId") String webSessionId,
            @Body UnicaOffer unicaOffer);

    @GET("/api-gateway/offers/unica/")
    public Observable<GeneralResponse<UnicaOffersSuccess>> getOffersUnica(@Query("msisdn") String msisdn);

    //API 25
    @POST("/api-gateway/subscriber/calldetails/")
    public Observable<GeneralResponse<CallDetailsSuccess>> getCallDetails(@Body CallDetailsRequest callDetailsRequest,
                                                                          @Header("vfOdsCid") String vfOdsCid,
                                                                          @Header("vfCRMRole") String vfCRMRole);

    //API 26
    @GET("/api-gateway/billing/dates/{account}")
    public Observable<GeneralResponse<BillsDatesSuccess>> getBillDates(@Path(value = "account", encoded = true) String account,
                                                                       @Header("vfCid") String vfCid,
                                                                       @Header("vfOdsCid") String vfOdsCid,
                                                                       @Query("range") int range,
                                                                       @Header("vfCRMRole") String vfCRMRole);

    //API 27
    @POST("/api-gateway/subscriber/calldetails/report/")
    public Observable<GeneralResponse> sendCallDetailsReport(@Body CallDetailsReportRequest callDetailsReportRequest,
                                                             @Header("vfOdsCid") String vfOdsCid);

    @GET("/api-gateway/profile/recharges/favorite-numbers/")
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> getFavoriteNumbers();

    @PUT("/api-gateway/profile/recharges/favorite-number/{msisdn}/{nickname}/")
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> addFavoriteNumber(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "nickname", encoded = true) String nickname);

    @HTTP(method = "DELETE", path = "/api-gateway/profile/recharges/favorite-number/{msisdn}/")
    public Observable<GeneralResponse<FavoriteNumbersSuccess>> deleteFavoriteNumber(@Header("vfSsoUsername") String vfSsoUsername, @Path(value = "msisdn", encoded = true) String msisdn);

    @GET("/api-gateway/subscriber/{msisdn}/validation?user_type=prepaid")
    public Observable<GeneralResponse> validateSubscriber(@Path(value = "msisdn", encoded = true) String msisdn);

    @GET("/api-gateway/recharges/recommended-values/")
    public Observable<GeneralResponse<RecommendedRechargesSuccess>> getRecommendedRechargesValues(@Query("recharge_numbers") Integer rechargeNumbers, @Query("min_amount") Integer minAmount);

    @GET("/api-gateway/notifications/flag/")
    public Observable<GeneralResponse<NotificationFlag>> getNotificationFlag();

    @POST("/api-gateway/notifications/flag/")
    public Observable<GeneralResponse<NotificationFlag>> setNotificationFlag(@Body NotificationRequest notificationRequest);

    @PUT("api-gateway/recharges/{ban}/immediately/{prepaid_msisdn}/")
    public Observable<GeneralResponse<BillRechargesSuccess>> addBillBanRechargesImmediatelyPrepaid(@Path(value = "ban", encoded = true) String ban,
                                                                                                   @Path(value = "prepaid_msisdn", encoded = true) String prepaid_msisdn,
                                                                                                   @Body BillRechargeRequest billRechargeRequest);

    @GET("/api-gateway/recharges/recurring/")
    public Observable<GeneralResponse<BillRechargesSuccess>> getBillRecharges();

    //http://portal-pet-ot.vodafone.ro/api-gateway/profile/bill/235368857/recharges/recurring/
    @GET("/api-gateway/recharges/{ban}/recurring/")
    public Observable<GeneralResponse<BillRechargesSuccess>> getBillRecharges(@Path(value = "ban", encoded = true) String ban);

    @HTTP(method = "DELETE", path = "/api-gateway/recharges/{ban}/recurring/{prepaid_msisdn}/", hasBody = true)
    public Observable<GeneralResponse<BillRechargesSuccess>> deleteBillRecharges(@Path(value = "ban", encoded = true) String ban,
                                                                                 @Path(value = "prepaid_msisdn", encoded = true) String prepaid_msisdn,
                                                                                 @Body BillRechargeRequest billRechargeRequest);

    @PUT("api-gateway/recharges/{ban}/recurring/{prepaid_msisdn}/")

    public Observable<GeneralResponse<BillRechargesSuccess>> addWeeklyRecurringRecharge(@Path(value = "ban", encoded = true) String ban,
                                                                                        @Path(value = "prepaid_msisdn", encoded = true) String prepaid_msisdn,
                                                                                        @Body BillRechargeRequest billRechargeRequest);

    @PUT("api-gateway/recharges/{ban}/recurring/{prepaid_msisdn}/")
    public Observable<GeneralResponse<BillRechargesSuccess>> addMonthlyRecurringRecharge(@Path(value = "ban", encoded = true) String ban,
                                                                                         @Path(value = "prepaid_msisdn", encoded = true) String prepaid_msisdn,
                                                                                         @Body BillRechargeRequest billRechargeRequest);

    @PUT("api-gateway/recharges/{ban}/recurring/{prepaid_msisdn}/")
    public Observable<GeneralResponse<BillRechargesSuccess>> addDateRecurringRecharge(@Path(value = "ban", encoded = true) String ban,
                                                                                      @Path(value = "prepaid_msisdn", encoded = true) String prepaid_msisdn,
                                                                                      @Body BillRechargeRequest billRechargeRequest);

    @POST("/api-gateway/payment/recharge/voucher/")
    public Observable<GeneralResponse<RechargeVoucherSuccess>> postRechargeWithVoucher(@Body RechargeVoucherRequest rechargeVoucherRequest);

    @GET("api-gateway/recharges/{ban}/history/?last_months_no=6")
    public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPostPaid(@Path(value = "ban", encoded = true) String ban);

    @GET("api-gateway/recharges/history/?last_months_no=6")
    public Observable<GeneralResponse<RechargeHistorySuccess>> getHistoryRechargesPrePaid();

    //API-74 Transfer credit
    @POST("/api-gateway/recharges/transfer/credit")
    public Observable<GeneralResponse> transferCredit(@Body TransferCreditRequest transferCreditRequest,
                                                      @Header("vfSid") String vfSid,
                                                      @Header("vfPhoneNumber") String vfPhoneNumber,
                                                      @Header("vfSsoUserRole") String vfSsoUserRole);

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/transfer_credit_terms.json")
    public Observable<TransferCreditTerms> getTransferCreditTerms();

    //API-42 VOS Offers for Ancom
    @GET("/api-gateway/offers/vos/{msisdn}/")
    public Observable<GeneralResponse<AncomPendingOffersSuccess>> getAncomPendingOffers(@Path(value = "msisdn", encoded = true) String msisdn, @Query("list_type") String list_type);

    @GET("/api-gateway/offers/vos/{msisdn}/")
    public Observable<GeneralResponse<AncomAcceptedOffersSuccess>> getAncomAcceptedOffers(@Path(value = "msisdn", encoded = true) String msisdn, @Query("list_type") String list_type);

    @PUT("/api-gateway/offers/vos/{msisdn}/")
    public Observable<GeneralResponse<AncomPendingOffersSuccess>> setAncomOffers(@Path(value = "msisdn", encoded = true) String msisdn, @Body UpdateOfferRequest updateOfferRequest);

    @GET("api-gateway/recharges/eligibility/")
    public Observable<GeneralResponse<UserPost4preEligibilitySuccess>> checkMsisdnPost4PreEligibility(@Header("vfPhoneNumber") String vfPhoneNumber);

    @POST("/api-gateway/identity/password/reset/")
    public Observable<GeneralResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @GET("/api-gateway/offers/prepaid/current/")
    public Observable<GeneralResponse<ActiveOffersSuccess>> getEligibleActiveOffers4PrePaid(@Header("sid") String sid);

    @GET("/api-gateway/offers/prepaid/available/")
    public Observable<GeneralResponse<EligibleOffersSuccess>> getEligibleOffers4PrePaid();

    @GET("/api-gateway/offers/postpaid/available/{msisdn}/{sid}/")
    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4PostPaid(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Query("bcd") int bcd);   //***********

    //API 40
    @GET("/api-gateway/offers/postpaid/ebu/{msisdn}/")
    public Observable<GeneralResponse<EligibleOffersPostSuccess>> getEligibleOffers4EBU(@Header("vfCRMRole") String vfCRMRole, @Path(value = "msisdn", encoded = true) String msisdn,
                                                                                        @Query(value = "flow") String type);

    //API 38
    @GET("/api-gateway/offers/eligibility/{sid}/")
    public Observable<GeneralResponse> getPendingOffers4PostPaid(@Path(value = "sid", encoded = true) String sid, @Query("type") String type);   //RestApiResponse    ?bcd=5

    @GET("/api-gateway/offers/eligibility/ebu/{sid}/")
    public Observable<GeneralResponse> getPendingOffers4EBU(@Header("vfCRMRole") String vfCRMRole, @Path(value = "sid", encoded = true) String sid, @Query("type") String type);

    @GET("/api-gateway/offers/eligibility/ebu/checketf/{sid}")
    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEtf(@Path(value = "sid", encoded = true) String sid,
                                                                            @Header("promotionId") String promotionId,
                                                                            @Header("excludedPromos") String excludedPromos);

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_banner_prepaiduser.json")
    public Observable<BannerOffersSuccess> getBannerOffersPrepaidUser();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_banner_{role}.json")
    public Observable<BannerOffersSuccess> getBannerOffers(@Path(value = "role", encoded = true) String role);

    @GET("/api-gateway/recharges/{ban}/eligibility/")
    public Observable<GeneralResponse<BanPost4preEligibilitySuccess>> checkBanPost4PreEligibility(@Path(value = "ban", encoded = true) String ban);

    @GET("/api-gateway/recharges/recurring/counters/")
    public Observable<GeneralResponse<Post4preCountersSuccess>> getPost4PreCounters();

    // API 45
    @GET("/api-gateway/billing/ebill/{ban}/")
    public Observable<GeneralResponse<EBillSuccess>> getEBillBan(@Path(value = "ban", encoded = true) String ban,
                                                                 @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                 @Header("vfSsoUsername") String vfSsoUsername);

    // API 45
    @PUT("/api-gateway/billing/ebill/{ban}/")
    public Observable<GeneralResponse<EBillSuccess>> putEBillBan(@Body EBillRequest eBillRequest,
                                                                 @Path(value = "ban", encoded = true) String ban,
                                                                 @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                 @Header("vfSsoUsername") String vfSsoUsername);

    // API 45
    // parametrii vfSsoUserRole si vfSsoUsername sunt required in noul yaml pentru acest api
    @PATCH("/api-gateway/billing/ebill/{ban}/")
    public Observable<GeneralResponse> patchEBillBan(@Body EBillRequest eBillRequest,
                                                     @Path(value = "ban", encoded = true) String ban);

    // API 45
    @GET("/api-gateway/billing/ebill/ebu/{ban}/")
    public Observable<GeneralResponse<EBillSuccess>> getEBillEBUBan(@Path(value = "ban", encoded = true) String ban,
                                                                    @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                    @Header("vfCRMRole") String vfCRMRole,
                                                                    @Header("vfOdsCid") String vfOdsCid,
                                                                    @Header("vfOdsEntityType") String vfOdsEntityType,
                                                                    @Header("vfOdsEntityId") String vfOdsEntityId,
                                                                    @Header("vfContactID") String vfContactID,
                                                                    @Header("vfSsoLastName") String vfSsoLastName,
                                                                    @Header("vfSsoFirstName") String vfSsoFirstName);

    // API 45
    @PUT("/api-gateway/billing/ebill/ebu/{ban}/")
    public Observable<GeneralResponse<EBillSuccess>> putEBillEBUBan(@Body EBillRequest eBillRequest,
                                                                    @Path(value = "ban", encoded = true) String ban,
                                                                    @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                    @Header("vfCRMRole") String vfCRMRole,
                                                                    @Header("vfSsoUsername") String vfSsoUsername,
                                                                    @Header("vfOdsCid") String vfOdsCid,
                                                                    @Header("vfOdsEntityType") String vfOdsEntityType,
                                                                    @Header("vfOdsEntityId") String vfOdsEntityId,
                                                                    @Header("vfContactID") String vfContactID,
                                                                    @Header("vfSsoFirstName") String vfSsoFirstName,
                                                                    @Header("vfSsoLastName") String vfSsoLastName,
                                                                    @Header("vfOdsSsn") String vfOdsSsn,
                                                                    @Header("vfOdsBen") String vfOdsBen);

    // API 45
    @PATCH("/api-gateway/billing/ebill/ebu/{ban}/")
    public Observable<GeneralResponse> patchEBillEBUBan(@Body EBillRequest eBillRequest,
                                                        @Path(value = "ban", encoded = true) String ban,
                                                        @Header("vfSsoUserRole") String vfSsoUserRole,
                                                        @Header("vfCRMRole") String vfCRMRole,
                                                        @Header("vfSsoUsername") String vfSsoUsername,
                                                        @Header("vfOdsCid") String vfOdsCid,
                                                        @Header("vfOdsEntityType") String vfOdsEntityType,
                                                        @Header("vfOdsEntityId") String vfOdsEntityId,
                                                        @Header("vfContactID") String vfContactID,
                                                        @Header("vfSsoLastName") String vfSsoLastName,
                                                        @Header("vfSsoFirstName") String vfSsoFirstName);

    //API 39
    @GET("/api-gateway/offers/postpaid/current/{msisdn}/{sid}/")
    public Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> getActiveOffersPostpaid(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid);

    //API 30
    @GET("/api-gateway/offers/postpaid/current/{msisdn}/{sid}/")
    public Observable<GeneralResponse<ActiveOffersPostpaidSuccess>> getFixedActiveOffersPostpaid(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Query(value = "type") String type);

    @GET("/api-gateway/offers/postpaid/current/{msisdn}/{sid}/{matrix_id}/")
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> validateOfferActivationPostpaid(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Path(value = "matrix_id", encoded = true) String matrix_id);

    //API 39
    @GET("/api-gateway/offers/postpaid/current/ebu/")
    public Observable<GeneralResponse<ActiveOffersPostpaidEbuSuccess>> getActiveOffersEbu(@Header(value = "vfOdsSid") String vfOdsSid);

    //API 37
    @PUT("/api-gateway/offers/prepaid/available/{offer_id}/")
    public Observable<GeneralResponse<EligibleOffersSuccess>> activateEligibleOffer(@Path(value = "offer_id", encoded = true) String offer_id, @Body ActivatePrepaidOfferRequest activatePrepaidOfferRequest);

    @GET("/api-gateway/offers/postpaid/available/{msisdn}/{sid}/{matrix_id}/")
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> getActivatePostPaidEligibleOffer(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Path(value = "matrix_id", encoded = true) String matrix_id);

    @PUT("/api-gateway/offers/postpaid/available/{msisdn}/{sid}/{matrix_id}/")
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> putActivatePostPaidEligibleOffer(@Body ActivationRequest activationRequest, @Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Path(value = "matrix_id", encoded = true) String matrix_id);

    //API 41 - delete
    @HTTP(method = "DELETE", path = "/api-gateway/offers/postpaid/current/{msisdn}/{sid}/{offer_id}/{instance_id}/", hasBody = true)
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deletePostpaidActiveOffer(@Path(value = "msisdn", encoded = true) String msisdn, @Path(value = "sid", encoded = true) String sid, @Path(value = "offer_id", encoded = true) Long offer_id, @Path(value = "instance_id", encoded = true) Long instance_id, @Body ActivationRequest activationRequest);

    //API 41 - delete
    @HTTP(method = "DELETE", path = "/api-gateway/offers/postpaid/ebu/", hasBody = true)
    public Observable<GeneralResponse<ActivationEligibilitySuccess>> deactivateOfferForEbu(@Header("vfOdsCid") String vfCid, @Header("vfContactId") String vfContactId, @Header("vfCRMRole") String vfCRMRole,
                                                                                           @Header("vfOdsBan") String vfOdsBan, @Header("vfOdsSid") String vfOdsSid, @Header("vfOdsBen") String vfOdsBen,
                                                                                           @Header("selectedMsisdn") String vfSelectedMSISDN , @Query("promoId") Long promoId,
                                                                                           @Query("promoName") String promoName);


    @GET("/api-gateway/offers/postpaid/ebu/")
    public Observable<GeneralResponse<EbuOfferEligibilitySuccess>> checkEbuOfferDeleteEligibility(@Header("vfCRMRole") String vfCRMRole, @Header("vfOdsSid") String vfOdsSid, @Header("contactId") String contactId, @Query("promoId") Long promoId);

    @PUT("/api-gateway/offers/postpaid/ebu/{msisdn}/{sid}/{promotionId}/")
    public Observable<GeneralResponse> activateOfferEbu(@Path("msisdn") String msisdn,
                                                                                      @Path("sid") String sid,
                                                                                      @Path("promotionId") String promotionId,
                                                                                      @Header("vfCRMRole") String vfCRMRole,
                                                                                      @Header("offerName") String offerName,
                                                                                      @Header("vfOdsCid") String vfOdsCid,
                                                                                      @Header("vfOdsBen") String vfOdsBen,
                                                                                      @Header("vfOdsBan") String vfOdsBan);


    //API 46 - ShopLogin
    @POST("/api-gateway/shop/login/")
    public Observable<GeneralResponse<ShopLoginSuccess>> postShopLogin(@Body ShopLoginRequest shopLoginRequest);

    //API 47 - Shop Eligibility
    @GET("/api-gateway/shop/eligibility/")
    public Observable<GeneralResponse<ShopEligibilitySuccess>> getShopEligibility(@Query(value = "msisdn") String msisdn, @Header(value = "shopSessionToken") String shopSessionToken);

    //API 48 - Shop Products
    @GET("/api-gateway/shop/products/recommended/")
    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsRecommended(@Query(value = "pricePlanSkuId") String pricePlanSkuId, @Header(value = "shopSessionToken") String shopSessionToken);

    //?mcareFlow=allPromotionalPhones&ignoreCatType=true&riscFlow=true
    @GET("/api-gateway/shop/products/listings/")
    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsListings(@Query(value = "pricePlanSkuId") String pricePlanSkuId,
                                                                                    @Header(value = "shopSessionToken") String shopSessionToken);

    @GET("/api-gateway/shop/products/details/")
    public Observable<GeneralResponse<ShopProductsSuccess>> getShopProductsDetails(@Query(value = "pricePlanSkuId") String pricePlanSkuId,
                                                                                   @Query(value = "phoneSkuId") String phoneSkuId,
                                                                                   @Header(value = "shopSessionToken") String shopSessionToken);

    //API 49 - Shop Price Plans
    @GET("/api-gateway/shop/priceplans/recommended/")
    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansRecommended(@Query(value = "phoneSkuId") String phoneSkuId, @Header(value = "shopSessionToken") String shopSessionToken);

    @GET("/api-gateway/shop/priceplans/listings/")
    public Observable<GeneralResponse<ShopPricePlanSuccess>> getShopPricePlansListings(@Query(value = "phoneSkuId") String phoneSkuId, @Header(value = "shopSessionToken") String shopSessionToken);

    //API 50 - Shop Price Plans
    @PUT("/api-gateway/shop/cart/")
    public Observable<GeneralResponse<ShopCartSuccess>> putShopCart(@Body ShopCartRequest shopCartRequest, @Header(value = "shopSessionToken") String shopSessionToken);

    @DELETE("/api-gateway/shop/cart/")
    public Observable<GeneralResponse<ShopCartSuccess>> deleteShopCart(@Body ShopCartRequest shopCartRequest, @Header(value = "shopSessionToken") String shopSessionToken);

    @GET("/consumption/groups/public/documents/web_asset/chat_faqs.json")
    public Observable<List<JsonList>> getChatFaqJsonList();

    //http://ip-api.com/
    @GET("/json")
    public Observable<CountryByIp> getCountryListJson();

    @GET("/consumption/groups/public/documents/web_asset/shops.js")
    public Observable<String> getLocationsList();

    //api 55
    @GET("/api-gateway/billing/history/{ban}")
    public Observable<GeneralResponse<BillHistorySuccess>> getBillHistory(@Header("vfOdsBen") String vfOdsBen,
                                                                          @Header("vfCid") String vfCid,
                                                                          @Header("vfOdsCid") String vfodsCid,
                                                                          @Header ("crmRole") String crmRole,
                                                                          @Path(value = "ban", encoded = true) String ban,
                                                                          @Query(value = "months") int months);

    //api 52 support - email
    @Multipart
    @POST("/api-gateway/support/assistance/email/")
    public Observable<GeneralResponse> postEmail(@Part("emailRequest") EmailRequest emailRequest, @Part MultipartBody.Part file);


    //api 53 support - chat
    @GET("/api-gateway/support/chat/eligibility/")
    public Observable<GeneralResponse> getChatEligibility(@Header("isChatAO") boolean isChatAO,
                                                          @Header("isChatBlackList") boolean isChatBlackList,
                                                          @Header("isChatRole") boolean isChatRole,
                                                          @Query(value = "checkUser") boolean user,
                                                          @Query(value = "checkTime") boolean time);

    //api 56 bill-summary
    @GET("/api-gateway/billing/summary/{ban}/")
    public Observable<GeneralResponse<BillSummarySuccess>> getBillSummary(@Header("vfCid") String vfCid,
                                                                          @Header("vfOdsCid") String vfodsCid,
                                                                          @Path(value = "ban", encoded = true) String ban,
                                                                          @Query(value = "bcd") long bcd,
                                                                          @Query(value = "term") boolean term);

    @GET("/api-gateway/billing/summary/details/{ban}/{sid}/")
    public Observable<GeneralResponse<BillSummaryDetailsSuccess>> getBillSummaryDetails(@Header("vfCid") String vfCid,
                                                                                        @Header("vfOdsCid") String vfodsCid,
                                                                                        @Path(value = "ban", encoded = true) String ban,
                                                                                        @Path(value = "sid", encoded = true) String sid,
                                                                                        @Query(value = "bcd") long bcd);

    @FormUrlEncoded
    @POST("/api-gateway/chat/call/getChatState/VdfChat/")
    public Observable<Response<ChatStateResponse>> getChatState(@Field("searchDB") boolean searchDB, @Field("isNewMcare") boolean isNewMcare);

    @FormUrlEncoded
    @POST("/api-gateway/chat/call/chatstate/VdfChat/")
    public Observable<String> updateChatState(@Field("chatState") int state,
                                              @Field("channel") String channel);


    @POST("/api-gateway/chat/call/start/VdfChat/")
    public Observable<Response<GeneralResponse<StartChatResponse>>> postStartChat(@Header("msisdn") String msisdn, @Body StartChatRequest startChatRequest);

    @FormUrlEncoded
    @POST("/api-gateway/chat/poll/session/VdfChat/")
    public Observable<String> pollSession(@Field("contactID") String contactId,
                                          @Field("sessionKey") String sessionKey,
                                          @Field("userEmail") String userEmail,
                                          @Field("userFromEnroll") boolean userFromEnroll,
                                          @Field("lastReadTime") String lastReadTime,
                                          @Field("channel") String channel);

    @FormUrlEncoded
    @POST("/api-gateway/chat/poll/messages/VdfChat/")
    public Observable<String> pollMessages(@Field("contactID") String contactId,
                                           @Field("sessionKey") String sessionKey,
                                           @Field("isTyping") boolean isTyping,
                                           @Field("userFromEnroll") boolean userFromEnroll,
                                           @Field("lastReadTime") String lastReadTime,
                                           @Field("channel") String channel);

    @FormUrlEncoded
    @POST("/api-gateway/chat/call/add/VdfChat/")
    public Observable<String> sendTextMessage(@Header("Cookie") String myCookie,
                                              @Field("contactID") String contactId,
                                              @Field("sessionKey") String sessionKey,
                                              @Field("message") VodafoneMessage message,
                                              @Field("userFromEnroll") boolean userFromEnroll,
                                              @Field("lastReadTime") String lastReadTime,
                                              @Field("channel") String channel);


    @FormUrlEncoded
    @POST("/api-gateway/chat/call/logout/VdfChat/")
    public Observable<String> logOutChat(@Header("Cookie") String myCookie,
                                         @Field("contactID") String contactId,
                                         @Field("sessionKey") String sessionKey,
                                         @Field("email") String userEmail,
                                         @Field("channel") String channel);

    @FormUrlEncoded
    @POST("/api-gateway/chat/call/logout/VdfChat/")
    public Observable<String> logOutChatWithSurvey(@Header("Cookie") String myCookie,
                                                   @Field("contactID") String contactId,
                                                   @Field("sessionKey") String sessionKey,
                                                   @Field("email") String userEmail,
                                                   @Field("channel") String channel,
                                                   @Field("pSurveyEligible") boolean pSurveyEligible);

    @Multipart
    @POST("/api-gateway/chat/call/uploadMobile/VdfChat/")
    public Observable<String> sendMediaMessage(@Header("Cookie") String myCookie,
                                               @Part("contactID") String contactId,
                                               @Part("sessionKey") String sessionKey,
                                               @Part MultipartBody.Part file,
                                               @Part("description") String description);

    @FormUrlEncoded
    @POST("/api-gateway/chat/call/switchButton/VdfChat/")
    public Observable<String> switchToPC(@Field("contactID") String contactId,
                                         @Field("sessionKey") String sessionKey,
                                         @Field("userFromEnroll") boolean userFromEnroll,
                                         @Field("channel") String channel,
                                         @Field("userEmail") String userEmail,
                                         @Field("message") String message);

    //api 58 sim details
    @GET("/api-gateway/subscriber/sim/{sid}/")
    public Observable<GeneralResponse<SIMDetailsSuccess>> getSimDetails(@Path(value = "sid", encoded = true) String sid);

    //api 59 sim status
    @GET("/api-gateway/subscriber/sim/status/{sid}/")
    public Observable<GeneralResponse<SIMStatusSuccess>> getSimStatus(@Path(value = "sid", encoded = true) String sid);

    @GET("/api-gateway/subscriber/sim/status/ebu/{sid}/")
    public Observable<GeneralResponse<SIMStatusEBUSuccess>> getEbuSimStatus(@Path(value = "sid", encoded = true) String sid);

    @PUT("/api-gateway/subscriber/sim/block/")
    public Observable<GeneralResponse> putSimBlock(@Body SIMChangeStatusRequest simChangeStatusRequest);

    @PUT("/api-gateway/subscriber/sim/ebu/block/")
    public Observable<GeneralResponse> putEbuSimBlock(@Body SIMChangeStatusRequestEBU simChangeStatusRequestEBU,
                                                      @Header("vfCRMRole") String vfCRMRole);

    @PUT("/api-gateway/subscriber/sim/unblock/")
    public Observable<GeneralResponse> putSimUnblock(@Body SIMChangeStatusRequest simChangeStatusRequest);

    @PUT("/api-gateway/subscriber/sim/ebu/unblock/")
    public Observable<GeneralResponse> putEbuSimUnblock(@Body SIMChangeStatusRequestEBU simChangeStatusRequestEBU,
                                                        @Header("vfCRMRole") String vfCRMRole);

    @GET("/api-gateway/billing/{ban}/ebill/")
    public Observable<ResponseBody> downloadBill(@Path(value = "ban", encoded = true) String ban, @Query(value = "date") long date, @Query(value = "detailed") String detailed);

    //api 51 order history
    @GET("/api-gateway/shop/orders/listings/")
    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrdersList(@Header("shopSessionToken") String shopSessionToken, @Header("vfSsoId") String vfSsoId, @Header("vfSsoUserRole") String vfSsoUserRole, @Header("vfSsoUsername") String vfSsoUsername);

    @GET("/api-gateway/shop/orders/details/")
    public Observable<GeneralResponse<ShopOrdersSuccess>> getOrderDetails(@Header("shopSessionToken") String shopSessionToken,
                                                                          @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                          @Header("vfSsoUsername") String vfSsoUsername,
                                                                          @Query("orderId") String orderId);

    //api 57 payment agreement
    @GET("/api-gateway/billing/payment/agreement/")
    public Observable<GeneralResponse<PaymentAgreementSuccess>> getPaymentAgreement(@Query("segment") String segment,
                                                                                    @Query("phone") String phone,
                                                                                    @Query("crmRole") String crmRole);


    @PUT("/api-gateway/billing/payment/agreement/")
    public Observable<GeneralResponse<PaymentAgreementSuccess>> putPaymentAgreement(@Body PaymentAgreementRequest paymentAgreementRequest);

    //api 57 Drop 2/ Sprint 10
    @POST("/api-gateway/billing/payment/agreement/email/{ban}")
    public Observable<GeneralResponse<PaymentAgreementSuccess>> postPaymentAgreement(@Path(value = "ban", encoded = true) String ban);


    @GET("/api-gateway/shop/loyalty/program/")
    public Observable<GeneralResponse<ShopLoyaltyProgramSuccess>> getLoyaltyProgram(@Query("ban") String selectedBan,
                                                                                    @Header("shopSessionToken") String shopSessionToken);

    @POST("/api-gateway/shop/loyalty/program/")
    public Observable<GeneralResponse<OptInSuccess>> postEnrollLoyaltyProgram(@Header("shopSessionToken") String shopSessionToken, @Body LoyaltyProgramRequest loyaltyProgramRequest);


    //api 62 access type
    @GET("/api-gateway/offers/postpaid/roaming/{sid}/")
    public Observable<GeneralResponse<AccessTypeSuccess>> getAccessType(@Path(value = "sid", encoded = true) String sid);

    //api 62 access type
    @PUT("/api-gateway/offers/postpaid/roaming/{msisdn}/{sid}/")
    public Observable<GeneralResponse<AccessTypeSuccess>> putAccessType(
            @Path(value = "msisdn", encoded = true) String msisdn,
            @Path(value = "sid", encoded = true) String sid,
            @Body AccessTypeRequest accessTypeRequest
    );

    @GET("/api-gateway/shop/loyalty/points/")
    public Observable<GeneralResponse<ShopLoyaltyPointsSuccess>> getLoyaltyPoints(@Query("ban") String ban,
                                                                                  @Query("dateTo") String dateTo,
                                                                                  @Query("dateFrom") String dateFrom,
                                                                                  @Header("shopSessionToken") String shopSessionToken);

    @HTTP(method = "DELETE", path = "/api-gateway/shop/loyalty/program/", hasBody = true)
    public Observable<GeneralResponse<OptOutSuccess>> quitLoyaltyProgram(@Header("shopSessionToken") String shopSessionToken,
                                                                         @Body LoyaltyProgramRequest loyaltyProgramRequest);


    //API 10
    @Multipart
    @POST("/api-gateway/profile/avatar/")
    public Observable<GeneralResponse<ProfileSuccess>> postProfileAvatar(@Query("selectedMsisdn") String selectedMsisdn,
                                                                         @Query("alias") String alias,
                                                                         @Query("type") String type,
                                                                         @Part MultipartBody.Part file,
                                                                         @Part("description") String description);

    @POST("/api-gateway/profile/avatar/")
    public Observable<GeneralResponse<ProfileSuccess>> postProfileAlias(@Query("selectedMsisdn") String selectedMsisdn,
                                                                        @Query("alias") String alias,
                                                                        @Query("type") String type);

    @DELETE("/api-gateway/profile/avatar/")
    public Observable<GeneralResponse<ProfileSuccess>> deleteAvatar(@Query("selectedMsisdn") String selectedMsisdn,
                                                                    @Query("type") String type);

    @PUT("/api-gateway/profile/")
    public Observable<GeneralResponse<ProfileSuccess>> putDefaultProfile(@Query("defaultMsisdn") String selectedMsisdn);

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_tariffs_postpaid.json")
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsPostpaid();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_tariffs_prepaid.json")
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsPrepaid();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_system_status.json")
    public Observable<GeneralResponse<SystemStatusSuccess>> getSystemStatus();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_hints.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHints();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_hints_prepaid.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHintsPrepaid();

    //API 63 Identity Selector
    @GET("/api-gateway/profile/identities/")
    public Observable<GeneralResponse<UserEntitiesSuccess>> getIdentities(@Header("vfContactID") String vfContactID,
                                                                          @Header("vfEBUMigrated") boolean vfEBUMigrated,
                                                                          @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                          @Header("vfMaCustomer") String vfMaCustomer);

    @PUT("/api-gateway/profile/identity/default-entity/")
    public Observable<GeneralResponse<UserEntitiesSuccess>> putDefaultIdentity(@Header("entityId") String entityId);

    //api 63
    @GET("/api-gateway/profile/identity/details/")
    public Observable<GeneralResponse<EntityDetailsSuccess>> getUserDetails(@Header("vfOdsCid") String vfodsCid);

    @GET("/api-gateway/profile/identity/{entityType}/inverse-hierarchy/details")
    public Observable<GeneralResponse<EntityChildItem>> getInverseHierarchy(@Header("entityId") String entityId,
                                                                            @Header("vfOdsPhoneNumber") String vfOdsPhoneNumber,
                                                                            @Path("entityType") String entityType);

    @GET("/api-gateway/profile/identity/customer-restriction")
    public Observable<GeneralResponse<CustomerRestrictionsSuccess>> getIdentityCostumerRestriction(@Header("vfOdsSsn") String vfOdsSsn);

    @GET("/api-gateway/profile/identity/ebu/getBen")
    public Observable<GeneralResponse<BenSuccessEbu>> getEbuBen(@Header("vfOdsBan") String vfOdsBan);

    //    @GET("/consumption/groups/public/documents/web_asset/mcare_faq.json")
    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_faq.json")
    public Observable<FaqPOJO> getFAQJson();

    @GET("/api-gateway/subscriber/creditinadvance/checkeligibility/")
    public Observable<GeneralResponse<CreditInAdvanceSuccess>> getCreditInAdvanceEligibility();

    @POST("/api-gateway/subscriber/creditinadvance/performcredit/")
    public Observable<GeneralResponse<CreditInAdvanceSuccess>> performCreditInAdvance(@Body EligibilityInfo eligibilityInfo);

    //api 65
    @GET("/api-gateway/billing/payment/confirmation/")
    public Observable<GeneralResponse<PaymentConfirmationSuccess>> getPaymentConfirmation(@Query("segment") String segment,
                                                                                          @Query("phone") String phone,
                                                                                          @Query("crmRole") String crmRole);


    @POST("/api-gateway/billing/payment/confirmation/")
    public Observable<GeneralResponse<PaymentConfirmationSuccess>> postPaymentConfirmation(@Body PaymentConfirmationRequest paymentConfirmationRequest,
                                                                                           @Header("vfOdsCid") String vfOdsCid,
                                                                                           @Header("vfOdsSid") String vfOdsSid);

    //API 66 User Requests
    @GET("/api-gateway/support/user/requests")
    public Observable<GeneralResponse<UserRequestsSuccess>> getUserRequests(//@Header("vfEBUMigrated") boolean vfEBUMigrated,
                                                                            //@Header("vfSsoUserRole") String vfSsoUserRole,
                                                                            @Header("vfOdsCid") String vfOdsCid,
                                                                            @Header("vfOdsBan") String vfOdsBan,
                                                                            @Header("crmRole") String crmRole);

    //API 66 User Requests (process request)
    @POST("/api-gateway/support/user/requests/process/{action}/")
    public Observable<GeneralResponse<UserRequestsSuccess>> postUserRequest(@Path(value = "action", encoded = true) String action,
                                                                            @Header("requestId") String requestId,
                                                                            @Header("requesterEmail") String requesterEmail,
                                                                            @Header("vfEBUMigrated") boolean vfEBUMigrated,
                                                                            @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                            @Header("vfOdsCid") String vfOdsCid,
                                                                            @Header("vfOdsBan") String vfOdsBan,
                                                                            @Header("crmRole") String crmRole,
                                                                            @Header("requesterName") String requesterName);

    // API 67 ICR
    @GET("/api-gateway/support/international/rates/own/")
    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOwn(@Header("vfPhoneNumber") String vfPhoneNumber,
                                                                                  @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                                  @Query("phone") String phone,
                                                                                  @Query("calledPhone") String calledPhone,
                                                                                  @Query("selectedMsisdn") String selectedMsisdn,
                                                                                  @Query("crmRole") String crmRole,
                                                                                  @Header("vfEBUMigrated") boolean vfEBUMigrated);

    @GET("/api-gateway/support/international/rates/other/")
    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrOther(@Header("vfSsoUserRole") String vfSsoUserRole,
                                                                                    @Query("callerPhone") String callerPhone,
                                                                                    @Query("calledPhone") String calledPhone,
                                                                                    @Query("crmRole") String crmRole);

    @POST("/api-gateway/support/international/rates/other/")
    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcr(@Query("crmRole") String crmRole,
                                                                               @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                               @Header("vfPhoneNumber") String vfPhoneNumber,
                                                                               @Body InternationalCallingRatesRequest body);

    @POST("/api-gateway/support/international/rates/sms/{phone}/")
    public Observable<GeneralResponse<InternationalCallingRateSuccess>> getIcrSms(@Path(value = "phone", encoded = true) String phone);


    //API 15 - CR - RECOVER USERNAME
    @POST("/api-gateway/identity/username/recover/")
    public Observable<GeneralResponse<RecoverUsernameResponse>> recoverUsername(@Body RecoverUsernameRequest recoverUsernameRequest);

    //API 16 - CR - RECOVER PASSWORD
    @POST("/api-gateway/identity/password/recover/")
    public Observable<GeneralResponse<RecoverPasswordCorrectResponse>> recoverPassword(@Body RecoverPasswordRequest recoverPasswordRequest);

    //API 68 Loyalty segment
    @GET("/api-gateway/shop/loyalty/segment/")
    public Observable<GeneralResponse<LoyaltySegmentSuccess>> getLoyaltySegment(@Query("crmRole") String crmRole);

    @POST("/api-gateway/shop/loyalty/segment/")
    public Observable<GeneralResponse<LoyaltySegmentSuccess>> postEbuLoyaltySegment(@Query("crmRole") String crmRole,
                                                                                    @Body LoyaltySegmentRequest loyaltySegmentRequest);

    //API 69 Loyalty vouchers
    @GET("/api-gateway/shop/loyalty/voucher/")
    public Observable<GeneralResponse<LoyaltyVoucherSuccess>> getLoyaltyVoucherList(@Query("loyaltySegment") String loyaltySegment,
                                                                                    @Query("treatmentSegment") String treatmentSegment,
                                                                                    @Header("vfCRMRole") String crmRole);

    //API 69 Loyalty reserved vouchers
    @GET("/api-gateway/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReservedSuccess>> getReservedLoyaltyVoucherList();

    //API 69 Loyalty reserved vouchers
    @POST("/api-gateway/shop/loyalty/voucher/reserve")
    public Observable<GeneralResponse<LoyaltyVoucherReserveSuccess>> postReservedVoucher(@Body ReserveVoucherRequest reserveVoucherRequest);

    //API 70 - GDPR get permissions
    @GET("/api-gateway/profile/gdpr/permissions")
    public Observable<GeneralResponse<GdprGetResponse>> getPermissions(@Header("vfPhoneNumber") String vfPhoneNumber,
                                                                       @Query("vfSid") String vfSid);

    //API 70 - GDPR set permissions
    @PUT("/api-gateway/profile/gdpr/permissions")
    public Observable<GeneralResponse> setPermissions(@Body GdprPermissions gdprPermissions,
                                                      @Header("vfSsoUsername") String vfSsoUsername,
                                                      @Query("customerId") String customerId,
                                                      @Query("requestType") String requestType,
                                                      @Query("phone") String phone);

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_vov.json")
    public Observable<VoiceOfVodafoneResponse> getVovS();

    //API-58
    @GET("/api-gateway/subscriber/sim/ebu/{msisdn}/")
    public Observable<GeneralResponse<SIMDetailsSuccess>> getEbuSimDetails(@Path(value = "msisdn", encoded = true) String subscriberMsisdn,
                                                                           @Header("vfOdsSid") String vfOdsSid);

    @POST("/api-gateway/subscriber/sim/replace/{selectedMsisdn}/")
    public Observable<GeneralResponse<SIMReplaceSuccess>> postReplaceSim(@Path(value = "selectedMsisdn", encoded = true) String selectedMsisdn,
                                                                         @Header("vfEBUMigrated") String vfEBUMigrated,
                                                                         @Header("vfContactID") String vfContactID,
                                                                         @Header("vfSid") String vfSid,
                                                                         @Header("vfCid") String vfCid,
                                                                         @Body SimReplaceBean simReplaceBean);

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_hints_ebu.json")
    public Observable<GeneralResponse<TravallingHintSuccess>> getTravellingHintsEbu();

    @GET("/consumption/groups/public/@vfinternet/documents/web_asset/mcare_roaming_tariffs_ebu.json")
    public Observable<GeneralResponse<RoamingTariffsSuccess>> getRoamingTariffsEbu();

    //API 62 Accces Type for EBU
    @GET("/api-gateway/offers/postpaid/roaming/ebu/{sid}")
    public Observable<GeneralResponse<AccessTypeEBU>> getAccessTypeEbu(@Path(value = "sid", encoded = true) String sid);

    //api 62 access type
    @PUT("/api-gateway/offers/postpaid/roaming/ebu/")
    public Observable<GeneralResponse> putAccessTypeEbu(
            @Header("vfCRMRole") String vfCRMRole,
            @Body AccessTypeEbuRequest accessTypeEbuRequest);

    //api 62 Credit Vetting Eligibility
    @GET("/api-gateway/offers/roaming/ebu/eligibility")
    public Observable<GeneralResponse> checkEligibilityEbu(@Header("vfOdsCid") String vfOdsCid, @Header("vfOdsSid") String vfOdsSid);

    // API-73 - GET for Prepaid
    @GET("/api-gateway/offers/prepaid/ion")
    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPrepaid(@Header("vfSid") String vfSid,
                                                                                     @Header("vfSsoUserRole") String vfSsoUserRole);

    // API-73 - GET for Postpaid
    @GET("/api-gateway/offers/postpaid/ion/{selectedMsisdn}/")
    public Observable<GeneralResponse<IonEligibilitySuccess>> getUnlimitedIonPostpaid(@Header("vfSsoUserRole") String vfSsoUserRole,
                                                                                      @Path(value = "selectedMsisdn", encoded = true) String selectedMsisdn);

    // API-73 - PUT for Prepaid
    @PUT("/api-gateway/offers/prepaid/ion/")
    public Observable<GeneralResponse> putUnlimitedIonPrepaid(@Body IONPrepaidRequest ionPrepaidRequest,
                                                              @Header("vfSid") String vfSid,
                                                              @Header("vfCid") String vfCid,
                                                              @Header("vfSsoUsername") String vfSsoUsername,
                                                              @Header("vfPhoneNumber") String vfPhoneNumber,
                                                              @Header("vfSsoEmail") String vfSsoEmail,
                                                              @Header("vfSsoUserRole") String vfSsoUserRole);

    // API-73 - PUT for Postpaid
    @PUT("/api-gateway/offers/postpaid/ion/")
    public Observable<GeneralResponse> putUnlimitedIonPostpaid(@Body IONPostpaidRequest ionPostpaidRequest,
                                                               @Header("vfCid") String vfCid,
                                                               @Header("vfSsoUsername") String vfSsoUsername,
                                                               @Header("vfSsoEmail") String vfSsoEmail,
                                                               @Header("vfSsoUserRole") String vfSsoUserRole);

    // API-73 - DELETE for Prepaid
    @HTTP(method = "DELETE", path = "/api-gateway/offers/prepaid/ion/", hasBody = true)
    public Observable<GeneralResponse> deleteUnlimitedIonPrepaid(@Body IONPrepaidRequest ionPrepaidRequest,
                                                                 @Header("vfSid") String vfSid,
                                                                 @Header("vfCid") String vfCid,
                                                                 @Header("vfSsoUsername") String vfSsoUsername,
                                                                 @Header("vfPhoneNumber") String vfPhoneNumber,
                                                                 @Header("vfSsoEmail") String vfSsoEmail,
                                                                 @Header("vfSsoUserRole") String vfSsoUserRole);

    // API-73 - DELETE for Postpaid
    @HTTP(method = "DELETE", path = "/api-gateway/offers/postpaid/ion/", hasBody = true)
    public Observable<GeneralResponse> deleteUnlimitedIonPostpaid(@Body IONPostpaidRequest ionPostpaidRequest,
                                                                  @Header("vfCid") String vfCid,
                                                                  @Header("vfSsoUsername") String vfSsoUsername,
                                                                  @Header("vfSsoEmail") String vfSsoEmail,
                                                                  @Header("vfSsoUserRole") String vfSsoUserRole);

    //API-72 VTV Devices
    @GET("/api-gateway/api-ott/vtv/devices/action/getbyoperator/")
    public Observable<GeneralResponse<GetByOperatorSuccess>> getDeviceByOperator(@Header("vtvUserRole") String vfSsoUserRole, @Query("msisdn") String msisdn);

    //API-72 VTV Rename Active Device name
    @POST("/api-gateway/api-ott/vtv/devices/action/rename/")
    public Observable<GeneralResponse> renameActiveDevice(@Header("vtvUserRole") String vfSsoUserRole, @Body RenameRequest renameRequest);

    @POST("/api-gateway/api-ott/vtv/devices/action/resetpin/")
    public Observable<GeneralResponse<ResetPinSuccess>> resetPIN(@Header("vtvUserRole") String vfSsoUserRole, @Body ResetPinRequest resetPinRequest);

    @HTTP(method = "DELETE", path = "/api-gateway/api-ott/vtv/devices/action/delete/", hasBody = true)
    public Observable<GeneralResponse> deleteActiveDevice(@Header("vtvUserRole") String vfSsoUserRole, @Body DeleteRequest deleteRequest);

    //API-35
    @POST("/api-gateway/offers/prepaid/roaming/label/")
    public Observable<GeneralResponse<RoamingLabelResponse>> getRoamingLabels(@Body RoamingLabelRequest roamingLabelRequest);

    //API-100
    //  @GET("http://vodafonero.getsandbox.com/profile/tv/hierarchy")
    @GET("/api-gateway/profile/tv/hierarchy")
    public Observable<GeneralResponse<TvHierarchyResponse>> getTvHierarchy(@Header("vfSsoUsername") String vfSsoUsername,
                                                                           @Header("vfPhoneNumber") String vfPhoneNumber,
                                                                           @Header("vfSsoUserRole") String vfSsoUserRole,
                                                                           @Header("vfEBUMigrated") boolean vfEBUMigrated,
                                                                           @Query(value = "fixedBan") String vfFixedBan,
                                                                           @Query(value = "cid") String vfFixedCid,
                                                                           @Query(value = "ban") String ban);

    //API-101
    @GET("/api-gateway/payment/cards/")
    public Observable<GeneralResponse<CardsResponse>> getCards(@Header("vfSsoUsername") String vfSsoUsername);

    @POST("/api-gateway/payment/cards/")
    public Observable<GeneralResponse<AddCardResponse>> addCard(@Header("vfSsoUsername") String vfSsoUsername,
                                                                @Header("vfPhoneNumber") String vfPhoneNumber,
                                                                @Header("vfSsoEmail") String vfSsoEmail);

    @DELETE("/api-gateway/payment/cards/{token}/")
    public Observable<GeneralResponse<DeleteCardResponse>> deleteCard(@Path(value = "token") String token);
}
