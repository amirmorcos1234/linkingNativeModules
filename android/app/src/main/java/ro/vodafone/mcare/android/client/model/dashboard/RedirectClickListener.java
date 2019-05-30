package ro.vodafone.mcare.android.client.model.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneParameter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.widget.VoiceOfVodafoneWidget;

/**
 * Created by Victor Radulescu on 1/31/2017.
 */

public  class RedirectClickListener implements View.OnClickListener {

    private VoiceOfVodafoneParameter voiceOfVodafoneParameter;

    public RedirectClickListener(VoiceOfVodafoneParameter voiceOfVodafoneParameter) {
        this.voiceOfVodafoneParameter = voiceOfVodafoneParameter;
        D.w("voiceOfVodafoneParameter = "+voiceOfVodafoneParameter);
        D.w("voiceOfVodafoneParameter = "+voiceOfVodafoneParameter.name());
    }


    @Override
    public void onClick(View v) {
        try{
            VoiceOfVodafoneWidget voiceOfVodafoneWidget = VoiceOfVodafoneController.getInstance().getVoiceOfVodafoneWidget();
            if(voiceOfVodafoneWidget!=null){
                Context context = voiceOfVodafoneWidget.getContext();
                Intent intent = new Intent();
                intent.setAction(voiceOfVodafoneParameter.getClassName());
                context.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
