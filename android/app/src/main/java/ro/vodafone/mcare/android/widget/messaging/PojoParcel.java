package ro.vodafone.mcare.android.widget.messaging;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;

/**
 * Created by User1 on 4/3/2017.
 */

public class PojoParcel implements Parcelable {

    public static final Creator<PojoParcel> CREATOR = new Creator<PojoParcel>() {
        @Override
        public PojoParcel createFromParcel(Parcel in) {
            return new PojoParcel(in);
        }

        @Override
        public PojoParcel[] newArray(int size) {
            return new PojoParcel[size];
        }
    };
    private List<MessageItem> mMessageItems;

    public PojoParcel(Parcel in) {
    }

    public PojoParcel(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public void setmMessageItems(List<MessageItem> mMessageItems) {

        this.mMessageItems = mMessageItems;

    }

    public List<MessageItem> getMessages(){
        return mMessageItems;
    }


}
