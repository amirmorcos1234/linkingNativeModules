package ro.vodafone.mcare.android.card.travelling;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.adapters.TravelingOptionsAdapter;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesOptionDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardButton;
import ro.vodafone.mcare.android.ui.views.CardHeader;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Alex on 5/4/2017.
 */

public class TravellingRoamingOptionsCard extends VodafoneAbstractCard {

	OfferRowInterface offerRowInterface;
	private ListView activeOffersListView;
	private TravelingOptionsAdapter activeOffersAdapter;
	String offerPrice;
	private Context mContext;

	public TravellingRoamingOptionsCard(Context context) {
		super(context);
		mContext = context;
		init(null);
	}

	public TravellingRoamingOptionsCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs);
	}

	public TravellingRoamingOptionsCard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setCardPaddingsInPx((int)(getResources().getDimension(R.dimen.default_padding_horizontal)), (int)(getResources().getDimension(R.dimen.default_padding_vertical)), (int)(getResources().getDimension(R.dimen.default_padding_horizontal)), (int)(getResources().getDimension(R.dimen.default_padding_vertical)));
	}

	public TravellingRoamingOptionsCard buildPostpaidCard(ActiveOfferPostpaid activeOfferPostpaid) {

		this.offerRowInterface = activeOfferPostpaid;

		if(activeOfferPostpaid.getOfferPrice() != null){
			offerPrice = String.valueOf(NumbersUtils.twoDigitsAfterDecimal(Float.valueOf(String.valueOf(activeOfferPostpaid.getOfferPrice()))));
		} else {
			offerPrice = "0.00";
		}

		atachHeader(null, activeOfferPostpaid.getOfferName(), String.format(getResources()
				.getString(R.string.your_services_options_amount_unit), offerPrice), activeOfferPostpaid.getOfferCategory());
		setupListView();
		atachButton();
		hideLoading();

		return this;
	}


	public TravellingRoamingOptionsCard buildPrepaidCard(ActiveOffer activeOffer) {
		this.offerRowInterface = activeOffer;
		atachHeader(null, activeOffer.getOfferName(), "", activeOffer.getOfferCategory());
		setupListView();
		atachButton();
		hideLoading();

		return this;
	}

	private void atachHeader(String headerIcon, String headerTitle, String headerSubTitle, String headerCategory) {
		addHeader(new CardHeader(mContext)
				.buildHeader(new CardHeaderModel(headerIcon, headerTitle, new SpannableString(headerSubTitle), headerCategory, null, null)));
	}

	private void atachButton() {
		addButton(new CardButton(mContext)
				.buildButton(new CardButtonModel("Detalii Opțiune", new OnClickListener() {
					@Override
					public void onClick(View view) {
						KeyboardHelper.hideKeyboard((TravelingAboardActivity) mContext);
						((TravelingAboardActivity) mContext).addFragment(YourServicesOptionDetailsFragment.createFragment(offerRowInterface));
					}
				}, null)));
	}

	private void setupListView() {

		if(VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
			activeOffersAdapter = new TravelingOptionsAdapter(offerRowInterface, mContext);
		} else
		if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
			CostControl costControl = (CostControl) RealmManager.getRealmObject(CostControl.class);
			List<BalanceShowAndNotShown> balanceShowAndNotShowns = new ArrayList<>();
			List<BalanceShowAndNotShown> currentOfferBalancesList = new ArrayList<>();
			ActiveOfferPostpaid activeOfferPostpaid = (ActiveOfferPostpaid) offerRowInterface;

			if(costControl != null){
				balanceShowAndNotShowns = costControl.getCurrentExtraoptions().getExtendedBalanceList();
			}

			if (balanceShowAndNotShowns != null) {
				for (BalanceShowAndNotShown balance : balanceShowAndNotShowns) {
					if(matchIds(activeOfferPostpaid.getOfferId(), balance.getId())
							&& matchIds(activeOfferPostpaid.getOfferInstanceId(), balance.getInstanceId())){
						currentOfferBalancesList.add(balance);
					}
				}
			}

			activeOffersAdapter = new TravelingOptionsAdapter(offerRowInterface, mContext, currentOfferBalancesList);
		}
		activeOffersListView = (ListView) findViewById(R.id.active_options_list_view);
		activeOffersListView.setDivider(null);
		activeOffersListView.setDividerHeight(0);
		activeOffersListView.setAdapter(activeOffersAdapter);

		activeOffersAdapter.notifyDataSetChanged();

		activeOffersListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ScreenMeasure.setListHeight(activeOffersListView, activeOffersAdapter);
				activeOffersListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}

	private boolean matchIds(Long firstId, Long secondId){
		return firstId != null && secondId != null && firstId.equals(secondId);
	}

	@Override
	protected int setContent() {
		return R.layout.card_travelling_roaming_options;
	}

}
