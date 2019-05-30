package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

/**
 * Created by user on 26.10.2017.
 */

public enum CategoryEnum {

    NATIONAL("N", "Naționale"),
    INTERNATIONAL("I", "Internațional"),
    ROAMING("R", "Roaming");

    private String shortCategoryName;
    private String categoryName;


    public String getShortCategoryName() {
        return shortCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    CategoryEnum(String shortCategoryName, String categoryName) {
        this.shortCategoryName = shortCategoryName;
        this.categoryName = categoryName;
    }

    public static CategoryEnum getCategoryByString(String category) {
        for (CategoryEnum ts : values()) {
            if (ts.shortCategoryName.equals(category)) return ts;
        }
        return null;
    }
}
