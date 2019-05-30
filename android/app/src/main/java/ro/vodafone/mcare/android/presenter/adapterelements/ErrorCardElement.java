package ro.vodafone.mcare.android.presenter.adapterelements;

import android.view.View;

import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;

/**
 * Created by Victor Radulescu on 9/5/2017.
 */

public class ErrorCardElement extends StaticAdapterElement {

    VodafoneGenericCard vodafoneGenericCard;
    public ErrorCardElement(int type, int order,VodafoneGenericCard vodafoneGenericCard) {
        super(type, order);
        this.vodafoneGenericCard = vodafoneGenericCard;
    }

    @Override
    protected VodafoneGenericCard getView() {
        return vodafoneGenericCard;
    }

    public void showLoading(){
        if(getView()==null){
            return;
        }
        getView().showLoading(true);
    }

    public void showError(){
        if(getView()==null){
            return;
        }
        getView().showError(true);
    }

    public void makeCardInvisible() {
        if(getView() == null) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
    }

    public void showError(String message){
        if(getView()==null) {
            return;
        }
        getView().showError(true,message);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(getView()==null){
            return;
        }
        getView().setOnClickListener(onClickListener);
    }

}
