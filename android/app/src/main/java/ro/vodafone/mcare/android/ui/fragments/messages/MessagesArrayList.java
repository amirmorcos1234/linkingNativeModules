package ro.vodafone.mcare.android.ui.fragments.messages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by bogdan.marica on 3/7/2017.
 */

public class MessagesArrayList<T> extends ArrayList<T> {

    public boolean has(NotificationMessage o) {

        for (int i = 0; i < size(); i++)
            if (((NotificationMessage) get(i)).getMainText().equals(o.getMainText()))
                return true;
        return false;

    }

    public boolean hasMore(Date date) {
        int count = 0;
        for (int i = 0; i < size(); i++)
            if (((NotificationMessage) get(i)).getDate().getMonth() == date.getMonth())
                count++;

        if (count > 1)
            return true;
        else
            return false;
    }

    public static final Comparator<NotificationMessage> comparator = new Comparator<NotificationMessage>() {

        public int compare(NotificationMessage nm1, NotificationMessage nm2) {

            Date date1 = nm1.getDate();
            Date date2 = nm2.getDate();

            return date2.compareTo(date1);
        }
    };

    public int getTitleItemByDate(Date date) {

        for (int i = 0; i < size(); i++)
            if (((NotificationMessage) get(i)).getDate().getMonth() == date.getMonth())
                return i;
        return -1;

    }
}