package ro.vodafone.mcare.android.client.model.vodafoneTv;

import com.google.gson.annotations.SerializedName;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TvHierarchyResponse extends RealmObject {

    @PrimaryKey
    private String id;
    @SerializedName("activeVtvList")
    RealmList<TvHierarchy> activeVtvList;

    public TvHierarchyResponse() {
        id = "1";
    }

    public RealmList<TvHierarchy> getActiveVtvList() {
        return activeVtvList;
    }

    public void setActiveVtvList(RealmList<TvHierarchy> activeVtvList) {
        this.activeVtvList = activeVtvList;
    }

    public RealmList<TvHierarchy> getActiveFixedNetSubscriptions() {
        RealmList<TvHierarchy> activeFixedNet = new RealmList<>();
        for (int i = 0; i < activeVtvList.size(); i++) {
            if (activeVtvList.get(i).getServiceType().toLowerCase().equals("fixed"))
                activeFixedNet.add(activeVtvList.get(i));
        }
        return activeFixedNet;
    }

    public String getId() {
        return id;
    }

}


