package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.orderHistory.OrderHistoryCard;
import ro.vodafone.mcare.android.client.model.realm.orders.ShopOrder;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.OrderHistoryDetailsFragment;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;

/**
 * Created by user2 on 4/20/2017.
 */

public class OrdersHistoryAdapter extends BaseAdapter {

    private static final String TAG = "OrdersHAdapter";
    private Context context;
    private List<ShopOrder> shopOrderList;


    public OrdersHistoryAdapter(Context context, List<ShopOrder> shopProducts) {
        this.context = context;
        this.shopOrderList = shopProducts;
    }

    @Override
    public int getCount() {
        return shopOrderList!=null ? shopOrderList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return shopOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = createOrderHistoryCard(position);
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ShopOrder> getShopOrders() {
        return shopOrderList;
    }

    public void setShopOrders(List<ShopOrder> shopOrderList) {
        this.shopOrderList = shopOrderList;
    }
    private OrderHistoryCard createOrderHistoryCard(int position){
        ShopOrder shopOrder = shopOrderList.get(position);


        String orderDateSubmitted = shopOrder.getDeliveryInfo().getDelivered();
        String orderDateFormated;
        if(orderDateSubmitted != null) {
            Date date = null;
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {

                date = format.parse(orderDateSubmitted);
                calendar.setTime(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String month = (calendar.get(Calendar.MONTH) + 1) + "";
            String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
                day = formatDate(calendar.get(Calendar.DAY_OF_MONTH));
            if ((calendar.get(Calendar.MONTH) + 1) < 10)
                month = formatDate(calendar.get(Calendar.MONTH) + 1);

            orderDateFormated = day + "." +month + "."+calendar.get(Calendar.YEAR);
        }
        else {
            orderDateFormated = "No date available";
        }
        String orderStatus = shopOrder.getDeliveryInfo().getStatus();
        orderStatus = orderStatus.replaceAll("&#x103;", "ă");
        orderStatus = orderStatus.replaceAll("&Icirc;", "Î");
        float orderTotal = shopOrder.getCostsInfo().getTotal();
        String orderTotalFormatted = String.format("%.2f", orderTotal);

        OrderHistoryCard orderHistoryCard = new OrderHistoryCard(getContext())
                .setOrderNumber(shopOrder.getOrderId())
                .setOrderStatus(orderStatus)
                .setOrderDate(orderDateFormated)
                .setOrderCost(orderTotalFormatted);

        String orderId = shopOrder.getOrderId();
        OffersFragment orderHistoryDetailsFragment = new OrderHistoryDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("order_id", orderId);
        orderHistoryDetailsFragment.setArguments(bundle);
        orderHistoryCard.setOnClickListener(new RedirectFragmentListener(context, orderHistoryDetailsFragment));


        orderHistoryCard.setId(R.id.card_body);
        return orderHistoryCard;

    }

    private String getShopPricePlanPriceText(Float price){
        return String.valueOf(price)+" € pe lună";
    }
    private String getAfterPhonePriceText(Float price){
        return "Preţ telefon "+String.valueOf(price)+" € ";
    }

    private String formatDate(int dateSingleDigit) {
        return ("0" + dateSingleDigit);
    }
}
