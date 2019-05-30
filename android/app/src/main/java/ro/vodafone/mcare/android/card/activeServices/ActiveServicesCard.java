package ro.vodafone.mcare.android.card.activeServices;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.ServiceDetailsFragment;
import ro.vodafone.mcare.android.ui.views.CardButton;
import ro.vodafone.mcare.android.ui.views.CardHeader;

/**
 * Created by Bivol Pavel on 18.03.2017.
 */
public class ActiveServicesCard extends VodafoneAbstractCard {

    private Context mContext;

    private OfferRowInterface offerRowInterface;
    private Promo promo;

    private ActiveServicesViewGroup viewGroup;

    public ActiveServicesCard(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public ActiveServicesCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public ActiveServicesCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        init(attrs);
    }

    @Override
    protected int setContent() {
        return 0;
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInDp(16, 0, 16, 0);
        //setOnErrorClickListner();
    }

    public ActiveServicesCard setOnErrorClickListner() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadingView == null || !loadingView.isVisible()) {
                    if (viewGroup != null && errorView != null && errorView.getVisibility() == VISIBLE) {
                        viewGroup.getController().requestData();
                    }
                }
            }
        });
        return this;
    }


    public ActiveServicesCard buildCBUCard(ActiveOfferPostpaid activeOfferPostpaid) {
        this.offerRowInterface = activeOfferPostpaid;
        String category ;
        if (AppConfiguration.getVodafoneTvCategories().contains(activeOfferPostpaid.getOfferCategory()))
            category = VodafoneTvLabels.getVtvOfferCardCategory();
        else
            category = activeOfferPostpaid.getOfferCategory();

        atachHeader(activeOfferPostpaid.getOfferImage(), activeOfferPostpaid.getOfferName(),
                activeOfferPostpaid.getOfferShortDescription(),
                category, null);
        atachButton();
        hideLoading();

        return this;
    }

    public ActiveServicesCard buildEBUCard(Promo promo) {

        this.promo = promo;

        atachHeaderWithPlaceholder(promo.getOfferImage(), ContextCompat.getDrawable(getContext(), R.drawable.default_service_icon_wifi), promo.getPromoName(), promo.getPromoCategory(),
                null, null);
        atachButton();
        hideLoading();

        return this;
    }

    public ActiveServicesCard buildPrepaidCard(ActiveOffer activeOffer) {
        this.offerRowInterface = activeOffer;
        String category = activeOffer.getOfferCategory();
        if (AppConfiguration.getVodafoneTvCategories().contains(activeOffer.getOfferCategory()))
            category = VodafoneTvLabels.getVtvOfferCardCategory();

        atachHeader(activeOffer.getOfferImage(), activeOffer.getOfferName(),
                activeOffer.getOfferShortDescription(), category, null);
        atachButton();
        hideLoading();

        return this;
    }

    private Drawable getDrawableFromPath(String imagePath) {
        if (imagePath != null) {
            ImageView image = new ImageView(mContext);
            Glide.with(mContext).load(imagePath).into(image);
            return image.getDrawable();
        }
        return null;
    }

    private void atachHeader(String headerIcon, String headerTitle, String headerSubTitle, String headerCategory, String headerExtraParameter) {

        addHeader(new CardHeader(mContext)
                .buildHeader(new CardHeaderModel(headerIcon, headerTitle, makeOfferPriceBoldText(headerSubTitle), null, headerCategory, headerExtraParameter)));
    }

    private void atachHeaderWithPlaceholder(String headerIcon, Drawable placeholder, String headerTitle, String headerSubTitle, String headerCategory, String headerExtraParameter) {
        addHeader(new CardHeader(mContext)
                .buildHeader(new CardHeaderModel(headerIcon, placeholder, headerTitle, makeOfferPriceBoldText(headerSubTitle), null, headerCategory, headerExtraParameter)));
    }

    private SpannableString makeOfferPriceBoldText(String offerPrice) {

        SpannableString spannableString = null;

        if (offerPrice != null) {
            spannableString = new SpannableString(offerPrice);
            setBoldTypeFace(spannableString, 0, getLastBoldPosition(spannableString.toString(), "€"));
        }

        return spannableString;
    }

    private void setBoldTypeFace(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
    }

    private int getLastBoldPosition(String label, String lastBoldSequence) {
        return label.indexOf(lastBoldSequence) + 1;
    }

    private void atachButton() {
        if (!(mContext instanceof ActivityFragmentInterface))
            return;
        addButton(new CardButton(mContext)

                .buildButton(new CardButtonModel("Detalii servicii", new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        User user = VodafoneController.getInstance().getUser();

                        if (user instanceof PrepaidUser) {
                            ((ActivityFragmentInterface) mContext).attachFragment(new ServiceDetailsFragment()
                                    .setArgsOnBundle(OfferRowInterface.class.getCanonicalName(), offerRowInterface));
                        } else if (user instanceof CBUUser) {
                            ((ActivityFragmentInterface) mContext).attachFragment(new ServiceDetailsFragment()
                                    .setArgsOnBundle(OfferRowInterface.class.getCanonicalName(), offerRowInterface));
                        } else if (user instanceof EbuMigrated) {
                            ((ActivityFragmentInterface) mContext).attachFragment(new ServiceDetailsFragment()
                                    .setArgsOnBundle(promo.getClass().getCanonicalName(), promo));
                        }
                    }
                }, null)));
    }

    public VodafoneAbstractCard showError(boolean hideContent, String errorMessage, boolean isClicableError) {
        super.showError(hideContent, errorMessage);
        errorView.setMargins(0, 16, 0, 16);
        if (isClicableError) {
            setOnErrorClickListner();
        }
        return this;
    }

    public ActiveServicesCard setViewGroup(ActiveServicesViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        return this;
    }
}
