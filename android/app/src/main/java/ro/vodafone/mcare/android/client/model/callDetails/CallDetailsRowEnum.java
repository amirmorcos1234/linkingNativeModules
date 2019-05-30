package ro.vodafone.mcare.android.client.model.callDetails;

import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;

/**
 * Created by Bivol Pavel on 20.05.2017.
 */

public enum  CallDetailsRowEnum {
    VOICE (Category.VOCE, "Apel", "național", "internațional", "național/intl", "în roaming"),
    DATE(Category.DATE, "Date", "naționale", "", "naționale", "roaming"),
    SMS(Category.SMS, "SMS", "național", "internațional", "național/intl", "roaming"),
    OTHER (Category.OTHER, "", "Național", "Internațional", "Național/Intl", "Roaming");


    private Category category;
    private final String name;
    private final String nationalAccesType;
    private final String internationalAccesType;
    private final String nationalInternationalAccesType;
    private final String roamingAccesType;


    public static CallDetailsRowEnum getAccesTypeFromCategory(Category category) {
        for (CallDetailsRowEnum ts : values()) {
            if (ts.category == category) return ts;
        }
        throw new IllegalArgumentException();
    }


    private CallDetailsRowEnum(Category category, String name, String nationalAccesType, String internationalAccesType, String nationalInternationalAccessType, String roamingAccesType) {
        this.category = category;
        this.name = name;
        this.nationalAccesType = nationalAccesType;
        this.internationalAccesType = internationalAccesType;
        this.nationalInternationalAccesType = nationalInternationalAccessType;
        this.roamingAccesType = roamingAccesType;
    }

    public String getName() {
        return name;
    }

    public String getNationalAccesType() {
        return nationalAccesType;
    }

    public String getInternationalAccesType() {
        return internationalAccesType;
    }

    public String getNationalInternationalAccesType() {
        return nationalInternationalAccesType;
    }

    public String getRoamingAccesType() {
        return roamingAccesType;
    }
}
