package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Serban Radulescu on 4/12/2018.
 */

public class SIMChangeStatusRequestEBU {

	@SerializedName("phoneNumber")
	private String phoneNumber = null;
	@SerializedName("vfOdsSid")
	private String vfOdsSid = null;
	@SerializedName("vfOdsCid")
	private String vfOdsCid = null;
	@SerializedName("vfOdsBan")
	private String vfOdsBan = null;
	@SerializedName("productId")
	private String productId = null;
	@SerializedName("productSpecName")
	private String productSpecName = null;
	@SerializedName("treatmentSegment")
	private String treatmentSegment = null;
	@SerializedName("emailVoiceSpecialist")
	private String emailVoiceSpecialist = null;

	public SIMChangeStatusRequestEBU(String phoneNumber, String vfOdsSid, String vfOdsCid, String vfOdsBan, String productId, String productSpecName, String treatmentSegment, String emailVoiceSpecialist) {
		this.phoneNumber = phoneNumber;
		this.vfOdsSid = vfOdsSid;
		this.vfOdsCid = vfOdsCid;
		this.vfOdsBan = vfOdsBan;
		this.productId = productId;
		this.productSpecName = productSpecName;
		this.treatmentSegment = treatmentSegment;
		this.emailVoiceSpecialist = emailVoiceSpecialist;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getVfOdsSid() {
		return vfOdsSid;
	}

	public void setVfOdsSid(String vfOdsSid) {
		this.vfOdsSid = vfOdsSid;
	}

	public String getVfOdsCid() {
		return vfOdsCid;
	}

	public void setVfOdsCid(String vfOdsCid) {
		this.vfOdsCid = vfOdsCid;
	}

	public String getVfOdsBan() {
		return vfOdsBan;
	}

	public void setVfOdsBan(String vfOdsBan) {
		this.vfOdsBan = vfOdsBan;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductSpecName() {
		return productSpecName;
	}

	public void setProductSpecName(String productSpecName) {
		this.productSpecName = productSpecName;
	}

	public String getTreatmentSegment() {
		return treatmentSegment;
	}

	public void setTreatmentSegment(String treatmentSegment) {
		this.treatmentSegment = treatmentSegment;
	}

	public String getEmailVoiceSpecialist() {
		return emailVoiceSpecialist;
	}

	public void setEmailVoiceSpecialist(String emailVoiceSpecialist) {
		this.emailVoiceSpecialist = emailVoiceSpecialist;
	}
}
