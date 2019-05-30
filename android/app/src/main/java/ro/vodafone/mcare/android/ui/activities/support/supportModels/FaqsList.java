package ro.vodafone.mcare.android.ui.activities.support.supportModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqsList {

    public boolean rotateToInit = false;
    private boolean isArrowUp = false;
    private boolean executeAnimation = false;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private List<Content> content = null;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("categoryTags")
    @Expose
    private List<String> categoryTags = null;
    @SerializedName("keywords")
    @Expose
    private List<String> keywords = null;
    @SerializedName("relatedQuestionIds")
    @Expose
    private List<Integer> relatedQuestionIds = null;
    @SerializedName("hasEmailButton")
    @Expose
    private Boolean hasEmailButton;
    private boolean isExpanded = false;
    @SerializedName("userRoles")
    @Expose
    private List<String> userRoles = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<String> getCategoryTags() {
        return categoryTags;
    }

    public void setCategoryTags(List<String> categoryTags) {
        this.categoryTags = categoryTags;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<Integer> getRelatedQuestionIds() {
        return relatedQuestionIds;
    }

    public void setRelatedQuestionIds(List<Integer> relatedQuestionIds) {
        this.relatedQuestionIds = relatedQuestionIds;
    }

    public Boolean getHasEmailButton() {
        return hasEmailButton;
    }

    public void setHasEmailButton(Boolean hasEmailButton) {
        this.hasEmailButton = hasEmailButton;
    }

	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<String> userRoles) {
		this.userRoles = userRoles;
	}

    public boolean getHasContentExpanded() {
        return isExpanded;
    }

    public void setHasContentExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public FaqsList resetExpanded() {
        setHasContentExpanded(false);
        setArrowUp(false);
        return this;
    }

    public boolean isArrowUp() {
        return isArrowUp;
    }

    public void setArrowUp(boolean arrowUp) {
        this.isArrowUp = arrowUp;
    }

    public boolean getExecuteAnimation() {
        return executeAnimation;
    }

    public void setExecuteAnimation(boolean executeAnimation) {
        this.executeAnimation = executeAnimation;
    }

}
