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
public class RecoverPasswordResponse {

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
     * IDM status for recovery Password
     **/
    @ApiModelProperty(value = "IDM status for recovery Password")
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
        RecoverPasswordResponse recoverPasswordResponse = (RecoverPasswordResponse) o;
        return (this.step == null ? recoverPasswordResponse.step == null : this.step.equals(recoverPasswordResponse.step)) &&
                (this.status == null ? recoverPasswordResponse.status == null : this.status.equals(recoverPasswordResponse.status));
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
        sb.append("class RecoverPasswordResponse {\n");

        sb.append("  step: ").append(step).append("\n");
        sb.append("  status: ").append(status).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
