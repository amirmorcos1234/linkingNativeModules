package ro.vodafone.mcare.android.presenter.adapterelements;

import android.view.View;

import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.ui.utils.textwatcher.AutoCompleteTextWatcher;
import ro.vodafone.mcare.android.ui.views.autocomplete.VodafoneAutoCompleteTextView;

/**
 * Created by Victor Radulescu on 2/12/2018.
 */

public class VdfAutoCompleteTvElement extends StaticAdapterElement {

    VodafoneAutoCompleteTextView vdfAutoCompleteTv;

    public VdfAutoCompleteTvElement(int type, int order,VodafoneAutoCompleteTextView vdfAutoCompleteTv) throws NullPointerException {
        super(type, order);
        this.vdfAutoCompleteTv = vdfAutoCompleteTv;
    }

    @Override
    protected View getView() {

        return vdfAutoCompleteTv;
    }

    public void setText(String text){
       if(vdfAutoCompleteTv==null){
           return;
       }
       vdfAutoCompleteTv.setText(text);
   }
   public void setHint(String hint){
       if(vdfAutoCompleteTv==null){
           return;
       }
       vdfAutoCompleteTv.setHint(hint);
   }
   public void addTextWatcher(AutoCompleteTextWatcher textWatcher){
       if(vdfAutoCompleteTv==null){
           return;
       }
       vdfAutoCompleteTv.addTextChangedListener(textWatcher);
   }

}
