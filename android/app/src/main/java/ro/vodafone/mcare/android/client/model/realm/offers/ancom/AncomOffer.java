/**
 * API-42 VOS Offers for Ancom
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.offers.ancom;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ancom offer bean
 **/
@ApiModel(description = "ancom offer bean")
public class AncomOffer extends RealmObject {
  
  @SerializedName("proposalId")
  private String proposalId = null;
  @SerializedName("offerId")
  private String offerId = null;
  @SerializedName("msisdn")
  private String msisdn = null;
  @SerializedName("contractDuration")
  private String contractDuration = null;
  @SerializedName("summary")
  private String summary = null;
  @SerializedName("creationDate")
  private Long creationDate = null;
  @SerializedName("termsUrl")
  private String termsUrl = null;
  @SerializedName("specificUrl")
  private String specificUrl = null;
  @SerializedName("isRefuseEnabled")
  private Boolean isRefuseEnabled = null;
  @SerializedName("contractDetails")
  private String contractDetails = null;

  /**
   * value from VOS response .proposalId
   **/
  @ApiModelProperty(value = "value from VOS response .proposalId")
  public String getProposalId() {
    return proposalId;
  }
  public void setProposalId(String proposalId) {
    this.proposalId = proposalId;
  }

  /**
   * value from VOS response .offerId
   **/
  @ApiModelProperty(value = "value from VOS response .offerId")
  public String getOfferId() {
    return offerId;
  }
  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  /**
   * msisdn value extracted from VOS response based on delimiter
   **/
  @ApiModelProperty(value = "msisdn value extracted from VOS response based on delimiter")
  public String getMsisdn() {
    return msisdn;
  }
  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  /**
   * contractDuration value extracted from VOS representing months
   **/
  @ApiModelProperty(value = "contractDuration value extracted from VOS representing months")
  public String getContractDuration() {
    return contractDuration;
  }
  public void setContractDuration(String contractDuration) {
    this.contractDuration = contractDuration;
  }

  /**
   * description value extracted from VOS response
   **/
  @ApiModelProperty(value = "description value extracted from VOS response")
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * creationDate from VOS sent in Unix Time
   **/
  @ApiModelProperty(value = "creationDate from VOS sent in Unix Time")
  public Long getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * termsUrl read from configuration file
   **/
  @ApiModelProperty(value = "termsUrl read from configuration file")
  public String getTermsUrl() {
    return termsUrl;
  }
  public void setTermsUrl(String termsUrl) {
    this.termsUrl = termsUrl;
  }

  /**
   * specificUrl read from configuration file
   **/
  @ApiModelProperty(value = "specificUrl read from configuration file")
  public String getSpecificUrl() {
    return specificUrl;
  }
  public void setSpecificUrl(String specificUrl) {
    this.specificUrl = specificUrl;
  }

  /**
   * isRefuseEnabled booleand read from configuration file (show or hide button)
   **/
  @ApiModelProperty(value = "isRefuseEnabled booleand read from configuration file (show or hide button)")
  public Boolean getIsRefuseEnabled() {
    return isRefuseEnabled;
  }
  public void setIsRefuseEnabled(Boolean isRefuseEnabled) {
    this.isRefuseEnabled = isRefuseEnabled;
  }

  public String getContractDetails() {
    return contractDetails;
  }
  public void setContractDetails(String contractDetails) {
    this.contractDetails = contractDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AncomOffer ancomOffer = (AncomOffer) o;
    return (this.proposalId == null ? ancomOffer.proposalId == null : this.proposalId.equals(ancomOffer.proposalId)) &&
            (this.offerId == null ? ancomOffer.offerId == null : this.offerId.equals(ancomOffer.offerId)) &&
            (this.msisdn == null ? ancomOffer.msisdn == null : this.msisdn.equals(ancomOffer.msisdn)) &&
            (this.contractDuration == null ? ancomOffer.contractDuration == null : this.contractDuration.equals(ancomOffer.contractDuration)) &&
            (this.summary == null ? ancomOffer.summary == null : this.summary.equals(ancomOffer.summary)) &&
            (this.creationDate == null ? ancomOffer.creationDate == null : this.creationDate.equals(ancomOffer.creationDate)) &&
            (this.termsUrl == null ? ancomOffer.termsUrl == null : this.termsUrl.equals(ancomOffer.termsUrl)) &&
            (this.specificUrl == null ? ancomOffer.specificUrl == null : this.specificUrl.equals(ancomOffer.specificUrl)) &&
            (this.isRefuseEnabled == null ? ancomOffer.isRefuseEnabled == null : this.isRefuseEnabled.equals(ancomOffer.isRefuseEnabled)) &&
            (this.contractDetails == null ? ancomOffer.contractDetails == null : this.contractDetails.equals(ancomOffer.contractDetails));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.proposalId == null ? 0: this.proposalId.hashCode());
    result = 31 * result + (this.offerId == null ? 0: this.offerId.hashCode());
    result = 31 * result + (this.msisdn == null ? 0: this.msisdn.hashCode());
    result = 31 * result + (this.contractDuration == null ? 0: this.contractDuration.hashCode());
    result = 31 * result + (this.summary == null ? 0: this.summary.hashCode());
    result = 31 * result + (this.creationDate == null ? 0: this.creationDate.hashCode());
    result = 31 * result + (this.termsUrl == null ? 0: this.termsUrl.hashCode());
    result = 31 * result + (this.specificUrl == null ? 0: this.specificUrl.hashCode());
    result = 31 * result + (this.isRefuseEnabled == null ? 0: this.isRefuseEnabled.hashCode());
    result = 31 * result + (this.contractDetails == null ? 0: this.contractDetails.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AncomOffer {\n");
    
    sb.append("  proposalId: ").append(proposalId).append("\n");
    sb.append("  offerId: ").append(offerId).append("\n");
    sb.append("  msisdn: ").append(msisdn).append("\n");
    sb.append("  contractDuration: ").append(contractDuration).append("\n");
    sb.append("  summary: ").append(summary).append("\n");
    sb.append("  creationDate: ").append(creationDate).append("\n");
    sb.append("  termsUrl: ").append(termsUrl).append("\n");
    sb.append("  specificUrl: ").append(specificUrl).append("\n");
    sb.append("  isRefuseEnabled: ").append(isRefuseEnabled).append("\n");
    sb.append("  contractDetails: ").append(contractDetails).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
