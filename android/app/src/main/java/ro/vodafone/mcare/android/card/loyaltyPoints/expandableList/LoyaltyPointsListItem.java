package ro.vodafone.mcare.android.card.loyaltyPoints.expandableList;

import android.view.View;

/**
 * Created by User on 25.04.2017.
 */

public class LoyaltyPointsListItem {

    String _itemName;
    String _itemSubtext;
    String _itemParameter;
    View.OnClickListener _tabLinkListner;

    public LoyaltyPointsListItem(String _itemName,String _itemSutbtext, String _itemParameter, View.OnClickListener _tabLinkListner) {
        this._itemName = _itemName;
        this._itemParameter = _itemParameter;
        this._tabLinkListner = _tabLinkListner;
        this._itemSubtext = _itemSutbtext;
    }

    public String get_itemName() {
        return _itemName;
    }

    public void set_itemName(String _itemName) {
        this._itemName = _itemName;
    }

    public String get_itemSubtext(){
        return _itemSubtext;
    }

    public void set_itemSubtext(String itemSubtext){
        this._itemSubtext = itemSubtext;
    }

    public String  get_itemParameter() {
        return _itemParameter;
    }

    public void set_itemParameter(String  _itemParameter) {
        this._itemParameter = _itemParameter;
    }

    public View.OnClickListener get_tabLinkListner() {
        return _tabLinkListner;
    }

    public void set_tabLinkListner(View.OnClickListener _tabLinkListner) {
        this._tabLinkListner = _tabLinkListner;
    }
}
