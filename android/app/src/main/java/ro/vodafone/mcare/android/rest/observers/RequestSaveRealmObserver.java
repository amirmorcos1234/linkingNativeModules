package ro.vodafone.mcare.android.rest.observers;

import android.support.annotation.CallSuper;
import android.util.Log;

import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmObject;
import retrofit2.adapter.rxjava.HttpException;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.eligibility.BanPost4preEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyPointsSuccess;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.ShopLoyaltyProgramSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceCreditSuccess;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.notifications.NotificationFlag;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.seamless.InitialToken;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusEBUSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.Configurations;
import ro.vodafone.mcare.android.client.model.realm.system.Labels;
import ro.vodafone.mcare.android.client.model.realm.system.TimeToLeaveMap;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.SystemStatusSuccess;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRechargesSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlanSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistorySuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeEBU;
import ro.vodafone.mcare.android.client.model.travellingAboard.AccessTypeSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.CountryByIp;
import ro.vodafone.mcare.android.client.model.travellingAboard.RoamingTariffsSuccess;
import ro.vodafone.mcare.android.client.model.travellingAboard.TravallingHintSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.vodafoneTv.GetByOperatorSuccess;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.exceptions.ClientException;
import ro.vodafone.mcare.android.service.exceptions.InternetConnectionProblemException;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessFlag;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observer;

/**
 * Created by Victor Radulescu on 1/10/2017.
 */

/**
 * Observer used to save in realm objects in on next.
 * Response object should have a @GeneralResponse type.
 * Do NOT use it if you don't have a persistent data in response.
 * TODO please call super.OnNext to save in memory using realm with no other modifications.
 *
 * @param <T> Type of object for persistence
 */
public class RequestSaveRealmObserver<T> implements Observer<T> {

    private static final String TAG = "REALM";

    boolean tryAutoLoginIf401Error = true;

    public RequestSaveRealmObserver(boolean tryAutoLoginIf401Error) {
        this.tryAutoLoginIf401Error = tryAutoLoginIf401Error;
    }

    public RequestSaveRealmObserver() {
    }

    @Override
    public void onCompleted() {
    }

