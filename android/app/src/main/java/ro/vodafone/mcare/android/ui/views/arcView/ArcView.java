package ro.vodafone.mcare.android.ui.views.arcView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;

import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;


/**
 * Created by Bivol Pavel on 15.06.2017.
 */

public class ArcView {

    public static void atachTracksToArcView(DecoView arcView, ArcViewModel arcViewModel){
        try{
            // Create background track
            arcView.addSeries(new SeriesItem.Builder(arcViewModel.getBackgroundTrackColor())
                    .setRange(0, 100, 100)
                    .setInitialVisibility(true)
                    //.setLineWidth(arcViewModel.getBackgroundTrackLineWidth())
                    .setLineWidth(ScreenMeasure.dpToPx(Math.round(arcViewModel.getDataSeriesTrackLineWidth())))
                    .build());

            //Create data series trac
            if(arcViewModel.getMaxValue() > 0){

                ArcViewModel avm = (ArcViewModel) arcViewModel.clone();

                if(avm.getInitialValue() > avm.getMaxValue()){
                    avm.setMaxValue(avm.getInitialValue());
                }

                SeriesItem seriesItem1 = new SeriesItem.Builder(avm.getDataSeriesTrackColor())
                        .setRange(avm.getMinValue(), avm.getMaxValue(), avm.getInitialValue())
                        .setLineWidth(ScreenMeasure.dpToPx(Math.round(avm.getDataSeriesTrackLineWidth())))
                        .build();

                arcView.addSeries(seriesItem1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
