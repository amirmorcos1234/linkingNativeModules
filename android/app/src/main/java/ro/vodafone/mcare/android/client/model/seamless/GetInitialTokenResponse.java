package ro.vodafone.mcare.android.client.model.seamless;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ro.vodafone.mcare.android.client.model.AbstractResponse;

/**
 * Created by Bivol Pavel on 28.12.2016.
 */
public class GetInitialTokenResponse extends AbstractResponse implements Serializable{

    @SerializedName("initialToken")
    @Expose
    private String initialToken;

    /**
     *
     * @return
     * The initialToken
     */
    public String getInitialToken() {
        return initialToken;
    }

    /**
     *
     * @param initialToken
     * The status
     */
    public void setInitialToken(String initialToken) {
        this.initialToken = initialToken;
    }
}
