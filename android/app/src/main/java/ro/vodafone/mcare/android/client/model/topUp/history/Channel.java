package ro.vodafone.mcare.android.client.model.topUp.history;

/**
 * Created by Alex on 3/4/2017.
 */

public enum Channel {
    WEB(1, "Web"), MSITE(2, "mSite"), MCARE(3, "mCare"), YOU(4, "YOU");
    private int id;
    private String name;

    Channel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public static Channel fromId(int id) {
        for (Channel ts : values()) {
            if (ts.id == id) return ts;
        }
        throw new IllegalArgumentException();
    }
}