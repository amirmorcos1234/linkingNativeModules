package ro.vodafone.mcare.android.service.tracking.adobe;

import android.app.Fragment;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by marius on 28/5/15.
 *
 * @music Gramatik - Just Jammin'
 */
public abstract class FragmentTrack extends Fragment {

    private final PublishSubject<Void> destroyedSubject = PublishSubject.create();

    /**
     * An observable sending a next when the Fragment has been destroyed
     *
     * Example: use it with a takeUntil on your observable to ensure it's not going to leak
     */
    public Observable<Void> destroyed() { return destroyedSubject.asObservable(); }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyedSubject.onNext(null);
    }

    /**
     * Performs an action after a certain delay
     *
     * Example: doAfterDelay(1, TimeUnit.SECONDS, ignored-> doSomething());
     */
    public Subscription doAfterDelay(int time, TimeUnit timeUnit, Action1<Long> action) {
        return delayObservable(time, timeUnit)
                .subscribe(action);
    }

    /**
     * Performs an action after a certain delay if the filter condition is met
     *
     * Example: doAfterDelay(1, TimeUnit.SECONDS,
     *                       ignored-> stuff != null,
     *                       ignored-> doSomething());
     */
    public Subscription doAfterDelay(int time, TimeUnit timeUnit, Func1<Long, Boolean> filter, Action1<Long> action) {
        return delayObservable(time, timeUnit)
                .filter(filter)
                .subscribe(action);
    }

    /**
     * Performs an action after a certain delay in milliseconds
     *
     * Example: doAfterDelay(500, ignored-> doSomething());
     */
    public Subscription doAfterDelay(int time, Action1<Long> action) {
        return doAfterDelay(time, TimeUnit.MILLISECONDS, action);
    }

    /**
     * Performs an action after a certain delay in milliseconds if the filter condition is met
     *
     * Example: doAfterDelay(1,
     *                       ignored-> stuff != null,
     *                       ignored-> doSomething());
     */
    public Subscription doAfterDelay(int time, Func1<Long, Boolean> filter, Action1<Long> action) {
        return doAfterDelay(time, TimeUnit.MILLISECONDS, filter, action);
    }

    private Observable<Long> delayObservable(int time, TimeUnit timeUnit) {
        return Observable
                .interval(time, timeUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .takeUntil(destroyed());
    }

}