package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.costControl.CostControlDataVolatile;
import ro.vodafone.mcare.android.client.model.costControl.Extraoption;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.exceptions.InvalidDataException;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.nonscrollable.NonScrollExpandableListView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.ProfileUtils;
import ro.vodafone.mcare.android.widget.CostControlWidget;
import ro.vodafone.mcare.android.widget.CostControlWidgetPostpaid;
import ro.vodafone.mcare.android.widget.animators.DashboardAnimator;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;
import ro.vodafone.mcare.android.widget.gauge.ExtraOptionsController;
import rx.Subscription;

/**
 * Created by Victor Radulescu on 3/1/2017.
 */

public class PostpaidDashboardFragment extends BaseDashboardFragment implements CostControlWidgetController.CostControlRequestListener {

	private static final int ID_LAST_UPDATE_VIEWGROUP = R.id.info_last_updated;
	protected CostControlWidget costControlWidget;
	ImageView refresh;
	VodafoneTextView last_update;
	VodafoneTextView dayLeftTv;

	public static PostpaidDashboardFragment newInstance() {

		PostpaidDashboardFragment dashboardFragment = new PostpaidDashboardFragment();

		Bundle bundle = new Bundle();
		dashboardFragment.setArguments(bundle);
		return dashboardFragment;
	}

	@Override
	protected void initExtraWidgets(RelativeLayout baseContent)
	{
		initCostControl(baseContent);
		initInfoText();
		initCards(null);
	}

	@Override
	protected void addExtraViewsInAnimator(DashboardAnimator dashboardAnimator) {
		dashboardAnimator.addCostControlAnimation(costControlWidget);
	}

	@Override
	protected void setCardsLayoutParams(NonScrollExpandableListView expandableListView) {
		((RelativeLayout.LayoutParams) expandableListView.getLayoutParams()).addRule(RelativeLayout.BELOW, ID_LAST_UPDATE_VIEWGROUP);
	}

