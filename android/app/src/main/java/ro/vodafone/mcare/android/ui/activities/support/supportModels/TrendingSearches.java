package ro.vodafone.mcare.android.ui.activities.support.supportModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user1 on 04-Jun-18.
 */

public class TrendingSearches {
	@SerializedName("role")
	@Expose
	private String role;
	@SerializedName("searches")
	@Expose
	private List<String> searches = null;



	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getSearches() {
		return searches;
	}

	public void setSearches(List<String> searches) {
		this.searches = searches;
	}

}
