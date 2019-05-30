package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import butterknife.ButterKnife;
import io.realm.RealmObject;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.CustomizePhoneCard;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.card.offers.PayCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.shop.ShopCurrentSelection;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.shop.ProductImage;
import ro.vodafone.mcare.android.client.model.shop.RecommendedPricePlan;
import ro.vodafone.mcare.android.client.model.shop.ShopCartSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.banners.PhoneBanner;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.PhoneShopWebViewActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.TypeFaceSpan;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static ro.vodafone.mcare.android.ui.activities.offers.OffersActivity.DEEP_LINK_KEY;
import static ro.vodafone.mcare.android.ui.webviews.PhoneShopWebViewActivity.PRICE_PLAN_KEY;


/**
 * Created by Victor Radulescu on 3/30/2017.
 */

public class PhoneDetailsFragment extends OffersFragment implements CustomizePhoneCard.OnUserSelectionListener {

	private static final String TAG = "PhoneDetailsFragment";
	public static final String PRICE_PLAN_SKU_ID_KEY = "pricePlanSkuId";
	private final String ID_SHOP_PRODUCT_SUCCESS_OTHERS = "others";
	private final String ID_SHOP_PRODUCT_SUCCESS_RECOMANDED = "recomended";

	LinearLayout viewGroup;

	CustomizePhoneCard customizePhoneCard;

	PayCard payCard;

	String phoneSkuId;
	String pricePlanSkuId;
	String shopSessionToken;

	ShopProduct currentShopProduct;

	private boolean shopRequestInProcess = true;

	private int selectedModel = 0, selectColor = 0;

	private String oneUsageSerializedData;
	private String secondSerializedData;

	private boolean requestListingsInProgress = false;
	private boolean requestsRecomendedInProgress = false;
	private boolean requestListingsFailed =false;
	private boolean requestRecomendedFailed =false;
	private Semaphore semaphore;

	public static PhoneDetailsFragment newInstance(Bundle args) {

		PhoneDetailsFragment fragment = new PhoneDetailsFragment();
		fragment.phoneSkuId = args.getString(ShopProduct.PHONE_SKU_ID);
		fragment.pricePlanSkuId = args.getString(ShopProduct.PRICE_PLAN_SKU_ID);
		fragment.setArguments(args);
		return fragment;
	}

