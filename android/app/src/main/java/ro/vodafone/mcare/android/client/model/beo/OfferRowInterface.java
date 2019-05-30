package ro.vodafone.mcare.android.client.model.beo;

import java.io.Serializable;

/**
 * Created by Alex on 3/17/2017.
 */

public interface OfferRowInterface extends Serializable{

    public static final String KEY_ID = "OfferRowInterface";

    Long getOfferId();

    void setOfferId(Long offerId);

    String getOfferName();

    Double getOfferPrice();

    String getOfferShortDescription();

    String getOfferCategory();
}
