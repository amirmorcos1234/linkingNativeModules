package ro.vodafone.mcare.android.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.interfaces.fragment.base.BaseFragmentCommunicationListener;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by user2 on 5/5/2017.
 */

public class BaseFragment extends Fragment {
    public static final String KEY_ARGS_TIMESTAMP = "mutableArgsTimestamp";

    ProgressDialog progressDialog;
    private static final String TAG = "BaseFragment";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;

    protected Bundle mutableArgs = new Bundle();

    public BaseFragmentCommunicationListener baseFragmentCommunicationListener;


    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VodafoneController.getInstance().setLifecycleOwner(this);
        if (getArguments() != null) {
            if (getArgsTimestamp(getArguments()) >= getArgsTimestamp(mutableArgs)) {
                mutableArgs.clear();
                mutableArgs.putAll(getArguments());
            }
        }
        if (savedInstanceState != null) {
            if (getArgsTimestamp(savedInstanceState) >= getArgsTimestamp(mutableArgs)) {
                mutableArgs.clear();
                mutableArgs.putAll(savedInstanceState);
            }
        }
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
            if (getArgsTimestamp(savedInstanceState) >= getArgsTimestamp(mutableArgs)) {
                mutableArgs.clear();
                mutableArgs.putAll(savedInstanceState);
            }
    }

    @Override
    @CallSuper
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            if (getArgsTimestamp(savedInstanceState) >= getArgsTimestamp(mutableArgs)) {
                mutableArgs.clear();
                mutableArgs.putAll(savedInstanceState);
            }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            baseFragmentCommunicationListener = (BaseFragmentCommunicationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LoyaltyVoucherCommunicationListener");
        }
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mutableArgs != null)
            outState.putAll(mutableArgs);
        outState.putLong(KEY_ARGS_TIMESTAMP, System.currentTimeMillis());
    }

    private long getArgsTimestamp(Bundle bundle) {
        if (bundle == null)
            return 0;
        return bundle.getLong(KEY_ARGS_TIMESTAMP, 0);
    }

    public void showLoadingDialog() {
        try {
            setupProgressDialog();
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLoadingDialog() {
        try {
            setupProgressDialog();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.d(TAG, "stopLoadingDialog");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProgressDialog getProgressDialog() {
        try {
            if (progressDialog == null) {
                if (getActivity() != null && getActivity() instanceof BaseActivity) {
                    progressDialog = ((BaseActivity) getActivity()).getProgressDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }


    public void stopLoadingAfterDuration(long seconds) {


        Observable.timer(seconds, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        D.w();
                        stopLoadingDialog();
                    }
                });
    }


    private void setupProgressDialog() {
        try {
            if (progressDialog == null) {
                if (getActivity() != null && getActivity() instanceof BaseActivity) {
                    progressDialog = ((BaseActivity) getActivity()).getProgressDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void displayCallDialog(final String title, final String overlayText) {
        if (getContext() == null) {
            return;
        }
        final Dialog overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonActivate = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        Button buttonRefuze = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(title);
        overlaySubtext.setText(overlayText);

        buttonActivate.setText(AppLabels.getOverlayCallBtn());
        buttonRefuze.setText(AppLabels.getOverlayRefuseBtn());

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
        overlayDismissButton.setVisibility(View.GONE);

        buttonRefuze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
            }
        });

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCallPermission()) {
                    enableCall();
                }
            }
        });

//        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                overlyDialog.dismiss();
//            }
//        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlyDialog.dismiss();
            }
        });
    }

    public void enableCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:*222"));
        startActivity(callIntent);
    }

    public boolean checkCallPermission() {

        boolean hasCallPermission = false;
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 23) {
            int hasCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
            if (hasCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                }
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

            } else {
                hasCallPermission = true;
            }
        } else {
            hasCallPermission = true;
        }

        return hasCallPermission;
    }

    public void showWarningToast(final String message) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new CustomToast.Builder(getActivity()).message(message).success(false).show();
//                    new CustomToast(getActivity(), getActivity(), message, true).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToActivityCompositeSubcription(Subscription subscription) {
        if (subscription == null) {
            return;
        }
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).addToActivityCompositeSubcription(subscription);
        }
    }

    /**
     * Call this in fragment's onStart() method to ensure it is not called when app is brought from background
     * @param pageName
     */
    public void callForAdobeTarget(final String pageName) {
        Log.d(TAG, "track page is from background: " + VodafoneController.getInstance().isAppComesFromBackground());

        if (VodafoneController.getInstance().isAppComesFromBackground()
                || VodafoneController.getInstance().isFromBackPress()|| !VodafoneController.getInstance().isWasScreenOn())
            return;

        new AdobeTargetController().trackPage(this, pageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        VodafoneController.getInstance().setFromBackPress(false);
    }
}
