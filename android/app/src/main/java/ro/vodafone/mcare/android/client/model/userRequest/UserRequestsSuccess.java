/**
 * API-66 User Requests
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 9.0.7
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.userRequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data associated with successful response
 **/
@ApiModel(description = "object containing data associated with successful response")
public class UserRequestsSuccess {
  
  @SerializedName("acceptedList")
  private List<UserRequest> acceptedList = null;
  @SerializedName("rejectedList")
  private List<UserRequest> rejectedList = null;
  @SerializedName("pendingList")
  private List<UserRequest> pendingList = null;

  /**
   * users requests lists
   **/
  @ApiModelProperty(value = "users requests lists")
  public List<UserRequest> getAcceptedList() {
    return acceptedList;
  }
  public void setAcceptedList(List<UserRequest> acceptedList) {
    this.acceptedList = acceptedList;
  }

  /**
   * users requests lists
   **/
  @ApiModelProperty(value = "users requests lists")
  public List<UserRequest> getRejectedList() {
    return rejectedList;
  }
  public void setRejectedList(List<UserRequest> rejectedList) {
    this.rejectedList = rejectedList;
  }

  /**
   * users requests lists
   **/
  @ApiModelProperty(value = "users requests lists")
  public List<UserRequest> getPendingList() {
    return pendingList;
  }
  public void setPendingList(List<UserRequest> pendingList) {
    this.pendingList = pendingList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserRequestsSuccess userRequestsSuccess = (UserRequestsSuccess) o;
    return (this.acceptedList == null ? userRequestsSuccess.acceptedList == null : this.acceptedList.equals(userRequestsSuccess.acceptedList)) &&
        (this.rejectedList == null ? userRequestsSuccess.rejectedList == null : this.rejectedList.equals(userRequestsSuccess.rejectedList)) &&
        (this.pendingList == null ? userRequestsSuccess.pendingList == null : this.pendingList.equals(userRequestsSuccess.pendingList));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.acceptedList == null ? 0: this.acceptedList.hashCode());
    result = 31 * result + (this.rejectedList == null ? 0: this.rejectedList.hashCode());
    result = 31 * result + (this.pendingList == null ? 0: this.pendingList.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserRequestsSuccess {\n");
    
    sb.append("  acceptedList: ").append(acceptedList).append("\n");
    sb.append("  rejectedList: ").append(rejectedList).append("\n");
    sb.append("  pendingList: ").append(pendingList).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}