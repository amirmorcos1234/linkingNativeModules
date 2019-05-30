package ro.vodafone.mcare.android.ui.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by User on 18.05.2017.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter() {

    }

    public DecimalDigitsInputFilter(int digitsAfterZero) {
        mPattern=Pattern.compile("^[0-9\\+]+((\\.[0-9]{0," + (digitsAfterZero) + "})?)||(\\.)?");
    }

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
         mPattern=Pattern.compile("(([0-9]{1})([0-9]{0,"+(digitsBeforeZero-1)+"})?)?(\\.[0-9]{0,"+digitsAfterZero+"})?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString());
        if (!builder.toString().matches(mPattern.pattern())) {
            if(source.length()==0)
                return dest.subSequence(dstart, dend);
            return "";
        }

        return null;

    }

}