package ro.vodafone.mcare.android.ui.activities.support.supportModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqPOJO {

    @SerializedName("cachingTime")
    @Expose
    private Integer cachingTime;
    @SerializedName("popularSearchTerms")
    @Expose
    private List<String> popularSearchTerms = null;
    @SerializedName("categoriesList")
    @Expose
    private List<CategoriesList> categoriesList = null;
    @SerializedName("faqsList")
    @Expose
    private List<FaqsList> faqsList = null;
    @SerializedName("trendingSearches")
    @Expose
    private List<TrendingSearches> trendingSearches = null;

    public Integer getCachingTime() {
        return cachingTime;
    }

    public void setCachingTime(Integer cachingTime) {
        this.cachingTime = cachingTime;
    }

    public List<String> getPopularSearchTerms() {
        return popularSearchTerms;
    }

    public void setPopularSearchTerms(List<String> popularSearchTerms) {
        this.popularSearchTerms = popularSearchTerms;
    }

    public List<CategoriesList> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<CategoriesList> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<FaqsList> getFaqsList() {
        return faqsList;
    }

    public void setFaqsList(List<FaqsList> faqsList) {
        this.faqsList = faqsList;
    }

    public List<TrendingSearches> getTrendingSearches() {
        return trendingSearches;
    }

    public void setTrendingSearches(List<TrendingSearches> trendingSearches) {
        this.trendingSearches = trendingSearches;
    }

}
