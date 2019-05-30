package ro.vodafone.mcare.android.client.model.beo.postpaid.activation;

/**
 * Created by Alex on 3/27/2017.
 */

public enum ActivationTimeEnum {

    NOW("NOW"), BC("BC-1");

    private String id;

    ActivationTimeEnum(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}