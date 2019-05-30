package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Serban Radulescu on 5/4/2018.
 */

public class ReserveVoucherRequest {

	@SerializedName("voucherName")
	private String voucherName;

	@SerializedName("loyaltySegment")
	private String loyaltySegment;

	@SerializedName("campaignExpiryDate")
	private long campaignExpiryDate;

	@SerializedName("treatmentSegment")
	private String treatmentSegment;

	public ReserveVoucherRequest(String voucherName, String loyaltySegment, long campaignExpiryDate, String treatmentSegment) {
		this.voucherName = voucherName;
		this.loyaltySegment = loyaltySegment;
		this.campaignExpiryDate = campaignExpiryDate;
		this.treatmentSegment = treatmentSegment;
	}
}
