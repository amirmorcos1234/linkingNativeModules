package ro.vodafone.mcare.android.ui.activities.offers;

/**
 * Created by Alex on 3/22/2017.
 */

public enum ActivationPayTypeEnum {

    SIMPLE(0), CREDIT(1), CARD(2);

    private int id;

    ActivationPayTypeEnum(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}