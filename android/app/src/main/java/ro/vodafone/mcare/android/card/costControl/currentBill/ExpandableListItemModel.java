package ro.vodafone.mcare.android.card.costControl.currentBill;

/**
 * Created by Bivol Pavel on 15.04.2017.
 */

public class ExpandableListItemModel {

    private String _itemName;
    private String itemKey = "";
    private Float _itemParameter;
    private String _unit;
    private boolean _isClickable;
    private boolean isExpanded;
    public ExpandableListItemModel(String itemKey, String _itemName, Float _itemParameter, String _unit, boolean _isClickable) {
        this._itemName = _itemName;
        this.itemKey = itemKey;
        this._itemParameter = _itemParameter;
        this._unit = _unit;
        this._isClickable = _isClickable;
    }

    public ExpandableListItemModel(String _itemName, Float _itemParameter, String _unit, boolean _isClickable) {
        this._itemName = _itemName;
        this._itemParameter = _itemParameter;
        this._unit = _unit;
        this._isClickable = _isClickable;
    }

    public String get_itemName() {
        return _itemName;
    }

    public void set_itemName(String _itemName) {
        this._itemName = _itemName;
    }

    public Float get_itemParameter() {
        return _itemParameter;
    }

    public void set_itemParameter(Float _itemParameter) {
        this._itemParameter = _itemParameter;
    }

    public String get_unit() {
        return _unit;
    }

    public void set_unit(String _unit) {
        this._unit = _unit;
    }

    public boolean is_isClickable() {
        return _isClickable;
    }

    public void set_isClickable(boolean _isClickable) {
        this._isClickable = _isClickable;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
