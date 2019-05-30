package ro.vodafone.mcare.android.client.model.billing;

import java.util.HashMap;
import java.util.List;

import ro.vodafone.mcare.android.card.costControl.currentBill.ExpandableListItemModel;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;

/**
 * Created by Victor Radulescu on 3/8/2018.
 */

public class CurrentBillAdapterModel {

    private final String msisdn;
    private AdditionalCost additionalCost;

    private List<ExpandableListItemModel> listDataHeader;

    private HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild;

    private boolean isExpanded;

    public CurrentBillAdapterModel(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public AdditionalCost getAdditionalCost() {
        return additionalCost;
    }

    public void setAdditionalCost(AdditionalCost additionalCost) {
        this.additionalCost = additionalCost;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


    public List<ExpandableListItemModel> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(List<ExpandableListItemModel> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    public HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
