package ro.vodafone.mcare.android.client.model.identity;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Serban Radulescu on 7/3/2018.
 */

@ApiModel(description = "Object containing data associated with BEN from FADistribution WS")
public class BenSuccessEbu {
	@SerializedName("vfOdsBen")
	private String vfOdsBen = null;

	/**
	 * ben from
	 **/
	@ApiModelProperty(required = true, value = "ben from")
	public String getVfOdsBen() {
		return vfOdsBen;
	}
	public void setVfOdsBen(String vfOdsBen) {
		this.vfOdsBen = vfOdsBen;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BenSuccessEbu bENSuccessEBU = (BenSuccessEbu) o;
		return (this.vfOdsBen == null ? bENSuccessEBU.vfOdsBen == null : this.vfOdsBen.equals(bENSuccessEBU.vfOdsBen));
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (this.vfOdsBen == null ? 0: this.vfOdsBen.hashCode());
		return result;
	}

	@Override
	public String toString()  {
		StringBuilder sb = new StringBuilder();
		sb.append("class BENSuccessEBU {\n");

		sb.append("  vfOdsBen: ").append(vfOdsBen).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
