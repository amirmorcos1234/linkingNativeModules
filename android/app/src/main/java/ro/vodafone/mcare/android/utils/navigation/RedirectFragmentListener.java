package ro.vodafone.mcare.android.utils.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Victor Radulescu on 3/27/2017.
 */

public class RedirectFragmentListener implements View.OnClickListener {

    Fragment fragment;


    Context context;

    public RedirectFragmentListener(Context context,Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        if(context instanceof  SwitchFragmentListener){
            ((SwitchFragmentListener)context).onFragmentAdd(fragment);
        }
    }

}
