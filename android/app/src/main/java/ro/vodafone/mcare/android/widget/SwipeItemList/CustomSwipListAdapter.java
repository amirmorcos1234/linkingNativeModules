package ro.vodafone.mcare.android.widget.SwipeItemList;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.fragments.messages.NotificationMessage;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static ro.vodafone.mcare.android.ui.fragments.messages.NotificationMessage.IMAG_TYPE;
import static ro.vodafone.mcare.android.ui.fragments.messages.NotificationMessage.TITLE_TYPE;


/**
 * Created by bogdan.marica on 3/6/2017.
 */

public class CustomSwipListAdapter extends BaseAdapter {

    private ArrayList<?> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context ct;
    public CustomSwipListAdapter(ArrayList arrayList, Context ct) {
        this.ct = ct;
        mInflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = arrayList;
    }

    public boolean getSwipEnableByPosition(int position) {

        return getItem(position).getType() == NotificationMessage.IMAG_TYPE;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public NotificationMessage getItem(int position) {
        return (NotificationMessage) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //     parent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        switch (getItem(position).getType()) {

            case (TITLE_TYPE):
                if (convertView == null)
                    convertView = mInflater.inflate(R.layout.message_card_title, parent, false);
                ;

                VodafoneTextView cardTitle = (VodafoneTextView) convertView.findViewById(R.id.cardTitle);
                cardTitle.setText(getItem(position).getMainText());

                break;

            case (IMAG_TYPE):
                if (convertView == null)
                    convertView = mInflater.inflate(R.layout.message_card, parent, false);
                ;

                NotificationMessage notificationMessage = getItem(position);
                VodafoneTextView title = (VodafoneTextView) convertView.findViewById(R.id.cardTitle);
                title.setText(notificationMessage.getMainText());
                VodafoneTextView date = (VodafoneTextView) convertView.findViewById(R.id.date);
                date.setText(notificationMessage.getStringDate());
                ImageView image = (ImageView) convertView.findViewById(R.id.image);

                switch (notificationMessage.getImagType()) {

                    case (1):
                        image.setImageDrawable(ContextCompat.getDrawable(ct, R.drawable.ic_add_circle_outline_black_24dp));
                        break;
                    case (2):
                        image.setImageDrawable(ContextCompat.getDrawable(ct, R.drawable.ic_message_black_24dp));
                        break;
                    case (3):
                        break;
                    case (4):
                        break;
                    case (5):
                        break;
                    default:
                        break;
                }

                if (notificationMessage.getSeen() == 1)//seen -- so gray backgr
                    image.setBackgroundColor(ContextCompat.getColor(ct, R.color.card_circle_icon_red));
                else
                    image.setBackgroundColor(ContextCompat.getColor(ct, R.color.card_background_gray));


                break;
        }

        return convertView;
    }


    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return ((NotificationMessage) getItem(position)).getType();
        //  return position;
    }
}