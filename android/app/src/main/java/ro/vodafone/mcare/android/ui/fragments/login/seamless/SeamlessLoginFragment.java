package ro.vodafone.mcare.android.ui.fragments.login.seamless;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.actions.LoginAction;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.Hosts;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.seamless.InitialToken;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SeamlessLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneResponse;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.exceptions.AuthenticationServiceException;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;
import rx.Observer;

public class SeamlessLoginFragment extends BaseFragment {

	public static String TAG = "SeamlessLoginFragment";

	private VodafoneButton seamlessLoginButton;
	private VodafoneButton authentificationButton;

	private LinearLayout seamlessFragmentContainer;
	private RelativeLayout header;

	private VodafoneTextView title;
	private VodafoneDialog vodafoneDialog;

	private String initialToken;
	private String previousMsisdn;

	private UserProfile userProfile;

	private boolean fromPreloginPage = false;
	private boolean userProfileRequestCompleted = false;
	private boolean userProfileHierarchyRequestCompleted = false;
	private Realm realm;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_seamless_login, null);
		realm = Realm.getDefaultInstance();

		String command = this.getArguments().getString(LoginActivity.COMMAND);

		header = (RelativeLayout) getActivity().findViewById(R.id.header);
		title = (VodafoneTextView) getActivity().findViewById(R.id.title);

		seamlessFragmentContainer = (LinearLayout) v.findViewById(R.id.seamless_fragment_container);

		seamlessLoginButton = (VodafoneButton) v.findViewById(R.id.enter_button);
		seamlessLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//displaySeamlessLoginPopup();

				displayLoadingSpinner();
				checkMsisdnAndWifiStatus();
			}
		});

		authentificationButton = (VodafoneButton) v.findViewById(R.id.authentification_button);
		authentificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);
				if (loginActivity != null) {
					loginActivity.displayLoginFragment();
				}
			}
		});


		if (command != null) {
			switch (command) {
				case LoginActivity.DISPLAY_PRE_LOGIN_PAGE_COMMAND:
					displayPreLoginPage();
					break;
				case LoginActivity.SEAMLESS_LOGIN_COMMAND:
					isLoggedInUser();
					break;
				case LoginActivity.CHECK_MOBILE_PROVIDER_COMMAND:
					checkMobileProvider();
					break;
				default:
					displayPreLoginPage();
					break;
			}
		}

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (realm.isClosed()) {
			realm = Realm.getDefaultInstance();
		}
	}

	private void displayPreLoginPage() {
		Log.d(TAG, "displayPreLoginPage() ");
		fromPreloginPage = true;
		title.setVisibility(View.VISIBLE);
		seamlessFragmentContainer.setVisibility(View.VISIBLE);

		callForAdobeTarget(AdobePageNamesConstants.LOGIN_TYPE);
	}

	private void displayLoadingSpinner() {
		showLoadingDialog();
	}

	private void hideLoadingSpinner() {
		Log.d(TAG, "hideLoadingSpinner() ");
		stopLoadingDialog();
	}


	public void isLoggedInUser() {
		displayLoadingSpinner();

		UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
		AppConfiguration appConfiguration = VodafoneController.getInstance().getGeneralAppConfiguration();
		boolean haveAnyValidCredentials = userProfile != null || CredentialUtils.haveValidCredentials();

		if (haveAnyValidCredentials && appConfiguration.isKeepMeLoggedIn()) {
			tryAutoLogin(userProfile);
		} else {
			checkMsisdnAndWifiStatus();
		}
	}

	//TODO ADD api 10 and api 19 to login
	public void tryAutoLogin(UserProfile userProfile) {

		if (getActivity() == null) {
			return;
		}

		String username = userProfile != null ? userProfile.getUserName() : null;
		String password = userProfile != null ? userProfile.getPassword() : null;
		username = username == null ? CredentialUtils.getUsername() : username;
		password = password == null ? CredentialUtils.getPassword() : password;
		if (null != username && null != password) {

			AuthenticationService authenticationService = new AuthenticationService(getActivity().getApplicationContext());
			final String finalPassword = password;
			authenticationService.login(username, password)
					.subscribe(new RequestSessionObserver<GeneralResponse<UserProfile>>() {

						@Override
						public void onError(Throwable e) {
							super.onError(e);

							checkMsisdnAndWifiStatus();
						}

						@Override
						public void onNext(GeneralResponse<UserProfile> userProfileGeneralResponse) {

							UserProfile userProfile = userProfileGeneralResponse.getTransactionSuccess();

							if (userProfileGeneralResponse.getTransactionStatus() == 0 && userProfile != null
									&& userProfile.getUserStatus() != null && userProfile.getUserStatus().equals("active")) {
								userProfile.setPassword(finalPassword);
								VodafoneController.getInstance().setUser(userProfile, false);

								if (GdprController.shoudPerformGetPermissionsForCBUUserAndPrepaid())
									GdprController.getPermissionsAfterLogin(false, getProgressDialog());
								else
									hideLoadingSpinner();

								Log.d(TAG, "auto login here IN SEAMLESS WHY?!??!");

								new AuthenticationService(getContext()).getVoVs().subscribe(new RequestSessionObserver<VoiceOfVodafoneResponse>() {
									@Override
									public void onNext(VoiceOfVodafoneResponse voiceOfVodafoneResponse) {
										if (VodafoneController.getInstance().getUser() != null)
											VodafoneController.getInstance().setVoiceOfVodafoneResponse(voiceOfVodafoneResponse);

										getAutoLoginUserData();
									}

									@Override
									public void onError(Throwable e) {
										super.onError(e);
										getAutoLoginUserData();
									}

									@Override
									public void onCompleted() {
										D.d(("TryAutoLogin Vovs"));

									}
								});

							} else {
								checkMsisdnAndWifiStatus();
							}
						}
					});
		} else {
			checkMsisdnAndWifiStatus();
		}
	}

	private void getUserProfile() {
		if (getContext() == null) {
			return;
		}
		Log.d(TAG, "getUserProfile: ");
		UserDataService userDataService = new UserDataService(getContext());
		//TODO: CR: what happens if this call finishes while the app is in the background (or the activity is not in foreground)?
		userDataService.getUserProfile(false).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {
			@Override
			public void onCompleted() {
				userProfileRequestCompleted = true;
				User user = VodafoneController.getInstance().getUser();
				/*if(user instanceof SeamlessPostPaidHighAccess) {
                    return;
                }*/
				if (user instanceof EbuMigrated && !(user instanceof SeamlessEbuUser)) {
					stopLoadingDialog();
					setupIdentitySelector();
					return;
				}

				Log.d(TAG, "getUserProfile onCompleted");
				enterDashboard();
			}

			@Override
			public void onNext(GeneralResponse<Profile> response) {

				if (response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null && response.getTransactionStatus() != 2) {
					super.onNext(response);
					VodafoneController.setApi10Failed(false);
					D.w("isTobe " + response.getTransactionSuccess().isTobe());
					D.w("isVMB " + response.getTransactionSuccess().isVMB());
					if (String.valueOf(response.getTransactionSuccess().isTobe()).equals(null) ||
							String.valueOf(response.getTransactionSuccess().isVMB()).equals(null))
						showRequestFailedMessage(null, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-10");

				} else {
					showRequestFailedMessage(null, AppLabels.getToastErrorSomeInfoNotLoaded(), "");
					VodafoneController.setApi10Failed(true);
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				userProfileRequestCompleted = true;
				VodafoneController.setApi10Failed(true);
				showRequestFailedMessage(e, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-10");
				User user = VodafoneController.getInstance().getUser();
				if (user instanceof EbuMigrated) {
					stopLoadingDialog();
					setupIdentitySelector();
					return;
				}
				enterDashboard();
			}
		});
	}

	private void getSubscriberHierarchy() {
		if (getContext() == null) {
			return;
		}
		UserDataService userDataService = new UserDataService(getContext());

		userDataService.getSubscriberHierarchy(true).subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>() {

			@Override
			public void onCompleted() {
				Log.d(TAG, "onCompleted");
				userProfileHierarchyRequestCompleted = true;
				enterDashboard();
			}

			@Override
			public void onNext(GeneralResponse<UserProfileHierarchy> response) {

				boolean responseIsSuccessful = response != null
						&& response.getTransactionStatus() == 0
						&& response.getTransactionSuccess() != null;

				if (getContext() != null && VodafoneController.getInstance().getUser() instanceof CBUUser)
					getContext().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
							.putInt("errorDetectedFlag", responseIsSuccessful
									? response.getTransactionSuccess().getErrorDetectedFlag()
									: DashboardController.API19_ERROR_FLAG).apply();

				if (responseIsSuccessful) {
					super.onNext(response);
					if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
						if (response.getTransactionSuccess().ifApi19CallFailed())
							VoiceOfVodafoneController.getInstance().createApi19FailedVov();
						else if (response.getTransactionSuccess().ifApi19CallTimeout())
							VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
					}
					return;
				}

				if (VodafoneController.getInstance().getUser() instanceof CBUUser)
					VoiceOfVodafoneController.getInstance().createApi19FailedVov();
				showRequestFailedMessage(null, AppLabels.getToastErrorSomeInfoNotLoaded(), "API-19");
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				userProfileHierarchyRequestCompleted = true;
				showRequestFailedMessage(e, AppLabels.getToastErrorSomeInfoNotLoaded(), "");
				if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
					if (getContext() != null)
						getContext().getSharedPreferences("API19", Context.MODE_PRIVATE).edit()
								.putInt("errorDetectedFlag", DashboardController.API19_TIMEOUT_FLAG)
								.apply();
					VoiceOfVodafoneController.getInstance().createApi19TimeOutVov();
				}
				enterDashboard();
			}
		});

	}

	private void setupIdentitySelector() {
		Log.d(TAG, "ebu migrated user - start flow");
		Intent intent = new Intent(getActivity(), LoginIdentitySelectorActivity.class);
		startActivity(intent);
	}

	private void displayPreloginPopup() {

		vodafoneDialog = new VodafoneDialog(getActivity(), SeamlessLabels.getDisconnectedVodafoneNetworkLabel())
				.setPositiveMessage(SeamlessLabels.getAuthenticationButtonLabel())
				.setNegativeMessage(SeamlessLabels.getRevokeButtonLabel())
				.setDismissActionOnNegative()
				.setPositiveAction(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((LoginActivity) getActivity()).displayLoginFragment();
						vodafoneDialog.dismiss();
					}
				});
		vodafoneDialog.setCancelable(false);
		vodafoneDialog.show();
	}

	private void checkMsisdnAndWifiStatus() {

		clearUserData();

		Log.d(TAG, "getMsisdnAndWifiStatus");

		//Get Msisdn from db
		AppConfiguration appConfiguration = VodafoneController.getInstance().getGeneralAppConfiguration();
		if (Hosts.hardcodedSeamlessMsisdn != null) {
			previousMsisdn = Hosts.hardcodedSeamlessMsisdn;
		} else if (appConfiguration != null) {
			previousMsisdn = appConfiguration.getPreviousMsisdn();
		}

		//Check if msisdn is saved in db
		if (previousMsisdn != null && !previousMsisdn.equals("")) {
			getInitialToken();
		} else {
			//Check wi-fi connectivity
			if (NetworkUtils.isWifiConnection(getContext())) {
				hideLoadingSpinner();
				LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);
				if (loginActivity != null) {
					loginActivity.displayWifiSettingsFragment();
				}
			} else {
				checkMobileProvider();
			}
		}
	}

	private void checkMobileProvider() {
		if (NetworkUtils.isIOYM(getContext())) {
			getInitialToken();
		} else {
			hideLoadingSpinner();
			LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);
			if (loginActivity != null) {
				loginActivity.displayLoginFragment();
			}
		}
	}

	//Seamless login first step
	private void getInitialToken() {
		Log.d(TAG, "getInitialToken()");

		AuthenticationService authenticationService = new AuthenticationService(getContext());
		Observer requestObserver = new RequestSaveRealmObserver<GeneralResponse<InitialToken>>(false) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				Log.d(TAG, "onError");
				hideLoadingSpinner();

				if (fromPreloginPage) {
					new CustomToast.Builder(VodafoneController.currentActivity()).message(LoginLabels.getSeamlessLoginFailedApiCall()).success(false).show();
				}
				if (getActivity() instanceof LoginActivity) {
					((LoginActivity) getActivity()).displayLoginFragment();
				}
			}

			@Override
			public void onCompleted() {
				Log.d(TAG, "onCompleted");
			}

			@Override
			public void onNext(GeneralResponse<InitialToken> initialTokenGeneralResponse) {
				super.onNext(initialTokenGeneralResponse);
				hideLoadingSpinner();

				if (initialTokenGeneralResponse.getTransactionStatus() == 0) {

					InitialToken initialTokenResponse = initialTokenGeneralResponse.getTransactionSuccess();
					Log.d(TAG, "onNext + token :" + initialTokenResponse.getInitialToken());

					if (null != initialTokenResponse.getInitialToken()) {

						initialToken = initialTokenResponse.getInitialToken();

						getSeamlessMsisdn();
					} else {
						if (fromPreloginPage) {
							new CustomToast.Builder(VodafoneController.currentActivity()).message(LoginLabels.getSeamlessLoginFailedApiCall()).success(false).show();
						}
						if (getActivity() instanceof LoginActivity) {
							((LoginActivity) getActivity()).displayLoginFragment();
						}
					}
				} else {
					if (fromPreloginPage) {
						new CustomToast.Builder(VodafoneController.currentActivity()).message(LoginLabels.getSeamlessLoginFailedApiCall()).success(false).show();
					}
					if (getActivity() instanceof LoginActivity) {
						((LoginActivity) getActivity()).displayLoginFragment();
					}
				}
			}
		};

		authenticationService.retrieveInitialToken()
				.subscribe(requestObserver);

	}

	//Seamless login second step
	private void getSeamlessMsisdn() {

		displayLoadingSpinner();
		Log.d(TAG, "Enter getSeamlessMsisdn() for previousSeamlessMsisdn " + previousMsisdn);
		AuthenticationService authenticationService = new AuthenticationService(getContext());
		Log.d(TAG, "Call authenticationService.seamlessAddHeader for seamlessMsisdn " + previousMsisdn);

		authenticationService.seamlessAddHeader(initialToken, NetworkUtils.isIOYM(getContext()) ? null : previousMsisdn)
				.subscribe(new RequestSessionObserver<GeneralResponse>() {
					@Override
					public void onNext(GeneralResponse response) {
						Log.d(TAG, "onNext + msidns :");
						hideLoadingSpinner();

						if (response.getTransactionStatus() == 0) {
							seamlessLogin();
						} else {
							String errorCode = response.getTransactionFault().getFaultCode();
							if (errorCode.equals(ErrorCodes.API3_SEAMLESS_FLAG_NOT_SET.getErrorCode())) {
								if (fromPreloginPage) {
									displayPreloginPopup();
									displayPreLoginPage();
								} else {
									if (getActivity() instanceof LoginActivity) {
										((LoginActivity) getActivity()).displayLoginFragment();
									}
								}
							} else {
								hideLoadingSpinner();
								if (fromPreloginPage) {
									new CustomToast.Builder(VodafoneController.currentActivity()).message(LoginLabels.getSeamlessLoginFailedApiCall()).success(false).show();
								}
								if (getActivity() instanceof LoginActivity) {
									((LoginActivity) getActivity()).displayLoginFragment();
								}
							}
						}
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						Log.d(TAG, "onError");
						hideLoadingSpinner();
						if (fromPreloginPage) {
							new CustomToast.Builder(VodafoneController.currentActivity()).message(LoginLabels.getSeamlessLoginFailedApiCall()).success(false).show();
						}
						if (getActivity() instanceof LoginActivity) {
							((LoginActivity) getActivity()).displayLoginFragment();
						}
					}
				});
	}

	//Seamless login third step
	private void seamlessLogin() {

		Log.d(TAG, "seamlessLogin - initialToken : " + initialToken);
		displayLoadingSpinner();
		AuthenticationService authenticationService = new AuthenticationService(getContext());

		authenticationService.retrieveSeamlessUserProfile(initialToken)
				.subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfile>>(false) {

					@Override
					public void onNext(GeneralResponse<UserProfile> userProfileGeneralResponse) {
						Log.d(TAG, "seamlessLogin - onNext");
						super.onNext(userProfileGeneralResponse);

						if (userProfileGeneralResponse.getTransactionStatus() == 0) {
							userProfile = userProfileGeneralResponse.getTransactionSuccess();
						} else {
							((LoginActivity) getActivity()).displayLoginFragment();
						}
					}

					@Override
					public void onCompleted() {
						if (userProfile == null) {
							userProfile = (UserProfile) RealmManager.getRealmObject(realm, UserProfile.class);
						}
						if (isUserQualifiesForSeamless(userProfile)) {
							hideLoadingSpinner();
							VodafoneController.getInstance().setUser(userProfile, true);

							new AuthenticationService(getContext()).getVoVs().subscribe(new RequestSessionObserver<VoiceOfVodafoneResponse>() {
								@Override
								public void onNext(VoiceOfVodafoneResponse voiceOfVodafoneResponse) {
									User user = VodafoneController.getInstance().getUser();
									if (user != null)
										VodafoneController.getInstance().setVoiceOfVodafoneResponse(voiceOfVodafoneResponse);

									if(!(user instanceof SeamlessEbuUser)) {
										getUserData();
									} else {
										new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);
									}
								}

								@Override
								public void onError(Throwable e) {
									super.onError(e);
									User user = VodafoneController.getInstance().getUser();

									if(!(user instanceof SeamlessEbuUser)) {
										getUserData();
									} else {
										new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);
									}

								}

								@Override
								public void onCompleted() {
									D.d(("SeamlessLogin Vovs"));

								}
							});


						} else {
							hideLoadingSpinner();
							((LoginActivity) getActivity()).displayLoginFragment();
						}
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						Log.d(TAG, "onError");

						hideLoadingSpinner();
						((LoginActivity) getActivity()).displayLoginFragment();
					}
				});
	}

	private boolean isUserQualifiesForSeamless(UserProfile userProfile) {

		boolean isUserQualifies = true;

		if (userProfile != null) {

			if(userProfile.isMigrated()) {
				return true;
			}

			if (userProfile.getSid() == null || userProfile.getCid() == null) {
				isUserQualifies = false;
			}

		} else {
			isUserQualifies = false;
		}

		return isUserQualifies;
	}

	private void getUserData() {
        /*-	API-10 se apeleaza atat Prepaid cat si Postpaid
          -	API-19 se apeleaza doar Postpaid, pentru rolurile PrivateUser si ResCorp
        */
		displayLoadingSpinner();
		User user = VodafoneController.getInstance().getUser();
		if (user == null) {
			return;
		}

		//check seamless roles before API-10
		if (user instanceof SeamlessPostPaidHighAccess) {
			getUserProfile();
			getSubscriberHierarchy();
		} else {
			userProfileHierarchyRequestCompleted = true;
			getUserProfile();
		}
	}

	private void getAutoLoginUserData() {
        /*-	API-10 se apeleaza atat Prepaid cat si Postpaid
          -	API-19 se apeleaza doar Postpaid, pentru rolurile PrivateUser si ResCorp
        */
		User user = VodafoneController.getInstance().getUser();
		if (user == null) {
			return;
		}
		getUserProfile();
		if (shouldGetUserProfileHierarchy()) {
			getSubscriberHierarchy();
		} else {
			userProfileHierarchyRequestCompleted = true;
			enterDashboard();
		}
	}

	private boolean shouldGetUserProfileHierarchy() {
		User user = VodafoneController.getInstance().getUser();
		return user != null && (user instanceof CBUUser || user instanceof EbuNonMigrated);
	}

	private void enterDashboard() {
		if (!userProfileHierarchyRequestCompleted || !userProfileRequestCompleted) {
			return;
		}
		new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD);

		//hide loading spinner
		stopLoadingDialog();
	}

	private void showRequestFailedMessage(Throwable e, String toastMessage, String apiKey) {
		try {
			//hide loading spinner
			stopLoadingDialog();

			new CustomToast.Builder(VodafoneController.currentActivity()).message(toastMessage).success(false).show();

			if (e instanceof AuthenticationServiceException) {
				throw new AuthenticationServiceException(R.string.server_error, e);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		hideLoadingSpinner();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		realm.close();
	}


	public void clearUserData() {
		UserDataController.getInstance().setCurrentLoginAction(LoginAction.LOGOUT).startAction();
	}

}
