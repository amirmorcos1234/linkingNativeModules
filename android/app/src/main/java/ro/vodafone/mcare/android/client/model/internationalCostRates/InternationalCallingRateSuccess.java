/*
 * API-67 International Calling rates
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 10.0.5
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ro.vodafone.mcare.android.client.model.internationalCostRates;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * Object containing data associated with successful transaction
 */
@ApiModel(description = "Object containing data associated with successful transaction")
public class InternationalCallingRateSuccess {
  @SerializedName("rate")
  private Float rate = null;

  public InternationalCallingRateSuccess rate(Float rate) {
    this.rate = rate;
    return this;
  }

   /**
   * Rate per minute
   * @return rate
  **/
  @ApiModelProperty(value = "Rate per minute")
  public Float getRate() {
    return rate;
  }

  public void setRate(Float rate) {
    this.rate = rate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InternationalCallingRateSuccess internationalCallingRateSuccess = (InternationalCallingRateSuccess) o;
    return Objects.equals(this.rate, internationalCallingRateSuccess.rate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InternationalCallingRateSuccess {\n");
    
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

