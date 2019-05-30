package ro.vodafone.mcare.android.ui.fragments.Beo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.activeOptions.ActiveOptionsCard;
import ro.vodafone.mcare.android.client.model.beo.EligibleOffersInterface;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.fragments.Beo.BeoFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.SelectionPageButton;

/**
 * Created by Alex on 3/10/2017.
 */


public class BeoOfferAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    public static String TAG = "BeoOfferAdapter";
    private Context context;
    private List<EligibleCategories> eligibleCategoriesList;
    private List<EligibleCategoriesPost> eligibleCategoriesPostList;
    private boolean isPrepaid = false;
    private boolean isServicePage = false;

    public BeoOfferAdapter(Context context, List<EligibleCategories> eligibleCategoriesList, List<EligibleCategoriesPost> eligibleCategoriesPostList, boolean isPrepaid, boolean isFromServicePage) {
        this.context = context;
        this.eligibleCategoriesList = eligibleCategoriesList;
        this.eligibleCategoriesPostList = eligibleCategoriesPostList;
        this.isPrepaid = isPrepaid;
        this.isServicePage = isFromServicePage;

        //sortOffersByCategory();
        sortOffersByPriority();
    }

    private void sortOffersByPriority(){
        Log.d(TAG, "sortOffersByPriority()");
        if(eligibleCategoriesList != null){
            Log.d(TAG, "sort eligibleCategoriesList");
            for(EligibleCategories eligibleCategories : eligibleCategoriesList){
                try {
                    Collections.sort(eligibleCategories.getEligibleOffersList(),new Comparator<PrepaidOfferRow>() {
                        @Override
                        public int compare(PrepaidOfferRow s1, PrepaidOfferRow s2) {
                            if(s1.getOfferPriority() == null){
                                if(s2.getOfferPriority() == null){
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if(s2.getOfferPriority() == null){
                                    return -1;
                                } else {
                                    return s1.getOfferPriority().compareTo(s2.getOfferPriority());
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(eligibleCategoriesPostList != null){
            Log.d(TAG, "sort eligibleCategoriesPostList");
            for(EligibleCategoriesPost eligibleCategoriesPost : eligibleCategoriesPostList){
                try {
                    Collections.sort(eligibleCategoriesPost.getEligibleOffersList(),new Comparator<PostpaidOfferRow>() {
                        @Override
                        public int compare(PostpaidOfferRow s1, PostpaidOfferRow s2) {
                            //return s1!=null&&s2!=null?s1.getOfferPriority().compareTo(s2.getOfferPriority()):0;

                            if(s1.getOfferPriority() == null){
                                if(s2.getOfferPriority() == null){
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if(s2.getOfferPriority() == null){
                                    return -1;
                                } else {
                                    return s1.getOfferPriority().compareTo(s2.getOfferPriority());
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /*private void sortOffersByCategory(){
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            if(eligibleCategoriesPostList != null){
                Collections.sort(eligibleCategoriesPostList, new BeoCategoryComparator());
            }
        }
    }*/

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(isPrepaid){
            EligibleCategories eligibleCategories = (EligibleCategories) getGroup(groupPosition);
            return eligibleCategories.getEligibleOffersList().get(childPosition);
        }else{
            EligibleCategoriesPost eligibleCategoriesPost = (EligibleCategoriesPost) getGroup(groupPosition);
            return eligibleCategoriesPost.getEligibleOffersList().get(childPosition);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public long getChildCount(int groupPosition) {
        int result = 0;
        if(isPrepaid){
            EligibleCategories eligibleCategories = (EligibleCategories) getGroup(groupPosition);
            result = eligibleCategories.getEligibleOffersList().size();
        }else{
            EligibleCategoriesPost eligibleCategoriesPost = (EligibleCategoriesPost) getGroup(groupPosition);
            result = eligibleCategoriesPost.getEligibleOffersList().size();
        }

        return result;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        OfferRowInterface item = (OfferRowInterface) getChild(groupPosition, childPosition);
        EligibleOffersInterface eligibleCategories;
        if (isPrepaid) {
            eligibleCategories = (EligibleCategories) getGroup(groupPosition);
        }else{
            eligibleCategories  = (EligibleCategoriesPost) getGroup(groupPosition);
        }

        if(null != eligibleCategories.getIsHidden() && eligibleCategories.getIsHidden() ) {
            //return a o height view to prevent nullPointer exception
            LinearLayout rlmain = new LinearLayout(context);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0,0);
            rlmain.setLayoutParams(llp);

            return rlmain;

        }else{
            SelectionPageButton spb;

            String imagePath = null;
            if(item instanceof PostpaidOfferRow){
                imagePath = ((PostpaidOfferRow) item).getOfferImage();
            }else{
                imagePath = ((PrepaidOfferRow) item).getOfferImage();
            }
            Log.d(TAG,"imagePath : " + imagePath);
            if(imagePath != null){
                Log.d(TAG,"imagePath not null");

                String offerPrice;

                //Old functionality
                /*if(VodafoneController.getInstance().getUser() instanceof PostPaidUser && isServicePage){
                    offerPrice = String.format(BEOLabels.getEuro_amount_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                } else {
                    offerPrice = String.format(BEOLabels.getEuro_price_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                }*/


                User user = VodafoneController.getInstance().getUser();
                if(user instanceof EbuMigrated || (user instanceof CBUUser && !isServicePage)){
                    offerPrice = String.format(BEOLabels.getEuro_amount_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                } else {
                    offerPrice = String.format(BEOLabels.getEuro_price_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                }

                offerPrice = offerPrice.replaceAll(",", ".");
                spb =  new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue), item.getOfferName(),
                         offerPrice , new BeoFragment(), null);

                if(isServicePage){
                    ImageView iconView = (ImageView) spb.getLayoutView().findViewById(R.id.button_icon);
                    Glide.with(context)
                            .load(imagePath)
                            .into(iconView);
                    iconView.setVisibility(View.VISIBLE);

                    Log.d(TAG,"Drawable d " + iconView.getDrawable());
                }

                return spb.getLayoutView();
            }
            else{
                Log.d(TAG,"imagePath  null");
                String amountLabel;
                if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
                    amountLabel = String.format(BEOLabels.getEuro_amount_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                } else {
                    amountLabel = String.format(BEOLabels.getEuro_price_currency(),NumbersUtils.twoDigitsAfterDecimal(Double.valueOf(String.valueOf(item.getOfferPrice()))));
                }
                amountLabel = amountLabel.replaceAll(",", ".");
                Log.d(TAG, amountLabel);

                if(context!=null){
                    spb =  new SelectionPageButton(context, ContextCompat.getColor(context, R.color.product_card_primary_dark_blue),
                            item.getOfferName(), amountLabel, new BeoFragment(), null);
                    return spb.getLayoutView();
                }
            }
        }
        return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int result = 0;

        if(isPrepaid){
            EligibleCategories eligibleCategories = (EligibleCategories) getGroup(groupPosition);
            result = eligibleCategories.getEligibleOffersList().size();
        }else{
            EligibleCategoriesPost eligibleCategoriesPost = (EligibleCategoriesPost) getGroup(groupPosition);
            result = eligibleCategoriesPost.getEligibleOffersList().size();
        }

        return result;
    }

    @Override
    public EligibleOffersInterface getGroup(int groupPosition) {
        if(isPrepaid) {
            return eligibleCategoriesList.get(groupPosition);
        }else{
            return eligibleCategoriesPostList.get(groupPosition);
        }
    }

    @Override
    public int getGroupCount() {
        if(isPrepaid) {
            return eligibleCategoriesList.size();
        }else{
            return eligibleCategoriesPostList.size();
        }

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inf = (LayoutInflater) (context!=null?context:VodafoneController.getInstance())
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.beo_category_item, null);
        }
        TextView categoryTittle = (TextView) convertView.findViewById(R.id.category_tittle);

        if(null != getGroup(groupPosition).getIsHidden()&& getGroup(groupPosition).getIsHidden()){
            categoryTittle.setVisibility(View.GONE);
        }else{
            categoryTittle.setText(getGroup(groupPosition).getCategory());
            categoryTittle.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Group Text Clicked","Group Text Clicked");
                        }
                    });

        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

/*    static class BeoCategoryComparator implements Comparator<EligibleCategoriesPost> {
        public int compare(EligibleCategoriesPost c1, EligibleCategoriesPost c2)
        {
            BeoCategoryEnum categoryEnum1 = null;
            BeoCategoryEnum categoryEnum2 = null;

            if(c1 != null){
                categoryEnum1 =c1.getCategoryEnum();
            }

            if(c2 != null){
                categoryEnum2 =c2.getCategoryEnum();
            }

            if(categoryEnum1 == null)
                if(categoryEnum2 == null)
                    return 0; //equal
                else
                    return 1; // null is after other strings
            else // this.member != null
                if(categoryEnum2 == null)
                    return -1;  // all other strings are before null
                else
                    return categoryEnum1.compareTo(categoryEnum2);
        }
    }*/

}
