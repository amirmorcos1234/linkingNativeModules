package ro.vodafone.mcare.android.ui.utils;

import java.math.BigDecimal;

import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.BalanceInfo;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;

/**
 * Created by user on 15.06.2017.
 */

public class AmountUnitUtils {

    public final static String GB_DATA_UNIT = "GB";
    public final static String MB_DATA_UNIT = "MB";
    public final static String KB_DATA_UNIT = "KB";
    public final static String TB_DATA_UNIT = "TB";

    final static String SEC_UNIT = "sec";
    final static String MIN_UNIT = "min";

    public final static String UNLIMITED = "Nelimitat";

    final static String SMS = "SMS";

    static AmountUnitModel secToMin(Double value){
        return new AmountUnitModel(value/60, MIN_UNIT);
    }

    public static AmountUnitModel getAmountUnitObject(BalanceShowAndNotShown balance, Double amount){
        AmountUnitModel amountUnit = new AmountUnitModel();

        switch (balance.getAmountUnit()) {
            case num:

                switch (balance.getAmountTypeId()){
                    case vas:
                        amountUnit.setUnit(MIN_UNIT + " " + SMS);
                        break;
                    case sms:
                        amountUnit.setUnit(SMS);
                        break;
                    default:
                        amountUnit.setUnit("");
                }
                amountUnit.setAmount(amount);
                break;
            case sec:
                amountUnit = secToMin(amount);
                break;
            case min:
                amountUnit.setAmount(amount);
                amountUnit.setUnit(MIN_UNIT);
                break;
            case kb:
                amountUnit = formatData(amount, new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT, TB_DATA_UNIT});
                break;
            case mb:
                amountUnit = formatData(amount, new String[] {MB_DATA_UNIT, GB_DATA_UNIT, TB_DATA_UNIT});
                break;
            case gb:
                amountUnit.setAmount(amount);
                amountUnit.setUnit(GB_DATA_UNIT);
                break;
            case unl:
                amountUnit.setUnit(UNLIMITED);
                break;
        }

        return amountUnit;
    }

    public static AmountUnitModel getAmountUnitObject(BalanceInfo.BalanceTypeEnum balanceTypeEnum ,BalanceInfo.BalanceUnitEnum balanceUnitEnum, Double amount){
        AmountUnitModel amountUnit = new AmountUnitModel();

        switch (balanceUnitEnum) {
            case num:
                switch (balanceTypeEnum){
                    case vas:
                        amountUnit.setUnit(MIN_UNIT + " " + SMS);
                        break;
                    case sms:
                        amountUnit.setUnit(SMS);
                        break;
                    default:
                        amountUnit.setUnit("");
                }
                amountUnit.setAmount(amount);
                break;
            case sec:
                amountUnit = secToMin(amount);
                break;
            case min:
                amountUnit.setAmount(amount);
                amountUnit.setUnit(MIN_UNIT);
                break;
            case kb:
                amountUnit = formatData(amount, new String[] {KB_DATA_UNIT, MB_DATA_UNIT, GB_DATA_UNIT, TB_DATA_UNIT});
                break;
            case mb:
                amountUnit = formatData(amount, new String[] {MB_DATA_UNIT, GB_DATA_UNIT, TB_DATA_UNIT});
                break;
            case gb:
                amountUnit.setAmount(amount);
                amountUnit.setUnit(GB_DATA_UNIT);
                break;
            case unl:
                amountUnit.setUnit(UNLIMITED);
                break;
        }

        return amountUnit;
    }

    private static AmountUnitModel formatData(Double value, String[] units) {

        if(value == null || value == 0){
            return new AmountUnitModel(0D, MB_DATA_UNIT);
        }

        int digitGroups = (int) (Math.log10(value)/Math.log10(1024));
        Double formatedDataValue = value / Math.pow(1024, digitGroups);

        if(digitGroups>0 && digitGroups< units.length){
            //TB convert to GB
            if(units[digitGroups].equals(TB_DATA_UNIT)){
                return new AmountUnitModel(formatedDataValue * 1024 ,  units[digitGroups-1]);
            }
            return new AmountUnitModel(formatedDataValue ,  units[digitGroups]);
        }
        return new AmountUnitModel(formatedDataValue ,  units[0]);
    }

}
