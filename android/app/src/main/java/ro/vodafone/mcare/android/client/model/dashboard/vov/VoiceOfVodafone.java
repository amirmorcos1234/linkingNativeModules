package ro.vodafone.mcare.android.client.model.dashboard.vov;


import com.google.gson.annotations.SerializedName;

import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

/**
 * Created by Victor Radulescu on 1/26/2017.
 */

public class VoiceOfVodafone{

    private int id_vov;

    private int priority;

    private VoiceOfVodafoneCategory category;

    private String title;

    private String message;

    private String leftButtonTitle;

    private String rightButtonTitle;

    private boolean showLeftBtn=false;

    private boolean showRightBtn= false;

    private VoiceOfVodafoneParameter parameter;

    private VoiceOfVodafoneAction leftAction = VoiceOfVodafoneAction.Dismiss;

    private VoiceOfVodafoneAction rightAction = VoiceOfVodafoneAction.Dismiss;

    private String leftActionUrl = "";

    private String rightActionUrl = "";

    private VoiceOfVodafoneType vovType = VoiceOfVodafoneType.Dialog;

    IntentActionName intentActionName=IntentActionName.NONE;
    /**
     * Time to live in seconds
     */
    @SerializedName("timeToLive")
    private long timeToLive=0;

    public VoiceOfVodafone(int id_vov) {
        this.id_vov = id_vov;
    }

    public VoiceOfVodafone(int id_vov, VoiceOfVodafoneType vovType) {
        this.id_vov = id_vov;
        this.vovType = vovType;
    }

    public VoiceOfVodafone(int id_vov, int priority , VoiceOfVodafoneCategory category, String title, String message) {
        this.id_vov = id_vov;
        this.category = category;
        this.priority=priority;
        this.title = title;
        this.message = message;
        vovType = VoiceOfVodafoneType.Notification;
    }

    public VoiceOfVodafone( int id_vov, String title, String message, boolean showLeftBtn,boolean showRightBtn) {
        this.id_vov = id_vov;
        this.title = title;
        this.message = message;
        this.showLeftBtn = showLeftBtn;
        this.showRightBtn = showRightBtn;
        vovType = VoiceOfVodafoneType.Dialog;
    }

    public VoiceOfVodafone(int id_vov, String title, String message, boolean showLeftBtn, boolean showRightBtn, VoiceOfVodafoneAction leftAction, VoiceOfVodafoneAction rightAction) {
        this.id_vov = id_vov;
        this.title = title;
        this.message = message;
        this.showLeftBtn = showLeftBtn;
        this.showRightBtn = showRightBtn;
        this.leftAction = leftAction;
        this.rightAction = rightAction;
        vovType = VoiceOfVodafoneType.Dialog;

    }

    public VoiceOfVodafone(int id_vov, int priority, VoiceOfVodafoneCategory category, String title, String message, String leftButtonTitle, String rightButtonTitle, boolean showLeftBtn, boolean showRightBtn, VoiceOfVodafoneAction leftAction, VoiceOfVodafoneAction rightAction) {
        this.id_vov = id_vov;
        this.priority = priority;
        this.category = category;
        this.title = title;
        this.message = message;
        this.leftButtonTitle = leftButtonTitle;
        this.rightButtonTitle = rightButtonTitle;
        this.showLeftBtn = showLeftBtn;
        this.showRightBtn = showRightBtn;
        this.leftAction = leftAction;
        this.rightAction = rightAction;
        vovType = VoiceOfVodafoneType.Dialog;
    }

    public int getId_vov() {
        return id_vov;
    }

    public void setId_vov(int id_vov) {
        this.id_vov = id_vov;
    }

    public VoiceOfVodafoneType getVovType() {
        return vovType;
    }

    public void setVovType(VoiceOfVodafoneType vovType) {
        this.vovType = vovType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShowLeftBtn() {
        return showLeftBtn;
    }

    public void setShowLeftBtn(boolean showLeftBtn) {
        this.showLeftBtn = showLeftBtn;
    }

    public boolean isShowRightBtn() {
        return showRightBtn;
    }

    public void setShowRightBtn(boolean showRightBtn) {
        this.showRightBtn = showRightBtn;
    }
    public int getLayout(){
        return vovType.getLayout();
    }

    public VoiceOfVodafoneAction getLeftAction() {
        return leftAction;
    }

    public void setLeftAction(VoiceOfVodafoneAction leftAction) {
        this.leftAction = leftAction;
    }

    public VoiceOfVodafoneAction getRightAction() {
        return rightAction;
    }

    public void setRightAction(VoiceOfVodafoneAction rightAction) {
        this.rightAction = rightAction;
    }

    public String getLeftActionUrl() {
        return leftActionUrl;
    }

    public void setLeftActionUrl(String leftActionUrl) {
        this.leftActionUrl = leftActionUrl;
    }

    public String getRightActionUrl() {
        return rightActionUrl;
    }

    public void setRightActionUrl(String rightActionUrl) {
        this.rightActionUrl = rightActionUrl;
    }

    public String getLeftButtonTitle() {
        return leftButtonTitle;
    }

    public void setLeftButtonTitle(String leftButtonTitle) {
        this.leftButtonTitle = leftButtonTitle;
    }

    public String getRightButtonTitle() {
        return rightButtonTitle;
    }

    public void setRightButtonTitle(String rightButtonTitle) {
        this.rightButtonTitle = rightButtonTitle;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public VoiceOfVodafoneCategory getCategory() {
        return category;
    }

    public void setCategory(VoiceOfVodafoneCategory category) {
        this.category = category;
    }

    public VoiceOfVodafoneParameter getParameter() {
        return parameter;
    }

    public void setParameter(VoiceOfVodafoneParameter parameter) {
        this.parameter = parameter;
    }

    public IntentActionName getIntentActionName() {
        return intentActionName;
    }

    public void setIntentActionName(IntentActionName intentActionName) {
        this.intentActionName = intentActionName;
    }
}

