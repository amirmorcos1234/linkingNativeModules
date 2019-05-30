package ro.vodafone.mcare.android.ui.utils.butterknife;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Victor Radulescu on 9/1/2017.
 */

public class ButterKnifeActions {
    public static final ButterKnife.Action<View> GONE = new ButterKnife.Action<View>() {

        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
}
