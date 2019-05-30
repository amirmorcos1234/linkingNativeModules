package ro.vodafone.mcare.android.ui.fragments.callDetails;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;

import ro.vodafone.mcare.android.R;

/**
 * Created by Bivol Pavel on 31.03.2017.
 */

public class CallDetailsTabHost extends FragmentTabHost {

    private Context mContext;

    public CallDetailsTabHost(Context context) {
        super(context);
        this.mContext = context;
        init();
    }


    private void init(){
        inflate(mContext, R.layout.call_details_tabhost, this);
    }



 /*   public build(){

    }*/

}