    @Override
    @CallSuper
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            if (((HttpException) e).code() == 401 && tryAutoLoginIf401Error) {

                D.e(((HttpException) e).message());
                if (VodafoneController.getInstance().isSeamless()) {
                    D.d("Should autologin seamless");
                    return;
                } else {
                    //AutoLoginObservable.getInstance().start();
                    D.d("Should autologin full");
                }
            }
            ///VodafoneController.getInstance().getHttpExceptionSessionExpireListener().onSessionExpire();
        }
        if (e instanceof InternetConnectionProblemException) {
            try {
                throw new AuthenticationServiceException(
                        R.string.internet_connection_problem, e);
            } catch (AuthenticationServiceException e1) {
                e1.printStackTrace();
            }
        } else if (e instanceof ClientException) {
            try {
                throw new AuthenticationServiceException(
                        R.string.internet_connection_problem, e);
            } catch (AuthenticationServiceException e1) {
                e1.printStackTrace();
            }
        } else {
            Log.e("RequestSessionObserver", "Observable error RequestSaveRealmObserver.onError: " + e.getMessage(), e);
        }
    }

    @Override
    @CallSuper
    public void onNext(T response) {
        if (response == null) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();

        if (response instanceof GeneralResponse) {
            GeneralResponse generalResponse = (GeneralResponse) response;
            //if have data
            if (generalResponse.getTransactionSuccess() != null) {
                //save data if labels or configurations
                Object transactionSuccess = generalResponse.getTransactionSuccess();

                if (transactionSuccess instanceof Labels) {
                    try {
                        Labels success = (Labels) (generalResponse).getTransactionSuccess();
                        if (((Labels) transactionSuccess).getSystemProperties() != null) {
                            RealmManager.update(realm, success);
                            Log.d(TAG, success.getClass().getName());
                            saveTimeToLive(transactionSuccess.getClass(),
                                    ((GeneralResponse) response).getTimeToLive(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof Configurations) {
                    try {
                        Configurations success = (Configurations) (generalResponse).getTransactionSuccess();
                        //onCostControlRequestCompleted Configurations
                        if (((Configurations) transactionSuccess).getSystemProperties() != null) {
                            RealmManager.update(realm, success);
                            Log.d(TAG, success.getClass().getName());
                            saveTimeToLive(transactionSuccess.getClass(),
                                    ((GeneralResponse) response).getTimeToLive(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof UserProfile) {
                    try {
                        RealmManager.update(realm, (UserProfile) generalResponse.getTransactionSuccess());
                        Log.d(TAG, transactionSuccess.getClass().getName());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof InitialToken) {
                    try {
                        RealmManager.update(realm, (InitialToken) generalResponse.getTransactionSuccess());
                        Log.d(TAG, "Initial toke successfully saved on realm");
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof CostControl) {
                    try {
                        RealmManager.delete(realm, Balance.class);
                        RealmManager.update(realm, (CostControl) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof UserProfileHierarchy) {
                    try {

                        UserProfileHierarchy uph = (UserProfileHierarchy) generalResponse.getTransactionSuccess();
                        RealmManager.startTransaction();
                        //TODO: please sort this banList locally if you need it

                        if (!(VodafoneController.getInstance().getUser() instanceof CBUUser))
                            Collections.sort(uph.getBanList(), Ban.getBanComparator());

                        Log.d(TAG, "save transactionSuccess instanceof UserProfileHierarchy");
                        RealmManager.update(realm, (UserProfileHierarchy) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof BalanceCreditSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof BalanceCreditSuccess");
                        RealmManager.update(realm, (BalanceCreditSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof BalanceSecondarySuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof BalanceSecondarySuccess");
                        RealmManager.update(realm, (BalanceSecondarySuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof RecommendedRechargesSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof RecommendedRechargesSuccess");
                        RealmManager.update(realm, (RecommendedRechargesSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof Profile) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof Profile");
                        RealmManager.update(realm, (Profile) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof UnicaOffersSuccess) {
                    try {
                        RealmManager.delete(UnicaOffer.class);
                        Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                        RealmManager.update(realm, (UnicaOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof BillRechargesSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof BillRechargesSuccess");
                        RealmManager.update(realm, (BillRechargesSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof FavoriteNumbersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                        RealmManager.update(realm, (FavoriteNumbersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof RechargeHistorySuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                        RealmManager.update(realm, (RechargeHistorySuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof AncomPendingOffersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof AncomPendingOffersSuccess");
                        RealmManager.update(realm, (AncomPendingOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof BannerOffersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof BannerOffersSuccess");
                        RealmManager.delete(BannerOffersSuccess.class);
                        RealmManager.update(realm, (BannerOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof NotificationFlag) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof NotificationFlag");
                        RealmManager.delete(NotificationFlag.class);
                        RealmManager.update(realm, (NotificationFlag) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof SeamlessFlag) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof SeamlessFlag");
                        RealmManager.delete(SeamlessFlag.class);
                        RealmManager.update(realm, (SeamlessFlag) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof ShopLoginSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ShopLoginSuccess");
                        RealmManager.update(realm, (ShopLoginSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof ShopPricePlanSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ShopPricePlanSuccess");
                        RealmManager.update(realm, (ShopPricePlanSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof ActiveOffersPostpaidSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ActiveOffersPostpaidSuccess");
                        RealmManager.update(realm, (ActiveOffersPostpaidSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }//
                } else if (transactionSuccess instanceof ShopProductsSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ActiveOffersPostpaidSuccess");
                        RealmManager.update(realm, (ShopProductsSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }//ActiveOffersPostpaidSuccess
                } else if (transactionSuccess instanceof ShopEligibilitySuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ShopEligibilitySuccess");
                        RealmManager.update(realm, (ShopEligibilitySuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }//ActiveOffersPostpaidSuccess
                } else if (transactionSuccess instanceof AncomPendingOffersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof AncomPendingOffersSuccess");
                        RealmManager.update(realm, (AncomPendingOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }//ActiveOffersPostpaidSuccess
                } else if (transactionSuccess instanceof ShopLoyaltyProgramSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ShopLoyaltyProgramSuccess ");
                        RealmManager.update(realm, (ShopLoyaltyProgramSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (transactionSuccess instanceof ShopLoyaltyPointsSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ShopLoyaltyPointsSuccess ");
                        RealmManager.update(realm, (ShopLoyaltyPointsSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof SystemStatusSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof SystemStatusSuccess ");
                        RealmManager.update(realm, (SystemStatusSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLiveStatic(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof EligibleOffersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof EligibleOffersSuccess ");
                        RealmManager.update(realm, (EligibleOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLiveStatic(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof EligibleOffersPostSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof EligibleOffersPostSuccess ");
                        RealmManager.update(realm, (EligibleOffersPostSuccess) generalResponse.getTransactionSuccess());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof TravallingHintSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof TravallingHintSuccess ");
                        RealmManager.update(realm, (TravallingHintSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLiveStatic(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof RoamingTariffsSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof RoamingTariffsSuccess ");
                        RealmManager.update(realm, (RoamingTariffsSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLiveStatic(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof InvoiceDetailsSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof InvoiceDetailsSuccess");
                        RealmManager.update(realm, (InvoiceDetailsSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof BanPost4preEligibilitySuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof BanPost4preEligibilitySuccess");
                        RealmManager.update(realm, (BanPost4preEligibilitySuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof AccessTypeSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof AccessTypeSuccess");
                        RealmManager.update(realm, (AccessTypeSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof ActiveOffersSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ActiveOffersSuccess");
                        RealmManager.update(realm, (ActiveOffersSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof UserEntitiesSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof UserEntitiesSuccess");
                        RealmManager.update(realm, (UserEntitiesSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof CreditInAdvanceSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof ActiveOffersSuccess");
                        RealmManager.update(realm, (CreditInAdvanceSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof GdprGetResponse) {

                    try {
                        Log.d(TAG, "save transactionSucces instanceof GdprGetResponse");
                        RealmManager.update(realm, (GdprGetResponse) generalResponse.getTransactionSuccess());
                        long timeToLiveGdpr = ((GeneralResponse) response).getTimeToLive();
                        int key = ((GdprGetResponse) generalResponse.getTransactionSuccess()).getId_gdpr();
                        saveTimeToLiveAfterKey(transactionSuccess.getClass(), timeToLiveGdpr, true, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (transactionSuccess instanceof SIMStatusEBUSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof SIMStatusEBUSuccess");
                        RealmManager.update(realm, (SIMStatusEBUSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof AccessTypeEBU) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof AccessTypeEBU");
                        RealmManager.update(realm, (AccessTypeEBU) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof CountryByIp) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof CountryByIp");
                        RealmManager.update(realm, (CountryByIp) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof GetByOperatorSuccess) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof GetByOperatorSuccess");
                        RealmManager.update(realm, (GetByOperatorSuccess) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), ((GeneralResponse) response).getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (transactionSuccess instanceof TvHierarchyResponse) {
                    try {
                        Log.d(TAG, "save transactionSuccess instanceof TvHierarchyResponse");
                        RealmManager.update(realm, (TvHierarchyResponse) generalResponse.getTransactionSuccess());
                        saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (transactionSuccess instanceof RealmObject) {
                    try {
                        Log.d(TAG, "save transaction intanceof " + transactionSuccess.getClass());
                        RealmManager.update(realm, (RealmObject) generalResponse.getTransactionSuccess());
                        saveTimeToLive(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e(TAG, generalResponse.toString() + " is NOT instance of any known cache bean");
            }
            realm.close();
        } else if (response instanceof CountryByIp) {
            saveCountryByIP((CountryByIp) response);
        }
    }


    private void saveTimeToLive(Class className, long timeToLive, boolean isUserData) {
        Realm realm = Realm.getDefaultInstance();
        Log.d("TimeToLive", className.getName());
        TimeToLeaveMap timeToLeaveMap = new TimeToLeaveMap(className.getName(), timeToLive, isUserData);
        RealmManager.update(realm, timeToLeaveMap);
        realm.close();
    }

    private static void saveTimeToLiveStatic(Class className, long timeToLive, boolean isUserData) {
        Realm realm = Realm.getDefaultInstance();
        Log.d("TimeToLive", className.getName());
        TimeToLeaveMap timeToLeaveMap = new TimeToLeaveMap(className.getName(), timeToLive, isUserData);
        RealmManager.update(realm, timeToLeaveMap);
        realm.close();
    }

    private static void saveTimeToLiveAfterKey(Class className, long timeToLive, boolean isUserData, int key) {
        Realm realm = Realm.getDefaultInstance();
        Log.d("TimeToLive", className.getName() + key);
        TimeToLeaveMap timeToLeaveMap = new TimeToLeaveMap(className.getName() + key, timeToLive, isUserData);
        RealmManager.update(realm, timeToLeaveMap);
        realm.close();
    }

    public static synchronized void save(GeneralResponse generalResponse) {
        if (generalResponse == null) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        //if have data
        if (generalResponse.getTransactionSuccess() != null) {
            //save data if labels or configurations
            Object transactionSuccess = generalResponse.getTransactionSuccess();

            if (transactionSuccess instanceof Labels) {
                try {
                    Labels success = (Labels) (generalResponse).getTransactionSuccess();
                    if (((Labels) transactionSuccess).getSystemProperties() != null) {
                        RealmManager.update(realm, success);
                        Log.d(TAG, success.getClass().getName());
                        saveTimeToLiveStatic(((Labels) transactionSuccess).getSystemProperties().getClass(),
                                generalResponse.getTimeToLive(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof Configurations) {
                try {
                    Configurations success = (Configurations) (generalResponse).getTransactionSuccess();
                    //onCostControlRequestCompleted Configurations
                    if (((Configurations) transactionSuccess).getSystemProperties() != null) {
                        RealmManager.update(realm, success);
                        Log.d(TAG, success.getClass().getName());
                        saveTimeToLiveStatic(((Configurations) transactionSuccess).getSystemProperties().getClass(),
                                generalResponse.getTimeToLive(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof UserProfile) {
                try {
                    RealmManager.update(realm, (UserProfile) generalResponse.getTransactionSuccess());
                    Log.d(TAG, transactionSuccess.getClass().getName());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof InitialToken) {
                try {
                    RealmManager.update(realm, (InitialToken) generalResponse.getTransactionSuccess());
                    Log.d(TAG, "Initial token successfully saved on realm");
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof CostControl) {
                try {
                    RealmManager.delete(realm, Balance.class);
                    RealmManager.update(realm, (CostControl) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof UserProfileHierarchy) {
                try {
                    //TODO clean the collections sorts
                    UserProfileHierarchy uph = (UserProfileHierarchy) generalResponse.getTransactionSuccess();
                    RealmManager.startTransaction();

                    if (!(VodafoneController.getInstance().getUser() instanceof CBUUser))
                        Collections.sort(uph.getBanList(), Ban.getBanComparator());

                    Log.d(TAG, "save transactionSuccess instanceof UserProfileHierarchy");
                    RealmManager.update(realm, (UserProfileHierarchy) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof BalanceCreditSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof BalanceCreditSuccess");
                    RealmManager.update(realm, (BalanceCreditSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof BalanceSecondarySuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof BalanceSecondarySuccess");
                    RealmManager.update(realm, (BalanceSecondarySuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof RecommendedRechargesSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof RecommendedRechargesSuccess");
                    RealmManager.update(realm, (RecommendedRechargesSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof Profile) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof Profile");
                    RealmManager.update(realm, (Profile) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof UnicaOffersSuccess) {
                try {
                    RealmManager.delete(UnicaOffer.class);
                    Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                    RealmManager.update(realm, (UnicaOffersSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof BillRechargesSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof BillRechargesSuccess");
                    RealmManager.update(realm, (BillRechargesSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof FavoriteNumbersSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                    RealmManager.update(realm, (FavoriteNumbersSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof RechargeHistorySuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof UnicaOffersSuccess");
                    RealmManager.update(realm, (RechargeHistorySuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof AncomPendingOffersSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof AncomPendingOffersSuccess");
                    RealmManager.update(realm, (AncomPendingOffersSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof BannerOffersSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof BannerOffersSuccess");
                    RealmManager.delete(BannerOffersSuccess.class);
                    RealmManager.update(realm, (BannerOffersSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof NotificationFlag) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof NotificationFlag");
                    RealmManager.delete(NotificationFlag.class);
                    RealmManager.update(realm, (NotificationFlag) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof SeamlessFlag) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof SeamlessFlag");
                    RealmManager.delete(SeamlessFlag.class);
                    RealmManager.update(realm, (SeamlessFlag) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof ShopLoginSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ShopLoginSuccess");
                    RealmManager.update(realm, (ShopLoginSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof ShopPricePlanSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ShopPricePlanSuccess");
                    RealmManager.update(realm, (ShopPricePlanSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof ActiveOffersPostpaidSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ActiveOffersPostpaidSuccess");
                    RealmManager.update(realm, (ActiveOffersPostpaidSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof ShopProductsSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ActiveOffersPostpaidSuccess");
                    RealmManager.update(realm, (ShopProductsSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof ShopEligibilitySuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ShopEligibilitySuccess");
                    RealmManager.update(realm, (ShopEligibilitySuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);

                } catch (Exception e) {
                    e.printStackTrace();
                }//ActiveOffersPostpaidSuccess
            } else if (transactionSuccess instanceof ShopLoyaltyProgramSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ShopLoyaltyProgramSuccess ");
                    RealmManager.update(realm, (ShopLoyaltyProgramSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (transactionSuccess instanceof ShopLoyaltyPointsSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof ShopLoyaltyPointsSuccess ");
                    RealmManager.update(realm, (ShopLoyaltyPointsSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof SystemStatusSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof SystemStatusSuccess ");
                    RealmManager.update(realm, (SystemStatusSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof EligibleOffersPostSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof EligibleOffersPostSuccess ");
                    RealmManager.update(realm, (SystemStatusSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof EligibleOffersSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof EligibleOffersSuccess ");
                    RealmManager.update(realm, (SystemStatusSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof TravallingHintSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof TravallingHintSuccess ");
                    RealmManager.update(realm, (TravallingHintSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof RoamingTariffsSuccess) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof RoamingTariffsSuccess ");
                    RealmManager.update(realm, (RoamingTariffsSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof InvoiceDetailsSuccess) {
                try {
                    Log.d(TAG, "save transaction intanceof InvoiceDetailsSuccess");
                    RealmManager.update(realm, (InvoiceDetailsSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof BanPost4preEligibilitySuccess) {
                try {
                    Log.d(TAG, "save transaction intanceof BanPost4preEligibilitySuccess ");
                    RealmManager.update(realm, (BanPost4preEligibilitySuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof AccessTypeSuccess) {
                try {
                    Log.d(TAG, "save transaction intanceof AccessTypeSuccess ");
                    RealmManager.update(realm, (AccessTypeSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof CreditInAdvanceSuccess) {
                try {
                    Log.d(TAG, "save transaction intanceof CreditInAdvanceSuccess");
                    RealmManager.update(realm, (CreditInAdvanceSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof GdprGetResponse) {

                try {
                    Log.d(TAG, "save transactionSucces instanceof GdprGetResponse");
                    RealmManager.update(realm, (GdprGetResponse) generalResponse.getTransactionSuccess());
                    long timeToLiveGdpr = generalResponse.getTimeToLive();
                    int key = ((GdprGetResponse) generalResponse.getTransactionSuccess()).getId_gdpr();
                    saveTimeToLiveAfterKey(transactionSuccess.getClass(), timeToLiveGdpr, true, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof SIMStatusEBUSuccess) {
                try {
                    Log.d(TAG, "save transaction intanceof SIMStatusEBUSuccess");
                    RealmManager.update(realm, (SIMStatusEBUSuccess) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof AccessTypeEBU) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof AccessTypeEBU");
                    RealmManager.update(realm, (AccessTypeEBU) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof CountryByIp) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof CountryByIp");
                    RealmManager.update(realm, (CountryByIp) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (transactionSuccess instanceof TvHierarchyResponse) {
                try {
                    Log.d(TAG, "save transactionSuccess instanceof TvHierarchyResponse");
                    RealmManager.update(realm, (TvHierarchyResponse) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (transactionSuccess instanceof RealmObject) {
                try {
                    Log.d(TAG, "save transaction intanceof " + transactionSuccess.getClass());
                    RealmManager.update(realm, (RealmObject) generalResponse.getTransactionSuccess());
                    saveTimeToLiveStatic(transactionSuccess.getClass(), generalResponse.getTimeToLive(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "transactionSuccess is NOT instance of any known cache bean");
            }
            realm.close();
        }
    }

    private void saveCountryByIP(CountryByIp response) {

        Realm realm = Realm.getDefaultInstance();
        try {
            Log.d(TAG, "save transactionSuccess instanceof CountryByIp");
            RealmManager.delete(realm, CountryByIp.class);
            RealmManager.update(realm, response);
            // saveTimeToLiveStatic(response.getClass(), r.getTimeToLive(), true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

}
