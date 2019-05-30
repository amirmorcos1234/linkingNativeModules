package ro.vodafone.mcare.android.client.model.profile;

/**
 * Created by Serban Radulescu on 1/9/2018.
 */


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * object containing data associated with successful transaction
 **/
@ApiModel(description = "object containing data associated with successful transaction")
public class ProfileSubscriptionSuccess extends RealmObject {

    @PrimaryKey
    private long id;

    @SerializedName("isVDFSubscription")
    private Boolean isVDFSubscription = null;

    public ProfileSubscriptionSuccess() {
        this.id = 1;
    }

    /**
     * flag if provided msisdn is vdf subscription.
     **/
    @ApiModelProperty(value = "flag if provided msisdn is vdf subscription.")
    public Boolean getIsVDFSubscription() {
        return isVDFSubscription;
    }
    public void setIsVDFSubscription(Boolean isVDFSubscription) {
        this.isVDFSubscription = isVDFSubscription;
    }
}

