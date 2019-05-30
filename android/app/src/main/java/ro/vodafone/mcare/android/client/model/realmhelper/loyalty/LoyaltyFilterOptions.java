package ro.vodafone.mcare.android.client.model.realmhelper.loyalty;

import java.util.ArrayList;

/**
 * Created by Victor Radulescu on 9/15/2017.
 */

public class LoyaltyFilterOptions {

    private ArrayList<String> categories;
    private boolean [] showExpireOptions;

    public LoyaltyFilterOptions(ArrayList<String> categories, boolean[] showExpireOptions) {
        this.categories = categories;
        this.showExpireOptions = showExpireOptions;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public boolean[] getShowExpireOptions() {
        return showExpireOptions;
    }

    public void setShowExpireOptions(boolean[] showExpireOptions) {
        this.showExpireOptions = showExpireOptions;
    }
    public int getNumberOfExpiredOptionsFlaggedTrue(){
        int numberOfOptionsFlaggedTrue = 0;
        if(showExpireOptions!=null&& showExpireOptions.length>0){
            for (boolean showExpireOption : showExpireOptions) {
                if (showExpireOption) {
                    numberOfOptionsFlaggedTrue++;
                }
            }
        }
        return numberOfOptionsFlaggedTrue;
    }
    public int getSafeLenghtOfExpiredOptions(){
        if(showExpireOptions==null){
            return 0;
        }else{
            return showExpireOptions.length;
        }
    }
}
