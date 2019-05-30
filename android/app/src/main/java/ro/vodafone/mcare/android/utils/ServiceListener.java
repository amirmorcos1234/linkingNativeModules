package ro.vodafone.mcare.android.utils;

/**
 * Created by Andrei DOLTU on 3/13/2017.
 */



import com.vodafone.netperform.NetPerformStateListener;

import java.util.Observable;

/**
 * ROSDK: This listener is triggered by the background service to broadcast the current
 * service state.
 */

public class ServiceListener extends Observable implements NetPerformStateListener {
    private static ServiceListener listener;

    private ServiceListener() {
    }

    public synchronized static ServiceListener getInstance() {
        if (listener == null) {
            listener = new ServiceListener();
        }
        return listener;
    }

    @Override
    public void onStarted() {
        notifyChanges();
    }

    @Override
    public void onStopped() {
        notifyChanges();
    }

    @Override
    public void onPersonalizedStarted() {

    }

    @Override
    public void onPersonalizedStopped() {

    }

    @Override
    public void onPersonalizationUpdated() {

    }

    @Override
    public void onError(Error error) {

    }

    public void notifyChanges() {
        setChanged();
        notifyObservers();
    }
}
