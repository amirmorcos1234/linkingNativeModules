package ro.vodafone.mcare.android.service.tracking.adobe.target;

import java.io.Serializable;


/**
 * Created by Bivol Pavel on 20.04.2018.
 */

public class AdobeTargetResponseElement implements Serializable {

    private String elementID;
    private String element;
    private String title;
    private String content;
    private String priority;
    private String TTL;
    private String Xbutton;
    private String button1display;
    private String button1label;
    private String button1urltype;
    private String button1action;
    private String button2display;
    private String button2label;
    private String button2urltype;
    private String button2action;
    private String recurrence;
    private String recurrenceTTL;
    private String timetodisplay;
    private String button1type;
    private String button2type;
    private String bannerimage;
    private String bannerredirecturl;
    private String bannerredirecturltype;
    private String Type;

    public AdobeTargetResponseElement() {
    }

    public String getElementID() {
        return elementID;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTTL() {
        return TTL;
    }

    public void setTTL(String TTL) {
        this.TTL = TTL;
    }

    public String getXbutton() {
        return Xbutton;
    }

    public void setXbutton(String xbutton) {
        Xbutton = xbutton;
    }

    public String getButton1display() {
        return button1display;
    }

    public void setButton1display(String button1display) {
        this.button1display = button1display;
    }

    public String getButton1label() {
        return button1label;
    }

    public void setButton1label(String button1label) {
        this.button1label = button1label;
    }

    public String getButton1urltype() {
        return button1urltype;
    }

    public void setButton1urltype(String button1urltype) {
        this.button1urltype = button1urltype;
    }

    public String getButton1action() {
        return button1action;
    }

    public void setButton1action(String button1action) {
        this.button1action = button1action;
    }

    public String getButton2display() {
        return button2display;
    }

    public void setButton2display(String button2display) {
        this.button2display = button2display;
    }

    public String getButton2label() {
        return button2label;
    }

    public void setButton2label(String button2label) {
        this.button2label = button2label;
    }

    public String getButton2urltype() {
        return button2urltype;
    }

    public void setButton2urltype(String button2urltype) {
        this.button2urltype = button2urltype;
    }

    public String getButton2action() {
        return button2action;
    }

    public void setButton2action(String button2action) {
        this.button2action = button2action;
    }


    public String getButton1type() {
        return button1type;
    }

    public void setButton1type(String button1type) {
        this.button1type = button1type;
    }

    public String getButton2type() {
        return button2type;
    }

    public void setButton2type(String button2type) {
        this.button2type = button2type;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getRecurrenceTTL() {
        return recurrenceTTL;
    }

    public void setRecurrenceTTL(String recurrenceTTL) {
        this.recurrenceTTL = recurrenceTTL;
    }

    public String getTimetodisplay() {
        return timetodisplay;
    }

    public void setTimetodisplay(String timetodisplay) {
        this.timetodisplay = timetodisplay;
    }

    public String getBannerImage() {
        return bannerimage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerimage = bannerImage;
    }

    public String getBannerRedirectUrl() {
        return bannerredirecturl;
    }

    public void setBannerRedirectUrl(String bannerRedirectUrl) {
        this.bannerredirecturl = bannerRedirectUrl;
    }

    public String getBannerredirecturltype() {
        return bannerredirecturltype;
    }

    public void setBannerredirecturltype(String bannerredirecturltype) {
        this.bannerredirecturltype = bannerredirecturltype;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public boolean isXButtonDisplayed() {
        if(Xbutton == null)
            return true;
        if (Xbutton.toLowerCase().equals("show"))
            return true;
        else if (Xbutton.toLowerCase().equals("hide"))
            return false;
        return true;
    }

    public boolean isButton1Displayed(){
        if(button1display == null)
            return false;

        if(button1display.toLowerCase().equals("show"))
            return true;
        else if(button1display.toLowerCase().equals("hide"))
            return false;

        return false;
    }

    public boolean isButton2Displayed(){
        if(button2display == null)
            return false;

        if(button2display.toLowerCase().equals("show"))
            return true;
        else if(button2display.toLowerCase().equals("hide"))
            return false;

        return false;
    }

    public boolean isButton1TypePrimary(){
        if(button1type == null)
            return true;

        if(button1type.toLowerCase().equals("primary"))
            return true;
        else if(button1type.toLowerCase().equals("secondary"))
            return false;

        return true;
    }

    public boolean isButton2TypePrimary(){
        if(button2type == null)
            return false;

        if(button2type.toLowerCase().equals("primary"))
            return true;
        else if(button2type.toLowerCase().equals("secondary"))
            return false;

        return false;
    }

    public boolean isToastTypeSuccess(){
        if(Type == null)
            return true;

        if(Type.equals("success"))
            return true;
        else if(Type.equals("error"))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "AdobeTargetResponseElement{" +
                "elementID='" + elementID + '\'' +
                ", element='" + element + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", priority='" + priority + '\'' +
                ", TTL='" + TTL + '\'' +
                ", Xbutton='" + Xbutton + '\'' +
                ", button1display='" + button1display + '\'' +
                ", button1label='" + button1label + '\'' +
                ", button1urltype='" + button1urltype + '\'' +
                ", button1action='" + button1action + '\'' +
                ", button2display='" + button2display + '\'' +
                ", button2label='" + button2label + '\'' +
                ", button2urltype='" + button2urltype + '\'' +
                ", button2action='" + button2action + '\'' +
                ", recurrence='" + recurrence + '\'' +
                ", recurrenceTTL='" + recurrenceTTL + '\'' +
                ", timetodisplay='" + timetodisplay + '\'' +
                ", button1type='" + button1type + '\'' +
                ", button2type='" + button2type + '\'' +
                ", bannerImage='" + bannerimage + '\'' +
                ", bannerRedirectUrl='" + bannerredirecturl + '\'' +
                '}';
    }
}
