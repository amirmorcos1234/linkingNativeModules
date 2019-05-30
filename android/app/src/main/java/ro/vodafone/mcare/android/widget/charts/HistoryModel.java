package ro.vodafone.mcare.android.widget.charts;

import android.graphics.Rect;
import android.graphics.RectF;

import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;

/**
 * Created by Victor Radulescu on 3/7/2017.
 */

public class HistoryModel extends BaseModel implements Comparable {

    private int month;

    private float avarageSum = 0;


    public HistoryModel(String _legendLabel, float _value, int _color) {
        super(_legendLabel);
        this.label = _legendLabel;
        mValue = _value;
        mColor = _color;
    }

    public HistoryModel(String _legendLabel, float _value, int _color,int month) {
        super(_legendLabel);
        this.label = _legendLabel;
        mValue = _value;
        mColor = _color;
        this.month = month;
    }

    public HistoryModel(float _value, int _color) {
        super("" + _value);
        mValue = _value;
        mColor = _color;
    }

    public HistoryModel( String label, int mColor) {
        super(label);
        this.label = label;
        this.mColor = mColor;
        mValue = 0;
    }

    public HistoryModel(float _value) {
        super("" + _value);
        mValue = _value;
        mColor = 0xFFFF0000;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float _value) {
        mValue = _value;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int _color) {
        mColor = _color;
    }

    public RectF getBarBounds() {
        return mBarBounds;
    }

    public void setBarBounds(RectF _bounds) {
        mBarBounds = _bounds;
    }

    public boolean isShowValue() {
        return mShowValue;
    }

    public void setShowValue(boolean _showValue) {
        mShowValue = _showValue;
    }

    public Rect getValueBounds() {
        return mValueBounds;
    }

    public void setValueBounds(Rect _valueBounds) {
        mValueBounds = _valueBounds;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public float getAvarageSum() {
        return avarageSum;
    }

    public void setAvarageSum(float avarageSum) {
        this.avarageSum = avarageSum;
    }

    @Override
    public int compareTo(Object o) {
        BarModel bar = (BarModel) o;
        if (this.mValue > bar.getValue()) {
            return 1;
        }
        else if (this.mValue == bar.getValue()) {
            return 0;
        }
        else {
            return -1;
        }
    }

    /**
     * Label of the bar.
     */
    private String label;

    /**
     * Value of the bar.
     */
    private float mValue;

    /**
     * Color in which the bar will be drawn.
     */
    private int mColor;

    /**
     * Bar boundaries.
     */
    private RectF mBarBounds;

    private boolean mShowValue = false;

    private Rect mValueBounds = new Rect();
}
