package ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.mvp;

/**
 * Created by cosmin on 07.07.2017.
 */

public abstract class BaseMVPPresenter<VIEW extends BaseMVPContract.ContractView> {

    private final VIEW view;

    public BaseMVPPresenter(VIEW view) {
        this.view = view;
    }

    public VIEW getView() {
        return view;
    }

}