	protected void initCostControl(RelativeLayout viewGroup) {
		costControlWidget = (CostControlWidgetPostpaid) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_cost_control_postpaid_widget, viewGroup, false);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.dashboard_menu_avatar_widget);
		layoutParams.topMargin = -ScreenMeasure.dpToPx(ExtraOptionsController.EXTRA_RADIOS_IN_PX) / 2;
		viewGroup.addView(costControlWidget, layoutParams);
		CostControlWidgetController.getInstance().addCostControlRequestListener(this);
	}

	protected void initInfoText() {
		setupDaysLeftUntilBillCycle();
		initLastUpdate();
	}

	protected void setupDaysLeftUntilBillCycle() {
		Profile profile = (Profile) RealmManager.getRealmObject(realm, Profile.class);
		if (profile == null || profile.getBillCycleDate() == null) {
			initDaysLeftUntilBillCycle(-1);
		} else {
			initDaysLeftUntilBillCycle(profile.getBillCycleDate());
		}
	}

	protected void initDaysLeftUntilBillCycle(int billCycleDate) {
		dayLeftTv = new VodafoneTextView(getContext());
		dayLeftTv.setTag("daysLeftUntilBillCycle");
		dayLeftTv.setId(R.id.day);
		//dayLeftTv.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
		dayLeftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medium_text_size));
		dayLeftTv.setTextColor(Color.WHITE);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.cost_control_widget);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		int margins = getResources().getDimensionPixelSize(R.dimen.general_margin);
		layoutParams.setMargins(margins, 0, margins, margins / 2);
		addViewToGroup(dayLeftTv, layoutParams);
		if (billCycleDate != -1) {
			setDaysLeftUntilBillCycle(billCycleDate);
		} else {
			dayLeftTv.setVisibility(View.GONE);
		}
	}

	private void initLastUpdate() {
		RelativeLayout lastUpdateGroupView = (RelativeLayout) getLayoutInflater(getArguments()).inflate(R.layout.dashboard_last_update, null, false);

		last_update = (VodafoneTextView) lastUpdateGroupView.findViewById(R.id.last_update);
		refresh = (ImageView) lastUpdateGroupView.findViewById(R.id.refresh);
		refresh.setColorFilter(Color.parseColor("#ffffff"));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.day);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		addViewToGroup(lastUpdateGroupView, layoutParams);
	}

	protected void setDaysLeftUntilBillCycle(int billCycleDate) {
		if (dayLeftTv != null) {
			dayLeftTv.setVisibility(View.VISIBLE);
			String text = getString(R.string.dashboard_postpaid_left_days_until_bill_cycle, String.valueOf(billCycleDate));
			SpannableString sb = new SpannableString(text);

			int indexOfSpan = text.indexOf(String.valueOf(billCycleDate));
			sb.setSpan(new StyleSpan(Typeface.BOLD),
					indexOfSpan,
					text.length(),
					0);
			dayLeftTv.setText(sb);
		}
	}

	private void setInfoLastUpdate() {
		try {
			long lastUpdateDateUnix = ((CostControl) RealmManager.getRealmObject(realm, CostControl.class)).getLastUpdateDate();

			String s = DateUtils.getTime(lastUpdateDateUnix);
			String infoLastUpdate = String.format(AppLabels.getLast_update(), s);
			last_update.setText(infoLastUpdate);
			refresh.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initCards(@Nullable CostControl costControl) {
		prepareListData(costControl);
	}

	@Override
	public void onCostControlRequestSucces(CostControl costControl) {
		if (costControlWidget != null)
			costControlWidget.onCostControlRequestCompleted(costControl);
		setInfoLastUpdate();
		getInvoicePayBillMessageVov();
		initCards(costControl);
		if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
			getAdditionalCostForOtherMsidns();
		}
	}

	@Override
	public void onCostControlRequestFailed(String error) {
		if (costControlWidget != null)
			costControlWidget.onCostControlRequestFailed(error);
		if (getContext() != null) {
			getInvoicePayBillMessageVov();
			showToastErrorMessage();
			//Display error card for Cost suplimentar – on MSISDN level
			addErrorCard();

			//Display error card for Cost suplimentar – on BAN level
			if (VodafoneController.getInstance().getUser() instanceof ResCorp) {
				addErrorCard();
			}
			refresh.setVisibility(View.GONE);
			last_update.setVisibility(View.GONE);
		}
	}

	@Override
	public void onExtraOptionsRequestCompleted() {
		if (costControlWidget != null)
			costControlWidget.onExtraOptionsRequestCompleted();
	}

	private void getAdditionalCostForOtherMsidns() {
		UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(realm,
				UserProfileHierarchy.class);
		if (!(VodafoneController.getInstance().getUser() instanceof ResCorp) ||
				userProfileHierarchy == null ||
				userProfileHierarchy.getSubscriberList() == null ||
				userProfileHierarchy.getSubscriberList().size() < 2 ||
				userProfileHierarchy.getSubscriberList().size() > 5) {
			return;
		}
		List<String> msisdnList = new ArrayList<>();

		for (Subscriber subcriber : userProfileHierarchy.getSubscriberList()) {
			if (subcriber.getMsisdn() != null && !subcriber.getMsisdn().isEmpty()) {
				msisdnList.add(subcriber.getMsisdn());
			}
		}

		Subscription subscription = new UserDataService(dashboardActivity.getApplicationContext()).getCostControlForOtherMsidns(msisdnList).subscribe(new RequestSessionObserver<CostControlDataVolatile>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (e instanceof InvalidDataException) {
					showToastErrorMessage();
					addErrorCard();
				}
			}

			@Override
			public void onNext(CostControlDataVolatile costControlDataVolatile) {
				if (getActivity() != null && costControlDataVolatile != null) {
					addSuplimentaryCostCard(costControlDataVolatile);
				}
			}
		});
		compositeSubscription.add(subscription);
	}

	private void addSuplimentaryCostCard(CostControlDataVolatile costControlDataVolatile) {
		List<AdditionalCost> additionalCosts = costControlDataVolatile.getAdditionalCosts();
		if (additionalCosts != null && !additionalCosts.isEmpty()) {
			RealmManager.startTransaction();
			AdditionalCost summedAddtionalCost = new AdditionalCost();
			for (AdditionalCost additionalCost : additionalCosts) {

				summedAddtionalCost.setCurrency(additionalCost.getCurrency());

				float currentTotalCost = summedAddtionalCost.getTotalCost();
				summedAddtionalCost.setTotalCost(currentTotalCost + additionalCost.getTotalCost());
				float currentDataCost = summedAddtionalCost.getDataCost();
				summedAddtionalCost.setDataCost(currentDataCost + additionalCost.getDataCost());
				float currentSmsCost = summedAddtionalCost.getSmsCost();
				summedAddtionalCost.setSmsCost(currentSmsCost + additionalCost.getSmsCost());
				float currentVoiceCost = summedAddtionalCost.getVoiceCost();
				summedAddtionalCost.setVoiceCost(currentVoiceCost + additionalCost.getVoiceCost());
				float currentOtherCost = summedAddtionalCost.getOtherCost();
				summedAddtionalCost.setOtherCost(currentOtherCost + additionalCost.getOtherCost());
			}
			RealmManager.commitTransaction();

			String time = ProfileUtils.getLastBillCycleDate();
			String textHigh = dashboardActivity.getResources().getString(R.string.text_high_postpaid_multiple_balance_card, time);
			BalanceCardHeader balanceCardHeader = getBalanceCardHeaderWithData(summedAddtionalCost, textHigh, "Costuri suplimentare");
			if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
				balanceCardHeader.setShowSecondaryFooterButton(((PostPaidUser) VodafoneController.getInstance().getUser()).showSecondaryButtonInSuplimentaryCost());
			}
			Double sumOfDynamicAllocation = getSumOfDynamicAllocation(costControlDataVolatile);
			if (sumOfDynamicAllocation != null && sumOfDynamicAllocation > 0) {
				balanceCardHeader.setTextLow(super.setSpannableStringForDynamicRaise(NumbersUtils.twoDigitsAfterDecimal(sumOfDynamicAllocation)));
			}
			super.addOverspentCard(summedAddtionalCost, balanceCardHeader);
		}
	}

	private Double getSumOfDynamicAllocation(CostControlDataVolatile costControlDataVolatile) {
		double sumOfDynamicalAllocation = 0;
		List<Extraoption> extraoptions = costControlDataVolatile.getExtraoptions();

		for (int i = 0; i < extraoptions.size(); i++) {
			List<BalanceShowAndNotShown> balanceList = extraoptions.get(i).getExtendedBalanceList();
			for (BalanceShowAndNotShown balance : balanceList) {
				if (balance.getAmountTypeId() != null && balance.getAmountTypeId().equals(AmountTypeIdEnum.chunk) &&
						(balance.getChunkmodel() != null && balance.getChunkmodel() == 1)) {
					sumOfDynamicalAllocation += balance.getChunktotalcharge();
				}
			}
		}

		return sumOfDynamicalAllocation;
	}

	public void getInvoicePayBillMessageVov() {
		if (getContext() == null || !shouldGetInvoicePayBillMessageVov()) {
			return;
		}
		String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
		String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
		Subscription subscription = new BillingServices(getActivity().getApplicationContext()).getInvoiceDetails(msisdn, ban).subscribe(new RequestSaveRealmObserver<GeneralResponse<InvoiceDetailsSuccess>>() {
			@Override
			public void onNext(GeneralResponse<InvoiceDetailsSuccess> generalResponse) {
				super.onNext(generalResponse);
				if (generalResponse.getTransactionStatus() == 0 && generalResponse.getTransactionSuccess() != null) {
					InvoiceDetailsSuccess invoiceDetailsSuccess = generalResponse.getTransactionSuccess();
					VoiceOfVodafone vov = null;
					try {
						Float invoiceAmount = Float.valueOf(invoiceDetailsSuccess.getInvoiceAmount());
						if (invoiceAmount > 0) {
							String message = "Factură scadentă la " + DateUtils.getDate(invoiceDetailsSuccess.getIssueDate(),
									new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"))) + ", total estimat ";

							String secondMessage = NumbersUtils.twoDigitsAfterDecimal(invoiceAmount) + " " + BillingOverviewLabels.getBilling_overview_ron_unit() + ".";// din care ";
							vov = new VoiceOfVodafone(3, 10, VoiceOfVodafoneCategory.Pay_Bill, null, message + secondMessage, "Plăteşte factura", "Mai târziu",
									true, true, VoiceOfVodafoneAction.RedirectWithIntent, VoiceOfVodafoneAction.Dismiss);
							vov.setIntentActionName(IntentActionName.PAY_BILL_OWN);
						}
						VoiceOfVodafoneController.getInstance().pushStashToView(vov);
						DashboardController.getInstance().setReloadDashboardOnResume(false);
						VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		compositeSubscription.add(subscription);
	}

	protected boolean shouldGetInvoicePayBillMessageVov() {
		User user = VodafoneController.getInstance().getUser();
		return user instanceof ResCorp || user instanceof PrivateUser || user instanceof ChooserUser
				|| user instanceof DelegatedChooserUser || user instanceof AuthorisedPersonUser;
	}

}
