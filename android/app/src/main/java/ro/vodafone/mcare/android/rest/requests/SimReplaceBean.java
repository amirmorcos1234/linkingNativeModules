package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

public class SimReplaceBean {

    @SerializedName("seriaSIM")
    private String seriaSIM;

    @SerializedName("contactNumber")
    private String contactNumber;

    @SerializedName("banId")
    private String banId;

    @SerializedName("treatmentSegment")
    private String treatmentSegment;

    @SerializedName("customerSegment")
    private String customerSegment;

    @SerializedName("vfOdsCid")
    private String vfOdsCid;

    @SerializedName("vfOdsSid")
    private String vfOdsSid;

    @SerializedName("crmRole")
    private String crmRole;

    public SimReplaceBean(String seriaSIM, String contactNumber, String banId, String treatmentSegment, String customerSegment, String vfOdsCid, String vfOdsSid, String crmRole) {
        this.seriaSIM = seriaSIM;
        this.contactNumber = contactNumber;
        this.banId = banId;
        this.treatmentSegment = treatmentSegment;
        this.customerSegment = customerSegment;
        this.vfOdsCid = vfOdsCid;
        this.vfOdsSid = vfOdsSid;
        this.crmRole = crmRole;
    }

    public String getSeriaSIM() {
        return seriaSIM;
    }

    public void setSeriaSIM(String seriaSIM) {
        this.seriaSIM = seriaSIM;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getBanId() {
        return banId;
    }

    public void setBanId(String banId) {
        this.banId = banId;
    }

    public String getTreatmentSegment() {
        return treatmentSegment;
    }

    public void setTreatmentSegment(String treatmentSegment) {
        this.treatmentSegment = treatmentSegment;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public String getVfOdsCid() {
        return vfOdsCid;
    }

    public void setVfOdsCid(String vfOdsCid) {
        this.vfOdsCid = vfOdsCid;
    }

    public String getVfOdsSid() {
        return vfOdsSid;
    }

    public void setVfOdsSid(String vfOdsSid) {
        this.vfOdsSid = vfOdsSid;
    }

    public String getCrmRole() {
        return crmRole;
    }

    public void setCrmRole(String crmRole) {
        this.crmRole = crmRole;
    }
}
