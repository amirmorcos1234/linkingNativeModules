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
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 5/2/2017.
 */

public class CostControlWidgetPostpaid extends CostControlWidget {


    public CostControlWidgetPostpaid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CostControlWidgetPostpaid(Context context, AttributeSet attrs, int defStyleAttr) {
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
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                new NavigationAction(context).startAction(IntentActionName.OFFERS_BEO_NO_SEAMLESS);
                extraOptionsController.toggle(300);
                // Toast.makeText(context, "Redirect to Bonusuri si extraoptiuni", Toast.LENGTH_SHORT).show();
            }
        });
        buttonList.add(buttonMore);
        View  offerView = getOfferViewAfterMostImportantHiddenEligibleCategory();
        if(offerView!=null){
            buttonList.add(offerView);
        }

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

            new NavigationAction(getContext()).startAction(IntentActionName.SERVICES_PRODUCTS);
        }
    };

    protected View getOfferViewAfterMostImportantHiddenEligibleCategory(){
        Realm realm = Realm.getDefaultInstance();
        EligibleOffersPostSuccess eligibleOffersSuccesses = (EligibleOffersPostSuccess) RealmManager
                .getRealmObject(realm, EligibleOffersPostSuccess.class);

        if(eligibleOffersSuccesses==null){
            return null;
        }
        RealmResults<EligibleCategoriesPost> categories =  eligibleOffersSuccesses.getEligibleOptionsCategories()
                .where().equalTo(EligibleCategories.IS_HIDDEN,true).notEqualTo(EligibleCategories.CATEGORY, "Hidden", Case.INSENSITIVE).findAll();

        RealmResults<EligibleCategoriesPost> services =  eligibleOffersSuccesses.getEligibleServicesCategories()
                .where().equalTo(EligibleCategories.IS_HIDDEN,true).notEqualTo(EligibleCategories.CATEGORY, "Hidden", Case.INSENSITIVE).findAll();

        Log.d("CostControl","Services and Categories "+(services!=null? String.valueOf(services.size()):"") + (categories!=null? String.valueOf(categories.size()):""));

        RealmList<PostpaidOfferRow> offersFromCateogories = new RealmList<>();
        if(categories!=null) {
            for (EligibleCategoriesPost category : categories) {
                if (category.getEligibleOffersList() != null) {
                    offersFromCateogories.addAll(category.getEligibleOffersList());
                }
            }
        }
        RealmList<PostpaidOfferRow> offersFromServices = new RealmList<>();

        if(services!=null){
            for (EligibleCategoriesPost service: services) {
                if(service.getEligibleOffersList()!=null){
                    offersFromServices.addAll(service.getEligibleOffersList());
                }
            }
        }

        Log.d("CostControl", " Offers categories " + String.valueOf(offersFromCateogories.size()));
        Log.d("CostControl", " Offers services " + String.valueOf(offersFromServices.size()));

        PostpaidOfferRow offerCategoryBest=null;
        if(!offersFromCateogories.isEmpty()){
            Collections.sort(offersFromCateogories, PostpaidOfferRow.getComparatorAfterPriority());
            offerCategoryBest = offersFromCateogories.get(0);

        }
        PostpaidOfferRow offerServiceBest=null;
        if(!offersFromServices.isEmpty()){
            Collections.sort(offersFromServices, PostpaidOfferRow.getComparatorAfterPriority());
            offerServiceBest = offersFromServices.get(0);
        }

        String matrixId = "0";

        FrameLayout view = null;

        if(offerCategoryBest==null && offerServiceBest!=null){
            matrixId = offerServiceBest.getMatrixId();
            view =createDialViewImageButton(R.drawable.black_circle,R.drawable.play_circle_48,R.color.white);

        }else if(offerCategoryBest!=null ){
            matrixId = offerCategoryBest.getMatrixId();
            view =createDialViewImageButton(R.drawable.black_circle,R.drawable.lock_48,R.color.white);
        }

        if(view!=null){
            //final PostpaidOfferRow finalPrepaidOfferCategoryBest = offerCategoryBest;
            final String finalMatrixId = matrixId;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.NullIfEmptyAndTrim(finalMatrixId) != null) {
                        if(VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                            Log.d(TAG, "postPaid " + String.valueOf(finalMatrixId));
                            IntentActionName.OFFERS_BEO_DETAILS.setOneUsageSerializedData(finalMatrixId);
                            new NavigationAction(getContext()).startAction(IntentActionName.OFFERS_BEO_DETAILS);
                            //new NavigationAction(getContext(), IntentActionName.OFFERS).setExtraParameter(String.valueOf(finalOfferId)).startAction(IntentActionName.OFFERS);
                        }
                    }
                    else
                    {
                        new NavigationAction(getContext(), IntentActionName.OFFERS).setExtraParameter(String.valueOf(finalMatrixId)).startAction(IntentActionName.OFFERS);
                    }
                    extraOptionsController.toggle(300);

                }
            });
        }

        realm.close();
        return view;
    }
}
