package ro.vodafone.mcare.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.vodafoneTv.ActiveDevicesFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.utils.FragmentUtils;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Prodan Pavel on 18.06.2018.
 */

public class VodafoneTvActivity extends MenuActivity implements ActivityFragmentInterface {
    private NavigationHeader mNavigationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachFragment(new ActiveDevicesFragment());
        initNavigationHeader();
    }

    @Override
    protected int setContent() {
        return R.layout.activity_vodafone_tv;
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {

    }

    public void initNavigationHeader() {
        mNavigationHeader = findViewById(R.id.vodafoneTv_nav_header);
        mNavigationHeader.setActivity(this);
    }

    @Override
    public void attachFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        if (VodafoneController.isActivityVisible()) {
            transaction.commit();
        } else {
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public NavigationHeader getNavigationHeader() {
        return mNavigationHeader;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_SELECTOR_UPDATED) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            f.onActivityResult(requestCode, resultCode, data);
        }
    }
}
