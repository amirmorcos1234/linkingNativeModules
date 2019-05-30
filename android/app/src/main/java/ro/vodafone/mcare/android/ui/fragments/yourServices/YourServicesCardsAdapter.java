package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;

@Deprecated
public class YourServicesCardsAdapter extends ArrayAdapter<YourServicesElement> {

    public YourServicesCardsAdapter(Context context, ArrayList<YourServicesElement> objects) {
        super(context, 0, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        YourServicesElement yourServicesElement = getItem(position);
        if (convertView == null) {
            if (yourServicesElement != null)
                if (yourServicesElement.type == 1) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, parent, false);
                    convertView = assignAndPopulateArrowCard(convertView, yourServicesElement);
                } else if (yourServicesElement.type == 2) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_button, parent, false);
                    convertView = assignAndPopulateButtonCard(convertView, yourServicesElement);

                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_title, parent, false);
                    convertView = assignAndPopulateTitleCard(convertView, yourServicesElement);
                }

        }

        return convertView;
    }

    private View assignAndPopulateArrowCard(View v, final YourServicesElement yourServicesElement) {
        final VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        final VodafoneTextView cardSubtext = (VodafoneTextView) v.findViewById(R.id.cardSubtext);
        ImageView arrow = (ImageView) v.findViewById(R.id.cardArrow);
        LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.arrowCardLayout);

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (cardTitle.getText().equals(OffersLabels.getOffersForYouAcceptedOffers())) {
                    YourServicesActivity activity = (YourServicesActivity) getContext();
                     activity.attachFragment(new AncomAcceptedOffersFragment());
                }

            }

        });
        // Populate the data into the template view using the data object
        cardTitle.setText(yourServicesElement.title);

        if (yourServicesElement.subtext.equals(""))
            cardSubtext.setVisibility(View.GONE);
        else
            cardSubtext.setText(yourServicesElement.subtext);

        return v;
    }

    private View assignAndPopulateButtonCard(View v, YourServicesElement yourServicesElement) {
        VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        VodafoneTextView cardSubtext = (VodafoneTextView) v.findViewById(R.id.cardSubtext);
        SwitchButton button = (SwitchButton) v.findViewById(R.id.settingsCardButton);

      //  button.setMyStyle();

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("okso", "CHECKED : " + isChecked);
            }
        });


        // Populate the data into the template view using the data object
        cardTitle.setText(yourServicesElement.title);

        if (yourServicesElement.subtext.equals(""))
            cardSubtext.setVisibility(View.GONE);
        else
            cardSubtext.setText(yourServicesElement.subtext);

        return v;
    }

    private View assignAndPopulateTitleCard(View v, YourServicesElement yourServicesElement) {
        VodafoneTextView cardTitle = (VodafoneTextView) v.findViewById(R.id.cardTitle);
        cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        cardTitle.setText(yourServicesElement.title);

        return v;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
