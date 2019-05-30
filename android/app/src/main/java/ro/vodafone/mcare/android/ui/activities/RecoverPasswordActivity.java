package ro.vodafone.mcare.android.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.fragments.recover.RecoverPasswordFragmentFirstStep;
import ro.vodafone.mcare.android.ui.fragments.recover.RecoverPasswordFragmentSecondStep;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.utils.FragmentUtils;

/**
 * Created by Bogdan Marica on 20.10.2017.
 */
public class RecoverPasswordActivity extends BaseActivity {

    public static String TAG = "RecoverUsernameActivity";
    Boolean isMigrated = null;
    private ImageView backButton;
    private String phoneNumber;
    private String email;
    private String username;
    private String customerType;

    private Fragment currentFragment;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        setVodafoneBackgroundOnWindow();
        backButton = (ImageView) findViewById(R.id.back_button);

        displayRecoverPasswordFirstStepFragment();
    }

    public void displayRecoverPasswordFirstStepFragment() {
        RecoverPasswordFragmentFirstStep fragment = new RecoverPasswordFragmentFirstStep();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                .commit();

        currentFragment = fragment;
    }

    public void displayRecoverPasswordSecondStepFragment() {
        RecoverPasswordFragmentSecondStep fragment = new RecoverPasswordFragmentSecondStep();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_right)
                .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                .commit();

        currentFragment = fragment;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void setBackButtonVisibility(boolean isVisible) {
        if (isVisible) {
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            backButton.setVisibility(View.INVISIBLE);
            backButton.setOnClickListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        VodafoneController.getInstance().setFromBackPress(true);
        if (currentFragment != null && currentFragment instanceof RecoverPasswordFragmentFirstStep) {
            RecoverPasswordFragmentFirstStep recoverPasswordFragmentFirstStep =
                    (RecoverPasswordFragmentFirstStep) currentFragment;
            if (recoverPasswordFragmentFirstStep.isStepBVisible()) {
                KeyboardHelper.hideKeyboard(RecoverPasswordActivity.this);
                recoverPasswordFragmentFirstStep.inflateStepAViewAfterSteptB();
                return;
            }
        } else if (currentFragment != null && currentFragment instanceof RecoverPasswordFragmentSecondStep) {
            RecoverPasswordFragmentFirstStep fragment = new RecoverPasswordFragmentFirstStep();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fab_slide_in_from_left, R.anim.fab_slide_out_to_left)
                    .replace(R.id.fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                    .commit();

            currentFragment = fragment;
            return;
        }
        super.onBackPressed();
    }
}
