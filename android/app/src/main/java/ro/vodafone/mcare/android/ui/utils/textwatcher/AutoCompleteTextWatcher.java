package ro.vodafone.mcare.android.ui.utils.textwatcher;

import android.support.annotation.CallSuper;
import android.text.Editable;
import android.text.TextWatcher;

import ro.vodafone.mcare.android.ui.views.autocomplete.VodafoneAutoCompleteTextView;

public abstract  class AutoCompleteTextWatcher implements TextWatcher {

    private int minCharFilter = 1;
    public AutoCompleteTextWatcher(VodafoneAutoCompleteTextView vodafoneAutoCompleteTextView) {
        this.vodafoneAutoCompleteTextView = vodafoneAutoCompleteTextView;
    }

    final VodafoneAutoCompleteTextView vodafoneAutoCompleteTextView;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @CallSuper
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (searchStringIsNull(s)) {
            onInvalidTextChanged();
        } else if(haveMinimumChars(s.toString())){
            onValidTextChanged(s.toString(), start, before, count);
        }
    }

    private boolean haveMinimumChars(String input) {
        return input!=null && input.length()>= minCharFilter;
    }

    @Override
    @CallSuper
    public void afterTextChanged(Editable s) {
        vodafoneAutoCompleteTextView.displayClearTextIcon(s.toString());
    }

    public abstract void onInvalidTextChanged();

    public abstract void onValidTextChanged(String validText, int start, int before, int count);

    private boolean searchStringIsNull(CharSequence s) {
        return s == null || s.equals("") || s.toString().replaceAll(" ", "").equals("");
    }

    public int getMinCharFilter() {
        return minCharFilter;
    }

    public void setMinCharFilter(int minCharFilter) {
        this.minCharFilter = minCharFilter;
    }

    public VodafoneAutoCompleteTextView getVodafoneAutoCompleteTextView() {
        return vodafoneAutoCompleteTextView;
    }
}