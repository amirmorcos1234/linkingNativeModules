package ro.vodafone.mcare.android.widget.gauge;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.Extraoption;
import ro.vodafone.mcare.android.client.model.dashboard.gauge.GaugeOptionsType;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.widget.CostControlWidget;

/**
 * Created by Victor Radulescu on 2/9/2017.
 */

public  class OptionsController {


    private static final int STARTING_ANGLE = 180;

    private static final int STARTING_SWEEP_ANGLE = -40;

    private static final  int EXTRA_RADIOS_IN_DP= 40;

    private static final int ANGLE_SPACE_BETWEEN_ELEMENTS = 30;

    private static final int OPTIONS_BUTTON_SIZE_IN_DP = 48;

    private View viewToDrawArcFrom;

    private Context context;

    List<GaugeOptionsType> gaugeOptionsTypes;

    List<OptionsToggleImageButton> optionToggleImageButtons;

    private CostControlWidget costControlWidget;

    OptionsChangedListener optionsChangedListener;

    Path arcPath;

    public OptionsController(Context context, CostControlWidget costControlWidget, OptionsChangedListener optionsChangedListener) {
        this.context = context;
        this.viewToDrawArcFrom = costControlWidget.getArcView();
        this.costControlWidget = costControlWidget;
        this.optionsChangedListener = optionsChangedListener;
    }

    public void setup() {
        if(isDataReady() && isLayoutReady()){
            setOptionsList();
            setOptionsSwitcher();
        }else if(isDataReady()){
            setupOptionsWhenLayoutReady();
        }
    }

