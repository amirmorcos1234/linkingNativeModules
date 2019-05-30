package ro.vodafone.mcare.android.widget.balancecards;

import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;

/**
 * Created by User on 17.02.2017.
 */

public class BalanceCardDetail {
    private String leftText;
    private int icon;
    private String rightText;
    private Category category;

    public BalanceCardDetail(String leftText, int icon, String rightText, Category category) {
        this.leftText = leftText;
        this.icon = icon;
        this.rightText = rightText;
        this.category = category;
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
