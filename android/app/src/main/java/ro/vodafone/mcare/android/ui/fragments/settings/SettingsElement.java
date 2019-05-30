package ro.vodafone.mcare.android.ui.fragments.settings;

import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;

/**
 * Created by bogdan.marica on 2/23/2017.
 */

public class SettingsElement {

    String title;
    String subtext;
    int type;
    SwitchButton button;

    SettingsElement() {
    }

    ;

    SettingsElement(String title, String subtext, int type) {
        this.title = title;
        this.subtext = subtext;
        this.type = type;
    }

    private void setValues(String title, String subtext, int type, SwitchButton button) {
        this.title = title;
        this.subtext = subtext;
        this.type = type;
        this.button = button;
    }

    private String getTitle() {
        return title;
    }

    public String getSubtext() {
        return subtext;
    }

    public int getType() {
        return type;
    }

    private SwitchButton getButton() {
        return button;
    }


    @Override
    public String toString() {
        return title + "-[]-" + type;
    }
}
