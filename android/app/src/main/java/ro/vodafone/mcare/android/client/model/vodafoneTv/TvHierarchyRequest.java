package ro.vodafone.mcare.android.client.model.vodafoneTv;

import com.google.gson.annotations.SerializedName;

public class TvHierarchyRequest {
    @SerializedName("banList")
    private String banList[] = null;
    @SerializedName("vfFixedCid")
    private String vfFixedCid;

    public TvHierarchyRequest() {
    }

    public TvHierarchyRequest(String[] banList, String vfFixedCid) {
        this.banList = banList;
        this.vfFixedCid = vfFixedCid;
    }

    public String[] getBanList() {
        return banList;
    }

    public void setBanList(String[] banList) {
        this.banList = banList;
    }

    public String getVfFixedCid() {
        return vfFixedCid;
    }

    public void setVfFixedCid(String vfFixedCid) {
        this.vfFixedCid = vfFixedCid;
    }
}
