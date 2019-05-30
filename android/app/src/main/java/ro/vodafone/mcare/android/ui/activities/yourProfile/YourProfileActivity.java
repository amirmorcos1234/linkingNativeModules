package ro.vodafone.mcare.android.ui.activities.yourProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.AccountSpecialistLabels;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base.UnlimitedBaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by George B. on 7/10/2017.
 */


public class YourProfileActivity extends MenuActivity implements ActivityFragmentInterface {

    public static String TAG = "YourProfileActivity";
    NavigationHeader navigationHeader;
    private VodafoneTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

        setTitle();

        initNavigationFragment();
        attachFragment(new YourProfileFragment());
    }

    @Override
    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    private void initNavigationFragment() {
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this);
        navigationHeader.setDefaultAvatarWithUserDataAndNoIdentity();

//        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    @Override
    protected int setContent() {
        return R.layout.activity_your_profile;
    }

    @Override
    public void attachFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (FragmentUtils.getVisibleFragment(this, false) != null && FragmentUtils.getVisibleFragment(this, false).getClass() != fragment.getClass()) {
            //transaction.setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment));
        transaction.commit();
//        fragmentManager.executePendingTransactions();
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {}

    public void setTitle() {

        setTitle(AccountSpecialistLabels.getYourProfileTitle());
    }

    public void setTitle(String text) {
        try {
            title.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

//        if (f instanceof PaymentConfirmationFragment) {
//            f.onActivityResult(requestCode, resultCode, data);
//        }
//        if (resultCode == RESULT_SELECTOR_UPDATED)
//            if (f instanceof PaymentAgreementFragment || f instanceof UnlimitedBaseFragment)
//                f.onActivityResult(requestCode, resultCode, data);
    }
}
