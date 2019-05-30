package ro.vodafone.mcare.android.ui.utils.rxactivityresult;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ResultHandleFragment extends Fragment {
    public final PublishSubject<ActivityResult> resultPublisher = PublishSubject.create();
    public final BehaviorSubject<Boolean> isAttachedBehavior = BehaviorSubject.create(false);

    public ResultHandleFragment() {

    }

    public PublishSubject<ActivityResult> getResultPublisher() {
        return resultPublisher;
    }

    public BehaviorSubject<Boolean> getIsAttachedBehavior() {
        return isAttachedBehavior;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    private void onAttachToContext(Context context) {
        isAttachedBehavior.onNext(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resultPublisher.onNext(new ActivityResult(requestCode, resultCode, data));
    }
}
