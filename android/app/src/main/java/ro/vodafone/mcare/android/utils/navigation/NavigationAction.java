package ro.vodafone.mcare.android.utils.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;


/**
 * Created by Victor Radulescu on 3/20/2017.
 */

public class NavigationAction {

    public static final String FRAGMENT_CLASS_NAME_BUNDLE_KEY = "fragmentClassName";
    public static final String EXTRA_PARAMETER_BUNDLE_KEY = "extraParameter";


    private Context context;

    private boolean finishCurrent;

    private IntentActionName intentActionName;

    public NavigationAction(Context context) {
        this.context = context;
    }

    public NavigationAction(Context context, IntentActionName intentActionName) {
        this.context = context;
        this.intentActionName = intentActionName;
    }

    public NavigationAction finishCurrent(boolean finishCurrent) {
        this.finishCurrent = finishCurrent;
        return this;
    }

    public void startLoggedInNotificationAction(){
        if(intentActionName==null || context==null){
            return;
        }
        intentActionName.runActionNotification(context, finishCurrent);

    }
    public void startAction() {
        if(intentActionName==null || context==null){
            return;
        }
        intentActionName.runAction(context, finishCurrent);
    }
    public void startAction(IntentActionName intentActionName) {
        if(intentActionName==null || context==null){
            return;
        }
        this.intentActionName = intentActionName;
        intentActionName.runAction(context, finishCurrent);
    }

    public void startAction(IntentActionName intentActionName, boolean finishCurrent) {
        if(intentActionName==null || context==null){
            return;
        }
        this.intentActionName = intentActionName;
        intentActionName.runAction(context, finishCurrent);
    }
    public void startAction(IntentActionName intentActionName, boolean finishCurrent,boolean newActivity) {
        if(intentActionName==null || context==null){
            return;
        }
        this.intentActionName = intentActionName;
        intentActionName.runAction(context, finishCurrent,newActivity);
    }

    public void startAction(Class myClass, IntentActionName intentActionName, boolean finishCurrent) {
        this.intentActionName = intentActionName;
        intentActionName.runAction(myClass, context, finishCurrent);
    }


    /*if(context instanceof  SwitchFragmentListener){
            ((SwitchFragmentListener)context).onFragmentAdd(fragment);
        }*/
    public void justSwitchFragment(String fragmentName) {
        if (fragmentName == null || fragmentName.isEmpty() || getFragmentClass(fragmentName) == null)
            return;
    }

    public void justSwitchFragment(Fragment fragment) {
        if (fragment == null) return;
        if (context instanceof SwitchFragmentListener) {
            ((SwitchFragmentListener) context).onFragmentAdd(fragment);
        }
    }


    public  Class getFragmentClass(String fragmentClassName) {
        Class fragmentClas = null;
        try {
            fragmentClas = Class.forName(fragmentClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragmentClas;
    }

    public IntentActionName getIntentActionName() {
        return intentActionName;
    }

    public NavigationAction setExtraParameter(String extraParameter) {
        if (intentActionName != null) {
            intentActionName.setExtraParameter(extraParameter);
        }
        return this;
    }
    public NavigationAction setOneUsageSerializedData(String oneUsageSerializedData) {
        if (intentActionName != null) {
            intentActionName.setOneUsageSerializedData(oneUsageSerializedData);
        }
        return this;
    }
    public NavigationAction setSecondUsageSerializedData(String oneUsageSerializedData) {
        if (intentActionName != null) {
            intentActionName.setSecondSerializedData(oneUsageSerializedData);
        }
        return this;
    }
    public Context getContext() {
        return context;
    }
}
