package ro.vodafone.mcare.android.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeoutException;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneRestPojo;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.fragments.settings.PermissionsFragment;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;
import rx.Subscription;

/**
 * Created by Bivol Pavel on 30.11.2017.
 */

public class LoginService {

	private Context context;
	private LoginResultInterface resultInterface;

	private boolean userProfileRequestCompleted = false;
	private boolean userProfileHierarchyRequestCompleted = false;
	private boolean vovRequestCompleted = false;

	public LoginService(LoginResultInterface resultInterface) {
		this.resultInterface = resultInterface;
		this.context = resultInterface.getContext();
	}

	public void performLogin(String ussername, final String password, final boolean isRememberMe) {
		resultInterface.showLoading();

		if (context == null) {
			return;
		}

		AuthenticationService authenticationService = new AuthenticationService(context);
		Subscription loginSubcription = authenticationService.login(ussername, password)
				.subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfile>>(false) {

					@Override
					public void onNext(GeneralResponse<UserProfile> response) {
						super.onNext(response);

						if (response.getTransactionSuccess() != null) {

							UserProfile userProfile = response.getTransactionSuccess();
							CredentialUtils.saveUsername(userProfile.getUserName());
							userProfile.setPassword(password);//  WE NEED THE PASSWORD INSIDE AP : EX RESET PASSWORD FLOW
							RealmManager.update(userProfile);

							setIsRememberMe(isRememberMe);
							setUserProfileOnController(userProfile);

							if (isPendingUser(userProfile)) {
								resultInterface.showCompleteProfile();
								resultInterface.stopLoading();
							} else if (isInconsistentProfile(userProfile)) {
								resultInterface.showConfirmProfile();
								resultInterface.stopLoading();
							} else {
								getUserData();
							}

							getVoVs();

						} else {

							String errorCode = response.getTransactionFault().getFaultCode();

							resultInterface.stopLoading();
							resultInterface.treatErrorCode(response, errorCode);

							clearUserData();
						}
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);

						try {
							resultInterface.stopLoading();
							resultInterface.showRequestFailedMessage(e, LoginLabels.getLoginFailedApiCall2(), "");

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

				});

		((BaseActivity) context).addToActivityCompositeSubcription(loginSubcription);
	}

	private void getUserData() {
		if (VodafoneController.getInstance().getUser() == null) {
			return;
		}
		getUserProfile();

		if (GdprController.shoudPerformGetPermissionsForCBUUserAndPrepaid())
			GdprController.getPermissionsAfterLogin(false);

		if (shouldGetUserProfileHierarchy(VodafoneController.getInstance().getUser())) {
			getSubscriberHierarchy();
		} else {
			userProfileHierarchyRequestCompleted = true;
			enterDashboard();
		}

	}

	private void getSubscriberHierarchy() {
		UserDataService userDataService = new UserDataService(context);
		userDataService.getSubscriberHierarchy(true)
				.subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>() {

					@Override
					public void onCompleted() {
						userProfileHierarchyRequestCompleted = true;
						enterDashboard();
					}

					@Override
					public void onNext(GeneralResponse<UserProfileHierarchy> response) {
						boolean responseIsSuccessful = response != null
								&& response.getTransactionStatus() == 0
								&& response.getTransactionSuccess() != null;

						if (context != null)
							context.getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
									.putInt("errorDetectedFlag", responseIsSuccessful
											? response.getTransactionSuccess().getErrorDetectedFlag()
											: DashboardController.API19_ERROR_FLAG).apply();

						if (responseIsSuccessful) {
							super.onNext(response);
							if (response.getTransactionSuccess().ifApi19CallFailed())
								VoiceOfVodafoneController.getInstance().createApi19FailedVov();
							else if (response.getTransactionSuccess().ifApi19CallTimeout())
								VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
							return;
						}

						VoiceOfVodafoneController.getInstance().createApi19FailedVov();
						resultInterface.showRequestFailedMessage(null, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-19");
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						userProfileHierarchyRequestCompleted = true;
						resultInterface.showRequestFailedMessage(e, AppLabels.getToastErrorSomeInfoNotLoaded(), "");
						if (context != null)
							context.getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
									.putInt("errorDetectedFlag", DashboardController.API19_TIMEOUT_FLAG)
									.apply();
						VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
						enterDashboard();
					}
				});
	}

