package ro.vodafone.mcare.android.client.model.simReplace;

import com.google.gson.annotations.SerializedName;

public class SIMReplaceSuccess {

    @SerializedName("success")
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
