package ro.vodafone.mcare.android.ui.fragments.yourProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Unbinder;
import ro.vodafone.mcare.android.client.model.realm.system.AccountSpecialistLabels;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.interfaces.MvpContract;
import ro.vodafone.mcare.android.ui.activities.yourProfile.YourProfileActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;

/**
 * Created by George B. on 7/10/2017.
 */

public class YourProfileBaseFragment extends BaseFragment {

    protected Unbinder unbinder;
    protected MvpContract.Presenter basePresenter;
    protected ActivityFragmentInterface activityInterface;

    protected boolean shouldShowMsisdnSelector = true;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof ActivityFragmentInterface)
            activityInterface = (ActivityFragmentInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activityInterface != null)
            activityInterface.getNavigationHeader().setTitle(getTitle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((YourProfileActivity) getActivity()).getNavigationHeader().displayDefaultHeader();

        if (!shouldShowMsisdnSelector)
            ((YourProfileActivity) getActivity()).getNavigationHeader().hideSelectorViewWithoutTriangle();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public String getTitle() {
        return AccountSpecialistLabels.getYourProfileTitle();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        if (basePresenter != null)
            basePresenter.unsubscribe();
        activityInterface = null;
    }
}