	private void getVoVs() {

		new AuthenticationService(resultInterface.getContext()).getVoVs().subscribe(new RequestSessionObserver<VoiceOfVodafoneResponse>() {

			@Override
			public void onNext(VoiceOfVodafoneResponse voiceOfVodafoneResponse) {
				if (VodafoneController.getInstance().getUser() != null)
					VodafoneController.getInstance().setVoiceOfVodafoneResponse(voiceOfVodafoneResponse);

				vovRequestCompleted = true;
				enterDashboard();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				vovRequestCompleted = true;
				enterDashboard();
			}

			@Override
			public void onCompleted() {
				D.d(("LoginService Vovs"));
			}

		});
	}

	private void getUserProfile() {

		new UserDataService(resultInterface.getContext()).getUserProfile(true)
				.subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
					@Override
					public void onCompleted() {
						userProfileRequestCompleted = true;


						if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {

							if (resultInterface == null || resultInterface.getContext() == null) {
								return;
							}

							resultInterface.stopLoading();
							resultInterface.showIdentitySelector();
							return;
						}

						enterDashboard();
					}

					@Override
					public void onNext(GeneralResponse<Profile> response) {
						if (resultInterface == null || resultInterface.getContext() == null) {
							return;
						}
						if (response.getTransactionSuccess() != null) {
							super.onNext(response);
						} else {
							resultInterface.showRequestFailedMessage(null, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-10");
							VodafoneController.setApi10Failed(true);
						}
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						userProfileRequestCompleted = true;

						if (resultInterface == null || resultInterface.getContext() == null) {
							return;
						}
						VodafoneController.setApi10Failed(true);
						resultInterface.showRequestFailedMessage(e, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-10");
						if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
							resultInterface.stopLoading();
							resultInterface.showIdentitySelector();
						} else {
							enterDashboard();
						}
					}
				});
	}

	//TODO code cleanup -  what if permissions are not received? Any importance?
	private synchronized void enterDashboard() {
		if (!userProfileHierarchyRequestCompleted || !userProfileRequestCompleted || !vovRequestCompleted) {
			return;
		}

		resultInterface.enterDashboard();
//        resultInterface.stopLoading();
	}

	private boolean shouldGetUserProfileHierarchy(User user) {
		return user != null && (user instanceof CBUUser || user instanceof EbuNonMigrated);
	}

	private void setIsRememberMe(boolean isRememberMe) {
		if (isRememberMe) {
			VodafoneController.getInstance().getAppConfiguration().setIsKeepMeLoggedIn(true);
			VodafoneController.getInstance().getGeneralAppConfiguration().setIsKeepMeLoggedIn(true);
		} else {
			VodafoneController.getInstance().getAppConfiguration().setIsKeepMeLoggedIn(false);
			VodafoneController.getInstance().getGeneralAppConfiguration().setIsKeepMeLoggedIn(false);
		}
	}

	private void setUserProfileOnController(UserProfile userProfile) {
		VodafoneController.getInstance().setUser(userProfile, false);
	}

	private boolean isPendingUser(UserProfile userProfile) {
		return userProfile.getUserStatus() != null && userProfile.getUserStatus().equals("pending");
	}

	private boolean isInconsistentProfile(UserProfile userProfile) {
		boolean isInconsistent = false;

		if (userProfile != null) {
			if ((userProfile.getCid() == null || userProfile.getCid().equals("")) && (userProfile.getSid() == null
					|| userProfile.getSid().equals("")) && (userProfile.getUserRoleString().equals(UserRole.PREPAID.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.RES_CORP.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.RES_SUB.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.PRIVATE_USER.getDescription()))) {
				isInconsistent = true;
			}
			if ((userProfile.getCid() != null) && (userProfile.getSid() == null || userProfile.getSid().equals(""))
					&& (userProfile.getUserRoleString().equals(UserRole.PREPAID.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.RES_CORP.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.RES_SUB.getDescription())
					|| userProfile.getUserRoleString().equals(UserRole.PRIVATE_USER.getDescription()))) {
				isInconsistent = true;
			}

			if (userProfile.getUserStatus() != null && userProfile.getUserStatus().equals("inconsistent")) {
				isInconsistent = true;
			}
		}

		return isInconsistent;
	}

	private void clearUserData() {
		UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT).startAction();
	}

	public interface LoginResultInterface {

		void enterDashboard();

		void showCompleteProfile();

		void showConfirmProfile();

		void showIdentitySelector();

		void treatErrorCode(GeneralResponse response, String errorCode);

		void showRequestFailedMessage(Throwable e, String toastMessage, String apiKey);

		void showLoading();

		void stopLoading();

		Context getContext();

	}
}
