package ro.vodafone.mcare.android.utils.pojoconvertors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneType;
import ro.vodafone.mcare.android.client.model.vov.VoiceOfVodafoneRestPojo;
import ro.vodafone.mcare.android.client.model.vov.VovLeftButtonAction;
import ro.vodafone.mcare.android.client.model.vov.VovRightButtonAction;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

/**
 * Created by Victor Radulescu on 2/2/2018.
 */

public class VovPojoToViewModelConvertor {

    public static VoiceOfVodafone getViewModelFromRestPojo(VoiceOfVodafoneRestPojo pojo) {
        if (pojo == null) {
            return null;
        }

        if (pojo.getLoginType() == null || pojo.getLoginType().size() < 1)
            return null;

        if (pojo.getUserRole() == null || pojo.getUserRole().size() < 1)
            return null;

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(pojo.getPriority());

        try {
            voiceOfVodafone.setCategory(VoiceOfVodafoneCategory.fromString(pojo.getCategory()));
            if (voiceOfVodafone.getCategory() == null) {
                return null;
                //voiceOfVodafone.setCategory(VoiceOfVodafoneCategory.OTHER);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

        voiceOfVodafone.setPriority(pojo.getPriority());
        voiceOfVodafone.setMessage(pojo.getMessage());
        voiceOfVodafone.setTitle(pojo.getTitle());
        voiceOfVodafone.setVovType(VoiceOfVodafoneType.getVoiceOfVodafoneTypeAfterString(pojo.getLayout()));

        if (pojo.getVovLeftButtonAction() != null && pojo.getVovLeftButtonAction().isEnabled()) {
            VovLeftButtonAction leftBtnAction = pojo.getVovLeftButtonAction();
            voiceOfVodafone.setLeftButtonTitle(leftBtnAction.getTitle());
            voiceOfVodafone.setShowLeftBtn(true);
            voiceOfVodafone.setLeftAction(VoiceOfVodafoneAction.fromString(leftBtnAction.getAction()));
            if (voiceOfVodafone.getLeftAction() == VoiceOfVodafoneAction.OpenUrl) {
                voiceOfVodafone.setLeftActionUrl(leftBtnAction.getActionParameter());
                IntentActionName intent = IntentActionName.WEBVIEW;
                voiceOfVodafone.setIntentActionName(intent);
            }

        }
        if (pojo.getVovRightButtonAction() != null && pojo.getVovRightButtonAction().isEnabled()) {
            VovRightButtonAction rightBtnAction = pojo.getVovRightButtonAction();
            voiceOfVodafone.setRightButtonTitle(rightBtnAction.getTitle());
            voiceOfVodafone.setShowRightBtn(true);
            voiceOfVodafone.setRightAction(VoiceOfVodafoneAction.fromString(rightBtnAction.getAction()));
            if (voiceOfVodafone.getRightAction() == VoiceOfVodafoneAction.OpenUrl) {
                voiceOfVodafone.setRightActionUrl(rightBtnAction.getActionParameter());
                IntentActionName intent = IntentActionName.WEBVIEW;
                voiceOfVodafone.setIntentActionName(intent);
            }
        }
        return voiceOfVodafone;
    }

    public static ArrayList<VoiceOfVodafone> getVoiceOfVodafonesViewModelAfterRestPojo(List<VoiceOfVodafoneRestPojo> voiceOfVodafoneRestPojos, String userRole, String userName, boolean isSeamless) {
        ArrayList<VoiceOfVodafone> voiceOfVodafones = new ArrayList<>();
        LinkedHashMap<VoiceOfVodafoneCategory, VoiceOfVodafone> vovMap = new LinkedHashMap<>();

        for (VoiceOfVodafoneRestPojo pojo : voiceOfVodafoneRestPojos) {
            try {

                boolean addVoVByLogin = false;
                if (pojo.getLoginType() != null)
                    for (String string : pojo.getLoginType()) {
                        if (string.equalsIgnoreCase(!isSeamless ? "authenticated" : "seamless")) {
                            addVoVByLogin = true;
                            break;
                        }
                    }
                if (!addVoVByLogin)
                    continue;

                boolean addVoVByUserRole = false;
                if (pojo.getUserRole() != null)
                    for (String string : pojo.getUserRole()) {
                        if (string.equalsIgnoreCase(userRole)) {
                            addVoVByUserRole = true;
                            break;
                        }
                    }
                if (!addVoVByUserRole)
                    continue;

                VoiceOfVodafone vov = getViewModelFromRestPojo(pojo);

                if (vov == null)
                    continue;


                if (vov.getTitle().contains("{userName}"))
                    vov.setTitle(vov.getTitle().replace("{userName}", userName));

                if(vovMap.containsKey(vov.getCategory()) && vovMap.get(vov.getCategory()).getPriority() >= vov.getPriority())
                    continue;

                if(vovMap.containsKey(vov.getCategory()))
                    vovMap.remove(vov.getCategory());

                vovMap.put(vov.getCategory(), vov);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        voiceOfVodafones.addAll(vovMap.values());

        return voiceOfVodafones;
    }
}