	public PhoneDetailsFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		semaphore = new Semaphore(1, true);
		setCurrentShopInfo();
	}

	private void setHeaderText() {
		try {

			((OffersActivity) getActivity()).getNavigationHeader().removeViewFromContainer();
			((OffersActivity) getActivity()).getNavigationHeader().hideSelectorView();
			((OffersActivity) getActivity()).getNavigationHeader().setTitle("Ofertă pentru tine");
			((OffersActivity) getActivity()).getToolbar().showToolBar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "create view");

		viewGroup = (LinearLayout) inflater.inflate(R.layout.fragment_retention_phone_details, null);
		ButterKnife.bind(this, viewGroup);

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap(6);

		tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
		tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
		tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
		PhonesDetailsTrackingEvent event = new PhonesDetailsTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);

		oneUsageSerializedData = IntentActionName.RETENTION_PHONE_DETAILS.getOneUsageSerializedData();
		secondSerializedData = IntentActionName.RETENTION_PHONE_DETAILS.getOneUsageSecondSerializedData();

		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		removeViews();
		setHeaderText();
		if(oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
			startLoading();
			phoneSkuId = secondSerializedData;
			checkShopEligibility();
		} else
			requestData(true, true);

        callForAdobeTarget(AdobePageNamesConstants.RETENTION_DEVICE.concat(getPhoneSkuId()));
	}

	@Override
	public void onDetach() {
		super.onDetach();
		OffersActivity offersActivity = (OffersActivity) getActivity();
		offersActivity.getNavigationHeader().removeViewFromContainer();
	}

	private void setupBanner(ShopProduct shopProduct, boolean refreshView) {
		List<ProductImage> images = shopProduct.getImages();
		List<String> imageList = new ArrayList<>();
		if (images != null) {
			for (ProductImage image : images) {
				imageList.add(image.getImageURL());
			}
		}
		//add missing empty images if imageList is emoty
		if (imageList.isEmpty()) {
			if (shopProduct.getDefaultImageUrl() != null) {
				imageList.add(shopProduct.getDefaultImageUrl());
			} else {
				imageList.add("");
			}
		}
		OffersActivity offersActivity = (OffersActivity) getActivity();

		try {
			offersActivity.getNavigationHeader().removeViewFromContainer();
		} catch (Exception e) {
			e.printStackTrace();
		}

		offersActivity.getNavigationHeader().showBannerView();
		offersActivity.getNavigationHeader().addViewToContainer(new PhoneBanner(getContext())
				.setUriList(imageList)
				.setTitle(shopProduct.getPhoneDisplayName())
				.buildBanner());
	}

	private PayCard addPayCard(ShopProduct shopProduct, boolean refreshView) {
		if (!refreshView) {
			return payCard;
		}

		String pricePlanSkuIdCheck = shopProduct.getPricePlanSkuId();

		VodafoneTextView titleTextView = new VodafoneTextView(getContext());

		//check the type of the price plan for the correct display title
		if (pricePlanSkuIdCheck.contains("keep")) {
			titleTextView.setText("Cu abonamentul tău actual");
		} else {
			titleTextView.setText("Cu abonament");
		}

		titleTextView.setTextSize(20);
		titleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_title_color));

		titleTextView.setGravity(Gravity.CENTER);
		int margins = ScreenMeasure.dpToPx(10);
		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLayoutParams.setMargins(0, margins, 0, margins);
		viewGroup.addView(titleTextView, titleLayoutParams);

		Bundle secondaryBundle = new Bundle();
		secondaryBundle.putString(ShopProduct.PHONE_SKU_ID, shopProduct.getPhoneSkuId());
		secondaryBundle.putString(ShopProduct.PRICE_PLAN_SKU_ID, shopProduct.getPricePlanSkuId());


