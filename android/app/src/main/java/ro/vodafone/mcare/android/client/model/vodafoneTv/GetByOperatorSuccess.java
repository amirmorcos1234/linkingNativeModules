/**
 * Phoenix Integration Devices API
 * Phoenix Integration Devices API (new mCare)
 * <p>
 * OpenAPI spec version: 1.0.0
 * <p>
 * <p>
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.vodafoneTv;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class GetByOperatorSuccess extends RealmObject {

    @PrimaryKey
    private long id;

    public GetByOperatorSuccess(){
        id = 1;
    }

    @SerializedName("masterUser_id")
    private String masterUserId = null;
    @SerializedName("defaultUser_id")
    private String defaultUserId = null;
    @SerializedName("deviceFamilies")
    private RealmList<DeviceFamiliesList> deviceFamilies = null;
    @SerializedName("deleteNextDeviceAction")
    private Long deleteNextDeviceAction = null;
    @SerializedName("deleteRestricted")
    private Boolean deleteRestricted = null;
    @SerializedName("devicesLimit")
    private Integer devicesLimit = null;
    @SerializedName("externalId")
    private String externalId = null;

    /**
     **/
    @ApiModelProperty(value = "")
    public String getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(String masterUserId) {
        this.masterUserId = masterUserId;
    }

    public String getDefaultUserId() {
        return defaultUserId;
    }

    public void setDefaultUserId(String defaultUserId) {
        this.defaultUserId = defaultUserId;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public List<DeviceFamiliesList> getDeviceFamilies() {
        return deviceFamilies;
    }

    public void setDeviceFamilies(RealmList<DeviceFamiliesList> deviceFamilies) {
        this.deviceFamilies = deviceFamilies;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public Long getDeleteNextDeviceAction() {
        return deleteNextDeviceAction;
    }

    public void setDeleteNextDeviceAction(Long deleteNextDeviceAction) {
        this.deleteNextDeviceAction = deleteNextDeviceAction;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public Boolean getDeleteRestricted() {
        return deleteRestricted;
    }

    public void setDeleteRestricted(Boolean deleteRestricted) {
        this.deleteRestricted = deleteRestricted;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public Integer getDevicesLimit() {
        return devicesLimit;
    }

    public void setDevicesLimit(Integer devicesLimit) {
        this.devicesLimit = devicesLimit;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetByOperatorSuccess getByOperatorSuccess = (GetByOperatorSuccess) o;
        return (this.masterUserId == null ? getByOperatorSuccess.masterUserId == null : this.masterUserId.equals(getByOperatorSuccess.masterUserId)) &&
                (this.deviceFamilies == null ? getByOperatorSuccess.deviceFamilies == null : this.deviceFamilies.equals(getByOperatorSuccess.deviceFamilies)) &&
                (this.deleteNextDeviceAction == null ? getByOperatorSuccess.deleteNextDeviceAction == null : this.deleteNextDeviceAction.equals(getByOperatorSuccess.deleteNextDeviceAction)) &&
                (this.deleteRestricted == null ? getByOperatorSuccess.deleteRestricted == null : this.deleteRestricted.equals(getByOperatorSuccess.deleteRestricted)) &&
                (this.devicesLimit == null ? getByOperatorSuccess.devicesLimit == null : this.devicesLimit.equals(getByOperatorSuccess.devicesLimit)) &&
                (this.externalId == null ? getByOperatorSuccess.externalId == null : this.externalId.equals(getByOperatorSuccess.externalId));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.masterUserId == null ? 0 : this.masterUserId.hashCode());
        result = 31 * result + (this.deviceFamilies == null ? 0 : this.deviceFamilies.hashCode());
        result = 31 * result + (this.deleteNextDeviceAction == null ? 0 : this.deleteNextDeviceAction.hashCode());
        result = 31 * result + (this.deleteRestricted == null ? 0 : this.deleteRestricted.hashCode());
        result = 31 * result + (this.devicesLimit == null ? 0 : this.devicesLimit.hashCode());
        result = 31 * result + (this.externalId == null ? 0 : this.externalId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GetByOperatorSuccess {\n");

        sb.append("  masterUserId: ").append(masterUserId).append("\n");
        sb.append("  deviceFamilies: ").append(deviceFamilies).append("\n");
        sb.append("  deleteNextDeviceAction: ").append(deleteNextDeviceAction).append("\n");
        sb.append("  deleteRestricted: ").append(deleteRestricted).append("\n");
        sb.append("  devicesLimit: ").append(devicesLimit).append("\n");
        sb.append("  externalId: ").append(externalId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
