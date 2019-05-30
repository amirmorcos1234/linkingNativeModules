package ro.vodafone.mcare.android.client.model.card;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;

/**
 * Created by Bivol Pavel on 22.03.2017.
 */
public class CardHeaderModel {

    private String header_icon;
    private String header_title;
    private SpannableString header_subTitle;
    private String header_category;
    private String header_bottom_category;
    private String header_extraParameter;
    private Drawable headerIconPlaceholder;

    public CardHeaderModel(String header_icon, String header_title, SpannableString header_subTitle, String header_category, String header_bottom_category, String header_extraParameter){
        this.header_icon = header_icon;
        this.header_title = header_title;
        this.header_subTitle = header_subTitle;
        this.header_category = header_category;
        this.header_bottom_category = header_bottom_category;
        this.header_extraParameter = header_extraParameter;
    }

    public CardHeaderModel(String header_icon, Drawable headerIconPlaceholder, String header_title, SpannableString header_subTitle, String header_category, String header_bottom_category, String header_extraParameter){
        this.header_icon = header_icon;
        this.header_title = header_title;
        this.header_subTitle = header_subTitle;
        this.header_category = header_category;
        this.header_bottom_category = header_bottom_category;
        this.header_extraParameter = header_extraParameter;
        this.headerIconPlaceholder = headerIconPlaceholder;
    }

    public String getHeader_icon() {
        return header_icon;
    }

    public void setHeader_icon(String header_icon) {
        this.header_icon = header_icon;
    }

    public String getHeader_title() {
        return header_title;
    }

    public void setHeader_title(String header_title) {
        this.header_title = header_title;
    }

    public SpannableString getHeader_subTitle() {
        return header_subTitle;
    }

    public void setHeader_subTitle(SpannableString header_subTitle) {
        this.header_subTitle = header_subTitle;
    }

    public String getHeader_category() {
        return header_category;
    }

    public void setHeader_category(String header_category) {
        this.header_category = header_category;
    }

    public String getHeader_bottom_category() {
        return header_bottom_category;
    }

    public void setHeader_bottom_category(String header_bottom_category) {
        this.header_bottom_category = header_bottom_category;
    }

    public String getHeader_extraParameter() {
        return header_extraParameter;
    }

    public void setHeader_extraParameter(String header_extraParameter) {
        this.header_extraParameter = header_extraParameter;
    }

    public Drawable getHeaderIconPlaceholder() {
        return headerIconPlaceholder;
    }

    public void setHeaderIconPlaceholder(Drawable headerIconPlaceholder) {
        this.headerIconPlaceholder = headerIconPlaceholder;
    }
}
