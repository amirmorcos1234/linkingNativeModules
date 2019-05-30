package ro.vodafone.mcare.android.ui.views.viewholders.general;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Victor Radulescu on 8/30/2017.
 */

public abstract class DynamicViewHolder<T,V extends View> extends BasicViewHolder {

    public DynamicViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void setupWithData(Activity activity, T element);
    public  V getView(){
        return (V)itemView;
    }
}
