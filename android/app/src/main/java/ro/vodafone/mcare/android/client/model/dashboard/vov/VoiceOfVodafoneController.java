package ro.vodafone.mcare.android.client.model.dashboard.vov;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.widget.VoiceOfVodafoneWidget;

/**
 * Created by Victor Radulescu on 1/27/2017.
 */
public class VoiceOfVodafoneController {

    private static VoiceOfVodafoneController ourInstance = new VoiceOfVodafoneController();

    public static VoiceOfVodafoneController getInstance() {
        return ourInstance;
    }

    private HashMap<VoiceOfVodafoneCategory, VoiceOfVodafone> vovsStash;

    private boolean shouldRefresh = false;

    private VoiceOfVodafoneController() {
        if (vovsStash == null)
            vovsStash = new HashMap<>();
    }

    private ArrayList<VoiceOfVodafone> voiceOfVodafones = new ArrayList<>();

    private VoiceOfVodafoneWidget voiceOfVodafoneWidget;

    public VoiceOfVodafoneWidget getVoiceOfVodafoneWidget() {

        return voiceOfVodafoneWidget;
    }

    public void setVoiceOfVodafoneWidget(VoiceOfVodafoneWidget voiceOfVodafoneWidget) {
        this.voiceOfVodafoneWidget = voiceOfVodafoneWidget;
    }

    public void cleanSubcribers() {
        if (voiceOfVodafoneWidget != null) {
            voiceOfVodafoneWidget.destroy();
        }
    }

    public void clearVoiceOfVodafone() {
        cleanSubcribers();
        voiceOfVodafones = null;
        this.voiceOfVodafoneWidget = null;
    }

    public View.OnClickListener getClickListenerByAction(Context context, VoiceOfVodafoneAction voiceOfVodafoneAction, VoiceOfVodafoneParameter voiceOfVodafoneParameter, VoiceOfVodafone voiceOfVodafone, boolean isLeft) {
        View.OnClickListener onClickListener = null;
        if (voiceOfVodafoneWidget != null && voiceOfVodafoneAction != null) {
            onClickListener = new VoiceOfVodafoneListeners(voiceOfVodafoneWidget).getListenerByAction(context, voiceOfVodafoneAction, voiceOfVodafoneParameter, voiceOfVodafone, isLeft);
        }
        return onClickListener;
    }

    public List<VoiceOfVodafone> getVoiceOfVodafones() {
        if (voiceOfVodafones == null || voiceOfVodafones.isEmpty()) {
            voiceOfVodafones = new ArrayList<>();
        }
        if(voiceOfVodafones.isEmpty() || (voiceOfVodafones.size() == 1 && voiceOfVodafones.get(0).getCategory() == VoiceOfVodafoneCategory.UserProfileHierarchy))
            voiceOfVodafones.addAll(VodafoneController.getInstance().getUser().getInitialVoiceOfVodafones());

        sortVoiceOfVodafonesAfterPriority();
        if (voiceOfVodafones.size() > 3) {
            return voiceOfVodafones.subList(0, 3);
        }
        return voiceOfVodafones;
    }

    public void setVoiceOfVodafones(ArrayList<VoiceOfVodafone> voiceOfVodafones) {
        this.voiceOfVodafones = voiceOfVodafones;
        sortVoiceOfVodafonesAfterPriority();
    }

    private void sortVoiceOfVodafonesAfterPriority() {
        try {
            Collections.sort(voiceOfVodafones, voiceOfVodafonePriorityComparator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createApi19TimeOutVov() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.UserProfileHierarchy, null,
                AppLabels.getApi19TimeoutErrorMessage(), "Reîncearcă", null,
                true, false, VoiceOfVodafoneAction.ReloadAPI19, null);
        voiceOfVodafone.setTimeToLive(getTtlTimestamp(2));
        pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        refreshVoiceOfVodafoneWidget();
    }

    public void createApi19FailedVov() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.UserProfileHierarchy, null,
                AppLabels.getApi19FailedErrorMessage(), "Ok", null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        voiceOfVodafone.setTimeToLive(getTtlTimestamp(2));
        pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        refreshVoiceOfVodafoneWidget();
    }

    private long getTtlTimestamp(int numberOfDays) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, numberOfDays);
        return c.getTimeInMillis()/1000;
    }

    private Comparator<VoiceOfVodafone> voiceOfVodafonePriorityComparator = new Comparator<VoiceOfVodafone>() {
        @Override
        public int compare(VoiceOfVodafone vov1, VoiceOfVodafone vov2) {
            if (vov1.getPriority() == vov2.getPriority())
                return 0;
            return vov1.getPriority() < vov2.getPriority() ? 1 : -1;
        }
    };

    public synchronized void pushStashToView(VoiceOfVodafone voiceOfVodafoneToPush) {
        if (voiceOfVodafoneToPush != null) {
            pushStashToView(voiceOfVodafoneToPush.getCategory(), voiceOfVodafoneToPush);
        }
    }

    public synchronized void pushStashToView(VoiceOfVodafoneCategory type, VoiceOfVodafone voiceOfVodafoneToPush) {
        if (voiceOfVodafones == null) {
            voiceOfVodafones = new ArrayList<>();
        }
        shouldRefresh = true;
        //Check if no duplicates
        if (voiceOfVodafones.contains(voiceOfVodafoneToPush)) {
            return;
        }
        VoiceOfVodafone oldVoiceOfVodafoneToBeRemoved = vovsStash.put(type, voiceOfVodafoneToPush);
        if (oldVoiceOfVodafoneToBeRemoved != null) {
            voiceOfVodafones.remove(oldVoiceOfVodafoneToBeRemoved);
        }
        voiceOfVodafones.add(voiceOfVodafoneToPush);
        Collections.sort(voiceOfVodafones, voiceOfVodafonePriorityComparator);
    }

    public synchronized void removeVoiceOfVodafone(VoiceOfVodafone voiceOfVodafone, boolean popUpNext, boolean dismissFromWidget) {
        try {
            if (voiceOfVodafones == null) {
                voiceOfVodafones = new ArrayList<>();
            }
            voiceOfVodafones.remove(voiceOfVodafone);
            sortVoiceOfVodafonesAfterPriority();
            if (popUpNext) {
                popInNext();
            }
            if (dismissFromWidget) {
                voiceOfVodafoneWidget.dimissWithoutTransition(voiceOfVodafone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Deprecated
    public synchronized void adddVoiceOfVodafone(int position, VoiceOfVodafone voiceOfVodafone) {
        Log.d("VovC", "addVoiceOfVodafone" + position);
        try {
            voiceOfVodafones.add(position, voiceOfVodafone);
            sortVoiceOfVodafonesAfterPriority();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popInNext() {
        try {
            shouldRefresh = true;
            voiceOfVodafoneWidget.refreshIfNeeded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isStashValidToPop() {
        return vovsStash != null && !vovsStash.isEmpty();
    }


    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }

    public void refreshVoiceOfVodafoneWidget() {
        try {
            if (voiceOfVodafoneWidget != null) {
                voiceOfVodafoneWidget.refreshIfNeeded();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