    private void setupOptionsWhenLayoutReady() {
        ViewTreeObserver vto = costControlWidget.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(costControlWidget!=null && costControlWidget.getViewTreeObserver()!=null && costControlWidget.getViewTreeObserver().isAlive()) {
                    costControlWidget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if(isDataReady()){
                        setOptionsList();
                        setOptionsSwitcher();
                    }
                }
            }
        });
    }

    private OptionsToggleImageButton createOptionsButton(GaugeOptionsType type){
        OptionsToggleImageButton newOptionsButton = new OptionsToggleImageButton(context,type);
        costControlWidget.addView(newOptionsButton);
        return newOptionsButton;
    }

    public void setOptionsList(){
        cleanOptions();
        generateOptionsViews();
    }

    public void cleanOptions(){
        try {
            if (optionToggleImageButtons != null && costControlWidget != null) {
                for (int i = 0; i < optionToggleImageButtons.size(); i++) {
                    costControlWidget.removeView(optionToggleImageButtons.get(i));
                }
                optionToggleImageButtons.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateOptionsViews(){
        if(gaugeOptionsTypes==null || gaugeOptionsTypes.isEmpty()){
            return;
        }
        optionToggleImageButtons = new ArrayList<>();
        for (int i = 0; i < gaugeOptionsTypes.size(); i++) {
            OptionsToggleImageButton optionButton = createOptionsButton(gaugeOptionsTypes.get(i));
            int optionsButtonSizeInPx = ScreenMeasure.dpToPx(OPTIONS_BUTTON_SIZE_IN_DP);
            optionToggleImageButtons.add(i,optionButton);
            drawInArc(i,optionsButtonSizeInPx,optionsButtonSizeInPx);
        }
    }

    public void setOptionsSwitcher(){
        if(optionToggleImageButtons==null || optionToggleImageButtons.isEmpty()){
            return;
        }
            optionToggleImageButtons.get(0).setSelected(true);
            optionsChangedListener.OnOptionsChanged(optionToggleImageButtons.get(0).getType());
        for (int i = 0; i < optionToggleImageButtons.size(); i++) {
            optionToggleImageButtons.get(i).setOnTouchListener(toggleOptionsBtnClickListener);
        }
    }

    public void updateWithBalanceList(Extraoption currentExtraoptions) {

        List<Balance> balanceList = currentExtraoptions.getShortBalanceList();
        gaugeOptionsTypes = new ArrayList<>();
        if(balanceList!=null) {
            for (int i = 0; i < balanceList.size(); i++) {
                GaugeOptionsType newGaugeOptionType = updateUnit(balanceList.get(i));
                if (newGaugeOptionType != null && shouldBeDisplayed(balanceList.get(i))) {
                    gaugeOptionsTypes.add(newGaugeOptionType);
                }
            }
        }
    }

    private boolean shouldBeDisplayed(Balance balance) {
        if(balance!=null && balance.getAmountTypeId() == AmountTypeIdEnum.chunk
                && (balance.getChunkmodel()!=0 && balance.getChunkmodel()!=1)){
            return false;
        }
        return true;
    }

    View.OnTouchListener toggleOptionsBtnClickListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(!v.isSelected()) {
                for (OptionsToggleImageButton option : optionToggleImageButtons) {
                    if (((OptionsToggleImageButton)v).getType() != option.getType()) {
                        option.setSelected(false);
                    } else {
                        option.setSelected(true);
                        optionsChangedListener.OnOptionsChanged(option.getType());
                    }
                }
            }
            return false;
        }

    };


    private void drawInArc(int viewPositionInList,int width,int heigth){
        //Path path = getDefaultPath(ScreenMeasure.dpToPx(64),ScreenMeasure.dpToPx(64),ScreenMeasure.dpToPx(150),sweepAngle);
        arcPath = getArcPathWithDimens(viewPositionInList,width,heigth);
        PathMeasure pathMeasure = new PathMeasure(arcPath, false);
        float[] point = new float[2];
        pathMeasure.getPosTan(0, point, null);
        point[0]=point[0]-width;
        point[1]=point[1]-heigth;
        optionToggleImageButtons.get(viewPositionInList).setX(point[0]);
        optionToggleImageButtons.get(viewPositionInList).setY(point[1]);
    }
    private Path getArcPathWithDimens(int position,int viewWidth, int viewHeigth){

        int xCenter =  (int)viewToDrawArcFrom.getX()+ viewToDrawArcFrom.getWidth()/2 + viewWidth/2;
        int yCenter =  (int)viewToDrawArcFrom.getY()+ viewToDrawArcFrom.getHeight()/2 + viewHeigth/2;

        int radius = viewToDrawArcFrom.getHeight()/2 + ScreenMeasure.dpToPx(EXTRA_RADIOS_IN_DP);

        Point point1 = new Point(xCenter-radius,yCenter-radius);
        Point point2 = new Point(xCenter+radius,yCenter+radius);

        final RectF oval = new RectF();
        oval.set(point1.x,point1.y,point2.x,point2.y);
        Path arcPath = new Path();

        float angle =STARTING_ANGLE-(position*ANGLE_SPACE_BETWEEN_ELEMENTS);

        arcPath.arcTo(oval, angle, STARTING_SWEEP_ANGLE, true);
        return arcPath;
    }



    //     data,  voice,  sms,  vas,  cvt,  chunk,
    private GaugeOptionsType updateUnit(Balance balance){
        try{
            GaugeOptionsType gaugeOption = null;

            if(balance.getAmountTypeId()==null || balance.getAmountUnit() ==null){
                return null;
            }
            switch (balance.getAmountTypeId()){
                case data:
                    gaugeOption = GaugeOptionsType.MOBILE_DATA;
                    break;
                case voice:
                    gaugeOption = GaugeOptionsType.VOICE;
                    break;
                case sms:
                    gaugeOption = GaugeOptionsType.SMS;
                    break;
                case vas:
                    gaugeOption = GaugeOptionsType.VAS;
                    break;
                case cvt:
                    gaugeOption = GaugeOptionsType.CVT;
                    break;
                case chunk:
                    gaugeOption = GaugeOptionsType.CHUNCK;
                    break;
            }
            setFormat(gaugeOption,balance);
            return  gaugeOption;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public boolean isDataReady() {
        return gaugeOptionsTypes!=null;
    }
    public boolean isLayoutReady(){
        return costControlWidget!=null && costControlWidget.getMeasuredHeight()>0;
    }

    public GaugeOptionsType getDefaultOption() {

        return gaugeOptionsTypes==null || gaugeOptionsTypes.isEmpty()? null:gaugeOptionsTypes.get(0);
    }

    private void setFormat(GaugeOptionsType gaugeOptionsType,Balance balance){
        gaugeOptionsType.getFormat().setAmountTypeIdEnum(balance.getAmountTypeId());
        gaugeOptionsType.getFormat().setAmountUnitEnum(balance.getAmountUnit());
        gaugeOptionsType.getFormat().setFormatedValues(balance);
    }

    public interface OptionsChangedListener {
        void OnOptionsChanged(GaugeOptionsType optionType);
    }

    public boolean areOptionDrawn(){
        return optionToggleImageButtons!=null && !optionToggleImageButtons.isEmpty();
    }

}
