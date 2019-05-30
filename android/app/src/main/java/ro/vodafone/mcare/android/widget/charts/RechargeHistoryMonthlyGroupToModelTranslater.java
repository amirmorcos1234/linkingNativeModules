package ro.vodafone.mcare.android.widget.charts;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ro.vodafone.mcare.android.client.model.topUp.history.RechargeHistoryMonthlyGroup;

/**
 * Created by Victor Radulescu on 3/8/2017.
 */

public class RechargeHistoryMonthlyGroupToModelTranslater {

    List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryList;

    List<HistoryModel> historyModels;

    int barChartColor = Color.argb(225,60,176,200);

    public RechargeHistoryMonthlyGroupToModelTranslater (){

    }

    public RechargeHistoryMonthlyGroupToModelTranslater(List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryList) {
        this.monthlyRechargeHistoryList = monthlyRechargeHistoryList;
    }

    public List<HistoryModel> parse(int numberOfMonths){
        historyModels = new ArrayList<>();
        if(monthlyRechargeHistoryList.isEmpty()){
            return historyModels;
        }
        //int monthsNumberInRechargeList = monthlyRechargeHistoryList.size();
        int newestMonth = 0;
        ArrayList<Integer> months = new ArrayList<>();
        for (RechargeHistoryMonthlyGroup rechargeMonthly: monthlyRechargeHistoryList) {
            months.add(rechargeMonthly.getMonth());
        }
        for (int i=0;i < numberOfMonths;i++) {
          //  if(i == monthsNumberInRechargeList) {
            //sortHistoryModelsAfterMonth();
                //newestMonth = historyModels.get(monthlyRechargeHistoryList.size()-1).getMonth();
            newestMonth = getCurrentMonth()+1; //+1 because our system works from 1 to 12 ( calendar works from 0 to 11)
            if(months.contains(getPreviosMonth(newestMonth,i))){
                HistoryModel historyModel = getHistroyModelWithMonthlyGroup(monthlyRechargeHistoryList.get(i));
                historyModels.add(0,historyModel);
            }else{
                historyModels.add(0,getDefaultHistroyModelWithMonthlyGroup(newestMonth,i));
            }
           // }
           // if(i < monthsNumberInRechargeList && !monthlyRechargeHistoryList.isEmpty()){
                //HistoryModel historyModel = getHistroyModelWithMonthlyGroup(monthlyRechargeHistoryList.get(i));
                //historyModels.add(historyModel);
          //  }else{
               // historyModels.add(0,getDefaultHistroyModelWithMonthlyGroup(newestMonth,i));
           // }
        }
        return historyModels;
    }

    private void sortHistoryModelsAfterMonth(){
       Collections.sort(historyModels, historyModelComparator);
    }
    private int getCurrentMonth(){
        java.util.Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    private HistoryModel getHistroyModelWithMonthlyGroup(RechargeHistoryMonthlyGroup monthlyGroup){
        int month = monthlyGroup.getMonth();
       // int recharges = monthlyGroup.getRechargeHistoryRow().size();
        int sum = monthlyGroup.getSumOfAllRechargeHistory();
        HistoryModel historyModel= new HistoryModel(
                getMonthStringRepresentation(month),
                sum,
                barChartColor,
                month);
        historyModel.setAvarageSum(monthlyGroup.getAverageSpent());

        return historyModel;
    }
    private HistoryModel getDefaultHistroyModelWithMonthlyGroup(int newestMonth,int monthsAgo){

        int previousMonth = getPreviosMonth(newestMonth,monthsAgo);

        return new HistoryModel(
                getMonthStringRepresentation(previousMonth),
                0,
                barChartColor);
    }

    private int getPreviosMonth(int newestMonth,int monthsAgo){

        return  (newestMonth - monthsAgo < 1) ? (12+newestMonth-monthsAgo) : newestMonth;

    }

    public String getMonthStringRepresentation(int month){
        switch (month){
            case 1:
                return "Ian";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "Mai";
            case 6:
                return "Iun";
            case 7:
                return "Iul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "";
    }

    private boolean isValidMonth(long month){
        return month >=1 && month<=12;
    }

  /*  private boolean isBettween6MonthsAgo(int month,int currentMonth){
        return  month<currentMonth;
    }*/

    public List<RechargeHistoryMonthlyGroup> getMonthlyRechargeHistoryList() {
        return monthlyRechargeHistoryList;
    }

    public void setMonthlyRechargeHistoryList(List<RechargeHistoryMonthlyGroup> monthlyRechargeHistoryList) {
        this.monthlyRechargeHistoryList = monthlyRechargeHistoryList;
    }

    public int getBarChartColor() {
        return barChartColor;
    }

    public void setBarChartColor(int barChartColor) {
        this.barChartColor = barChartColor;
    }

    private Comparator<HistoryModel> historyModelComparator = new Comparator<HistoryModel>() {
        @Override
        public int compare(HistoryModel modelH1, HistoryModel modelH2) {
            if(modelH1.getMonth()==modelH2.getMonth())
                return 0;
            return modelH1.getMonth()< modelH2.getMonth()? -1:1;
        }
    };
}
