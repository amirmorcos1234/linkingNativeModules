package ro.vodafone.mcare.android.utils.comparators;

import java.util.Comparator;
import java.util.Objects;

import ro.vodafone.mcare.android.client.model.shop.ShopProduct;

/**
 * Created by Victor Radulescu on 4/20/2017.
 */

public class ShopProductComparator implements Comparator<ShopProduct> {

    boolean ascending = true;

    public ShopProductComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(ShopProduct shopProduct1, ShopProduct shopProduct2) {
        int trueValue = ascending ? -1:1;
        int falseValue = ascending ? 1:-1;
        if(Objects.equals(shopProduct1.getDiscountedPrice(), shopProduct2.getDiscountedPrice()))
            return 0;
        return shopProduct1.getDiscountedPrice()< shopProduct2.getDiscountedPrice()? trueValue:falseValue;
    }
}