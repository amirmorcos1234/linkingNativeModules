package ro.vodafone.mcare.android.ui.utils.listeners;

import android.content.Context;
import android.view.View;

import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 7/3/2017.
 */

public class RedirectIntentClickListener implements View.OnClickListener {

    private final IntentActionName intentActionName;
    private final Context context;

    public RedirectIntentClickListener(NavigationAction navigationAction) {
        this.context = navigationAction.getContext();
        this.intentActionName = navigationAction.getIntentActionName();
    }

    @Override
    public void onClick(View v) {
        try{
            if(context!=null)
                new NavigationAction(context).startAction(intentActionName);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
