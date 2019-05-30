package ro.vodafone.mcare.android.widget.animators;

import android.view.View;

import com.github.florent37.viewanimator.ViewAnimator;

import ro.vodafone.mcare.android.widget.BubbleMenuButton;
import ro.vodafone.mcare.android.widget.VoiceOfVodafoneWidget;
import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;


/**
 * Created by Victor Radulescu on 2/27/2017.
 */

public class DashboardAnimator extends RelativeViewAnimator {

    //private  DashboardAnimator instance;

    public void addVoiceOfVodafoneAnimation(VoiceOfVodafoneWidget voiceOfVodafone){
        View dotsView = voiceOfVodafone.getDotsLayout();

        AnimationInfo animationVoiceOfVodafone = new AnimationInfo(voiceOfVodafone);
        animationVoiceOfVodafone.setDuration(1000);
        animationVoiceOfVodafone.setInterpolator(Interpolators.EASE_OUT_ELASTIC);
        animationVoiceOfVodafone.setInitialAlfa(0);

        animationVoiceOfVodafone.setAnimator(ViewAnimator
                .animate(animationVoiceOfVodafone.getView())
                .interpolator(animationVoiceOfVodafone.getInterpolator())
                .duration(animationVoiceOfVodafone.getDuration())
                .scale(0.5f,1f)
                .dp().translationY(-50, 0)
        );
        addStandardAlfaAnimation(voiceOfVodafone,0);
        animationInfos.add(animationVoiceOfVodafone);

        AnimationInfo animationDots = new AnimationInfo(dotsView);
        animationDots.setDuration(670);
        animationDots.setStartDelay(200);
        animationDots.setInterpolator(Interpolators.OUT_EXPO);
        animationDots.setInitialAlfa(0);


        animationDots.setAnimator(ViewAnimator
                .animate(dotsView)
                .interpolator(animationVoiceOfVodafone.getInterpolator())
                .duration(animationVoiceOfVodafone.getDuration())
                .dp().translationY(-20, 0)
        );
        addStandardAlfaAnimation(dotsView,200);
        animationInfos.add(animationDots);


    }

    public void addCostControlAnimation(View costControl){

        AnimationInfo animationInfo = new AnimationInfo(costControl);
        animationInfo.setDuration(500);
        animationInfo.setStartDelay(830);
        animationInfo.setInterpolator(Interpolators.OUT_QUART);
        animationInfo.setView(costControl);
        animationInfo.setInitialAlfa(0);

        animationInfo.setAnimator(ViewAnimator
                .animate(costControl)
                .interpolator(Interpolators.OUT_QUART)
                .duration(animationInfo.getDuration())
                .scale(0.5f,1f)
                );

        addStandardAlfaAnimation(costControl,830);
        animationInfos.add(animationInfo);

    }
    public void addServiceSelectorAnimation(View seriviceSelector){

        AnimationInfo animationInfo = new AnimationInfo(seriviceSelector);
        animationInfo.setDuration(670);
        animationInfo.setStartDelay(800);
        animationInfo.setInterpolator(Interpolators.OUT_EXPO);
        animationInfo.setView(seriviceSelector);
        animationInfo.setInitialAlfa(0);

        animationInfo.setAnimator(ViewAnimator
                .animate(seriviceSelector)
                .interpolator(Interpolators.OUT_EXPO)
                .duration(animationInfo.getDuration())
                .scale(0.25f,1f)
                );

        addStandardAlfaAnimation(seriviceSelector,800);
        animationInfos.add(animationInfo);

    }

    public void addMenuButtonAnimation(BubbleMenuButton menuButton){
        AnimationInfo animationInfo = new AnimationInfo(menuButton);
        animationInfo.setDuration(500);
        animationInfo.setInterpolator(Interpolators.OUT_EXPO);
        animationInfo.setView(menuButton);

        animationInfo.setAnimator(ViewAnimator
                .animate(menuButton)
                .dp().translationY(-24, 0)
                .interpolator(Interpolators.OUT_EXPO)
                .duration(animationInfo.getDuration()));

        //View infoView = menuButton.getBubbleNumberTextview();
        /*AnimationInfo animationInfoExtra = new AnimationInfo(infoView);
        animationInfoExtra.setDuration(300);
        animationInfoExtra.setStartDelay(400);
        animationInfoExtra.setInterpolator(Interpolators.OUT_BACK);
        animationInfoExtra.setInitialAlfa(0);

        animationInfoExtra.setAnimator(ViewAnimator
                .animate(infoView)
                .interpolator(Interpolators.OUT_BACK)
                .duration(animationInfoExtra.getDuration())
                .scale(0.5f,1f)
                .dp().pivotX(-5,0)
        );*/

        animationInfos.add(animationInfo);
      //  addStandardAlfaAnimation(infoView,400);
      //  animationInfos.add(animationInfoExtra);

    }

    private void addStandardAlfaAnimation(View view,long startDelay){
        AnimationInfo animationInfo = new AnimationInfo(view);
        animationInfo.setDuration(170);
        animationInfo.setStartDelay(startDelay);
        animationInfo.setView(view);

        animationInfo.setAnimator(ViewAnimator
                .animate(view)
                .duration(animationInfo.getDuration())
                .alpha(0,1f)
        );
        animationInfos.add(animationInfo);
    }


}
