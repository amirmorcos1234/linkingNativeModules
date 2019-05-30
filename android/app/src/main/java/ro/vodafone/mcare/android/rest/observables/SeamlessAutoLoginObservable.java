package ro.vodafone.mcare.android.rest.observables;

import android.content.Context;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.seamless.InitialToken;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 ** Created by Victor Radulescu on 8/2/2017.
 ** AutoLogin Observable builder for seamless
 */

public class SeamlessAutoLoginObservable {

    private static SeamlessAutoLoginObservable instance;

    boolean passedLogin , passedProfile;

    private boolean isSubscriberHierarchyCalled = false;

    public static SeamlessAutoLoginObservable getInstance() {
        if(instance==null){
            instance = new SeamlessAutoLoginObservable();
        }
        return instance;
    }

    private SeamlessAutoLoginObservable() {
    }

    public static boolean isInProceess=false;

    private static long lastTimeWhenAutoLoginStarted = 0;

    private static final long timeToPassBeforeANewAutoLoginStarted = 3000;

    private static int numberOfFailedRequests=0;
    private static final long longtimeToPassBeforeANewAutoLoginStarted = 60*1000;
    private static final int NUMBER_OF_FAILED_REQUESTS_TO_ALLOW_AUTOLOGIN_BELOW_THEIR_NORMAL_TIME = 10;

