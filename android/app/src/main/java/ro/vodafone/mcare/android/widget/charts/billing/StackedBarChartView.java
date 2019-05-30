 /*******************************************************************************
 * Descharts library
 * Copyright (c) 2014 Bradipao <bradipao@gmail.com>
 * https://plus.google.com/+SidBradipao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

 package ro.vodafone.mcare.android.widget.charts.billing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import java.util.ArrayList;

import ro.vodafone.mcare.android.utils.Logger;

 /**
 * StackedBarChartView() class represents a stacked bar graph widget.
 */

public class StackedBarChartView extends CartesianView {

    // data holder
    private ArrayList<ChartValueSerie> mSeries = new ArrayList<ChartValueSerie>();
    private ChartValueSerie mStacked = new ChartValueSerie();
    private int mXnum=0;
    private int mLabelMaxNum = 10;
     public static Logger LOGGER = Logger.getInstance(StackedBarChartView.class);

    // objects
    private Paint mPnt = new Paint();
    private Paint mPntFill = new Paint();

    /**
     * Constructor.
     */
    public StackedBarChartView(Context context){
        super(context);
        initPaint();
    }

    /**
     * Constructor.
     */
    public StackedBarChartView(Context context,AttributeSet attrs) {
        super(context,attrs);
        initPaint();
    }

    /**
     * Draws the plot.
     */
    public void onDraw(Canvas cnv) {
        if(mSeries.size() != 0){
            calculateAbsoluteYmax();
            super.onDraw(cnv);

            if (bRedraw) {
                drawData();
                bRedraw = false;
            }

            // draw bitmap
            cnv.drawBitmap(mBmp,0,0,null);
        }
    }

