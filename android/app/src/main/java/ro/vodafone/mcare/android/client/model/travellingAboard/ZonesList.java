package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alex on 4/28/2017.
 */

public class ZonesList extends RealmObject {

    public static final String ZONE_ID = "zoneId";

    @PrimaryKey
    @SerializedName("zoneId")
    private int zoneId;

    @SerializedName("operator")
    private String operator;

    @SerializedName("callLocal")
    private String callLocal;

    @SerializedName("callRo")
    private String callRo;

    @SerializedName("callEU")
    private String callEU;

    @SerializedName("callNonEU")
    private String callNonEU;

    @SerializedName("callReceived")
    private String callReceived;

    @SerializedName("smsSent")
    private String smsSent;

    @SerializedName("internet")
    public String internet;


    public ZonesList() {
    }

/*    public ZonesList(int zoneId, String operator, String callLocal, String callRo, String callEU, String callNonEU, String callReceived, String smsSent, String internet) {
        this.zoneId = zoneId;
        this.operator = operator;
        this.callLocal = callLocal;
        this.callRo = callRo;
        this.callEU = callEU;
        this.callNonEU = callNonEU;
        this.callReceived = callReceived;
        this.smsSent = smsSent;
        this.internet = internet;
    }*/

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCallLocal() {
        return callLocal;
    }

    public void setCallLocal(String callLocal) {
        this.callLocal = callLocal;
    }

    public String getCallRo() {
        return callRo;
    }

    public void setCallRo(String callRo) {
        this.callRo = callRo;
    }

    public String getCallEU() {
        return callEU;
    }

    public void setCallEU(String callEU) {
        this.callEU = callEU;
    }

    public String getCallNonEU() {
        return callNonEU;
    }

    public void setCallNonEU(String callNonEU) {
        this.callNonEU = callNonEU;
    }

    public String getCallReceived() {
        return callReceived;
    }

    public void setCallReceived(String callReceived) {
        this.callReceived = callReceived;
    }

    public String getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(String smsSent) {
        this.smsSent = smsSent;
    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class ZonesList {\n");

        sb.append("  zoneId: ").append(zoneId).append("\n");
        sb.append("  operator: ").append(operator).append("\n");
        sb.append("  callLocal: ").append(callLocal).append("\n");
        sb.append("  callRo: ").append(callRo).append("\n");
        sb.append("  callEU: ").append(callEU).append("\n");
        sb.append("  callNonEU: ").append(callNonEU).append("\n");
        sb.append("  callReceived: ").append(callReceived).append("\n");
        sb.append("  smsSent: ").append(smsSent).append("\n");
        sb.append("  internet: ").append(internet).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
