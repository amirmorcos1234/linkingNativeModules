package ro.vodafone.mcare.android.client.adapters.shop;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.DecimalFormat;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.model.realm.shop.ShopCurrentSelection;
import ro.vodafone.mcare.android.client.model.shop.RecommendedPricePlan;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Victor Radulescu on 4/7/2017.
 */

public class RetentionRecommendedOffersAdapter extends BaseAdapter {

    private Context context;
    private List<RecommendedPricePlan> pricePlanList;
    String title;
    String currentPricePlanSkuId;


    public RetentionRecommendedOffersAdapter(Context context, List<RecommendedPricePlan> pricePlanList,String title, String currentPricePlanSkuId) {
        this.context = context;
        this.pricePlanList = pricePlanList;
        this.title = title;
        this.currentPricePlanSkuId = currentPricePlanSkuId;
    }

    @Override
    public int getCount() {
        return pricePlanList !=null ? pricePlanList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return pricePlanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = createServiceCard(position);
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<RecommendedPricePlan> getPricePlanList() {
        return pricePlanList;
    }

    public void setPricePlanList(List<RecommendedPricePlan> pricePlanList) {
        this.pricePlanList = pricePlanList;
    }
    private ServiceCard createServiceCard(int position){
        final RecommendedPricePlan recommendedPricePlan = pricePlanList.get(position);
        final String pricePlanSkuId =recommendedPricePlan.getPricePlanSkuId();
        Float monthlyFee = getSmallestPrice(recommendedPricePlan.getBundlePrice(),
                                            recommendedPricePlan.getDiscountedPrice());
        ServiceCard serviceCard = new ServiceCard(getContext())
                .setTitle(recommendedPricePlan.getPricePlanDisplayName())
                .setDetailsFromHtml(recommendedPricePlan.getPricePlanBenefits(),false, false)
                .setPrice(getAfterPhonePriceText(monthlyFee))
                .setPeriod(getAfterPhonePeriodText(recommendedPricePlan.getPricePlanMothlyFee(),
                        recommendedPricePlan.getPricePlanContractPeriod() ) )
                .hideSpacer()
                .hideArrow();
        if(currentPricePlanSkuId.equalsIgnoreCase(recommendedPricePlan.getPricePlanSkuId())){
            serviceCard.addSelectedButton("Abonament selectat", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        setCurrentShopInfo(pricePlanSkuId);
                        ((OffersActivity) context).onBackPressed();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else{
            serviceCard.addUnselectedButton("Selectează abonament",  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        setCurrentShopInfo(pricePlanSkuId);
                        ((OffersActivity) context).onBackPressed();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            });
        }

        if(position==0){
            serviceCard.addCardTitle(title, R.color.purple_title_color, null);
        }
      /*  if(!currentOffersTitleSetet && isFromCurrentServiceOffer(recommendedPricePlan.getPricePlanSkuId())) {
            serviceCard.addCardTitle("Cu abonamentul tău actual", R.color.card_title_purple);
            currentOffersTitleSetet = true;
        }
        if(!otherOffersTitleSetet && !isFromCurrentServiceOffer(recommendedPricePlan.getPricePlanSkuId())){
            serviceCard.addCardTitle("Alte abonamente", R.color.card_title_purple);
            otherOffersTitleSetet = true;
        }*/

        return serviceCard;

    }
    private boolean isFromCurrentServiceOffer(String pricePlanSkuId){
       return pricePlanSkuId!=null && pricePlanSkuId.contains("keep");
    }


    private String getAfterPhonePriceText(Float price){
        if(price==null){
            return "";
        }
        String priceFormatted = "Preţ telefon "+new DecimalFormat("0.00").format(price)+" € ";
        priceFormatted = priceFormatted.replaceAll(",", ".");
        return priceFormatted;
    }
    private Float getSmallestPrice(Float fullPrice,Float discount){
        return fullPrice>discount?discount:fullPrice;
    }

    private SpannableStringBuilder getAfterPhonePeriodText(Float monthlyFee, Integer period){
        if(monthlyFee==null){
            return new SpannableStringBuilder("");
        }
        String priceText = new DecimalFormat("0.00").format(monthlyFee)+" € pe lună";
        priceText = priceText.replaceAll(",", ".");
        period = period/12;

        String finalText;
        if(period <= 1) {
            finalText = priceText + " cu contract pe " + period + " an";
        }
        else {
            finalText = priceText + " cu contract pe " + period + " ani";
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 0, priceText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
    }
    private void setCurrentShopInfo(String pricePlanSkuId){
        RealmManager.startTransaction();
        ShopCurrentSelection shopCurrentSelection = new ShopCurrentSelection();
        shopCurrentSelection.setPricePlanSkuId(pricePlanSkuId);
        RealmManager.update(shopCurrentSelection);
    }
}
