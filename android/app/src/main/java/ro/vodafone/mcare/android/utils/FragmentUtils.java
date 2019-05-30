package ro.vodafone.mcare.android.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.manager.SupportRequestManagerFragment;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Bivol Pavel on 28.01.2017.
 */
public class FragmentUtils {

    public static Fragment getVisibleFragment(AppCompatActivity activity, boolean haveFragmentParrent){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        List<Fragment> visibleFragments = new ArrayList<>();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.getUserVisibleHint() && !(fragment instanceof SupportRequestManagerFragment))
                    visibleFragments.add(fragment);
            }
        }

        if(haveFragmentParrent){
            if(visibleFragments != null && visibleFragments.size() > 0){
                if(visibleFragments.get(visibleFragments.size() - 1).getChildFragmentManager().getFragments() == null ||
                        visibleFragments.get(visibleFragments.size() - 1).getChildFragmentManager().getFragments().isEmpty()  ) {
                    Log.d("FragmentUtils", "Childs not found List is empty");
                    return visibleFragments.get(visibleFragments.size() - 1);
                }else{
                    Log.d("FragmentUtils", "Childs found : " + visibleFragments.get(visibleFragments.size() - 1).getChildFragmentManager().getFragments().size());
                    return visibleFragments.get(visibleFragments.size() - 1).getChildFragmentManager().getFragments().get(0);
                }
            }
        }else{
            if(visibleFragments.size() != 0){
                return visibleFragments.get(visibleFragments.size() - 1);
            }
        }

        return null;
    }
    public static   Class getFragmentClass(String fragmentClassName) {
        Class fragmentClas = null;
        try {
            fragmentClas = Class.forName(fragmentClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragmentClas;
    }

    public static Fragment getInstance(FragmentManager fm, Class<? extends Fragment> clazz) {
        return getInstance(fm, clazz, null, false);
    }

    public static Fragment getInstance(FragmentManager fm, Class<? extends Fragment> clazz, boolean shouldCreate) {
        return FragmentUtils.getInstance(fm, clazz, null, shouldCreate);
    }

    public static Fragment getInstance(FragmentManager fm, Class<? extends Fragment> clazz, Bundle args, boolean shouldCreate){
        if(fm == null)
            return null;

        Fragment fragment = fm.findFragmentByTag(getTagForFragment(clazz));
        if((fragment == null) && shouldCreate)
            fragment = newInstance(clazz, args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    public static Object newInstanceByClassName(String fragmentName){
        Class<? extends Fragment> clazz;

        try {
            clazz = (Class<? extends Fragment>)Class.forName(fragmentName);
            return FragmentUtils.newInstance(clazz);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Fragment class " + fragmentName + " not found", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object newInstanceByClassName(String fragmentName, String extraParameter){
        Class<? extends Fragment> clazz;

        try {
            clazz = (Class<? extends Fragment>)Class.forName(fragmentName);
            Bundle args = new Bundle();
            args.putString(NavigationAction.EXTRA_PARAMETER_BUNDLE_KEY, extraParameter);
            return FragmentUtils.newInstance(clazz, args);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Fragment class " + fragmentName + " not found", e);
        }
    }

    public static Fragment newInstance(Class<? extends Fragment> clazz){
        return FragmentUtils.newInstance(clazz, null);
    }

    public static Fragment newInstance(Class<? extends Fragment> clazz, Bundle args){
        Fragment fragment;
        try {
            fragment = clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create fragment " + clazz.getCanonicalName(), e);
        }
        if(args == null)
            args = new Bundle();
        args.putLong(BaseFragment.KEY_ARGS_TIMESTAMP, System.currentTimeMillis());
        fragment.setArguments(args);
        return fragment;
    }

    public static String getTagForFragment(Object classOrObject){
        if(classOrObject instanceof Class)
            return ((Class)classOrObject).getCanonicalName();
        else
            return classOrObject.getClass().getCanonicalName();
    }

    public static void clearFragmentsBackStack(AppCompatActivity activity){
        FragmentManager fm = activity.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static boolean isFragmentInBackStack(Class<? extends Fragment> clazz, AppCompatActivity appCompatActivity){
        String backStateName = clazz.getName();
        FragmentManager manager = appCompatActivity.getSupportFragmentManager();
        return manager.popBackStackImmediate (backStateName, 0);
    }
}