//        RecommendedPricePlan shopPricePlan = (RecommendedPricePlan) RealmManager.getRealmObjectAfterStringField(RecommendedPricePlan.class,PRICE_PLAN_SKU_ID_KEY,shopProduct.getPricePlanSkuId());
//
		PayCard payCard;
		if (shopProduct.getPricePlanDisplayName() != null && !shopProduct.getPricePlanDisplayName().equals("")) {
			payCard = new PayCard(getContext()).setPrimaryButtonClickListener(primaryClickListenerPayCard)
					.setPrimaryButtonVisibility(shopProduct.getStockLevel())
					.setPrimaryButtonText(RetentionLabels.getAddToCartText())
					.setSecondaryButtonClickListener(new RedirectFragmentListener(getActivity(), PricePlansOtherFragment.newInstance(secondaryBundle)))
					.setSecondaryButtontext(RetentionLabels.getSeeOtherBenefitsForPhoneText())
					.setTitle(shopProduct.getPricePlanDisplayName())
					.setPricePhone(getFormatPhonePrice(shopProduct.getDiscountedPrice(), shopProduct.getBundlePrice()))
					.setDetailsFromHtml(shopProduct.getPricePlanDetailsHtml())
					.showPricePlanDetails()
					.showPricePlanSeparator()
					.addToDetails(shopProduct.getPricePlanBenefits())
					.setGridImages(shopProduct.getAdditionalBenefits())
					.removePadding()
					.setPricePlan(getFormatPricePlan(shopProduct.getPricePlanMothlyFee()))
					.setPricePlanDuration(shopProduct.getPricePlanSkuId(), shopProduct.getPricePlanContractPeriod());
			viewGroup.addView(payCard);
		} else {
			Integer durationYears = shopProduct.getPricePlanContractPeriod() / 12;
			String formattedDuration;
			if (durationYears == 1) {
				formattedDuration = durationYears + " an";
			} else {
				formattedDuration = durationYears + " ani";
			}

			String noPricePlanDisplayName = "Prelungirea abonamentului actual cu păstrarea beneficiilor pe " + formattedDuration;
			payCard = new PayCard(getContext()).setPrimaryButtonClickListener(primaryClickListenerPayCard)
					.setPrimaryButtonVisibility(shopProduct.getStockLevel())
					.setSecondaryButtonClickListener(new RedirectFragmentListener(getActivity(), PricePlansOtherFragment.newInstance(secondaryBundle)))
					.setTitle(noPricePlanDisplayName)
					.setPricePhone(getFormatPhonePrice(shopProduct.getDiscountedPrice(), shopProduct.getBundlePrice()))
					.hidePricePlanDetails()
					.hidePricePlanSeparator()
					.hidePricePlanDuration();

			viewGroup.addView(payCard);
		}

		return payCard;
	}

	private void addOrReplaceExpandableWebViewCard(String title, String htmlContent) {
		if (htmlContent == null) {
			return;
		}
		ExpandableWebViewCard expandableWebViewCard = (ExpandableWebViewCard) viewGroup.findViewWithTag(title);
		if (expandableWebViewCard == null) {
			expandableWebViewCard = new ExpandableWebViewCard(getContext());
			expandableWebViewCard.setTag(title);
		}
		OffersActivity offersActivity = (OffersActivity) getActivity();


		expandableWebViewCard
				.setTitle(title)
				.setHtmlInTextView()
				.setLoadHtmlContent(true)
				.setHtmlContent(htmlContent)
				.setImageArrowDirectionDown()
				.setPagingScrollView(offersActivity.getMenuScrollView())
				.build();
		expandableWebViewCard.setCardMargins(ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(6), ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(6));
		expandableWebViewCard.setPadding(0, ScreenMeasure.dpToPx(5), 0 , ScreenMeasure.dpToPx(5));
		if (expandableWebViewCard.getParent() == null) {
			viewGroup.addView(expandableWebViewCard);
		}
	}

	private void setCurrentShopProduct(ShopProduct shopProduct) {
		this.currentShopProduct = shopProduct;
	}

	private void setupGreenTaxCard(ShopProduct product) {
		//product.setGreenTax(3.10);

		if (product.getGreenTax() == null || product.getGreenTax() <= 0)
			return;

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.card_green_tax, null);

		VodafoneTextView titleTextView = view.findViewById(R.id.titleTextView);
		VodafoneTextView subtitleTextview = view.findViewById(R.id.subTitleTextView);


		String title = RetentionLabels.getGreenTaxTitle();
		String price = NumbersUtils.twoDigitsAfterDecimalAlwaysDot(product.getGreenTax()) + " RON";
		String finalTitle = title.replace("{greenTax}", price);
		SpannableStringBuilder sBuilder = SpannableStringBuilder.valueOf(finalTitle);

		final ForegroundColorSpan priceColor = new ForegroundColorSpan(getContext().getResources().getColor(R.color.green_tax_price));
		final TypeFaceSpan priceTypeFace = new TypeFaceSpan(Fonts.getVodafoneRGBD());

		sBuilder.setSpan(priceColor, finalTitle.indexOf(price),  finalTitle.indexOf(price) + price.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		sBuilder.setSpan(priceTypeFace, finalTitle.indexOf(price), finalTitle.indexOf(price) + price.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);


		titleTextView.setText(sBuilder, TextView.BufferType.SPANNABLE);
		subtitleTextview.setText(RetentionLabels.getGreenTaxSubTitle());

		viewGroup.addView(view);

	}

	private void setupCustomizePhoneCard(ShopProduct shopProduct, boolean refreshView) {

		//get the selected color and memory of the phone
		for (int i = 0; i < shopProduct.getColors().size(); i++) {
			if (shopProduct.getColors().get(i).getPhoneSkuId().equals(shopProduct.getPhoneSkuId())) {
				selectColor = i;
				Log.d(TAG, "selectColor" + selectColor);
			}
		}

		for (int i = 0; i < shopProduct.getMemories().size(); i++) {
			if (shopProduct.getMemories().get(i).getPhoneSkuId().equals(shopProduct.getPhoneSkuId())) {
				selectedModel = i;

				Log.d(TAG, "selectedModel" + selectedModel);
			}
		}

		if (refreshView) {
			customizePhoneCard = new CustomizePhoneCard(getContext())
					.setupColors(shopProduct.getColors())
					.setupMemories(shopProduct.getMemories())
					.setSelectedColor(selectColor)
					.setSelectedModel(selectedModel)
					.setStockNumber(shopProduct.getStockLevel());
			customizePhoneCard.build(this);
			viewGroup.addView(customizePhoneCard);

			return;
		}
		customizePhoneCard
				.setSelectedColor(selectColor)
				.setSelectedModel(selectedModel)
				.setStockNumber(shopProduct.getStockLevel());
		customizePhoneCard.setInfo();


	}

	private void setPhoneDetails(ShopProduct shopProduct, boolean refreshView) {
		setupBanner(shopProduct, refreshView);
		setupCustomizePhoneCard(shopProduct, refreshView);
		payCard = addPayCard(shopProduct, refreshView);
		setupGreenTaxCard(shopProduct);
		addOrReplaceExpandableWebViewCard("Descriere", shopProduct.getDescriptionHtml());
		addOrReplaceExpandableWebViewCard("Specificaţii", shopProduct.getTehnicalSpecificationsHtml());
	}

	private void getShopProductsDetails(final boolean refreshView) {
		ShopLoginSuccess shopSesionToken = (ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class);
		RealmManager.delete(RecommendedPricePlan.class);

		if (shopSesionToken == null) {
			try {
				new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.RETENTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new ShopService(getContext()).getShopProductsDetails(
					getPricePlanSkuId(),
					getPhoneSkuId(),
					shopSesionToken.getShopSessionToken()).subscribe(new RequestSaveRealmObserver<GeneralResponse<ShopProductsSuccess>>() {
				@Override
				public void onCompleted() {
					stopLoading();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					showError(refreshView);

				}

				@Override
				public void onNext(GeneralResponse<ShopProductsSuccess> shopProductsSuccessGeneralResponse) {
					if (shopProductsSuccessGeneralResponse != null &&
							shopProductsSuccessGeneralResponse.getTransactionSuccess() != null &&
							shopProductsSuccessGeneralResponse.getTransactionSuccess().getProductsList() != null &&
							shopProductsSuccessGeneralResponse.getTransactionSuccess().getProductsList().isValid() &&
							!shopProductsSuccessGeneralResponse.getTransactionSuccess().getProductsList().isEmpty()) {
						super.onNext(shopProductsSuccessGeneralResponse);
						RealmManager.startTransaction();
						shopProductsSuccessGeneralResponse.getTransactionSuccess().setId("phoneDetails");
						RealmManager.commitTransaction();

						ShopProduct shopProduct = shopProductsSuccessGeneralResponse.getTransactionSuccess().getProductsList().get(0);
						// RealmManager.update(shopProduct);
						setCurrentShopProduct(shopProduct);
						setPhoneDetails(shopProduct, refreshView);
					} else {
						showError(refreshView);
					}
				}
			});
		}
	}

	private void shopLogin() {

		D.w("in shopLogin");


		new ShopService(getContext()).postShopLogin(null, null)
				.doOnNext(new Action1<GeneralResponse<? extends RealmObject>>() {
					@Override
					public void call(GeneralResponse<? extends RealmObject> shopLoginGeneralResponse) {
						shopSessionToken = ((ShopLoginSuccess) shopLoginGeneralResponse.getTransactionSuccess()).getShopSessionToken();
						RealmManager.update(shopLoginGeneralResponse.getTransactionSuccess());
					}
				})
				.doOnError(new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil").success(false).show();
						payCard.setPrimaryButtonClickListener(primaryClickListenerPayCard);
						stopLoading();
					}
				})
				.flatMap(new Func1<GeneralResponse<? extends RealmObject>, Observable<GeneralResponse<? extends RealmObject>>>() {
					@Override
					public Observable<GeneralResponse<? extends RealmObject>> call(GeneralResponse<? extends RealmObject> shopLoginSuccessGeneralResponse) {
						if (shopSessionToken != null) {
							return (new ShopService(getContext()).putShopCart(currentShopProduct.getPhoneSkuId(),
									currentShopProduct.getPricePlanSkuId(),
									currentShopProduct.getProductId(),
									currentShopProduct.getCfgSkuId(),
									shopSessionToken))
									.doOnNext(new Action1<GeneralResponse<? extends RealmObject>>() {
										@Override
										public void call(GeneralResponse<? extends RealmObject> shopCartSuccessGeneralResponse) {

										}
									});
						} else {
							stopLoading();
							new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil").success(false).show();
							return null;
						}

					}
				}).subscribe(new RequestSessionObserver<GeneralResponse<? extends RealmObject>>() {
			@Override
			public void onNext(GeneralResponse<? extends RealmObject> generalResponse) {
				if (getContext() == null) {
					return;
				}
				if (generalResponse.getTransactionStatus() != 0) {
					stopLoading();
					new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil").success(false).show();
					payCard.setPrimaryButtonClickListener(primaryClickListenerPayCard);

				} else {
					String url = ((ShopCartSuccess) generalResponse.getTransactionSuccess()).getShoppingCartUrl();

					AddPhoneToCartTrackingEvent event = new AddPhoneToCartTrackingEvent();
					VodafoneController.getInstance().getTrackingService().trackCustom(event);

					String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
					FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
					firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
					firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("pp_retention_MSISDN", msisdn);
					firebaseAnalyticsItem.addFirebaseAnalyticsParams("pp_retention_pp_name",
							currentShopProduct.getPricePlanDisplayName(), "pp_retention_skuid", currentShopProduct.getPricePlanSkuId(),
							"pp_retention_xxEUR", currentShopProduct.getBundlePrice() + "EUR");

					Intent intent = new Intent(getContext(), PhoneShopWebViewActivity.class);
					intent.putExtra(WebviewActivity.KEY_URL, url);
					intent.putExtra(PRICE_PLAN_KEY, firebaseAnalyticsItem);
					getActivity().startActivityForResult(intent, 10000);
					stopLoading();
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (getContext() == null) {
					return;
				}
				new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil").success(false).show();
				stopLoading();
				payCard.setPrimaryButtonClickListener(primaryClickListenerPayCard);
			}
		});

	}

	private void checkShopEligibility() {
		Observable<GeneralResponse<ShopEligibilitySuccess>> observable = RetentionObservables
				.getInstance().getCheckUserEligibilityObservable();
		Subscription subscription = observable.subscribe(new RequestSessionObserver<GeneralResponse<ShopEligibilitySuccess>>() {
			@Override
			public void onNext(GeneralResponse<ShopEligibilitySuccess> generalResponse) {
				RequestSaveRealmObserver.save(generalResponse);
				if(ResponseValidatorUtils.isValidGeneralRealmResponse(generalResponse)) {
					if (!generalResponse.getTransactionSuccess().getAllowPhones()) {
						stopLoadingDialog();
						showError(RetentionLabels.getNotEligiblePhone());
						return;
					}
					if (!generalResponse.getTransactionSuccess().getIsShoppingCartEmpty()) {
						stopLoadingDialog();
						showError(RetentionLabels.getRetentionShoppingCartEmptyFalseBody());
						return;
					}
					requestsRecomendedInProgress = true;
					requestListingsInProgress = true;
					getRecommendedPhones();
					getOtherPhones();
					return;
				}
				stopLoadingDialog();
				showError(true);
			}

			@Override
			public void onCompleted() { }

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				stopLoadingDialog();
				showError(true);
			}
		});
		addToActivityCompositeSubcription(subscription);
	}

	private void getRecommendedPhones() {
		new ShopService(getContext()).getShopProductsRecommended(pricePlanSkuId != null ? pricePlanSkuId : "",
				((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class))
						.getShopSessionToken()).subscribe(
				new RequestSaveRealmObserver<GeneralResponse<ShopProductsSuccess>>() {

					@Override
					public void onCompleted() { }

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						requestsRecomendedInProgress = false;
						requestRecomendedFailed =true;
						stopLoadingIfRequestsCompleted();
						if (!requestsGetPhonesInProgress() & requestListingsFailed)
							showError(true);
					}

					@Override
					public void onNext(GeneralResponse<ShopProductsSuccess> response) {

						try {
							semaphore.acquire();

							requestsRecomendedInProgress = false;
							stopLoadingIfRequestsCompleted();

							if (response != null && response.getTransactionSuccess() != null) {
								if (response.getTransactionSuccess().getProductsList().size() == 0) {
									requestRecomendedFailed = true;
									if (!requestsGetPhonesInProgress() && requestListingsFailed)
										showError("Nu există telefoane disponibile.");
									semaphore.release();
									return;
								}

								RealmManager.startTransaction();
								response.getTransactionSuccess().setId(ID_SHOP_PRODUCT_SUCCESS_RECOMANDED);
								RealmManager.commitTransaction();
								super.onNext(response);
								if (!requestsGetPhonesInProgress())
									checkPhoneIdExists();

								semaphore.release();
								return;
							}

							requestRecomendedFailed = true;

							if (!requestsGetPhonesInProgress() & requestListingsFailed)
								showError(true);

							semaphore.release();

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void getOtherPhones() {
		ShopLoginSuccess shopLoginSuccess = (ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class);
		String shopSessionToken = shopLoginSuccess.getShopSessionToken();
		Log.d(TAG, shopSessionToken);

		new ShopService(getContext()).getShopProductsListings(pricePlanSkuId!=null?pricePlanSkuId:"",
				shopSessionToken).subscribe(
				new RequestSaveRealmObserver<GeneralResponse<ShopProductsSuccess>>() {
					@Override
					public void onCompleted() { }

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						requestListingsInProgress = false;
						requestListingsFailed = true;
						stopLoadingIfRequestsCompleted();
						if (!requestsGetPhonesInProgress() & requestRecomendedFailed)
							showError(true);
					}

					@Override
					public void onNext(GeneralResponse<ShopProductsSuccess> response) {

						try {
							semaphore.acquire();

							requestListingsInProgress = false;
							stopLoadingIfRequestsCompleted();

							if (response != null && response.getTransactionSuccess() != null) {
								if (response.getTransactionSuccess().getProductsList().size() == 0) {
									requestListingsFailed = true;
									if (!requestsGetPhonesInProgress() && requestRecomendedFailed)
										showError("Nu există telefoane disponibile.");
									semaphore.release();
									return;
								}
								RealmManager.startTransaction();
								response.getTransactionSuccess().setId(ID_SHOP_PRODUCT_SUCCESS_OTHERS);
								RealmManager.commitTransaction();
								super.onNext(response);
								if (!requestsGetPhonesInProgress())
									checkPhoneIdExists();

								semaphore.release();
								return;
							}

							requestListingsFailed = true;

							if (!requestsGetPhonesInProgress() & requestRecomendedFailed)
								showError(true);

							semaphore.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void checkPhoneIdExists() {
		ShopProduct shopProduct = (ShopProduct) RealmManager.getRealmObjectAfterStringField(ShopProduct.class, ShopProduct.PHONE_SKU_ID, secondSerializedData);
		if (shopProduct != null) {
			pricePlanSkuId = shopProduct.getPricePlanSkuId();
			phoneSkuId = shopProduct.getPhoneSkuId();
			setCurrentShopInfo();
			requestData(true, true);
			return;
		}
		stopLoading();
		showError(RetentionLabels.getPhoneNotInList());
	}

	private void requestData(boolean showLoading, boolean removeViews) {
		if (removeViews)
			removeViews();

		getShopProductsDetails(removeViews);
		shopRequestInProcess = true;
		if (showLoading && requestsInProgress())
			startLoading();
	}

	private void putInCartWithLoading() {
		startLoading();
		shopLogin();
	}

	private void startLoading() {
		shopRequestInProcess = true;
		showLoadingDialog();
	}

	private void stopLoadingIfRequestsCompleted() {
		if (requestsGetPhonesInProgress()) {
			return;
		}
		// shopRequestInProcess =false;
		stopLoading();
	}

	private boolean requestsGetPhonesInProgress(){
		return requestsRecomendedInProgress || requestListingsInProgress;
	}

	private boolean requestsInProgress() {
		return shopRequestInProcess;
	}

	private void stopLoading() {
		if (getContext() == null) {
			return;
		}
		shopRequestInProcess = false;
		stopLoadingDialog();
	}

	@Override
	public String getTitle() {
		return "Ofertă pentru tine";
	}

	private SpannableStringBuilder getFormatPhonePrice(Float discountedPrice, Float bundlePrice) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");

		boolean discountValueValid = discountedPrice != null;
		boolean valueValid = bundlePrice != null;
		if (valueValid) {
			bundlePrice = (float) bundlePrice;
		} else {
			return SpannableStringBuilder.valueOf("");
		}

		String bundlePriceText = decimalFormat.format(bundlePrice) + " €";
		String discountedPriceText = discountValueValid ? decimalFormat.format((float) discountedPrice) + " €" : "";
		bundlePriceText = bundlePriceText.replaceAll(",", ".");
		discountedPriceText = discountedPriceText.replaceAll(",", ".");
		if (discountValueValid && discountedPrice < bundlePrice) {
			String finalText = "Preț telefon " + bundlePriceText + " " + discountedPriceText;

			SpannableStringBuilder sb = new SpannableStringBuilder(finalText);
			try {
				StrikethroughSpan strykeSpannable = new StrikethroughSpan();
				sb.setSpan(strykeSpannable, finalText.indexOf(bundlePriceText), finalText.indexOf(bundlePriceText) + bundlePriceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
				RelativeSizeSpan relativeSpan = new RelativeSizeSpan(2f);
				int firstCharIndexForDiscount = finalText.indexOf(discountedPriceText);
				sb.setSpan(styleSpan, firstCharIndexForDiscount, firstCharIndexForDiscount + discountedPriceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				sb.setSpan(relativeSpan, firstCharIndexForDiscount, firstCharIndexForDiscount + discountedPriceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				return sb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sb;
		} else {
			String finalText = "Preț telefon " + bundlePriceText;
			SpannableStringBuilder sb = new SpannableStringBuilder(finalText);
			try {

				StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
				RelativeSizeSpan relativeSpan = new RelativeSizeSpan(2f);
				sb.setSpan(styleSpan,
						finalText.indexOf(bundlePriceText),
						finalText.indexOf(bundlePriceText) + bundlePriceText.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				sb.setSpan(relativeSpan,
						finalText.indexOf(bundlePriceText),
						finalText.indexOf(bundlePriceText) + bundlePriceText.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sb;
		}

		//String finalText = "Preț telefon " +bundlePriceText;
		//return new SpannableStringBuilder(finalText);
	}

	private SpannableStringBuilder getFormatPricePlan(Float price) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		if (price != null) {
			price = (float) price;
		} else {
			return SpannableStringBuilder.valueOf("");
		}
		String priceText = decimalFormat.format(price) + " €";
		priceText = priceText.replaceAll(",", ".");
		String finalText = "Cost abonament " + priceText + " pe lună";

		SpannableStringBuilder sb = new SpannableStringBuilder(finalText);
		try {

			StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
			sb.setSpan(styleSpan, finalText.indexOf(priceText), finalText.indexOf(priceText) + priceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}

	private void showError(boolean refreshView) {
		stopLoading();
		viewGroup.removeAllViews();
		if (!refreshView) {
			Toast.makeText(getContext(), "Selecţie indisponibilă", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			GeneralCardsWithTitleBodyAndTwoButtons errorCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
			errorCard.showError();
			errorCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeViews();
					if(oneUsageSerializedData != null && oneUsageSerializedData.equalsIgnoreCase(DEEP_LINK_KEY)) {
						startLoading();
						checkShopEligibility();
						return;
					}
					requestData(true, true);
				}
			});
			viewGroup.addView(errorCard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		if (getActivity() == null)
			return;

		viewGroup.removeAllViews();

		VodafoneGenericCard errorCard = new VodafoneGenericCard(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		viewGroup.addView(errorCard,layoutParams);
		errorCard.setOnClickListener(null);
		errorCard.showError(true, message);
	}

	private View.OnClickListener primaryClickListenerPayCard = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			payCard.setPrimaryButtonClickListener(null);
			putInCartWithLoading();
		}
	};

	private void removeViews() {
		if (viewGroup == null) {
			return;
		}
		viewGroup.removeAllViews();
	}


	@Override
	public void onMemorySelect(int selectedModel, String phoneSkuId, String pricePlanSkuId) {
		this.pricePlanSkuId = pricePlanSkuId;
		this.phoneSkuId = phoneSkuId;
		this.selectedModel = selectedModel;

		/*//Tealium Track Event
		Map<String, Object> tealiumMapEvent = new HashMap(6);
		tealiumMapEvent.put("screen_name", "retention");
		tealiumMapEvent.put("event_name", "mcare:retention:" + getPhoneSkuId() + ":button:memorie");
		tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

		setCurrentShopInfo();
		requestData(true, true);
	}

	@Override
	public void onColorSelect(int selectedColor, String phoneSkuId, String pricePlanSkuId) {
		this.pricePlanSkuId = pricePlanSkuId;
		this.phoneSkuId = phoneSkuId;
		this.selectColor = selectedColor;

		/*//Tealium Track Event
		Map<String, Object> tealiumMapEvent = new HashMap(6);
		tealiumMapEvent.put("screen_name", "retention");
		tealiumMapEvent.put("event_name", "mcare:retention:" + getPhoneSkuId() + ":button:culoare");
		tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackEvent("event_name", tealiumMapEvent);*/

		setCurrentShopInfo();
		requestData(true, true);
	}

	private void setCurrentShopInfo() {
		RealmManager.startTransaction();
		ShopCurrentSelection shopCurrentSelection = new ShopCurrentSelection();
		shopCurrentSelection.setPricePlanSkuId(pricePlanSkuId);
		shopCurrentSelection.setPhoneSkuId(phoneSkuId);
		RealmManager.update(shopCurrentSelection);
	}

	private String getPricePlanSkuId() {
		ShopCurrentSelection shopCurrentSelection = (ShopCurrentSelection) RealmManager.getRealmObject(ShopCurrentSelection.class);

		String current = shopCurrentSelection.getPricePlanSkuId();
		return current != null ? current : pricePlanSkuId;
	}

	private String getPhoneSkuId() {
		ShopCurrentSelection shopCurrentSelection = (ShopCurrentSelection) RealmManager.getRealmObject(ShopCurrentSelection.class);

		String current = shopCurrentSelection.getPhoneSkuId();
		return current != null ? current : phoneSkuId;
	}



	public class PhonesDetailsTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "mcare:" + "retention:" + getPhoneSkuId();
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "retention:" + getPhoneSkuId());


			s.prop5 = "sales:product details";
			s.getContextData().put("prop5", s.prop5);
			s.channel = "retention in self care";
			s.getContextData().put("&&channel", s.channel);
			s.prop21 = "mcare:" + "retention:" + getPhoneSkuId();
			s.getContextData().put("prop21", s.prop21);
		}
	}

	public class AddPhoneToCartTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}

			s.events = "scAdd";
			s.getContextData().put("scAdd", s.scAdd);

			s.events = "scOpen";
			s.getContextData().put("scOpen", s.scOpen);
		}
	}

}
