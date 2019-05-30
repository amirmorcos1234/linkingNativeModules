/**
 * API-24  Payment Request
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 3.0.6
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.rest.requests;

import io.swagger.annotations.ApiModel;

/**
 * Parent of all API requests reaching server running in a normal state
 **/
@ApiModel(description = "Parent of all API requests reaching server running in a normal state")
public class RestApiRequest {
  


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestApiRequest restApiRequest = (RestApiRequest) o;
    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestApiRequest {\n");
    
    sb.append("}\n");
    return sb.toString();
  }
}