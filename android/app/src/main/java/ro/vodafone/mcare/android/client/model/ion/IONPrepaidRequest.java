package ro.vodafone.mcare.android.client.model.ion;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class IONPrepaidRequest {

    @SerializedName("offerId")
    private String offerId = null;

    @SerializedName("ionPhoneNo")
    private String ionPhoneNo = null;

    public IONPrepaidRequest offerId(String offerId) {
        this.offerId = offerId;
        return this;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public IONPrepaidRequest ionPhoneNo(String ionPhoneNo) {
        this.ionPhoneNo = ionPhoneNo;
        return this;
    }

    public String getIonPhoneNo() {
        return ionPhoneNo;
    }

    public void setIonPhoneNo(String ionPhoneNo) {
        this.ionPhoneNo = ionPhoneNo;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IONPrepaidRequest ioNPrepaidRequest = (IONPrepaidRequest) o;
        return Objects.equals(this.offerId, ioNPrepaidRequest.offerId) &&
                Objects.equals(this.ionPhoneNo, ioNPrepaidRequest.ionPhoneNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerId, ionPhoneNo);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IONPrepaidRequest {\n");

        sb.append("    offerId: ").append(toIndentedString(offerId)).append("\n");
        sb.append("    ionPhoneNo: ").append(toIndentedString(ionPhoneNo)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
