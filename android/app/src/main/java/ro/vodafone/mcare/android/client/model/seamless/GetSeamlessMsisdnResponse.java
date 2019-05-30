package ro.vodafone.mcare.android.client.model.seamless;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ro.vodafone.mcare.android.client.model.AbstractResponse;

/**
 * Created by user on 28.12.2016.
 */
public class GetSeamlessMsisdnResponse extends AbstractResponse implements Serializable {

    @SerializedName("seamlessMsisdn")
    @Expose
    private String seamlessMsisdn;

    /**
     *
     * @return
     * The seamlessMsisdn
     */
    public String getSeamlessMsisdnn() {
        return seamlessMsisdn;
    }

    /**
     *
     * @param seamlessMsisdn
     * The status
     */
    public void setSeamlessMsisdn(String seamlessMsisdn) {
        this.seamlessMsisdn = seamlessMsisdn;
    }

}
