package ro.vodafone.mcare.android.client.model.card;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Bivol Pavel on 22.03.2017.
 */
public class CardButtonModel {

    private String button_name;
    private View.OnClickListener button_onClickListner;
    private Drawable button_arrow;

    public CardButtonModel(String button_name, View.OnClickListener button_onClickListner, Drawable button_arrow) {
        this.button_name = button_name;
        this.button_onClickListner = button_onClickListner;
        this.button_arrow = button_arrow;
    }

    public String getButton_name() {
        return button_name;
    }

    public void setButton_name(String button_name) {
        this.button_name = button_name;
    }

    public View.OnClickListener getButton_onClickListner() {
        return button_onClickListner;
    }

    public void setButton_onClickListner(View.OnClickListener button_onClickListner) {
        this.button_onClickListner = button_onClickListner;
    }

    public Drawable getButton_arrow() {
        return button_arrow;
    }

    public void setButton_arrow(Drawable button_arrow) {
        this.button_arrow = button_arrow;
    }
}
