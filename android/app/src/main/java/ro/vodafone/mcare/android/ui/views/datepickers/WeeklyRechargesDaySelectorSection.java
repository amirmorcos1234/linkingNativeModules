package ro.vodafone.mcare.android.ui.views.datepickers;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ro.vodafone.mcare.android.R;

/**
 * Created by User on 16.03.2017.
 */

public class WeeklyRechargesDaySelectorSection extends LinearLayout {
    private Context mContext;
    private LinearLayout radioGroupLayout;

    private List<WeeklyRechargesDayItem> radioButtonBobbles;

    private String selectedDay;

    private int selectedDayId;

    private Callback callback;

    private static final String TAG = "WeeklyRechargesDaySelectorSection";

    LocalDate currentDate = LocalDate.now();



    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected List<String> daysEnumList = new LinkedList<>();
    public WeeklyRechargesDaySelectorSection(Context context) {
        super(context);
        this.mContext = context;
        if(!isInEditMode()){
            init(null);
        }

    }

    public WeeklyRechargesDaySelectorSection(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if(!isInEditMode()){
            init(attrs);
        }

    }

    public WeeklyRechargesDaySelectorSection(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if(!isInEditMode()){
            init(attrs);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeeklyRechargesDaySelectorSection(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if(!isInEditMode()){
            init(attrs);
        }

    }

    private void init(AttributeSet attrs){
        inflate(mContext, R.layout.day_selector_section, this);

        radioGroupLayout = (LinearLayout) findViewById(R.id.radio_group_layout);
        createListOfDays();

    }

    public void createListOfDays(){
        for (int i =0; i<DaysEnum.values().length; i++) {
            daysEnumList.add(String.valueOf(DaysEnum.values()[i]));
        }

        buildBoobles();
        setClickListner();

    }

    private void buildBoobles(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);

        radioButtonBobbles = new ArrayList<>();

        for(int i = 0; i<daysEnumList.size(); i++){
            WeeklyRechargesDayItem weeklyRechargesDayItem = new WeeklyRechargesDayItem(mContext);

            weeklyRechargesDayItem.setLayoutParams(param);
            weeklyRechargesDayItem.setDay(daysEnumList.get(i));

            radioGroupLayout.addView(weeklyRechargesDayItem);
            radioButtonBobbles.add(weeklyRechargesDayItem);
        }
    }

    private void setClickListner(){
        for (WeeklyRechargesDayItem weeklyRechargesDayItem : radioButtonBobbles){
            weeklyRechargesDayItem.setOnClickListener(dayPickListener);
        }
    }

    private View.OnClickListener dayPickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                changeSelectedButton((WeeklyRechargesDayItem) v);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void changeSelectedButton(WeeklyRechargesDayItem button) {
        for (WeeklyRechargesDayItem weeklyRechargesDayItem : radioButtonBobbles) {
            if (weeklyRechargesDayItem.isSelected()) {
                weeklyRechargesDayItem.unselect();
            }
        }

        button.onSelect();
        selectedDay = button.getSelectedDay();
        for (DaysEnum d : DaysEnum.values()){
            if(d.toString().equalsIgnoreCase(selectedDay)){
                selectedDayId = DaysEnum.valueOf(selectedDay).getId();
            }
        }

        if (callback!=null) callback.selectElement(selectedDayId);

    }

    private enum DaysEnum{
        L(1), M(2), Mi(3), J(4), V(5), S(6), D(7);

        private int id;

        DaysEnum(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public void setDefaultSelectedDay(){
        changeSelectedButton(radioButtonBobbles.get(currentDate.getDayOfWeek() - 1 ));
    }

    public interface Callback {
        public void selectElement(int selectDayId);
    }
}
