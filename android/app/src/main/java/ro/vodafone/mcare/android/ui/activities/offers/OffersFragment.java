package ro.vodafone.mcare.android.ui.activities.offers;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ro.vodafone.mcare.android.ui.fragments.BaseFragment;

/**
 * Created by user on 08.04.2017.
 */

public abstract class OffersFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    public abstract String getTitle();

}
