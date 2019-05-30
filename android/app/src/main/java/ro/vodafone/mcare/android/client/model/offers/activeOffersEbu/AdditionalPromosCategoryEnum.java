package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

/**
 * Created by Bivol Pavel on 23.10.2017.
 */

public enum AdditionalPromosCategoryEnum {

    BONUSURI("Bonusuri"),
    ROAMING("Roaming"),
    MINUTE_SI_MESAJE("Minute și mesaje"),
    INTERNATIONAL("Internațional"),
    DATE("Date"),
    PROMOTII_CU_PUNCTE("Promoții cu puncte"),
    SERVICII("Servicii"),
    ALTELE("Altele");

    private String category;

    private AdditionalPromosCategoryEnum(String category){
        this.category = category;
    }

    public static AdditionalPromosCategoryEnum getCategoryByString(String category) {
        for (AdditionalPromosCategoryEnum ts : values()) {
            if (ts.category.equals(category)) return ts;
        }
        return null;
    }

    /*public static int getaAtiveOptionsPriority(String category) {
        for (AdditionalPromosCategoryEnum ts : values()) {
            if (ts.category.equals(category)) return ts.activeOptionsPriority;
        }
        throw new IllegalArgumentException();
    }

    public static int getPendingOffersPriority(String category) {
        for (AdditionalPromosCategoryEnum ts : values()) {
            if (ts.category.equals(category)) return ts.pendingOffersPriority;
        }
        throw new IllegalArgumentException();
    }*/

}
