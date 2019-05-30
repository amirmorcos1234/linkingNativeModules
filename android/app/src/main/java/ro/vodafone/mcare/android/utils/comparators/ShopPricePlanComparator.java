package ro.vodafone.mcare.android.utils.comparators;

import java.util.Comparator;
import java.util.Objects;

import ro.vodafone.mcare.android.client.model.shop.ShopPricePlan;

/**
 * Created by Victor Radulescu on 4/20/2017.
 */

public class ShopPricePlanComparator implements Comparator<ShopPricePlan> {

    boolean ascending = true;

    public ShopPricePlanComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(ShopPricePlan shopPricePlan1, ShopPricePlan shopPricePlan2) {
        int trueValue = ascending ? -1:1;
        int falseValue = ascending ? 1:-1;
        if(Objects.equals(shopPricePlan1.getPricePlanMothlyFee(), shopPricePlan2.getPricePlanMothlyFee()))
            return 0;
        return shopPricePlan1.getPricePlanMothlyFee()< shopPricePlan2.getPricePlanMothlyFee()? trueValue:falseValue;
    }
}
