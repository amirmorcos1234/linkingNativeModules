package ro.vodafone.mcare.android.client.model.recover;

/**
 * Created by Bogdan Marica on 8/28/2017.
 */

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response object with specific information about step and user.
 **/
@ApiModel(description = "Response object with specific information about step and user.")
public class RecoverUsernameResponse {

    @SerializedName("step")
    private String step = null;
    @SerializedName("status")
    private String status = null;

    /**
     * Response Step From IDM
     **/
    @ApiModelProperty(value = "Response Step From IDM")
    public String getStep() {
        return step;
    }
    public void setStep(String step) {
        this.step = step;
    }

    /**
     * IDM status for Recovery Username
     **/
    @ApiModelProperty(value = "IDM status for Recovery Username")
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoverUsernameResponse recoverUsernameResponse = (RecoverUsernameResponse) o;
        return (this.step == null ? recoverUsernameResponse.step == null : this.step.equals(recoverUsernameResponse.step)) &&
                (this.status == null ? recoverUsernameResponse.status == null : this.status.equals(recoverUsernameResponse.status));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.step == null ? 0: this.step.hashCode());
        result = 31 * result + (this.status == null ? 0: this.status.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class RecoverUsernameResponse {\n");

        sb.append("  step: ").append(step).append("\n");
        sb.append("  status: ").append(status).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}