package ro.vodafone.mcare.android.rest.observables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.OnboardingActivity;
import ro.vodafone.mcare.android.ui.activities.SplashScreenActivity;
import ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Victor Radulescu on 4/14/2017.
 */

public class AutoLoginObservable {
    private static String TAG = "AutoLoginObservable";
    private static AutoLoginObservable instance;

    public static boolean passedLogin , passedProfile;

    private boolean isSubscriberHierarchyCalled = false;

    public static AutoLoginObservable getInstance() {
        if(instance==null){
            instance = new AutoLoginObservable();
        }
        return instance;
    }

    private AutoLoginObservable() {
    }

    public static  boolean  isInProceess=false;

    private static   long lastTimeWhenAutoLoginStarted = 0;

    private static int numberOfFailedRequests=0;
    private static final long longtimeToPassBeforeANewAutoLoginStarted = 60*1000;
    private static final long ebuShortTimeToPassBeforeANewAutoLoginStarted =500;
    private static final int NUMBER_OF_FAILED_REQUESTS_TO_ALLOW_AUTOLOGIN_BELOW_THEIR_NORMAL_TIME = 15;

    private boolean isKeepMeLoggedIn = false;

    public synchronized void start() {

        if(!isUserSessionActivity()){
            return;
        }
        numberOfFailedRequests++;
        if(!hasALongTimePassedFromLastAutoLogin() && !hasManyRequestsFailed()){
            return;
        }
        resetNumberOfFailedRequests();

        lastTimeWhenAutoLoginStarted = System.currentTimeMillis();

        Log.d(TAG,"Is starting before");
        AppConfiguration appConfiguration = VodafoneController.getInstance().getGeneralAppConfiguration();
        isKeepMeLoggedIn = appConfiguration == null || appConfiguration.isKeepMeLoggedIn();

        passedLogin= false;
        isInProceess = true;
        passedProfile= false;
        if(!isKeepMeLoggedIn){
            setLastTimeWhenAutoLoginStarted();
            showExpiredToast();
            if(VodafoneController.currentActivity()!=null && !(VodafoneController.currentActivity() instanceof DashboardActivity)) {
                VodafoneController.currentActivity().finish();
            }
            Activity dashboardActivity= VodafoneController.getInstance().getDashboardActivity();
            if(dashboardActivity!=null && !dashboardActivity.isFinishing()){
                Intent intent = new Intent(dashboardActivity, LoginActivity.class);
                dashboardActivity.startActivity(intent);
                dashboardActivity.finish();
            }
            return;
        }
        isInProceess = true;

        Log.d(TAG,"Is starting after");

        setLastTimeWhenAutoLoginStarted();
        Observable cbuAuthenticationCallsObservable = getLoginObservable().doOnNext(new Action1<GeneralResponse<? extends RealmObject>>() {
             @Override
             public void call(GeneralResponse<? extends RealmObject> userProfileGeneralResponse) {
                 if(userProfileGeneralResponse.getTransactionSuccess()==null || userProfileGeneralResponse.getTransactionStatus()!=0){
                     Log.d(TAG,"logout");
                     showExpiredToast();
                     new NavigationAction(VodafoneController.getInstance()).startAction(IntentActionName.LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA,true);
                     isInProceess =false;
                     return;
                 }
                 passedLogin = true;
                 Log.d(TAG,"login ok");
                 String password = VodafoneController.getInstance().getUserProfile().getPassword();
                 UserProfile userProfile = (UserProfile) RealmManager.getRealmObject(UserProfile.class);
                 if(password==null || password.isEmpty() || password.equals(" ")){
                     if(userProfile!=null){
                         password = userProfile.getPassword();
                         Log.d(TAG,"login  "+password);
                     }
                 }
                 BaseService.clearData();
                 Log.d(TAG,"login  "+password);
                 Log.d(TAG,"login ssoTokenId old  "+VodafoneController.getInstance().getUserProfile().getSsoTokenId());
                 RealmManager.startTransaction();
                 ((UserProfile) userProfileGeneralResponse.getTransactionSuccess()).setPassword(password);
                 RequestSaveRealmObserver.save( userProfileGeneralResponse);

                 VodafoneController.getInstance().setUser((UserProfile) userProfileGeneralResponse.getTransactionSuccess(),false);

                 GdprController.getPermissionsAfterLogin(false);

                 Log.d(TAG,"login ssoTokenId new  "+VodafoneController.getInstance().getUserProfile().getSsoTokenId());

             }
         }).doOnError(logOutErrorFunc).flatMap(new Func1<GeneralResponse<? extends RealmObject>, Observable<GeneralResponse<? extends RealmObject>>>() {
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
        }).flatMap(new Func1<GeneralResponse, Observable<GeneralResponse>>() {
            @Override
            public Observable<GeneralResponse> call(GeneralResponse generalResponse) {
                if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                    if (((UserProfile)RealmManager.getRealmObject(UserProfile.class)).getUserStatus().equalsIgnoreCase("active")) {
                        Log.d(TAG,"getUser  active getSubscriberHierarchy");

                        return getSubscriberHierarchy();
                    } else {
                        Log.d(TAG,"getUser not active getSubscriberHierarchy");
                    }
                }
                return (Observable) Observable.just(generalResponse).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        isInProceess =false;
                        Log.d(TAG,"getUser do not get subcribeHierachy");
                    }
                });

            }
        });
        //maybe TODO on ERROR and ONNext for api 19 call
       cbuAuthenticationCallsObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse>(false) {
            @Override
            public void onCompleted() {
                super.onCompleted();
                isSubscriberHierarchyCalled = false;

                new AuthenticationService(VodafoneController.getInstance()).getVoVs().subscribe(new RequestSessionObserver<VoiceOfVodafoneResponse>() {
                    @Override
                    public void onNext(VoiceOfVodafoneResponse voiceOfVodafoneResponse) {
                        if(VodafoneController.getInstance().getUser() != null)
                            VodafoneController.getInstance().setVoiceOfVodafoneResponse(voiceOfVodafoneResponse);

                        openLoginIdentitySelector();
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        openLoginIdentitySelector();
                    }
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,("AutoLoginObservable Vovs" ));

                    }
                });


            }
            @Override
             public void onError(Throwable e) {
                super.onError(e);
                isInProceess = false;

                if (isSubscriberHierarchyCalled && VodafoneController.getInstance().getUser() instanceof CBUUser) {
                    if (VodafoneController.getInstance() != null)
                        VodafoneController.getInstance().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
                                .putInt("errorDetectedFlag", DashboardController.API19_TIMEOUT_FLAG)
                                .apply();
                    VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
                }

                isSubscriberHierarchyCalled = false;
                Log.d(TAG,"AutoLoginObservable onError "+e.getMessage());
             }

             @Override
             public void onNext(GeneralResponse generalResponse) {
                 isInProceess = false;

                 boolean responseIsSuccessful = generalResponse != null
                         && generalResponse.getTransactionStatus() == 0
                         && generalResponse.getTransactionSuccess() != null;

                 if (responseIsSuccessful) {
                     Log.d(TAG,("AutoLoginObservable onNext "+(generalResponse.getTransactionSuccess() instanceof UserProfile?"userProfile da":"nu") ));
                     Log.d(TAG,("AutoLoginObservable onNext "+(generalResponse.getTransactionSuccess() instanceof UserProfileHierarchy?"UserProfileHierarchy da":"nu") ));
                     Log.d(TAG,("AutoLoginObservable onNext "+(generalResponse.getTransactionSuccess() instanceof Profile?"Profile da":"nu") ));
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

             }
         });
    }

    private void openLoginIdentitySelector()
    {
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            if(VodafoneController.currentActivity()!=null){
                Intent intent = new Intent(VodafoneController.currentActivity(),LoginIdentitySelectorActivity.class);
                VodafoneController.currentActivity().startActivity(intent);
                VodafoneController.clearAllMenuActivitys();
            }
        }
    }

    private Observable<GeneralResponse<UserProfile>> getLoginObservable() {
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        String username = null;
        String password = null;
        if(userProfile!=null){
            username = userProfile.getUserName();
            password = userProfile.getPassword();
            Log.d(TAG,"AutoLoginObservable singleton: username "+username+" password "+password );
        }
        if(password==null || password.isEmpty() || password.contains(" ")) {
            userProfile = ((UserProfile) RealmManager.getRealmObject(UserProfile.class));
            if (userProfile != null) {
                username = userProfile.getUserName();
                password = userProfile.getPassword();
                Log.d(TAG,"AutoLoginObservable realm: username " + username + " password " + password);
            }
        }
        AuthenticationService authenticationService = new AuthenticationService(VodafoneController.getInstance());
        return authenticationService.login(username, password);
    }


    private Observable getSubscriberHierarchy() {
        isSubscriberHierarchyCalled = true;
        return new UserDataService(VodafoneController.getInstance()).getSubscriberHierarchy(true);
    }


    private boolean isInconsistentProfile(UserProfile userProfile){
        boolean isInconsistent = false;

        if(userProfile != null){
            if((userProfile.getCid() == null || userProfile.getCid().equals(""))  && (userProfile.getSid() == null || userProfile.getSid().equals("")) &&
                    (userProfile.getUserRoleString().equals(UserRole.PREPAID.getDescription()) || userProfile.getUserRoleString().equals(UserRole.RES_CORP.getDescription())
                            || userProfile.getUserRoleString().equals(UserRole.RES_SUB.getDescription()) || userProfile.getUserRoleString().equals(UserRole.PRIVATE_USER.getDescription()))){
                isInconsistent = true;
            }
        }

        return isInconsistent;
    }

    private Observable getUserProfile(){
        return  new UserDataService(VodafoneController.getInstance()).getUserProfile(true);
    }

    public synchronized  void setLastTimeWhenAutoLoginStarted() {
        lastTimeWhenAutoLoginStarted = System.currentTimeMillis();
    }

    public boolean isKeepMeLoggedIn() {
        return isKeepMeLoggedIn;
    }

    public synchronized  boolean hasALongTimePassedFromLastAutoLogin(){
        return System.currentTimeMillis()-lastTimeWhenAutoLoginStarted > ((VodafoneController.getInstance().getUser() instanceof EbuMigrated)?ebuShortTimeToPassBeforeANewAutoLoginStarted:longtimeToPassBeforeANewAutoLoginStarted);
    }


    public synchronized static boolean hasManyRequestsFailed() {
        return numberOfFailedRequests >= NUMBER_OF_FAILED_REQUESTS_TO_ALLOW_AUTOLOGIN_BELOW_THEIR_NORMAL_TIME;
    }

    public static void resetNumberOfFailedRequests() {
        numberOfFailedRequests = 0;
    }

    public synchronized static boolean isUserSessionActivity(){
        Activity activity = VodafoneController.currentActivity();
        if(activity==null){
            return false;
        }else {
            if(activity instanceof LoginActivity || activity instanceof SplashScreenActivity || activity instanceof OnboardingActivity){
                return false;
            }
        }
        return true;
    }
    Action1<Throwable> logOutErrorFunc= new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            isInProceess =false;
            showExpiredToast();
            new NavigationAction(VodafoneController.getInstance()).startAction(IntentActionName.LOGOUT_JUST_REDIRECT_AND_CLEA_USERDATA,true);
            Log.d(TAG,"AutoLoginObservable onError "+throwable.getMessage());
        }
    };
    private void showExpiredToast(){

        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                Context context = VodafoneController.currentActivity()!=null ? VodafoneController.currentActivity():VodafoneController.getInstance();
                if(context!=null){
                    new CustomToast.Builder(context).message("Sesiunea a expirat").success(false).show();
                    //Toast.makeText(context, "Sesiunea a expirat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
