/**
 * API-21 Balance Prepaid
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.balance;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * object containing data associated with successful transaction for API-21
 **/

public class BalanceCreditSuccess extends RealmObject{
  
  @SerializedName("balance")
  private Float balance = null;
  @SerializedName("balanceValidity")
  private Long balanceValidity = null;

  @PrimaryKey
  private long id;

    public BalanceCreditSuccess() {
        id =1;
    }

    /**
   * contains balance amount
   **/
  public Float getBalance() {
    return balance;
  }
  public void setBalance(Float balance) {
    this.balance = balance;
  }

  /**
   * represents balance validity in Unix time (milliseconds)
   **/
  public Long getBalanceValidity() {
    return balanceValidity;
  }
  public void setBalanceValidity(Long balanceValidity) {
    this.balanceValidity = balanceValidity;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalanceCreditSuccess balanceCreditSuccess = (BalanceCreditSuccess) o;
    return (this.balance == null ? balanceCreditSuccess.balance == null : this.balance.equals(balanceCreditSuccess.balance)) &&
        (this.balanceValidity == null ? balanceCreditSuccess.balanceValidity == null : this.balanceValidity.equals(balanceCreditSuccess.balanceValidity));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.balance == null ? 0: this.balance.hashCode());
    result = 31 * result + (this.balanceValidity == null ? 0: this.balanceValidity.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreditBalanceSuccess {\n");
    
    sb.append("  balance: ").append(balance).append("\n");
    sb.append("  balanceValidity: ").append(balanceValidity).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
