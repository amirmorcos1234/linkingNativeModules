package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by bogdan marica on 4/8/2017.
 */
@DontObfuscate
public class VodafoneMessage {

    private String firstName;
    private String lastName;
    private String message;

    public VodafoneMessage(String firstName,
                           String lastName,
                           String message) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
//        D.e("text = " + this.message);
    }

    @Override
    public String toString() {
        return "[" + firstName + " " + lastName + "] " + message;
    }
}
