package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bivol Pavel on 16.01.2017.
 */
public class SeamlessFlagRequest extends BaseRequest{

    @SerializedName("seamlessFlag")
    private boolean seamlessFlag;

    public SeamlessFlagRequest(boolean seamlessFlag) {
        this.seamlessFlag = seamlessFlag;
    }

    public boolean isSeamlessFlag() {
        return seamlessFlag;
    }

    public void setSeamlessFlag(boolean seamlessFlag) {
        this.seamlessFlag = seamlessFlag;
    }

    public boolean getSeamlessFlag() {
        return seamlessFlag;
    }

}
