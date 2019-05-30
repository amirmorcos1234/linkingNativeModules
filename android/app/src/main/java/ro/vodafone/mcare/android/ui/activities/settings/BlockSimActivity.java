package ro.vodafone.mcare.android.ui.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusEBUSuccess;
import ro.vodafone.mcare.android.client.model.realm.simStatus.SIMStatusSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.interfaces.fragment.settings.BlockSimCommunicationListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.SIMChangeStatusRequestEBU;
import ro.vodafone.mcare.android.service.SimStatusService;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.BaseBlockSimFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.CBUBlockSimFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.EBUBlockSimFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Subscription;

/**
 * Created by Serban Radulescu on 3/23/2018.
 */

public class BlockSimActivity extends MenuActivity implements BlockSimCommunicationListener {

	private static final String TAG = BlockSimActivity.class.getSimpleName();

	private int API_DELAY_TIME;
	public static final String DASHBOARD_BLOCK_SIM_TOAST_MESSAGE = "show_block_sim_toast";


	@BindView(R.id.block_sim_navigation_header)
	NavigationHeader navigationHeader;

	@BindView(R.id.block_sim_fragment_container)
	FrameLayout blockSimFragmentContainer;

	private SimStatusService simStatusService;
	private boolean simActive;
	private Subscription subscription;
	private Runnable redirectToDashboardRunnable;
	private long startTime;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(this);

		API_DELAY_TIME = 5;//the default value
		String ebuBlockSimApiWaitingTime = AppConfiguration.getEbuBlockSimApiWaitingTime();
		try {
			API_DELAY_TIME = Integer.parseInt(ebuBlockSimApiWaitingTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		API_DELAY_TIME = API_DELAY_TIME * 1000;
		Log.d(TAG, API_DELAY_TIME+"");

		setTitle(this.getResources().getString(R.string.blockSIM));
		attachFragment();
		simStatusService = new SimStatusService(this);
		showLoadingDialog();
	}

	public void setTitle(String text) {
		try {
			navigationHeader.setTitle(text);
		} catch (Exception e) {
			Log.e(TAG, "problems with getTitle");
		}
	}

	public NavigationHeader getNavigationHeader() {
		return navigationHeader;
	}

	@Override
	protected int setContent() {
		return R.layout.activity_block_sim;
	}

	public void attachFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		BaseFragment blockSimFragment;
		if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
			blockSimFragment = EBUBlockSimFragment.newInstance();
		} else {
			blockSimFragment = CBUBlockSimFragment.newInstance();
		}

