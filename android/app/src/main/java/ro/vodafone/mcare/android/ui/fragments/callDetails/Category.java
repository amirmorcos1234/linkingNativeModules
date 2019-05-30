package ro.vodafone.mcare.android.ui.fragments.callDetails;

import ro.vodafone.mcare.android.R;

/**
 * Created by Bivol Pavel on 16.02.2017.
 */
public enum Category {
    DATE(0, "DATE", R.drawable.data_sharing),
    VOCE(1, "VOCE", R.drawable.minutes),
    SMS(2, "SMS", R.drawable.sms),
    OTHER(3, "OTHER", R.drawable.apps_48);

    private int id;
    private String name;
    private int imageResource;

    Category(int id, String name, int imageResource) {
        this.id = id;
        this.name = name;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }


    public int getImageResource() {
        return imageResource;
    }

    public static Category fromId(int id) {
        for (Category ts : values()) {
            if (ts.id == id) return ts;
        }
        throw new IllegalArgumentException();
    }
}