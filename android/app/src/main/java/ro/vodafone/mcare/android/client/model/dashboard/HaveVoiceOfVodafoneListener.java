package ro.vodafone.mcare.android.client.model.dashboard;

import android.view.View;

import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;

/**
 * Created by Victor Radulescu on 5/31/2017.
 */

public abstract class HaveVoiceOfVodafoneListener implements View.OnClickListener {

    private VoiceOfVodafone voiceOfVodafone;

    public HaveVoiceOfVodafoneListener(VoiceOfVodafone voiceOfVodafone) {
        this.voiceOfVodafone = voiceOfVodafone;
    }

    public VoiceOfVodafone getVoiceOfVodafone() {
        return voiceOfVodafone;
    }
}
