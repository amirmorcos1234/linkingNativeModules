package ro.vodafone.mcare.android.client.model.realm.seamless;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Victor Radulescu on 1/13/2017.
 */

public class InitialToken extends RealmObject{

    public static final String INITIAL_TOKEN = "initialToken";

    @SerializedName("initialToken")
    private String initialToken;

    @PrimaryKey
    private int id_initialToken;

    public InitialToken(){ id_initialToken =1;}

    public int getId_initialToken() {
        return id_initialToken;
    }

    public String getInitialToken() {
        return initialToken;
    }

    public void setInitialToken(String initialToken) {
        this.initialToken = initialToken;
    }
}
