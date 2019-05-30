package ro.vodafone.mcare.android.ui.utils;

import com.vfg.commonutils.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Bivol Pavel on 19.04.2017.
 */

public class NumbersUtils {
    public static String twoDigitsAfterDecimal(Float amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0.00");
            f.setRoundingMode(RoundingMode.HALF_UP);
            return f.format(amount);
        }
        return "0.00";
    }

    public static String twoDigitsAfterDecimal(Double amount) {
        if (amount != null && amount != 0 && !amount.isNaN()) {
            DecimalFormat f = new DecimalFormat("0.00");
            f.setRoundingMode(RoundingMode.HALF_UP);
            return f.format(amount);
        }
        return "0.00";
    }

    public static String twoDigitsAfterDecimalAlwaysDot(Double amount) {
        if (amount != null && amount != 0 && !amount.isNaN()) {
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.ROOT);
			DecimalFormat f = (DecimalFormat)nf;
			f.applyPattern("0.00");
            return f.format(amount);
        }
        return "0.00";
    }

    public static String roundDownWithTwoDigitsAfterDecimal(Float amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0.00");
            f.setRoundingMode(RoundingMode.DOWN);
            return f.format(amount);
        }
        return "0.00";
    }

    public static String roundDownWithTwoDigitsAfterDecimal(Double amount) {
        if (amount != null && amount != 0 && !amount.isNaN()) {
            DecimalFormat f = new DecimalFormat("0.00");
            f.setRoundingMode(RoundingMode.DOWN);
            return f.format(amount);
        }
        return "0.00";
    }

    public static String oneDigitAfterDecimal(Float amount) {
        if (amount != null && amount != 0 && !amount.isNaN()) {
            DecimalFormat f = new DecimalFormat("##0.#");
            return f.format(amount);
        }
        return "0.0";
    }

    public static String oneDigitAfterDecimal(Double amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("##0.#");
            return f.format(amount);
        }
        return "0.0";
    }

    public static String roundUP(Float amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0");
            f.setRoundingMode(RoundingMode.HALF_UP);
            return f.format(amount);
        }
        return "0";
    }

    public static String roundUP(Double amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0");
            f.setRoundingMode(RoundingMode.HALF_UP);
            return f.format(amount);
        }
        return "0";
    }

    public static String roundDown(Float amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0");
            f.setRoundingMode(RoundingMode.DOWN);
            return f.format(amount);
        }
        return "0";
    }

    public static String roundDown(Double amount) {
        if (amount != null && amount != 0) {
            DecimalFormat f = new DecimalFormat("0");
            f.setRoundingMode(RoundingMode.DOWN);
            return f.format(amount);
        }
        return "0";
    }

    public static BigDecimal truncateDecimal(Double x, int numberofDecimals){
        if(x == null){
            x = 0D;
        }

        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }

    public static BigDecimal truncateDecimal(Float x,int numberofDecimals){
        if(x == null){
            x = 0F;
        }

        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }

    public static boolean isFloat(String value) {
        try {
            float number = Float.parseFloat(value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static String getIntegerPart(String value){
        return StringUtils.substringBefore(value, ".");
    }
}
