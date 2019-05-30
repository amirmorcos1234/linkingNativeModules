package ro.vodafone.mcare.android.widget.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Radulescu on 3/6/2017.
 */

/**
 * A simple Bar Chart where the bar heights are dependent on each other.
 */
public class BarChart extends BaseBarChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public BarChart(Context context) {
        super(context);

        initializeGraph();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BarChart,
                0, 0
        );

        try {

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initializeGraph();
    }

    /**
     * Adds a new {@link org.eazegraph.lib.models.BarModel} to the BarChart.
     * @param _Bar The BarModel which will be added to the chart.
     */
    public void addBar(HistoryModel _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }

    /**
     * Adds a new list of {@link org.eazegraph.lib.models.BarModel} to the BarChart.
     * @param _List The BarModel list which will be added to the chart.
     */
    public void addBarList(List<HistoryModel> _List) {
        mData = _List;
        onDataChanged();
    }

    /**
     * Returns the data which is currently present in the chart.
     * @return The currently used data.
     */
    @Override
    public List<HistoryModel> getData() {
        return mData;
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void clearChart() {
        mData.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();
        mData = new ArrayList<>();

        mValuePaint = new Paint(mLegendPaint);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        if(this.isInEditMode()) {
            addBar(new HistoryModel(2.3f));
            addBar(new HistoryModel(2.f));
            addBar(new HistoryModel(3.3f));
            addBar(new HistoryModel(1.1f));
            addBar(new HistoryModel(2.7f));
            addBar(new HistoryModel(2.3f));
            addBar(new HistoryModel(2.f));
            addBar(new HistoryModel(3.3f));
            addBar(new HistoryModel(1.1f));
            addBar(new HistoryModel(2.7f));
        }
    }

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * has changed.
     */
    @Override
    protected void onDataChanged() {
        calculateBarPositions(mData.size());
        super.onDataChanged();
    }

    /**
     * Calculates the bar boundaries based on the bar width and bar margin.
     * @param _Width    Calculated bar width
     * @param _Margin   Calculated bar margin
     */
    protected void calculateBounds(float _Width, float _Margin) {
        float maxValue = 0;
        int   last     = 0;

        for (int i=0;i<mData.size();i++) {
            HistoryModel model = mData.get(i);
            if(model.getValue() > maxValue) {
                maxValue = model.getValue();
                maxBarIndex = i;
            }
        }

        int valuePadding = mShowValues ? (int) mValuePaint.getTextSize() + mValueDistance : 0;

        float heightMultiplier = (mGraphHeight - valuePadding) / maxValue;

        for (HistoryModel model : mData) {
            float height = model.getValue() * heightMultiplier;
            last += _Margin / 2;
            model.setBarBounds(new RectF(last, mGraphHeight - height, last + _Width, mGraphHeight));
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));
            last += _Width + (_Margin / 2);
        }

        Utils.calculateLegendInformation(mData, 0, mContentRect.width(), mLegendPaint);
    }

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected void drawBars(Canvas _Canvas) {

        RectF maxBounds = getBarBounds().get(maxBarIndex);

        for (HistoryModel model : mData) {
            if(model.getValue()>0){
                RectF bounds = model.getBarBounds();

                mGraphPaint.setColor(model.getColor());

           /* _Canvas.drawRect(
                    bounds.left,
                    bounds.bottom - (bounds.height() * mRevealValue),
                    bounds.right,
                    bounds.bottom, mGraphPaint);*/
                //canvas.drawRect(new RectF(0, 110, 100, 290), paint);
                //canvas.drawRoundRect(new RectF(0, 100, 100, 200), 6, 6, paint);
                _Canvas.drawRoundRect(new RectF(  bounds.left,
                                bounds.bottom - (bounds.height() * mRevealValue)+ Utils.dpToPx(bottomValueMarginInDP),
                                bounds.right,
                                bounds.bottom -Utils.dpToPx(bottomValueMarginInDP))
                        , 6, 6, mGraphPaint);
            }
                if (mShowValues ) {
                    _Canvas.drawText(Utils.getFloatString(model.getValue(), mShowDecimal), model.getLegendBounds().centerX(),
                            maxBounds.bottom - (maxBounds.height() * mRevealValue) - mValueDistance - Utils.dpToPx(bottomValueMarginInDP), mValuePaint);
                }

        }
        //_Canvas.save(Canvas.ALL_SAVE_FLAG);
        if(maxBounds.bottom- maxBounds.top > Utils.dpToPx(5)){
            //drawDashedLine(_Canvas,maxBounds);
        }

    }

    /**
     * Returns the list of data sets which hold the information about the legend boundaries and text.
     * @return List of BaseModel data sets.
     */
    @Override
    protected List<? extends BaseModel> getLegendData() {
        return mData;
    }

    @Override
    protected List<RectF> getBarBounds() {
        ArrayList<RectF> bounds = new ArrayList<RectF>();
        for (HistoryModel model : mData) {
            bounds.add(model.getBarBounds());
        }
        return bounds;
    }
    Path path = new Path();
    private void drawDashedLine(Canvas canvas, RectF maxBarRect){

        //path.reset();
        Rect graphRect = canvas.getClipBounds();
        int startingX = graphRect.left;
        int endX = graphRect.right;

        Paint fgPaintSel = new Paint();
        fgPaintSel.setARGB(102, 255, 255,255);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        //fgPaintSel.setStrokeWidth(2);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {Utils.dpToPx(5),Utils.dpToPx(10)}, 0));
        //canvas.drawLine(startingX,maxBarRect.bottom,endX,maxBarRect.bottom -Utils.dpToPx(1),fgPaintSel);

        //path.addRect(startingX,maxBarRect.bottom,endX,maxBarRect.bottom -Utils.dpToPx(1), Path.Direction.CW);
        // view.drawLine();
        path.moveTo(startingX, maxBarRect.bottom);
        path.quadTo(startingX,maxBarRect.bottom,endX,maxBarRect.bottom -Utils.dpToPx(1));

        path.moveTo(startingX, maxBarRect.bottom);

        canvas.drawPath(path,fgPaintSel);

    }
    public void setLegendColor(int legendColor) {
        super.setLegendColor(legendColor);
        // this.mLegendColor = legendColor;
        mLegendPaint.setColor(legendColor);
        mValuePaint.setColor(legendColor);
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = org.eazegraph.lib.charts.BarChart.class.getSimpleName();

    private List<HistoryModel>  mData;

    private Paint           mValuePaint;
    private int             mValueDistance = (int) Utils.dpToPx(2);

    protected int maxBarIndex = 0;

    protected int bottomValueMarginInDP = 2;


}
