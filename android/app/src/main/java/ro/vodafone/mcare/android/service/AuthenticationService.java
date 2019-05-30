package ro.vodafone.mcare.android.service;

import android.content.Context;

import java.util.List;

import retrofit2.Response;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.seamless.InitialToken;
import ro.vodafone.mcare.android.client.model.recover.RecoverPasswordCorrectResponse;
import ro.vodafone.mcare.android.client.model.recover.RecoverUsernameResponse;
import ro.vodafone.mcare.android.client.model.register.AccountCheck;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneRestPojo;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.rest.requests.BaseRequest;
import ro.vodafone.mcare.android.rest.requests.CompleteProfileRequest;
import ro.vodafone.mcare.android.rest.requests.ConfirmProfileRequest;
import ro.vodafone.mcare.android.rest.requests.EventReportRequest;
import ro.vodafone.mcare.android.rest.requests.LoginRequest;
import ro.vodafone.mcare.android.rest.requests.LogoutRequest;
import ro.vodafone.mcare.android.rest.requests.PhoneCheckRequest;
import ro.vodafone.mcare.android.rest.requests.RecoverPasswordRequest;
import ro.vodafone.mcare.android.rest.requests.RecoverUsernameRequest;
import ro.vodafone.mcare.android.rest.requests.ResendCodeRequest;
import ro.vodafone.mcare.android.rest.requests.ResetPasswordRequest;
import ro.vodafone.mcare.android.rest.requests.SeamlessAddHeaderRequest;
import ro.vodafone.mcare.android.rest.requests.SeamlessFlagRequest;
import ro.vodafone.mcare.android.rest.requests.SeamlessLoginRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterActivateRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterCreateRequest;
import ro.vodafone.mcare.android.rest.requests.SelfRegisterResendRequest;
import ro.vodafone.mcare.android.ui.fragments.login.seamless.SeamlessFlag;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 12/5/2016.
 */

public class AuthenticationService extends BaseService {
    public static String TAG = "AuthenticationService";

    public AuthenticationService(Context context) {
        super(context);
    }

