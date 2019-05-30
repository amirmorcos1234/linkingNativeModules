package ro.vodafone.mcare.android.ui.fragments.Beo.adapter;

/**
 * Created by Bivol Pavel on 23.10.2017.
 */

@Deprecated
public enum BeoCategoryEnum {

    ROAMING("Roaming"),
    MINUTE_SI_MESAJE("Minute și mesaje"),
    INTERNATIONAL("Internaţional"),
    DATE("Date"),
    BONUSURI("Bonusuri"),
    ALTELE("Altele");

    private String category;

    private BeoCategoryEnum(String category){
        this.category = category;
    }

    public static BeoCategoryEnum getCategoryByString(String category) {
        for (BeoCategoryEnum ts : values()) {
            if (ts.category.equals(category)) return ts;
        }
        return null;
    }
}