    /**
     * Clears ArrayList of series.
     */
    public void clearSeries(){
        while (mSeries.size()>0){
            mSeries.remove(0);
        }
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Adds a serie to data holder ArrayList.
     */
    public void addSerie(ChartValueSerie serie) {
        mSeries.add(serie);
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Returns ArrayList of series.
     */
    public ArrayList<ChartValueSerie> getSeries() {
        return mSeries;
    }

    /**
     * Sets line visibility.
     */
    public void setLineVis(int index,boolean show) {
        mSeries.get(index).setVisible(show);
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Sets line color and size.
     */
    public void setLineStyle(int index,int color,float size) {
        mSeries.get(index).setStyle(color,size);
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Sets line color and size and dip.
     */
    public void setLineStyle(int index,int color,float size,boolean usedip) {
        mSeries.get(index).setStyle(color,size,usedip);
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Sets line color, fillcolor and size and dip.
     */
    public void setLineStyle(int index,int color,int botomColor, int topColor, float size,float width,boolean usedip) {
        mSeries.get(index).setStyle(color,botomColor, topColor,size, width,usedip);
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Sets maximum number of labels on X axis.
     */
    public void setLabelMaxNum(int maxnum) {
        if (maxnum<=0) return;
        mLabelMaxNum = maxnum;
        bRedraw = true;
        postInvalidate();
    }

    /**
     * Gets X,Y ranges across all series
     */
    protected void getXYminmax() {
        // calculate stacked serie
        calcStackedSerie();
        // calculate minmax
        mXnum = mStacked.getSize();
        mYmax = mStacked.mYmax;
        ChartValueSerie serie;
        for (ii=0;ii<mSeries.size();ii++) {
            serie = mSeries.get(ii);
            if (ii==0) {
                mYmin = serie.mYmin;
            } else {
                if (serie.mYmin<mYmin) mYmin = serie.mYmin;
            }
        }
    }

    private void calculateAbsoluteYmax(){
        ChartValueSerie firstSerie;
        ChartValueSerie secondSerie;

        mAbsoluteYMax = 0;

        if(mSeries.size() == 2){
            firstSerie = mSeries.get(0);
            secondSerie = mSeries.get(1);

            for(ii=0;ii<firstSerie.getSize();ii++){
                if (ii==0) {
                    mAbsoluteYMax = firstSerie.getPoint(ii).y + secondSerie.getPoint(ii).y;
                } else {
                    if (firstSerie.getPoint(ii).y + secondSerie.getPoint(ii).y > mAbsoluteYMax) mAbsoluteYMax = firstSerie.getPoint(ii).y + secondSerie.getPoint(ii).y;
                }
            }
        }
        //Todo calcultate for another mSerie size
        LOGGER.d("mAbsoluteYMax" +mAbsoluteYMax);
    }


    /**
     * Draw data from all series
     */
    protected void drawData() {
        float pY;
        ChartValueSerie serie;
        ChartValue v;

        try {
            for (ii=0;ii<mSeries.size();ii++) {
                serie = mSeries.get(ii);
                if (serie.isVisible()) {
                    // set paint
                    mPnt.reset();
                    mPnt.setStyle(Paint.Style.STROKE);
                    mPnt.setColor(serie.mColor);
                    mPntFill.reset();
                    mPntFill.setStyle(Paint.Style.FILL);
                    if (serie.mUseDip) mPnt.setStrokeWidth(dipToPixel(serie.mWidth));
                    else mPnt.setStrokeWidth(serie.mWidth);
                    mPnt.setAntiAlias(true);
                    mPntFill.setAntiAlias(false);

                    // iterate through points
                    for (jj=0;jj<mStacked.mPointList.size();jj++) {
                        v = mStacked.mPointList.get(jj);
                        pY = v.y;
                        // draw rect
                        float width = serie.mBarWidth;

                        if (!Float.isNaN(pY)) {

                            float left = sX+jj*aX+aX/2-width/2;
                            float top = eY;
                            float right = sX+jj*aX+aX/2+width/2;
                            float bottom = eY-(pY-bY)*aY;

                            float gradientAmount = 0.75f;
                            mPntFill.setShader(new LinearGradient(0,top,0,bottom+(top-bottom)*(1-gradientAmount),serie.mFillBottonColor,serie.mFillTopColor,Shader.TileMode.CLAMP));

                            mCnv.drawRect(left,top,right,bottom,mPntFill);

                            if(eY != eY-(pY-bY)*aY){
                                mCnv.drawCircle(sX+jj*aX+aX/2, eY-(pY-bY)*aY, width/2, mPntFill);
                            }
                        }
                        mStacked.updatePoint(jj,v.y-serie.getPoint(jj).y);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

     /**
      * Draw X label on top or bottom
      */
     protected void drawXlabel() {
         mPntText.setTextAlign(Paint.Align.CENTER);
         mPntText.setTextSize(p_x_label_text_size);
         mPath.reset();
         ChartValueSerie mLabel = mSeries.get(0);
         String label;
         int numlab = mLabel.getSize();
         int numdiv = 1 + (numlab-1)/mLabelMaxNum;
         if (p_xtext_bottom) {
             for (ii=0;ii<mLabel.getSize();ii++) {
                 mPath.moveTo(sX+bX+ii*aX,eY-3);
                 mPath.lineTo(sX+bX+ii*aX,eY+3);
                 label = mLabel.mPointList.get(ii).t;
                 if ((label!=null)&&(ii<numlab)&&((ii%numdiv)==0))
                     mCnv.drawText(label,sX+bX+ii*aX,eY+p_x_label_text_size+dipToPixel(10),mPntText);
             }
         } else {
             for (ii=0;ii<mLabel.getSize();ii++) {
                 mPath.moveTo(sX+bX+ii*aX,sY-3);
                 mPath.lineTo(sX+bX+ii*aX,sY+3);
                 label = mLabel.mPointList.get(ii).t;
                 if ((label!=null)&&(ii<numlab)&&((ii%numdiv)==0))
                     mCnv.drawText(label,sX+bX+ii*aX,sY-p_x_label_text_size+dipToPixel(10),mPntText);
             }
         }
         mCnv.drawPath(mPath,mPntAxis);
     }

    /**
     * Calculates drawing coefficients
     */
    protected void calcXYcoefs() {
        aX = (float) dX/mXnum;
        bX = (float) aX/2;
        aY = (float) dY/Math.abs(mYmaxGrid-mYminGrid);
        bY = (float) mYminGrid;
    }


    /**
     * Calculates cumulated values of all series stacked
     */
    protected void calcStackedSerie() {
        // return if no serie exists
        if (mSeries.size()==0) return;
        // clear Stacked serie
        mStacked.clearPointList();
        // else iterate through values of first serie
        ChartValueSerie f = mSeries.get(0);
        float acc = 0;
        for (ii=0;ii<f.getSize();ii++) {
            // first serie always exists
            if (f.isVisible()) acc = f.getPoint(ii).y;
            else acc = 0;
            // add next series if present
            for (jj=1;jj<mSeries.size();jj++) {
                if ((mSeries.get(jj).isVisible())&&(ii<mSeries.get(jj).getSize()))
                    acc += mSeries.get(jj).getPoint(ii).y;
            }
            // store in Stacked
            mStacked.addPoint(new ChartValue(null,acc));
        }
    }
}