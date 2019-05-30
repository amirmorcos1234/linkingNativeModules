package ro.vodafone.mcare.android.ui.views.arcView;

/**
 * Created by Bivol Pavel on 15.06.2017.
 */

public class ArcViewModel implements Cloneable{

    //Tracks Values
    private float minValue;
    private float maxValue;
    private float initialValue;

    //Tracks Lines Widths
    private float backgroundTrackLineWidth;
    private float dataSeriesTrackLineWidth;

    //Tracks colors
    private int backgroundTrackColor;
    private int dataSeriesTrackColor;

    public ArcViewModel(float minValue, float maxValue, float initialValue, float backgroundTrackLineWidth, float dataSeriesTrackLineWidth, int backgroundTrackColor, int dataSeriesTrackColor) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
        this.backgroundTrackLineWidth = backgroundTrackLineWidth;
        this.dataSeriesTrackLineWidth = dataSeriesTrackLineWidth;
        this.backgroundTrackColor = backgroundTrackColor;
        this.dataSeriesTrackColor = dataSeriesTrackColor;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(float initialValue) {
        this.initialValue = initialValue;
    }

    public float getBackgroundTrackLineWidth() {
        return backgroundTrackLineWidth;
    }

    public void setBackgroundTrackLineWidth(float backgroundTrackLineWidth) {
        this.backgroundTrackLineWidth = backgroundTrackLineWidth;
    }

    public float getDataSeriesTrackLineWidth() {
        return dataSeriesTrackLineWidth;
    }

    public void setDataSeriesTrackLineWidth(float dataSeriesTrackLineWidth) {
        this.dataSeriesTrackLineWidth = dataSeriesTrackLineWidth;
    }

    public int getBackgroundTrackColor() {
        return backgroundTrackColor;
    }

    public void setBackgroundTrackColor(int backgroundTrackColor) {
        this.backgroundTrackColor = backgroundTrackColor;
    }

    public int getDataSeriesTrackColor() {
        return dataSeriesTrackColor;
    }

    public void setDataSeriesTrackColor(int dataSeriesTrackColor) {
        this.dataSeriesTrackColor = dataSeriesTrackColor;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