    public synchronized void start() {

        numberOfFailedRequests++;
        if(!hasALongTimePassedFromLastAutoLogin() && !hasManyRequestsFailed()){
            return;
        }
        resetNumberOfFailedRequests();

        isInProceess = true;

        D.d("Is starting after");

        setLastTimeWhenAutoLoginStarted();
        Observable seamlessObservable = getInitialToken().flatMap(funcInitialTokenReturnAddHeader).flatMap(funcAddHeaderReturnSeamlessLogin);

        seamlessObservable.flatMap(funcUserProfileReturnProfileIfActive).flatMap(new Func1<GeneralResponse, Observable<GeneralResponse>>() {
            @Override
            public Observable<GeneralResponse> call(GeneralResponse generalResponse) {
                if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
                    if(((UserProfile)RealmManager.getRealmObject(UserProfile.class)).getUserStatus().equalsIgnoreCase("active")){
                        D.d("getUser  active getSubscriberHierarchy");
                        return getSubscriberHierarchy();
                    }else{
                        D.d("getUser not active getSubscriberHierarchy");
                    }
                }
                return (Observable) Observable.just(generalResponse).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        isInProceess =false;
                        D.d("getUser do not get subcribeHierachy");
                    }
                });
            }
        }).subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {

            @Override
            public void onCompleted() {
                isSubscriberHierarchyCalled = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isInProceess =false;

                if (isSubscriberHierarchyCalled && VodafoneController.getInstance().getUser() instanceof CBUUser) {
                    if (VodafoneController.getInstance() != null)
                        VodafoneController.getInstance().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                .putInt("errorDetectedFlag", DashboardController.API19_TIMEOUT_FLAG)
                                .apply();
                    VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
                }

                isSubscriberHierarchyCalled = false;

                D.d("AutoLoginObservable onError "+e.getMessage());
            }

            @Override
            public void onNext(GeneralResponse generalResponse) {
                isInProceess = false;

                boolean responseIsSuccessful = generalResponse != null
                        && generalResponse.getTransactionStatus() == 0
                        && generalResponse.getTransactionSuccess() != null;

                if (responseIsSuccessful) {
                    D.d(("AutoLoginObservable onNext " + (generalResponse.getTransactionSuccess() instanceof UserProfile ? "userProfile da" : "nu")));
                    D.d(("AutoLoginObservable onNext " + (generalResponse.getTransactionSuccess() instanceof UserProfileHierarchy ? "UserProfileHierarchy da" : "nu")));
                    D.d(("AutoLoginObservable onNext " + (generalResponse.getTransactionSuccess() instanceof Profile ? "Profile da" : "nu")));

                    super.onNext(generalResponse);

                    if (generalResponse.getTransactionSuccess() instanceof UserProfileHierarchy && VodafoneController.getInstance().getUser() instanceof CBUUser) {
                        UserProfileHierarchy hierarchy = (UserProfileHierarchy) generalResponse.getTransactionSuccess();

                        if (VodafoneController.getInstance() != null)
                            VodafoneController.getInstance().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                    .putInt("errorDetectedFlag", hierarchy.getErrorDetectedFlag()).apply();

                        if (hierarchy.ifApi19CallFailed())
                            VoiceOfVodafoneController.getInstance().createApi19FailedVov();
                        else if (hierarchy.ifApi19CallTimeout())
                            VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
                    }
                    return;
                }

                if (isSubscriberHierarchyCalled && VodafoneController.getInstance().getUser() instanceof CBUUser) {
                    if (VodafoneController.getInstance() != null)
                        VodafoneController.getInstance().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                .putInt("errorDetectedFlag", DashboardController.API19_ERROR_FLAG).apply();
                    VoiceOfVodafoneController.getInstance().createApi19FailedVov();
                }

                   /*if(passedLogin){
                        return;
                    }
                    new NavigationAction(VodafoneController.getInstance()).startAction(IntentActionName.LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA,true);*/
            }
        });

    }

    private synchronized Observable getSubscriberHierarchy() {
        isSubscriberHierarchyCalled = true;
        return new UserDataService(VodafoneController.getInstance()).getSubscriberHierarchy(true);
    }

    private  synchronized Observable getUserProfile(){
        return  new UserDataService(VodafoneController.getInstance()).getUserProfile(true);
    }

    private synchronized  void setLastTimeWhenAutoLoginStarted() {
        lastTimeWhenAutoLoginStarted = System.currentTimeMillis();
    }
    private synchronized  boolean hasTimePassedFromLastAutoLogin(){
        return true;
        //return System.currentTimeMillis()-lastTimeWhenAutoLoginStarted > timeToPassBeforeANewAutoLoginStarted;
    }
    public synchronized  boolean hasALongTimePassedFromLastAutoLogin(){
        return System.currentTimeMillis()-lastTimeWhenAutoLoginStarted > longtimeToPassBeforeANewAutoLoginStarted;

    }
    private synchronized Observable<GeneralResponse<InitialToken>> getInitialToken(){
        return new AuthenticationService(VodafoneController.getInstance()).retrieveInitialToken().doOnError(logOutErrorAction);
    }
    private synchronized Observable<GeneralResponse> addSeamlessHeader(String initialToken) {

        //D.d( "Enter addSeamlessHeader() for previousSeamlessMsisdn" + previousMsisdn);
        String previousMsisdn=null;
        if( VodafoneController.getInstance().getUserProfile()!=null){
            previousMsisdn =VodafoneController.getInstance().getUserProfile().getMsisdn();
        }
        AuthenticationService authenticationService = new AuthenticationService(VodafoneController.getInstance());

       // D.d("Call authenticationService.seamlessAddHeader for seamlessMsisdn" + previousMsisdn);
        return authenticationService.seamlessAddHeader(initialToken, NetworkUtils.isIOYM(VodafoneController.getInstance())? null:previousMsisdn)
                .doOnError(logOutErrorAction);
    }

    private synchronized Observable<GeneralResponse<UserProfile>> getSeamlessLogin(){
        InitialToken initialToken = (InitialToken) RealmManager.getRealmObject(InitialToken.class);
        if(initialToken==null){
            return null;
        }
        String initialTokenString = initialToken.getInitialToken();
       return new AuthenticationService(VodafoneController.getInstance()).retrieveSeamlessUserProfile(initialTokenString).doOnError(logOutErrorAction);
    }
    private Func1 funcInitialTokenReturnAddHeader = new Func1<GeneralResponse<InitialToken>, Observable<GeneralResponse>>(){

        @Override
        public Observable<GeneralResponse> call(GeneralResponse<InitialToken> generalResponse) {
            if(validGeneralResponse(generalResponse)){
                RequestSaveRealmObserver.save(generalResponse);
                String initialToken = generalResponse.getTransactionSuccess().getInitialToken();
                D.w("Initial Token: "+initialToken);
                return addSeamlessHeader(initialToken).doOnError(logOutErrorAction);
            }
            logout();
            return null;
        }
    };
    private Func1 funcAddHeaderReturnSeamlessLogin = new Func1<GeneralResponse, Observable<GeneralResponse<UserProfile>>>(){

        @Override
        public Observable<GeneralResponse<UserProfile>> call(GeneralResponse generalResponse) {
            if(generalResponse!=null){
                int status = generalResponse.getTransactionStatus();
                if(status==0){
                    Observable loginObs = getSeamlessLogin();
                    if(loginObs!=null){
                        return loginObs.doOnNext(doOnNextLogin).doOnError(logOutErrorAction);
                    }
                }
            }
            logout();
            return null;
        }
    };
    private Action1<Throwable> logOutErrorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            isInProceess =false;
            new NavigationAction(VodafoneController.getInstance()).startAction(IntentActionName.LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA,true);
            D.d("AutoLoginObservable onError "+throwable.getMessage());
            new CustomToast.Builder(VodafoneController.getInstance()).message("Sesiunea a expirat").success(false).show();
            //Toast.makeText(VodafoneController.getInstance(), "Sesiunea a expirat", Toast.LENGTH_SHORT).show();
        }
    };


    private boolean validGeneralResponse(GeneralResponse<? extends RealmObject> generalResponse){
        return generalResponse != null && generalResponse.getTransactionSuccess() != null && generalResponse.getTransactionStatus()==0;
    }
    public synchronized void logout(){
        isInProceess =false;
        new NavigationAction(VodafoneController.getInstance()).startAction(IntentActionName.LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA,true);
        showExpiredToast();
    }
    private void showExpiredToast(){
        Context context = VodafoneController.currentActivity()!=null ? VodafoneController.currentActivity():VodafoneController.getInstance();
        if(context!=null){
            new CustomToast.Builder(context).message("Sesiunea a expirat").success(false).show();
            //Toast.makeText(context, "Sesiunea a expirat", Toast.LENGTH_SHORT).show();
        }
    }
    private Action1<GeneralResponse<UserProfile>> doOnNextLogin = new Action1<GeneralResponse<UserProfile>>() {
        @Override
        public void call(GeneralResponse<UserProfile> userProfileGeneralResponse) {
            if(!validGeneralResponse(userProfileGeneralResponse)){
                D.d("logout");
                logout();
                return;
            }
            passedLogin = true;
            BaseService.clearData();
            RequestSaveRealmObserver.save( userProfileGeneralResponse);

            VodafoneController.getInstance().setUser(userProfileGeneralResponse.getTransactionSuccess(),true);
        }
    };
    Func1<GeneralResponse<? extends RealmObject>, Observable<GeneralResponse<? extends RealmObject>>> funcUserProfileReturnProfileIfActive = new Func1<GeneralResponse<? extends RealmObject>, Observable<GeneralResponse<? extends RealmObject>>>() {
        @Override
        public Observable<GeneralResponse<? extends RealmObject>> call( GeneralResponse<? extends RealmObject> userProfileGeneralResponse) {
            if(userProfileGeneralResponse!=null && userProfileGeneralResponse.getTransactionSuccess()!=null ){
                if(userProfileGeneralResponse.getTransactionSuccess() instanceof UserProfile
                        &&
                        ((UserProfile)  userProfileGeneralResponse.getTransactionSuccess()).getUserStatus().equalsIgnoreCase("active")){
                    return getUserProfile().doOnNext(new Action1<GeneralResponse<? extends RealmObject>>() {
                        @Override
                        public void call(GeneralResponse<? extends RealmObject> generalResponse) {
                            if(generalResponse.getTransactionSuccess()==null){
                                D.d("logout ok  getUserProfile failed");
                                isInProceess =false;
                            }
                            passedProfile = true;
                            RequestSaveRealmObserver.save(generalResponse);
                        }
                    });
                }
            }
            return (Observable) Observable.just(userProfileGeneralResponse);
        }
    };


    public synchronized static boolean hasManyRequestsFailed() {
        return numberOfFailedRequests >= NUMBER_OF_FAILED_REQUESTS_TO_ALLOW_AUTOLOGIN_BELOW_THEIR_NORMAL_TIME;
    }

    public static void resetNumberOfFailedRequests() {
        numberOfFailedRequests = 0;
    }
}
