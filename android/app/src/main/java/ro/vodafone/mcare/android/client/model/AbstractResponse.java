package ro.vodafone.mcare.android.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by user on 28.12.2016.
 */
public class AbstractResponse implements Serializable{

    @SerializedName("status")
    @Expose
    private Long status;

    /**
     *
     * @return
     * The status
     */
    public Long getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Long status) {
        this.status = status;
    }

}
