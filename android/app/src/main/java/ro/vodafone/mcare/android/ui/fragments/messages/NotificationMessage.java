package ro.vodafone.mcare.android.ui.fragments.messages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bogdan.marica on 3/6/2017.
 */


public class NotificationMessage {

    public static final int TITLE_TYPE = 1;
    public static final int IMAG_TYPE = 2;

    private String mainText = null;
    private Date date = null;
    private int imgType = -1;
    private int type = -1;
    private int seen = -1;


    public NotificationMessage() {
    }

    public NotificationMessage(String mainText, Date date) {
        this.mainText = mainText;
        type = TITLE_TYPE;
        this.date = date;
    }

    public NotificationMessage(String mainText, Date date, int imgType, int seen) {
        this.mainText = mainText;
        type = IMAG_TYPE;
        this.date = date;
        this.imgType = imgType;
        this.seen = seen;
    }

    public int getType() {
        return type;
    }

    public int getImagType() {
        return imgType;
    }

    public String getMainText() {
        return mainText;
    }

    public Date getDate() {
        return date;
    }

    public int getSeen() {
        return seen;
    }

    public String getMonthNameFromNumber(int mn) {

        switch (mn) {
            case (0):
                return "Ianuarie";
            case (1):
                return "Februarie";
            case (2):
                return "Martie";
            case (3):
                return "Aprilie";
            case (4):
                return "Mai";
            case (5):
                return "Iunie";
            case (6):
                return "Iulie";
            case (7):
                return "August";
            case (8):
                return "Septembrie";
            case (9):
                return "Octombrie";
            case (10):
                return "Noiembrie";
            case (11):
                return "Decembrie";
        }
        return null;
    }

    @Override
    public String toString() {
        return ("*" + mainText + "*" + date + "*");
    }


    public String getStringDate() {

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String d1 = format.format(date);

        return (d1.substring(0, 2) + " " + getMonthNameFromNumber(Integer.parseInt(d1.substring(3, 5))) + " " + d1.substring(6, 10));
    }
}
