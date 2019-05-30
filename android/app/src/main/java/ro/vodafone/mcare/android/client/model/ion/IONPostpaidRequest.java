package ro.vodafone.mcare.android.client.model.ion;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class IONPostpaidRequest {

    @SerializedName("offerId")
    private String offerId = null;

    @SerializedName("ionPhoneNo")
    private String ionPhoneNo = null;

    @SerializedName("selectedMsisdn")
    private String selectedMsisdn = null;

    @SerializedName("selectedSid")
    private String selectedSid = null;

    public IONPostpaidRequest offerId(String offerId) {
        this.offerId = offerId;
        return this;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public IONPostpaidRequest ionPhoneNo(String ionPhoneNo) {
        this.ionPhoneNo = ionPhoneNo;
        return this;
    }

    public String getIonPhoneNo() {
        return ionPhoneNo;
    }

    public void setIonPhoneNo(String ionPhoneNo) {
        this.ionPhoneNo = ionPhoneNo;
    }

    public IONPostpaidRequest selectedMsisdn(String selectedMsisdn) {
        this.selectedMsisdn = selectedMsisdn;
        return this;
    }

    public String getSelectedMsisdn() {
        return selectedMsisdn;
    }

    public void setSelectedMsisdn(String selectedMsisdn) {
        this.selectedMsisdn = selectedMsisdn;
    }

    public IONPostpaidRequest selectdSid(String selectdSid) {
        this.selectedSid = selectdSid;
        return this;
    }

    public String getSelectdSid() {
        return selectedSid;
    }

    public void setSelectdSid(String selectdSid) {
        this.selectedSid = selectdSid;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IONPostpaidRequest ioNPostpaidRequest = (IONPostpaidRequest) o;
        return Objects.equals(this.offerId, ioNPostpaidRequest.offerId) &&
                Objects.equals(this.ionPhoneNo, ioNPostpaidRequest.ionPhoneNo) &&
                Objects.equals(this.selectedMsisdn, ioNPostpaidRequest.selectedMsisdn) &&
                Objects.equals(this.selectedSid, ioNPostpaidRequest.selectedSid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerId, ionPhoneNo, selectedMsisdn, selectedSid);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IONPostpaidRequest {\n");

        sb.append("    offerId: ").append(toIndentedString(offerId)).append("\n");
        sb.append("    ionPhoneNo: ").append(toIndentedString(ionPhoneNo)).append("\n");
        sb.append("    selectedMsisdn: ").append(toIndentedString(selectedMsisdn)).append("\n");
        sb.append("    selectedSid: ").append(toIndentedString(selectedSid)).append("\n");
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