    public Observable<GeneralResponse<UserProfile>> login(String username, String password) {

        LoginRequest loginRequest = new LoginRequest(username, password);

        Observable<Response<GeneralResponse<UserProfile>>> responseObservable =
                RetrofitCall.
                        getInstance().
                        retrieveUserProfile(loginRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        Observable observable = responseObservable.flatMap(new Func1<Response<GeneralResponse<UserProfile>>, Observable<?>>() {
            @Override
            public Observable<?> call(Response<GeneralResponse<UserProfile>> generalResponseResponse) {
                if (generalResponseResponse != null) {

                    List<String> cookie = generalResponseResponse.headers().toMultimap().get("set-cookie");

                    if (cookie != null)
                        BaseService.setCookieAfterLogin(cookie);

                    return Observable.just(generalResponseResponse.body());
                }
                return null;
            }
        });
        return observable;
    }

    public Observable<GeneralResponse> logout() {
        UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);

        String ssoTokenId = userProfile != null ? userProfile.getSsoTokenId() : "";
        LogoutRequest logoutRequest = new LogoutRequest(ssoTokenId);

        String cookie = createCookieStatic(context);

        //TODO get ssoTokenId
        Observable<GeneralResponse> observable =
                RetrofitCall.
                        getInstance().
                        logout( logoutRequest).
                        subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


    public Observable<GeneralResponse<InitialToken>> retrieveInitialToken() {
        BaseRequest baseRequest = new BaseRequest();

        Observable<GeneralResponse<InitialToken>> observable =
                RetrofitCall.getInstance().retrieveInitialToken(baseRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> seamlessAddHeader(String initialToken, String previousSeamlessMsisdn) {

        SeamlessAddHeaderRequest seamlessAddHeaderRequest = new SeamlessAddHeaderRequest(initialToken);
        Observable<GeneralResponse> observable;

        if (previousSeamlessMsisdn != null || NetworkUtils.isDebugEnvironment()) {
            //call direct API url with cached previousSeamlessMsisdn
            observable = RetrofitCall.getInstance().seamlessAddHeader(seamlessAddHeaderRequest, previousSeamlessMsisdn)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        } else {
            //call Header Enrichment url without previousSeamlessMsisdn, seamlessMsisdn is added by Network
            observable = RetrofitCall.getInstance().seamlessAddHeader(seamlessAddHeaderRequest)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        }
        return observable;
    }


    public Observable<GeneralResponse<UserProfile>> retrieveSeamlessUserProfile(String initialToken) {

        SeamlessLoginRequest seamlessLoginRequest = new SeamlessLoginRequest(initialToken);

        Observable<Response<GeneralResponse<UserProfile>>> responseObservable =
                RetrofitCall.getInstance().retrieveSeamlessUserProfile(seamlessLoginRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable observable = responseObservable.flatMap(new Func1<Response<GeneralResponse<UserProfile>>, Observable<?>>() {
            @Override
            public Observable<?> call(Response<GeneralResponse<UserProfile>> generalResponseResponse) {
                if (generalResponseResponse != null) {
                    List<String> cookie = generalResponseResponse.headers().toMultimap().get("set-cookie");

                    if (cookie != null)
                        BaseService.setCookieAfterLogin(cookie);

                    return Observable.just(generalResponseResponse.body());
                }
                return null;
            }
        });


                /*new Func1<Response<GeneralResponse<UserProfile>>, Observable<?>>() {
            @Override
            public Observable<?> call(Response<GeneralResponse<UserProfile>> generalResponseResponse) {
                if(generalResponseResponse!=null){
                    String cookie = generalResponseResponse.headers().get("Cookie");
                    BaseService.setCookieAfterLogin(cookie);
                    return Observable.just(generalResponseResponse.body());
                }
                return null;
            }
        });*/

        return observable;
    }

    /**
     * May be changed. ActionType may be implemented as parameter or retrive from somewhere else ( from database- realm)
     *
     * @param reffererId what usser have done ( login, logout...)
     * @return
     */
    public Observable<GeneralResponse> sendSeamlessEvent(String reffererId) {
        String networkType = NetworkUtils.checkNetworkType(context).toString();
        long actionDate = System.currentTimeMillis();
        //TODO need to be implemented
        String actionType = "login";

        EventReportRequest eventReportRequest = new EventReportRequest(networkType, actionDate, reffererId, actionType);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().sendSeamlessEvent(eventReportRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> setSeamlessFlag(boolean allowSeamless) {
        D.w("set Seamless Flag = " + allowSeamless);
        SeamlessFlagRequest seamlessFlagRequest = new SeamlessFlagRequest(allowSeamless);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().setSeamlessFlag(seamlessFlagRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse<SeamlessFlag>> getSeamlessFlag() {
//        D.w();
        Observable<GeneralResponse<SeamlessFlag>> observable =
                RetrofitCall.getInstance().getSeamlessFlag()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        return observable;
    }

    public Observable<GeneralResponse> registerAccount(SelfRegisterCreateRequest selfRegisterCreateRequest) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().registerAccount(selfRegisterCreateRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> activateUserAccount(String username, String email, String activationCode, String password, String confirmationPassword) {

        SelfRegisterActivateRequest selfRegisterActivateRequest = new SelfRegisterActivateRequest(username, email, activationCode, password, confirmationPassword);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().activateUserAccount(selfRegisterActivateRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> activateUserAccount(String username, String email, String activationCode, String password, String confirmationPassword,Boolean isMigrated) {

        SelfRegisterActivateRequest selfRegisterActivateRequest = new SelfRegisterActivateRequest(username, email, activationCode, password, confirmationPassword);
        selfRegisterActivateRequest.setIsMigrated(isMigrated);
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().activateUserAccount(selfRegisterActivateRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> resendActivationCode(String username, String phoneNumber, String email, String customerType) {

        SelfRegisterResendRequest selfRegisterResendRequest = new SelfRegisterResendRequest(username, phoneNumber, email, customerType);

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().resendActivationCode(selfRegisterResendRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


//    public Observable<GeneralResponse> recoverUsername(String phoneNumber, String email) {
//
//        RecoverUsernameRequest recoverUsernameRequest = new RecoverUsernameRequest(phoneNumber, email);
//
//        Observable<GeneralResponse> observable =
//                RetrofitCall.getInstance().recoverUsername(recoverUsernameRequest)
//                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
//
//        return observable;
//    }

    public Observable<GeneralResponse<RecoverPasswordCorrectResponse>> recoverPassword(RecoverPasswordRequest recoverPasswordResponse) {

        Observable<GeneralResponse<RecoverPasswordCorrectResponse>> observable =
                RetrofitCall.getInstance().recoverPassword(recoverPasswordResponse)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


    public Observable<GeneralResponse<AccountCheck>> registerAccountCheck(String phoneNumber) {

        PhoneCheckRequest accountCheckRequest = new PhoneCheckRequest(phoneNumber);


        Observable<GeneralResponse<AccountCheck>> observable =
                RetrofitCall.
                        getInstance().
                        registerAccountCheck(accountCheckRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse> confirmProfile(String phoneNumber, String email) {

        ConfirmProfileRequest confirmProfileRequest = new ConfirmProfileRequest(phoneNumber, email);
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().confirmProfile(confirmProfileRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> completeProfile(String email, String password, String confirmPassword, boolean acceptTerm) {

        CompleteProfileRequest completeProfileRequest = new CompleteProfileRequest(email, password, confirmPassword, acceptTerm);


        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().completeProfile(completeProfileRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> activateAccount(String username, String email, String activationCode, String password, String confirmPassword, boolean isMigrated) {

        SelfRegisterActivateRequest selfRegisterActivateRequest= new SelfRegisterActivateRequest(username, email, activationCode, password, confirmPassword, isMigrated);

        Observable<GeneralResponse> observable =
                RetrofitCall.
                        getInstance().
                        activateAccount(selfRegisterActivateRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<GeneralResponse> resendCode(String username, String phoneNumber, String email, String customerType) {

        ResendCodeRequest resendCodeRequest = new ResendCodeRequest(username, email, phoneNumber, customerType);

        Observable<GeneralResponse> observable =
                RetrofitCall.
                        getInstance().
                        resendCode(resendCodeRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public Observable<GeneralResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {

        Observable<GeneralResponse> observable =
                RetrofitCall.
                        getInstance().
                        resetPassword(resetPasswordRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }


    public Observable<GeneralResponse<RecoverUsernameResponse>> recoverUsername(RecoverUsernameRequest recoverUsernameRequest) {

        Observable<GeneralResponse<RecoverUsernameResponse>> observable =
                RetrofitCall.
                        getInstance().
                        recoverUsername(recoverUsernameRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<VoiceOfVodafoneResponse> getVoVs(){

        Observable<VoiceOfVodafoneResponse> observable =
                RetrofitCall.getInstance().retrieveVoVs()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return  observable;
    }


}
