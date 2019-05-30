package ro.vodafone.mcare.android.client.adapters.shop;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.DecimalFormat;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.offers.PhoneCard;
import ro.vodafone.mcare.android.client.model.shop.ShopProduct;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PhoneDetailsFragment;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;

/**
 * Created by Victor Radulescu on 4/3/2017.
 */

public class PhoneOffersAdapter  extends BaseAdapter {

    private Context context;
    private List<ShopProduct> shopProducts;
    String title;


    public PhoneOffersAdapter(Context context, List<ShopProduct> shopProducts, String title) {
        this.context = context;
        this.shopProducts = shopProducts;
        this.title = title;
    }

    @Override
    public int getCount() {
        return shopProducts!=null ? shopProducts.size():0;
    }

    @Override
    public Object getItem(int position) {
        return shopProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = createPhoneCard(position);
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ShopProduct> getShopProducts() {
        return shopProducts;
    }

    public void setShopProducts(List<ShopProduct> shopProducts) {
        this.shopProducts = shopProducts;
    }
    private PhoneCard createPhoneCard(int position){
        ShopProduct shopPricePlan = shopProducts.get(position);
        String details;

        //check if the price plan is of keep type
        //if yes, then the monthly fee is not displayed
        if(!shopPricePlan.getPricePlanSkuId().contains("keep")){
            details = "Cu "+shopPricePlan.getPricePlanDisplayName()
                    +" prelungire contractuală pe "+getFormatPeriod(shopPricePlan.getPricePlanContractPeriod());

            DecimalFormat decimalFormat =new DecimalFormat("0.00");
            String bundlePriceText = decimalFormat.format(shopPricePlan.getPricePlanMothlyFee());
            bundlePriceText = bundlePriceText.replaceAll(",", ".");
            details = details + " " + bundlePriceText + " € lunar";
        }
        else
        {
            details = "Prelungirea "+shopPricePlan.getPricePlanDisplayName()
                +" abonamentului actual cu păstrarea beneficiilor pe "+getFormatPeriod(shopPricePlan.getPricePlanContractPeriod());
        }

        SpannableStringBuilder phonePriceText= getFormatPhonePrice(shopPricePlan.getDiscountedPrice(),shopPricePlan.getBundlePrice());

        PhoneCard serviceCard = new PhoneCard((context != null) ? context : getContext())
                .setTitle(shopPricePlan.getPhoneDisplayName())
                .setDetails(details)
                .setPrice(phonePriceText)
                .loadImage(shopPricePlan.getDefaultImageUrl());
        if(position==0){
            serviceCard.addCardTitle(title, R.color.purple_title_color, null);
        }

        serviceCard.setId(R.id.card_body);

        Bundle  bundle = new Bundle();
        bundle.putString(ShopProduct.PHONE_SKU_ID,shopPricePlan.getPhoneSkuId());
        bundle.putString(ShopProduct.PRICE_PLAN_SKU_ID,shopPricePlan.getPricePlanSkuId());

        serviceCard.setOnClickListener(new RedirectFragmentListener(getContext(), PhoneDetailsFragment.newInstance(bundle)));

        return serviceCard;

    }

    private SpannableStringBuilder getFormatPhonePrice(Float discountedPrice, Float bundlePrice) {
        DecimalFormat decimalFormat =new DecimalFormat("0.00");

        String bundlePriceText = decimalFormat.format(bundlePrice) + "€";
        String discountedPriceText = decimalFormat.format(discountedPrice) + "€";
        bundlePriceText = bundlePriceText.replaceAll(",", ".");
        discountedPriceText = discountedPriceText.replaceAll(",", ".");
        if(discountedPrice<bundlePrice){
            String finalText = "Preț telefon: " +bundlePriceText+ " "+discountedPriceText;

            SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

            StrikethroughSpan strykeSpannable = new StrikethroughSpan();
            sb.setSpan(strykeSpannable, finalText.indexOf(bundlePriceText),finalText.indexOf(bundlePriceText)+ bundlePriceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            return sb;
        }
        String finalText = "Preț telefon: " +bundlePriceText;
        return new SpannableStringBuilder(finalText);
    }

    private String getFormatPeriod(int period){
        if(period==12){
            return "1 ani";
        }else{
            return "2 ani";
        }
    }
}
