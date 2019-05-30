package ro.vodafone.mcare.android.widget.balancecards;

import android.text.SpannableString;

/**
 * Created by Anca on 17.02.2017.
 */

public class BalanceCardHeader {

    private String textTitle;
    private String textHigh;
    private String textMiddle;
    private SpannableString textLow;

    private boolean arrowVisible = true;
    private boolean showJustHighText = false;
    private boolean hidePrimaryFooterButton = false;
    private boolean showSecondaryFooterButton = false;

    public BalanceCardHeader(String textTitle,String textHigh, String textMiddle, SpannableString textLow) {
        this.textTitle = textTitle;
        this.textHigh = textHigh;
        this.textMiddle = textMiddle;
        this.textLow = textLow;
    }  public BalanceCardHeader(String textHigh, String textMiddle, SpannableString textLow) {
        this.textHigh = textHigh;
        this.textMiddle = textMiddle;
        this.textLow = textLow;
    }

    public String getTextMiddle() {
        return textMiddle;
    }

    public SpannableString getTextLow() {
        return textLow;
    }

    public String getTextHigh() {
        return textHigh;
    }

    public void setTextHigh(String textHigh) {
        this.textHigh = textHigh;
    }

    public void setTextMiddle(String textMiddle) {
        this.textMiddle = textMiddle;
    }

    public void setTextLow(SpannableString textLow) {
        this.textLow = textLow;
    }


    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public boolean isArrowVisible() {
        return arrowVisible;
    }

    public void setArrowVisible(boolean arrowVisible) {
        this.arrowVisible = arrowVisible;
    }

    public boolean showJustHighText() {
        return showJustHighText;
    }

    public void setShowJustHighTextFlag(boolean showJustHighText) {
        this.showJustHighText = showJustHighText;
    }

    public boolean shouldHideFooter() {
        return hidePrimaryFooterButton;
    }

    public void setHidePrimaryFooterButton(boolean hidePrimaryFooterButton) {
        this.hidePrimaryFooterButton = hidePrimaryFooterButton;
    }

    public boolean showSecondaryFooterButton() {
        return showSecondaryFooterButton;
    }

    public void setShowSecondaryFooterButton(boolean showSecondaryFooterButton) {
        this.showSecondaryFooterButton = showSecondaryFooterButton;
    }

}
