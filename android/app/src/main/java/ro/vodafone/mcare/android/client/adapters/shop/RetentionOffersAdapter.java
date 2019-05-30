package ro.vodafone.mcare.android.client.adapters.shop;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.DecimalFormat;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.model.shop.ShopPricePlan;
import ro.vodafone.mcare.android.ui.fragments.offers.shop.PricePlanDetailsFragment;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;

/**
 * Created by Victor Radulescu on 4/1/2017.
 */

public class RetentionOffersAdapter extends BaseAdapter {

    private Context context;
    private List<ShopPricePlan> pricePlanList;
    String title;

    public RetentionOffersAdapter(Context context, List<ShopPricePlan> pricePlanList, String title) {
        this.context = context;
        this.pricePlanList = pricePlanList;
        this.title = title;
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

    public List<ShopPricePlan> getPricePlanList() {
        return pricePlanList;
    }

    public void setPricePlanList(List<ShopPricePlan> pricePlanList) {
        this.pricePlanList = pricePlanList;
    }
    private ServiceCard createServiceCard(int position){
        ShopPricePlan shopPricePlan = pricePlanList.get(position);
        Float monthlyFee = 0f;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        try {
            monthlyFee = shopPricePlan.getPricePlanMothlyFee();
        }catch (Exception e){
            e.printStackTrace();
        }

        String monthlyFeeString = decimalFormat.format(monthlyFee);
        monthlyFeeString = monthlyFeeString.replaceAll(",", ".");
        monthlyFeeString = monthlyFeeString +" €  pe lună";
      /*  try {
            monthlyFee = Float.parseFloat(monthlyFeeString);
        }catch (Exception e){
            monthlyFee = 0f;
            e.printStackTrace();
        }*/

        ServiceCard serviceCard = new ServiceCard(getContext())
                .setTitle(shopPricePlan.getPricePlanDisplayName())
                .setPrice(monthlyFeeString)
                .setDetailsFromHtml(shopPricePlan.getPricePlanBenefits(), false, false)
                .setPeriod(getPeriodTextView(shopPricePlan.getPricePlanContractPeriod()));

        serviceCard.setOnClickListener(new RedirectFragmentListener(context, PricePlanDetailsFragment.newInstance(shopPricePlan.getPricePlanSkuId())));
        if(position==0){
            serviceCard.addCardTitle(title, R.color.purple_title_color, null);
        }

        serviceCard.setId(R.id.card_body);
        return serviceCard;

    }

    private String getShopPricePlanPriceText(Float price){
        return (new DecimalFormat("0.00").format(price)+" €  pe lună");
    }
    private SpannableStringBuilder getPeriodTextView(Integer period){
        if(period!=null){
            period = period /12;
            if(period == 1)
                return new SpannableStringBuilder("Perioada contractuala "+period+ " an");
            else
                return new SpannableStringBuilder("Perioada contractuala "+period+ " ani");
        }

        return new SpannableStringBuilder("Perioada contractuala ");
    }
    private String getAfterPhonePriceText(Float price){
        return "Preţ telefon "+String.valueOf(price)+" € ";
    }

}
