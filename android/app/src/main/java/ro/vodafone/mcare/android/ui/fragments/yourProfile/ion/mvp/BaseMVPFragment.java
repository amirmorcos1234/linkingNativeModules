package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.vodafone.mcare.android.ui.fragments.BaseFragment;

/**
 * Created by cosmin on 5/19/2017.
 */

public abstract class BaseMVPFragment<PRESENTER extends BaseMVPContract.ContractPresenter> extends BaseFragment {

    private PRESENTER presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        presenter = bindPresenter();
        View view = onCreateFragmentView();
        return view;
    }

    public abstract View onCreateFragmentView();

    public abstract PRESENTER bindPresenter();

    public PRESENTER getPresenter() {
        return presenter;
    }

}
