package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.PhoneCard;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.card.orderHistory.OrderHistoryDetailsCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrder;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrdersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OrderHistoryService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by user2 on 4/21/2017.
 */

public class OrderHistoryDetailsFragment extends OffersFragment {

    private static final String TAG = "OrderHistory";
    static final String ORDER_ID_KEY = "order_id";

    RealmList<ShopOrder> shopOrderRealmList;
    ShopOrdersSuccess shopOrdersSuccess;
    ShopOrder shopOrderDetails;
    LinearLayout viewGroup;
    NavigationHeader navigationHeader;

    private OrderHistoryDetailsCard orderDeliveryInfoCard;
    private PhoneCard orderProductInfoCard;
    private ServiceCard orderServiceInfoCard;
    private OrderHistoryDetailsCard orderPaymentMethodInfoCard;

    String orderId;

    public OrderHistoryDetailsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        viewGroup = new LinearLayout(getContext());
        viewGroup.setOrientation(LinearLayout.VERTICAL);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewGroup.setLayoutParams(params);
        viewGroup.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.general_background_light_gray));

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        OrderHistoryDetailsTrackingEvent event = new OrderHistoryDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return viewGroup;
    }

    @Override
    public void onStart(){
        super.onStart();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orderId = bundle.getString(ORDER_ID_KEY, "");
        }

        showLoadingDialog();
        configureHeader();

        getOrderDetails();

        callForAdobeTarget(AdobePageNamesConstants.RETENTION_ORDER_DETAILS);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "detach");
        ((OffersActivity) getActivity()).setTitle();
        if(navigationHeader != null) {
            navigationHeader.removeViewFromContainer();
        }
    }

    @Override
    public String getTitle() {
        return OffersLabels.getOrderHistoryDetailsTitle();
    }

    public void configureHeader() {
        navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();
        navigationHeader.displayDefaultHeader();
        navigationHeader.hideSelectorView();
        navigationHeader.setTitle(getTitle());
    }

    public void getOrderDetails() {

        showLoadingDialog();
        String userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        String userName = VodafoneController.getInstance().getUserProfile().getUserName();
        final String shopSessionToken = ((ShopLoginSuccess) RealmManager.getRealmObject(ShopLoginSuccess.class)).getShopSessionToken();

        new OrderHistoryService(getContext()).getOrderHistoryDetails(orderId, shopSessionToken, userRole, userName).subscribe(new RequestSessionObserver<GeneralResponse<ShopOrdersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ShopOrdersSuccess> ordersHistoryResponse) {
                if (ordersHistoryResponse.getTransactionSuccess() != null) {
                    D.w("ORDERS DETAILS = " + ordersHistoryResponse.getTransactionSuccess());
                    if(ordersHistoryResponse.getTransactionSuccess() == null)
                        inflateErrorLayout();
                    else
                    {
                        shopOrdersSuccess = ordersHistoryResponse.getTransactionSuccess();
                        shopOrderRealmList = shopOrdersSuccess.getOrdersList();
                        if(shopOrderRealmList.size() != 0 && getActivity() != null)
                            inflateOrderDetailsLayout();
                        else
                            inflateErrorLayout();
                    }

                } else {
                    onError(new Throwable("Server failed"));
                }
                stopLoadingDialog();
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
                D.d("on Completed");
            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                inflateErrorLayout();
                stopLoadingDialog();
            }
        });

    }

    public void inflateOrderDetailsLayout() {
        shopOrderDetails = shopOrderRealmList.get(0);
        viewGroup.removeAllViews();

        orderDeliveryInfoCard = buildDeliveryInfoCard();
        orderDeliveryInfoCard.addCardTitle(OffersLabels.getOrderHistoryDetailsDeliveryInfo(), R.color.purple_title_color, null);
        viewGroup.addView(orderDeliveryInfoCard);


        for(int i = 0 ; i < shopOrderDetails.getCommerceItems().size() ; i++){
            Log.d(TAG, shopOrderDetails.getCommerceItems().get(i).toString());
            if(shopOrderDetails.getCommerceItems().get(i).getType() != null) {
                if (shopOrderDetails.getCommerceItems().get(i).getType().equals("phone")) {

                    orderProductInfoCard = buildProductInfoCard(i);
                    if(i == 0){
                        orderProductInfoCard.addCardTitle(OffersLabels.getOrderHistoryDetailsProductInfo(), R.color.purple_title_color, null);
                    }

                    viewGroup.addView(orderProductInfoCard);
                } else {
                    String commerceType = shopOrderDetails.getCommerceItems().get(i).getType();
                    if (commerceType.equals("service") || commerceType.equals("priceplan")) {

                        orderServiceInfoCard = buildServiceInfoCard(i);
                        if (i == 0) {
                            orderServiceInfoCard.addCardTitle(OffersLabels.getOrderHistoryDetailsProductInfo(), R.color.purple_title_color, null);
                        }

                        viewGroup.addView(orderServiceInfoCard);
                    }
                }
            }
        }


        orderPaymentMethodInfoCard = buildPaymentMethodInfoCard();
        orderPaymentMethodInfoCard.addCardTitle(OffersLabels.getOrderHistoryDetailsPaymentInfo(), R.color.purple_title_color, null);
        viewGroup.addView(orderPaymentMethodInfoCard);


        View orderTotalAmountInfoCard = buildTotalAmountInfoCard();

        viewGroup.addView(orderTotalAmountInfoCard);
        configureHeaderStatus();
    }

    public void inflateErrorLayout() {
        try {
            viewGroup.removeAllViews();

            VodafoneGenericCard orderHistoryDetailsErrorCard = new VodafoneGenericCard(getActivity());

            viewGroup.addView(orderHistoryDetailsErrorCard);

            orderHistoryDetailsErrorCard.showError(true, SettingsLabels.getSimpleSmallError());
            orderHistoryDetailsErrorCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOrderDetails();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public OrderHistoryDetailsCard buildDeliveryInfoCard() {
        OrderHistoryDetailsCard card = new OrderHistoryDetailsCard(getActivity());
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        String estimatedDelivered = shopOrderDetails.getDeliveryInfo().getDelivered();
        String estimatedDeliveryTimeParsed;

        if(estimatedDelivered != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = format.parse(estimatedDelivered);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int intMonth = calendar.get(Calendar.MONTH) + 1;

            String month = getMonthStringRepresentation(intMonth);
            String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
            String year = calendar.get(Calendar.YEAR) + "";
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
                day = formatDate(calendar.get(Calendar.DAY_OF_MONTH));
            estimatedDeliveryTimeParsed = day + " " + month + " " + year;
        }
        else
        {
            estimatedDeliveryTimeParsed = "No date available";
        }

        String userOwnMsisdn = shopOrderDetails.getDeliveryInfo().getClientContactNumber();
        if(userOwnMsisdn != null) {
            if (userOwnMsisdn.charAt(0) == '4') {
                userOwnMsisdn = userOwnMsisdn.substring(1, userOwnMsisdn.length());
            }
        }

        String deliveryInfo = shopOrderDetails.getDeliveryInfo().getStatus();
        if(deliveryInfo != null) {
            deliveryInfo = deliveryInfo.replaceAll("&Icirc;", "Î");
        }


        card.setTitle(OffersLabels.getOrderHistoryDetailsNumber())
                .setDetails(shopOrderDetails.getOrderId())
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsDateSubmitted(), estimatedDeliveryTimeParsed, false, true)
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsEstimatedDeliveryTime(), shopOrderDetails.getDeliveryInfo().getEstimatedDeliveryTime(),false, true)
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsStatus(), deliveryInfo, false, true)
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsAwbId(), shopOrderDetails.getDeliveryInfo().getReferenceNote(), false, true)
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsCustomerPhoneNumber(), userOwnMsisdn, false, true)
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsCustomerDeliveryAddress(), shopOrderDetails.getDeliveryInfo().getDeliveryAddress(), true, true);

        return card;
    }

    public PhoneCard buildProductInfoCard(int i) {

        PhoneCard card = new PhoneCard(getActivity());
        float bundlePriceFloat = shopOrderDetails.getCommerceItems().get(0).getBundlePrice();
        String bundlePriceFormatted = String.format("%.2f", bundlePriceFloat);
        bundlePriceFormatted = bundlePriceFormatted.replaceAll(",", ".");

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("Preț telefon: " + bundlePriceFormatted  + " €");
        String pricePlanFee;
                

       if(shopOrderDetails.getCommerceItems().get(i).getPricePlanMonthlyFee() != null) {
            pricePlanFee = new DecimalFormat("0.00").format(shopOrderDetails.getCommerceItems().get(i).getPricePlanMonthlyFee());

            int pricePlanContractPeriod = Integer.parseInt(shopOrderDetails.getCommerceItems().get(i).getPricePlanContractPeriod()) / 12;

            card.setTitle(shopOrderDetails.getCommerceItems().get(i).getPhoneDisplayName())
                    .setDetails("Cu " + shopOrderDetails.getCommerceItems().get(i).getPricePlanDisplayName() + " prelungire contractuală pe " + pricePlanContractPeriod + " ani "
                            + pricePlanFee + " EUR lunar")
                    .setPricePurple(spannableStringBuilder)
                    .loadImage(shopOrderDetails.getCommerceItems().get(i).getDisplayImageUrl())
                    .hideArrow();
            return card;
       } else {
            int pricePlanContractPeriod = Integer.parseInt(shopOrderDetails.getCommerceItems().get(i).getPricePlanContractPeriod()) / 12;
            card.setTitle(shopOrderDetails.getCommerceItems().get(i).getPhoneDisplayName())
                    .setDetails("Cu prelungire contractuală pe " + pricePlanContractPeriod + " ani")
                    .setPricePurple(spannableStringBuilder)
                    .loadImage(shopOrderDetails.getCommerceItems().get(i).getDisplayImageUrl())
                    .hideArrow();
            return card;
       }
    }

    public ServiceCard buildServiceInfoCard(int i) {

        ServiceCard card = new ServiceCard(getActivity());

        int pricePlanContractPeriod = Integer.parseInt(shopOrderDetails.getCommerceItems().get(i).getPricePlanContractPeriod()) / 12;

        String monthlyPricePlanFee = String.format("%.2f", shopOrderDetails.getCommerceItems().get(0).getBundlePrice());
        monthlyPricePlanFee = monthlyPricePlanFee.replaceAll(",", ".") + " EUR";

        card.setTitle(shopOrderDetails.getCommerceItems().get(i).getPhoneDisplayName())
                .addToDetails(shopOrderDetails.getCommerceItems().get(i).getPricePlanDisplayName() + " prelungire contractuala pe " +
                        pricePlanContractPeriod + " ani " + shopOrderDetails.getCommerceItems().get(i).getPricePlanMonthlyFee()
                        + " EUR lunar")
                .setPrice(monthlyPricePlanFee)
                .setPurplePriceColor()
                .hideArrow();
        return card;
    }

    public OrderHistoryDetailsCard buildPaymentMethodInfoCard() {
        OrderHistoryDetailsCard card = new OrderHistoryDetailsCard(getActivity());
        card.setTitle(OffersLabels.getOrderHistoryDetailsSelectedPaymentMethod())
                .setDetails(shopOrderDetails.getPaymentDetails().getPaymentMethod())
                .addExtraTextGroup(OffersLabels.getOrderHistoryDetailsCustomerAddress(), shopOrderDetails.getPaymentDetails().getAddress(), true, true);
        return card;
    }

    public View buildTotalAmountInfoCard() {

        View v = (View) View.inflate(getActivity(), R.layout.card_order_total_amount, null);

        View discountsSeparatorLine = (View) v.findViewById(R.id.separator_line);
        View orderExchangeRateSeparatorLine = v.findViewById(R.id.second_separator_line);

        VodafoneTextView promotionalDiscountLabel = (VodafoneTextView) v.findViewById(R.id.discount_label);
        VodafoneTextView totalAmountLabel = (VodafoneTextView) v.findViewById(R.id.total_amount_label);
        VodafoneTextView exchangeRateLabel = (VodafoneTextView) v.findViewById(R.id.exchange_rate_label);

        VodafoneTextView promotionalDiscountValue = (VodafoneTextView) v.findViewById(R.id.discount_value);
        VodafoneTextView totalAmountValue = (VodafoneTextView) v.findViewById(R.id.total_amount_value);
        VodafoneTextView exchangeRateValue = (VodafoneTextView) v.findViewById(R.id.exchange_rate_value);

        String discountLabel = "";
        String discountValue = "";
        if(shopOrderDetails.getCostsInfo().getDiscounts().size() != 0) {

            String[] separated = shopOrderDetails.getCostsInfo().getDiscounts().get(0).getName().split("; ");
            Log.d(TAG, separated[1]);
            Log.d(TAG, separated[0]);
            discountLabel = separated[1];

            String regex="([0-9]+[.][0-9]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(separated[0]);

            if(matcher.find()) {
                float discountValueFloat = Float.parseFloat(matcher.group());
                discountValue = new DecimalFormat("0.00").format(discountValueFloat);
                discountValue = "-" + discountValue;
            }


            for (int i = 1 ; i < shopOrderDetails.getCostsInfo().getDiscounts().size(); i++) {
                separated = shopOrderDetails.getCostsInfo().getDiscounts().get(i).getName().split("; ");
                discountLabel = discountLabel + "\n" + separated[1];

                matcher = pattern.matcher(separated[0]);
                if(matcher.find()) {
                    float discountValueFloatAux = Float.parseFloat(matcher.group());
                    discountValue = new DecimalFormat("0.00").format(discountValueFloatAux);
                    discountValue = "-\n" + discountValue;
                }

            }

            discountValue = discountValue + " " + getString(R.string.euro_sign);
            promotionalDiscountLabel.setText(discountLabel);
            promotionalDiscountValue.setText(discountValue);

            promotionalDiscountLabel.setVisibility(View.VISIBLE);
            promotionalDiscountValue.setVisibility(View.VISIBLE);
            discountsSeparatorLine.setVisibility(View.VISIBLE);
        }
        else {
            promotionalDiscountLabel.setVisibility(View.GONE);
            promotionalDiscountValue.setVisibility(View.GONE);
            discountsSeparatorLine.setVisibility(View.GONE);
        }

        if(shopOrderDetails.getCostsInfo().getIntroducedCoupon() != null &&
                shopOrderDetails.getCostsInfo().getCouponValue() != null) {
            String introducedCouponLabel = "Cod promotional";
            String introducedCoupon = introducedCouponLabel + " " + shopOrderDetails.getCostsInfo().getIntroducedCoupon();

            discountLabel = discountLabel + "\n" + introducedCoupon;

            Double couponValue = shopOrderDetails.getCostsInfo().getCouponValue();

            discountValue = discountValue + "\n - " + couponValue + " " + getString(R.string.euro_sign);

            promotionalDiscountLabel.setText(discountLabel);
            promotionalDiscountValue.setText(discountValue);

            promotionalDiscountLabel.setVisibility(View.VISIBLE);
            promotionalDiscountValue.setVisibility(View.VISIBLE);
            discountsSeparatorLine.setVisibility(View.VISIBLE);
        }

        if(shopOrderDetails.getCostsInfo().getNumberOfLPPoints() != null &&
                shopOrderDetails.getCostsInfo().getUsedLPsInEuro() != null) {
            String numberofLP = "Ai folosit" + shopOrderDetails.getCostsInfo().getNumberOfLPPoints() +
                    " puncte de loialitate";

            discountLabel = discountLabel + "\n" + numberofLP;

            Double lPValue = shopOrderDetails.getCostsInfo().getUsedLPsInEuro();

            discountValue = discountValue + "\n - " + lPValue + " " + getString(R.string.euro_sign);

            promotionalDiscountLabel.setText(discountLabel);
            promotionalDiscountValue.setText(discountValue);

            promotionalDiscountLabel.setVisibility(View.VISIBLE);
            promotionalDiscountValue.setVisibility(View.VISIBLE);
            discountsSeparatorLine.setVisibility(View.VISIBLE);
        }


        if(shopOrderDetails.getCostsInfo().getTotal() != null) {
            float orderTotalCost = shopOrderDetails.getCostsInfo().getTotal();
            String orderTotalCostFormatted = new DecimalFormat("0.00").format(orderTotalCost);
            orderTotalCostFormatted += " RON";
            totalAmountLabel.setText(OffersLabels.getOrderHistoryDetailsTotalAmount());
            totalAmountValue.setText(orderTotalCostFormatted);
        }

        if(shopOrderDetails.getCostsInfo().getExchangeRate() != null) {
            String exchangeRate = shopOrderDetails.getCostsInfo().getExchangeRate() + " Lei";
            exchangeRateLabel.setText(OffersLabels.getOrderHistoryDetailsExchangeRate());
            exchangeRateValue.setText(exchangeRate);
        } else {
            orderExchangeRateSeparatorLine.setVisibility(View.GONE);
            exchangeRateLabel.setVisibility(View.GONE);
            exchangeRateValue.setVisibility(View.GONE);
        }

        return v;
    }

    private void configureHeaderStatus() {

        View v = (View) View.inflate(getActivity(), R.layout.order_details_header, null);
        DynamicColorImageView colorImageView = (DynamicColorImageView) v.findViewById(R.id.order_status_iv);

        VodafoneTextView orderStatusTv = (VodafoneTextView) v.findViewById(R.id.order_status_label);
        VodafoneTextView orderPhoneNumber = (VodafoneTextView) v.findViewById(R.id.order_phone_number_label);

        String orderStatus = shopOrderDetails.getDeliveryInfo().getStatus();
        orderStatus = orderStatus.replaceAll("&Icirc;", "Î");

        if(orderStatus.startsWith("I")) {
            orderStatus = orderStatus.replaceFirst("I", "Î");
        }

        orderStatus = orderStatus.replaceAll("&#x103;", "ă");
        String userOwnMsisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        if(userOwnMsisdn.charAt(0) == '4')
            userOwnMsisdn = userOwnMsisdn.substring(1, userOwnMsisdn.length());
        String orderCustomerPhoneNumber = "Comandă pentru numărul: " + userOwnMsisdn;
        Drawable img;
        Log.d(TAG, "before switch:" + orderStatus);
        switch (orderStatus){
            case "Se procesează plata":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.like_48);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);

                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderOrderRequestSent());
                break;
            case "În curs de procesare":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.like_48);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);

                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderOrderRequestSent());
                break;
            case "Anulată":
                Log.d(TAG, "in switch " + orderStatus);
                img =  ContextCompat.getDrawable(getContext(),R.drawable.order_return_product);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);
                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderCancelled());
                break;
            case "Returnată":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.order_return_product);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);
                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderReturned());
                break;
            case "Eroare procesare":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.order_return_product);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);
                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderUnprocessed());
                break;
            case "Livrată":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.delivery_48);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);
                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderDelivered());
                break;
            case "Expediată":
                img =  ContextCompat.getDrawable(getContext(),R.drawable.delivery_48);
                colorImageView.setContextDrawableColor(img,R.color.white);
                colorImageView.setImageDrawable(img);
                orderStatusTv.setText(OffersLabels.getOrderHistoryHeaderSent());
                break;
            default:
                break;
        }

        orderPhoneNumber.setText(orderCustomerPhoneNumber);
        navigationHeader.removeViewFromContainer();
        navigationHeader.addViewToContainer(v);
        navigationHeader.showBannerView();
    }

    private String getMonthStringRepresentation(int month){
        switch (month){
            case 1:
                return "Ian";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "Mai";
            case 6:
                return "Iun";
            case 7:
                return "Iul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "";
    }

    private String formatDate(int dateSingleDigit) {
        return ("0" + dateSingleDigit);
    }



    public static class OrderHistoryDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:order details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:order details");


            s.prop5 = "sales:order history";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
