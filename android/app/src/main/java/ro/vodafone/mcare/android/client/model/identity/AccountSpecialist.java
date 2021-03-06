/**
 * API-63 Identity Selector
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 9.0.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.identity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about account specialist
 **/
@ApiModel(description = "object containing data about account specialist")
public class AccountSpecialist extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  private String id = null;
  @SerializedName("firstName")
  private String firstName = null;
  @SerializedName("lastName")
  private String lastName = null;
  @SerializedName("email")
  private String email = null;
  @SerializedName("phone")
  private String phone = null;
  @SerializedName("department")
  private String department = null;
  @SerializedName("lockDate")
  private String lockDate = null;

  /**
   * Unique id for current account specialist
   **/
  @ApiModelProperty(value = "Unique id for current account specialist")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * First name for current account specialist
   **/
  @ApiModelProperty(value = "First name for current account specialist")
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Last name for current account specialist
   **/
  @ApiModelProperty(value = "Last name for current account specialist")
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Contact email for current account specialist
   **/
  @ApiModelProperty(value = "Contact email for current account specialist")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Contact phone for current account specialist
   **/
  @ApiModelProperty(value = "Contact phone for current account specialist")
  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Current account specialist's department
   **/
  @ApiModelProperty(value = "Current account specialist's department")
  public String getDepartment() {
    return department;
  }
  public void setDepartment(String department) {
    this.department = department;
  }

  /**
   * Lock date
   **/
  @ApiModelProperty(value = "Lock date")
  public String getLockDate() {
    return lockDate;
  }
  public void setLockDate(String lockDate) {
    this.lockDate = lockDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountSpecialist accountSpecialist = (AccountSpecialist) o;
    return (this.id == null ? accountSpecialist.id == null : this.id.equals(accountSpecialist.id)) &&
        (this.firstName == null ? accountSpecialist.firstName == null : this.firstName.equals(accountSpecialist.firstName)) &&
        (this.lastName == null ? accountSpecialist.lastName == null : this.lastName.equals(accountSpecialist.lastName)) &&
        (this.email == null ? accountSpecialist.email == null : this.email.equals(accountSpecialist.email)) &&
        (this.phone == null ? accountSpecialist.phone == null : this.phone.equals(accountSpecialist.phone)) &&
        (this.department == null ? accountSpecialist.department == null : this.department.equals(accountSpecialist.department)) &&
        (this.lockDate == null ? accountSpecialist.lockDate == null : this.lockDate.equals(accountSpecialist.lockDate));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.id == null ? 0: this.id.hashCode());
    result = 31 * result + (this.firstName == null ? 0: this.firstName.hashCode());
    result = 31 * result + (this.lastName == null ? 0: this.lastName.hashCode());
    result = 31 * result + (this.email == null ? 0: this.email.hashCode());
    result = 31 * result + (this.phone == null ? 0: this.phone.hashCode());
    result = 31 * result + (this.department == null ? 0: this.department.hashCode());
    result = 31 * result + (this.lockDate == null ? 0: this.lockDate.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountSpecialist {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  firstName: ").append(firstName).append("\n");
    sb.append("  lastName: ").append(lastName).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  phone: ").append(phone).append("\n");
    sb.append("  department: ").append(department).append("\n");
    sb.append("  lockDate: ").append(lockDate).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
