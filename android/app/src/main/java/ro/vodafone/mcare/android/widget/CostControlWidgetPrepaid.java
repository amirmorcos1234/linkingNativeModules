package ro.vodafone.mcare.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 5/2/2017.
 */

public class CostControlWidgetPrepaid extends CostControlWidget {

    public CostControlWidgetPrepaid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CostControlWidgetPrepaid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    List<View> generateExtraOptionsButtons() {


            List<View> buttonList= new ArrayList<>();

            Button buttonMore = createDialViewButton(R.drawable.black_circle,"Altele", Color.WHITE);
            buttonMore.setClickable(true);
            buttonMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Tealium Track Event
                    Map<String, Object> tealiumMapEvent = new HashMap(6);
                    tealiumMapEvent.put("screen_name","dashboard");
                    tealiumMapEvent.put("event_name","mcare: dashboard: button: gauge plus");
                    tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                    new NavigationAction(getContext()).startAction(IntentActionName.OFFERS_BEO_NO_SEAMLESS);
                    extraOptionsController.toggle(300);
                    // Toast.makeText(context, "Redirect to Bonusuri si extraoptiuni", Toast.LENGTH_SHORT).show();
                }
            });

            buttonList.add(buttonMore);

            View  offerView = getOfferViewAfterMostImportantHiddenEligibleCategory();
        if(offerView!=null){
            buttonList.add(offerView);
        }

       /*     try {
                if (extraOffers==null || (!extraOffers.isValid()) || (extraOffers.isValid() && extraOffers.isEmpty())){
                    Log.e(TAG, "unicaOffers.isEmpty()");
                } else {
                    Log.d(TAG, "extraOffers size " + extraOffers.size());
                    for (final UnicaOffer offer : extraOffers) {
                        Log.d(TAG, "Extra Offer from Unica : " + offer.toString());
                        Button button = createDialViewButton(R.drawable.black_circle,
                                offer.getDisplayName(),
                                Color.WHITE);
                        button.setClickable(true);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new NavigationAction(getContext()).startAction(IntentActionName.OFFERS);
                            }
                        });
                        buttonList.add(button);
                    }
                }
            }catch (Exception ex){
                Log.e(TAG, "DialView offers from Unica issue.", ex);
            }*/

        return buttonList;
    }

    @Override
    protected OnClickListener getGaugeClickListener() {
        return gaugeClickListener;
    }
    protected View.OnClickListener gaugeClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","dashboard");
            tealiumMapEvent.put("event_name","mcare: dashboard: button: gauge");
            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            new NavigationAction(getContext()).startAction(IntentActionName.COST_CONTROL);
        }
    };

    private View getOfferViewAfterMostImportantHiddenEligibleCategory(){
        Realm realm = Realm.getDefaultInstance();
        EligibleOffersSuccess eligibleOffersSuccesses = (EligibleOffersSuccess) RealmManager
                .getRealmObject(realm, EligibleOffersSuccess.class);
        if(eligibleOffersSuccesses==null){
            return null;
        }
        RealmResults<EligibleCategories> categories =  eligibleOffersSuccesses.getEligibleOptionsCategories()
                .where().equalTo(EligibleCategories.IS_HIDDEN,true).notEqualTo(EligibleCategories.CATEGORY, "Hidden", Case.INSENSITIVE).findAll();

        RealmResults<EligibleCategories> services =  eligibleOffersSuccesses.getEligibleServicesCategories()
                .where().equalTo(EligibleCategories.IS_HIDDEN,true).notEqualTo(EligibleCategories.CATEGORY, "Hidden", Case.INSENSITIVE).findAll();

        Log.d("CostControlPrepaid","Services and Categories "+(services!=null? String.valueOf(services.size()):"") + (categories!=null? String.valueOf(categories.size()):""));

        RealmList<PrepaidOfferRow> offersFromCateogories = new RealmList<>();
        if(categories!=null) {
            for (EligibleCategories category : categories) {
                if (category.getEligibleOffersList() != null) {
                    offersFromCateogories.addAll(category.getEligibleOffersList());
                }
            }
        }
        RealmList<PrepaidOfferRow> offersFromServices = new RealmList<>();

        if(services!=null){
            for (EligibleCategories service: services) {
                if(service.getEligibleOffersList()!=null){
                    offersFromServices.addAll(service.getEligibleOffersList());
                }
            }
        }
        Log.d("CostControlPrepaid", " Offers categories " + String.valueOf(offersFromCateogories.size()));
        Log.d("CostControlPrepaid", " Offers services " + String.valueOf(offersFromServices.size()));

        PrepaidOfferRow prepaidOfferCategoryBest=null;
        if(!offersFromCateogories.isEmpty()){
            Collections.sort(offersFromCateogories, PrepaidOfferRow.getComparatorAfterPriority());
            prepaidOfferCategoryBest = offersFromCateogories.get(0);

        }
        PrepaidOfferRow prepaidOfferServiceBest=null;
        if(!offersFromServices.isEmpty()){
            Collections.sort(offersFromServices, PrepaidOfferRow.getComparatorAfterPriority());
            prepaidOfferServiceBest = offersFromServices.get(0);
        }

        long offerId = 0;

        FrameLayout view = null;

        if(prepaidOfferCategoryBest==null && prepaidOfferServiceBest!=null){
            offerId = prepaidOfferServiceBest.getOfferId();
             view =createDialViewImageButton(R.drawable.black_circle,R.drawable.play_circle_48,R.color.white);

        }else if(prepaidOfferCategoryBest!=null ){
            offerId = prepaidOfferCategoryBest.getOfferId();
            view =createDialViewImageButton(R.drawable.black_circle,R.drawable.lock_48,R.color.white);
        }
        if(view!=null){
            final long finalOfferId = offerId;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NavigationAction(getContext(),IntentActionName.OFFERS_BEO_DETAILS).setOneUsageSerializedData(String.valueOf(finalOfferId)).startAction(IntentActionName.OFFERS_BEO_DETAILS);
                    extraOptionsController.toggle(300);

                }
            });
        }

        realm.close();
        return view;
    }


}
