package ro.vodafone.mcare.android.ui.fragments.yourServices.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringEscapeUtils;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomAcceptedOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;

public class OffersContractDetailsFragment extends OffersFragment {

    public static final String OFFER_ID = "offerId";
    public static final String OFFER_TYPE = "offerType";
    public static final String PENDING_OFFER = "pending";
    public static final String ACCEPTED_OFFER = "accepted";

    private LinearLayout parent;
    private NavigationHeader navigationHeader;
    private AncomPendingOffersSuccess ancomPendingOffersSuccess;
    private AncomAcceptedOffersSuccess ancomAcceptedOffersSuccess;
    private AncomOffer ancomOffer;
    private String offerType;

    public static OffersContractDetailsFragment newInstance(String offerId, String offerType) {
        OffersContractDetailsFragment offersContractDetailsFragment = new OffersContractDetailsFragment();

        Bundle args = new Bundle();
        args.putString(OFFER_ID, offerId);
        args.putString(OFFER_TYPE, offerType);
        offersContractDetailsFragment.setArguments(args);

        return offersContractDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offerType = getArguments().getString(OFFER_TYPE);
        navigationHeader = offerType.equals(ACCEPTED_OFFER)
                ? ((YourServicesActivity) getActivity()).getNavigationHeader()
                : ((OffersActivity) getActivity()).getNavigationHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (offerType.equals(ACCEPTED_OFFER))
            ((YourServicesActivity) getActivity()).scrolltoTop();
        else
            ((OffersActivity) getActivity()).scrolltoTop();

        int dimenPixelSize = getResources().getDimensionPixelSize(R.dimen.parent_padding);

        parent = new LinearLayout(getContext());
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundColor(getResources().getColor(R.color.general_background_light_gray));
        parent.setVisibility(View.GONE);

        parent.setPadding(dimenPixelSize, dimenPixelSize, dimenPixelSize, dimenPixelSize);

        navigationHeader.setTitle(getTitle());
        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        parent.removeAllViews();
        hideSelector();
        getCurrentOffer();
        inflateContractDetails();

        callForAdobeTarget(AdobePageNamesConstants.PG_CONTRACT_DETAILS_VOS);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(offerType.equals(PENDING_OFFER)) {
            if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
                navigationHeader.setTitle(OffersLabels.getOffers_for_you_page_prepaid_title());
            } else {
                navigationHeader.setTitle(OffersLabels.getOffers_for_you_page_postpaid_title());
            }
        }

    }

    @Override
    public String getTitle() {
        return OffersLabels.getAcceptedOffersContractDetails();
    }

    private void hideSelector() {
        navigationHeader.displayDefaultHeader();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    private void getCurrentOffer() {

        showLoadingDialog();

        RealmList<AncomOffer> ancomOffers;

        if (offerType.equals(ACCEPTED_OFFER)) {
            ancomAcceptedOffersSuccess = (AncomAcceptedOffersSuccess) RealmManager.getRealmObject(AncomAcceptedOffersSuccess.class);
            ancomOffers = ancomAcceptedOffersSuccess.getAncomOffers();
        } else {
            ancomPendingOffersSuccess = (AncomPendingOffersSuccess) RealmManager.getRealmObject(AncomPendingOffersSuccess.class);
            ancomOffers = ancomPendingOffersSuccess.getAncomOffers();
        }

        for (AncomOffer ancomOffer : ancomOffers) {
            if (ancomOffer.getOfferId().equals(getArguments().getString(OFFER_ID))) {
                this.ancomOffer = ancomOffer;
                return;
            }
        }

    }

    private void inflateContractDetails() {
        if (getActivity() == null)
            return;

        String textDetails = "";
        if(ancomOffer.getContractDetails() != null) {
            textDetails = StringEscapeUtils.unescapeHtml4(ancomOffer.getContractDetails());
        }
        View v = View.inflate(getActivity(), R.layout.card_offers_contract_details, null);
        ((VodafoneTextView) v.findViewById(R.id.offer_description)).setText(textDetails);
        parent.addView(v);

        stopLoadingDialog();
        parent.setVisibility(View.VISIBLE);
    }
}
