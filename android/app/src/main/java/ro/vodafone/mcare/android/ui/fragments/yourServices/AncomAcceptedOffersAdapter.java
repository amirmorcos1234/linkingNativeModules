package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;

/**
 * Created by Deaconescu Eliza on 19.03.2017.
 */
@Deprecated
public class AncomAcceptedOffersAdapter extends BaseAdapter {

    public static String TAG = "AncomAcceptedOffersAdapter";
    private Context context;
    private List<AncomOffer> ancomOffersList;

    public AncomAcceptedOffersAdapter(Context context, List<AncomOffer> ancomOffersList) {
        this.context = context;
        this.ancomOffersList = ancomOffersList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final AncomOffer child = ancomOffersList.get(i);

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.fragment_your_services_accepted_offers, null);
        }

        TextView acceptedOffersTC = (TextView) view.findViewById(R.id.accepted_offers_t_c) ;
        TextView acceptedOffersGeneralTC = (TextView) view.findViewById(R.id.accepted_offers_general_t_c) ;
        RelativeLayout image = (RelativeLayout) view.findViewById(R.id.redirect_contrat_details);
        TextView contractDuration = (TextView) view.findViewById(R.id.contractDuration);
        TextView  proposalId = (TextView) view.findViewById(R.id.proposalId);
        TextView creationDate = (TextView) view.findViewById(R.id.creationDateAcceptedOffers);
        TextView  msisdn = (TextView) view.findViewById(R.id.msisdnAcceptedOffers);

        SpannableString myString = new SpannableString(context.getResources().getString(R.string.accepted_offers_t_c));
        openWebView(myString,0,29,acceptedOffersTC,SettingsLabels.getLinkAncomOffersTC());

        SpannableString myStringSupported = new SpannableString(OffersLabels.getAcceptedOffersGeneralTC());
        openWebView(myStringSupported,0,53,acceptedOffersGeneralTC, SettingsLabels.getLinkContractDetailsAccepted());

        if (child.getMsisdn() != null) {
            Log.d("ChildVIew", "child.getPrepaidMsisdn() " + child.getMsisdn());
            msisdn.setText(child.getMsisdn());
        }
        if (child.getProposalId() != null) {
            Log.d("ChildVIew", "child.getChannelTypeId() " + child.getProposalId());
            proposalId.setText(child.getProposalId());
        }
        if (child.getCreationDate() != null) {
            Log.d("ChildVIew", "child.getAmount() " + child.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = formatter.format(new Date(child.getCreationDate()));
            creationDate.setText(dateString);
        }
        if (child.getContractDuration() != null) {
            Log.d("ChildVIew", "child.getActionTypeId() " + child.getContractDuration());
            contractDuration.setText(String.format(VodafoneController.getInstance().getApplicationContext().getResources().getString(R.string.your_option_months),child.getContractDuration()));
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, SettingsLabels.getLinkContractDetails());
                context.startActivity(intent);
            }
        });

        return view;
    }

    private void openWebView(SpannableString spannableString, int start, int end, TextView textView, final String url) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, url);
                context.startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setHighlightColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setLinkTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
    }

        @Override
        public int getCount() {
            return ancomOffersList.size();
        }

        @Override
        public Object getItem(int i) {
            return ancomOffersList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


}