		if(FragmentUtils.getVisibleFragment(this,false)!=null&&FragmentUtils.getVisibleFragment(this,false).getClass() != blockSimFragment.getClass())
		{
			transaction.addToBackStack(null);
		}
		transaction.replace(R.id.block_sim_fragment_container, blockSimFragment, FragmentUtils.getTagForFragment(blockSimFragment));
		transaction.commit();
	}


	@Override
	public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {

	}

	@Override
	public void checkSimStatus() {
		String userSid = getUserSid();

		simStatusService.getSimStatus(userSid).subscribe(new RequestSessionObserver<GeneralResponse<SIMStatusSuccess>>() {
			@Override
			public void onNext(GeneralResponse<SIMStatusSuccess> simStatusGeneralResponse) {
				stopLoadingDialog();
				if (simStatusGeneralResponse.getTransactionSuccess() != null) {
					simActive = simStatusGeneralResponse.getTransactionSuccess().isActiv();
					onSimStatusReceived(simActive, false);
				} else {
					onSimStatusReceived(false, true);
				}
			}

			@Override
			public void onCompleted() {
				D.d("on Completed");
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				stopLoadingDialog();
				onSimStatusReceived(false, true);
			}
		});
	}

	@Override
	public void sendUnblockSimRequest(String alternativePhoneNumber) {

		String userSid = getUserSid();

		simStatusService.putSimUnblock(userSid, alternativePhoneNumber).subscribe(new RequestSessionObserver<GeneralResponse>() {
			@Override
			public void onNext(GeneralResponse generalResponse) {

				if (generalResponse.getTransactionStatus() == 0) {
					redirectToDashboard(SettingsLabels.getBlockSimVovMessage(), SettingsLabels.getBlockSimSuccessToastMessage());
				} else {
					onError(new Throwable("Server failed"));
				}
			}

			@Override
			public void onCompleted() {
				super.onCompleted();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				getChatBubble().displayBubble(true);
				new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
				enableButton();
			}
		});
	}

	@Override
	public void sendBlockSimRequest(String alternativePhoneNumber) {
		String userSid = getUserSid();

		simStatusService.putSimBlock(userSid, alternativePhoneNumber).subscribe(new RequestSessionObserver<GeneralResponse>() {
			@Override
			public void onNext(GeneralResponse generalResponse) {
				stopLoadingDialog();

				if (generalResponse.getTransactionStatus() == 0) {
					redirectToDashboard(SettingsLabels.getBlockSimVovMessage(), SettingsLabels.getBlockSimSuccessToastMessage());
				} else if (generalResponse.getTransactionStatus() != 0) {
					if (generalResponse.getTransactionFault() != null) {
						if (generalResponse.getTransactionFault().getFaultCode().equals("EC05801")) {
							redirectToDashboard(SettingsLabels.getBlockSimVovMessage(), SettingsLabels.getBlockSimSuccessToastMessage());
						} else {
							onError(new Throwable("Server failed"));
						}
					} else {
						onError(new Throwable("Server failed"));
					}
				}
			}

			@Override
			public void onCompleted() {
				super.onCompleted();
				stopLoadingDialog();
				enableButton();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				stopLoadingDialog();
				new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
				enableButton();
				getChatBubble().displayBubble(true);
			}
		});
	}

	@Override
	public void checkEbuSimStatus() {
		String userSid = getEbuUserSid();

		simStatusService.getEbuSimStatus(userSid).subscribe(new RequestSessionObserver<GeneralResponse<SIMStatusEBUSuccess>>() {
			@Override
			public void onNext(GeneralResponse<SIMStatusEBUSuccess> simStatusGeneralResponse) {
				stopLoadingDialog();
				if (simStatusGeneralResponse.getTransactionSuccess() != null) {
					RequestSaveRealmObserver.save(simStatusGeneralResponse);
					simActive = simStatusGeneralResponse.getTransactionSuccess().getIsActiv();
					onSimStatusReceived(simActive, false);
				} else {
					onSimStatusReceived(false, true);
				}
			}

			@Override
			public void onCompleted() {
				D.d("on Completed");
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				stopLoadingDialog();
				onSimStatusReceived(false, true);
			}
		});
	}

	@Override
	public void sendEbuUnblockSimRequest() {
		SIMChangeStatusRequestEBU simChangeStatusRequestEBU = getUserInfoForBodyRequest();

		subscription = simStatusService.putEbuSimUnblock(simChangeStatusRequestEBU).subscribe(new RequestSessionObserver<GeneralResponse>() {
			@Override
			public void onNext(GeneralResponse generalResponse) {
				Log.d(TAG, "unblock onNext");
				if(VodafoneController.getInstance().getUser() instanceof SubUserMigrated) {
					onEbuSubUserUnblockResponseReceived(generalResponse);
				} else {
					onEbuUnblockResponseReceived(generalResponse);
				}
			}
			@Override
			public void onCompleted() {
				super.onCompleted();
				Log.d(TAG, "unblock onCompleted");
				removeSubscriptionAndCallBacks();
			}
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				Log.d(TAG, "unblock onError");

				if(!(VodafoneController.getInstance().getUser() instanceof SubUserMigrated) && getApiWaitingTime() > API_DELAY_TIME) {
					redirectToDashboard(SettingsLabels.getEbuUnBlockSimDelayFailedVovMessage(), null);
				} else {
					new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
				}
				onGeneralErrorReceived();
			}
		});
		initRunnableWithDelay(subscription);
	}

	@Override
	public void sendEbuBlockSimRequest() {
		SIMChangeStatusRequestEBU simChangeStatusRequestEBU = getUserInfoForBodyRequest();

		subscription = simStatusService.putEbuSimBlock(simChangeStatusRequestEBU).subscribe(new RequestSessionObserver<GeneralResponse>() {
			@Override
			public void onNext(GeneralResponse generalResponse) {
				Log.d(TAG, "block onNext");
				stopLoadingDialog();

				if(getApiWaitingTime() < API_DELAY_TIME){
					if (generalResponse.getTransactionStatus() == 0) {
						redirectToDashboard(SettingsLabels.getEbuBlockSimSuccessVovMessage(),
								SettingsLabels.getEbuBlockSimSuccessToastMessage());
					} else {
						new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
						onGeneralErrorReceived();
					}
				} else {
					if(generalResponse.getTransactionStatus() == 0){
						redirectToDashboard(SettingsLabels.getEbuBlockSimDelaySuccessVovMessage(), null);
					} else {
						redirectToDashboard(SettingsLabels.getEbuBlockSimDelayFailedVovMessage(), null);
					}
				}
			}

			@Override
			public void onCompleted() {
				Log.d(TAG, "block onCompleted");
				super.onCompleted();
				stopLoadingDialog();
				removeSubscriptionAndCallBacks();
			}

			@Override
			public void onError(Throwable e) {
				Log.d(TAG, "block onError");
				super.onError(e);

				if(getApiWaitingTime() > API_DELAY_TIME) {
					redirectToDashboard(SettingsLabels.getEbuBlockSimDelayFailedVovMessage(), null);
				} else {
					new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
				}
				onGeneralErrorReceived();

			}
		});
		initRunnableWithDelay(subscription);
	}

	private void onGeneralErrorReceived() {
		stopLoadingDialog();
		enableButton();
		getChatBubble().displayBubble(true);
		removeSubscriptionAndCallBacks();
	}

	private void onEbuUnblockResponseReceived(GeneralResponse generalResponse) {
		if(getApiWaitingTime() < API_DELAY_TIME){
			if (generalResponse.getTransactionStatus() == 0) {
				redirectToDashboard(SettingsLabels.getEbuUnBlockSimSuccessVovMessage(),
						SettingsLabels.getEbuUnBlockSimSuccessToastMessage());
			} else {
				new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
				onGeneralErrorReceived();
			}
		} else {
			if(generalResponse.getTransactionStatus() == 0){
				redirectToDashboard(SettingsLabels.getEbuUnBlockSimDelaySuccessVovMessage(), null);
			} else {
				redirectToDashboard(SettingsLabels.getEbuUnBlockSimDelayFailedVovMessage(), null);
			}
		}
	}

	private void onEbuSubUserUnblockResponseReceived(GeneralResponse generalResponse) {
		if (generalResponse.getTransactionStatus() == 0) {
			redirectToDashboard(SettingsLabels.getEbuSubUserUnBlockSimRequestVovMessage(),
					SettingsLabels.getEbuSubUserUnBlockSimRequestToastMessage());
		} else
		if(generalResponse.getTransactionStatus() == 2 && generalResponse.getTransactionFault() != null &&
				generalResponse.getTransactionFault().getFaultCode().equals("EC05902")) {
			stopLoadingDialog();
			new CustomToast.Builder(BlockSimActivity.this).message(SettingsLabels.getEbuSubUserUnBlockSimRequestPendingToastMessage()).success(false).show();
			enableButton();
			getChatBubble().displayBubble(true);
			removeSubscriptionAndCallBacks();
		} else {
			new CustomToast.Builder(BlockSimActivity.this).message("Serviciu momentan indisponibil.").success(false).show();
			onGeneralErrorReceived();
		}
	}

	private void removeSubscriptionAndCallBacks() {
		unSubscribeSubscriptionFromController(subscription);
		VodafoneController.getInstance().handler.removeCallbacks(redirectToDashboardRunnable);
	}

	public void redirectToDashboard(String vovMessage, String toastMessage) {
		stopLoadingDialog();
		VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(18, 20, VoiceOfVodafoneCategory.BAR_UNBAR,
				null, vovMessage, "Ok, am înțeles", null,
				true, false, VoiceOfVodafoneAction.Dismiss, null);
		VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
		VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

		if(toastMessage != null) {
			String toastMessageKey = DASHBOARD_BLOCK_SIM_TOAST_MESSAGE + "/" + toastMessage;
			IntentActionName.DASHBOARD.setOneUsageSerializedData(toastMessageKey);
		}

		new NavigationAction(this).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
	}

	public void onSimStatusReceived(boolean simActive, boolean error) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.block_sim_fragment_container);
		if(fragment == null) {
			return;
		}
		if(error) {
			((BaseBlockSimFragment) fragment).inflateErrorLayout();
		} else {
			((BaseBlockSimFragment) fragment).inflateBlockSimGeneralLayout(simActive);
		}
	}

	private void enableButton() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.block_sim_fragment_container);
		if(fragment != null) {
			((BaseBlockSimFragment) fragment).enableButton();
		}
	}

	private String getUserSid() {
		String userSid = "";
		if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null)
			userSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
		else
			userSid = VodafoneController.getInstance().getUserProfile().getSid();
		return userSid;
	}

	private String getEbuUserSid() {
		Log.d(TAG, "sid: " + UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid());
		if(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null)
			Log.d(TAG, "Selected subscriber is not null");
		else
			Log.d(TAG, "Selected subscriber is null");
		return UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
	}

	private Long getApiWaitingTime(){
		return System.currentTimeMillis() - startTime;
	}

	private void initRunnableWithDelay(final Subscription subscription){
		startTime = System.currentTimeMillis();
		VodafoneController.getInstance()
				.handler
				.postDelayed(getDelayedRunnable(subscription), API_DELAY_TIME);
	}

	private Runnable getDelayedRunnable(final Subscription subscription){
		redirectToDashboardRunnable = new Runnable() {
			@Override
			public void run() {
				VodafoneController.getInstance().addSubscription(subscription);

				if(simActive) {
					redirectToDashboard(SettingsLabels.getEbuBlockSimRequestPendingVovMessage(),
							SettingsLabels.getEbuBlockSimRequestPendingToastMessage());
				} else {
					redirectToDashboard(SettingsLabels.getEbuUnBlockSimRequestPendingVovMessage(),
							SettingsLabels.getEbuUnBlockSimRequestPendingToastMessage());
				}
				Log.d(TAG, "5 seconds passed, redirecting to dashboard");

			}
		};
		return redirectToDashboardRunnable;
	}

	private void unSubscribeSubscriptionFromController(Subscription subscription){
		if(VodafoneController.getInstance().getCompositeSubscription() != null){
			VodafoneController.getInstance().removeSubscription(subscription);
		}
	}

	private SIMChangeStatusRequestEBU getUserInfoForBodyRequest() {
		SIMStatusEBUSuccess simStatusEBUSuccess = (SIMStatusEBUSuccess) RealmManager.getRealmObject(SIMStatusEBUSuccess.class);
		EntityDetailsSuccess entityDetailsSuccess = (EntityDetailsSuccess) RealmManager.getRealmObject(EntityDetailsSuccess.class);
		String phoneNumber = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
		String vfOdsSid = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid();
		String vfOdsCid = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid();
		String vfOdsBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
		String productId = simStatusEBUSuccess.getProductId();
		String productSpecName = simStatusEBUSuccess.getProductSpecName();
		String treatmentSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
		String emailVoiceSpecialist = null;
		if(entityDetailsSuccess != null && entityDetailsSuccess.getVoiceSpecialist() != null) {
			emailVoiceSpecialist = entityDetailsSuccess.getVoiceSpecialist().getEmail();
		}

		return new SIMChangeStatusRequestEBU(phoneNumber,
				vfOdsSid, vfOdsCid, vfOdsBan, productId, productSpecName, treatmentSegment, emailVoiceSpecialist);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG , "onActivityResult() with code: " + requestCode);

		Fragment f = getSupportFragmentManager().findFragmentById(R.id.block_sim_fragment_container);
		f.onActivityResult(requestCode, resultCode, data);
	}
}
